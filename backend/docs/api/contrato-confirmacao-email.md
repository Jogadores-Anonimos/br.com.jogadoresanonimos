# Contrato de API — Confirmação de e-mail no cadastro

Convenções gerais (mesmas do resto do projeto): envelope
`{ success, data, error }`, paths sob `/api/v1`, nenhum token em query
string (sempre corpo), datas ISO-8601 UTC. Catálogo completo de
`error.code` em `contrato-erro-padrao.md`.

## Contexto e decisão de negócio (fechada com o usuário, 2026-08-09)

Conta pendente de confirmação de e-mail **não bloqueia login nem
navegação normal**. A pendência só restringe dois fluxos: compra física
na loja e recuperação de senha por e-mail (`POST /auth/esqueci-senha`,
ramo usuário comum). Ambos exigem `email_confirmado_em IS NOT NULL`.

Mecanismo: token opaco CSPRNG de 32 bytes (`SecureRandom`, Base64
URL-safe, mesmo padrão de `PasswordResetToken`), enviado por link. Só o
hash **SHA-256 puro** (não HMAC) do token é persistido — diferente do
padrão HMAC usado em `PasswordResetToken`/`AdminResetCodigo`, porque ali
o segredo do servidor mitiga adivinhação de um espaço pequeno (senha,
código de 6 dígitos); aqui o próprio token já tem 256 bits de entropia
CSPRNG, então SHA-256 simples já é suficiente para impedir que o valor
em claro seja recuperado a partir do que está no banco — não precisa de
segredo adicional de servidor. TTL 24h para `tipo = CADASTRO`, single-use
(ver seção 1.1 para o TTL diferenciado de `tipo = TROCA_EMAIL`).

## 1. Geração e envio — integrado ao cadastro existente

Não é um endpoint novo: acontece dentro de
`POST /api/v1/auth/registro` (`AuthController.registrar` →
`AuthService.registrar`), depois que a conta é persistida com sucesso.

- **Idempotente**: não se aplica isoladamente — é parte do fluxo de
  registro, que já não é idempotente (cria um recurso).
- **Falha de envio nunca derruba o cadastro**: `EmailService` já é
  `@Async` com retry/backoff interno (3 tentativas, 2s/6s/18s); o
  método de geração de token grava o registro em
  `email_confirmacao_token` de forma síncrona (consistência forte —
  token precisa existir antes do e-mail poder ser confirmado), mas a
  chamada ao envio em si é assíncrona e não lança para o chamador.
  Resposta do registro é sempre a mesma de hoje (`201`, `UsuarioResponse`)
  independente do resultado do envio.
- **Consistência**: forte na escrita do token (mesma transação do
  registro); envio de e-mail é assíncrono (eventual, tolera atraso —
  natureza de e-mail transacional, igual ao padrão já usado em
  recuperação de senha).

Response do registro **não muda de contrato** (mesmo shape de hoje).

## 1.1 Atualização 2026-08-10 (`backend`) — segundo motivo de emissão de token: troca de e-mail

`email_confirmacao_token` ganhou `tipo` (`CADASTRO` | `TROCA_EMAIL`) e
`novo_email` (nullable, só preenchido quando `tipo = TROCA_EMAIL`) —
migration `V7`. Motivo: fechar achado de segurança em que
`PUT /api/v1/usuarios/me` trocava `usuario.email` direto, sem senha nem
confirmação (qualquer requisição autenticada sequestrava o canal de
recuperação de conta). Ver
`contrato-usuario-conta-propria.md`, seção 4, para o endpoint que agora
gera token `tipo = TROCA_EMAIL`
(`POST /api/v1/usuarios/me/email/solicitar-troca`).

Diferença de destino do e-mail: token `CADASTRO` é enviado para o único
e-mail que a conta tem (o que está sendo confirmado). Token
`TROCA_EMAIL` é enviado para o e-mail **atual/antigo** da conta — nunca
para `novo_email` — porque a prova de posse que interessa aqui é "o dono
da caixa atual autoriza a troca", não "o dono da caixa nova existe".

**Atualização 2026-08-12 (`backend`) — TTL diferenciado por `tipo`**:
token `TROCA_EMAIL` usa TTL de 30 minutos, não mais 24h. Motivo: troca de
e-mail é ação mais sensível que confirmação de cadastro novo — o token
vai em query string (mesma exceção deliberada da seção 2), e uma janela
de 24h amplia demais o risco de um vazamento em log de proxy permitir
que outra pessoa confirme a troca dentro da validade. Token `CADASTRO`
mantém 24h (risco menor: não muda o e-mail de uma conta já existente,
só ativa a conta nova). Constantes em `AuthService`:
`TTL_TOKEN_CONFIRMACAO_CADASTRO` (24h) e `TTL_TOKEN_TROCA_EMAIL` (30min).

## 2. `GET /api/v1/auth/confirmar-email`

- **Autenticação**: nenhuma (rota pública, prova de posse é o token).
- **Idempotente**: sim — confirmar um token que já resultou em
  `email_confirmado_em` preenchido (mesma conta) responde sucesso, não
  erro. Justificativa: usuário pode clicar o link duas vezes (aba
  duplicada, retry de rede) sem ver mensagem de falha.
- **Verbo**: `GET`, seguindo o precedente de link clicável (o usuário
  chega a este endpoint clicando o botão do e-mail, não preenchendo
  formulário) — diferente de `POST /auth/redefinir-senha`, que exige
  formulário (nova senha) e por isso é `POST`. Aqui não há corpo de
  requisição, só o token. **Token vai em query string aqui — exceção
  deliberada e avaliada**: a regra geral do projeto proíbe token/segredo
  em URL porque QUERY STRING fica em log de proxy/browser history, mas
  este token é de uso único, TTL 24h, e o próprio valor perde utilidade
  após o primeiro uso (não é reutilizável como senha) — mesmo trade-off
  já aceito implicitamente por qualquer link de confirmação de e-mail de
  mercado. Ainda assim, o token nunca é logado pelo backend (nem em
  DEBUG), e a resposta não ecoa o token de volta.
- **Rate limit**: bucket por `hash(token)` (SHA-256, mesmo hash já
  calculado para a busca), 5/minuto — protege contra brute-force ainda
  que o espaço de 256 bits torne isso inviável na prática; consistência
  com o padrão de rate limit por identificador já usado em
  `redefinir-senha`.
- **Consistência**: forte, síncrona.

> **Atualização 2026-08-10 (`backend`)**: comportamento agora ramifica
> por `tipo` da linha do token encontrada por `token_hash`:
> - `tipo = CADASTRO` (default): comportamento inalterado, marca
>   `usuario.email_confirmado_em = now()`.
> - `tipo = TROCA_EMAIL`: copia `token.novo_email` para `usuario.email`
>   **e** marca `usuario.email_confirmado_em = now()` — é o único momento
>   em que `usuario.email` muda nesse fluxo, nunca no passo de
>   solicitação (`POST /usuarios/me/email/solicitar-troca`). Antes de
>   copiar, revalida que `novo_email` ainda não está em uso por outra
>   conta (409 `USUARIO_DADOS_EM_USO`) — outra conta pode ter registrado
>   esse e-mail no intervalo entre a solicitação e o clique no link.
>
> Idempotência do endpoint (token já usado + conta já no estado
> esperado → 200) vale igual para os dois tipos: se `tipo = TROCA_EMAIL`
> e `usuario.email` já é igual a `token.novo_email`, responde sucesso sem
> reprocessar.

**Request**: query param `token` (string, obrigatório).

**Response 200 (sucesso ou já confirmado antes — mesmo shape)**:
```json
{
  "success": true,
  "data": { "mensagem": "E-mail confirmado com sucesso." },
  "error": null
}
```

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | `token` ausente/vazio |
| 401 | `AUTH_EMAIL_CONFIRMACAO_TOKEN_INVALIDO` | token inexistente, expirado, ou usuário do token não existe mais |
| 409 | `USUARIO_DADOS_EM_USO` | `tipo = TROCA_EMAIL`: `novo_email` do token já está em uso por outra conta no momento da confirmação |
| 429 | `AUTH_RATE_LIMITED` | excesso de tentativas para o mesmo token |

Caso "já confirmado antes" **não é erro**: se o token já foi usado
(`usado_em` preenchido) mas a conta associada já está com
`email_confirmado_em` preenchido, responde sucesso (200) — trata como
idempotente. Se o token já foi usado mas por algum motivo a conta não
está confirmada (não deveria acontecer no fluxo normal), ainda assim
responde sucesso e corrige o estado, para não expor uma inconsistência
interna ao usuário final.

Token expirado (`expira_em` no passado) e nunca usado: erro
`AUTH_EMAIL_CONFIRMACAO_TOKEN_INVALIDO` — mensagem não diferencia
"expirado" de "inválido"/"inexistente" (evita side-channel sobre
existência de tokens gerados por outra pessoa), mas a tela de resultado
do frontend pode oferecer reenvio nesse caso (o frontend decide, não é
informação exposta pelo `error.code`).

## 3. `POST /api/v1/auth/reenviar-confirmacao-email`

- **Autenticação**: nenhuma (rota pública).
- **Idempotente**: sim, no sentido de resposta (sempre 200 genérico);
  cada chamada bem-sucedida internamente invalida o token anterior ainda
  válido daquela conta e gera um novo (mesmo padrão de
  `esqueciSenha` — token antigo perde utilidade prática porque um novo
  passa a ser o único caminho vivo, embora tecnicamente ainda não
  esteja marcado `usado_em`; se necessário no futuro, considerar marcar
  o anterior como usado explicitamente).
- **Rate limit**: dois buckets via `RateLimiterService`, seguindo o
  padrão de bucket duplo já usado em `registro`/
  `registro-varredura-global`:
  - Por `usuario_id` resolvido internamente: 3 por 10 minutos
    (`reenviar-confirmacao-email:hash(usuarioId)`). Números propostos
    pelo `backend`, sujeitos a revisão do `security`.
  - Global dedicado: 30 por minuto
    (`reenviar-confirmacao-email-varredura-global`), mesmo valor já
    usado em `registro-varredura-global`, mesmo racional (limitar
    varredura em massa sem depender de IP).
- **Anti-enumeration puro**: resposta **sempre** a mesma mensagem
  genérica 200, exista ou não a conta associada ao identificador — isso
  é diferente da exceção já registrada no `CLAUDE.md` para o 409 de
  cadastro (mensagem específica por campo). Essa exceção **não se
  aplica aqui**: aqui não há campo de formulário sendo validado no
  submit, é uma ação livre (”reenviar e-mail”) que qualquer visitante
  pode disparar contra qualquer identificador, cenário clássico de
  scraping de contas existentes.

**Request**:
```json
{ "identificador": "string, apelido OU e-mail" }
```
Mesmo shape de `EsqueciSenhaRequest`, por consistência.

**Response 200 (sempre, exista ou não a conta, já confirmado ou não)**:
```json
{
  "success": true,
  "data": {
    "mensagem": "Se o apelido ou e-mail existir em nossa base e o e-mail ainda não estiver confirmado, enviaremos um novo link de confirmação."
  },
  "error": null
}
```

**Lógica de decisão no servidor** (`AuthService.reenviarConfirmacaoEmail`):
1. Rate limit global (`reenviar-confirmacao-email-varredura-global`).
2. Resolve usuário por `identificador` (mesma query de
   `findByApelidoIgnoreCaseOrEmailIgnoreCase`).
3. `Optional` vazio → não faz nada, cai na resposta genérica.
4. `Optional` presente e `email_confirmado_em` já preenchido → não faz
   nada (idempotente, sem reenviar nem gerar novo token), cai na mesma
   resposta genérica — não revela ao chamador que a conta já está
   confirmada.
5. `Optional` presente, e-mail ainda não confirmado → rate limit por
   `usuario_id`, gera novo token, dispara envio assíncrono.
6. Resposta genérica idêntica em todos os casos.

**Erros**: só `429 AUTH_RATE_LIMITED` (rate limit global,
`checarRateLimitConta` por identificador não se aplica aqui do mesmo
jeito que em `esqueciSenha` porque a resposta é sempre 200 mesmo com
rate limit por usuário — rate limit por usuário estourado ainda assim
retorna a mensagem genérica 200, não 429, para não vazar timing de
"conta existe e está sendo limitada"; só o bucket **global** de
varredura de fato retorna 429 ao chamador, pois esse não distingue por
conta) e `400 VALIDATION_ERROR` (identificador vazio).

## 4. Restrição de fluxos que exigem e-mail confirmado

Dois pontos do sistema passam a checar `email_confirmado_em != null`
antes de prosseguir:

### 4.1 Recuperação de senha por e-mail (ramo usuário comum)
`AuthService.esqueciSenha`, dentro de `gerarEEnviarTokenComum`: se
`usuario.getEmailConfirmadoEm() == null`, **não gera nem envia token**
— mas a resposta HTTP continua a mesma mensagem genérica de sempre (não
é um erro novo visível, para não vazar se a conta existe e está
pendente de confirmação). Efeito prático: usuário com e-mail não
confirmado que tenta recuperar senha recebe a mesma mensagem de sempre,
mas nunca recebe o e-mail de fato — mitigação para não criar um oráculo
de enumeration cruzado com o estado de confirmação. **Ramo admin não é
afetado** (admins sempre têm e-mail já confirmado por serem contas
criadas/promovidas por outro admin, fora do fluxo de auto-cadastro —
sem checagem adicional necessária neste ramo).

### 4.2 Compra física na loja
**Não implementado ainda** — não existe endpoint de loja/compra no
backend nesta data (`2026-08-09`). Ponto de extensão documentado aqui
para quando a feature de loja for criada: o service correspondente deve
checar `usuario.getEmailConfirmadoEm() == null` e lançar
`ConflitoDeEstadoException` (ou exceção equivalente já usada no domínio
de loja) com um `error.code` a definir naquele momento (ex:
`LOJA_EMAIL_NAO_CONFIRMADO`), mensagem indicando a necessidade de
confirmar o e-mail antes de prosseguir. Fica registrado em
`PENDENCIAS.md` como dependência cruzada.

## 5. Tabela `email_confirmacao_token`

Migration `V5__cria_tabela_email_confirmacao_token.sql`, mesma
convenção estrutural de `admin_reset_codigo` (`V4`):

```sql
CREATE TABLE email_confirmacao_token (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    usuario_id uuid NOT NULL,
    token_hash character varying(255) NOT NULL,
    expira_em timestamp with time zone NOT NULL,
    usado_em timestamp with time zone,
    criado_em timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT email_confirmacao_token_pkey PRIMARY KEY (id),
    CONSTRAINT email_confirmacao_token_token_hash_key UNIQUE (token_hash),
    CONSTRAINT email_confirmacao_token_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE INDEX idx_email_confirmacao_token_usuario ON email_confirmacao_token USING btree (usuario_id);
```

> **Atualização 2026-08-10 (`backend`)**, migration `V7` (só DDL, expand
> puro): `email_confirmacao_token` ganhou 2 colunas.
> ```sql
> ALTER TABLE email_confirmacao_token ADD COLUMN tipo character varying(20) NOT NULL DEFAULT 'CADASTRO';
> ALTER TABLE email_confirmacao_token ADD COLUMN novo_email character varying(255);
> ```
> `tipo` fica com `DEFAULT 'CADASTRO'` permanentemente (não é removido
> numa migration de contração): linhas históricas e o fluxo de cadastro
> continuam a fazer sentido com esse default, diferente de uma coluna
> que precisaria virar obrigatória sem default depois — aqui o default
> é semanticamente correto para sempre, não é um remendo temporário.
> `novo_email` fica nullable (só preenchido quando `tipo = TROCA_EMAIL`).

Coluna nova em `usuario` (mesma migration `V5`, expand puro — aditiva,
nullable, sem default forçado):
```sql
ALTER TABLE usuario ADD COLUMN email_confirmado_em timestamp with time zone;
```

Contas já existentes ficam com `email_confirmado_em IS NULL` (pendente)
até confirmarem — comportamento aceito, consistente com "não bloqueia
login", só passam a ver o banner de pendência quando o frontend
implementar essa parte.

## 6. Erros novos no catálogo (`contrato-erro-padrao.md`)

| code | HTTP | quando |
|---|---|---|
| `AUTH_EMAIL_CONFIRMACAO_TOKEN_INVALIDO` | 401 | confirmar e-mail: token ausente, expirado, ou usuário do token não existe mais |

`AUTH_RATE_LIMITED` (já existente) passa a cobrir também
`/auth/confirmar-email` (por hash do token) e
`/auth/reenviar-confirmacao-email` (bucket global de varredura).

`USUARIO_DADOS_EM_USO` (já existente, catálogo de `usuario`) passa a
também poder ser retornado por `GET /auth/confirmar-email` quando
`tipo = TROCA_EMAIL` (ver seção 2, 2026-08-10).

## 7. Observabilidade

Logs seguem o padrão já usado em `AuthService` (`hmacHasher.hash(...)`
do identificador/usuarioId em `log.info`/`log.warn`, nunca o token nem
e-mail em claro). Token e seu hash SHA-256 nunca aparecem em nenhum
nível de log — nem para debug.

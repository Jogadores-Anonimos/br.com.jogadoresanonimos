# Contrato de API — Recuperação de Senha

> Escopo: **um único endpoint de solicitação** (`POST /api/v1/auth/esqueci-senha`),
> que decide internamente no servidor se o destinatário recebe link
> (usuário comum) ou código de 6 dígitos (admin). O endpoint de
> confirmação continua separado por tipo de conta: usuário comum troca a
> senha com o token da URL (`/auth/redefinir-senha`), admin troca a senha
> com o código digitado (`/admin/auth/redefinir-senha`).

Convenções gerais (mesmas do resto do projeto): envelope
`{ success, data, error }`, paths sob `/api/v1`, nenhum token/código em
query string (sempre corpo), datas ISO-8601 UTC. Catálogo completo de
`error.code` em `contrato-erro-padrao.md`.

## Revisão de desenho (2026-08-08) — o que mudou e por quê

**Desenho anterior** (primeira versão deste documento, já implementada
nesta mesma sessão antes desta revisão): dois endpoints de solicitação
distintos — `POST /api/v1/auth/esqueci-senha` (comum) e
`POST /api/v1/admin/auth/esqueci-senha` (admin), cada um com seu próprio
rate limit e mensagem genérica. **Esse desenho foi abandonado antes de
qualquer consumo pelo frontend** (não há tela publicada, não é breaking
change) — motivo da revisão:

- Ter dois paths de solicitação diferentes já é, por si só, um sinal
  binário: a existência de `/admin/auth/esqueci-senha` como rota
  publicamente descobrível (ex. um scanner batendo em `/admin/*`) convida
  a tentar diferenciar admin de usuário comum sem nem precisar de
  timing/enumeration — o path escolhido já é metadado. Unificar em um
  único endpoint remove essa superfície por completo: de fora, não existe
  "o endpoint que os admins usam".
- O comportamento desejado (mensagem idêntica, branch de e-mail
  diferente) sempre dependeu de resolver `identificador → Usuario` de
  qualquer forma — a decisão real sempre foi no servidor, nunca no
  cliente. Ter dois controllers fazendo a mesma resolução de usuário e
  chamando rate limiters em paralelo é duplicação sem ganho: o
  `AuthService.esqueciSenha` já existente passa a ser o único ponto de
  decisão.
- Simplifica a spec de rate limit: um bucket por identificador e um
  bucket de varredura global, não dois pares redundantes (um por
  endpoint).

**Endpoint removido nesta revisão**: `POST /api/v1/admin/auth/esqueci-senha`
deixa de existir. Não chegou a ser consumido por nenhuma tela publicada
(UI do fluxo admin ainda não foi implementada pelo `ux-ui`/`frontend`) —
não é breaking change de contrato já em uso pelo frontend em produção,
mas fica registrado que um service Angular (`AdminAuthService.esqueciSenha`
em `frontend/src/app/services/admin-auth/admin-auth.ts`) foi escrito
apontando para esse path na mesma sessão e agora está órfão; ajuste é
tarefa do `frontend-services`, fora do escopo deste documento (backend).

**O que não muda**: endpoint de confirmação do admin
(`POST /api/v1/admin/auth/redefinir-senha`), entidade `AdminResetCodigo`,
`AdminResetCodigoRepository`, migration `admin_reset_codigo`, `error.code`
`ADMIN_RESET_CODIGO_INVALIDO`, TTL de 10 minutos, rate limit por código,
e a garantia de não-bypass do MFA TOTP — nada disso muda nesta revisão.

---

## 1. `POST /api/v1/auth/esqueci-senha` (único endpoint de solicitação)

- **Autenticação**: nenhuma (rota pública).
- **Idempotente**: sim (múltiplas chamadas com mesmo identificador geram
  cada uma um novo token/código, invalidando implicitamente o anterior
  ainda válido daquela mesma conta).
- **Rate limit**: bucket por `hash(identificador normalizado)`
  (5/minuto, `AuthService.checarRateLimitConta`, mesma constante já usada
  por `login`/`registro`/`redefinir-senha`). **Sem bucket de varredura
  global dedicado nesta revisão** — diferente do desenho anterior (que
  tinha `admin-esqueci-senha-varredura-global` de 5/minuto só para o
  path admin): como agora é o mesmo endpoint do fluxo comum, ele já não
  tinha bucket de varredura global própria antes desta revisão (só o
  `registro-varredura-global`, que é de outro endpoint), então não é
  introduzido um bucket novo aqui — decisão de manter o escopo desta
  revisão restrito à unificação de endpoint, sem propor mitigação nova de
  scraping. Se o `security` julgar necessário um bucket de varredura
  global para `/auth/esqueci-senha` (comum + admin combinados), é uma
  decisão separada, não incluída nesta rodada.
- **Consistência**: forte na escrita do token (comum) ou código (admin);
  envio de e-mail é assíncrono nos dois casos (ver seção 3, timing).

**Request**:
```json
{ "identificador": "string, apelido OU e-mail" }
```
Mesmo shape usado desde a primeira versão deste contrato — não muda.

**Response 200 (sempre, exista ou não a conta, seja ela admin ou não)**:
```json
{
  "success": true,
  "data": {
    "mensagem": "Se o apelido ou e-mail existir em nossa base, enviaremos um link de redefinição."
  },
  "error": null
}
```
**Mensagem fixa e única** — não varia por tipo de conta (nem para admin,
nem para usuário comum, nem para conta inexistente). O texto menciona
"link" mesmo quando o servidor efetivamente vai enviar um código (caso
admin) — isso é deliberado: qualquer diferença de texto entre os dois
casos seria, por si só, o oráculo que este desenho existe para evitar. Um
admin que já sabe seu próprio fluxo entende, pelo e-mail recebido, que
deve digitar um código; o texto da resposta HTTP não precisa (e não deve)
adiantar isso.

**Erros**: só `429 AUTH_RATE_LIMITED` e `400 VALIDATION_ERROR` (corpo
ausente/identificador vazio) chegam a existir — qualquer outra situação
de negócio (conta não existe, existe mas é admin, existe e não é admin)
é absorvida no 200 genérico acima.

### 1.1 Lógica de decisão no servidor (`AuthService.esqueciSenha`)

Único ponto de decisão do sistema — implementado em
`backend/src/main/java/br/com/jogadoresanonimos/auth/service/AuthService.java`:

1. Rate limit por identificador (ver acima).
2. Resolve usuário por `identificador` (apelido OU e-mail,
   `UsuarioRepository.findByApelidoIgnoreCaseOrEmailIgnoreCase`, mesma
   query do login).
3. `Optional` vazio → não faz nada (nenhum token, nenhum código, nenhum
   e-mail). Cai direto na resposta genérica.
4. `Optional` presente e `usuario.getRole() != Role.ADMIN` → gera token
   de reset (comportamento inalterado desde a primeira versão, ver seção
   2 abaixo).
5. `Optional` presente e `usuario.getRole() == Role.ADMIN` → gera código
   de 6 dígitos (reaproveita a entidade/repositório já existentes de
   `AdminResetCodigo`/`AdminResetCodigoRepository`, ver seção 3 abaixo) —
   a única mudança de fato é **quem invoca** essa lógica: antes era
   `AdminAuthService.esqueciSenha` (endpoint próprio), agora é um branch
   privado dentro de `AuthService.esqueciSenha`.
6. Resposta genérica idêntica em todos os casos.

`AdminAuthService` deixou de ter o método `esqueciSenha` — mantém
somente `redefinirSenha` (ver seção 4). `EmailService` não muda: os dois
métodos já existentes (`enviarLinkRedefinicaoSenha`,
`enviarCodigoRedefinicaoSenhaAdmin`) continuam existindo, só passam a ser
chamados a partir de um único ponto de decisão (`AuthService`) em vez de
dois controllers/services separados.

---

## 2. Ramo usuário comum (token de 32 bytes, link por e-mail)

Comportamento **inalterado** desde a primeira versão do contrato
(`contrato-auth-cms-upload.md`, seção 1.3, ainda referenciada como
histórico, mas a integração agora é via `AuthService.esqueciSenha`
diretamente, não um endpoint dedicado no controller — o controller nunca
mudou, é o mesmo `AuthController.esqueciSenha` de sempre):

- Gera token bruto de 32 bytes (`SecureRandom`, Base64 URL-safe), grava
  só o **hash HMAC** em `password_reset_token.token_hash` (nunca o token
  em claro), TTL de 30 minutos.
- Dispara `EmailService.enviarLinkRedefinicaoSenha` (assíncrono).
- Confirmação em `POST /api/v1/auth/redefinir-senha` — sem mudança
  (token, `hash(token) == token_hash`, `PasswordResetToken.valido()`,
  erro único `401 AUTH_RESET_TOKEN_INVALIDO`, uso único via `usado_em`).

---

## 3. Ramo admin (código de 6 dígitos, nunca link)

Comportamento de geração **inalterado** desde a primeira versão deste
contrato — só a orquestração (quem chama) mudou, ver seção 1.1.

### Por que um código continua separado de um link, mesmo com endpoint unificado
- **Nunca um link** para admin: reduz superfície de phishing/link
  forjado num público com maior privilégio (`ROLE_ADMIN`) e evita que o
  código de reset trafegue em URL (histórico de navegador, logs de
  proxy) — o código é digitado manualmente na tela do site, nunca
  clicado a partir do e-mail.
- Código curto (6 dígitos) é digitação rápida, modelo mental parecido com
  o `codigoTotp` do MFA já existente — mas **não substitui nem interage
  com o TOTP** (ver seção 5, garantia de não-bypass).
- `AdminResetCodigo` continua **paralela e independente** de
  `PasswordResetToken` — formato do segredo é fundamentalmente diferente
  (dígitos vs. token opaco), TTL mais curto (10min vs 30min); misturar
  num campo polimórfico único não se justifica dado o volume baixo do
  projeto.

### Geração (dentro de `AuthService.gerarEEnviarCodigoAdmin`, privado)
1. Gera código numérico de 6 dígitos (`SecureRandom`,
   `String.format("%06d", ...)`).
2. `hash(codigo)` via `HmacHasher` (mesma classe usada em toda parte do
   projeto para hash de identificador/token).
3. Persiste em `admin_reset_codigo` com TTL de 10 minutos.
4. Em colisão de `codigo_hash` (constraint `UNIQUE`,
   `DataIntegrityViolationException`): retry automático, até 5
   tentativas, gerando novo código a cada uma — mesma lógica já existente
   antes da unificação, só moveu de classe.
5. Dispara `EmailService.enviarCodigoRedefinicaoSenhaAdmin` (assíncrono),
   com o código em texto (não um link).

### Tabela `admin_reset_codigo`
Sem mudança de schema nesta revisão — migration já existente (`V4`,
conferir número real aplicado em
`backend/src/main/resources/db/migration/`) permanece válida. Estrutura:
hash do código (nunca o código em claro), `expira_em`, `usado_em`
nullable (flag de uso único), `tentativas_invalidas` mantida só como
campo de extensão futura (não incrementada nem lida por este contrato —
ver justificativa completa na análise de força-bruta já registrada
anteriormente neste projeto, inalterada nesta revisão).

---

## 4. `POST /api/v1/admin/auth/redefinir-senha` (confirmação — sem mudança)

Endpoint de confirmação do admin **não é tocado por esta revisão** —
continua existindo exatamente como já implementado:

- **Autenticação**: nenhuma (a prova de identidade é o código).
- **Idempotente**: não (primeira chamada válida consome o código).
- **Rate limit**: por `hash(codigo)`, 5/minuto.
- **Consistência**: forte, síncrona.

**Request**:
```json
{
  "codigo": "string, 6 dígitos numéricos",
  "novaSenha": "string, mínimo 10 chars",
  "confirmarNovaSenha": "string, deve bater com novaSenha"
}
```

**Response 200**:
```json
{
  "success": true,
  "data": { "mensagem": "Senha redefinida com sucesso." },
  "error": null
}
```

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | `codigo` ausente/não numérico/tamanho diferente de 6, senha curta, ou `novaSenha != confirmarNovaSenha` |
| 401 | `ADMIN_RESET_CODIGO_INVALIDO` | código inválido, expirado ou já utilizado — mensagem genérica única |
| 429 | `AUTH_RATE_LIMITED` | excesso de tentativas para o mesmo código apresentado |

Implementação: `AdminAuthController.redefinirSenha` →
`AdminAuthService.redefinirSenha` — classes mantidas (não removidas),
só perderam o método `esqueciSenha` que não existe mais nelas.

---

## 5. Garantia de não-bypass do MFA TOTP — inalterada

Reafirmando a restrição crítica deste fluxo, que **não muda** com a
unificação: este endpoint (`/admin/auth/redefinir-senha`) troca **somente
a senha**. Se `usuario.mfa_ativo = true`, o próximo `POST /auth/login`
continua exigindo `POST /auth/login/mfa` normalmente — reset de senha
nunca é caminho de bypass do segundo fator, nunca toca
`mfa_ativo`/`mfa_secret_cifrado`, nunca retorna `LoginResponse`/JWT. Ver
`contrato-mfa-admin.md`. A unificação do endpoint de **solicitação**
(seção 1) não tem qualquer relação com esta garantia — ela vive
inteiramente no endpoint de **confirmação** (seção 4), que não foi
alterado.

---

## 6. Timing — análise para revisão do `security`

Ponto levantado explicitamente para esta revisão: como agora os dois
ramos (comum vs. admin) partem do mesmo endpoint, uma diferença de tempo
de resposta observável entre eles seria um side-channel revelando se a
conta é admin — o mesmo tipo de risco que a mensagem genérica já mitiga
no corpo da resposta, mas por outro canal (tempo, não conteúdo).

**Trabalho síncrono comparável nos dois ramos** (acontece antes da
resposta HTTP ser enviada, portanto potencialmente observável por quem
mede o tempo de resposta):
- Resolução do usuário: idêntica nos dois ramos (mesma query, roda uma
  vez só, antes do branch — não duplica custo).
- Geração de segredo aleatório: `SecureRandom` para 32 bytes (comum) vs.
  `SecureRandom.nextInt(1_000_000)` (admin) — ambos são operações de CPU
  da ordem de microssegundos, diferença não mensurável de forma
  confiável sobre a latência de rede/HTTP real (que já introduz jitter
  de milissegundos, ordens de magnitude maior).
- `HmacHasher.hash(...)`: mesma função, mesmo custo computacional nos
  dois ramos (HMAC-SHA256 sobre um valor de tamanho comparável — token
  Base64 de 32 bytes vs. string de 6 dígitos, ambos processados em uma
  única chamada HMAC, custo dominado pelo tamanho de bloco do algoritmo,
  não pelo tamanho da entrada nesta escala).
- Persistência: um único `INSERT` em ambos os casos
  (`password_reset_token` vs. `admin_reset_codigo`), mesma forma de
  tabela (poucos campos, sem índice adicional custoso na escrita),
  latência de I/O de banco comparável. **Exceção**: o ramo admin tem um
  loop de retry em caso de colisão de `UNIQUE` (até 5 tentativas) — na
  prática, colisão é extremamente rara (espaço de 10^6 valores, poucos
  admins gerando código simultaneamente), então o caminho feliz (uma
  tentativa, sem colisão) é o caso dominante e comparável ao ramo comum.
  Se colisão ocorrer, o ramo admin fica mensuravelmente mais lento
  naquela requisição específica — risco residual aceito: é um evento raro
  o suficiente (produto de duas probabilidades baixas: ser admin E
  colidir no espaço de 6 dígitos) para não constituir um side-channel
  praticamente explorável, mas fica registrado para o `security` avaliar
  se quer mitigação adicional (ex: jitter artificial no retry).

**Ponto decisivo — envio de e-mail é assíncrono nos dois ramos**: tanto
`enviarLinkRedefinicaoSenha` quanto `enviarCodigoRedefinicaoSenhaAdmin`
são anotados `@Async` em `EmailService`. Isso significa que a chamada ao
método de envio **retorna imediatamente** (dispara a execução em outra
thread) e o método `esqueciSenha` do `AuthService` prossegue e retorna a
resposta HTTP sem esperar o envio real completar (nem sucesso, nem os
retries com backoff exponencial de 2s/6s/18s em caso de falha). Portanto:
**a diferença de I/O de e-mail entre os dois ramos não é, em nenhuma
hipótese, observável na resposta HTTP** — ela acontece inteiramente depois
da resposta já ter sido enviada ao cliente. Esse é o fator que mais
poderia introduzir diferença de timing (chamada de rede a um provedor de
e-mail é ordens de magnitude mais lenta que hash/persistência local), e
ele já está neutralizado pelo desenho existente, sem necessidade de
mudança nesta revisão.

**Conclusão da análise**: o custo síncrono dos dois ramos é comparável
(diferença na casa de microssegundos entre operações de CPU, mascarada
pelo jitter normal de rede/HTTP), e o único fator que poderia introduzir
diferença grande (I/O de e-mail) já é assíncrono e não observável na
resposta. Nenhuma mitigação adicional de timing (ex: padding artificial
de latência, técnica de "resposta em tempo constante") é proposta nesta
revisão — fica como decisão explícita do `security` revisar e, se
julgar insuficiente (ex: por causa do caso de colisão com retry citado
acima), pedir ajuste em rodada futura.

### 6.1 Decisão do `security` (2026-08-08) — retry de colisão não é side-channel explorável

Avaliado especificamente o risco residual apontado pelo `backend`: o
loop de retry em `AuthService.gerarEEnviarCodigoAdmin` (colisão de
`UNIQUE(codigo_hash)`) introduz latência extra mensuravelmente maior
**só** no ramo admin, só quando colide. **Decisão: risco aceito, sem
mitigação (sem jitter artificial).** Motivos, combinados:

1. **Probabilidade de colisão por tentativa é baixa e limitada pelo
   volume do projeto.** Espaço de 10^6 códigos, TTL de 10 minutos,
   volume total do site (~300 pedidos/dia). Mesmo com uma estimativa
   deliberadamente folgada (dezenas de admins gerando código na mesma
   janela de 10 minutos — já otimista demais para a escala real deste
   projeto), a chance de colisão numa tentativa fica na ordem de
   10^-4–10^-3. Não é o volume de linhas na tabela ao longo do tempo que
   importa aqui, é quantos códigos **ativos simultaneamente** existem —
   e isso é sempre pequeno neste projeto.
2. **O rate limit por identificador já neutraliza a amostragem
   necessária para separar sinal de ruído.** `checarRateLimitConta`
   limita a 5 requisições/minuto por identificador (seção 1, aplicado
   igualmente nos dois ramos, antes do branch). Para diferenciar
   estatisticamente "retry raro (~0,1% das requisições)" do jitter
   normal de rede/HTTP (que a análise acima já qualifica como ordens de
   magnitude maior que a diferença de CPU/I/O local entre os ramos), um
   atacante precisaria de um volume de amostras que o rate limit já
   impede de coletar contra um único identificador.
3. **Pré-requisito lógico enfraquece ainda mais o ataque.** Para que
   medir timing nesse identificador específico faça sentido, o atacante
   já precisaria suspeitar que aquele identificador é admin — ou seja,
   já teria comprometido o sigilo por outro meio antes de chegar a este
   side-channel. Não é um vetor de descoberta primária, é uma
   confirmação marginal de um sinal que exigiria amostragem já barrada
   pelo item 2.
4. **Magnitude do delay extra é pequena em termos absolutos.** Uma
   colisão dispara `SecureRandom.nextInt` + `HmacHasher.hash` + um novo
   `INSERT` — todas operações locais na casa de baixo milissegundo, não
   uma chamada de rede. Comparável em ordem de grandeza ao próprio
   jitter que a seção acima já reconhece como mascarando a diferença
   normal entre os ramos, não uma ordem de magnitude acima dele.

**Trade-off explícito**: jitter artificial de latência no retry
reduziria ainda mais esse resíduo já pequeno, mas adicionaria
complexidade permanente (branch de delay sintético, mais um parâmetro
para calibrar) para mitigar um risco cujo produto de probabilidades já
é desprezível dado o rate limit e o volume reais deste projeto — não se
justifica dado o "Contexto de escala" do `CLAUDE.md`. **Gatilho de
revisão futura**: se o rate limit por identificador for relaxado (hoje
5/min) ou o volume de contas admin crescer ordens de magnitude além do
atual, reavaliar — nenhuma mudança de código necessária agora.

---

## Resumo — o que muda de fato nesta revisão

- **Removido**: `POST /api/v1/admin/auth/esqueci-senha`
  (`AdminAuthController`, `AdminAuthService.esqueciSenha`). Nenhum código
  de erro removido do catálogo (nada era exclusivo desse endpoint).
- **Alterado**: `POST /api/v1/auth/esqueci-senha`
  (`AuthController.esqueciSenha` → `AuthService.esqueciSenha`) passa a
  decidir internamente entre token (comum) e código (admin), com a mesma
  resposta genérica nos dois casos. Mesmo path, mesmo request/response
  shape de antes — não é breaking change para quem já consome esse
  endpoint (usuário comum), só passa a também cobrir o caso admin.
- **Inalterado**: `POST /api/v1/auth/redefinir-senha` (comum),
  `POST /api/v1/admin/auth/redefinir-senha` (admin, confirmação),
  entidade/tabela `admin_reset_codigo`, `error.code`
  `ADMIN_RESET_CODIGO_INVALIDO`, TTL de 10 minutos do código admin,
  garantia de não-bypass do MFA TOTP.
- **Pendência sinalizada, fora de escopo backend**: frontend já tinha um
  service (`AdminAuthService.esqueciSenha` em
  `frontend/src/app/services/admin-auth/admin-auth.ts`) apontando para o
  endpoint agora removido — não há tela publicada consumindo, mas o
  código do service ficou órfão; ajuste é tarefa do `frontend-services`.
- **Validado pelo `security` (2026-08-08)**: código em
  `AuthService.java` confere com este contrato — resposta genérica
  idêntica nos dois ramos (sem variação de corpo/status), MFA TOTP não
  tocado por este fluxo (login/loginComMfa inalterados, nenhum acesso a
  `mfaAtivo`/`mfaSecretCifrado` em `esqueciSenha`/`redefinirSenha`),
  rate limit por identificador aplicado antes do branch admin/comum nos
  dois casos. Risco de timing do retry de colisão (seção 6) avaliado e
  aceito sem mitigação — ver decisão detalhada na seção 6.1.

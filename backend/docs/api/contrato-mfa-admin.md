# Contrato de API — MFA (TOTP) para Admin

> Status: especificado e implementado por `security` em 2026-07-15.
> Escopo: MFA opcional (ativável pelo próprio admin) via TOTP (RFC 6238),
> compatível com Google Authenticator/Authy/1Password. Aplica-se somente
> a usuários `ROLE_ADMIN`; participantes não têm MFA.

Convenções gerais (mesmas do resto do projeto): envelope
`{ success, data, error }`, paths sob `/api/v1`, nenhum segredo em query
string, datas ISO-8601 UTC.

## Decisão: MFA opcional, não obrigatório
Admin ativa quando quiser — sem forçar enrollment no primeiro login.
Simplicidade > compliance rígida, dado o volume baixo do projeto e o
público interno (poucos admins). Reavaliar para obrigatório só se o
número de admins crescer ou houver incidente que justifique.

## Decisão: algoritmo e parâmetros TOTP
- RFC 6238 padrão: HMAC-SHA1, 6 dígitos, período de 30s, janela de
  tolerância de ±1 step (aceita código do step anterior/seguinte, cobre
  clock drift/latência de digitação — mesmo default usado por
  Google Authenticator e a maioria das libs de mercado).
- **HMAC-SHA1** (não SHA256) é intencional aqui, apesar de SHA1 ser fraco
  para outros usos: é o único algoritmo realmente interoperável com os
  apps autenticadores populares (Google Authenticator ignora o parâmetro
  `algorithm` da URI e sempre usa SHA1). Usar SHA256/512 quebraria a
  compatibilidade sem ganho de segurança real (o HMAC em si não é o elo
  fraco do TOTP).
- Sem dependência nova: implementação própria (`TotpService`) usando
  `javax.crypto.Mac`, mesmo padrão já usado em `HmacHasher`. Biblioteca
  dedicada (ex. `java-otp`) não foi adicionada — RFC 6238 é ~30 linhas de
  código sobre HOTP (RFC 4226), não justifica dependência nova nesta
  escala de uso (só admins).

## Decisão: armazenamento do secret
- Secret TOTP (20 bytes aleatórios, Base32) é **cifrado** (não hash) no
  banco — precisa ser recuperado em texto claro a cada login para
  recalcular o código esperado, hash unidirecional não serve aqui.
- Cifra: **AES-256-GCM**, chave de 256 bits via env var
  `MFA_SECRET_ENCRYPTION_KEY` (Base64, 32 bytes decodificados), nunca
  hardcoded — mesmo padrão de `JWT_SECRET`/`LOG_HMAC_SECRET`. Nonce (12
  bytes) gerado por chamada via `SecureRandom`, armazenado concatenado
  ao ciphertext (não é secreto, só precisa ser único por cifragem).
- Coluna `usuario.mfa_secret_cifrado` (TEXT, Base64 de `nonce || ciphertext || tag`),
  nullable — só populada quando o admin ativa MFA.
- Coluna `usuario.mfa_ativo` (BOOLEAN NOT NULL DEFAULT false) — distingue
  "tem secret gerado mas não confirmado" (enrollment em andamento) de
  "MFA realmente exigido no login". Só existe secret pendente durante a
  janela entre enroll e confirmação; se o admin nunca confirmar, o
  secret antigo é sobrescrito na próxima tentativa de enroll (sem tabela
  extra de estado).

## Decisão: fluxo de login em duas etapas
`POST /auth/login` continua validando senha/recaptcha normalmente. Se o
usuário for `ROLE_ADMIN` **e** `mfa_ativo = true`, a resposta **não**
inclui token — retorna um desafio MFA (sem revelar isso a
não-admins nem a admins sem MFA ativo, que recebem o login normal já
com JWT, sem mudança de contrato para eles).

Não foi criado um "token de sessão intermediário" (ex: um segundo JWT de
curta duração para representar "senha ok, aguardando TOTP") por
simplicidade — o desafio é resolvido reenviando as mesmas credenciais
(`identificador` + `senha` + `recaptchaToken`) mais o `codigoTotp` num
único endpoint novo. Evita estado de sessão parcial no servidor e mais
uma tabela/cache. Trade-off aceito: o admin digita a senha de novo se
precisar tentar outro código TOTP — fricção mínima, aceitável no volume
do projeto.

### `POST /api/v1/auth/login` (contrato já existente, resposta estendida)
- **Autenticação**: nenhuma.
- Mesma validação de rate limit/recaptcha/senha já documentada em
  `contrato-auth-cms-upload.md` (seção 1.2) — nada muda nessa parte.

**Response 200 — admin com MFA ativo (novo caso)**:
```json
{
  "success": true,
  "data": {
    "mfaRequerido": true,
    "token": null,
    "expiraEm": null,
    "usuario": null
  },
  "error": null
}
```
**Response 200 — fluxo normal (participante, ou admin sem MFA ativo)**:
```json
{
  "success": true,
  "data": {
    "mfaRequerido": false,
    "token": "jwt string",
    "expiraEm": "2026-07-15T15:30:00Z",
    "usuario": { "id": "uuid", "apelido": "string", "role": "ADMIN" }
  },
  "error": null
}
```
`LoginResponse` ganhou o campo `mfaRequerido` (boolean, default `false`
para não quebrar clientes existentes que ignoram campo desconhecido).
`token`/`expiraEm`/`usuario` são `null` somente quando `mfaRequerido =
true` — **nenhum JWT é emitido nessa resposta**, mesmo que a senha
estivesse correta.

**Erros**: iguais aos já documentados (400/401/429/502). Um código
TOTP não entra aqui — a senha já foi validada como correta; o desafio
MFA é resolvido no endpoint abaixo.

---

### `POST /api/v1/auth/login/mfa` (novo, resolve o desafio)
- **Autenticação**: nenhuma (equivalente a login — a prova de identidade
  é senha + TOTP, não uma sessão prévia).
- **Idempotente**: sim (não muda estado em caso de sucesso, exceto
  rate limit).
- **Rate limit**: mesmo bucket duplo do login (`usuario_id` resolvido +
  identificador bruto normalizado), **mais um limite dedicado e mais
  agressivo por usuário para tentativas de TOTP** (ex. 5 tentativas por
  5 minutos) — o espaço de códigos de 6 dígitos é pequeno (10^6), TOTP
  sem rate limit próprio é força-bruta viável em uma janela de 30s.
- **Consistência**: forte, leitura do primário (mesmo motivo do login).

**Request**:
```json
{
  "identificador": "string, apelido OU e-mail",
  "senha": "string",
  "recaptchaToken": "string, aceito mas não validado (ver nota abaixo)",
  "codigoTotp": "string, 6 dígitos"
}
```
Reenvia as mesmas credenciais do login original (ver decisão acima) —
não depende de estado de sessão intermediário no servidor.

`recaptchaToken` **não é validado contra o Google nesta etapa** — o
token do `siteverify` é de uso único (a própria API do Google invalida
após o primeiro `success`), e o frontend reenvia o mesmo token gerado no
passo 1. Validar de novo aqui sempre falharia em produção
(`timeout-or-duplicate`), travando o admin no 2º fator. O captcha já foi
verificado em `POST /api/v1/auth/login` antes de saber que MFA seria
exigido — não faz sentido provar humanidade duas vezes na mesma
tentativa de login. Campo continua obrigatório (`@NotBlank`) só para não
quebrar o contrato de request existente; o valor em si é ignorado pelo
backend. Abuso do endpoint sem captcha é coberto pelo rate limit
dedicado de MFA (bucket `login-mfa`, ver linha "Rate limit" acima), que
não depende de captcha.

**Response 200** (mesmo shape do login normal, sempre com token):
```json
{
  "success": true,
  "data": {
    "mfaRequerido": false,
    "token": "jwt string",
    "expiraEm": "2026-07-15T15:30:00Z",
    "usuario": { "id": "uuid", "apelido": "string", "role": "ADMIN" }
  },
  "error": null
}
```

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | `codigoTotp` ausente/não numérico/tamanho errado, ou `recaptchaToken` ausente (formato, não validade) |
| 401 | `UNAUTHENTICATED` | identificador/senha inválidos, usuário não é admin com MFA ativo (ataque de replay de fluxo), ou `codigoTotp` incorreto — mensagem genérica única, mesmo padrão anti-enumeration do login |
| 429 | `RATE_LIMITED` | excesso de tentativas (bucket de login ou bucket específico de TOTP) |

`502 UPSTREAM_ERROR` não se aplica aqui (sem chamada ao `siteverify`
nesta etapa).

Ordem de validação no `AuthService`: rate limit (login + login-mfa) →
senha → checar `mfa_ativo` (se `false`, mesmo erro genérico 401 — não
confirma nem nega a existência de MFA para o identificador, evita
oráculo) → decifrar secret → validar TOTP com janela ±1 step → emitir
JWT.

---

## Bloco MFA — Enrollment (admin autenticado, self-service)

Todas as rotas abaixo exigem `ROLE_ADMIN` já autenticado (JWT do login
normal, sem MFA — o próprio admin ativa o segundo fator depois de já
estar logado). Prefixo `/api/v1/admin/mfa`.

### `POST /api/v1/admin/mfa/enroll`
- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: não — cada chamada gera e substitui um secret pendente
  novo (sobrescreve qualquer enrollment anterior não confirmado).
- **Rate limit**: baixo (5/hora por admin) — não é rota de alto risco de
  abuso externo (exige já estar autenticado), mas evita geração
  descontrolada de secrets.
- **Consistência**: forte, síncrona.
- **Pré-condição**: `mfa_ativo` deve ser `false`. Se já ativo, retorna
  `409 CONFLICT` — admin precisa desativar antes de reconfigurar (ver
  endpoint de desativação abaixo), evita perder acesso por engano no meio
  de uma reconfiguração.

**Request**: nenhum corpo.

**Response 200**:
```json
{
  "success": true,
  "data": {
    "otpauthUri": "otpauth://totp/JogadoresAnonimos:apelido?secret=BASE32SECRET&issuer=JogadoresAnonimos&algorithm=SHA1&digits=6&period=30"
  },
  "error": null
}
```
`otpauthUri` é a URI padrão de provisionamento TOTP — frontend renderiza
o QR code a partir dela (biblioteca de QR no client, fora de escopo
deste contrato de backend) ou exibe o `secret` para digitação manual
extraído da própria URI. **Backend nunca gera imagem de QR** — evita
dependência de geração de imagem no servidor por algo puramente
apresentacional.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 403 | `FORBIDDEN` | autenticado sem `ROLE_ADMIN` |
| 409 | `CONFLICT` | MFA já ativo para este admin |
| 429 | `RATE_LIMITED` | excesso de tentativas de enroll |

### `POST /api/v1/admin/mfa/confirmar`
- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: não (primeira confirmação válida ativa o MFA;
  chamada repetida com `mfa_ativo` já `true` falha com 409).
- **Rate limit**: mesmo bucket dedicado de TOTP do login (por admin).
- Exige que o admin tenha um secret pendente de `/enroll` e digite um
  código TOTP válido gerado a partir dele — prova que o app autenticador
  foi configurado corretamente antes de MFA passar a ser exigido no
  login (evita lockout por secret mal escaneado).

**Request**:
```json
{ "codigoTotp": "string, 6 dígitos" }
```

**Response 200**:
```json
{ "success": true, "data": { "mensagem": "MFA ativado com sucesso." }, "error": null }
```

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | `codigoTotp` ausente/formato inválido |
| 401 | `UNAUTHENTICATED` | código incorreto |
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `NOT_FOUND` | não há enrollment pendente (nenhuma chamada prévia a `/enroll`) |
| 409 | `CONFLICT` | MFA já está ativo |
| 429 | `RATE_LIMITED` | excesso de tentativas |

### `POST /api/v1/admin/mfa/desativar`
- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: sim (desativar já desativado é no-op, mesma resposta).
- Exige senha atual no corpo (reautenticação leve) — evita que uma sessão
  JWT roubada desative o segundo fator sozinha sem conhecer a senha.

**Request**:
```json
{ "senha": "string" }
```

**Response 200**:
```json
{ "success": true, "data": { "mensagem": "MFA desativado." }, "error": null }
```

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 401 | `UNAUTHENTICATED` | senha incorreta |
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |

Efeito: `mfa_ativo = false` e `mfa_secret_cifrado = NULL` (apaga o
secret, não só desativa a flag — reconfiguração futura sempre parte de
um enroll novo, nunca reaproveita segredo antigo potencialmente
comprometido).

---

## Migration

Reservada e implementada por `security`:
```sql
-- V6__adiciona_mfa_totp_usuario.sql
ALTER TABLE usuario ADD COLUMN mfa_secret_cifrado TEXT;
ALTER TABLE usuario ADD COLUMN mfa_ativo BOOLEAN NOT NULL DEFAULT false;
```
Nullable/expand-only — não quebra nenhuma linha existente, nenhum outro
módulo lê essas colunas.

## Configuração nova
`app.mfa.secret-encryption-key` (env var `MFA_SECRET_ENCRYPTION_KEY`,
Base64 de 32 bytes) — segue o mesmo padrão de `JWT_SECRET`, nunca
hardcoded, precisa ser gerada e adicionada ao `.env`/secrets do Docker
antes de qualquer deploy que use MFA. **Não editado neste trabalho**
(mesma restrição já aplicada a outras env vars sensíveis) — adicionar
manualmente.

## Anonimato / auditoria
- Login com MFA falho é logado no mesmo padrão de `login_falho` já
  existente em `AuthService` — só o hash HMAC do identificador
  normalizado, nunca o código TOTP em si, nunca o secret.
- Nenhum log inclui `codigoTotp`, `otpauthUri` completa ou o secret
  decifrado em nenhum nível (inclusive DEBUG).

## Correção 2026-07-15 — `mfaAtivo` exposto no perfil do usuário logado

Achado da checagem final: o frontend admin não tinha como saber, ao
carregar a sessão, se o MFA do admin logado já estava ativo —
`UsuarioMeResponse` (`GET /api/v1/usuarios/me`, ver
`contrato-usuario-conta-propria.md`) não expunha `usuario.mfa_ativo`,
apesar de a coluna existir desde a migration `V6` deste contrato.
Campo `mfaAtivo` (boolean) adicionado a `UsuarioMeResponse` — mesmo DTO
retornado por `GET /me` e por `PUT /me`. Nenhum endpoint novo criado.
Mudança aditiva, sem impacto em `LoginResponse` (`data.usuario`, que
continua só com `id`/`apelido`/`role`).

## Pendências explícitas para outros agentes
- `frontend-services`: implementar tela de enrollment (exibir QR a
  partir de `otpauthUri` — biblioteca de QR no client, ex. `qrcode`
  ou similar, decisão do `frontend`), tela de "digitar código TOTP" pós
  senha quando `mfaRequerido: true` vier do login, e opção de
  desativar/reconfigurar no painel admin. **Contrato de `LoginResponse`
  mudou (campo `mfaRequerido` novo) — não é breaking (aditivo), mas o
  client precisa checar esse campo antes de assumir que `token` sempre
  vem preenchido.**
- `devops`: gerar e disponibilizar `MFA_SECRET_ENCRYPTION_KEY` real nos
  secrets de produção antes do primeiro admin ativar MFA.

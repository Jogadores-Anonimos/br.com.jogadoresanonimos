# Contrato de API — Step-up MFA na promoção de admin

> Status: **especificado por `security` em 2026-08-09, aguardando
> implementação pelo `backend`.** Retomada do item adiado em
> `contrato-usuario-admin.md` ("Fora de escopo desta regra 3: step-up de
> MFA/TOTP no momento da promoção a admin") — priorizado pelo cliente
> antes do deploy final.

Convenções gerais (mesmas do resto do projeto): envelope
`{ success, data, error }`, paths sob `/api/v1`, nenhum segredo em query
string, datas ISO-8601 UTC. Catálogo completo de `error.code` em
`contrato-erro-padrao.md`.

## Decisão: onde o step-up entra no fluxo

Dentro do **mesmo** endpoint já existente,
`PUT /api/v1/admin/usuarios/{id}/role` — não é criado um endpoint novo,
nem um "token de sessão parcial" para representar "promoção pendente de
confirmação". Mesmo princípio já usado no step MFA do login
(`contrato-mfa-admin.md`): evitar estado intermediário no servidor.

O código TOTP do **admin promotor** (quem está autenticado e faz a
chamada) viaja no corpo da mesma requisição de troca de role, como campo
opcional. Não existe um "endpoint de confirmação" separado.

## Decisão: quando o step-up é exigido

Só na transição **não-ADMIN → ADMIN** (promoção). Rebaixamento
(`ADMIN → não-ADMIN`) não exige TOTP do promotor — não é o risco que este
controle mira (concentração indevida de acesso admin), e as regras já
existentes (auto-rebaixamento, último admin) já cobrem os riscos de
rebaixamento.

Reenviar a mesma role (no-op, já existente) continua sem exigir nada —
não houve mudança de estado.

## Decisão: ordem de validação (fail-fast, mesma transação)

O step-up é checado **depois** das regras de negócio já existentes que
podem rejeitar a promoção por outro motivo (limite máximo de admins), e
**antes** de efetivar a alteração de role. Racional: não fazer sentido
pedir/gastar tentativa de TOTP para uma promoção que seria rejeitada de
qualquer forma; e não efetivar a promoção sem o TOTP validado.

Ordem final dentro de `UsuarioService#editarRole`:
1. Rate limit de escrita admin (já existente).
2. Busca do usuário-alvo (404 se não existe).
3. No-op se `novaRole == role atual` (retorna sem exigir TOTP — nenhuma
   mudança de estado).
4. Auto-rebaixamento (já existente, só relevante em rebaixamento).
5. Último admin (já existente, só relevante em rebaixamento).
6. Limite máximo de admins (já existente, só relevante em promoção).
7. **Novo**: se a mudança é promoção (`não-ADMIN → ADMIN`), exige e
   valida `codigoTotp` do **admin promotor** (`solicitanteId`, não do
   usuário-alvo).
8. Efetiva a alteração de role + registra auditoria (já existente).

## Decisão: admin promotor sem TOTP configurado (`mfa_ativo = false`)

**Bloqueia a promoção.** Não força setup automático de MFA dentro do
mesmo fluxo (isso misturaria duas operações num único endpoint — trocar
role e fazer enrollment de MFA — quebrando atomicidade e contrato
simples de request/response). Retorna erro específico instruindo o
admin a configurar MFA primeiro via
`POST /api/v1/admin/mfa/enroll` (fluxo self-service já existente, ver
`contrato-mfa-admin.md`), e repetir a promoção depois.

Isso é **deny by default** aplicado ao próprio controle: sem segundo
fator configurado, a operação sensível simplesmente não acontece — sem
bypass, sem exceção "primeira vez é grátis".

Efeito colateral aceito conscientemente: um admin recém-promovido (ou
admin antigo que nunca configurou MFA) não consegue promover ninguém até
configurar seu próprio TOTP. Isso é o comportamento desejado (empurra
adoção de MFA sem torná-lo obrigatório para as demais ações admin, que
continuam sem exigir MFA — ver decisão "MFA opcional" em
`contrato-mfa-admin.md`, que não muda).

## Contrato — mudança em `PUT /api/v1/admin/usuarios/{id}/role`

### Request (`EditarRoleRequest`, campo novo)
```json
{
  "role": "ADMIN",
  "codigoTotp": "123456"
}
```
- `role`: inalterado (`@NotNull`, enum `PARTICIPANTE`/`ADMIN`).
- `codigoTotp`: **novo, opcional no DTO** (sem `@NotBlank`) — só é
  obrigatório em tempo de negócio quando a transição é promoção. Mantém
  o endpoint retrocompatível para o caso de rebaixamento/no-op, que
  continuam sem exigir o campo. Validação de presença/formato (6 dígitos
  numéricos) acontece no service, não no Bean Validation do DTO, porque
  a obrigatoriedade é condicional à transição de role — mesmo padrão já
  usado em `alterarSenha` (senha atual só faz sentido condicionalmente).

### Response 200 — inalterada
Mesmo shape de hoje (`UsuarioAdminResponse`), sem campo novo. O sucesso
da promoção já implica que o step-up foi validado; não há necessidade de
ecoar isso na resposta.

### Erros — novos `error.code`

| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | `codigoTotp` com formato inválido (presente mas não 6 dígitos numéricos) quando a transição é promoção |
| 409 | `USUARIO_PROMOCAO_MFA_NAO_CONFIGURADO` | promoção exigiria step-up, mas o admin promotor não tem `mfa_ativo = true` |
| 401 | `USUARIO_PROMOCAO_TOTP_INVALIDO` | `codigoTotp` ausente ou incorreto para o admin promotor, na tentativa de promoção |
| 429 | `USUARIO_RATE_LIMITED` | reaproveita o rate limit já existente do endpoint (`admin-usuario-escrita:{solicitanteId}`) — sem bucket dedicado novo, ver decisão de rate limit abaixo |

Mensagens (`error.message`):
- `USUARIO_PROMOCAO_MFA_NAO_CONFIGURADO`: "Configure a autenticação em
  duas etapas na sua conta antes de promover outro usuário a
  administrador."
- `USUARIO_PROMOCAO_TOTP_INVALIDO`: "Código de autenticação em duas
  etapas ausente ou incorreto." — mensagem genérica que não distingue
  "ausente" de "incorreto" (mesmo racional anti-oráculo já usado em
  `AUTH_INVALID_CREDENTIALS`/`MFA_CODIGO_INVALIDO`), mas aqui o risco de
  enumeração é irrelevante (o solicitante já está autenticado e é o
  dono do próprio TOTP) — a genericidade é só para não vazar qual das
  duas causas ocorreu, sem necessidade prática de esconder mais que
  isso.

Nenhum `error.code` existente muda de significado.
`USUARIO_LIMITE_ADMIN_ATINGIDO` continua tendo prioridade sobre o
step-up (ver ordem de validação acima) — o step-up só é cobrado quando a
promoção já é possível por todas as outras regras.

## Decisão: rate limit

Reaproveita o bucket já existente (`admin-usuario-escrita:{solicitanteId}`,
60/min) — **sem bucket dedicado novo** para tentativas de TOTP aqui, ao
contrário do login MFA (que tem bucket próprio mais agressivo).
Diferença de contexto: no login, o TOTP é a única barreira que resta
depois da senha, atacável por qualquer um que descubra a senha; aqui, o
ator já é um admin autenticado e a ação (`editarRole`) já é de baixíssimo
volume esperado (poucos admins, poucas promoções). Não há superfície de
força bruta relevante nova — o mesmo rate limit de escrita admin já
limita a 60 tentativas/minuto, suficiente para não viabilizar brute
force de 6 dígitos (10^6 combinações) numa janela útil.

## Decisão: `recaptchaToken` — não exigido aqui

Diferente do login, este endpoint já roda atrás de sessão JWT
autenticada de admin (`@PreAuthorize("hasRole('ADMIN')")`), sem
formulário público exposto a bot. reCAPTCHA não se aplica ao mesmo
racional já usado em outras rotas admin autenticadas (enroll/confirmar
MFA também não pedem captcha).

## Anonimato / auditoria

- O registro de auditoria da promoção continua o mesmo desenho
  (`AcaoAuditoria.EDITAR`, `entidade = "usuario"`, `entidadeId` = id do
  usuário promovido, `statusAnterior`/`statusNovo` = roles). O fato de o
  step-up ter sido validado **não é logado como campo adicional** — não é
  necessário para investigação (o log de auditoria já prova que a
  promoção só foi possível se o service chegou até o fim do método, e o
  step-up é barreira de negócio, não evento a auditar separadamente).
- **Correção de bug real (2026-08-09, achado por `qa-backend-integration`
  em teste de integração real, não em teste com rollback automático)**:
  `admin_auditoria_log.status_anterior`/`status_novo` tinham `CHECK
  CONSTRAINT` restrita a `pending`/`approved`/`rejected` (herdada do uso
  original da tabela para moderação de relato). `UsuarioService
  .editarRole` grava `Role.name()` (`PARTICIPANTE`/`ADMIN`) nessas
  colunas, e `MfaService` já gravava `MFA_INATIVO`/`MFA_PENDENTE`/
  `MFA_ATIVO` — ambos violavam a constraint em produção real (qualquer
  promoção/rebaixamento de admin ou ativação/desativação de MFA quebrava
  com 500). A tabela já era usada como log de auditoria administrativa
  genérico por 3 domínios diferentes (`relato`, `mfa`, `usuario`) antes
  desta correção — a constraint é que estava desatualizada em relação a
  esse uso, não o código. Corrigido relaxando a constraint (migration
  `V6__relaxa_constraint_status_admin_auditoria_log.sql`: remove os dois
  `CHECK`, amplia `varchar(20)` para `varchar(30)`) em vez de criar uma
  tabela de auditoria separada para mudança de role — não há motivo para
  duplicar a estrutura de log só porque o domínio de valores livres é
  maior que o esperado originalmente.
- Tentativa de TOTP incorreta neste fluxo **não gera log próprio** (seria
  redundante com o rate limit já existente, e evita acumular eventos de
  segurança de baixo valor). Se no futuro o `security`/`devops` quiser
  alertar sobre tentativas repetidas de promoção com TOTP errado, isso
  deve usar identificador técnico do admin (UUID), nunca apelido/e-mail —
  mesma regra de anonimato de log já aplicada a `login_falho`.
- `codigoTotp` nunca aparece em log, em nenhum nível (mesma regra já
  aplicada ao MFA de login/enrollment).

## Migration

**Nenhuma.** Reaproveita `usuario.mfa_secret_cifrado`/`mfa_ativo`
(`V6`, já existentes) do admin promotor. Nenhuma coluna nova.

## Mudança de contrato — aditiva, não-breaking

`EditarRoleRequest` ganha campo opcional (`codigoTotp`) — clientes que
não promovem admin (rebaixamento/no-op) continuam funcionando sem
enviá-lo. Clientes que promovem admin **passam a precisar** enviar o
campo, ou recebem `409 USUARIO_PROMOCAO_MFA_NAO_CONFIGURADO` /
`401 USUARIO_PROMOCAO_TOTP_INVALIDO` dependendo do estado do promotor —
isso é breaking **em comportamento**, não em schema, e precisa ser
comunicado ao `frontend`/`frontend-services` antes de implementar: a
tela de edição de role precisa coletar o código TOTP do admin logado no
momento de promover alguém (input adicional, exibido condicionalmente
quando a `novaRole` selecionada for `ADMIN` e a role atual não for
`ADMIN`), e tratar os dois `error.code` novos com mensagem própria (não
genérica) — em especial `USUARIO_PROMOCAO_MFA_NAO_CONFIGURADO` deveria
linkar/orientar para a tela de ativação de MFA do próprio admin.

## Pendências explícitas para outros agentes

- `backend`: implementar a validação em `UsuarioService#editarRole`
  (decifrar secret do promotor via `TotpService`/serviço já usado no
  login MFA, validar janela ±1 step, mesmo padrão), adicionar
  `codigoTotp` a `EditarRoleRequest`, dois `ErrorCode` novos
  (`USUARIO_PROMOCAO_MFA_NAO_CONFIGURADO`,
  `USUARIO_PROMOCAO_TOTP_INVALIDO`) em `contrato-erro-padrao.md` e no
  enum `ErrorCode`. Reaproveitar `TotpService` já existente — não
  duplicar lógica de validação de TOTP.
- `frontend-services`/`frontend`: atualizar tela de promoção de admin
  para coletar `codigoTotp` condicionalmente e tratar os dois erros
  novos com mensagem específica (ver seção acima).
- `qa-backend-unit`/`qa-backend-integration`: cobrir os casos novos —
  promoção sem TOTP configurado, promoção com TOTP incorreto/ausente,
  promoção com TOTP correto (caminho feliz), rebaixamento/no-op sem
  exigir TOTP.

## Risco residual antes desta implementação (registrado para encerrar o adiamento)

Enquanto este contrato não for implementado: promoção a admin depende
só de outro admin já autenticado + limite numérico + auditoria (sem
step-up). Risco aceito era: uma sessão JWT de admin comprometida (sem
mais nenhuma prova de posse) já bastava para promover outro usuário.
Este contrato fecha essa lacuna. Depois de implementado, atualizar
`PENDENCIAS.md` (remover da seção Backlog) e
`contrato-usuario-admin.md` (a nota "Fora de escopo desta regra 3" some,
substituída por referência a este contrato).

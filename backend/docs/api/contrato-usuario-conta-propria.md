# Contrato de API — Edição da própria conta (usuário comum)

> Status: **implementado em 2026-07-13**. Fecha o gap da aba "Detalhes"
> de `minha-conta` (frontend), até então 100% stub
> (`onSalvarDetalhes()` só mostrava "Alteração de conta ainda não
> disponível — em breve"). Ver `Pendencias Admin Dashboard.md`, seção
> "Escopo adicional — área do usuário".

Convenções gerais (mesmas do restante do projeto, ver
`contrato-usuario-admin.md`):
- Envelope de resposta sempre `{ success, data, error }`.
- Todos os paths sob `/api/v1`.
- Catálogo completo de `error.code` em `contrato-erro-padrao.md`.
- Nenhum token/identificador sensível em query string.
- `traceId` presente em toda resposta de erro.
- **Nunca** expor `senhaHash` em nenhum response.

---

## Modelo de dados

Reaproveita a entidade `Usuario` já existente (`V1`). Sem migration nova.

---

## 1. `PUT /api/v1/usuarios/me` (editar apelido da própria conta)

> **Mudança breaking em 2026-08-10 (`backend`, achado de `security`)**:
> este endpoint **não aceita mais `email` no payload**. Troca de e-mail
> exige senha atual + confirmação por link (endpoint #4 abaixo). Motivo:
> antes desta mudança, qualquer requisição autenticada trocava o e-mail
> da conta sem nenhuma prova adicional (sem senha, sem confirmação) —
> sequestro de sessão (XSS, token vazado) virava sequestro de conta via
> fluxo de recuperação de senha. Consumido hoje por
> `frontend/src/app/services/auth/auth.ts` (`atualizarConta`) e
> `frontend/src/app/pages/minha-conta/minha-conta.ts`
> (`onSalvarDetalhes`) — **o `frontend-services` precisa remover `email`
> de `AtualizarContaRequest` e separar a UI de troca de e-mail para o
> fluxo novo antes de considerar esta entrega completa no cliente**.

- **Autenticação**: obrigatória, qualquer role (`@AuthenticationPrincipal
  AuthenticatedUser`) — não exige `ROLE_ADMIN`.
- **Autorização adicional**: nenhuma — o alvo da edição é sempre o
  próprio usuário do token, nunca um id arbitrário via path/body.
- **Idempotente**: sim — reenviar o mesmo valor é no-op.
- **Consistência**: forte, síncrona (escrita no primário). Usuário espera
  ver o apelido novo refletido imediatamente ao voltar pra tela.
- **Sem exigência de senha atual**: alterar apelido é considerado baixo
  risco (não muda credencial de acesso nem canal de recuperação de
  conta). Troca de senha é endpoint separado (#2); troca de e-mail é o
  endpoint separado #4, que exige senha atual + confirmação por link.

**Request** (`AtualizarContaRequest`):
```json
{
  "apelido": "novoApelido"
}
```
- `apelido`: obrigatório, 3-30 chars, sem espaços (mesma regra de
  `RegistroRequest`).

**Response 200** (`UsuarioMeResponse`, mesmo shape já usado em `GET /me`,
inclui `email` — só não é mais possível editá-lo por aqui):
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "apelido": "novoApelido",
    "email": "email-nao-muda-por-aqui@exemplo.com",
    "role": "PARTICIPANTE",
    "criadoEm": "2026-07-01T10:00:00Z",
    "deveTrocarSenha": false,
    "mfaAtivo": false,
    "emailConfirmado": true
  },
  "error": null
}
```

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | apelido ausente ou fora do formato |
| 401 | `AUTH_TOKEN_AUSENTE_OU_INVALIDO` | token ausente/inválido |
| 409 | `USUARIO_DADOS_EM_USO` | apelido já em uso por outra conta |

---

## 2. `PUT /api/v1/usuarios/me/senha` (trocar a própria senha)

- **Autenticação**: obrigatória, qualquer role.
- **Idempotente**: não (reenviar a mesma request com a mesma
  `senhaAtual`/`novaSenha` troca o hash novamente e reinicia
  `senhaAlteradaEm` — sem efeito colateral perigoso, mas não é a
  definição estrita de idempotente).
- **Consistência**: forte, síncrona. Precisa refletir imediatamente (JWT
  carrega `senhaAlteradaEm`, ver `JwtService`, para invalidar tokens
  antigos emitidos antes da troca).
- **Exige senha atual**: sim — valida via `PasswordEncoder.matches`
  (mesmo mecanismo usado no login/`AuthService`), antes de gravar o novo
  hash.
- **Rejeita nova senha igual à atual**: sim — mesmo `PasswordEncoder.matches`
  aplicado a `novaSenha` contra o hash vigente, antes de gravar o novo
  hash. Reuso da senha atual é rejeitado como erro de validação de campo
  (`novaSenha`), não como erro de autenticação.

**Request** (`AlterarSenhaRequest`):
```json
{
  "senhaAtual": "string",
  "novaSenha": "string",
  "confirmarNovaSenha": "string"
}
```
- `senhaAtual`: obrigatória.
- `novaSenha`: obrigatória, mínimo 10 caracteres (mesma regra de
  `RegistroRequest`/`RedefinirSenhaRequest`).
- `confirmarNovaSenha`: obrigatória, deve ser igual a `novaSenha`.

**Response 200** (`MensagemResponse`, já existente em `auth.dto`):
```json
{
  "success": true,
  "data": { "mensagem": "Senha alterada com sucesso." },
  "error": null
}
```

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | campos ausentes, nova senha curta, ou `novaSenha` ≠ `confirmarNovaSenha` |
| 400 | `USUARIO_SENHA_IGUAL_ATUAL` | `novaSenha` é igual à senha atual (checado após confirmar `senhaAtual`) |
| 401 | `AUTH_TOKEN_AUSENTE_OU_INVALIDO` / `USUARIO_SENHA_ATUAL_INCORRETA` | token ausente/inválido, ou `senhaAtual` incorreta |

`senhaAtual` incorreta usa `UnauthenticatedException` (401), não
`ValidationException` — mesma decisão de não distinguir "senha errada"
de "usuário errado" no login (`AuthService.login`), para não dar pista
de enumeração a quem tiver o token de outra pessoa (cenário de
token roubado tentando confirmar a senha por força bruta).

---

## 4. `POST /api/v1/usuarios/me/email/solicitar-troca` (solicitar troca do próprio e-mail)

Endpoint novo, 2026-08-10. Design fechado com `security`: o servidor
nunca troca `usuario.email` neste passo — só emite um token de
confirmação, enviado ao e-mail **atual/antigo** (não ao novo). O e-mail
só muda quando o link é clicado (ver seção 4 de
`contrato-confirmacao-email.md`, ramificação por `tipo = TROCA_EMAIL`).
Enviar ao e-mail antigo (não ao novo) é a defesa central: se o e-mail
novo pertence a um atacante que sequestrou a sessão, o dono legítimo da
caixa antiga recebe o alerta e pode reagir (redefinir senha, contatar
suporte) antes da troca se concretizar.

- **Autenticação**: obrigatória, qualquer role.
- **Autorização adicional**: nenhuma — sempre o usuário do token.
- **Idempotente**: não — cada chamada bem-sucedida invalida o token
  `TROCA_EMAIL` pendente anterior da mesma conta (se existir) e cria um
  novo, mesmo padrão de `reenviar-confirmacao-email`.
- **Consistência**: forte, síncrona na escrita do token (mesma transação
  da checagem de senha/unicidade); envio do e-mail é assíncrono
  (`EmailService`, já `@Async` com retry, mesmo padrão dos outros fluxos
  de e-mail transacional deste projeto) — atraso de alguns segundos no
  envio é aceitável, o dado que precisa de consistência forte é a
  existência do token no banco antes de responder 200 ao cliente.
- **Exige senha atual**: sim, via `PasswordEncoder.matches` (mesmo
  mecanismo de `UsuarioService.alterarSenha`).
- **Rate limit**: 3 por 10 minutos por `usuario_id`
  (`RateLimiterService`, chave `troca-email:hash(usuarioId)` — mesmo
  padrão de hash de identificador já usado no projeto para não persistir
  UUID em claro em estrutura de rate limit teoricamente logável).

**Request**:
```json
{
  "senhaAtual": "string",
  "novoEmail": "novo@exemplo.com"
}
```
- `senhaAtual`: obrigatória.
- `novoEmail`: obrigatório, formato válido (mesma regra `@Email` de
  `RegistroRequest`), deve ser diferente do e-mail atual da conta, não
  pode já estar em uso por outra conta.

**Response 200** (`MensagemResponse`):
```json
{
  "success": true,
  "data": { "mensagem": "Enviamos um link de confirmação para o seu e-mail atual. Clique nele para concluir a troca." },
  "error": null
}
```
Mensagem não genérica (diferente do padrão anti-enumeration de
`reenviar-confirmacao-email`) porque aqui o chamador já está autenticado
como o próprio dono da conta — não há enumeration a proteger contra o
próprio usuário logado.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | `novoEmail` ausente, formato inválido, ou igual ao e-mail atual |
| 401 | `AUTH_TOKEN_AUSENTE_OU_INVALIDO` | token ausente/inválido |
| 401 | `USUARIO_SENHA_ATUAL_INCORRETA` | `senhaAtual` não confere |
| 409 | `USUARIO_DADOS_EM_USO` | `novoEmail` já em uso por outra conta |
| 429 | `USUARIO_RATE_LIMITED` | excesso de tentativas para a mesma conta |

`senhaAtual` incorreta usa `UnauthenticatedException` (401), mesmo
racional já documentado no endpoint #2 (não distinguir motivo de forma
mais específica que "senha incorreta" para não facilitar brute force
contra token de sessão roubado).

Checagem de unicidade de `novoEmail` roda de novo no momento da
confirmação (`GET /auth/confirmar-email`), porque outra conta pode
registrar o mesmo e-mail no intervalo entre a solicitação e o clique no
link (TTL do token é o mesmo dos demais tokens de confirmação, 24h).

---

## Decisão de anonimato — impacto de trocar apelido em relatos publicados

**Confirmado lendo `Relato` (`backend/src/main/java/br/com/jogadoresanonimos/relato/entity/Relato.java`)
e `RelatoController`/`RelatoService`**: `Relato.nomeExibicao` é uma
coluna própria, preenchida no momento do envio (`EnviarRelatoRequest`),
**sem vínculo dinâmico com `Usuario.apelido`**. O relato guarda
`autorId` (para moderação/exclusão pelo próprio autor) e `nomeExibicao`
como string independente.

**Conclusão**: trocar o apelido da conta **não afeta** relatos já
publicados — eles continuam mostrando o `nomeExibicao` que o usuário
escolheu no momento do envio (congelado por design, não por efeito
colateral desta tarefa). O problema levantado na tarefa ("apelido antigo
vs. novo em relatos") **não existe** no modelo atual: não há coluna
derivada de `apelido` em `Relato`, então não há nada para decidir aqui.
Esse desenho já era coerente com o requisito de anonimato antes desta
tarefa — usuário podia inclusive publicar com um `nomeExibicao`
diferente do próprio apelido de conta desde o início.

---

## Fora de escopo nesta entrega

- Reautenticação/reemissão automática de JWT após troca de e-mail/senha
  — o frontend precisa tratar a resposta 200 e, no caso de troca de
  senha, orientar o usuário (ou o próprio client) a logar de novo caso
  o backend passe a invalidar tokens antigos de forma mais agressiva no
  futuro. Hoje `JwtService` já usa `senhaAlteradaEm` para invalidar
  tokens emitidos antes da troca (mecanismo pré-existente, não alterado
  aqui) — então o token atual da aba que fez a troca deixa de ser válido
  na primeira requisição seguinte; o comportamento esperado no frontend
  é deslogar o usuário após sucesso em `PUT /me/senha`.
- Exclusão de conta.

## Mudança breaking sinalizada — sim, `PUT /api/v1/usuarios/me`

Ver aviso na seção 1. `email` removido do request; response continua
tendo `email` (somente leitura pela via deste endpoint). Frontend precisa
adaptar `AtualizarContaRequest`/`onSalvarDetalhes` e apontar a troca de
e-mail para o endpoint novo #4. Endpoint #4 e a ramificação em
`GET /auth/confirmar-email` (`contrato-confirmacao-email.md`) são
aditivos, sem impacto em contrato já consumido.

## Implementação (backend)

- `br.com.jogadoresanonimos.usuario.dto.AtualizarContaRequest` — perdeu
  `email` (2026-08-10).
- `br.com.jogadoresanonimos.usuario.dto.AlterarSenhaRequest` — DTO já
  existente.
- `br.com.jogadoresanonimos.usuario.dto.SolicitarTrocaEmailRequest` —
  novo DTO (2026-08-10).
- `br.com.jogadoresanonimos.usuario.service.UsuarioService#atualizarConta`
  — checagem de unicidade só de apelido agora.
  `#alterarSenha` — inalterado.
  `#solicitarTrocaEmail` — novo (2026-08-10): valida senha, valida
  `novoEmail`, invalida token `TROCA_EMAIL` pendente anterior, cria token
  novo, dispara envio assíncrono ao e-mail atual.
- `br.com.jogadoresanonimos.usuario.controller.UsuarioController` —
  ganhou o endpoint `POST /me/email/solicitar-troca` (2026-08-10), além
  dos dois já existentes, mesmo controller do `GET /me`.
- `PasswordEncoder` injetado no `UsuarioService` (mesmo bean já
  configurado e usado por `AuthService`).
- Geração/persistência de token de troca de e-mail reaproveita
  `EmailConfirmacaoToken`/`EmailConfirmacaoTokenRepository` (`auth`
  package) via novo método de serviço compartilhado em `AuthService`,
  para não duplicar a lógica de hash SHA-256/TTL/envio já usada no fluxo
  de cadastro — ver `contrato-confirmacao-email.md`.
- Testes unitários em
  `backend/src/test/java/br/com/jogadoresanonimos/usuario/`.

## Sem migration

Nenhuma coluna nova — reaproveita `usuario` (`V1`) integralmente.

> **Atualização 2026-07-15 (`backend`)**: `usuario` ganhou coluna
> `deve_trocar_senha` (migration `V5`, ver `contrato-auth-cms-upload.md`).
> Não é parte do escopo original deste contrato, mas
> `PUT /api/v1/usuarios/me/senha` (endpoint 2 acima) passou a limpar essa
> flag como efeito colateral de qualquer troca de senha bem-sucedida
> (`Usuario.setSenhaHash`) — nenhuma mudança de shape de request/response
> deste endpoint, só efeito colateral novo no banco.

> **Atualização 2026-07-15 (`backend`)**: `UsuarioMeResponse` (usado tanto
> em `GET /api/v1/usuarios/me` quanto na resposta do `PUT /me` acima)
> ganhou o campo `mfaAtivo` (boolean), refletindo `usuario.mfa_ativo`
> (coluna já existente desde a migration `V6`, ver
> `contrato-mfa-admin.md`). Corrige gap: o frontend admin não tinha como
> saber se o MFA do próprio admin logado já estava ativo sem esse campo
> na sessão/perfil. Mudança aditiva (novo campo em objeto JSON existente)
> — não é breaking, mas o `frontend-services` deve atualizar a
> interface/model TypeScript correspondente para consumir o campo.

> **Atualização 2026-08-09 (`backend`)**: `UsuarioMeResponse` ganhou o
> campo `emailConfirmado` (boolean), derivado de
> `usuario.email_confirmado_em IS NOT NULL` (coluna existente desde a
> migration `V5`, ver `contrato-confirmacao-email.md`). Expõe só o
> booleano, não o timestamp cru — suficiente para o frontend decidir
> banner de pendência e bloqueio/aviso no fluxo de loja física, sem
> vazar quando a confirmação aconteceu. Corrige gap identificado pelo
> `frontend-services`: não havia como a UI saber o estado de confirmação
> do usuário logado sem esse campo em `GET /usuarios/me`. Mudança
> aditiva (novo campo em objeto JSON existente) — não é breaking, mas o
> `frontend-services` deve atualizar a interface/model TypeScript
> correspondente para consumir o campo.

> **Atualização 2026-08-10 (`backend`)**: `PUT /api/v1/usuarios/me/senha`
> passou a rejeitar `novaSenha` igual à senha atual, checando via
> `PasswordEncoder.matches(novaSenha, hashAtual)` — mesmo verificador já
> usado para validar `senhaAtual`. Corrige bug reportado em
> `PENDENCIAS.md` (fluxo de troca de senha aceitava reuso da senha
> vigente). Erro novo: `USUARIO_SENHA_IGUAL_ATUAL` (400, ver tabela de
> erros do endpoint 2). Mudança aditiva de contrato — endpoint já
> existente ganha só mais um `error.code` possível, nenhum shape de
> request/response muda; frontend que já trata erro genérico de
> `VALIDATION_ERROR`/campo não quebra, mas pode opcionalmente mostrar
> mensagem específica para esse código.

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
- Nenhum token/identificador sensível em query string.
- `traceId` presente em toda resposta de erro.
- **Nunca** expor `senhaHash` em nenhum response.

---

## Modelo de dados

Reaproveita a entidade `Usuario` já existente (`V1`). Sem migration nova.

---

## 1. `PUT /api/v1/usuarios/me` (editar apelido/e-mail da própria conta)

- **Autenticação**: obrigatória, qualquer role (`@AuthenticationPrincipal
  AuthenticatedUser`) — não exige `ROLE_ADMIN`.
- **Autorização adicional**: nenhuma — o alvo da edição é sempre o
  próprio usuário do token, nunca um id arbitrário via path/body.
- **Idempotente**: sim — reenviar os mesmos valores é no-op.
- **Consistência**: forte, síncrona (escrita no primário). Usuário espera
  ver o apelido/e-mail novo refletido imediatamente ao voltar pra tela.
- **Sem exigência de senha atual**: alterar apelido/e-mail é considerado
  baixo risco (não muda credencial de acesso). Troca de senha é endpoint
  separado (#2), que exige confirmação de senha atual.

**Request** (`AtualizarContaRequest`):
```json
{
  "apelido": "novoApelido",
  "email": "novo@exemplo.com"
}
```
- `apelido`: obrigatório, 3-30 chars, sem espaços (mesma regra de
  `RegistroRequest`).
- `email`: obrigatório, formato válido (mesma regra de `RegistroRequest`).

**Response 200** (`UsuarioMeResponse`, mesmo shape já usado em `GET /me`):
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "apelido": "novoApelido",
    "email": "novo@exemplo.com",
    "role": "PARTICIPANTE",
    "criadoEm": "2026-07-01T10:00:00Z",
    "deveTrocarSenha": false,
    "mfaAtivo": false
  },
  "error": null
}
```

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | apelido/e-mail ausente ou fora do formato |
| 401 | `UNAUTHENTICATED` | token ausente/inválido |
| 409 | `CONFLICT` | apelido ou e-mail já em uso por outra conta |

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
| 401 | `UNAUTHENTICATED` | token ausente/inválido, ou `senhaAtual` incorreta |

`senhaAtual` incorreta usa `UnauthenticatedException` (401), não
`ValidationException` — mesma decisão de não distinguir "senha errada"
de "usuário errado" no login (`AuthService.login`), para não dar pista
de enumeração a quem tiver o token de outra pessoa (cenário de
token roubado tentando confirmar a senha por força bruta).

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
- Reenvio de e-mail de verificação ao trocar e-mail (o projeto não exige
  e-mail verificado, ver `CLAUDE.md`).

## Mudança breaking sinalizada — nenhuma

Endpoints novos, não alteram `GET /api/v1/usuarios/me` nem nenhum
contrato já consumido pelo frontend.

## Implementação (backend)

- `br.com.jogadoresanonimos.usuario.dto.AtualizarContaRequest` — novo DTO.
- `br.com.jogadoresanonimos.usuario.dto.AlterarSenhaRequest` — novo DTO.
- `br.com.jogadoresanonimos.usuario.service.UsuarioService#atualizarConta`,
  `#alterarSenha` — regra de unicidade reaproveitada
  (`existsByApelidoIgnoreCase`/`existsByEmailIgnoreCase`, mas excluindo o
  próprio id da checagem — diferente do registro, que não tem id prévio).
- `br.com.jogadoresanonimos.usuario.controller.UsuarioController` —
  ganhou os dois endpoints novos, mesmo controller do `GET /me` (mesmo
  recurso `/usuarios/me`).
- `PasswordEncoder` injetado no `UsuarioService` (mesmo bean já
  configurado e usado por `AuthService`).
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

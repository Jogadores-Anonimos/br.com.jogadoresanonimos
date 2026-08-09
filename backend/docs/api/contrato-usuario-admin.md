# Contrato de API — Gestão de Usuários (Admin), v1 conservadora

> Status: **implementado em 2026-07-12**. Escopo v1 confirmado pela spec
> de UI aprovada (`Pendencias Admin Dashboard.md`): **só listar usuários
> e editar role** — sem banir/deletar, pendente de decisão do agente
> `security` sobre efeito em cascata (relatos/presenças associadas a um
> usuário excluído).

Convenções gerais (mesmas do restante do projeto, ver `contrato-avisos.md`):
- Envelope de resposta sempre `{ success, data, error }`.
- Todos os paths sob `/api/v1`.
- Catálogo completo de `error.code` em `contrato-erro-padrao.md`.
- Nenhum token/identificador sensível em query string.
- `traceId` presente em toda resposta de erro.
- **Nunca** expor `senhaHash` em nenhum response — nem aqui, nem em
  `/me`.

---

## Modelo de dados

Reaproveita a entidade `Usuario` já existente (`V1`), sem migration
nova. Nenhuma coluna nova foi necessária para o escopo desta tarefa.

---

## 1. `GET /api/v1/admin/usuarios` (admin, lista todos)

- **Autenticação + autorização**: `ROLE_ADMIN`, `@PreAuthorize`.
- **Idempotente**: sim.
- **Consistência**: forte — leitura do primário. Volume de usuários é
  baixo (~300 pedidos de ajuda/dia é limite superior do produto todo,
  não da base de usuários cadastrados); sem justificativa para
  réplica/cache nesta v1.
- **Sem paginação** nesta entrega — mesmo raciocínio de `contrato-avisos.md`
  (volume baixo). Se a base crescer a ponto de precisar, é incremento
  aditivo (`pagina`/`tamanho` opcionais).
- **Nunca expõe** `senhaHash` — só `apelido`, `email`, `role`, `criadoEm`.

**Request**: `GET /api/v1/admin/usuarios`

**Response 200** (`List<UsuarioAdminResponse>`):
```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "apelido": "string",
      "email": "string",
      "role": "PARTICIPANTE",
      "criadoEm": "2026-07-12T14:30:00Z"
    }
  ],
  "error": null
}
```

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 403 | `AUTH_FORBIDDEN` | autenticado sem `ROLE_ADMIN` |

---

## 2. `PUT /api/v1/admin/usuarios/{id}/role` (admin edita role)

- **Autenticação + autorização**: `ROLE_ADMIN`, `@PreAuthorize`.
- **Idempotente**: sim — reenviar a mesma role é no-op (retorna 200 sem
  alterar nada).
- **Consistência**: forte, síncrona. Alteração de permissão precisa
  refletir imediatamente (próxima requisição autenticada do usuário-alvo
  já deve carregar a nova role no JWT reemitido/reautenticado).

**Regras de negócio implementadas**, checadas nesta ordem dentro do
mesmo bloco transacional (fail-fast: a primeira que bater já rejeita,
sem checar as seguintes):
1. **Auto-rebaixamento** — um admin não pode remover a própria role de
   admin (`solicitanteId == id do alvo` e a mudança é `ADMIN → não-ADMIN`)
   — evita autoexclusão acidental de acesso.
2. **Último admin** — não é possível remover o último `ADMIN` do
   sistema. Antes de rebaixar um `ADMIN` para `PARTICIPANTE`, o service
   conta quantos `ADMIN` existem com lock pessimista
   (`usuarioRepository.buscarPorRoleParaAtualizacao(Role.ADMIN)`); se for
   o único, rejeita. Cobre o caso de um segundo admin tentando rebaixar o
   único outro admin restante (cenário não coberto pela regra 1, que só
   olha auto-rebaixamento).
3. **Limite máximo de admins** (`ADMIN_LIMITE_MAXIMO`, decisão fechada do
   agente `security`) — só entra quando a mudança é `não-ADMIN → ADMIN`.
   Reusa o **mesmo** método com lock pessimista da regra 2
   (`buscarPorRoleParaAtualizacao(Role.ADMIN)`) para contar admins atuais
   antes de promover, fechando a race condition de duas promoções
   simultâneas estourando o limite (mesma transação, mesmo lock).
   `ADMIN_LIMITE_MAXIMO` é env obrigatória, sem default: ausente ou
   valor não-numérico/`<= 0` derruba a aplicação no startup
   (`UsuarioService`, fail-fast — nunca silencioso). Mensagem de erro ao
   cliente é genérica ("Limite máximo de administradores já foi
   atingido."), sem expor o valor configurado nem a contagem atual (evita
   vazar informação operacional interna).
4. **Step-up MFA na promoção** (`USUARIO_PROMOCAO_MFA_NAO_CONFIGURADO` /
   `USUARIO_PROMOCAO_TOTP_INVALIDO`) — implementado, ver
   `contrato-step-up-mfa-promocao-admin.md`. Só entra quando a mudança é
   `não-ADMIN → ADMIN`, depois das regras 1-3. Exige `codigoTotp` (6
   dígitos) do admin promotor (`solicitanteId`), validado via
   `TotpService` (mesmo usado no MFA de login/enrollment). Rebaixamento e
   no-op não exigem TOTP.

**Request** (`EditarRoleRequest`):
```json
{
  "role": "ADMIN",
  "codigoTotp": "123456"
}
```
`role` é obrigatório (`@NotNull`), enum `PARTICIPANTE`/`ADMIN`.
`codigoTotp` é opcional no DTO — só obrigatório em tempo de negócio
quando a transição é promoção (`não-ADMIN → ADMIN`).

**Response 200** (`UsuarioAdminResponse`): mesmo shape do item 1, com a
role atualizada.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | `role` ausente/fora do enum, ou `codigoTotp` presente com formato inválido (promoção) |
| 401 | `USUARIO_PROMOCAO_TOTP_INVALIDO` | promoção: `codigoTotp` ausente ou incorreto para o admin promotor |
| 403 | `AUTH_FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `USUARIO_NAO_ENCONTRADO` | id não existe |
| 409 | `USUARIO_AUTO_REBAIXAMENTO_ADMIN` / `USUARIO_ULTIMO_ADMIN` / `USUARIO_LIMITE_ADMIN_ATINGIDO` / `USUARIO_PROMOCAO_MFA_NAO_CONFIGURADO` | tentativa de auto-rebaixamento, de remover o último admin, de promover além do limite configurado, ou promotor sem MFA configurado |

---

## Fora de escopo nesta entrega (confirmado pela spec)

- Banir usuário.
- Deletar usuário (pendente decisão do `security` sobre cascata em
  relatos/presenças).
- Edição de `apelido`/`email` pelo admin (só o próprio usuário edita os
  próprios dados, ver pendência separada "Aba Detalhes" em
  `Pendencias Admin Dashboard.md`).
- Paginação/busca/filtro na listagem.

## Mudança breaking sinalizada — nenhuma

Módulo novo (`GET /me` já existente não foi alterado). Nenhum contrato
anterior consumido pelo frontend é afetado.

## Implementação (backend)

- `br.com.jogadoresanonimos.usuario.entity.Usuario#alterarRole` — método
  de domínio, sem setter genérico de role.
- `br.com.jogadoresanonimos.usuario.repository.UsuarioRepository` —
  adicionados `countByRole(Role)` e `findAllByOrderByCriadoEmDesc()`.
- `br.com.jogadoresanonimos.usuario.dto.UsuarioAdminResponse` — DTO
  próprio (inclui `email`), separado do `UsuarioResponse` público
  existente (que **não** expõe email) para não misturar contrato público
  com admin.
- `br.com.jogadoresanonimos.usuario.dto.EditarRoleRequest` — DTO de
  request com `@NotNull` em `role`, `codigoTotp` opcional (sem
  `@NotBlank` — obrigatoriedade é condicional à transição de role,
  checada no service).
- `br.com.jogadoresanonimos.usuario.service.UsuarioService#listarTodos`,
  `#editarRole` — regras de negócio (auto-rebaixamento, último admin,
  limite máximo de admins, step-up MFA na promoção) ficam no service,
  não no controller. Step-up reaproveita `TotpService`/`MfaSecretCipher`
  já usados em `MfaService` (login/enrollment) — sem lógica de TOTP
  duplicada.
- `app.admin.limite-maximo` (`application.properties`) mapeia
  `ADMIN_LIMITE_MAXIMO`, sem valor default — parse e validação fail-fast
  no construtor de `UsuarioService`.
- `br.com.jogadoresanonimos.usuario.controller.UsuarioAdminController` —
  novo controller (`/api/v1/admin/usuarios`), separado do
  `UsuarioController` público existente (`/api/v1/usuarios/me`), mesmo
  padrão de separação público/admin usado em `aviso`/`relato`/etc.
- Testes unitários em
  `backend/src/test/java/br/com/jogadoresanonimos/usuario/` (`controller`
  e `service`), cobrindo as duas regras de negócio e o caminho feliz.

## Sem migration

Nenhuma coluna nova — reaproveita `usuario` (`V1`) integralmente.

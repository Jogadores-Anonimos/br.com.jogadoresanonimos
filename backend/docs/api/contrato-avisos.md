# Contrato de API — Módulo Aviso

> Status: **implementado em 2026-07-12** — entity, repository, service,
> DTOs, migration (`V10`) e controllers completos (`AvisoController`
> público, `AvisoAdminController` admin), com testes unitários.
> Baseado no modelo hoje mockado em
> `frontend/src/app/services/avisos.ts` e no escopo confirmado em
> `Pendencias Admin Dashboard.md` (linha "Avisos").

Convenções gerais (mesmas do restante do projeto, ver
`contrato-relatos.md`):
- Envelope de resposta sempre `{ success, data, error }`.
- Todos os paths sob `/api/v1`.
- Nenhum token/identificador sensível em query string.
- `traceId` presente em toda resposta de erro.
- Datas em ISO-8601 UTC.

---

## Modelo de dados

Tabela `aviso`: `titulo` (obrigatório, até 200 chars), `corpo`
(obrigatório, até 2000 chars), `fixado` (bool, default `false`),
`data_expiracao` (timestamptz opcional — `NULL` = nunca expira).
`data_publicacao` é o próprio `criado_em` da linha (não existe campo de
agendamento futuro nesta entrega — todo aviso criado já é "publicado"
imediatamente; agendamento de publicação futura não foi pedido no
escopo aprovado, seria incremento posterior se necessário).

Sem vínculo com `usuario` (nem autor nem "atualizado por") — avisos são
institucionais, não têm requisito de auditoria de autoria como o CMS de
páginas tem hoje. Se isso for necessário no futuro, é campo aditivo
(nova coluna nullable), não uma mudança breaking.

Não há paginação nos endpoints desta entrega — volume esperado de
avisos é baixo (institucional, não conteúdo gerado por usuário em massa
como relato). Se a lista crescer a ponto de precisar paginação,
reavaliar como incremento futuro sem quebrar contrato (adicionar
`pagina`/`tamanho` opcionais, default mantém comportamento atual).

---

## 1. `GET /api/v1/avisos` (público, lista ativos)

- **Autenticação**: nenhuma.
- **Idempotente**: sim.
- **Consistência**: pode tolerar poucos segundos de atraso — leitura de
  primário nesta entrega (sem cache dedicado), volume baixo não
  justifica infraestrutura extra agora. Reavaliar com
  `system-design`/`devops` só se houver gargalo real.
- **Filtro**: retorna somente avisos "ativos", isto é, com
  `data_expiracao IS NULL OR data_expiracao > now()`. Aviso expirado
  continua no banco (não é removido automaticamente), só some da
  listagem pública.
- **Ordenação**: `fixado DESC, criado_em DESC` — mesmo critério do mock
  atual do frontend (`avisos.ts`).

**Request**: `GET /api/v1/avisos`

**Response 200** (`List<AvisoPublicoResponse>`):
```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "titulo": "string",
      "corpo": "string",
      "dataPublicacao": "2026-07-12T14:30:00Z",
      "fixado": true
    }
  ],
  "error": null
}
```

**Nota**: `dataExpiracao` não é exposta neste endpoint público — é campo de
controle interno de curadoria do admin (avisos expirados já são filtrados
na query, então o dado nunca deveria vazar pro público de qualquer forma;
omitir o campo reforça isso e evita expor a data planejada de saída de um
aviso a quem não deveria ver).

**Erros**: nenhum específico (lista vazia é resposta válida, não erro).

---

## 2. `GET /api/v1/admin/avisos` (admin, lista todos)

- **Autenticação + autorização**: `ROLE_ADMIN`, `@PreAuthorize`.
- **Idempotente**: sim.
- **Consistência**: forte — leitura do primário (admin precisa ver o
  estado real, incluindo avisos expirados, para poder editá-los/reativar
  mudando a data).
- **Sem filtro**: retorna todos os avisos, inclusive expirados —
  diferente do endpoint público. Mesma ordenação (`fixado DESC,
  criado_em DESC`) para consistência de UI entre admin e público.

**Request**: `GET /api/v1/admin/avisos`

**Response 200** (`List<AvisoAdminResponse>`):
```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "titulo": "string",
      "corpo": "string",
      "dataPublicacao": "2026-07-12T14:30:00Z",
      "fixado": true,
      "dataExpiracao": null,
      "expirado": false
    }
  ],
  "error": null
}
```
`AvisoAdminResponse` inclui `expirado` (bool calculado) para a UI do
admin sinalizar visualmente sem precisar comparar datas no cliente.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 403 | `FORBIDDEN` | autenticado sem `ROLE_ADMIN` |

---

## 3. `POST /api/v1/admin/avisos` (admin cria)

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: não (cria recurso novo a cada chamada).
- **Consistência**: forte, síncrona.

**Request** (`CriarAvisoRequest`):
```json
{
  "titulo": "string, 1-200 chars",
  "corpo": "string, 1-2000 chars",
  "fixado": false,
  "dataExpiracao": "2026-08-01T00:00:00Z"
}
```
`fixado` opcional (default `false` se omitido). `dataExpiracao`
opcional; se informada, deve ser uma data futura no momento da criação
(validação de aplicação, não `CHECK` no banco — mesmo padrão do
`relato`, evita `CHECK` complexo em SQL).

**Response 201** (`AvisoAdminResponse`): mesmo shape do item 2.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | título/corpo ausente ou fora do tamanho; `dataExpiracao` no passado |
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |

---

## 4. `PUT /api/v1/admin/avisos/{id}` (admin edita)

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: sim (PUT substitui os campos editáveis).
- **Consistência**: forte, síncrona.
- Permite editar `titulo`, `corpo`, `fixado`, `dataExpiracao`
  (inclusive limpar a expiração enviando `null`, ou reativar um aviso
  expirado estendendo a data — não há endpoint separado de "reativar").

**Request** (`EditarAvisoRequest`): mesmo shape do `CriarAvisoRequest`.

**Response 200**: `AvisoAdminResponse` atualizado.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | título/corpo fora do tamanho; `dataExpiracao` no passado |
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `NOT_FOUND` | id não existe |

---

## 5. `DELETE /api/v1/admin/avisos/{id}` (admin exclui)

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: sim (segunda chamada no mesmo id retorna 404).
- **Consistência**: forte, síncrona. Exclusão física — aviso não é
  referenciado por nenhuma outra entidade, sem motivo para soft-delete.

**Response 200**: `{ "success": true, "data": { "mensagem": "Aviso removido." }, "error": null }`

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `NOT_FOUND` | id não existe |

---

## Migration — `V10__cria_tabela_aviso.sql`

```sql
CREATE TABLE aviso (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  titulo VARCHAR(200) NOT NULL,
  corpo VARCHAR(2000) NOT NULL,
  fixado BOOLEAN NOT NULL DEFAULT false,
  data_expiracao TIMESTAMPTZ,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_aviso_fixado_criado_em ON aviso(fixado, criado_em);
```

Índice composto `(fixado, criado_em)`: cobre diretamente o padrão de
acesso da listagem (`ORDER BY fixado DESC, criado_em DESC`), tanto para
o endpoint público quanto o admin. Não há índice dedicado para
`data_expiracao` — filtro de expiração é sobre uma tabela pequena
(institucional, não conteúdo de usuário em massa), sequential scan é
aceitável no volume esperado; reavaliar se a tabela crescer muito.

---

## Mudança breaking sinalizada — nenhuma

Módulo novo, sem contrato anterior consumido pelo frontend (hoje é mock
puro em `avisos.ts`). O shape de `Aviso` (`id`, `titulo`, `corpo`,
`dataPublicacao`, `fixado`) é mantido no endpoint público, com uma
diferença deliberada em relação ao mock: `dataExpiracao` **não** é
exposta publicamente (só existe em `AvisoAdminResponse`) — é campo de
curadoria interna do admin, confirmado pelo usuário 2026-07-12. O
endpoint admin também retorna `expirado` (campo novo, não quebra nada
que já exista).

## Pendências explícitas para outros agentes

- `frontend-services`: trocar `AvisosService` mock pela chamada real
  (`GET /api/v1/avisos`, sem paginação — retorna array direto em
  `data`, não `Page<T>`). Para o admin, criar novo service consumindo
  os 4 endpoints de `/api/v1/admin/avisos`.

## Implementação (backend)

- `br.com.jogadoresanonimos.aviso.controller.AvisoController` —
  `GET /api/v1/avisos`.
- `br.com.jogadoresanonimos.aviso.controller.AvisoAdminController` —
  os 4 endpoints admin, classe inteira sob
  `@PreAuthorize("hasRole('ADMIN')")`.
- `br.com.jogadoresanonimos.aviso.service.AvisoService` — `listarAtivos`,
  `listarTodos`, `criar`, `editar`, `remover`.
- Testes unitários em
  `backend/src/test/java/br/com/jogadoresanonimos/aviso/controller/`
  (`AvisoControllerTest`, `AvisoAdminControllerTest`), mockando
  `AvisoService` — mesmo padrão de teste puro (sem `MockMvc`) usado no
  módulo `relato`.

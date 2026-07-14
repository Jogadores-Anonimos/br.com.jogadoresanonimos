# Contrato de API — Módulo Relato

> Status: **implementado em 2026-07-12** — entity, repository, service,
> DTOs, migration e controllers completos (`RelatoController` público,
> `RelatoAdminController` admin), com testes unitários dos controllers.
> Baseado na spec de UI aprovada pelo usuário (`ux-ui`, moderação de
> relatos: pendente/aprovado/rejeitado) e no modelo hoje mockado em
> `frontend/src/app/services/relatos.ts`.
>
> **Nota de nomenclatura de query params**: a implementação usa
> `pagina`/`tamanho` (não `page`/`size` como nos exemplos abaixo),
> consistente com o restante do código em português (`PageResponse` já
> expõe campos `pagina`/`tamanho`). Os exemplos de request desta spec
> foram ajustados para refletir o nome real.

Convenções gerais (mesmas do restante do projeto, ver
`contrato-auth-cms-upload.md`):
- Envelope de resposta sempre `{ success, data, error }`.
- Todos os paths sob `/api/v1`.
- Nenhum token/identificador sensível em query string.
- `traceId` presente em toda resposta de erro.
- Datas em ISO-8601 UTC.

---

## Modelo de dados e decisão de anonimato

Tabela `relato` tem `autor_id` (FK para `usuario`, `NOT NULL`), mas esse
campo **nunca é serializado em nenhum DTO de resposta**, nem no endpoint
admin de moderação. Motivo: anonimato é requisito de arquitetura do
projeto (`CLAUDE.md`), mas o vínculo interno com o autor ainda é
necessário para:
- Permitir que o próprio usuário logado veja o status dos relatos que
  enviou (endpoint futuro `GET /api/v1/relatos/meus`, fora do escopo
  desta entrega, mas o campo já sustenta isso sem migration nova).
- Rate limiting de envio por usuário (não por IP — ver seção de rate
  limit abaixo).
- Auditoria futura em caso de abuso (ex: relato com conteúdo ilegal),
  sem que isso apareça na tela de moderação do dia a dia.

Todos os DTOs (`RelatoPublicoResponse`, `RelatoAdminResponse`) só têm:
`id`, `texto`, `nomeExibicao`, `status`, `dataCriacao` (+ `dataAtualizacao`
no admin). Nunca `autorId`, apelido ou e-mail do autor.

---

## 1. `POST /api/v1/relatos` (usuário autenticado envia relato)

- **Autenticação**: obrigatória (JWT). Relato anônimo na exibição, mas
  exige autenticação no envio (mesmo padrão de necessidade de vínculo
  interno para rate-limit/abuso, sem expor isso ao público).
- **Autorização**: qualquer usuário autenticado (`PARTICIPANTE` ou
  `ADMIN`).
- **Idempotente**: não (cria recurso novo a cada chamada).
- **Rate limit**: sim — `RateLimiterService`, bucket por `usuario_id`
  (ex: 5 relatos por 10 minutos) — evita flood de um único usuário sem
  precisar de IP (anonimato preservado, chave é o id interno do JWT, não
  IP). Limite exato é placeholder documentado aqui; ajuste fino fica a
  critério do `security` se avaliar necessário.
- **Consistência**: forte — grava direto no primário, síncrono (usuário
  precisa ver seu próprio envio refletido se navegar para uma tela de
  "meus relatos" no futuro).

**Request** (`EnviarRelatoRequest`):
```json
{
  "texto": "string, 50-4000 chars",
  "nomeExibicao": "string opcional, máx 30 chars"
}
```

**Response 201** (`RelatoEnvioResponse`):
```json
{
  "success": true,
  "data": { "id": "uuid", "status": "pending" },
  "error": null
}
```
Não retorna o texto de volta (o cliente já tem os dados que enviou) —
minimiza payload de resposta.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | texto fora de 50-4000 chars; `nomeExibicao` > 30 chars |
| 401 | `UNAUTHENTICATED` | token ausente/inválido/expirado |
| 429 | `RATE_LIMITED` | excesso de envios na janela |

---

## 2. `GET /api/v1/relatos` (público, lista aprovados)

- **Autenticação**: nenhuma.
- **Idempotente**: sim.
- **Consistência**: pode tolerar poucos segundos de atraso — relato
  aprovado não muda com frequência após aprovação; **não** há requisito
  de refletir a última escrita instantaneamente aqui (diferente do
  envio). Ainda assim, sem cache dedicado nesta entrega — volume da
  tabela é baixo (~300 pedidos/dia é o total de tráfego do site, não
  volume de relatos); adicionar cache seria prematuro. Reavaliar com
  `system-design`/`devops` se a tabela crescer muito.
- **Paginação**: sim, `page`/`size` (query params, sem dado sensível) —
  mesmo com volume baixo hoje, evita over-fetching se a lista crescer;
  `size` default 20, máx 50.

**Request**: `GET /api/v1/relatos?pagina=0&tamanho=20`

**Response 200** (`Page<RelatoPublicoResponse>`):
```json
{
  "success": true,
  "data": {
    "conteudo": [
      { "id": "uuid", "texto": "string", "nomeExibicao": "string ou null", "dataCriacao": "2026-07-12T14:30:00Z" }
    ],
    "pagina": 0,
    "tamanho": 20,
    "totalElementos": 4,
    "totalPaginas": 1
  },
  "error": null
}
```
Ordenado por `dataCriacao DESC` (mais recentes primeiro), mesmo critério
do mock atual do frontend.

**Erros**: nenhum específico (lista vazia é resposta válida, não erro).

---

## 3. `GET /api/v1/admin/relatos?status=pending|approved|rejected` (admin, lista por status)

- **Autenticação + autorização**: `ROLE_ADMIN`, `@PreAuthorize`.
- **Idempotente**: sim.
- **Consistência**: forte — leitura do primário (fila de moderação
  precisa refletir a última ação de outro admin imediatamente, ex: dois
  admins moderando ao mesmo tempo).
- **Paginação**: sim, mesmo padrão do endpoint público.

**Request**: `GET /api/v1/admin/relatos?status=pending&pagina=0&tamanho=20`
- `status` obrigatório, um dos três valores do enum.

**Response 200** (`Page<RelatoAdminResponse>`):
```json
{
  "success": true,
  "data": {
    "conteudo": [
      {
        "id": "uuid",
        "texto": "string",
        "nomeExibicao": "string ou null",
        "status": "pending",
        "dataCriacao": "2026-07-12T14:30:00Z",
        "dataAtualizacao": "2026-07-12T14:30:00Z"
      }
    ],
    "pagina": 0,
    "tamanho": 20,
    "totalElementos": 1,
    "totalPaginas": 1
  },
  "error": null
}
```
Sem `autorId`/apelido/e-mail — ver seção "Modelo de dados e decisão de
anonimato" acima. Ordenado por `dataCriacao ASC` para `pending` (fila
FIFO, relato mais antigo primeiro) e `dataAtualizacao DESC` para
`approved`/`rejected` (mais recente moderado primeiro) — decisão de
UX simples, sem parâmetro de sort configurável nesta entrega.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | `status` ausente ou fora do enum |
| 403 | `FORBIDDEN` | autenticado sem `ROLE_ADMIN` |

---

## 4. `PUT /api/v1/admin/relatos/{id}` (admin edita texto)

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: sim (PUT substitui o texto).
- **Consistência**: forte, síncrona.
- Permite editar em qualquer status (não só `pending`) — útil também
  para corrigir um relato já aprovado se um problema for notado depois
  (ex: identificação de terceiro passou despercebida na primeira
  moderação).

**Request** (`EditarRelatoRequest`):
```json
{ "texto": "string, 50-4000 chars" }
```
Não permite editar `nomeExibicao` nesta entrega — é escolha do autor,
editar isso pelo admin não foi pedido no escopo aprovado; se necessário
no futuro, é campo adicional no mesmo request (não é breaking change).

**Response 200**: `RelatoAdminResponse` atualizado (mesmo shape do item
3, com `status` inalterado — editar texto não muda o status).

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | texto fora de 50-4000 chars |
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `NOT_FOUND` | id não existe |

---

## 5. `POST /api/v1/admin/relatos/{id}/aprovar`

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: sim — aprovar um relato já aprovado é no-op (mesmo
  estado final), retorna 200 normalmente (não é erro repetir).
- **Consistência**: forte, síncrona.
- **Regra de transição**: permitido a partir de `pending` ou `rejected`
  (admin pode reverter uma rejeição direto para aprovado sem passar por
  pending de novo — decisão de simplicidade, evita passo extra na UI).

**Request**: sem corpo.

**Response 200**: `RelatoAdminResponse` com `status: "approved"`.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `NOT_FOUND` | id não existe |

---

## 6. `POST /api/v1/admin/relatos/{id}/rejeitar`

- Mesmo contrato do item 5, mas `status: "rejected"`. Transição
  permitida a partir de qualquer status atual.

**Erros**: mesmos do item 5.

---

## 7. `POST /api/v1/admin/relatos/{id}/reverter`

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: sim.
- Volta o `status` para `pending`, independente do estado atual
  (`approved` ou `rejected`) — ação explícita e separada de edição de
  conteúdo, conforme pedido no escopo aprovado (o texto do relato não
  muda, só o status).

**Request**: sem corpo.

**Response 200**: `RelatoAdminResponse` com `status: "pending"`.

**Erros**: mesmos do item 5.

---

## 8. `DELETE /api/v1/admin/relatos/{id}`

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: sim (deletar duas vezes o mesmo id: segunda chamada
  retorna 404, tratado como "já não existe").
- **Consistência**: forte, síncrona. Exclusão física (não soft-delete)
  — relato não tem nenhuma outra entidade referenciando-o (diferente de
  `imagem`/`arquivo` referenciados por `cms_bloco`), não há motivo para
  reter o registro.

**Response 200**: `{ "success": true, "data": { "mensagem": "Relato removido." }, "error": null }`

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `NOT_FOUND` | id não existe |

---

## Migration — `V7__cria_tabela_relato.sql`

```sql
CREATE TABLE relato (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  texto VARCHAR(4000) NOT NULL,
  nome_exibicao VARCHAR(30),
  status VARCHAR(20) NOT NULL CHECK (status IN ('pending', 'approved', 'rejected')),
  autor_id UUID NOT NULL REFERENCES usuario(id),
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_relato_status_criado_em ON relato(status, criado_em);
CREATE INDEX idx_relato_autor ON relato(autor_id);
```

Índice composto `(status, criado_em)`: padrão de acesso principal é
"listar por status, ordenado por data" (tanto o público filtrando
`approved` quanto o admin filtrando por status escolhido) — cobre o
`WHERE status = ?` e evita sort adicional em memória para a ordenação
mais comum.

Índice simples em `autor_id`: sustenta o rate-limit por usuário e o
futuro endpoint "meus relatos", sem custo de manutenção relevante dado o
volume baixo.

Não há `CHECK` de tamanho mínimo de `texto` no banco (só `VARCHAR(4000)`
como teto) — a validação de mínimo 50 chars fica só na camada de
aplicação (Bean Validation), consistente com o padrão do projeto (CMS
também valida markup/tamanho na aplicação, não via `CHECK` complexo no
SQL).

---

## Mudança breaking sinalizada — nenhuma

Módulo novo, sem contrato anterior consumido pelo frontend (hoje é mock
puro em `relatos.ts`). Ao migrar `frontend-services` para a API real, o
shape de `Relato` (`id`, `texto`, `nomeExibicao`, `status`, `dataCriacao`)
é mantido idêntico ao mock atual — únicas adições são de infraestrutura
de página (`Page<T>` no lugar de array simples) e o novo endpoint de
envio retornar só `{ id, status }` em vez de `{ recebido: boolean }`
(o mock tem um formato ligeiramente mais simples que a resposta real;
isso é esperado já que o mock nunca teve um contrato fechado formalmente
— combinar com `frontend-services` antes de trocar o mock pela chamada
real).

## Pendências explícitas para outros agentes

- `security`: confirmar limite exato do rate limit de envio de relato
  (implementado como placeholder: 5 por 10min por usuário, ver
  `RelatoService.enviar`) e se recomenda algo adicional (ex: CAPTCHA)
  dado que o formulário está atrás de login, reduzindo o risco de bot
  anônimo em massa.
- `frontend-services`: ajustar `RelatosService` mock para o contrato
  acima quando for integrar (principalmente `Page<T>` na listagem com
  query params `pagina`/`tamanho`, e exigência de autenticação no envio,
  que o mock atual não modela).

## Implementação (backend)

- `br.com.jogadoresanonimos.relato.controller.RelatoController` —
  `POST /api/v1/relatos` e `GET /api/v1/relatos`.
- `br.com.jogadoresanonimos.relato.controller.RelatoAdminController` —
  os 6 endpoints admin, classe inteira sob `@PreAuthorize("hasRole('ADMIN')")`.
- Nenhum método novo foi necessário no `RelatoService`; os controllers só
  consomem o que já estava implementado (`enviar`, `listarAprovados`,
  `listarPorStatus`, `editar`, `aprovar`, `rejeitar`, `reverter`, `remover`).
- Testes unitários em
  `backend/src/test/java/br/com/jogadoresanonimos/relato/controller/`
  (`RelatoControllerTest`, `RelatoAdminControllerTest`), mockando
  `RelatoService` — mesmo padrão de teste puro (sem `MockMvc`/contexto
  Spring) usado hoje no resto do projeto (ex: `CmsServiceTest`).

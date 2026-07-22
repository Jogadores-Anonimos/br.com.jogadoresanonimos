# Contrato de API — Módulo Reflexão do Dia

> Status: **implementado em 2026-07-12** — entity, repository, service,
> DTOs, migration (`V15`) e controllers completos (`ReflexaoDiaController`
> público, `ReflexaoDiaAdminController` admin), com testes unitários.
> Baseado no modelo hoje mockado em
> `frontend/src/app/services/reflexao-do-dia.ts` e no escopo confirmado
> em `Pendencias Admin Dashboard.md` (linha "Reflexão do Dia").

Convenções gerais (mesmas do restante do projeto, ver
`contrato-avisos.md`):
- Envelope de resposta sempre `{ success, data, error }`.
- Todos os paths sob `/api/v1`.
- Catálogo completo de `error.code` em `contrato-erro-padrao.md`.
- Nenhum token/identificador sensível em query string.
- `traceId` presente em toda resposta de erro.

---

## Modelo de dados

Tabela `reflexao_dia`: chave lógica é `(mes, dia)` — **sem ano**. A
reflexão é recorrente todo ano, mesma decisão do calendário do GA
americano e do mock atual do frontend (`reflexao-do-dia.ts`). Constraint
de unicidade `(mes, dia)` garante no máximo uma reflexão cadastrada por
data do calendário.

`mes` (1-12) e `dia` (1-31) são validados tanto por `CHECK` no banco
quanto na camada de aplicação (o `CHECK` cobre o range bruto; a validação
de aplicação cobre a combinação — ex: `dia=30` com `mes=2` é rejeitada
pelo service antes de chegar ao banco, já que fevereiro tem no máximo 28
dias nesta modelagem sem ano/bissexto).

`texto` até 2000 chars (mesmo limite do `corpo` de `Aviso`).

Sem paginação — o volume é fixo e pequeno (no máximo 366 linhas na
tabela inteira, uma por dia do calendário).

---

## 1. `GET /api/v1/reflexao-do-dia/hoje` (público)

- **Autenticação**: nenhuma.
- **Idempotente**: sim (dentro do mesmo dia; resultado muda naturalmente
  à meia-noite, isso não é considerado quebra de idempotência).
- **Consistência**: leitura do primário, sem cache dedicado — mesmo
  raciocínio do módulo `aviso` (volume baixo não justifica infra extra
  agora).
- `mes`/`dia` são calculados no servidor (`LocalDate.now()`), nunca
  recebidos do cliente — evita depender do relógio/fuso do navegador.

**Response 200** (`ReflexaoDiaResponse`):
```json
{
  "success": true,
  "data": { "mes": 7, "dia": 12, "texto": "string" },
  "error": null
}
```

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 404 | `REFLEXAO_NAO_ENCONTRADA` | nenhuma reflexão cadastrada para a data de hoje |

---

## 2. `GET /api/v1/reflexao-do-dia/{mes}/{dia}` (público)

- **Autenticação**: nenhuma.
- **Idempotente**: sim.
- **Consistência**: mesma do endpoint anterior.

**Response 200**: mesmo shape do item 1.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | `mes` fora de 1-12, ou `dia` inválido para o `mes` informado |
| 404 | `REFLEXAO_NAO_ENCONTRADA` | data válida, mas sem reflexão cadastrada |

---

## 3. `GET /api/v1/admin/reflexao-do-dia?mes={mes}` (admin, lista o mês inteiro)

- **Autenticação + autorização**: `ROLE_ADMIN`, `@PreAuthorize`.
- **Idempotente**: sim.
- **Consistência**: forte — leitura do primário.
- Retorna **todos os dias do mês** (28-31, conforme o mês), preenchido
  ou não — pensado para a UI de calendário do admin mostrar de forma
  visual quais dias já têm conteúdo cadastrado (`texto: null` = dia
  vazio, sem precisar de uma segunda chamada por dia).

**Request**: `GET /api/v1/admin/reflexao-do-dia?mes=7`

**Response 200** (`List<ReflexaoDiaDoMesResponse>`):
```json
{
  "success": true,
  "data": [
    { "dia": 1, "texto": "string ou null" },
    { "dia": 2, "texto": null }
  ],
  "error": null
}
```

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | `mes` fora de 1-12 |
| 403 | `AUTH_FORBIDDEN` | autenticado sem `ROLE_ADMIN` |

---

## 4. `PUT /api/v1/admin/reflexao-do-dia/{mes}/{dia}` (admin, upsert)

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: sim (mesmo `texto` reenviado produz o mesmo estado).
- **Consistência**: forte, síncrona.
- Cria a reflexão daquele dia se não existir, ou substitui o texto se já
  existir (upsert por `(mes, dia)`, não por `id` — o admin nunca precisa
  saber/gerenciar o `id` interno, só a data).

**Request** (`SalvarReflexaoDiaRequest`):
```json
{ "texto": "string, 1-2000 chars" }
```

**Response 200** (`ReflexaoDiaResponse`): mesmo shape do item 1.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | `texto` ausente/vazio/excede 2000 chars, ou `mes`/`dia` inválidos |
| 403 | `AUTH_FORBIDDEN` | sem `ROLE_ADMIN` |

---

## Migration — `V15__cria_tabela_reflexao_dia.sql`

```sql
CREATE TABLE reflexao_dia (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  mes SMALLINT NOT NULL,
  dia SMALLINT NOT NULL,
  texto VARCHAR(2000) NOT NULL,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT chk_reflexao_dia_mes CHECK (mes BETWEEN 1 AND 12),
  CONSTRAINT chk_reflexao_dia_dia CHECK (dia BETWEEN 1 AND 31)
);

CREATE UNIQUE INDEX uq_reflexao_dia_mes_dia ON reflexao_dia(mes, dia);
```

O índice único em `(mes, dia)` cobre dois papéis ao mesmo tempo: garante
a constraint de unicidade e serve como índice de busca para os dois
padrões de leitura da tabela (`findByMesAndDia` e `findAllByMesOrderByDiaAsc`,
este último usa o prefixo `mes` do índice composto). Não há índice
adicional — volume total da tabela é trivial (máximo ~366 linhas).

---

## Mudança breaking sinalizada — nenhuma

Módulo novo, sem contrato anterior consumido pelo frontend (hoje é mock
puro em `reflexao-do-dia.ts`). O shape público (`mes`, `dia`, `texto`)
é idêntico à interface `ReflexaoDoDia` já usada no mock — nenhuma
adaptação de shape esperada do lado do `frontend-services`, só trocar a
implementação do service Angular pela chamada HTTP real.

## Pendências explícitas para outros agentes

- `frontend-services`: trocar `ReflexaoDoDiaService` mock pelas chamadas
  reais (`GET /reflexao-do-dia/hoje`, `GET /reflexao-do-dia/{mes}/{dia}`).
  Tratar 404 como "sem reflexão cadastrada" (estado válido, não erro de
  rede) — hoje o mock nunca retorna 404 porque todo dia tem texto
  placeholder gerado; o backend real não gera placeholder, dias sem
  conteúdo cadastrado simplesmente não existem na tabela.
- `frontend-services`/`frontend`: para a UI de calendário do admin,
  novo service consumindo `GET /admin/reflexao-do-dia?mes=` (lista
  completa do mês, incluindo dias vazios) e `PUT /admin/reflexao-do-dia/{mes}/{dia}`
  (upsert, formulário por dia conforme a spec de UX aprovada).

## Implementação (backend)

- `br.com.jogadoresanonimos.reflexao.controller.ReflexaoDiaController` —
  `GET /reflexao-do-dia/hoje`, `GET /reflexao-do-dia/{mes}/{dia}`.
- `br.com.jogadoresanonimos.reflexao.controller.ReflexaoDiaAdminController`
  — `GET /admin/reflexao-do-dia?mes=`, `PUT /admin/reflexao-do-dia/{mes}/{dia}`,
  classe inteira sob `@PreAuthorize("hasRole('ADMIN')")`.
- `br.com.jogadoresanonimos.reflexao.service.ReflexaoDiaService` —
  `buscarDeHoje`, `buscarPorData`, `listarMes`, `salvar` (upsert).
- `SecurityConfig`: `GET /api/v1/reflexao-do-dia/**` liberado com
  `permitAll` (cobre os dois endpoints públicos com um único matcher,
  já que ambos são GET e públicos).
- Testes unitários em
  `backend/src/test/java/br/com/jogadoresanonimos/reflexao/` —
  `service/ReflexaoDiaServiceTest` (validação de mes/dia, upsert
  criar/editar), `controller/ReflexaoDiaControllerTest`,
  `controller/ReflexaoDiaAdminControllerTest` — mesmo padrão de teste
  puro (sem `MockMvc`) usado no módulo `aviso`.

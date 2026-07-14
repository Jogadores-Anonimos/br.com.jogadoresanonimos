# Contrato de API — Módulo Hotline (Linhas de Ajuda)

> Status: **implementado em 2026-07-12** — entities, repositories,
> service, DTOs, migration (`V13`) e controllers completos
> (`HotlineController` público, `HotlineAdminController` admin), com
> testes unitários. Baseado no modelo hoje mockado em
> `frontend/src/app/pages/hotlines/hotlines.ts` e no escopo confirmado
> em `Pendencias Admin Dashboard.md` (linha "Linhas de Ajuda /
> Hotlines").

Convenções gerais (mesmas do restante do projeto, ver
`contrato-avisos.md`):
- Envelope de resposta sempre `{ success, data, error }`.
- Todos os paths sob `/api/v1`.
- Nenhum token/identificador sensível em query string.
- `traceId` presente em toda resposta de erro.

---

## Modelo de dados

Hierarquia de 2 níveis, duas tabelas relacionadas (não uma tabela única
com região repetida por linha, para não desnormalizar múltiplos
telefones por região em colunas ou JSON):

- `hotline_regiao`: `nome` (obrigatório, até 100 chars, único
  case-insensitive — evita duplicar "Bahia" e "bahia" como registros
  diferentes).
- `hotline_telefone`: `regiao_id` (FK, `ON DELETE CASCADE` — excluir uma
  região exclui todos os telefones dela, conforme requisito confirmado),
  `label` (obrigatório, até 100 chars), `telefone` (obrigatório, até 30
  chars, armazenado como string livre — sem `CHECK` de formato, o
  telefone de hoje é `"(a definir)"` no mock, então uma validação de
  formato de telefone real quebraria a migração de dados existente).

Sem paginação — volume esperado é baixo (10 estados no mock atual,
institucional, não conteúdo gerado por usuário em massa).

---

## 1. `GET /api/v1/hotlines` (público, lista tudo agrupado)

- **Autenticação**: nenhuma.
- **Idempotente**: sim.
- **Consistência**: pode tolerar poucos segundos de atraso — leitura de
  primário nesta entrega (sem cache dedicado), volume baixo não
  justifica infraestrutura extra agora.
- **Ordenação**: regiões em ordem alfabética (`nome ASC`); telefones
  dentro de cada região em ordem de criação (`criado_em ASC`).

**Request**: `GET /api/v1/hotlines`

**Response 200** (`List<HotlineRegiaoResponse>`):
```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "nome": "Bahia",
      "telefones": [
        { "id": "uuid", "label": "Linha de apoio", "telefone": "(71) 0000-0000" }
      ]
    }
  ],
  "error": null
}
```

**Erros**: nenhum específico (lista vazia é resposta válida, não erro).

---

## 2. `GET /api/v1/admin/hotlines` (admin, mesmo shape)

- **Autenticação + autorização**: `ROLE_ADMIN`, `@PreAuthorize`.
- **Idempotente**: sim.
- **Consistência**: forte — leitura do primário.
- Mesmo shape e ordenação do endpoint público; existe como endpoint
  separado (não reaproveita o público) para manter o padrão do projeto
  de público vs. admin como superfícies distintas, mesmo quando o
  retorno é idêntico hoje — abre espaço para o admin expor campos extras
  no futuro (ex: auditoria) sem quebrar o contrato público.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 403 | `FORBIDDEN` | autenticado sem `ROLE_ADMIN` |

---

## 3. `POST /api/v1/admin/hotlines/regioes` (admin cria região)

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: não.
- **Consistência**: forte, síncrona.

**Request** (`CriarRegiaoRequest`):
```json
{ "nome": "string, 1-100 chars" }
```

**Response 201** (`HotlineRegiaoResponse`): região criada, com
`telefones: []`.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | nome ausente ou fora do tamanho |
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |
| 409 | `CONFLICT` | já existe região com esse nome (case-insensitive) |

---

## 4. `DELETE /api/v1/admin/hotlines/regioes/{id}` (admin exclui região inteira)

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: sim (segunda chamada retorna 404).
- **Consistência**: forte, síncrona. Exclusão física, cascade nos
  telefones da região (`ON DELETE CASCADE` no banco + `orphanRemoval`
  na entidade JPA).

**Response 200**: `{ "success": true, "data": { "mensagem": "Região removida." }, "error": null }`

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `NOT_FOUND` | id não existe |

---

## 5. `POST /api/v1/admin/hotlines/regioes/{regiaoId}/telefones` (admin adiciona telefone)

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: não.
- **Consistência**: forte, síncrona.

**Request** (`CriarTelefoneRequest`):
```json
{ "label": "string, 1-100 chars", "telefone": "string, 1-30 chars" }
```

**Response 201** (`HotlineRegiaoResponse`): região atualizada com o
telefone novo incluído na lista (evita round-trip extra do frontend
para re-buscar a região completa).

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | label/telefone ausente ou fora do tamanho |
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `NOT_FOUND` | `regiaoId` não existe |

---

## 6. `PUT /api/v1/admin/hotlines/telefones/{id}` (admin edita telefone)

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: sim.
- **Consistência**: forte, síncrona.

**Request** (`EditarTelefoneRequest`): mesmo shape do
`CriarTelefoneRequest`.

**Response 200** (`HotlineTelefoneResponse`): telefone atualizado
(sozinho, não a região inteira — edição de telefone não precisa do
contexto da região).

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | label/telefone ausente ou fora do tamanho |
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `NOT_FOUND` | id não existe |

---

## 7. `DELETE /api/v1/admin/hotlines/telefones/{id}` (admin exclui telefone individual)

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: sim (segunda chamada retorna 404).
- **Consistência**: forte, síncrona. Exclusão física — telefone não é
  referenciado por nenhuma outra entidade.

**Response 200**: `{ "success": true, "data": { "mensagem": "Telefone removido." }, "error": null }`

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `NOT_FOUND` | id não existe |

---

## Migration — `V13__cria_tabelas_hotline.sql`

```sql
CREATE TABLE hotline_regiao (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nome VARCHAR(100) NOT NULL,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX idx_hotline_regiao_nome ON hotline_regiao(nome);

CREATE TABLE hotline_telefone (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  regiao_id UUID NOT NULL REFERENCES hotline_regiao(id) ON DELETE CASCADE,
  label VARCHAR(100) NOT NULL,
  telefone VARCHAR(30) NOT NULL,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_hotline_telefone_regiao ON hotline_telefone(regiao_id);
```

`idx_hotline_regiao_nome` é único (não só um índice comum): reforça no
banco a mesma regra checada em `HotlineService.criarRegiao` (evita
race condition entre duas requisições concorrentes criando a mesma
região — o `existsByNomeIgnoreCase` sozinho na aplicação não garante
isso sob concorrência). `idx_hotline_telefone_regiao` cobre o padrão de
acesso principal: buscar todos os telefones de uma região (usado tanto
no agrupamento da listagem via `@EntityGraph` quanto potencialmente em
consultas futuras).

**Nota sobre unicidade case-insensitive**: o índice único do Postgres
em `nome` é case-sensitive por padrão. A checagem de duplicidade real
(case-insensitive, "Bahia" vs "bahia") é feita na camada de aplicação
via `existsByNomeIgnoreCase` antes do insert — o índice único no banco é
uma segunda camada de proteção contra duplicata exata, não substitui a
checagem de app. Se no futuro houver problema de concorrência real
(dois admins simultâneos criando a mesma região com capitalização
diferente), a solução seria um índice funcional
`CREATE UNIQUE INDEX ON hotline_regiao (lower(nome))` — não implementado
agora por ser um caso de borda de baixa probabilidade dado que é sempre
um admin autenticado criando, não usuário anônimo em massa.

---

## Mudança breaking sinalizada — nenhuma

Módulo novo, sem contrato anterior consumido pelo frontend (hoje é mock
puro em `hotlines.ts`, com telefone `"(a definir)"` para os 10
estados). O shape muda de "array de objetos" para "array com `id` e
telefones aninhados com `id` próprio" — isso é aditivo do ponto de vista
de dado (o frontend hoje não persiste nada, é troca completa de mock
para API real).

## Pendências explícitas para outros agentes

- `frontend-services`: criar `HotlinesService` consumindo
  `GET /api/v1/hotlines` (público, array direto em `data`, não paginado)
  e, para o admin, os 6 endpoints de `/api/v1/admin/hotlines`.
- `frontend`: seed inicial das 10 regiões hoje hardcoded em
  `hotlines.ts` (Bahia, Distrito Federal, Goiás, Minas Gerais, Paraná,
  Pernambuco, Rio de Janeiro, Rio Grande do Sul, Santa Catarina, São
  Paulo) precisa ser recriado via admin UI ou seed manual — não incluí
  uma migration de seed automática porque os telefones reais ainda são
  `"(a definir)"` (dado de placeholder, não dado real a persistir).

## Implementação (backend)

- `br.com.jogadoresanonimos.hotline.controller.HotlineController` —
  `GET /api/v1/hotlines`.
- `br.com.jogadoresanonimos.hotline.controller.HotlineAdminController` —
  os 6 endpoints admin, classe inteira sob
  `@PreAuthorize("hasRole('ADMIN')")`.
- `br.com.jogadoresanonimos.hotline.service.HotlineService` —
  `listarTodas`, `criarRegiao`, `removerRegiao`, `adicionarTelefone`,
  `editarTelefone`, `removerTelefone`.
- `br.com.jogadoresanonimos.hotline.entity.HotlineRegiao` /
  `HotlineTelefone` — relação `@OneToMany`/`@ManyToOne`, cascade +
  orphan removal do lado da região.
- Testes unitários em
  `backend/src/test/java/br/com/jogadoresanonimos/hotline/` (`controller/`
  e `service/`), mockando repositories/service — mesmo padrão de teste
  puro (sem `MockMvc`) usado no módulo `aviso`.
- `SecurityConfig`: adicionada
  `.requestMatchers(HttpMethod.GET, "/api/v1/hotlines").permitAll()`.

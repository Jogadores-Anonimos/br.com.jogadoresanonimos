# Contrato de API — Módulo Escritório (Escritórios Regionais / Intergrupos)

> Status: **implementado em 2026-07-12, revisado em 2026-07-13, revisado
> em 2026-07-13 (2ª revisão)** — entities, repositories, service, DTOs,
> migrations (`V14`, `V22`, `V25`) e controllers completos
> (`EscritorioRegionalController` público, `EscritorioRegionalAdminController`
> admin CRUD por nível), com testes unitários. Baseado no modelo hoje
> mockado em `frontend/src/app/pages/escritorios-regionais/escritorios-regionais.ts`
> e no escopo confirmado em `Pendencias Admin Dashboard.md` (linha
> "Intergrupos / Escritórios Regionais").
>
> **Revisão 2026-07-13 (spec aprovada pelo `ux-ui`, a pedido do
> cliente) — mudança breaking de contrato, já consumido pelo
> frontend**: ver seção "Revisão 2026-07-13" abaixo para o detalhe
> completo. Resumo:
> 1. `estado_intergrupo.nome` deixa de ser texto livre — passa a ser
>    validado contra a lista fixa das 27 UFs do Brasil (o frontend
>    envia a UF); unicidade de UF dentro da região agora retorna
>    `409 CONFLICT` (antes era `400 VALIDATION_ERROR`).
> 2. `grupo_intergrupo`: `horarios` (string) → `diasSemana: string[]` +
>    `horario: string`; `telefone` (string única) → `telefones: []`
>    (mesmo formato de `HotlineTelefoneResponse`, com `id`/`label`
>    opcional/`telefone`).
> 3. Campo `ordem` deixa de ser recebido em `criar`/`editar` nos 4
>    níveis (região, estado, grupo, escritório de serviço) — novos
>    endpoints dedicados de reordenação (`mover-cima`/`mover-baixo`)
>    substituem a edição manual do campo.
>
> **Revisão 2026-07-13 (2ª) — `EscritorioServico` ganha `bairro` e
> `telefones[]`, replicando exatamente o padrão já usado em
> `GrupoIntergrupo`/`GrupoIntergrupoTelefone`, para suportar
> preenchimento de endereço via CEP (viacep) no frontend, igual ao que
> já acontece no formulário de Grupo. Mudança aditiva (`bairro`
> opcional, `telefones` opcional e por padrão vazio) — não quebra
> nenhum client que hoje envia o payload antigo, mas o response ganha
> dois campos novos que o `frontend-services` precisa passar a ler. Ver
> seção "Revisão 2026-07-13 (2ª)" abaixo.
>
> **Revisão 2026-07-14 (mudança breaking de contrato, já consumido pelo
> frontend)**: `diasSemana: string[]` + `horario: string` (único para
> todos os dias) são substituídos por `agenda: { diaSemana, horario }[]`
> — cada dia da semana marcado passa a ter seu próprio horário. Ver
> seção "Revisão 2026-07-14" abaixo para o detalhe completo.
>
> **Revisão 2026-07-14 (2ª)** — seeds + novo endpoint de endereço
> (não-breaking) + remoção de `POST`/`DELETE /regioes` (breaking, mas
> sem consumidor real ainda em produção — ver detalhe abaixo):
> 1. Seed dos 2 Escritórios de Serviço reais (ERSJAB, JAERJ) via
>    migration `V2__seed_escritorios_servico_reais.sql`.
> 2. Seed das 5 regiões fixas de intergrupo (Sudeste, Sul, Centro-Oeste,
>    Nordeste, Norte) via migration
>    `V3__seed_regioes_intergrupo_fixas.sql`.
> 3. Novo endpoint `POST /api/v1/admin/escritorios-regionais/regioes/{regiaoId}/enderecos`
>    — cria/reaproveita o `EstadoIntergrupo` automaticamente a partir da
>    UF informada e cria o `GrupoIntergrupo` em uma única chamada, sem
>    exigir que o admin crie o Estado manualmente antes.
> 4. `POST /api/v1/admin/escritorios-regionais/regioes` e
>    `DELETE /api/v1/admin/escritorios-regionais/regioes/{id}` são
>    **removidos** — região agora é conjunto fixo de 5, seedado por
>    migration (decisão do usuário/PO, mesmo padrão já usado para as
>    27 UFs de `EstadoIntergrupo`). `PUT /regioes/{id}` (editar nome) e
>    os endpoints de leitura/reordenação continuam existindo.
>
> Ver seção "Revisão 2026-07-14 (2ª)" abaixo para o detalhe completo.
>
> **Revisão 2026-08-11 — novo endpoint público `GET
> /api/v1/unidades-federativas` (aditivo, não-breaking)**: requisito do
> cliente — a página pública de Endereços precisa indicar, para as UFs
> sem grupo físico cadastrado, que o contato deve ser feito via
> WhatsApp da Linha de Ajuda. O backend não guarda "estado sem grupo"
> como registro (ver decisão de modelagem original, `estado_intergrupo`
> só existe quando há ao menos 1 grupo) — o novo endpoint só expõe o
> mapeamento estático UF → região (IBGE) para o frontend calcular a
> diferença entre as 27 UFs e as UFs que vieram populadas em
> `GET /api/v1/escritorios-regionais`. Ver seção "Revisão 2026-08-11"
> abaixo para o detalhe completo.
>
> **Revisão 2026-08-12 — corrige o vínculo UF↔região para código fixo
> (não-breaking, complementa e corrige o risco documentado na Revisão
> 2026-08-11)**: `regiao_intergrupo` ganha coluna `codigo` (um dos 5
> valores fixos `NORTE`, `NORDESTE`, `CENTRO_OESTE`, `SUDESTE`, `SUL`,
> nunca editável via API). `GET /api/v1/unidades-federativas` passa a
> devolver `codigoRegiao` no lugar de `regiao` (nome livre); `GET
> /api/v1/escritorios-regionais` e o CRUD de região passam a incluir
> `codigo` em `RegiaoIntergrupoResponse`. O vínculo UF→região deixa de
> depender de igualdade de texto contra `RegiaoIntergrupo.nome` — editar
> o nome de exibição via `PUT /regioes/{id}` não quebra mais o
> cruzamento. Ver seção "Revisão 2026-08-12" abaixo para o detalhe
> completo.

Convenções gerais (mesmas do restante do projeto, ver `contrato-avisos.md`):
- Envelope de resposta sempre `{ success, data, error }`.
- Todos os paths sob `/api/v1`.
- Catálogo completo de `error.code` em `contrato-erro-padrao.md`.
- Nenhum token/identificador sensível em query string.
- `traceId` presente em toda resposta de erro.
- Datas em ISO-8601 UTC.

---

## Modelo de dados

Duas árvores de dados independentes, ambas devolvidas juntas no mesmo
endpoint de leitura (é assim que o frontend consome hoje — uma seção de
"escritórios de serviço" e outra de "intergrupos" na mesma página):

### 1. Escritório de Serviço (`escritorio_servico`) — entidade plana

Corresponde à interface `EscritorioServico` do mock: escritório físico
de atendimento/serviço da irmandade (ex: ERSJAB em São Paulo), não tem
hierarquia — é uma lista simples.

Campos: `nome` (obrigatório, até 200), `endereco` (obrigatório, até
300), `complemento` (opcional, até 150 — ex: CEP/sala), `bairro`
(opcional, até 150 — ver "Revisão 2026-07-13 (2ª)"), `cidade`
(obrigatório, até 150), `emails` (lista de strings, opcional),
`telefones` (lista, opcional — ver "Revisão 2026-07-13 (2ª)"),
`ordem` (inteiro, controla a posição de exibição manual).

**Decisão de modelagem — `emails` como coluna única `VARCHAR` delimitada
por `;`, não tabela filha**: o volume observado no mock é de 0-2 emails
por escritório, sempre um array pequeno e sem necessidade de busca por
email individual. Uma tabela `escritorio_servico_email` separada
resolveria o mesmo problema com uma junção a mais em toda leitura, sem
ganho real neste volume. Se no futuro for necessário consultar/filtrar
por email individualmente, é uma migration aditiva simples (nova tabela
+ backfill), não uma mudança breaking de contrato de API (a API sempre
expôs `emails` como `string[]`, o storage interno é encapsulado pela
entidade).

### 2. Intergrupo — hierarquia de 3 níveis

Corresponde a `RegiaoIntergrupos → EstadoIntergrupos → GrupoIntergrupo[]`
do mock. Três tabelas com FK em cascata:

- `regiao_intergrupo`: `nome` (obrigatório, até 100, único), `ordem`.
- `estado_intergrupo`: FK `regiao_id` (`ON DELETE CASCADE`), `uf`
  (obrigatório, `CHAR(2)`, validado contra a lista fixa das 27 UFs do
  Brasil — ver "Revisão 2026-07-13"), único dentro da região, `ordem`.
- `grupo_intergrupo`: FK `estado_id` (`ON DELETE CASCADE`), `nome`
  (obrigatório, até 200), `endereco` (obrigatório, até 300), `bairro`
  (opcional, até 150), `cidade` (obrigatório, até 150), `agenda`
  (obrigatório, lista de `{diaSemana, horario}` via tabela filha — ver
  "Revisão 2026-07-14"), `ordem`. Telefones vivem em tabela filha
  própria (`grupo_intergrupo_telefone`), mesmo padrão de
  `hotline_telefone`.

**Exclusão em cascata**: excluir uma região remove todos os estados e
grupos filhos (FK `ON DELETE CASCADE` no banco); excluir um estado
remove todos os grupos filhos. O backend suporta a cascata via FK — a
confirmação em UI ("tem certeza? isso vai apagar X grupos") é
responsabilidade do `frontend`, não duplicada aqui.

**Campo `ordem`**: adicionado nas 4 tabelas (não existe no mock, que usa
ordem de array). Decisão de modelagem: o mock tem uma ordem editorial
específica (ex: região Sudeste primeiro, dentro dela SP antes do RJ) que
não é alfabética nem cronológica — sem esse campo, a única opção seria
ordenar por `criado_em`, que não reflete a intenção de exibição do
admin ao reordenar itens depois de criados. Índices compostos
`(regiao_id, ordem)` / `(estado_id, ordem)` cobrem a leitura ordenada
por nível.

Sem paginação nesta entrega — volume total é baixo (5 regiões, ~15
estados, 80+ grupos, 2 escritórios de serviço no mock atual), a UI já
existente é drill-down (uma região/estado por vez), não lista longa
rolável.

---

## 1. `GET /api/v1/escritorios-regionais` (público, estrutura completa)

- **Autenticação**: nenhuma.
- **Idempotente**: sim.
- **Consistência**: pode tolerar poucos segundos de atraso — leitura de
  primário nesta entrega (sem cache dedicado), mesmo raciocínio do
  módulo `aviso`. Reavaliar com `system-design`/`devops` só se houver
  gargalo real.
- Retorna `escritorios` (lista plana) e `regioes` (árvore completa com
  `estados` e `grupos` aninhados), pronto para o frontend reconstruir a
  UI de drill-down sem chamadas adicionais.

**Request**: `GET /api/v1/escritorios-regionais`

**Response 200** (`EscritoriosRegionaisResponse`):
```json
{
  "success": true,
  "data": {
    "escritorios": [
      {
        "id": "uuid",
        "nome": "ERSJAB — Escritório Regional de Serviços...",
        "endereco": "Rua Paulino Guimarães nº 93, Sala 12, Luz (Centro)",
        "complemento": "CEP 01109-020",
        "bairro": "Luz",
        "cidade": "São Paulo - SP",
        "emails": ["jogadoresanonimossaopaulo@gmail.com"],
        "telefones": [
          { "id": "uuid", "label": null, "telefone": "(11) 3333-4444" }
        ],
        "ordem": 0,
        "criadoEm": "2026-07-12T14:30:00Z",
        "atualizadoEm": "2026-07-12T14:30:00Z"
      }
    ],
    "regioes": [
      {
        "id": "uuid",
        "nome": "Região Sudeste",
        "ordem": 0,
        "estados": [
          {
            "id": "uuid",
            "regiaoId": "uuid",
            "nome": "São Paulo",
            "ordem": 0,
            "grupos": [
              {
                "id": "uuid",
                "estadoId": "uuid",
                "nome": "Grupo Santa Ifigênia",
                "endereco": "R. Santa Ifigênia nº 30",
                "bairro": "Santa Ifigênia",
                "cidade": "São Paulo - SP",
                "agenda": [
                  { "diaSemana": "DOMINGO", "horario": "9h-11h" }
                ],
                "telefones": [],
                "ordem": 0,
                "criadoEm": "2026-07-12T14:30:00Z",
                "atualizadoEm": "2026-07-12T14:30:00Z"
              }
            ],
            "criadoEm": "2026-07-12T14:30:00Z",
            "atualizadoEm": "2026-07-12T14:30:00Z"
          }
        ],
        "criadoEm": "2026-07-12T14:30:00Z",
        "atualizadoEm": "2026-07-12T14:30:00Z"
      }
    ]
  },
  "error": null
}
```

**Erros**: nenhum específico (estrutura vazia é resposta válida, não erro).

---

## 2. `GET /api/v1/admin/escritorios-regionais` (admin, mesma estrutura)

- **Autenticação + autorização**: `ROLE_ADMIN`, `@PreAuthorize`.
- **Idempotente**: sim.
- **Consistência**: forte — leitura do primário.
- Mesmo shape do endpoint público (não há diferença de conteúdo entre
  admin e público nesta entidade — ao contrário de `aviso`, não existe
  aqui um estado "expirado"/rascunho que precise ser ocultado do
  público; tudo que existe é público por natureza institucional).

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 403 | `AUTH_FORBIDDEN` | autenticado sem `ROLE_ADMIN` |

---

## CRUD por nível (admin)

Todos sob `@PreAuthorize("hasRole('ADMIN')")`, consistência forte e
síncrona, não idempotentes em `POST` (cria recurso novo a cada chamada),
idempotentes em `PUT`/`DELETE`. **A partir da revisão 2026-07-13, nenhum
payload de criar/editar recebe `ordem`** — todo item novo entra no fim
da lista (`MAX(ordem) + 1` dentro do escopo do nível, ou `0` se lista
vazia); reordenar é feito só pelos endpoints dedicados da seção
"Reordenação" mais abaixo.

### 3. ~~`POST /api/v1/admin/escritorios-regionais/regioes`~~ — **removido na Revisão 2026-07-14 (2ª)**

Região deixou de ser criável via API — conjunto fixo de 5, seedado por
migration (`V3__seed_regioes_intergrupo_fixas.sql`). Ver seção "Revisão
2026-07-14 (2ª)" para o detalhe.

### 4. `PUT /api/v1/admin/escritorios-regionais/regioes/{id}`

**Request** (`EditarRegiaoRequest`): `{ "nome": "string, 1-100" }`
(mantido — só edita o nome de uma das 5 regiões já seedadas).
**Response 200**: `RegiaoIntergrupoResponse` com `estados` atual.
**Erros**: `400 VALIDATION_ERROR`, `403 FORBIDDEN`, `404 NOT_FOUND`.

### 5. ~~`DELETE /api/v1/admin/escritorios-regionais/regioes/{id}`~~ — **removido na Revisão 2026-07-14 (2ª)**

Exclusão de região deixou de ser possível via API — mesmo motivo do
item 3. Ver seção "Revisão 2026-07-14 (2ª)".

### 6. `POST /api/v1/admin/escritorios-regionais/regioes/{regiaoId}/estados`

**Request** (`CriarEstadoRequest`, revisado): `{ "uf": "SP" }` — `uf`
obrigatória, 2 letras, validada contra o enum fixo das 27 UFs
brasileiras (ver "Revisão 2026-07-13" para o enum completo e a
mensagem de erro).

**Response 201** (`EstadoIntergrupoResponse`, `grupos: []`) — inclui
`uf` e `nome` (nome por extenso derivado da UF, ex: `"São Paulo"`, só
para exibição, não é input).

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | `uf` ausente/vazia ou não é uma das 27 UFs válidas |
| 403 | `AUTH_FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `ESCRITORIO_REGIAO_NAO_ENCONTRADA` | `regiaoId` não existe |
| 409 | `ESCRITORIO_ESTADO_UF_EM_USO` | já existe um estado com essa UF nesta região (mensagem orienta o admin a editar o existente) |

### 7. `PUT /api/v1/admin/escritorios-regionais/regioes/{regiaoId}/estados/{id}`

**Request**: mesmo shape de `CriarEstadoRequest` (`{ "uf": "SP" }`).
**Response 200**: `EstadoIntergrupoResponse` com `grupos` atual.
**Erros**: mesma tabela do item 6, mais `404 NOT_FOUND` quando estado
não pertence à região do path. O `409 CONFLICT` na edição não dispara
se a UF não mudou (edita outros campos do mesmo estado sem trocar UF).

### 8. `DELETE /api/v1/admin/escritorios-regionais/regioes/{regiaoId}/estados/{id}`

Exclusão em cascata (remove grupos filhos via FK).
**Response 200**: `{ "mensagem": "Estado removido." }`.
**Erros**: `403 FORBIDDEN`, `404 NOT_FOUND`.

### 9. `POST /api/v1/admin/escritorios-regionais/estados/{estadoId}/grupos`

**Request** (`CriarGrupoRequest`, revisado na "Revisão 2026-07-14"):
```json
{
  "nome": "string, 1-200",
  "endereco": "string, 1-300",
  "bairro": "string, opcional, até 150",
  "cidade": "string, 1-150",
  "agenda": [
    { "diaSemana": "SEGUNDA", "horario": "19:00" },
    { "diaSemana": "QUINTA", "horario": "20:30" }
  ],
  "telefones": [
    { "label": "string, opcional, até 100", "telefone": "string, 1-30" }
  ]
}
```

`agenda`: lista não vazia de itens `{ diaSemana, horario }`. `diaSemana`
é um valor do enum `DiaSemana` (`DOMINGO, SEGUNDA, TERCA, QUARTA,
QUINTA, SEXTA, SABADO`), **sem `diaSemana` repetido na lista** (ex: não
pode haver duas entradas de `SEGUNDA` — retorna `400 VALIDATION_ERROR`).
`horario` é obrigatório e próprio de cada item (até 50 caracteres, ex:
`"18h"` ou `"12h30 e 18h"`) — dias diferentes podem repetir o mesmo
texto de horário ou ter horários diferentes, sem restrição nesse
sentido (ver "Revisão 2026-07-14" para o histórico da mudança de
`diasSemana[] + horario` único para `agenda[]`). `telefones` pode ser
lista vazia — grupo sem telefone cadastrado é um caso real do mock
atual (`null`).

**Response 201** (`GrupoIntergrupoResponse`).

**Erros**: `400 VALIDATION_ERROR` (`diasSemana` vazio ou com valor
inválido, `horario` vazio, `telefone` de algum item vazio/fora do
tamanho), `403 FORBIDDEN`, `404 NOT_FOUND` (`estadoId` não existe). Sem
verificação de duplicidade de nome — grupos com nomes parecidos entre
estados diferentes são esperados (ex: "Grupo Central" existe
potencialmente em mais de um estado).

### 10. `PUT /api/v1/admin/escritorios-regionais/estados/{estadoId}/grupos/{id}`

**Request**: mesmo shape de `CriarGrupoRequest`. Editar substitui a
lista de telefones inteira (mesmo padrão de idempotência de `PUT` já
usado no resto do módulo — não é PATCH incremental).
**Response 200**: `GrupoIntergrupoResponse` atualizado.
**Erros**: `400 VALIDATION_ERROR`, `403 FORBIDDEN`, `404 NOT_FOUND`
(estado ou grupo não existe, ou grupo não pertence ao estado do path).

### 11. `DELETE /api/v1/admin/escritorios-regionais/estados/{estadoId}/grupos/{id}`

**Response 200**: `{ "mensagem": "Grupo removido." }`.
**Erros**: `403 FORBIDDEN`, `404 NOT_FOUND`.

### 12. `POST /api/v1/admin/escritorios-regionais/escritorios-servico`

**Request** (`CriarEscritorioServicoRequest`):
```json
{
  "nome": "string, 1-200",
  "endereco": "string, 1-300",
  "complemento": "string, opcional, até 150",
  "bairro": "string, opcional, até 150",
  "cidade": "string, 1-150",
  "emails": ["string, cada item até 200"],
  "telefones": [
    { "label": "string, opcional, até 100", "telefone": "string, 1-100" }
  ]
}
```

`bairro` e `telefones` são opcionais (ver "Revisão 2026-07-13 (2ª)").

**Response 201** (`EscritorioServicoResponse`).
**Erros**: `400 VALIDATION_ERROR`, `403 FORBIDDEN`.

### 13. `PUT /api/v1/admin/escritorios-regionais/escritorios-servico/{id}`

**Request**: mesmo shape de `CriarEscritorioServicoRequest`. Editar
substitui a lista de telefones inteira (mesmo padrão de `PUT` idempotente
já usado em `GrupoIntergrupo`, não é PATCH incremental).
**Response 200**: `EscritorioServicoResponse` atualizado.
**Erros**: `400 VALIDATION_ERROR`, `403 FORBIDDEN`, `404 NOT_FOUND`.

### 14. `DELETE /api/v1/admin/escritorios-regionais/escritorios-servico/{id}`

**Response 200**: `{ "mensagem": "Escritório de serviço removido." }`.
**Erros**: `403 FORBIDDEN`, `404 NOT_FOUND`.

---

## Reordenação (novo — substitui edição manual de `ordem`)

Endpoints dedicados por nível, todos `POST`, sem corpo de requisição,
`ROLE_ADMIN`, consistência forte e síncrona. Semântica: troca a `ordem`
do item com a do vizinho imediato (anterior/posterior) dentro do mesmo
escopo (mesma região para estado, mesmo estado para grupo, escopo
global para região/escritório de serviço/faq) — `swap` de duas linhas,
não reindexação completa da lista (mais barato e menos propenso a
condição de corrida do que recalcular todos os `ordem` da lista a cada
chamada).

**Idempotência**: não idempotente no sentido estrito (duas chamadas
seguidas movem o item duas posições), mas é seguro para repetição
acidental de rede — mover além do limite (primeiro item tentando subir,
último tentando descer) é *no-op* silencioso (retorna 200 com o estado
inalterado), não erro.

- `POST /api/v1/admin/escritorios-regionais/regioes/{id}/mover-cima`
- `POST /api/v1/admin/escritorios-regionais/regioes/{id}/mover-baixo`
- `POST /api/v1/admin/escritorios-regionais/regioes/{regiaoId}/estados/{id}/mover-cima`
- `POST /api/v1/admin/escritorios-regionais/regioes/{regiaoId}/estados/{id}/mover-baixo`
- `POST /api/v1/admin/escritorios-regionais/estados/{estadoId}/grupos/{id}/mover-cima`
- `POST /api/v1/admin/escritorios-regionais/estados/{estadoId}/grupos/{id}/mover-baixo`
- `POST /api/v1/admin/escritorios-regionais/escritorios-servico/{id}/mover-cima`
- `POST /api/v1/admin/escritorios-regionais/escritorios-servico/{id}/mover-baixo`

**Response 200**: mesmo DTO do nível (`RegiaoIntergrupoResponse`,
`EstadoIntergrupoResponse`, `GrupoIntergrupoResponse` ou
`EscritorioServicoResponse`) — não retorna a lista inteira reordenada,
só o item movido; o frontend já tem a lista completa carregada (via
`GET` da estrutura completa) e reordena localmente ou re-busca, à
escolha do `frontend`/`frontend-services`.

**Erros**: `403 FORBIDDEN`, `404 NOT_FOUND` (id não existe, ou não
pertence ao escopo do path).

---

## Migration — `V14__cria_tabelas_escritorio.sql`

```sql
CREATE TABLE regiao_intergrupo (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nome VARCHAR(100) NOT NULL,
  ordem INTEGER NOT NULL DEFAULT 0,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX idx_regiao_intergrupo_nome ON regiao_intergrupo(nome);
CREATE INDEX idx_regiao_intergrupo_ordem ON regiao_intergrupo(ordem);

CREATE TABLE estado_intergrupo (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  regiao_id UUID NOT NULL REFERENCES regiao_intergrupo(id) ON DELETE CASCADE,
  nome VARCHAR(100) NOT NULL,
  ordem INTEGER NOT NULL DEFAULT 0,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_estado_intergrupo_regiao_id ON estado_intergrupo(regiao_id, ordem);
CREATE UNIQUE INDEX idx_estado_intergrupo_regiao_nome ON estado_intergrupo(regiao_id, nome);

CREATE TABLE grupo_intergrupo (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  estado_id UUID NOT NULL REFERENCES estado_intergrupo(id) ON DELETE CASCADE,
  nome VARCHAR(200) NOT NULL,
  endereco VARCHAR(300) NOT NULL,
  bairro VARCHAR(150),
  cidade VARCHAR(150) NOT NULL,
  telefone VARCHAR(100),
  horarios VARCHAR(300) NOT NULL,
  ordem INTEGER NOT NULL DEFAULT 0,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_grupo_intergrupo_estado_id ON grupo_intergrupo(estado_id, ordem);

CREATE TABLE escritorio_servico (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nome VARCHAR(200) NOT NULL,
  endereco VARCHAR(300) NOT NULL,
  complemento VARCHAR(150),
  cidade VARCHAR(150) NOT NULL,
  emails VARCHAR(500),
  ordem INTEGER NOT NULL DEFAULT 0,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_escritorio_servico_ordem ON escritorio_servico(ordem);
```

---

## Revisão 2026-07-13 — detalhamento completo

### 1. Estado validado contra as 27 UFs do Brasil

**Decisão de modelagem**: coluna `estado_intergrupo.nome` (texto livre)
é substituída por `uf` (`CHAR(2)`, ex: `"SP"`), validada em memória
contra um enum Java fixo `UnidadeFederativa` (as 26 UFs + DF) — não
uma tabela `unidade_federativa` no banco, porque a lista é estática por
lei (não muda em runtime, não é conteúdo administrável) e o volume é
27 valores fixos; uma tabela de referência só adicionaria uma junção
sem ganho, mesmo raciocínio já documentado para `emails` em
`EscritorioServico`. O nome por extenso (ex: `"São Paulo"`) é derivado
do enum na camada de apresentação (`EstadoIntergrupoResponse.nome`),
não persistido — evita duplicar a fonte de verdade (UF → nome) em dois
lugares.

**Unicidade por região**: índice único `(regiao_id, uf)` — substitui o
antigo `(regiao_id, nome)`. Violação retorna `409 CONFLICT` (antes era
`400 VALIDATION_ERROR` para nome duplicado) — muda o código de erro
retornado para esse caso, mas como o nome do estado nunca foi de fato
usado como texto livre em produção (o módulo foi implementado em
2026-07-12 e ainda não tem dado real além do mock convertido), não há
migração de dado a fazer além de mapear o `nome` livre existente para a
UF correspondente na migration (dado o mock trazia nomes de estados
completos e reconhecíveis, ex: "São Paulo" → `SP`).

### 2. Grupo — `diasSemana` + `horario`, `telefones[]`

**`horarios` (texto livre) → `diasSemana: DiaSemana[]` + `horario:
String`**: a motivação é permitir que o frontend filtre/exiba grupos
por dia da semana no futuro (hoje só exibe texto), sem precisar
reparsear string livre. Um único `horario` textual por grupo (não por
dia) foi a decisão confirmada pelo `ux-ui`/cliente — não modela
`{dia, horario}` por enquanto porque o dado de origem nunca tinha essa
granularidade (grupos com múltiplos dias sempre compartilhavam o mesmo
texto de horário no mock).

**`telefone` (string única) → `telefones: []`**: nova tabela
`grupo_intergrupo_telefone` — mesmo formato de `hotline_telefone`
(`id`, `label` opcional, `telefone`), reaproveitando a decisão de
modelagem já documentada em `contrato-hotlines.md` (tabela filha em vez
de coluna serializada, pois aqui o padrão de acesso already-known é
"grupo tem 0 a N telefones", igual hotline). Diferença proposital:
`label` é opcional aqui (hotline exige `label`) porque no mock de
grupo o "rótulo" do telefone raramente existe (grupos têm só números,
sem qualificação tipo "Central"/"Emergência" como nas hotlines
regionais) — tornar obrigatório forçaria o admin a inventar um label
sem sentido para todo telefone de grupo.

**Migration de dado**: `V22` faz `INSERT INTO grupo_intergrupo_telefone`
a partir do `telefone` textual antigo, quando presente, usando o texto
inteiro como único registro com `label = NULL` (não tenta separar
`"71-98624-0512 / 71-99275-7068"` em dois registros — isso exigiria um
parser heurístico de texto livre, arriscado de fazer errado em
migration; o admin pode separar manualmente depois via UI, é
mudança de dado, não de schema, então cabe em uma correção pontual sem
nova migration).

### 3. Reordenação via endpoints dedicados

**Decisão de modelagem**: endpoints de `mover-cima`/`mover-baixo`
fazem um `UPDATE` de troca (swap) entre a `ordem` do item e a do
vizinho adjacente no mesmo escopo, dentro de uma única transação — não
recalculam toda a lista (`ordem = 0, 1, 2, ...`) a cada chamada, o que
seria O(n) updates por movimento em vez de O(1). Mesmo padrão é
replicado para os 4 níveis de escritório (região/estado/grupo/serviço)
e para `faq` (ver `contrato-faq.md`), reaproveitando a mesma lógica de
serviço internamente (método utilitário compartilhado, não duplicado
por entidade — ver seção "Implementação" abaixo).

O campo `ordem` continua existindo e sendo a fonte de verdade da
ordenação de leitura (`ORDER BY ordem ASC`), só deixa de ser um input
de formulário.

### Migration — `V22__adiciona_uf_dias_semana_telefones_grupo.sql`

**Migration destrutiva confirmada previamente** (troca de coluna
`nome`→`uf` em `estado_intergrupo`, troca de `horarios`→`diasSemana`/
`horario` e `telefone`→tabela filha em `grupo_intergrupo`) — segue o
padrão *expand/contract*: adiciona as colunas/tabelas novas, migra o
dado existente, remove as colunas antigas na mesma migration porque o
volume de dado real hoje é baixo/mockado (módulo implementado há 1 dia,
sem tráfego de produção acumulado) e o contrato antigo não tem consumidor
externo além do próprio frontend deste monorepo, que está sendo
atualizado na mesma entrega — não há uma janela de compatibilidade
retroativa a proteger aqui.

```sql
ALTER TABLE estado_intergrupo ADD COLUMN uf CHAR(2);

UPDATE estado_intergrupo SET uf = CASE nome
  WHEN 'Acre' THEN 'AC' WHEN 'Alagoas' THEN 'AL' WHEN 'Amapá' THEN 'AP'
  WHEN 'Amazonas' THEN 'AM' WHEN 'Bahia' THEN 'BA' WHEN 'Ceará' THEN 'CE'
  WHEN 'Distrito Federal' THEN 'DF' WHEN 'Espírito Santo' THEN 'ES'
  WHEN 'Goiás' THEN 'GO' WHEN 'Maranhão' THEN 'MA'
  WHEN 'Mato Grosso' THEN 'MT' WHEN 'Mato Grosso do Sul' THEN 'MS'
  WHEN 'Minas Gerais' THEN 'MG' WHEN 'Pará' THEN 'PA'
  WHEN 'Paraíba' THEN 'PB' WHEN 'Paraná' THEN 'PR'
  WHEN 'Pernambuco' THEN 'PE' WHEN 'Piauí' THEN 'PI'
  WHEN 'Rio de Janeiro' THEN 'RJ' WHEN 'Rio Grande do Norte' THEN 'RN'
  WHEN 'Rio Grande do Sul' THEN 'RS' WHEN 'Rondônia' THEN 'RO'
  WHEN 'Roraima' THEN 'RR' WHEN 'Santa Catarina' THEN 'SC'
  WHEN 'São Paulo' THEN 'SP' WHEN 'Sergipe' THEN 'SE'
  WHEN 'Tocantins' THEN 'TO' ELSE NULL END;

ALTER TABLE estado_intergrupo ALTER COLUMN uf SET NOT NULL;
ALTER TABLE estado_intergrupo DROP COLUMN nome;

DROP INDEX IF EXISTS idx_estado_intergrupo_regiao_nome;
CREATE UNIQUE INDEX idx_estado_intergrupo_regiao_uf ON estado_intergrupo(regiao_id, uf);

CREATE TABLE grupo_intergrupo_dia_semana (
  grupo_id UUID NOT NULL REFERENCES grupo_intergrupo(id) ON DELETE CASCADE,
  dia_semana VARCHAR(10) NOT NULL,
  PRIMARY KEY (grupo_id, dia_semana)
);

ALTER TABLE grupo_intergrupo ADD COLUMN horario VARCHAR(50);
UPDATE grupo_intergrupo SET horario = horarios WHERE horario IS NULL;
ALTER TABLE grupo_intergrupo ALTER COLUMN horario SET NOT NULL;
ALTER TABLE grupo_intergrupo DROP COLUMN horarios;

CREATE TABLE grupo_intergrupo_telefone (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  grupo_id UUID NOT NULL REFERENCES grupo_intergrupo(id) ON DELETE CASCADE,
  label VARCHAR(100),
  telefone VARCHAR(100) NOT NULL,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_grupo_intergrupo_telefone_grupo ON grupo_intergrupo_telefone(grupo_id);

INSERT INTO grupo_intergrupo_telefone (grupo_id, label, telefone)
SELECT id, NULL, telefone FROM grupo_intergrupo WHERE telefone IS NOT NULL;

ALTER TABLE grupo_intergrupo DROP COLUMN telefone;
```

`grupo_intergrupo_dia_semana` é uma tabela de associação simples
(sem `id` próprio, PK composta) — o padrão de acesso é sempre "todos os
dias de um grupo" ou "este dia pertence a este grupo", nunca busca de
dia isolado por si só, então PK composta é suficiente e mais barata que
UUID+índice extra.

---

## Revisão 2026-07-13 (2ª) — `EscritorioServico.bairro` + `telefones[]`

**Motivação**: o frontend passará a preencher o endereço do escritório
de serviço via busca de CEP (viacep), igual já acontece hoje no
formulário de `GrupoIntergrupo` — o viacep retorna `bairro` como campo
próprio, então a entidade precisa de uma coluna dedicada para não
perder essa informação dentro de `endereco` (texto livre).

**`bairro`**: coluna `VARCHAR(150)` nullable, exatamente o mesmo padrão
já usado em `grupo_intergrupo.bairro` (mesmo tamanho, mesma
obrigatoriedade opcional) — não há motivo para divergir de um campo já
resolvido no módulo irmão.

**`telefones[]`**: nova tabela filha `escritorio_servico_telefone`,
réplica exata do padrão `grupo_intergrupo_telefone` (`id`, FK
`escritorio_id` `ON DELETE CASCADE`, `label` opcional, `telefone`
obrigatório, timestamps). Mesma decisão de modelagem já documentada
para `GrupoIntergrupoTelefone`: tabela filha em vez de coluna
serializada, porque o padrão de acesso é "escritório tem 0 a N
telefones" e o volume por escritório é pequeno o bastante para não
justificar nada mais sofisticado — reaproveita a mesma decisão, não uma
nova. `label` opcional pelo mesmo motivo do grupo (números de escritório
raramente têm um rótulo tipo "Central"/"Emergência").

Diferente do módulo de Grupo, aqui **não há migração de dado** a fazer
(não existia coluna `telefone` textual anterior em `escritorio_servico`
para migrar) — é puramente aditivo.

**Requests**: `CriarEscritorioServicoRequest`/`EditarEscritorioServicoRequest`
ganham `bairro` (opcional) e `telefones` (lista opcional, `null`/ausente
tratado como lista vazia, mesmo comportamento de `CriarGrupoRequest`).
`editar` sempre substitui a lista de telefones inteira (idempotente).

**Response**: `EscritorioServicoResponse` ganha `bairro` e
`telefones: EscritorioTelefoneResponse[]` (mesmo shape de
`GrupoTelefoneResponse`: `id`, `label` opcional, `telefone`).

### Migration — `V25__adiciona_bairro_telefones_escritorio_servico.sql`

Puramente aditiva (expand, sem contract) — não remove nem altera
nenhuma coluna existente:

```sql
ALTER TABLE escritorio_servico ADD COLUMN bairro VARCHAR(150);

CREATE TABLE escritorio_servico_telefone (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  escritorio_id UUID NOT NULL REFERENCES escritorio_servico(id) ON DELETE CASCADE,
  label VARCHAR(100),
  telefone VARCHAR(100) NOT NULL,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_escritorio_servico_telefone_escritorio ON escritorio_servico_telefone(escritorio_id);
```

### Mudança breaking desta revisão

**Não é breaking** — `bairro` e `telefones` são aditivos em request
(opcionais, comportamento antigo preservado se omitidos) e em response
(campos novos, clients existentes que ignoram campos desconhecidos no
JSON continuam funcionando). Sinalizado apenas como atenção para
`frontend-services`/`frontend`:
- Atualizar `EscritorioServico`/`CriarEscritorioServicoRequest`/
  `EditarEscritorioServicoRequest` do lado do cliente para incluir
  `bairro` e `telefones`.
- Trocar o preenchimento manual de endereço por busca de CEP (viacep) no
  formulário de escritório de serviço, reaproveitando o componente já
  usado no formulário de Grupo, se existir.
- Adicionar lista editável de telefones no formulário de escritório de
  serviço (reaproveitar o componente já usado para telefones de Grupo).

---

## Revisão 2026-07-14 — `diasSemana[] + horario` único → `agenda[]` com horário por dia

**Motivação (regra de negócio definida pelo PO/cliente)**: cada dia da
semana de um grupo pode ter um horário próprio (ex: segunda às 19h,
quinta às 20h30) — o modelo anterior (`diasSemana: string[]` + um único
`horario` aplicado a todos os dias) não permitia isso.

**Modelagem escolhida — `@Embeddable` `AgendaItemGrupo` (`diaSemana` +
`horario`) via `@ElementCollection`, não uma entidade JPA com identidade
própria**: o padrão de acesso é sempre "toda a agenda de um grupo" (nunca
busca de um item de agenda isolado por `id` próprio), o volume é
baixíssimo (poucos itens por grupo, ~300 pedidos/dia no site inteiro), e
a regra de não-duplicidade de dia é resolvida com uma `UNIQUE` composta
na tabela — não há necessidade de identidade própria por linha,
reaproveitando o mesmo raciocínio já usado para `grupo_intergrupo_dia_semana`
(PK composta, sem UUID próprio).

**Tabela nova**: `grupo_intergrupo_horario` substitui
`grupo_intergrupo_dia_semana` e a coluna `grupo_intergrupo.horario`:
```sql
CREATE TABLE grupo_intergrupo_horario (
  grupo_intergrupo_id UUID NOT NULL REFERENCES grupo_intergrupo(id) ON DELETE CASCADE,
  dia_semana VARCHAR(10) NOT NULL,
  horario VARCHAR(50) NOT NULL,
  PRIMARY KEY (grupo_intergrupo_id, dia_semana)
);
```
A partir da V13, a PK composta é
`(grupo_intergrupo_id, dia_semana, horario)`. Assim, um grupo pode ter
mais de um horário no mesmo dia, enquanto a combinação exata de dia e
horário continua protegida contra duplicidade no próprio banco.

**Migration de dado**: para cada linha de `grupo_intergrupo`, para cada
dia já marcado em `grupo_intergrupo_dia_semana`, insere uma linha em
`grupo_intergrupo_horario` com aquele dia + o `horario` único que já
existia na coluna antiga (`grupo_intergrupo.horario`) — nenhum dado é
perdido, só reestruturado (1 horário por grupo → N horários por grupo,
um por dia, todos iguais ao valor antigo até o admin editar
individualmente cada dia).

**Decisão sobre dropar a coluna/tabela antiga na mesma migration**: assim
como em `V22` (mesmo módulo, revisão anterior) e diferente do padrão
estrito de expand/contract, a coluna `grupo_intergrupo.horario` e a
tabela `grupo_intergrupo_dia_semana` são **dropadas na mesma migration**
(`V30`), não em uma `V31` futura. Motivo: não há consumidor externo do
contrato antigo além do próprio frontend deste monorepo (que é
atualizado na mesma entrega), o volume de dado real é baixo (poucas
dezenas de grupos cadastrados), e o precedente já foi estabelecido pelo
próprio módulo em `V22`. Não há janela de compatibilidade retroativa a
proteger aqui — manter as colunas mortas só adicionaria dívida técnica
sem benefício de rollback real (rollback de dado já é resolvido por
backup de banco, não por coluna duplicada).

### Migration — `V30__migra_agenda_grupo_intergrupo_para_dia_horario.sql`

```sql
CREATE TABLE grupo_intergrupo_horario (
  grupo_intergrupo_id UUID NOT NULL REFERENCES grupo_intergrupo(id) ON DELETE CASCADE,
  dia_semana VARCHAR(10) NOT NULL,
  horario VARCHAR(50) NOT NULL,
  PRIMARY KEY (grupo_intergrupo_id, dia_semana)
);

INSERT INTO grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario)
SELECT d.grupo_id, d.dia_semana, g.horario
FROM grupo_intergrupo_dia_semana d
JOIN grupo_intergrupo g ON g.id = d.grupo_id;

ALTER TABLE grupo_intergrupo DROP COLUMN horario;
DROP TABLE grupo_intergrupo_dia_semana;
```

### Contrato revisado

`GrupoIntergrupoResponse.diasSemana` e `.horario` deixam de existir —
viram `agenda: { diaSemana: string, horario: string }[]`, ordenado pela
ordem natural do enum `DiaSemana` (`DOMINGO` primeiro) para exibição
estável no frontend.

`CriarGrupoRequest`/`EditarGrupoRequest.diasSemana` + `.horario` deixam
de existir — viram `agenda: AgendaItemRequest[]` (`@NotEmpty`, cada item
com `diaSemana` e `horario` obrigatórios). Validação de negócio no
service permite repetir `diaSemana`, mas rejeita a combinação exata
`diaSemana` + `horario` e limita a agenda a 336 itens, retornando
`400 VALIDATION_ERROR` antes mesmo de tentar persistir (a PK do
banco é a rede de segurança final, não a validação primária — o erro de
aplicação é mais claro para o client do que uma violação de constraint
crua).

Os itens de `agenda` são devolvidos em ordem de dia da semana e, dentro
do mesmo dia, pela hora inicial reconhecida (`18h30` antes de `20h`).
Textos sem hora reconhecível ficam depois dos horários reconhecidos,
com ordenação textual estável.

### Migration — `V13__permite_multiplos_horarios_grupo_intergrupo.sql`

V13 não recria a tabela nem transforma linhas: apenas substitui a PK
de duas colunas pela PK de três colunas. Todas as agendas existentes
são preservadas sem alteração de valores ou contagem.

```sql
ALTER TABLE grupo_intergrupo_horario
    DROP CONSTRAINT grupo_intergrupo_horario_pkey,
    ADD CONSTRAINT grupo_intergrupo_horario_pkey
        PRIMARY KEY (grupo_intergrupo_id, dia_semana, horario);
```

### Mudança breaking desta revisão

**Sim, é breaking** — remove campos (`diasSemana`, `horario`) do
response e do request de `GrupoIntergrupoResponse`/`CriarGrupoRequest`/
`EditarGrupoRequest`, substituindo por `agenda`. Pontos de atenção para
`frontend-services`/`frontend`:
- Trocar o seletor de múltiplos dias + campo único de horário por uma
  lista editável de `{ diaSemana, horario }` (um input de horário por
  dia selecionado).
- Ler `agenda` no lugar de `diasSemana`/`horario` em qualquer lugar que
  hoje exibe a agenda do grupo (ex: card de grupo na página pública de
  escritórios regionais).
- Validar no client que não há `diaSemana` repetido antes de enviar
  (evita round-trip para descobrir o erro, mas a validação de servidor
  continua sendo a fonte de verdade).

---

## Revisão 2026-07-14 (2ª) — seeds reais e endpoint de "Endereço" sem passo manual de Estado

### 1. Seed dos 2 Escritórios Regionais de Serviço reais

Migration `V2__seed_escritorios_servico_reais.sql` insere os 2
escritórios reais da GA Brasil (ERSJAB em São Paulo, JAERJ no Rio de
Janeiro), com dados tirados do site oficial da irmandade. Campos sem
dado confirmado (telefone) ficam vazios — não inventados. `complemento`
carrega o CEP (mesmo padrão já usado no exemplo deste documento), já que
a entidade `EscritorioServico` não tem coluna própria de CEP.

### 2. Seed das 5 regiões fixas de intergrupo

Migration `V3__seed_regioes_intergrupo_fixas.sql` insere as 5 regiões
fixas usadas pelo site de referência da GA (Sudeste, Sul, Centro-Oeste,
Nordeste, Norte), na ordem `0..4`.

**Decisão final sobre `POST`/`DELETE /regioes` (confirmada pelo
usuário/PO)**: removidos do backend, não só ocultados na UI. Região
passa a ser bloqueada em todas as camadas — mesmo padrão já usado para
`EstadoIntergrupo` (as 27 UFs são um enum fixo, não uma tabela
administrável por CRUD livre). `EscritorioRegionalAdminController`
perde `criarRegiao`/`removerRegiao`, `EscritorioRegionalService` perde
os métodos correspondentes, e o DTO `CriarRegiaoRequest` (do módulo
`escritorio`, distinto do DTO homônimo do módulo `hotline`, que não foi
tocado) foi removido por ficar sem consumidor. `PUT /regioes/{id}`
(editar nome) e os endpoints de leitura/reordenação continuam intactos —
a regra de negócio é sobre quantidade fixa de regiões, não sobre
impedir correção de nome ou reordenação editorial.

### 3. `POST /api/v1/admin/escritorios-regionais/regioes/{regiaoId}/enderecos` — novo endpoint

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: não (cria recurso novo a cada chamada, mesmo padrão
  dos demais `POST` de criação deste módulo).
- **Consistência**: forte, síncrona.

Atalho de orquestração sobre o fluxo já existente
(criar Estado → criar Grupo): recebe os campos de endereço já resolvidos
pelo frontend via CEP (viacep) + `uf`, nome do grupo e agenda, e resolve
internamente se precisa criar o `EstadoIntergrupo` ou reaproveitar um já
existente — sem expor um endpoint de "criar Estado" separado para esse
fluxo.

**Request** (`CriarEnderecoGrupoRequest`):
```json
{
  "uf": "SP",
  "nome": "string, 1-200",
  "endereco": "string, 1-300",
  "bairro": "string, opcional, até 150",
  "cidade": "string, 1-150",
  "agenda": [
    { "diaSemana": "SEGUNDA", "horario": "19:00" }
  ],
  "telefones": [
    { "label": "string, opcional, até 100", "telefone": "string, 1-100" }
  ]
}
```

Mesma validação de `agenda`/`telefones` já documentada em
`CriarGrupoRequest` (item 9 acima) — reaproveita literalmente o mesmo
método de validação/persistência internamente, só muda quem resolve o
`estadoId`.

**Orquestração interna**:
1. Busca `EstadoIntergrupo` por `(regiaoId, uf)` (`findByRegiaoIdAndUf`).
2. Se não existir, cria automaticamente — mesma lógica de `criarEstado`
   (próxima `ordem` dentro da região), sem checar duplicidade porque a
   própria busca por `(regiaoId, uf)` já garante que não existe.
3. Cria o `GrupoIntergrupo` dentro do estado resolvido, reaproveitando
   o método privado `criarGrupoNoEstado` (o mesmo usado por
   `POST /estados/{estadoId}/grupos`), sem duplicar validação de
   agenda/telefones.

**Sem checagem de duplicidade de endereço**: confirmado pelo
usuário/PO — endereços físicos dificilmente colidem o suficiente para
justificar uma regra de unicidade, e criar uma adicionaria fricção
(falso positivo) sem benefício real medido.

**Response 201** (`GrupoIntergrupoResponse`, já inclui `estadoId` do
estado usado/criado — não precisa de DTO de resposta dedicado).

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | `uf` ausente/inválida, `agenda` vazia/com dia duplicado/dados obrigatórios ausentes |
| 403 | `AUTH_FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `ESCRITORIO_REGIAO_NAO_ENCONTRADA` | `regiaoId` não existe |

**Endpoints antigos preservados**: `POST /regioes/{regiaoId}/estados` e
`POST /estados/{estadoId}/grupos` continuam funcionando exatamente como
antes — o endpoint novo é um atalho adicional, não uma substituição.

### Mudança breaking desta revisão

**Parcialmente breaking**: seeds e o endpoint de endereço são aditivos
(sem impacto em contrato existente); a remoção de
`POST`/`DELETE /regioes` **é breaking** — remove 2 endpoints já
documentados (ainda sem uso real confirmado em produção, dado o volume
baixo e a idade recente do módulo). Pontos de atenção para
`frontend-services`/`frontend`:
- Remover qualquer chamada a `POST /regioes` e
  `DELETE /regioes/{id}` do `EscritoriosRegionaisService` — os 2
  endpoints não existem mais (retornam `404`/rota inexistente).
- Remover da UI de admin a ação de criar/excluir região por completo
  (não é mais uma questão de ocultar — a API não aceita mais essa
  operação). Mantém edição de nome e os botões de mover
  cima/baixo das 5 regiões seedadas.
- Trocar o fluxo de cadastro de Grupo no admin (hoje: escolher Região →
  criar/escolher Estado → criar Grupo) por: escolher Região (as 5 fixas)
  + CEP autopreenchendo UF/cidade/bairro/endereço (reaproveitar o
  componente já usado em Reuniões) + número + nome + agenda, chamando
  o novo endpoint em uma única submissão.
- O formulário de "criar Estado" continua existindo no código (endpoint
  preservado) mas deixa de ser exposto no fluxo principal de cadastro de
  endereço — decisão de UI cabe ao `ux-ui`/`frontend`.

---

## Revisão 2026-08-11 — `GET /api/v1/unidades-federativas` (UF → região)

### Motivação

Requisito do cliente: a página pública de Endereços precisa informar,
para os estados brasileiros **sem** grupo físico cadastrado, que o
contato deve ser feito via WhatsApp da Linha de Ajuda (a Linha de Ajuda
usa esse canal para formar grupos de WhatsApp que depois viram grupos
físicos). É só exibição de dado faltante — sem fluxo de criação/gestão
admin por trás.

### Levantamento que motivou a decisão

`estado_intergrupo` só existe quando há pelo menos 1 grupo físico
cadastrado na UF (criado via `POST /regioes/{regiaoId}/estados` ou
automaticamente via `POST /regioes/{regiaoId}/enderecos`) — decisão de
modelagem original deste módulo, não alterada aqui. Isso significa que
"UFs sem grupo" nunca é um registro no banco, é a ausência de um.
`RegiaoIntergrupo` só tem `nome` + `ordem` — não existe hoje nenhum
mapeamento persistido de "quais UFs pertencem a qual das 5 regiões". Se
o frontend precisar comparar as 27 UFs do Brasil contra as UFs que
vieram populadas em cada região (para descobrir as que faltam), ele
precisa saber a que região cada UF pertence — e nada no backend expunha
isso antes desta revisão.

### Decisão de modelagem

**Duas opções avaliadas**:
- (a) Mudar `estado_intergrupo` para sempre ter as 27 UFs como registro
  por região, com `grupos: []` quando não há grupo físico. Rejeitada:
  exige migration de schema/seed maior (27 estados × grupos vazios),
  ajuste de índice de unicidade e mudança de contrato já consumido pelo
  frontend, para resolver um problema que é puramente de apresentação —
  o frontend não precisa de um "estado vazio" persistido, só precisa
  saber a que região cada UF pertence para montar o diff no cliente.
- (b) **Escolhida** — manter o modelo como está (estado só existe com
  grupo) e expor UF → região como dado estático, para o frontend
  derivar "estados sem grupo" comparando a lista fixa de 27 UFs contra
  as UFs que vieram populadas no `GET /api/v1/escritorios-regionais`.

**Implementação da opção (b)**: campo `codigoRegiao` (`String`, um dos 5
códigos fixos `NORTE`, `NORDESTE`, `CENTRO_OESTE`, `SUDESTE`, `SUL`)
adicionado ao enum Java `UnidadeFederativa`, mesmo padrão já usado no
módulo (enum fixo, sem tabela de referência no banco — ver justificativa
já documentada na Revisão 2026-07-13: a divisão geográfica UF→região é
fato do mundo real imutável, o volume é 27 valores estáticos, uma
tabela só adicionaria uma junção sem ganho real). Novo endpoint público
`GET /api/v1/unidades-federativas` devolve as 27 UFs com `uf`, `nome`
(por extenso) e `codigoRegiao`.

**Vínculo entre `codigoRegiao` do enum e `RegiaoIntergrupo.nome` do
banco — risco documentado nesta revisão, eliminado na Revisão
2026-08-12**: na implementação original desta revisão, o vínculo era
por **igualdade de texto** (o valor do campo `regiao` do enum precisava
bater exatamente com `RegiaoIntergrupo.nome` seedado em
`V2__baseline_conteudo.sql`) — editar o nome de uma região via `PUT
/regioes/{id}` quebrava esse vínculo silenciosamente. Esse risco foi
corrigido na Revisão 2026-08-12 (ver seção abaixo), que introduz um
código fixo e não-editável em `regiao_intergrupo`, eliminando a
dependência de igualdade de texto. O texto desta subseção permanece
apenas como histórico da decisão original — a implementação atual do
endpoint já usa o `codigo` fixo, não mais o `nome`.

**UUID de região não é usado como chave de vínculo**: os UUIDs de
`regiao_intergrupo` são gerados em `INSERT` (`gen_random_uuid()`) e
variam por ambiente/deploy (dev, staging, produção têm UUIDs
diferentes para a mesma região "Sudeste") — não podem ser hardcoded no
enum Java. `ordem` também não serve (é reordenável pelo admin). A partir
da Revisão 2026-08-12, a chave de vínculo é o `codigo` fixo (`NORTE`,
`NORDESTE`, `CENTRO_OESTE`, `SUDESTE`, `SUL`), não mais o nome.

### Contrato do endpoint

**`GET /api/v1/unidades-federativas`**
- **Autenticação**: nenhuma.
- **Idempotente**: sim.
- **Consistência**: dado estático em memória (enum Java) — não lê
  banco, não há questão de réplica/lag aqui.

**Request**: sem parâmetros.

**Response 200** (`List<UnidadeFederativaResponse>`) — shape atualizado
na Revisão 2026-08-12 (`regiao` → `codigoRegiao`, ver seção abaixo):
```json
{
  "success": true,
  "data": [
    { "uf": "SP", "nome": "São Paulo", "codigoRegiao": "SUDESTE" },
    { "uf": "AC", "nome": "Acre", "codigoRegiao": "NORTE" }
  ],
  "error": null
}
```
Lista sempre com as 27 UFs, na ordem de declaração do enum (mesma ordem
já usada em `UnidadeFederativa.values()`, não alfabética).

**Erros**: nenhum específico (não há input, não há caso de falha além
de erro genérico de infraestrutura).

### Mudança breaking desta revisão

**Não é breaking** — endpoint novo, não altera nenhum contrato
existente. O shape do response foi ajustado um dia depois (Revisão
2026-08-12, `regiao` → `codigoRegiao`) antes de qualquer client em
produção consumir o endpoint (implementado nesta mesma revisão, ainda
não commitado) — ver detalhe e nota de compatibilidade na seção
"Revisão 2026-08-12" abaixo.

---

## Revisão 2026-08-12 — vínculo UF↔região por código fixo (corrige risco da Revisão 2026-08-11)

### Motivação

A Revisão 2026-08-11 documentou um risco conhecido: o vínculo entre
`UnidadeFederativa` (Java) e `RegiaoIntergrupo` (banco) era feito
comparando o nome de exibição da região (`RegiaoIntergrupo.nome`)
contra um texto fixo no enum Java. Como `PUT /regioes/{id}` permite ao
admin editar `nome` livremente, esse vínculo podia quebrar
silenciosamente (sem erro, sem validação — o cruzamento simplesmente
deixava de encontrar a região). Esta revisão elimina essa dependência
de texto.

### Decisão de modelagem

**Código fixo em vez de nome como chave de vínculo**: nova coluna
`regiao_intergrupo.codigo` (`VARCHAR(20)`, `NOT NULL`, `UNIQUE`), um dos
5 valores do novo enum Java `RegiaoCodigo` (`NORTE`, `NORDESTE`,
`CENTRO_OESTE`, `SUDESTE`, `SUL`). `UnidadeFederativa` passa a carregar
`RegiaoCodigo` (era `String regiao` livre) e `RegiaoIntergrupo` passa a
carregar o mesmo `RegiaoCodigo` como campo `codigo`, definido apenas no
construtor (seed) — **não existe setter/método de edição para
`codigo`**, `editar(String nome)` continua só alterando `nome`. Editar o
nome de exibição de uma região via `PUT /regioes/{id}` não afeta mais o
vínculo de forma alguma; o vínculo passa a ser `enum == enum`
(comparação de identidade Java, não string), eliminando a classe inteira
de bug (erro de acentuação, espaço, prefixo "Região", capitalização
etc. no nome já não tinha influência sobre o código, e agora sequer é
possível quebrar o vínculo por edição de nome).

**Por que não reaproveitar `RegiaoCodigo` como enum "burro" (sem
tabela) igual `UnidadeFederativa`**: diferente de UF→região (fato do
mundo real, nunca precisa ser uma linha do banco), `RegiaoIntergrupo` já
é uma entidade JPA existente com CRUD parcial (edição de nome,
reordenação) — o código só precisa ser **mais uma coluna** dela, não
uma tabela de referência nova. `@Enumerated(EnumType.STRING)` no campo
`codigo` da entidade já resolve a persistência sem overhead de junção.

### Migration — `V8__adiciona_codigo_regiao_intergrupo.sql`

Migration pontual (não é caso de V1 schema / V2 seed — é uma correção
posterior e específica), aditiva com preenchimento imediato das 5 linhas
já existentes (produção já tem essas 5 regiões reais seedadas por
`V2__baseline_conteudo.sql`, então a coluna nunca fica sem valor em
nenhuma linha, mesmo durante o deploy):

```sql
ALTER TABLE regiao_intergrupo ADD COLUMN codigo VARCHAR(20);

UPDATE regiao_intergrupo SET codigo = 'NORTE' WHERE nome = 'Norte';
UPDATE regiao_intergrupo SET codigo = 'NORDESTE' WHERE nome = 'Nordeste';
UPDATE regiao_intergrupo SET codigo = 'CENTRO_OESTE' WHERE nome = 'Centro-Oeste';
UPDATE regiao_intergrupo SET codigo = 'SUDESTE' WHERE nome = 'Sudeste';
UPDATE regiao_intergrupo SET codigo = 'SUL' WHERE nome = 'Sul';

ALTER TABLE regiao_intergrupo ALTER COLUMN codigo SET NOT NULL;

CREATE UNIQUE INDEX idx_regiao_intergrupo_codigo ON regiao_intergrupo(codigo);
```

O `UPDATE` por `nome` só é usado nesta migration pontual, para popular o
dado histórico (os 5 nomes atuais em produção, conferidos em
`V2__baseline_conteudo.sql`: `Norte`, `Nordeste`, `Centro-Oeste`,
`Sudeste`, `Sul`). Depois desta migration, `codigo` nunca mais é escrito
a partir de `nome` — é fixo por linha, nunca recalculado.

### Contrato revisado

- `GET /api/v1/unidades-federativas`: `UnidadeFederativaResponse.regiao`
  (`String` livre, ex: `"Sudeste"`) é **substituído** por
  `codigoRegiao` (`String`, um dos 5 valores fixos `NORTE`, `NORDESTE`,
  `CENTRO_OESTE`, `SUDESTE`, `SUL`). Não é mantido lado a lado com
  `regiao` — o campo antigo não tinha uso de exibição própria (servia
  só para o vínculo, que agora é feito por código), então manter os dois
  seria redundância sem propósito.
- `GET /api/v1/escritorios-regionais` e todo endpoint admin que retorna
  `RegiaoIntergrupoResponse` (CRUD por nível, reordenação): DTO ganha o
  campo `codigo` (`String`, ex: `"SUDESTE"`), como **adição** — `nome`
  continua existindo e sendo o único campo editável de exibição.
- `PUT /regioes/{id}` (`EditarRegiaoRequest`): sem mudança de request —
  continua recebendo só `{ "nome": "string" }`. Documentado
  explicitamente: **`codigo` nunca é aceito nem editável via API**, é
  fixo desde o seed (`V2__baseline_conteudo.sql` + `V8` desta revisão).
  Uma futura 6ª região (se algum dia existir) exigiria nova migration
  Flyway com `codigo` definido no `INSERT`, não uma edição via endpoint.

### Mudança breaking desta revisão

**Não é breaking para `GET /api/v1/escritorios-regionais`** e para o
CRUD/reordenação de região — `codigo` é campo aditivo no response,
`nome`/`ordem`/demais campos inalterados, nenhum request muda de shape.

**Ajuste de shape em `GET /api/v1/unidades-federativas` sem ser breaking
formal**: o endpoint foi implementado na Revisão 2026-08-11 (um dia
antes desta correção) e ainda não tinha sido commitado nem consumido
por nenhum client real — `regiao` vira `codigoRegiao` diretamente,
sem período de transição com os dois campos coexistindo. Pontos de
atenção para `frontend-services` (consumo ainda não implementado):
- Ler `codigoRegiao` (não `regiao`) em `UnidadeFederativaResponse`.
- Ao cruzar `GET /api/v1/unidades-federativas` com `GET
  /api/v1/escritorios-regionais` para calcular "UFs sem grupo físico"
  (ver pendência da Revisão 2026-08-11 abaixo), comparar
  `unidadeFederativa.codigoRegiao` contra `regiao.codigo` (novo campo em
  `RegiaoIntergrupoResponse`) — **não mais contra `regiao.nome`**. Isso
  já resolve o risco antes mesmo de a integração do frontend começar.

---

## Mudança breaking sinalizada

**Sim — revisão 2026-07-13 altera contrato já consumido pelo
frontend** (o módulo foi implementado em 2026-07-12, um dia antes desta
revisão, mas o `frontend-services`/`frontend` já pode ter integrado
`GET /api/v1/escritorios-regionais` e os formulários de admin). Pontos
de atenção para `frontend-services`/`frontend`:

- `EstadoIntergrupoResponse.nome` continua existindo no JSON (nome por
  extenso, derivado da UF), mas passa a vir acompanhado de `uf` — o
  formulário de criar/editar estado deve trocar o campo de texto livre
  por um `<select>` com as 27 UFs.
- `GrupoIntergrupoResponse.horarios` (string) deixa de existir — vira
  `diasSemana: string[]` + `horario: string`. Qualquer lugar do
  frontend que exibia `horarios` como texto direto precisa compor a
  exibição a partir dos dois campos novos.
- `GrupoIntergrupoResponse.telefone` (string ou null) deixa de existir
  — vira `telefones: []` (mesmo shape de `HotlineTelefoneResponse`,
  exceto `label` opcional aqui).
- `ordem` deixa de ser aceito em `CriarRegiaoRequest`, `CriarEstadoRequest`,
  `CriarGrupoRequest`, `CriarEscritorioServicoRequest` e seus
  equivalentes de edição — formulários que hoje têm um campo numérico
  de ordem devem removê-lo e usar os novos endpoints de
  `mover-cima`/`mover-baixo`.

Módulo original (antes desta revisão) era novo, sem contrato anterior
consumido pelo frontend (mock puro). O shape do JSON público reflete
fielmente as interfaces `EscritorioServico` e
`RegiaoIntergrupos`/`EstadoIntergrupos`/`GrupoIntergrupo` já usadas no
componente, com adição de `id`/`ordem`/timestamps em cada nível.

## Pendências para outros agentes (Revisão 2026-08-11 — UF sem grupo → WhatsApp Linha de Ajuda, contrato final já corrigido pela Revisão 2026-08-12)

- `frontend-services`: criar método novo (ex: `listarUnidadesFederativas()`)
  consumindo `GET /api/v1/unidades-federativas` (público, sem
  paginação, retorna as 27 UFs com `uf`/`nome`/`codigoRegiao`). No
  client, cruzar essa lista com `regioes[].codigo` +
  `regioes[].estados[].uf` que já vêm de `GET
  /api/v1/escritorios-regionais`: para cada região retornada nesse
  segundo endpoint, filtrar as UFs de `unidades-federativas` cujo campo
  `codigoRegiao` bate com o `codigo` da região (comparação de código
  fixo, não mais de nome — risco de vínculo por texto já eliminado na
  Revisão 2026-08-12) e que **não aparecem** em `estados[].uf` daquela
  região — essas são as "UFs sem grupo físico".
- `frontend`/`ux-ui`: na página pública de Endereços, dentro de cada
  card de região, exibir as UFs sem grupo (calculadas pelo
  `frontend-services` acima) com uma mensagem indicando que o contato
  deve ser feito via WhatsApp da Linha de Ajuda — o número/link de
  WhatsApp da Linha de Ajuda em si **não faz parte deste contrato**
  (não existe hoje nenhuma entidade de "Linha de Ajuda" no backend;
  confirmar com o `po`/cliente se esse número já existe como conteúdo
  estático em outro lugar do site — ex: rodapé — ou se precisa de uma
  nova fonte de dado, o que seria uma tarefa separada, não coberta por
  esta revisão).
- `qa-backend`: teste unitário simples cobrindo que
  `GET /api/v1/unidades-federativas` retorna exatamente 27 itens e que
  cada `regiao` retornada é um dos 5 valores esperados.

## Pendências explícitas para outros agentes

- `frontend-services`: criar/atualizar `EscritoriosRegionaisService`
  consumindo `GET /api/v1/escritorios-regionais` (público, sem
  paginação — retorna objeto único `{ escritorios, regioes }` em
  `data`). Para o admin, atualizar o service existente para o novo
  shape de `CriarEstadoRequest`/`CriarGrupoRequest` (sem `ordem`, com
  `uf`/`diasSemana`/`horario`/`telefones`) e adicionar chamadas aos 8
  novos endpoints de `mover-cima`/`mover-baixo`.
- `frontend`/`ux-ui`: trocar o campo de texto livre de estado por
  `<select>` de UF; trocar o input de horários por seletor de múltiplos
  dias + campo único de horário; trocar o campo único de telefone por
  lista editável de telefones (reaproveitar o componente já usado no
  admin de Hotlines, se existir); remover o campo numérico de "ordem"
  dos formulários e adicionar os botões de mover para cima/baixo nas
  listagens dos 4 níveis.
- `qa-backend`: escrever testes para a validação de UF (valores válidos
  e inválidos), unicidade de UF por região (`409 CONFLICT`), e para os
  endpoints de reordenação (swap correto, no-op nos limites da lista,
  escopo isolado por região/estado).

## Implementação (backend)

- Entidades: `br.com.jogadoresanonimos.escritorio.entity.{RegiaoIntergrupo,
  EstadoIntergrupo,GrupoIntergrupo,EscritorioServico,GrupoIntergrupoTelefone,
  EscritorioServicoTelefone}`.
  Novo enum `br.com.jogadoresanonimos.escritorio.entity.UnidadeFederativa`
  (27 UFs, com nome por extenso) e
  `br.com.jogadoresanonimos.escritorio.entity.DiaSemana`.
  `br.com.jogadoresanonimos.escritorio.entity.AgendaItemGrupo` (`@Embeddable`,
  `diaSemana` + `horario`), usado em `GrupoIntergrupo.agenda`
  (`@ElementCollection`, ver "Revisão 2026-07-14").
- Repositories: `br.com.jogadoresanonimos.escritorio.repository.*`.
- `br.com.jogadoresanonimos.escritorio.service.EscritorioRegionalService` —
  toda a lógica de CRUD por nível, leitura da estrutura completa e
  reordenação (método privado compartilhado de swap de `ordem`).
- `br.com.jogadoresanonimos.escritorio.controller.EscritorioRegionalController` —
  `GET /api/v1/escritorios-regionais` e `GET /api/v1/unidades-federativas`
  (Revisão 2026-08-11).
- `UnidadeFederativa` (enum) ganha campo `codigoRegiao` (era `regiao`
  livre na Revisão 2026-08-11, tipado como `RegiaoCodigo` a partir da
  Revisão 2026-08-12). `UnidadeFederativaResponse` (DTO novo) +
  `EscritorioRegionalService.listarUnidadesFederativas`. `SecurityConfig`
  libera `GET /api/v1/unidades-federativas` como público.
- Novo enum `br.com.jogadoresanonimos.escritorio.entity.RegiaoCodigo`
  (`NORTE`, `NORDESTE`, `CENTRO_OESTE`, `SUDESTE`, `SUL`) — Revisão
  2026-08-12. `RegiaoIntergrupo` ganha campo `codigo`
  (`@Enumerated(EnumType.STRING)`, `updatable = false`, definido só no
  construtor), sem método de edição — `editar(String nome)` continua
  intocado. `RegiaoIntergrupoResponse` ganha `codigo` no response.
  Migration `V8__adiciona_codigo_regiao_intergrupo.sql`.
- `br.com.jogadoresanonimos.escritorio.controller.EscritorioRegionalAdminController` —
  20 endpoints admin (1 leitura + CRUD por nível, sem
  criar/excluir região + 8 de reordenação +
  `POST /regioes/{regiaoId}/enderecos`, ver "Revisão 2026-07-14 (2ª)").
  `criarRegiao`/`removerRegiao` removidos do controller e do service;
  `EscritorioRegionalService.criarRegiao`/`removerRegiao` não existem
  mais; DTO `br.com.jogadoresanonimos.escritorio.dto.CriarRegiaoRequest`
  removido (sem consumidor).
- `CriarEnderecoGrupoRequest` (DTO novo) +
  `EscritorioRegionalService.criarEnderecoGrupo`/`criarEstadoAutomaticamente`/
  `criarGrupoNoEstado` (método privado compartilhado entre `criarGrupo` e
  `criarEnderecoGrupo`).
- `EstadoIntergrupoRepository.findByRegiaoIdAndUf` (novo, usado para
  resolver/reaproveitar o Estado no endpoint de endereço).
- Migrations `V2__seed_escritorios_servico_reais.sql` (2 escritórios
  reais) e `V3__seed_regioes_intergrupo_fixas.sql` (5 regiões fixas).
- `SecurityConfig`: `requestMatchers(HttpMethod.GET,
  "/api/v1/escritorios-regionais").permitAll()` (mantido da entrega
  anterior).
- Testes unitários em
  `backend/src/test/java/br/com/jogadoresanonimos/escritorio/` (service
  + os 2 controllers), mockando `EscritorioRegionalService` — mesmo
  padrão de teste puro (sem `MockMvc`) usado nos módulos `aviso`/`relato`.
  Cobertura adicional exigida nesta revisão: validação de UF inválida,
  conflito de UF duplicada na região, e os 8 endpoints de reordenação
  (delegado ao `qa-backend`).

## SDD 2026-08-22 — Endereços como projeção organizacional

Um `GrupoIntergrupo` novo criado por qualquer endpoint de Endereços
passa a criar, na mesma transação, uma `Reuniao` presencial canônica e
o vínculo V14. Nome, endereço/local e agenda são escritos primeiro na
reunião e espelhados no grupo; região, UF organizacional, ordem e
telefones permanecem propriedade do grupo.

Na fase dual-mode, grupos anteriores sem vínculo continuam editáveis e
removíveis isoladamente. Para grupos vinculados, edição e exclusão
operam sobre os dois registros com locks pessimistas e transação única.
`EditarGrupoRequest.atualizadoEm` é opcional por compatibilidade; quando
enviado, deve ser o `atualizadoEm` do grupo retornado pela leitura e divergência
retorna `409 REUNIAO_VERSAO_CONFLITO`.

Excluir estado que contenha grupo vinculado retorna conflito antes da
cascata. A FK `ON DELETE RESTRICT` é a proteção final. Não existe
deleção em cascata bidirecional, listener ou trigger de sincronização.
O backfill permanece manual e separado; o relatório SQL de candidatos
não executa `UPDATE` nem escolhe pares automaticamente.

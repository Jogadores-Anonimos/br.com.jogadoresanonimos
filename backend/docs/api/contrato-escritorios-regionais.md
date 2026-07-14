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

Convenções gerais (mesmas do restante do projeto, ver `contrato-avisos.md`):
- Envelope de resposta sempre `{ success, data, error }`.
- Todos os paths sob `/api/v1`.
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
  (opcional, até 150), `cidade` (obrigatório, até 150), `dias_semana`
  (obrigatório, lista de dias via tabela filha — ver "Revisão
  2026-07-13"), `horario` (obrigatório, até 50), `ordem`. Telefones
  vivem em tabela filha própria (`grupo_intergrupo_telefone`), mesmo
  padrão de `hotline_telefone`.

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
                "telefone": null,
                "horarios": "Dom 9h-11h",
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
| 403 | `FORBIDDEN` | autenticado sem `ROLE_ADMIN` |

---

## CRUD por nível (admin)

Todos sob `@PreAuthorize("hasRole('ADMIN')")`, consistência forte e
síncrona, não idempotentes em `POST` (cria recurso novo a cada chamada),
idempotentes em `PUT`/`DELETE`. **A partir da revisão 2026-07-13, nenhum
payload de criar/editar recebe `ordem`** — todo item novo entra no fim
da lista (`MAX(ordem) + 1` dentro do escopo do nível, ou `0` se lista
vazia); reordenar é feito só pelos endpoints dedicados da seção
"Reordenação" mais abaixo.

### 3. `POST /api/v1/admin/escritorios-regionais/regioes`

**Request** (`CriarRegiaoRequest`): `{ "nome": "string, 1-100" }`

**Response 201** (`RegiaoIntergrupoResponse`, `estados: []`).

**Erros**: `400 VALIDATION_ERROR` (nome vazio/duplicado — nome é único
globalmente, `409` não usado aqui pois é validação de negócio simples,
mesmo padrão de outros módulos), `403 FORBIDDEN`.

### 4. `PUT /api/v1/admin/escritorios-regionais/regioes/{id}`

**Request**: mesmo shape de `CriarRegiaoRequest`.
**Response 200**: `RegiaoIntergrupoResponse` com `estados` atual.
**Erros**: `400 VALIDATION_ERROR`, `403 FORBIDDEN`, `404 NOT_FOUND`.

### 5. `DELETE /api/v1/admin/escritorios-regionais/regioes/{id}`

Exclusão em cascata (remove estados e grupos filhos via FK).
**Response 200**: `{ "mensagem": "Região removida." }`.
**Erros**: `403 FORBIDDEN`, `404 NOT_FOUND`.

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
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `NOT_FOUND` | `regiaoId` não existe |
| 409 | `CONFLICT` | já existe um estado com essa UF nesta região (mensagem orienta o admin a editar o existente) |

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

**Request** (`CriarGrupoRequest`, revisado):
```json
{
  "nome": "string, 1-200",
  "endereco": "string, 1-300",
  "bairro": "string, opcional, até 150",
  "cidade": "string, 1-150",
  "diasSemana": ["SEGUNDA", "QUINTA"],
  "horario": "string, 1-50",
  "telefones": [
    { "label": "string, opcional, até 100", "telefone": "string, 1-30" }
  ]
}
```

`diasSemana`: lista não vazia de valores do enum `DiaSemana`
(`DOMINGO, SEGUNDA, TERCA, QUARTA, QUINTA, SEXTA, SABADO`), sem
duplicados. `horario` é um único horário textual (ex: `"18h"` ou
`"12h30 e 18h"`) aplicado a todos os dias selecionados — não há
horário por dia individual nesta entrega (o mock de origem nunca tinha
horários diferentes por dia dentro do mesmo grupo, só dias diferentes
com o mesmo horário ou textos livres já compostos; se essa necessidade
aparecer, é uma migration aditiva futura trocando `horario` único por
lista de `{dia, horario}`). `telefones` pode ser lista vazia — grupo
sem telefone cadastrado é um caso real do mock atual (`null`).

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
- Repositories: `br.com.jogadoresanonimos.escritorio.repository.*`.
- `br.com.jogadoresanonimos.escritorio.service.EscritorioRegionalService` —
  toda a lógica de CRUD por nível, leitura da estrutura completa e
  reordenação (método privado compartilhado de swap de `ordem`).
- `br.com.jogadoresanonimos.escritorio.controller.EscritorioRegionalController` —
  `GET /api/v1/escritorios-regionais`.
- `br.com.jogadoresanonimos.escritorio.controller.EscritorioRegionalAdminController` —
  os 21 endpoints admin (1 leitura + CRUD por nível + 8 de reordenação).
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

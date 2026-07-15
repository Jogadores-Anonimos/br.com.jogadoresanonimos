# Contrato de API — Módulo Reunião

> Status: **implementado em 2026-07-12** — entity, repository, service,
> DTOs, migration (`V12`) e controllers completos (`ReuniaoController`
> público, `ReuniaoAdminController` admin), com testes unitários.
> Baseado no modelo hoje hardcoded em
> `frontend/src/app/pages/reunioes/reunioes.ts` (interface `Reuniao`) e
> no escopo confirmado em `Pendencias Admin Dashboard.md` (linha
> "Reuniões").

Convenções gerais (mesmas do restante do projeto, ver
`contrato-avisos.md`/`contrato-relatos.md`):
- Envelope de resposta sempre `{ success, data, error }`.
- Todos os paths sob `/api/v1`.
- Nenhum token/identificador sensível em query string.
- `traceId` presente em toda resposta de erro.

---

## Modelo de dados

Tabela `reuniao`: `nome`, `categoria` (`presencial` | `virtual`),
`descricao`, `dia` (texto livre, ex: "Segunda-feira" — mantém o formato
já usado pelo mock do frontend, não é normalizado para enum aqui),
`horario` (texto livre, ex: "19h30"), `local` (endereço para presencial
ou texto tipo "Link de acesso" para virtual), `whatsapp` (opcional),
`dia_semana` (opcional, usado só por reuniões presenciais para o filtro
client-side), `periodo` (opcional, idem), `latitude`/`longitude`
(`DOUBLE PRECISION`, opcionais).

**Decisão confirmada com o usuário**: sem geocodificação automática
nesta v1. O admin insere `latitude`/`longitude` manualmente (copiando de
um serviço externo tipo Google Maps) — os campos são só armazenamento
numérico, sem nenhuma chamada a API de geocoding no backend.

`dia_semana`/`periodo`/`latitude`/`longitude`/`whatsapp` são nullable
porque reuniões virtuais no mock atual não preenchem esses campos (não
fazem sentido para filtro de raio/dia da semana presencial da forma como
a página de mapa usa hoje — ver `reunioes.ts`).

Sem paginação — volume institucional baixo (reuniões da associação, não
conteúdo gerado por usuário em massa), mesmo raciocínio do módulo
`aviso`. Toda a filtragem por dia/período/raio de geolocalização
continua **client-side** no frontend (`reunioes.ts` já implementa isso
sobre o array completo) — o backend só entrega a lista crua.

Sem coluna de auditoria de autor (`atualizado_por`) nesta entrega —
mesmo raciocínio do `aviso`: não é conteúdo com requisito de auditoria
de autoria como o CMS de páginas institucionais.

---

## 1. `GET /api/v1/reunioes` (público, lista todas)

- **Autenticação**: nenhuma.
- **Idempotente**: sim.
- **Consistência**: pode tolerar poucos segundos de atraso — leitura do
  primário nesta entrega (sem cache dedicado, volume baixo não justifica
  infraestrutura extra agora).
- **Sem filtro no backend**: retorna todas as reuniões cadastradas,
  ordenadas por `nome` (ordem estável e previsível; toda filtragem real
  de dia/período/raio/categoria acontece no cliente, como já é hoje com
  o mock).

**Request**: `GET /api/v1/reunioes`

**Response 200** (`List<ReuniaoResponse>`):
```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "nome": "Grupo Centro",
      "categoria": "presencial",
      "descricao": "string",
      "dia": "Segunda-feira",
      "horario": "19h30",
      "local": "endereço",
      "whatsapp": "5599999999999",
      "diaSemana": "Segunda",
      "periodo": "Noite",
      "latitude": -23.5505,
      "longitude": -46.6333
    }
  ],
  "error": null
}
```

**Erros**: nenhum específico (lista vazia é resposta válida, não erro).

---

## 2. `GET /api/v1/admin/reunioes` (admin, lista todas)

- **Autenticação + autorização**: `ROLE_ADMIN`, `@PreAuthorize`.
- **Idempotente**: sim.
- **Consistência**: forte — leitura do primário.
- Mesmo shape do endpoint público (`ReuniaoResponse`) — endpoint próprio
  existe só por consistência de rota com o resto do admin (`/admin/*`),
  não porque o admin vê campos extras.

**Response 200**: mesmo shape do item 1.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 403 | `FORBIDDEN` | autenticado sem `ROLE_ADMIN` |

---

## 3. `POST /api/v1/admin/reunioes` (admin cria)

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: não.
- **Consistência**: forte, síncrona.

**Request** (`CriarReuniaoRequest`):
```json
{
  "nome": "string, 1-200 chars",
  "categoria": "presencial | virtual",
  "descricao": "string, 1-2000 chars",
  "dia": "string, 1-100 chars",
  "horario": "string, 1-50 chars",
  "local": "string, 1-300 chars",
  "whatsapp": "string, até 20 chars, opcional",
  "diaSemana": "string, até 20 chars, opcional",
  "periodo": "string, até 20 chars, opcional",
  "latitude": -23.5505,
  "longitude": -46.6333
}
```
`latitude`/`longitude` opcionais; quando informados, validados no range
geográfico válido (`-90..90` / `-180..180`) — validação de aplicação
(`@DecimalMin`/`@DecimalMax`), sem `CHECK` no banco. Campos de texto
opcionais em branco são normalizados para `null` no service (evita
strings vazias poluindo o filtro client-side).

`whatsapp`: exceção à normalização acima — quando ausente/vazio **na
criação**, aplica-se o telefone padrão da associação
(`11 99571-6942`, 14 chars, dentro do limite de 20) em vez de `null`.
Decisão de produto: toda reunião sem contato próprio direciona para o
número institucional. Normalização ocorre no service
(`ReuniaoService.normalizarWhatsapp`), não no banco/DTO.

**Response 201**: `ReuniaoResponse` criado.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | campo obrigatório ausente/fora do tamanho; `categoria` inválida; lat/long fora do range |
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |

---

## 4. `PUT /api/v1/admin/reunioes/{id}` (admin edita)

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: sim.
- **Consistência**: forte, síncrona.

**Request** (`EditarReuniaoRequest`): mesmo shape do `CriarReuniaoRequest`.

**Response 200**: `ReuniaoResponse` atualizado.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | mesmo do create |
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `NOT_FOUND` | id não existe |

---

## 5. `DELETE /api/v1/admin/reunioes/{id}` (admin exclui)

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: sim (segunda chamada no mesmo id retorna 404).
- **Consistência**: forte, síncrona. Exclusão física — reunião não é
  referenciada por nenhuma outra entidade.

**Response 200**: `{ "success": true, "data": { "mensagem": "Reunião removida." }, "error": null }`

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `NOT_FOUND` | id não existe |

---

## Migration — `V12__cria_tabela_reuniao.sql`

```sql
CREATE TABLE reuniao (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nome VARCHAR(200) NOT NULL,
  categoria VARCHAR(20) NOT NULL,
  descricao VARCHAR(2000) NOT NULL,
  dia VARCHAR(100) NOT NULL,
  horario VARCHAR(50) NOT NULL,
  local VARCHAR(300) NOT NULL,
  whatsapp VARCHAR(20),
  dia_semana VARCHAR(20),
  periodo VARCHAR(20),
  latitude DOUBLE PRECISION,
  longitude DOUBLE PRECISION,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT chk_reuniao_categoria CHECK (categoria IN ('presencial', 'virtual'))
);

CREATE INDEX idx_reuniao_categoria ON reuniao(categoria);
```

`categoria` como `VARCHAR` + `CHECK` (em vez de tipo `ENUM` do Postgres):
mesma convenção usada nos outros módulos do projeto até agora — evita a
complexidade de `ALTER TYPE` em migration futura se um novo valor de
categoria for adicionado. Índice em `categoria` cobre o padrão de acesso
mais comum da página pública (usuário sempre escolhe presencial/virtual
antes de ver a lista) — os demais filtros (dia, período, raio) são
client-side sobre um volume pequeno de linhas, não precisam de índice
dedicado agora.

---

## Mudança breaking sinalizada — nenhuma

Módulo novo, sem contrato anterior consumido por um service real do
frontend (hoje é array hardcoded direto no componente `reunioes.ts`, sem
service dedicado). O shape de `ReuniaoResponse` replica 1:1 a interface
`Reuniao` já usada no componente, então a troca de "array hardcoded"
para "chamada a `GET /api/v1/reunioes`" não deve exigir mudança de
template/lógica de filtro/mapa — só a origem do dado.

## Pendências explícitas para outros agentes

- `frontend-services`: criar `ReunioesService` consumindo
  `GET /api/v1/reunioes` (retorna array direto em `data`, sem
  paginação) para a página pública `/reunioes`, substituindo o array
  `readonly reunioes: Reuniao[]` hoje hardcoded no componente. Para o
  admin, criar service consumindo os 4 endpoints de
  `/api/v1/admin/reunioes`.
- `frontend`: trocar o componente-placeholder da rota `/admin/reunioes`
  (`AdminEmConstrucao`) por uma tela real de CRUD (padrão
  lista+formulário, conforme spec de UI aprovada) — incluindo os dois
  campos numéricos de latitude/longitude como input manual (sem mapa de
  seleção nesta v1, é texto/número simples).

## Implementação (backend)

- `br.com.jogadoresanonimos.reuniao.controller.ReuniaoController` —
  `GET /api/v1/reunioes`.
- `br.com.jogadoresanonimos.reuniao.controller.ReuniaoAdminController` —
  os 4 endpoints admin, classe inteira sob
  `@PreAuthorize("hasRole('ADMIN')")`.
- `br.com.jogadoresanonimos.reuniao.service.ReuniaoService` —
  `listarTodas`, `criar`, `editar`, `remover`.
- Regra `permitAll` para `GET /api/v1/reunioes` adicionada em
  `SecurityConfig` (sem essa linha, `anyRequest().authenticated()`
  bloquearia o endpoint público por engano — erro já cometido e
  corrigido em módulos anteriores).
- Testes unitários em
  `backend/src/test/java/br/com/jogadoresanonimos/reuniao/` —
  `ReuniaoControllerTest`, `ReuniaoAdminControllerTest` (mockando
  `ReuniaoService`, sem `MockMvc`, mesmo padrão do módulo `aviso`) e
  `ReuniaoServiceTest` (mockando `ReuniaoRepository`, cobre criação com
  trim/normalização de campos opcionais em branco, edição e remoção,
  incluindo caminhos de `RecursoNaoEncontradoException`).

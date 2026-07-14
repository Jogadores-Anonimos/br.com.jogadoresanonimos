# Contrato de API — Módulo Mural de Apoio

> Status: **implementado em 2026-07-12** — entity, repository, service,
> DTOs, migration (`V11`) e controllers completos (`MuralDeApoioController`
> público, `MuralDeApoioAdminController` admin), com testes unitários de
> controllers e service. Baseado no modelo hoje mockado em
> `frontend/src/app/services/mural-de-apoio.ts`
> (`BannerMuralApoio`) e na decisão de arquitetura já registrada em
> `Pendencias Admin Dashboard.md` (entidade própria `Banner do Mural`,
> com toggle `ativo`, sem reuso de `CmsBloco` genérico).

Convenções gerais (mesmas do restante do projeto, ver
`contrato-relatos.md`/`contrato-auth-cms-upload.md`):
- Envelope de resposta sempre `{ success, data, error }`.
- Todos os paths sob `/api/v1`.
- Nenhum token/identificador sensível em query string.
- `traceId` presente em toda resposta de erro.
- Datas em ISO-8601 UTC.

---

## Modelo de dados e decisão de reuso de imagem

Banner não armazena a imagem em si nem a URL — só `imagem_id` (FK para
`imagem.id`), reaproveitando o fluxo de upload já existente
(`POST /api/v1/admin/imagens`, módulo `imagem`), com EXIF já removido
nesse fluxo. Isso segue exatamente o padrão que `cms_bloco` (tipo
`imagem`) já usa para resolver `imagemId → URL pública` via
`StorageClient.urlPublicaPara()` (ver `CmsService.montarBlocos`).

`BannerMuralService` resolve a URL pública em tempo de leitura (não
persiste a URL), pelo mesmo motivo do CMS: a URL pode mudar se o
storage/CDN mudar, sem precisar de migration de dados.

Sem nenhum campo de "quem publicou o banner" — anonimato institucional
já era decisão do frontend (mock atual não tem esse campo); mantido no
backend.

---

## 1. `GET /api/v1/mural-de-apoio` (público, lista banners ativos)

- **Autenticação**: nenhuma. Rota liberada explicitamente em
  `SecurityConfig` (`permitAll` só para `GET` nesse path — `POST`/`PUT`/
  `DELETE` administrativos continuam sob `/api/v1/admin/**`, que exige
  `ROLE_ADMIN`).
- **Idempotente**: sim.
- **Consistência**: pode tolerar poucos segundos de atraso — mesmo
  raciocínio do endpoint público de relatos aprovados (banner não muda
  com frequência). Sem cache dedicado nesta entrega, volume baixo
  (~2.500 req/dia no total do site) não justifica a complexidade agora;
  reavaliar com `system-design`/`devops` se crescer.
- **Paginação**: não. Lista completa de banners ativos — volume esperado
  é pequeno (poucos grupos/cartazes cadastrados, referência: mock atual
  tem 4 itens), diferente de relatos que podem crescer sem limite.
  Reavaliar paginação se o volume real divergir muito dessa expectativa.

**Request**: `GET /api/v1/mural-de-apoio`

**Response 200** (`List<BannerMuralPublicoResponse>`):
```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "imagemUrl": "https://.../chave-do-storage.jpg",
      "altText": "string",
      "legenda": "string ou null",
      "linkDestino": "string ou null",
      "dataPublicacao": "2026-07-12T14:30:00Z"
    }
  ],
  "error": null
}
```
Ordenado por `dataPublicacao` (= `criadoEm`) DESC, mesmo critério do
mock atual do frontend. Curadoria de "destaque" (top N da Home) fica a
critério do frontend — mesma decisão já tomada hoje no mock
(`MuralDeApoioService.getBannersDestaque()` fatia os primeiros N no
cliente); o backend só entrega a lista completa de ativos.

**Erros**: nenhum específico (lista vazia é resposta válida, não erro).

---

## 2. `GET /api/v1/admin/mural-de-apoio` (admin, lista todos)

- **Autenticação + autorização**: `ROLE_ADMIN` (`@PreAuthorize` na
  classe + matcher `/api/v1/admin/**` em `SecurityConfig`).
- **Idempotente**: sim.
- **Consistência**: forte — leitura do primário (admin precisa ver o
  próprio toggle refletido imediatamente após editar).
- **Paginação**: não, mesmo raciocínio do endpoint público (volume
  baixo).

**Request**: `GET /api/v1/admin/mural-de-apoio`

**Response 200** (`List<BannerMuralAdminResponse>`):
```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "imagemId": "uuid",
      "imagemUrl": "https://.../chave-do-storage.jpg",
      "altText": "string",
      "legenda": "string ou null",
      "linkDestino": "string ou null",
      "ativo": true,
      "dataPublicacao": "2026-07-12T14:30:00Z",
      "dataAtualizacao": "2026-07-12T14:30:00Z"
    }
  ],
  "error": null
}
```
Inclui banners com `ativo: false` (diferente do endpoint público).
Ordenado por `dataPublicacao` DESC.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 403 | `FORBIDDEN` | autenticado sem `ROLE_ADMIN` |

---

## 3. `POST /api/v1/admin/mural-de-apoio` (admin cria banner)

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: não (cria recurso novo a cada chamada).
- **Consistência**: forte, síncrona.
- Banner criado sempre com `ativo: true` por padrão (decisão de
  simplicidade: se o admin quiser subir um banner já inativo, precisa
  criar e depois editar com `ativo: false` — não há campo `ativo` no
  request de criação, evita confusão de estado inicial).

**Request** (`CriarBannerMuralRequest`):
```json
{
  "imagemId": "uuid, obrigatório",
  "altText": "string, obrigatório, máx 300 chars",
  "legenda": "string opcional, máx 200 chars",
  "linkDestino": "string opcional, máx 500 chars"
}
```

**Response 201** (`BannerMuralAdminResponse`): mesmo shape do item 2.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | `altText` ausente/vazio/> 300 chars; `legenda`/`linkDestino` acima do limite |
| 400 | `VALIDATION_ERROR` | `imagemId` não corresponde a upload existente |
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |

---

## 4. `PUT /api/v1/admin/mural-de-apoio/{id}` (admin edita banner, inclui toggle `ativo`)

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: sim (PUT substitui o estado inteiro do banner).
- **Consistência**: forte, síncrona.
- Único endpoint que altera `ativo` — não há endpoint dedicado tipo
  `POST /{id}/ativar`, por simetria com o padrão de "editar tudo de
  uma vez" já usado no módulo `aviso` (`EditarAvisoRequest` também
  carrega o campo booleano `fixado` inteiro, não um toggle separado).

**Request** (`EditarBannerMuralRequest`):
```json
{
  "imagemId": "uuid, obrigatório",
  "altText": "string, obrigatório, máx 300 chars",
  "legenda": "string opcional, máx 200 chars",
  "linkDestino": "string opcional, máx 500 chars",
  "ativo": true
}
```

**Response 200**: `BannerMuralAdminResponse` atualizado.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | mesmos do item 3 |
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `NOT_FOUND` | id não existe |

---

## 5. `DELETE /api/v1/admin/mural-de-apoio/{id}` (admin exclui banner)

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: sim (segunda chamada no mesmo id retorna 404).
- **Consistência**: forte, síncrona. Exclusão física (não soft-delete) —
  diferente do toggle `ativo` (que é a forma correta de "esconder sem
  apagar", conforme requisito da tarefa); `DELETE` é para remoção
  definitiva do registro. Não afeta a linha de `imagem` referenciada
  (o registro de imagem permanece; ver `ImagemOrfaGcJob` para expurgo de
  imagens órfãs, já existente no módulo `imagem`).

**Response 200**: `{ "success": true, "data": { "mensagem": "Banner removido." }, "error": null }`

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `NOT_FOUND` | id não existe |

---

## Migration — `V11__cria_tabela_banner_mural.sql`

```sql
CREATE TABLE banner_mural (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  imagem_id UUID NOT NULL REFERENCES imagem(id),
  alt_text VARCHAR(300) NOT NULL,
  legenda VARCHAR(200),
  link_destino VARCHAR(500),
  ativo BOOLEAN NOT NULL DEFAULT true,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_banner_mural_ativo_criado_em ON banner_mural(ativo, criado_em DESC);
CREATE INDEX idx_banner_mural_imagem ON banner_mural(imagem_id);
```

Índice composto `(ativo, criado_em DESC)`: padrão de acesso principal do
endpoint público é "listar ativos, ordenado por data desc" — cobre o
`WHERE ativo = true` e evita sort adicional em memória. `ativo` é
booleana (baixíssima cardinalidade), mas como o índice já é composto
com `criado_em` (que domina a ordenação), o ganho de seletividade da
primeira coluna ainda ajuda o planner a evitar sort ao filtrar
`ativo = true` — não é o caso de um índice parcial só em `ativo` puro,
que teria pouco benefício isolado.

`imagem_id` como `FK` normal (não `ON DELETE CASCADE`) — segue
exatamente o mesmo padrão de `cms_bloco.imagem_id` (V4): a imagem não é
apagada automaticamente ao apagar o banner nem vice-versa (o expurgo de
imagem órfã já tem job dedicado, `ImagemOrfaGcJob`, que decide isso de
forma centralizada, não via `CASCADE` de FK).

---

## Mudança breaking sinalizada — nenhuma

Módulo novo, sem contrato anterior consumido pelo frontend (hoje é mock
puro em `mural-de-apoio.ts`). Diferenças a alinhar com `frontend-services`
ao trocar o mock pela chamada real:
- Mock atual tem `id: string` (slug-like, ex: `'banner-grupo-cuiaba'`);
  API real usa `UUID`. Isso é esperado — o mock nunca teve contrato
  fechado; o id real não é mais legível, mas não é usado para nada além
  de key de lista/edição no frontend.
- Endpoint real **já retorna só banners ativos** (`GET /mural-de-apoio`),
  então o filtro `.filter((banner) => banner.ativo)` que hoje existe no
  service mockado (`getBannersDestaque`/`getBannersCompleto`) pode ser
  removido no lado do cliente — o backend já garante isso.
- Resposta real não é envelope de array direto sem paginação (mesmo
  formato do mock, `data: BannerMuralApoio[]`), então a troca é direta
  sem precisar adaptar para `Page<T>` (diferente de relatos/avisos).

## Pendências explícitas para outros agentes

- `frontend-services`: trocar `MuralDeApoioService` para consumir
  `GET /api/v1/mural-de-apoio` no lugar do mock, e criar o client do
  admin (`GET/POST/PUT/DELETE /api/v1/admin/mural-de-apoio`) quando a
  tela de admin for implementada.
- `security`: nenhuma pendência nova identificada — reaproveita
  autenticação/autorização já definidas para `/api/v1/admin/**`, e o
  upload de imagem em si (com remoção de EXIF) já é responsabilidade do
  módulo `imagem` existente.

## Implementação (backend)

- `br.com.jogadoresanonimos.mural.entity.BannerMural`
- `br.com.jogadoresanonimos.mural.repository.BannerMuralRepository`
- `br.com.jogadoresanonimos.mural.service.BannerMuralService` — resolve
  `imagemId → URL pública` via `StorageClient.urlPublicaPara()` (mesmo
  padrão do `CmsService`), valida existência da imagem antes de
  criar/editar.
- `br.com.jogadoresanonimos.mural.controller.MuralDeApoioController` —
  `GET /api/v1/mural-de-apoio` (público).
- `br.com.jogadoresanonimos.mural.controller.MuralDeApoioAdminController` —
  os 4 endpoints admin, classe inteira sob `@PreAuthorize("hasRole('ADMIN')")`.
- `br.com.jogadoresanonimos.config.SecurityConfig` — adicionado matcher
  explícito `permitAll` para `GET /api/v1/mural-de-apoio` (necessário
  porque a regra padrão do projeto é `anyRequest().authenticated()`, e
  esse endpoint precisa ser público). **Nota**: durante essa tarefa foi
  observado que os endpoints públicos análogos `GET /api/v1/relatos` e
  `GET /api/v1/avisos` não têm matcher `permitAll` equivalente em
  `SecurityConfig` hoje — parece uma lacuna pré-existente desses dois
  módulos (fora do escopo desta tarefa, não alterado aqui; sinalizar
  para `security`/dono desses módulos confirmar se é intencional ou bug).
- Testes unitários em
  `backend/src/test/java/br/com/jogadoresanonimos/mural/` —
  `MuralDeApoioControllerTest`, `MuralDeApoioAdminControllerTest`
  (mockando `BannerMuralService`, mesmo padrão sem `MockMvc`/contexto
  Spring usado no resto do projeto) e `BannerMuralServiceTest` (mockando
  `BannerMuralRepository`/`ImagemRepository`/`StorageClient`, cobrindo
  validação de `imagemId` inexistente, toggle de `ativo`, resolução de
  URL pública e os erros 404).

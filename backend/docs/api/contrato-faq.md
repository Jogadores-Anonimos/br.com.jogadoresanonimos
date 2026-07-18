# Contrato de API — Módulo FAQ (Perguntas e Respostas)

> Status: **implementado em 2026-07-12, revisado em 2026-07-13** —
> entity, repository, service, DTOs, migration (`V16`) e controllers
> completos (`FaqController` público, `FaqAdminController` admin), com
> testes unitários. Baseado no modelo hoje mockado em
> `frontend/src/app/pages/perguntas-e-respostas/perguntas-e-respostas.ts`
> e no escopo confirmado em `Pendencias Admin Dashboard.md` (linha
> "Perguntas e Respostas").
>
> **Revisão 2026-07-13 (spec aprovada pelo `ux-ui`, a pedido do
> cliente) — mudança breaking de contrato, já consumido pelo
> frontend**: `ordem` deixa de ser recebido em `criar`/`editar` — novo
> item entra no fim da lista automaticamente. Reordenar passa a ser
> feito por dois novos endpoints dedicados
> (`mover-cima`/`mover-baixo`), mesmo padrão usado no módulo
> `contrato-escritorios-regionais.md`.

Convenções gerais (mesmas do restante do projeto, ver
`contrato-avisos.md`/`contrato-relatos.md`):
- Envelope de resposta sempre `{ success, data, error }`.
- Todos os paths sob `/api/v1`.
- Nenhum token/identificador sensível em query string.
- `traceId` presente em toda resposta de erro.

---

## Modelo de dados

Tabela `faq`: `pergunta` (obrigatório, até 500 chars), `resposta`
(obrigatório, até 4000 chars), `ordem` (inteiro, controla a sequência de
exibição — não é alfabética nem cronológica, é curadoria manual do
admin). Sem hierarquia, sem status/toggle de ativo — CRUD simples,
ainda mais enxuto que `aviso` (sem `fixado`/`dataExpiracao`), conforme
spec aprovada.

Sem vínculo com `usuario` (nem autor nem "atualizado por") — mesmo
raciocínio do módulo `aviso`: conteúdo institucional, sem requisito de
auditoria de autoria nesta entrega.

Sem paginação — volume de perguntas frequentes é baixo por natureza
(institucional, curado manualmente pelo admin), mesmo raciocínio do
módulo `aviso`.

Mesmo DTO (`FaqResponse`) é reaproveitado tanto no endpoint público
quanto no admin — diferente de `aviso`, não há campo que precise ficar
oculto do público (não existe status de expiração/moderação para
esconder), então não há necessidade de dois DTOs distintos.

---

## 1. `GET /api/v1/faq` (público, lista todas)

- **Autenticação**: nenhuma.
- **Idempotente**: sim.
- **Consistência**: pode tolerar poucos segundos de atraso — leitura de
  primário nesta entrega (sem cache dedicado), mesmo raciocínio de
  `aviso`/`hotline`: volume baixo não justifica infraestrutura extra
  agora.
- **Ordenação**: `ordem ASC` — sequência definida manualmente pelo admin.

**Request**: `GET /api/v1/faq`

**Response 200** (`List<FaqResponse>`):
```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "pergunta": "string",
      "resposta": "string",
      "ordem": 1
    }
  ],
  "error": null
}
```

**Erros**: nenhum específico (lista vazia é resposta válida, não erro).

---

## 2. `GET /api/v1/admin/faq` (admin, lista todas)

- **Autenticação + autorização**: `ROLE_ADMIN`, `@PreAuthorize`.
- **Idempotente**: sim.
- **Consistência**: forte — leitura do primário.
- Mesmo shape e mesma ordenação (`ordem ASC`) do endpoint público — não
  há diferença de filtro entre público e admin neste módulo (sem
  status/expiração para esconder), diferente de `aviso`.

**Request**: `GET /api/v1/admin/faq`

**Response 200**: idêntico ao item 1.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 403 | `FORBIDDEN` | autenticado sem `ROLE_ADMIN` |

---

## 3. `POST /api/v1/admin/faq` (admin cria)

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: não (cria recurso novo a cada chamada).
- **Consistência**: forte, síncrona.

**Request** (`CriarFaqRequest`, revisado — sem `ordem`):
```json
{
  "pergunta": "string, 1-500 chars",
  "resposta": "string, 1-4000 chars"
}
```

Item novo entra sempre no fim da lista (`MAX(ordem) + 1`, ou `0` se
não houver nenhuma pergunta cadastrada ainda).

**Response 201** (`FaqResponse`): mesmo shape do item 1.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | pergunta/resposta ausente ou fora do tamanho |
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |

---

## 4. `PUT /api/v1/admin/faq/{id}` (admin edita)

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: sim (PUT substitui os campos editáveis).
- **Consistência**: forte, síncrona.
- Permite editar `pergunta` e `resposta` — `ordem` não é mais aceita
  neste payload (revisão 2026-07-13); reordenar é feito pelos
  endpoints dedicados da seção 6.

**Request** (`EditarFaqRequest`, revisado — sem `ordem`): mesmo shape
do `CriarFaqRequest` atual.

**Response 200**: `FaqResponse` atualizado.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | pergunta/resposta fora do tamanho |
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `NOT_FOUND` | id não existe |

---

## 5. `DELETE /api/v1/admin/faq/{id}` (admin exclui)

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: sim (segunda chamada no mesmo id retorna 404).
- **Consistência**: forte, síncrona. Exclusão física — FAQ não é
  referenciada por nenhuma outra entidade, sem motivo para soft-delete.

**Response 200**: `{ "success": true, "data": { "mensagem": "Pergunta removida." }, "error": null }`

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `NOT_FOUND` | id não existe |

---

## 6. Reordenação (novo — substitui edição manual de `ordem`)

Mesmo padrão descrito em `contrato-escritorios-regionais.md` (seção
"Reordenação"): `POST`, sem corpo, `ROLE_ADMIN`, consistência forte e
síncrona, swap de `ordem` com o vizinho adjacente (não reindexação
completa da lista). Mover além do limite (primeira pergunta subindo,
última descendo) é *no-op* silencioso (200, sem alteração).

- `POST /api/v1/admin/faq/{id}/mover-cima`
- `POST /api/v1/admin/faq/{id}/mover-baixo`

**Response 200**: `FaqResponse` do item movido.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `NOT_FOUND` | id não existe |

---

## Conteúdo — `V13__define_texto_definitivo_pagina_perguntas_e_respostas.sql`

Seed placeholder original (10 perguntas genéricas, inserido em
`V1__baseline.sql`, migration já aplicada — não editada) substituído
pelo conteúdo definitivo (9 perguntas, texto de referência fornecido
pelo cliente) via `DELETE FROM faq` + novo `INSERT`, mesmo padrão usado
em `V7`/`V8`/`V9`/`V10`/`V11`/`V12` para os demais textos institucionais
definitivos.

Diferente das páginas `CmsPagina` (`sobre`, `historia`, etc.), a FAQ não
usa HTML/`HtmlSanitizer` — `pergunta`/`resposta` são texto puro
(`VARCHAR`), sem tags. Uma resposta com itens numerados (pergunta 3)
usa quebra de linha simples (`\n\n`) dentro do próprio `VARCHAR`, sem
marcação — cabe ao frontend decidir como renderizar quebras de linha em
texto plano (`white-space: pre-line` ou equivalente), não é
responsabilidade deste contrato.

Título de exibição "Perguntas Frequentes" já é hardcoded no componente
Angular (`perguntas-e-respostas.ts`/`.html`), não em `CmsPagina` — ver
`contrato-cms-paginas-institucionais-vinte-perguntas.md`, que documenta
explicitamente que este módulo não cria `CmsPagina` (heading fixo de UI,
sem parágrafo institucional). Nenhuma mudança de contrato de API
necessária para o pedido de renomear o título de exibição.

## Migration — `V16__cria_tabela_faq.sql`

```sql
CREATE TABLE faq (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  pergunta VARCHAR(500) NOT NULL,
  resposta VARCHAR(4000) NOT NULL,
  ordem INTEGER NOT NULL DEFAULT 0,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_faq_ordem ON faq(ordem);
```

Índice simples em `ordem`: cobre diretamente o padrão de acesso
(`ORDER BY ordem ASC`), tanto no endpoint público quanto no admin.
Tabela pequena e de baixo volume — sem necessidade de índice composto.

Nenhuma migration nova é necessária para a revisão 2026-07-13 — a
coluna `ordem` já existe desde `V16`, a mudança é só de contrato de API
(deixa de ser um input) e de lógica de serviço (swap em vez de valor
livre).

---

## Mudança breaking sinalizada

**Sim — revisão 2026-07-13 altera contrato já consumido pelo
frontend**: `CriarFaqRequest`/`EditarFaqRequest` deixam de aceitar
`ordem`. Formulários de admin que hoje têm um campo numérico de ordem
devem removê-lo e usar os dois novos endpoints de
`mover-cima`/`mover-baixo`.

Módulo original (antes desta revisão) era novo, sem contrato anterior
consumido pelo frontend (mock puro). O shape do mock original
(`{ pergunta, resposta }`) foi mantido e estendido com `id` e `ordem`.

## Pendências explícitas para outros agentes

- `frontend-services`: criar/atualizar `FaqService` para
  `GET /api/v1/faq` (público, retorna array direto em `data`, sem
  paginação). Para o admin, atualizar o service para o novo shape de
  `CriarFaqRequest`/`EditarFaqRequest` (sem `ordem`) e adicionar os 2
  novos endpoints de `mover-cima`/`mover-baixo`.
- `frontend`/`ux-ui`: remover o campo numérico de "ordem" do formulário
  de pergunta/resposta e adicionar botões de mover para cima/baixo na
  listagem admin — mesmo padrão de UI a ser usado no módulo de
  Escritórios Regionais (reaproveitar componente se possível).
- `qa-backend`: escrever testes para os 2 endpoints de reordenação
  (swap correto, no-op nos limites da lista).

## Implementação (backend)

- `br.com.jogadoresanonimos.faq.controller.FaqController` —
  `GET /api/v1/faq`.
- `br.com.jogadoresanonimos.faq.controller.FaqAdminController` — os 6
  endpoints admin (4 originais + 2 de reordenação), classe inteira sob
  `@PreAuthorize("hasRole('ADMIN')")`.
- `br.com.jogadoresanonimos.faq.service.FaqService` — `listarTodos`,
  `criar`, `editar`, `remover`, `moverCima`, `moverBaixo`.
- `SecurityConfig` — `permitAll` adicionado para
  `GET /api/v1/faq`.
- Testes unitários em
  `backend/src/test/java/br/com/jogadoresanonimos/faq/` (`controller/`
  e `service/`), mockando dependências — mesmo padrão de teste puro
  (sem `MockMvc`) usado no módulo `aviso`.

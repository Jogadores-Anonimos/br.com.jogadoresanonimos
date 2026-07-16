# Contrato de API — Auditoria Admin (leitura)

> Status: **implementado em 2026-07-16**. `admin_auditoria_log` já era
> gravado (write-only) desde o módulo de relatos/CMS; esta entrega
> adiciona o endpoint de leitura paginada e o expurgo automático.

Convenções gerais (mesmas do restante do projeto, ver
`contrato-relatos.md`):
- Envelope de resposta sempre `{ success, data, error }`.
- Todos os paths sob `/api/v1`.
- Nenhum token/identificador sensível em query string.
- Query params de paginação `pagina`/`tamanho` (não `page`/`size`).
- Datas em ISO-8601 UTC.

---

## Modelo de dados e decisão de anonimato

`admin_auditoria_log` guarda `admin_id` (UUID técnico do admin que
executou a ação) e `entidade_id` (UUID técnico do recurso afetado, ex:
id do relato). Não guarda texto de relato, apelido, e-mail nem qualquer
dado de participante — só ids técnicos e metadados de transição de
status. Não há novo risco de vazamento ao expor esses campos: são ids
internos, não reidentificam usuário sem acesso direto ao banco (mesmo
nível de exposição que já existe hoje para admins olhando `RelatoAdminResponse`
por `entidade_id`). DTO de resposta (`AuditoriaLogResponse`) expõe
`adminId`/`entidadeId` como estão — não há campo de participante a
esconder aqui, diferente do módulo de relato.

---

## 1. `GET /api/v1/admin/auditoria` (admin, lista logs de auditoria)

- **Autenticação + autorização**: `ROLE_ADMIN`, `@PreAuthorize`.
- **Idempotente**: sim.
- **Consistência**: forte — leitura do primário (mesmo padrão dos
  demais endpoints admin paginados do projeto; volume baixo não
  justifica réplica).
- **Rate limit**: nenhum — confirmado que endpoints admin de leitura não
  têm rate limit hoje no projeto (só escrita).
- **Paginação**: sim, `pagina`/`tamanho`, default tamanho 20, máx 50
  (mesma validação de `RelatoService`).
- **Filtros** (todos opcionais, combináveis via AND):
  - `entidade` (string, ex: `relato`)
  - `acao` (enum `AcaoAuditoria`: `APROVAR`/`REJEITAR`/`REVERTER`/`REMOVER`/`EDITAR`)
  - `dataInicio` (ISO-8601, inclusive, filtra `criadoEm >=`)
  - `dataFim` (ISO-8601, inclusive, filtra `criadoEm <=`)
- Ordenado por `criadoEm DESC` (mais recente primeiro) — é log de
  auditoria, uso principal é investigar ação recente.

**Request**: `GET /api/v1/admin/auditoria?entidade=relato&acao=APROVAR&dataInicio=2026-01-01T00:00:00Z&dataFim=2026-07-16T23:59:59Z&pagina=0&tamanho=20`

**Response 200** (`Page<AuditoriaLogResponse>`):
```json
{
  "success": true,
  "data": {
    "conteudo": [
      {
        "id": "uuid",
        "adminId": "uuid",
        "entidade": "relato",
        "entidadeId": "uuid",
        "acao": "APROVAR",
        "statusAnterior": "pending",
        "statusNovo": "approved",
        "criadoEm": "2026-07-16T14:30:00Z"
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

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | `acao` fora do enum; `dataInicio` > `dataFim` |
| 403 | `FORBIDDEN` | autenticado sem `ROLE_ADMIN` |

---

## Expurgo automático (job agendado, sem endpoint de API)

`AuditoriaLogGcJob`, `@Scheduled` diário, mesmo padrão de
`ArquivoOrfaGcJob`/`ImagemOrfaGcJob`. Deleta registros com
`criadoEm` mais antigo que a retenção configurada via property
`app.auditoria.retencao-dias` (default `730` = 24 meses). Retenção
longa por natureza (log de auditoria, não dado operacional do dia a
dia) — property em vez de hardcoded para permitir ajuste de compliance
sem novo deploy.

Falha do job: sem mensageria/fila envolvida (delete direto via
repository, transacional), portanto sem necessidade de retry/DLQ —
se o job falhar numa execução, roda de novo no próximo dia; registros
não expurgados a mais não causam problema de integridade.

---

## Migration

Nenhuma migration criada nesta entrega. `admin_auditoria_log` já tinha
todas as colunas necessárias (`V2` criou a tabela com `criado_em`).
Índice em `criado_em`/`entidade` avaliado e **não** criado: volume da
tabela é baixo (ordem de dezenas de ações admin por dia, mesmo contexto
de escala do `CLAUDE.md`), sequential scan é aceitável na filtragem
paginada de admin e no delete diário do GC job. Reavaliar com
`system-design` se o volume crescer ou se a listagem admin apresentar
lentidão perceptível.

---

## Mudança breaking sinalizada — nenhuma

Endpoint novo, sem contrato anterior consumido pelo frontend.

## Implementação (backend)

- `br.com.jogadoresanonimos.auditoria.repository.AdminAuditoriaLogRepository`
  — método `buscarComFiltros` via `@Query` JPQL com parâmetros opcionais.
- `br.com.jogadoresanonimos.auditoria.dto.AuditoriaLogResponse` — DTO de
  resposta.
- `br.com.jogadoresanonimos.auditoria.service.AdminAuditoriaService` —
  método `listar(entidade, acao, dataInicio, dataFim, pagina, tamanho)`.
- `br.com.jogadoresanonimos.auditoria.controller.AdminAuditoriaController`
  — `GET /api/v1/admin/auditoria`.
- `br.com.jogadoresanonimos.auditoria.service.AuditoriaLogGcJob` —
  `@Scheduled` diário, property `app.auditoria.retencao-dias` (default
  `730`).

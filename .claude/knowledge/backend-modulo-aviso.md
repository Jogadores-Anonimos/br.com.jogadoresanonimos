# Backend — módulo Aviso (2026-07-12)

O que foi feito: módulo `aviso` completo em
`backend/src/main/java/br/com/jogadoresanonimos/aviso/` (entity,
repository, dto, service, controller), migration
`backend/src/main/resources/db/migration/V10__cria_tabela_aviso.sql`,
testes unitários (controllers + service, 12 testes) e contrato em
`backend/docs/api/contrato-avisos.md`.

## Endpoints
- `GET /api/v1/avisos` — público, lista ativos (não expirados),
  `fixado DESC, criado_em DESC`.
- `GET /api/v1/admin/avisos` — admin lista todos (inclusive expirados,
  com campo `expirado` calculado).
- `POST/PUT/DELETE /api/v1/admin/avisos[/{id}]` — CRUD, tudo
  `@PreAuthorize("hasRole('ADMIN')")`.

## Decisões e trade-offs
- **Sem paginação**: diferente do módulo `relato` (que pagina), avisos
  retornam `List<T>` direto — volume institucional esperado é baixo,
  paginação seria overengineering agora. Se crescer, é incremento
  aditivo (query params opcionais), não breaking change.
- **Filtro de expiração na query, não em memória**: `AvisoRepository`
  tem `findAtivosOrdenados` com `WHERE dataExpiracao IS NULL OR
  dataExpiracao > :agora`, evita trazer registros expirados do banco
  à toa.
- **`dataPublicacao` = `criado_em`**: não existe campo de agendamento
  futuro nesta entrega (todo aviso criado já está "publicado"). Se
  precisar de agendamento, é campo aditivo futuro.
- **`@Future` do Bean Validation** em `dataExpiracao` nos DTOs de
  request — valida no request tanto criação quanto edição (edição
  também rejeita data de expiração passada, mesmo que o aviso já
  estivesse expirado antes — reativar exige mandar uma data nova
  futura).
- **Índice único**: `(fixado, criado_em)` — cobre a ordenação da
  listagem (pública e admin). Sem índice em `data_expiracao` (tabela
  pequena, sequential scan aceitável no volume esperado).
- Sem vínculo com `usuario` (nem autor, nem "atualizado por") —
  diferente do CMS, que exige autoria. Aviso institucional não tinha
  esse requisito no escopo aprovado; é campo aditivo se precisar depois.

## Onde
- `backend/src/main/java/br/com/jogadoresanonimos/aviso/` (entity,
  repository, dto, service, controller)
- `backend/src/main/resources/db/migration/V10__cria_tabela_aviso.sql`
- `backend/src/test/java/br/com/jogadoresanonimos/aviso/` (controller
  + service tests)
- `backend/docs/api/contrato-avisos.md`
- `Pendencias Admin Dashboard.md` (linha "Avisos" atualizada)

## Pendência para outro agente
- `frontend-services`: trocar `AvisosService` mock
  (`frontend/src/app/services/avisos.ts`) pela chamada real a
  `GET /api/v1/avisos` (retorna array direto em `data`, sem `Page<T>`).
  Criar novo service admin consumindo os 4 endpoints
  `/api/v1/admin/avisos`.

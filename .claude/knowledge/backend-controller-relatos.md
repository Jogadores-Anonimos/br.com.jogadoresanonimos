# Backend — controller do módulo Relato implementado (moderação)

**Data**: 2026-07-12
**Agente**: backend

## O que foi feito

Um agente anterior tinha deixado o módulo `relato` incompleto: entity
(`Relato`, `StatusRelato`), repository, service (`RelatoService`) e DTOs
já existiam, migration `V7__cria_tabela_relato.sql` já aplicada, mas o
**controller inteiro faltava** — sem ele a API não existia de fato.

Completado:
- `RelatoController` (`/api/v1/relatos`): `POST` (envio autenticado,
  status inicial `pending`) e `GET` (público, só aprovados, paginado).
- `RelatoAdminController` (`/api/v1/admin/relatos`, classe inteira sob
  `@PreAuthorize("hasRole('ADMIN')")`): listar por status, editar texto,
  aprovar, rejeitar, reverter para pendente, excluir.
- Nenhuma alteração foi necessária no `RelatoService` — todos os métodos
  que os endpoints precisavam já existiam com assinatura compatível
  (`enviar`, `listarAprovados`, `listarPorStatus`, `editar`, `aprovar`,
  `rejeitar`, `reverter`, `remover`).
- Testes unitários dos dois controllers (`RelatoControllerTest`,
  `RelatoAdminControllerTest`), mockando `RelatoService` — sem
  `MockMvc`/contexto Spring, mesmo padrão de teste puro já usado no resto
  do projeto (ex: `CmsServiceTest`), porque nenhum outro módulo do
  projeto ainda tinha teste de controller para copiar o padrão exato.

## Decisões/trade-offs

- **Contrato já existia documentado** (`backend/docs/api/contrato-relatos.md`,
  spec fechada por um agente anterior no mesmo dia) e batia exatamente
  com o service já implementado — não precisou renegociar nada, só
  atualizar o status do documento de "spec fechada" para "implementado"
  e corrigir um detalhe: a spec usava `page`/`size` como nome de query
  param nos exemplos, mas o `RelatoService`/`PageResponse` já usam
  `pagina`/`tamanho` (consistente com o resto do código em português) —
  documentação ajustada para refletir o nome real em vez de mudar o
  código para bater com o texto.
- **Anonimato**: confirmado que nenhum DTO de resposta (nem o admin)
  expõe `autorId`/apelido/e-mail — só `id`, `texto`, `nomeExibicao`
  (escolhido pelo próprio autor), `status` e datas. Isso já vinha dos
  DTOs pré-existentes (`RelatoAdminResponse`, `RelatoPublicoResponse`),
  o controller só passou a resposta do service adiante sem adicionar
  campo novo.
- Rate limit de envio (5 por 10 min por `autor_id`, via
  `RateLimiterService`) já estava implementado no service — controller
  não precisou de nada adicional, exceção de rate limit já cai no
  `GlobalExceptionHandler` existente.

## Onde no projeto

- `backend/src/main/java/br/com/jogadoresanonimos/relato/controller/RelatoController.java` (novo)
- `backend/src/main/java/br/com/jogadoresanonimos/relato/controller/RelatoAdminController.java` (novo)
- `backend/src/test/java/br/com/jogadoresanonimos/relato/controller/RelatoControllerTest.java` (novo)
- `backend/src/test/java/br/com/jogadoresanonimos/relato/controller/RelatoAdminControllerTest.java` (novo)
- `backend/docs/api/contrato-relatos.md` (atualizado: status de "spec
  fechada" para "implementado", query params corrigidos)
- `Pendencias Admin Dashboard.md` (linha da tabela de Relatos atualizada)

## Validação

`./mvnw -o test` — 8 testes novos do módulo relato passam (100%). Suíte
completa: 58 testes, mesma falha de baseline pré-existente
(`JogadoresAnonimosApplicationTests.contextLoads`, falha por falta de
senha de Postgres local no ambiente — não relacionado a essa mudança,
confirmado rodando o teste isolado e lendo a causa raiz
`PSQLException: SCRAM-based authentication`). Nenhuma regressão.

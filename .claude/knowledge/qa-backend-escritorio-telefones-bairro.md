# QA — Cobertura de testes para `bairro` + `telefones` em EscritorioServico

Coordenado por: `po` | Agente: `qa-backend` | Data: 2026-07-13
Status: **concluída**

## Contexto
O `backend` adicionou suporte a `telefones` (lista, um-para-muitos via
`EscritorioServicoTelefone`) e `bairro` (campo opcional) na entidade
`EscritorioServico` (módulo `escritorio`), com migration
`V25__adiciona_bairro_telefones_escritorio_servico.sql`. Os testes
existentes só tinham sido ajustados para compatibilidade de assinatura,
sem cobertura nova dos campos. O `qa-backend` foi acionado para fechar
essa lacuna.

## O que foi feito
Estratégia: replicado o padrão já usado para telefones de
`GrupoIntergrupo` no mesmo arquivo de testes — testes unitários com
repository mockado via Mockito (sem Testcontainers); contrato de API
testado via `ResponseEntity`/`ApiResponse` no controller test mockando o
service.

Cobertura adicionada: criação com múltiplos telefones + bairro; edição
substituindo/adicionando/removendo telefones (via `substituirTelefones`,
que faz `clear` + `addAll`, mesmo comportamento de `GrupoIntergrupo`);
bordas de bairro nulo e lista de telefones vazia; resposta de API
(`EscritorioServicoResponse`/`EscritorioTelefoneResponse`) mapeando
telefones e bairro corretamente.

### Arquivos alterados
- `backend/src/test/java/br/com/jogadoresanonimos/escritorio/service/EscritorioRegionalServiceTest.java`
  — 5 testes novos + import de `EscritorioServicoTelefone`:
  - `criarEscritorioServico_deveSalvarComBairroEMultiplosTelefones`
  - `criarEscritorioServico_devePermitirBairroNuloEListaDeTelefonesVazia`
  - `editarEscritorioServico_deveSubstituirBairroEAdicionarNovosTelefonesRemovendoAntigos`
  - `editarEscritorioServico_deveEsvaziarBairroETelefonesQuandoOmitidos`
- `backend/src/test/java/br/com/jogadoresanonimos/escritorio/controller/EscritorioRegionalAdminControllerTest.java`
  — 1 teste novo:
  - `criarEscritorioServico_deveRetornarCreatedComBairroETelefones`

Total: 6 testes novos.

## Resultado
`mvnw test -Dtest=EscritorioRegionalServiceTest,EscritorioRegionalAdminControllerTest`
— Tests run: 49, Failures: 0, Errors: 0, BUILD SUCCESS. Nenhum bug
encontrado. Nenhum commit feito (regra do projeto).

## Débito técnico identificado (não resolvido nesta tarefa)
Não existem testes de integração (Testcontainers) para o módulo
`escritorio` ainda — nem para esta feature nem antes dela. Cobertura de
integração real (persistência da migration `V25`, cascade/orphanRemoval
de telefones no banco) é tarefa separada, a combinar com o agente
`backend`.

## Onde no código
- `backend/src/test/java/br/com/jogadoresanonimos/escritorio/service/EscritorioRegionalServiceTest.java`
- `backend/src/test/java/br/com/jogadoresanonimos/escritorio/controller/EscritorioRegionalAdminControllerTest.java`

## Ensinado por
qa-backend

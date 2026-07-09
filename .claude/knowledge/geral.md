# Conhecimento geral (cross-camada)

## [2026-07-09] Lição de processo: mocks que dependem de "data atual" nunca podem ser hardcoded

- **O que foi decidido/corrigido**: durante a revisão da feature
  "Reflexão do Dia", o `code-reviewer` encontrou que o mock
  `REFLEXOES_MOCK` (`frontend/src/app/services/reflexao-do-dia.ts`)
  gerava o "mês com conteúdo real" via `gerarMesesDoAno(7)` — número 7
  (julho) hardcoded porque era o mês em que o arquivo foi escrito.
  Corrigido para `gerarMesesDoAno(new Date().getMonth() + 1)`, calculado
  dinamicamente no carregamento do módulo.
- **Trade-off ou motivo**: o bug não gerava nenhum erro visível — a
  partir do mês seguinte ao de escrita do mock, o conteúdo "real"
  continuaria sempre associado ao mês antigo hardcoded, divergindo
  silenciosamente do mês que a lógica de "hoje" (`new Date()`) calculava
  como atual. Esse tipo de defeito é fácil de não pegar em QA manual
  porque só aparece meses depois, fora da janela normal de teste da
  feature.
- **Regra geral a aplicar em qualquer mock futuro (qualquer camada, não
  só frontend)**: se um mock depende de "data atual" (mês, dia, ano,
  "próximos N dias", etc.), o valor de referência deve **sempre** ser
  derivado de `new Date()` (ou equivalente no backend) no momento da
  geração/execução — nunca escrito como número mágico fixo, nem em
  código nem "documentado só em comentário" como forma de justificar o
  hardcode. Isso vale tanto para mocks de frontend quanto para
  seeds/fixtures de teste no backend.
- **Onde no código (exemplo de referência)**:
  `frontend/src/app/services/reflexao-do-dia.ts` — ver detalhe completo
  em `.claude/knowledge/service-reflexao-do-dia.md`.
- **Ensinado por**: code-reviewer (achado), frontend-services (correção aplicada).
- **Sinalizado ao `po`**: este é o tipo de lição que já apareceu de
  forma pontual em pelo menos uma feature (Reflexão do Dia) e tem
  potencial de se repetir em qualquer mock futuro que envolva datas
  (ex: "avisos com expiração", "próximas reuniões", etc.). Sugestão
  objetiva: considerar adicionar em `CLAUDE.md`, na seção de convenções
  de código, uma regra curta do tipo "mocks/seeds que dependem da data
  atual devem sempre derivar de `new Date()`/equivalente em tempo de
  execução, nunca hardcoded". Cabe ao `po` decidir se formaliza.

## [2026-07-09] Padrão adotado: nome de componente não deve colidir com nome de interface de domínio

- **O que foi decidido**: na página `/reflexao-do-dia`, o componente
  Angular foi nomeado `ReflexaoDoDiaPage` em vez de `ReflexaoDoDia`,
  porque `ReflexaoDoDia` já é o nome da interface de dados exportada
  pelo service `frontend/src/app/services/reflexao-do-dia.ts`. Nomear o
  componente com o mesmo identificador da interface relacionada causa
  colisão/erro de import quando os dois são usados no mesmo arquivo.
- **Trade-off ou motivo**: convenção simples e de baixo custo — sufixar
  o componente de página (`...Page`) quando o nome "natural" da feature
  já é usado por um tipo de domínio no service correspondente.
- **Onde no código**: `frontend/src/app/pages/reflexao-do-dia/reflexao-do-dia.ts`
  (classe `ReflexaoDoDiaPage`) vs. `frontend/src/app/services/reflexao-do-dia.ts`
  (interface `ReflexaoDoDia`).
- **Ensinado por**: frontend.
- **Nota para o `po`**: ainda é só uma ocorrência registrada; não
  sinalizando como regra permanente ainda — vale reavaliar se esse
  padrão de nome se repetir em uma 2ª feature antes de propor
  formalização no CLAUDE.md.

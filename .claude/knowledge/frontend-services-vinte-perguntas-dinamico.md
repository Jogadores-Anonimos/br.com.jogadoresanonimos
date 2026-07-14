# VintePerguntasService — perguntas dinâmicas (criar/remover/mover) (2026-07-13)

## Contexto
Backend reverteu a decisão original de `numero` fixo 1-20 (nunca chegou a
ter seed real, tabela sempre esteve vazia — causa raiz de bug separado de
"página pública sumida"). Novo contrato:
`backend/docs/api/contrato-cms-paginas-institucionais-vinte-perguntas.md`,
seção "Revisão — perguntas dinâmicas".

## O que foi feito
- `frontend/src/app/services/vinte-perguntas.model.ts` — `VinteQuestaoResponse`
  ganhou `id: string` (chave estável) e `ordem: number` (posição mutável).
  `numero` foi mantido (agora é derivado/1-based, só para compatibilidade
  com o cálculo de resultado do quiz público que já dependia dele). Novo
  `VinteQuestaoCriarRequest` (`{ texto: string }`).
- `frontend/src/app/services/vinte-perguntas.ts`:
  - **Breaking change aplicado**: `atualizarPergunta(numero, req)` virou
    `atualizarPergunta(id: string, req)` — path agora é `PUT
    /admin/vinte-perguntas/{id}` (era `{numero}`).
  - Novo `criarPergunta(texto: string)` → `POST /admin/vinte-perguntas`
    (entra no final da lista).
  - Novo `removerPergunta(id: string)` → `DELETE
    /admin/vinte-perguntas/{id}`, retorna `ApiEnvelope<{ mensagem: string
    }>` (não `null` — o backend responde `MensagemResponse`).
  - Novos `moverPerguntaCima(id)` / `moverPerguntaBaixo(id)` → `POST
    .../{id}/mover-cima` e `.../mover-baixo`, paridade com `FaqService`.
    Implementados por completude de contrato mesmo sendo "bônus" (backend
    já expõe); UX de admin decide se usa.
- `frontend/src/app/services/vinte-perguntas.mock.ts` — mock reescrito:
  antes era um array literal de objetos `{numero, texto}`; agora gera
  `id`/`ordem`/`numero`/`texto` a partir de uma lista de textos, para não
  duplicar 20 objetos manualmente e já nascer com o shape novo. Os métodos
  de mock (criar/remover/mover) mutam esse array in-place, mesmo padrão
  "mock mutável só para dev local" já usado no restante do arquivo.

## Trade-off
- Todos os métodos seguem tratamento de erro com `catchError` e mock
  espelhando a mesma assinatura pública (`environment.useMocks`), sem
  exceção — inclusive os "bônus" de mover, que não foram pedidos
  explicitamente mas já tinham endpoint pronto no backend.

## Achado — pendência para o agente `frontend`
`frontend/src/app/pages/admin/pagina-editor/pagina-editor.ts` já tinha uma
implementação prévia (de outra sessão/agente, em paralelo) de
criar/remover pergunta apoiada na assinatura **antiga** do service
(`numero: number`, `criarPergunta()` sem argumento). Com o contrato novo
aplicado, a build quebra em 3 pontos confirmados via `npx tsc --noEmit`:
- linha do `atualizarPergunta(numero, ...)` — esperava `string`, recebeu
  `number`.
- linha do `criarPergunta()` — agora exige o argumento `texto`.
- linha do `removerPergunta(numero)` — esperava `string`, recebeu
  `number`; e o consumo do retorno também precisa mudar (não é mais
  `null`, é `{ mensagem }`).
Não editei esse arquivo (fora do escopo de `frontend-services`, é
componente/página) — precisa ser ajustado pelo agente `frontend` trocando
os signals `perguntasSalvandoNumero`/`perguntasRemovendoNumero` (e o
tracking de item por `numero`) para usar `id: string`.

## Onde no projeto
- `frontend/src/app/services/vinte-perguntas.ts`
- `frontend/src/app/services/vinte-perguntas.model.ts`
- `frontend/src/app/services/vinte-perguntas.mock.ts`
- Pendência aberta em `frontend/src/app/pages/admin/pagina-editor/pagina-editor.ts`
- Contrato de referência:
  `backend/docs/api/contrato-cms-paginas-institucionais-vinte-perguntas.md`

# Frontend — Páginas institucionais migradas para CMS (2026-07-12)

## O que foi feito
12 páginas institucionais Angular trocaram texto hardcoded por blocos
vindos de `Cms.getPaginaPublica(slug)` (`frontend/src/app/services/cms.ts`),
seguindo o padrão já usado em `literatura.ts`: `ngOnInit` → filtra
`resposta.data.blocos` por `tipo === 'texto'` → renderiza com
`@for` + `[innerHTML]="bloco.conteudo"`.

Páginas migradas: `sobre`, `historia`, `anonimato`, `privacidade`,
`termos`, `jogo-compulsivo`, `uma-nova-chance`, `programa-de-recuperacao`,
`programa-de-unidade`, `contribuir` (só o texto informativo, o gate
"você é membro?" continua fixo no componente).

`vinte-perguntas`: sem `CmsPagina` própria (backend confirmou não haver
texto introdutório separado do quiz). Criado service novo
`frontend/src/app/services/vinte-perguntas.ts` (+ `.model.ts`/`.mock.ts`,
mesmo padrão de `cms.ts`/`arquivo.ts`: `HttpClient` + `environment.useMocks`
+ `ApiEnvelope`) consumindo `GET /api/v1/vinte-perguntas`. O componente
`vinte-perguntas.ts` buscou o texto das 20 perguntas nesse service em
`ngOnInit`, ordenando por `numero`; toda a lógica de quiz (progresso,
contagem de "sim", resultado) ficou intacta, só a fonte do texto mudou.
Botão "Começar o questionário" agora fica `[disabled]` até `perguntas`
carregar, evitando estado quebrado se o usuário clicar antes da resposta
da API chegar.

## Fora do escopo desta entrega (decisão do backend, não mexida aqui)
`hotlines` e `perguntas-e-respostas` não têm `CmsPagina` — são 100% lista
estruturada (accordion por estado / FAQ), sem texto introdutório
separado. Ficam como pendência de CRUD estruturado próprio (ver
`Pendencias Admin Dashboard.md`), não como página CMS.

## Trade-off visual aceito (não é bug)
A migration do backend (`V9__seed_paginas_institucionais_cms.sql`)
achatou `<h2>`/`<ol>`/`<li>`/`<strong>` em blocos de texto simples
sequenciais (`<p>...</p>` puro, sem negrito), porque o `HtmlSanitizer`
do CMS só permite `b/i/p/br/a`. Isso quebra a fidelidade visual 1:1 que a
tarefa original pedia para as páginas que tinham heading/lista numerada
estilizada em grid de cards:
- `sobre`: os dois `<h2>` viraram blocos de texto simples no meio do
  fluxo (sem destaque visual de heading).
- `programa-de-recuperacao` (12 passos) e `programa-de-unidade` (12
  princípios): o grid de cards numerados (`.passos-lista`/
  `.principios-lista`, `display: grid; grid-template-columns: repeat(3,
  1fr)`) foi removido — os 12 itens agora renderizam como parágrafos
  simples em sequência (mesmo estilo de `.section p` das páginas
  simples), porque o dado migrado é uma sequência plana de blocos de
  texto sem nenhum diferenciador entre "lead" e "item de lista".
  Não dava para recuperar isso via CSS/frontend sem sniffar conteúdo por
  regex (frágil, evitado de propósito).
- `historia.css` tinha uma classe `.placeholder-note` (nota em itálico
  com borda) amarrada a um `<p>` específico — a nota de "data a
  confirmar" agora renderiza como parágrafo normal, sem esse destaque,
  porque o bloco CMS não carrega `class`.
- `contribuir`: a lista `<ul>` de formas de contribuição (Pix,
  transferência, presencial) virou 3 blocos de texto em sequência dentro
  de `.contribuir__metodos`, sem marcadores de lista.

Se o cliente quiser recuperar a fidelidade visual completa, as opções são
(a) ampliar a allowlist do `HtmlSanitizer` no backend para aceitar
`h2/strong/ol/li` com sanitização adequada, ou (b) o CMS ganhar um campo
de "tipo de bloco" mais rico (ex: bloco de heading dedicado, bloco de
lista numerada dedicado) em vez de só `texto/imagem/arquivo` — ambas são
decisões de `backend`/`system-design`, não algo que o frontend deveria
contornar sozinho.

## Onde
- `frontend/src/app/pages/{sobre,historia,anonimato,privacidade,termos,
  jogo-compulsivo,uma-nova-chance,programa-de-recuperacao,
  programa-de-unidade,contribuir,vinte-perguntas}/*.ts|*.html|*.css`
- `frontend/src/app/services/vinte-perguntas.ts` (novo)
- `frontend/src/app/services/vinte-perguntas.model.ts` (novo)
- `frontend/src/app/services/vinte-perguntas.mock.ts` (novo)

## Validação
`ng build` (dev e produção) rodou limpo, sem erros novos — os dois
warnings existentes (`RouterLink` não usado em `reunioes`, `leaflet` não
é ESM) são pré-existentes, não relacionados a esta tarefa.

# Frontend — Lista e editor de blocos do CMS de páginas institucionais (admin)

Data: 2026-07-12. Agente: `frontend`.

## O que foi feito
Dois componentes novos, sem tocar em `app.routes.ts` (evitar conflito com
outro agente trabalhando em paralelo nas rotas):

- `frontend/src/app/pages/admin/paginas-lista/` (`PaginasLista`) — tabela
  simples (título, última edição, link "Editar"), consome
  `Cms.listarPaginasAdmin()`. Sem busca/paginação/criar/excluir (regra de
  negócio confirmada em `Pendencias Admin Dashboard.md`).
- `frontend/src/app/pages/admin/pagina-editor/` (`PaginaEditor`) — recebe
  `slug` via `ActivatedRoute` (rota esperada: `/admin/paginas/:slug`),
  consome `Cms.getPaginaAdmin(slug)` / `Cms.atualizarPagina(slug, req)`.
  Lista vertical de blocos (texto/imagem), mover para cima/baixo
  (desabilitado nas pontas), remover com `window.confirm`, adicionar bloco
  no fim, botão único "Salvar alterações" (topo e rodapé), indicador
  "Você tem alterações não salvas" (signal `temAlteracoesPendentes`),
  aviso nativo `beforeunload` quando há edição pendente, e confirm ao usar
  o botão "Voltar" (não há `CanDeactivate` guard — decisão consciente:
  volume/risco baixos, `beforeunload` + confirm no botão de saída interno
  cobrem o caso real sem adicionar mais uma peça de infra de rota).

## Caso especial `vinte-perguntas`
Tela ganha uma segunda seção fixa "As 20 perguntas" quando
`slug === 'vinte-perguntas'`. Confirmado lendo
`backend/docs/api/contrato-cms-paginas-institucionais-vinte-perguntas.md`
e `frontend/src/app/services/vinte-perguntas.ts`: as 20 perguntas **não**
são bloco CMS genérico (tabela dedicada `vinte_questao`, chave estável
`numero` 1-20, sem reordenação). Backend já documenta
`GET/PUT /api/v1/admin/vinte-perguntas` — **mas o service Angular
(`VintePerguntasService`) só tem `listar()` público**, sem os métodos
admin (`listarAdmin`/`atualizarPergunta`). Por isso a seção de perguntas
na UI está **read-only** (textarea `disabled`) com aviso visível
explicando que falta o service admin — pendência sinalizada para
`frontend-services`, não implementada por mim (fora do meu escopo chamar
API diretamente).

## Gap adicional encontrado: upload de imagem
Não existe `Imagem`/`ImagemService` em `frontend/src/app/services/`,
apesar do backend Bloco 3 (`POST/DELETE /api/v1/admin/imagens`) já estar
documentado como implementado em
`backend/docs/api/contrato-auth-cms-upload.md`. `CmsBlocoImagem` espera
`imagemId` já resolvido (upload dissociado da composição de página, por
design). O bloco de imagem no editor permite editar `alt` e mostra
preview quando já existe `url`, mas o botão de trocar/enviar imagem foi
substituído por um aviso inline ("Upload de imagem indisponível... " )
em vez de eu implementar a chamada HTTP — mesma régua usada para
vinte-perguntas. **Pendência clara para `frontend-services`**: criar
`Imagem`/`ImagemService` com `uploadImagem(file, alt)` (multipart, mesmo
padrão de `Arquivo.uploadArquivo`).

## Trade-offs
- Editor não usa NgRx/state global — signals locais do componente (volume
  de dados pequeno, um único formulário de página por vez).
- Reordenação recalcula `ordem` de todos os blocos localmente (1..N) antes
  de salvar — o PUT é idempotente e substitui o array inteiro, não há
  reordenação parcial no backend.
- `sair()` navega para `/admin/paginas` (path esperado para a rota da
  lista) — ainda não existe em `app.routes.ts`, cabe ao `po`/agente que
  for editar as rotas usar esse path exato.

## Build
`tsc --noEmit` e `ng build --configuration development` rodaram limpos
(único warning presente é preexistente, em `reunioes.ts`, não relacionado
a esta entrega).

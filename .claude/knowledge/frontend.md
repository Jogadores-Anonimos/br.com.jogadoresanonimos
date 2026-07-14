## [2026-07-13] Camada Angular de services do módulo Escritório atualizada para telefones + bairro (frontend-services)
- **O que foi feito/decidido**: `frontend-services` atualizou a camada de
  services Angular de `EscritorioServico` para refletir a 2ª revisão do
  contrato de API em `backend/docs/api/contrato-escritorios-regionais.md`
  (novos campos `telefones: [{ label?, telefone }]` e `bairro?: string`
  nos DTOs de request/response), replicando o padrão já existente de
  telefones de Grupo (`GrupoIntergrupo`/`GrupoIntergrupoTelefone`) para
  Escritório.
  - `frontend/src/app/services/escritorio-regional.model.ts`: nova
    interface `EscritorioTelefone` (`{ id, label: string|null, telefone
    }`); `EscritorioServico` ganhou `bairro: string | null` e `telefones:
    EscritorioTelefone[]`; nova interface `CriarEscritorioTelefoneRequest`
    (`{ label?, telefone }`); `CriarEscritorioServicoRequest` ganhou
    `bairro?: string` e `telefones: CriarEscritorioTelefoneRequest[]`.
  - `frontend/src/app/services/escritorio-regional.mock.ts`: seeds mock
    atualizados com `bairro` e `telefones`.
  - `frontend/src/app/services/escritorio-regional.ts`:
    `criarEscritorioServicoAdmin`/`editarEscritorioServicoAdmin` (branch
    mock) passaram a montar/atualizar `bairro` e `telefones`. Confirmado
    que o service já segue o padrão `environment.useMocks` para alternar
    entre HTTP real (`environment.apiBaseUrl`) e mock, igual ao resto do
    arquivo — nada de mock hardcoded substituindo a chamada real.
- **Trade-off ou motivo**: reaproveitar 1:1 o padrão de modelagem já usado
  para telefones de Grupo, em vez de criar uma abordagem nova só para
  Escritório.
- **Onde no código**: `frontend/src/app/services/escritorio-regional.model.ts`,
  `frontend/src/app/services/escritorio-regional.mock.ts`,
  `frontend/src/app/services/escritorio-regional.ts`.
- **Testes**: validado com `tsc --noEmit`, sem erros. Nenhum commit feito.
- **Ensinado por**: frontend-services

## [2026-07-13] Form de "Escritório de Serviço" (admin-escritorios-regionais) alinhado ao form de Grupo: CEP + telefones
- **O que foi feito/decidido**: no componente `admin-escritorios-regionais`
  (`frontend/src/app/pages/admin/admin-escritorios-regionais/`), o form da
  seção "Escritórios de Serviço" não tinha campo de CEP nem de telefones,
  diferente do form de Grupo (mesmo componente) que já tinha os dois. O
  backend já suportava `bairro` e `telefones` (lista) para
  `EscritorioServico` (contrato: `backend/docs/api/contrato-escritorios-regionais.md`,
  revisão 2026-07-13) e o `frontend-services` já tinha atualizado o model
  `escritorio-regional.model.ts` em paralelo, então o `frontend` seguiu
  direto sem esperar. Adicionado ao form de Escritório: campo CEP + botão
  "Buscar endereço" (viacep) e campo Bairro, replicando a UX já usada no
  form de Grupo; bloco de telefones (label opcional + telefone) também
  adicionado, reaproveitando estrutura visual e o helper
  `proximoIdTelefoneFormulario()` já existente — nenhuma classe CSS nova
  precisou ser criada.
- **Decisão de arquitetura pontual**: em vez de duplicar a chamada HTTP ao
  viacep entre os dois forms (Grupo e Escritório) dentro do mesmo
  componente, foi extraído um método privado compartilhado
  `buscarEnderecoPorCep(cep, numero, setBuscando, setErro, aoEncontrar)`
  na mesma classe do componente — sem criar service novo, por ser projeto
  de baixa escala (~2.500 req/dia).
- **Trade-off ou motivo**: **padrão a reaproveitar** — quando dois forms
  no mesmo componente precisam da mesma integração externa simples,
  extrair um método privado compartilhado em vez de duplicar lógica ou
  criar uma abstração/service nova.
- **Onde no código**: `frontend/src/app/pages/admin/admin-escritorios-regionais/admin-escritorios-regionais.ts`,
  `admin-escritorios-regionais.html`. Form de Grupo e dropdown de UF não
  foram tocados (restrição do usuário).
- **Ensinado por**: frontend

## [2026-07-09] Busca funcional na navbar (client-side, sem persistência, sem histórico por decisão de anonimato)

- **O que foi feito/decidido**: implementada busca funcional em tempo real
  dentro do `.search-panel` já existente da navbar (mesmo comportamento
  desktop/mobile). Novo arquivo
  `frontend/src/app/components/navbar/navbar-search-index.ts` com índice
  estático `NAVBAR_SEARCH_INDEX` (18 itens, interface `SearchIndexItem {
  titulo, rota, keywords[] }`) cobrindo as páginas de conteúdo do site,
  com sinônimos manuais em português. Busca é 100% client-side (`.filter()`
  simples sobre o array, sem lib nova), limitada a 8 resultados exibidos.
  Especificação veio do `ux-ui`, implementação de dois agentes `frontend`
  em paralelo.
- **Decisão de anonimato (não reverter sem revisitar)**: o termo de busca
  (`termoBusca`, propriedade do componente `Navbar`) vive só em memória do
  componente — nunca logado (sem console.log/analytics), nunca persistido
  (sem localStorage/sessionStorage/cookie) e nunca vai para a URL como
  query string (navegação via `router.navigateByUrl(item.rota)`, sem
  `queryParams`). `termoBusca` é resetado para `''` em todo caminho que
  fecha o painel: toggle do botão de busca, clique fora (`aoClicarFora`),
  tecla Esc (novo `@HostListener('document:keydown.escape')`), seleção de
  um resultado, e abertura do menu mobile.
  **Foi deliberadamente decidido não implementar "buscas recentes"/
  histórico de busca** — padrão comum de mercado, mas incompatível com o
  requisito de anonimato do projeto (CLAUDE.md: "o anonimato do usuário
  deve ser preservado em todas as camadas do sistema"). Ausência de
  histórico é decisão consciente, não funcionalidade faltante.
- **Itens excluídos do índice, também deliberado**: `minha-conta` (área
  autenticada/pessoal), `depoimentos` (é só redirect duplicado de
  `/relatos`, sem componente próprio) e a home (`/`).
- **Onde no código**: `frontend/src/app/components/navbar/navbar.ts`,
  `navbar.html`, `navbar.css`, novo arquivo
  `frontend/src/app/components/navbar/navbar-search-index.ts`.
- **Ensinado por**: frontend (dois agentes em paralelo), especificação de
  ux-ui.

## [2026-07-09] Reordenação de "Relatos" no dropdown/sidebar de Recursos (logo após "20 Perguntas")

- **O que foi feito/decidido**: em `frontend/src/app/components/navbar/navbar.html`
  (dropdown "Recursos") e `frontend/src/app/components/resource-layout/resource-layout.html`
  (sublista "Recursos" do menu lateral), o item "Relatos" foi movido para
  logo depois de "20 Perguntas", mantendo a ordem relativa dos demais
  itens. Alteração replicada nos dois arquivos na mesma tarefa (ver
  convenção já registrada acima sobre manter navbar e resource-layout
  sincronizados).
- **Trade-off ou motivo**: decisão de produto do `ux-ui` — o dropdown da
  navbar continua enxuto (10 itens), sem expandir para os itens
  exclusivos do menu lateral (Mural de Apoio, Reflexão do Dia, Jogo
  Compulsivo, Uma Nova Chance permanecem só na sidebar).
- **Onde no código**: `frontend/src/app/components/navbar/navbar.html`
  (dropdown "Recursos"), `frontend/src/app/components/resource-layout/resource-layout.html`
  (`resource-nav-sublist`).
- **Ensinado por**: frontend, especificação de ux-ui.

## [2026-07-09] Limpeza de comentários excessivos em 22 arquivos do frontend Angular

- **O que foi feito/decidido**: `frontend` limpou comentários excessivos
  em 22 arquivos de `frontend/src/app` (components, pages, services,
  config e rotas). Removidos: blocos longos/docstrings gigantes que
  narram histórico, contexto de decisão redundante ou descrevem o óbvio
  (ex: bloco de 20 linhas sobre proveniência de imagens, JSDoc de 13
  linhas explicando uma lista de prefixos, docstrings triviais tipo
  `/** Quantidade máxima de avisos... */` acima de constante
  autoexplicativa). Mantidos: comentários curtos de uma linha que
  explicam um "porquê" não-óbvio — decisão de negócio/segurança (ex: em
  `relatos.ts`, comentário dizendo que campos como e-mail/CPF/
  geolocalização foram deliberadamente excluídos por política de
  anonimato) ou workaround de bug (ex: em `minha-conta.ts`, comentário
  sobre evitar retry do reCAPTCHA contra DOM já removido). Nenhuma
  lógica foi alterada, nenhum commit foi feito.
- **Trade-off ou motivo**: reduzir ruído/verbosidade no código sem perder
  o "porquê" de decisões não-óbvias de negócio/segurança ou workarounds
  de bug — critério aplicado: cortar o que descreve o óbvio ou narra
  histórico, manter o que evita que outro agente reintroduza um bug ou
  quebre uma regra de anonimato por desconhecer o motivo original.
- **Onde no código**: `frontend/src/app/app.css`, `app.ts`,
  `components/barra-deslizante/barra-deslizante.css`,
  `components/navbar/navbar.css`,
  `components/reflexao-preview/reflexao-preview.css`,
  `components/reflexao-preview/reflexao-preview.ts`,
  `components/resource-layout/resource-layout.ts`,
  `components/voltar-ao-topo/voltar-ao-topo.css`,
  `components/voltar-ao-topo/voltar-ao-topo.ts`,
  `components/whatsapp-flutuante/whatsapp-flutuante.css`,
  `components/whatsapp-flutuante/whatsapp-flutuante.ts`,
  `config/meses.config.ts`, `config/whatsapp.config.ts`,
  `pages/avisos/avisos.ts`, `pages/home/home.css`, `pages/home/home.ts`,
  `pages/minha-conta/minha-conta.css`, `pages/minha-conta/minha-conta.ts`,
  `pages/reflexao-do-dia/reflexao-do-dia.css`,
  `pages/reflexao-do-dia/reflexao-do-dia.ts`,
  `pages/relatos/relatos.css`, `pages/relatos/relatos.ts`,
  `services/avisos.ts`, `services/mural-de-apoio.ts`,
  `services/reflexao-do-dia.ts`, `services/relatos.ts`.
- **Ensinado por**: frontend

## [2026-07-09] Página /reuniões: seção "cta-final" removida a pedido explícito do usuário (não é decisão de UX/produto do time)

- **O que foi feito/decidido**: o agente `frontend` removeu o bloco
  `<section class="cta-final">` inteiro de
  `frontend/src/app/pages/reunioes/reunioes.html`. A seção continha o
  título "Ainda não tem certeza se o jogo é um problema para você?", um
  parágrafo sobre o questionário 20 Perguntas / hotlines, e os links
  "Fazer as 20 Perguntas" (`/vinte-perguntas`) e "Linhas de Ajuda -
  Brasil" (`/hotlines`). Em `reunioes.css` foram removidas as regras
  específicas: `.cta-final`, `.cta-final h2`, `.cta-final p`,
  `.cta-final-acoes`, e a referência a `.cta-final` dentro do media query
  `@media (max-width: 768px)` (que agora só cuida de `.section`). As
  classes `.btn-cta`, `.btn-cta:hover`, `.btn-cta:focus-visible`,
  `.btn-cta--outline`, `.btn-cta--outline:hover` **não** foram removidas,
  pois continuam em uso pelo modal de detalhes da mesma página
  (`.detalhes-acoes .btn-cta`) e seu media query de 480px.
- **Trade-off ou motivo**: remoção foi pedido explícito do usuário, não
  decisão de UX/produto do time — **não reintroduzir essa seção por
  engano em trabalho futuro na página de reuniões**, não é bug a ser
  corrigido.
- **Onde no código**: `frontend/src/app/pages/reunioes/reunioes.html`
  (bloco `<section class="cta-final">` removido),
  `frontend/src/app/pages/reunioes/reunioes.css` (`.cta-final*` removidas).
- **Ensinado por**: frontend (via po)

## [2026-07-09] Página /reuniões: CTA diferenciado para reunião virtual (link direto de WhatsApp no card e no modal), sem mudar reserva de lugar presencial

- **O que foi feito/decidido**: em `frontend/src/app/pages/reunioes/reunioes.html`
  e `reunioes.css` (sem mudanças em `reunioes.ts`, a lógica existente já
  bastava). No card da lista, reuniões `virtual` agora mostram um link
  direto de WhatsApp (`btn-cta btn-whatsapp`, usando `whatsappLink(reuniao)`)
  ao lado de "Ver detalhes", dentro de um novo wrapper
  `.reuniao-card-acoes` (card de reunião `presencial` ficou igual, sem
  mudança). No modal de detalhes, para `categoria === 'virtual'` o botão
  "Reservar Lugar" foi trocado por um link "Solicitar link da reunião",
  que também usa `whatsappLink()` via `<a target="_blank" rel="noopener">`,
  sem tocar em `reservarLugarLocal()`/`reservaConfirmada` (esse fluxo
  continua exclusivo do `presencial`). CSS reusa 100% de classes/tokens já
  existentes do design system (`btn-cta`, `btn-whatsapp`,
  `btn-cta--outline`, `--spacing-*`); único CSS novo foi de layout
  (`.reuniao-card-acoes` e ajuste em `@media (max-width: 480px)` para
  empilhamento em telas pequenas).
- **Trade-off ou motivo**: reserva de lugar físico não faz sentido para
  reunião online; usuário reportou que o botão de WhatsApp só aparecia
  dentro do modal "Ver detalhes" e queria contato mais visível/direto no
  fluxo virtual.
- **Onde no código**: `frontend/src/app/pages/reunioes/reunioes.html`,
  `frontend/src/app/pages/reunioes/reunioes.css` (`.reuniao-card-acoes`).
  `reunioes.ts` não alterado.
- **Ensinado por**: frontend

## [2026-07-09] Página /reuniões: categorias reduzidas de 4 para 2 (só "presencial" e "virtual"), a pedido direto do usuário

- **O que foi feito/decidido**: em `frontend/src/app/pages/reunioes/reunioes.ts`,
  o tipo `CategoriaReuniao` foi reduzido de 4 valores para 2
  (`'presencial' | 'virtual'`), removendo os filtros/cards e as reuniões
  mockadas de "Nacional" e "Telefone". Foram acrescentadas 2 reuniões
  virtuais mock (total de 3 reuniões virtuais), para a lista dar suporte
  real ao fluxo de card "Ver detalhes" / botão de WhatsApp (antes havia
  poucas reuniões mockadas para sustentar esse fluxo com sentido).
  Em `reunioes.css`, o grid `.categorias-grid` passou de
  `repeat(4, 1fr)` para `repeat(2, 1fr)`, centralizado com
  `max-width: 720px`; a media query de 1300px que forçava a transição de
  4 para 2 colunas foi simplificada (regra redundante removida, mantido
  só o ajuste de padding/min-height dos cards).
  `reunioes.html` não precisou de nenhuma mudança — já iterava
  dinamicamente sobre `categorias` e `reunioesFiltradas`; o fluxo de
  clicar em "Virtual" (lista filtrada → modal de detalhes → botão
  WhatsApp) continua intacto, e não usa o bloco de mapa/filtros exclusivo
  de "presencial".
- **Trade-off ou motivo**: decisão de produto tomada diretamente pelo
  usuário (não passou por `ux-ui`, pois não é uma decisão de mercado/UX,
  é redução de escopo funcional das categorias de reunião).
- **Onde no código**: `frontend/src/app/pages/reunioes/reunioes.ts`
  (tipo `CategoriaReuniao`, mocks de reuniões virtuais),
  `frontend/src/app/pages/reunioes/reunioes.css` (`.categorias-grid` e
  media query de 1300px). `reunioes.html` não alterado. Nada commitado.
- **Ensinado por**: frontend

## [2026-07-09] Bug corrigido: botões flutuantes (WhatsApp e "voltar ao topo") encostados/sobrepostos por cálculo de largura errado

- **O que foi feito/decidido**: usuário reportou que o botão flutuante do
  WhatsApp estava "feio" e que o botão "voltar ao topo" estava colado nele,
  sem espaçamento. Causa raiz: `voltar-ao-topo.css` calculava seu `right`
  via `calc()` assumindo que o botão do WhatsApp
  (`whatsapp-flutuante.css`) tinha 48px de largura, mas na verdade ele tem
  56px — os dois círculos ficavam 8px mais próximos do que deveriam,
  encostando/sobrepondo levemente.
  Correção aplicada:
  1. `voltar-ao-topo.css`: `right: calc(...)` corrigido de `48px` para
     `56px` nos dois breakpoints (desktop e `max-width: 480px`),
     restaurando o gap real de `var(--spacing-xs)` (8px) entre os dois
     botões flutuantes.
  2. `whatsapp-flutuante.css`: `.whatsapp-fab__icon` aumentado de `30px`
     para `32px` (dentro do círculo de 56px), ajuste fino para aproximar a
     proporção ícone/círculo do padrão de mercado (~57% do diâmetro, vs
     ~54% antes) — SVG inline do path oficial do WhatsApp já estava
     correto (não era emoji nem ícone genérico), não houve troca de
     abordagem.
  3. Sombra (`--shadow-md`) e cor (`--color-whatsapp`, #25d366) do botão
     do WhatsApp já estavam adequadas, mantidas sem alteração.
- **Trade-off ou motivo**: nenhum — era um bug de cálculo (valor errado no
  `calc()`), não uma decisão de design nova.
- **Padrão a reforçar**: sempre que dois elementos `position: fixed`
  dependerem da largura um do outro para calcular `right`/`bottom` via
  `calc()`, o comentário no CSS deve deixar claro que esse valor está
  acoplado a outro arquivo/componente — e ao alterar a largura de um fab
  flutuante, é preciso lembrar de revisar cálculos de posicionamento de
  elementos vizinhos que dependem dele. Neste caso, `voltar-ao-topo.css`
  dependia de `whatsapp-flutuante.css`, mas o comentário original já
  tinha o valor errado (48px em vez de 56px), o que causou o bug.
- **Onde no código**:
  `frontend/src/app/components/voltar-ao-topo/voltar-ao-topo.css`
  (cálculo de `right`),
  `frontend/src/app/components/whatsapp-flutuante/whatsapp-flutuante.css`
  (`.whatsapp-fab__icon`). Nada commitado.
- **Ensinado por**: frontend

## [2026-07-09] Ajuste pontual na seção `.ajuda-imediata` da Home: remoção do subtexto

- **Contexto do pedido**: seção `.ajuda-imediata` (CTA de WhatsApp,
  ver entrada logo abaixo) já existia; ajuste pontual pedido pelo usuário
  logo em seguida. `po` delegou direto ao `frontend`, sem passar por
  `ux-ui`, por ser remoção de conteúdo já decidido pelo usuário (não uma
  decisão aberta de "o que deve existir na interface").
- **O que foi feito/decidido**:
  1. Em `frontend/src/app/pages/home/home.html` (seção `.ajuda-imediata`,
     linhas ~25-42), removido o parágrafo
     `<p class="ajuda-imediata__subtexto">` (descrição sobre conversa
     reservada e anônima pelo WhatsApp). Mantidos apenas o
     `<h2 class="ajuda-imediata__headline">Precisa falar com alguém
     agora?</h2>` e o botão `<a class="ajuda-imediata__botao">` (ícone
     SVG + texto "Falar no WhatsApp agora").
  2. Em `frontend/src/app/pages/home/home.css`, removidas as regras CSS
     órfãs de `.ajuda-imediata__subtexto` (regra base e a do media query
     `@media (max-width: 600px)`).
  3. `gap` de `.ajuda-imediata__conteudo` ajustado de
     `var(--spacing-sm)` (16px) para `var(--spacing-md)` (24px), para
     compensar visualmente a ausência do parágrafo — token já existente
     em `frontend/src/styles.css`, nenhum valor hardcoded/variável nova.
- **Trade-off ou motivo**: simplificação de copy pedida pelo usuário;
  ajuste de espaçamento reaproveitou token já existente em vez de criar
  novo.
- **Onde no código**: `frontend/src/app/pages/home/home.html` (seção
  `.ajuda-imediata`), `frontend/src/app/pages/home/home.css`
  (`.ajuda-imediata__conteudo`, remoção de `.ajuda-imediata__subtexto`).
  Nada commitado.
- **Ensinado por**: frontend (via aprendiz), delegado por po.

## [2026-07-09] Nova seção "ajuda-imediata" na Home: CTA grande de WhatsApp, distinto do botão flutuante

- **Contexto do pedido**: usuário pediu uma seção nova na Home com botão
  grande de destaque levando ao WhatsApp ("Precisa de ajuda?"), diferente
  do botão flutuante já existente (discreto, no canto da tela,
  `whatsapp-flutuante`). O `po` acionou primeiro o `ux-ui` para decidir
  posicionamento/copy/tratamento visual, depois delegou a implementação
  ao `frontend`.
- **O que foi decidido (ux-ui)**:
  - Nova seção `ajuda-imediata` posicionada logo APÓS `.hero` e ANTES de
    `.cta-primary` (não no final da página) — motivo: público pode estar
    em momento de crise, precisa da via de menor fricção (conversa 1:1
    via WhatsApp) visível na primeira dobra, antes da oferta que exige
    mais compromisso (`.cta-primary`, que fala de participar de reunião).
  - Copy definido: headline "Precisa falar com alguém agora?", subtexto
    reforçando anonimato e evitando linguagem alarmista ("não precisa
    esperar" em vez de "emergência"/"crise"), botão "Falar no WhatsApp
    agora".
  - Tratamento visual: destaque forte, distinto do `.cta-primary` (painel
    cinza) — especificamente pedido para EVITAR vermelho/tom de alerta
    (`--color-danger`/`--color-cta`), pois isso associaria "pedir ajuda"
    a emergência/erro, contrário ao tom de acolhimento do site.
- **O que foi implementado (frontend)**:
  - `frontend/src/app/pages/home/home.html`: nova `<section
    class="ajuda-imediata">` inserida entre `.hero` e `.cta-primary`.
    Link estático `<a [href]="whatsappChatUrl" target="_blank"
    rel="noopener noreferrer">` com `aria-label` explicando que abre em
    nova aba, reaproveitando o mesmo path de ícone SVG do WhatsApp já
    usado em `whatsapp-flutuante.html`.
  - `frontend/src/app/pages/home/home.ts`: importado `WHATSAPP_CHAT_URL`
    de `../../config/whatsapp.config` e exposto como `readonly
    whatsappChatUrl`, mesmo padrão já usado em `WhatsappFlutuante` —
    reforça o padrão do projeto de nunca duplicar a URL/telefone do
    WhatsApp, sempre reimportar da mesma config central.
  - `frontend/src/app/pages/home/home.css`: novo bloco BEM
    `.ajuda-imediata*`. Usou `--cor-padrao-ja` (vinho da marca) como
    fundo da seção (para diferenciar do painel cinza do `.cta-primary`
    logo abaixo) e **`--color-whatsapp` (verde da marca, #25d366) em vez
    de `--color-cta` (vermelho, #dc2626)** como cor do botão de ação —
    mesmo `--color-cta` sendo tecnicamente "a cor de ação padrão do
    site" (usada em `.btn--cta` do quiz-teaser), foi deliberadamente
    evitada aqui por ser vermelho e remeter a alerta/emergência, o que
    destoa do tom de acolhimento pedido para uma seção de "pedir ajuda".
    **Precedente estabelecido**: nem toda seção de CTA deve usar
    `--color-cta` por padrão — o contexto emocional do conteúdo pode
    justificar outro token de cor já existente (aqui, a cor de marca do
    canal em si, WhatsApp). Nenhum token novo foi criado.
    `:focus-visible` seguiu o padrão já existente (`--yellow-gold`).
    Responsivo com breakpoints em 600px/480px, CTA único por tela, botão
    full-width em mobile.
  - Build (`ng build --configuration development`) e `tsc --noEmit`
    validados sem erro pelo próprio agente `frontend` antes de reportar
    conclusão.
- **Trade-off ou motivo**: cor de marca do canal (verde WhatsApp) em vez
  do token de CTA padrão do site (vermelho), para não comunicar
  emergência/erro num contexto de acolhimento; posição logo após o hero,
  não no fim da página, para minimizar fricção de quem pode estar em
  crise.
- **Onde no código**: `frontend/src/app/pages/home/home.html` (seção
  `.ajuda-imediata`), `frontend/src/app/pages/home/home.ts`
  (`whatsappChatUrl`), `frontend/src/app/pages/home/home.css` (estilos
  `.ajuda-imediata*`). Nenhum outro arquivo tocado. Nada commitado.
- **Ensinado por**: ux-ui (especificação), frontend (implementação),
  coordenados por po.

## [2026-07-09] Bug corrigido: assets do "Mural de Apoio" salvos na pasta errada (`frontend/public/` em vez de `frontend/src/public/`)

- **O que foi feito/decidido**: O Angular deste projeto serve assets
  estáticos a partir de `frontend/src/public/`, configurado em
  `frontend/angular.json` (`"assets": [{ "glob": "**/*", "input":
  "src/public" }]`) — não de `frontend/public/` na raiz do projeto. As 4
  imagens reais do "Mural de Apoio" haviam sido salvas em
  `frontend/public/images/mural-de-apoio/` (pasta errada, faltando o
  `src/`), por isso nunca apareciam no site em runtime mesmo com o
  código/mock apontando corretamente para os caminhos. Corrigido movendo
  os 4 arquivos para `frontend/src/public/images/mural-de-apoio/` e
  removendo a pasta incorreta `frontend/public/`.
- **Trade-off ou motivo**: não é uma escolha de design, é a única pasta
  que o Angular CLI realmente copia para o build/serve, conforme
  `angular.json` — salvar fora dela produz um 404 silencioso de imagem,
  sem erro de compilação.
- **Convenção a reforçar**: qualquer novo asset estático de imagem deste
  projeto deve sempre ir para `frontend/src/public/images/<categoria>/`,
  nunca para `frontend/public/` (pasta na raiz, sem `src/`, não é servida
  pelo Angular neste projeto).
- **Onde no código**: `frontend/angular.json` (config de `assets`),
  `frontend/src/public/images/mural-de-apoio/` (destino correto).
- **Ensinado por**: frontend

## [2026-07-09] Bug corrigido: link "/avisos" ausente na sidebar de recursos (navbar e resource-layout não sincronizados)

- **O que foi feito/decidido**: Existem dois pontos de navegação
  distintos que listam as páginas de "Recursos": o dropdown em
  `frontend/src/app/components/navbar/navbar.html` e a lista
  `resource-nav-sublist` em
  `frontend/src/app/components/resource-layout/resource-layout.html`
  (sidebar mostrada dentro de cada página de recurso). Não há fonte única
  de verdade entre eles — são duas listas hardcoded que precisam ser
  atualizadas manualmente em conjunto. O link "/avisos" havia sido
  adicionado só na navbar e ficou esquecido na sidebar, causando
  inconsistência de navegação entre os dois pontos.
- **Trade-off ou motivo**: nenhuma refatoração para fonte única foi feita
  nesta correção — apenas replicado o link ausente na sidebar. Reforça um
  padrão de risco já conhecido no projeto (duplicação de itens entre
  navbar e `resource-layout`, ver também entradas anteriores sobre os 3
  novos itens de Recursos e a defasagem pré-existente identificada nesse
  mesmo dropdown).
- **Convenção a reforçar**: sempre que um link de recurso for
  adicionado/removido em `navbar.html` (dropdown "Recursos") ou em
  `resource-layout.html` (`resource-nav-sublist`), replicar a mudança no
  outro arquivo na mesma tarefa — não tratar como dois passos separados.
- **Onde no código**: `frontend/src/app/components/navbar/navbar.html`
  (dropdown "Recursos"), `frontend/src/app/components/resource-layout/resource-layout.html`
  (`resource-nav-sublist`).
- **Ensinado por**: frontend

## [2026-07-09] Investigação: alternância de fundo da Home "não aparece" — não é bug de código, é baixo contraste entre tokens

- **O que foi investigado**: usuário reportou que seções da Home
  apareciam com fundo branco onde deveria haver o cinza padrão do site,
  como se a alternância de fundo não existisse. `frontend` rodou
  `ng serve` e inspecionou com Playwright (não só leitura estática de
  CSS) para confirmar.
- **Resultado da investigação**: todas as seções de
  `frontend/src/app/pages/home/home.html` já usam o token correto e a
  alternância no código está certa: `.mural-teaser` →
  `var(--color-bg-surface)` (#ffffff), `<app-reflexao-preview>` →
  `var(--color-bg-section)` (#f8fafc), `.avisos-teaser` →
  `var(--color-bg-surface)` (#ffffff), `.relatos-teaser` →
  `var(--color-bg-section)` (#f8fafc). Não havia bug de implementação,
  nem na seção nova `.avisos-teaser` (suspeita inicial). O problema real:
  os dois tokens globais em `frontend/src/styles.css` —
  `--color-bg-surface` (#ffffff) e `--color-bg-section` (#f8fafc) — têm
  contraste baixo demais entre si (diferença de ~7 pontos por canal RGB),
  tornando a alternância quase imperceptível a olho nu mesmo estando
  implementada corretamente.
- **Nenhuma alteração de código foi feita.** Ficou como sugestão pendente
  de aprovação do usuário (via `po`): aumentar o contraste de
  `--color-bg-section` para algo como `#eef1f5` ou `#eceff2` — decisão de
  variável global, impacta todas as páginas que usam o token, não só a
  Home, por isso não pode ser aplicada sem validação do usuário.
- **Lição de processo**: ao investigar problema visual reportado pelo
  usuário, vale rodar o dev server e inspecionar visualmente/computado
  (ex: via Playwright, `getComputedStyle`) em vez de confiar só na
  leitura estática do CSS — o código pode estar "correto" e ainda assim o
  problema ser real (baixo contraste entre tokens), e isso só aparece
  observando de fato.
- **Onde no código**: `frontend/src/app/pages/home/home.html` (seções
  investigadas), `frontend/src/styles.css` (tokens `--color-bg-surface` e
  `--color-bg-section`, candidatos à mudança pendente de aprovação).
- **Ensinado por**: frontend

## [2026-07-09] Correção de contraste: Reflexão do Dia usava vinho da marca como fundo sólido (único caso no site)

- **O que foi feito/decidido**: A página `/reflexao-do-dia` e o card de
  preview dela na Home (`reflexao-preview`) usavam `--cor-padrao-ja`
  (vinho da marca) como **fundo sólido** de seção/card inteiro — único
  lugar do site com esse uso; todas as outras páginas usam
  `--cor-padrao-ja` só como acento (texto de título, borda, badge), nunca
  como fundo de página/card (confirmado em `avisos.css`/`literatura.css`).
  O `po` acionou o `ux-ui` primeiro (a página tinha grades de botões de
  mês/dia com contraste pensado para fundo escuro) para definir os novos
  estados de contraste antes do `frontend` implementar.
  1. **Fundo vinho sólido removido** em dois arquivos:
     - `reflexao-do-dia.css` (`.reflexao`): fundo de `var(--cor-padrao-ja)`
       para `var(--color-bg-section)`, texto de `var(--white)` para
       `var(--color-text)`.
     - `reflexao-preview.css` (`.reflexao-preview__card`): fundo para
       `var(--color-bg-surface)` (a section externa `.reflexao-preview`
       já usava `--color-bg-section`), mesmo ajuste de texto.
     - Título (`.reflexao__titulo`, `.reflexao-preview__titulo`) virou
       acento vinho (`color: var(--cor-padrao-ja)`), igual ao padrão de
       `.aviso-titulo`/`.literatura-card h2`.
     - Textos de apoio trocaram de tokens pensados para fundo escuro
       (`--color-nav-text-muted`) para o token neutro de fundo claro
       (`--color-text-muted`).
     - Estado de erro do preview trocou de `--yellow` (contraste para
       fundo escuro) para `--color-danger` (padrão de erro do resto do
       site).
     - `.reflexao__mes-destaque` trocou de `--yellow-gold` para
       `--cor-padrao-ja` — dourado sobre o novo fundo quase-branco não
       tinha contraste suficiente para texto.
     - `:focus-visible` do CTA do preview (`--white` antes) também
       precisou trocar para `--cor-padrao-ja` — mesmo problema de
       contraste, corrigido por extensão lógica ainda que não estivesse
       listado explicitamente na tarefa original.
  2. **Grade de botões da página completa
     (`.reflexao__botao-mes`/`.reflexao__botao-dia`), especificada pelo
     `ux-ui` estado a estado**:
     - `.reflexao__botao-mes`: ganhou borda nova
       `1px solid var(--color-border)` no estado normal e hover (sem
       isso, botão branco sobre fundo agora claro fica sem nenhuma
       separação visual); hover mudou fundo para `--color-bg-frame`;
       foco mudou outline de `--yellow-gold` para `--cor-padrao-ja`.
     - `.reflexao__botao-dia--ativo`: **inverteu o esquema de cor** —
       antes fundo branco/texto vinho, agora fundo `--cor-padrao-ja`
       sólido/texto `--white`. Motivo: um "ativo" branco sobre fundo
       agora claro deixaria de funcionar como indicador de seleção
       (ficaria igual ao fundo da seção); o vinho sólido virou o ponto de
       maior contraste da grade. Foco de `.reflexao__botao-dia` também
       mudou de `--white` para `--cor-padrao-ja`.
  3. **Data de hoje adicionada ao card de preview da Home**: extraída a
     constante `NOMES_MESES` (antes só existia dentro de
     `reflexao-do-dia.ts`) para novo arquivo compartilhado
     `frontend/src/app/config/meses.config.ts`, seguindo o padrão de
     pasta `config/` já usado no projeto (`whatsapp.config.ts`).
     `reflexao-do-dia.ts` refatorado para importar dali (sem duplicar
     array). Novo getter `dataFormatada` em `reflexao-preview.ts`
     (retorna `''` se `reflexao` for `null`), formata como "{dia} de
     {mês}" reaproveitando `NOMES_MESES`. Exibido no template dentro do
     `@else if (reflexao)` já existente, com nova classe
     `.reflexao-preview__data` (mesmo estilo de `.reflexao__data` da
     página completa).
- **Trade-off ou motivo**: Nenhuma variável nova foi criada no design
  system — todas as trocas usaram tokens já existentes em
  `frontend/src/styles.css` (`--color-bg-section`, `--color-bg-surface`,
  `--color-text`, `--color-text-muted`, `--color-border`,
  `--color-bg-frame`, `--color-danger`, `--cor-padrao-ja`, `--white`,
  `--yellow-gold`, `--yellow`).
  **Padrão/lição a reter para o time**: ao migrar um componente de fundo
  escuro para fundo claro (ou vice-versa), toda cor de texto/outline de
  foco pensada para o fundo antigo precisa ser reavaliada
  individualmente, estado a estado (normal/hover/ativo/foco) — não é uma
  troca mecânica de uma única variável de fundo. Um elemento
  (`.reflexao__botao-dia--ativo`) até trocou completamente qual token era
  fundo e qual era texto, não só qual variável usar.
  **Novo padrão de estrutura**: `frontend/src/app/config/meses.config.ts`
  estabelece um local para constantes compartilhadas entre uma página
  completa e o componente de preview dela na Home, evitando duplicar
  arrays de nomenclatura — aplicável a outras duplas página/preview que
  possam surgir no projeto.
  Build (`ng build`) validado sem erros pelo próprio agente `frontend`
  antes de reportar conclusão.
- **Onde no código**: `frontend/src/app/pages/reflexao-do-dia/reflexao-do-dia.css`,
  `frontend/src/app/pages/reflexao-do-dia/reflexao-do-dia.ts`,
  `frontend/src/app/components/reflexao-preview/reflexao-preview.css`,
  `frontend/src/app/components/reflexao-preview/reflexao-preview.ts`,
  `frontend/src/app/components/reflexao-preview/reflexao-preview.html`,
  `frontend/src/app/config/meses.config.ts` (novo arquivo).
- **Não confundir com pendência separada já registrada**: a entrada
  `.claude/knowledge/service-reflexao-do-dia.md` já documentava dois
  débitos LOW de acessibilidade no `reflexao-preview` (padding hardcoded
  em vez de `var(--spacing-*)`, e ausência de `aria-live` no preview da
  Home) apontados pelo `code-reviewer` na entrega original da feature.
  Esta tarefa **não tocou** nesses dois pontos — continuam pendentes,
  como débito à parte do que foi corrigido aqui (contraste de cor, não
  acessibilidade estrutural).
- **Ensinado por**: ux-ui (especificação de contraste), frontend
  (implementação), coordenados por po.

## [2026-07-09] Correção de layout: Mural de Apoio tratava banner-flyer como capa de post de blog

- **O que foi feito/decidido**: A entrega original da feature "Mural de
  Apoio" (ver entrada mais abaixo, "[2026-07-09] Feature 'Mural de Apoio'
  (banners institucionais)") tratou cada banner como card de blog: imagem
  `aspect-ratio: 16/9` + legenda de texto abaixo (`banner.legenda`), com
  parágrafo introdutório longo no topo da página. Quando o
  `frontend-services` trocou o mock fictício por 4 cartazes REAIS
  baixados de https://jogadoresanonimos.com.br/ (arquivos em
  `frontend/public/images/mural-de-apoio/`), ficou claro que o formato
  real é pôster/flyer vertical, autoexplicativo (o próprio cartaz já
  contém nome do grupo, endereço, dia/horário e telefone) — o layout de
  "capa de blog" cortava informação crítica do cartaz com
  `object-fit: cover` em 16:9 e duplicava informação com uma legenda
  visível desnecessária.
  Correção (spec fechada pelo `ux-ui`), aplicada em ambas as superfícies
  (página `/mural-de-apoio` e teaser da Home):
  1. Removido o bloco `@if (banner.legenda) { <p class="...legenda">...
     </p> }` dos dois templates (`mural-de-apoio.html` e `home.html`,
     seção `.mural-teaser`) — cartão agora é só a imagem, sem
     card-com-padding-e-texto embaixo.
  2. `aspect-ratio` do wrapper da imagem trocado de `16/9` para **`9/16`**
     em `.mural-card__imagem-wrap` (mural-de-apoio.css) e
     `.mural-teaser__imagem-wrap` (home.css). Proporção `9/16` (não
     `3/4`, cogitado inicialmente pelo ux-ui antes de ver os arquivos
     reais) escolhida por medição real dos 4 arquivos: 2 imagens são
     exatamente 576x1024 (=0.5625=9/16 exato), as outras 2 são 641x1024
     (=0.626, mais próximo de 9/16 do que de 3/4=0.75) — `9/16` minimiza
     o corte de `object-fit: cover` no topo/rodapé do cartaz (onde fica
     o texto), aceitando um corte pequeno nas laterais das duas imagens
     641x1024.
  3. Grid trocado de `repeat(auto-fit, minmax(240px, 1fr))` para
     `repeat(auto-fill, minmax(220px, 1fr))` **combinado com
     `max-width: 320px; margin: 0 auto;` no próprio card** (não só no
     grid) — necessário porque um pôster vertical em coluna larga de
     desktop (`auto-fit` sozinho) viraria involuntariamente uma "hero
     image" gigante; o `max-width` no item, não no grid, é o que
     efetivamente trava esse crescimento.
  4. Removido o `<p class="lead">` (parágrafo introdutório longo) do
     topo de `mural-de-apoio.html`, e a classe `.lead` correspondente do
     CSS (ficaria código morto sem uso). O `<h1>` já vem do
     `resource-layout` (`titulo="Mural de Apoio"`), não precisa de
     subtítulo.
  5. Mantido sem alteração: clicabilidade condicional
     (`@if (banner.linkDestino)` vira `<a>`, senão `<div>` — hoje todos
     os 4 banners reais renderizam como `<div>` não clicável, nenhum
     `linkDestino` foi inventado), `[alt]="banner.altText"` (contém a
     transcrição completa do cartaz — única fonte textual/acessível que
     resta), estados de carregando/erro/vazio, e a seção `cta-final` da
     página (`/mural-de-apoio`, é uma seção separada, não é a legenda do
     banner).
- **Trade-off ou motivo**: `9/16` em vez de `3/4` é o exemplo concreto de
  "meça o material real antes de aplicar a proporção pedida na spec" —
  a spec do ux-ui já veio com a ressalva de usar julgamento comparando
  com os arquivos reais, e a medição (via PIL/Python, `Image.open(...).size`)
  desempatou a favor de `9/16`. Nenhum token novo de cor/espaçamento foi
  necessário — reaproveitados `--spacing-lg`, `--radius-lg`, `--shadow-sm`/
  `--shadow-md`, `--color-border`, `--color-bg-section`, já usados antes
  no mesmo componente.
- **Onde no código**: `frontend/src/app/pages/mural-de-apoio/mural-de-apoio.html`,
  `frontend/src/app/pages/mural-de-apoio/mural-de-apoio.css`,
  `frontend/src/app/pages/home/home.html` (seção `.mural-teaser`),
  `frontend/src/app/pages/home/home.css` (classes `.mural-teaser__*`).
  Verificado com `npx tsc --noEmit` e `npx ng build --configuration
  development`, ambos sem erro.
- **Ensinado por**: frontend

## [2026-07-09] Home ganhou seção `.avisos-teaser` — CONTRADIZ decisão de UX anterior registrada neste mesmo arquivo

- **ALERTA para o `po`/`ux-ui`**: esta entrada documenta uma tarefa
  executada a pedido explícito e detalhado do usuário/po, mas que entra
  em conflito direto com a decisão já registrada mais abaixo neste
  arquivo ("[2026-07-09] Decisão de UX: página /avisos e por que a Home
  NÃO ganha seção própria de avisos"): lá ficou fechado que a Home **não
  deveria** ganhar seção de avisos, por ser redundante com a barra
  deslizante (`BarraDeslizante`, integrada globalmente em
  `app.html`/`app.ts`, visível em toda página exceto `/minha-conta`).
  Esta tarefa pediu exatamente o oposto. Não coube ao `frontend`
  questionar/bloquear a execução (instrução veio explícita, com
  requisitos funcionais e de design detalhados), mas fica sinalizado
  aqui para o `po`/`ux-ui` decidirem conscientemente: (a) revogar a
  decisão antiga por escrito, (b) reverter esta seção nova, ou (c)
  conviver com as duas coisas (barra deslizante + teaser na Home) sabendo
  que official-decision-wise elas foram pensadas como mutuamente
  exclusivas.
- **O que foi feito**: nova seção `.avisos-teaser` em `frontend/src/app/pages/home/home.html`,
  posicionada entre `<app-reflexao-preview>` e `.newsletter-teaser`.
  Consome `AvisosService.listarAvisos()` (sem alterar contrato público),
  fatiando os 3 primeiros itens da lista já ordenada pelo service
  (fixado primeiro, depois mais recente) — `AVISOS_TEASER_QUANTIDADE = 3`
  em `home.ts`. Estados `carregandoAvisos`/`erroAvisos`/lista vazia
  seguem exatamente o mesmo padrão de nomenclatura e estrutura condicional
  já usado por `carregandoMural`/`erroMural`/`bannersMural.length > 0` no
  mesmo componente. Header com título "Avisos" + link "Ver todos"
  (`routerLink="/avisos"`), replicando o header do `.mural-teaser`. Avisos
  com `fixado: true` recebem badge "Importante" + borda mais grossa,
  usando o token `--color-warning` (já existente em `styles.css`, nunca
  usado antes no projeto) — escolhido em vez de `--color-danger` porque
  esse último já é usado como `--color-cta` (cor de botões de ação, não
  de alerta) e "warning/aviso importante" é semanticamente mais preciso
  que "danger/perigo" para o caso de uso (evita comunicar erro/risco onde
  o correto é "atenção/relevante").
- **Trade-off ou motivo**: nenhum token novo precisou ser criado
  (`--color-warning` já existia, sem uso prévio) — reforça o padrão do
  projeto de checar tokens adjacentes antes de propor um novo. Nenhuma
  cor/fonte/espaçamento hardcoded.
- **Onde no código**: `frontend/src/app/pages/home/home.ts` (novo estado
  `avisos`/`carregandoAvisos`/`erroAvisos`, injeção de `AvisosService`),
  `frontend/src/app/pages/home/home.html` (seção `.avisos-teaser`),
  `frontend/src/app/pages/home/home.css` (estilos `.avisos-teaser*`,
  posicionados antes de `.newsletter-teaser`). `AvisosService` e a página
  `/avisos` não foram alterados.
- **Edição concorrente detectada durante esta tarefa (não é trabalho
  meu, apenas registro)**: enquanto esta tarefa estava em andamento, outra
  instância de `frontend` editou os mesmos três arquivos em paralelo,
  trocando a antiga seção `.newsletter-teaser` (que ainda linkava
  "Boletim Informativo") por uma nova `.relatos-teaser`, resolvendo a
  pendência antiga já registrada acima ("Home: link do card
  newsletter-teaser corrigido para /relatos" e "Depoimentos renomeado
  para Relatos"). O merge de `home.ts` ocorreu sem conflito (blocos de
  estado `avisos*` e `relatos*` coexistem, cada um com seu próprio
  `constructor`/`ngOnInit`). **Pendência aberta encontrada, fora do
  escopo desta tarefa**: em `home.css`, a seção `.newsletter-teaser*`
  ainda existe no arquivo mas ficou órfã (nada no HTML usa mais essas
  classes); e a nova seção `.relatos-teaser*` usada em `home.html` ainda
  **não tem CSS correspondente** em `home.css` (renderiza sem estilo).
  Não foi corrigido aqui para não sobrescrever trabalho incompleto de
  outra instância concorrente — sinalizado para o `po` acompanhar se
  essa outra tarefa foi finalizada corretamente.
- **Ensinado por**: frontend

## [2026-07-09] Correção pontual: barra deslizante (borda amarela, sticky no scroll, ocultação por rota)

- **O que foi feito/decidido**: A pedido do usuário (`po` delegou direto ao
  `frontend`, sem passar por `ux-ui` por ser correção pontual de
  CSS/comportamento, não decisão de interface): três problemas na barra
  deslizante corrigidos na mesma tarefa.
  1. **Faixa decorativa `.ticker-border` removida** (6px/4px,
     `var(--yellow-gold)`, topo e base do ticker) — era a origem do
     "padding amarelo" indevido relatado pelo usuário. O `@Input()
     borderColor` em `barra-deslizante.ts` foi removido junto (ficaria
     código morto sem a faixa que o consumia).
  2. **Sticky centralizado num wrapper comum**: antes, `<app-navbar>` e
     `<app-barra-deslizante>` eram irmãos soltos em `app.html`, e só a
     navbar tinha `position: sticky` no próprio `:host` (`navbar.css`) —
     por isso a barra sumia ao rolar, sem acompanhar o header. Agora
     existe `<header class="app-header-sticky">` em `app.html` envolvendo
     os dois; `position: sticky; top: 0; z-index: 1000` mora nesse
     wrapper (`app.css`), e o `:host` de `navbar.css` mudou de `position:
     sticky` para `position: relative` (necessário para o `.search-panel`
     interno, que é `position: absolute`, continuar ancorado).
     **Importante para trabalho futuro isolado na navbar**: a navbar
     sozinha não é mais sticky por si só — depende do wrapper
     `.app-header-sticky` em `app.html`. Remover/alterar esse wrapper sem
     saber disso faz a navbar perder o sticky silenciosamente.
  3. **Nova custom property `--ticker-height`** em `app.css` (no wrapper
     `.app-header-sticky`, ativada condicionalmente via classe
     `.com-barra-deslizante` espelhando o signal
     `mostrarBarraDeslizante()`), valores `39px` (desktop) / `26px`
     (`@media max-width: 900px`). Consumida por `navbar.css` para somar à
     altura do header no cálculo de `top`/`max-height` do menu mobile
     (`.nav-menu`, `position: fixed`). Valor derivado manualmente de
     font-size + line-height + padding do `.ticker-item` em
     `barra-deslizante.css` — **não é calculado automaticamente**; se o
     padding/font-size do ticker mudar, `--ticker-height` precisa ser
     atualizado manualmente junto (mesmo padrão de manutenção manual já
     usado para `--nav-height`/`--utility-bar-height` no projeto). Dívida
     técnica pequena, aceita conscientemente e documentada em comentário
     no código.
  4. **Ocultação por rota generalizada** em `app.ts`: de checar só
     `startsWith('/minha-conta')` para um array
     `PREFIXOS_SEM_BARRA_DESLIZANTE = ['/minha-conta', '/login',
     '/dashboard']` verificado com `.some(...)`. Estado do projeto em
     2026-07-09: só `/minha-conta` existe de fato em `app.routes.ts` —
     `/login` e `/dashboard` ainda não existem como rotas; os prefixos
     ficaram preparados no código mas inertes até essas rotas serem
     criadas. Qualquer agente que crie essas rotas no futuro não precisa
     tocar em `app.ts` de novo — a ocultação já está coberta.
- **Trade-off ou motivo**: centralizar o sticky num wrapper comum em vez
  de duplicar `position: sticky` em cada componente evita que os dois
  elementos "descolem" um do outro no scroll; generalizar a ocultação por
  array (em vez de mais um `startsWith` isolado) evita reabrir `app.ts`
  a cada nova área de conta/login criada.
- **Onde no código**: `frontend/src/app/app.html`, `frontend/src/app/app.css`,
  `frontend/src/app/app.ts`,
  `frontend/src/app/components/barra-deslizante/` (`.html`, `.css`, `.ts`),
  `frontend/src/app/components/navbar/navbar.css`.
- **Ensinado por**: frontend

## [2026-07-09] Navbar: item "Recursos" tornado clicável (link + toggle separados)

- **O que foi feito/decidido**: No dropdown "Recursos" da navbar
  (`frontend/src/app/components/navbar/navbar.html`), o item era um único
  `<button class="dropdown-toggle">` que só abria/fechava o submenu, sem
  navegar para lugar nenhum. A pedido do usuário, passou a ser também
  clicável, levando direto para `/reunioes` (Encontrar uma Reunião),
  replicando o padrão já existente no item "Sobre os Jogadores Anônimos".
  Separado em dois elementos irmãos dentro do mesmo `<li class="dropdown">`:
  1. `<a class="dropdown-label" routerLink="/reunioes" routerLinkActive="active">Recursos</a>`
     — label clicável, é o link real.
  2. `<button class="dropdown-toggle dropdown-toggle--icon" type="button"
     (click)="toggleDropdown('recursos', $event)"
     [attr.aria-expanded]="dropdownsAbertos.recursos"
     aria-label="Abrir submenu Recursos">` — contém só o SVG do chevron,
     controla exclusivamente a abertura/fechamento do dropdown.
  Nenhuma mudança em `.ts` (chaves `recursos` em `dropdownsAbertos` /
  `abrirDropdown` / `fecharDropdown` / `toggleDropdown` preservadas) nem
  em `.css` — os seletores `.dropdown-label` e `.dropdown-toggle--icon`
  em `navbar.css` já eram genéricos (não escopados ao item "Sobre"),
  cobrindo o novo elemento em desktop e mobile sem exigir token novo.
- **Trade-off ou motivo**: `routerLink` e `(click)="toggleDropdown(...)"`
  não podem ficar no mesmo elemento — isso impede a navegação e/ou
  dispara o toggle acidentalmente ao clicar no texto do link. Separar em
  dois elementos irmãos resolve sem duplicar lógica de estado.
- **Onde no código**: `frontend/src/app/components/navbar/navbar.html`
  (item "Recursos" dentro do `<li class="dropdown">`).
- **Convenção estabelecida do componente navbar** (2º item a seguir esse
  padrão, após "Sobre os Jogadores Anônimos" — já pode ser tratado como
  padrão do componente, não caso isolado): para itens de navbar que
  precisam ser simultaneamente um link direto E um trigger de dropdown,
  usar dois elementos irmãos dentro do `<li class="dropdown">`: um
  `<a class="dropdown-label" routerLink="...">` (o link real) e um
  `<button class="dropdown-toggle dropdown-toggle--icon">` separado (só
  ícone chevron, controla só o toggle). Nunca colocar `routerLink` e
  `(click)="toggleDropdown(...)"` no mesmo elemento.
- **Ensinado por**: frontend

## [2026-07-09] Home: link do card "newsletter-teaser" corrigido para /relatos

- **O que foi feito**: resolve a pendência registrada na entrada abaixo
  ("Depoimentos renomeado para Relatos; Boletim Informativo removido").
  No card `newsletter-teaser` da Home (`frontend/src/app/pages/home/home.html`),
  o `routerLink` do CTA foi trocado de `/boletim-informativo` (rota já
  removida do projeto) para `/relatos`. Confirmado via grep que não
  sobra nenhuma referência a `/boletim-informativo` no arquivo.
- **O que NÃO foi alterado**: nenhum texto/copy do card — kicker "Fique
  conectado com a gente", título "Boletim Informativo", corpo do texto e
  o texto do próprio link ("Ler o Boletim Informativo") permanecem
  exatamente como estavam. Só o destino do link mudou; o card ainda se
  chama/fala "Boletim Informativo" no copy, mesmo apontando para
  `/relatos` — decisão de copy (se o card deve ser reescrito para falar
  de "Relatos" em vez de "Boletim Informativo") não fez parte desta
  tarefa e continua em aberto.
- **Trade-off ou motivo**: link quebrado era o problema mais urgente
  (usuário clicando em CTA da Home caía em rota inexistente); trocar só
  o destino é a correção mínima e reversível — não decide sozinho a
  questão maior de copy/redesenho da seção, que ainda depende da decisão
  pendente da "barra deslizante" em `app.html` mencionada na entrada
  anterior.
- **Onde no código**: `frontend/src/app/pages/home/home.html` (linha do
  `routerLink="/relatos"` dentro de `.newsletter-teaser__cta`).
- **Pendência ainda aberta**: decidir se o card deve ser reescrito
  (copy/título) para não falar mais de "Boletim Informativo" agora que
  aponta para "Relatos", ou se fica assim até uma revisão maior da Home.
- **Ensinado por**: frontend.

## [2026-07-09] Depoimentos renomeado para Relatos; Boletim Informativo removido

- **O que foi feito/decidido**: A pedido explícito do usuário, com
  decisão de UX já fechada pelo `ux-ui`:
  - Página e service `Depoimentos` renomeados por completo para
    `Relatos` — não só a pasta/arquivo, mas também classe do componente
    (`Depoimentos` -> `Relatos`, seletor `app-relatos`), classe do
    service (`DepoimentosService` -> `RelatosService`), interfaces
    (`Depoimento`->`Relato`, `EnviarDepoimentoRequest`->
    `EnviarRelatoRequest`, `DepoimentosResponse`->`RelatosResponse`, etc.)
    e nomes de método (`listarDepoimentosAprovados`->
    `listarRelatosAprovados`, `enviarDepoimento`->`enviarRelato`).
    Decisão tomada: alinhar nomenclatura 100% (não manter nomes antigos
    de método com arquivo novo) para não deixar divergência entre
    service e página. Classes CSS (`.depoimento-card` etc.) também
    renomeadas para `.relato-card` etc.
  - Rota principal passou de `/depoimentos` para `/relatos`
    (`frontend/src/app/app.routes.ts`), com redirect preservado:
    `{ path: 'depoimentos', redirectTo: 'relatos', pathMatch: 'full' }`
    — motivo: usuários em recuperação podem ter links antigos salvos,
    não pode quebrar.
  - Página "Boletim Informativo" removida por completo (pasta
    `frontend/src/app/pages/boletim-informativo/` deletada, rota e
    import removidos de `app.routes.ts`, sem redirect — decisão do
    `ux-ui` foi remoção direta, sem preservar nada, mesmo padrão já
    aplicado à remoção da página "Gam-Anon" anteriormente no projeto).
  - Itens de menu atualizados em `navbar.html` e
    `resource-layout.html`: onde antes havia dois itens separados
    ("Depoimentos" e "Boletim Informativo"), agora existe só "Relatos",
    na mesma posição do antigo "Depoimentos" (entre "Programa de
    Recuperação" e "Escritórios Regionais" — o item "Boletim
    Informativo" ficava logo depois de "Escritórios Regionais").
  - **Achado não previsto na tarefa original, corrigido por
    consequência lógica do pedido**: `frontend/src/app/components/footer/footer.html`
    também tinha um link para `/boletim-informativo` (terceiro ponto de
    menu, não mencionado explicitamente na tarefa) — like ficaria
    quebrado após a remoção da rota, então foi trocado para `/relatos`
    (mesmo padrão dos outros dois menus).
  - **Fora de escopo, propositalmente não tocado**: `frontend/src/app/pages/home/home.html`
    tem uma seção inteira ("newsletter-teaser") promovendo o "Boletim
    Informativo" com link para a rota removida — deixado como está, a
    pedido explícito do usuário, por depender de confirmação direta dele
    (ligado à decisão pendente da "barra deslizante" em `app.html`).
    Ou seja: esse link ficará quebrado até uma tarefa futura tratar essa
    seção da Home.
- **Trade-off/motivo**: redirect de `/depoimentos` para `/relatos`
  prioriza não quebrar links já compartilhados por usuários vulneráveis;
  remoção direta (sem redirect) do Boletim Informativo se justifica por
  não ter conteúdo real publicado ainda (Arquivo de Edições vazio,
  exemplos de "Relatos de Membros" no Boletim eram fictícios/ilustrativos
  e a nova página Relatos já resolve essa necessidade de verdade).
- **Onde no código**: `frontend/src/app/services/relatos.ts`,
  `frontend/src/app/pages/relatos/` (`.ts`/`.html`/`.css`),
  `frontend/src/app/app.routes.ts`, `frontend/src/app/components/navbar/navbar.html`,
  `frontend/src/app/components/resource-layout/resource-layout.html`,
  `frontend/src/app/components/footer/footer.html`. Pasta
  `frontend/src/app/pages/boletim-informativo/` e arquivo
  `frontend/src/app/services/depoimentos.ts` foram deletados.
- **Pendência conhecida**: `home.html` ainda linka para
  `/boletim-informativo` (rota removida) — precisa de uma tarefa futura,
  confirmada pelo usuário, para tratar essa seção da Home junto com a
  decisão da barra deslizante em `app.html`.
- **Ensinado por**: frontend (via aprendiz).

## [2026-07-09] Botões flutuantes: acessibilidade (aria/inert) e confirmação do número institucional

- **O que foi feito/decidido**: Complemento à entrega abaixo (mesma
  feature, verificado em disco após outra instância de `frontend` ter
  batido no limite de sessão antes de reportar). Confirma que os dois
  componentes (`whatsapp-flutuante`, `voltar-ao-topo`) estão de fato
  registrados em `frontend/src/app/app.ts` (array `imports`) e
  `frontend/src/app/app.html` (logo após `<app-footer>`), sem alterar
  `BarraDeslizante`, navbar, footer ou `router-outlet` já existentes.
  Detalhes de acessibilidade do popup do WhatsApp, não cobertos na
  entrada original: `aria-label` dinâmico no botão (troca entre
  "abrir"/"fechar" o chat), `aria-expanded` e `aria-controls` apontando
  para o popup, e `inert`/`aria-hidden` aplicado ao popup quando fechado
  (além do fechar por Esc via `HostListener('document:keydown.escape')`,
  já registrado abaixo). `WHATSAPP_PHONE_NUMBER = '5521994721933'`
  (constante em `whatsapp.config.ts`) foi confirmado pelo usuário como
  número institucional real e definitivo — não é dado pessoal de
  usuário, portanto sem conflito com a regra de anonimato do projeto.
  Cor do popup usa o token já existente `--color-bg-surface` (linha ~21
  de `styles.css`), reaproveitado sem criar variável nova.
- **Trade-off ou motivo**: `inert` + `aria-hidden` no popup fechado (em
  vez de só `display:none` via classe condicional) para impedir foco de
  teclado/leitor de tela em conteúdo invisível — mesmo racional de
  outros overlays não-modais do projeto.
- **Onde no código**: `frontend/src/app/components/whatsapp-flutuante/`
  (`.ts`, `.html`, `.css`), `frontend/src/app/config/whatsapp.config.ts`,
  `frontend/src/app/app.ts`, `frontend/src/app/app.html`.
- **Pendência conhecida**: o popup é uma "casca" fiel ao visual/
  comportamento do Join.chat, mas ainda não é a integração real do
  serviço de terceiro (sem script de embed oficial nem conta conectada)
  — TODO explícito no topo do `.ts` e em `whatsapp.config.ts` para quando
  a conta real da organização for conectada.
- **Ensinado por**: frontend (via aprendiz, consolidando trabalho de
  instância anterior que não pôde reportar).

## [2026-07-09] Botões flutuantes: WhatsApp/Join.chat e "voltar ao topo"

- **O que foi feito/decidido**: Criados dois componentes standalone novos
  em `frontend/src/app/components/`:
  - `whatsapp-flutuante/` (seletor `app-whatsapp-flutuante`): círculo
    verde fixed no canto inferior direito, com ícone SVG inline do
    WhatsApp (fill sólido, `viewBox 0 0 24 24`, seguindo o padrão de SVG
    já usado na navbar). Ao clicar, abre um popup não-bloqueante (sem
    overlay/modal) com header "powered by Join.chat", saudação "Olá" (só
    isso, sem mais texto) e botão "Abrir bate-papo" que chama
    `window.open` para a URL do WhatsApp em nova aba. Fecha com Esc
    (`HostListener` em `document:keydown.escape`).
  - `voltar-ao-topo/` (seletor `app-voltar-ao-topo`): círculo cinza
    sólido com chevron para cima, fixed ao lado esquerdo do botão de
    WhatsApp (mesmo `bottom`, `right` deslocado por `48px + gap`). Só
    aparece após 300px de scroll; usa `fromEvent(window, 'scroll',
    {passive: true})` + `auditTime(100ms)` do RxJS em vez de recalcular
    a cada evento de scroll (throttle para não sobrecarregar a main
    thread). Clique faz `window.scrollTo({top:0, behavior:'smooth'})`.

  URL do WhatsApp centralizada em `frontend/src/app/config/whatsapp.config.ts`
  (constante `WHATSAPP_CHAT_URL`, pasta `config/` criada pela primeira
  vez no projeto), com TODO explícito documentando que isso deve virar o
  script oficial de embed do Join.chat quando a conta da organização for
  conectada — hoje é só link direto `wa.me`/`web.whatsapp.com`.

  Ambos os componentes foram registrados em `app.ts` (array `imports`)
  e `app.html` (tags logo após `<app-footer>`), sem tocar no que já
  existia (`Navbar`, `Footer`, `BarraDeslizante` preservados).

  **Tokens de cor novos adicionados a `frontend/src/styles.css`** (não
  existiam tokens semânticos adequados): `--color-whatsapp: #25d366`
  (verde oficial da marca WhatsApp — `--color-success` existente era
  semanticamente "sucesso", não "marca", e o tom `#16a34a` não é fiel ao
  ícone real) e `--color-button-neutral: #98a1a9` (mesmo valor de
  `--color-map-panel`, mas com nome semântico correto para o novo
  contexto de "botão neutro"). Ambos comentados no `:root` com a seção
  "Botões flutuantes (widget de chat / voltar ao topo)".
- **Trade-off ou motivo**: `border-radius: 50%` usado direto (não
  `var(--radius-lg)`, que é `0` no projeto) — exceção documentada em
  comentário no CSS, pois botão de ação flutuante circular é convenção
  de mercado independente do resto do site ser "quadrado". `z-index: 900`
  hardcoded (com comentário) em ambos os componentes — abaixo dos
  `z-index` da navbar (998-1000) e acima do conteúdo normal de página;
  não usa variável CSS pois o design system trata z-index como exceção
  aceitável. Nenhum analytics/contador de clique foi adicionado (proibido
  pelo projeto). Nenhuma chamada HTTP nova — 100% estático.
- **Onde no código**: `frontend/src/app/components/whatsapp-flutuante/`
  (`.ts`, `.html`, `.css`), `frontend/src/app/components/voltar-ao-topo/`
  (`.ts`, `.html`, `.css`), `frontend/src/app/config/whatsapp.config.ts`,
  `frontend/src/styles.css` (tokens novos), `frontend/src/app/app.ts` e
  `frontend/src/app/app.html` (integração global).
- **Pendência que depende do usuário/organização**: substituir o link
  direto do WhatsApp pelo script oficial de embed do Join.chat assim que
  a conta da organização for conectada (token/script ainda não
  fornecido) — ver TODO em `whatsapp.config.ts`.
- **Ensinado por**: agente `frontend`.

## [2026-07-09] 3 páginas novas de Recursos (Anonimato, Jogo Compulsivo, Uma Nova Chance) + parágrafo em "Sobre"

- **O que foi feito/decidido**: Criadas 3 páginas de conteúdo estático
  seguindo à risca o padrão estrutural de `frontend/src/app/pages/historia/`
  (componente standalone simples, sem lógica: `.ts` só importa e renderiza
  `ResourceLayout` com `titulo`, `.html` é `<section class="section">` com
  parágrafos `<p>`, `.css` idêntico ao de `historia.css` menos a classe
  `.placeholder-note`, que não se aplicava):
  - `frontend/src/app/pages/anonimato/` — rota `/anonimato`, título "Anonimato"
  - `frontend/src/app/pages/jogo-compulsivo/` — rota `/jogo-compulsivo`, título "Jogo Compulsivo"
  - `frontend/src/app/pages/uma-nova-chance/` — rota `/uma-nova-chance`, título "Uma Nova Chance"

  Todo o texto das 3 páginas (4 parágrafos cada) veio pronto de
  especificação do agente `ux-ui`/`po` — frontend só estruturou em HTML,
  não redigiu nem editou o conteúdo.

  Rotas adicionadas em `app.routes.ts` (imports + `Routes[]`) logo após a
  rota `historia`. Itens de navegação adicionados ao final do
  `resource-nav-sublist` em `resource-layout.html` (depois de
  "Reflexão do Dia", que já estava lá de outra instância concorrente) e
  também espelhados no dropdown "Recursos" da navbar principal
  (`components/navbar/navbar.html`), que já seguia esse padrão de
  duplicar os itens do sidebar.

  Além disso, adicionado um 3º parágrafo (só parágrafo, não seção nova)
  na página `sobre.html`, dentro da seção existente `section-alt` / "O
  único requisito", explicando que as reuniões são conduzidas pelos
  próprios membros sem hierarquia/autoridade externa.
- **Trade-off ou motivo**: Reaproveitar o padrão `historia` (o mais
  simples do projeto: sem service, sem estado) porque essas 3 páginas são
  puramente informativas/estáticas, sem necessidade de dados de API —
  não havia motivo para criar um service novo. Edições em
  `app.routes.ts` e `resource-layout.html` foram cirúrgicas (não
  reescrita de arquivo inteiro) porque outras instâncias de agente
  frontend estavam simultaneamente editando os mesmos arquivos (rotas
  `mural-de-apoio`/`reflexao-do-dia`) — releitura do estado atual do
  arquivo foi feita antes de cada edição para evitar sobrescrever
  trabalho concorrente.
- **Observação sobre débito pré-existente (não corrigido aqui)**: o
  dropdown "Recursos" da navbar principal (`navbar.html`) já estava
  desatualizado em relação ao sidebar de `resource-layout.html` antes
  desta tarefa — não continha ainda "Mural de Apoio" nem "Reflexão do
  Dia". Não foi escopo desta tarefa corrigir isso retroativamente, apenas
  evitar aumentar a defasagem adicionando os 3 itens novos nos dois
  lugares.
- **Onde no código**:
  `frontend/src/app/pages/anonimato/` (`.ts`, `.html`, `.css`)
  `frontend/src/app/pages/jogo-compulsivo/` (`.ts`, `.html`, `.css`)
  `frontend/src/app/pages/uma-nova-chance/` (`.ts`, `.html`, `.css`)
  `frontend/src/app/app.routes.ts` (3 rotas novas)
  `frontend/src/app/components/resource-layout/resource-layout.html` (3 itens de nav)
  `frontend/src/app/components/navbar/navbar.html` (3 itens espelhados no dropdown Recursos)
  `frontend/src/app/pages/sobre/sobre.html` (parágrafo novo na seção "O único requisito")
- **Ensinado por**: frontend

## [2026-07-08] Feature "Depoimentos" (relatos de recuperação, em Recursos)

- **O que foi feito/decidido**: Criada a página `/depoimentos`
  (`frontend/src/app/pages/depoimentos/{depoimentos.ts,html,css}`),
  componente standalone usando `resource-layout` como wrapper (mesmo
  padrão de `literatura`/`programa-de-recuperacao`). Página única
  combinando listagem de depoimentos aprovados (mais recentes primeiro,
  cards truncados com "ler mais", estado vazio acolhedor) no topo e
  formulário de envio ao final, funcionando como o próprio CTA de
  encerramento (sem `cta-final` genérico, diferente das outras páginas de
  Recursos). Dois avisos obrigatórios antes do textarea: anonimato
  reforçado e moderação prévia (honesto sobre não ser instantâneo).
  Consome `DepoimentosService` (`frontend/src/app/services/depoimentos.ts`,
  mock com `of + delay + catchError`) via injeção — sem chamada HTTP
  direta no componente. Rota registrada em `app.routes.ts`; item de menu
  "Depoimentos" adicionado em `navbar.html` e em `resource-layout.html`,
  posicionado entre "Programa de Recuperação" e "Programa de Unidade"
  (posição definida pelo `ux-ui`).
- **Trade-off ou motivo**: Página única em vez de duas (ler/escrever
  separadas) decidida pelo `ux-ui` para reduzir fricção no caso de uso
  mais comum (ler relatos de outros antes de decidir escrever o
  próprio); confirmado como padrão do gênero via pesquisa no site
  americano de referência e afiliados regionais. Contrato de dados
  (`Depoimento`, `EnviarDepoimentoRequest`) restrito a texto + nome de
  exibição opcional, alinhado à política de segurança do `security`
  (ver `.claude/knowledge/seguranca-depoimentos.md`) — nenhum campo de
  contato/identificação foi exposto.
- **Onde no código**: `frontend/src/app/pages/depoimentos/` (`.ts`,
  `.html`, `.css`); `frontend/src/app/services/depoimentos.ts`;
  `frontend/src/app/app.routes.ts` (rota `depoimentos`); navbar e
  `frontend/src/app/components/resource-layout/resource-layout.html`
  (item de nav). Detalhe completo consolidado em
  `.claude/knowledge/depoimentos.md`.
- **Pendência sinalizada**: tela de moderação/admin para os depoimentos
  não foi pedida explicitamente pelo usuário — não deve ser assumida
  como escopo até confirmação direta (sinalizado pelo `ux-ui`).
- **Ensinado por**: ux-ui, frontend-services, frontend (coordenados por po)

## [2026-07-09] RESOLVIDO: conflito de nome em reflexao-do-dia.ts — feature concluída

- **Atualização**: a pendência sinalizada anteriormente aqui (possível
  colisão entre o nome do componente e a interface `ReflexaoDoDia`
  exportada pelo service) foi resolvida durante a implementação: o
  componente da página foi nomeado `ReflexaoDoDiaPage` (não
  `ReflexaoDoDia`), evitando colisão com a interface de domínio do
  mesmo nome. A feature "Reflexão do Dia" foi concluída nesta mesma
  janela (spec do `ux-ui`, service do `frontend-services`, página +
  componente de preview do `frontend`, revisão do `code-reviewer` com
  correção de um bug MEDIUM em mock de data hardcoded).
- **Detalhe completo consolidado em**: `.claude/knowledge/service-reflexao-do-dia.md`.
- **Ensinado por**: frontend, code-reviewer (via aprendiz, consolidação pós-review).

## [2026-07-09] Decisão de UX: página /avisos e por que a Home NÃO ganha seção própria de avisos

- **O que foi feito/decidido**: Definido pelo `ux-ui` o contrato de dados
  `Aviso` consumido pela feature (ver detalhe de implementação do service
  na entrada seguinte): `id`, `titulo`, `corpo`, `dataPublicacao` (ISO),
  `fixado` (boolean), `dataExpiracao?` (ISO opcional). Regra de
  ordenação: fixado primeiro, depois `dataPublicacao` decrescente — sem
  paginação, filtro ou busca. Posição no menu: item "Avisos" como
  **primeiro** item do dropdown "Recursos" da navbar, por ser conteúdo
  dinâmico (se destaca dos demais itens, que são conteúdo estático).
  Decisão explícita e fechada: **a Home não ganha uma seção de "avisos
  importantes"** — isso seria redundante com a barra deslizante
  (`barra-deslizante`), que já cumpre esse papel de destaque global de
  avisos em qualquer página do site.
- **Trade-off ou motivo**: sem paginação/filtro/busca por baixo volume de
  avisos esperado no projeto (evitar overengineering). A decisão sobre a
  Home é registrada aqui explicitamente para não ser reaberta por engano
  em uma tarefa futura — se alguém propuser essa seção de novo, checar
  esta entrada antes.
- **Onde no código**: decisão de produto/UX, sem código associado
  diretamente; ver `frontend/src/app/services/avisos.ts` (contrato
  `Aviso` implementado) e `frontend/src/app/pages/avisos/` (página).
- **Ensinado por**: ux-ui

## [2026-07-09] Feature nova: página /avisos

- **O que foi feito/decidido**: Criada página completa `/avisos`
  (`frontend/src/app/pages/avisos/{avisos.ts,html,css}`), seguindo o
  mesmo padrão estrutural de `literatura.ts`/`literatura.html`: usa
  `<app-resource-layout titulo="Avisos">`. Consome
  `AvisosService.listarAvisos()` (service já existente, mock já retorna
  ordenado fixado-primeiro-depois-data-decrescente) sem reordenar de novo
  na page. Avisos com `fixado === true` recebem badge "Importante" +
  borda `var(--yellow-gold)`. Data formatada via `DatePipe` do Angular
  (`date:'dd/MM/yyyy'`), não formatação manual. Rota adicionada em
  `app.routes.ts` (`avisos`). Item "Avisos" adicionado como primeiro item
  do dropdown "Recursos" em `navbar.html`.
- **Trade-off ou motivo**: decisão deliberada de não ter
  paginação/filtros/busca/categorias, por baixo volume de avisos esperado
  — evitar overengineering.
- **Onde no código**: `frontend/src/app/pages/avisos/avisos.ts` (+ `.html`, `.css`),
  `frontend/src/app/app.routes.ts` (rota `avisos`),
  `frontend/src/app/components/navbar/navbar.html` (item "Avisos" no dropdown Recursos),
  `frontend/src/app/services/avisos.ts` (service já existente, apenas consumido).
- **Ensinado por**: frontend

## [2026-07-09] Service `AvisosService` (mock)

- **O que foi feito/decidido**: Criado
  `frontend/src/app/services/avisos.ts`: interfaces `Aviso` (`id`,
  `titulo`, `corpo`, `dataPublicacao`, `fixado`, `dataExpiracao?`) e
  `AvisosResponse` (`{ success, data?, error? }`, padrão do CLAUDE.md);
  classe `AvisosService` (`providedIn: 'root'`) com o método público
  `listarAvisos(): Observable<AvisosResponse>`. Mock via
  `of(...).pipe(delay(500), map(...))`; a ordenação (fixado primeiro,
  depois `dataPublicacao` decrescente) é aplicada **dentro do service**,
  não delegada ao componente consumidor. 6 avisos de exemplo no mock, sem
  dado pessoal identificável (região genérica, ex: "Sul", em vez de
  endereço específico). Comentário no arquivo indicando que a troca
  futura para HTTP real (`this.http.get<AvisosResponse>('/api/avisos')`)
  deve manter a mesma assinatura pública do método.
- **Trade-off ou motivo**: centralizar a ordenação no service (e não no
  componente) evita que cada tela que um dia consuma `AvisosService`
  precise reimplementar a mesma regra de negócio — mesma lógica já usada
  em outros services do projeto (ex: `ReflexaoDoDiaService`,
  `DepoimentosService`) de manter a página "burra" quanto a regras que
  são do domínio de dados.
- **Onde no código**: `frontend/src/app/services/avisos.ts`.
- **Ensinado por**: frontend-services

## [2026-07-09] Barra deslizante integrada ao layout global (app.ts/app.html)

- **O que foi feito/decidido**: O componente `BarraDeslizante`
  (`frontend/src/app/components/barra-deslizante/`) existia mas não era
  consumido em lugar nenhum do app até esta entrega. Integrado agora em
  `frontend/src/app/app.ts`/`app.html`, exibido logo abaixo do header em
  todas as rotas, **exceto** `/minha-conta`. Implementado com `Router` +
  `NavigationEnd` + `signal`: um `signal<boolean>` (`mostrarBarraDeslizante`)
  é atualizado a cada `NavigationEnd`, comparando
  `event.urlAfterRedirects.startsWith('/minha-conta')`.
  ```ts
  mostrarBarraDeslizante = signal(true);
  constructor(private router: Router) {
    this.router.events
      .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
      .subscribe((event) => {
        this.mostrarBarraDeslizante.set(!event.urlAfterRedirects.startsWith('/minha-conta'));
      });
  }
  ```
- **Trade-off ou motivo**: ocultar a barra em `/minha-conta` porque é uma
  área de conta/configuração do usuário, onde o destaque global de
  avisos não é o foco da tela (mesmo racional de outras áreas de conta
  que suprimem elementos de "marketing"/destaque do site institucional).
  `startsWith` foi escolhido em vez de comparação exata para já cobrir
  eventuais sub-rotas futuras de `/minha-conta` sem precisar tocar nesse
  código de novo.
- **Onde no código**: `frontend/src/app/app.ts`, `frontend/src/app/app.html`.
- **Ensinado por**: frontend

## [2026-07-09] Design system: barra deslizante sem cores hardcoded

- **O que foi feito/decidido**: Removidas cores hardcoded do componente
  `barra-deslizante`. Antes: `@Input() color = '#C9A84C'` (ts),
  `'#b8903a'` fixo no html, `color: #fff` e `rgba(255,255,255,0.5)` no
  css. Depois, tudo via variáveis já existentes em
  `frontend/src/styles.css`: fundo `var(--cor-padrao-ja)` (vinho
  `#742C24`), bordas `var(--yellow-gold)` (`#D4A843`, agora via novo
  `@Input() borderColor`), texto `var(--white)`, separador
  `var(--color-nav-text-muted)` (reaproveitado — já existia como
  `rgba(255,255,255,0.75)`, pensado originalmente para navbar, mas
  serviu sem alteração para este caso fora desse contexto).
- **Trade-off ou motivo**: nenhum token novo precisou ser criado — reforça
  o padrão de revisar tokens "adjacentes" já existentes antes de propor
  variável nova no design system.
- **Onde no código**: `frontend/src/app/components/barra-deslizante/`
  (`.ts`, `.html`, `.css`); tokens em `frontend/src/styles.css`.
- **Ensinado por**: frontend

## [2026-07-09] Bug corrigido: gap visual entre header e barra deslizante

- **O que foi feito/decidido**: Causa raiz em
  `frontend/src/app/components/barra-deslizante/barra-deslizante.css`:
  `.ticker-wrapper` tinha `padding: 10px 0`, cujo padding-top empurrava o
  elemento para baixo no fluxo do documento, criando gap sem background
  entre header e barra. Correção: padding removido do `.ticker-wrapper`
  (ficou só `overflow: hidden`); respiro vertical realocado para
  `.ticker-item` (`padding: 10px 32px`) e `.ticker-sep` (`padding: 10px
  4px`), incluindo breakpoint mobile.
- **Trade-off ou motivo**: padrão a reter — respiro visual de uma
  faixa/ticker deve viver nos itens internos, não no wrapper externo,
  para não afetar o posicionamento do elemento no fluxo do documento.
- **Onde no código**: `frontend/src/app/components/barra-deslizante/barra-deslizante.css`
  (`.ticker-wrapper`, `.ticker-item`, `.ticker-sep`).
- **Ensinado por**: frontend

## [2026-07-09] Correção de bug: link morto no mock do Mural de Apoio

- **O que foi feito/decidido**: `code-reviewer` encontrou que o
  `banner-3` (legenda "GAM-ANON: apoio para familiares e amigos de
  jogadores") apontava `linkDestino: '/gam-anon'`, uma rota que não
  existe (nem nunca existiu em `app.routes.ts` na versão atual, nem tem
  pasta em `pages/`) — provável resquício de uma rota removida em outra
  tarefa. Corrigido para `linkDestino: '/programa-de-recuperacao'`
  (rota real mais próxima em significado, já que não há página dedicada
  a familiares/GAM-ANON ainda); `/hotlines` foi descartada porque já é o
  destino do `banner-1`. Legenda ajustada minimamente para
  "GAM-ANON: apoio para familiares e amigos de jogadores. Conheça o
  programa de recuperação." para o texto não prometer uma página que
  não existe.
- **Trade-off ou motivo**: em mocks com `linkDestino` para rotas
  internas, checar `app.routes.ts` antes de fechar o dado é obrigatório
  — o Angular Router falha essa navegação silenciosamente (sem erro
  visível), então um link morto em mock passa despercebido em QA manual.
  Se/quando existir uma página real de apoio a familiares, revisar este
  `linkDestino` de novo.
- **Onde no código**: `frontend/src/app/services/mural-de-apoio.ts`
  (`MURAL_DE_APOIO_MOCK`, item `banner-3`).
- **Ensinado por**: frontend-services

## [2026-07-09] Feature "Mural de Apoio" (banners institucionais)

- **O que foi feito/decidido**: Criada a página completa `/mural-de-apoio`
  (`frontend/src/app/pages/mural-de-apoio/{mural-de-apoio.ts,html,css}`),
  seguindo o padrão estrutural de `hotlines`/`loja` (standalone component,
  `ResourceLayout` com `titulo`, breadcrumb automático). Consome
  `MuralDeApoioService.getBannersCompleto()` no `ngOnInit`, com estados de
  `carregando`/`erro` tratados no template (mensagem acolhedora, não
  técnica). Grid responsivo de cards (`repeat(auto-fit, minmax(240px,1fr))`
  → 1 coluna em mobile), `aspect-ratio: 16/9` fixo no wrapper da imagem
  para não quebrar o grid entre cards de proporções diferentes. Card vira
  `<a [routerLink]>` só quando `banner.linkDestino` existe (via `@if`),
  senão é `<div>` estática — decisão do contrato de dados, não do
  componente. `alt` da imagem sempre vem de `banner.altText` (campo
  obrigatório da interface, nunca `alt=""`).
  Rota adicionada em `app.routes.ts` (`{ path: 'mural-de-apoio', component: MuralDeApoio }`).
  Item de navegação adicionado ao final do grupo "Recursos" em
  `resource-layout.html`.
  Seção nova na Home (`mural-teaser`) inserida entre `quiz-teaser` e
  `newsletter-teaser`, consumindo `getBannersDestaque()` a partir de
  `home.ts` (`Home implements OnInit` agora). Grade **estática, sem
  carrossel/autoplay** — decisão explícita do ux-ui: autoplay é gatilho
  visual e problema de acessibilidade para o público do site (pessoas em
  situação vulnerável/estresse). Link "Ver todos" ao final aponta para
  `/mural-de-apoio`.
  Nota de ordem final: uma seção `app-reflexao-preview` (tarefa
  concorrente de outro agente) também acabou entrando entre
  `quiz-teaser` e `newsletter-teaser` nesta mesma data — não é conflito
  de código, apenas registro de como ficou a ordem das seções da home em
  2026-07-09.
  Padrão do card replicado nos dois lugares (página completa e teaser):
  vira `<a [routerLink]="banner.linkDestino">` só quando `linkDestino`
  existe, senão `<div>` não clicável. O template assume que
  `linkDestino` é sempre rota interna Angular (usa `[routerLink]`, não
  `[href]`) — se o contrato do service um dia aceitar URL externa, o
  template precisa diferenciar os dois casos.
- **Trade-off ou motivo**: Reaproveitado 100% o service já existente
  (`frontend/src/app/services/mural-de-apoio.ts`, mock com dados
  estáticos e `delay(500)` simulando latência) — nenhuma chamada HTTP foi
  implementada no componente, conforme regra do projeto (frontend nunca
  chama API direto). Estilo dos cards duplicado propositalmente entre
  `mural-de-apoio.css` (página completa) e `home.css` (teaser) em vez de
  extrair um componente compartilhado agora, para não acoplar duas
  telas com necessidades de leiaute ligeiramente diferentes (header com
  "ver todos" vs. `cta-final` da página) nesta fase inicial do projeto —
  se o padrão se repetir em uma 3ª tela, vale extrair
  `mural-card`/`banner-card` como componente próprio.
- **Onde no código**:
  `frontend/src/app/pages/mural-de-apoio/mural-de-apoio.ts` (+ `.html`, `.css`)
  `frontend/src/app/app.routes.ts` (rota `mural-de-apoio`)
  `frontend/src/app/components/resource-layout/resource-layout.html` (item de nav)
  `frontend/src/app/pages/home/home.ts` e `home.html`/`home.css` (seção `mural-teaser`)
  `frontend/src/app/services/mural-de-apoio.ts` (service já existente, apenas consumido)
- **Pendência sinalizada (não é trabalho de frontend)**: quando existir
  backend/admin real para upload de banners, os metadados EXIF da imagem
  precisam ser removidos antes de salvar (regra geral do CLAUDE.md). Essa
  responsabilidade é do `backend`/`security`; o service e o componente de
  frontend só consomem `imagemUrl` já processada/final — nada a fazer
  aqui, apenas não esquecer ao implementar o upload real.
- **Ensinado por**: frontend

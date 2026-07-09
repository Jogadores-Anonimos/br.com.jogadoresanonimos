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

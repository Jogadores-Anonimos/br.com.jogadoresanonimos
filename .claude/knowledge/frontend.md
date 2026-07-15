### Ajuda-imediata (seção Home, CTA de WhatsApp)
- **Onde**: `frontend/src/app/pages/home/home.html`/`.css`/`.ts` (seção `.ajuda-imediata`)
- **Propósito**: CTA grande de WhatsApp na Home, distinto do botão flutuante, visível na primeira dobra para público em possível crise
- **Como**: posicionada logo após `.hero`, antes de `.cta-primary`; usa `--cor-padrao-ja` como fundo e `--color-whatsapp` (verde, não `--color-cta` vermelho) no botão; `WHATSAPP_CHAT_URL` importado de `config/whatsapp.config.ts`, nunca duplicado
- **Trade-off**: verde da marca do canal em vez do token de CTA padrão do site — vermelho remeteria a emergência/erro, contrário ao tom de acolhimento; precedente de que nem toda seção de CTA usa `--color-cta` por padrão

### Arquivo/services organizados por domínio (feature folders)
- **Onde**: `frontend/src/app/services/*/`
- **Propósito**: navegação e manutenção de ~48 services que estavam flat na pasta
- **Como**: cada domínio em subpasta própria (`auth/`, `cms/`, `relatos/`, etc.) agrupando `service.ts`+`*.model.ts`+`*.mock.ts`; cross-cutting (`api-envelope.ts`, `recaptcha-loader.ts`) fica na raiz; imports cross-domain viram `../outro-dominio/arquivo`, sem duplicar tipo (single source of truth)

### AvisosService — Home vs decisão de UX anterior (conflito registrado)
- **Onde**: `frontend/src/app/pages/home/home.ts`/`.html`/`.css` (seção `.avisos-teaser`)
- **Propósito**: registrar que a Home ganhou seção de avisos por pedido explícito, contradizendo decisão de UX anterior de que a Home não deveria ter essa seção (redundante com a barra deslizante)
- **Como**: consome `AvisosService.listarAvisos()`, mostra 3 primeiros; badge "Importante" com `--color-warning` para avisos fixados
- **Trade-off**: pendência sinalizada ao `po`/`ux-ui` para decidir conscientemente entre revogar a decisão antiga, reverter esta seção, ou conviver com as duas

### AuthService — sessionStorage sempre, localStorage condicional a lembrarDeMim (bugfix)
- **Onde**: `frontend/src/app/services/auth/auth.ts` (`aplicarLoginBemSucedido`, `lerSessaoPersistida`, `limparSessaoPersistida`, `rehidratarSessao`)
- **Propósito**: corrigir bug onde F5 na mesma aba deslogava usuário que não marcou "lembrar de mim" (token só ficava em memória)
- **Como**: `sessionStorage` gravado sempre após login, independente de `lembrarDeMim` (já não sobrevive a fechar aba/navegador, cobre esse caso sozinho); `lembrarDeMim=true` só adiciona gravação em `localStorage` (mesma `SessaoPersistida`); leitura prioriza `sessionStorage`, cai para `localStorage` via `lerSessaoDeStorage`; `limparSessaoPersistida` limpa os dois; `rehidratarSessao` sincroniza `sessionStorage` da aba nova ao reidratar de `localStorage`; `expiraEm` respeitado em ambos
- **Complemento**: signal público `rehidratacaoConcluida` (readonly, `asReadonly()`, mesmo padrão de `usuarioAtual`) criado para `admin.guard.ts` saber quando `rehidratarSessao()` (assíncrono, roda no construtor via subscribe em `buscarUsuarioLogado()`) terminou; `set(true)` nos 4 caminhos de saída: sem sessão persistida, sessão expirada, e dentro do subscribe (sucesso/falha compartilham o mesmo `set(true)` após o if/else)

### admin.guard — aguarda rehidratação de sessão antes de checar usuário (bugfix)
- **Onde**: `frontend/src/app/guards/admin.guard.ts` (`adminGuard`)
- **Propósito**: corrigir falso "não logado" em refresh de página — guard lia `auth.usuarioAtual()` antes de `Auth.rehidratarSessao()` terminar, redirecionando erroneamente para `/minha-conta` com sessão válida
- **Como**: guard virou `async`; antes de ler `usuarioAtual()`, faz `await firstValueFrom(toObservable(auth.rehidratacaoConcluida).pipe(filter(c => c), take(1)))` (`toObservable` de `@angular/core/rxjs-interop`, `firstValueFrom`/`filter`/`take` de `rxjs`); lógica de redirecionamento preservada (`/minha-conta` se não logado, `/` se não ADMIN, `true` se ADMIN)
- **Trade-off**: padrão (`toObservable(signal) + filter + take(1) + firstValueFrom`, guard `async`) é reutilizável por qualquer `CanActivateFn` que dependa de sessão rehidratada

### Bug: custom properties CSS de `:host` não herdam entre elementos irmãos no DOM
- **Onde**: `pages/admin/admin-layout/admin-layout.css`, `app.ts` (`PREFIXOS_SEM_BARRA_DESLIZANTE`)
- **Propósito**: corrigir gap/overlap na sidebar sticky do admin e ticker aparecendo indevidamente no admin
- **Como**: `--utility-bar-height`/`--ticker-height` são setadas no `:host` de `app-navbar` (filho de `<header>`); `<main>` (onde vive o admin via `router-outlet`) é irmão de `<header>`, não descendente, então nunca herdava — `getPropertyValue` resolvia vazio e caía no fallback `0px`; fix declara localmente no `:host` do `admin-layout` os mesmos valores (`--utility-bar-height: 48px`, `--ticker-height: 0px`, admin nunca mostra ticker); `top`/`height` da `.admin-sidebar` somam `--nav-height + --utility-bar-height + --ticker-height`, mesmo padrão de `navbar.css:641/651`; `/admin` adicionado a `PREFIXOS_SEM_BARRA_DESLIZANTE`
- **Trade-off**: padrão a repetir em qualquer layout novo (admin ou futuro) que dependa dessas variáveis — replicar valor localmente no `:host`, nunca assumir herança entre irmãos de DOM só porque parecem "no mesmo layout" visualmente

### Barra deslizante — integração global (app.ts/app.html)
- **Onde**: `frontend/src/app/app.ts`, `app.html`, `components/barra-deslizante/`
- **Propósito**: exibir a barra em todas as rotas exceto `/minha-conta`
- **Como**: `signal<boolean>` atualizado a cada `NavigationEnd`, comparando `startsWith('/minha-conta')`; sticky centralizado em wrapper comum `<header class="app-header-sticky">` (não em cada componente isoladamente) para navbar e barra não descolarem no scroll; custom property `--ticker-height` consumida pela navbar para cálculo de `top`/altura do menu mobile
- **Trade-off**: `--ticker-height` é derivada manualmente do CSS do ticker, não calculada automaticamente — precisa atualização manual se padding/font-size do ticker mudar

### Barra deslizante — sem cor hardcoded
- **Onde**: `components/barra-deslizante/`
- **Propósito**: eliminar cores hardcoded (`#C9A84C`, `#b8903a`, `#fff`)
- **Como**: fundo `var(--cor-padrao-ja)`, bordas `var(--yellow-gold)` via `@Input() borderColor`, texto `var(--white)`

### Botões flutuantes (WhatsApp e voltar ao topo)
- **Onde**: `components/whatsapp-flutuante/`, `components/voltar-ao-topo/`, `config/whatsapp.config.ts`
- **Propósito**: CTA discreto de contato via WhatsApp + atalho de scroll, fixos no canto da tela
- **Como**: `whatsapp-flutuante` abre popup não-bloqueante (sem overlay/modal), fecha com Esc, `aria-expanded`/`aria-controls`/`inert` no popup fechado; `voltar-ao-topo` só aparece após 300px de scroll, usa `fromEvent` + `auditTime(100ms)` do RxJS para throttle; URL do WhatsApp centralizada em `whatsapp.config.ts` (`WHATSAPP_PHONE_NUMBER` confirmado como número institucional real)
- **Trade-off**: `border-radius: 50%` hardcoded (não `var(--radius-lg)`, que é `0` no projeto) — exceção documentada, convenção de mercado para botão circular flutuante; `z-index: 900` hardcoded também documentado; ainda é "casca" fiel ao Join.chat, não integração real de embed

### Bug: assets estáticos na pasta errada (`frontend/public/` vs `frontend/src/public/`)
- **Onde**: `angular.json` (config `assets`), `frontend/src/public/images/`
- **Propósito**: Angular deste projeto serve assets só de `frontend/src/public/`
- **Como**: qualquer asset estático novo deve ir para `frontend/src/public/images/<categoria>/`, nunca `frontend/public/` (raiz, sem `src/`) — salvar fora produz 404 silencioso, sem erro de compilação

### Bug: cálculo de largura errado entre botões flutuantes
- **Onde**: `components/voltar-ao-topo/voltar-ao-topo.css`, `components/whatsapp-flutuante/whatsapp-flutuante.css`
- **Propósito**: registro de bug de espaçamento entre dois elementos `position: fixed`
- **Como**: `voltar-ao-topo.css` calculava `right` via `calc()` assumindo 48px de largura do botão WhatsApp, mas era 56px — corrigido
- **Trade-off**: sempre que um elemento fixed depende da largura de outro para `calc()`, deixar isso explícito (comentário) evita esse tipo de bug ao alterar um dos dois

### Bug: link morto em mock (Mural de Apoio)
- **Onde**: `frontend/src/app/services/mural-de-apoio.ts` (`MURAL_DE_APOIO_MOCK`)
- **Propósito**: `linkDestino: '/gam-anon'` apontava para rota inexistente
- **Como**: corrigido para `/programa-de-recuperacao`; Angular Router falha navegação silenciosamente para rota inexistente, sem erro visível
- **Trade-off**: sempre checar `app.routes.ts` antes de fechar `linkDestino` em mock

### Bug: subscribe() sem handler de `error` trava flag de loading (Home — relatos/mural/avisos)
- **Onde**: `pages/home/home.ts` (subscribes de mural, avisos, relatos), `services/relatos/relatos.ts` (`listarRelatosAprovados`)
- **Propósito**: preview de relatos ficava preso em "Carregando relatos recentes..." mesmo com `GET /api/v1/relatos` retornando 200
- **Como**: `subscribe()` passava só `next`; falha silenciosa na cadeia RxJS (ex: exceção no `map` do service, não erro HTTP) nunca chega ao `next` e sem handler `error` a flag `carregando*` nunca vira `false`; fix adiciona `catchError` no pipe do service e troca os três `subscribe()` da Home para objeto `{ next, error }`, setando `carregando*=false`/`erro*=true` no caminho de erro
- **Trade-off**: padrão a checar em qualquer Observable HTTP com flag de loading — `subscribe` sempre precisa de handler `error` explícito que desligue a flag, senão qualquer falha não-HTTP na cadeia trava o loading indefinidamente

### CMS — páginas institucionais migradas de hardcode para blocos
- **Onde**: `pages/{sobre,historia,anonimato,privacidade,termos,jogo-compulsivo,uma-nova-chance,programa-de-recuperacao,programa-de-unidade,contribuir,vinte-perguntas}/`
- **Propósito**: consumir `Cms.getPaginaPublica(slug)` em vez de texto hardcoded
- **Como**: `ngOnInit` filtra `blocos` por `tipo === 'texto'`, renderiza com `@for` + `[innerHTML]`; `vinte-perguntas` não tem `CmsPagina` própria, usa service dedicado (`vinte-perguntas.ts`/`.model.ts`/`.mock.ts`) consumindo `GET /api/v1/vinte-perguntas`
- **Trade-off**: `HtmlSanitizer` do backend só permite `b/i/p/br/a`, então heading/lista numerada estilizada (`sobre`, `programa-de-recuperacao`, `programa-de-unidade`, `contribuir`) perdeu formatação, virando parágrafos simples sequenciais — não recuperável via CSS sem sniffar conteúdo por regex

### CMS admin — lista e editor de blocos
- **Onde**: `pages/admin/paginas-lista/` (`PaginasLista`), `pages/admin/pagina-editor/` (`PaginaEditor`)
- **Propósito**: telas admin de CRUD de página CMS
- **Como**: `PaginasLista` é tabela simples sem busca/paginação/criar/excluir (regra de negócio confirmada); `PaginaEditor` recebe `slug` via rota, lista vertical de blocos com mover/remover/adicionar, indicador de alterações não salvas (signal), `beforeunload` + confirm no botão de saída (sem `CanDeactivate` guard, decisão consciente por volume/risco baixos)
- **Trade-off**: sem NgRx/state global — signals locais bastam para volume de dados pequeno (um form de página por vez)

### Depoimentos → Relatos (rename completo)
- **Onde**: `frontend/src/app/pages/relatos/`, `services/relatos.ts`, `app.routes.ts`
- **Propósito**: alinhar nomenclatura 100% (página, service, interfaces, métodos, classes CSS)
- **Como**: rota `/depoimentos` → `/relatos` com redirect preservado (`redirectTo`, usuários podem ter link salvo); página "Boletim Informativo" removida sem redirect (decisão do `ux-ui`, sem conteúdo real publicado)
- **Trade-off**: `home.html` manteve link quebrado para `/boletim-informativo` propositalmente (fora de escopo, pendência aberta até tarefa confirmada pelo usuário) — depois corrigido em tarefa separada para `/relatos`

### Feature "Depoimentos"/Relatos — página única (ler + escrever)
- **Onde**: `pages/relatos/` (antes `pages/depoimentos/`)
- **Propósito**: reduzir fricção do caso de uso mais comum (ler relatos antes de escrever o próprio)
- **Como**: topo lista aprovados (mais recentes primeiro, "ler mais"), final tem formulário como CTA de encerramento (sem `cta-final` genérico); dois avisos obrigatórios antes do textarea (anonimato + moderação prévia)
- **Trade-off**: confirmado como padrão do gênero via pesquisa em site americano de referência e afiliados regionais

### Feature "Mural de Apoio"
- **Onde**: `pages/mural-de-apoio/`, seção `.mural-teaser` na Home
- **Propósito**: página de banners institucionais (cartazes de grupos JA) + teaser na Home
- **Como**: card vira `<a [routerLink]>` só quando `banner.linkDestino` existe, senão `<div>` estática; grid estático sem carrossel/autoplay (decisão de acessibilidade do `ux-ui` — autoplay é problema para público em situação de estresse); `alt` sempre de `banner.altText`, campo obrigatório
- **Trade-off**: estilo de card duplicado entre página completa e teaser em vez de extrair componente compartilhado nesta fase (telas com necessidades de layout ligeiramente diferentes)

### Mural de Apoio — correção de layout (pôster vertical, não capa de blog)
- **Onde**: `pages/mural-de-apoio/mural-de-apoio.css`, `home.css` (`.mural-teaser`)
- **Propósito**: formato real dos cartazes é pôster/flyer vertical autoexplicativo, não card de blog horizontal
- **Como**: `aspect-ratio` do wrapper de imagem trocado de `16/9` para `9/16` (medição real dos arquivos: `9/16` minimiza corte de `object-fit: cover` no topo/rodapé, onde fica o texto); `max-width: 320px; margin: 0 auto` no próprio card (não só no grid) evita pôster virar "hero image" gigante em desktop; legendas de texto removidas (cartaz já é autoexplicativo)
- **Trade-off**: medir o material real antes de aplicar a proporção pedida na spec original (`3/4` cogitado inicialmente, `9/16` venceu por medição)

### Feature "Reflexão do Dia" + preview na Home
- **Onde**: `pages/reflexao-do-dia/`, `components/reflexao-preview/`, `services/reflexao-do-dia.ts`
- **Propósito**: fluxo mês→dia→texto com mês/dia atuais pré-selecionados automaticamente (calculados de `new Date()`)
- **Como**: `ReflexaoDoDiaService` com `obterDiasNoMes`, `obterReflexaoDeHoje`, `obterReflexaoPorData`; componente nomeado `ReflexaoDoDiaPage` (evita colisão com interface `ReflexaoDoDia`); `aria-live="polite"` no container de texto
- **Trade-off**: fevereiro sempre tratado com 28 dias (sem ano bissexto, modelagem é só mês+dia); débito LOW não corrigido: padding hardcoded em vez de `var(--spacing-*)` e ausência de `aria-live` no preview da Home (só a página completa tem)

### Reflexão do Dia — correção de contraste (fundo vinho sólido removido)
- **Onde**: `pages/reflexao-do-dia/reflexao-do-dia.css`, `components/reflexao-preview/reflexao-preview.css`
- **Propósito**: página usava `--cor-padrao-ja` como fundo sólido de seção/card inteiro — único caso no site (resto usa só como acento)
- **Como**: fundo trocado para `--color-bg-section`/`--color-bg-surface`, título virou acento vinho; grade de botões teve cada estado (normal/hover/ativo/foco) revisado individualmente, não troca mecânica de uma variável só; `.reflexao__botao-dia--ativo` inverteu o esquema (fundo vinho sólido/texto branco) para não perder contraste de "selecionado" com fundo claro
- **Trade-off**: nenhuma variável nova criada, tudo reaproveitou tokens existentes; lição geral — migrar de fundo escuro para claro exige reavaliar cor de texto/outline estado a estado

### Feature "Avisos"
- **Onde**: `pages/avisos/`, `services/avisos.ts`
- **Propósito**: página de avisos institucionais, sem paginação/filtro/busca (baixo volume esperado)
- **Como**: `AvisosService.listarAvisos()` centraliza ordenação (fixado primeiro, depois `dataPublicacao` decrescente) no service, não no componente; badge "Importante" + borda `--yellow-gold` para fixados

### Bug: quebra de texto em relatos (home + admin) — min-width:0 ausente em `<li>` de flex column
- **Onde**: `pages/home/home.css` (`.relatos-teaser__preview-item`), `pages/admin/admin-relatos/admin-relatos.css` (`.admin-relatos__card`)
- **Propósito**: `<p>` neto com `overflow-wrap`/`word-break` não quebrava texto longo, mesmo já tendo essas propriedades
- **Como**: `<li>` é filho direto de container `flex-direction: column`; item flex tem `min-width: auto` por padrão, que no eixo column é a largura — nunca encolhia abaixo do conteúdo, então o `<p>` neto nunca recebia largura restrita para o `overflow-wrap` atuar; fix adiciona `min-width: 0` no `<li>`
- **Trade-off**: padrão de diagnóstico a repetir — se `overflow-wrap`/`word-break` não funciona visualmente num filho dentro de flex/grid, suspeitar primeiro de `min-width`/`min-height: auto` no ancestral flex/grid antes de mexer mais no elemento de texto

### Menu "Recursos" — item clicável + toggle separados
- **Onde**: `components/navbar/navbar.html`
- **Propósito**: item de dropdown que também navega direto (ex: "Recursos" → `/reunioes`)
- **Como**: dois elementos irmãos dentro do `<li class="dropdown">`: `<a class="dropdown-label" routerLink="...">` (link real) + `<button class="dropdown-toggle dropdown-toggle--icon">` (só ícone, controla toggle)
- **Trade-off**: `routerLink` e `(click)="toggleDropdown(...)"` nunca no mesmo elemento — impede navegação e/ou dispara toggle acidentalmente

### Navbar/resource-layout — duplicação sem fonte única de verdade
- **Onde**: `components/navbar/navbar.html` (dropdown "Recursos"), `components/resource-layout/resource-layout.html` (`resource-nav-sublist`)
- **Propósito**: registrar padrão de risco — duas listas hardcoded que precisam ser atualizadas manualmente em conjunto
- **Como**: sempre que um link de recurso for adicionado/removido em um, replicar no outro na mesma tarefa

### Busca funcional na navbar (client-side, sem persistência)
- **Onde**: `components/navbar/navbar-search-index.ts`, `navbar.ts`
- **Propósito**: busca em tempo real dentro do `.search-panel`, respeitando anonimato
- **Como**: índice estático `NAVBAR_SEARCH_INDEX` (18 itens, `titulo`/`rota`/`keywords[]`), busca 100% client-side (`.filter()`, sem lib nova), limitada a 8 resultados
- **Trade-off**: termo de busca nunca logado/persistido/vai para URL; decisão deliberada de não implementar "buscas recentes"/histórico, incompatível com anonimato

### Páginas de Recursos padrão simples (sem service/estado)
- **Onde**: `pages/anonimato/`, `pages/jogo-compulsivo/`, `pages/uma-nova-chance/` (padrão de `pages/historia/`)
- **Propósito**: página puramente informativa/estática
- **Como**: `.ts` só importa e renderiza `ResourceLayout` com `titulo`, `.html` é `<section class="section">` com parágrafos

### ResourceLayout — wrapper padrão de página de recurso
- **Onde**: `components/resource-layout/`
- **Propósito**: evitar reimplementar breadcrumb/banner/landmarks em cada página nova
- **Como**: `[mostrarSidebar]="false"` quando não precisa de sidebar (ex: `esqueci-senha`, `redefinir-senha`, `minha-conta`)

### UI de autenticação (registro, login, esqueci-senha, redefinir-senha)
- **Onde**: `pages/minha-conta/`, `pages/esqueci-senha/` (novo), `pages/redefinir-senha/` (novo)
- **Propósito**: completar telas do fluxo de auth conforme contrato já documentado
- **Como**: campo `apelido` (3-30 chars, sem espaço) e `confirmarSenha` adicionados ao registro; mensagens de erro por campo usam `error.fields` do envelope; `esqueci-senha` sempre mostra mensagem genérica de sucesso (anti-enumeration, sem branch dependente de "conta existe"); `redefinir-senha` lê token via query param (`?token=...`), nunca rota fixa, entra em estado de erro se token ausente
- **Trade-off**: validação client-side (HTML5) é só UX, nunca substitui a validação do service/backend (`error.fields` é a fonte de verdade)

### Services Auth/CMS mockados — padrão `environment.useMocks`
- **Onde**: `services/auth.ts`, `services/cms.ts`, `services/api-envelope.ts`, `frontend/src/environments/environment.ts`
- **Propósito**: permitir troca de mock para chamada HTTP real sem mudar assinatura pública
- **Como**: `if (!environment.useMocks) { chamada real } else { mock }` dentro de cada método (não `HttpInterceptor` dedicado); `ApiEnvelope<T>` genérico centraliza `{ success, data, error }`; `UsuarioResumo` (sem email) vs `UsuarioMe` (com email) como tipos TypeScript distintos — impossível, a nível de tipo, ler `.email` de resposta de login/registro

### environment.prod.ts + fileReplacements (build de produção)
- **Onde**: `frontend/src/environments/environment.prod.ts` (novo), `angular.json` (target `build`, configuration `production`)
- **Propósito**: separar config de produção da de dev, sem mudar código-fonte por ambiente
- **Como**: `fileReplacements` troca `environment.ts` por `environment.prod.ts` na configuration `production`; `apiBaseUrl` de prod é relativo (`/api/v1`), assume reverse proxy servindo front+back no mesmo domínio; `ng build` já usa `production` como `defaultConfiguration` do builder `@angular/build:application`, então build sem flag sai correto
- **Trade-off**: `recaptchaSiteKey` de prod está temporariamente igual à de dev — não existe chave própria de produção ainda, pendência registrada em `PENDENCIAS.md`; domínio real de produção também não existe, depende de hospedagem do cliente

### VintePerguntasService/ImagemService — services admin
- **Onde**: `services/imagem.ts`, `services/vinte-perguntas.ts`
- **Propósito**: fechar gaps de upload de imagem e CRUD admin de perguntas, endpoints já existiam no backend
- **Como**: mesmo padrão de `arquivo.ts` (`ApiEnvelope<T>`, `environment.useMocks`, `*.mock.ts` separado); `uploadImagem`/`removerImagem` via `FormData`; `listarAdmin`/`atualizarPergunta` como métodos/URLs distintos de `listar()` público (autorização diferente, podem divergir de shape no futuro)

### VintePerguntasService — perguntas dinâmicas (breaking change)
- **Onde**: `services/vinte-perguntas.ts`, `.model.ts`, `.mock.ts`
- **Propósito**: backend reverteu `numero` fixo 1-20 para lista dinâmica (criar/remover/mover)
- **Como**: `VinteQuestaoResponse` ganhou `id: string` (chave estável) + `ordem: number` (mutável); `atualizarPergunta`/`removerPergunta` passaram a usar `id`, não `numero`; novos `criarPergunta`, `moverPerguntaCima`/`moverPerguntaBaixo`

### Design system — botões CSS, z-index e valores de referência
- **Onde**: `frontend/src/styles.css`
- **Propósito**: evitar token novo quando já existe adjacente
- **Como**: sempre checar tokens existentes (`--color-bg-surface`, `--color-bg-section`, `--color-warning`, `--color-whatsapp`) antes de propor variável nova

### Fix: download de PDF autenticado (regressão pós-endurecimento de `/arquivos/{id}/download`)
- **Onde**: `services/arquivo/arquivo.ts` (`baixarArquivo`), `services/arquivo/download-blob.util.ts`, `pages/literatura/literatura.ts`/`.html`/`.css`, `pages/minha-conta/minha-conta.ts`/`.html`/`.css`
- **Propósito**: `<a [href]="urlDownload" target="_blank">` não passa pelo `authInterceptor` (só `HttpClient` carrega o JWT) — endpoint virou autenticado e link direto passou a dar 401
- **Como**: `baixarArquivo(id)` no service faz `http.get(..., { responseType: 'blob' })`; componente assina, chama `disparaDownloadDeBlob(blob, nomeOriginal)` (cria `<a>` temporário com `URL.createObjectURL`, `.click()`, `revokeObjectURL`); `<a href>` virou `<button (click)>` estilizado igual (herdou classes, só adicionou `border:none`/`cursor:pointer`/estado `:disabled`); estado `idsBaixando`/`downloadsBaixando` (Set) + `erroDownloadPorId` (Map) para loading/erro por item
- **Trade-off**: helper de disparo de blob centralizado em `services/arquivo/` (não em `shared/utils`) por só ter um consumidor de domínio hoje; padrão replicável para qualquer outro download autenticado futuro
- **Pendência bloqueante**: `SecurityConfig.java` já tem a regra nova em disco (`GET /api/v1/arquivos` permitAll, resto autenticado) mas o processo Spring Boot rodando em `localhost:8080` ainda serve o binário antigo (`/api/v1/arquivos/**` permitAll) — confirmado via `curl` (200 sem token). Precisa restart do backend (agente `backend`/`devops`) para validar ponta a ponta contra API real; fix do frontend foi validado por `tsc --noEmit` + `ng build` limpos e por inspeção de contrato, não por request 401→200 real.

### CRUD admin — merge local em vez de refetch pós create/update/delete (performance MEDIUM)
- **Onde**: `pages/admin/admin-avisos`, `admin-faq`, `admin-literatura`, `admin-hotlines`, `admin-reunioes`, `admin-escritorios-regionais` (`.ts` de cada); referência de padrão pré-existente: `admin-usuarios.ts`, `admin-relatos.ts`
- **Propósito**: eliminar round-trip de listagem completa após toda mutação (create/edit/delete/mover) — service já devolve a entidade salva no envelope, refetch era redundante
- **Como**: cada página guarda o array local e faz merge imutável (`.map`/`.filter`/spread) usando o `envelope.data` retornado pela própria chamada de mutação; reordenação local reaplicada quando a lista tem ordem (avisos: fixado+data; reuniões: nome; faq/escritórios: campo `ordem`); mover-cima/mover-baixo (faq, escritórios: região/estado/grupo) resolvido por swap local de `ordem` entre item atual e vizinho na lista já ordenada em tela, porque o service só devolve o item movido, não o vizinho trocado
- **Caso não resolvido por merge local**: `admin-escritorios-regionais.salvarEndereco` — `criarEnderecoAdmin` pode criar um `EstadoIntergrupo` novo implicitamente (uf ainda não tinha estado na região) e devolve só o `GrupoIntergrupo`, sem os dados do estado pai (nome/ordem); código checa se o estado já existe localmente (merge) e só cai em `carregarEstrutura()` (refetch) nesse caso raro de estado novo — sinalizado como possível evolução de contrato (backend devolver o estado pai) para o agente `backend`/`frontend-services`, não implementado agora por ser fora de escopo da tarefa
- **admin-hotlines.salvarTelefones**: caso mais complexo (forkJoin de add/edit/delete de telefones misturados); telefones adicionados via `adicionarTelefoneAdmin` (que retorna a região inteira) são extraídos do envelope só depois de todas as chamadas resolverem, telefones editados/mantidos são recompostos a partir do estado local + formulário — não depende de refetch em nenhum caminho
- **Trade-off**: `admin-hotlines`/`admin-escritorios-regionais` ficaram com mais linhas por causa da reconstrução de estrutura aninhada (região→estado→grupo), mas eliminam o `carregarEstrutura()` do caminho feliz; validado só por `tsc --noEmit` + `ng build` limpos, sem teste E2E

### admin-seguranca — mfaAtivo hardcoded false (bugfix HIGH)
- **Onde**: `frontend/src/app/pages/admin/admin-seguranca/admin-seguranca.ts`/`.html`
- **Propósito**: corrigir admin com MFA já ativo vendo tela de "ativar MFA" (QR/enroll) em vez do estado real
- **Como**: `ngOnInit` chama `auth.buscarUsuarioLogado()`, popula `mfaAtivo` com `resposta.data.mfaAtivo` (campo de `UsuarioMe`, nao esta no signal `usuarioAtual`); flag `carregandoEstadoInicial` evita flash do card errado antes da resposta chegar, controla os 3 blocos `@if`
- **Complemento**: `onclick="this.select()"` inline no input readonly (linha da chave otpauth) trocado por `(click)="selecionarChaveConfiguracao(campoOtpauth)"` com template reference `#campoOtpauth`, metodo tipado `HTMLInputElement` no componente

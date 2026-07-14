# Pendências — Dashboard Admin (escopo real)

## Sessão de testes reais 2026-07-13, parte 2 — mais bugs e melhorias

Continuação do teste manual, depois dos 13 itens da parte 1 (ver seção
abaixo). Tudo já corrigido e validado com `ng build`:

14. **Erro real de upload de PDF não aparecia pro usuário.** Todos os
    métodos de `arquivo.ts` (`listarArquivos`, `listarArquivosAdmin`,
    `uploadArquivo`, `editarArquivo`, `deletarArquivo`) tinham
    `catchError` que descartava a mensagem real do backend (ex: "PDF
    rejeitado — contém OpenAction") e mostrava um genérico. Criado
    helper `apiErrorDeHttpError` em `api-envelope.ts` (extrai
    `error.error.message` do corpo da resposta HTTP quando existe) e
    aplicado nos 5 pontos. Vale revisar se esse mesmo padrão de
    `catchError(() => genérico)` se repete em outros services — não foi
    auditado em todos, só corrigido onde foi reportado.
15. **Erro "PDF rejeitado — contém OpenAction" não era bug** — é o
    validador de segurança (`ArquivoProcessor`) funcionando conforme
    projetado, rejeitando PDF com JavaScript/auto-execução embutido.
16. **Capa/preview de PDF implementada.** Upload de literatura agora
    gera thumbnail da primeira página via PDFBox (já era dependência do
    projeto, sem lib nova) — `ArquivoProcessor.gerarThumbnail()`, nova
    coluna `thumbnail_chave` em `arquivo` (migration `V20`), exposta
    como `thumbnailUrl` em `ArquivoResponse`. Falha ao gerar não derruba
    o upload (PDF válido, só sem capa, cai no ícone genérico). Exibida
    em `/literatura` (capa 120×156) e no admin (48×62).
17. **Admin de Reuniões redesenhado** (formulário tinha campo "Dia" e
    "Dia da semana" duplicados, horário texto livre, período manual,
    lat/long manual):
    - "Dia" (texto livre) removido — "Dia da semana" (dropdown) virou
      obrigatório e único, `dia`/`periodo` enviados ao backend são
      derivados automaticamente (`periodoParaHorario()` calcula a
      faixa a partir do horário).
    - Horário virou dropdown de 30 em 30 min (00:00–23:30).
    - CEP com busca via ViaCEP (preenche endereço) + Nominatim/
      OpenStreetMap (geocodifica pra lat/long) — ambas gratuitas, sem
      chave de API. Campo "Número" separado, concatenado no endereço
      antes de geocodificar.
    - Label do campo de endereço trocado de "Local" pra "Endereço".
18. **Página pública `/reunioes` ainda usava 8 reuniões mock hardcoded**
    — nunca tinha sido migrada pra API real. Corrigido: agora usa
    `ReuniaoService.listarReunioes()` (mesmo service já usado no
    admin), com estado de carregando/erro. Removida a interface
    `Reuniao` local duplicada — reusa `reuniao.model.ts`.
19. **12 Passos / 12 Princípios sem visual de card.** Migração pro CMS
    (sessão anterior) transformou a lista numerada numa única `<ol>`
    dentro de um bloco — funcionalmente correto pro admin editar, mas
    sem o estilo de card original. Resolvido via CSS (`.section ol`/
    `.section ol li` como grid de cards), sem mudar a estrutura de
    conteúdo — aplicado em `programa-de-recuperacao.css` e
    `programa-de-unidade.css`.
20. **Gestão de Usuários — filtro por apelido/e-mail/role** adicionado
    (client-side, lista já vem inteira do backend — volume baixo não
    justifica filtro server-side).

## Sessão de testes reais 2026-07-13, parte 1 — bugs encontrados e corrigidos

Usuário testou o ambiente completo pela primeira vez com `useMocks:
false` (backend real). Lista do que apareceu e foi resolvido nesta
sessão, na ordem em que surgiu:

1. **Login travava em "Entrando..." pra sempre.** Causa raiz real: bug
   em `Navbar` (`scrolled`, campo mutável escrito fora do Angular por
   callback de scroll) disparava `NG0100`
   (`ExpressionChangedAfterItHasBeenCheckedError`) — em app zoneless,
   isso aborta o ciclo de change detection inteiro, inclusive de
   componentes não relacionados. Corrigido convertendo `scrolled` pra
   `signal`. Mesmo padrão de bug também existia em `Reunioes`
   (`localizacaoPermitida`/`localizacaoCarregando`/`localizacaoIndisponivel`
   setados dentro de callback de geolocalização + `appRef.tick()` manual)
   — corrigido do mesmo jeito.
2. **CORS bloqueando toda chamada à API.** `CORS_ALLOWED_ORIGIN` vazio
   no `.env` (warning já aparecia desde o início da sessão anterior).
   Corrigido com valor padrão no `docker-compose.yml`
   (`${CORS_ALLOWED_ORIGIN:-http://localhost:4200}`), sem depender do
   `.env` estar preenchido em dev.
3. **Login com admin real retornava 401.** Causa: `AdminBootstrap` nunca
   criou o admin de verdade — o usuário-sistema fake do seed de CMS
   (`sistema.seed.cms`, criado só pra FK de auditoria) tinha `role
   ADMIN` por engano, e isso fazia o bootstrap achar que já existia
   admin e pular a criação. Corrigido com migration `V19` (muda a role
   desse usuário-sistema pra `PARTICIPANTE`).
4. **Login retornava 500 depois do bootstrap corrigido.**
   `io.jsonwebtoken.lang.UnknownClassException` — faltava a dependência
   `jjwt-impl` no `pom.xml` (só tinha `jjwt-api`/`jjwt-jackson`, sem a
   implementação; geração de JWT nunca funcionava). Adicionada
   (confirmado com o usuário antes, por causa da regra de não adicionar
   dependência nova sem perguntar — esta foi tratada como completar uma
   lib já escolhida, não escolha nova).
5. **Botão de login/cadastro sem timeout de segurança.** Adicionado
   `timeout()` + `finalize()` em `onLoginSubmit`/`onRegistroSubmit`
   (`minha-conta.ts`) — garante que o botão nunca fica preso
   indefinidamente mesmo com bug futuro. Ajustado de 15s → 8s → **5s**
   (valor final, a pedido do usuário).
6. **Escritórios Regionais dava 500 ao carregar no admin.**
   `MultipleBagFetchException` do Hibernate — a query
   `findAllComHierarquiaCompleta()` tentava `JOIN FETCH` duas coleções
   `List` (bag) ao mesmo tempo (`regiao.estados` e `estado.grupos`), o
   que o Hibernate não permite. Corrigido removendo o segundo fetch
   (`estado.grupos` carrega lazy dentro da mesma transação read-only,
   aceitável dado o volume baixo esperado).
7. **Literatura não aparecia mesmo com backend real.** Causa arquitetural:
   a página pública dependia de um `CmsPagina` slug `literatura` que
   nunca existiu no banco (não fazia parte do seed `V9`, e admin não
   pode criar página nova). **Simplificado**: `/literatura` agora lê
   direto da lista de arquivos (`GET /api/v1/arquivos`, endpoint público
   novo, mesma fonte que o admin já gerencia em `/admin/arquivos`) —
   removida toda a indireção via CMS/bloco pra essa página. Lista
   decorativa hardcoded (`publicacoes`) também foi removida a pedido do
   usuário — só mostra dado real agora, vazio até o admin subir PDFs.
8. **UX do admin: sem jeito de chegar no painel sem perder a sessão.**
   Sessão vive só em memória (decisão de anonimato já tomada) — digitar
   `/admin` na URL causa reload completo e perde o login. Corrigido com
   link "Painel Administrativo" em `/minha-conta` (navegação interna,
   sem reload) e no `Navbar` (visível só pra `role === 'ADMIN'`, no
   lugar de "Minha Conta"). Além disso, login como admin agora
   redireciona automaticamente pra `/admin` — admin nunca vê o dashboard
   de usuário comum.
9. **Editor de página institucional permitia adicionar/remover bloco.**
   Violava a regra de negócio confirmada ("layout fixo, só editar
   conteúdo já existente"). Removida a UI e os métodos de
   adicionar/remover/reordenar bloco do `PaginaEditor` — só resta editar
   texto/imagem dos blocos que já existem.
10. **Navegação quebrada entre páginas irmãs dentro do editor.** Bug real
    de Angular Router: `paginas/:slug` é rota parametrizada, então
    navegar de `paginas/sobre` pra `paginas/historia` reaproveita a
    mesma instância do componente (não recria) — como `slug` era lido
    uma vez só via `route.snapshot` no construtor, a página ficava
    travada mostrando sempre o primeiro slug carregado, dando a
    impressão de que só dava pra navegar entre seções diferentes do
    menu (que trocam de componente de verdade). Corrigido reagindo a
    `route.paramMap` (`takeUntilDestroyed`), recarregando o conteúdo
    toda vez que o slug muda.
11. **Pedidos hardcoded no dashboard do usuário.** Removidos a pedido do
    usuário — loja em standby, lista fica vazia em vez de mock fake.
12. **Erro de "reCAPTCHA Error" no console.** Investigado: ruído do
    script do Google, sem relação com o backend (que não tem verificação
    de reCAPTCHA implementada) nem com nenhum bug real.
13. **CONFLICT ao tentar recriar usuário já cadastrado.** Comportamento
    esperado (mensagem genérica anti-enumeração) — usuário já tinha uma
    conta de teste anterior no banco com o mesmo apelido/e-mail.

**Lição confirmada de novo nesta sessão**: `tsc --noEmit` sozinho não
pega tudo — vários desses bugs só apareceram testando de verdade no
navegador (não em `ng build` nem em teste unitário). Testar contra
ambiente real, clicando de verdade, continua sendo o que evita voltar
pro usuário com "funciona no meu ambiente".

> **Spec de UI aprovada pelo usuário em 2026-07-12** (`ux-ui`, 2 rodadas
> convergentes). Decisões finais: menu em 4 grupos por frequência de
> uso; 3 padrões de tela reutilizáveis (lista+formulário, drill-down
> hierárquico, índice de data); Relatos como fluxo de moderação
> (pendente/aprovado/rejeitado); Usuários v1 só lista+editar role, sem
> banir/deletar (pendente `security`); sem dashboard de métricas —
> entrada do admin redireciona pra "Relatos pendentes" se houver fila,
> senão Avisos; sem RBAC granular por área. Ordem de prioridade: (1)
> páginas institucionais → CMS, (2) Relatos, (3) Avisos+Mural, (4)
> Reuniões/Hotlines/Escritórios, (5) Reflexão/FAQ/Literatura/Usuários.
> Implementação iniciada — ver seções abaixo para detalhe de cada área.

> Gerado em 2026-07-12. Levantamento inicial do escopo admin foi
> incompleto (só cobriu CMS de página + relatos). Este documento é o
> escopo completo, informado direto pelo usuário, para orientar
> delegação a `ux-ui` → `backend` → `frontend`/`frontend-services`.

## Regra geral confirmada pelo usuário
- **Sem criar/excluir páginas novas, sem mudar layout.** Admin só edita
  conteúdo (texto/imagem) dentro do que já existe.
- Páginas puramente institucionais (ex: história, anonimato,
  privacidade, termos, sobre, programa de recuperação/unidade, jogo
  compulsivo, uma nova chance, vinte perguntas) — **só editar texto**.

## Áreas com gerenciamento de conteúdo (além de texto simples) — levantamento fechado em 2026-07-12

| Área | Necessidade | Estado atual |
|---|---|---|
| Relatos | Aprovar / editar / excluir | **Backend completo** (2026-07-12): entidade, migration (`V7`), repository, service, DTOs, `RelatoController` (público) + `RelatoAdminController` (moderação), testes unitários, contrato documentado em `backend/docs/api/contrato-relatos.md`. Falta só `frontend-services`/`frontend` trocar o mock pela chamada real |
| Reuniões | Gerenciar (CRUD) | **Backend completo** (2026-07-12): entidade, migration (`V12`), repository, service, DTOs, `ReuniaoController` (público) + `ReuniaoAdminController` (CRUD), testes unitários (controllers + service), contrato documentado em `backend/docs/api/contrato-reunioes.md`. Sem geocodificação automática (admin insere lat/long manualmente). Falta só `frontend-services`/`frontend` trocar o array hardcoded pela chamada real |
| Intergrupos / Escritórios Regionais | Gerenciar | **Backend completo** (2026-07-12): 4 entidades (`RegiaoIntergrupo`→`EstadoIntergrupo`→`GrupoIntergrupo` em hierarquia de 3 níveis com FK `ON DELETE CASCADE`, mais `EscritorioServico` como entidade plana separada), migration (`V14`), repositories, `EscritorioRegionalService` (CRUD completo por nível), `EscritorioRegionalController` (público) + `EscritorioRegionalAdminController` (13 endpoints: 1 leitura + 4 por nível × 3 níveis + 3 de escritório de serviço), testes unitários (service + 2 controllers), contrato documentado em `backend/docs/api/contrato-escritorios-regionais.md`. Falta só `frontend-services`/`frontend` trocar o mock pela chamada real |
| Mural de Apoio | Adicionar/remover imagens | **Backend completo** (2026-07-12): entidade (`BannerMural`, toggle `ativo`), migration (`V11`), repository, service (reusa upload de imagem existente, resolve `imagemId → URL pública`), `MuralDeApoioController` (público) + `MuralDeApoioAdminController` (CRUD), testes unitários (controllers + service), contrato documentado em `backend/docs/api/contrato-mural-de-apoio.md`. Falta só `frontend-services`/`frontend` trocar o mock pela chamada real |
| Reflexão do Dia | Gerenciar (conteúdo diário) | **Backend completo** (2026-07-12): entidade `ReflexaoDia` (chave única `mes`+`dia`, sem ano — recorrente todo ano), migration (`V15`), repository, service, DTOs, `ReflexaoDiaController` (público: hoje + por data) + `ReflexaoDiaAdminController` (lista mês inteiro + upsert por dia), testes unitários (service + 2 controllers), `SecurityConfig` liberado pro GET público, contrato documentado em `backend/docs/api/contrato-reflexao-do-dia.md`. Falta só `frontend-services`/`frontend` trocar o mock pela chamada real |
| Literatura | Gerenciar quais livros aparecem | Usa CMS existente (`CmsPagina`/`CmsBloco` tipo arquivo) + `arquivo` (upload/remoção). **Edição de metadados fechada em 2026-07-12** (`backend`): `PUT /api/v1/admin/arquivos/{id}` atualiza `titulo`/`descricao` sem tocar no binário, testes unitários, contrato em `backend/docs/api/contrato-arquivos.md`. Falta só `frontend-services`/`frontend` trocar o mock de metadados pela chamada real |
| Avisos | Gerenciar (criar/editar/remover, controla o que aparece na Home) | **Backend completo** (2026-07-12): entidade, migration (`V10`), repository, service, DTOs, `AvisoController` (público, lista ativos) + `AvisoAdminController` (CRUD completo), testes unitários (controllers + service), contrato documentado em `backend/docs/api/contrato-avisos.md`. Falta só `frontend-services`/`frontend` trocar o mock pela chamada real |
| Linhas de Ajuda / Hotlines | CRUD completo: múltiplos telefones por região, criar região nova, excluir telefone/região (confirmado 2026-07-12) | **Backend completo** (2026-07-12): entidades `HotlineRegiao`/`HotlineTelefone` (hierarquia 2 níveis, cascade na exclusão de região), migration (`V13`), repositories, service, DTOs, `HotlineController` (público) + `HotlineAdminController` (CRUD completo: criar/excluir região, adicionar/editar/excluir telefone), testes unitários (controllers + service), `SecurityConfig` liberado pro GET público, contrato documentado em `backend/docs/api/contrato-hotlines.md`. Falta só `frontend-services`/`frontend` trocar o mock pela chamada real |
| Perguntas e Respostas | Gerenciar (add/editar/remover) | **Backend completo** (2026-07-12): entidade `Faq` (sem hierarquia/status, com campo `ordem` para controle manual de sequência), migration (`V16`), repository, service, DTOs, `FaqController` (público) + `FaqAdminController` (CRUD completo), testes unitários (controllers + service), `SecurityConfig` liberado pro GET público, contrato documentado em `backend/docs/api/contrato-faq.md`. Falta só `frontend-services`/`frontend` criar o service (não existe hoje) e trocar o array hardcoded pela chamada real |
| Loja | **Fora de escopo por enquanto** | N/A |
| Usuários | Gerenciar — v1 conservadora confirmada: só listar + editar role (banir/deletar adiado, pendente `security`) | **Backend v1 completo** (2026-07-12): `GET /api/v1/admin/usuarios` (lista, sem expor senha/hash) + `PUT /api/v1/admin/usuarios/{id}/role`, `UsuarioAdminController`, regras de negócio no `UsuarioService` (bloqueia auto-rebaixamento e remoção do último admin do sistema), testes unitários, contrato em `backend/docs/api/contrato-usuario-admin.md`. Sem migration (reaproveita `usuario` de `V1`). Falta só `frontend-services`/`frontend` trocar o mock pela chamada real |
| Home | Selecionar quais itens gerenciáveis aparecem nela (ex: quais avisos, quais relatos, banner mural, reflexão) | Já consome Mural/Avisos/Relatos/Reflexão via services — hoje é "top N automático", falta noção de curadoria manual pelo admin |

**Confirmado**: única área com QUALQUER infraestrutura backend real hoje
é CMS genérico (páginas institucionais + literatura, via `cms`/`arquivo`/
`imagem`). Todo o resto (relatos, reuniões, escritórios/intergrupos,
mural, reflexão, avisos, hotlines, perguntas e respostas, usuários) é
**mock frontend puro, sem nenhum backend**. Migrations hoje: `V1` usuário,
`V2` password reset, `V3` imagem, `V4` CMS, `V5` arquivo, `V6` bloco
arquivo — nada de domínio novo.

## Próximos passos
1. ~~Levantamento técnico completo~~ — **feito** (backend confirmado
   zero pra 9 das 11 áreas).
2. `ux-ui` refaz a especificação do admin com escopo completo: edição de
   texto/imagem em páginas institucionais existentes (sem criar/excluir/
   layout) + moderação de relatos + CRUD de reuniões/escritórios/mural/
   reflexão/literatura/avisos/hotlines/perguntas + gestão de usuário +
   curadoria de Home.
3. `backend` planeja módulos novos (praticamente 1 por área: relato,
   reuniao, escritorio/intergrupo, mural, reflexao, aviso, hotline,
   pergunta-resposta, usuario-admin, home-curadoria) — decidir com
   `system-design` se algumas dessas áreas cabem no próprio CMS genérico
   (bloco tipado) em vez de entidade nova, dado o volume baixo do
   projeto.
4. `frontend-services` + `frontend` implementam por área, depois que
   back e UI de cada uma estiverem fechados.

## Observação
Escopo bem maior que o desenho inicial. Fatiar em entregas por área em
vez de tentar uma PR única — cada área acima é praticamente um mini-CRUD
independente.

## Decisão de arquitetura — `system-design`, 2026-07-12
Nenhuma das 9 áreas reaproveita `CmsBloco` genérico. Todas viram
entidade de domínio própria (tabela/migration/repository dedicados),
porque cada uma tem campo estruturado, status de moderação, busca por
chave própria (mês/dia, estado) ou hierarquia que o modelo bloco
genérico (`pagina_id` + `ordem` + `conteudo TEXT` livre, sem status)
não atende sem virar parsing de texto/JSON contorcido.
- **CMS genérico continua só pra**: páginas institucionais fixas
  (texto/imagem) e literatura (via `arquivo`) — uso já existente.
- **Entidade própria pra todas as 9**: Relato (com status
  pending/approved/rejected), Reuniao (campos geo tipados),
  Escritorio/Intergrupo (hierarquia região→estado→grupo), Banner do
  Mural (com toggle ativo), ReflexaoDia (chave única mes+dia), Aviso
  (fixado + expiração), Hotline (agrupado por estado), Faq (lista
  simples, mas ainda entidade própria — custo de tabela nova é trivial
  vs. acoplamento forçado ao CMS).
- **Usuários**: não é conteúdo novo, é extensão de endpoints admin sobre
  a entidade `Usuario` já existente (`GET /admin/usuarios` + ações a
  definir). `security` precisa revisar antes de qualquer ação
  destrutiva (deletar usuário com relatos associados etc).
- Proporcional ao projeto: tudo em Postgres único, sem NoSQL/sharding —
  são várias tabelas relacionais simples, não infra nova.

## Escopo adicional — área do usuário (não-admin), confirmado 2026-07-12

Além do dashboard admin, `minha-conta` (usuário comum) tem partes que
pareciam prontas mas são só stub — confirmado lendo
`frontend/src/app/pages/minha-conta/minha-conta.ts`/`.html`:

- **Aba "Detalhes"**: **fechada em 2026-07-13** (`backend` + `frontend`).
  Backend: `PUT /api/v1/usuarios/me` (edita `apelido`/`email`, sem exigir
  senha atual — baixo risco) e `PUT /api/v1/usuarios/me/senha` (exige
  `senhaAtual`, valida via `PasswordEncoder.matches` mesmo mecanismo do
  login), ambos em `UsuarioController` (`@AuthenticationPrincipal`, sem
  role especial), regras em `UsuarioService#atualizarConta`/
  `#alterarSenha` (unicidade de apelido/e-mail reaproveitando
  `existsByApelidoIgnoreCase`/`existsByEmailIgnoreCase`, excluindo o
  próprio id da checagem). Testes unitários em
  `backend/src/test/java/br/com/jogadoresanonimos/usuario/`, contrato em
  `backend/docs/api/contrato-usuario-conta-propria.md`. **Decisão de
  anonimato confirmada**: `Relato.nomeExibicao` é coluna própria sem
  vínculo dinâmico com `Usuario.apelido` — trocar o apelido da conta não
  altera relatos já publicados (já era assim antes desta tarefa, não foi
  preciso mudar nada no domínio de relato). Sem migration (reaproveita
  `usuario` de `V1`). Frontend: `Auth.atualizarConta`/`Auth.alterarSenha`
  (`frontend/src/app/services/auth.ts`), `onSalvarDetalhes()` em
  `minha-conta.ts` agora salva apelido/e-mail sempre e só troca a senha
  se os 3 campos de senha forem preenchidos; após troca de senha bem
  sucedida o usuário é deslogado (token antigo já fica inválido no
  backend via `senhaAlteradaEm`, ver `JwtService`). `tsc --noEmit` e
  `ng build --configuration development` limpos.
- **Aba "Downloads"**: hoje mostra literatura pública genérica (mesmo
  conteúdo da página `/literatura`, sem vínculo com usuário). **Definição
  do usuário**: literatura ganha botão "Adicionar" (visível só logado);
  usuário salva PDF na própria biblioteca; aba Downloads passa a listar
  só os PDFs que ele salvou, com opção de **excluir da lista pessoal**
  (não apaga o arquivo do storage/CMS, só a associação) ou baixar.
  Precisa: tabela de associação usuário↔arquivo (many-to-many), endpoints
  `POST`/`DELETE` nessa associação, listagem filtrada por usuário logado.
  **Backend completo (2026-07-12)**: migration `V17__cria_tabela_usuario_arquivo.sql`
  (tabela `usuario_arquivo`, PK composta `(usuario_id, arquivo_id)`,
  `ON DELETE CASCADE` em ambas FKs, índice `(usuario_id, criado_em DESC)`),
  pacote `br.com.jogadoresanonimos.usuario.biblioteca` (entity
  `UsuarioArquivo`/`UsuarioArquivoId`, `UsuarioArquivoRepository`,
  `BibliotecaPessoalService`, `BibliotecaPessoalController`) com
  `GET/POST/DELETE /api/v1/usuarios/me/biblioteca...`, reaproveitando
  `ArquivoResponse` já existente (sem DTO novo). Isolamento entre
  usuários garantido estruturalmente: usuário sempre vem do JWT
  (`@AuthenticationPrincipal`), nenhum endpoint aceita id de usuário
  arbitrário via path/query. `POST` idempotente (não duplica, não
  falha se já existe); `DELETE` idempotente (200 mesmo se associação já
  não existir). Testes unitários (controller + service) em
  `backend/src/test/java/br/com/jogadoresanonimos/usuario/biblioteca/`,
  contrato documentado em
  `backend/docs/api/contrato-biblioteca-pessoal.md`. Sem alteração em
  `SecurityConfig` — endpoint já cai no `anyRequest().authenticated()`
  padrão (usuário comum, não precisa de role ADMIN). Falta só
  `frontend-services`/`frontend`: botão "Adicionar à minha biblioteca" em
  `/literatura` e troca da fonte de dado da aba Downloads em
  `minha-conta`.
- **Aba "Endereços"**: hoje travada (`disabled`, "recurso ainda não
  disponível"). **Decisão do usuário: adiar junto com a loja** — não
  mexer agora.
- **Aba "Pedidos"**: mock, confirmado que fica parado enquanto loja
  estiver em standby — nenhuma ação aqui.

## Pendência técnica — edição de arquivo (literatura) — fechada em 2026-07-12 (`backend`)

`ArquivoController`/`ArquivoService` tinham só `upload` (POST) e
`remover` (DELETE) — sem endpoint de editar título/descrição de um
arquivo já enviado. Implementado `PUT /api/v1/admin/arquivos/{id}`
(atualiza `titulo`/`descricao` via `Arquivo#editarMetadados`, sem tocar
no binário/storage/`chaveArquivo`), `@PreAuthorize("hasRole('ADMIN')")`
mesmo padrão dos outros dois endpoints. Testes unitários e contrato em
`backend/docs/api/contrato-arquivos.md`. Sem migration (colunas já
existem desde `V5`).

## Páginas institucionais — backend fechado em 2026-07-12 (`backend`)

Suposição original de que "o resto é só editar texto via CMS" estava
errada — nenhuma das 13 páginas informacionais usava `CmsPagina`/
`CmsBloco`, todas eram HTML ou array hardcoded direto no componente
Angular. **Status atual: as 13 já têm registro `CmsPagina` no banco.**

- `vinte-perguntas`: módulo dedicado (`vinteperguntas`, `V8`), já tinha
  sido fechado numa sessão anterior — tem lógica de quiz acoplada ao
  array de perguntas, por isso não usa o CMS genérico.
- As outras 12 — sobre, historia, anonimato, privacidade, termos,
  jogo-compulsivo, uma-nova-chance, programa-de-recuperacao (12 passos),
  programa-de-unidade (12 princípios), perguntas-e-respostas, hotlines,
  contribuir — tiveram o conteúdo migrado nesta sessão via
  `V9__seed_paginas_institucionais_cms.sql`, copiando o texto
  litearalmente do HTML/array atual do frontend (nenhum texto novo foi
  escrito). **Ressalva**: `perguntas-e-respostas` e `hotlines` não
  ganharam `CmsPagina` — as duas páginas são 100% lista estruturada
  (FAQ / accordion por estado), sem nenhum texto introdutório separado
  da lista; não havia o que migrar para bloco de texto. Essas duas
  seguem como pendência de CRUD estruturado próprio (linha "Linhas de
  Ajuda / Hotlines" e "Perguntas e Respostas" na tabela do topo deste
  documento), não como página CMS.
- `CmsAdminController`/`CmsPublicController` já cobrem as 10 páginas
  novas sem nenhuma alteração de código — os endpoints são genéricos por
  `slug`, sem whitelist. Confirmado lendo o controller e rodando
  `./mvnw compile` (sucesso) e a migration de ponta a ponta contra um
  Postgres real (container descartável), validando que `V9` aplica sem
  erro e os 10 registros + blocos ficam com o conteúdo esperado.
- Modelo de bloco: um bloco de texto por parágrafo/heading/item de lista
  numerada, todos usando só as tags que o `HtmlSanitizer` do CMS permite
  (`b`, `i`, `p`, `br`, `a`) — o conteúdo original tinha `<h2>`,
  `<strong>`, `<ol>`/`<li>`, que não estão na allowlist. Migration já
  nasce sem essas tags (heading virou bloco de texto simples) para não
  sofrer strip silencioso na primeira vez que o admin salvar a página
  pela UI.
- `atualizado_por`/`alterado_por` exigem `usuario.id` válido (FK NOT
  NULL), mas o admin real só existe em runtime via `AdminBootstrap`
  (roda depois do Flyway). A migration cria um usuário de sistema com
  UUID fixo (`sistema.seed.cms`, role `ADMIN`, sem hash de senha válido
  — nunca loga) só para ancorar a autoria do seed.
- CTAs/UI chrome de cada página (botões, modal de "você é membro?" em
  `contribuir`, accordion de `hotlines`, lógica de quiz de
  `vinte-perguntas`) **não** foram para o CMS — são fluxo/UI do Angular,
  fora de escopo desta tarefa (que era só migrar texto). Isso é trabalho
  do agente `frontend`: trocar o componente de "HTML fixo" para
  "renderiza blocos vindos de `GET /api/v1/cms/paginas/{slug}`".

**Trabalho restante pra essas 12 (+1 vinte-perguntas) virarem editáveis
na prática pelo admin**: só falta o lado `frontend` trocar cada
componente Angular de "HTML fixo" pra "renderiza blocos vindos da API"
— backend e dado já estão prontos.
Atenção especial a `vinte-perguntas`: tem lógica de quiz acoplada ao
array de perguntas, cuidado pra separar "texto editável" de "mecânica
do quiz" ao migrar.

## Home — confirmado 2026-07-12: já pronta pra dado real

`home.ts` já consome `MuralDeApoioService`, `AvisosService` e
`RelatosService` via chamada real com switch `environment.useMocks` —
quando os backends dessas áreas ficarem reais, a home não precisa de
trabalho extra, só desligar o mock. Hoje mostra automaticamente os
últimos N itens (sem curadoria manual do admin) — curadoria manual
(escolher especificamente o que aparece, não só "mais recentes") seria
incremento futuro, não bloqueio.

## Próximos passos (atualizado)
1. ~~Levantamento técnico~~ — feito.
2. ~~Decisão de arquitetura de dados~~ — feito (`system-design`, acima).
3. ~~`ux-ui` refaz a especificação do admin~~ — feito (2026-07-12, ver
   topo do documento).
4. ~~`frontend` constrói a casca de navegação do dashboard~~ — feito
   (2026-07-12, ver seção abaixo).
5. `backend` implementa os módulos novos (pode paralelizar por área,
   já que são entidades independentes) — sujeito a `security` revisar
   parte de usuários.
6. `frontend-services` + `frontend` implementam o conteúdo real de cada
   área (CRUD/moderação) depois que back e UI de cada uma estiverem
   fechados — a casca criada no passo 4 já tem o placeholder e a rota
   prontos pra cada uma receber esse trabalho sem mexer em navegação.

## Shell de navegação do admin — implementado 2026-07-12 (`frontend`)

Construída só a casca (nav lateral + roteamento), sem lógica de CRUD —
cada área troca só o componente-placeholder pelo componente real quando o
backend correspondente ficar pronto.

**Rotas** (`frontend/src/app/app.routes.ts`):
- `/admin` — shell (`AdminLayout`), protegido por `adminGuard`
  (`frontend/src/app/guards/admin.guard.ts`), com rotas filhas lazy.
- `/admin` (index, vazio) redireciona pra `/admin/avisos` — **provisório**,
  comentado no código: quando o módulo de Relatos existir, trocar por
  lógica condicional (checar fila pendente e ir pra
  `relatos?status=pending` só se houver algo, senão cair em Avisos).
- Rotas filhas usam path plano (`avisos`, `relatos`, `reunioes`,
  `escritorios-regionais`, `hotlines`, `perguntas-e-respostas`,
  `literatura`, `usuarios`, `mural-de-apoio`, `reflexao-do-dia`) e path
  prefixado `paginas/*` pras 11 páginas institucionais que não têm CRUD
  de dado estruturado próprio (sobre, historia, anonimato,
  jogo-compulsivo, uma-nova-chance, programa-de-recuperacao,
  programa-de-unidade, vinte-perguntas, privacidade, termos, contribuir).
  **Decisão**: hotlines e perguntas-e-respostas ficaram só no grupo
  "Estrutura de apoio" (path plano, sem prefixo `paginas/`), não
  duplicadas em "Páginas do site" — evita dois itens de menu apontando
  pra áreas conceitualmente sobrepostas (a spec permitia essa fusão a
  critério do frontend).
- Todas as 21 rotas filhas hoje carregam o mesmo componente placeholder
  `AdminEmConstrucao` (`frontend/src/app/pages/admin/admin-em-construcao/`),
  parametrizado via `route.data.titulo` — trocar por `loadComponent` real
  área por área não exige tocar em mais nada além da rota daquela área.

**Guard** (`frontend/src/app/guards/admin.guard.ts`):
- `CanActivateFn` funcional, reusa `Auth.usuarioAtual()` (signal
  síncrono, sem `localStorage`/rehydration — coerente com a sessão hoje
  vivendo só em memória por requisito de anonimato).
- Sem usuário → redireciona pra `/minha-conta` (onde vive o formulário de
  login hoje; não existe rota `/login` dedicada no projeto).
- Logado sem role `ADMIN` → redireciona silenciosamente pra `/` (sem
  mensagem de erro).
- Comentário no código deixa explícito que isso é só UX — permissão real
  é 100% `@PreAuthorize` no backend.

**Componente shell** (`frontend/src/app/pages/admin/admin-layout/`):
- `AdminLayout` — nav lateral fixa em desktop (`position: sticky`,
  colapsa em accordion com botão toggle abaixo de 900px) + `router-outlet`
  pro conteúdo da área selecionada.
- Menu montado a partir de um array `grupos` no componente (não
  hardcoded no template), replicando os 4 grupos da spec aprovada
  (Conteúdo do dia a dia / Páginas do site / Estrutura de apoio / Conta e
  biblioteca).
- Reaproveita só os tokens já existentes em `frontend/src/styles.css`
  (`--cor-padrao-ja`, `--color-bg-section`, `--spacing-*`, etc) — nenhum
  token novo foi necessário.
- Navbar/footer/botões flutuantes públicos continuam visíveis (são
  globais em `app.html`, fora do `router-outlet`) — não foi pedido pra
  esconder no escopo desta tarefa; se o admin precisar de um shell "sem
  navbar pública" no futuro, é uma decisão de UX a confirmar com
  `ux-ui`/usuário antes de implementar.

**Não incluído nesta entrega** (fora de escopo, conforme pedido): telas
de conteúdo/CRUD de cada área, dashboard de métricas (explicitamente
fora de escopo pela spec), lógica real de detecção de fila de relatos
pendentes.

## Frontend-services — services reais criados/evoluídos em 2026-07-12

Todos os 11 módulos do backend agora têm service Angular correspondente
(`frontend/src/app/services/`), com interface fiel ao contrato, mock
próprio e leitura de `environment.useMocks`:

- **Novos** (não existia service antes): `reuniao`, `hotline`,
  `escritorio-regional`, `faq`, `usuario-admin`, `biblioteca-pessoal`,
  `pagina.model` (tipo `Pagina<T>` genérico de paginação), e
  `vinte-perguntas` (criado à parte, na tarefa de migração de páginas).
- **Evoluídos in-place** (nome de classe/arquivo e assinatura pública
  mantidos, para não quebrar componentes que já os consomem):
  `relatos.ts` (+ `relato.model.ts`/`.mock.ts`, agora fala o contrato
  real internamente e ganhou os 6 métodos de moderação admin),
  `avisos.ts` (+ CRUD admin), `mural-de-apoio.ts` (+ CRUD admin),
  `reflexao-do-dia.ts` (+ `listarMesAdmin`/`salvarReflexaoAdmin` —
  **atenção**: mudou de comportamento, dias sem reflexão cadastrada
  agora retornam `success:false` em vez de placeholder gerado, pra
  replicar o 404 real do backend), `arquivo.ts` (+ `editarArquivo`).
- Mock do `escritorio-regional` tem só 2-4 grupos por estado (não os
  ~80 originais) — avisar se precisar de volume igual ao antigo pra
  testar UI de lista longa.

**Gap fechado em 2026-07-13 (`security`)**: interceptor HTTP funcional
(`HttpInterceptorFn`) criado em
`frontend/src/app/interceptors/auth.interceptor.ts`, anexando
`Authorization: Bearer <token>` em toda chamada pra
`environment.apiBaseUrl`. Token guardado só em memória no `Auth`
service (campo privado `tokenAtual`, exposto via `obterToken()`),
consistente com a decisão já tomada no `adminGuard` de não persistir
sessão em `localStorage`. Em `401` de chamada da própria API, o
interceptor chama `Auth.logout()` e deixa o erro seguir pro
`catchError` de cada tela (sem redirect forçado). Registrado em
`frontend/src/app/app.config.ts` via
`provideHttpClient(withInterceptors([authInterceptor]))`. Detalhe
completo da decisão em `.claude/knowledge/security.md`. `npx tsc
--noEmit` limpo.

`tsc --noEmit` e `ng build` (dev) rodaram limpos.

## Páginas institucionais — frontend fechado em 2026-07-12 (`frontend`)

As 12 páginas com CMS genérico + `vinte-perguntas` foram trocadas de
"HTML/array fixo" pra "consome `cms.getPaginaPublica(slug)`" (mesmo
padrão de `literatura.ts`). `vinte-perguntas` ganhou service próprio
(`vinte-perguntas.ts`) só pro texto das 20 perguntas — lógica de quiz
intacta. `hotlines`/`perguntas-e-respostas` não precisaram de mudança
aqui (confirmado: sem texto introdutório, são só lista estruturada,
tratadas junto com o CRUD de cada uma). `ng build` limpo.

**Regressão visual — fechada em 2026-07-13 (`backend`)**: decisão foi
ampliar a allowlist do `HtmlSanitizer` (não criar tipo de bloco novo no
CMS). Risco avaliado como equivalente ao já aceito: só ADMIN autenticado
escreve conteúdo de CMS (mesma superfície de confiança de `b/i/p/br/a`
hoje), e nenhuma tag nova carrega atributo (sem `href`/`style`/`script`),
então não abre vetor de XSS/CSS injection novo — só ganha fidelidade
visual.

- **Allowlist final** (`backend/src/main/java/br/com/jogadoresanonimos/cms/util/HtmlSanitizer.java`):
  `b`, `i`, `p`, `br`, `a` (já existiam) + `h2`, `h3`, `strong`, `ul`,
  `ol`, `li` (novas). Nenhuma tag foi excluída da proposta original —
  todas de formatação pura, sem atributo permitido além de `href` em
  `a` (já sanitizado por allowlist de protocolo `http`/`https`).
- **Migration nova** (não editar `V9`, já aplicada):
  `V18__restaura_markup_rico_paginas_institucionais.sql` — `UPDATE`/
  `DELETE` (mantendo `INSERT` original intocado) nos blocos das 4
  páginas afetadas, restaurando o HTML original recuperado do histórico
  git (`git show 3fe955f:frontend/.../*.html`, mesmo texto literal, só
  tag trocada — nenhum texto novo foi escrito):
  - `sobre`: blocos 2 e 6 viram `<h2>` ("O único requisito" / "Sobre a
    doença do jogo").
  - `historia`: bloco 3 (nota "data a confirmar") ganha `<strong>`
    (equivalente semântico ao destaque de classe CSS que existia no
    Angular original, já que classe CSS não é permitida na allowlist).
  - `programa-de-recuperacao`/`programa-de-unidade`: os 12 passos/
    princípios (antes um `<p>` solto por item) viram um único bloco
    `<ol><li><p><strong>N.</strong> texto</p></li></ol>`, replicando a
    estrutura `ol.passos-lista > li.passo-item > p > strong` do HTML
    original — blocos de item individuais (ordem 4-14 e 3-13) foram
    removidos via `DELETE` (o `UNIQUE(pagina_id, ordem)` tolera gaps,
    confirmado lendo `CmsPagina#blocos` que usa `@OrderBy("ordem ASC")`
    sem exigir contiguidade).
  - `contribuir`: bloco 3 vira `<h2>Formas de contribuição</h2>`, bloco
    4 vira `<ul><li><strong>Rótulo:</strong> texto</li></ul>` com os 3
    itens, blocos 5-6 (que antes eram os itens 2 e 3 em `<p>` solto)
    removidos via `DELETE`.
- CTA/UI chrome (`cta-final`, botão "Encontrar uma Reunião" etc)
  continuam fora do CMS — são fluxo Angular, não texto, mesma decisão já
  registrada na migração original.
- Validado: `./mvnw compile` limpo; migration aplicada de ponta a ponta
  contra Postgres real (container Docker descartável) via
  `flyway:migrate`, conteúdo final de todas as 4 páginas conferido linha
  a linha com `psql`; `./mvnw test` → 198 testes, 197 passando (só o
  `contextLoads` falha por falta de Postgres local em `localhost:5432`,
  mesmo erro pré-existente já documentado, não relacionado a esta
  mudança). 2 testes novos adicionados em `HtmlSanitizerTest` cobrindo
  as tags novas.
- Não afeta frontend: `[innerHTML]` já renderiza qualquer HTML vindo do
  banco, sem mudança de componente Angular necessária.

## Estado real agora (2026-07-12, fim de sessão)

✅ Backend: 100% do escopo (13 páginas institucionais, 8 entidades
novas, literatura editável, usuários v1, biblioteca pessoal).
✅ Frontend-services: 100% dos services criados/evoluídos.
✅ Frontend: 13 páginas institucionais consumindo CMS de verdade; shell
de navegação do admin (nav, guard, rotas, 21 placeholders).

## Telas de admin — TODAS fechadas em 2026-07-13 (`frontend`)

As 21 rotas do admin (`/admin/avisos`, `/admin/relatos`,
`/admin/reunioes`, `/admin/hotlines`, `/admin/escritorios-regionais`,
`/admin/perguntas-e-respostas`, `/admin/literatura`, `/admin/usuarios`,
`/admin/mural-de-apoio`, `/admin/reflexao-do-dia`, `/admin/paginas` +
`/admin/paginas/:slug` dinâmica) foram todas implementadas e ligadas
— zero placeholder `AdminEmConstrucao` restante. Padrões usados
conforme spec `ux-ui`: lista+formulário (Avisos, Mural, FAQ, Literatura,
Usuários, Hotlines-telefone), drill-down hierárquico (Escritórios
Regionais 3 níveis, Hotlines região→telefone), calendário por data
(Reflexão do Dia), fluxo de moderação com abas de status (Relatos), e
editor de blocos com reorder por seta (páginas institucionais, incl.
caso especial vinte-perguntas com seção separada só-texto).

**Gaps reais encontrados e fechados durante essa rodada** (nenhum
sobrou pendente):
- Faltava `ImagemService` no frontend (upload/remoção de imagem) —
  criado, e depois plugado de verdade no Mural de Apoio e no editor de
  blocos de página (troca de aviso/TODO por upload real).
- `VintePerguntasService` não tinha métodos admin (`listarAdmin`/
  `atualizarPergunta`) — criados e plugados na seção "As 20 perguntas"
  do editor de página (antes só leitura, agora editável de verdade).
- Faltava `GET /api/v1/admin/arquivos` no backend (listagem de
  literatura pro admin) — endpoint novo criado (`ArquivoController`/
  `ArquivoService`/`ArquivoRepository`, sem migration, reaproveita
  coluna já existente), mais o método `listarArquivosAdmin()` no
  frontend e a tela de Literatura completa (edição de metadados +
  upload/exclusão, já que o service tinha esses métodos prontos).
- 2 bugs reais pegos só por `ng build` (não por `tsc --noEmit` isolado,
  que não faz checagem estrita de template Angular): `admin-escritorios-
  regionais` tinha um discriminated union (`ExclusaoAlvo`) que não
  estreita corretamente dentro de template Angular — corrigido com
  getters no componente (`alvoRegiao`/`alvoEstado`/`alvoGrupo`/
  `alvoEscritorio`) que fazem a narrowing em TS puro; `admin-hotlines`
  tentava um `subscribe()` único sobre union de dois Observables com
  shape de retorno diferente (editar telefone retorna telefone, criar
  retorna região) — corrigido separando em dois `if`/`subscribe`
  distintos. **Lição**: `tsc --noEmit` sozinho não é suficiente pra
  validar Angular com template externo — sempre rodar `ng build`
  antes de considerar uma tela pronta.

Validado: `ng build --configuration development` limpo (só warning
pré-existente não relacionado), `./mvnw test` 182 testes/181 passando
(mesmo erro pré-existente de contexto sem Postgres local).

## Biblioteca pessoal (literatura) — UI fechada em 2026-07-13 (`frontend`)

`/literatura` (`frontend/src/app/pages/literatura/literatura.ts`/`.html`):
cada PDF do bloco "Publicações em PDF" ganhou ação condicionada a
`Auth.usuarioAtual()` — deslogado não vê nada, logado vê "Adicionar à
minha biblioteca" (chama `BibliotecaPessoalService.adicionarABiblioteca`,
sem modal, ação de baixo risco) ou, se já salvo, um estado fixo "Já está
na minha biblioteca" com ícone de check (`--color-success`). Ao carregar
a página, se logado, chama `listarMinhaBiblioteca()` uma vez pra
pré-marcar quais `arquivoId` já estão salvos (`Set<string>`); um segundo
`Set` local (`idsSalvando`) evita duplo clique/corrida enquanto a
requisição está em voo.

`minha-conta` (`frontend/src/app/pages/minha-conta/minha-conta.ts`/`.html`),
aba Downloads: trocada a fonte de dado de `cms.getPaginaPublica('literatura')`
(conteúdo público genérico) para `BibliotecaPessoalService.listarMinhaBiblioteca()`
(só os PDFs que o próprio usuário salvou). Carregamento agora vive dentro
do `effect` que já observa `Auth.usuarioAtual()` — cobre tanto o caso de
entrar na página já logado quanto logar depois de já estar na página
(estado 1 → estado logado), e limpa a lista no logout. Cada item tem
"Baixar" (mesmo padrão de link direto) e "Remover da minha biblioteca"
(chama `removerDaBiblioteca`, com `title`/`aria-label` explícitos de que
o arquivo continua no site — só a associação pessoal é removida; sem
confirmação forte, ação reversível). Estado vazio linka de volta pra
`/literatura`. Removida a dependência de `Cms`/`CmsBlocoArquivo` neste
componente (não é mais usada na aba Downloads); `ngOnInit` ficou vazio e
foi removido junto com `implements OnInit`.

CSS novo usa só tokens já existentes (`--color-success`, `--color-danger`,
`--color-bg-section`, `--spacing-*`, `--radius-*`, `--cor-padrao-ja`) —
nenhuma variável nova precisou ser criada. `tsc --noEmit` e
`ng build --configuration development` rodaram limpos (único warning é
pré-existente em `reunioes.ts`, não relacionado a esta mudança).

## O que ainda falta (não é tela de admin, é integração/backend)

- ~~Aba "Detalhes" de `minha-conta` continua stub~~ — **fechada em
  2026-07-13** (`backend` + `frontend`), ver seção "Escopo adicional —
  área do usuário" acima.
- ~~Interceptor HTTP de `Authorization` ainda não existe~~ — **fechado em
  2026-07-13** (`security`), ver seção "Frontend-services" acima.
- ~~Decisão pendente sobre a regressão visual do CMS~~ — **fechada em
  2026-07-13** (`backend`), ver seção "Páginas institucionais —
  frontend fechado" acima: allowlist do `HtmlSanitizer` ampliada
  (`h2`/`h3`/`strong`/`ul`/`ol`/`li`) + migration `V18` restaurando o
  markup rico nas 4 páginas afetadas.
- Lógica real de detecção de fila de Relatos pendentes pro redirect de
  entrada do admin (hoje cai direto em Avisos, comentário no código já
  sinaliza isso como provisório).

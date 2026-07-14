# Decisões de Sistemas — Backend, Admin e Infraestrutura

> Documento de trabalho gerado em 2026-07-09 para revisão e aprovação.
> Nada aqui foi implementado ou registrado como regra de negócio permanente
> ainda — isso só acontece depois da aprovação, via agente `po`, que
> atualiza o `CLAUDE.md`.
>
> Elaborado com base em análise dos agentes `system-design`, `backend`,
> `security` e `ux-ui`.

## 1. Visão geral da arquitetura

- **Frontend**: Angular (já pronto, exceto dashboard administrativo) — SPA
  consumindo API REST do backend.
- **Backend**: Spring Boot 3.2+ (Java 21+), **monolito único** — volume de
  tráfego (~300 pedidos de ajuda/dia, ~2.500 requisições/dia) não
  justifica microsserviços.
- **Banco de dados**: PostgreSQL único para tudo (usuários, reuniões,
  avisos, reflexões, relatos, CMS de conteúdo). Não há necessidade de
  banco separado para imagens.
- **Armazenamento de imagem**: bucket S3-compatible externo gerenciado
  (Cloudflare R2 ou Backblaze B2) — só o binário fica lá; metadado
  (nome, tamanho, alt text, chave do arquivo) fica no Postgres.
- **Ambientes**: dev + prod (sem staging dedicado por ora).
- **Docker**: Compose orquestra backend + Postgres + frontend; bucket de
  imagem é serviço externo gerenciado, não entra no Compose.

## 2. Painel administrativo (CMS leve)

- **Todas as páginas institucionais são editáveis** pelo admin — decisão
  do usuário: como são poucas e estáveis, não vale a pena separar
  "hardcoded vs. editável". Simplifica o modelo: uma regra só.
- Modelo de dados: `cms_pagina` (slug, título) + `cms_bloco` (blocos
  ordenados dentro da página, tipo `texto` ou `imagem`).
- Recursos com modelo próprio, **fora do CMS genérico**: `reuniao`,
  `aviso`, `reflexao_diaria`, `relato` — mantêm tabela e regras próprias,
  não viram blocos de CMS.
- Painel é **tela separada** (lista de conteúdos → formulário), **não**
  edição inline sobre a página pública.
- Texto rico limitado a formatação básica (negrito/itálico/link) — nunca
  HTML livre (evita quebra de layout e XSS armazenado).
- **Sem versionamento/histórico completo** (sem "desfazer" com diff
  visual) — apenas auditoria simples: `atualizado_em` / `atualizado_por`
  (ou tabela de log com snapshot do valor anterior) nas entidades
  editáveis por admin.
- Fora de escopo por ora: editor de layout/drag-and-drop, workflow de
  aprovação multi-etapas.

## 3. Roles de usuário

- Campo `role` simples na tabela de usuário: `PARTICIPANTE` | `ADMIN`
  (enum via `VARCHAR` + `CHECK`, sem tabela de permissões granulares).
- Atribuição/revogação de admin é **manual** (migration/script direto no
  banco), não vira tela de produto — operação rara e de alto impacto;
  reavaliar só se o número de admins crescer bastante.
- Bootstrap do primeiro admin via variável de ambiente/secret, com senha
  forçando troca no primeiro login.

## 4. Autenticação

- Login: **apelido + senha** (Argon2id/bcrypt, mínimo 10 caracteres sem
  regra de composição forçada).
- **E-mail passa a ser coletado no cadastro** — decisão do usuário,
  exceção deliberada à regra geral de anonimato do projeto. Motivos:
  1. Permitir recuperação de conta por e-mail (fluxo padrão de "esqueci
     minha senha"), em vez de depender só de uma chave de recuperação
     exibida uma única vez.
  2. Necessário para envio de itens físicos da loja (endereço/contato
     de entrega depende de um canal de contato).
  - **Pendência para a próxima sessão**: o fluxo de frontend para captura
    de e-mail no cadastro ainda não foi desenhado — a ser tratado pelo
    agente `ux-ui`/`frontend` depois.
  - **Atenção de arquitetura**: e-mail é dado pessoal e não pode ser
    exposto/logado junto com dados de participação, relatos ou presença
    em reuniões (mesmo requisito de isolamento que já vale para os
    demais dados sensíveis). Isso precisa ser modelado com cuidado (ex:
    e-mail em tabela separada, acesso restrito, nunca em log) — a ser
    detalhado com `security`/`backend` quando o fluxo for desenhado.
  - Esta exceção precisa ser formalizada no `CLAUDE.md` pelo `po` quando
    aprovada, já que hoje o documento diz que o anonimato tem prioridade
    sobre qualquer prática técnica comum — este é o primeiro caso em que
    o próprio usuário optou por abrir mão de parte disso conscientemente.
- Admin: **MFA via TOTP** (app autenticador) + códigos de backup —
  **planejado, mas fica para depois do CRUD básico**, quando o dashboard
  já estiver em teste.
- JWT carrega `role` como claim assinado, revalidado a cada requisição.
  Autorização sempre checada no backend (`@PreAuthorize`), nunca inferida
  pela rota do frontend.
- Rate limiting e bloqueio de tentativas de login por conta/sessão, nunca
  por IP. Mensagens de erro de login genéricas (evita enumeration).

## 5. Storage de imagem

- **Decisão revisada (2026-07-12): disco local persistente do servidor de
  produção**, não bucket S3-compatible externo. Reverte a recomendação
  anterior deste documento (R2/B2).
- **Motivo da mudança**: usar bucket gerenciado de terceiro (R2/B2) exige
  decidir *titularidade da conta* — fica no nome do cliente (JA Brasil) ou
  do desenvolvedor responsável pela infra? Essa é uma decisão de negócio
  (custódia de credencial, continuidade se a relação com o dev terminar,
  quem paga/renova) que o usuário optou por **não precisar tomar agora**.
  Disco local do próprio servidor de produção evita essa decisão sem
  bloquear o desenvolvimento: o binário mora onde a aplicação já roda, sob
  a mesma titularidade de infra que o resto do sistema.
- **O que não muda**: `StorageClient` (interface) já isola essa decisão do
  resto do código — a implementação de disco (antes `StubStorageClient`,
  usada só em dev/teste, agora promovida a `LocalDiskStorageClient` e
  usada também em produção) sem necessidade de nova implementação nem de
  tocar em código de domínio (`imagem` service/controller permanecem
  iguais, só a config de ambiente aponta o path de disco de prod, via
  env vars já existentes `STORAGE_STUB_DIR`/`STORAGE_PUBLIC_BASE_URL` —
  nomes mantidos por decisão do `backend`, não valia o custo de
  sincronizar rename em 3 arquivos de config). Toda a validação de
  segurança já pronta continua exatamente como está: remoção de EXIF
  antes de persistir, validação por magic bytes (não extensão/
  `Content-Type`), sem aceitar SVG, sem upload por URL, checagem
  anti-polyglot em PDF, GC de imagem órfã (`@Scheduled` diário, período
  de graça de 7 dias), rate limit de upload (10/min por admin + 60/min
  global).

### Trade-off 1 — Risco de crescimento de disco sem controle
- **Risco real**: sem bucket gerenciado, não há quota nativa do provedor
  nem alerta de billing automático (o mecanismo que cobria isso na decisão
  anterior, "free tier ~10GB + alerta em 80%", deixa de existir). Disco
  cheio no servidor de produção é um risco mais sério que estourar quota
  de bucket: pode afetar o próprio sistema operacional/aplicação, não só
  a feature de upload.
- **Mitigação decidida**:
  1. **Quota de aplicação**: manter o limite de 5MB por arquivo já
     definido; adicionar um limite agregado (ex: soma de tamanho de
     upload por período, monitorado por consulta simples ao Postgres —
     `SUM(tamanho_bytes)` na tabela de metadado de imagem/PDF — não
     precisa de mecanismo novo, é query direta).
  2. **Alerta de disco**: monitoramento de espaço em disco do servidor
     via `devops` (ex: alerta simples de infraestrutura ao atingir 70-80%
     de uso), já que não existe mais o alerta de billing do provedor
     cloud cobrindo esse papel.
  3. **Política de retenção/expurgo**: o GC de órfã (7 dias de graça) já
     ataca o caso "upload sem registro associado". Falta decidir, quando
     o volume justificar, uma política de retenção para imagens
     *associadas* a conteúdo removido (ex: se um relato ou página CMS é
     excluído, o quê acontece com a imagem referenciada) — não é urgente
     no volume atual (~300 pedidos/dia), mas fica registrado como
     pendência a fechar com `backend`/`po` antes de crescer.
- **Por que essa mitigação e não mais**: no volume atual, estimativa
  grosseira de crescimento de disco é baixíssima (poucas dezenas de MB/dia
  mesmo em pico de uploads) — não justifica hoje um mecanismo automatizado
  de quota "dura" (bloquear upload ao atingir X GB) além do alerta
  observável por `devops`. Adicionar isso agora seria resolver um
  problema que não existe de forma concreta ainda.

### Trade-off 2 — Backup do diretório de storage
- **Situação anterior**: com R2/B2, durabilidade e backup vinham
  embutidos no provedor (multi-AZ, replicação) — não era preciso pensar
  nisso.
- **Com disco local, isso deixa de existir por padrão** — é a principal
  perda real da mudança de decisão, e precisa de ação explícita, não pode
  ficar implícito.
- **Decisão**: o diretório de storage de imagem/PDF em produção **precisa
  entrar na rotina de backup do servidor** junto com o dump do Postgres
  (mesma cadência/retenção já definida ou a definir para o banco, a
  fechar com `devops`) — sem isso, perda de disco = perda permanente de
  todo binário enviado por usuários (fotos de perfil, PDFs, etc.), sem
  possibilidade de recuperação. Este ponto é bloqueante para produção: não
  faz sentido ativar `LocalDiskStorageClient` em produção sem essa
  rotina de backup existindo, mesmo que o volume seja baixo — a perda é
  total e irreversível, o risco não é de escala, é de ausência de
  mecanismo.
- **Escopo do backup**: cópia periódica do diretório de storage
  (snapshot/rsync/tarball, o mecanismo exato é decisão de `devops`) com
  retenção equivalente à do banco — o metadado (nome, tamanho, chave do
  arquivo) já vive no Postgres e por si só não recupera o binário; os
  dois precisam ser restauráveis em conjunto e de forma consistente
  (backup do Postgres e do diretório não podem ficar defasados um do
  outro a ponto de o metadado apontar para arquivo que não existe mais no
  backup, ou vice-versa).

### Trade-off 3 — Escala horizontal futura (múltiplas instâncias de app)
- **Limitação inerente à decisão atual**: disco local é local *daquela
  instância* do servidor de aplicação. Se um dia o backend rodar em mais
  de uma instância simultânea (ex: para alta disponibilidade ou para
  absorver mais tráfego), as instâncias **não compartilham o mesmo
  disco** por padrão — uma instância grava um upload, a outra não o
  enxerga, quebrando a consistência de leitura (usuário sobe uma foto de
  perfil, é servido por uma instância diferente na próxima requisição, e
  a imagem "não existe" para aquela instância).
- **Decisão para agora**: **não implementar nada preventivamente para
  esse cenário.** Com uma única instância de aplicação (cenário atual e
  aguardando qualquer indício concreto de necessidade de mais de uma), o
  disco local é suficiente e mais simples — replicar disco entre
  instâncias (ex: volume de rede compartilhado, NFS) ou voltar a um
  storage compartilhado (S3-compatible) só se justifica quando a segunda
  instância existir de fato.
- **Gatilho explícito que forçaria migrar para storage compartilhado**:
  o dia em que `devops` precisar subir mais de uma instância do backend
  rodando simultaneamente atrás do load balancer (seja por
  disponibilidade, seja porque o volume de tráfego cresceu ordens de
  grandeza a ponto de justificar), a decisão de storage de imagem
  **precisa ser revisitada antes** desse deploy, não depois — nesse
  momento sim volta à mesa a escolha entre (a) storage compartilhado tipo
  S3-compatible (R2/B2, retomando a decisão original deste documento) ou
  (b) volume de rede compartilhado entre as instâncias. A interface
  `StorageClient` já existente é o que torna essa futura migração
  barata: troca-se a implementação, não o contrato usado pelo resto do
  código.

### Trade-off 4 — Outras considerações relevantes nesse volume
- **Performance de I/O**: disco local elimina a latência de rede de um
  bucket externo (upload/download do binário é I/O local) — ganho
  pequeno mas real nesse volume, sem custo adicional.
- **Segurança de path**: como o disco agora é compartilhado com o próprio
  sistema operacional/aplicação (diferente de um bucket isolado), reforça
  a importância de já ter (e manter) chave de arquivo gerada por UUID, não
  por nome original do upload — evita path traversal e colisão de nome;
  isso já era prática do projeto e continua sendo suficiente, mas é mais
  crítico agora do que era com bucket externo isolado. Confirmado pelo
  `backend`: chave é sempre `UUID.randomUUID()` gerado internamente,
  nunca vem de input do usuário; `download()` mantém checagem extra
  (`normalize().startsWith(baseDir)`) como defesa adicional contra path
  traversal.
- **CDN continua fazendo sentido para servir os arquivos**, independente
  de o binário estar em disco local ou bucket externo — a decisão de CDN
  (seção não coberta aqui, ver `system-design`/`devops` para
  configuração de fato) não muda com essa troca de storage; o servidor de
  origem (`origin`) do CDN só passa a apontar para o próprio backend/
  disco local em vez de apontar para o bucket.
- **Custo**: elimina qualquer custo (ainda que mínimo, dado free tier) de
  bucket externo — disco local já está pago dentro do custo do próprio
  servidor de produção.
- **Simplicidade operacional líquida**: um serviço a menos para
  provisionar/monitorar/ter credencial (bucket externo) em troca de um
  novo requisito operacional que precisa existir de fato (backup de
  disco, trade-off 2). Não é uma simplificação "de graça" — troca-se um
  tipo de responsabilidade operacional por outro, e a rotina de backup é
  a peça que não pode ser esquecida.

## 6. Superfície de ataque — pontos centrais

- Autorização por role obrigatória em todo endpoint admin, isolado sob
  `/api/v1/admin/...` com políticas próprias de rate limit/CORS/log.
- XSS armazenado é o risco central do CMS — qualquer campo de conteúdo
  que aceite marcação precisa ser sanitizado no backend com allowlist
  (nunca confiar em sanitização client-side).
- Log de auditoria em toda alteração de conteúdo público (quem, quando,
  entidade, snapshot do valor anterior).
- E-mail (novo dado coletado) deve seguir a mesma política de dados
  sensíveis: nunca em log, acesso restrito, nunca junto de dado de
  participação/relato.

## 7. Infraestrutura / DevOps

- Ambientes: dev + prod (sem staging por ora — reavaliar se a equipe de
  desenvolvimento crescer ou migrations ficarem arriscadas).
- CI mínimo (lint + testes + build) já vale a pena desde já; CD completo
  pode esperar deploy manual/documentado no início.
- Backend expõe só `/health` publicamente (Actuator restrito); config
  100% via variáveis de ambiente; timeouts em toda chamada de rede; pool
  de conexão modesto (Hikari 5-10 conexões); logs estruturados em stdout
  sem dado pessoal; imagem Docker multi-stage rodando como usuário
  não-root.
- Observability inicial: só health check + métricas básicas
  (Micrometer) — nada de stack pesada (Prometheus/Grafana/ELK) até
  haver necessidade concreta.

## 8. CORS

> Complemento elaborado com `backend` e `security` em 2026-07-09.

- **Cenário real do projeto**: origem única e conhecida (o próprio domínio
  Angular do site), não é API pública multi-cliente. Isso muda a postura:
  CORS aqui é puramente uma trava de segurança contra requisição
  cross-origin feita por *outro site* no browser da vítima — não é
  mecanismo de "permitir integração".
- **Política: allowlist fechada, nunca wildcard.** `allowedOrigins` é
  exatamente uma origem (o domínio do site), nunca `*` — e nunca `*`
  combinado com `allowCredentials(true)` (inválido pelo spec e pelo
  Spring). `allowedMethods` e `allowedHeaders` restritos ao que é
  realmente usado (`GET, POST, PUT, PATCH, DELETE` / `Authorization,
  Content-Type`), não `*`. `maxAge` alto (ex. 3600s) para reduzir
  preflight, sem abrir mão da política.
- **Dev vs prod: sim, diferente, via Spring Profiles.** Origem permitida
  vem de property (`app.cors.allowed-origins`) alimentada por variável de
  ambiente — `http://localhost:4200` em dev, domínio real em prod (ex.
  `https://jogadoresanonimos.org.br`). As duas listas nunca convivem no
  mesmo profile ativo; erro clássico a evitar é deixar `localhost` "por
  garantia" na lista de prod.
- **Onde centralizar**: um único `@Bean CorsConfigurationSource`
  registrado dentro da `SecurityFilterChain` do Spring Security
  (`http.cors(...)`), **não** via `WebMvcConfigurer#addCorsMappings`.
  Motivo: o projeto já usa Spring Security para autenticação/autorização;
  configurar CORS numa camada separada da security chain cria risco de
  as duas ficarem desalinhadas (uma libera, outra barra, ou uma faz
  bypass da outra). Centralizado na security chain, há uma única fonte de
  verdade e o preflight `OPTIONS` é tratado antes da exigência de JWT.
- **Rotas `/api/v1/admin/...`: mesma política de CORS, sem tratamento
  especial.** CORS não é a camada de proteção que importa ali — seria
  redundante e daria falsa sensação de segurança. CORS é enforced pelo
  *browser*, não pelo servidor: não impede chamada direta via
  `curl`/script/token roubado. A proteção real das rotas admin continua
  sendo autorização por role (`@PreAuthorize`, `ROLE_ADMIN` verificado no
  backend, nunca inferido pela rota do frontend), rate limiting mais
  agressivo e log de auditoria — não CORS. Se no futuro o dashboard admin
  virar uma aplicação Angular separada (subdomínio próprio), a allowlist
  ganha uma segunda entrada explícita, mas continua sendo lista fechada.

## 9. Tratamento de erros

> Complemento elaborado com `backend` e `security` em 2026-07-09.

- **Mecanismo único**: um único `@RestControllerAdvice` global (não um
  por módulo/controller), com um `@ExceptionHandler` por categoria de
  exceção. Nenhum controller deve capturar exceção de negócio
  manualmente — só deixar propagar para o advice. Isso evita duplicação
  e drift de formato entre endpoints; adicionar uma exceção nova no
  futuro é só adicionar um handler novo, sem tocar em controllers
  existentes.
- **Exceções de negócio vivem no service layer** (ex.:
  `RecursoNaoEncontradoException`, `ConflitoDeEstadoException`), lançadas
  no service, nunca no controller — o service não deve conhecer
  `HttpStatus`, mantendo a separação Controller → Service → Repository.
- **Formato de resposta**: sempre o envelope já padronizado do projeto,
  com `data: null` em caso de erro e `error` como objeto (`code` estável
  para lógica do frontend + `message` amigável em português, seguro para
  exibir):
  ```
  { "success": false, "data": null, "error": { "code": "...", "message": "..." } }
  ```
- **Taxonomia e mapeamento**:

  | Categoria | HTTP | `error.code` | O que pode ir na mensagem ao cliente |
  |---|---|---|---|
  | Validação de entrada | 400 | `VALIDATION_ERROR` | Específica sobre o próprio input (ex. "senha deve ter no mínimo 10 caracteres") — é regra pública, não segredo de sistema; pode incluir `error.fields` (lista `{campo, mensagem}`) para o Angular destacar o campo |
  | Autenticação (login falho) | 401 | `UNAUTHENTICATED` | Mensagem genérica única — já é regra do projeto não diferenciar "usuário não existe" de "senha errada" |
  | Autorização (sem permissão/role) | 403 | `FORBIDDEN` | Genérica ("acesso negado") — usar só quando faz sentido revelar que o recurso existe mas a ação é vedada por role (ex. painel admin) |
  | Acesso a recurso de outro dono (IDOR) | **404**, não 403 | `NOT_FOUND` | Ver ponto de segurança abaixo — nega a própria existência do recurso |
  | Não encontrado | 404 | `NOT_FOUND` | Genérica |
  | Conflito de estado (ex. apelido já existe) | 409 | `CONFLICT` | Pode ser específica (regra pública) |
  | Rate limit excedido | 429 | `RATE_LIMITED` | Genérica ("muitas tentativas, aguarde"), pode incluir `Retry-After` |
  | Erro interno não mapeado (fallback) | 500 | `INTERNAL_ERROR` | 100% genérica ("ocorreu um erro, tente novamente") + código de erro correlato para busca em log |

- **Ponto de segurança mais importante para este projeto: 404 em vez de
  403 para IDOR.** Se um PARTICIPANTE tenta acessar relato/presença de
  outro usuário, a resposta correta é 404 — 403 confirmaria a existência
  do recurso para quem não tem acesso a ele, o que já é, por si, um
  pequeno vazamento de metadado. Como relatos/presença são o dado mais
  sensível do sistema, esse detalhe importa mais aqui do que em uma API
  comum.
- **Nunca vazar, em nenhuma mensagem de erro ao cliente**: stack trace,
  nome de exception, SQL/mensagem de driver JDBC/Postgres, nome de
  coluna/tabela, versão de dependência, path de arquivo/pacote interno.
  Em prod, desabilitar a página de erro padrão do Spring Boot
  (`server.error.include-stacktrace=never`,
  `server.error.include-message=never`, `server.error.include-exception=false`)
  — o erro mais comum é a whitelabel error page do Spring vazando `trace`/
  `message`/`path` por omissão.
- **Correlação para suporte sem dado pessoal**: incluir um código de
  erro/`traceId` gerado por requisição (não relacionado ao usuário) tanto
  no log interno (nível `ERROR`, com stack trace completa) quanto,
  opcionalmente, na resposta ao cliente — permite localizar o incidente
  no log sem precisar de dado identificável do usuário. Esse identificador
  segue a mesma regra de anonimato do item de logs abaixo.
- **Local no código**: o `@RestControllerAdvice` deve viver em pacote
  comum (ex. `common.exception` ou `web.error`), separado dos pacotes de
  domínio, deixando claro que é infraestrutura transversal, não regra de
  negócio de um módulo específico.

## 10. Logs e auditoria

> Complemento elaborado com `backend` e `security` em 2026-07-09. Formaliza
> como política geral o que a seção 6 já mencionava só para o CMS.

Dois tipos de log, com propósito, conteúdo, retenção e acesso diferentes:

### Log operacional (troubleshooting técnico)
- **O que vai**: método/path da requisição (sem query string sensível),
  status code, latência, código de erro correlato; em erro técnico,
  stack trace completa e nome da exception (único lugar onde detalhe
  técnico completo pode existir); tentativa de login falho/rate limit
  atingido identificada só por identificador técnico não reversível
  (hash de token/conta), nunca por apelido, e-mail ou IP.
- **Nunca vai**: apelido, e-mail, conteúdo de relato, IP, qualquer campo
  de payload de rota sensível — mesmo mascarado (mais seguro simplesmente
  omitir do que mascarar mal). Sugerido: lista explícita de rotas
  excluídas de log de payload no filtro de log HTTP (ex.
  `/api/v1/auth/**`, `/api/v1/relatos/**`, `/api/v1/reunioes/**/presenca`,
  `/api/v1/depoimentos/**`), em vez de depender de revisão manual por
  endpoint.
- **Retenção**: curta, 14–30 dias — suficiente para debug de incidente
  recente, minimizando dado guardado (alinhado a minimização de dados da
  LGPD). Rotação automática a cargo de `devops`.
- **Acesso**: desenvolvedores/`devops` em plantão. Sem necessidade de
  trilha de auditoria própria sobre quem consultou (não há dado pessoal
  ali, se a regra acima for seguida).

### Log de auditoria administrativa (rastreabilidade de ação de admin)
- **O que vai**: quem (id técnico do admin — aceitável identificar o
  admin agente, diferente do participante, que é o sujeito que o
  anonimato do projeto protege), quando, ação (criar/editar/
  publicar/despublicar/excluir), entidade/id do recurso, snapshot do
  valor anterior (já previsto na seção 2).
- **Nunca vai**: dado de participante mesmo quando indireto — ex., se um
  admin aprova/rejeita um depoimento, o log registra "depoimento X mudou
  de pending para approved", nunca o texto do depoimento nem o autor.
- **Retenção**: mais longa, 12–24 meses — permite investigar abuso de
  poder de admin ou reverter erro de conteúdo muito depois do fato; ainda
  assim não "para sempre", revisar/expurgar após esse período (política
  de retenção geral de LGPD a fechar com `po`/`devops`).
- **Acesso**: restrito a administrador(es) de sistema, não a todo admin
  de conteúdo nem a desenvolvedor por padrão — só em investigação pontual
  documentada. Evita que a própria auditoria vire uma nova superfície de
  vigilância interna.

### Por que a separação importa neste projeto
Log operacional tende a vazar dado pessoal por descuido (é o que captura
request/response bruto) — por isso a disciplina de excluir payload de
rotas sensíveis precisa estar no filtro, não na memória de quem escreve
cada endpoint. Log de auditoria é sobre ação administrativa, não sobre
comportamento de participante — pode (e deve) identificar o admin agente,
mas trata qualquer dado de participante referenciado só por id técnico do
recurso, nunca por conteúdo.

## 11. Pendências para a próxima sessão

1. ~~**Professor**: traduzir este arquivo + `.claude/knowledge/` pro
   Notion.~~ **Feito em 2026-07-09**: as 11 seções foram publicadas
   (páginas Spring Boot, Docker, Angular no Notion) e `.claude/knowledge/`
   foi zerado. ~~**Pendência em andamento**: separar o conteúdo de
   segurança e de CI-CD/infraestrutura em páginas próprias.~~ **Feito em
   2026-07-10**: criadas as páginas "Segurança" (Autenticação JWT,
   Autorização por Role, CORS, Upload de Arquivos, Superfície de Ataque,
   Tratamento de Erros, Logs/Auditoria, Rate Limiting) e "CI-CD /
   Infraestrutura" (CI mínimo, health check/Actuator, pool de conexão,
   observabilidade) no mesmo banco de dados do Notion onde ficam
   Spring Boot/Docker/Angular; os toggles equivalentes foram removidos
   de dentro de Spring Boot e Docker, com uma nota apontando pra nova
   página em cada lugar. CMS Leve permaneceu em Spring Boot (não é
   conteúdo de segurança).
2. ~~Desenhar o fluxo de frontend de cadastro com e-mail (UX +
   validação).~~ **Feito em 2026-07-12** por `ux-ui`: spec de cadastro
   (apelido novo campo — corrige bug real do código atual que só pedia
   e-mail+senha e não deixava ninguém logar depois —, e-mail com texto
   de apoio de anonimato, confirmar senha novo campo), tela "esqueci
   senha" (link até então morto) e tela "redefinir senha" (nova, com
   token). Ver `.claude/knowledge/` e resumo da sessão de 2026-07-12.
3. ~~Definir formalmente com `security`/`backend` como o e-mail fica
   isolado dos dados de participação/relatos no banco.~~ **Feito em
   2026-07-12**: e-mail fica na própria tabela `usuario`
   (`VARCHAR NOT NULL UNIQUE`), sem tabela separada; isolamento é por
   regra de aplicação (nunca em query/DTO/log de relato, presença ou
   reunião). Registrado em `.claude/knowledge/seguranca-email-cadastro.md`
   e formalizado no `CLAUDE.md`.
4. ~~Decidir se o e-mail também substitui totalmente a "chave de
   recuperação exibida uma vez" ou se os dois mecanismos convivem.~~
   **Feito em 2026-07-12**: e-mail substitui totalmente — chave de
   recuperação abandonada, os dois não convivem. Mesma referência acima.
   - **Requisito adicional aprovado no meio da sessão**: login passa a
     aceitar apelido OU e-mail (não só apelido). Ajustado com `security`:
     índice único em `email` cobre a query de login; rate limit e
     hash de log de tentativa falha cobrem e-mail com a mesma regra que
     já valia para apelido (nunca texto puro em log).
5. ~~Após aprovação final deste documento, acionar o `po` para registrar
   as decisões aprovadas como regra de negócio permanente no
   `CLAUDE.md`.~~ **Feito em 2026-07-12** para o bloco de
   autenticação/e-mail (pendências 2-4 acima). Falta ainda registrar CMS/
   upload quando esse bloco fechar de vez (ver pendência 6 abaixo).
6. ~~**Em andamento**: `backend` já entregou o SDD~~ **Implementação
   inicial feita em 2026-07-12** por `backend`: estrutura de pacotes
   (`common`, `config`, `security`, `usuario`, `auth`, `cms`, `imagem`),
   os 3 blocos completos (auth/e-mail com Argon2id+JWT, CMS com
   sanitização HTML e auditoria, upload com magic bytes+EXIF strip+GC),
   migrations Flyway V1-V4, tratamento de erro (seção 9) e logs (seção
   10) aplicados, 23 testes JUnit+Mockito passando. Detalhe em
   `.claude/knowledge/backend-implementacao-inicial.md`.
   **Falta ainda**: valores reais de rate limit e MFA/TOTP admin (fechar
   com `security`); `StorageClient` real (R2/B2, hoje é stub em disco);
   `EmailService` real via SMTP/API (hoje só loga); suporte a WebP no
   processamento de imagem (`ImageIO` do JDK não lê nativo — decidir lib
   ou restringir a jpeg/png); `docker-compose.yml` do projeto; GC de
   imagem órfã ainda não cobre "objeto no bucket sem linha no Postgres"
   (depende de bucket real pra listar); forçar troca de senha no
   primeiro login do admin bootstrap.
   **Pendências de recurso externo, a cargo do usuário/dono do
   projeto** (ver detalhe completo no knowledge acima): (1) instância
   Postgres real dev+prod; (2) segredos `JWT_SECRET`/`LOG_HMAC_SECRET`;
   (3) bucket S3-compatible real — decidir R2 vs B2 e gerar credenciais;
   (4) provedor de e-mail (SMTP ou API — SendGrid/SES/Mailgun/outro) para
   o fluxo de esqueci-senha funcionar de verdade; (5) valores de
   `ADMIN_BOOTSTRAP_APELIDO`/`EMAIL`/`SENHA` do primeiro admin.

   Três blocos do contrato original:
   - **Auth/e-mail**: fechado.
   - **CMS** (`cms_pagina`/`cms_bloco`/`cms_alteracao_log`): fechado.
   - **Upload de imagem**: fechado quanto à segurança (EXIF strip, magic
     bytes, chave UUID, ordem bucket-depois-postgres, rate limit 10/min
     por admin + 60/min global). **Bloqueado quanto à infra** — três
     pendências endereçadas a `devops` que não puderam ser delegadas
     nesta sessão porque os agentes `devops`, `qa-backend` e `qa-system`
     não apareceram na lista de `subagent_type` disponível para o `po`
     (gap de ambiente, não de definição — os arquivos
     `.claude/agents/Devops.md`, `Qa backend.md`, `Qa system.md` existem
     e estão corretos; testado com variações de nome/capitalização sem
     sucesso). Reavaliar logo no início da próxima sessão, antes de
     qualquer outra coisa, se a lista de agentes já carrega esses três.
     ~~Pendências que ficam esperando `devops`~~ **Fechadas em 2026-07-12**
     por `devops`, ver seção "Decisões de infra — `devops`" no final do
     `contrato-auth-cms-upload.md`:
     1. ~~Tamanho máximo de arquivo aceito + política de quota do bucket
        S3-compatible (R2/B2)~~ — 5MB por arquivo (proxy + Spring
        multipart config), quota de bucket coberta por free tier
        (~10GB) com alerta de billing em 80%, sem cota por admin.
     2. ~~Retry/backoff da fila de envio de e-mail (esqueci-senha)~~ —
        sem broker externo, `@Async`+`@Retryable` em memória, 3
        tentativas/backoff exponencial curto; falha definitiva só loga
        (sem endereço em texto puro) e conta com o usuário re-chamar
        `/esqueci-senha` (idempotente).
     3. ~~GC de imagem órfã no bucket~~ — `@Scheduled` diário no próprio
        backend (sem cron externo), período de graça de 7 dias antes de
        considerar upload não-associado como órfão, log só de contagem.
   - ~~`frontend-services` pode começar a mockar em cima do contrato já
     fechado (blocos auth/CMS e a parte de segurança do upload) sem
     esperar as pendências de infra.~~ **Feito em 2026-07-12** — ver
     detalhe na pendência 8 abaixo.
7. ~~**Frontend**: revisar o código existente com foco em performance~~
   **Feito em 2026-07-12**: 2 correções de baixo risco aplicadas —
   throttle (`auditTime(100)`) no scroll listener de
   `navbar.ts` (antes sem debounce, diferente do padrão já usado em
   `voltar-ao-topo.ts`); `loading="lazy"` + `decoding="async"` em imagens
   de `@for` em `home.html`/`mural-de-apoio.html`/`literatura.html`.
   Bundle inicial: 716.84 kB. **Achados reportados, não aplicados**
   (exigem decisão de arquitetura, ficam como pendência nova — ver item
   9): falta lazy loading de rotas (nenhuma das 22 rotas usa
   `loadComponent`, inclui `reunioes` que carrega `leaflet` no bundle
   inicial mesmo pra quem não visita a página); budget de bundle em
   `angular.json` (3-5mb) ~4x maior que o necessário hoje; script do
   reCAPTCHA carregado global em `index.html` em vez de sob demanda;
   `reunioes.ts` sem guard `isPlatformBrowser` (dívida só se SSR entrar
   depois); sem SSR/hidratação (decisão consciente de não priorizar, dado
   volume baixo do projeto). Detalhe em `.claude/knowledge/frontend.md`.
8. ~~`frontend-services`: mockar services de auth/e-mail e CMS em cima do
   contrato fechado~~ **Feito em 2026-07-12**: Blocos 1 (auth/e-mail) e 2
   (CMS) mockados com envelope `{success, data, error}` idêntico ao real,
   flag `environment.useMocks` central pra trocar mock→HTTP real sem
   mudar assinatura pública. Bloco 3 (upload) ignorado por escolha —
   `imagemId` no CMS já é referência opaca, não bloqueia. **Achado crítico
   resolvido no processo**: `auth.ts` estava com contrato antigo,
   dessincronizado de `minha-conta.ts` (chamava `Auth.register()`/
   `usuario.identificador` que não existiam) — bloqueava build de
   produção; `frontend-services` reescreveu `auth.ts` e ajustou
   `minha-conta.ts` para o contrato novo. Build validado (`tsc --noEmit` e
   `ng build development` limpos). Detalhe em
   `.claude/knowledge/frontend-services-auth-cms.md`.
9. ~~**Nova pendência, aberta em 2026-07-12**: `minha-conta.html` (form de
   registro) não tem campo de apelido nem confirmar-senha ainda, e não
   existem as telas de "esqueci senha"/"redefinir senha"~~ **Feito em
   2026-07-12** por `frontend`: campo apelido + confirmar-senha no
   registro (com erro por campo, batendo `error.fields` do contrato),
   label de login trocado para "Apelido ou e-mail", telas novas
   `esqueci-senha` e `redefinir-senha` (componente+rota cada, consumindo
   `authService.esqueciSenha`/`redefinirSenha` já prontos, mensagem
   genérica de sucesso sem diferenciar conta existente). Build validado.
   Detalhe em `.claude/knowledge/frontend-auth-ui.md`.
   ~~**Ainda em aberto, sem decisão**: as 4 melhorias de performance
   reportadas no item 7~~ **Feito em 2026-07-12** por `frontend`: lazy
   loading (`loadComponent`) aplicado em todas as rotas exceto Home;
   budget de bundle apertado de 3-5mb para 800kb/1.2mb em
   `angular.json`; reCAPTCHA tirado do `index.html` global, carregado sob
   demanda via novo `recaptcha-loader.ts` (script tag dinâmica, sem lib
   nova — sinalizado para `security` revisar quando conveniente); guard
   `isPlatformBrowser` adicionado em `reunioes.ts`. Resultado: bundle
   inicial 716.84 kB → 412.80 kB raw / 106.60 kB transfer, `leaflet`
   isolado em chunk lazy. Detalhe em `.claude/knowledge/frontend.md`.

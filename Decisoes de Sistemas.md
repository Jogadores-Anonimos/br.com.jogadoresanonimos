# Decisões de Sistemas — Backend, Admin e Infraestrutura

> Registro das decisões de arquitetura já tomadas (backend, admin,
> infraestrutura, segurança). Pendências reais em aberto ficam em
> `PENDENCIAS.md`, não aqui.

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
- **Nomenclatura `enderecos` (público) vs `escritorios-regionais`
  (admin/service) — decisão fechada, manter divergente.** Página pública
  foi renomeada para `enderecos` por clareza de UX para o usuário final;
  admin e camada de service continuam `escritorios-regionais`/
  `EscritorioRegionalService` porque é o nome técnico/de domínio do
  recurso no backend. Não é inconsistência a corrigir — são públicos
  diferentes (usuário final vs. camada técnica) com nomes propositalmente
  diferentes.

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
- **E-mail passa a ser coletado no cadastro** — exceção deliberada à
  regra geral de anonimato do projeto, formalizada no `CLAUDE.md`.
  Motivos: (1) recuperação de conta por e-mail em vez de chave de
  recuperação exibida uma única vez; (2) necessário para envio de itens
  físicos da loja. E-mail fica na própria tabela `usuario`
  (`VARCHAR NOT NULL UNIQUE`), isolamento por regra de aplicação (nunca
  em query/DTO/log de relato, presença ou reunião). Login aceita apelido
  OU e-mail.
- Admin: **MFA via TOTP** (app autenticador) + códigos de backup —
  **planejado, mas fica para depois do CRUD básico**, quando o dashboard
  já estiver em teste.
- JWT carrega `role` como claim assinado, revalidado a cada requisição.
  Autorização sempre checada no backend (`@PreAuthorize`), nunca inferida
  pela rota do frontend.
- Rate limiting e bloqueio de tentativas de login por conta/sessão, nunca
  por IP. Mensagens de erro de login genéricas (evita enumeration).

## 5. Storage de imagem

- **Decisão: disco local persistente do servidor de produção**, não
  bucket S3-compatible externo — evita decidir titularidade de conta
  terceira (cliente vs dev) antes de precisar, dado o volume baixo do
  projeto.
- `StorageClient` (interface) isola essa decisão do resto do código —
  implementação de disco (`LocalDiskStorageClient`) usada em dev, teste
  e produção, sem tocar em código de domínio. Env vars:
  `STORAGE_STUB_DIR`/`STORAGE_PUBLIC_BASE_URL`.
- Validação de segurança: remoção de EXIF antes de persistir, validação
  por magic bytes (não extensão/`Content-Type`), sem aceitar SVG, sem
  upload por URL, checagem anti-polyglot em PDF, GC de imagem órfã
  (`@Scheduled` diário, período de graça de 7 dias), rate limit de
  upload (10/min por admin + 60/min global).

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
  3. **Política de retenção/expurgo — fechada em 2026-07-15**: imagem
     *associada* a conteúdo removido (bloco de CMS, banner do mural) cai
     no mesmo fluxo de GC de órfã já existente, sem mecanismo novo. Ao
     deletar o conteúdo pai (DELETE simples, sem soft-delete de imagem),
     a `Imagem` correspondente naturalmente para de ter referência ativa
     e o GC a coleta no ciclo seguinte, dentro do mesmo período de graça
     de 7 dias. Corrigido também um bug pré-existente descoberto ao
     analisar essa decisão: `ImagemOrfaGcJob` só checava
     `CmsBlocoRepository.existsByImagemId`, não checava
     `BannerMuralRepository` — um banner do mural ativo podia ser
     erroneamente coletado após 7 dias. Fix: adicionado
     `BannerMuralRepository.existsByImagemId` na mesma cadeia de filtro
     do job. `Relato` e `Aviso` não têm campo de imagem, fora do escopo
     desta decisão.
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
- **CDN — decisão fechada em 2026-07-15 (`system-design`)**: vale a pena
  quando existir hospedagem de produção — Cloudflare (ou equivalente) na
  frente do domínio real, cache de assets estáticos (JS/CSS/imagens do
  frontend Angular + arquivos servidos do disco local via
  `ImagemPublicController`), `origin` apontando pro próprio backend/disco
  local, sem bucket S3 externo (decisão de storage não muda). Não é
  questão de volume baixo justificar adiar — CDN de assets estáticos é
  barato mesmo em baixo tráfego — é bloqueio puro de pré-requisito: não
  há domínio/DNS real ainda. Entra no checklist de deploy assim que a
  hospedagem for decidida (ver `PENDENCIAS.md`, seção "Infraestrutura /
  Deploy").
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

## 12. Rate limiting

> Complemento elaborado com `security` em 2026-07-15. Fecha a pendência
> "valores reais de rate limit" (antes só upload estava fechado).

- **Mecanismo único, sem lib nova**: `RateLimiterService` +
  `TokenBucket` (`backend/src/main/java/br/com/jogadoresanonimos/common/ratelimit/`)
  — token bucket em memória (`ConcurrentHashMap` de chave → bucket),
  chamado direto do service layer via `rateLimiterService.check(chave,
  capacidade, janela)`, lançando `RateLimitedException` → HTTP 429
  (`error.code = RATE_LIMITED`) já mapeado no `GlobalExceptionHandler`.
  Todo novo limite deve reaproveitar esse mecanismo, não introduzir
  Bucket4j/Redis/etc — volume do projeto (~2.500 req/dia) não justifica.
- **Chave nunca é IP.** Login/registro/esqueci-senha/redefinir-senha usam
  hash HMAC (`HmacHasher`, chave via secret de ambiente) do identificador
  ou do token de reset — nunca o valor em claro, nunca IP. Ações
  autenticadas (relato, conta, biblioteca, upload) usam o UUID técnico do
  usuário já validado pelo JWT, que não é dado pessoal por si só (mesmo
  padrão já em uso no upload de imagem/arquivo). Alinhado à regra de
  anonimato do projeto: rate limit por IP é padrão de mercado aceitável,
  mas aqui a alternativa (chave hasheada/id técnico) já resolve o mesmo
  problema sem persistir IP nem em memória.

### Valores fechados

| Endpoint | Chave | Limite | Motivo |
|---|---|---|---|
| `POST /api/v1/auth/login` | hash do identificador (apelido/e-mail) | 5 / 1 min | Padrão de mercado contra brute force (OWASP: ~5/min é ponto de partida comum); reCAPTCHA já soma como segunda camada. Mensagem de erro genérica evita enumeration. **Já implementado**, mantido sem alteração. |
| `POST /api/v1/auth/registro` | hash de `apelido\|email` | 5 / 1 min | Contém spam de criação de conta em massa; reCAPTCHA já soma. **Já implementado**, mantido. |
| `POST /api/v1/auth/esqueci-senha` | hash do identificador | 5 / 1 min | Evita abuso de envio de e-mail (custo de terceiro) e reduz sinal de enumeração de conta existente — resposta já é sempre genérica independente de a conta existir. **Já implementado**, mantido. |
| `POST /api/v1/auth/redefinir-senha` | hash do token de reset recebido | 5 / 1 min | **Novo.** Token é aleatório de 32 bytes (baixo risco de brute force prático), mas defesa em profundidade é barata e consistente com o resto do fluxo de auth — sem isso era o único passo do fluxo de recuperação de senha sem limite. |
| `PUT /api/v1/usuarios/me` (atualizar conta) | UUID do usuário autenticado | 5 / 1 min | **Novo.** Evita uso do endpoint para varrer/confirmar apelidos ou e-mails já cadastrados via resposta de conflito (409), além de conter flood trivial. |
| `PUT /api/v1/usuarios/me/senha` (alterar senha) | UUID do usuário autenticado | 5 / 1 min | **Novo.** Ainda que exija JWT válido + senha atual, limita tentativa de força bruta contra a senha atual em caso de token comprometido/reuso de sessão. |
| `POST/DELETE /api/v1/usuarios/me/biblioteca/{id}` | UUID do usuário autenticado | 30 / 1 min | **Novo.** Ação de baixo risco (favoritar/desfavoritar arquivo já autorizado), limite mais permissivo só para conter flood/loop de cliente com bug, não abuso malicioso real. |
| `POST /api/v1/relatos` (enviar relato) | UUID do autor | 5 / 10 min | Conteúdo sensível gerado por usuário, sujeito a moderação; limite já fechado anteriormente, mantido sem alteração. |
| `POST /api/v1/admin/imagens` (upload) | UUID do admin + limite global | 10/min por admin + 60/min global | Já fechado anteriormente, mantido sem alteração. |
| `POST /api/v1/admin/arquivos` (upload PDF) | UUID do admin + limite global | 10/min por admin + 60/min global | Já fechado anteriormente, mantido sem alteração. |

### Endpoints avaliados e descartados (sem rate limit dedicado)

- Todos os `GET` públicos (reuniões, hotlines, escritórios regionais,
  FAQ, CMS, vinte perguntas, mural de apoio, avisos, reflexão do dia,
  relatos aprovados, download de arquivo) — leitura pública, sem efeito
  colateral de escrita/custo de envio; não há sinal concreto de abuso a
  mitigar hoje. Revisitar se algum desses virar alvo de scraping/DDoS
  observado em produção (rate limit global "leve" por rota, não por
  usuário, seria a primeira opção nesse cenário).
- Sem endpoint de "contato" server-side no projeto (fluxo de contato é
  estático, WhatsApp/telefone) — não há superfície de spam de formulário
  de contato para limitar.

## 13. Pendências

Ver `PENDENCIAS.md` — arquivo único de pendências reais em aberto.
Histórico de implementação de cada bloco (auth/e-mail, CMS, upload,
frontend, admin) vive em `.claude/knowledge/`.

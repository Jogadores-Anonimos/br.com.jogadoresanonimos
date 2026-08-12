# Decisões de Sistemas - Backend, Admin e Infraestrutura

> Descreve o sistema **como ele é hoje** decisões de arquitetura já
> fechadas (backend, admin, infraestrutura, segurança), organizadas por
> tema/componente, não por ordem cronológica. Onde uma decisão substituiu
> outra, só a decisão final aparece; o raciocínio de descartar a opção
> anterior é mantido em uma frase quando ensina algo útil. Pendências
> reais em aberto ficam em `PENDENCIAS.md`, não aqui.

## 1. Visão geral da arquitetura

 **Frontend**: Angular (SPA consumindo API REST do backend), hospedado
  na **Vercel** (deploy estático separado do backend).
 **Backend**: Spring Boot 3.2+ (Java 21+), **monolito único**, rodando
  como container Docker num **VPS** próprio sob **Docker Swarm**
  (single node). Volume de tráfego (~300 pedidos de ajuda/dia, ~2.500
  requisições/dia) não justifica microsserviços, múltiplos nodes ou
  Kubernetes  ver seção 7 para detalhamento da infraestrutura.
 **Banco de dados**: PostgreSQL único para tudo (usuários, reuniões,
  avisos, reflexões, relatos, CMS de conteúdo), rodando no mesmo VPS via
  Swarm, sem réplica. Não há banco separado para imagens.
 **Armazenamento de imagem**: **disco local persistente do servidor de
  produção**, isolado atrás da interface `StorageClient`, não bucket
  S3compatible externo  ver seção 4 para o racional completo.
 **Ambientes**: dev + prod (sem staging dedicado por ora  reavaliar se
  a equipe crescer ou migrations ficarem arriscadas).
 **Docker**: `dockercompose.yml` (raiz do repo) orquestra backend +
  Postgres + frontend **só em desenvolvimento local** (hot reload, volume
  de código montado); produção usa `stack.prod.yml` via `docker stack
  deploy` no Swarm do VPS (ver seção 7A)  os dois arquivos não são
  intercambiáveis.

## 2. Hospedagem e infraestrutura

### 2A. Topologia geral
 **Frontend**: Vercel, deploy estático, DNS apex/`www` **fora** do proxy
  Cloudflare (DNS only)  preserva o roteamento que a Vercel já gerencia
  nativamente.
 **Backend + Postgres**: VPS único, Docker Swarm (node único, hostname
  `ja`, role Leader), compartilhado com outro projeto administrado por um
  colega. Redes overlay:
   `ja` (10.0.1.0/24)  rede pública do Swarm; Traefik só roteia serviço
    que esteja **nela e** com label `traefik.enable=true` (as duas
    condições, não uma ou outra). Backend entra nela.
   `infrasiteproxy` (10.0.2.0/24)  só Traefik conectado; propósito do
    colega, não usada pelo projeto.
   `backendinternal` (`internal: true`, sem saída pública)  rede
    overlay própria do stack deste projeto, compartilhada só entre
    backend e Postgres. Postgres nunca entra na rede `ja` nem tem porta
    publicada  inalcançável pelo Traefik ou por qualquer serviço fora do
    próprio stack.
 **Firewall (`ufw`)** libera só `22` (SSH `eduardo`), `22022` (SSH
  legado do colega), `80`, `443`.
 **Portainer** roda no Swarm (2 serviços) para gestão via web; escopo de
  uso pro projeto ainda não confirmado (ver `PENDENCIAS.md`).

### 2B. Traefik e TLS
 **Traefik** (`traefik:v3.5.3`) é o reverse proxy único do VPS 
  `providers.swarm=true`, `providers.docker.network=ja`,
  `providers.docker.exposedbydefault=false`. Cobre roteamento e
  emissão/renovação de TLS via labels Docker (resolver ACME
  `letsencryptresolver`, HTTP challenge), sem Certbot separado; redirect
  HTTP→HTTPS ativo.
 Labels do serviço `backend` ficam em `deploy.labels` (não `labels` de
  topo  em modo Swarm o Traefik só lê labels dentro de `deploy`):
  `traefik.enable=true`, `traefik.docker.network=ja` (desambigua a rede
  quando o serviço está em mais de uma), router com
  `` Host(`api.jogadoresanonimos.com.br`) ``, entrypoint `websecure`,
  certresolver `letsencryptresolver`.

### 2C. Docker Swarm  decisões do `stack.prod.yml`
 Volume singlenode (`postgres_data`, `backend_storage`, sem driver
  distribuído) e `replicas: 1` nos dois serviços  coerente com node
  único de 2GB RAM. **Nunca subir `replicas > 1` do Postgres** (dado não
  replicado, splitbrain garantido); não há indício de tráfego que
  justifique mais de uma réplica do backend hoje.
 `resources.limits`/`reservations` de CPU/memória definidos nos dois
  serviços, protegendo o node único de um serviço consumir memória a
  ponto de derrubar o outro. Limite de memória do backend: **460M**
  (ajustado a partir de 400M após medição real em produção mostrar uso em
  99.4% do limite anterior, sem margem de segurança  risco concreto de
  OOMkill em pico). Heap da JVM mantido em `Xmx256m Xms256m`
  (`JAVA_TOOL_OPTIONS`)  a diferença observada é overhead nativo da JVM
  (metaspace, thread stacks, buffers offheap), não heap de aplicação, daí
  o ajuste ser no limite do container, não no heap.
  `resources.reservations.memory` (256M) reflete uso normal, não pico, e
  não muda.
 `update_config.order: startfirst` + `rollback_config` no backend 
  Swarm sobe o container novo antes de derrubar o antigo (minimiza
  downtime) e reverte automaticamente se o healthcheck da nova versão
  falhar (`failure_action: rollback`). Postgres usa `stopfirst` (não faz
  sentido rodar 2 Postgres apontando pro mesmo volume ao mesmo tempo).
 `depends_on`/`condition: service_healthy` **não é suportado em modo
  Swarm** (Compose ignora silenciosamente, diferente do
  `dockercompose.yml` de dev). Mitigado por `restart_policy: onfailure`
  no backend: se subir antes do Postgres estar pronto e falhar a conexão,
  reinicia sozinho até conseguir.
 `eduardo` está no grupo `docker`; Docker roda como serviço `systemd`
  habilitado (sobe sozinho se o VPS reiniciar).
 Forma de deploy: `docker stack deploy c stack.prod.yml <nomedastack>`
  no VPS  nunca `dockercompose up` em produção.
 **Quando (não) migrar para Kubernetes**: Swarm cobre o que o projeto
  precisa hoje (single node, rolling update simples, restart automático)
  sem a complexidade operacional de um cluster K8s (etcd, control plane,
  múltiplos tipos de manifesto). Gatilho de revisão: necessidade real de
  múltiplos nodes/autoscaling/múltiplos serviços independentes  nenhum
  existe hoje (ver "Contexto de escala" no `CLAUDE.md`).

### 2D. Docker Secrets (segredos sensíveis)
 Segredos sensíveis (`DB_PASSWORD`, `JWT_SECRET`, `LOG_HMAC_SECRET`,
  `MFA_SECRET_ENCRYPTION_KEY`, `RECAPTCHA_SECRET_KEY`, `RESEND_API_KEY`,
  `ADMIN_BOOTSTRAP_SENHA`) usam **Docker Secrets nativo do Swarm**
  (`/run/secrets/...`), nunca variável de ambiente em texto claro 
  motivo: `environment:` fica visível em texto claro via `docker service
  inspect` para qualquer usuário com acesso ao daemon Docker do VPS
  (servidor é compartilhado com o colega).
 Não migram para secret (não são segredo, são config/host): `DB_NAME`,
  `DB_USERNAME`, `DB_POOL_MAX/MIN`, `JWT_TTL_MINUTES`,
  `CORS_ALLOWED_ORIGINS`, `FRONTEND_URL`, `STORAGE_*`,
  `ADMIN_BOOTSTRAP_APELIDO`, `ADMIN_BOOTSTRAP_EMAIL`,
  `ADMIN_LIMITE_MAXIMO`. Essas continuam precisando estar exportadas no
  shell que roda o deploy (`docker stack deploy` não interpola `.env`
  sozinho, diferente de `dockercompose up`): `set a; source .env.vps;
  set +a; docker stack deploy c stack.prod.yml jaapp`.
 **Mecanismo de leitura**: não existe suporte nativo do Spring Boot ao
  sufixo `_FILE` (é convenção do entrypoint das imagens oficiais
  Postgres/MySQL, não do Spring). O projeto usa um
  `EnvironmentPostProcessor` customizado
  (`DockerSecretsEnvironmentPostProcessor`, sem dependência nova,
  registrado via `METAINF/spring.factories`), que roda antes do `@Value`
  ser resolvido: para cada nome de segredo migrado, se existir o arquivo
  `/run/secrets/<nomeemsnakecase>` (caminho fixo onde o Swarm monta o
  secret), lê o conteúdo e injeta como `PropertySource` de prioridade
  mais alta, com o mesmo nome de env var que o código já usa
  (`DB_PASSWORD`, `JWT_SECRET`, etc)  zero mudança nos pontos de leitura
  existentes. Em dev (sem Swarm, `.env` puro), o arquivo não existe e a
  var de ambiente convencional continua valendo  mesma classe funciona
  nos dois ambientes sem branch por profile.
 `postgres` do próprio `stack.prod.yml` recebe `POSTGRES_PASSWORD` via
  `POSTGRES_PASSWORD_FILE` (esse sim suportado nativamente pela imagem
  oficial), reusando o secret `db_password` compartilhado com o backend.
 Prérequisito operacional: os 7 secrets precisam existir no Swarm
  (`docker secret create <nome> `) antes de `docker stack deploy`, senão
  o serviço `backend` não sobe.

### 2E. Rollback de imagem
 Sem CI/CD completo (não se justifica ainda pela cadência de deploy
  manual  ver seção 8), a imagem do backend é buildada com duas tags a
  cada rebuild manual: `latest` (a que `stack.prod.yml` referencia) e o
  SHA curto do commit (`git revparse short HEAD`), preservando a
  imagem anterior sob sua própria tag:
  ```
  cd ~/deploy/backend
  git revparse short HEAD
  docker build t jogadoresanonimos/backend:latest \
    t jogadoresanonimos/backend:<shacurto> .
  docker stack deploy c stack.prod.yml jaa
  ```
  Rollback consiste em reapontar `stack.prod.yml` para a tag do SHA
  anterior e rodar `docker stack deploy` de novo  nunca apagar a imagem
  antiga do rebuild seguinte sem antes confirmar que o novo deploy está
  estável. `docker image ls jogadoresanonimos/backend` lista as versões
  disponíveis localmente. Reavaliar CI/CD de verdade (build automatizado
  + registry) só se a cadência manual virar gargalo.

### 2F. Hardening de SSH  mantido `PasswordAuthentication yes`
 VPS é compartilhado com um colega administrador que acessa root pela
  porta legada `22022` usando senha, sem chave própria cadastrada em
  `authorized_keys`. Tentativa de hardening
  (`PasswordAuthentication no` + `PermitRootLogin prohibitpassword`)
  funciona para `eduardo`/`root` via chave, mas trava o colega sem
  alternativa de acesso  foi revertida.
 **Decisão**: este VPS mantém `PasswordAuthentication yes` e
  `PermitRootLogin yes`. Prioriza não travar o colega sobre reduzir a
  superfície de ataque de força bruta nesse servidor específico.
 **Condição para reabrir**: colega gera/cadastra chave pública própria
  em `authorized_keys` do root e confirma login por chave funcionando; só
  então reaplicar `PasswordAuthentication no` +
  `PermitRootLogin prohibitpassword` (nos dois lugares  `sshd_config`
  principal e o dropin do cloudinit, ver achado técnico em
  `.claude/knowledge/devops.md`).

### 2G. Cloudflare / CDN
 Zona DNS inteira `jogadoresanonimos.com.br` migrada para nameservers
  próprios do Cloudflare, mas **proxy ativo (nuvem laranja) só no
  subdomínio `api.`**  os demais registros (`www`, apex/Vercel, email,
  cPanel) ficam **DNS only** (nuvem cinza), preservando o roteamento que
  já existia antes da migração. Não é "colocar tudo atrás de CDN"; é
  proxy cirúrgico só onde há ganho concreto  a API, que recebe o volume
  de requisição real do backend.
 Cache de assets estáticos do frontend (JS/CSS/imagens Angular) e dos
  arquivos servidos do disco local (via `ImagemPublicController`) é
  coberto pela CDN da própria Vercel no domínio do frontend  não precisa
  de bucket S3 externo nem muda a decisão de storage (seção 4). CDN vale
  a pena mesmo em tráfego baixo porque o ganho de latência para usuários
  espalhados pelo Brasil (e o pouco de tráfego da África) é real e o
  custo de configurar é baixo  não é questão de volume justificar adiar.
 **SSL/TLS**: modo **Full (strict)** no Cloudflare  exige certificado
  válido também na origem (Traefik/Let's Encrypt), não Flexible (que
  faria HTTPS só até o Cloudflare e HTTP em texto claro até a origem,
  inaceitável dado que a API trafega JWT/dado sensível).
 **`forwardedHeaders.trustedIPs` no Traefik**: configurado com os ranges
  de IP publicados pelo Cloudflare, para que o Traefik só confie no header
  `XForwardedFor` quando a requisição realmente vier de um proxy
  Cloudflare (evita spoofing de IP de origem por requisição direta que
  burla o proxy). Isso preserva o IP real do visitante disponível na
  camada de log/infraestrutura **sem** ferir o requisito de anonimato do
  projeto  rate limiting da aplicação já não usa IP como chave (ver
  seção 6), então esse header serve só observabilidade de infraestrutura,
  não decisão de negócio sobre usuário.

### 2H. Observabilidade  caminho mínimo, sem serviço externo gerenciado
 **Spring Boot Actuator** com endpoints de métricas builtin
  (`/actuator/health`, `/actuator/metrics`, `/actuator/httpexchanges`),
  restrito à rede interna/admin autenticado  nunca exposto publicamente
  além do `/health`.
 **Log estruturado local** (ver seção 5), sem novo pipeline de coleta.
 **Painel simples** consumindo esses dados localmente (dashboard leve
  onbox ou visualização direta dos endpoints do Actuator), sem stack de
  série temporal dedicada.
 **Explicitamente descartado**: Datadog, New Relic, Better Stack ou
  qualquer APM/observabilityasaservice equivalente. Dois motivos:
   **Soberania de dado**: enviar dado operacional (log de requisição, IP
    de infraestrutura, padrão de acesso) para fora do ambiente controlado
    tensiona com a postura de anonimato/minimização de dado que rege o
    resto do sistema.
   **Volume**: ~2.500 requisições/dia é tráfego baixo; tracing
    distribuído, alerting avançado e correlação entre serviços resolvem
    um problema de escala que este monolito único não tem. Actuator + log
    estruturado já respondem "está no ar, está lenta, quanto tráfego"
    com custo próximo de zero.
 **Tradeoff aceito**: abre mão de dashboards prontos, alerting
  sofisticado e retenção histórica de longo prazo em troca de
  simplicidade operacional e de não depender de terceiro para dado de
  infraestrutura.
 **Gatilho de revisão**: volume crescer ordens de grandeza, sistema
  deixar de ser monolito único, ou necessidade concreta de alerting
  proativo que Actuator não cobre  nesse momento Prometheus/Grafana
  selfhosted é o próximo degrau antes de considerar um SaaS.

### 2I. Backend expõe/roda com
 Só `/health` público (Actuator restrito no resto); config 100% via
  variáveis de ambiente; timeouts em toda chamada de rede; pool de
  conexão modesto (Hikari 510 conexões); logs estruturados em stdout sem
  dado pessoal; imagem Docker multistage rodando como usuário nãoroot.
 CI mínimo (lint + testes + build) já vale a pena desde já; CD completo
  segue manual/documentado por ora (ver seção 8).

## 3. Segurança

### 3A. Autenticação
 Login: **apelido + senha** (Argon2id, mínimo 10 caracteres, sem regra
  de composição forçada). Login aceita apelido OU email.
 **Email é coletado no cadastro**  exceção deliberada à regra geral de
  anonimato do projeto, formalizada no `CLAUDE.md`. Motivos: (1)
  recuperação de conta por email em vez de chave de recuperação exibida
  uma única vez; (2) envio de itens físicos da loja. Fica na própria
  tabela `usuario` (`VARCHAR NOT NULL UNIQUE`), isolado por regra de
  aplicação (nunca em query/DTO/log de relato, presença ou reunião).
 Admin: **MFA via TOTP** (app autenticador)  login inicial e etapa TOTP
  validam reCAPTCHA cada um com seu próprio widget (`AuthService.loginComMfa`).
 JWT carrega `role` como claim assinado, revalidado a cada requisição.
  Autorização sempre checada no backend (`@PreAuthorize`), nunca inferida
  pela rota do frontend.
 Rate limiting e bloqueio de tentativas de login por conta/sessão, nunca
  por IP (ver seção 3D). Mensagens de erro de login genéricas (evita
  enumeration).

### 3B. Roles de usuário e limite de admins
 Campo `role` simples na tabela de usuário: `PARTICIPANTE` | `ADMIN`
  (enum via `VARCHAR` + `CHECK`, sem tabela de permissões granulares).
 Atribuição/revogação via tela de produto (`adminusuarios`) e endpoint
  `PUT /api/v1/admin/usuarios/{id}/role`
  (`UsuarioService#editarRole`). Regras de proteção: admin não pode se
  autorebaixar; não é possível remover o último admin do sistema; existe
  **limite máximo de admins ativos** (`ADMIN_LIMITE_MAXIMO`, env
  obrigatória sem default, failfast no startup se ausente/inválida) que
  bloqueia novas promoções acima do teto. Contagem de admins usa lock
  pessimista (`buscarPorRoleParaAtualizacao`) tanto para remoção do
  último admin quanto para promoção, fechando race condition de
  promoções simultâneas. Ver `backend/docs/api/contratousuarioadmin.md`.
 Bootstrap do primeiro admin via variável de ambiente/secret, com senha
  forçando troca no primeiro login.

### 3C. Superfície de ataque  pontos centrais
 Autorização por role obrigatória em todo endpoint admin, isolado sob
  `/api/v1/admin/...` com políticas próprias de rate limit/CORS/log.
 XSS armazenado é o risco central do CMS  qualquer campo de conteúdo
  que aceite marcação precisa ser sanitizado no backend com allowlist
  (nunca confiar em sanitização clientside).
 Log de auditoria em toda alteração de conteúdo público (quem, quando,
  entidade, snapshot do valor anterior  ver seção 5).
 Email (dado coletado no cadastro) segue a mesma política de dados
  sensíveis: nunca em log, acesso restrito, nunca junto de dado de
  participação/relato.

### 3D. Rate limiting
 **Mecanismo único, sem lib nova**: `RateLimiterService` + `TokenBucket`
  (`backend/src/main/java/br/com/jogadoresanonimos/common/ratelimit/`) 
  token bucket em memória (`ConcurrentHashMap` de chave → bucket),
  chamado direto do service layer via `rateLimiterService.check(chave,
  capacidade, janela)`, lançando `RateLimitedException` → HTTP 429
  (`error.code = RATE_LIMITED`). Todo novo limite reaproveita esse
  mecanismo, não introduz Bucket4j/Redis/etc  volume do projeto (~2.500
  req/dia) não justifica.
 **Chave nunca é IP.** Login/registro/esquecisenha/redefinirsenha usam
  hash HMAC (`HmacHasher`, chave via secret de ambiente) do identificador
  ou do token de reset  nunca o valor em claro, nunca IP. Ações
  autenticadas (relato, conta, biblioteca, upload) usam o UUID técnico do
  usuário já validado pelo JWT, que não é dado pessoal por si só. Alinhado
  à regra de anonimato do projeto: rate limit por IP é padrão de mercado
  aceitável, mas aqui a alternativa (chave hasheada/id técnico) resolve o
  mesmo problema sem persistir IP nem em memória.

| Endpoint | Chave | Limite | Motivo |
|||||
| `POST /api/v1/auth/login` | hash do identificador (apelido/email) | 5 / 1 min | Padrão de mercado contra brute force (OWASP: ~5/min é ponto de partida comum); reCAPTCHA soma como segunda camada; mensagem de erro genérica evita enumeration. |
| `POST /api/v1/auth/registro` | hash de `apelido\|email` | 5 / 1 min | Contém spam de criação de conta em massa; reCAPTCHA soma. |
| `POST /api/v1/auth/esquecisenha` | hash do identificador | 5 / 1 min | Evita abuso de envio de email (custo de terceiro) e reduz sinal de enumeração  resposta já é sempre genérica independente de a conta existir. |
| `POST /api/v1/auth/redefinirsenha` | hash do token de reset recebido | 5 / 1 min | Token é aleatório de 32 bytes (baixo risco de brute force prático), mas defesa em profundidade é barata e consistente com o resto do fluxo. |
| `PUT /api/v1/usuarios/me` (atualizar conta) | UUID do usuário autenticado | 5 / 1 min | Evita uso do endpoint para varrer/confirmar apelidos ou emails já cadastrados via resposta de conflito (409), além de conter flood trivial. |
| `PUT /api/v1/usuarios/me/senha` (alterar senha) | UUID do usuário autenticado | 5 / 1 min | Ainda que exija JWT válido + senha atual, limita tentativa de força bruta contra a senha atual em caso de token comprometido/reuso de sessão. |
| `POST/DELETE /api/v1/usuarios/me/biblioteca/{id}` | UUID do usuário autenticado | 30 / 1 min | Ação de baixo risco (favoritar/desfavoritar arquivo já autorizado); limite mais permissivo só para conter flood/loop de cliente com bug. |
| `POST /api/v1/relatos` (enviar relato) | UUID do autor | 5 / 10 min | Conteúdo sensível gerado por usuário, sujeito a moderação. |
| `POST /api/v1/admin/imagens` (upload) | UUID do admin + limite global | 10/min por admin + 60/min global |  |
| `POST /api/v1/admin/arquivos` (upload PDF) | UUID do admin + limite global | 10/min por admin + 60/min global |  |

**Endpoints avaliados e descartados (sem rate limit dedicado)**: todos os
`GET` públicos (reuniões, hotlines, escritórios regionais, FAQ, CMS,
vinte perguntas, mural de apoio, avisos, reflexão do dia, relatos
aprovados, download de arquivo)  leitura pública, sem efeito colateral
de escrita/custo de envio; sem sinal concreto de abuso a mitigar hoje.
Revisitar se algum virar alvo de scraping/DDoS observado em produção
(rate limit global "leve" por rota, não por usuário, seria a primeira
opção). Não há endpoint de "contato" serverside no projeto (fluxo é
estático, WhatsApp/telefone)  sem superfície de spam de formulário a
limitar.

### 3E. Row Level Security (RLS) do Postgres  avaliado e descartado
 **Contexto**: revisão de defesa em profundidade contra IDOR (acesso a
  relato/presença/arquivo de outro usuário) levantou RLS nativo do
  Postgres como camada adicional, abaixo da aplicação  chegou a ser
  desenhado o escopo completo (tabelas candidatas: relato, presença de
  reunião, biblioteca de arquivo do usuário; mecanismo de propagação de
  identidade via `SET LOCAL app.current_user_id` por transação) antes de
  ser descartado.
 **Riscos identificados que motivaram descartar**:
  1. **Único consumidor do banco é o próprio backend Spring**  não há
     BI, relatório ou serviço externo lendo o Postgres diretamente. RLS
     resolve um problema de "múltiplos consumidores com necessidade de
     isolamento automático de linha", que este projeto não tem: a camada
     de isolamento já é 100% aplicação (service layer + `@PreAuthorize`).
  2. **Risco de mascarar bug**: policy de RLS mal configurada não dá
     erro  retorna silenciosamente 0 linhas. Um bug de policy vira "meu
     relato sumiu" em vez de uma falha visível e rastreável, dificultando
     debug.
  3. **Risco de vazamento de contexto entre requisições**: o projeto usa
     pool de conexão compartilhado (HikariCP). RLS depende de contexto de
     sessão Postgres (`SET LOCAL app.current_user_id = '...'`)  usar
     `SET` normal (sem `LOCAL`, ou fora do escopo exato da transação)
     vazaria o identificador de um usuário para a próxima requisição que
     reusar a mesma conexão do pool, expondo dado de um usuário para
     outro  o oposto do requisito de anonimato do projeto, e um erro
     fácil de cometer sem perceber em code review.
 **Alternativa implementada, cobrindo o mesmo risco residual**: suíte de
  teste de integração dedicada a IDOR, `IdorEndpointsSensiveisIT.java`
  (`backend/src/test/java/br/com/jogadoresanonimos/security/`), 13
  testes, validada 13/13 contra Postgres real  cobre acesso cruzado a
  relato, presença e arquivo de biblioteca entre usuários diferentes,
  validando que a resposta é 404 (não 403, ver seção 3F) em cada caso.
 **Gatilho de revisão**: se um dia existir um segundo consumidor direto
  do banco (ex: ferramenta de BI, réplica de leitura para relatório), RLS
  volta à mesa  o isolamento por aplicação sozinho deixa de ser
  suficiente porque o novo consumidor não passa pelo service layer.

### 3F. Tratamento de erros
 **Mecanismo único**: um único `@RestControllerAdvice` global (não um
  por módulo/controller), com um `@ExceptionHandler` por categoria de
  exceção. Nenhum controller captura exceção de negócio manualmente  só
  deixa propagar para o advice. Evita duplicação e drift de formato entre
  endpoints; adicionar exceção nova é só adicionar handler novo.
 **Exceções de negócio vivem no service layer** (ex.:
  `RecursoNaoEncontradoException`, `ConflitoDeEstadoException`), lançadas
  no service, nunca no controller  o service não conhece `HttpStatus`,
  mantendo a separação Controller → Service → Repository.
 **Formato de resposta**: sempre o envelope padronizado do projeto, com
  `data: null` em caso de erro e `error` como objeto (`code` estável para
  lógica do frontend + `message` amigável em português, seguro para
  exibir):
  ```
  { "success": false, "data": null, "error": { "code": "...", "message": "..." } }
  ```

| Categoria | HTTP | `error.code` | O que pode ir na mensagem ao cliente |
|||||
| Validação de entrada | 400 | `VALIDATION_ERROR` | Específica sobre o próprio input (ex. "senha deve ter no mínimo 10 caracteres")  é regra pública, não segredo de sistema; pode incluir `error.fields` (lista `{campo, mensagem}`) para o Angular destacar o campo |
| Autenticação (login falho) | 401 | `UNAUTHENTICATED` | Mensagem genérica única  regra do projeto de não diferenciar "usuário não existe" de "senha errada" |
| Autorização (sem permissão/role) | 403 | `FORBIDDEN` | Genérica ("acesso negado")  usar só quando faz sentido revelar que o recurso existe mas a ação é vedada por role (ex. painel admin) |
| Acesso a recurso de outro dono (IDOR) | **404**, não 403 | `NOT_FOUND` | Ver ponto de segurança abaixo  nega a própria existência do recurso |
| Não encontrado | 404 | `NOT_FOUND` | Genérica |
| Conflito de estado (ex. apelido já existe) | 409 | `CONFLICT` | Pode ser específica (regra pública) |
| Rate limit excedido | 429 | `RATE_LIMITED` | Genérica ("muitas tentativas, aguarde"), pode incluir `RetryAfter` |
| Erro interno não mapeado (fallback) | 500 | `INTERNAL_ERROR` | 100% genérica ("ocorreu um erro, tente novamente") + código de erro correlato para busca em log |

 **Ponto de segurança mais importante para este projeto: 404 em vez de
  403 para IDOR.** Se um PARTICIPANTE tenta acessar relato/presença de
  outro usuário, a resposta correta é 404  403 confirmaria a existência
  do recurso para quem não tem acesso a ele, o que já é, por si, um
  pequeno vazamento de metadado. Como relatos/presença são o dado mais
  sensível do sistema, esse detalhe importa mais aqui do que em uma API
  comum.
 **Nunca vazar, em nenhuma mensagem de erro ao cliente**: stack trace,
  nome de exception, SQL/mensagem de driver JDBC/Postgres, nome de
  coluna/tabela, versão de dependência, path de arquivo/pacote interno.
  Em prod, página de erro padrão do Spring Boot desabilitada
  (`server.error.includestacktrace=never`,
  `server.error.includemessage=never`,
  `server.error.includeexception=false`)  o erro mais comum é a
  whitelabel error page do Spring vazando `trace`/`message`/`path` por
  omissão.
 **Correlação para suporte sem dado pessoal**: código de erro/`traceId`
  gerado por requisição (não relacionado ao usuário) tanto no log interno
  (nível `ERROR`, com stack trace completa) quanto, opcionalmente, na
  resposta ao cliente  permite localizar o incidente no log sem dado
  identificável do usuário. Segue a mesma regra de anonimato da seção 5.
 **Local no código**: o `@RestControllerAdvice` vive em pacote comum
  (ex. `common.exception` ou `web.error`), separado dos pacotes de
  domínio  infraestrutura transversal, não regra de negócio de módulo.

### 3G. Headers de segurança HTTP (frontend)
 Frontend estático (Vercel) declara três headers de resposta para todas
  as rotas (`frontend/vercel.json`, `source: "/(.*)"`, bloco `headers`):
   **CSP**: allowlist fechada mapeada contra os domínios externos
    realmente usados no código (não lista genérica)  Google reCAPTCHA e
    Google Fonts (`scriptsrc`/`stylesrc`/`fontsrc`), YouTube embed via
    `youtubenocookie.com` (`framesrc`), API própria mais IBGE, ViaCEP e
    Nominatim/OpenStreetMap (`connectsrc`, usados nos formulários de
    endereço/geolocalização). `objectsrc 'none'`, `frameancestors
    'none'` (equivalente a clickjacking protection, reforça o
    `XFrameOptions` abaixo), `baseuri 'self'`, `formaction 'self'`,
    `upgradeinsecurerequests`.
   **`XFrameOptions: DENY`**: bloqueia o site sendo embutido em
    `iframe` de terceiro (clickjacking).
   **`XContentTypeOptions: nosniff`**: impede o browser de inferir
    `ContentType` diferente do declarado pelo servidor (mitiga alguns
    vetores de XSS via MIME sniffing).
 **Por que na Vercel e não no Traefik**: o frontend é servido
  estaticamente pela Vercel (deploy separado do backend, ver seção 1),
  então o header precisa ser declarado na configuração da própria Vercel
   Traefik só está na frente da API (`api.jogadoresanonimos.com.br`),
  nunca do frontend.

### 3H. CORS
 **Cenário real do projeto**: origem única e conhecida (o próprio
  domínio Angular do site), não é API pública multicliente. CORS aqui é
  puramente uma trava de segurança contra requisição crossorigin feita
  por *outro site* no browser da vítima  não é mecanismo de "permitir
  integração".
 **Política: allowlist fechada, nunca wildcard.** `allowedOrigins` é uma
  lista fechada de origens conhecidas do próprio site (nunca `*`  e
  nunca `*` combinado com `allowCredentials(true)`, inválido pelo spec e
  pelo Spring). Em produção a lista cobre o domínio canônico e o de
  redirect, cada um com e sem `www.`: `https://jogadoresanonimos.com.br`,
  `https://www.jogadoresanonimos.com.br`, `https://jogadoresanonimos.org.br`,
  `https://www.jogadoresanonimos.org.br`  o `.org.br` faz redirect HTTP
  pro `.com.br`, mas antes do redirect completar o browser ainda envia
  `Origin: https://jogadoresanonimos.org.br`, por isso ele precisa estar
  na allowlist também. `allowedMethods` e `allowedHeaders` restritos ao
  que é realmente usado (`GET, POST, PUT, PATCH, DELETE` /
  `Authorization, ContentType`), não `*`. `maxAge` alto (ex. 3600s) para
  reduzir preflight, sem abrir mão da política.
 **Dev vs prod: diferente, via Spring Profiles.** Origens permitidas vêm
  de property (`app.cors.allowedorigins`, tipo `List<String>`)
  alimentada pela variável `CORS_ALLOWED_ORIGINS` (múltiplas origens
  separadas por vírgula, sem espaço)  `http://localhost:4200` em dev, a
  lista de domínios reais em prod. As duas listas nunca convivem no mesmo
  profile ativo; erro clássico a evitar é deixar `localhost` "por
  garantia" na lista de prod.
 **Onde centralizar**: um único `@Bean CorsConfigurationSource`
  registrado dentro da `SecurityFilterChain` do Spring Security
  (`http.cors(...)`), **não** via `WebMvcConfigurer#addCorsMappings`.
  Motivo: o projeto já usa Spring Security para autenticação/autorização;
  configurar CORS numa camada separada da security chain cria risco de as
  duas ficarem desalinhadas (uma libera, outra barra, ou uma faz bypass
  da outra). Centralizado na security chain, há uma única fonte de
  verdade e o preflight `OPTIONS` é tratado antes da exigência de JWT.
 **Rotas `/api/v1/admin/...`: mesma política de CORS, sem tratamento
  especial.** CORS não é a camada de proteção que importa ali  seria
  redundante e daria falsa sensação de segurança, pois é enforced pelo
  *browser*, não pelo servidor (não impede chamada direta via
  `curl`/script/token roubado). A proteção real das rotas admin é
  autorização por role (`@PreAuthorize`, `ROLE_ADMIN` verificado no
  backend), rate limiting mais agressivo e log de auditoria  não CORS.
  Se no futuro o dashboard admin virar aplicação Angular separada
  (subdomínio próprio), a allowlist ganha uma segunda entrada explícita,
  mas continua sendo lista fechada.

## 4. Dados e armazenamento

### 4A. Storage de imagem  disco local
 **Decisão**: disco local persistente do servidor de produção, não
  bucket S3compatible externo  evita decidir titularidade de conta
  terceira (cliente vs dev) antes de precisar, dado o volume baixo do
  projeto.
 `StorageClient` (interface) isola essa decisão do resto do código 
  implementação de disco (`LocalDiskStorageClient`) usada em dev, teste e
  produção, sem tocar em código de domínio. Env vars:
  `STORAGE_STUB_DIR`/`STORAGE_PUBLIC_BASE_URL`.
 Validação de segurança: remoção de EXIF antes de persistir, validação
  por magic bytes (não extensão/`ContentType`), sem aceitar SVG, sem
  upload por URL, checagem antipolyglot em PDF, GC de imagem órfã
  (`@Scheduled` diário, período de graça de 7 dias), rate limit de upload
  (10/min por admin + 60/min global, ver seção 3D).
 **Segurança de path**: chave de arquivo é sempre `UUID.randomUUID()`
  gerado internamente, nunca vem de input do usuário  evita path
  traversal e colisão de nome. `download()` mantém checagem extra
  (`normalize().startsWith(baseDir)`) como defesa adicional, mais
  relevante aqui do que seria com um bucket externo isolado, já que o
  disco agora é compartilhado com o próprio sistema operacional/aplicação.
 **Performance**: disco local elimina a latência de rede de um bucket
  externo (upload/download é I/O local)  ganho pequeno mas real nesse
  volume, sem custo adicional.
 **Custo**: elimina custo de bucket externo  disco local já está pago
  dentro do custo do próprio servidor.
 **Simplicidade operacional líquida**: um serviço a menos para
  provisionar/monitorar/ter credencial (bucket externo) em troca de um
  requisito operacional que precisa existir de fato  a rotina de backup
  (seção 4C) é a peça que não pode ser esquecida.

### 4B. Crescimento de disco sem controle
 **Risco real**: sem bucket gerenciado, não há quota nativa do provedor
  nem alerta de billing automático. Disco cheio no servidor de produção é
  mais sério que estourar quota de bucket: pode afetar o próprio sistema
  operacional/aplicação, não só a feature de upload.
 **Mitigação**:
  1. **Quota de aplicação**: limite de 5MB por arquivo; limite agregado
     monitorado por consulta simples ao Postgres (`SUM(tamanho_bytes)` na
     tabela de metadado de imagem/PDF)  não precisa de mecanismo novo.
  2. **Alerta de disco**: monitoramento de espaço via `devops` (alerta
     simples ao atingir 7080% de uso), já que não existe mais o alerta
     de billing de provedor cloud.
  3. **Política de retenção/expurgo**: imagem *associada* a conteúdo
     removido (bloco de CMS, banner do mural) cai no mesmo fluxo de GC de
     órfã já existente, sem mecanismo novo. Ao deletar o conteúdo pai
     (DELETE simples, sem softdelete de imagem), a `Imagem`
     correspondente naturalmente para de ter referência ativa e o GC a
     coleta no ciclo seguinte, dentro do mesmo período de graça de 7
     dias. `ImagemOrfaGcJob` checa tanto `CmsBlocoRepository` quanto
     `BannerMuralRepository` (`existsByImagemId` nos dois) na cadeia de
     filtro, para que um banner do mural ativo não seja erroneamente
     coletado. `Relato` e `Aviso` não têm campo de imagem, fora do escopo.
 **Por que essa mitigação e não mais**: no volume atual, estimativa
  grosseira de crescimento de disco é baixíssima (poucas dezenas de MB/dia
  mesmo em pico de uploads)  não justifica hoje um mecanismo automatizado
  de quota "dura" (bloquear upload ao atingir X GB) além do alerta
  observável por `devops`.

### 4C. Backup do diretório de storage
 Com disco local, durabilidade/backup deixam de vir embutidos num
  provedor gerenciado (diferente de um bucket S3compatible)  é a
  principal perda real da decisão de storage, e precisa de ação
  explícita.
 **Decisão**: o diretório de storage de imagem/PDF em produção entra na
  rotina de backup do servidor junto com o dump do Postgres (mesma
  cadência/retenção do banco)  sem isso, perda de disco = perda
  permanente de todo binário enviado por usuários, sem recuperação. Este
  ponto é **bloqueante para produção**: não faz sentido ativar
  `LocalDiskStorageClient` em produção sem essa rotina existindo, mesmo
  com volume baixo  a perda é total e irreversível, o risco não é de
  escala, é de ausência de mecanismo.
 **Escopo**: cópia periódica do diretório de storage
  (snapshot/rsync/tarball, mecanismo exato é decisão de `devops`) com
  retenção equivalente à do banco  o metadado (nome, tamanho, chave do
  arquivo) já vive no Postgres e por si só não recupera o binário; os
  dois precisam ser restauráveis em conjunto e de forma consistente (sem
  ficarem defasados um do outro a ponto de o metadado apontar para
  arquivo que não existe mais no backup, ou viceversa).
 **Restore validado ponta a ponta**: testado em ambiente isolado
  (Postgres descartável subido via Docker especificamente para o teste,
  nunca contra o Postgres de produção)  dump aplica sem erro de restore,
  contagem de linhas restauradas bate com o esperado. Fecha o risco de
  "backup existe mas nunca foi provado restaurável"  um backup não
  testado não é, na prática, garantia de recuperação.

### 4D. Escala horizontal futura (múltiplas instâncias de app)
 **Limitação inerente à decisão atual**: disco local é local *daquela
  instância* do servidor. Se o backend um dia rodar em mais de uma
  instância simultânea, as instâncias não compartilham o mesmo disco por
  padrão  uma grava um upload, a outra não o enxerga, quebrando
  consistência de leitura.
 **Decisão para agora**: não implementar nada preventivamente. Com uma
  única instância (cenário atual), disco local é suficiente e mais
  simples  replicar disco entre instâncias (volume de rede compartilhado,
  NFS) ou voltar a storage compartilhado (S3compatible) só se justifica
  quando a segunda instância existir de fato.
 **Gatilho explícito**: o dia em que `devops` precisar subir mais de uma
  instância do backend simultaneamente atrás do load balancer (seja por
  disponibilidade, seja por volume de tráfego crescido ordens de
  grandeza), a decisão de storage precisa ser revisitada **antes** desse
  deploy  volta à mesa a escolha entre (a) storage compartilhado tipo
  S3compatible ou (b) volume de rede compartilhado. A interface
  `StorageClient` já existente é o que torna essa futura migração
  barata: trocase a implementação, não o contrato usado pelo resto do
  código.

## 5. Logs e auditoria

Dois tipos de log, com propósito, conteúdo, retenção e acesso diferentes.

### 5A. Log operacional (troubleshooting técnico)
 **O que vai**: método/path da requisição (sem query string sensível),
  status code, latência, código de erro correlato; em erro técnico,
  stack trace completa e nome da exception (único lugar onde detalhe
  técnico completo pode existir); tentativa de login falho/rate limit
  atingido identificada só por identificador técnico não reversível
  (hash de token/conta), nunca por apelido, email ou IP.
 **Nunca vai**: apelido, email, conteúdo de relato, IP, qualquer campo
  de payload de rota sensível  mesmo mascarado (mais seguro simplesmente
  omitir do que mascarar mal). Lista explícita de rotas excluídas de log
  de payload no filtro de log HTTP (ex. `/api/v1/auth/**`,
  `/api/v1/relatos/**`, `/api/v1/reunioes/**/presenca`,
  `/api/v1/depoimentos/**`), em vez de depender de revisão manual por
  endpoint.
 **Retenção**: curta, 14–30 dias  suficiente para debug de incidente
  recente, minimizando dado guardado (alinhado a minimização de dados da
  LGPD). Rotação automática a cargo de `devops`.
 **Acesso**: desenvolvedores/`devops` em plantão. Sem necessidade de
  trilha de auditoria própria sobre quem consultou (não há dado pessoal
  ali, se a regra acima for seguida).

### 5B. Log de auditoria administrativa (rastreabilidade de ação de admin)
 **O que vai**: quem (id técnico do admin  aceitável identificar o
  admin agente, diferente do participante, que é o sujeito que o
  anonimato do projeto protege), quando, ação
  (criar/editar/publicar/despublicar/excluir), entidade/id do recurso,
  snapshot do valor anterior.
 **Nunca vai**: dado de participante mesmo quando indireto  ex., se um
  admin aprova/rejeita um depoimento, o log registra "depoimento X mudou
  de pending para approved", nunca o texto do depoimento nem o autor.
 **Retenção**: mais longa, 12–24 meses  permite investigar abuso de
  poder de admin ou reverter erro de conteúdo muito depois do fato; ainda
  assim não "para sempre", revisar/expurgar após esse período (política
  de retenção geral de LGPD a fechar com `po`/`devops`).
 **Acesso**: restrito a administrador(es) de sistema, não a todo admin
  de conteúdo nem a desenvolvedor por padrão  só em investigação pontual
  documentada. Evita que a própria auditoria vire nova superfície de
  vigilância interna.

### 5C. Log estruturado em arquivo, com rotação
 `logbackspring.xml` tem, só no profile `prod`, um `RollingFileAppender`
  em formato JSON (`timestamp`, `level`, `logger`, `thread`, `message`)
  escrevendo em `${LOG_FILE_PATH:/app/logs}/jogadoresanonimos.log`, além
  do `ConsoleAppender` (ativo nos dois profiles)  soma um destino
  persistente em disco ao log em stdout.
 **Rotação**: `SizeAndTimeBasedRollingPolicy`  corta por tamanho (50MB)
  ou diariamente, o que vier primeiro; `maxHistory=7` (7 dias de
  histórico, arquivos antigos comprimidos em `.gz`); `totalSizeCap=500MB`
  como teto absoluto de disco ocupado por log, independente da contagem
  de dias.
 **Por que 7 dias**: alinhado ao mesmo prazo de retenção usado no backup
  do servidor (seção 4C)  consistência com a política de retenção de
  LGPD já adotada em outro mecanismo do projeto, e dentro da janela
  "1430 dias" do log operacional (5A).
 **Persistência**: volume Docker dedicado `backend_logs`
  (`stack.prod.yml`), montado em `/app/logs` no container  sobrevive a
  redeploy/restart, permitindo consultar log de incidente mesmo depois de
  um rolling update (`update_config: order: startfirst`, seção 2C).
 **Sem dado pessoal**: mesma regra do log operacional (5A)  o appender
  novo não muda o que é logado, só onde persiste; a disciplina de excluir
  payload de rota sensível continua no filtro de log HTTP.

### 5D. Por que a separação importa neste projeto
Log operacional tende a vazar dado pessoal por descuido (é o que captura
request/response bruto)  por isso a disciplina de excluir payload de
rotas sensíveis precisa estar no filtro, não na memória de quem escreve
cada endpoint. Log de auditoria é sobre ação administrativa, não sobre
comportamento de participante  pode (e deve) identificar o admin agente,
mas trata qualquer dado de participante referenciado só por id técnico do
recurso, nunca por conteúdo.

## 6. Anonimato e privacidade

 **Pilar central de arquitetura, não só de UI**: nunca logar, persistir
  ou expor dado que permita identificar um usuário (nome real, IP, email
  pessoal) junto com dado de participação ou relato. Em conflito entre
  prática técnica comum (ex: logar IP para rate limiting) e esse
  requisito, o anonimato vence  buscase a alternativa (identificador de
  sessão não reversível). Aplicado de forma concreta em: rate limiting
  por hash/UUID técnico, nunca IP (seção 3D); logs sem dado pessoal
  (seção 5); RLS descartado em parte por reforçar isolamento por
  aplicação sem introduzir novo canal de identidade (seção 3E); IDOR
  respondendo 404 (seção 3F); Traefik confiando em `XForwardedFor` só
  para observabilidade de infraestrutura, nunca para decisão de negócio
  sobre usuário (seção 2G).
 **Email é a exceção deliberada** a essa regra geral  coletado no
  cadastro (seção 3A), formalizada no `CLAUDE.md`, isolada por regra de
  aplicação (nunca em query/DTO/log de relato, presença ou reunião).
 **Mensagem de cadastro específica por campo**: o endpoint de cadastro
  informa explicitamente qual campo colidiu quando `apelido` ou `email`
  já está em uso ("Esse apelido já está em uso, escolha outro" vs "Esse
  email já está cadastrado"). Risco aceito conscientemente: user
  enumeration no cadastro (é possível varrer apelidos/emails para
  descobrir contas existentes). Escopo estrito: só o cadastro  a
  mensagem de login (401) continua genérica, sem indicar se o
  identificador existe ou a senha está errada. Mitigação complementar:
  rate limit global por ação (registrovarreduraglobal, sem identificar
  quem faz a requisição, preservando o requisito de anonimato) além do
  rate limit já existente por identificador (seção 3D). Ver
  `backend/docs/api/contratoauthcmsupload.md`, seção 1.1, para o
  contrato completo.

## 7. Painel administrativo (CMS) e roles

 **Todas as páginas institucionais são editáveis** pelo admin  como são
  poucas e estáveis, não vale a pena separar "hardcoded vs. editável".
  Simplifica o modelo: uma regra só.
 Modelo de dados: `cms_pagina` (slug, título) + `cms_bloco` (blocos
  ordenados dentro da página, tipo `texto` ou `imagem`).
 Recursos com modelo próprio, **fora do CMS genérico**: `reuniao`,
  `aviso`, `reflexao_diaria`, `relato`  mantêm tabela e regras próprias,
  não viram blocos de CMS.
 Painel é **tela separada** (lista de conteúdos → formulário), **não**
  edição inline sobre a página pública.
 Texto rico limitado a formatação básica (negrito/itálico/link)  nunca
  HTML livre (evita quebra de layout e XSS armazenado, ver seção 3C).
 **Sem versionamento/histórico completo** (sem "desfazer" com diff
  visual)  apenas auditoria simples: `atualizado_em` / `atualizado_por`
  (ou tabela de log com snapshot do valor anterior) nas entidades
  editáveis por admin.
 Fora de escopo por ora: editor de layout/draganddrop, workflow de
  aprovação multietapas.
 **Nomenclatura `enderecos` (público) vs `escritoriosregionais`
  (admin/service)  divergência deliberada, não é inconsistência a
  corrigir.** Página pública é `enderecos` por clareza de UX para o
  usuário final; admin e camada de service continuam
  `escritoriosregionais`/`EscritorioRegionalService`, nome técnico/de
  domínio do recurso no backend  são públicos diferentes (usuário final
  vs. camada técnica) com nomes propositalmente diferentes.
 **Roles de usuário e limite de admins**: ver seção 3B.

## 8. Filtro e paginação de reuniões (clientside)

 **Cenário**: painel admin de reuniões precisa de filtro (busca por
  texto, categoria, dia da semana); página pública de reuniões precisa de
  paginação, mantendo a ordenação por distância (geolocalização do
  usuário) já existente.
 **Decisão**: filtro (admin) e paginação "carregar mais" (público) são
  **100% clientside**  sem novo endpoint, sem parâmetro de
  paginação/filtro na API. O backend continua devolvendo a lista completa
  de reuniões já ordenada; o front filtra/pagina em memória.
 **Por quê**: volume atual é de **94 reuniões**  filtrar/paginar em
  memória no browser é imperceptível nesse tamanho, e implementar
  paginação/filtro serverside (parâmetros, contrato de API novo, testes
  de contrato) seria complexidade desproporcional ao problema real.
  Também evita reimplementar a ordenação por distância no backend, que já
  existe e funciona clientside.
 **Gatilho de revisão**: se o total de reuniões passar de
  **~1.0002.000**, revisitar  nesse volume o payload completo e o custo
  de filtro em memória no browser deixam de ser desprezíveis, e
  paginação/filtro serverside passam a valer a pena.

## 9. Processo de deploy (CI/CD)

 **Estado atual**: deploy manual e documentado (ver seção 2E para o
  processo de build/rollback de imagem)  decisão consciente, não lacuna.
  CI mínimo (lint + testes + build) já vale a pena desde já; CD completo
  (pipeline automatizado publicando direto em produção) espera a cadência
  de deploy justificar o investimento.
 **Reavaliar** quando a cadência de deploy manual virar gargalo real
  (frequência alta o suficiente para o processo manual custar tempo de
  forma perceptível)  nesse momento, build automatizado + registry de
  imagem é o próximo passo.

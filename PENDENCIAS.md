# Pendências

> Arquivo único de pendências reais em aberto. Itens concluídos não ficam
> aqui — histórico de decisão/implementação vive em `Decisoes de
> Sistemas.md` e `.claude/knowledge/`.

## Estado atual da infraestrutura (2026-08-08)

- **Decisões de hospedagem: TODAS FECHADAS** (ver histórico em `Decisoes
  de Sistemas.md`). Não há mais nada de hospedagem esperando decisão do
  cliente — o que resta é **execução**.
- **VPS**: acesso SSH recebido e funcionando (2026-08-08) —
  `eduardo@143.95.219.110`, autenticação por chave já configurada
  (`ssh-keygen ed25519`, chave copiada pra `~/.ssh/authorized_keys` no
  servidor). **Bloqueado**: usuário `eduardo` não está no grupo `sudo`
  nem no grupo `docker` — sem privilégio pra instalar/configurar nada.
  Existe uma pasta `/srv/infra-site` (dono `eduardo:infra-site`, grupo
  próprio, vazia) já preparada pelo colega que administra o servidor —
  sinal de que ele pretende cuidar da parte de sistema (Docker, firewall,
  Nginx, Certbot) e deixar só a operação de deploy pra nós. **Precisa
  perguntar pra ele**: (1) ele vai instalar Docker + colocar `eduardo` no
  grupo `docker` pra rodar `docker compose` sem sudo? (2) ou é só pra
  colocar os arquivos em `/srv/infra-site` e ele quem sobe? (3) SSH
  hardening (desligar login por senha, mudar porta) fica só com ele?
  Até essa resposta, o "Checklist técnico do VPS" abaixo segue pendente
  de definição de quem executa cada item.
- **Contas criadas**: Resend, Vercel, GitHub. Domínio é o que já existia
  na HostGator (site WordPress obsoleto), `jogadoresanonimos.com.br`.
- **Provedor decidido**: VPS Cloud NVMe HostGator, 2GB RAM (NVMe 2), sem
  painel. Postgres em container Docker. Split frontend (Vercel) / backend
  + Postgres (VPS), backend exposto em `api.jogadoresanonimos.com.br`.

## Pode fazer agora

### 1. Resend — plugar no app
1. ~~Adicionar/verificar domínio `jogadoresanonimos.com.br` no Resend~~ —
   **feito e verificado (2026-08-08)**. 4 registros DNS criados no Zone
   Editor HostGator: TXT `resend._domainkey` (DKIM), MX `send` (prioridade
   10), TXT `send` (SPF, subdomínio dedicado do Resend — sem conflito com
   SPF de outro provedor na raiz), TXT `_dmarc` (`v=DMARC1; p=none;
   rua=mailto:jogadoresanonimos@yahoo.com.br`). Status Resend: `Verified`,
   "Your domain is ready to send emails."
2. ~~API Key do Resend~~ — **feita, gerada e colada no `.env`
   (2026-08-08)**.
3. ~~Plugar no `EmailService` (hoje só loga)~~ — **feito e testado com
   envio real (2026-08-08)**. Envio via API HTTP do Resend (`RestTemplate`
   já existente no projeto, sem dependência nova), property
   `app.email.resend-api-key` (env `RESEND_API_KEY`). Cobre os dois
   e-mails da recuperação de senha (link usuário comum, código admin).
   Fail-fast no boot em prod no `ProdConfigValidator` — profile `prod`
   quebra o boot se `RESEND_API_KEY` estiver vazia, junto do recaptcha.
   **Correção de bug real** (achada só testando com envio de verdade):
   remetente técnico estava `naoresponda@send.jogadoresanonimos.com.br`
   (subdomínio usado só pros registros DNS MX/SPF), mas o domínio
   verificado no Resend é a raiz `jogadoresanonimos.com.br` — corrigido
   para `naoresponda@jogadoresanonimos.com.br`. Teria quebrado em
   produção sem o teste real. Copy/visual revisados pelo `ux-ui` (tom
   acolhedor, sem julgamento) e cores oficiais da JA aplicadas
   (`#742C24` bordô institucional, `#D4A843` dourado, sem borda). Destrava,
   de verdade, a recuperação de senha (ver seção "Features de e-mail"
   abaixo) e é pré-requisito pra verificação de e-mail no cadastro.
4. **Logo da JA no e-mail — pendente, depende do deploy do frontend no
   Vercel.** E-mail HTML não pode referenciar caminho relativo do site
   (`/images/header/logo-jogadores-anonimos-monograma.png`), precisa de
   URL pública absoluta — hoje não existe porque nada está deployado
   ainda. Assim que o frontend subir no Vercel, a imagem passa a ter URL
   real e o logo pode entrar no template.
5. E-mail oficial da associação (`jogadoresanonimossaopaulo@gmail.com`)
   entra como contato/reply-to de referência no conteúdo dos e-mails, não
   como remetente técnico/`from` (Gmail não pode ser verificado como
   domínio próprio). Rodapé atual usa texto genérico ("fale conosco pelo
   site oficial") em vez do e-mail explícito — decisão do usuário
   (2026-08-08), mantida.

## Setup do VPS (executar quando o acesso chegar)

`devops` detalha e executa. Decisões já fechadas — aqui é só o que falta
fazer.

### Checklist técnico do VPS (do zero)
- Instalar Docker + Docker Compose no Ubuntu.
- Postgres em container Docker (decisão fechada) — só executar.
- Configurar firewall (`ufw`): liberar só `80`/`443`/`22` (ou porta SSH
  custom, ver abaixo), fechar Postgres pra acesso externo.
- Configurar SSH: autenticação por chave (não senha), considerar mudar
  porta padrão.
- Nginx como reverse proxy só pro backend Spring Boot em
  `api.jogadoresanonimos.com.br` (Vercel serve o estático, não o VPS).
- Certbot pra TLS do subdomínio `api.` — renovação automática precisa ser
  configurada explicitamente (cron/systemd timer).
- Rotina de backup (Postgres + storage local de imagens) — montada
  manualmente, sem backup gerenciado de plataforma.
- Monitoramento básico — uptime check simples (ver "Keep-alive externo"
  abaixo, decisão adiada até o VPS estar rodando).
- Decidir se o Docker Compose roda como serviço `systemd`, pra reiniciar
  sozinho se o VPS reiniciar.

### Envs / config no `.env` do VPS (antes do deploy)
- `SPRING_PROFILES_ACTIVE=prod`.
- `RECAPTCHA_SECRET_KEY`: já gerada em `.env` local, colar no `.env` do
  VPS. `ProdConfigValidator` já falha o boot se vazia ou se for a chave de
  teste do Google. Site Key de prod já aplicada em `environment.prod.ts`.
- `JWT_SECRET`, `LOG_HMAC_SECRET`, `MFA_SECRET_ENCRYPTION_KEY`: geradas em
  dev (`.env` local), destino final é o `.env` do VPS (ou Docker secrets,
  a decidir com `devops`).
- `CORS_ALLOWED_ORIGINS` (lista dos hosts do Vercel, sem espaço após
  vírgula):
  `https://jogadoresanonimos.com.br,https://www.jogadoresanonimos.com.br,https://jogadoresanonimos.org.br,https://www.jogadoresanonimos.org.br`
  (`.org.br` mantido mesmo só redirecionando, pois o browser envia
  `Origin` do domínio original antes do redirect completar — ver
  `Decisoes de Sistemas.md` seção 8).
- **`apiBaseUrl` de produção**: `environment.prod.ts` hoje tem
  `https://jogadoresanonimos.com.br/api/v1`, precisa virar
  `https://api.jogadoresanonimos.com.br/api/v1` (absoluto — `arquivo.ts`
  usa `new URL(apiBaseUrl).origin`). Execução do `frontend`/
  `frontend-services` quando chegar a hora.
- **Dados do primeiro admin real** (apelido/e-mail/senha de bootstrap):
  pendente.

### Risco aberto do fail-fast de config
`ProdConfigValidator` cobre `RECAPTCHA_SECRET_KEY` e `RESEND_API_KEY`
(fail-fast real, boot quebra se vazia); `JWT_SECRET` e
`ADMIN_LIMITE_MAXIMO` têm fail-fast indireto (quebram no boot/uso).
**Não corrigido**: `MFA_SECRET_ENCRYPTION_KEY` e `LOG_HMAC_SECRET` só
quebram no primeiro uso real (não no startup); `CORS_ALLOWED_ORIGINS`
vazio não quebra boot, só bloqueia CORS silenciosamente. Recomendação
em aberto: expandir `ProdConfigValidator` para cobrir essas 3 envs.

**Decisão de processo em aberto (sugestão do `aprendiz`, 2026-08-08)**:
já é o segundo caso de env nova com default vazio mascarando falha
silenciosa até o primeiro uso real (primeiro foi
`LOG_HMAC_SECRET`/`MFA_SECRET_ENCRYPTION_KEY`, agora achado de novo com
`RESEND_API_KEY` faltando no `docker-compose.yml` — já corrigido). Regra
proposta pro `CLAUDE.md`: toda env nova usada via `@Value` deve ser
fail-fast (sem default vazio) ou ter o default documentado como
aceitável. Ainda não decidido pelo usuário.

~~`docker-compose.yml` não declarava `RESEND_API_KEY` no serviço
`backend`~~ — **corrigido (2026-08-08)**: env adicionada, mesmo padrão
de `JWT_SECRET`/`RECAPTCHA_SECRET_KEY`.

## Deploy / cutover (ordem recomendada)
1. ~~Backup do WordPress~~ — **feito e confirmado (2026-08-08)**.
2. **Setup do VPS do zero** (checklist acima) — backend rodando e
   respondendo no IP antes de tocar em DNS.
3. **Deploy do frontend no Vercel**: conectar repositório/build Angular,
   configurar domínio customizado `jogadoresanonimos.com.br` + `www.` no
   painel Vercel (TLS automático deles).
4. **Nginx + Certbot no VPS, só pro subdomínio `api.`**: reverse proxy pro
   backend em `api.jogadoresanonimos.com.br`; emitir TLS via Let's
   Encrypt/Certbot pra esse host (precisa do host no Nginx antes de rodar
   o Certbot).
5. **DNS**: apex `jogadoresanonimos.com.br` + `www.` → CNAME/ALIAS pro
   Vercel (valor exato no painel Vercel no momento do cutover, não de
   memória); `api.` → A record pro IP dedicado do VPS. TTL 300s antes do
   corte, normal depois de confirmado.
6. **Redirect `.org.br` → `.com.br`**: configurado no Vercel (domain
   redirect no painel deles), não no Nginx. Apontar DNS do `.org.br`/`www`
   também pro Vercel como domínio adicional com redirect.
7. **DNS do Resend** (SPF/DKIM/DMARC) — ver "Pode fazer agora", item 2.
8. Validar propagação + certificado TLS nos domínios (Vercel + `api.`) →
   confirmar app respondendo → desligar/arquivar WordPress na HostGator
   (manter como fallback por um tempo, não apagar).

### Segurança do multi-domínio (aplicar no cutover)
- HSTS não propaga entre domínios diferentes (`.com.br`/`.org.br` são
  eTLD+1 distintos) — cada host relevante precisa do próprio header
  `Strict-Transport-Security`. Submeter à preload list só o canônico.
- Certificado TLS: Vercel cobre `.com.br`/`www.` automaticamente; Certbot
  no VPS cobre só `api.jogadoresanonimos.com.br`. Confirmar os dois lados
  emitidos/válidos antes do corte de DNS.
- Redirect sempre HTTPS→HTTPS na borda; sem cookie/sessão compartilhada
  entre domínios (JWT vai em header/body, não cookie) — relevante entre
  frontend (Vercel) e backend (`api.` no VPS).

### Itens de infra que continuam pendentes de execução
- **Keep-alive externo** (ping em `/actuator/health` a cada ~10-14min):
  **decisão adiada deliberadamente pelo cliente (2026-08-04)** — a causa
  raiz (cold start do Render) deixou de existir com VPS próprio; cliente
  optou por observar o comportamento real do VPS antes de decidir manter
  (como uptime check) ou desligar.
- **Backup do diretório de storage local**: entrar na rotina de backup do
  servidor junto com o Postgres (já listado no checklist do VPS).
- **CDN (Cloudflare ou equivalente)**: decisão tomada (`Decisoes de
  Sistemas.md` seção 5) — origin aponta pro backend/disco local, sem
  bucket S3. Falta execução. Vercel já cobre CDN do estático; esta
  pendência é sobre o backend/disco local no VPS.

## Feedback do cliente (rodada pós-aprovação MVP)

- **Verificação semanal de imagem quebrada**: cliente quer checagem
  periódica. Dado o volume baixo, tende a ser rotina manual (checklist do
  admin), não monitoramento automatizado. Decidir com `devops`/
  `system-design` quando o VPS estiver de pé.
- **Diretório/guideline de imagem para salas** — **pergunta em aberto pro
  cliente, sem resposta ainda.** Hoje `Reuniao` não tem campo de imagem
  (nem model, nem form, nem backend); o único upload de imagem de conteúdo
  existente é o Mural de Apoio (`ImagemService`/`ImagemController`, limite
  5MB, sem validação de dimensão, preview já no form). Pergunta exata: ele
  quer dizer o Mural de Apoio (já existe) ou uma imagem/capa nova por sala
  de reunião (feature nova, precisa migration + endpoint)? **Não avançar
  implementação até essa resposta.** Guideline já especificado para quando
  vier a resposta:
  - Proporção fixa 16:9, mínimo 1200x675px, peso máximo 5MB.
  - Preview obrigatório no form (replicar padrão do Mural de Apoio).
  - Validação client-side bloqueia `< 1200x675px`; crop guiado 16:9 é
    nice-to-have, cortável para v1 com `object-fit: cover` como fallback.
  - Validação server-side de dimensão mínima obrigatória em
    `ImagemProcessor.java`, novo `ErrorCode` `UPLOAD_DIMENSAO_INSUFICIENTE`
    (`backend/docs/api/contrato-erro-padrao.md`). Server não valida
    proporção exata.
  - Se virar feature nova: `po` → `backend` (migration +
    `CriarReuniaoRequest`/`EditarReuniaoRequest`) antes do `frontend`
    mexer em `admin-reunioes`.
- **Atenção reforçada nos 3 primeiros dias pós-deploy**: lembrete
  operacional do cliente, vincular à data de deploy quando marcada.

## Features de e-mail

### Recuperação de senha — completa, incluindo envio real
Implementada ponta a ponta (backend + frontend + testes escritos),
desenho V2 unificado (endpoint único `POST /api/v1/auth/esqueci-senha`,
branch server-side por `role`, anti-enumeration, reset de admin nunca pula
MFA). Detalhe de implementação/decisão foi para `.claude/knowledge/` e
`backend/docs/api/contrato-recuperacao-senha.md`. Envio real de e-mail via
Resend plugado (2026-08-08, ver "Pode fazer agora", item 3) — feature
destravada de ponta a ponta. Testes escritos mas não executados
(Docker/Testcontainers com problema conhecido nesta máquina Windows).

### Verificação de e-mail válido/existente no cadastro
Cliente quer confirmar que o e-mail existe de fato (não só formato via
regex). Ainda **sem decisão de arquitetura** — só o pedido registrado.
Pré-requisitos antes de implementar:
- ~~Envio real de e-mail plugado (Resend no `EmailService`)~~ — feito.
- Fluxo de confirmação (e-mail com link/código antes da conta ativar).
- Decisão de UX de conta "pendente de confirmação" (`ux-ui`).
- Rate limit de reenvio de confirmação (`security`).

## Backlog (não trava deploy)
- **Step-up MFA na promoção de admin** — adiado deliberadamente pelo
  `security` (risco imediato baixo dado limite numérico + auditoria já
  existentes; retomar quando priorizado).
- **Literatura em PDF** — cliente vai enviar PDFs de literatura
  institucional (menciona 3 links de
  `gamblersanonymous.org/gamblers-anonymous-literature-digital/`, possível
  referência de formato pra página de Literatura). Sem previsão. Quando
  chegar: upload real via admin (PDF passa por `ArquivoService`, validação
  de magic bytes/anti-polyglot) — não dá pra inserir referência de arquivo
  direto via migration.

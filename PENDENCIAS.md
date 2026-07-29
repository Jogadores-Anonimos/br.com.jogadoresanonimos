# Pendências

> Arquivo único de pendências reais em aberto. Itens concluídos não ficam
> aqui — histórico de decisão/implementação vive em `Decisoes de
> Sistemas.md` e `.claude/knowledge/`.

## Infraestrutura / Deploy (aguardando decisão de hospedagem do cliente)

**Domínio de produção FECHADO (2026-07-26):** HostGator (cPanel, "Plano
M", hospedagem compartilhada tradicional — sem Docker/Java/Postgres,
serve só domínio/DNS/e-mail). `jogadoresanonimos.com.br` hoje roda
WordPress em produção real (conteúdo relevante já migrado pelo cliente
para o site novo) — será desligado/substituído pelo app novo (Render).
Cutover autorizado a qualquer momento. Dois domínios, ambos já
registrados na mesma conta: `jogadoresanonimos.com.br` (canônico, mantém
SEO do WP) e `jogadoresanonimos.org.br` (redirect 301 pro `.com.br`).

- **`apiBaseUrl` de produção** — **AJUSTADO EM CÓDIGO (2026-07-26,
  `backend`)**. `frontend/src/environments/environment.prod.ts`:
  `apiBaseUrl` mudou de `/api/v1` relativo para
  `https://jogadoresanonimos.com.br/api/v1` absoluto (necessário porque
  `arquivo.ts` já faz `new URL(apiBaseUrl).origin`, que exige URL
  absoluta). **Suposição não confirmada:** assumiu mesmo domínio do
  front, sem subdomínio `api.` dedicado — se a infraestrutura definitiva
  decidir por subdomínio próprio, só trocar esse valor.
- **`recaptchaSiteKey`/`RECAPTCHA_SECRET_KEY` de produção** — **AÇÃO
  MANUAL DO CLIENTE, ainda bloqueada.** Análise do `security`
  (2026-07-26): um único par de chaves reCAPTCHA aceita múltiplos
  domínios cadastrados no mesmo registro do console Google — não precisa
  de par por domínio. Cadastrar nesse registro apenas
  `jogadoresanonimos.com.br` + `www.jogadoresanonimos.com.br`; **não**
  cadastrar `.org.br` (só existe como redirect 301, nunca renderiza
  formulário/chama reCAPTCHA). Depois de gerado: `RECAPTCHA_SECRET_KEY`
  vai no secret do Render (backend) e `recaptchaSiteKey` em
  `frontend/src/environments/environment.prod.ts` (público, não é
  secret). É troca pura de valor de config, sem mudança de código —
  `ProdConfigValidator.java` já falha o boot em produção se a secret
  ficar vazia ou for a chave de teste do Google (proteção já existe).
  Pré-requisito prático para o restante do deploy — segue bloqueado até
  o cliente gerar o par no console Google.
- **Cold start do Render free tier mascarando timeout como falha em
  login/registro** — investigado em 2026-07-22: causa client-side (
  timeout de 5s do `HttpClient` do frontend) já corrigida para 60s.
  Causa adicional identificada no backend: timeout de conexão+leitura
  do `RestTemplate` usado por `RecaptchaService` (chamada síncrona a
  `google.com/recaptcha/api/siteverify`) estava em 5s+5s, no limite
  mínimo tecnicamente aceitável pela documentação do Google (recomendado
  >=10s) — ajustado para 10s+10s em
  `JogadoresAnonimosApplication.java`. Isso reduz falso-negativo de rede
  lenta, mas **não resolve a causa raiz do cold start em si** (instância
  Render dormente demorando a acordar antes mesmo da requisição chegar
  ao `RecaptchaService`) — isso é decisão de infraestrutura (`devops`),
  não de timeout de chamada HTTP interna. Sem retry automático de
  reCAPTCHA implementado deliberadamente (token é de uso único, retry
  pode ser rejeitado como replay pelo Google e só adiciona latência ao
  pior caso); sem fallback de bypass de reCAPTCHA em timeout (abriria
  brecha de abuso permanente por causa de problema de infra temporário).
- **Keep-alive externo configurado até 2026-07-24** — ping gratuito de
  terceiro (UptimeRobot/cron-job.org) contra `/actuator/health` a cada
  ~10-14 min só durante a janela de avaliação do MVP pelo cliente, para
  reduzir cold start do Render free tier sem custo. Revisar depois do
  prazo: remover se o projeto ficar parado, ou decidir se continua
  (ainda mascara sintoma, não resolve causa raiz — ver item de cold
  start acima) assim que a decisão de hospedagem definitiva for tomada.
- **Postgres produção** — dev já fechado (container local via compose),
  prod pendente.
- **Provedor de e-mail real** — `EmailService` hoje só loga, sem
  SMTP/API real. Decisão do `system-design` (2026-07-26): **Resend**
  recomendado (free tier 3.000 e-mails/mês cobre o volume do projeto
  com folga; melhor deliverability/setup que Gmail SMTP — descartado
  por limite baixo e reputação ruim — e SES — descartado por sandbox
  restritivo; integração via SMTP relay reaproveita `JavaMailSender`/
  `EmailService` já existente). Domínio de produção **já existe agora**
  (`jogadoresanonimos.com.br`), o que era o bloqueio anterior — mas
  **AÇÃO MANUAL DO CLIENTE ainda pendente**: criar a conta Resend em si.
  Depois de criada, os registros DNS reais de SPF/DKIM/DMARC saem lá
  (ver checklist DNS abaixo, item novo). Cliente informou o e-mail
  oficial da associação (`jogadoresanonimossaopaulo@gmail.com`) — fica
  registrado como **e-mail de contato/reply-to de referência** no
  conteúdo dos e-mails transacionais, não como remetente técnico/`from`
  (Gmail não pode ser verificado como domínio próprio). Bloqueante para
  as duas features de e-mail abaixo, até a conta Resend existir.
- **Onde guardar secrets** (`JWT_SECRET`, `LOG_HMAC_SECRET`,
  `MFA_SECRET_ENCRYPTION_KEY`) — todas já geradas e configuradas em dev
  (`.env` local); falta só o destino final de armazenamento/injeção em
  produção (Render secrets, ver `render.yaml`).
- **Dados do primeiro admin real** (apelido/e-mail/senha de bootstrap).
- **Backup do diretório de storage local** — bloqueante pra produção,
  precisa entrar na rotina de backup do servidor junto com o Postgres.
- **`SPRING_PROFILES_ACTIVE=prod` no deploy real** — checklist de envs
  levantado pelo `backend` (2026-07-26):
  - Fail-fast **real e já implementado** (`ProdConfigValidator`):
    `RECAPTCHA_SECRET_KEY` obrigatória, não pode ser vazia nem a chave
    de teste do Google.
  - Fail-fast **indireto** (quebra o boot/uso na prática, sem validator
    dedicado): `JWT_SECRET` (chave curta demais rejeitada pelo JJWT no
    boot); `ADMIN_LIMITE_MAXIMO` (`UsuarioService` lança
    `IllegalStateException` se ausente/inválido).
  - **Falha silenciosa/lazy — risco, não corrigido nesta tarefa**:
    `MFA_SECRET_ENCRYPTION_KEY` e `LOG_HMAC_SECRET` só quebram no
    primeiro uso real (cifrar TOTP / gerar hash de log), não no
    startup; `CORS_ALLOWED_ORIGINS` vazio não quebra o boot, só bloqueia
    CORS silenciosamente (nenhum browser consegue chamar a API).
    Recomendação registrada (fora do escopo desta tarefa): expandir
    `ProdConfigValidator` para cobrir essas 3 envs.
- **CORS multi-origem — RESOLVIDO EM CÓDIGO (2026-07-26, `backend`).**
  `app.cors.allowed-origins` (`SecurityConfig.java`) trocado de `String`
  para `List<String>` (Spring converte CSV automaticamente via
  `@Value`). Env renomeada `CORS_ALLOWED_ORIGIN` → `CORS_ALLOWED_ORIGINS`
  (plural) em `application.properties`, `application.properties.example`,
  `docker-compose.yml`, `docker-compose.staging.yml`, `render.yaml`.
  Valor de produção a configurar no Render:
  `https://jogadoresanonimos.com.br,https://www.jogadoresanonimos.com.br,https://jogadoresanonimos.org.br,https://www.jogadoresanonimos.org.br`
  (sem espaço após vírgula). **Nota do `security`:** `.org.br`/`www.org.br`
  tecnicamente não precisam estar na allowlist, já que esse domínio só
  redireciona e nunca chama a API diretamente — mantido na lista mesmo
  assim porque o browser envia o header `Origin` do domínio original
  antes do redirect completar (decisão documentada em `Decisoes de
  Sistemas.md` seção 8). **AÇÃO MANUAL:** reconfigurar a env var no
  Render com o novo nome/valor antes do deploy.
- **CDN (Cloudflare ou equivalente) na frente do domínio real** —
  decisão já tomada pelo `system-design` (ver `Decisoes de Sistemas.md`
  seção 5): origin apontando pro próprio backend/disco local, sem
  bucket S3, sem mudar a decisão de storage já fechada. Entra no
  checklist de deploy — domínio já decidido, falta execução.

### Checklist de cutover DNS/backup (levantamento do `devops`, 2026-07-26 — AÇÃO MANUAL DO USUÁRIO no painel HostGator/Render, sem acesso direto do time a esses painéis)

1. **Backup do WordPress antes de qualquer mudança de DNS**: cPanel →
   Backup Wizard → Full Backup (ou separado: phpMyAdmin export do banco
   MySQL + compactar/baixar `wp-content/` e `wp-config.php` via File
   Manager/FTP). Conferir integridade dos arquivos baixados. Guardar
   fora da HostGator (Drive/disco local), reter por ~90 dias pós-cutover
   e descartar depois (backup de infra legada do cliente, fora do
   escopo de retenção LGPD do app novo).
2. **Custom Domains no Render**: cadastrar `jogadoresanonimos.com.br`,
   `www.jogadoresanonimos.com.br`, `jogadoresanonimos.org.br` e
   `www.jogadoresanonimos.org.br` no serviço Render antes de mudar o
   DNS, para o Let's Encrypt automático já emitir certificado assim que
   detectar o apontamento correto.
3. **DNS de `.com.br` no Zone Editor HostGator**: apex sem `www` não
   aceita CNAME (limitação de protocolo) — usar ANAME/ALIAS se o Zone
   Editor suportar (apontando pro hostname `.onrender.com`), senão A
   record pro IP fixo que o Render documenta para apex domains
   (confirmar o valor exato no dashboard Render no momento da
   configuração, não usar de memória). `www.jogadoresanonimos.com.br`:
   CNAME direto pro hostname `.onrender.com` (sem restrição). Reduzir
   TTL pra 300s antes do corte, voltar a TTL normal depois de confirmado.
4. **Redirect `.org.br` → `.com.br`**: recomendação do `devops` é tratar
   no lado do Render/aplicação (não cPanel Redirects) — apontar DNS do
   `.org.br`/`www` pro Render igual ao `.com.br`, cadastrar como Custom
   Domain adicional no mesmo serviço, e configurar redirect 301
   preservando path na camada de roteamento (app/proxy do Render).
   Centraliza a lógica num único lugar em vez de duplicar em cPanel +
   app. Definir com `po`/`backend` onde exatamente esse redirect é
   implementado quando for a hora.
5. **DNS do Resend (SPF/DKIM/DMARC)** — bloqueado até a conta Resend ser
   criada (ver pendência de e-mail acima). Pode adiantar só o **DMARC**
   desde já (`_dmarc.jogadoresanonimos.com.br`, `v=DMARC1; p=none;
   rua=mailto:<email de monitoramento do cliente>`, modo observação).
   SPF/DKIM saem exatos só depois de criada a conta — atenção pra não
   duplicar registro SPF se já existir um (e-mail atual do cPanel):
   precisa mesclar `include:` num único TXT, SPF não aceita múltiplos
   registros por domínio.
6. **Ordem recomendada**: backup confirmado → Custom Domains no Render →
   DNS `.com.br` (validar propagação + certificado TLS) → DNS `.org.br`
   → confirmar app respondendo nos dois domínios → desligar/arquivar
   WordPress na HostGator (não apagar imediatamente, manter como
   fallback por um tempo) → Resend à parte quando a conta existir.

### Checklist de segurança do multi-domínio (parecer do `security`, 2026-07-26)

- **HSTS não propaga entre domínios diferentes** — `.com.br` e `.org.br`
  são eTLD+1 distintos, cada um dos 4 hosts precisa do próprio header
  `Strict-Transport-Security`. Submeter à preload list só o canônico
  (`.com.br`) — não faz sentido para um domínio que só redireciona.
- **Certificado TLS deve cobrir os 4 hosts** — confirmar no Render que
  `.org.br`/`www.org.br` foram adicionados explicitamente como domínios
  customizados do serviço para entrar na emissão automática (Let's
  Encrypt não cobre domínio não cadastrado).
- Redirect sempre HTTPS→HTTPS na borda (nunca passar por HTTP do
  destino); sem cookie/sessão compartilhada entre os dois domínios (já
  não há — JWT vai em header/body, não cookie).

## Feedback do cliente 2026-07-26 (rodada pós-aprovação MVP)

- **Verificação semanal de imagem quebrada** — cliente quer checagem
  periódica de imagens quebradas no site, válida enquanto durar a
  mensalidade de hospedagem paga. Dado o volume baixo do projeto (ver
  "Contexto de escala" no `CLAUDE.md`), tende a ser rotina manual leve
  (ex: checklist do cliente/admin) em vez de monitoramento automatizado
  dedicado — mas só decidir com `devops`/`system-design` quando a
  hospedagem definitiva estiver fechada (hoje ainda bloqueado pelas
  pendências de "Infraestrutura / Deploy" acima). Registrado, sem ação
  ainda.
- **Diretório/guideline de imagem para salas** — **PERGUNTA EM ABERTO
  PRO CLIENTE antes de implementar (reconfirmado 2026-07-26 — cliente
  ainda não respondeu esse ponto específico, mesmo respondendo os
  outros itens desta rodada de feedback).** Nem o usuário (dono do
  projeto) entendeu com certeza o pedido do cliente sobre "diretório
  padrão de arquivos (tamanho, cores, fontes)". O `ux-ui` (2026-07-26)
  descobriu que hoje **sala de reunião (`Reuniao`) não tem campo de
  imagem nenhum** — nem no model, nem no form de admin, nem no backend. O
  único upload de imagem de conteúdo que existe hoje é o **Mural de
  Apoio** (`admin-mural-de-apoio`, via `ImagemService`/`ImagemController`,
  limite 5MB, sem validação de dimensão/proporção, já tem preview no
  form). Pergunta exata para o cliente: ele quer dizer o **Mural de
  Apoio** (já existe, atrito visual pode ocorrer ali hoje), ou está
  pedindo uma **imagem/capa nova por sala de reunião** (não existe hoje,
  feature nova — precisa migration + endpoint novo, maior escopo)?
  **Não avançar em nenhuma implementação até essa resposta.** Guideline
  já especificado, serve para os dois casos assim que a resposta
  chegar:
  - Proporção fixa **16:9**, dimensão mínima **1200x675px**.
  - Peso máximo: manter **5MB** (mesmo limite do `ImagemService` hoje).
  - Preview obrigatório no form (já existe no Mural de Apoio — replicar
    se virar feature nova de sala).
  - Validação client-side bloqueia `< 1200x675px`; oferecer crop guiado
    16:9 no client é nice-to-have (custo maior — se desproporcional ao
    volume do projeto, cortar para v1 e usar `object-fit: cover` fixo
    como fallback).
  - Validação server-side de dimensão mínima **deve existir** (não só
    client) em `ImagemProcessor.java`, novo `ErrorCode`
    `UPLOAD_DIMENSAO_INSUFICIENTE` no contrato de erro padrão
    (`backend/docs/api/contrato-erro-padrao.md`). Server não valida
    proporção exata.
  - Se for feature nova de imagem por sala: acionar `po` → `backend`
    (migration + `CriarReuniaoRequest`/`EditarReuniaoRequest`) antes do
    `frontend` mexer em `admin-reunioes`.
  **Nada implementado ainda** — aguardando resposta do cliente sobre
  qual dos dois casos é.
- ~~Visibilidade do carrinho de checkout~~ — **CONCLUÍDO (2026-07-26).**
  Ícone de carrinho da navbar oculto via flag `showCart = false`
  (`frontend/src/app/components/navbar/navbar.ts` e `navbar.html`,
  2 blocos `.utility-cart` desktop/mobile). CSS mantido intacto para
  religar quando checkout existir. Build de produção validado.
- **Mural de apoio como carrossel automático** — avaliação do `ux-ui`
  (2026-07-26): **pendente de confirmação do cliente antes de
  implementar.** A parte de "botões laterais que aceleram a rotação ao
  clicar" foi sinalizada como problemática — conflita com WCAG 2.2.2
  (conteúdo em auto-movimento precisa de controle de pausar, não de
  acelerar) e é dark-pattern-like (usuário quer controle/pausa, não mais
  velocidade). Recomendação: trocar "botões que aceleram" por navegação
  manual prev/next (clique pausa o auto-play), manter auto-play só se
  opcional e pausável (hover/foco/touch/aba oculta/vídeo tocando/
  `prefers-reduced-motion`), com dots + `aria-live` para leitor de tela.
  Sem impacto em backend/modelo de dados. Confirmar com o cliente se
  topa cortar a "aceleração ao clique" antes de acionar o `frontend`.
- **Limitar quantidade de usuários admin** — **item 1 CONCLUÍDO
  (2026-07-26).** Limite numérico configurável via env
  `ADMIN_LIMITE_MAXIMO` implementado em `UsuarioService.editarRole`,
  fail-fast no startup se a env estiver ausente/inválida (sem default
  silencioso), reusando o mesmo lock pessimista já existente
  (`buscarPorRoleParaAtualizacao(Role.ADMIN)`) tanto para "último admin"
  quanto para o novo limite máximo — fecha race condition de duas
  promoções simultâneas estourando o limite. Novo `ErrorCode`
  `USUARIO_LIMITE_ADMIN_ATINGIDO`, mensagem genérica ao cliente, sem
  expor limite/contagem configurados. Ordem de validação: auto-
  rebaixamento → último-admin (remoção) → limite-máximo (promoção) →
  aplicar. Testes unitários atualizados (`UsuarioServiceTest`, 21/21
  verde). Docs atualizados: `backend/docs/api/contrato-usuario-admin.md`,
  `backend/docs/api/contrato-erro-padrao.md`. **Divergência de
  documentação corrigida**: `Decisoes de Sistemas.md` seção 3 não diz
  mais "manual via script" (código já tinha endpoint/tela
  `admin-usuarios` fazendo isso via API).
  **Pendência de ambiente**: falta setar `ADMIN_LIMITE_MAXIMO` real nos
  `.env`/`.env.staging`/secrets de cada ambiente — sem isso a aplicação
  não sobe (fail-fast é o comportamento esperado, não é bug).
  **Item 2 (step-up MFA na promoção) segue em aberto, adiado
  deliberadamente pelo `security`** (2026-07-26): exigir código TOTP
  fresco do admin-promotor no momento de promover alguém a admin (não só
  validar a sessão JWT) — novo campo `codigoTotp` em
  `EditarRoleRequest`, só quando `novaRole == ADMIN`. Mexe em contrato de
  API, precisa sync com `frontend-services`/`frontend` (tela pede o TOTP
  do admin logado antes de confirmar). Motivo do adiamento: risco
  imediato já é baixo dado o limite numérico + auditoria existente da
  troca de role; esforço é maior (contrato + 3 camadas). Retomar quando
  priorizado.
  Curadoria de quem são as "pessoas-chave" continua decisão manual do
  cliente/admin — sistema não infere isso sozinho; limite numérico +
  step-up MFA são os controles técnicos que sustentam a política, não
  substituem allowlist de negócio. Recomendação do `security` mantida:
  **não** usar allowlist de apelidos/e-mails hardcoded.
  Arquivos: `backend/src/main/java/br/com/jogadoresanonimos/usuario/
  service/UsuarioService.java`, `common/exception/ErrorCode.java`,
  `resources/application.properties`, `test/.../UsuarioServiceTest.java`,
  `docker-compose.yml`, `docker-compose.staging.yml`.
- **"Esqueci minha senha" do login ADM com código de 4-6 dígitos por
  e-mail (não link)** — cliente pediu especificamente esse formato para
  o fluxo de admin. **Não implementar agora.** Quando a feature de
  "recuperação de senha por e-mail" (já registrada abaixo, em andamento
  em outra tarefa em paralelo) for implementada: (1) o fluxo de ADM deve
  usar código numérico curto (4-6 dígitos), não link; (2) verificar com
  quem estiver implementando aquele trabalho se dá para reaproveitar a
  mesma infraestrutura de token/e-mail para os dois casos (usuário comum
  e admin) ou se precisa de tratamento separado, dado que ADM já tem MFA
  TOTP (evitar duas etapas de segundo fator conflitantes/redundantes no
  login de admin).
- **Atenção reforçada nos 3 primeiros dias pós-deploy** — lembrete
  operacional do cliente para observar comportamento do site de perto
  logo após o deploy real. Vincular à seção "Roteiro à frente" abaixo
  (item de Deploy) quando a data de deploy for marcada.

## Feature nova: verificação de e-mail válido/existente no cadastro

- Cliente quer confirmar que o e-mail informado no cadastro existe de
  fato (não só formato via regex) — hoje não há verificação nenhuma
  além de formato. Pré-requisitos a levantar antes de implementar:
  provedor de e-mail real (`EmailService` hoje só loga, ver pendência de
  infra acima — bloqueante), fluxo de confirmação (provável e-mail com
  link/código que o usuário precisa clicar/inserir antes da conta virar
  ativa), decisão de UX de conta "pendente de confirmação" (`ux-ui`),
  rate limit de reenvio de confirmação (`security`). Sem decisão de
  arquitetura ainda — só registrado como pedido do cliente.

## Feature nova: recuperação de senha por e-mail

- Cliente pediu recuperação de senha via e-mail (usuário esqueceu senha,
  recebe código/link no e-mail cadastrado, redefine senha). Hoje não
  existe esse fluxo no sistema. Pré-requisitos a levantar antes de
  implementar:
  - **Provedor de e-mail real** — mesma dependência do item de
    verificação de e-mail acima, `EmailService` hoje só loga
    (bloqueante, é infraestrutura, ver pendência já registrada).
  - **Contrato de fluxo** (`backend` + `security`, SDD antes de
    implementar): endpoint de solicitar recuperação (por e-mail ou
    apelido, sem revelar se existe conta — mesma lógica de
    anti-enumeration já usada no login), geração de código/token de uso
    único com expiração curta, endpoint de validar código + definir
    nova senha, invalidação do token após uso, rate limit de solicitação
    (`security`).
  - **Estilização com identidade visual da JA** no e-mail enviado
    (`ux-ui` define o conteúdo/tom, `frontend`/`backend` decidem se é
    HTML de e-mail com o padrão visual do site ou texto simples — a
    definir).
  - Ainda sem decisão de arquitetura tomada — só registrado o pedido e
    os pré-requisitos conhecidos até aqui, falta detalhar com
    `system-design`/`security`/`backend`/`ux-ui` quando for priorizado.
  - **Análise do `security` (2026-07-26):** fluxo de usuário comum já
    parece pronto do ponto de vista de arquitetura — falta só o
    provedor de e-mail real (Resend, bloqueado aguardando domínio, ver
    pendência de infra acima). **Alerta de segurança para quando for
    implementado:** o reset por código curto (4-6 dígitos) pedido pelo
    cliente para o login ADM **não pode virar bypass do MFA TOTP** já
    existente para admin — resetar a senha de um admin não pode, sob
    hipótese nenhuma, pular a etapa MFA no login seguinte. Deixar isso
    explícito no contrato/implementação quando essa feature for
    priorizada.

## Roteiro à frente (não são bugs, é o plano combinado em 2026-07-14)

1. Deploy — bloqueado até fechar "Infraestrutura / Deploy" (Postgres
   produção, provedor de e-mail real, onde guardar secrets, dados do
   admin real, backup de storage), tudo dependente de decisão de
   hospedagem do cliente. Antes de ir: checagem final de performance
   front/back, code review e testes.

## Pendente: literatura em PDF

- [ ] Cliente vai enviar PDFs de literatura institucional (menciona
  também 3 links repetidos pra `gamblersanonymous.org/gamblers-
  anonymous-literature-digital/` — possivelmente referência de formato
  pra página de Literatura, a confirmar quando o material chegar). Sem
  previsão de data. Quando chegar: mesmo processo desta sessão (upload
  real via admin se for arquivo binário — PDF passa por
  `ArquivoService`, validação de magic bytes/anti-polyglot — não dá pra
  inserir referência de arquivo direto via migration, precisa do fluxo
  real de upload).

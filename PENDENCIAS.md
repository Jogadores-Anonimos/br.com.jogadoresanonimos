# Pendências

> Arquivo único de pendências reais em aberto. Itens concluídos não ficam
> aqui — histórico de decisão/implementação vive em `Decisoes de
> Sistemas.md` e `.claude/knowledge/`.

## Antes de começar (topo da fila)

**Review geral ponta a ponta do projeto inteiro — EXECUTADO (2026-08-09).**
`code-reviewer` (frontend/backend/segurança/infra num parecer só) e
`system-design` (arquitetura/trade-offs à luz do Swarm/Traefik/VPS real)
rodaram em paralelo, cobrindo o repositório inteiro. Achados abaixo, por
severidade.

### Achados — `code-reviewer`

**Alto**
- `frontend/src/environments/environment.prod.ts:3` — `apiBaseUrl` ainda
  aponta pro domínio raiz (`https://jogadoresanonimos.com.br/api/v1`),
  precisa virar `https://api.jogadoresanonimos.com.br/api/v1`. Já
  rastreado na seção "Envs / config no `.env` do VPS" abaixo — mantido
  como pendência de execução do `frontend`/`frontend-services`, não
  corrigido nesta rodada (depende do domínio final confirmado).
- `frontend/src/app/pages/admin/admin-reunioes/admin-reunioes.ts:81,220-252`
  — `HttpClient` injetado direto no componente pra chamar ViaCEP/Nominatim,
  bypassando a convenção do projeto de sempre ter um service pra API.
  Extrair `EnderecoLookupService` (`buscarCep`/`geocodificar`). Componente
  também não tem `.spec.ts`, diferente dos demais `admin-*`.

**Médio**
- `backend/.../LocalDiskStorageClient.java:44,56` (upload/delete) só
  valida path traversal (`normalize()+startsWith(baseDir)`) no `download`
  (linha 71-74). Risco teórico hoje (chave sempre é UUID gerado
  internamente, nunca input direto de usuário), mas falta defesa em
  profundidade — extrair helper `resolverDentroDoBaseDir(chave)` e aplicar
  nos 3 métodos.
- ~~Artefatos órfãos do plano de infra anterior~~ (Caddy manual +
  frontend no VPS, hoje é Traefik+Vercel) — **removidos (2026-08-09)**:
  `docker-compose.staging.yml`, `Caddyfile.staging` (raiz) e
  `frontend/Dockerfile` (builda com Caddy standalone, não referenciado
  em `stack.prod.yml` nem `docker-compose.yml` de dev).

**Baixo**
- `application.properties:23` — default de `RECAPTCHA_SECRET_KEY` é a
  chave pública de teste do Google; `ProdConfigValidator` já barra em
  prod. Sem ação, só nota de atenção para outros profiles não-prod.

**Confirmado sem achado**: migration `V6` (fix da constraint
`admin_auditoria_log`) completa, sem resquício de dependência dos valores
antigos fora do domínio de `Relato` (que legitimamente ainda usa
`pending/approved/rejected`). Zero SQL injection (JPQL parametrizado em
toda `@Query`), IDOR evitado consistentemente via `principal.id()` do
JWT, anonimato tratado a sério em log/DTO (hash em vez de identificador
direto, e-mail mascarado em listagem admin), fail-fast de config de prod
cobre todas as envs sensíveis, rate limit in-memory proporcional à
escala — nenhum overengineering nem lacuna encontrada nesses pontos.

### Achados — `system-design`

**Crítico**
- ~~Postgres em produção já com dado real (admin bootstrap) e sem
  rotina de backup~~ — **resolvido (2026-08-09)**. `systemd timer`
  instalado e ativo (via root, `eduardo` não tem `sudo`), próxima
  execução automática agendada. Primeiro backup real bem-sucedido
  confirmado local + Google Drive (`pg_dump` + storage cifrados). Sem
  pendência restante neste item.

**Alto**
- SPOF Postgres+backend no mesmo VPS de 2GB é aceitável nesse volume
  **isoladamente**, mas combinado com a ausência de backup (item acima)
  vira risco duplo — resolve sozinho quando o backup existir (cai pra
  baixo, risco residual aceito e documentado).
- Margem de memória do VPS (2GB) é apertada mas dentro do orçamento — não
  redesenhar preventivamente; só rodar o futuro `pg_dump` fora de horário
  de pico e monitorar RSS real durante a execução (é exatamente o cenário
  de coincidência de eventos já sinalizado antes). Revisitar upgrade de
  plano só se houver OOM real observado, não antes.

**Médio**
- Rede overlay `infra-site-proxy`: sem risco técnico ao nosso stack hoje
  (projeto não usa essa rede, `stack.prod.yml` já isola corretamente em
  `ja` + `backend-internal`). O risco real é de governança de VPS
  compartilhado com o colega, não de arquitetura — mantém a pendência de
  confirmação como está, sem bloquear o deploy atual.
- Rollback de imagem Docker com tag `latest` — sobe de prioridade após o
  primeiro deploy manual real. Não precisa de CI/CD completo agora:
  mínimo viável é taguear a imagem por SHA curto do commit a cada rebuild
  manual, antes de `docker service update`, para permitir reverter.
  Reavaliar CI/CD de verdade só se a cadência de deploy aumentar.
- Keep-alive anti-cold-start (corretamente descartado, causa raiz não
  existe mais) é diferente de **uptime monitoring externo** (detectar
  queda), que continua em aberto e é item barato (ex: free-tier tipo
  UptimeRobot, sem overhead no VPS). Separar os dois no backlog — ver
  "Itens de infra" abaixo.

**Baixo**
- CDN de disco local do backend: sem urgência nova, decisão já fechada,
  só falta execução — Traefik/Swarm não muda isso.
- Traefik/Swarm não alterou nenhuma decisão de rede/roteamento de forma
  substantiva, só a mecânica de deploy (`docker stack deploy`). Nenhum
  ajuste necessário em `stack.prod.yml` (já proporcional: `replicas: 1`,
  resource limits, sem Kubernetes/service mesh).

**Confirmado sem overengineering**: nenhuma decisão prévia (storage
local, sem cache/fila/CDN de backend, Actuator puro sem
Prometheus/Grafana, filtro/paginação client-side de reuniões, rate limit
em memória sem Redis) é desproporcional ao volume (~2.500 req/dia),
isolada ou somada no VPS de 2GB. Único gap real é execução de backup
(item crítico acima).

## Rotina de backup recorrente (executando de verdade desde 2026-08-09)

Resolve o achado crítico do review: Postgres em produção com dado real
(admin bootstrap) e zero backup até esta data. Solução mínima
proporcional ao volume do projeto — sem ferramenta de backup
enterprise, sem serviço gerenciado novo.

**Primeiro backup real bem-sucedido: 2026-08-09 16:06 UTC**
(`jaa-backup-20260809-190632.tar.gpg`, 200873 bytes) — dump do
`jaa_postgres` real + tar do volume `jaa_backend_storage`, cifrado e
confirmado nos dois destinos: `~/backups/` no VPS e
`gdrive-jaa:jaa-backups` no Google Drive (conta
`jogadoresanonimosbr@gmail.com`, remote `rclone` autenticado via OAuth
`authorize` rodado localmente pelo usuário, sem instalar rclone no VPS
com privilégio de root). Existe cópia off-box de verdade agora — deixa
de ser risco residual "só scripts prontos".

**Ajuste de execução descoberto só ao rodar no VPS real (2026-08-09)**:
o usuário `eduardo` não tem `sudo` sem senha interativa (esperado, é
política do VPS compartilhado). O `backup.sh`/`restore.sh` originais
liam o volume Docker direto pelo path do host
(`/var/lib/docker/volumes/.../_data`), que é `root`-only — quebrava sem
sudo. Corrigido: os dois scripts agora taram/extraem o volume via um
container Alpine descartável (`docker run --rm -v
jaa_backend_storage:/data ...`), operação que `eduardo` já pode fazer
sem sudo (grupo `docker`). Mais portável também — não depende do
storage driver do Docker manter aquele path fixo. `pg_dump`/`psql` já
usavam `docker exec` no `jaa_postgres`, não precisaram de ajuste.

**`rclone` instalado sem sudo**: binário oficial baixado direto
(`https://downloads.rclone.org/rclone-current-linux-amd64.zip`) e
colocado em `~/bin/rclone` (adicionado ao `PATH` do usuário via
`.bashrc`), em vez do pacote `apt`/script `curl | sudo bash` — mesma
limitação de sudo acima. `systemd` **não usa `$PATH` do usuário
interativo**, então `jaa-backup.service` precisa do path absoluto
`/home/eduardo/bin/rclone` (ou `Environment=PATH=...` na unit) — ver
pendência abaixo.

**Resolvido (2026-08-09)**: `eduardo` não tem `sudo` (nem com senha —
não está no sudoers), então a instalação do timer foi feita via
`root` (acesso por chave já configurado). `Environment=PATH=/home/eduardo/bin:/usr/bin:/bin`
adicionado na unit antes de instalar. `jaa-backup.timer` ativo
(`systemctl status`: `active (waiting)`), próxima execução agendada
para 03:30 (horário local). Backup automático diário rodando de
verdade, sem pendência.

**O quê**: `pg_dump` do Postgres (formato plain, comprimido) + `tar` do
diretório de storage de imagens (`backend_storage`/
`LocalDiskStorageClient`), empacotados juntos num único arquivo por
execução (garante que o metadado do banco e o binário no storage nunca
ficam de rodadas diferentes/inconsistentes entre si — ver `Decisoes de
Sistemas.md` seção 5, Trade-off 2). Todo o pacote é cifrado com `gpg`
(AES256, senha simétrica) antes de sair do servidor.

**Onde fica armazenado**: dois lugares.
1. **Local, no VPS**: `~/backups/` (fora do repo git, fora de volume
   Docker) — serve para restore rápido sem depender de rede.
2. **Off-box (fora do VPS), via `rclone` para Google Drive**: conta
   Google já existente do projeto, sem custo adicional, sem contrariar a
   decisão fechada de não usar bucket S3-compatible para o storage
   principal (isso é backup, escopo diferente, decisão própria). Este é
   o lugar que protege contra perda total do disco do VPS — sem cópia
   off-box, um backup só local no mesmo disco não protege nada.

Arquivo nunca sai em texto claro: mesmo indo para o Google Drive, está
cifrado com `gpg` antes do upload — mitiga o fato de o dump conter
e-mail de usuário (exceção de anonimato já documentada no `CLAUDE.md`).
Passphrase do `gpg` fica só no `.env.backup` do VPS (permissão `600`,
fora do repo), nunca commitada.

**Frequência**: diária, `03:30 UTC` (horário de menor tráfego esperado,
evita coincidir backup + pico de uso — mesma preocupação já levantada
para memória do VPS).

**Retenção**: 7 dias, local e no Google Drive (arquivos mais antigos são
apagados automaticamente pelo próprio script) — consistente com o
princípio de retenção mínima de dado sensível da LGPD (`CLAUDE.md`,
seção Privacidade). Backup não é arquivo morto: existe só para permitir
recuperação de um desastre recente, não para reter histórico longo de
dado de usuário.

**Arquivos** (`infra/backup/`, raiz do repo):
- `backup.sh` — faz o dump, empacota, cifra, copia local + Google
  Drive, aplica retenção. Idempotente, seguro para rodar manualmente a
  qualquer momento além do agendamento.
- `restore.sh <arquivo.tar.gpg>` — decifra, restaura Postgres (`psql`
  direto no dump) e o storage de imagens. **Sempre manual, nunca
  automatizado** — restore é uma decisão operacional deliberada, não
  algo que deve rodar sozinho.
- `jaa-backup.service` / `jaa-backup.timer` — unidades `systemd` para
  agendar o `backup.sh` diariamente, escolhido em vez de `cron` puro
  porque o VPS já roda Docker como serviço `systemd` (consistência,
  menos uma ferramenta diferente para o colega precisar conhecer) e
  porque `systemd` já loga em `journalctl` sem configuração extra.
- `.env.backup.example` — modelo do arquivo de variáveis
  (`DB_NAME`/`DB_USERNAME`/`DB_PASSWORD`/`BACKUP_GPG_PASSPHRASE`/
  `RCLONE_REMOTE`/etc.), nunca committar o real.

**Como foi configurado no VPS de fato (2026-08-09)**:
1. ~~Copiar `infra/backup/` para `~/deploy/infra/backup`~~ — **feito**.
2. ~~Instalar `rclone`/`gnupg`~~ — **feito**, `gnupg` já vinha instalado
   no VPS; `rclone` sem `apt`/sudo, binário oficial em `~/bin/rclone`
   (ver nota acima).
3. ~~`rclone config` remote `gdrive-jaa`~~ — **feito**, via
   `rclone authorize "drive"` rodado pelo usuário localmente (tem
   navegador), token colado na config do servidor com
   `rclone config create gdrive-jaa drive token='...' scope=drive
   --non-interactive` (mais 2 prompts de state machine respondidos:
   aceitar client_id compartilhado do rclone, não é Shared/Team Drive).
   Pasta `jaa-backups` criada no Drive.
4. ~~`.env.backup` real~~ — **feito**, `chmod 600`, `DB_*` copiados do
   `.env.prod`, `BACKUP_GPG_PASSPHRASE` gerada com `openssl rand -base64
   32` direto no servidor (nunca trafegou pela sessão/log).
5. **Pendente** — `sudo cp jaa-backup.service jaa-backup.timer
   /etc/systemd/system/` (ver bloco de pendência acima sobre `PATH` do
   `rclone` na unit antes de copiar).
6. **Pendente** — `sudo systemctl daemon-reload && sudo systemctl
   enable --now jaa-backup.timer`.
7. ~~Validar execução ponta a ponta~~ — **feito, mas via execução manual
   direta do script** (`bash ~/deploy/infra/backup/backup.sh`), não via
   `systemctl start jaa-backup.service` (unit ainda não instalada).
   Repetir esse `systemctl start` depois do passo 5/6 pra confirmar que
   a unit também funciona igual à execução manual.

**Como restaurar em caso de desastre (resumo)**:
1. Baixar o `.tar.gpg` mais recente do Google Drive (ou usar o local em
   `~/backups/` se o disco ainda estiver íntegro).
2. Garantir que `jaa_postgres` está rodando (`docker service ls`).
3. `~/deploy/infra/backup/restore.sh caminho/para/jaa-backup-TIMESTAMP.tar.gpg`
   — decifra, aplica o dump no Postgres via `psql`, extrai o storage de
   imagens de volta pro volume Docker.
4. `docker service update --force jaa_backend` para o backend reiniciar
   já enxergando o estado restaurado (pool de conexão, cache local se
   houver).
5. Conferir `/actuator/health` e uma rota que leia dado do banco antes
   de considerar concluído.

**Risco residual atual (2026-08-09, bem menor que antes)**: dado de
produção já tem backup real off-box (Google Drive) desde o primeiro
backup bem-sucedido acima — deixa de ser "zero proteção". O que resta é
só a **automação do agendamento** (timer `systemd`, pendente de `sudo`
com senha do usuário) — até isso rodar, o backup diário depende de
alguém disparar `bash ~/deploy/infra/backup/backup.sh` manualmente.

## Estado atual da infraestrutura (2026-08-09)

- **Decisões de hospedagem: TODAS FECHADAS** (ver histórico em `Decisoes
  de Sistemas.md`). Não há mais nada de hospedagem esperando decisão do
  cliente — o que resta é **execução**.
- **VPS — mapeamento real via SSH concluído (2026-08-09)**. Acesso SSH
  funcionando desde 2026-08-08 — `eduardo@143.95.219.110` por chave
  (`ssh-keygen ed25519`).
  - **Docker**: instalado, `eduardo` já no grupo `docker`, serviço
    `systemd` habilitado (sobe sozinho se o VPS reiniciar). **Feito,
    confirmado.**
  - **Orquestrador real é Docker Swarm** (não docker-compose simples) —
    node único, hostname `ja`, role Leader, Swarm ativo. **Muda a forma
    de deploy**: usar `docker stack deploy`, não `docker-compose up`, em
    produção (ver `Decisoes de Sistemas.md`, seção 7-B, detalhe completo).
  - **Traefik** (`traefik:v3.5.3`) já rodando como stack Swarm —
    `--providers.swarm=true`, `--providers.docker.network=ja`,
    `--providers.docker.exposedbydefault=false`, resolver ACME
    `letsencryptresolver` via HTTP challenge, redirect HTTP→HTTPS ativo.
    **Confirmado: substitui de vez o plano antigo de Nginx manual +
    Certbot manual** — Traefik cobre roteamento e TLS via ACME embutido,
    Certbot **removido do plano definitivamente**, não é mais "a
    confirmar".
  - **Portainer**: instalado (2 serviços), painel de gestão de container
    via web. **Uso pro nosso projeto ainda não confirmado** — ver
    pendência de confirmação com o colega, abaixo.
  - **Redes overlay existentes**: `ja` (10.0.1.0/24, tem Portainer +
    Traefik — é onde o backend precisa entrar pra ser roteado) e
    `infra-site-proxy` (10.0.2.0/24, só Traefik conectado, **propósito
    não confirmado, não usar sem perguntar ao colega**).
  - **Firewall (`ufw`) já ativo e confirmado**: libera só `22` (SSH
    eduardo), `22022` (SSH legado colega), `80`, `443`. Postgres não
    exposto externamente. **Feito.**
  - **Deployado (2026-08-09)**: `jaa_backend` e `jaa_postgres` rodando
    1/1 via `docker stack deploy -c stack.prod.yml jaa`. Falta só o DNS
    de `api.jogadoresanonimos.com.br` apontar pro IP do VPS para o
    Traefik emitir o certificado TLS real (ver seção própria abaixo).
- **Contas criadas**: Resend, Vercel, GitHub. Domínio é o que já existia
  na HostGator (site WordPress obsoleto), `jogadoresanonimos.com.br`.
- **Provedor decidido**: VPS Cloud NVMe HostGator, 2GB RAM (NVMe 2), sem
  painel. Postgres em container Docker via Swarm. Split frontend
  (Vercel) / backend + Postgres (VPS, stack Swarm), backend exposto em
  `api.jogadoresanonimos.com.br` via Traefik.

### Pendência de confirmação com o colega (sinalizado, não decidido)
- **Propósito da rede overlay `infra-site-proxy`** (10.0.2.0/24) — só
  Traefik conectado hoje, motivo de existir não está claro. Não usar pro
  nosso backend até confirmar o que é.
- **Escopo de uso do Portainer** — perguntar se ele vai administrar
  também os containers deste projeto (backend/Postgres) via interface
  web, ou se é ferramenta de uso exclusivo do colega para outra coisa no
  mesmo servidor. Isso afeta se documentamos deploy só via CLI
  (`docker stack deploy`) ou também via Portainer.

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
   de verdade, a recuperação de senha e é pré-requisito pra verificação de
   e-mail no cadastro.
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

## Riscos do mapeamento real do VPS (2026-08-09)

### A. Memória do VPS (2GB) — parecer do `system-design`
`free -h`: 1.9Gi total, 132Mi livre no momento, 473Mi já em uso por
Traefik+Portainer (resto é buff/cache reclamável, não é o mesmo que
"disponível" para heap de JVM). Vamos somar Backend Spring Boot +
Postgres no mesmo servidor.

**Conclusão**: 2GB **cabe** os 4 componentes no volume atual
(~2.500 req/dia), mas **sem margem de segurança** — cenário conservador
(Traefik+Portainer 473Mi + Postgres ~180Mi + Spring Boot ~450Mi RSS real
+ SO ~120Mi) soma ~1.2Gi dos 1.9Gi disponíveis. Risco real de OOM
concentrado em coincidência de eventos (pico de tráfego + `pg_dump` de
backup rodando junto, por exemplo), não no dia a dia. Configurar limites
explícitos de memória é necessário antes de subir Backend+Postgres em
produção — não é opcional dado o orçamento apertado. Upgrade de plano de
VPS é opção a considerar se o usuário quiser folga real em vez de
operar no limite — **decisão do usuário, não dos agentes**.

**Limites propostos para o `devops` aplicar (execução pendente de
aprovação)**:
- JVM Spring Boot: `-Xmx256m -Xms256m -XX:MaxMetaspaceSize=128m
  -XX:+UseSerialGC` (RSS total esperado ~350-450Mi; não ultrapassar
  `-Xmx320m` neste servidor sem reavaliar).
- Postgres: `shared_buffers=128MB`, `work_mem=4MB`, `max_connections=20`
  (RSS total esperado ~150-200Mi).
- Docker Swarm — hard limits por serviço em `stack.prod.yml`:
  `resources.limits.memory` ~400Mi no serviço backend, ~200Mi no
  Postgres (contém um vazamento/pico num container sem derrubar os
  outros).
- Swap: se ainda não existe, swapfile pequeno (512Mi-1Gi) como rede de
  segurança contra o OOM killer errar o alvo em pico momentâneo — não
  resolve falta de RAM estrutural, só absorve espícula.

**A: aplicado (2026-08-09).** Stack file (`stack.prod.yml`) atualizado
com JVM `-Xmx256m`, Postgres `shared_buffers=128MB`/`max_connections=20`,
hard limits Swarm (400M backend / 200M Postgres) — swap já existia no
servidor (2GB), não precisou criar swapfile. Nada rodando ainda no
servidor pra aplicar limite ao vivo (backend/Postgres não deployados).

Hardening de SSH (`PasswordAuthentication`/`PermitRootLogin`): decisão
fechada, mantém como está — ver `Decisoes de Sistemas.md`, seção 7-C.
Lição técnica (drop-in do cloud-init sobrepondo `sshd_config`) em
`.claude/knowledge/devops.md`. Sem pendência aqui.

## Setup do VPS (retomar)

`devops` detalha e executa. Decisões de arquitetura já fechadas.
Orquestrador real confirmado como **Docker Swarm** (não docker-compose
simples) — deploy de produção é via `docker stack deploy -c
stack.prod.yml <nome-da-stack>`, executado na VPS. `docker-compose.yml`
(raiz do repo) continua existindo só para desenvolvimento local, nunca
usado em produção. Ver `Decisoes de Sistemas.md`, seção 7-B.

### Checklist técnico do VPS
- ~~Instalar Docker + Docker Compose~~ — **feito, confirmado (colega já
  instalou, Docker Swarm ativo, `systemd` habilitado).**
- ~~Configurar firewall (`ufw`)~~ — **feito, confirmado**: libera só
  `22`/`22022`/`80`/`443`, Postgres não exposto externamente.
- ~~Configurar SSH: autenticação por chave~~ — **feito pro usuário
  `eduardo`** (2026-08-08). Hardening adicional: ver seção própria
  "Hardening SSH (proposto, aguardando aprovação)" abaixo — já tem plano
  concreto, falta só o aval antes de rodar no servidor compartilhado com
  o colega.
- ~~Nginx como reverse proxy~~ — **removido do plano definitivamente.**
  Traefik (`v3.5.3`, stack Swarm já rodando) cobre o roteamento via
  labels Docker (`traefik.enable=true` + `Host(...)` na rede overlay
  `ja`).
- ~~Certbot pra TLS~~ — **removido do plano definitivamente.** Traefik já
  tem resolver ACME (`letsencryptresolver`, HTTP challenge) configurado
  e funcionando para os serviços existentes; backend usa o mesmo
  resolver via label `traefik.http.routers.jaa-backend.tls.certresolver`.
- **Postgres em container Docker**: ainda não deployado. Definido em
  `stack.prod.yml` (raiz do repo), rede overlay interna própria do
  stack (`backend-internal`, `internal: true`), nunca na rede `ja`, sem
  porta publicada.
- **Backend**: ainda não deployado. Definido em `stack.prod.yml`, rede
  `ja` (`external: true`) + `backend-internal`, labels Traefik para
  `api.jogadoresanonimos.com.br`.
- **Portainer**: já instalado pelo colega — uso pro nosso projeto ainda
  não confirmado (ver pendência de confirmação acima).
- ~~Rotina de backup (Postgres + storage local de imagens)~~ — **scripts
  prontos (2026-08-09)**, ver seção "Rotina de backup recorrente"
  abaixo. Falta só a execução no VPS (copiar `infra/backup/` pro
  servidor, instalar `rclone`/`gpg`, preencher `.env.backup`, habilitar
  o timer).
- Monitoramento básico — uptime check simples (ver "Keep-alive externo"
  abaixo, decisão adiada até o VPS estar rodando).
- ~~Decidir se sobe como serviço `systemd`~~ — **resolvido**: Docker já é
  serviço `systemd` habilitado; Swarm reagenda os serviços da stack
  automaticamente quando o Docker sobe de novo, sem script adicional.

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

~~`docker-compose.yml` não declarava `RESEND_API_KEY` no serviço
`backend`~~ — **corrigido (2026-08-08)**: env adicionada, mesmo padrão
de `JWT_SECRET`/`RECAPTCHA_SECRET_KEY`.

**Novo arquivo de produção: `stack.prod.yml`** (raiz do repo, 2026-08-09)
— stack Swarm (backend + Postgres) pra `docker stack deploy` na VPS, ver
`Decisoes de Sistemas.md` seção 7-B para o detalhe de cada decisão
(redes, labels Traefik, secrets via env, limites de recurso, rollback).
Ainda não executado no VPS.

~~Artefatos órfãos de plano anterior~~ — **removidos (2026-08-09)**:
`docker-compose.staging.yml`, `Caddyfile.staging` (raiz) e
`frontend/Dockerfile` (buildava com Caddy standalone) foram escritos
para um plano anterior de Caddy manual + frontend também no VPS, hoje
obsoleto (Traefik já assume reverse proxy/TLS, frontend é Vercel).
Confirmado que nenhum script/CI/documentação viva referenciava esses
arquivos antes de apagar.

~~Risco aberto do fail-fast de config~~ — **corrigido (2026-08-09)**:
`ProdConfigValidator` agora também cobre `MFA_SECRET_ENCRYPTION_KEY`,
`LOG_HMAC_SECRET` e `CORS_ALLOWED_ORIGINS` (fail-fast real, boot quebra
em prod se vazio ou com o default de dev), mesmo padrão de
`RECAPTCHA_SECRET_KEY`/`RESEND_API_KEY`. Regra de processo (env nova via
`@Value` deve ser fail-fast por padrão) adicionada ao `CLAUDE.md`, seção
"Padrões de arquitetura".

## Deploy do backend + Postgres no VPS — EXECUTADO (2026-08-09)

`docker stack deploy -c stack.prod.yml jaa` rodado com sucesso no VPS.
Ambos os bloqueadores do checklist pré-voo abaixo foram resolvidos:

1. **Imagem buildada direto no VPS** (opção (a) do checklist, sem
   registry/CI-CD, conforme já sinalizado como caminho mais simples pro
   estágio atual). Código-fonte do backend copiado via `tar`+`scp` (não
   `git clone` — repo remoto está bem atrasado do working tree local,
   muitos arquivos modificados/untracked ainda não commitados). Fonte
   fica em `~/deploy/backend` no servidor, para rebuilds futuros. Build
   `docker build -t jogadoresanonimos/backend:latest .` rodou limpo
   (~24s), imagem final 209MB.
2. **`.env.prod` montado em `~eduardo/.env.prod`** (permissão `600`, fora
   do repo git). Segredos criptográficos (`JWT_SECRET`,
   `LOG_HMAC_SECRET`, `MFA_SECRET_ENCRYPTION_KEY`, `RECAPTCHA_SECRET_KEY`,
   `RESEND_API_KEY`) copiados do `.env` de dev local (transferido via
   `scp` direto pro servidor e apagado logo depois de extrair os
   valores — nunca passou pelo terminal/log da sessão). `DB_NAME`/
   `DB_USERNAME`/`DB_PASSWORD` gerados novos e específicos de produção
   (senha aleatória 32 chars, não reaproveita a de dev).
   `STORAGE_PUBLIC_BASE_URL=https://api.jogadoresanonimos.com.br/stub-storage`
   (path confirmado em `LocalDiskStorageClient`/`ImagemPublicController`).
   `ADMIN_LIMITE_MAXIMO=5` — não existia valor em dev (propriedade sem
   default, fail-fast se ausente/inválida em `UsuarioService`), 5 é valor
   razoável documentado aqui para o porte da associação; ajustável depois
   sem migration.

**Resultado da subida**: `docker service ls` — `jaa_backend` e
`jaa_postgres` 1/1, junto de `portainer_agent`/`portainer_portainer`/
`traefik_traefik` também 1/1 (não afetados). Uma réplica de backend
falhou na primeira tentativa (`UnknownHostException: postgres` — rede
overlay do Postgres ainda não resolvia DNS no instante exato do primeiro
boot), `restart_policy: on-failure` do Swarm reagendou automaticamente e
a segunda tentativa subiu limpo ~15s depois: Flyway aplicou as 6
migrations existentes (v1 a v6) em schema vazio, sem erro, `admin_bootstrap_criado`
logado, `ProdConfigValidator` não acusou fail-fast nenhum (todas as envs
obrigatórias presentes). `/actuator/health` testado dentro do container
(`{"status":"UP"}`) e via Traefik usando IP direto + header `Host`
forjado (`200`, health `UP`) — confirma que o roteamento Traefik→backend
e o TLS (self-signed do Traefik nesse teste) já funcionam ponta a ponta.

**Resolvido (2026-08-09): DNS de `api.jogadoresanonimos.com.br`
configurado** (registro A → `143.95.219.110`, no Zone Editor da
HostGator — primeira tentativa foi feita sem querer na zona `.org.br`,
corrigida para `.com.br`). Traefik havia tentado o ACME HTTP challenge
antes do DNS existir (falhou com `NXDOMAIN`, não reagenda sozinho) —
forçado `docker service update --force traefik_traefik` pra reprocessar.
**Certificado real da Let's Encrypt emitido**: `issuer=Let's Encrypt`,
válido `2026-08-09` a `2026-11-07`. `https://api.jogadoresanonimos.com.br/actuator/health`
responde `200` sem `-k`, confirmado. Sem pendência restante neste item.

**Pendente de decisão/execução depois deste deploy**:
- Confirmar dados reais do admin bootstrap com o cliente antes de expor
  publicamente (hoje usa os valores passados nesta tarefa —
  `adminJA`/`jogadoresanonimos@yahoo.com.br`) — trocar a senha após
  primeiro login se for só provisória.
- CORS: `CORS_ALLOWED_ORIGINS` aplicado com a lista de domínios Vercel +
  `.org.br` já documentada; revalidar de fato assim que o frontend
  estiver no Vercel e os domínios finais confirmados.
- Rotina de backup do Postgres/storage ainda não existe (ver checklist
  do VPS abaixo) — banco de produção já tem dado real (admin bootstrap)
  a partir de agora, isso eleva a prioridade.

## Deploy / cutover (ordem recomendada)
1. ~~Backup do WordPress~~ — **feito e confirmado (2026-08-08)**.
2. **Setup do VPS do zero** (checklist acima) — backend rodando e
   respondendo no IP antes de tocar em DNS.
3. **Deploy do frontend no Vercel**: conectar repositório/build Angular,
   configurar domínio customizado `jogadoresanonimos.com.br` + `www.` no
   painel Vercel (TLS automático deles).
4. **Deploy do backend + Postgres no VPS via Swarm**: `docker stack
   deploy -c stack.prod.yml <nome-da-stack>`, com Traefik (já rodando)
   assumindo o roteamento + TLS automático de `api.jogadoresanonimos.com.br`
   por label Docker — sem Nginx/Certbot manual (plano antigo removido).
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
- Certificado TLS: Vercel cobre `.com.br`/`www.` automaticamente; Traefik
  (ACME embutido) no VPS cobre `api.jogadoresanonimos.com.br`. Confirmar
  os dois lados emitidos/válidos antes do corte de DNS.
- Redirect sempre HTTPS→HTTPS na borda; sem cookie/sessão compartilhada
  entre domínios (JWT vai em header/body, não cookie) — relevante entre
  frontend (Vercel) e backend (`api.` no VPS).

### Itens de infra que continuam pendentes de execução
- ~~**[CRÍTICO] Backup do Postgres + storage local de imagens**~~ —
  **executando de verdade desde 2026-08-09** (ver seção "Rotina de
  backup recorrente"). Falta só instalar o `systemd timer` (bloqueado
  por `sudo` interativo, ver pendência detalhada na seção própria) —
  execução manual do script cobre o intervalo até lá.
- **Uptime monitoring externo** (diferente de keep-alive anti-cold-start
  abaixo, que segue corretamente descartado): ainda em aberto — hoje não
  há forma de saber se o site caiu. Item barato (ex: free-tier tipo
  UptimeRobot, sem overhead no VPS). `devops` executa quando houver
  disponibilidade.
- **Keep-alive externo** (ping em `/actuator/health` a cada ~10-14min):
  **decisão adiada deliberadamente pelo cliente (2026-08-04)** — a causa
  raiz (cold start do Render) deixou de existir com VPS próprio; cliente
  optou por observar o comportamento real do VPS antes de decidir manter
  (como uptime check) ou desligar. Mantido descartado — não confundir com
  o item de uptime monitoring acima, que é um problema diferente
  (detectar queda, não cold start).
- ~~**Rollback de imagem Docker com tag `latest`**~~ — **processo
  documentado (2026-08-09)**, ver `Decisoes de Sistemas.md` seção 7-B.
  A cada rebuild manual no VPS, taguear a imagem com `latest` **e** com o
  SHA curto do commit (`git rev-parse --short HEAD`), antes de `docker
  stack deploy`:
  ```
  docker build -t jogadoresanonimos/backend:latest \
    -t jogadoresanonimos/backend:<sha-curto> .
  ```
  Rollback = trocar a tag da imagem em `stack.prod.yml` pro SHA anterior
  e rodar `docker stack deploy` de novo. Sem CI/CD completo (não se
  justifica pela cadência manual atual) — reavaliar se a cadência de
  deploy aumentar. Nenhum deploy real ainda usou esse processo (o deploy
  de 2026-08-09 buildou só com `latest`); aplicar a partir do próximo
  rebuild.
- **Uptime monitoring externo** [achado `system-design`, 2026-08-09] —
  ainda não configurado, **bloqueado pelo DNS** de
  `api.jogadoresanonimos.com.br` (ainda não aponta pro VPS, ver seção
  "Deploy / cutover"). Passo a passo para configurar assim que o DNS
  estiver de pé:
  1. Criar conta grátis em **UptimeRobot** (recomendado: free tier cobre
     até 50 monitores, checagem a cada 5min, alerta por e-mail — mais que
     suficiente pro volume deste projeto de único endpoint a monitorar;
     alternativa equivalente: Better Uptime free tier).
  2. Criar monitor tipo HTTP(S), URL
     `https://api.jogadoresanonimos.com.br/actuator/health`, intervalo
     5min (padrão free tier).
  3. Configurar alerta por e-mail para
     `jogadoresanonimosbr@gmail.com` (mesma conta já usada pro backup no
     Google Drive) quando o monitor cair.
  4. Confirmar que o monitor primeiro reporta "up" antes de considerar
     concluído (evita alerta falso logo na configuração, por conta de
     propagação de DNS/certificado ainda em andamento).
  Requer conta/e-mail — decisão de qual e-mail usar fica com o usuário
  antes de cadastrar.
- **CDN (Cloudflare ou equivalente)** [baixa prioridade, achado
  `system-design` 2026-08-09]: decisão tomada (`Decisoes de Sistemas.md`
  seção 5) — origin aponta pro backend/disco local, sem bucket S3. Falta
  execução, e execução **depende do DNS** de
  `api.jogadoresanonimos.com.br` estar apontando pro VPS primeiro (não dá
  pra configurar Cloudflare na frente de um domínio que ainda não
  resolve). Próximo passo, só depois do DNS: colocar
  `api.jogadoresanonimos.com.br` atrás do Cloudflare (proxy modo
  "orange cloud"), origin = IP do VPS, TLS modo "Full (strict)" pra não
  quebrar o certificado ACME que o Traefik já emite. Vercel já cobre CDN
  do estático (frontend); esta pendência é só sobre o
  backend/disco local no VPS. Sem ação adicional antes do DNS.
- **[MÉDIO, achado `code-reviewer` 2026-08-09] `LocalDiskStorageClient`
  — defesa em profundidade contra path traversal**: só `download` valida
  `normalize()+startsWith(baseDir)`; `upload`/`delete` não têm a mesma
  checagem. Risco teórico hoje (chave sempre é UUID gerado internamente),
  mas extrair helper `resolverDentroDoBaseDir(chave)` e aplicar nos 3
  métodos é barato. `backend` executa.
- **[ALTO, achado `code-reviewer` 2026-08-09] `admin-reunioes.ts` usa
  `HttpClient` direto no componente** (ViaCEP/Nominatim), bypassando a
  convenção de service — extrair `EnderecoLookupService`. `frontend`
  executa.
- **[MÉDIO, achado `code-reviewer` 2026-08-09] Artefatos órfãos**:
  `docker-compose.staging.yml`, `Caddyfile.staging` e `frontend/Dockerfile`
  (builda com Caddy standalone, não referenciado em nenhum stack ativo)
  — plano de infra anterior (Caddy+VPS), hoje obsoleto (Traefik+Vercel).
  Confirmar com o usuário antes de remover.

## Observabilidade

Decisão fechada (2026-08-09): observabilidade mínima via Spring Boot
Actuator + log estruturado local + painel simples, sem serviço externo
gerenciado (ver `Decisoes de Sistemas.md`). Sem Micrometer/Prometheus/
Grafana — overengineering dado o volume do projeto (~2.500 req/dia).

### Implementado agora (não depende do VPS)
- `management.endpoints.web.exposure.include=health,metrics,httpexchanges,info`
  em `application.properties`/`.example`.
- `/actuator/health` público (necessário pro ping anti-cold-start
  existente); todo o resto de `/actuator/**` exige `ROLE_ADMIN` via JWT
  (mesmo padrão de `/api/v1/admin/**`), configurado em `SecurityConfig`.
- Endpoints sensíveis (`env`, `beans`, `configprops`, `threaddump`,
  `heapdump`, `shutdown`) desabilitados explicitamente — nunca expostos
  mesmo atrás de auth, risco de vazar segredo de ambiente/heap.
- Bean `HttpExchangeRepository` (`InMemoryHttpExchangeRepository`,
  capacidade 200) criado em `ObservabilidadeConfig` — sem isso
  `/actuator/httpexchanges` não retorna nada.
- Headers de request/response excluídos do registro de httpexchanges
  (`management.httpexchanges.recording.request-headers/response-headers
  =false`) e `remote-address` não incluído — Authorization/cookies e IP
  não aparecem no exchange, por causa do requisito de anonimato.

### Pendente de execução (só faz sentido com VPS real)
- **Log estruturado em arquivo com rotação** (ex: JSON via Logback
  `RollingFileAppender`) — hoje log só vai pro stdout do container.
  Precisa de path de disco persistente no VPS e política de retenção
  (ver LGPD no CLAUDE.md — não guardar log além do necessário).
- **Painel simples de consulta** — decisão: nenhum serviço adicional
  rodando (VPS é recurso escasso). "Painel" = consultar `/actuator/health`,
  `/actuator/metrics/{nome}` e `/actuator/httpexchanges` autenticado como
  admin, mais `docker logs`/arquivo de log local. Nenhum Grafana/Prometheus.
- **Alertas** (taxa de erro, latência, uso de recurso, eventos de
  segurança do agente `security`) — definir depois que houver baseline
  real de uso no VPS; sem serviço de alerta externo contratado por ora.
- **Retenção de log de infraestrutura** — definir prazo concreto junto à
  política de retenção geral de dados (LGPD) quando o log em arquivo
  existir de fato.

## Features de e-mail

### Recuperação de senha — completa, incluindo envio real
Implementada ponta a ponta (backend + frontend + testes escritos),
desenho V2 unificado (endpoint único `POST /api/v1/auth/esqueci-senha`,
branch server-side por `role`, anti-enumeration, reset de admin nunca pula
MFA). Detalhe de implementação/decisão foi para `.claude/knowledge/` e
`backend/docs/api/contrato-recuperacao-senha.md`. Envio real de e-mail via
Resend plugado (2026-08-08). Texto do e-mail de recuperação
(`EmailService.enviarLinkRedefinicaoSenha`) revisado — travessão
removido, frase reescrita com vírgula.

**Suíte executada de ponta a ponta (2026-08-09), com Postgres real
(`ja-test-postgres`, porta 5433, evita colisão com o Postgres de dev na
5432). `mvnw.cmd clean test` ainda trava no goal `clean` (mesmo handle de
arquivo preso em `target/classes`, mesmo com OneDrive pausado e
`backend/target` excluído do Defender) — contorno: `mvnw.cmd test` sem
`clean` roda normalmente. `mvnw test` sozinho só executa `*Test.java`
(convenção Surefire padrão, sem plugin Failsafe configurado no `pom.xml`)
— testes `*IT.java` (integração, `@ActiveProfiles("qa-integration")`)
precisam ser chamados explicitamente via `-Dtest=NomeDaClasseIT`, e
exigem env vars extras no processo (`DB_URL`/`DB_USERNAME`/`DB_PASSWORD`
apontando pro Postgres de teste, mais `ADMIN_LIMITE_MAXIMO` e as demais
envs obrigatórias do boot — sem isso o contexto Spring nem sobe,
`ADMIN_LIMITE_MAXIMO não configurado`).

**Atualização (2026-08-09) — os 3 bugs de teste corrigidos e
reverificados com Postgres real (`ja-test-postgres`, porta 5433):**
1. `EmailConfirmacaoTokenRepositoryIT` / `AuthConfirmacaoEmailControllerIT`
   — apelido de teste estourando `varchar(30)` corrigido (prefixo curto +
   `UUID.randomUUID().toString().substring(0, 8)`, bem dentro do limite).
   `AuthConfirmacaoEmailControllerIT`: **15/15 verde**.
   `EmailConfirmacaoTokenRepositoryIT`: **6/7 verde** — 1 falha
   remanescente, bug diferente e fora do escopo desta correção (ver
   "Bug de teste fora de escopo" abaixo).
2. `UsuarioAdminRolePromocaoMfaIT` — helper `tokenDe()` corrigido pra usar
   `usuario.getSenhaAlteradaEm()` real em vez de `Instant` arbitrário. Ao
   corrigir, apareceu um segundo problema que mascarava o resultado (ver
   "Bug real de produção achado" abaixo) — corrigido esse bloqueio
   também, a suíde passou a exercitar o código de verdade e revelou um
   **bug real de produção** (não mais de teste). Detalhe abaixo.
3. `AuthEsqueciSenhaControllerIT.java.bak` restaurado como `.java`,
   corrigido erro de compilação (variável reatribuída dentro de lambda,
   não permitido em Java — passou a usar variável nova
   `usuarioConfirmado`). **10/10 verde.**

**3 itens de código de teste — RESOLVIDOS (2026-08-09, `qa-backend-integration`)**:

1. ~~`EmailConfirmacaoTokenRepositoryIT.deleteDoUsuario_deveApagarTokensPorCascade`
   dava falso negativo~~ — **corrigido**. Causa: `DELETE` via conexão JDBC
   crua rodava fora/antes do commit da transação do `@DataJpaTest`, então
   nunca via a linha inserida. Corrigido seguindo o padrão já usado em
   `EstadoIntergrupoRepositoryIT` (`@Transactional(propagation =
   Propagation.NOT_SUPPORTED)` no método, com todo o setup também via
   JDBC cru, sem `TestEntityManager`, já que este exige transação ativa).
   Teste agora exercita o cascade de verdade. **7/7 verde.**
2. ~~`UsuarioAdminRolePromocaoMfaIT` com isolamento ruim
   (`@DirtiesContext(AFTER_CLASS)`)~~ — **corrigido**. Trocado por
   `@AfterEach` explícito que deleta os usuários de teste criados em cada
   método (lista rastreada em `criarUsuario`). Ao rodar a classe inteira
   surgiu um segundo bug de teste (não de produção): `RestTemplate` com
   `SimpleClientHttpRequestFactory`-equivalente do Spring Framework 7
   (`JdkClientHttpRequestFactory` não estava sendo usado, o default do
   `RestTemplate` some com o corpo de erro em requests `PUT`) fazia
   `ex.getResponseBodyAsString()` retornar `""` em vez do JSON de erro
   esperado, nos 2 cenários de TOTP incorreto/ausente — só nesses porque
   são os únicos que combinam `PUT` com corpo de erro 4xx nessa classe.
   Corrigido trocando para `new RestTemplate(new
   JdkClientHttpRequestFactory())`. **7/7 verde, confirmado 2x seguidas
   (sem flakiness de ordem).** Nota: o bug real de produção da constraint
   `admin_auditoria_log` (documentado abaixo até esta atualização) já
   tinha sido corrigido pelo `backend` na migration
   `V6__relaxa_constraint_status_admin_auditoria_log.sql` antes desta
   rodada — por isso não reapareceu aqui.
3. ~~5 classes falhando numa rodada completa de `mvnw test`~~ — **causa
   raiz identificada para todas, 2 corrigidas nesta rodada (eram teste),
   3 reportadas ao `qa-backend-unit` (fora do escopo deste agente)**:
   - `JogadoresAnonimosApplicationTests.contextLoads` — **problema de
     ambiente, não de código.** Só falha quando as env vars obrigatórias
     de boot (`DB_URL`/`ADMIN_LIMITE_MAXIMO`/`JWT_SECRET`/etc.) não estão
     setadas no processo Maven. Com as env vars corretas, passa
     normalmente (confirmado na suíte completa, 359 testes).
   - `ReflexaoDiaServiceTest.listarMes_deveRetornarTodosOsDiasDoMesComNullParaNaoCadastrados`
     — **teste desatualizado, corrigido.** Esperava 28 dias em fevereiro;
     `ReflexaoDiaService.DIAS_POR_MES` usa 29 de propósito (calendário
     cíclico de reflexão diária, cobre 29/fev em qualquer ano sem
     depender do ano corrente) — decisão de design intencional, não bug.
     Assert corrigido para 29. **10/10 verde.**
   - ~~`VinteQuestaoServiceTest`, `FaqServiceTest`,
     `EscritorioRegionalServiceTest` (7 erros, métodos
     `moverParaCima`/`moverParaBaixo`/`moverEstadoParaCima`/
     `moverGrupoParaBaixo`/`moverRegiaoParaCima`)~~ — **corrigido pelo
     `qa-backend-unit`.** Causa raiz: `ReordenacaoUtil.trocarComVizinho`
     busca item e vizinho via `repository.buscarPorIdComLock` (lock
     pessimista, commit `8736af8`), não mais `findById`; testes só
     stubavam `findById`. Corrigido com stub de `buscarPorIdComLock`
     para ambos os UUIDs (item e vizinho) nos 7 testes de reordenação
     das 3 classes. 47/47 verde.

**Suíte completa reverificada após as correções (2026-08-09,
`ja-test-postgres` porta 5433)**: `mvnw.cmd test` (359 testes) —
`contextLoads`, `ReflexaoDiaServiceTest` e as 3 classes de reordenação
acima, todos verdes. Sem erros remanescentes conhecidos nesta rodada.

### Verificação de e-mail válido/existente no cadastro — implementação completa
Backend e frontend concluídos (2026-08-09).
- **Backend**: migration `V5` (`email_confirmacao_token` +
  `usuario.email_confirmado_em`), token CSPRNG 32 bytes, hash SHA-256
  puro (não HMAC — justificado no contrato), TTL 24h, single-use.
  Endpoints: geração integrada ao `POST /auth/registro` (falha de envio
  não derruba cadastro), `GET /api/v1/auth/confirmar-email` (idempotente,
  3 casos), `POST /api/v1/auth/reenviar-confirmacao-email` (sempre 200
  genérico, rate limit duplo). `esqueciSenha` (ramo usuário comum) agora
  não envia token de reset se e-mail não confirmado — resposta HTTP
  inalterada (sem vazar estado). Try/catch defensivo adicionado nos 3
  métodos de envio assíncrono do `EmailService` (o novo + os 2 já
  existentes). Contrato completo:
  `backend/docs/api/contrato-confirmacao-email.md`.
- **Frontend**: página `/confirmar-email` com os 3 estados
  (sucesso/expirado/já confirmado), banner de pendência persistente,
  avisos pontuais em loja e esqueci-senha.
- **Decisão de negócio confirmada pelo usuário (2026-08-09): conta
  pendente de confirmação NÃO bloqueia login nem acesso ao site.** Login
  funciona normal sem confirmar e-mail; só aparece banner de aviso
  persistente. Pendência de confirmação restringe apenas duas coisas:
  compra física na loja e recuperação de senha por e-mail.
- **Code review**: parecer final **aprovado com ressalva**. Ressalvas já
  corrigidas — linhas em branco decorativas removidas em
  `confirmar-email.html` (linhas 14/31) e `esqueci-senha.html` (linhas
  14/21). Achado MEDIUM sobre try/catch cosmético em torno da chamada
  `@Async` ficou registrado como observação, não como ação pendente (a
  proteção real já está no catch interno do `EmailService`).
- **Rate limit de reenvio de confirmação — revisado e aprovado pelo
  `security` (2026-08-09)**: 3/10min por usuário + 30/min bucket global,
  sem alteração. Consistente com precedente `registro-varredura-global`
  (mesmos 30/min); volume do site (~2.500 req/dia) torna os limiares
  folgados o bastante para não gerar fricção legítima e ainda cobrir
  abuso (spam de e-mail a terceiro, scraping de contas). Ver
  `backend/docs/api/contrato-confirmacao-email.md`, seção 3.
- **Testes: escritos (2026-08-09), execução da suíte completa bloqueada
  por ambiente.**
  - `qa-backend-unit` (`AuthServiceTest.java`): cobertura completa —
    geração de token no registro (hash SHA-256 não-HMAC, TTL ~24h,
    ordem síncrona token-antes-do-envio), falha do envio assíncrono não
    propaga exceção pro registro, `confirmarEmail` (token vazio, não
    encontrado, usuário do token não existe mais, expirado e nunca
    usado, sucesso, idempotente com token já usado), reenvio (conta
    inexistente, já confirmada, dispara novo token quando pendente,
    rate limit por usuário 3/10min ainda responde genérico, rate limit
    global 30/min lança `RateLimitedException`), `esqueciSenha` não gera
    nem envia token quando e-mail não confirmado.
  - `qa-backend-integration`: `AuthConfirmacaoEmailControllerIT.java` +
    `EmailConfirmacaoTokenRepositoryIT.java` cobrindo os endpoints
    (`confirmar-email` sucesso/idempotente/expirado 401/vazio 400/rate
    limit 429; `reenviar-confirmacao-email` sempre 200 genérico nos 3
    ramos + rate limit duplo; persistência do token na mesma transação
    do registro; migration V5 não quebra dado legado). Também
    complementado `AuthEsqueciSenhaControllerIT.java` com o caso e-mail
    não confirmado → sem `PasswordResetToken`, resposta genérica
    idêntica.
  - **Executado e corrigido (2026-08-09)**: ambiente destravado (Postgres
    real na porta 5433 + envs completas, contorno de `clean` travado
    documentado acima). `AuthServiceTest` 100% verde.
    `AuthConfirmacaoEmailControllerIT` **15/15 verde** e
    `AuthEsqueciSenhaControllerIT` **10/10 verde** depois do fix de
    apelido/`.bak` (ver "Suíte executada de ponta a ponta" acima).
    `EmailConfirmacaoTokenRepositoryIT` 6/7 (1 falha residual fora de
    escopo, ver acima). Feature considerada testada de fato.

## Priorizado pelo cliente antes do deploy final

- ~~Step-up MFA na promoção de admin~~ — **backend e frontend completos
  (2026-08-09)**. Ver
  `backend/docs/api/contrato-step-up-mfa-promocao-admin.md` e
  `contrato-usuario-admin.md` (regra 4). Promoção (`não-ADMIN → ADMIN`)
  em `PUT /api/v1/admin/usuarios/{id}/role` exige `codigoTotp` do admin
  promotor, validado via `TotpService` já existente. Frontend: input
  TOTP condicional em `admin-usuarios`, tratamento dos 2 `error.code`
  novos (`USUARIO_PROMOCAO_MFA_NAO_CONFIGURADO`,
  `USUARIO_PROMOCAO_TOTP_INVALIDO`). Ajuste extra: retry inline no modal
  quando o TOTP é inválido — antes fechava o fluxo inteiro, agora mantém
  o modal aberto, limpa só o campo do código e mostra o erro.
  **Testes: executados (2026-08-09).** `qa-backend-unit`
  (`UsuarioServiceTest.java`) — **28/28 passando, sem bug**: promoção sem
  MFA configurado (409), TOTP ausente/incorreto/formato inválido
  (401/400), caminho feliz, prioridade do limite de admins sobre o
  step-up, rebaixamento e no-op ignoram TOTP mesmo se enviado.
  `qa-backend-integration` (`UsuarioAdminRolePromocaoMfaIT.java`) —
  bug de teste do `tokenDe()` corrigido (usa
  `solicitante.getSenhaAlteradaEm()` real). Ao destravar o teste de
  verdade (também precisou remover `@Transactional` de classe, que
  escondia dados de setup da chamada HTTP real), a suíte revelou um
  **bug real de produção, não mais de teste**: `UsuarioService
  .editarRole` grava `Role.name()` em `admin_auditoria_log
  .status_anterior`/`status_novo`, e a constraint do banco só aceita
  `pending`/`approved`/`rejected` — **toda promoção/rebaixamento de
  admin quebra com 500 em produção hoje**. Feature de step-up MFA acima
  está implementada mas nunca rodou de fato fora de teste com rollback.
  Reportado ao `backend` para decidir o fix de schema/constraint — não é
  decisão do `qa-backend-integration`. Ver detalhe completo na seção
  "Suíte executada de ponta a ponta" acima.

  **Corrigido pelo `backend` (2026-08-09).** Confirmado: a tabela
  `admin_auditoria_log` já era log de auditoria administrativa genérico
  (campo `entidade` varia entre `relato`, `mfa`, `usuario`), usado por 3
  serviços diferentes — a constraint `CHECK` em `status_anterior`/
  `status_novo` (`pending`/`approved`/`rejected`) é que ficou presa ao
  domínio original (moderação de relato) e nunca foi atualizada quando o
  log virou genérico. Achado adicional: `MfaService` (enroll/confirmar/
  desativar MFA) **já violava a mesma constraint em produção antes desta
  tarefa** (`MFA_INATIVO`/`MFA_PENDENTE`/`MFA_ATIVO` também não batem com
  `pending/approved/rejected`) — não é bug introduzido pelo step-up MFA,
  é pré-existente e mais abrangente do que o relatado inicialmente.
  Correção: migration `V6__relaxa_constraint_status_admin_auditoria_log.sql`
  remove os dois `CHECK` e amplia `varchar(20)` para `varchar(30)`
  (`AdminAuditoriaLog.java` atualizado no mesmo tamanho). Decisão:
  relaxar a constraint existente, não criar tabela de auditoria separada
  para mudança de role — duplicar estrutura não se justifica só porque o
  domínio de valores é maior que o previsto originalmente. Documentado em
  `backend/docs/api/contrato-step-up-mfa-promocao-admin.md`, seção
  "Anonimato / auditoria". **Validado com Postgres real** (`ja-test-postgres`,
  porta 5433): `UsuarioAdminRolePromocaoMfaIT
  .devePromoverComSucessoQuandoTotpDoPromotorEstaCorreto` isolado → 200 OK,
  sem `ConstraintViolationException`. Rodada completa da classe ainda
  acumula falha entre métodos por reaproveitar admins de teste anteriores
  sem limpeza por método (`@DirtiesContext(AFTER_CLASS)` só limpa no
  fim) — isso é problema de isolamento de teste, não do fix; sinalizar ao
  `qa-backend-integration` para revisar `@DirtiesContext` por método ou
  limpeza explícita no `@BeforeEach` dessa classe.

## Literatura em PDF
Removido do backlog — cliente vai enviar depois, sem previsão. Entra via
admin quando o material chegar.

## SEO / descoberta (preparação pré-deploy, 2026-08-09)
- **Google Search Console — pendente de execução pós-deploy.** Depende
  do domínio `jogadoresanonimos.com.br` estar de pé em produção (ver
  "Deploy / cutover" acima). Não é tarefa de código: registrar a
  propriedade no Search Console, verificar domínio (DNS TXT ou meta
  tag), submeter o `sitemap.xml` gerado pelo `frontend`. Fazer só depois
  do corte de DNS confirmado e site respondendo publicamente.
- **Meta tags, sitemap, llms.txt, robots.txt e copy — completo
  (2026-08-09).**
  - `ux-ui`: title/description/og:title/og:description + palavras-chave
    para as 21 páginas públicas, tom acolhedor, sem clickbait/estigma.
  - `frontend`: service central `Seo` (`frontend/src/app/services/seo.ts`)
    lendo `data.seo` por rota em `NavigationEnd` — decisão de
    centralizar em vez de repetir Title/Meta em cada componente.
    `data.seo` de cada rota pública em `frontend/src/app/app.routes.ts`
    já com o copy final do `ux-ui` aplicado (não é mais placeholder).
    `frontend/src/public/sitemap.xml` (22 URLs públicas, estático,
    build-time), `frontend/src/public/llms.txt` e
    `frontend/src/public/robots.txt` (novo — não existia antes;
    disallow de `/admin`, `/minha-conta`, `/esqueci-senha`,
    `/redefinir-senha`, `/confirmar-email`, aponta pro sitemap).
  - Rotas de auth/conta (`/admin/*`, `/minha-conta`, `/esqueci-senha`,
    `/redefinir-senha`, `/confirmar-email`, `/admin/redefinir-senha`)
    deliberadamente fora do SEO/sitemap — não são conteúdo institucional
    indexável.
  - Nada commitado.

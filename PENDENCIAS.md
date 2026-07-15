# Pendências

> Arquivo único de pendências reais em aberto. Itens concluídos não ficam
> aqui — histórico de decisão/implementação vive em `Decisoes de
> Sistemas.md` e `.claude/knowledge/`.

## Infraestrutura / Deploy (aguardando decisão de hospedagem do cliente)

- **`apiBaseUrl` real de produção e `recaptchaSiteKey` de produção** —
  `frontend/src/environments/environment.prod.ts` já existe (com
  `fileReplacements` configurado no `angular.json`), mas `apiBaseUrl` está
  como `/api/v1` (relativo, assumindo reverse proxy servindo front+back
  no mesmo domínio) até a hospedagem ser decidida. `recaptchaSiteKey`
  também está com a mesma chave do dev por não haver chave própria de
  produção documentada — chave de dev provavelmente só está registrada
  para `localhost`/domínio de dev no console do Google reCAPTCHA e vai
  falhar validação em domínio novo. Pré-requisito prático para qualquer
  outro passo de deploy.
- **Postgres produção** — dev já fechado (container local via compose),
  prod pendente.
- **Provedor de e-mail real** — `EmailService` hoje só loga, sem
  SMTP/API real.
- **Onde guardar secrets** (`JWT_SECRET`, `LOG_HMAC_SECRET`,
  `MFA_SECRET_ENCRYPTION_KEY`) — chave `MFA_SECRET_ENCRYPTION_KEY` já foi
  gerada (32 bytes Base64, AES-256) e entregue ao usuário fora do repo;
  falta só o destino final de armazenamento/injeção em produção.
- **Dados do primeiro admin real** (apelido/e-mail/senha de bootstrap).
- **Backup do diretório de storage local** — bloqueante pra produção,
  precisa entrar na rotina de backup do servidor junto com o Postgres.
- **CDN (Cloudflare ou equivalente) na frente do domínio real** —
  decisão já tomada pelo `system-design` (ver `Decisoes de Sistemas.md`
  seção 5): origin apontando pro próprio backend/disco local, sem
  bucket S3, sem mudar a decisão de storage já fechada. Entra no
  checklist de deploy assim que hospedagem for decidida — não é questão
  de volume de tráfego, CDN de assets estáticos é barato mesmo em baixo
  tráfego.

## Checagem final (2026-07-15) — achados a corrigir antes de "100% funcional"

> Resultado do passo 1 do roteiro abaixo. Revisão feita por frontend,
> backend, banco, security, code-reviewer e qa-backend-unit (estático,
> sem rodar suíte). Itens HIGH e MEDIUM técnicos corrigidos em
> 2026-07-15 (ver `.claude/knowledge/` para detalhe de cada fix). Nenhum
> commit feito. Resta 1 decisão de produto (abaixo) e os itens LOW.

### Corrigido nesta sessão (2026-07-15), fora do fluxo normal de achado

- **CRÍTICO, achado durante o trabalho de MEDIUM (não estava na lista
  original)**: `TokenBucket.tryConsume()` nunca bloqueava — rate
  limiting estava inoperante em todo o projeto (login, MFA, registro,
  esqueci-senha, upload). Bug de lógica no ramo CAS sem tokens
  (`return compareAndSet(...) || tryConsume()` sempre retornava `true`).
  Corrigido pelo `security`. Isso significa que **todo rate limit do
  sistema era cosmético até esta correção** — reforça a prioridade de
  ter testes cobrindo rate limit (feito também nesta sessão).
- Baseline `V1__baseline.sql` regenerada (segunda consolidação, V2-V7 →
  V1), schema validado por diff de `pg_dump` zero. Próxima migration
  nova é `V2`. Ver `backend/docs/migrations-consolidadas-decisoes.md`.
- GC de arquivo órfão agora respeita biblioteca pessoal
  (`UsuarioArquivoRepository`) além do CMS.
- N+1 removido nos jobs de GC (`ArquivoOrfaGcJob`/`ImagemOrfaGcJob`,
  agora query única) e em `listarEstruturaCompleta` de escritórios
  regionais (fetch join completo).
- Refetch completo trocado por merge local no admin CRUD (avisos, faq,
  literatura, hotlines, reuniões, escritórios/endereços).
- Testes adicionados: `TotpService`, `MfaSecretCipher`, `MfaService`,
  `MfaAdminController`, `AuthService.loginComMfa`, e novo
  `RelatoServiceTest` verificando trilha de auditoria em todas as ações.
- `Auth` service (frontend) ganhou mock para
  `loginComMfa`/`enrollMfa`/`confirmarMfa`/`desativarMfa`.

### Decisão de produto pendente (não é bug — precisa de decisão sua)

- [ ] **Auditoria admin write-only.** Hoje só grava, sem endpoint de
  leitura nem rotina de expurgo/retenção. Recomendação técnica: manter
  como está por ora — não há tela/consumidor definido para um painel de
  auditoria ainda, criar endpoint agora seria antecipar trabalho sem uso
  real. Decidir quando: (a) construir o endpoint de leitura + tela admin
  agora mesmo assim, ou (b) manter como pendência até haver necessidade
  concreta, definindo já a política de retenção/expurgo.

### LOW

- [ ] Cores hardcoded fora do design system: `home.css:289,467,468`,
  `admin-reflexao-do-dia.css:137`.
- [ ] God components: `admin-escritorios-regionais.ts` (1028 linhas) e
  `minha-conta.ts` (470 linhas) — candidatos a quebra em subcomponentes.
- [ ] Inconsistência de nomenclatura: página pública renomeada
  `enderecos`, mas admin/service continuam
  `escritorios-regionais`/`EscritorioRegionalService`.
- [ ] Handler HTML inline (`onclick`) em `admin-seguranca.html:44`, foge
  do padrão Angular do resto do projeto.
- [ ] `reunioes.ts` carrega Leaflet eager (`import * as L`) mesmo antes
  do usuário escolher reunião presencial — trocar por `import()`
  dinâmico.
- [ ] Cobertura de teste frontend baixa: só 8 `*.spec.ts` para 40
  componentes de página (`admin-seguranca`, `enderecos`, editor CMS sem
  teste nenhum).
- [ ] Documentação de contrato desatualizada: `HtmlSanitizer` (allowlist
  real já inclui `h2`/`h3`/`ul`/`ol`/`li`/`strong`, doc não reflete) e
  `contrato-cms-paginas-institucionais-vinte-perguntas.md` (bug de
  estado vazio já corrigido, doc ainda lista como pendente).
- [ ] Inconsistência de import: uso de nome totalmente qualificado
  inline em vez de import em alguns pontos de `AuthService.java`,
  `ReflexaoDiaService.java`, `VinteQuestaoService.java`,
  `RateLimiterService.java`.
- [ ] Linha em branco decorativa dentro de método em
  `AuthService.java:146` e `JwtAuthenticationFilter.java:42`
  (convenção do projeto proíbe).
- [ ] Índice único `(uf, lower(cidade))` bate com Java, mas UF não tem
  CHECK constraint no banco restringindo aos 27 valores válidos —
  validado só pela aplicação. Sinalizado, sem ação por ora.
- [ ] Inconsistência de rigor: colunas de enum de auditoria
  (`admin_auditoria_log.status_anterior`/`status_novo`/`acao`) sem
  CHECK constraint, diferente de `usuario.role` que tem.

## Roteiro à frente (não são bugs, é o plano combinado em 2026-07-14)

1. Checagem final: front, back, code review, performance (front+back),
   funcionalidades, banco, segurança — considerar "100% funcional"
   depois disso.
2. Usuário fornece os dados reais (incluindo imagens de banners, pdfs)
   iniciais do site — entram já numa migration/seed de **dev**, pra
   testar com o usuário antes de subir pra produção. Independente do
   passo 1, pode acontecer em paralelo assim que o usuário tiver o
   material.
3. Documentar o projeto.
4. Pedido pendente do usuário pro `.claude/knowledge/` — ainda não
   detalhado, ele vai passar depois.
5. Ajuste no agente `professor`.
6. Depois de documentado e arrumado: peça visual ponta a ponta, linha a
   linha, do funcionamento do sistema — pra gravar demo e enviar. 
   (Deve ser explicado todo sistema, suas escolhas, 
   por que se usa cada coisa em cada etapa, exemplo: 
   O Controller pega do banco e usamos esse padrao nas classes por conta disso, 
   principalmente explicar backend e segurança com bastante calma e didatica.)
7. Deploy — bloqueado até fechar "Infraestrutura / Deploy" (Postgres
   produção, provedor de e-mail real, onde guardar secrets, dados do
   admin real, backup de storage), tudo dependente de decisão de
   hospedagem do cliente. Antes de ir: consolidar migrations Flyway numa
   única V1 (confirmado pelo usuário, só nesse momento final, não antes).
   Depois disso: checagem final de performance front/back, code review e
   testes antes de ir.

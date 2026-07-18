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
  `MFA_SECRET_ENCRYPTION_KEY`) — todas já geradas e configuradas em dev
  (`.env` local); falta só o destino final de armazenamento/injeção em
  produção.
- **Dados do primeiro admin real** (apelido/e-mail/senha de bootstrap).
- **Backup do diretório de storage local** — bloqueante pra produção,
  precisa entrar na rotina de backup do servidor junto com o Postgres.
- **`SPRING_PROFILES_ACTIVE=prod` no deploy real** — necessário para
  ativar `ProdConfigValidator` (fail-fast de `RECAPTCHA_SECRET_KEY`
  insegura) e qualquer outra config futura restrita a produção.
- **CORS só aceita 1 origem via string simples** (`app.cors.allowed-
  origins` é `String`, não `List`) — trocar pra suportar múltiplas
  origens (ex: apex + `www.`) assim que o domínio final de produção for
  decidido.
- **CDN (Cloudflare ou equivalente) na frente do domínio real** —
  decisão já tomada pelo `system-design` (ver `Decisoes de Sistemas.md`
  seção 5): origin apontando pro próprio backend/disco local, sem
  bucket S3, sem mudar a decisão de storage já fechada. Entra no
  checklist de deploy assim que hospedagem for decidida — não é questão
  de volume de tráfego, CDN de assets estáticos é barato mesmo em baixo
  tráfego.

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

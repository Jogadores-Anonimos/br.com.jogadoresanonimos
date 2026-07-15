### docker-compose (dev)
- **Onde**: `docker-compose.yml` (raiz), `backend/Dockerfile.dev`, `frontend/Dockerfile.dev`, `.env.example`
- **Propósito**: ambiente de dev local; produção segue pendente (hosting do cliente ainda não confirmado)
- **Como**: `backend/Dockerfile.dev` (eclipse-temurin:21-jdk, copia mvnw/pom.xml antes do src para cache, sem multi-stage); `frontend/Dockerfile.dev` (node:22-alpine, `ng serve --host 0.0.0.0 --poll 2000`, poll necessário por bind mount Windows→Linux não disparar inotify); 3 serviços no compose (postgres com healthcheck, backend com `depends_on condition: service_healthy`, frontend); bucket S3-compatible não entra no compose, hoje simulado por storage local
- **Trade-off**: container único de Postgres, sem réplica/cluster — tráfego baixo (~2.500 req/dia) não justifica

### Fila de e-mail (esqueci-senha)
- **Onde**: `EmailService` (backend)
- **Propósito**: enviar e-mail de recuperação sem broker externo
- **Como**: `@Async` + Spring Retry `@Retryable`, 3 tentativas, backoff exponencial (2s, 6s, 18s); falha definitiva só loga (hash técnico), sem retry manual — usuário rechama endpoint (idempotente)
- **Trade-off**: Kafka/RabbitMQ/SQS seria overengineering para o volume do projeto

### MFA_SECRET_ENCRYPTION_KEY (produção)
- **Onde**: `backend/src/main/resources/application.properties:22` (`app.mfa.secret-encryption-key`), `MfaSecretCipher` (backend)
- **Propósito**: chave de cifra AES-256-GCM do segredo MFA, pendência de PENDENCIAS.md, gerada independente da decisão de hospedagem
- **Como**: gerada via `openssl rand -base64 32` (32 bytes aleatórios, Base64 ~44 chars); entregue ao usuário fora do repo (mensagem direta, guardar em gerenciador de senhas); não commitada em nenhum arquivo
- **Trade-off**: `MfaSecretCipher` só valida null/blank no boot, não tamanho/formato — chave malformada (≠16/24/32 bytes decodificados) só quebra de forma lazy em `cifrar()`/`decifrar()`, não no deploy; onde injetar em produção segue pendente (mesmo bloqueio de `JWT_SECRET`/`LOG_HMAC_SECRET`, depende de hosting do cliente)

### GC de imagem/arquivo órfão
- **Onde**: `ImagemOrfaGcJob`, `ArquivoOrfaGcJob` (backend, `@Scheduled`)
- **Propósito**: remover objeto de storage sem referência em `imagem`/`arquivo`; também cobre imagem de conteúdo removido (bloco CMS, banner do mural) — sem soft-delete de imagem, DELETE do conteúdo pai já deixa a imagem órfã naturalmente
- **Como**: job dentro do próprio processo Spring Boot (sem cron externo/K8s CronJob), roda 1x/dia em horário de baixo tráfego; remove imediatamente se insert falhou, remove após 7 dias de graça se sem bloco/banner associado; checa `CmsBlocoRepository.existsByImagemId` e `BannerMuralRepository.existsByImagemId`; log só conta remoções, nunca nome de arquivo
- **Trade-off**: se backend escalar para múltiplas réplicas, precisa lock distribuído ou mover job para fora do processo; bug corrigido em 2026-07: job só checava `CmsBlocoRepository`, não `BannerMuralRepository` — banner ativo podia ser coletado erroneamente após 7 dias; lacuna de teste fechada por `qa-backend-unit`: `ImagemOrfaGcJobTest` (6 casos, inclui regressão do bug de `BannerMuralRepository`), build 6/6 verde

### Limite de upload de imagem CMS
- **Onde**: proxy + `application.properties`
- **Propósito**: limitar tamanho de upload de imagem no CMS
- **Como**: 5MB/arquivo via `client_max_body_size` (proxy) + `max-file-size=5MB`/`max-request-size=6MB` (Spring); sem quota individual por admin (rate limit de contagem já cobre)

### Limite de upload de PDF (literatura CMS)
- **Onde**: proxy + `application.properties`
- **Propósito**: limitar tamanho de upload de PDF de literatura, maior que o de imagem
- **Como**: 20MB/arquivo (`client_max_body_size 20m` só na rota de PDF, `max-file-size=20MB`/`max-request-size=21MB`); mesmo bucket/quota já usado para imagem, mesmo rate limit (10/min por admin, 60/min global) e GC de órfão reaproveitados
- **Trade-off**: PDF pode carregar metadata equivalente a EXIF no dicionário `/Info`/XMP — strip pendente, não bloqueante para primeira versão

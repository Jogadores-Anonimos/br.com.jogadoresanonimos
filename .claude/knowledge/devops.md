# Devops

## [2026-07-12] docker-compose dev criado (prod pendente)
- Ideia: docker-compose.yml de dev local criado, resolve item 1 de "Pendencias Infra Backend.md". Prod segue pendente (cliente tem hosting próprio, provedor ainda não confirmado)
- backend/Dockerfile.dev: eclipse-temurin:21-jdk, copia mvnw/.mvn/pom.xml antes do src (cache de dependência), expõe 8080, roda `./mvnw spring-boot:run`. Sem multi-stage agora (fica reservado pra imagem de produção: JDK só no build, JRE enxuto final, usuário não-root)
- frontend/Dockerfile.dev: node:22-alpine, npm ci, roda `ng serve --host 0.0.0.0 --poll 2000` — `--host 0.0.0.0` pq dev server só escuta localhost por padrão (inacessível de fora do container); `--poll 2000` pq bind mount Windows→Linux às vezes não dispara inotify, sem polling hot reload não funciona
- compose raiz, 3 serviços:
  - postgres: postgres:16-alpine, volume nomeado postgres_data, healthcheck pg_isready, env via .env
  - backend: depends_on postgres com `condition: service_healthy` (evita conectar em banco subindo), todas env vars do application.properties mapeadas, volume ./backend/src pra hot reload, volume nomeado backend_storage no path do STORAGE_STUB_DIR (imagens de teste do StubStorageClient isoladas do bind mount de código)
  - frontend: depende do backend, volumes de src + config Angular (angular.json, tsconfig*.json) pra hot reload sem rebuild
- Bucket S3-compatible (R2/B2) não entra no compose — serviço externo gerenciado, hoje simulado por StubStorageClient via STORAGE_STUB_DIR
- .env.example criado na raiz com todas as vars, placeholder
- .gitignore da raiz tinha entrada quebrada (`.backend/.env`, path errado) — corrigido pra `.env` e `backend/.env` separados
- Motivo: tráfego baixo (~300 pedidos/dia, ~2.500 req/dia) — container único de Postgres basta, sem réplica/cluster. Foco do compose é só velocidade de iteração dev
- Onde: docker-compose.yml (raiz), backend/Dockerfile.dev, frontend/Dockerfile.dev, .env.example (raiz), .gitignore (raiz)
- Pendência aberta: produção — imagem multi-stage, gerenciador de secrets, usuário não-root, provedor de hosting, tudo aguardando cliente decidir

## [2026-07-12] Limite de upload de imagem CMS
- Ideia: 5MB/arquivo — proxy `client_max_body_size` + Spring `max-file-size=5MB`/`max-request-size=6MB`
- Quota bucket: sem limite rígido além do free tier do provedor (R2/B2, ~10GB), alerta de billing em 80% configurado no console do provedor (não em código)
- Sem quota individual por admin (rate limit de contagem já cobre abuso de velocidade)
- Pendente: escolha final R2 vs B2 ainda em aberto
- Onde: `backend/docs/api/contrato-auth-cms-upload.md`

## [2026-07-12] Fila de e-mail (esqueci-senha)
- Ideia: sem broker externo (Kafka/RabbitMQ/SQS = overengineering pro volume do projeto). Usa `@Async` + Spring Retry `@Retryable`, 3 tentativas, backoff exponencial (2s, 6s, 18s)
- Falha definitiva após 3 tentativas: só loga (hash técnico, nunca e-mail em claro), sem retry manual — usuário rechama `/esqueci-senha` (idempotente, gera novo token/envio)
- Alerta operacional se taxa de falha definitiva > 5%/hora
- Onde: `backend/docs/api/contrato-auth-cms-upload.md`

## [2026-07-12] Limite de upload de PDF (literatura CMS)
- Contexto: feature nova de upload de PDF para bloco de literatura no CMS. Ux-ui propôs 20MB por arquivo (vs 5MB já decidido para imagem) — confirmado sem ajuste, porque PDF de texto institucional (mesmo com página escaneada) legitimamente passa dos 5MB usados para imagem, e 20MB ainda é um teto baixo o suficiente para não abrir brecha de abuso.
- Config em 3 camadas, mesmo padrão já usado para imagem: proxy (`client_max_body_size 20m`, aplicado só na rota de upload de PDF, não no limite global do proxy), Spring (`max-file-size=20MB`, `max-request-size=21MB`), e validação de tipo real por magic bytes (assinatura `%PDF-` no início do arquivo) — nunca confiar em extensão ou Content-Type enviado pelo cliente, mesma lógica do `ImagemProcessor` que já existe para imagem.
- Diferença de imagem: PDF não tem EXIF, mas pode carregar metadata equivalente no dicionário `/Info` ou em XMP (autor, software gerador, caminho de arquivo original) — se o PDF vier de um documento de voluntário, isso pode vazar nome/dado pessoal nos metadados do arquivo. Recomendação: strip do `/Info` dict no backend antes de salvar, mesmo espírito do "nunca confiar no que chega" que já vale para imagem. Não é bloqueante para a primeira versão da feature, mas deve entrar como melhoria de segurança em algum momento — alinhar com `security` quando for priorizado.
- Quota: decisão foi não criar bucket nem política de quota separada por tipo de arquivo. PDF cai no mesmo bucket e mesmo free tier já decidido para imagem (R2 ou B2, ~10GB), com o mesmo alerta de billing em 80% configurado no console do provedor (não em código). Motivo: volume de upload de literatura é baixo (só admin faz upload, não usuário final) e 20MB por arquivo não estressa esse free tier.
- Rate limit e GC de órfão: reusa a mesma infraestrutura já existente para imagem (`RateLimiterService` com limite 10/min por admin + 60/min global; job `@Scheduled` de GC que remove arquivo sem bloco associado após 7 dias de graça) — sem duplicar contador ou job por tipo de arquivo.
- Onde: `backend/docs/api/contrato-auth-cms-upload.md` (a atualizar pelo agente `backend` com a seção de PDF).

## [2026-07-12] GC de imagem órfã no bucket
- Ideia: job `@Scheduled` dentro do próprio backend Spring Boot (sem cron externo/K8s CronJob), 1x/dia em horário de baixo tráfego
- Remove objeto sem linha em `imagem` (insert falhou) imediatamente
- Remove imagem sem `cms_bloco` associado após 7 dias de graça (tempo do admin montar a página)
- Log só conta remoções, nunca nome de arquivo
- Ressalva: se backend escalar para múltiplas réplicas, precisa lock distribuído ou mover job pra fora do processo — não é o caso hoje
- Onde: `backend/docs/api/contrato-auth-cms-upload.md`

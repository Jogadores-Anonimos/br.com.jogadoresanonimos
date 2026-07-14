# Backend — implementação inicial (Auth/E-mail, CMS Leve, Upload de Imagem)

**Data**: 2026-07-12
**Agente**: backend

## O que foi feito

Implementação real do backend Spring Boot em cima do contrato já fechado
em `backend/docs/api/contrato-auth-cms-upload.md` (blocos 1, 2 e 3), que
antes era só especificação (SDD), sem nenhum código de produto — o
`backend/` só tinha o esqueleto do Spring Initializr.

### Estrutura de pacotes proposta e usada
Como não havia nenhum padrão de pacotes ainda, foi adotado convencional
por domínio (não por camada técnica pura):
```
br.com.jogadoresanonimos
├── common/          → ApiResponse, ApiError, exceções de negócio,
│                       GlobalExceptionHandler, RateLimiterService (token
│                       bucket em memória), HmacHasher
├── config/          → SecurityConfig (JWT, CORS, autorização por role)
├── security/        → JwtService, JwtAuthenticationFilter, AuthenticatedUser
├── usuario/          → entidade Usuario, Role, UsuarioRepository,
│                       UsuarioController (/me), AdminBootstrap
├── auth/             → AuthController/AuthService (registro, login,
│                       esqueci-senha, redefinir-senha), PasswordResetToken,
│                       EmailService
├── cms/              → CmsPagina/CmsBloco/CmsAlteracaoLog, HtmlSanitizer,
│                       CmsService, CmsPublicController, CmsAdminController
└── imagem/           → Imagem, ImagemProcessor (magic bytes + EXIF strip),
                        StorageClient (interface) + StubStorageClient,
                        ImagemService/Controller, ImagemOrfaGcJob (GC diário)
```

### Bloco 1 — Auth/E-mail
- Endpoints implementados: `POST /api/v1/auth/registro`, `/login`,
  `/esqueci-senha`, `/redefinir-senha`, `GET /api/v1/usuarios/me`.
- Senha com Argon2id (`Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()`
  — nome do método mudou nessa versão do Spring Security 7, não é mais
  `defaultsForSpringSecurity()` sem sufixo).
- JWT com claim `senhaAlteradaEm` (epoch millis) — ao trocar senha, o
  filtro (`JwtAuthenticationFilter`) invalida qualquer token emitido antes
  da troca, sem precisar de blacklist/tabela de revogação.
- Rate limit: `RateLimiterService` com token bucket em memória
  (`ConcurrentHashMap` de buckets por chave lógica), chave = ação +
  HMAC do identificador normalizado (nunca texto puro). Limites hoje são
  placeholder (5 tentativas/min) — **ainda não fechados com o agente
  security**, só a estrutura está pronta pra receber o valor certo.
- Anti-enumeration: mensagens de erro genéricas em login (401 sempre
  igual), registro (409 nunca diz qual campo colidiu), esqueci-senha
  (sempre 200 com mensagem genérica), redefinir-senha (401 genérico pra
  token inválido/expirado/usado).
- Fila de e-mail: sem broker externo, `@Async` (`EmailService`) com retry
  manual em memória (3 tentativas, backoff exponencial 2s→6s→18s) —
  decisão de infra do devops (D2), implementada exatamente como
  especificado. **Hoje só loga a tentativa de envio, não integra SMTP/API
  real** — é um TODO explícito no código, precisa de credencial externa.
- Migrations: `V1` cria `usuario` já com email `NOT NULL UNIQUE` direto
  (sem expand/contract) porque o projeto ainda está em fase de protótipo,
  sem usuário real em produção — decisão já autorizada no próprio
  contrato para esse cenário específico. `V2` cria
  `password_reset_token` (hash do token, nunca o token bruto).

### Bloco 2 — CMS Leve
- Endpoints públicos (`GET /api/v1/cms/paginas/{slug}`) e admin
  (`GET`/`PUT`/`POST /api/v1/admin/cms/paginas...`), protegidos por
  `@PreAuthorize("hasRole('ADMIN')")`.
- `HtmlSanitizer`: allowlist manual via regex (`<b> <i> <a href> <p> <br>`),
  rejeita qualquer outra tag e valida que `href` só aceita `http(s)://` ou
  caminho relativo (bloqueia `javascript:`). **Limitação documentada no
  próprio código**: é regex, não um parser HTML de verdade — se o risco/
  volume crescer, trocar por lib dedicada (ex. OWASP Java HTML Sanitizer)
  é decisão de dependência nova, precisa aprovação.
- Auditoria: toda `PUT` de página grava snapshot anterior (JSON serializado
  via Jackson) em `cms_alteracao_log` antes de aplicar a mudança, na mesma
  transação.
- Índice único composto `(pagina_id, ordem)` em `cms_bloco` conforme
  contrato — cobre unicidade e a query de listagem ordenada.

### Bloco 3 — Upload de Imagem
- `ImagemProcessor`: detecção de tipo real por magic bytes (JPEG/PNG/WebP,
  nunca por extensão/Content-Type do cliente) + remoção de EXIF via
  reencode com `ImageIO` (reencodar descarta metadata original sem
  precisar de lib externa de manipulação de EXIF).
- **Limitação conhecida e documentada no código**: `ImageIO` puro do JDK
  não lê WebP nativamente sem plugin adicional — hoje o magic-byte check
  aceita WebP mas o processamento vai falhar com `UnsupportedMediaException`.
  Precisa decisão do time antes de liberar WebP de verdade: adicionar lib
  (dependência nova, pede aprovação) ou restringir a jpeg/png por ora.
- `StorageClient` é uma interface; a implementação hoje (`StubStorageClient`)
  grava em disco local — **não é apta a produção**, existe só para o
  fluxo funcionar sem credencial de bucket real. Trocar por client R2/B2
  real é só implementar a interface de novo, sem tocar no resto do fluxo.
- GC diário de imagem órfã (`ImagemOrfaGcJob`, `@Scheduled` 4h BRT):
  cobre o caso "upload nunca associado a um bloco após 7 dias de graça".
  **Não cobre ainda** o caso "objeto existe no bucket mas não tem linha em
  `imagem`" — isso depende de listar objetos do bucket real (API do
  provedor), que só existe quando o client real substituir o stub.
- Rate limit de upload: 10/min por admin + 60/min global, mesma
  `RateLimiterService` do bloco 1.

### Tratamento de erro e observabilidade
- `GlobalExceptionHandler` único (`@RestControllerAdvice`), mapeando cada
  exceção de negócio pro `error.code` da taxonomia da seção 9 do doc de
  decisões, sempre no envelope `{success, data, error}`. Nunca vaza stack
  trace/nome de exception/SQL — página de erro padrão do Spring desativada
  via `server.error.include-*=never`.
- Logs sempre com `traceId` (UUID por request), nunca dado pessoal —
  tentativa de login/reset falha só loga hash HMAC do identificador
  (`HmacHasher`), nunca apelido/e-mail em texto puro.
- Actuator restrito só a `/actuator/health`, sem detalhe (`show-details=never`).

## Trade-offs/decisões que vale saber
1. **Rate limit em memória, não Redis** — proporcional ao volume do
   projeto (~2.500 req/dia). Se escalar horizontalmente com múltiplas
   réplicas, precisa migrar pra store compartilhado (decisão futura com
   devops/system-design, já sinalizada no código).
2. **Sanitizer HTML por regex, não parser** — decisão consciente pra não
   adicionar dependência nova sem perguntar; documentado como ponto de
   revisão futura.
3. **StorageClient com stub em disco** — o pipeline de segurança (EXIF
   strip, magic bytes, chave UUID não previsível) já está completo e
   correto; só falta a implementação real do client apontando pro bucket
   verdadeiro quando houver credencial.
4. **EmailService só loga, não envia de verdade** — mesma lógica: fluxo/
   retry/idempotência já implementados, falta só a integração SMTP/API
   quando houver credencial.
5. **Migration de email colapsada (sem expand/contract)** — só porque o
   contrato já autorizou isso explicitamente para fase de protótipo sem
   usuário real em produção; se isso mudar, próxima migration de schema
   já não pode mais fazer esse atalho.

## Testes escritos
JUnit + Mockito, sem subir contexto Spring (rápidos, isolados):
- `AuthServiceTest`: 11 casos (registro com senha divergente, conflito
  genérico de apelido/e-mail, login com mensagem genérica em usuário
  inexistente/senha errada, emissão de token, esqueci-senha idempotente
  mesmo sem usuário, redefinir-senha com token inválido/válido).
- `HtmlSanitizerTest`: 6 casos (allowlist ok, XSS via `<script>`,
  `javascript:` em href, `<a>` sem href, `<img onerror>`).
- `CmsServiceTest`: 6 casos (404 de slug, conflito de slug duplicado,
  ordem de bloco duplicada, `imagemId` inexistente, sanitização aplicada
  antes de persistir, criação bem-sucedida).
Todos os 23 testes novos passam (`mvn test -Dtest=...`, offline, sem
banco real — tudo mockado).

## Onde olhar no projeto
- Contrato: `backend/docs/api/contrato-auth-cms-upload.md` (atualizado
  com nota de "implementado" e pendências externas).
- Código: `backend/src/main/java/br/com/jogadoresanonimos/{common,config,security,usuario,auth,cms,imagem}`.
- Migrations: `backend/src/main/resources/db/migration/V1` a `V4`.
- Config: `backend/src/main/resources/application.properties` (tudo via
  variável de ambiente, nenhum segredo hardcoded).
- Testes: `backend/src/test/java/br/com/jogadoresanonimos/{auth,cms}`.

## Pendências (não implementadas ainda, fora do escopo desta etapa)
- MFA/TOTP para admin (planejado para depois do CRUD básico, conforme
  seção 4 do doc de decisões).
- Limites reais de rate limit (hoje é placeholder) — falta fechar com
  security.
- Implementação real de `StorageClient` (R2 ou B2) — falta credencial.
- Integração real de `EmailService` (SMTP/API) — falta credencial/serviço.
- Suporte real a upload de WebP (limitação de `ImageIO` documentada no
  código).
- `docker-compose.yml` ainda não existe no projeto — Postgres de
  dev/prod ainda não provisionado.

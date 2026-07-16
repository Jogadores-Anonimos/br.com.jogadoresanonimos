### Chave HMAC de correlação de log (login/reset)
- **Onde**: variável de ambiente `HMAC_LOG_CORRELATION_KEY` (32+ bytes), separada da chave de assinatura JWT
- **Propósito**: logar tentativa de login/reset falha sem expor apelido/e-mail em texto puro
- **Como**: HMAC-SHA256 sobre identificador normalizado (lowercase/trim), truncado a 16 bytes hex antes de logar; rotação manual a cada 6-12 meses ou em suspeita de vazamento
- **Trade-off**: sem dual-key simultâneo — girar a chave só torna logs antigos incorrelacionáveis com novos, aceitável dado volume baixo e retenção curta

### Depoimentos/Relatos: dados permitidos no formulário
- **Onde**: contrato de `relato`/depoimentos (frontend + backend)
- **Propósito**: coletar relato de recuperação preservando anonimato
- **Como**: só texto (50-4000 chars) + "nome de exibição" opcional (até 30 chars, nunca validado contra nome real); nunca pedir e-mail, nome completo, telefone, CPF/CNPJ, endereço, geolocalização, upload de imagem/anexo

### Depoimentos/Relatos: fluxo de moderação sem identificar remetente
- **Onde**: módulo `relato` (backend), painel admin
- **Propósito**: moderação editorial sem virar ferramenta de reidentificação
- **Como**: status `pending`→`approved`/`rejected`; só `approved` aparece na listagem pública; se vinculado a usuário autenticado (FK interna para permitir auto-edição), o vínculo nunca é exposto ao moderador nem em resposta de API pública

### Depoimentos/Relatos: rate limit por hash de sessão, nunca IP
- **Onde**: `relato` (backend), política geral de rate limit
- **Propósito**: anti-abuso sem violar requisito de anonimato
- **Como**: hash HMAC-SHA256 não-reversível de token de sessão opaco, armazenado separado do conteúdo, TTL curto (~24h); nunca IP como chave
- **Trade-off**: IP correlacionaria abuso entre sessões/dispositivos melhor, mas viola anonimato — mitigado pelo baixo volume esperado

### Auditoria de ação administrativa (tabela genérica, sem 2ª confirmação)
- **Onde**: `RelatoAdminController` (aprovar/rejeitar/reverter/remover/editar), decisão de `security` documentada em `Decisoes de Sistemas.md` seções 2 e 10
- **Propósito**: fechar pendência de segurança — ações admin sobre relato não tinham trilha de quem/quando/status alterado
- **Como**: tabela genérica `admin_auditoria_log` (não campo inline na entidade), reutilizável para outras entidades administráveis futuras; nunca guarda texto do relato/autor original, só `admin_id`, `entidade`, `entidade_id`, `acao`, `status_anterior`/`status_novo`
- **Trade-off**: sem segunda confirmação em ação destrutiva (remover) — overengineering para o volume do projeto (~300 pedidos/dia); log de auditoria com snapshot já cobre rastreabilidade pós-fato

### E-mail no cadastro: sem tabela separada
- **Onde**: tabela `usuario`, coluna `email` (`VARCHAR NOT NULL UNIQUE`)
- **Propósito**: exceção deliberada à regra de anonimato — e-mail serve para recuperação de conta e envio de itens de loja
- **Como**: isolamento garantido por regra de aplicação (DTOs/projeções de relato/presença/reunião nunca incluem `email`, nunca a entidade `Usuario` completa; `email` só aparece em `/usuarios/me` e no service de disparo de e-mail); e-mail entra na lista de campos banidos de log
- **Trade-off**: sem criptografia adicional em repouso no MVP (precisa ser recuperável, não é senha); tabela separada só se houver necessidade real futura (ex: acesso de serviço terceiro sem dar acesso ao resto de `usuario`)

### E-mail: login por apelido OU e-mail
- **Onde**: fluxo de login (backend)
- **Propósito**: aceitar apelido ou e-mail como identificador, mesma senha
- **Como**: `SELECT ... FROM usuario WHERE apelido = :id OR email = :id`; requer índice único em `email` (já coberto por `UNIQUE`); rate limit precisa cobrir os dois identificadores contra o mesmo bucket lógico da conta-alvo (mais um bucket por identificador bruto normalizado, para enumeration); mensagem de erro genérica não revela qual campo errou nem se a conta existe

### E-mail: chave de recuperação abandonada em favor de e-mail
- **Onde**: fluxo de "esqueci minha senha"
- **Propósito**: mecanismo antigo de "chave exibida uma vez" descartado — os dois não convivem
- **Como**: apelido ou e-mail → token de reset curto (~30min) → só hash do token é armazenado → link por e-mail → resposta sempre genérica ("se o e-mail existir, enviamos um link")
- **Trade-off**: chave de recuperação preserva anonimato total mas tem alta taxa de perda no público do projeto (dispositivo compartilhado/navegação privada); e-mail já seria o fallback real mesmo mantendo os dois mecanismos

### HMAC de log — cobertura estendida a e-mail
- **Onde**: mesmo mecanismo de hash de apelido em log de tentativa de login falha
- **Propósito**: nunca logar e-mail em texto puro, nem em log de erro/auditoria de rate limit
- **Como**: mesma HMAC com chave de aplicação, truncada, só para correlação técnica — nunca para reidentificar

### HtmlSanitizer — OWASP Java HTML Sanitizer (migração de regex)
- **Onde**: `cms/util/HtmlSanitizer.java`
- **Propósito**: eliminar limitação estrutural do sanitizer regex-based (edge cases de aninhamento malformado), risco de XSS armazenado no CMS
- **Como**: `com.googlecode.owasp-java-html-sanitizer`, versão real `20240325.1`; usa overload `policy.sanitize(html, listener, context)` (não o `sanitize(html)` simples) para poder lançar `ValidationException` granular via `HtmlChangeListener`; `allowUrlProtocols("http", "https")` combinado com `AttributePolicy` customizado no `href` (regex `^(https?://|/)[^"'<>\s]*$`) — a policy nativa sozinha é mais permissiva do que o contrato exige
- **Trade-off**: escolhido sobre JSoup por ter `HtmlChangeListener` (mensagem granular de rejeição); JSoup só tem `isValid()` booleano sem dizer qual tag/atributo foi rejeitado

### HtmlSanitizer — bug: href protocol-relative não rejeitado
- **Onde**: `cms/util/HtmlSanitizer.java`, regex `SAFE_HREF`
- **Propósito**: registro de bug encontrado em QA
- **Como**: `href="//evil.com"` não é rejeitado — a regex trata qualquer valor começando com `/` como caminho relativo seguro, incluindo `//evil.com`
- **Trade-off**: necessário checagem extra rejeitando valores que começam com `//` antes de aceitar por começar com `/`

### HtmlSanitizer — mitigação regex-based (versão anterior à migração OWASP)
- **Onde**: `cms/util/HtmlSanitizer.java` (histórico, superado pela migração OWASP)
- **Propósito**: escapar texto puro fora de allowlist sem adicionar dependência nova
- **Como**: método `escaparTextoPuro` escapa `< > & " '` fora de tag reconhecida por `TAG_PATTERN`; guarda regex `JA_ESCAPADO` evita duplo-escaping

### Interceptor HTTP de Authorization — JWT só em memória
- **Onde**: `frontend/src/app/services/auth.ts` (campo `tokenAtual`), `frontend/src/app/interceptors/auth.interceptor.ts`
- **Propósito**: reduzir superfície de XSS não deixando token acessível via `localStorage`/`sessionStorage`
- **Como**: token guardado em campo privado (não signal, não storage), exposto via `obterToken()`; `HttpInterceptorFn` funcional anexa `Authorization: Bearer` só em requests para `environment.apiBaseUrl`; erro 401 chama `Auth.logout()` e deixa o erro seguir, sem decidir navegação
- **Trade-off**: sessão não sobrevive a F5, em troca de reduzir superfície de XSS; alternativa mais robusta (httpOnly cookie + refresh token) não adotada por exigir mudança de contrato no backend

### JWT denylist (logout/revogação)
- **Onde**: migration `V6` (tabela `jwt_denylist`), `JwtService.gerarToken`/`jtiDe`, `JwtAuthenticationFilter`, `JwtDenylistGcJob`, `POST /api/v1/auth/logout`
- **Propósito**: revogar token antes da expiração natural (logout real), sem depender de refresh curto
- **Como**: `jti` emitido em todo token, checado contra a denylist no filtro antes de autenticar; sem Redis (volume não justifica); GC diário remove entradas expiradas, mesmo padrão dos outros GC jobs
- **Trade-off**: Postgres em vez de store em memória compartilhado — troca aceita pelo volume baixo do projeto

### MFA `desativar` exige TOTP + auditoria em ações de MFA
- **Onde**: `MfaService` (`enroll`/`confirmar`/`desativar`)
- **Propósito**: defesa em profundidade — senha sozinha não bastava para desativar MFA
- **Como**: `desativar` agora exige `codigoTotp` válido (checado via `TotpService`) além da senha; as três ações passam a registrar em `AdminAuditoriaService` (ação `EDITAR`, entidade `"mfa"`, entidadeId = próprio usuário, nunca loga o secret)

### ProdConfigValidator (fail-fast de configuração em produção)
- **Onde**: `config/ProdConfigValidator` (`@Profile("prod")`, `ApplicationRunner`)
- **Propósito**: impedir boot em produção com segredo de configuração inválido/default
- **Como**: derruba o boot se `RECAPTCHA_SECRET_KEY` estiver vazia ou igual à chave de teste pública do Google; só ativa com `SPRING_PROFILES_ACTIVE=prod` (ainda não configurado no deploy real, pendente com `devops`)

### Rate limit em ações admin de escrita em massa
- **Onde**: `RelatoService` (editar/aprovar/rejeitar/reverter/remover), `UsuarioService.editarRole`
- **Propósito**: cobrir lacuna — ações administrativas em massa não tinham rate limit
- **Como**: 60/min por `adminId`, reaproveitando `RateLimiterService`/`TokenBucket` já existente

### Rate limit de upload de imagem (bucket duplo)
- **Onde**: `POST /api/v1/admin/imagens`
- **Propósito**: limitar abuso de upload por admin e globalmente
- **Como**: 10 uploads/min por admin (burst 15) + 60/min global (burst 80); nenhum bucket usa IP como chave (consistência com anonimato); 429 com header `Retry-After`, sem revelar qual bucket estourou

### reCAPTCHA v2 checkbox — `remoteip` nunca enviado ao Google
- **Onde**: `auth/service/RecaptchaService.java`, `RecaptchaServiceTest.validar_nuncaEnviaRemoteip`
- **Propósito**: parâmetro opcional `remoteip` do `siteverify` vazaria IP do usuário atrelado a evento de auth (login/registro) para terceiro (Google) — viola anonimato
- **Como**: request a `https://www.google.com/recaptcha/api/siteverify` só com `secret` + `response` (token), nunca `remoteip`; decisão intencional coberta por teste dedicado

### TokenBucket — bug CRÍTICO: rate limit nunca bloqueava (nenhum endpoint protegido)
- **Onde**: `common/ratelimit/TokenBucket.java`, método `tryConsume()`
- **Severidade**: alta — afetava TODO uso de `RateLimiterService.check` (login, MFA enroll/confirmar/desativar, registro, esqueci-senha, upload)
- **Causa**: no ramo "sem tokens" (`refilled.tokens <= 0`), código fazia `return state.compareAndSet(current, refilled) || tryConsume()` — quando o CAS tinha sucesso, retornava `true` (permitindo consumo) em vez de negar; só negava se caísse na recursão por falha de CAS, que na prática quase nunca ocorre em teste single-thread
- **Fix**: ramo "sem tokens" agora sempre retorna `false`; só faz `continue`/nova tentativa se precisar reler estado após CAS falho, nunca retorna `true` nesse caminho
- **Detectado por**: `qa-backend-unit`, teste `MfaServiceTest.enroll_deveFalharComRateLimitAposExcederTentativas`, ao reproduzir isolado (capacity=5, 6+ chamadas todas `true`)
- **Efeito colateral encontrado**: teste `desativar_deveFalharComRateLimitAposExcederTentativas` também tinha bug próprio — usava senha errada desde a 1ª iteração, então `UnauthenticatedException` (checada depois do rate limit no service) nunca dava chance do bucket estourar antes de lançar; corrigido para usar senha correta e resetar MFA a cada iteração, isolando só o rate limit
- **Trade-off**: nenhum — bug puro de lógica, sem trade-off de design envolvido

### UsuarioAdminResponse.fromMascarado() — mascaramento de e-mail em listagem admin
- **Onde**: `usuario/dto/UsuarioAdminResponse.java`, listagem admin de usuários
- **Propósito**: reduzir exposição de e-mail em massa numa tela de listagem
- **Como**: `fromMascarado()` formata `f***@dominio.com`; ação pontual de editar role mantém e-mail completo — decisão de produto confirmada com o usuário (mascarar só em listagem em massa, não em ação individual)

### LocalDiskStorageClient — warn em SO sem suporte POSIX
- **Onde**: `LocalDiskStorageClient.aplicarPermissoesRestritivasSePosix`
- **Propósito**: SO sem POSIX (ex: Windows) fazia a checagem de permissão falhar silenciosamente
- **Como**: agora loga `warn` quando o SO não suporta `PosixFileAttributeView`, em vez de engolir a falha sem sinal

### Sanitizer HTML — decisão original (regex, sem dependência nova)
- **Onde**: `cms/util/HtmlSanitizer.java` (histórico, superado pela migração OWASP)
- **Propósito**: sanitizar HTML do CMS sem adicionar dependência sem aprovação do usuário
- **Como**: allowlist manual via regex (`<b> <i> <a href> <p> <br>`), valida `href` só `http(s)://` ou caminho relativo

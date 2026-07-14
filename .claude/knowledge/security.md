## [2026-07-12] Fix: WARN de senha de segurança gerada (UserDetailsServiceAutoConfiguration) no startup

- **Sintoma**: todo `docker compose up --build` do backend logava
  `WARN UserDetailsServiceAutoConfiguration: Using generated security
  password: <uuid>` + criação de um `InMemoryUserDetailsManager` global.
- **Diagnóstico**: autoconfig padrão do Spring Boot dispara sempre que
  Spring Security está no classpath e não encontra nenhum bean
  `UserDetailsService`/`AuthenticationManager`/`AuthenticationProvider`
  no contexto. Esse projeto usa autenticação JWT 100% custom
  (`AuthService.login()` valida senha via `PasswordEncoder.matches()`
  diretamente; `JwtAuthenticationFilter` popula o `SecurityContextHolder`
  manualmente) — nunca usa esses beans, então nenhum deles existia.
- **Era problema real?** Funcionalmente inofensivo *no estado atual*: o
  `SecurityFilterChain` (`backend/src/main/java/br/com/jogadoresanonimos/config/SecurityConfig.java`)
  não configura `.httpBasic()`/`.formLogin()`, então nada no sistema
  consulta esse `UserDetailsService` fantasma — bean morto, inalcançável.
  Mas era um risco de regressão silenciosa: se algum dia alguém
  adicionasse `.httpBasic()` (ex: debug local, endpoint interno) sem
  perceber, o app passaria a aceitar login HTTP Basic com usuário `user`
  e senha aleatória só disponível no log — autenticação paralela não
  intencional, fora do modelo de autorização por `Role` do resto do
  sistema. Também é ruído que atrapalha notar warnings reais no log.
- **Fix**: excluído `UserDetailsServiceAutoConfiguration` explicitamente
  via `@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)`
  em vez de criar um `UserDetailsService` no-op — evita introduzir um
  bean sem propósito real que poderia ser acidentalmente conectado a um
  `AuthenticationProvider` no futuro. A exclusão documenta a intenção
  ("nunca vamos usar esse mecanismo") de forma mais forte que um bean
  vazio faria.
- **Armadilha de versão (Spring Boot 4.1.0)**: o pacote da classe mudou
  na reestruturação modular do Spring Boot 4 — não é mais
  `org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration`
  (Spring Boot 3.x), e sim
  `org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration`,
  vivendo no módulo `spring-boot-security` (não mais dentro de
  `spring-boot-autoconfigure`). Import errado falha silenciosamente como
  erro de compilação (`package does not exist`), não como erro do
  Spring — checar isso primeiro se outro exclude de autoconfig for
  necessário no futuro nesse projeto.
- **Validação**: build completo (`docker compose up -d --build postgres
  backend`) confirmou que o WARN e o `InMemoryUserDetailsManager` somem
  do log, e a aplicação sobe normalmente (`Started
  JogadoresAnonimosApplication in 7.546 seconds`). Também rodei
  `./mvnw test -Dtest=JogadoresAnonimosApplicationTests` — falha
  remanescente é só a ausência de Postgres local fora do Docker (SCRAM
  auth sem senha), baseline conhecido e documentado em entradas
  anteriores deste arquivo, não relacionado a essa mudança.
- **Onde no projeto**: `backend/src/main/java/br/com/jogadoresanonimos/JogadoresAnonimosApplication.java`
  (exclude do autoconfig + comentário curto explicando o motivo).
- Por: security

## [2026-07-12] Validação final: HtmlSanitizer (OWASP) em produção sem regressão
- Ideia: `./mvnw test` rodado em `backend/` confirma migração regex→OWASP HTML Sanitizer
  estável — 50 testes, 0 falhas, 1 erro
- Erro é `JogadoresAnonimosApplicationTests.contextLoads` (falta Postgres local rodando,
  SCRAM auth sem senha) — baseline conhecido, não regressão, não relacionado ao sanitizer
- Confirma: bug do href protocol-relative (`//evil.com`, ver entrada QA acima) já foi
  corrigido pelo backend antes desta rodada — `HtmlSanitizerTest` completo passando
- Contrato público mantido: mesma allowlist (`b`, `i`, `a[href]`, `p`, `br`), método
  `sanitizar(String)` null-safe, lança `ValidationException` em entrada inválida
- Por: aprendiz (validação de fechamento, sem nova execução de teste — sessão anterior já validou)

## [2026-07-12] Mitigação de XSS armazenado no HtmlSanitizer do CMS (sem dependência nova)
- **Contexto**: `code-reviewer` apontou (severidade MEDIUM) que o
  `HtmlSanitizer` (`backend/src/main/java/br/com/jogadoresanonimos/cms/util/HtmlSanitizer.java`)
  era regex-based e não escapava texto puro fora de tags permitidas —
  risco de XSS armazenado caso uma conta admin fosse comprometida (o
  conteúdo do CMS é escrito por admin e exibido depois para outros
  usuários).
- **Decisão**: não adicionar dependência nova (ex: OWASP Java HTML
  Sanitizer, JSoup) sem aprovação do usuário — regra do projeto exige
  perguntar antes de dependência nova, e usuário não estava disponível
  nesta tarefa. Em vez disso, aplicada mitigação só com o que já existia
  na classe.
- **O que foi feito**: novo método privado `escaparTextoPuro` no próprio
  `HtmlSanitizer`, que escapa `< > & " '` em qualquer trecho de texto que
  fique fora de uma tag reconhecida pelo `TAG_PATTERN` (inclui o caso de
  um `<` solto que não chega a formar tag válida). Guarda via regex
  `JA_ESCAPADO` evita duplo-escaping de entidades que o usuário já tenha
  digitado corretamente (ex: `&amp;`, `&lt;`).
- **Limitação residual** (documentada em comentário no topo da própria
  classe): abordagem regex-based ainda não é um parser HTML de verdade,
  existem edge cases de aninhamento malformado. Recomendação registrada
  no código: migrar para lib madura (OWASP Java HTML Sanitizer) se
  volume/risco de conteúdo do CMS crescer — depende de aprovação do
  usuário para nova dependência, ainda pendente.
- **Testes**: `backend/src/test/java/br/com/jogadoresanonimos/cms/util/HtmlSanitizerTest.java`
  ganhou casos cobrindo texto puro com `<script>` fora de tag reconhecida,
  entidades soltas (`&`, aspas simples) e entidades já escapadas
  (garantindo ausência de duplo-escaping).
- Por: code-reviewer (apontamento) + backend (mitigação e testes)

## [2026-07-12] Decisão: migrar HtmlSanitizer de regex para OWASP Java HTML Sanitizer

- **Contexto**: usuário aprovou dependência nova para resolver a
  limitação residual documentada no `HtmlSanitizer` (regex-based, edge
  cases de aninhamento malformado). Duas opções avaliadas: OWASP Java
  HTML Sanitizer vs JSoup + Safelist customizado.
- **Decisão**: usar **OWASP Java HTML Sanitizer**
  (`com.googlecode.owasp-java-html-sanitizer:owasp-java-html-sanitizer`,
  versão recomendada no momento da pesquisa: `20260313.1` — conferir
  Maven Central por versão mais nova antes de fixar no `pom.xml`, o
  artefato versiona por data, não semver).
- **Por quê (ponto decisivo)**: o contrato atual do `sanitizar()` exige
  **lançar `ValidationException`** quando encontra tag/atributo fora da
  allowlist — não pode virar strip silencioso (que é o comportamento
  padrão de ambas as libs). A diferença está em qual delas oferece um
  hook oficial pra isso:
  - OWASP expõe `HtmlChangeListener` (`discardedTag(context, elementName)`,
    `discardedAttributes(...)`), disparado no momento exato da rejeição
    dentro de `policy.sanitize(html, listener, context)`. É o lugar certo
    pra lançar a `ValidationException` com mensagem específica (ex: "tag
    `<script>` não permitida"), preservando a granularidade que o
    contrato atual já entrega.
  - JSoup não tem listener equivalente. O único mecanismo é
    `Jsoup.isValid(html, safelist)` como passo **separado** antes de
    `.clean()` (dois parses), retornando só um `boolean` — sem dizer qual
    tag/atributo foi rejeitado. Pra ter mensagem granular seria preciso
    reimplementar manualmente a comparação árvore-vs-safelist, ou seja,
    reinventar o que o OWASP já entrega pronto.
  - Secundário: OWASP é lib pequena e focada (só sanitização, não é
    parser HTML genérico) — superfície de ataque da dependência é menor
    que JSoup (parser completo com muito mais funcionalidade que o
    necessário aqui). OWASP é mantido ativamente (release em mar/2026).
- **A allowlist replicada (`b`, `i`, `a[href]`, `p`, `br` + validação de
  esquema http/https) não fica mais fraca em nenhum ponto, e fica mais
  forte especificamente em**:
  - Tags aninhadas malformadas / atributo "vazando" pra virar tag nova —
    exatamente a limitação documentada que motivou a troca. Regex não
    entende árvore; parser real (OWASP) sim.
  - Entities/encoding esquisito em `href` (ex: entity decodificada tarde
    demais tipo `&#106;avascript:`) — parser HTML5-aware normaliza antes
    de revalidar; regex compara contra char literal, mais suscetível a
    esse tipo de bypass em teoria (no caso específico atual o regex
    acabava rejeitando por sorte de padrão, não por garantia estrutural).
  - Nested/tag quebrada de propósito (`<scr<script>ipt>`) — tokenizer
    real não cai nesse truque; regex também não caía, mas por acidente de
    design, não por garantia.
  - Atributos escondidos tipo `onerror`/`onclick`: já eram bloqueados
    hoje (regex atual descarta todos os atributos de `<b>/<i>/<p>` e
    recria `<a>` só com `href` extraído) — OWASP faz o mesmo via allowlist
    declarativa por elemento, equivalente, não é ganho nem perda.
- **Armadilhas pro `backend` na implementação**:
  1. Usar `policy.sanitize(html, listener, context)` (overload de 3 args)
     — não o `sanitize(html)` simples, que ignora rejeição e só limpa
     silenciosamente.
  2. `allowUrlProtocols("http", "https")` cobre esquema absoluto; testar
     explicitamente se `href="/caminho/relativo"` (sem esquema) passa
     como esperado — comportamento default da lib para path relativo deve
     ser conferido com teste, não assumido.
  3. **Não** usar `requireRelNofollowOnLinks()` por ora — mudaria o
     output armazenado (adiciona `rel="nofollow"` automaticamente),
     quebrando contrato de string atual. Registrar como possível
     hardening futuro (evitar spam de link building via CMS), decisão de
     produto separada.
  4. Manter guard `if (conteudoBruto == null) return null;` no wrapper —
     a lib não trata `null` internamente.
  5. Conferir diff de output em casos como `<br>` (lib pode normalizar
     pra `<br />` dependendo de config) e fechamento automático de `<p>`
     — rodar os testes de contrato existentes antes de considerar a
     migração completa, não assumir paridade byte-a-byte com o regex
     antigo.
  6. `allowElements`/`allowAttributes` da lib já são case-insensitive,
     não precisa normalizar case manualmente antes de chamar.
- **Trade-off**: dependência nova adicionada (aprovada pelo usuário) em
  troca de eliminar uma classe inteira de bug estrutural (parser real vs
  regex) — para o volume/risco do projeto (CMS editado só por admin, mas
  XSS armazenado ali afeta todos os visitantes), o ganho de robustez
  justifica a dependência.
- **Onde no projeto**: `backend/src/main/java/br/com/jogadoresanonimos/cms/util/HtmlSanitizer.java`
  (implementação pendente, cabe ao `backend`); testes em
  `backend/src/test/java/br/com/jogadoresanonimos/cms/util/HtmlSanitizerTest.java`.
- Por: security (decisão de lib) — implementação a cargo do backend

## [2026-07-12] Implementação: HtmlSanitizer migrado para OWASP Java HTML Sanitizer

- **Versão real usada**: `20240325.1` (confirmada via API do Maven Central
  no momento da implementação). A versão `20260313.1` citada na decisão
  anterior do `security` **não existe** no Maven Central — era estimativa,
  não confirmada. Fixada em `backend/pom.xml`.
- **Achado importante não previsto na decisão original**: `HtmlPolicyBuilder`
  do OWASP bloqueia o atributo `href` **globalmente**, mesmo com
  `allowAttributes("href").onElements("a")` configurado, se nenhum
  `allowUrlProtocols(...)` for chamado (comportamento documentado no
  javadoc do método: "effectively disallows href globally" sem protocolo
  branco listado). Sem isso, todo `<a href="https://...">` era descartado
  como se `<a>` estivesse fora da allowlist — armadilha fácil de cair
  silenciosamente. Resolvido com `allowUrlProtocols("http", "https")`
  **combinado** com um `AttributePolicy` customizado no `href`
  reaplicando a regex `^(https?://|/)[^"'<>\s]*$` — a policy nativa de
  protocolo sozinha é mais permissiva que o contrato exige (deixa passar
  `href="//evil.com"` protocol-relative e `href="caminho-sem-barra"` sem
  esquema, tratando "ausência de protocolo" como sempre ok). A combinação
  das duas camadas fecha essa brecha sem perder a rejeição de
  `javascript:`/`data:`/etc.
- **Achado sobre o callback `HtmlChangeListener`**: no fluxo do
  `HtmlPolicyBuilder`, `discardedAttributes` nunca dispara de verdade
  (é vestigial da interface, usado só por outro fluxo legado da lib,
  `Sanitizers`). Quando `href` é rejeitado ou ausente, a tag `<a>` inteira
  é removida via `discardedTag("a")` — não sobra `<a>` sem `href` no
  output. Isso invalidou a estratégia prevista de "detectar `<a>` sobrando
  pós-sanitização" ou usar `discardedAttributes` para diferenciar "href
  ausente" de "esquema inválido". Solução: dentro do listener, quando
  `discardedTag("a")` dispara, uma regex auxiliar simples inspeciona o
  markup **original** (não o sanitizado) só para decidir qual das duas
  mensagens mostrar — essa regex não é fonte de verdade de segurança (quem
  decide o que é seguro é a `PolicyFactory`), é só para granularidade de
  UX de erro.
- **`throw` fora do callback**: mantido como planejado — listener guarda a
  primeira mensagem de descarte em campo de instância, `sanitizar()` só
  lança `ValidationException` depois que `policy.sanitize(...)` retorna.
- **Entity disfarçada em href testada manualmente** (`href="jav&#97;script:alert(1)"`):
  o parser do OWASP decodifica a entity antes de entregar o valor pro
  `AttributePolicy`, então chega já decodificado e é bloqueado
  corretamente pela checagem de protocolo — confirma que não há o mesmo
  problema que existiria com regex comparando contra HTML cru não
  decodificado.
- **Diferenças de formatação de output vs regex antigo** (não são bug,
  são equivalentes semanticamente): `<br>` vira `<br />`; aspas dupla e
  simples em texto escapado viram `&#34;`/`&#39;` em vez de
  `&quot;`/`&#x27;`. Ajustadas as asserções desses casos em
  `HtmlSanitizerTest` (4 dos 10 testes existentes), sem enfraquecer o que
  cada teste verifica.
- **Onde no projeto**: `backend/pom.xml` (dependência),
  `backend/src/main/java/br/com/jogadoresanonimos/cms/util/HtmlSanitizer.java`
  (implementação), `backend/src/test/java/br/com/jogadoresanonimos/cms/util/HtmlSanitizerTest.java`
  (asserções ajustadas).
- Por: backend (implementação, confirmação de versão real da lib, e dois
  achados de comportamento da lib não previstos na decisão original do
  `security`)

## [2026-07-12] QA: expansão de testes do HtmlSanitizer (OWASP) e bug real encontrado

- **O que foi coberto**: `HtmlSanitizerTest` expandido de 10 para 25 casos,
  sem alterar os 10 originais. Novos casos: tags malformadas/aninhamento
  cruzado (`<b><i>x</b></i>`, `<p><p>x</p>` sem fechar, tag nunca fechada
  no fim da string); atributo malicioso em tag permitida (`onclick`,
  `onmouseover`, `style` com `javascript:`) — confirmado que só o atributo
  é removido, tag sobrevive, sem lançar exception (comportamento correto
  do OWASP, diferente de tag fora da allowlist); href com esquema
  disfarçado em entity decimal/numérica (`jav&#97;script:`,
  `&#106;avascript:`) — rejeitado corretamente; entities numéricas/hex
  misturadas em texto puro preservadas; tag quebrada de propósito
  (`<scr<script>ipt>`) e `<script>` aninhado dentro de `<a>` válida —
  ambos rejeitados; href relativo (`/pagina`) aceito; href com esquema
  `data:` rejeitado.
- **Bug real encontrado (reportado ao `backend`, não corrigido por mim)**:
  `href="//evil.com"` (protocol-relative) **não é rejeitado** —
  `HtmlSanitizer.sanitizar()` retorna a tag `<a>` normalmente em vez de
  lançar `ValidationException`. Causa provável: a regex `SAFE_HREF` em
  `backend/src/main/java/br/com/jogadoresanonimos/cms/util/HtmlSanitizer.java`
  (`^(https?://|/)[^"'<>\s]*$`) trata qualquer valor começando com `/`
  como caminho relativo seguro, incluindo `//evil.com` (que começa com
  `/` e bate no mesmo ramo da alternância). O próprio knowledge já citava
  essa brecha como algo que a combinação `allowUrlProtocols` +
  `AttributePolicy` deveria fechar (entrada de implementação, achado 2),
  mas o regex atual não fecha — precisa de checagem extra tipo rejeitar
  se o valor começa com `//` antes de aceitar por começar com `/`.
  Teste que reproduz: `deveRejeitarHrefProtocolRelativo` em
  `backend/src/test/java/br/com/jogadoresanonimos/cms/util/HtmlSanitizerTest.java:149`.
- **Resultado da suíte completa** (`./mvnw test`): 50 testes, 1 falha (o
  bug acima, esperado até correção do backend), 1 erro (`contextLoads`,
  falta Postgres local, baseline conhecido, não é regressão). Nenhuma
  outra regressão.
- **Decisão de estratégia**: não mockei o sanitizer nem o parser OWASP —
  teste é unitário puro sobre a classe real (`new HtmlSanitizer()`), sem
  dependência externa, então não havia necessidade de Testcontainers
  aqui; contrato público (assinatura, exceptions, allowlist) não mudou,
  então testes são só adição, não migração.
- **Pendência**: aguardando `backend` corrigir a regex de href e então
  destravar `deveRejeitarHrefProtocolRelativo` (hoje falhando de propósito
  até o fix).
- Por: qa-backend

## [2026-07-12] Validação anti-polyglot de upload de PDF (literatura)
- Ideia: `ArquivoProcessor` (PDFBox) valida PDF de admin antes de salvar, contra ataque de polyglot file
- Bloqueia: `/JavaScript`, `/OpenAction`, `/AA` (auto-action), `/EmbeddedFile`, `/Launch` no PDF; exige `%PDF-` no byte 0; rejeita bytes extras antes/depois do envelope PDF (técnica clássica de esconder outro formato dentro do PDF)
- Onde: `backend/src/main/java/.../arquivo/service/ArquivoProcessor.java`
- Por: backend

## [2026-07-12] Chave HMAC para correlação de log (tentativas de login/reset) e rate limit de upload de imagem

- **O que foi feito/decidido**: fechadas as duas pendências que o `backend`
  sinalizou no contrato SDD `backend/docs/api/contrato-auth-cms-upload.md`
  (auth/CMS/upload).

  **1. Chave HMAC para hash de log de tentativa de login/esqueci-senha**
  (já previsto em `.claude/knowledge/seguranca-email-cadastro.md`, faltava
  definir onde a chave vive):
  - Vive em variável de ambiente/secret (`HMAC_LOG_CORRELATION_KEY`),
    32+ bytes aleatórios, nunca hardcoded nem commitada — mesmo padrão dos
    outros segredos do projeto (JWT signing key, credenciais de storage).
  - É uma chave **separada** da chave de assinatura do JWT (least
    privilege: comprometer uma não deve comprometer a outra).
  - Algoritmo: HMAC-SHA256 (nunca SHA1/MD5 puro). Aplicado sobre o
    identificador normalizado (apelido ou e-mail, lowercase/trim) antes de
    logar tentativas de login/reset falhas. O output é truncado (16 bytes
    hex) antes de ir pro log — reduz ainda mais a superfície mesmo sendo
    já não-reversível sem a chave.
  - Rotação: manual/operacional a cada 6-12 meses, ou imediatamente em caso
    de suspeita de vazamento. Sem necessidade de dual-key simultâneo (isso
    seria necessário só se algo dependesse da chave em tempo real, tipo
    sessão JWT ativa — aqui só serve pra correlacionar linhas de log entre
    si, então girar a chave só torna logs *antigos* incorrelacionáveis com
    os *novos*, o que é aceitável dado o volume baixo do projeto e a
    retenção de log curta). Rotação é ato do `devops` (trocar env var +
    redeploy), sem migration de dado envolvida.

  **2. Rate limit do endpoint de upload de imagem** (`POST
  /api/v1/admin/imagens`, só `ROLE_ADMIN`), bucket duplo, token bucket:
  - Por admin (`usuario_id` do token): 10 uploads/minuto, burst 15.
  - Global do endpoint (soma de todos os admins): 60 uploads/minuto,
    burst 80 — rede de segurança contra múltiplas contas admin
    comprometidas ao mesmo tempo ou automação com credencial roubada.
  - Nenhum dos dois buckets usa IP como chave (mantém consistência com o
    padrão já usado no resto do projeto — rate limit sempre por
    identificador de sessão/conta, nunca IP, por causa do requisito de
    anonimato).
  - Ao exceder: `429 RATE_LIMITED` com header `Retry-After`; mensagem não
    diz qual dos dois buckets estourou primeiro (só o header técnico).
  - Reaproveita a mesma infraestrutura de token bucket já usada em
    login/registro — não cria mecanismo novo isolado pra upload.

- **Trade-off/motivo**: volume do projeto é baixo (~2.500 req/dia
  totais), então os limites de upload são folgados o bastante pra uso
  humano legítimo (edição de CMS em lote) e qualquer coisa acima é sinal
  de automação não prevista — números são ponto de partida, ajustáveis
  com telemetria real. Na chave HMAC, optou-se por rotação simples sem
  dual-key porque o caso de uso (correlação de log, não autenticação viva)
  não justifica a complexidade extra que o dual-key exigiria — evitar
  overengineering em cenário de baixa escala.

- **Onde no projeto**:
  - Contrato de API: `backend/docs/api/contrato-auth-cms-upload.md`
    (seção "Pendências explícitas para outros agentes", bloco 1 e 3).
  - Decisão anterior relacionada (hash de tentativa de login com HMAC,
    ainda sem definir onde a chave vive): `.claude/knowledge/seguranca-email-cadastro.md`,
    seção "Atualização 2026-07-12: login por apelido OU e-mail".
  - Implementação cabe ao `backend` (uso da variável de ambiente e do
    token bucket) e ao `devops` (provisionar a env var e o processo de
    rotação).

## [2026-07-13] Interceptor HTTP de Authorization — token JWT só em memória, nunca em storage

- **Contexto**: `frontend-services` tinha sinalizado gap real — `Auth`
  service recebia o JWT do backend no login mas não expunha de volta pra
  outros services, e não existia `HttpInterceptor` anexando o header
  `Authorization`. Bloqueava qualquer endpoint autenticado assim que
  `environment.useMocks` fosse desligado.
- **Decisão de armazenamento — token só em memória (campo privado na
  classe `Auth`, não signal, não `localStorage`/`sessionStorage`)**:
  - Consistente com decisão **já tomada** no `adminGuard`
    (`frontend/src/app/guards/admin.guard.ts`), que já assumia
    `Auth.usuarioAtual()` como signal síncrono sem persistência — não
    reabri essa discussão, só estendi a mesma linha pro token.
  - Trade-off aceito: sessão não sobrevive a F5 (perda de conveniência)
    em troca de reduzir superfície de XSS — nada sensível fica acessível
    via `localStorage`/`sessionStorage`, que qualquer script injetado
    conseguiria ler. Para um site que lida com dado de pessoa em situação
    vulnerável (relatos, participação em reuniões), essa troca favorece
    o requisito de anonimato/segurança do projeto sobre conveniência de
    sessão persistente.
  - Alternativa mais robusta (httpOnly cookie + refresh token) não foi
    adotada nesta rodada porque exigiria mudança de contrato no backend
    (backend hoje devolve o JWT no corpo da resposta de login, não seta
    cookie) — fica registrado como possível hardening futuro, não como
    bloqueio desta entrega.
- **O que foi implementado**:
  - `Auth.tokenAtual` (campo privado, não readonly signal) + método
    público `obterToken(): string | null` pro interceptor consultar.
  - `aplicarLoginBemSucedido()` (chamado tanto no fluxo mock quanto no
    real) agora também guarda `envelope.data.token`, além do usuário.
  - `logout()` limpa `tokenAtual` junto com `usuarioAtualSignal`.
  - `HttpInterceptorFn` funcional (padrão moderno, não classe) em
    `frontend/src/app/interceptors/auth.interceptor.ts`: anexa
    `Authorization: Bearer <token>` só em requisições cujo `req.url`
    começa com `environment.apiBaseUrl` (não vaza o token pra chamada de
    terceiro nenhuma, caso alguma exista no futuro). Em erro `401` de uma
    chamada da própria API, chama `Auth.logout()` e deixa o erro seguir
    (`throwError`) pro `catchError` de cada service tratar — interceptor
    não decide navegação/redirect, isso é responsabilidade de cada tela
    (mesmo princípio já usado no `adminGuard`: guard/interceptor cuidam
    só de mecanismo, não de UX de erro).
  - Registrado em `frontend/src/app/app.config.ts` via
    `provideHttpClient(withInterceptors([authInterceptor]))`.
- **Nunca em query string**: o token só vai no header `Authorization`,
  nunca em query string/corpo serializado em URL — consistente com a
  regra do `CLAUDE.md` de tokens nunca em GET/query string.
- **Validado**: `npx tsc --noEmit` em `frontend/` limpo após a mudança.
- **Onde no projeto**:
  - `frontend/src/app/services/auth.ts` (token em memória, exposto via
    `obterToken()`, limpo em `logout()`).
  - `frontend/src/app/interceptors/auth.interceptor.ts` (novo arquivo).
  - `frontend/src/app/app.config.ts` (registro do interceptor).
  - `frontend/src/app/guards/admin.guard.ts` (decisão de referência que
    este trabalho manteve consistente, não alterado nesta tarefa).
- Por: security

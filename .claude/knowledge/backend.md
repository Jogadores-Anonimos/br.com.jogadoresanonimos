## [2026-07-13] EscritorioServico ganha bairro + telefones (paridade com GrupoIntergrupo)
- O que foi feito/decidido: estendido o módulo `escritorio` para dar a `EscritorioServico` os mesmos recursos que `GrupoIntergrupo` já tinha: campo `bairro` (nullable, suporta preenchimento de endereço via CEP/viacep no frontend) e lista de telefones via entidade filha `EscritorioServicoTelefone` (FK `ON DELETE CASCADE`, `label` opcional + `telefone` obrigatório).
- Trade-off ou motivo: reaproveitada 1:1 a decisão de modelagem já documentada para `GrupoIntergrupoTelefone` (tabela filha em vez de coluna serializada, por padrão de acesso "0 a N telefones" com volume baixo). Migration `V25` é puramente aditiva (expand sem contract, sem backfill/drop) — diferente do módulo Grupo, que teve passo de backfill/drop na `V22`, pois aqui não há dado legado a migrar.
- Onde no código/infra: `backend/src/main/java/br/com/jogadoresanonimos/escritorio/entity/EscritorioServico.java`, `entity/EscritorioServicoTelefone.java` (novo), `dto/EscritorioTelefoneRequest.java`/`EscritorioTelefoneResponse.java` (novos), `service/EscritorioRegionalService.java`, `db/migration/V25__adiciona_bairro_telefones_escritorio_servico.sql`; contrato em `backend/docs/api/contrato-escritorios-regionais.md` (seção "Revisão 2026-07-13 (2ª)", mudança não-breaking: campos novos opcionais em request, campos novos em response)
- Testes: build e os 45 testes do pacote `escritorio` passam (BUILD SUCCESS). Nenhum commit feito (regra do projeto). Na entrega original do `backend` os testes existentes só foram ajustados para compatibilidade de assinatura, sem cobertura nova dos campos `bairro`/`telefones` — cobertura fechada depois pelo `qa-backend` (ver `.claude/knowledge/qa-backend-escritorio-telefones-bairro.md`).
- Ensinado por: backend

## [2026-07-13] Soft-exclude de páginas CMS migradas para conteúdo estático
- O que foi feito/decidido: páginas CMS migradas para conteúdo estático no frontend (`programa-de-recuperacao`, `programa-de-unidade`) não são deletadas do banco. Adicionada flag `oculta_na_listagem_admin` na tabela `cms_pagina`, usada para soft-exclude dessas páginas da listagem admin (endpoint `listarAdmin`), preservando o registro.
- Trade-off ou motivo: existe FK de auditoria (`cms_alteracao_log`) apontando para essas páginas e o conteúdo não é órfão/vazio — apagar via migration destrutiva perderia histórico de auditoria. Padrão a reaplicar no futuro: sempre que uma página CMS for "aposentada" para hardcode no frontend, marcar essa flag em vez de apagar, a menos que se confirme explicitamente que remoção é aceitável (sem histórico/FK relevante).
- Onde no código/infra: `backend/src/main/resources/db/migration/V24__oculta_paginas_migradas_para_conteudo_estatico.sql`, `backend/src/main/java/br/com/jogadoresanonimos/cms/entity/CmsPagina.java`, `cms/repository/CmsPaginaRepository.java`, `cms/service/CmsService.java`
- Ensinado por: backend

## [2026-07-12] Módulo FAQ / Perguntas e Respostas (backend completo)
- Ideia: CRUD admin simples de perguntas frequentes, com endpoint público listando todas ordenadas por `ordem`. Módulo mais enxuto do ciclo do dashboard admin: sem hierarquia, sem status/toggle, sem campos de expiração — mais simples até que `aviso`.
- Modelagem: `Faq` só tem `pergunta` (500 chars), `resposta` (4000 chars) e `ordem` (int, controle manual de sequência de exibição pelo admin — não é alfabética nem cronológica). Sem vínculo com `usuario` (mesma decisão de `aviso`: conteúdo institucional, sem requisito de auditoria de autoria nesta entrega).
- Um único DTO (`FaqResponse`) reaproveitado tanto no público quanto no admin — diferente de `aviso`/`mural`, não existe campo sensível (status/expiração) que precise ficar oculto do público, então não se justificava duplicar em `FaqPublicoResponse`/`FaqAdminResponse`.
- Índice simples `(ordem)` — cobre `ORDER BY ordem ASC` nos dois endpoints (público e admin usam a mesma ordenação, sem filtro diferente entre eles).
- Migration `V16__cria_tabela_faq.sql` — número reservado explicitamente pelo usuário para não colidir com `V15`/`V17` em uso paralelo por outros agentes na mesma sessão.
- `SecurityConfig`: adicionado `permitAll` explícito para `GET /api/v1/faq` (mesmo padrão dos outros módulos públicos — `HttpMethod.GET` matcher, não abre os métodos de escrita).
- Onde: `backend/src/main/java/br/com/jogadoresanonimos/faq/**`, `backend/src/main/resources/db/migration/V16__cria_tabela_faq.sql`, `backend/src/main/java/br/com/jogadoresanonimos/config/SecurityConfig.java`, contrato em `backend/docs/api/contrato-faq.md`
- Testes: 12 novos (`FaqControllerTest`, `FaqAdminControllerTest`, `FaqServiceTest`), todos passando; `./mvnw compile` limpo.
- Pendência para outros agentes: `frontend-services` precisa criar `FaqService` do zero (não existia nenhum service, só array hardcoded em `perguntas-e-respostas.ts`) — não é troca de mock por chamada real, é service novo.
- Por: backend

## [2026-07-12] Módulo Mural de Apoio (backend completo)
- Ideia: CRUD admin de banners do "Mural de Apoio" (cartazes de grupos JA), com endpoint público listando só banners ativos. Terceiro módulo de domínio próprio criado no ciclo do dashboard admin (após Relato e Aviso), seguindo a mesma decisão de `system-design` de não reaproveitar `CmsBloco` genérico.
- Modelagem: `BannerMural` só guarda `imagem_id` (FK para `imagem.id`), nunca a URL — resolvida em tempo de leitura via `StorageClient.urlPublicaPara()`, exatamente como `CmsService.montarBlocos` já fazia para blocos tipo imagem. Reuso do fluxo de upload existente (`POST /api/v1/admin/imagens`), sem endpoint de upload novo.
- Toggle `ativo`: banner inativo não é excluído, só some da listagem pública (`findByAtivoTrue`); só é alterado via `PUT` completo (mesmo padrão de "editar tudo de uma vez" do módulo `aviso`, sem endpoint dedicado `/ativar`).
- Índice `(ativo, criado_em DESC)` composto: cobre o filtro do endpoint público sem sort em memória, mesma lógica do índice `(status, criado_em)` de `relato`.
- Migration `V11__cria_tabela_banner_mural.sql` — número reservado explicitamente pelo usuário para não colidir com `V10` (módulo Avisos, criado em paralelo por outra sessão/agente).
- Achado colateral (não corrigido, fora do escopo): `GET /api/v1/relatos` e `GET /api/v1/avisos` são endpoints pensados como públicos mas **não têm matcher `permitAll` em `SecurityConfig`** — só path fixo (`/api/v1/auth/**`, `/api/v1/cms/**`, `/api/v1/arquivos/**`) está liberado, o resto cai em `anyRequest().authenticated()`. Adicionei `permitAll` explícito só para `GET /api/v1/mural-de-apoio` (via `HttpMethod.GET` matcher, para não abrir os métodos de escrita). Sinalizado no contrato para `security` avaliar se é bug pré-existente nos outros dois módulos.
- Onde: `backend/src/main/java/br/com/jogadoresanonimos/mural/**`, `backend/src/main/resources/db/migration/V11__cria_tabela_banner_mural.sql`, `backend/src/main/java/br/com/jogadoresanonimos/config/SecurityConfig.java`, contrato em `backend/docs/api/contrato-mural-de-apoio.md`
- Testes: 11 novos (`MuralDeApoioControllerTest`, `MuralDeApoioAdminControllerTest`, `BannerMuralServiceTest`), todos passando; suite completa 80/81 (único erro é `contextLoads` por falta de Postgres local, pré-existente)
- Por: backend

## [2026-07-12] Bug de bootstrap: Spring Boot 4.1.0 / Spring Framework 7 autoconfigura Jackson 3, não Jackson 2
- Bug: Spring Boot 4.1.0 / Spring Framework 7 autoconfigura Jackson 3 (`tools.jackson.databind`) por padrão, não Jackson 2 clássico (`com.fasterxml.jackson.databind`). `CmsService` injetava `ObjectMapper` clássico via construtor — sem bean candidato registrado, app falhava ao subir com `NoSuchBeanDefinitionException` logo no primeiro `docker compose up` real do projeto.
- Causa raiz: único uso de Jackson clássico no código era `CmsService.serializarSnapshot` + teste correspondente. Havia uma dependência solta `com.fasterxml.jackson.core:jackson-databind` (sem versão, herdada do parent, resolvida em 2.x) no `backend/pom.xml` — era resquício, só necessária transitivamente por `jjwt-jackson`, e não registra bean Spring.
- Fix aplicado: migrado `CmsService.java` e `CmsServiceTest.java` para usar `tools.jackson.databind.ObjectMapper` (Jackson 3, aproveita o bean autoconfigurado nativo do Spring Boot 4). Removida a dependência solta de Jackson 2 do `pom.xml` (permanece só como transitiva via `jjwt-jackson` em runtime).
- Achado secundário corrigido no mesmo `pom.xml`: havia duas declarações duplicadas de `spring-boot-maven-plugin` (uma com exclusão de lombok, outra vazia); mescladas em uma só — Maven avisava risco de build instável.
- Validação: `./mvnw test` (50 testes, 1 falha esperada em `contextLoads` por falta de Postgres local) + validação extra com Postgres real via docker isolado, confirmando que o contexto Spring sobe completo (Flyway roda as 6 migrations, `entityManagerFactory` e `CmsService` resolvem normalmente).
- Regra a fixar pro futuro: qualquer novo uso de Jackson no backend deve usar `tools.jackson.databind` (Jackson 3), nunca `com.fasterxml.jackson.databind` — evita reintroduzir esse bug.
- Onde: `backend/src/main/java/br/com/jogadoresanonimos/cms/service/CmsService.java`, `backend/src/test/java/br/com/jogadoresanonimos/cms/service/CmsServiceTest.java`, `backend/pom.xml`
- Por: backend

## [2026-07-12] Correções do code-reviewer aplicadas no backend (5 itens)
- Ideia: `backend` aplicou 5 correções apontadas pelo `code-reviewer`, nenhuma commitada (aguardando ok do usuário)
- Item 1 (HIGH) — `UsuarioController.me()` furava camada Controller→Service→Repository, chamando `UsuarioRepository` direto (único caso assim no projeto). Corrigido criando `UsuarioService.buscarMe(UUID)` (lança `UnauthenticatedException` se não achar usuário); controller passou a injetar só o service.
  - Padrão a seguir daqui pra frente: **toda lógica de usuário deve passar por `UsuarioService`**, nunca acessar `UsuarioRepository` direto do controller.
  - Onde: `backend/src/main/java/br/com/jogadoresanonimos/usuario/service/UsuarioService.java`
- Item 2 (HIGH) — Faltava `application.properties.example`. `application.properties` real já usava `${VAR:default}` em tudo sensível (DB_PASSWORD, JWT_SECRET, LOG_HMAC_SECRET, ADMIN_BOOTSTRAP_SENHA), sem segredo versionado — não era vazamento, só falta de doc. Criado `backend/src/main/resources/application.properties.example` espelhando as envs esperadas com placeholders.
- Item 3 (MEDIUM) — `RateLimiterService` com `ConcurrentHashMap` sem expiração (leak de memória). Sem adicionar lib nova (Caffeine não está no classpath).
  - Solução: `TokenBucket.expirado(Duration idleTtl)` compara `windowStart` (já funciona como "última atividade") contra refillWindow+ttl; `RateLimiterService` ganhou `@Scheduled` a cada 10min que remove buckets ociosos há mais de 30min.
  - Padrão a seguir: **TTL de 30min / cleanup a cada 10min, tudo em memória, sem lib nova** — é o padrão de referência caso outro rate limiter precise ser criado no futuro (adequado ao volume baixo do projeto, ~2500 req/dia; system-design já orienta contra overengineering aqui).
- Item 4 (LOW) — `pom.xml` tinha starter duplicado (`spring-boot-starter-webmvc` e variante `-test`) junto com `spring-boot-starter-web`. Removidos os duplicados; `spring-boot-starter-web` já cobre MVC.
- Item 5 (LOW) — `AdminBootstrap` usava `findAll().stream().anyMatch(...)` pra checar se já existe admin (ineficiente). Adicionado `existsByRole(Role role)` em `UsuarioRepository`; `AdminBootstrap` agora usa `existsByRole(Role.ADMIN)`.
- Testes: `./mvnw test` rodado após as correções, 34/35 passando; único erro é `contextLoads` por falta de Postgres local (esperado sem container, não é regressão)
- Por: backend

## [2026-07-12] Feature literatura em PDF (bloco CMS de arquivo)
- Ideia: novo bloco CMS `TipoBloco.arquivo` p/ anexar PDF em páginas (ex: literatura da irmandade)
- Onde: `backend/domain/arquivo/` (controller, dto, entity, repository, service) — espelha estrutura de `imagem`
- Endpoints: `POST /api/v1/admin/arquivos`, `DELETE /api/v1/admin/arquivos/{id}` (409 se arquivo ainda referenciado por bloco)
- Limite: 20MB/arquivo; reusa storage/quota já usado por `imagem`, sem infra nova
- GC: `ArquivoOrfaGcJob` remove arquivo órfão (sem bloco referenciando) após 7 dias
- Migrations: V5/V6 (Flyway)
- Contrato SDD: `backend/docs/api/contrato-auth-cms-upload.md` (atualizado)
- Estado: 30/31 testes passando; único erro é falta de Postgres local no ambiente de teste, não regressão

## [2026-07-12] Validação de PDF anti-polyglot (ArquivoProcessor)
- Ideia: `ArquivoProcessor` usa PDFBox p/ validar PDF antes de salvar
- Regras: rejeita `/JavaScript`, `/OpenAction`, `/AA`, `/EmbeddedFile`, `/Launch`; exige `%PDF-` no byte 0; rejeita bytes extra antes/depois do envelope PDF
- Motivo: evitar upload de PDF-polyglot (arquivo válido como PDF e também como outro formato executável)
- Por: backend

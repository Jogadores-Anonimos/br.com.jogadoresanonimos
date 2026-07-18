### AdminBootstrap
- **Onde**: `usuario/service/AdminBootstrap.java` (`CommandLineRunner`)
- **Propósito**: garante que existe pelo menos um usuário `ROLE_ADMIN` no startup
- **Como**: usa `UsuarioRepository.existsByRole(Role.ADMIN)` (não `findAll().anyMatch`) para checar antes de criar

### AgendaItemGrupo (`@Embeddable` + `@ElementCollection`)
- **Onde**: `escritorio/entity/AgendaItemGrupo.java`, `GrupoIntergrupo.java`
- **Propósito**: substituiu `diasSemana: List<DiaSemana>` + `horario` único por item de agenda com dia+horário próprios
- **Como**: `agenda: List<AgendaItemGrupo>` via `@ElementCollection` (sem identidade própria); não-duplicidade de dia garantida no service (`resolverAgenda`) e `PRIMARY KEY (grupo_intergrupo_id, dia_semana)` na tabela `grupo_intergrupo_horario`
- **Trade-off**: escolhido em vez de entidade JPA própria porque acesso é sempre "toda a agenda do grupo", nunca item isolado, e volume é baixo

### AdminAuditoriaLog (módulo `auditoria`)
- **Onde**: `br.com.jogadoresanonimos.auditoria` (`AdminAuditoriaLog`, enum `AcaoAuditoria`, `AdminAuditoriaLogRepository`, `AdminAuditoriaService`); migration `V7`
- **Propósito**: implementa a decisão de `security` de trilha de auditoria genérica para ação administrativa (ver `.claude/knowledge/security.md`)
- **Como**: `RelatoService` (editar/aprovar/rejeitar/reverter/remover) recebe `adminId`, captura status anterior antes da mutação, chama `AdminAuditoriaService.registrar`; `RelatoAdminController` injeta `@AuthenticationPrincipal AuthenticatedUser principal` (mesmo padrão de `CmsAdminController`/`UsuarioAdminController`) e repassa `principal.id()`
- **Trade-off**: em `remover`, como o registro é apagado do banco, `status_novo` grava o mesmo valor de `status_anterior` (snapshot do estado no instante da remoção) — evita inventar enum de "status pós-remoção" fora do escopo combinado com `security`

### Argon2id (hash de senha)
- **Onde**: `auth/service/AuthService`
- **Propósito**: hash de senha de usuário
- **Como**: `Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()` (nome do método no Spring Security 7, mudou de `defaultsForSpringSecurity()`)

### ArquivoProcessor (validação anti-polyglot de PDF)
- **Onde**: `arquivo/service/ArquivoProcessor.java`
- **Propósito**: evitar upload de PDF-polyglot (válido como PDF e também como outro formato executável)
- **Como**: usa PDFBox; rejeita `/JavaScript`, `/OpenAction`, `/AA`, `/EmbeddedFile`, `/Launch`; exige `%PDF-` no byte 0; rejeita bytes extras antes/depois do envelope PDF

### AdminAuditoriaLogSpecifications (`JpaSpecificationExecutor`)
- **Onde**: `auditoria/repository/AdminAuditoriaLogSpecifications.java`, `AdminAuditoriaLogRepository`
- **Propósito**: substituir `@Query` HQL com padrão `(:param IS NULL OR coluna >= :param)` para filtro opcional de `Instant`, que quebrava (Postgres não infere tipo do parâmetro, `CAST` no HQL evolui erro pra "cannot cast bytea to timestamp")
- **Como**: `Specification` dinâmica, predicado só entra na query quando o filtro não é nulo — nunca gera cláusula `IS NULL OR` no SQL
- **Trade-off**: padrão a reaproveitar em qualquer filtro opcional de data futuro no projeto, em vez do `@Query` com `IS NULL OR`

### Auditoria de mudança de role (`UsuarioService.editarRole`)
- **Onde**: `usuario/service/UsuarioService.editarRole`
- **Propósito**: mudança de role (ADMIN↔PARTICIPANTE) é ação sensível, não tinha rastro em `AdminAuditoriaService`
- **Como**: registra `entidade="usuario"`, `AcaoAuditoria.EDITAR`, role anterior/nova; só grava quando a role muda de fato (no-op não gera log)

### Baseline única de migrations (V1__baseline.sql)
- **Onde**: `backend/src/main/resources/db/migration/V1__baseline.sql`
- **Propósito**: consolidar migrations `V1`-`V30` (exceto `V26`, nunca existiu) em uma só, reduzindo ruído de histórico
- **Como**: gerada via `pg_dump --schema-only` + INSERTs de seed reaproveitados; decisões de negócio de migrations descartadas documentadas em `backend/docs/migrations-consolidadas-decisoes.md`
- **Trade-off**: dev local com volume Docker antigo sofre checksum mismatch do Flyway contra a nova baseline — exige `docker compose down -v` ou `flyway repair`

### Conteúdo institucional real do cliente (migrations V9-V17, páginas CMS)
- **Onde**: `backend/src/main/resources/db/migration/` (`V9`-`V17`)
- **Propósito**: substituir placeholder/protótipo genérico (baseado no site GA americano) por texto definitivo aprovado pelo cliente em 10 páginas
- **Como**: `sobre`/"Como Funciona" (V9+V17, V17 removeu 9 blocos antigos de protótipo que ainda apareciam antes do texto novo); `historia` (V10); `programa-de-recuperacao`/`programa-de-unidade` (V11/V12, texto específico JA Brasil, diferente da tradução genérica AA que estava hardcoded no frontend); `perguntas-e-respostas`/FAQ (V13, módulo `Faq` dedicado, 9 perguntas, texto plano sem HTML); `jogo-compulsivo` (V14); `uma-nova-chance` (V15, poema "Uma Outra Chance"); `anonimato` (V16); `termos`/`privacidade` (V7 rascunho, V8 definitivo, LGPD, sem menção a "acompanhamento profissional de saúde" a pedido do cliente)
- **Trade-off**: sempre confirmar se conteúdo "existente" numa página CMS é placeholder de protótipo ou dado real aprovado antes de decidir entre substituir vs. anexar (V9 só anexou, V17 precisou corrigir removendo o antigo)

### Biblioteca pessoal (usuario ↔ arquivo)
- **Onde**: `usuario/biblioteca/` (entity `UsuarioArquivo`/`UsuarioArquivoId`, `UsuarioArquivoRepository`, `BibliotecaPessoalService`, `BibliotecaPessoalController`); migration `V17`
- **Propósito**: permitir que usuário logado salve PDFs de literatura na própria biblioteca (aba Downloads de `minha-conta`)
- **Como**: subpacote dentro do módulo `usuario` (não módulo isolado); tabela de junção com `@EmbeddedId` composto (`usuarioId`+`arquivoId`) como `PRIMARY KEY`; `usuarioId` sempre vem de `@AuthenticationPrincipal`, nunca de path/query; `POST`/`DELETE` idempotentes (idempotência = "estado desejado", não erro 404 em segunda chamada); `ON DELETE CASCADE` nas FKs
- **Trade-off**: idempotência de DELETE diverge do padrão usado em `RelatoAdminController` (que retorna 404) porque aqui é "preferência", não "recurso que deveria existir"

### Bug incidental: `List.of(Object[]...)` ambíguo em teste (`ImagemOrfaGcJobTest`)
- **Onde**: `imagem/service/ImagemOrfaGcJobTest.java`
- **Propósito**: revalidação de suíte (qa-backend-unit) encontrou erro de compilação/inferência de tipo
- **Como**: `List.of(Object[]...)` ambíguo entre `List<Object>` e `List<Object[]>`; corrigido com `List.<Object[]>of(...)` explícito

### Bug: Jackson 3 vs Jackson 2 (Spring Boot 4.1.0 / Spring Framework 7)
- **Onde**: `cms/service/CmsService.java`, `pom.xml`
- **Propósito**: Spring Boot 4.1.0 autoconfigura Jackson 3 (`tools.jackson.databind`) por padrão, não Jackson 2 clássico
- **Como**: `CmsService.serializarSnapshot` migrado para `tools.jackson.databind.ObjectMapper`; dependência solta `jackson-databind` (2.x) removida do `pom.xml` (permanece só transitiva via `jjwt-jackson`)
- **Trade-off**: qualquer novo uso de Jackson no backend deve usar `tools.jackson.databind` (Jackson 3), nunca `com.fasterxml.jackson.databind`

### Bug: schema mismatch Hibernate (`uf` CHAR vs VARCHAR) — resolvido
- **Onde**: `escritorio/entity/EstadoIntergrupo.java` (campo `uf`), `V1__baseline.sql`, `V3__ajusta_tipo_uf_estado_intergrupo.sql`
- **Propósito**: divergência era só de banco físico desatualizado (`CHAR(2)`) — entidade e baseline já usavam `VARCHAR(2)` corretamente
- **Como**: `V3` faz `ALTER TYPE` idempotente na coluna `uf` para corrigir instâncias de banco já provisionadas com o tipo antigo
- **Trade-off**: migration defensiva, não altera nenhum contrato — só sincroniza ambientes divergentes

### Bug: colisão de migration `V2` duplicada (dois arquivos `V2__*.sql`)
- **Onde**: `backend/src/main/resources/db/migration/`
- **Propósito**: agentes paralelos criaram dois `V2__*.sql`; resolvido renomeando um deles
- **Como**: próxima migration livre confirmada em `V4`+ — sempre checar o diretório real antes de criar, não confiar em número lembrado de sessão anterior

### Bug: `docker-compose.yml` build "fantasma" — `target/classes` do container ≠ `target/` do host
- **Onde**: `docker-compose.yml` (serviço `backend`), volume mount `./backend/src`
- **Propósito**: compose só monta `./backend/src`, não a pasta `target/` inteira — build antigo com migrations obsoletas ficava preso dentro do container mesmo após corrigir o código-fonte no host
- **Como**: fix foi `docker compose exec backend rm -rf /app/target/classes/db/migration` + restart do container
- **Trade-off**: armadilha conhecida para debugar builds "fantasma" (código correto no host, comportamento antigo no container) neste projeto

### Bug: `MFA_SECRET_ENCRYPTION_KEY` ausente no `docker-compose.yml`
- **Onde**: `docker-compose.yml` (`backend.environment`)
- **Propósito**: só `JWT_SECRET`/`LOG_HMAC_SECRET` estavam sendo repassados ao container, faltava `MFA_SECRET_ENCRYPTION_KEY`
- **Como**: chave adicionada à lista de `environment:` do serviço `backend`; nova chave gerada e configurada no `.env` local (usuário não tinha a anterior)

### Auditoria de performance 2026-07-17 (pós-conteúdo real)
- **Onde**: `escritorio/repository/EscritorioServicoRepository` (`@EntityGraph`), `relato/**` (DTO, controller, `SecurityConfig`)
- **Propósito**: rodada após popular conteúdo real (94 reuniões, 366 reflexões, 13 relatos, 69 grupos, mural)
- **Como**: N+1 em `EscritorioServico.telefones` corrigido com `@EntityGraph(attributePaths = "telefones")` em `findAllByOrderByOrdemAsc` (mesmo padrão de `HotlineRegiaoRepository`); sem achado ALTO/CRÍTICO em nenhuma camada, volume real (~2.500 req/dia) bem coberto pela arquitetura atual

### CHECK constraints em `admin_auditoria_log` (V2)
- **Onde**: `V2__adiciona_check_constraints_uf_e_auditoria.sql`
- **Propósito**: garantir integridade de valores de enum a nível de banco, não só de aplicação
- **Como**: CHECK em `acao` (APROVAR,REJEITAR,REVERTER,REMOVER,EDITAR) e em `status_anterior`/`status_novo` (pending,approved,rejected)

### Consolidação por domínio + subpasta técnica
- **Onde**: cada módulo em `backend/src/main/java/br/com/jogadoresanonimos/<dominio>/`
- **Propósito**: organizar por domínio/feature, mas com subpastas técnicas explícitas dentro
- **Como**: padrão `<dominio>/<camada-tecnica>/Classe.java` (`controller/`, `service/`, `repository/`, `entity/`, `dto/`, `util/`); enums de domínio (`Role`, `TipoBloco`) vão para `entity/`; utilitário puro sem estado (`HtmlSanitizer`, `HmacHasher`) vai para `util/`; processor/client usado só por um service (`ImagemProcessor`, `StorageClient`) vai para `service/` junto do dono; job agendado/bootstrap (`ImagemOrfaGcJob`, `AdminBootstrap`) vai para `service/`
- **Trade-off**: `config/` e `security/` (transversais) ficam na raiz, fora de qualquer domínio

### Editar conta própria (usuário comum)
- **Onde**: `usuario/controller/UsuarioController` (`PUT /api/v1/usuarios/me`, `PUT /api/v1/usuarios/me/senha`)
- **Propósito**: fechar aba "Detalhes" de `minha-conta`, antes stub
- **Como**: editar apelido/email sem exigir senha atual; trocar senha exige `senhaAtual` validada via `PasswordEncoder.matches`; checagem de unicidade de apelido/email exclui o próprio usuário; senha atual incorreta usa 401 (`UnauthenticatedException`), não 400, para não dar pista de enumeração
- **Trade-off**: `Relato.nomeExibicao` não tem vínculo dinâmico com `Usuario.apelido` — trocar apelido não afeta relatos já publicados (desenho pré-existente, só confirmado nesta tarefa)

### EscritorioServico: bairro + telefones
- **Onde**: `escritorio/entity/EscritorioServico.java`, `EscritorioServicoTelefone.java`, migration `V25`
- **Propósito**: dar paridade de recursos entre `EscritorioServico` e `GrupoIntergrupo` (bairro nullable + lista de telefones)
- **Como**: entidade filha `EscritorioServicoTelefone` (FK `ON DELETE CASCADE`, `label` opcional + `telefone` obrigatório), reaproveitando 1:1 a modelagem já usada em `GrupoIntergrupoTelefone`
- **Trade-off**: migration `V25` é puramente aditiva (sem backfill/drop), diferente da `V22` do módulo Grupo, pois não havia dado legado a migrar

### Estilo: import qualificado inline vs import de topo
- **Onde**: `AuthService.java`, `ReflexaoDiaService.java`, `VinteQuestaoService.java`, `RateLimiterService.java`
- **Propósito**: consistência de estilo, uso de nome totalmente qualificado inline no meio do código substituído por import no topo do arquivo

### FAQ (módulo)
- **Onde**: `faq/**`, migration `V16`
- **Propósito**: CRUD admin simples de perguntas frequentes, com endpoint público ordenado por `ordem`
- **Como**: `Faq` só tem `pergunta`, `resposta`, `ordem` (controle manual, não alfabética/cronológica); sem vínculo com `usuario`; um único DTO (`FaqResponse`) reaproveitado no público e admin, por não ter campo sensível a ocultar; índice simples `(ordem)`

### GC jobs — remoção de `@Transactional` do loop + delete storage-primeiro
- **Onde**: `ImagemOrfaGcJob`, `ArquivoOrfaGcJob`
- **Propósito**: `@Transactional` no loop prendia conexão Hikari durante chamada de rede ao storage
- **Como**: `@Transactional` removida do loop; ordem agora é delete no storage primeiro, delete no banco em batch (`deleteAllByIdInBatch`) só pros IDs confirmados — idempotente, falha no meio do loop não perde nada, item reentra na próxima execução
- **Achado de brinde**: `ArquivoOrfaGcJob` não removia `thumbnailChave` órfã do storage, corrigido junto

### GlobalExceptionHandler + taxonomia de erro
- **Onde**: `common/exception/GlobalExceptionHandler.java` (`@RestControllerAdvice`)
- **Propósito**: mapear toda exceção de negócio para `error.code` da taxonomia, sempre no envelope `{success, data, error}`
- **Como**: nunca vaza stack trace/nome de exception/SQL; página de erro padrão do Spring desativada via `server.error.include-*=never`

### HotlineRegiao: `nome` livre → UF + cidade
- **Onde**: `hotline/**`, migration `V27__descarta_dados_legado_recria_hotline_regiao_uf_cidade.sql`
- **Propósito**: substituir campo `nome` texto livre por `uf` (enum `UnidadeFederativa`, reaproveitado de `escritorio`) + `cidade`
- **Como**: conflito de UF+cidade (case-insensitive) lança `ConflitoDeRegiaoHotlineException` (409); novo `PUT /api/v1/admin/hotlines/regioes/{id}`; endpoint público só retorna regiões com pelo menos 1 telefone (`JOIN` no repository, nunca no controller); índice único funcional (`lower(cidade)`)
- **Trade-off**: migration descarta dados legados via `DELETE` em vez de migrar — `nome` não tinha padrão parseável para UF+cidade, heurística arriscava dado errado silenciosamente; recadastro manual pelo admin é mais seguro

### LocalDiskStorageClient (storage de imagem/PDF)
- **Onde**: `imagem/service/LocalDiskStorageClient.java` (implementa `StorageClient`)
- **Propósito**: produção usa disco local persistente, não bucket S3-compatible externo (decisão revertida do plano original)
- **Como**: promovido do antigo `StubStorageClient`; permissão restritiva ao gravar (`rw-r-----` arquivo, `rwx------` diretório) via `PosixFileAttributeView` (no-op silencioso no Windows); chave do objeto sempre `UUID.randomUUID() + "." + extensao`, nunca nome enviado pelo usuário (proteção contra path traversal); `download()` também checa `path.normalize().startsWith(baseDir)`
- **Trade-off**: volume baixo do projeto não justifica complexidade operacional de bucket externo (credenciais, billing); troca aceita é não ter CDN/distribuição geográfica nativa

### Índice em `admin_auditoria_log.criado_em` (migration V5)
- **Onde**: `V5__*.sql`, tabela `admin_auditoria_log`
- **Propósito**: performance de query de expurgo/listagem por data
- **Como**: índice simples em `criado_em`, não composto com `entidade`
- **Trade-off**: `entidade` é filtro independente/opcional e expurgo nunca filtra por ela — um composto liderado por `entidade` não serviria às duas queries mais frequentes

### Migração de UF+cidade e telefones em Hotline: agenda expand+contract na mesma migration
- **Onde**: `escritorio/db/migration/V30__migra_agenda_grupo_intergrupo_para_dia_horario.sql`
- **Propósito**: registrar exceção deliberada ao padrão geral do projeto (expand/contract em migrations separadas)
- **Como**: `V30` cria tabela nova, migra dado via `INSERT...SELECT...JOIN`, já dropa coluna/tabela antiga na mesma migration
- **Trade-off**: aceito por não haver consumidor externo do contrato além do frontend do monorepo, volume baixo, e precedente direto do próprio módulo em `V22`

### Mock de entidade sem setter via reflection (`Field.setAccessible`)
- **Onde**: `imagem/service/ImagemOrfaGcJobTest.java`, entidade `Imagem` (sem setter público para `id`/`criadoEm`)
- **Propósito**: montar fixture de teste com data controlada (ex: fora do período de graça de 7 dias) sem expor setter na entidade de produção
- **Como**: `Field.setAccessible(true)` + `field.set(instancia, valor)` para atribuir diretamente o campo privado
- **Trade-off**: aceito por ser só em código de teste; não justifica adicionar setter público na entidade só para teste

### Módulo Mural de Apoio
- **Onde**: `mural/**`, migration `V11`
- **Propósito**: CRUD admin de banners institucionais, com endpoint público listando só ativos
- **Como**: `BannerMural` guarda só `imagem_id` (FK), URL resolvida em tempo de leitura via `StorageClient.urlPublicaPara()`; toggle `ativo` (não exclui, só some da listagem pública via `findByAtivoTrue`); índice composto `(ativo, criado_em DESC)`

### N+1 em `CmsService.montarBlocos`
- **Onde**: `cms/service/CmsService.java` (`montarBlocos`, `atualizar`)
- **Propósito**: eliminar query por bloco de imagem/arquivo
- **Como**: batch `findAllById` de imagem/arquivo em vez de query individual por bloco; `atualizar` não refaz mais `buscarAdmin` (causava o N+1 duas vezes) — monta resposta a partir da entidade já em mãos na mesma transação

### RateLimiterService (token bucket em memória)
- **Onde**: `common/ratelimit/RateLimiterService`
- **Propósito**: rate limit de login/registro/upload sem dependência de Redis, proporcional ao volume do projeto (~2.500 req/dia)
- **Como**: `ConcurrentHashMap` de buckets por chave lógica (ação + HMAC do identificador normalizado, nunca texto puro); `TokenBucket.expirado(Duration idleTtl)` + `@Scheduled` a cada 10min remove buckets ociosos há mais de 30min (evita leak de memória sem lib nova)
- **Trade-off**: se escalar horizontalmente com múltiplas réplicas, precisa migrar para store compartilhado

### RecaptchaService (validação server-side v2 checkbox)
- **Onde**: `auth/service/RecaptchaService.java`, chamado por `AuthService.registrar`/`login`
- **Propósito**: antes só existia widget visual no frontend, sem checagem real no backend
- **Como**: chama `https://www.google.com/recaptcha/api/siteverify` (timeout 5s); ordem no `AuthService`: rate limit -> captcha -> lógica de negócio, antes de repository/hash de senha; fail-closed (timeout/5xx/`success=false`/resposta nula bloqueia) — erro de comunicação vira `UpstreamException` (502), falha de captcha vira `ValidationException` (400, field error em `recaptchaToken`)
- **Trade-off**: secret via env `RECAPTCHA_SECRET_KEY`, default em `application.properties`/`.example`/`docker-compose.yml` é a chave de teste pública do Google (`6LeIxAcTAAAAAGG-vFI1TnRWxMZNFuojJ4WifJWe`, sempre passa) — trocar antes de produção; `.env`/`.env.example` não foram tocados (bloqueio de sandbox), precisa configurar manualmente

### RegistroRequest/LoginRequest: campo `recaptchaToken`
- **Onde**: `backend/docs/api/contrato-auth-cms-upload.md`, DTOs de auth
- **Propósito**: contrato de API para suportar validação de captcha
- **Como**: campo obrigatório `recaptchaToken`; frontend (`auth.model.ts`, `minha-conta.ts`) captura token real do callback do widget v2 e reseta o widget após submit (token é single-use)

### Relato — controller de moderação
- **Onde**: `relato/controller/RelatoController.java` (`POST`/`GET` público), `RelatoAdminController.java` (`@PreAuthorize("hasRole('ADMIN')")`)
- **Propósito**: completar módulo que já tinha entity/repository/service/migration mas faltava o controller inteiro
- **Como**: nenhum DTO de resposta expõe `autorId`/apelido/e-mail, só `id`, `texto`, `nomeExibicao`, `status`, datas; rate limit de envio (5/10min por `autor_id`) já vinha do service

### Relato — incidente: relatos de teste aprovados manualmente apareceram em produção/dev
- **Onde**: `relato/service/RelatoService.java` (`listarAprovados`, linhas 44-46), `relato/controller/RelatoAdminController.java`
- **Propósito**: registrar investigação de relatos-lixo ("rwgrrwgrrwgrrw...", "grwgwerg", "teste") visíveis publicamente na home e página de relatos
- **Como**: `listarAprovados` filtra corretamente por `StatusRelato.approved` (consistente com constraint SQL em `V1__baseline.sql:123`); nenhuma migration Flyway (V1-V6) insere seed na tabela `relato`; causa raiz foi aprovação manual desses 3 relatos de teste via admin real durante teste manual do fluxo, não bug de código nem seed; correção foi `DELETE` direto no Postgres do container de dev (`brcomjogadoresanonimos-postgres-1`), tabela `relato` confirmada vazia depois — sem migration nova (não é schema change) e sem alteração de código
- **Trade-off**: pendência sinalizada ao agente `security` (não decidida): `RelatoAdminController` só tem `@PreAuthorize("hasRole('ADMIN')")` em aprovar/rejeitar/reverter/remover, sem trilha de auditoria de quem aprovou/quando e sem segunda confirmação para ações destrutivas — não causou este incidente, mas é lacuna a avaliar

### Relato — endpoint público de detalhe + payload resumido na listagem (auditoria 2026-07-17)
- **Onde**: `relato/dto/RelatoPublicoListaResponse.java`, `relato/controller/RelatoController.java` (`GET /api/v1/relatos/{id}`), `SecurityConfig.java`
- **Propósito**: listagem pública mandava texto completo (até 8000 chars) em toda página, corrigido
- **Como**: `RelatoPublicoListaResponse` traz `textoResumo` (300 chars + "..."); novo `GET /api/v1/relatos/{id}` retorna texto completo, 404 uniforme se não existe ou não está aprovado (anti-IDOR, mesmo padrão já usado no projeto); `SecurityConfig` ganhou `permitAll` novo pro path `/api/v1/relatos/*`
- **Trade-off**: endpoint admin não mudou — fila de moderação é sempre pequena, não precisa truncar

### Relato: query params em português
- **Onde**: `backend/docs/api/contrato-relatos.md`
- **Propósito**: consistência de nomenclatura com o resto do código
- **Como**: `pagina`/`tamanho`, não `page`/`size` (documentação foi ajustada para bater com o código, não o contrário)

### UnidadeFederativa (enum compartilhado)
- **Onde**: `escritorio/entity/UnidadeFederativa.java`, reaproveitado por `hotline`
- **Propósito**: evitar duplicar lista de 27 UFs entre módulos
- **Como**: módulo `hotline` referencia a classe do módulo `escritorio` diretamente, sem cópia

### UserDetailsServiceAutoConfiguration (excluído)
- **Onde**: `JogadoresAnonimosApplication.java`
- **Propósito**: eliminar warning de "generated security password" e bean `InMemoryUserDetailsManager` fantasma — projeto usa JWT 100% custom, nunca usa `UserDetailsService`/`AuthenticationProvider`
- **Como**: `@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)` em vez de criar bean no-op
- **Trade-off**: no Spring Boot 4, a classe migrou de pacote (`org.springframework.boot.security.autoconfigure`, módulo `spring-boot-security`), não é mais a do Spring Boot 3.x

### Soft-exclude de página CMS (flag `oculta_na_listagem_admin`)
- **Onde**: `cms/entity/CmsPagina.java`, `cms/service/CmsService.java`, migration `V24`
- **Propósito**: página CMS migrada para conteúdo estático no frontend não é deletada do banco (preserva FK de auditoria em `cms_alteracao_log`)
- **Como**: flag booleana usada para soft-exclude da listagem admin (`listarAdmin`)
- **Trade-off**: padrão a reaplicar sempre que página CMS for "aposentada" para hardcode, a menos que se confirme que remoção é segura (sem histórico/FK relevante)

### Testcontainers (ausente — gap de infra de teste)
- **Onde**: backend, todo o projeto — sem dependência `testcontainers` no `pom.xml`
- **Propósito**: validaria migrations Flyway contra Postgres real e descartável, sem depender de banco externo
- **Como (estado atual)**: único teste de integração com banco é `HotlineRegiaoRepositoryIT`, conecta a Postgres externo pré-existente via `application-qa-integration.properties`, porta hardcoded 5432; demais testes são unitários Mockito puro
- **Trade-off**: tentativa com porta alternativa (55432) falhou porque a config de teste ignora porta customizada; remapear para 5432 colide com o Postgres de dev do `docker-compose.yml`

### TwelveMonkeys ImageIO WebP (`com.twelvemonkeys.imageio:imageio-webp`)
- **Onde**: `backend/pom.xml` (dependência `3.11.0`); `imagem/service/ImagemProcessor.java` (`detectarTipo`, novo `TipoImagem.WEBP`)
- **Propósito**: `ImageIO` nativo do JDK não decodifica WebP; usuário pediu suporte real a upload (antes rejeitado explicitamente)
- **Como**: magic bytes RIFF/WEBP mapeados para `TipoImagem.WEBP`; validações de magic bytes, anti-polyglot, EXIF strip e proibição de SVG/upload por URL valem também para WebP, sem caminho especial
- **Trade-off**: módulo é só leitura (sem `ImageWriterSpi`) — WebP válido é reencodado para PNG na saída, mesmo pipeline dos outros formatos; monitorar advisories de CVE em updates futuros (histórico de CVE do TwelveMonkeys concentrado em módulos TIFF/PSD, não WebP)

### Validação de PDF/imagem por magic bytes, nunca extensão/Content-Type
- **Onde**: `arquivo/service/ArquivoProcessor`, `imagem/service/ImagemProcessor`
- **Propósito**: nunca confiar no que o cliente envia como tipo de arquivo
- **Como**: detecção de tipo real por assinatura binária (magic bytes); EXIF removido via reencode com `ImageIO` (descarta metadata sem lib externa); WebP passou a ser aceito via TwelveMonkeys (ver entrada própria)

### Padrão de atenção: colisão de versão Flyway
- **Onde**: `backend/src/main/resources/db/migration/`
- **Propósito**: Flyway resolve por número de versão, não por nome de arquivo — colisão só aparece em runtime/CI, não em compilação
- **Como**: sempre checar a última versão existente antes de criar uma nova; já aconteceu >=3 vezes entre agentes/sessões em paralelo (FAQ `V26`→`V28`, hotline `V26`/`V27`)

### GC de arquivo/imagem órfão: biblioteca pessoal + N+1
- **Onde**: `arquivo/service/ArquivoOrfaGcJob.java`, `arquivo/service/ArquivoService.remover()`, `arquivo/repository/ArquivoRepository`, `imagem/service/ImagemOrfaGcJob.java`, `imagem/repository/ImagemRepository`, `usuario/biblioteca/repository/UsuarioArquivoRepository`
- **Bug corrigido**: GC e remoção manual de `Arquivo` só checavam `CmsBlocoRepository.existsByArquivoId`, ignorando `UsuarioArquivoRepository` (biblioteca pessoal) — arquivo salvo por usuário podia ser apagado silenciosamente
- **Como**: query única `NOT EXISTS` (`CmsBloco` + `UsuarioArquivo`/`BannerMural` conforme o caso) direto no repository (`buscarIdsOrfaosAnterioresA`/`buscarIdsOrfaasAnterioresA`) substitui `findAll()` + filtro em Java + `existsBy` por linha; jobs agora só chamam `findAllById` sobre os ids já filtrados no banco
- **Trade-off**: `CmsBloco.arquivoId`/`imagemId` são colunas UUID diretas (não `@ManyToOne`), então o `NOT EXISTS` no JPQL usa `b.arquivoId = a.id`, não `b.arquivo.id`

### N+1 em hierarquia de escritórios regionais
- **Onde**: `escritorio/repository/RegiaoIntergrupoRepository.findAllComHierarquiaCompleta()`
- **Bug corrigido**: fetch join só cobria `regiao.estados`; `estado.grupos` e `grupo.telefones` ficavam LAZY, causando N+1 em `EscritorioRegionalService.listarEstruturaCompleta()`
- **Como**: `LEFT JOIN FETCH` encadeado (`r.estados e`, `e.grupos g`, `g.telefones`) com `SELECT DISTINCT` para evitar duplicação cartesiana entre coleções irmãs

### Bug: `JpaSystemException` em `RegiaoIntergrupoRepository` — múltiplas coleções-bag fetch-joined (Hibernate 7)
- **Onde**: `escritorio/repository/RegiaoIntergrupoRepository` (`findAllComHierarquiaCompleta`), `GrupoIntergrupo.agenda` (`@ElementCollection(EAGER)`)
- **Propósito**: `GET /api/v1/admin/escritorios-regionais` quebrava com `Could not generate fetch: RegiaoIntergrupo -> estados` — `agenda` (EAGER) colidia com `LEFT JOIN FETCH ... telefones` no mesmo nó pai; Hibernate 7 não suporta múltiplas coleções-bag fetch-joined no mesmo nó (mesmo problema de fundo do antigo `MultipleBagFetchException`, mas falha em `buildFetch` em vez de na validação)
- **Como**: query única dividida em 3 (`findAllComEstados`, `carregarGruposDosEstados`, `carregarTelefonesDosGrupos`), no máximo uma coleção-bag fetch-joined por query, orquestradas na mesma transação — Hibernate reconcilia por identidade no Persistence Context
- **Trade-off**: padrão a reaproveitar em qualquer hierarquia com múltiplas coleções-bag no projeto

### Serviço nativo `postgresql-x64-18` no Windows (não é bug do projeto)
- **Onde**: ambiente local do desenvolvedor (fora do Docker)
- **Propósito**: registrar achado para não confundir debugging futuro
- **Como**: serviço Windows escuta na porta 5432 do host, mas não conflita com o container (compose usa rede interna `postgres:5432`); só confunde quem tentar `psql localhost:5432` de fora do Docker esperando pegar o banco do compose

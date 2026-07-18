# Site da Associação Jogadores Anônimos do Brasil

## Visão geral
Site para atender com informações e suporte a jogadores compulsivos,
baseado no site americano da irmandade (https://gamblersanonymous.org/).
Muitas coisas serão copiadas do Gamblers Anonymous, mas o site brasileiro
terá algumas diferenças.
O site é feito em Angular, com backend em Spring Boot e banco em PostgreSQL.
**O anonimato do usuário deve ser preservado em todas as camadas do sistema.**

> Este arquivo é a fonte de verdade das regras de negócio do projeto.
> **Só o agente `po` pode alterá-lo** — seja a pedido direto do usuário,
> seja por sugestão do agente `aprendiz` (aprovada pelo usuário antes de
> aplicada). Ver `.claude/agents/po.md`.

## Contexto de escala (leia antes de propor arquitetura)
- Volume estimado: **~300 pedidos de ajuda/dia**, cerca de **~2.500
  requisições/dia** no total, picos ainda não medidos.
- Isso é tráfego baixo — a maioria das decisões de arquitetura deve
  favorecer simplicidade, não soluções de "grande escala" (ver agente
  `system-design`, que existe justamente para ponderar isso e evitar
  overengineering).
- Acesso é nacional (Brasil inteiro), com um volume pequeno vindo da
  África. Isso importa principalmente para CDN/latência, não para
  decisões de multi-região de banco de dados.

## Stack
- Linguagem/Framework: Java 21+, Spring Boot 3.2+, Angular 17+
- Banco de dados: PostgreSQL
- Armazenamento de imagens: disco local persistente do servidor de
  produção, isolado atrás da interface `StorageClient`
  (implementação `LocalDiskStorageClient`), não bucket S3-compatible
  externo. Ver `Decisoes de Sistemas.md`, seção 5.
- Estilização: CSS
- Testes: JUnit, Mockito
- Infra: Docker
- Outros: JWT, Swagger

## Comandos
- Subir ambiente: `docker compose up`
- Backend (dev): `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`)
- Frontend (dev): `npm start` (equivalente a `ng serve`)
- Testes backend: `./mvnw test` (Windows: `mvnw.cmd test`)
- Testes frontend: `npm test` (equivalente a `ng test`)
- Build backend: `./mvnw clean package` (Windows: `mvnw.cmd clean package`)
- Build frontend: `npm run build` (`ng build --configuration production`)

## Estrutura do projeto
- `backend/` — Spring Boot, ainda sem nada implementado
- `frontend/` — Angular, separado em componentes, serviços e pages
- `docker-compose.yml` — orquestra backend, frontend, Postgres e storage de imagens
- `.claude/agents/` — definição do time de agentes (ver seção abaixo)
- `.claude/rules/` — regras aplicadas automaticamente por caminho de arquivo (ex: design system)

## Time de agentes
Todo trabalho técnico deste projeto é feito por agentes especializados,
coordenados pelo agente `po`. Ninguém (nem o Claude "genérico" da sessão)
escreve código de produto diretamente — o pedido sempre passa pelo `po`,
que delega.

| Agente | Responsabilidade |
|---|---|
| `po` | Porta de entrada obrigatória, quebra e delega tarefas, único agente que edita este documento |
| `ux-ui` | Decide o que manter/adaptar/cortar/criar na interface, com base em UX de mercado e regra de negócio, antes do `frontend` implementar |
| `frontend` | Componentes, pages, estilos, navegação, performance de UI, acessibilidade |
| `frontend-services` | Camada de services Angular, contratos de dados do lado do cliente, mocks |
| `backend` | Spring Boot, arquitetura de dados, migrations, documentação de contrato de API (SDD) |
| `security` | Autenticação, MFA, criptografia/hash, proteção contra ataques, CORS, rate limiting, auditoria |
| `devops` | Ambientes, CI/CD, Docker/Kubernetes, nuvem, deploy/rollback, observabilidade de infra (uso vai crescer quando o deploy real começar) |
| `system-design` | Trade-offs de arquitetura (bancos, particionamento, filas, cache/CDN, gateway, serverless) — e freio contra overengineering |
| `qa-frontend` | Testes unitários e E2E do frontend Angular |
| `qa-backend-unit` | Testes unitários de backend (JUnit + Mockito, mock de dependência) |
| `qa-backend-integration` | Testes de integração/contrato de backend (Testcontainers, banco real, MockMvc) |
| `qa-system` | Testes de integração full-stack, carga, stress e resiliência do sistema completo |
| `code-reviewer` | Revisa (não edita) o trabalho dos demais agentes, cobrindo frontend/backend/security/infra |
| `aprendiz` | Registra em `.claude/knowledge/` o que o time fez/decidiu; antes de um lote de notas relacionadas, também monta a ordem de pré-requisito entre os tópicos e valida com o usuário |
| `professor` | Traduz o que o sistema aprendeu em conteúdo de estudo no vault Obsidian do usuário, no padrão dele |

Ver `.claude/agents/*.md` para a definição completa de cada um.

## Convenções de código
- Preferir interfaces a types
- No frontend, sempre que criar uma página: criar um componente e uma
  rota; se essa página tiver um formulário, criar um service para lidar
  com a API; e, caso essa página tenha muita complexidade própria, criar
  um componente adicional para isso — tudo para ajudar na manutenção e
  legibilidade do código.
- **Comentário no código é proibido, sem exceção nenhuma** (frontend,
  backend e migrations SQL). Isso inclui o caso de decisão de segurança
  ou trade-off técnico crítico e não-óbvio (ex: por que uma allowlist de
  sanitização é assim, por que um path de storage é seguro contra path
  traversal) — essa justificativa não vai mais no código-fonte, e sim em
  commit message/PR/documentação externa. Comentário só deve existir se o
  usuário pedir explicitamente naquela tarefa pontual. Preferir código
  legível por si só a comentário — inclusive se isso deixar o código
  estilisticamente "mais feio", isso é aceitável. Comentário estilo
  log/changelog (com data no formato AAAA-MM-DD, ou que narra status
  temporal, tipo "validado em", "disponível desde", "desligado em", "ver X
  se precisar voltar") permanece proibido pelo mesmo motivo: não é
  porquê técnico atemporal, é diário de bordo e pertence a commit
  message/PR, não ao código-fonte.
- **Sem linha em branco decorativa no código** (frontend TS/HTML/CSS e
  backend Java): só quebra de linha onde é sintaticamente necessária (fim
  de statement) ou dentro de string/stream de dados multi-linha. Nada de
  linha em branco só para separar blocos ou "organizar visualmente".

## Relato de tarefa dos agentes
- Resumo final de tarefa deve ser curto e direto (estilo telegráfico),
  sem prosa explicando passo a passo o que foi feito. O `aprendiz` já
  registra o conhecimento técnico em `.claude/knowledge/` — o resumo ao
  usuário/`po` só confirma o que foi entregue e aponta bloqueios/decisões
  pendentes, para reduzir gasto de token.

## Padrões de arquitetura
- Antes de criar uma nova migration Flyway, sempre conferir qual é a
  última versão (V-número) já existente em
  `backend/src/main/resources/db/migration/`, para evitar colisão de
  número quando agentes trabalham em paralelo (já aconteceu no projeto).
- Reservar explicitamente o próximo número de migration assim que a
  tarefa começar (ex: criar o arquivo vazio com o nome final logo no
  início do trabalho), não só conferir o último V-número — colisão entre
  agentes trabalhando em paralelo já aconteceu ~3 vezes neste projeto.
- Teste de integração de backend pode usar `@DataJpaTest`/configuração de
  teste padrão do Spring; Testcontainers (PostgreSQL efêmero, porta
  dinâmica) não é mais obrigatório — considerar quando fizer sentido
  (ex: comportamento que dependa de Postgres real), evitando
  overengineering dado o volume baixo do projeto (ver "Contexto de
  escala"). Se usar Postgres externo, nunca depender de porta fixa (ex:
  5432), que colide com o Postgres de desenvolvimento do
  `docker-compose.yml`.
- Quando migrations Flyway forem consolidadas, o resultado final deve
  ser sempre exatamente 2 arquivos: um `V1` só de schema (DDL —
  tabelas, índices, constraints) e um `V2` só de conteúdo/seed (DML —
  INSERT de dado). Nunca um único arquivo misturando schema e seed,
  nunca mais que esses 2 arquivos.
- Sempre delegue as tarefas de frontend, frontend-services, backend,
  security e devops para o agente correspondente — não misture
  responsabilidades entre eles.
- Não deixe de usar os agentes; trabalhar sem agente é exceção, só para
  casos em que nenhum agente existente cobre a tarefa (e nesse caso, o
  `po` deve sinalizar isso e propor um novo agente antes de prosseguir).
- Pode delegar mais de uma instância do mesmo tipo de agente em paralelo
  quando a tarefa for grande (ex: duas ou três tarefas de frontend).
- Contratos de API são documentados **antes** de implementar (ver seção
  SDD no agente `backend`), combinados entre `backend` e
  `frontend-services` para evitar retrabalho.
- Anonimato é requisito de arquitetura, não só de UI: nunca logar,
  persistir ou expor dados que permitam identificar um usuário (nome
  real, IP, e-mail pessoal) junto com dados de participação ou relatos.
  Em caso de conflito entre uma prática técnica comum (ex: logar IP para
  rate limiting) e esse requisito, o anonimato tem prioridade — buscar a
  alternativa (ex: identificador de sessão não reversível).
- Autenticação: login por apelido + senha (Argon2id, mínimo 10
  caracteres, sem regra de composição forçada); login aceita apelido OU
  e-mail. **E-mail é coletado no cadastro — exceção deliberada à regra
  geral de anonimato deste documento**, formalizada para (1) recuperação
  de conta e (2) envio de itens físicos da loja; isolado por regra de
  aplicação (nunca em query/DTO/log de relato, presença ou reunião). MFA
  via TOTP para admin está planejado, ainda pendente de implementação.
  Ver `Decisoes de Sistemas.md`, seção 4.
- Uploads de imagem devem ter metadados EXIF removidos antes de salvar
  (para não vazar geolocalização/dispositivo do usuário), validação por
  magic bytes (não extensão/`Content-Type`), sem aceitar SVG. Storage é
  disco local (ver seção Stack acima); GC de imagem órfã e rate limit de
  upload já implementados. Ver `Decisoes de Sistemas.md`, seção 5.
- Respostas de API sempre no formato `{ success, data, error }`.
- Logs de aplicação não podem conter dados pessoais, nem em nível debug.
- Tokens de autenticação, senhas e dados sensíveis nunca trafegam em
  query string / método GET — sempre em header ou corpo de requisição.
- Variáveis sensíveis (senhas de banco, chaves JWT, credenciais de
  storage) ficam em `.env` / secrets do Docker — nunca hardcoded ou
  commitadas.
- Não pedir permissão para utilizar tools ou escrever quando eu pedir
  alterações no backend ou frontend, mas sempre documentar a decisão
  tomada e o motivo.
- Não faça commit de nada, apenas escreva o código e explique a decisão
  tomada, caso seja necessário, e aguarde instruções para commitar.
  **Isso é uma regra indiscutível do projeto.**

## Privacidade e conformidade
- Site deve seguir a LGPD (Lei Geral de Proteção de Dados).
- Sem analytics de terceiros que rastreiem usuário individualmente.
- Definir política de retenção de dados sensíveis (relatos, presença em
  reuniões, etc) e quem tem acesso a eles — inclui retenção de logs de
  infraestrutura (agente `devops`) e eventos de auditoria de segurança
  (agente `security`).

## O que NÃO fazer
- Não commitar a não ser que eu peça
- Não commitar direto na branch main
- Não adicionar dependências novas sem perguntar
- Não implementar nenhuma feature de rastreamento/analytics sem
  confirmar antes
- Não armazenar dados que permitam reidentificação de usuários
- Não commitar `.env`, credenciais de banco ou chaves de storage de
  imagem
- Não escrever código de produto fora de um agente especializado

## Contexto adicional
- Referência funcional: https://gamblersanonymous.org/
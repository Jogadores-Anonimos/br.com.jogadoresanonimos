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
- Armazenamento de imagens: [definir — ex: MinIO self-hosted ou S3-compatible]
- Estilização: CSS
- Testes: JUnit, Mockito
- Infra: Docker
- Outros: JWT, Swagger

## Comandos
- Subir ambiente: `docker compose up`
- Backend (dev): [definir, ex: `./mvnw spring-boot:run`]
- Frontend (dev): [definir, ex: `ng serve`]
- Testes backend: [definir, ex: `./mvnw test`]
- Testes frontend: [definir, ex: `ng test`]
- Build: [definir]

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
| `qa-backend` | Testes unitários e de integração do backend (com banco real em container) |
| `qa-system` | Testes de integração full-stack, carga, stress e resiliência do sistema completo |
| `code-reviewer` | Revisa (não edita) o trabalho dos demais agentes, cobrindo frontend/backend/security/infra |
| `aprendiz` | Registra em `.claude/knowledge/` o que o time fez/decidiu, para não perder contexto entre sessões |
| `professor` | Traduz o que o sistema aprendeu em conteúdo de estudo no Notion do usuário, no padrão dele |

Ver `.claude/agents/*.md` para a definição completa de cada um.

## Convenções de código
- Preferir interfaces a types
- No frontend, sempre que criar uma página: criar um componente e uma
  rota; se essa página tiver um formulário, criar um service para lidar
  com a API; e, caso essa página tenha muita complexidade própria, criar
  um componente adicional para isso — tudo para ajudar na manutenção e
  legibilidade do código.

## Padrões de arquitetura
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
- Autenticação: [definir modelo — ex: apelido + senha, sem exigir e-mail
  verificado; MFA opcional para funções administrativas, ver agente
  `security`]
- Uploads de imagem devem ter metadados EXIF removidos antes de salvar
  (para não vazar geolocalização/dispositivo do usuário). O bucket usado,
  a política de expurgo e as regras de upload são definidas junto aos
  agentes `backend`/`devops`/`security` — documentar aqui a decisão final
  quando fechada.
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
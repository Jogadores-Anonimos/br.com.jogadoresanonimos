# Site da Associação Jogadores Anônimos do Brasil

## Visão geral
Site para atender com informações e suporte a jogadores compulsivos,
baseado no site americano da irmandade (https://gamblersanonymous.org/).
O site é feito em Angular, com backend em Spring Boot e banco em PostgreSQL.
**O anonimato do usuário deve ser preservado em todas as camadas do sistema.**

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

## Convenções de código
- Preferir interfaces a types
- No frontend sempre que criar uma pagina, criar um componente e uma rota,
  se essa pagina tiver um formulário, criar um service para lidar com a API, 
  e caso essa pagina tenha outra, crie um component para isso, para ajudar na manutenção e legibilidade do código.

## Padrões de arquitetura
- Anonimato é requisito de arquitetura, não só de UI: nunca logar, persistir
  ou expor dados que permitam identificar um usuário (nome real, IP, e-mail
  pessoal) junto com dados de participação ou relatos.
- Autenticação: [definir modelo — ex: apelido + senha, sem exigir e-mail
  verificado]
- Uploads de imagem devem ter metadados EXIF removidos antes de salvar
  (para não vazar geolocalização/dispositivo do usuário). O time de storage
  de imagens vai evoluir essa parte — documentar aqui a decisão final quando
  fechada (bucket usado, política de expurgo, regras de upload).
- Respostas de API sempre no formato `{ success, data, error }`.
- Logs de aplicação não podem conter dados pessoais, nem em nível debug.
- Variáveis sensíveis (senhas de banco, chaves JWT, credenciais de storage)
  ficam em `.env` / secrets do Docker — nunca hardcoded ou commitadas.

## Privacidade e conformidade
- Site deve seguir a LGPD (Lei Geral de Proteção de Dados).
- Sem analytics de terceiros que rastreiem usuário individualmente.
- Definir política de retenção de dados sensíveis (relatos, presença em
  reuniões, etc) e quem tem acesso a eles.

## O que NÃO fazer
- Não commitar direto na branch main
- Não adicionar dependências novas sem perguntar
- Não implementar nenhuma feature de rastreamento/analytics sem confirmar antes
- Não armazenar dados que permitam reidentificação de usuários
- Não commitar `.env`, credenciais de banco ou chaves de storage de imagem

## Contexto adicional
- Referência funcional: https://gamblersanonymous.org/
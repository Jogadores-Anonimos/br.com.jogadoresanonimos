# Jogadores Anônimos do Brasil

![Angular](https://img.shields.io/badge/Angular-DD0031?style=flat&logo=angular&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?style=flat&logo=typescript&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=flat&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white)
![Vercel](https://img.shields.io/badge/Vercel-000000?style=flat&logo=vercel&logoColor=white)
![Cloudflare](https://img.shields.io/badge/Cloudflare-F38020?style=flat&logo=cloudflare&logoColor=white)

Site institucional e sistema de apoio para pessoas com problemas de jogo
compulsivo, familiares e interessados na irmandade Jogadores Anônimos.
Baseado no site americano de referência
([gamblersanonymous.org](https://gamblersanonymous.org/)), adaptado às
necessidades específicas da associação brasileira.

Além do conteúdo institucional (programa de recuperação, reuniões,
literatura, perguntas frequentes), o site oferece cadastro de usuário,
relatos de recuperação, biblioteca de conteúdo, localização de grupos
presenciais e um painel administrativo para gestão de todo o conteúdo
público. **O anonimato do usuário é preservado em todas as camadas do
sistema**: é requisito de arquitetura, não só de interface.

## Stack

| Camada | Tecnologia |
|---|---|
| Frontend | Angular 21, TypeScript, CSS |
| Backend | Spring Boot 3.2+, Java 21 |
| Banco de dados | PostgreSQL 16 |
| Autenticação | JWT + Argon2id + MFA/TOTP (admin) |
| Infraestrutura | Docker (Compose em dev, Swarm em produção) |

## Estrutura do projeto

```
backend/             Spring Boot: controllers, services, repositories,
                      entidades JPA, migrations Flyway
frontend/             Angular: components, pages, services
docker-compose.yml    Orquestração local de dev (backend + frontend + Postgres)
stack.prod.yml        Stack de produção (Docker Swarm)
```

## Como rodar localmente

### Pré-requisitos

- Docker e Docker Compose
- Java 21+ e Maven (só se for rodar o backend fora do container)
- Node.js 20+ e npm (só se for rodar o frontend fora do container)

### Variáveis de ambiente

Antes de subir o projeto, crie um arquivo `.env` na raiz do repositório
com as variáveis abaixo. Nenhuma delas vem com valor padrão: a
aplicação falha ao iniciar sem elas (fail-fast), propositalmente, para
não mascarar configuração ausente.

```
DB_NAME=jogadoresanonimos
DB_USERNAME=jogadoresanonimos
DB_PASSWORD=
DB_POOL_MAX=10
DB_POOL_MIN=5

JWT_SECRET=
JWT_TTL_MINUTES=60
LOG_HMAC_SECRET=
MFA_SECRET_ENCRYPTION_KEY=

CORS_ALLOWED_ORIGINS=http://localhost:4200
FRONTEND_URL=http://localhost:4200

RECAPTCHA_SECRET_KEY=
RESEND_API_KEY=

STORAGE_STUB_DIR=/app/data/imagens
STORAGE_PUBLIC_BASE_URL=http://localhost:8080/api/v1/imagens

ADMIN_BOOTSTRAP_APELIDO=
ADMIN_BOOTSTRAP_EMAIL=
ADMIN_BOOTSTRAP_SENHA=
ADMIN_LIMITE_MAXIMO=5
```

Notas:
- `JWT_SECRET`, `LOG_HMAC_SECRET` e `MFA_SECRET_ENCRYPTION_KEY` devem ser
  strings aleatórias longas (ex: `openssl rand -base64 32`).
- `RECAPTCHA_SECRET_KEY` exige uma chave do Google reCAPTCHA (v2/v3,
  conforme configurado no frontend) para o par de domínio de teste
  `localhost`.
- `RESEND_API_KEY` é usada para envio de e-mail (recuperação de conta);
  sem uma chave válida, esses fluxos específicos não completam, mas o
  restante da aplicação sobe normalmente.
- As credenciais `ADMIN_BOOTSTRAP_*` criam o primeiro usuário
  administrador no primeiro boot da aplicação.

### Subir o ambiente completo

```bash
docker compose up
```

Sobe backend (`:8080`), frontend (`:4200`) e Postgres (`:5432`), com hot
reload nos dois lados (volume de código montado nos containers).

### Rodar backend e frontend isolados (fora do Docker)

```bash
# backend (na pasta backend/, precisa de Postgres acessível via DB_URL)
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run

# frontend (na pasta frontend/)
npm install
npm start                     # equivalente a `ng serve`
```

### Rodar os testes

```bash
# backend (na pasta backend/)
./mvnw test                   # Windows: mvnw.cmd test

# frontend (na pasta frontend/)
npm test                      # equivalente a `ng test`
```

Teste de integração de backend exige Postgres acessível localmente
(mesma variável `DB_PASSWORD`/configuração de conexão do `.env`); sem
isso, o contexto Spring de teste não sobe.

## Arquitetura e infraestrutura de produção

Arquitetura de aplicação: **monolito Spring Boot único** + **Postgres
único**, sem sharding, cache distribuído ou fila de mensagens: decisão
deliberada dado o volume real do projeto (baixas milhares de requisições
por dia). Ver `Decisoes de Sistemas.md` para o racional completo e os
gatilhos que forçariam revisão dessa decisão.

**Frontend**: deploy no [Vercel](https://vercel.com), com deploy
automático a cada push (build estático do Angular). CSP e headers de
segurança (`X-Frame-Options`, `X-Content-Type-Options`) configurados em
`frontend/vercel.json`.

**Backend + banco de dados**: rodam em VPS próprio, orquestrados via
**Docker Swarm** (`stack.prod.yml`, `docker stack deploy`). Principais
pontos do desenho:

- **Traefik** como reverse proxy: roteia `api.jogadoresanonimos.com.br`
  para o serviço de backend e emite/renova certificado TLS automaticamente
  via Let's Encrypt (HTTP challenge). Redirect HTTP → HTTPS ativo.
- **Redes isoladas**: o backend fica na rede overlay pública (a mesma que
  o Traefik usa para rotear) e em uma rede overlay interna própria,
  compartilhada só com o Postgres. O Postgres nunca é exposto à rede
  pública nem tem porta publicada: só é alcançável pelo backend.
- **Cloudflare** na frente do domínio da API, para proteção
  DDoS/WAF básica e cache de assets estáticos servidos pelo backend
  (ex: imagens do storage local).
- **Segredos via Docker Swarm Secrets nativo** (`db_password`,
  `jwt_secret`, `log_hmac_secret`, `mfa_secret_encryption_key`,
  `recaptcha_secret_key`, `resend_api_key`, `admin_bootstrap_senha`),
  nunca como variável de ambiente em texto claro no stack. Um
  `EnvironmentPostProcessor` do Spring Boot lê os arquivos montados em
  `/run/secrets/` e os injeta como propriedade de configuração,
  preservando os mesmos nomes de variável usados no restante do código
  em dev.
- **Storage de imagem/PDF**: disco local persistente do servidor
  (`StorageClient`/`LocalDiskStorageClient`), não bucket externo. Ver
  `Decisoes de Sistemas.md`, seção 5, para o trade-off completo.
- **Backup automático diário cifrado**: dump do Postgres e diretório de
  storage de imagem, com retenção definida e restauração testada em
  conjunto (backup do banco e do disco de imagem não podem ficar
  defasados entre si).
- **Réplica única** de backend e de Postgres (`replicas: 1`), coerente
  com o volume atual e com o node único do VPS; múltiplas réplicas de
  Postgres nunca são levantadas (dado não replicado). Deploy usa
  `update_config.order: start-first` com rollback automático em caso de
  falha de healthcheck.
- **Observabilidade**: Spring Boot Actuator (`/health`, `/metrics`,
  `/httpexchanges`) restrito a acesso interno/admin, mais log estruturado
  local, sem enviar dado operacional a um APM de terceiro (decisão
  deliberada de soberania de dado, ver `Decisoes de Sistemas.md`, seção
  12-B).

Este README não cobre o passo a passo operacional de deploy/rollback no
VPS (comandos exatos, paths do servidor): isso é documentação interna de
operação, não deste repositório.

## Segurança

- Hash de senha com Argon2id (mínimo 10 caracteres, sem regra de
  composição forçada); login por apelido ou e-mail.
- MFA via TOTP para contas administrativas.
- RBAC via Spring Security; toda rota `/api/v1/admin/**` restrita a
  `ROLE_ADMIN`, verificado sempre no backend.
- Rate limiting (token bucket) em endpoints sensíveis, chaveado por
  identificador técnico de usuário/sessão, nunca por IP.
- Upload de imagem/PDF validado por magic bytes (não extensão/
  `Content-Type`), com remoção de metadado EXIF e checagem anti-polyglot
  em PDF.
- Nenhum log ou rate limiter usa IP ou dado pessoal identificável.

Detalhamento completo em `Decisoes de Sistemas.md`.

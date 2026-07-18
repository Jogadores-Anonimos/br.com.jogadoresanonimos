# Jogadores Anônimos do Brasil

Site institucional e de apoio para pessoas com problemas de jogo compulsivo,
familiares e interessados na irmandade Jogadores Anônimos. Baseado no site
americano de referência ([gamblersanonymous.org](https://gamblersanonymous.org/)),
adaptado às necessidades específicas da associação brasileira.

**O anonimato do usuário é preservado em todas as camadas do sistema.**

## Stack

| Camada | Tecnologia |
|---|---|
| Frontend | Angular 21, TypeScript, CSS |
| Backend | Spring Boot 3.2+, Java 21 |
| Banco de dados | PostgreSQL 16 |
| Autenticação | JWT + Argon2id + MFA/TOTP |
| Infraestrutura | Docker Compose |

## Dashboard do projeto

### Código

| Métrica | Backend (Java) | Frontend (TypeScript) |
|---|---:|---:|
| Linhas de código (produto) | 8.824 | 10.402 |
| Linhas de teste | 5.460 | 2.329 |
| Linhas de template/estilo | — | HTML 5.954 / CSS 10.404 |
| Arquivos de teste | 43 | 12 |
| Casos de teste | 328 | 138 |

### Estrutura

| Item | Quantidade |
|---|---:|
| Páginas (rotas) | 26 |
| Componentes reutilizáveis | 7 |
| Services (frontend) | 18 |
| Services (backend) | 22 |
| Repositories (backend) | 23 |
| Entidades JPA | 27 |
| Controllers REST | 30 |
| Endpoints (rotas HTTP) | 103 |
| Tabelas no banco | 26 |
| Migrations Flyway | 2 (`V1` schema, `V2` conteúdo seed) |

### Conteúdo real cadastrado

| Item | Quantidade |
|---|---:|
| Páginas institucionais (CMS) | 10 |
| Grupos de reunião presencial / endereços | 69 |
| Reflexões diárias (365 dias + 29/02) | 366 |
| Relatos de recuperação | 13 |
| Perguntas frequentes | 9 |
| Itens no Mural de Apoio | 6 (5 imagens + 1 vídeo) |
| Regiões / Estados no hierarquia de grupos | 5 / 22 |

## Escalabilidade — estado atual

**Arquitetura atual**: monolito Spring Boot único, banco PostgreSQL único
(sem sharding/particionamento), storage de imagens/PDFs em disco local do
próprio servidor de produção via interface `StorageClient`
(`LocalDiskStorageClient`). Uma única instância de backend, sem load
balancer, sem cache distribuído, sem fila de mensagens — decisão deliberada
dado o volume real do projeto (~300 pedidos de ajuda/dia, ~2.500
requisições/dia).

**Capacidade sem mudança estrutural**: essa arquitetura atende
confortavelmente o volume atual e tem folga para crescer uma ordem de
grandeza (dezenas de milhares de requisições/dia) sem exigir revisão — um
único Postgres com pool de conexões modesto e uma instância de aplicação
bem dimensionada aguentam esse patamar sem dificuldade. Estimativa
conservadora, não testada sob carga real.

**Gatilhos que forçariam revisão estrutural**:
- Necessidade de mais de uma instância de backend simultânea (alta
  disponibilidade ou crescimento real de tráfego) — nesse momento o
  storage local deixa de ser viável (instâncias não compartilham disco) e
  a decisão precisa migrar para storage compartilhado antes do deploy
  multi-instância, não depois.
- Volume de escrita no Postgres crescendo ordens de grandeza a ponto de
  um único servidor de banco não sustentar — hoje não há indício disso.
- Padrão de acesso internacional mudando de forma relevante (ex: expansão
  para outro país) — reabriria discussão de latência/CDN multi-região.

**Pontos já preparados para escalar sem retrabalho grande**:
- Interface `StorageClient` isola a decisão de storage do restante do
  código — trocar disco local por S3-compatible é troca de implementação,
  não de contrato usado pelos serviços de domínio.
- Rate limiting via `RateLimiterService`/`TokenBucket` já em produção,
  absorve picos de abuso sem infraestrutura extra.
- Camadas Controller/Service/Repository bem separadas facilitam extrair
  um domínio para serviço próprio no futuro, se algum dia necessário (não
  previsto hoje).
- Backup de disco de storage já desenhado para acompanhar o backup do
  Postgres, evitando dívida técnica de durabilidade quando o volume
  crescer.

## Segurança

**Autenticação**
- Hash de senha com Argon2id (mínimo 10 caracteres, sem regra de
  composição forçada)
- MFA via TOTP para contas administrativas, secret criptografado em
  repouso (AES-GCM), fluxo enroll/confirmar/desativar (desativar exige
  senha + código TOTP)
- Cadastro por apelido + senha; e-mail coletado só como exceção
  deliberada ao anonimato (recuperação de conta/envio de itens da loja),
  nunca exposto junto de dado de participação

**Autorização**
- RBAC via Spring Security, rotas `/api/v1/admin/**` restritas a
  `ROLE_ADMIN`
- Endpoints de recurso próprio (relatos, biblioteca pessoal) resolvem o
  dono a partir do usuário autenticado no token, nunca de
  parâmetro/payload — mitiga IDOR (404, não 403, pra recurso de outro
  dono)
- Deny-by-default: só rotas GET públicas explicitamente listadas são
  liberadas

**Proteção contra ataques comuns**
- SQL Injection: acesso a dados via JPA/Specification (prepared
  statements), zero query nativa concatenada
- Rate limiting (token bucket) em endpoints sensíveis, chaveado por
  identificador técnico de usuário/sessão — nunca por IP
- XSS armazenado: allowlist rígida de tags/atributos no CMS
  (`HtmlSanitizer`), nunca HTML livre
- CORS com allowlist explícita de origem (sem `*`)
- CSRF não aplicável — API stateless com JWT em header, sem cookie de
  sessão

**Anonimato / LGPD**
- Nenhum log ou rate limiter usa IP ou dado identificador pessoal —
  chaves técnicas (UUID/hash HMAC) apenas
- Logs de auditoria administrativa nunca registram conteúdo de
  relato/participante, só a ação e o id técnico do recurso
- Erros expostos ao cliente não incluem stack trace, SQL ou detalhe de
  implementação (`GlobalExceptionHandler`)

**Upload de arquivo**
- Chave de armazenamento gerada por UUID, nunca o nome original do
  arquivo (evita path traversal e colisão)
- Validação por magic bytes (não extensão/`Content-Type`), sem SVG, sem
  upload por URL
- Remoção de metadado EXIF via reencode (`ImageIO`) antes de persistir
  imagem
- PDF: checagem anti-polyglot e bloqueio de `/JavaScript`, `/OpenAction`,
  `/EmbeddedFile`, `/Launch`
- GC diário de arquivo/imagem órfã (período de graça de 7 dias)

**Sessão / token**
- JWT stateless, TTL curto
- Denylist de JWT no logout, com job de limpeza periódica das entradas
  expiradas
- Token sempre via header `Authorization`, nunca em query string

## Testes

- Backend: 328 casos de teste (unitário com Mockito + integração), cobrindo
  auth/MFA, CMS, relatos, auditoria, upload, rate limiting.
- Frontend: 138 casos de teste (Vitest), cobrindo services, componentes de
  página e fluxos administrativos.
- Auditoria de performance e segurança rodada em 2026-07-17, sem achado
  crítico pendente.

## Comandos

```bash
docker compose up          # sobe backend + frontend + Postgres
./mvnw spring-boot:run     # backend isolado (dev)
npm start                  # frontend isolado (dev)
./mvnw test                # testes backend
npm test                   # testes frontend
```
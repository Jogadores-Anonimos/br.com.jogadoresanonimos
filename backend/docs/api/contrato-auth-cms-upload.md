# Contrato de API — Auth/E-mail, CMS Leve, Upload de Imagem e de PDF

> Status: **implementado em código em 2026-07-12** (blocos 1, 2, 3 e 4 —
> ver `backend/src/main/java/br/com/jogadoresanonimos/{auth,usuario,cms,imagem,arquivo}`).
> Baseado em `Decisoes de Sistemas.md` (seções 2, 4, 5, 6, 8, 9, 10) e
> `.claude/knowledge/seguranca-email-cadastro.md`.
> Shape dos DTOs não mudou em relação ao que `frontend-services` já
> mockou — nenhuma mudança breaking.
>
> **Atualização 2026-07-12**: decisão de storage revista — produção usa
> **disco local persistente**, não bucket S3-compatible externo (R2/B2
> descartado, ver seção "Decisão revista — storage local" abaixo).
> `StubStorageClient` foi promovido a `LocalDiskStorageClient`, mesma
> interface `StorageClient`, sem mudança de contrato de API.
>
> **Pendências externas para rodar de verdade** (ver resumo da sessão):
> Postgres real (dev/prod), `JWT_SECRET`/`LOG_HMAC_SECRET`, volume de disco
> persistente montado em produção para `LocalDiskStorageClient`, servidor
> SMTP/API de e-mail (hoje `EmailService` só loga, não envia de verdade).

Convenções gerais válidas para todos os blocos abaixo:
- Envelope de resposta sempre `{ success, data, error }` (ver seção 9 do
  doc de decisões para o formato de `error`).
- Todos os paths sob `/api/v1`.
- Nenhum endpoint aceita token/identificador sensível em query string —
  sempre header (`Authorization: Bearer <jwt>`) ou corpo.
- `traceId` (UUID por requisição) presente em toda resposta de erro,
  sem relação com identidade do usuário.
- Datas em ISO-8601 UTC (`2026-07-12T14:30:00Z`).

---

## Bloco 1 — Autenticação / E-mail

### 1.1 `POST /api/v1/auth/registro`
- **Autenticação**: nenhuma (rota pública).
- **Idempotente**: não (cria recurso; retries duplicados batem em 409).
- **Rate limit**: sim — token bucket por IP não se aplica (anonimato);
  usar bucket por combinação normalizada de apelido+email tentados (ver
  seção `security`), mais um limite global de criação de conta por
  janela curta para conter abuso em massa.
- **Consistência**: forte — grava direto no primário, síncrono.

**Request** (`RegistroRequest`):
```json
{
  "apelido": "string, 3-30 chars, sem espaço, único",
  "email": "string, formato e-mail válido, único",
  "senha": "string, mínimo 10 chars",
  "confirmarSenha": "string, deve bater com senha"
}
```

**Response 201** (`UsuarioResponse`):
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "apelido": "string",
    "role": "PARTICIPANTE",
    "criadoEm": "2026-07-12T14:30:00Z"
  },
  "error": null
}
```
Nunca retorna `email` no corpo de resposta do registro (mesma regra de
`/usuarios/me`, ver 1.5) — evita ecoar dado sensível desnecessariamente
na própria confirmação de cadastro.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | senha curta, `senha != confirmarSenha`, e-mail mal formado, apelido com espaço/caracteres inválidos — `error.fields` lista o campo |
| 409 | `CONFLICT` | apelido ou e-mail já cadastrado — mensagem genérica **não diz qual dos dois** colidiu (evita enumeration; ver nota abaixo) |
| 429 | `RATE_LIMITED` | limite de criação de conta na janela |

> Nota de anti-enumeration: mensagem de 409 é
> `"Não foi possível concluir o cadastro com os dados informados"`, sem
> especificar apelido vs. e-mail. Isso é uma escolha mais conservadora
> que a UX usual (que costuma dizer "apelido já existe"); documentado
> aqui porque é uma decisão deliberada de segurança, não descuido — a
> UX pode reavaliar com `ux-ui` se o custo de fricção for considerado
> alto, mas a decisão de segurança teria que ser revista primeiro.

---

### 1.2 `POST /api/v1/auth/login`
- **Autenticação**: nenhuma (rota pública).
- **Idempotente**: sim (não muda estado em caso de sucesso, exceto
  contadores de rate-limit/tentativa).
- **Rate limit**: sim — bucket por `usuario_id` resolvido internamente
  **e** bucket por identificador bruto normalizado (lowercase/trim),
  cobrindo tanto apelido quanto e-mail como o mesmo alvo lógico (ver
  seção 4 do knowledge de e-mail).
- **Consistência**: forte (leitura do primário — login não tolera
  replication lag, senão pode autenticar com senha já trocada/revogada).

**Request** (`LoginRequest`):
```json
{
  "identificador": "string, apelido OU e-mail",
  "senha": "string"
}
```

**Response 200** (`LoginResponse`):
```json
{
  "success": true,
  "data": {
    "token": "jwt string",
    "expiraEm": "2026-07-12T15:30:00Z",
    "usuario": {
      "id": "uuid",
      "apelido": "string",
      "role": "PARTICIPANTE"
    }
  },
  "error": null
}
```

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | corpo mal formado (campo ausente) |
| 401 | `UNAUTHENTICATED` | identificador ou senha inválidos — mensagem genérica única, nunca diz qual campo errou nem se a conta existe |
| 429 | `RATE_LIMITED` | muitas tentativas — inclui header `Retry-After` |

Query de resolução no backend: `WHERE apelido = :id OR email = :id`
(ou duas consultas separadas, decisão de implementação do `backend`) —
contrato de API é o mesmo independente da estratégia SQL escolhida.

---

### 1.3 `POST /api/v1/auth/esqueci-senha`
- **Autenticação**: nenhuma (rota pública).
- **Idempotente**: sim (múltiplas chamadas com mesmo identificador não
  criam efeito colateral cumulativo prejudicial — cada chamada gera um
  novo token de reset, invalidando implicitamente o anterior).
- **Rate limit**: sim — mesmo bucket duplo (usuario_id resolvido +
  identificador bruto normalizado) do login, para não virar canal de
  enumeration nem de spam de e-mail.
- **Consistência**: forte na escrita do token; o disparo do e-mail em
  si pode ser assíncrono (fila) sem prejuízo — usuário não espera o
  e-mail chegar na mesma tela, só espera a resposta HTTP.

**Request**:
```json
{ "identificador": "string, apelido OU e-mail" }
```

**Response 200 (sempre, exista ou não a conta)**:
```json
{
  "success": true,
  "data": {
    "mensagem": "Se o apelido ou e-mail existir em nossa base, enviaremos um link de redefinição."
  },
  "error": null
}
```
Nunca retorna 404 nem diferencia conta existente/inexistente — regra
de anti-enumeration já estabelecida no projeto.

**Erros**: só `429 RATE_LIMITED` e `400 VALIDATION_ERROR` (corpo ausente)
chegam a existir; qualquer outra falha de negócio é absorvida no 200
genérico acima.

**Efeito colateral assíncrono documentado**: geração do token de reset
(hash armazenado, TTL ~30min) é síncrona; envio do e-mail é enfileirado.
Falha de envio (provedor de e-mail fora do ar) não deve derrubar a
resposta HTTP — log operacional registra falha de entrega (sem conteúdo
do e-mail, sem endereço em texto puro) para retry manual/automático;
consumidor da fila deve ser idempotente (reenviar o mesmo link não gera
novo token, usa o já persistido enquanto válido).

---

### 1.4 `POST /api/v1/auth/redefinir-senha`
- **Autenticação**: nenhuma (rota pública; a prova de identidade é o
  token de reset, não uma sessão).
- **Idempotente**: não (primeira chamada válida consome o token; chamada
  repetida com o mesmo token falha com 400/409).
- **Rate limit**: sim, por token/IP-anonimizado — baixo, poucas
  tentativas por token.
- **Consistência**: forte, síncrona.

**Request**:
```json
{
  "token": "string, token de reset recebido por e-mail",
  "novaSenha": "string, mínimo 10 chars",
  "confirmarNovaSenha": "string, deve bater com novaSenha"
}
```

**Response 200**:
```json
{
  "success": true,
  "data": { "mensagem": "Senha redefinida com sucesso." },
  "error": null
}
```

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | senha curta ou `novaSenha != confirmarNovaSenha` |
| 401 | `UNAUTHENTICATED` | token inválido, expirado ou já utilizado — mensagem genérica ("link inválido ou expirado"), sem detalhar qual dos três |
| 429 | `RATE_LIMITED` | excesso de tentativas para o mesmo token |

Ao redefinir com sucesso, todas as sessões JWT ativas do usuário devem
ser consideradas revogadas (estratégia de revogação a alinhar com
`security` — ex: campo `senha_alterada_em` na claim/checagem do JWT).

---

### 1.5 `GET /api/v1/usuarios/me`
- **Autenticação**: obrigatória (JWT válido).
- **Autorização**: qualquer usuário autenticado vê só os próprios dados
  (não há parâmetro de id — sempre o usuário do token).
- **Idempotente**: sim (leitura).
- **Consistência**: forte — leitura do primário, nunca de réplica,
  porque é a tela de "meus dados" logo após ações como registro/edição;
  usuário não pode ver dado desatualizado do próprio perfil.

**Response 200** (`UsuarioMeResponse`):
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "apelido": "string",
    "email": "string",
    "role": "PARTICIPANTE",
    "criadoEm": "2026-07-12T14:30:00Z"
  },
  "error": null
}
```
Este é o **único** endpoint de leitura que expõe `email` — projeção
(DTO) exclusiva para este caso, nunca reaproveitada em outro endpoint
que liste usuários (ex: se no futuro existir listagem de participantes
para admin, ela usa outro DTO sem `email`).

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 401 | `UNAUTHENTICATED` | token ausente/inválido/expirado |

### Migration proposta — Bloco 1
```sql
-- V{n}__adiciona_email_usuario.sql
ALTER TABLE usuario ADD COLUMN email VARCHAR(255);
-- passo intermediário (expand): permite NULL para não quebrar linhas
-- existentes; backfill obrigatório antes do passo de contract abaixo
-- se já houver usuários em produção sem e-mail coletado.
```
```sql
-- V{n+1}__email_usuario_not_null_unique.sql (contract, após backfill)
ALTER TABLE usuario ALTER COLUMN email SET NOT NULL;
ALTER TABLE usuario ADD CONSTRAINT uq_usuario_email UNIQUE (email);
```
`UNIQUE` já cria índice B-tree automático em Postgres — cobre tanto a
constraint de unicidade quanto a busca `WHERE email = :id` do login, sem
necessidade de índice manual adicional.

Se o projeto ainda não tem nenhum usuário em produção (fase de
protótipo, ainda sem dado real), as duas migrations acima podem ser
colapsadas em uma só (`email VARCHAR(255) NOT NULL UNIQUE`) direto —
decisão a confirmar com o time antes de escrever, já que "não editar
migration aplicada" só importa se já houve apply real em algum ambiente
compartilhado.

---

## Bloco 2 — CMS Leve

Todas as rotas de escrita deste bloco: `ROLE_ADMIN` obrigatório,
`@PreAuthorize`, sob prefixo `/api/v1/admin/cms/...`. Leitura pública
(consumida pelas páginas institucionais do site) fica em
`/api/v1/cms/...`, sem autenticação.

### 2.1 `GET /api/v1/cms/paginas/{slug}` (pública)
- **Autenticação**: nenhuma.
- **Idempotente**: sim.
- **Consistência**: pode tolerar poucos segundos de atraso — página
  institucional não muda com frequência; **candidata a cache
  (ex: cache HTTP/CDN com invalidação curta, ou cache em memória com TTL
  curto)** em vez de sempre bater no banco. Decisão de qual mecanismo de
  cache usar fica com `system-design`/`devops` quando houver necessidade
  real; contrato aqui não impõe.

**Response 200** (`CmsPaginaPublicaResponse`):
```json
{
  "success": true,
  "data": {
    "slug": "sobre-nos",
    "titulo": "Sobre Nós",
    "blocos": [
      { "ordem": 1, "tipo": "texto", "conteudo": "<p>texto com <b>negrito</b> e <a href=\"...\">link</a></p>" },
      { "ordem": 2, "tipo": "imagem", "imagemId": "uuid", "url": "https://cdn.../chave", "alt": "descrição" }
    ]
  },
  "error": null
}
```
`conteudo` de blocos tipo `texto` é HTML já sanitizado no backend
(allowlist `<b>`, `<i>`, `<a href>`, `<p>`, `<br>`) — armazenado
sanitizado, não sanitizado só na leitura, para não depender de toda
leitura reprocessar.

**Erros**: `404 NOT_FOUND` se slug não existir.

### 2.2 `GET /api/v1/admin/cms/paginas` (admin, lista)
- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Response 200**: lista de `{ slug, titulo, atualizadoEm, atualizadoPor }`
  (sem os blocos completos — tela de listagem, evita over-fetching).

### 2.3 `GET /api/v1/admin/cms/paginas/{slug}` (admin, detalhe p/ edição)
- Mesma resposta de 2.1 mas incluindo `atualizadoEm`/`atualizadoPor` e
  `conteudo` bruto (antes de qualquer transformação de exibição) para
  popular o formulário do painel.

### 2.4 `PUT /api/v1/admin/cms/paginas/{slug}` (admin, editar página+blocos)
- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: sim (PUT substitui o estado completo dos blocos da
  página).
- **Consistência**: forte, síncrona.
- **Rate limit**: baixo, só para conter automação anômala — não é rota
  de alto risco de abuso externo (exige já estar autenticado como admin).

**Request** (`CmsPaginaUpdateRequest`):
```json
{
  "titulo": "string",
  "blocos": [
    { "ordem": 1, "tipo": "texto", "conteudo": "string, markup limitado" },
    { "ordem": 2, "tipo": "imagem", "imagemId": "uuid, referência a imagem já enviada via bloco 3", "alt": "string" }
  ]
}
```
Blocos tipo `imagem` referenciam `imagemId` de um upload **já existente**
(feito via endpoint do Bloco 3) — este endpoint não aceita binário
multipart, só a referência. Evita misturar responsabilidade de upload
com a de composição de página.

**Response 200**: mesmo shape de 2.3 (estado já persistido).

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | markup fora da allowlist, `ordem` duplicada/faltando, `imagemId` que não corresponde a upload existente |
| 403 | `FORBIDDEN` | autenticado mas sem `ROLE_ADMIN` |
| 404 | `NOT_FOUND` | slug não existe (para criar página nova, ver 2.5) |

Efeito colateral obrigatório (síncrono, mesma transação): grava
`atualizado_em`/`atualizado_por` na página e insere linha em
`cms_alteracao_log` com snapshot do estado anterior (JSON dos blocos
antes da mudança) — auditoria simples conforme seção 2/10 do doc de
decisões.

### 2.5 `POST /api/v1/admin/cms/paginas` (admin, criar página nova)
- Mesma política de auth/idempotência (não-idempotente, cria recurso) e
  consistência do 2.4.
- **Request**: `{ "slug": "string, kebab-case, único", "titulo": "string", "blocos": [...] }`
- **Response 201**: mesmo shape de página.
- **Erros**: `400 VALIDATION_ERROR`, `403 FORBIDDEN`, `409 CONFLICT` (slug já existe).

### Migrations propostas — Bloco 2
```sql
-- V{n}__cria_tabela_cms.sql
CREATE TABLE cms_pagina (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  slug VARCHAR(100) NOT NULL UNIQUE,
  titulo VARCHAR(200) NOT NULL,
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_por UUID NOT NULL REFERENCES usuario(id)
);

CREATE TABLE cms_bloco (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  pagina_id UUID NOT NULL REFERENCES cms_pagina(id) ON DELETE CASCADE,
  ordem INTEGER NOT NULL,
  tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('texto', 'imagem')),
  conteudo TEXT,          -- usado quando tipo = 'texto'
  imagem_id UUID REFERENCES imagem(id), -- usado quando tipo = 'imagem'
  alt VARCHAR(300),
  UNIQUE (pagina_id, ordem)
);

CREATE TABLE cms_alteracao_log (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  pagina_id UUID NOT NULL REFERENCES cms_pagina(id),
  alterado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  alterado_por UUID NOT NULL REFERENCES usuario(id),
  snapshot_anterior JSONB NOT NULL
);
CREATE INDEX idx_cms_alteracao_log_pagina ON cms_alteracao_log(pagina_id);
```
`imagem_id` referencia a tabela `imagem` do Bloco 3 (ver abaixo) — CMS
depende do módulo de upload, não o contrário.

Índice único composto `(pagina_id, ordem)` em `cms_bloco`: padrão de
acesso é sempre "buscar todos os blocos de uma página, ordenados" — o
índice serve tanto a unicidade (não pode haver dois blocos na mesma
posição) quanto a query de listagem ordenada.

---

## Bloco 3 — Upload de Imagem

### 3.1 `POST /api/v1/admin/imagens` (admin, upload)
- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: não (cada chamada cria um novo registro de imagem,
  mesmo se o binário for idêntico — dedup por hash de conteúdo é
  otimização futura, fora de escopo deste contrato).
- **Rate limit**: sim — upload é custoso (I/O, processamento de EXIF);
  token bucket mais restritivo que rotas de leitura.
- **Consistência**: forte — resposta só retorna sucesso após o binário
  já estar confirmado em disco e o metadado persistido no Postgres na
  mesma operação lógica (ver nota de transação abaixo).

**Request**: `multipart/form-data`
- `arquivo`: binário (jpeg/png/webp — validado por magic bytes, não por
  extensão/`Content-Type` declarado pelo cliente)
- `alt`: string, texto alternativo obrigatório (acessibilidade)

**Response 201** (`ImagemResponse`):
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "nomeOriginal": "foto.jpg",
    "tamanhoBytes": 204800,
    "alt": "string",
    "url": "https://cdn.dominio/chave-do-arquivo",
    "criadoEm": "2026-07-12T14:30:00Z"
  },
  "error": null
}
```

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | `alt` ausente; arquivo maior que limite (definir com `devops`, ex. 5MB) |
| 415 | `UNSUPPORTED_MEDIA` | magic bytes não correspondem a jpeg/png/webp (inclui rejeição explícita de SVG) |
| 403 | `FORBIDDEN` | autenticado sem `ROLE_ADMIN` |
| 429 | `RATE_LIMITED` | excesso de uploads na janela |
| 502 | `UPSTREAM_ERROR` | falha de I/O no disco local (ex. sem espaço, permissão) — não vaza path/detalhe interno na mensagem ao cliente |

**Pipeline obrigatório no backend antes de gravar em disco**:
1. Ler bytes, validar magic bytes contra allowlist (jpeg/png/webp).
2. Remover metadados EXIF (geolocalização, dispositivo) — sempre, antes
   de qualquer persistência.
3. Gerar chave de objeto não previsível (ex: UUID, não nome original) —
   evita enumeration/traversal de arquivos no diretório de storage.
4. Gravar em disco local (`LocalDiskStorageClient`).
5. Só após confirmação da escrita em disco, persistir metadado no
   Postgres na mesma transação lógica da request (se a escrita no
   Postgres falhar após gravação bem-sucedida, é preciso um mecanismo de
   limpeza/retry — ver nota de consistência abaixo).

**Nota de consistência (duas escritas em sistemas diferentes)**: como
não há transação distribuída real entre disco e Postgres, a ordem
"disco primeiro, Postgres depois" é deliberada — um arquivo órfão em
disco (gravação ok, insert falhou) é inofensivo e limpável depois (rotina
de garbage collection, comparando diretório vs. tabela `imagem`); a
ordem inversa (Postgres aponta pra arquivo que não existe em disco)
quebraria a página pública imediatamente, o que é pior. Este é o motivo
da ordem escolhida — documentar caso a implementação real precise
revisitar.

### 3.2 `DELETE /api/v1/admin/imagens/{id}` (admin, remover)
- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: sim (deletar duas vezes o mesmo id resulta no mesmo
  estado final; segunda chamada retorna 404, tratado como "já não
  existe", não como erro grave do ponto de vista do cliente).
- **Consistência**: forte — remove do bucket e marca/remove metadado no
  Postgres.

**Response 200**: `{ "success": true, "data": { "mensagem": "Imagem removida." }, "error": null }`

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `NOT_FOUND` | id não existe |
| 409 | `CONFLICT` | imagem ainda referenciada por um `cms_bloco` ativo — bloqueia exclusão até o bloco ser removido/trocado (evita link quebrado em página pública) |

### Migration proposta — Bloco 3
```sql
-- V{n}__cria_tabela_imagem.sql
CREATE TABLE imagem (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nome_original VARCHAR(255) NOT NULL,
  chave_arquivo VARCHAR(255) NOT NULL UNIQUE,
  tamanho_bytes BIGINT NOT NULL,
  alt VARCHAR(300) NOT NULL,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  criado_por UUID NOT NULL REFERENCES usuario(id)
);
```
`chave_arquivo` é a chave do objeto no bucket (não a URL completa —
monta a URL na resposta a partir de config de CDN/domínio, permite
trocar de bucket/CDN sem migration).

---

## Bloco 4 — Upload de Literatura em PDF

> Status: **implementado em código em 2026-07-12**. Spec fechada por
> `ux-ui` (bloco CMS `TipoBloco.arquivo`, upload dissociado da composição
> de página, mesmo padrão do Bloco 3), `devops` (limite 20MB, reusa
> storage/quota/rate-limiter/GC de órfã já existentes) e `security`
> (validação estrutural via Apache PDFBox, allowlist negativa).
> Dependência nova aprovada pelo usuário: `org.apache.pdfbox:pdfbox:3.0.3`.

### 4.1 `POST /api/v1/admin/arquivos` (admin, upload de PDF)
- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: não (cada chamada cria um novo registro, mesmo padrão
  do Bloco 3).
- **Rate limit**: mesmo mecanismo do Bloco 3 — bucket por admin (10/min) +
  bucket global (60/min), reaproveitando `RateLimiterService`.
- **Consistência**: forte — mesma ordem "bucket primeiro, Postgres depois"
  do Bloco 3, mesma nota de consistência (objeto órfão é limpável pelo GC,
  linha órfã sem objeto quebraria a página pública).

**Request**: `multipart/form-data`
- `arquivo`: binário PDF (magic bytes `%PDF-` estrito no byte 0, sem
  offset — mais rígido que a spec PDF; validação estrutural completa via
  PDFBox, ver pipeline abaixo). Limite: **20MB** (definido por `devops`,
  reaproveita mesma camada de enforcement do Bloco 3: multipart Spring
  elevado para 20MB globalmente + checagem explícita em código por
  módulo, já que agora o teto do Spring é compartilhado entre imagem
  5MB e arquivo 20MB).
- `titulo`: string, título de exibição, obrigatório.
- `descricao`: string, descrição curta, opcional.

**Response 201** (`ArquivoResponse`):
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "nomeOriginal": "literatura.pdf",
    "tamanhoBytes": 1048576,
    "titulo": "string",
    "descricao": "string ou null",
    "urlDownload": "/api/v1/arquivos/{id}/download",
    "criadoEm": "2026-07-12T14:30:00Z"
  },
  "error": null
}
```
`urlDownload` é um path relativo do próprio backend (não CDN/bucket
direto) — serve o PDF fora do document root, com headers de segurança
fixos (ver 4.3), nunca expõe a chave real do objeto no bucket.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | `titulo` ausente; arquivo maior que 20MB |
| 415 | `UNSUPPORTED_MEDIA` | magic bytes fora do byte 0; bytes extras após `%%EOF` (polyglot); PDF contém `/JavaScript`, `/OpenAction`, `/AA`, `/EmbeddedFile` ou `/Launch` |
| 403 | `FORBIDDEN` | autenticado sem `ROLE_ADMIN` |
| 429 | `RATE_LIMITED` | excesso de uploads na janela |
| 502 | `UPSTREAM_ERROR` | bucket indisponível/erro de rede |

**Pipeline obrigatório no backend** (paralelo ao Bloco 3, sem reencode —
reencodar destruiria o PDF, diferente de imagem):
1. Checar `%PDF-` estritamente no byte 0.
2. Parse estrutural via `PDFBox` (`Loader.loadPDF`).
3. Checar ausência de bytes extra após o último `%%EOF` (detecção de
   polyglot).
4. Checar allowlist negativa: `/JavaScript` (incluindo `/JS` e varredura
   de todos os objetos indiretos do documento, não só os alcançáveis
   pelo catálogo — cobre objeto órfão inserido para escapar de checagem
   superficial), `/OpenAction`, `/AA` (catálogo e por página),
   `/EmbeddedFile`, `/Launch`.
5. Gerar chave de objeto não previsível (UUID), sem reencode do binário.
6. Upload ao bucket, depois persistir metadado no Postgres (mesma ordem
   e mesma nota de consistência do Bloco 3).

### 4.2 `DELETE /api/v1/admin/arquivos/{id}` (admin, remover)
- Mesmo contrato do Bloco 3 (3.2): idempotente, `403 FORBIDDEN`,
  `404 NOT_FOUND`, `409 CONFLICT` se ainda referenciado por `cms_bloco`.

### 4.3 `GET /api/v1/arquivos/{id}/download` (pública, servir PDF)
- **Autenticação**: nenhuma — literatura institucional é conteúdo aberto,
  mesmo padrão de `/api/v1/cms/**`.
- **Idempotente**: sim (leitura).
- **Headers de resposta obrigatórios** (spec `security`):
  - `Content-Type: application/pdf` fixo (nunca reflete o que o cliente
    declarou no upload).
  - `X-Content-Type-Options: nosniff`.
  - `Content-Disposition: inline; filename="..."` com nome sanitizado
    (apenas `[a-zA-Z0-9._-]`, evita header injection via nome de arquivo
    original).
- Servido a partir do storage stub/bucket, nunca a partir de um path
  estático do document root do servidor web — é sempre o backend que lê
  o objeto e retorna os bytes.

**Erros**: `404 NOT_FOUND` se id não existir; `502 UPSTREAM_ERROR` se o
storage falhar ao ler.

### Migrations — Bloco 4
```sql
-- V5__cria_tabela_arquivo.sql
CREATE TABLE arquivo (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nome_original VARCHAR(255) NOT NULL,
  chave_arquivo VARCHAR(255) NOT NULL UNIQUE,
  tamanho_bytes BIGINT NOT NULL,
  titulo VARCHAR(300) NOT NULL,
  descricao VARCHAR(500),
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  criado_por UUID NOT NULL REFERENCES usuario(id)
);
```
```sql
-- V6__adiciona_bloco_arquivo.sql (expand: coluna nova nullable, sem
-- quebrar nenhuma linha existente)
ALTER TABLE cms_bloco ADD COLUMN arquivo_id UUID REFERENCES arquivo(id);
CREATE INDEX idx_cms_bloco_arquivo ON cms_bloco(arquivo_id);

ALTER TABLE cms_bloco DROP CONSTRAINT cms_bloco_tipo_check;
ALTER TABLE cms_bloco ADD CONSTRAINT cms_bloco_tipo_check
  CHECK (tipo IN ('texto', 'imagem', 'arquivo'));
```
`arquivo_id` é nullable e paralelo a `imagem_id` (mesmo padrão, um bloco
usa só uma das duas FKs conforme o `tipo`) — não é normalização
prematura: é o mesmo modelo já usado para `imagem`, só estendido.

### Mudança breaking sinalizada — nenhuma
`TipoBloco` ganhou o valor novo `arquivo` (aditivo — enum antigo
`texto`/`imagem` continua válido) e `BlocoDto`/`BlocoRequest` ganharam
campos novos opcionais (`arquivoId`, mais `titulo`/`descricao` só na
leitura, vindos da entidade `Arquivo`, não duplicados em `cms_bloco`).
Nenhum contrato existente (`imagem`, `texto`) mudou de shape — não é
breaking change para o que `frontend-services` já consumia dos Blocos
2/3.

### Ajuste retroativo de configuração (Bloco 3 e 4 compartilham teto)
`spring.servlet.multipart.max-file-size` subiu de 5MB para **20MB**
(precisa cobrir PDF) — como esse limite é global no Spring, a trava de
5MB para imagem deixou de vir só da config e passou a ser checada
explicitamente em código (`ImagemService`, `arquivo.getSize() > 5MB`),
para o limite de negócio de cada tipo de mídia não depender mais de um
único valor compartilhado.

---

## Resumo de dependências entre blocos
- Bloco 2 (CMS) depende da tabela `imagem` do Bloco 3 existir primeiro
  (FK `cms_bloco.imagem_id`) — ordem de migration: Bloco 3 antes do
  Bloco 2, ou Bloco 1 → 3 → 2 na numeração Flyway.
- Bloco 1 (e-mail) é independente dos outros dois, pode ser
  implementado em paralelo.
- Bloco 4 (PDF) depende da tabela `cms_bloco` do Bloco 2 já existir
  (adiciona coluna `arquivo_id` nela) — por isso migration V5 (tabela
  `arquivo`) vem antes de V6 (coluna em `cms_bloco`), na mesma ordem
  lógica que Bloco 3 → Bloco 2 já seguia.

## Pendências explícitas para outros agentes
- `security`: confirmar mecanismo de bucket duplo de rate limit
  (usuario_id resolvido + identificador bruto normalizado) e chave
  HMAC usada para hash de log de tentativa de login/reset.
- ~~`devops`: definir limite de tamanho de upload, política de retry para
  falha de envio de e-mail (fila), rotina de garbage collection de
  imagem órfã no bucket.~~ **Fechado em 2026-07-12, ver seção abaixo.**
- `frontend-services`: mockar os DTOs acima; sinalizar se algum shape
  não atende a tela já desenhada pelo `ux-ui` antes de eu implementar.

---

## Decisões de infra — `devops` (2026-07-12)

Fecha as 3 pendências acima. Só política/config — nada implementado,
nada de código backend. Escopo: infra que o backend precisa expor
(config/env) ou consumir (fila, cron, bucket).

### D1. Limite de tamanho de upload + quota de bucket

- **Limite por arquivo: 5MB**, confirma a sugestão do contrato. Motivo:
  CMS só aceita jpeg/png/webp para página institucional (não é upload de
  usuário final em massa, é conteúdo de admin) — 5MB já é folgado pra
  imagem web comprimida; qualquer coisa maior é sinal de arquivo não
  otimizado, melhor rejeitar e pedir compressão no client/no processo do
  admin do que aceitar e pagar storage/CDN por lixo.
- **Onde é enforced, em camadas** (defesa em profundidade, mas simples):
  1. **Reverse proxy / gateway (nginx ou equivalente na frente do Spring,
     se/quando existir)**: `client_max_body_size 5m` — barra o request
     antes de gastar thread/conexão do backend. Se não houver proxy
     próprio ainda (ex. Spring exposto direto atrás de um LB gerenciado),
     aplicar o limite equivalente no LB (varia por provedor, documentar
     quando o provedor for escolhido).
  2. **Spring Boot** (`application.yml`, valores diferentes por profile
     não fazem sentido aqui — é regra de negócio estável, não muda
     dev/prod): `spring.servlet.multipart.max-file-size=5MB` e
     `max-request-size=6MB` (headroom pequeno pro resto do multipart:
     campo `alt`, boundary). Gera 400 antes mesmo de o controller rodar
     — o contrato já mapeia isso pra `VALIDATION_ERROR`, o `backend`
     só precisa garantir que a exceção do Spring
     (`MaxUploadSizeExceededException`) caia no
     `@RestControllerAdvice` (seção 9 do doc de decisões) com esse code,
     não vaze stack trace.
  3. **Bucket (R2/B2)**: sem limite de tamanho por objeto configurável
     do lado do provedor que faça sentido aplicar aqui (ambos suportam
     objetos muito maiores que 5MB) — a trava real é só nas camadas 1-2.
- **Quota do bucket (R2 ou Backblaze B2)**: não é limite por objeto, é
  limite agregado de armazenamento/egress pra não ter conta surpresa.
  - Volume estimado: CMS tem poucas páginas, poucos blocos de imagem por
    página (ver seção 2 do doc de decisões — "todas as páginas
    institucionais editáveis", mas são poucas e estáveis). Mesmo com
    upload de teste/erro de admin, estimativa generosa: algumas centenas
    de imagens x 5MB = baixo single-digit GB. R2 free tier já cobre
    10GB/mês de storage e **não cobra egress** (diferencial vs S3
    tradicional); B2 tem 10GB grátis + egress pago mas barato. Não há
    necessidade de comprar tier pago agora.
  - **Alerta de billing/quota configurado no console do provedor**
    (R2: Cloudflare notifica por uso; B2: alertas de cap configuráveis)
    em ~80% do free tier — sinal de crescimento inesperado (loop de
    upload com bug, abuso) antes de virar custo real. Isso é
    configuração de conta no provedor, fora do código.
  - **Sem quota por admin individual** no MVP (ex. "admin X só pode
    subir N imagens") — não há multiusuário de conteúdo em volume que
    justifique; rate limit por contagem (10/min por admin, já definido
    por `security`) já contém abuso de velocidade, e o tamanho total é
    baixo o suficiente pra não precisar de cota individual agora.
    Reavaliar se o número de admins/conteúdo crescer muito.
- **Escolha R2 vs B2**: mantida em aberto no doc de decisões (seção 5)
  como "R2 ou B2" — não há motivo técnico forte pra decidir agora só por
  causa desta pendência; ambos atendem igual no volume do projeto. Decisão
  final de qual provedor efetivamente contratar fica pra quando o
  `backend` for implementar o client de storage (evita decisão prematura
  sem ver a API/SDK de cada um em uso real).

### D2. Retry/backoff da fila de e-mail (esqueci-senha)

- **Mecanismo de fila**: não introduzir um message broker dedicado
  (RabbitMQ/Kafka/SQS) só para isso — volume é baixo (recuperação de
  senha não é ação de alta frequência) e o projeto já decidiu monolito
  simples (seção 1 do doc de decisões). Usar mecanismo já nativo do
  ecossistema Spring: `@Async` + `@Retryable` (Spring Retry) numa
  fila lógica em memória (ex. `TaskExecutor` dedicado para envio de
  e-mail), sem estado persistido em tabela própria. Isso é proporcional
  ao estágio atual — adicionar um broker externo aqui seria
  overengineering (ver preocupação do `system-design` sobre isso).
  - Trade-off aceito: se o processo do backend cair entre a escrita do
    token (síncrona, já persistida) e o envio do e-mail (assíncrono, em
    memória), o e-mail daquele evento específico se perde — mas o token
    já está no banco, então o usuário pode simplesmente chamar
    `/esqueci-senha` de novo (idempotente por design, contrato 1.3) e
    receber um novo e-mail. Não precisa de durabilidade de fila pra isso
    ser aceitável.
- **Política de retry**: 3 tentativas, backoff exponencial curto
  (ex. 2s → 6s → 18s, `@Retryable(maxAttempts=3, backoff=@Backoff(delay=2000, multiplier=3))`)
  — suficiente pra absorver instabilidade transitória do provedor de
  e-mail (timeout de rede, 5xx momentâneo) sem seagurar a thread por
  muito tempo nem martelar o provedor.
- **Idempotência do consumidor** (já exigida pelo contrato): o envio usa
  sempre o token já persistido (hash no banco), nunca gera um novo —
  reenviar por retry ou por nova chamada do usuário aponta pro mesmo
  link enquanto o TTL (~30min) não expirar. Reenvio duplicado do mesmo
  e-mail não é um problema de correção, só UX (usuário recebe 2 e-mails
  iguais na janela de retry) — aceitável.
- **Falha definitiva (esgotou as 3 tentativas)**:
  1. **Nunca** propaga erro pro usuário nem muda a resposta HTTP já
     enviada (contrato já é claro: resposta 200 genérica não espera o
     e-mail).
  2. **Log operacional nível ERROR** (seção 10 do doc de decisões) com o
     código de erro correlato (`traceId` da requisição original) e a
     causa técnica (timeout, 5xx do provedor, etc) — nunca o endereço de
     e-mail em texto puro, só o mesmo hash/identificador técnico já usado
     em outros logs de tentativa (reaproveita a convenção da seção 10).
  3. **Sem retry manual automatizado depois disso** no MVP (não vale
     reprocessamento assíncrono via cron pra reenviar) — usuário
     simplesmente chama `/esqueci-senha` de novo se não receber o
     e-mail em alguns minutos, e isso já gera novo envio (idempotente,
     ver acima). Simplicidade > complexidade de fila com dead-letter
     agora; reavaliar só se o volume de falha de e-mail em produção se
     mostrar recorrente (nesse caso, sinal de trocar de provedor de
     e-mail, não de sofisticar a fila).
  4. **Alerta**: taxa de falha definitiva de envio de e-mail acima de um
     limiar (ex. >5% das tentativas em 1h) dispara alerta operacional
     (ver observabilidade, seção 7 do doc de decisões) — sinal de
     provedor de e-mail com problema, não de bug de aplicação.

### D3. GC de imagem órfã no bucket

- **Definição de órfã**: objeto que existe no bucket mas (a) não tem
  linha correspondente em `imagem` (insert no Postgres falhou após
  upload) **ou** (b) tem linha em `imagem` mas nenhum `cms_bloco` a
  referencia há mais que um período de graça (upload feito mas nunca
  usado numa página — ex. admin fez upload, testou, desistiu do bloco).
  - Caso (a) é o cenário citado explicitamente na "nota de consistência"
    do contrato (bucket-primeiro-depois-postgres).
  - Caso (b) não é inconsistência técnica (upload é intencionalmente
    dissociado da composição de página, contrato Bloco 2/3), mas é lixo
    acumulado se nunca for limpo — mesmo mecanismo de GC cobre os dois.
- **Mecanismo**: job agendado simples (`@Scheduled` no próprio Spring
  Boot, sem cron externo/Kubernetes CronJob — não há orquestração K8s
  neste estágio, ver seção Docker/K8s deste agente), rodando 1x/dia em
  horário de baixo tráfego (ex. 04h BRT):
  1. Lista objetos do bucket (via API do provedor, paginada).
  2. Para cada chave, verifica se existe `imagem.chave_arquivo`
     correspondente. Se não existe → candidato a exclusão imediata
     (caso a).
  3. Para chaves com linha em `imagem` existente, verifica se há
     `cms_bloco.imagem_id` referenciando — se não há **e** `criado_em`
     é mais antigo que **7 dias** (período de graça: dá tempo do admin
     terminar de montar a página sem o GC apagar upload "em uso mas
     ainda não salvo no bloco"), marca como candidato (caso b).
  4. Delete no bucket **e** delete da linha em `imagem` (mesma
     transação lógica do job) só depois de confirmar que não há
     `cms_bloco` referenciando — reaproveita a mesma constraint de
     negócio do `DELETE /admin/imagens/{id}` (409 se referenciada,
     contrato 3.2): o job de GC é, na prática, a versão automatizada
     dessa regra para uploads nunca associados.
  5. Log operacional (não auditoria — não é ação de admin, é rotina de
     sistema) registra contagem de objetos removidos por execução, sem
     detalhe de conteúdo/nome (nome original de arquivo pode ter dado
     incidental, ex. nome de pessoa em foto pessoal enviada por engano
     — omitir do log por precaução, só contar).
- **Frequência**: diária é suficiente — objeto órfão no bucket é
  "inofensivo" por design (mesma nota do contrato), não há urgência de
  segurança em limpar rápido, só custo de storage marginal. Diário
  evita acúmulo sem adicionar complexidade de rodar com mais frequência.
- **Por que não job externo (ex. Lambda/Cloud Function agendada)**: mais
  uma peça de infra pra manter (deploy, permissão, observabilidade
  separada) sem necessidade real no volume do projeto — `@Scheduled`
  dentro do próprio backend já resolve, reaproveita o mesmo pool de
  conexão/config/log já existente. Reavaliar só se o backend precisar
  escalar horizontalmente com múltiplas réplicas (nesse caso,
  `@Scheduled` simples rodaria em duplicidade em cada réplica — precisa
  de lock distribuído ou mover pra job externo; não é o caso hoje, ver
  seção Docker/K8s: reavaliar decisão de orquestração só quando houver
  tráfego que justifique múltiplas réplicas).

---

## Decisão revista — storage local em disco (2026-07-12, `backend`)

**Supersede parcialmente D1 e D3 acima** no que se refere a "bucket
S3-compatible (R2/B2)": a decisão de infra passou a ser **disco local
persistente**, mantida pela interface `StorageClient` já existente.
`StubStorageClient` foi promovido a implementação real de produção,
renomeado para `LocalDiskStorageClient` — mesmo contrato de interface,
nenhuma mudança de shape de request/response em nenhum bloco acima.

- **Motivo**: volume do projeto (~2.500 req/dia, poucas dezenas de
  uploads de admin) não justifica a complexidade operacional de
  contratar/gerenciar credenciais de bucket externo; disco local com
  volume persistente (Docker volume nomeado, já usado em dev) atende o
  mesmo padrão de acesso sem dependência de rede externa.
- **Env vars mantidas sem renomear**: `STORAGE_STUB_DIR` e
  `STORAGE_PUBLIC_BASE_URL` continuam com o mesmo nome — evita quebrar
  `application.properties`, `application.properties.example` e
  `docker-compose.yml` já commitados; o nome "stub" nas env vars é
  incidental (não aparece em nenhuma classe/contrato de API), custo de
  renomear não compensa o ganho semântico. Só a **classe Java** mudou de
  nome (`StubStorageClient` → `LocalDiskStorageClient`), porque o nome
  da classe é o que sinaliza "isso é código de produção, não descartável".
- **Path traversal**: `chave` do objeto é sempre gerada internamente
  (`UUID.randomUUID() + "." + extensao`, ver `ImagemService.upload` e
  `ArquivoService.upload`) — nunca deriva de nome de arquivo enviado pelo
  usuário. `download()` mantém a checagem de `normalize()` +
  `startsWith(baseDir)` como defesa em profundidade, mesmo a chave sendo
  sempre confiável na origem.
- **Permissões de arquivo**: `LocalDiskStorageClient` aplica permissão
  restritiva (`rw-r-----` em arquivo, `rwx------` em diretório) via
  `PosixFileAttributeView` quando o SO suporta (Linux/produção via
  Docker); em Windows (dev local) a view POSIX não existe e a chamada é
  no-op — sem erro, sem comportamento diferente do anterior em dev.
- **GC de órfão (D3)**: mecanismo (`@Scheduled` diário, período de graça
  de 7 dias) continua igual, só troca "bucket" por "diretório local" no
  texto — nenhuma mudança de lógica, já que o job sempre operou via
  `StorageClient.delete(chave)`, abstraído da implementação concreta.
- **Alerta de billing/quota de bucket (parte de D1)**: não se aplica mais
  (não há bucket externo cobrando por uso); quota vira responsabilidade
  de `devops` monitorar espaço em disco do volume, fora do escopo deste
  contrato de API.

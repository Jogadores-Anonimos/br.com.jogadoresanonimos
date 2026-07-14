# Backend — storage de imagem/PDF: bucket S3 descartado, disco local em produção

**Data**: 2026-07-12
**Agente**: backend

## O que foi feito

Decisão de infraestrutura revista: o plano original (documentado em
`backend/docs/api/contrato-auth-cms-upload.md`) previa produção usando
um bucket S3-compatible externo (Cloudflare R2 ou Backblaze B2), com um
`StubStorageClient` gravando em disco local apenas como stub de
desenvolvimento, a ser substituído antes de ir para produção.

Essa decisão foi revertida: produção também usa disco local persistente.
Não haverá bucket externo. Por isso:

- `StubStorageClient` foi removido e sua lógica foi promovida para uma
  nova classe `LocalDiskStorageClient`, que agora é a implementação real
  de produção da interface `StorageClient` (que continua a mesma,
  inalterada).
- Caminho: `backend/src/main/java/br/com/jogadoresanonimos/imagem/service/LocalDiskStorageClient.java`.
- A interface `StorageClient.java` teve a docstring atualizada para não
  falar mais em bucket S3.

## Por que essa troca (trade-off)

Volume do projeto é baixo (~2.500 requisições/dia no total, poucas
dezenas de uploads de admin por semana no CMS). Gerenciar credenciais de
bucket externo, contas em provedor R2/B2, alertas de billing etc. é
complexidade operacional desproporcional a esse volume. Disco local com
volume Docker persistente (já usado em dev) atende ao mesmo padrão de
acesso sem dependência de rede externa nem custo/gestão de conta externa.
Fica mais simples de operar, ao custo de não ter CDN/distribuição
geográfica nativa — aceitável no volume atual do projeto (a maioria do
tráfego é nacional, conforme contexto de escala do CLAUDE.md).

## Decisões técnicas dentro da troca

- **Nome de classe mudou, nome de env var não.** `STORAGE_STUB_DIR` e
  `STORAGE_PUBLIC_BASE_URL` continuam com esses nomes exatos em
  `application.properties`, `application.properties.example` e
  `docker-compose.yml` — não foram renomeados. Motivo: o nome "stub" nas
  env vars é incidental, não aparece em nenhum DTO/contrato de API
  consumido pelo frontend, e renomear geraria custo de sincronizar 3
  arquivos de config sem ganho real. Já o nome da **classe Java** foi
  trocado porque é isso que comunica intenção para quem lê o código:
  "isso é implementação de produção", não "descartar depois".
- **Path traversal**: confirmado que a `chave` do objeto sempre é gerada
  internamente como `UUID.randomUUID() + "." + extensao` (nunca deriva
  de nome de arquivo enviado pelo usuário) — ver `ImagemService.upload` e
  `ArquivoService.upload`. Isso já protegia contra traversal na escrita
  mesmo antes dessa mudança; o método `download()` mantém, como defesa em
  profundidade adicional, a checagem de `path.normalize().startsWith(baseDir)`.
- **Permissões de arquivo**: `LocalDiskStorageClient` agora aplica
  permissão restritiva ao gravar (`rw-r-----` no arquivo, `rwx------` no
  diretório) via `PosixFileAttributeView`, quando o sistema operacional
  suporta. Em Linux (produção via Docker) isso funciona de verdade; em
  Windows (ambiente de dev local do time) a view POSIX não existe e a
  chamada vira no-op silencioso — não quebra nada em dev, protege de
  verdade em produção.
- **GC de imagem órfã** (`ImagemOrfaGcJob`) não mudou de lógica — sempre
  operou através da interface `StorageClient.delete(chave)`, abstraída
  da implementação concreta, então a troca de bucket para disco não
  exigiu nenhuma alteração nesse job.

## Onde no projeto

- `backend/src/main/java/br/com/jogadoresanonimos/imagem/service/LocalDiskStorageClient.java` (novo, substitui `StubStorageClient.java`, removido)
- `backend/src/main/java/br/com/jogadoresanonimos/imagem/service/StorageClient.java` (docstring atualizada)
- `backend/src/main/java/br/com/jogadoresanonimos/imagem/service/ImagemOrfaGcJob.java` (docstring atualizada, lógica igual)
- `backend/docs/api/contrato-auth-cms-upload.md` (nova seção "Decisão
  revista — storage local em disco (2026-07-12, `backend`)", ao final do
  arquivo, documentando o que supersede em D1/D3 do bloco de decisões de
  `devops`)

## Validação

`./mvnw test` rodado após a mudança: 50 testes, mesma falha de baseline
(`contextLoads` falha por falta de Postgres local rodando — esperado,
nenhum teste novo quebrou, nenhuma regressão introduzida pela troca).

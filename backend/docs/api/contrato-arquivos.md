# Contrato de API — Módulo Arquivo (Literatura)

> Status: **upload/remoção implementados anteriormente**; endpoint de
> edição de metadados (`PUT`) **implementado em 2026-07-12**, cobrindo a
> pendência registrada em `Pendencias Admin Dashboard.md` ("Pendência
> técnica confirmada — edição de arquivo (literatura)").

Convenções gerais (mesmas do restante do projeto, ver `contrato-avisos.md`):
- Envelope de resposta sempre `{ success, data, error }`.
- Todos os paths sob `/api/v1`.
- Nenhum token/identificador sensível em query string.
- `traceId` presente em toda resposta de erro.

---

## 3. `PUT /api/v1/admin/arquivos/{id}` (admin edita metadados) — novo

- **Autenticação + autorização**: `ROLE_ADMIN`, `@PreAuthorize("hasRole('ADMIN')")`.
- **Idempotente**: sim (PUT substitui `titulo`/`descricao`).
- **Consistência**: forte, síncrona — transação única no primário.
- **Não altera** o arquivo binário nem a `chaveArquivo` no storage — só
  os campos de metadados (`titulo`, `descricao`). Para trocar o PDF em
  si, o fluxo continua sendo excluir e reenviar (`DELETE` + `POST`), sem
  mudança nesta tarefa.

**Request** (`EditarArquivoRequest`):
```json
{
  "titulo": "string, 1-300 chars",
  "descricao": "string, opcional, até 500 chars"
}
```

**Response 200** (`ArquivoResponse`, mesmo shape do `POST /upload`):
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "nomeOriginal": "livro.pdf",
    "tamanhoBytes": 123456,
    "titulo": "string",
    "descricao": "string",
    "urlDownload": "/api/v1/arquivos/{id}/download",
    "criadoEm": "2026-07-12T14:30:00Z"
  },
  "error": null
}
```

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | `titulo` ausente/em branco ou fora do tamanho; `descricao` fora do tamanho |
| 403 | `AUTH_FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `ARQUIVO_NAO_ENCONTRADO` | id não existe |

> Catálogo completo de `error.code` em `contrato-erro-padrao.md`.

---

## Mudança breaking sinalizada — nenhuma

Endpoint novo, aditivo. Não altera o shape de `ArquivoResponse` nem o
comportamento de `upload`/`remover` já existentes.

## Implementação (backend)

- `br.com.jogadoresanonimos.arquivo.entity.Arquivo#editarMetadados` —
  método de domínio que atualiza `titulo`/`descricao` na entidade
  gerenciada (sem tocar em `chaveArquivo`/`nomeOriginal`).
- `br.com.jogadoresanonimos.arquivo.dto.EditarArquivoRequest` — novo DTO
  de request, com `@NotBlank`/`@Size` (mesmo padrão de validação do
  `EditarAvisoRequest`).
- `br.com.jogadoresanonimos.arquivo.service.ArquivoService#editar` —
  busca por id (404 se ausente), valida `titulo` não-branco após
  `trim()`, aplica a edição via `arquivo.editarMetadados(...)`. Sem
  chamada a `storageClient` — dirty-checking do JPA persiste a mudança
  no `flush` da transação, sem `save()` explícito necessário.
- `br.com.jogadoresanonimos.arquivo.controller.ArquivoController#editar`
  — `PUT /api/v1/admin/arquivos/{id}`, `@PreAuthorize("hasRole('ADMIN')")`.
- Testes unitários em
  `backend/src/test/java/br/com/jogadoresanonimos/arquivo/` (`controller`
  e `service`), mesmo padrão sem `MockMvc` usado no restante do projeto.

## Sem migration

Nenhuma coluna nova — `titulo`/`descricao` já existem na tabela
`arquivo` desde `V5`. Endpoint só expõe uma operação de escrita que
faltava sobre colunas já existentes.

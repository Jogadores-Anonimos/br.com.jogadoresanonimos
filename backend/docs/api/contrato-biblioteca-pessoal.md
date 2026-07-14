# Contrato de API — Biblioteca Pessoal do Usuário (usuario↔arquivo)

> Status: **implementado em 2026-07-12** — entity de associação,
> migration (`V17`), repository, service, DTO, controller, testes
> unitários. Origem: `Pendencias Admin Dashboard.md`, seção "Escopo
> adicional — área do usuário", aba "Downloads" de `minha-conta`.

Convenções gerais (mesmas do restante do projeto, ver
`contrato-relatos.md`):
- Envelope de resposta sempre `{ success, data, error }`.
- Todos os paths sob `/api/v1`.
- Nenhum token/identificador sensível em query string.
- Datas em ISO-8601 UTC.

---

## Modelo de dados e isolamento entre usuários

Tabela de associação pura `usuario_arquivo` (`usuario_id`, `arquivo_id`,
`criado_em`), chave primária composta — um usuário só pode ter uma
associação por arquivo (idempotência natural via `PRIMARY KEY`/`ON
CONFLICT DO NOTHING`, sem precisar de checagem de duplicata na aplicação
antes do insert).

Isolamento entre usuários é garantido estruturalmente, não só por
convenção de código: **todas** as queries (listar, adicionar, remover)
são sempre filtradas por `usuario_id = :idDoTokenJWT`, nunca por um id de
usuário vindo de path/query param. Não existe (e não deve existir)
endpoint que aceite um `usuarioId` arbitrário — o único usuário possível
em qualquer operação é `@AuthenticationPrincipal AuthenticatedUser`. Isso
elimina por construção a possibilidade de um usuário listar/remover item
de outro (não há parâmetro para manipular).

Não há endpoint para "ver quem salvou um arquivo" (i.e., não existe
consulta no sentido inverso `arquivo → usuários`) — não foi pedido, e
seria um vazamento de informação por si só (mostraria interesse de um
usuário por conteúdo específico a terceiros, mesmo que fosse só para
admin).

---

## 1. `GET /api/v1/usuarios/me/biblioteca` (lista arquivos salvos pelo usuário logado)

- **Autenticação**: obrigatória (JWT). Sem exigência de role — qualquer
  usuário autenticado.
- **Idempotente**: sim (leitura).
- **Consistência**: forte — leitura do primário. Justificativa: usuário
  que acabou de clicar "adicionar" espera ver o item refletido na aba
  Downloads imediatamente ao navegar para lá; volume da tabela é baixo
  (não há motivo para réplica/cache).
- **Paginação**: não aplicada nesta entrega — biblioteca pessoal de um
  usuário tem volume naturalmente pequeno (subconjunto da literatura
  disponível, que já é pequena). Se a lista de literatura crescer muito
  no futuro, reavaliar.

**Request**: `GET /api/v1/usuarios/me/biblioteca` (sem parâmetros —
usuário vem do JWT).

**Response 200** (`List<ArquivoResponse>`, mesmo DTO já usado por
`ArquivoController`/CMS, reaproveitado sem alteração):
```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "nomeOriginal": "livro.pdf",
      "tamanhoBytes": 123456,
      "titulo": "Título do livro",
      "descricao": "descrição ou null",
      "urlDownload": "/api/v1/arquivos/{id}/download",
      "criadoEm": "2026-07-12T14:30:00Z"
    }
  ],
  "error": null
}
```
Ordenado por data em que o usuário adicionou à biblioteca (`usuario_arquivo.criado_em DESC`,
mais recente primeiro), não pela data de criação do arquivo em si.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 401 | `UNAUTHENTICATED` | token ausente/inválido/expirado |

---

## 2. `POST /api/v1/usuarios/me/biblioteca/{arquivoId}` (adiciona arquivo à biblioteca)

- **Autenticação**: obrigatória (JWT).
- **Idempotente**: sim — adicionar um arquivo já salvo não duplica nem
  falha, apenas confirma o estado (retorna 200/204 normalmente).
- **Consistência**: forte, síncrona.

**Request**: sem corpo. `arquivoId` no path.

**Response 200**: `{ "success": true, "data": { "mensagem": "Arquivo adicionado à biblioteca." }, "error": null }`

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 401 | `UNAUTHENTICATED` | token ausente/inválido/expirado |
| 404 | `NOT_FOUND` | `arquivoId` não existe |

---

## 3. `DELETE /api/v1/usuarios/me/biblioteca/{arquivoId}` (remove arquivo da biblioteca)

- **Autenticação**: obrigatória (JWT).
- **Idempotente**: sim — remover duas vezes o mesmo item: segunda
  chamada também retorna sucesso (estado final igual: associação não
  existe), não é tratado como erro 404 (diferente do padrão de
  "remover recurso" usado em `RelatoAdminController`, porque aqui a
  ausência da associação **é** o estado desejado, não um erro do
  cliente).
- **Consistência**: forte, síncrona.
- **Importante**: remove só a linha de associação em `usuario_arquivo`.
  **Nunca** apaga o `Arquivo` em si (storage/CMS) — esse dado é
  institucional, compartilhado entre todos os usuários.

**Request**: sem corpo. `arquivoId` no path.

**Response 200**: `{ "success": true, "data": { "mensagem": "Arquivo removido da biblioteca." }, "error": null }`

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 401 | `UNAUTHENTICATED` | token ausente/inválido/expirado |

Sem 404 mesmo se `arquivoId` nunca existiu — DELETE idempotente não
diferencia "nunca existiu" de "já foi removido", ambos resultam no mesmo
estado final (nenhuma associação).

---

## Migration — `V17__cria_tabela_usuario_arquivo.sql`

```sql
CREATE TABLE usuario_arquivo (
  usuario_id UUID NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
  arquivo_id UUID NOT NULL REFERENCES arquivo(id) ON DELETE CASCADE,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  PRIMARY KEY (usuario_id, arquivo_id)
);

CREATE INDEX idx_usuario_arquivo_usuario_criado_em ON usuario_arquivo(usuario_id, criado_em DESC);
```

- Chave primária composta `(usuario_id, arquivo_id)`: garante unicidade
  estrutural (não precisa de `UNIQUE` adicional) e serve diretamente
  como índice de existência para o `POST` idempotente
  (`ON CONFLICT DO NOTHING`).
- `ON DELETE CASCADE` em ambas as FKs: se um usuário for deletado (fluxo
  futuro de exclusão de conta) ou um arquivo for removido pelo admin
  (`ArquivoController.remover`), a associação órfã some sozinha — não é
  dado que precise de retenção/auditoria própria (é só um "favorito").
- Índice composto `(usuario_id, criado_em DESC)`: cobre exatamente o
  padrão de acesso do único endpoint de leitura — "listar os arquivos de
  um usuário, mais recentes primeiro" — sem sort adicional em memória.
- Sem coluna `id` própria — tabela de associação pura não precisa de
  chave substituta; simplifica o modelo (segue o mesmo raciocínio de
  "evitar desnormalização prematura" do `CLAUDE.md`).

---

## Mudança breaking sinalizada — nenhuma

Endpoint novo, sem contrato anterior consumido pelo frontend (aba
"Downloads" hoje mostra a lista genérica de literatura, sem vínculo por
usuário — é mock/reaproveitamento direto do array de `/literatura`, não
uma API própria). `frontend-services` pode implementar diretamente contra
este contrato.

## Pendências explícitas para outros agentes

- `frontend`: botão "Adicionar à minha biblioteca" em `/literatura`
  (visível só logado) chamando `POST`; aba "Downloads" de `minha-conta`
  trocando a fonte de dado para `GET /api/v1/usuarios/me/biblioteca` e
  usando `DELETE` para "Remover da minha biblioteca" (texto do botão
  deve deixar claro que não apaga o PDF do site, só da lista pessoal).
- `frontend-services`: novo service (ou extensão de um existente) para
  os 3 endpoints acima; reaproveitar o mesmo shape de `ArquivoResponse`
  já usado pela página `/literatura` hoje.

## Implementação (backend)

- Pacote `br.com.jogadoresanonimos.usuario.biblioteca` (subpacote do
  módulo `usuario` já existente — decisão: a associação pertence
  conceitualmente ao "meu perfil" do usuário, mesmo mexendo em dados de
  `arquivo`; criar um módulo `biblioteca` totalmente separado seria
  fragmentar sem necessidade um recurso que só faz sentido dentro do
  contexto de conta do usuário logado).
  - `entity/UsuarioArquivo.java` — entidade com `@EmbeddedId`
    (`UsuarioArquivoId`, composto por `usuarioId` + `arquivoId`).
  - `repository/UsuarioArquivoRepository.java`.
  - `service/BibliotecaPessoalService.java`.
  - `controller/BibliotecaPessoalController.java` —
    `GET/POST/DELETE /api/v1/usuarios/me/biblioteca...`.
- Reaproveita `ArquivoResponse` (já existe em
  `br.com.jogadoresanonimos.arquivo.dto`) para a listagem — sem DTO novo,
  evita duplicar shape que o frontend já conhece de `/literatura`.
- Testes unitários em
  `backend/src/test/java/br/com/jogadoresanonimos/usuario/biblioteca/`,
  mockando `BibliotecaPessoalService`/`UsuarioArquivoRepository`, mesmo
  padrão sem `MockMvc` usado em `RelatoControllerTest`.

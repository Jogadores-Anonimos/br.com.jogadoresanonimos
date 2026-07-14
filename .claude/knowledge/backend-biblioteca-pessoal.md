# Backend — Biblioteca pessoal do usuário (usuario ↔ arquivo)

**O que foi feito (2026-07-12)**: associação muitos-para-muitos entre
`usuario` e `arquivo` para permitir que um usuário logado salve PDFs de
literatura na própria "biblioteca pessoal" (aba Downloads de
`minha-conta`).

**Onde**: `backend/src/main/java/br/com/jogadoresanonimos/usuario/biblioteca/`
(entity `UsuarioArquivo`/`UsuarioArquivoId`, `UsuarioArquivoRepository`,
`BibliotecaPessoalService`, `BibliotecaPessoalController`). Migration
`V17__cria_tabela_usuario_arquivo.sql`. Contrato em
`backend/docs/api/contrato-biblioteca-pessoal.md`.

**Decisões e trade-offs**:
- Subpacote dentro do módulo `usuario` existente, não um módulo
  `biblioteca` isolado — a associação só faz sentido no contexto de
  "minha conta" do usuário logado, mesmo mexendo em dados de `arquivo`.
- Tabela de junção pura, sem coluna `id` própria: `@EmbeddedId` composto
  (`usuarioId` + `arquivoId`) vira `PRIMARY KEY` no banco, que já garante
  unicidade e serve como índice de existência — não precisa de `UNIQUE`
  adicional nem checagem redundante na aplicação.
- Isolamento entre usuários é estrutural, não só disciplina de código:
  todo endpoint usa `@AuthenticationPrincipal AuthenticatedUser` como
  única fonte do `usuarioId` — não existe parâmetro de path/query que
  aceite um id de usuário arbitrário, então não há como um usuário
  manipular a lista de outro por design (nada pra "esquecer de validar").
- `POST` e `DELETE` são idempotentes por decisão explícita: adicionar
  duas vezes não duplica nem erra (checa existência antes do insert);
  remover duas vezes não retorna 404 na segunda vez — o estado desejado
  ("associação não existe") já foi alcançado, então é sucesso, não erro
  repetido. Isso é diferente do padrão usado em `RelatoAdminController`
  (que retorna 404 se o id não existe) — a diferença é intencional: lá é
  "remover um recurso que deveria existir", aqui é "garantir que uma
  preferência do usuário não está mais setada".
- `ON DELETE CASCADE` nas duas FKs: se o usuário for deletado (fluxo
  futuro) ou o admin remover o arquivo (`ArquivoController.remover`), a
  associação órfã some sozinha sem exigir lógica extra — é só um
  "favorito", não dado que precise de retenção/auditoria.
- Reaproveita `ArquivoResponse` já existente (usado por
  `ArquivoController`/CMS) em vez de criar DTO novo — mesmo shape que o
  frontend já conhece de `/literatura`.
- Nenhuma mudança em `SecurityConfig`: os 3 endpoints caem no
  `anyRequest().authenticated()` padrão (usuário comum, sem role ADMIN).

**Pendente**: `frontend-services`/`frontend` — botão "Adicionar à minha
biblioteca" em `/literatura` e troca da fonte de dado da aba Downloads em
`minha-conta` para os 3 endpoints novos.

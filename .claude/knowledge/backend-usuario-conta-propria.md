# Backend — edição da própria conta (usuário comum)

**O que foi feito (2026-07-13)**: fechado o gap da aba "Detalhes" de
`minha-conta`, que era 100% stub. Dois endpoints novos em
`UsuarioController` (`/api/v1/usuarios/me`, mesmo controller do `GET
/me` já existente):

- `PUT /api/v1/usuarios/me` — edita `apelido`/`email`, autenticado
  (qualquer role), **sem exigir senha atual** (decisão: baixo risco,
  não muda credencial de acesso).
- `PUT /api/v1/usuarios/me/senha` — troca senha, exige `senhaAtual`
  (validada via `PasswordEncoder.matches`, mesmo mecanismo do
  `AuthService.login`) + `novaSenha`/`confirmarNovaSenha`.

**Trade-off/motivo**:
- Unicidade de apelido/e-mail reaproveita
  `existsByApelidoIgnoreCase`/`existsByEmailIgnoreCase` do
  `UsuarioRepository` (mesma regra do registro), mas com uma diferença
  importante: a checagem **exclui o próprio usuário** (senão qualquer
  reenvio dos dados atuais sem mudança daria falso conflito). Isso é
  feito comparando `usuario.getApelido()`/`getEmail()` atuais contra o
  valor da request antes de consultar `existsBy*`.
- `senhaAtual` incorreta usa `UnauthenticatedException` (401), não
  `ValidationException` (400) — mesma decisão de não distinguir "senha
  errada" de "identificador errado" já usada no login, para não dar
  pista de enumeração.
- Entidade `Usuario` ganhou um método de domínio novo,
  `editarConta(apelido, email)` — sem setters genéricos, mesmo padrão
  já usado em `alterarRole`/`setSenhaHash`.
- `UsuarioService` passou a receber `PasswordEncoder` no construtor
  (mesmo bean já configurado e usado por `AuthService`) — quebra de
  assinatura do construtor, todos os testes existentes de
  `UsuarioServiceTest` precisaram ser ajustados para o novo parâmetro.

**Decisão de anonimato investigada e confirmada (não uma mudança)**:
`Relato.nomeExibicao` é uma coluna própria da entidade `Relato`,
preenchida no momento do envio, **sem vínculo dinâmico com
`Usuario.apelido`**. Ou seja, trocar o apelido da conta não afeta
relatos já publicados — eles continuam com o nome de exibição escolhido
na hora do envio. Esse desenho já existia antes desta tarefa (o usuário
sempre pôde publicar um relato com nome de exibição diferente do
apelido da própria conta); não foi necessário nenhum ajuste de modelo.

**Onde**:
- `backend/src/main/java/br/com/jogadoresanonimos/usuario/entity/Usuario.java`
  (`editarConta`)
- `backend/src/main/java/br/com/jogadoresanonimos/usuario/dto/AtualizarContaRequest.java`,
  `AlterarSenhaRequest.java` (novos)
- `backend/src/main/java/br/com/jogadoresanonimos/usuario/service/UsuarioService.java`
  (`atualizarConta`, `alterarSenha`)
- `backend/src/main/java/br/com/jogadoresanonimos/usuario/controller/UsuarioController.java`
  (2 endpoints novos)
- Testes: `backend/src/test/java/br/com/jogadoresanonimos/usuario/controller/UsuarioControllerTest.java`
  (novo arquivo), `.../usuario/service/UsuarioServiceTest.java` (estendido)
- Contrato: `backend/docs/api/contrato-usuario-conta-propria.md`
- Frontend: `frontend/src/app/services/auth.model.ts` (2 interfaces
  novas), `auth.ts` (`atualizarConta`/`alterarSenha`, com mock e via
  HTTP real conforme `environment.useMocks`), `minha-conta.ts`
  (`onSalvarDetalhes()` real: sempre salva apelido/e-mail, só troca
  senha se os 3 campos de senha foram preenchidos, desloga o usuário
  após troca de senha bem-sucedida porque o JWT antigo vira inválido
  via `senhaAlteradaEm`).

**Sem migration** — reaproveita `usuario` (`V1`) integralmente.

**Validado**: `./mvnw test` (198 testes, único erro é
`JogadoresAnonimosApplicationTests.contextLoads`, pré-existente, sem
Postgres local — confirmado que já falhava antes desta tarefa via
`git stash`). `npx tsc --noEmit` e `ng build --configuration
development` limpos no frontend.

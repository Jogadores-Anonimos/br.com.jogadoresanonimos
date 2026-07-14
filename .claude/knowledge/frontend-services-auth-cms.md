# Services Angular de Auth e CMS implementados com mock (2026-07-12)

## O que foi feito
Implementados/reescritos os services Angular da camada `frontend/src/app/services/`
para os Blocos 1 (Autenticação/E-mail) e 2 (CMS Leve) do contrato de API fechado
em `backend/docs/api/contrato-auth-cms-upload.md`. Bloco 3 (upload de imagem)
foi deliberadamente deixado de fora desta rodada — infra ainda sendo definida
em paralelo por `devops`.

Todos os services seguem o mesmo padrão: envelope de resposta `{ success, data,
error }` idêntico ao que a API real vai devolver, dados mockados em arquivos
`*.mock.ts` separados, comentário `// TODO: substituir por chamada real quando
o endpoint estiver disponível` em cada branch de mock, e um flag central
`environment.useMocks` (novo arquivo `frontend/src/environments/environment.ts`)
que decide se o service chama `HttpClient` de verdade ou usa o mock. Quando o
backend expuser os endpoints, basta trocar `useMocks: true` para `false` — a
assinatura pública dos métodos não muda.

### Arquivos criados
- `frontend/src/environments/environment.ts` — `apiBaseUrl` centralizado
  (`http://localhost:8080/api/v1`) e flag `useMocks`.
- `frontend/src/app/services/api-envelope.ts` — tipo genérico `ApiEnvelope<T>`
  (`{ success, data, error }`) e `ApiError` (`code`, `message`, `fields?`,
  `traceId?`), com helpers `apiSuccess`/`apiError`. Centraliza o formato de
  resposta pra não duplicar em cada service.
- `frontend/src/app/services/auth.model.ts` — interfaces do domínio auth:
  `RegistroRequest`, `UsuarioResponse`, `LoginRequest`, `LoginResponse`,
  `UsuarioResumo` (sem email — usado em login/registro), `UsuarioMe` (com
  email — só para `/usuarios/me`), `EsqueciSenhaRequest`,
  `RedefinirSenhaRequest`, `MensagemResponse`.
- `frontend/src/app/services/auth.mock.ts` — base mockada em memória de
  usuários (com senha em claro só porque é mock local, nunca faria isso contra
  API real), helpers de busca por apelido/e-mail e geração de token fake.
- `frontend/src/app/services/cms.model.ts` — `CmsBloco` (union
  `CmsBlocoTexto | CmsBlocoImagem`), `CmsPaginaPublica`, `CmsPaginaAdminResumo`,
  `CmsPaginaAdminDetalhe`, `CmsPaginaUpdateRequest`, `CmsPaginaCreateRequest`.
- `frontend/src/app/services/cms.mock.ts` — duas páginas fictícias
  (`sobre-nos`, `como-funciona`) com blocos de texto e imagem.
- `frontend/src/app/services/cms.ts` — service com `getPaginaPublica`,
  `listarPaginasAdmin`, `getPaginaAdmin`, `atualizarPagina`, `criarPagina`.

### Arquivos reescritos
- `frontend/src/app/services/auth.ts` — reescrito do zero. Antes tinha uma
  assinatura própria (`usuarioOuEmail`/`email` sem apelido, resposta com
  `error: string`) que não batia com o contrato fechado. Agora expõe:
  `registrar`, `login`, `esqueciSenha`, `redefinirSenha`,
  `buscarUsuarioLogado`, `logout`. Mantém o `signal` público `usuarioAtual`
  (tipo `UsuarioResumo | null`) que os componentes já consumiam.
- `frontend/src/app/app.config.ts` — adicionado `provideHttpClient()` (não
  existia nenhum provider de HTTP no projeto até agora).
- `frontend/src/app/pages/minha-conta/minha-conta.ts` e `.html` — ajuste
  mínimo de compatibilidade (não é mudança de UX/layout, só a integração com
  a nova assinatura do service): `login()` passou a receber `identificador`
  em vez de `usuarioOuEmail`, `register()` virou `registrar()` e passou a
  exigir `apelido` (novo campo `registroApelido` no componente, ainda **sem
  input correspondente no formulário HTML** — pendência sinalizada abaixo),
  leitura de erro passou de `resposta.error` (string) para
  `resposta.error?.message` (objeto), e `usuario.identificador` no template
  virou `usuario.apelido`.

## Pendência sinalizada para `ux-ui`/`frontend`
O formulário de registro em `minha-conta.html` só tem campos de e-mail e
senha — não tem campo de **apelido**, que agora é obrigatório no contrato de
registro (`RegistroRequest.apelido`, 3-30 chars sem espaço, único). O
componente já expõe `registroApelido` para bind, mas falta o `<input>`
correspondente no template. Também não há campo de "confirmar senha" no
registro nem telas de "esqueci minha senha" / "redefinir senha" — os services
`esqueciSenha`/`redefinirSenha` já estão prontos e mockados, esperando por
UI. Isso é trabalho de `frontend` (e possivelmente `ux-ui` para desenhar o
fluxo de telas), não coberto nesta tarefa.

## Trade-offs e decisões
- **Sem tabela separada de mocks por HTTP interceptor**: optei por um `if
  (!environment.useMocks) { chamada real } else { mock }` dentro de cada
  método, replicando o padrão que já existia em `relatos.ts`. Alternativa
  seria um `HttpInterceptor` que intercepta e responde com mock — mais
  "realista" (passaria pelo pipeline HTTP de verdade), mas adiciona
  complexidade desnecessária para o estágio atual do projeto (mock local sem
  chance de persistir estado entre reloads de página de qualquer forma).
- **UsuarioResumo vs UsuarioMe como dois tipos diferentes**: deliberado,
  reflete a regra de anonimato do CLAUDE.md — email só pode aparecer na
  resposta de `/usuarios/me`. Ter dois tipos TypeScript distintos (em vez de
  um único `Usuario` com `email` opcional) torna impossível, a nível de
  tipo, um componente acidentalmente tentar ler `.email` de uma resposta de
  login/registro que nunca vai ter esse campo.
- **CMS_PAGINAS_MOCK é mutável (array `push`/reatribuição de índice)**: para
  simular efeito de escrita (criar/editar página) dentro da sessão do
  navegador sem precisar de um backend real ainda. Isso é só para dev local
  — não é persistência real, reseta a cada reload. Aceitável para mock.
- **environment.ts único (sem `environment.prod.ts` + `fileReplacements`)**:
  o projeto ainda não tinha pasta `environments/` nem configuração de
  build para trocar ambiente automaticamente. Criei o mínimo necessário
  (um arquivo, uma flag) em vez de montar toda a infraestrutura de
  environments de produção/dev do Angular CLI — isso fica para quando
  `devops` definir de fato o pipeline de deploy/ambientes (ver agente
  `system-design`/`devops`), evita overengineering prematuro.
- Bloco 3 (upload de imagem) não foi mockado nesta rodada por escolha
  explícita — o `imagemId` já é só uma referência opaca no contrato de CMS
  (`CmsBlocoImagem.imagemId`), então o CMS não fica bloqueado por isso;
  quando o service de upload for implementado, só populamos
  `imagemId`/`url` reais nos blocos.

## Onde no projeto
- `frontend/src/environments/environment.ts`
- `frontend/src/app/services/api-envelope.ts`
- `frontend/src/app/services/auth.ts`, `auth.model.ts`, `auth.mock.ts`
- `frontend/src/app/services/cms.ts`, `cms.model.ts`, `cms.mock.ts`
- `frontend/src/app/app.config.ts`
- `frontend/src/app/pages/minha-conta/minha-conta.ts` e `.html` (ajuste de
  integração, não de UX)
- Contrato de referência: `backend/docs/api/contrato-auth-cms-upload.md`
- Build validado com `npx tsc --noEmit` e `npx ng build --configuration
  development` (ambos passando, um warning pré-existente não relacionado em
  `reunioes.ts`).

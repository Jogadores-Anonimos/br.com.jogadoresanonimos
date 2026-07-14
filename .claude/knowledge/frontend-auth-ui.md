## [2026-07-12] UI do fluxo de autenticação implementada (frontend)

- **O que foi feito**: Implementadas as telas que faltavam do fluxo de
  autenticação, conforme spec fechada pelo `ux-ui` e contrato de API já
  documentado em `backend/docs/api/contrato-auth-cms-upload.md` (Bloco 1).
  Nenhuma chamada HTTP foi escrita à mão — tudo consumindo o service
  `Auth` (`frontend/src/app/services/auth.ts`) e os tipos de
  `frontend/src/app/services/auth.model.ts`, que já existiam prontos
  (inclusive com mocks) e não foram alterados.

- **Onde**:
  - `frontend/src/app/pages/minha-conta/minha-conta.ts` /
    `.html` / `.css` — form de registro ganhou os campos que faltavam:
    `apelido` (obrigatório, 3-30 caracteres, sem espaço — validação HTML5
    `minlength`/`maxlength`/`pattern` + mensagem amigável) e
    `confirmarSenha` (compara com a senha em tempo real via Angular
    template, além do backend/mock validar de novo). Mensagens de erro
    por campo agora usam `error.fields` do envelope de resposta
    (`resposta.error?.fields`), mapeando pra span de erro sob cada input
    — antes o componente ignorava esse dado do contrato. O campo de login
    já aceitava `identificador` (apelido OU e-mail) no nível do
    TypeScript/service; só o rótulo/placeholder do input ainda dizia
    "Usuário ou e-mail" de forma um pouco ambígua — troquei o label para
    "Apelido ou e-mail" e adicionei um placeholder explicando. O link
    "Esqueci minha senha" era um `<a href="#">` morto
    (`$event.preventDefault()` sem navegação nenhuma) — agora aponta via
    `routerLink` para a nova rota `/esqueci-senha`.
  - `frontend/src/app/pages/esqueci-senha/` (novo: `.ts`/`.html`/`.css`) —
    página nova com form de campo único "apelido ou e-mail", chama
    `authService.esqueciSenha`. Importante: a tela **sempre** mostra a
    mesma mensagem de sucesso genérica que vem em `resposta.data.mensagem`
    (contrato 1.3 é explícito: nunca diferenciar conta existente de
    inexistente, isso é anti-enumeration deliberado) — o componente não
    tem nenhum branch que dependa de "a conta existe ou não", só de
    `resposta.success` (erro só aparece por motivo técnico, tipo
    validação de campo vazio ou rate limit).
  - `frontend/src/app/pages/redefinir-senha/` (novo: `.ts`/`.html`/`.css`)
    — página nova. Lê o token da URL via `ActivatedRoute` (`?token=...`,
    query param, nunca em rota fixa) na própria construção do componente.
    Se o token estiver ausente na URL, a tela já entra num estado de erro
    ("link inválido ou expirado") sem nem mostrar o form, com link para
    solicitar um novo em `/esqueci-senha`. Se o backend/mock devolver 401
    (token inválido/expirado/já usado), a mensagem mostrada é a mesma
    genérica do contrato 1.4 — o componente não tenta adivinhar qual dos
    três motivos ocorreu, só repassa `resposta.error?.message` (que o
    service já centraliza).
  - `frontend/src/app/app.routes.ts` — duas rotas novas, standalone,
    seguindo o padrão já usado no arquivo (`{ path, component }`, sem
    guard porque ambas são públicas por design do contrato):
    `esqueci-senha` → `EsqueciSenha`, `redefinir-senha` → `RedefinirSenha`.

- **Padrão seguido**: todas as páginas novas usam o componente
  `ResourceLayout` já existente (`frontend/src/app/components/resource-layout/`)
  com `[mostrarSidebar]="false"`, igual ao padrão que `minha-conta` já usava
  — evita reinventar breadcrumb/banner/estrutura semântica
  (`<main>`/`<article>`/landmarks) que o layout compartilhado já resolve.
  Todo CSS novo usa só variáveis já existentes em `styles.css`
  (`--color-*`, `--spacing-*`, `--radius-*`, `--shadow-*`,
  `--cor-padrao-ja`, `--yellow-btn`/`--yellow-gold`) — nenhuma cor/fonte
  hardcoded foi introduzida, só reaproveitando os mesmos tokens que
  `minha-conta.css` já usava para os cards de login/registro.

- **Trade-off/decisão notável**: a validação client-side (HTML5
  `required`/`minlength`/`maxlength`/`pattern`, mais comparação de senha
  em tempo real no template) é só uma camada de UX para feedback rápido —
  ela não substitui a validação do service/backend (`error.fields`), que
  continua sendo a fonte de verdade exibida quando ela existir. Isso
  evita duplicar regra de negócio no componente (ex: o "mínimo 10
  caracteres" já está no service `Auth.validarRegistro()` do mock) — o
  componente só usa os mesmos números (10, 3, 30) para o form ficar
  responsivo antes do round-trip, mas sempre prioriza a mensagem que vier
  de `error.fields` quando presente.

- **Não fiz/não mudei**: não toquei em `auth.ts`, `auth.model.ts`,
  `auth.mock.ts` nem em nenhum service — só consumi. Não criei guard de
  rota para `/esqueci-senha`/`/redefinir-senha` porque o contrato marca as
  duas como rotas públicas sem autenticação. Não fiz commit (regra do
  projeto).

- **Build**: `ng build --configuration development` passou limpo (só um
  warning pré-existente e não relacionado em `reunioes.ts`, não introduzido
  por esta tarefa).

- **Pendência para outro agente**: nenhuma nova — o backend real desses
  endpoints ainda não existe (mock via `environment.useMocks`), então
  quando o `backend` implementar `/auth/esqueci-senha` e
  `/auth/redefinir-senha` de verdade, a UI já está pronta para consumir
  sem mudança (o service já tem os dois branches, mock e HTTP real).

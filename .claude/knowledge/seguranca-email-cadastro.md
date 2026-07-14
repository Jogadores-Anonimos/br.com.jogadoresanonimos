# Isolamento de e-mail no cadastro (decisão de segurança, 2026-07-12)

## Contexto
O projeto decidiu (seção 4 de "Decisões de Sistemas.md") coletar e-mail no
cadastro do usuário, como exceção deliberada à regra geral de anonimato do
projeto. Login continua sendo apelido + senha; o e-mail serve só para dois
propósitos: recuperação de conta e envio de itens de loja. Esta nota fecha
as pendências 3 e 4 da seção 11 do mesmo documento: como o e-mail fica
isolado dos dados de participação/relatos, e se ele substitui a "chave de
recuperação exibida uma vez" que era o mecanismo antigo planejado.

## Decisão 1: modelo de dados — sem tabela separada
O e-mail fica na própria tabela `usuario` (coluna `email`, `VARCHAR NOT
NULL UNIQUE`), e não em uma tabela separada tipo `usuario_contato`.

Motivo: separar em tabela própria só vale a pena quando existe necessidade
real de controle de acesso diferenciado por coluna, ou quando a informação
é consultada em contextos diferentes com frequência (JOIN opcional). Não é
o caso aqui — o e-mail é sempre 1:1 com o dono da conta, e as tabelas de
relato/presença/reunião nunca precisam (nem devem) fazer JOIN com o e-mail
do usuário. O isolamento real de segurança não vem do desenho de schema, e
sim de uma regra de aplicação: **nenhuma query, log ou resposta de API
relacionada a relato/presença/reunião pode carregar ou serializar a coluna
`email`.**

Isso é garantido na prática assim:
- Todo endpoint relacionado a relato/presença/reunião usa DTOs/projeções
  que nunca incluem `email` (ex: `UsuarioPublicoDTO`), nunca a entidade
  `Usuario` completa.
- As queries desses fluxos usam `SELECT` explícito das colunas
  necessárias, não a entidade inteira — evita vazar o campo por acidente
  numa serialização Jackson desatenta.
- `email` só aparece em dois lugares do sistema: a tela de "meus dados" do
  próprio usuário autenticado (`/api/v1/usuarios/me`, self-service) e o
  service interno que dispara e-mails (recuperação de senha, notificação
  de envio de item da loja).
- E-mail entra na mesma lista de campos banidos de log que já existe para
  apelido, IP e conteúdo de relato (seção 10 do doc de decisões) — deve
  estar na lista de rotas/campos excluídos do filtro de log HTTP.

Se no futuro surgir motivo concreto para separar fisicamente (ex:
criptografia de coluna isolada, ou dar acesso ao e-mail para um serviço
terceiro de disparo — SES/Postmark — sem dar acesso ao resto da tabela
`usuario`), aí sim vale extrair uma tabela `usuario_contato(usuario_id PK
FK, email)`. Não foi antecipado agora (YAGNI): não há necessidade real
hoje, e adicionar a tabela sem motivo só aumenta complexidade de JOIN em
todo lugar que hoje só olha `usuario`.

Sobre criptografia: e-mail fica em texto plano na coluna (protegido por
TLS em trânsito e acesso restrito via DTO/query) — não recebeu
criptografia simétrica adicional em repouso no MVP, porque precisa ser
recuperável para reenvio (não é senha, não pode ser só hash) e não é
considerado o dado "crítico" do projeto (esse posto é do relato/
participação). Fica como upgrade incremental futuro se `security`/`po`
quiser postura mais forte (ex: `pgcrypto` ou cripto na aplicação com chave
via secret de ambiente).

## Decisão 2: chave de recuperação é abandonada, e-mail é o único mecanismo
O mecanismo antigo de "chave de recuperação exibida uma vez" (o usuário
salva um código no cadastro, único jeito de recuperar a conta depois) foi
**descartado** em favor do e-mail. Os dois mecanismos não convivem.

Motivo do trade-off: a chave de recuperação exibida uma vez é o padrão
certo quando não se quer coletar nenhum dado de contato — preserva
anonimato total, mas transfere para o usuário o ônus de guardar um
segredo. Para o público deste projeto (pessoa em situação vulnerável,
muitas vezes em navegação privada ou dispositivo compartilhado/público),
a taxa de perda desse tipo de chave tende a ser alta, e não há como
recuperar uma chave perdida sem outro canal — vira fricção de suporte sem
solução. Como o projeto já decidiu abrir mão do anonimato nesse ponto
específico para ter e-mail, manter os dois mecanismos em paralelo não
reduz risco nenhum, só duplica complexidade (dois fluxos de "esqueci
senha", duas telas de UX para explicar) — e na prática o usuário que
perde a chave cairia no fluxo de e-mail mesmo assim, então o e-mail já é o
fallback real. Faz mais sentido ele ser o único caminho.

Fluxo de "esqueci minha senha" definido: usuário informa apelido ou
e-mail → backend gera token de reset de curta duração (~30min) → só o
hash do token é armazenado (nunca o token puro, mesmo padrão de qualquer
token sensível do projeto) → link de reset é enviado por e-mail →
resposta ao cliente é sempre genérica ("se o e-mail existir, enviamos um
link"), nunca confirmando se a conta/e-mail existe — consistente com a
regra já definida de mensagens de erro genéricas em autenticação (evita
enumeration de contas).

Ação pendente para `ux-ui`/`frontend`: confirmar que não existe nenhuma
tela feita/planejada para exibir a "chave de recuperação" — se existir
menção residual em algum lugar do design, remover.

## Onde no projeto
- Decisão registrada em `Decisoes de Sistemas.md`, seções 4 e 11
  (pendências 3 e 4), documento de trabalho ainda não aprovado/aplicado ao
  `CLAUDE.md` — só o `po` formaliza isso após aprovação do usuário.
- Modelo de tabela e DTOs a implementar pelo `backend` quando o fluxo for
  desenhado (SDD antes do código, como de praxe no projeto).

## Atualização 2026-07-12: login por apelido OU e-mail (requisito novo)
Requisito mudou em fase de design: login deixa de ser só apelido+senha,
passa a aceitar **apelido OU e-mail** como identificador, mesma senha.
Isso não invalida as decisões acima, mas exige dois ajustes.

### 1. E-mail vira dado ativo de autenticação, não só passivo — schema não muda
Antes, e-mail era só "dado de contato" (recuperação + loja), nunca tocado
por uma query de autenticação. Agora ele participa direto do fluxo de
login: `SELECT ... FROM usuario WHERE apelido = :id OR email = :id`.

Isso **não muda** a Decisão 1 (sem tabela separada `usuario_contato`).
Justificativa: o motivo de manter e-mail na própria tabela `usuario`
nunca foi "ele é passivo" — foi "é 1:1 com o dono da conta e não deve
vazar para os fluxos de relato/presença/reunião". Isso continua
verdadeiro: a query de login toca a tabela `usuario` (que já é o lugar
onde `email` mora), não os fluxos de relato/presença, então o isolamento
que importa (DTOs sem `email` em relato/presença/reunião, `email` nunca
serializado fora de `/usuarios/me` e do service de disparo) continua
intacto. O que muda é só que agora existe **mais um ponto de acesso
legítimo** à coluna `email`: o service de autenticação. Não é motivo para
separar tabela — se qualquer coisa, reforça que `email` é parte do
"núcleo de conta" (junto com `apelido`/hash de senha), então faz ainda
mais sentido ele ficar ao lado deles na mesma tabela.

Requisito de schema pro `backend`: **índice único em `email`** (`UNIQUE`
já cobre unicidade; garantir que existe índice de busca eficiente — em
Postgres, `UNIQUE` já cria índice B-tree automaticamente, então só
confirmar que a constraint existe, não precisa de índice adicional
manual). Sem isso a query `OR` do login faz seq scan em tabela grande.

### 2. Rate limiting e anti-enumeration cobrem e-mail também
Regra antiga de rate limiting de login (token bucket por identificador de
tentativa, ver política geral do agente `security`) cobria só apelido.
Agora precisa cobrir e-mail como identificador alternativo de tentativa,
senão dá pra contornar rate limit de um apelido tentando o mesmo alvo
pelo e-mail (ou vice-versa) — os dois precisam contar pro mesmo bucket
lógico da conta-alvo, não buckets independentes. Recomendação: bucket
chaveado pelo `usuario_id` resolvido internamente (se apelido ou e-mail
bater com um usuário existente) **e** um bucket adicional por
apelido/e-mail bruto normalizado (lowercase/trim) para cobrir o caso de
tentativa contra identificador que não existe (enumeration/brute-force
de descoberta de conta) — mesmo padrão que já vale pra apelido, só
estendido pro segundo campo.

Log de tentativa de login falha: mesma regra que já vale para apelido
**se aplica a e-mail** — nunca logar e-mail em texto puro, nem em log de
erro, nem em log de auditoria de rate limit. Usar hash não-reversível
(mesmo mecanismo já definido para apelido, ex: HMAC com chave de
aplicação, truncado, só para correlação técnica de eventos — nunca para
reidentificar). Mensagem de erro ao cliente continua genérica
("apelido/e-mail ou senha inválidos"), nunca revela qual dos dois campos
errou nem se o identificador existe — mesma lógica de anti-enumeration já
aplicada ao fluxo de "esqueci minha senha".

Pendência sinalizada pro `backend`: índice único em `email` (constraint
`UNIQUE` já resolve, só confirmar na migration) e query de login com
`OR` (ou duas tentativas de lookup separadas, apelido e e-mail, dando na
mesma) — decisão de SQL fica com `backend`, aqui só o requisito de
segurança (constraint + não vazar qual campo casou).

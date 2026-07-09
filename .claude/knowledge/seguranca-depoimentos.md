# Política de segurança — Depoimentos (Recursos)

Definida por: agente `security` | Data: 2026-07-08
Status: política aprovada, aguardando implementação (backend ainda não existe;
frontend vai mockar o envio por enquanto)

## Contexto
Página de "Depoimentos" dentro de Recursos (não na Home): relatos de recuperação
de membros. Requisito inegociável do projeto: anonimato em todas as camadas.
Escala: ~300 pedidos de ajuda/dia, ~2.500 req/dia total — soluções simples,
sem overengineering (nada de ZK proofs, TEE, ou infra dedicada de antifraude).

## 1. Dados do formulário

**Pedir:**
- Texto do relato (obrigatório, com limite de tamanho — ex: min 50 / max
  ~4000 caracteres, a definir com ux-ui/po).
- "Nome de exibição" opcional, em texto livre curto (ex: até 30 caracteres),
  com aviso na UI sugerindo primeiro nome/apelido (tradição de AA/GA:
  identificação só por primeiro nome). Se vazio, publicar como "Anônimo" ou
  variação equivalente. Este campo NUNCA é validado contra nome real nem
  cruzado com dado de cadastro.
- Nenhuma vinculação obrigatória a conta de usuário — depoimento pode ser
  enviado por usuário autenticado (sessão logada) ou, se o produto decidir
  permitir, por visitante não autenticado. Se vinculado a usuário
  autenticado internamente (para moderação/exclusão pelo próprio autor),
  o vínculo fica só no banco (FK interna), nunca exposto em resposta de API
  pública nem teste.

**Nunca pedir/aceitar:**
- E-mail, nome completo, telefone, CPF/CNPJ, endereço, geolocalização.
- Qualquer campo de upload de imagem/anexo nesta feature inicial (superfície
  de ataque extra — EXIF, malware, storage — sem necessidade de negócio
  demonstrada; se vier a existir, aplicar a regra já definida no CLAUDE.md
  de stripping de EXIF antes de salvar).

**Backend:** todo campo acima é revalidado no servidor (tipo, tamanho,
whitelist), independente da validação de frontend.

## 2. Identificador técnico anti-abuso

- Usar um **token de sessão opaco** (ex: cookie/localStorage com UUID
  gerado no client, ou token de sessão já existente se usuário autenticado),
  nunca IP, para throttling de envios.
- No backend, o rate limit é aplicado sobre um **hash não reversível
  (ex: HMAC-SHA256 com chave de servidor) do token de sessão**, nunca o
  token em claro nem IP, armazenado separado do conteúdo do depoimento —
  nunca na mesma linha/tabela que liga hash a texto do relato de forma
  permanente após a decisão de moderação.
- Esse hash de sessão é usado só para contar tentativas dentro de uma
  janela de tempo (ex: token bucket, ver política geral do agente
  `security`); não é dado de auditoria de conteúdo, é dado operacional de
  abuso, com TTL curto (expira/purga após a janela de rate limit, ex: 24h),
  não vira histórico permanente vinculado ao depoimento publicado.

## 3. Moderação (fluxo simples, sem identificar remetente)

Status: `pending` -> `approved` | `rejected`.

- Todo depoimento entra como `pending`. Não aparece em nenhuma listagem
  pública nesse estado.
- Só `approved` aparece na página pública de Depoimentos.
- Moderador (papel administrativo do site) vê fila de `pending`, aprova ou
  rejeita; rejeição não precisa (e não deve) registrar motivo vinculado a
  identidade do remetente — só o conteúdo do texto para decisão editorial.
- Se o depoimento for vinculado a um usuário autenticado internamente
  (para permitir que o próprio autor edite/apague depois), a interface de
  moderação **não expõe esse vínculo** ao moderador — moderador julga só o
  texto, não quem mandou. Isso evita que moderação vire ferramenta de
  reidentificação interna.
- Sem e-mail/notificação externa de status neste momento (evitaria precisar
  de contato pessoal do usuário, o que contradiria o requisito de
  anonimato); se o produto quiser notificar o autor depois, fazer via
  área logada do próprio site ("meus depoimentos"), nunca por e-mail/SMS.

## 4. Riscos de reidentificação indireta (sinalizar, não implementar)

- Texto livre pode conter informação que identifica o autor (nome de
  cidade pequena, nome de terceiros, detalhes muito específicos de
  trabalho/família). Isso é conteúdo, responsabilidade editorial do
  usuário final e da moderação humana — mas cabe a UI (decisão do
  `ux-ui`) exibir um aviso curto perto do campo de texto, ex: "Evite
  citar nomes completos, empresas ou locais que possam te identificar."
  Sinalizando aqui para o `ux-ui` avaliar; não é decisão de `security`
  implementar a cópia final.
- Moderador deve ter diretriz (fora do escopo técnico, é diretriz de
  conteúdo/PO) para rejeitar ou pedir edição de relatos excessivamente
  identificáveis antes de aprovar.

## 5. Anti-abuso / rate limiting (baixa escala, sem overengineering)

- Rate limit por hash de token de sessão (nunca IP, ver item 2): ex.
  token bucket com poucas submissões por janela (ex: 3 por hora, 5 por
  dia — números de negócio a calibrar, não é bloqueio técnico rígido).
- Endpoint de criação de depoimento é público e "caro" o suficiente
  (grava no banco, aguarda moderação humana) para justificar limite mais
  agressivo que leitura, seguindo o padrão já definido na política geral
  de rate limiting do projeto.
- Nenhuma necessidade de CAPTCHA/infra de terceiros dado o volume baixo
  do projeto (~2.500 req/dia totais) — reavaliar só se houver abuso real
  observado; não adicionar dependência de terceiro (ex: reCAPTCHA do
  Google) sem antes confirmar com o usuário/po, pois isso adiciona
  rastreamento de terceiro que o projeto explicitamente evita ("sem
  analytics de terceiros que rastreiem usuário individualmente").

## Trade-offs / motivo resumido
- Evitou-se qualquer campo de contato (e-mail/telefone) mesmo sendo prática
  comum em formulários de depoimento de outros sites, porque conflita com
  anonimato — trade-off aceito: não há como o site "confirmar" identidade
  ou reengajar o autor por fora, moderação e eventual edição futura do
  próprio relato precisam acontecer dentro da área logada do site.
- Rate limit por token de sessão hasheado em vez de IP: troca uma
  proteção anti-abuso ligeiramente mais forte (IP correlaciona abuso
  entre sessões/dispositivos) por preservar o requisito central do
  projeto; mitigado pela baixa escala esperada (abuso em massa é menos
  provável e mais fácil de mitigar manualmente nesse volume).
- Vínculo opcional autor-depoimento fica só interno (FK), nunca exposto
  a moderador nem em resposta de API pública — decisão de mitigar
  "moderação vira ferramenta de vigilância interna".

## Próximos passos (quando backend for implementado)
- `backend`: modelar tabela de depoimentos com status enum, sem colunas de
  identificação direta; hash de sessão para rate limit em estrutura/TTL
  separada (ex: cache Redis ou tabela com purga automática), não em coluna
  permanente junto ao relato.
- `frontend-services`: contrato do formulário (mock por enquanto) já deve
  refletir os campos definidos aqui (texto + nome de exibição opcional,
  nada mais) para não gerar retrabalho quando o backend real existir.
- `ux-ui`: avaliar copy de aviso contra auto-exposição (item 4) e UX do
  "nome de exibição" opcional.

## [2026-07-10] Bug de indentação em toggles aninhados no Notion (professor)
- **O que foi feito/decidido**: Descoberto e corrigido bug sistemático nas páginas
  criadas/editadas pelo `professor` via MCP do Notion (`notion-update-page`,
  `notion-create-pages`) nesta sessão: toggle-filho dentro de outro toggle
  (2º nível de aninhamento pra baixo) renderizava **vazio** no Notion
  ("Empty toggle. Click or drop blocks inside."), com o conteúdo real
  aparecendo como bloco solto logo depois, fora do toggle — quebrando a
  hierarquia visual da página.
- **Causa raiz**: regra de indentação do Notion-flavored Markdown (spec
  `notion://docs/enhanced-markdown-spec`) exige que o **conteúdo de um
  heading-toggle tenha uma tab a mais que o próprio heading**, sempre,
  recursivamente. O erro cometido foi manter o conteúdo de um toggle-filho
  na MESMA profundidade de tab que o heading desse toggle-filho, em vez de
  incrementar mais uma tab. Toggles de 1º nível (direto na raiz da página)
  não sofriam o bug por coincidência (0 tabs de heading + 1 tab de
  conteúdo já bate com a regra); o problema só aparece a partir do 2º
  nível de aninhamento (toggle dentro de toggle).
  ```
  # ERRADO — conteúdo de "Subtópico" na mesma profundidade do heading dele
  #### Grupo {toggle="true"}
  	#### Subtópico {toggle="true"}
  	<span>conteúdo</span>

  # CERTO — conteúdo de "Subtópico" uma tab a mais que o heading dele
  #### Grupo {toggle="true"}
  	#### Subtópico {toggle="true"}
  		<span>conteúdo</span>
  ```
- **Verificação obrigatória depois de qualquer edição de toggle aninhado**:
  não confiar só na leitura do texto retornado por `notion-fetch` — CONTAR
  literalmente as tabs antes de cada linha e confirmar que todo
  toggle-filho tem profundidade = profundidade do heading pai + 1. Idealmente,
  pedir confirmação visual ao usuário (ele vê o Notion renderizado; o
  agente não vê) antes de replicar a mesma edição em massa por várias
  páginas — evita repetir um erro estrutural em todas de uma vez.
- **Erro relacionado, também identificado nesta sessão**: nunca digitar
  as sequências de caracteres `\n` ou `\t` como texto literal dentro do
  parâmetro de conteúdo passado pra `notion-update-page`/`notion-create-pages`
  — isso corrompe a página (o backslash some e sobra um `n`/`nt` solto no
  meio do texto, ou o `{toggle="true"}` se desloca). Usar sempre quebra
  de linha e tab **reais** (multi-linha de verdade na chamada da tool),
  nunca escapes digitados.
- **Trade-off ou motivo**: esse tipo de erro é silencioso — o retorno do
  `notion-fetch` mostra o texto com `\t` corretamente presente (porque a
  tab foi de fato salva), então parece certo à primeira vista; só a
  renderização visual do Notion (que o agente não consegue ver sozinho)
  expõe o problema. Por isso a checagem por contagem de tab + confirmação
  do usuário é necessária, não é suficiente confiar no fetch.
- **Onde no código/infra**: `.claude/agents/Professor.md` (seção
  "O padrão de escrita" / regras de uso do MCP do Notion) — candidato a
  virar regra permanente ali, ex: "sempre indentar conteúdo de toggle
  aninhado com +1 tab em relação ao heading pai; nunca digitar `\n`/`\t`
  como texto; confirmar com o usuário após editar toggles de 2º nível
  antes de replicar o mesmo padrão em outras páginas."
- **Ensinado por**: usuário (via screenshot mostrando "Empty toggle" no
  Notion) + investigação direta na sessão (leitura do resource
  `notion://docs/enhanced-markdown-spec` do MCP do Notion).

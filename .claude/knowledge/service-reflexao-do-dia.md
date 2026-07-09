# Feature — Reflexão do Dia (Recursos + preview na Home)

Coordenado por: `po` | Sequência de agentes: `ux-ui` -> `frontend-services`
-> `frontend` -> `code-reviewer` | Data: 2026-07-08/09
Status: **concluída** (spec, service, página, componente de preview e review
com correção aplicada).

## Decisão de UX (`ux-ui`)
- Nome mantido: "Reflexão do Dia", rota `/reflexao-do-dia`, página de
  Recursos usando o layout compartilhado `app-resource-layout` (mesmo
  padrão de literatura/hotlines).
- Fluxo mês -> dia -> texto, adaptado para reduzir fricção em relação à
  referência americana: mês e dia atuais vêm **pré-selecionados
  automaticamente** (calculados da data real do sistema), então a
  reflexão de hoje aparece sem nenhum clique. Navegação para outros
  meses/dias é opcional, sem reload de página.
- Paleta: reaproveitados tokens já existentes no design system —
  `var(--cor-padrao-ja)` (vinho de fundo), `var(--yellow-gold)` (dourado
  dos dias), `var(--white)` (botões de mês) — em vez de copiar hex
  literal da imagem de referência do site americano (Gamblers
  Anonymous). Registrado como coincidência favorável entre tokens já
  existentes e a paleta da referência, não cópia cega de hex.
- Home: card de preview mostrando só a reflexão do dia atual (sem grade
  de navegação), com link para a página completa. Recursos: página
  completa navegável (mês -> dia -> texto).
- Grade de meses/dias responsiva (mobile-first), não replicando a grade
  fixa desktop da imagem original.
- Não existe token de fonte cursiva/decorativa no design system — usado
  `var(--font-brand)` como aproximação nesta implementação, com
  limitação documentada no próprio CSS; sugestão para `ux-ui`/
  design-system avaliarem um token dedicado no futuro se o padrão visual
  se repetir em outra feature.

## Implementação — `frontend-services`

Arquivo: `frontend/src/app/services/reflexao-do-dia.ts`.
`ReflexaoDoDiaService` (`providedIn: 'root'`), 100% mockado (não existe
endpoint no backend ainda).

### Contrato de dados
```ts
interface ReflexaoDoDia {
  mes: number; // 1-12
  dia: number; // 1-31, conforme o mês
  texto: string;
}

interface ReflexaoDoDiaResponse {
  success: boolean;
  data?: ReflexaoDoDia;
  error?: string;
}
```
Segue o padrão `{ success, data, error }` do CLAUDE.md, igual a
`avisos.ts`/`depoimentos.ts`.

### Métodos públicos
- `obterDiasNoMes(): number[]` — array de 12 posições (índice 0 =
  janeiro) com a quantidade de dias de cada mês, para o frontend montar
  a grade de seleção de dias.
- `obterReflexaoDeHoje(): Observable<ReflexaoDoDiaResponse>` — calcula
  mês/dia a partir de `new Date()` do sistema e delega para
  `obterReflexaoPorData`. Usado no preview da Home e como estado
  inicial da página de Recursos.
- `obterReflexaoPorData(mes: number, dia: number): Observable<ReflexaoDoDiaResponse>`
  — busca a reflexão de um mês+dia específico (navegação em Recursos).
  Retorna `success: false` com mensagem de erro para mês/dia inválidos.

Quando o backend existir, a implementação interna troca para
`this.http.get<ReflexaoDoDiaResponse>('/api/reflexao-do-dia/atual')` e
`this.http.get<ReflexaoDoDiaResponse>(\`/api/reflexao-do-dia/${mes}/${dia}\`)`
— a assinatura pública dos 3 métodos não muda. TODO documentado no topo
do arquivo com esses futuros endpoints reais.

### Estrutura do mock
- Array `REFLEXOES_MOCK: ReflexaoDoDia[][]` gerado dinamicamente por
  `gerarMesesDoAno(mesAtual)`, cobrindo as 365 combinações mês+dia
  (respeitando 28-31 dias por mês via constante `DIAS_POR_MES`).
- Apenas o mês atual do sistema recebe 31 textos de reflexão variados e
  plausíveis (`TEXTOS_JULHO`, nome do array mantido do momento original
  de escrita, mesmo após a correção do cálculo do mês — ver seção de
  review abaixo). Os outros 11 meses recebem texto placeholder genérico
  via `textoPlaceholder(dia, mes)`: "Reflexão do dia X de [mês] — texto
  de exemplo enquanto o conteúdo real não é cadastrado."

### Trade-off / simplificação sinalizada
- Fevereiro é tratado sempre com 28 dias (sem considerar ano bissexto),
  já que a modelagem é só mês+dia, sem ano. Documentado em comentário no
  código. Se precisar de precisão em anos bissextos no futuro, revisitar
  com `ux-ui` antes de mudar.

## Implementação — `frontend`

- `frontend/src/app/pages/reflexao-do-dia/` (`.ts`/`.html`/`.css`) —
  página completa. Classe do componente nomeada `ReflexaoDoDiaPage`
  (não `ReflexaoDoDia`), deliberadamente diferente do nome da interface
  de dados `ReflexaoDoDia` exportada pelo service, para evitar colisão
  de identificador entre componente e tipo de domínio relacionado — ver
  lição generalizável em `.claude/knowledge/geral.md`.
  Usa `app-resource-layout`; `aria-pressed`/`role="group"` nos
  seletores de mês/dia; `aria-live="polite"` no container de texto da
  reflexão (acessibilidade para leitores de tela ao trocar de dia sem
  reload).
- `frontend/src/app/components/reflexao-preview/` (`.ts`/`.html`/`.css`)
  — card para a Home, consome só `obterReflexaoDeHoje()`, trunca texto
  em 220 caracteres.
- Edições pontuais em arquivos compartilhados, sem conflito com outras
  features em paralelo na mesma janela (Avisos/Depoimentos/Mural de
  Apoio): `app.routes.ts` (nova rota `/reflexao-do-dia`),
  `resource-layout.html` (novo item de menu), `home.ts`/`home.html`
  (import e tag do preview).

## Achado do `code-reviewer` e correção aplicada

- **MEDIUM**: o mock `REFLEXOES_MOCK` estava gerado com o mês
  hardcoded (`gerarMesesDoAno(7)`, julho, mês de escrita do arquivo) em
  vez de calculado dinamicamente. A partir de agosto, o mock
  "populado com conteúdo real" continuaria fixo em julho, causando
  divergência silenciosa (sem erro) entre o mês que o mock tratava como
  especial e o mês que `obterReflexaoDeHoje()` calculava como atual a
  partir de `new Date()`.
- **Correção aplicada**: `gerarMesesDoAno(new Date().getMonth() + 1)`,
  calculado no carregamento do módulo — consistente com o cálculo já
  usado dentro de `obterReflexaoDeHoje()`.
- **Achados LOW, não bloqueantes, deixados como polish futuro**:
  - Padding hardcoded em vez de `var(--spacing-*)` em
    `reflexao-preview.css`.
  - Ausência de `aria-live` no preview da Home (só a página completa
    recebeu o atributo).

## Observação de contexto (não é decisão técnica, é registro de evento)

A imagem de referência original usada para especificar esta feature
(`Screenshot 2026-07-08 220602.png`) desapareceu do disco entre a
leitura inicial (feita por `po`/`ux-ui`) e uma tentativa de releitura
pelo agente `frontend` — provavelmente um arquivo de paste/temporário
que expirou. A implementação seguiu a descrição textual detalhada da
imagem (fornecida pelo `po` com base na leitura anterior), risco de
desvio visual considerado baixo. Registrado para quem for comparar a UI
final contra a imagem original depois, caso outra cópia estável seja
salva.

## Onde no código
- `frontend/src/app/services/reflexao-do-dia.ts`
- `frontend/src/app/pages/reflexao-do-dia/` (`.ts`, `.html`, `.css`)
- `frontend/src/app/components/reflexao-preview/` (`.ts`, `.html`, `.css`)
- `frontend/src/app/app.routes.ts` (rota `reflexao-do-dia`)
- `frontend/src/app/components/resource-layout/resource-layout.html` (item de nav)
- `frontend/src/app/pages/home/home.ts` / `home.html` (preview na Home)

## Ensinado por
ux-ui, frontend-services, frontend, code-reviewer (coordenados por po)

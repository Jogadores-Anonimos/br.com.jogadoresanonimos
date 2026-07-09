---
paths:
  - "frontend/**/*.css"
  - "frontend/**/*.scss"
  - "frontend/**/*.html"
  - "frontend/**/*.ts"
---

# Design System Rules

## Fonte da verdade
Todas as cores, fontes, espaçamentos e outros tokens visuais deste projeto
vêm de variáveis CSS globais definidas em `frontend/src/styles.css`.
Esse arquivo é a única fonte de verdade para o design system. Tratar o
design system como **contrato**: um componente não "resolve" um problema
visual fora dele — se o token necessário não existe, o token é criado no
arquivo global primeiro, nunca inline no componente.

## Regras obrigatórias

- **Mobile e responsive first.**
- Suporte a quem aumenta a fonte padrão do navegador ou usa zoom: o
  layout não pode quebrar (evitar alturas fixas em elementos com texto,
  preferir unidades relativas onde fizer sentido).
- **Nunca usar valores de cor hardcoded** (hex, rgb, hsl, nomes de cor
  como `blue`) diretamente em componentes ou estilos. Sempre referenciar
  a variável CSS correspondente, ex: `color: var(--color-primary);`.
- **Nunca declarar `font-family` direto no componente.** Usar sempre a
  variável de fonte definida globalmente, ex:
  `font-family: var(--font-body);`.
- Antes de criar ou editar qualquer estilo, **verificar se já existe uma
  variável adequada** em `styles.css` antes de propor uma nova.
- Se uma cor/fonte/espaçamento necessário não existir ainda como
  variável, **perguntar antes de criar um valor novo hardcoded** — a
  variável deve ser adicionada ao arquivo global primeiro, não inline no
  componente.
- Manter nomenclatura semântica nas variáveis (ex: `--color-primary`,
  `--color-danger`, `--color-text-muted`), não nomes literais da cor
  (ex: evitar `--azul-escuro`).
- Espaçamento, raio de borda e sombras também devem vir de variáveis
  (`--spacing-*`, `--radius-*`, `--shadow-*`) quando essas existirem no
  projeto, pelo mesmo motivo: consistência e facilidade de manutenção.
- Seguir fielmente o layout solicitado: se for apresentada uma imagem de
  referência, copiar fielmente (perguntar em caso de dúvida); usar
  criatividade apenas quando não houver referência, sempre dentro das
  regras deste documento.
- Acessibilidade (contraste mínimo adequado ao par de cores usado, foco
  visível) é parte do contrato do design system, não um ajuste à parte —
  ver detalhamento no agente `frontend`.

## Estrutura esperada das variáveis

```css
:root {
  /* Cores */
  --color-primary: #...;
  --color-secondary: #...;
  --color-background: #...;
  --color-text: #...;
  --color-text-muted: #...;
  --color-danger: #...;
  --color-success: #...;

  /* Tipografia */
  --font-heading: ...;
  --font-body: ...;
  --font-size-base: ...;

  /* Espaçamento */
  --spacing-sm: ...;
  --spacing-md: ...;
  --spacing-lg: ...;

  /* Outros */
  --radius-base: ...;
}
```

## Ao criar um componente novo
1. Checar `styles.css` para ver quais tokens já existem.
2. Usar somente `var(--nome-da-variavel)` nos estilos do componente.
3. Se faltar alguma variável necessária, sinalizar isso em vez de
   inventar um valor solto, propor a adição ao arquivo global.
4. Não duplicar definição de cor/fonte em múltiplos arquivos — sempre
   apontar para a mesma variável central.

## Exceções
- Valores puramente utilitários sem impacto visual de marca (ex:
  `z-index` de camadas internas, opacidades técnicas específicas de uma
  animação isolada) podem ficar hardcoded, mas devem ser exceção, não
  regra — e devem vir acompanhados de um comentário curto explicando o
  motivo.
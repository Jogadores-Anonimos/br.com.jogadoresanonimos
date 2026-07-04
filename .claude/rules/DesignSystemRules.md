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
vêm de variáveis CSS globais definidas em
`frontend/src/styles.css`.
Esse arquivo é a única fonte de verdade para o design system.

## Regras obrigatórias

- **Mobile e Responsive first**
- Assistência para quem deixa a fonte padrão grande, ou seja, não quebrar o layout com zoom ou fonte grande.
- **Nunca usar valores de cor hardcoded** (hex, rgb, hsl, nomes de cor como
  `blue`) diretamente em componentes ou estilos. Sempre referenciar a
  variável CSS correspondente, ex: `color: var(--color-primary);`.
- **Nunca declarar `font-family` direto no componente.** Usar sempre a
  variável de fonte definida globalmente, ex: `font-family: var(--font-body);`.
- Antes de criar ou editar qualquer estilo, **verificar se já existe uma
  variável adequada** em `variables.css` antes de propor uma nova.
- Se uma cor/fonte/espaçamento necessário não existir ainda como variável,
  **perguntar antes de criar um valor novo hardcoded**, a variável deve
  ser adicionada ao arquivo global primeiro, não inline no componente.
- Manter nomenclatura semântica nas variáveis (ex: `--color-primary`,
  `--color-danger`, `--color-text-muted`), não nomes literais da cor
  (ex: evitar `--azul-escuro`).
- Espaçamento, raio de borda e sombras também devem vir de variáveis
  (`--spacing-*`, `--radius-*`, `--shadow-*`) quando essas existirem no
  projeto, pelo mesmo motivo: consistência e facilidade de manutenção.
- Seguir fielmente o layout solicitado, por exemplo: se for apresentado uma imagem referência, e for pedido, 
- copie fielmente, mesmo que tenha que fazer perguntas sobre, caso seja pedido algo novo e sem referência, ai sim, 
- use sua criatividade, mas sempre seguindo as regras do design system.

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
1. Checar `variables.css` para ver quais tokens já existem.
2. Usar somente `var(--nome-da-variavel)` nos estilos do componente.
3. Se faltar alguma variável necessária, sinalizar isso em vez de
   inventar um valor solto, propor a adição ao arquivo global.
4. Não duplicar definição de cor/fonte em múltiplos arquivos — sempre
   apontar para a mesma variável central.

## Exceções
- Valores puramente utilitários sem impacto visual de marca (ex:
  `z-index` de camadas internas, opacidades técnicas específicas de uma
  animação isolada) podem ficar hardcoded, mas devem ser exceção, não regra.
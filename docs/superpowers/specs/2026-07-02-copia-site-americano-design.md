# Cópia do site americano (gamblersanonymous.org) para o site da JA Brasil

## Contexto

O primeiro protótipo do site foi rejeitado pelo cliente (ver memória
`project-prototype-reset-2026-07`). O repositório foi reduzido a um shell:
só restam as variáveis de cor em `frontend/src/styles.css` e o componente
`app-barra-deslizante`. A decisão do cliente é reconstruir o site seguindo
o **layout e a estrutura do site americano** (https://gamblersanonymous.org/),
com conteúdo em pt-BR e identidade visual da JA Brasil (cores + logo).

Este é o passo 1 de dois: copiar a estrutura americana. Um passo 2 (fora
deste spec) vai decidir o que fica, o que sai e o que entra de novo,
reaproveitando o que fizer sentido do protótipo antigo.

## Fora de escopo nesta rodada

- Qualquer integração real com backend (ele está desconectado).
- E-commerce funcional da Loja, autenticação real de Minha Conta.
- Páginas do protótipo antigo sem equivalente direto no site americano:
  Relatos (absorvido como seção dentro do Boletim), Contato dedicado,
  Anonimato, Jogo Compulsivo, Reflexão do Dia, Podcast, Painel extra,
  Uma Nova Chance, Gam-Anon (org irmã para familiares, sem equivalente
  conhecido da JAB).
- Dados reais de contato/endereço/telefone/pagamento — ver seção
  "Dados sensíveis" abaixo.

## Ação técnica preliminar

- Restaurar `frontend/src/public/images/header/logo-jogadores-anonimos.png`
  do commit `5455693` (era só asset visual, não parte do design rejeitado).
- Remover `<app-barra-deslizante>` de `frontend/src/app/app.html` — o
  componente e seus arquivos continuam existindo, só não fica montado
  globalmente por enquanto.

## Estrutura de navegação global

Baseada 1:1 na navegação real do site americano (levantada via fetch de
`gamblersanonymous.org` e de cada subpágina):

**Barra superior:** Contribuição · Loja · Carrinho (placeholder) + botões
destacados "Encontrar Reunião" e "Hotlines Brasil".

**Nav principal:**
`Home` | **Sobre** (dropdown: `História`) | **Recursos** (dropdown:
`Literatura`, `Programa de Recuperação`, `Programa de Unidade`,
`Perguntas & Respostas`, `20 Perguntas`, `Escritórios Regionais`,
`Boletim Informativo`) | `Loja` | `Minha Conta`

**Footer:** bloco "Recursos" (repete links do Recursos), bloco Contato
(telefone/fax/e-mail com placeholder), endereço (placeholder), copyright
JA Brasil 2026, links "Política de Privacidade" e "Termos de Uso".

## Páginas — conteúdo real levantado do site americano e adaptação

Cada página abaixo foi verificada individualmente (fetch da página real,
não só do menu). A coluna "Fonte pt-BR" indica se o texto já existe no
protótipo antigo (reaproveitar, adaptando ao novo layout) ou é novo.

| Rota | Página US correspondente | Estrutura real encontrada | Fonte pt-BR |
|---|---|---|---|
| `/` | Home | Hero com pergunta + "There is Hope", intro da fraternidade, CTA "Start your recovery journey" (2 botões), teaser 20 Perguntas, links rápidos, teaser Boletim | Adaptar do zero, tom da home americana |
| `/historia` | About + History | 2 páginas no US (About e History) fundidas em 1: definição da fraternidade, único requisito é vontade de parar, textos sobre negação/perda de controle/prática diária dos princípios, história real (origem jan/1957, 2 fundadores, 1ª reunião 13/set/1957 em Los Angeles) | Reaproveita texto de `quem-somos`; história do fundador da JAB precisa ser confirmada com o cliente — placeholder onde a data/fundação da JAB não for conhecida |
| `/programa-de-recuperacao` | Recovery Program | Lista dos 12 Passos (texto oficial GA, é conteúdo de programa público, não específico dos EUA) | Reaproveita texto de `doze-passos` (já tem os 12 passos em pt-BR) |
| `/programa-de-unidade` | Unity Program | Lista de 12 Princípios de Unidade (equivalente às 12 Tradições) + CTAs | Novo — traduzir os 12 princípios (conteúdo de programa público), sinalizar `[revisar tradução oficial com o cliente]` |
| `/perguntas-e-respostas` | Questions & Answers | 17 perguntas/respostas temáticas (definição de jogo compulsivo, primeiros passos de recuperação, intervenção, estrutura organizacional) | Novo — adaptar as perguntas de forma genérica (não são dado sensível, são conteúdo educativo) |
| `/vinte-perguntas` | 20 Questions | Questionário de autoavaliação, critério "7 ou mais sins = provável jogador compulsivo" | Reaproveita quiz de `autoaval` (já tem as 20 perguntas em pt-BR e a lógica), só reestiliza pro novo layout |
| `/literatura` | Gamblers Anonymous Literature | Grid de cards com ~14 publicações (livros, panfletos, cadernos de passos) | Novo — grid com títulos análogos, marcar `[confirmar catálogo real da JAB]` onde não houver certeza |
| `/escritorios-regionais` | Regional Service Offices | Lista de escritórios regionais + endereços de intergrupos | Novo — estrutura pronta para ERSJAB/JAERJ, endereços `[a definir]` |
| `/boletim-informativo` | Life-Line Bulletin | Newsletter mensal, arquivo de edições em PDF por mês, seção de notícias de grupo com relatos de membros | Novo — estrutura de arquivo de edições (vazia por ora) + seção "relatos de membros" absorvendo o conceito da antiga página `relatos` |
| `/reunioes` | Find a Meeting | 4 filtros: Presencial, Internacional/Nacional, Virtual, Telefone (sem mapa/busca textual) | Reaproveita `reunioes`, dados de reunião `[a definir]` |
| `/hotlines` | U.S. Hotlines | Lista por estado, telefone por região, "sem hotline no momento" onde não houver | Novo — estrutura por estado brasileiro, números `[a definir]` |
| `/contribuir` | Make a Contribution | Texto sobre autossustento financeiro (Tradição 7, conteúdo de programa público) + métodos de pagamento (PayPal/Venmo/Zelle/cheque) + checagem "você é membro?" | Novo — mesmo texto de autossustento, métodos de pagamento adaptados (ex: Pix) `[a definir]` |
| `/loja` | Shop | Grid de produtos com preço e "Add to cart", categorias na lateral, paginação | Placeholder estático "em breve", sem carrinho funcional |
| `/minha-conta` | My Account | Tela combinada login (usuário/senha, lembrar-me, esqueci senha) + registro (email/senha) | Placeholder estático com o mesmo formulário visual; sem autenticação real — service mockado (ver Padrão técnico) |
| `/privacidade`, `/termos` | footer links | — | Novo, texto placeholder mencionando intenção de conformidade com LGPD |

## Dados sensíveis / conteúdo que não pode ser inventado

Telefones, endereços, e-mails de contato, hotlines por região, dados de
pagamento e catálogo de literatura real da JAB **não devem ser inventados**.
Onde o site americano mostra um dado real (ex: telefone do ISO, endereço
de Upland CA), a versão JAB usa um placeholder visível, por exemplo:
`(11) 0000-0000 — a definir` ou `[endereço a confirmar]`. Conteúdo de
programa (12 Passos, 12 Princípios de Unidade, critério das 20 Perguntas)
não é dado sensível — é material público da irmandade, safe para traduzir.

## Visual

- Mantém as variáveis já existentes em `styles.css` (`--cor-padrao-ja`,
  `--yellow`, `--yellow-btn`, `--yellow-gold`, `--white`, `--black`) sem
  redefini-las.
- Adiciona variáveis semânticas novas necessárias para nav/footer/páginas
  (cinzas de fundo, cores de texto, `--nav-height`, espaçamento, raio de
  borda), seguindo a convenção do `.claude/rules/DesignSystemRules.md`
  (nomes semânticos, nunca cor solta em componente).
- Logo: `logo-jogadores-anonimos.png` restaurado do histórico do git.

## Padrão técnico

Por convenção do `CLAUDE.md`: cada rota acima = 1 componente Angular
standalone em `frontend/src/app/pages/<nome>/` + entrada em
`app.routes.ts`. Onde a página tiver formulário (Minha Conta), cria-se um
`service` em `frontend/src/app/services/` com dados **mockados** seguindo
a mesma interface que a API real vai ter futuramente (o backend está
desconectado). Páginas puramente de conteúdo estático não precisam de
service.

## Fora de escopo — não confundir com "não vai existir nunca"

Os itens listados em "Fora de escopo nesta rodada" e os placeholders de
dados reais serão revisitados na segunda etapa (definir o que fica, o que
sai, o que vem de novo), depois que esta cópia estiver no ar para o
cliente avaliar.

# Sessão 2026-07-12 — Construção do Dashboard Admin

> Registro da sessão pra retomar contexto depois. Não é fonte de verdade
> de pendências (isso é `Pendencias Admin Dashboard.md`) — é o "o que
> rolou e por quê" pra não perder o fio.

## Como começou

Sessão começou testando Docker (Docker Desktop precisou ser iniciado
manualmente). Ao validar o backend subindo, apareceu um WARN de
`UserDetailsServiceAutoConfiguration` gerando usuário fantasma com senha
aleatória — risco latente (auth é 100% JWT customizado, não usava esse
autoconfig, mas ele ficava ativo mesmo assim). Corrigido excluindo o
autoconfig explicitamente na classe principal.

## Como o escopo cresceu

Pergunta inicial era simples: "já temos dashboard admin?". Resposta era
não — só CMS genérico existia. Levantamento foi expandindo em várias
rodadas conforme o usuário foi lembrando de mais partes:

1. Primeiro só CMS de página + relatos.
2. Usuário corrigiu: sem criar/excluir página, sem mudar layout — só
   editar conteúdo existente.
3. Usuário lembrou: relatos precisa de moderação de verdade (aprovar/
   editar/excluir), backend não tinha nada disso.
4. Usuário adicionou: reuniões, escritórios/intergrupos, mural de
   apoio, reflexão do dia, literatura, avisos, hotlines, perguntas e
   respostas, usuários — tudo precisa ser gerenciável. Loja fica de
   fora (standby).
5. Investigação técnica revelou que **nenhuma dessas áreas tinha
   qualquer backend** — tudo mock/hardcoded no frontend.
6. Investigação revelou que **as 13 páginas institucionais também não
   usavam CMS** (suposição inicial errada) — eram HTML/array hardcoded.
7. Usuário adicionou escopo de usuário comum: gerenciar PDFs próprios
   (biblioteca pessoal), edição de detalhes da conta.
8. Usuário confirmou: admin editando arquivo de literatura precisa
   poder editar metadados sem excluir/reenviar.
9. Usuário confirmou: hotlines precisa CRUD completo (múltiplos
   telefones por região, criar/excluir região).

Lição: **o levantamento inicial nunca cobriu o site inteiro** — foi
precisa insistência do usuário, várias rodadas, pra chegar no escopo
real. Da próxima vez, perguntar explicitamente "isso cobre todas as
páginas do site?" antes de considerar um levantamento fechado.

## Decisões arquiteturais importantes

- **`system-design`**: nenhuma das 9 áreas novas reaproveita o modelo
  `CmsBloco` genérico — todas viram entidade de domínio própria, porque
  cada uma tem campo estruturado/status/hierarquia que o bloco genérico
  não atende sem virar parsing contorcido. CMS genérico fica só pra
  conteúdo de página institucional (texto/imagem) e literatura.
- **`ux-ui`**: menu do admin em 4 grupos por frequência de uso (não por
  ordem alfabética/técnica); 3 padrões de tela reutilizáveis (lista+
  formulário / drill-down hierárquico / calendário por data); Relatos
  tratado como fluxo de moderação, não CRUD simples; Usuários v1 bem
  conservadora (só listar+editar role); sem dashboard de métricas.

## Execução — o que funcionou bem

- **Paralelizar agentes do mesmo tipo por área independente funcionou
  muito bem** (várias rodadas de 2-4 agentes `backend` em paralelo,
  cada um numa entidade diferente) — reduziu tempo real de forma
  significativa.
- **Reservar número de migration Flyway explicitamente pra cada agente
  em paralelo é obrigatório** — na primeira rodada sem essa instrução,
  dois agentes pegaram o mesmo número (`V7`) e teria quebrado o Flyway
  se não tivesse sido pego a tempo. Depois disso, toda rodada paralela
  recebeu números pré-reservados nas instruções.
- **Validar contra Postgres real via Docker pegou 2 bugs que os testes
  unitários (tudo mockado) não pegavam**: migration `V15` criando
  coluna `dia`/`mes` como `SMALLINT` enquanto a entidade Java usava
  `int` (Hibernate rejeitava na validação de schema — só aparece com
  banco real), e dois endpoints públicos (`GET /relatos`, `GET
  /avisos`) sem `permitAll` no `SecurityConfig` (ficavam bloqueados por
  engano, `anyRequest().authenticated()` pega tudo sem matcher
  explícito). **Testar contra ambiente real antes de considerar algo
  "pronto" continua valendo a pena, mesmo com suíte de teste verde.**

## Estado no fim desta sessão

Ver `Pendencias Admin Dashboard.md`, seção "Estado real agora" — resumo
rápido: backend 100%, frontend-services 100%, páginas institucionais
migradas no frontend, shell de navegação do admin pronto. **Nenhuma
tela de CRUD real do admin foi construída ainda** — todas as 21 rotas
caem em placeholder "em construção". Próximo passo é essa parte.

Pendências técnicas abertas que não bloqueiam o próximo passo mas
precisam de decisão em algum momento:
- Interceptor HTTP de `Authorization` não existe (sessão de login não
  propaga token pras chamadas autenticadas).
- Regressão visual em 4 páginas institucionais migradas (headings/listas
  viraram parágrafo simples por causa da allowlist do `HtmlSanitizer`).
- Aba "Detalhes" de `minha-conta` (editar apelido/e-mail/senha do
  próprio usuário) nunca ganhou endpoint no backend — ficou fora do
  escopo desta rodada de trabalho.

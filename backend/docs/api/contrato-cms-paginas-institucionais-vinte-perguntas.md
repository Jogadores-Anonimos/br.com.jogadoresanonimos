# Contrato de API — Páginas Institucionais (CMS) e Vinte Perguntas

> Status: implementado em 2026-07-12 (tarefa "Páginas institucionais →
> CMS", item 1 da ordem de prioridade em `Pendencias Admin Dashboard.md`).

## Escopo desta entrega

Migração de conteúdo hardcoded do frontend para `CmsPagina`/`CmsBloco`
(infra já existente, ver `contrato-auth-cms-upload.md` bloco 2) para 11
das 13 páginas institucionais listadas no escopo:

sobre, historia, anonimato, privacidade, termos, jogo-compulsivo,
uma-nova-chance, programa-de-recuperacao, programa-de-unidade,
contribuir, vinte-perguntas (só o texto introdutório — as 20 perguntas
em si NÃO são bloco CMS, ver seção dedicada abaixo).

Os endpoints usados são os que **já existem** — nenhuma mudança de
contrato no CMS genérico:
- `GET /api/v1/cms/paginas/{slug}` (público)
- `GET /api/v1/admin/cms/paginas` / `GET /{slug}` / `PUT /{slug}` /
  `POST` (admin)

## Páginas que ficam de fora desta entrega (decisão documentada)

- **`hotlines`**: conteúdo real é lista estruturada por estado/telefone
  (entidade própria, fora de escopo desta tarefa — ver
  `Pendencias Admin Dashboard.md`). O único texto fixo da página é uma
  frase de instrução de UI ("Clique no estado desejado..."), não texto
  institucional — não cria `CmsPagina` para ela agora. Quando a entidade
  `Hotline` for implementada, se for necessário um texto de introdução
  editável, criar a `CmsPagina` nessa tarefa futura.
- **`perguntas-e-respostas`**: mesma lógica — conteúdo real é a lista de
  perguntas/respostas (entidade `Faq` própria, fora de escopo). Único
  texto fixo é o heading "Perguntas Frequentes", sem parágrafo
  institucional — não cria `CmsPagina` agora.

## Seed das 11 páginas — decisão de implementação

**Problema**: `cms_pagina.atualizado_por` é `UUID NOT NULL REFERENCES
usuario(id)`. O único usuário ADMIN do sistema é criado em runtime por
`AdminBootstrap` (`CommandLineRunner`, via env vars
`ADMIN_BOOTSTRAP_*`), não existe em nenhuma migration Flyway. Uma
migration SQL pura (`INSERT INTO cms_pagina ...`) não teria como
satisfazer essa FK de forma determinística (rodar antes do
`AdminBootstrap`, antes de qualquer usuário existir).

**Decisão**: seed feito por um segundo `CommandLineRunner`
(`CmsPaginaSeed`, pacote `cms`), **não por migration SQL**. Roda depois
do `AdminBootstrap` (`@Order` explícito), busca o primeiro usuário
`ADMIN` existente e, se achou um admin, cria (via `CmsService.criar`,
reaproveitando validação/sanitização) qualquer uma das 11 páginas cujo
slug ainda não exista em `cms_pagina`. Idempotente por natureza
(checagem de slug já existente) — seguro rodar em todo boot.
Se não houver nenhum admin ainda (`ADMIN_BOOTSTRAP_*` não configurado),
o seed não roda e loga aviso, mesma postura do `AdminBootstrap`.

Trade-off aceito: o conteúdo dessas páginas só aparece depois do
primeiro boot com admin configurado (não em `db/migration`, que roda
sempre). Para o volume/fase atual do projeto (dev/staging), isso é
aceitável — a alternativa (permitir `atualizado_por` nulo, ou um usuário
"sistema" fixo com UUID fixo por migration) muda o contrato de auditoria
do CMS para todos os outros usos, o que não foi pedido nesta tarefa.
Se o time preferir um usuário "sistema" fixo, é decisão a alinhar com
`security`/`po` antes de mudar (fora do escopo desta tarefa).

## Conteúdo migrado — mapeamento de HTML para blocos

O `HtmlSanitizer` (`cms/util/HtmlSanitizer.java`) permite as tags
`<b> <i> <p> <br> <a href> <h2> <h3> <strong> <ul> <ol> <li>` — a
limitação de `<h2>`/`<ol>`/`<li>` sinalizada originalmente nesta tarefa
foi resolvida (allowlist ampliada pelo `backend`, fora desta entrega).

Convenção usada para migrar o conteúdo hoje em `<h2>`/listas numeradas
sem perder a estrutura:
- Um `<h2>` de seção vira o próprio bloco de texto seguinte prefixado
  com `<p><b>Título</b></p>` seguido dos parágrafos daquela seção — ou,
  quando o título é isolado (ex: CTA final com botão fixo), o título
  **não** migra (é UI fixa do componente, não conteúdo, ver próxima
  seção).
- Listas numeradas (12 passos, 12 princípios) — cada item vira um bloco
  de texto próprio `<p><b>N.</b> texto</p>`, com `ordem` sequencial.
  Reordenável pelo admin como qualquer bloco CMS (não há requisito de
  ordem fixa para esses dois, diferente das 20 perguntas).

## O que NÃO virou bloco CMS (permanece hardcoded no componente)

Elementos de UI/interação fixos continuam no componente Angular — CMS
cobre só o texto narrativo/institucional:
- CTAs finais com botão e `routerLink` fixo (ex: "Encontrar uma
  Reunião", "Fazer uma Contribuição") em `programa-de-recuperacao`,
  `programa-de-unidade`.
- Gate de "você é membro?" e lógica de foco/acessibilidade de modal em
  `contribuir` — só o texto de "formas de contribuição" (com os
  placeholders `[a definir]` que já existiam) virou bloco CMS.
- Todo o mecanismo de quiz de `vinte-perguntas` (perguntas, progresso,
  resultado, botões Sim/Não) — ver seção dedicada abaixo.

## Vinte Perguntas — estrutura dedicada (fora do CMS genérico)

> **Atualização 2026-07-13**: a decisão original ("admin só edita texto,
> nunca cria/remove, `numero` é chave fixa 1-20") foi revertida a pedido
> do usuário. Ver seção "Revisão — perguntas dinâmicas" abaixo para o
> contrato vigente. A modelagem original com `numero` fixo (CHECK
> 1-20, sem `ordem`, sem create/delete) **nunca chegou a ser usada em
> produção**: o `VinteQuestaoSeed`/`CmsPaginaSeed` mencionado ali nunca
> foi implementado, então a tabela `vinte_questao` está vazia desde a
> migration `V8`. Isso também foi a causa raiz de um bug reportado
> separadamente (página pública "sumida"): sem dado nenhum, `GET
> /api/v1/vinte-perguntas` sempre respondeu `200` com `data: []`. **Já
> corrigido** — `frontend` implementou estados de loading/erro/vazio em
> `vinte-perguntas.ts`/`.html` (mensagem explícita quando não há
> pergunta cadastrada, com botão de tentar novamente).

### Revisão — perguntas dinâmicas (vigente)

Requisito novo: admin pode **criar**, **editar**, **remover** e
**reordenar** perguntas livremente (deixa de ser fixo em 20). Isso muda
a natureza da chave: `numero` fixo 1-20 com `CHECK` deixa de fazer
sentido — vira a mesma modelagem de lista ordenável já usada em
`Faq` (`faq/entity/Faq.java`, `common/util/ReordenacaoUtil.java`):
chave real é `id` (UUID), posição de exibição é `ordem` (int, mutável,
sem gap obrigatório, reordenável via mover-cima/mover-baixo).

**Compatibilidade com o quiz público**: o cálculo de resultado no
frontend (`vinte-perguntas.ts`) só depende de `perguntas.length` e da
contagem de respostas "sim" — não depende do valor literal de `numero`
em si, só usa `numero` para ordenar antes de extrair os textos
(`[...data].sort((a,b) => a.numero - b.numero).map(q => q.texto)`).
Decisão: manter o campo `numero` no `VinteQuestaoResponse` por
compatibilidade com esse contrato já consumido, mas ele passa a ser
**derivado da posição na lista ordenada por `ordem`** (1-based,
recalculado a cada listagem), não mais um valor armazenado/estável por
linha. Não é breaking para o consumidor público (mesmo formato JSON),
mas é breaking para quem dependia de `numero` como identificador
estável de uma pergunta específica através de edições — o identificador
estável agora é `id`, exposto também no response.

### Modelagem (migration `V21`, expand/contract sobre `V8`)

```sql
ALTER TABLE vinte_questao DROP CONSTRAINT vinte_questao_numero_check;
ALTER TABLE vinte_questao RENAME COLUMN numero TO ordem;
ALTER TABLE vinte_questao ALTER COLUMN ordem DROP NOT NULL;
ALTER TABLE vinte_questao ALTER COLUMN ordem TYPE INTEGER USING ordem::INTEGER;
UPDATE vinte_questao SET ordem = sub.rn - 1
  FROM (SELECT id, row_number() OVER (ORDER BY ordem) AS rn FROM vinte_questao) sub
  WHERE vinte_questao.id = sub.id;
ALTER TABLE vinte_questao ALTER COLUMN ordem SET NOT NULL;
ALTER TABLE vinte_questao ADD CONSTRAINT vinte_questao_ordem_unique UNIQUE (ordem);
```
Justificativa de não ser puramente aditiva (rename/retype de coluna):
a tabela está **vazia em produção** (seed nunca implementado, confirmado
por busca no código — não existe `VinteQuestaoSeed`/`CmsPaginaSeed`
nenhum lugar). Não há dado real em risco. Se isso mudar antes do deploy
desta migration, alinhar antes de aplicar (regra do agente `backend`
para migration destrutiva). `atualizado_por`/`atualizado_em`
permanecem inalterados.

Índice: `UNIQUE (ordem)` mantido (evita duas perguntas na mesma posição
por bug de concorrência) — volume é pequeno (dezenas de linhas), sem
necessidade de índice composto.

### Entidade `VinteQuestao` (após a mudança)
- `id: UUID` — chave primária, identificador estável exposto à API.
- `ordem: int` — posição de exibição, mutável via mover-cima/baixo ou
  atribuída sequencialmente na criação (`max(ordem) + 1`, mesmo padrão
  do `FaqService.criar`).
- `texto: String` (max 500).
- `atualizadoEm` / `atualizadoPor` — mantidos.
- Sem soft-delete — remoção é `DELETE` físico (consistente com `Faq`).

### Endpoints (contrato vigente)

#### `GET /api/v1/vinte-perguntas` (público) — inalterado na forma
- **Autenticação**: nenhuma. **Idempotente**: sim. **Consistência**:
  leitura direta do primário.
- **Response 200** (`ApiResponse<List<VinteQuestaoResponse>>`):
```json
{
  "success": true,
  "data": [
    { "id": "uuid", "numero": 1, "ordem": 0, "texto": "..." }
  ],
  "error": null
}
```
Ordenado por `ordem` ASC; `numero` = posição 1-based nessa lista
(derivado, não persistido). Lista pode ter qualquer tamanho (não mais
travada em 20) — **frontend deve remover qualquer suposição de "sempre
20 perguntas"** (ex: textos fixos tipo "20 Perguntas" no título viram
responsabilidade de UX/conteúdo, não de contrato de API).

#### `GET /api/v1/admin/vinte-perguntas` (admin) — inalterado

#### `POST /api/v1/admin/vinte-perguntas` (admin) — **novo**
- **Autenticação**: `Authorization: Bearer <jwt>`, role `ADMIN`.
- **Idempotente**: não (cada chamada cria uma linha nova).
- **Consistência**: forte, síncrona.
- **Request** (`VinteQuestaoCriarRequest`): `{ "texto": "string, obrigatório, 1-500 chars" }`
- Nova pergunta entra no final da lista (`ordem = max(ordem) + 1`, ou
  `0` se a tabela estiver vazia).
- **Response 201** (`ApiResponse<VinteQuestaoResponse>`).
- **Erros**: `400` texto vazio/> 500 chars, `401`/`403` padrão de auth.

#### `PUT /api/v1/admin/vinte-perguntas/{id}` (admin) — troca path param
- **Breaking change de path**: era `{numero}` (int 1-20), agora
  `{id}` (UUID). Necessário porque `numero` deixou de ser estável
  (reordenação muda a posição de todas as perguntas seguintes).
  Sinalizado ao agente `frontend-services`: método
  `atualizarPergunta` precisa passar a receber `id: string` em vez de
  `numero: number`.
- Demais aspectos inalterados: idempotente, consistência forte,
  request `{ "texto": "..." }`, erros `400`/`404`/`401`/`403`.

#### `DELETE /api/v1/admin/vinte-perguntas/{id}` (admin) — **novo**
- **Autenticação**: `Authorization: Bearer <jwt>`, role `ADMIN`.
- **Idempotente**: sim (deletar de novo um id já removido dá `404`,
  efeito líquido idêntico — recurso ausente).
- **Consistência**: forte, síncrona.
- **Regra de negócio**: não permite remover a **última** pergunta
  restante (mínimo de 1 pergunta sempre visível na página pública, para
  o quiz não ficar sem conteúdo nenhum) — `400 VALIDATION_ERROR` se for
  a única linha da tabela. Sem máximo de perguntas.
- **Response 200** (`ApiResponse<MensagemResponse>`):
  `{ "success": true, "data": { "mensagem": "Pergunta removida." }, "error": null }`
- **Erros**: `404` id não encontrado, `400` última pergunta,
  `401`/`403` padrão de auth.

#### `POST /api/v1/admin/vinte-perguntas/{id}/mover-cima` (admin) — **novo**
#### `POST /api/v1/admin/vinte-perguntas/{id}/mover-baixo` (admin) — **novo**
- Mesmo padrão de `FaqAdminController` (`ReordenacaoUtil.moverParaCima`/
  `moverParaBaixo`, troca de `ordem` com o vizinho). Sem efeito (fica
  parado) se já estiver no topo/fundo — não é erro, apenas idempotente
  ali (retorna 200 com o estado inalterado).
- **Response 200** (`ApiResponse<VinteQuestaoResponse>`).
- **Erros**: `404` id não encontrado, `401`/`403` padrão de auth.

### Breaking change — sinalização explícita para `frontend-services`
- `VinteQuestaoResponse` ganha campo novo `id` (não quebra consumidores
  que só liam `numero`/`texto`, é aditivo).
- `PUT` muda de path `{numero}` para `{id}` — **isso quebra o método
  `atualizarPergunta(numero, req)` hoje existente em
  `vinte-perguntas.ts`**. Precisa virar `atualizarPergunta(id, req)`.
- Novo `criarPergunta(texto)` → `POST`.
- Novo `removerPergunta(id)` → `DELETE`.
- Novo `moverPerguntaCima(id)` / `moverPerguntaBaixo(id)` → `POST`
  (opcional para esta entrega, mas o backend já expõe — UX de
  admin decide se usa; usuário só pediu criar/remover explicitamente,
  mover é bônus de paridade com FAQ).

### Auditoria
Inalterado — sem tabela de log dedicada, `atualizado_em`/
`atualizado_por` cobre rastreabilidade básica.

### Causa raiz do bug "página pública sumida" (corrigido)
Ver nota no topo desta seção. `vinte-perguntas.ts`/`.html` agora tratam
loading, erro de rede e lista vazia como estados distintos, cada um com
mensagem própria — não há mais tela "sem quiz" silenciosa.

Ponto ainda aberto, não é bug: não há mecanismo de seed automático para
`vinte_questao` — a tabela nasce vazia em qualquer ambiente novo, alguém
(admin) precisa cadastrar as perguntas iniciais via os endpoints `POST`
descritos acima.

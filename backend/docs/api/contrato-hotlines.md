# Contrato de API — Módulo Hotline (Linhas de Ajuda)

> Status: **reestruturado em 2026-07-14** — região deixou de ter `nome`
> livre e passou a ter `uf` (enum `UnidadeFederativa`, reaproveitado de
> `br.com.jogadoresanonimos.escritorio.entity.UnidadeFederativa`, sem
> duplicar a lista) + `cidade` (texto). Migration `V27` descarta os
> registros legados (não parseáveis com segurança) e recria as
> colunas da tabela `hotline_regiao` (ver seção de migration).
>
> **Mudança breaking**: o contrato anterior (`nome` como único campo de
> região, endpoint público sem filtro) já tinha sido documentado como
> "implementado em 2026-07-12" — este documento substitui aquela versão.
> Sinalizar para `frontend-services`: o campo `nome` de
> `HotlineRegiaoResponse` não existe mais, `CriarRegiaoRequest` mudou de
> shape, e o endpoint público agora omite regiões sem telefone.
>
> **Revisão 2026-07-14 (2ª) — remoção definitiva do campo `label` de
> telefone (mudança breaking de contrato, já consumido pelo
> frontend)**: `hotline_telefone.label` (`VARCHAR(100) NOT NULL`) é
> removido por completo, sem manter nullable — decisão do usuário/PO,
> não uma depreciação gradual. `HotlineTelefoneResponse`,
> `CriarTelefoneRequest` e `EditarTelefoneRequest` perdem o campo
> `label`. Migration `V4__remove_label_hotline_telefone.sql`. Ver
> "Revisão 2026-07-14 (2ª)" abaixo para o detalhe completo.

Convenções gerais (mesmas do restante do projeto, ver
`contrato-avisos.md`):
- Envelope de resposta sempre `{ success, data, error }`.
- Todos os paths sob `/api/v1`.
- Nenhum token/identificador sensível em query string.
- `traceId` presente em toda resposta de erro.

---

## Modelo de dados

Hierarquia de 2 níveis, duas tabelas relacionadas (não uma tabela única
com região repetida por linha, para não desnormalizar múltiplos
telefones por região em colunas ou JSON):

- `hotline_regiao`: `uf` (obrigatório, `VARCHAR(2)`, uma das 27 UFs do
  Brasil — mesmo enum `UnidadeFederativa` usado em
  `escritorio_regional`, não duplicado) + `cidade` (obrigatório, até 150
  chars). Combinação `uf` + `cidade` (case-insensitive) é única.
- `hotline_telefone`: `regiao_id` (FK, `ON DELETE CASCADE` — excluir uma
  região exclui todos os telefones dela), `telefone` (obrigatório, até 30
  chars, string livre — sem `CHECK` de formato). Não tem mais `label`
  (removido na "Revisão 2026-07-14 (2ª)").

Sem paginação — volume esperado é baixo (institucional, não conteúdo
gerado por usuário em massa).

**Regra de negócio — filtro do endpoint público**: o endpoint público
(`GET /api/v1/hotlines`) só retorna regiões com pelo menos 1 telefone
cadastrado. Esse filtro é aplicado na query do repository
(`findAllComPeloMenosUmTelefoneOrderByUfAscCidadeAsc`, via `JOIN` com
`hotline_telefone`), nunca no controller — uma região sem telefone
continua existindo e visível no admin (`GET /api/v1/admin/hotlines`),
apenas não aparece para o público. Motivo: uma região recém-criada mas
ainda sem telefone preenchido não deve virar um card vazio/quebrado na
tela pública de ajuda.

---

## 1. `GET /api/v1/hotlines` (público, lista regiões com telefone)

- **Autenticação**: nenhuma.
- **Idempotente**: sim.
- **Consistência**: pode tolerar poucos segundos de atraso — leitura de
  primário nesta entrega (sem cache dedicado), volume baixo não
  justifica infraestrutura extra agora.
- **Ordenação**: regiões por `uf ASC, cidade ASC`; telefones dentro de
  cada região em ordem de criação (`criado_em ASC`).
- **Filtro**: apenas regiões com `telefones` não vazio (ver regra acima).

**Request**: `GET /api/v1/hotlines`

**Response 200** (`List<HotlineRegiaoResponse>`):
```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "uf": "BA",
      "cidade": "Salvador",
      "telefones": [
        { "id": "uuid", "telefone": "(71) 0000-0000" }
      ]
    }
  ],
  "error": null
}
```

**Erros**: nenhum específico (lista vazia é resposta válida, não erro).

---

## 2. `GET /api/v1/admin/hotlines` (admin, lista todas as regiões, com ou sem telefone)

- **Autenticação + autorização**: `ROLE_ADMIN`, `@PreAuthorize`.
- **Idempotente**: sim.
- **Consistência**: forte — leitura do primário.
- Mesmo shape de `HotlineRegiaoResponse` do endpoint público, mas **sem**
  o filtro de telefone — retorna toda região cadastrada, mesmo com
  `telefones: []`, para que o admin veja e complete o cadastro.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 403 | `FORBIDDEN` | autenticado sem `ROLE_ADMIN` |

---

## 3. `POST /api/v1/admin/hotlines/regioes` (admin cria região)

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: não.
- **Consistência**: forte, síncrona.

**Request** (`CriarRegiaoRequest`):
```json
{ "uf": "BA", "cidade": "string, 1-150 chars" }
```
`uf` é validado contra o enum `UnidadeFederativa` (as 27 siglas de UF do
Brasil), case-insensitive na entrada (`"ba"` é aceito e normalizado para
`"BA"`).

**Response 201** (`HotlineRegiaoResponse`): região criada, com
`telefones: []`.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | `uf` ausente/inválida (não é uma das 27 UFs) ou `cidade` ausente/fora do tamanho |
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |
| 409 | `CONFLICT` | já existe região com essa combinação `uf` + `cidade` (case-insensitive) — mensagem orienta editar a região existente em vez de criar uma nova |

---

## 4. `PUT /api/v1/admin/hotlines/regioes/{id}` (admin edita UF/cidade da região) — **novo endpoint**

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: sim.
- **Consistência**: forte, síncrona.
- Adicionado nesta reestruturação porque região deixou de ter só um
  `nome` livre; agora existem dois campos estruturados que fazem sentido
  editar separadamente da criação (ex: corrigir a cidade sem recriar a
  região e perder os telefones).

**Request** (`EditarRegiaoRequest`): mesmo shape do `CriarRegiaoRequest`.
```json
{ "uf": "RJ", "cidade": "Niterói" }
```

**Response 200** (`HotlineRegiaoResponse`): região atualizada.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | `uf` inválida ou `cidade` ausente/fora do tamanho |
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `NOT_FOUND` | id não existe |
| 409 | `CONFLICT` | a nova combinação `uf` + `cidade` já pertence a **outra** região (a checagem é ignorada quando a combinação não muda em relação ao valor atual da própria região) |

---

## 5. `DELETE /api/v1/admin/hotlines/regioes/{id}` (admin exclui região inteira)

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: sim (segunda chamada retorna 404).
- **Consistência**: forte, síncrona. Exclusão física, cascade nos
  telefones da região (`ON DELETE CASCADE` no banco + `orphanRemoval`
  na entidade JPA).

**Response 200**: `{ "success": true, "data": { "mensagem": "Região removida." }, "error": null }`

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `NOT_FOUND` | id não existe |

---

## 6. `POST /api/v1/admin/hotlines/regioes/{regiaoId}/telefones` (admin adiciona telefone)

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: não.
- **Consistência**: forte, síncrona.

**Request** (`CriarTelefoneRequest`):
```json
{ "telefone": "string, 1-30 chars" }
```

**Response 201** (`HotlineRegiaoResponse`): região atualizada com o
telefone novo incluído na lista (evita round-trip extra do frontend
para re-buscar a região completa).

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | telefone ausente ou fora do tamanho |
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `NOT_FOUND` | `regiaoId` não existe |

---

## 7. `PUT /api/v1/admin/hotlines/telefones/{id}` (admin edita telefone)

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: sim.
- **Consistência**: forte, síncrona.

**Request** (`EditarTelefoneRequest`): mesmo shape do
`CriarTelefoneRequest`.

**Response 200** (`HotlineTelefoneResponse`): telefone atualizado
(sozinho, não a região inteira — edição de telefone não precisa do
contexto da região).

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | telefone ausente ou fora do tamanho |
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `NOT_FOUND` | id não existe |

---

## 8. `DELETE /api/v1/admin/hotlines/telefones/{id}` (admin exclui telefone individual)

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: sim (segunda chamada retorna 404).
- **Consistência**: forte, síncrona. Exclusão física — telefone não é
  referenciado por nenhuma outra entidade.

**Response 200**: `{ "success": true, "data": { "mensagem": "Telefone removido." }, "error": null }`

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 403 | `FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `NOT_FOUND` | id não existe |

---

## Revisão 2026-07-14 (2ª) — remoção do campo `label` de telefone

**Motivação (decisão do usuário/PO)**: `label` nunca teve uso
consistente no cadastro real das hotlines — os telefones institucionais
não têm um rótulo distintivo (diferente, por exemplo, de um caso onde
"Central"/"Emergência" fariam sentido) e o campo virou um passo de
formulário sem valor. Decisão explícita: remover o campo por completo,
sem manter nullable como transição — não há dado real de produção a
preservar (mesmo raciocínio já documentado na "Revisão 2026-07-13" de
`contrato-escritorios-regionais.md`).

**Migration destrutiva, não expand/contract**: `DROP COLUMN` direto, sem
etapa intermediária de tornar nullable primeiro — confirmado pelo
usuário/PO antes de escrever (regra do projeto para migration
destrutiva). Não há dado de produção acumulado a perder (módulo
reestruturado há poucas migrations).

**Contrato revisado**:
- `HotlineTelefoneResponse`: `{ id, telefone }` (sem `label`).
- `CriarTelefoneRequest`/`EditarTelefoneRequest`: `{ telefone }` (sem
  `label`).

### Migration — `V4__remove_label_hotline_telefone.sql`

```sql
ALTER TABLE hotline_telefone DROP COLUMN label;
```

### Mudança breaking desta revisão

**Sim** — remove campo de request e response já consumidos pelo
frontend. Pontos de atenção para `frontend-services`/`frontend`:
- Remover `label` de `HotlineTelefone`/`CriarTelefoneRequest`/
  `EditarTelefoneRequest` do lado do cliente.
- Remover o campo de texto "Descrição"/rótulo do formulário de
  telefone no admin de Hotlines.
- Exibição pública de telefone deixa de ter rótulo — mostrar só o
  número.

---

## Migration — `V27__descarta_dados_legado_recria_hotline_regiao_uf_cidade.sql`

```sql
DROP INDEX IF EXISTS idx_hotline_regiao_nome;

DELETE FROM hotline_telefone;
DELETE FROM hotline_regiao;

ALTER TABLE hotline_regiao
  DROP COLUMN nome,
  ADD COLUMN uf VARCHAR(2) NOT NULL,
  ADD COLUMN cidade VARCHAR(150) NOT NULL;

CREATE UNIQUE INDEX idx_hotline_regiao_uf_cidade ON hotline_regiao(uf, lower(cidade));
```

Nota: o arquivo original desta migration foi escrito como `V26`, mas
colidia com `V26__seed_faq.sql` já existente no projeto (Flyway
identifica migrations por versão, não por nome) — renumerado para `V27`.

### Decisão: apagar dados legados explicitamente, não usar DEFAULT mascarador — por quê

Avaliei duas opções: (a) escrever um `UPDATE` que tentasse separar o
`nome` livre existente em `uf` + `cidade`, ou (b) apagar as linhas
antigas de `hotline_telefone`/`hotline_regiao` e recriar as colunas do
zero como `NOT NULL` sem valor default.

Escolhi **(b)**, com `DELETE` explícito em vez de um `ADD COLUMN ...
DEFAULT 'SP' / 'A definir'` (que foi a primeira versão escrita e
apontada pelo `code-reviewer` como problemática): um default mascarador
faria qualquer linha antiga sobreviver como um registro "SP / A definir"
com aparência de dado real dentro do admin, quando na verdade é só um
placeholder técnico. Como não havia nenhuma migration de seed populando
essas tabelas (confirmado em `V13__cria_tabelas_hotline.sql` e nas
migrations seguintes), o descarte não perde dado de produção — apenas
remove a possibilidade de teoricamente existir alguma linha real,
tornando isso auditável na própria migration em vez de silencioso.

Motivo de não migrar automaticamente: `nome` é texto totalmente livre (o mock
original tinha valores como `"Bahia"`, `"Distrito Federal"` — nomes de
estado, não UF+cidade), sem nenhum padrão estruturado que permita
derivar programaticamente qual UF e qual cidade correspondem a cada
linha. Um `UPDATE` heurístico (ex: tentar casar `nome` contra
`UnidadeFederativa.getNomeCompleto()`) erraria silenciosamente qualquer
linha que já fosse uma cidade (não um nome de estado) e ainda deixaria
`cidade` sem valor correto — pior que simplesmente recriar. Como o
volume é baixíssimo (cadastro institucional, mantido por admin, não dado
de usuário final) e a task já era sinalizada como não tendo migration de
seed automática (ver nota de "Pendências" da versão anterior deste
documento — os telefones eram todos `"(a definir)"`), o recadastro
manual das regiões pelo admin é a opção mais simples e correta, alinhado
ao princípio de não fazer overengineering para um volume deste porte.

Se este projeto já tivesse dado real de produção em `hotline_regiao`
(cadastro manual feito por um admin, não placeholder), a decisão correta
teria sido **parar e confirmar explicitamente com o usuário antes** de
rodar uma migration destrutiva — o que este documento já deixa registrado
para qualquer rodada futura de reestruturação similar.

### Sobre o índice único

`idx_hotline_regiao_uf_cidade` é um índice único **funcional**
(`lower(cidade)`), diferente do índice antigo (`nome` puro, case-sensitive
no banco). Isso resolve de uma vez o problema que a versão anterior deste
documento apontava como pendência ("se no futuro houver problema de
concorrência real... a solução seria um índice funcional") — já
implementado aqui porque o padrão de `EstadoIntergrupo` (mesma técnica de
UF) não usa índice funcional em `cidade`, mas aqui `cidade` é livre (não
enum), então vale a proteção extra desde já.

---

## Mudança breaking sinalizada — sim, contrato já existente foi alterado

Resumo para `frontend-services`:
1. `HotlineRegiaoResponse.nome` → removido. Substituído por `uf`
   (string, sigla de 2 letras) + `cidade` (string).
2. `CriarRegiaoRequest`: `{ nome }` → `{ uf, cidade }`.
3. Novo endpoint `PUT /api/v1/admin/hotlines/regioes/{id}` com
   `EditarRegiaoRequest` (`{ uf, cidade }`), retorna `HotlineRegiaoResponse`.
4. `GET /api/v1/hotlines` (público) agora **omite** regiões sem telefone
   — uma região recém-criada pelo admin sem telefone ainda não aparece
   na tela pública até que ao menos 1 telefone seja adicionado.
5. Erro de conflito na criação/edição de região passou a ser
   especificamente sobre a combinação UF+cidade, não mais sobre "nome".

## Pendências explícitas para outros agentes

- `frontend-services`: atualizar `HotlinesService`/mocks para o novo
  shape de `HotlineRegiaoResponse` (`uf`/`cidade` em vez de `nome`) e
  para o novo endpoint de edição de região.
- `frontend`: formulário de criação/edição de região no admin passa a
  ter 2 campos (select de UF + input de cidade) em vez de 1 campo de
  texto livre; tela pública já reage automaticamente ao filtro do
  backend (regiões sem telefone somem sozinhas, nenhuma mudança de UI
  necessária para isso).
- Recadastro das regiões antigas (mock com nomes de estado) precisa ser
  refeito manualmente pelo admin com UF + cidade reais — nenhum dado foi
  migrado automaticamente (ver decisão de migration acima).

## Implementação (backend)

- `br.com.jogadoresanonimos.hotline.controller.HotlineController` —
  `GET /api/v1/hotlines` (delega para `listarPublicas()`, com filtro de
  telefone).
- `br.com.jogadoresanonimos.hotline.controller.HotlineAdminController` —
  7 endpoints admin (novo: `PUT /regioes/{id}`), classe inteira sob
  `@PreAuthorize("hasRole('ADMIN')")`.
- `br.com.jogadoresanonimos.hotline.service.HotlineService` —
  `listarTodas` (admin), `listarPublicas` (filtra por telefone),
  `criarRegiao`, `editarRegiao` (novo), `removerRegiao`,
  `adicionarTelefone`, `editarTelefone`, `removerTelefone`.
- `br.com.jogadoresanonimos.hotline.entity.HotlineRegiao` — campos `uf`
  (`@Enumerated(EnumType.STRING)`, reaproveitando
  `br.com.jogadoresanonimos.escritorio.entity.UnidadeFederativa`) e
  `cidade`, no lugar de `nome`.
- `br.com.jogadoresanonimos.hotline.repository.HotlineRegiaoRepository` —
  `findAllByOrderByUfAscCidadeAsc` (admin),
  `findAllComPeloMenosUmTelefoneOrderByUfAscCidadeAsc` (público, via
  `JOIN` com `hotline_telefone`), `existsByUfAndCidadeIgnoreCase`.
- `br.com.jogadoresanonimos.common.exception.ConflitoDeRegiaoHotlineException` —
  exceção de conflito 409 específica do módulo hotline, mesmo padrão de
  `ConflitoDeEstadoException` (usado em `EscritorioRegionalService`).
- Migration `V27__descarta_dados_legado_recria_hotline_regiao_uf_cidade.sql`.
- `br.com.jogadoresanonimos.hotline.entity.HotlineTelefone` — campo
  `label` removido (ver "Revisão 2026-07-14 (2ª)").
- Migration `V4__remove_label_hotline_telefone.sql`.
- Testes unitários atualizados em
  `backend/src/test/java/br/com/jogadoresanonimos/hotline/` (`controller/`
  e `service/`).

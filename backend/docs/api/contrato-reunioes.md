# Contrato de API — Módulo Reunião

## Mudança não-breaking — 2026-08-22 (múltiplos dias e horários)

`CriarReuniaoRequest`, `EditarReuniaoRequest` e `ReuniaoResponse` ganham
`agenda`, uma lista de itens no mesmo formato usado pela agenda de grupos:

```json
{
  "diaSemana": "SEGUNDA",
  "horario": "19h30"
}
```

`diaSemana` aceita `SEGUNDA`, `TERCA`, `QUARTA`, `QUINTA`, `SEXTA`,
`SABADO` ou `DOMINGO`; `horario` é texto obrigatório de até 50 caracteres.
Um mesmo dia pode aparecer mais de uma vez quando a reunião possui mais de
um horário nesse dia. Não é permitido repetir a mesma combinação de
`diaSemana` e `horario` na mesma reunião. A lista aceita no máximo 336 itens.
Respostas são sempre ordenadas de segunda a domingo e, dentro do mesmo dia,
pela hora/minuto inicial reconhecida nos formatos legados (`9h`,
`9h às 11h`, `19h30`, `19:30h` e `HH:mm`). Valores sem hora inicial
reconhecível usam ordem textual como fallback. A mesma ordem define o primeiro
item espelhado nos campos legados, independentemente da ordem de entrada.
Criação e edição exigem pelo menos um item, aceitando duas
formas de payload durante a fase de compatibilidade:

- cliente novo envia `agenda` com um ou mais itens;
- cliente legado omite `agenda` e continua enviando `dia`, `horario`,
  `diaSemana` e `periodo` no nível raiz.

Quando `agenda` é informada e não vazia, ela é a fonte de verdade e os
campos legados `dia`, `horario` e `diaSemana` no nível raiz são ignorados;
`periodo` continua aceito como metadado legado opcional. O backend persiste a
lista completa em `reuniao_horario` e também grava o primeiro item nas
colunas legadas de `reuniao`. O backend novo aceita clientes antigos; em um
`PUT` legado, atualiza o dia correspondente sem apagar os demais itens. Se o
dia recebido já existe na agenda, o primeiro horário desse dia é atualizado
e todos os demais horários, inclusive os do mesmo dia, são preservados. Se a
combinação recebida já existe, a agenda não é alterada. Se for um dia novo,
ele substitui somente o antigo item primário, preservando os adicionais.

O dual-write não torna seguro fazer rollback para uma versão antiga do
backend depois que uma agenda com múltiplos itens foi criada: a versão antiga
não conhece `reuniao_horario` e pode alterar apenas as colunas legadas, sem
sincronizar a lista. Nesse cenário, rollback exige bloquear escritas e aplicar
correção forward ou procedimento operacional específico; não há garantia de
compatibilidade de escrita bidirecional entre as duas versões.

Toda resposta inclui `agenda`. Os campos raiz `dia`, `horario`,
`diaSemana` e `periodo` continuam presentes. `horario` e `diaSemana`
representam o primeiro item na ordem semanal; `dia` recebe a forma legível
do primeiro dia (`Segunda`, `Terça`, etc.) e `periodo` permanece como o valor
legado recebido, pois não faz parte da nova agenda.
Dados existentes recebem exatamente um item por reunião por backfill, sem
remoção nem alteração das colunas antigas. A fase `contract`, com eventual
remoção dos campos legados, fica fora deste contrato e exige migration futura
destrutiva, confirmação explícita e todos os clientes já migrados.

Validação inválida retorna `400` com `error.code = VALIDATION_ERROR`.
Os endpoints, autenticação, autorização, idempotência e demais
códigos de erro permanecem inalterados.

> Status: **implementado em 2026-07-12**, **endereço estruturado
> adicionado em 2026-08-16** (migration `V9`), **`linkReuniao` adicionado
> em 2026-08-16** (migration `V10`) — entity, repository, service, DTOs
> e controllers completos (`ReuniaoController` público,
> `ReuniaoAdminController` admin), com testes unitários.
> Baseado no modelo hoje hardcoded em
> `frontend/src/app/pages/reunioes/reunioes.ts` (interface `Reuniao`) e
> no escopo confirmado em `Pendencias Admin Dashboard.md` (linha
> "Reuniões").

## Mudança não-breaking — 2026-08-16 (`linkReuniao`, campo dedicado)

**Contexto**: `ux-ui` especificou o formulário de reunião virtual com
campo próprio "Link da reunião (opcional)", separado de "Número/contato"
(`whatsapp`). Até esta mudança, reunião virtual reaproveitava `local`
(texto livre) informalmente para esse propósito.

**Decisão: campo novo (`link_reuniao`/`linkReuniao`), não reaproveitar
`local`.** Motivos:
- O pedido do cliente é explícito por um campo separado de
  "Número/contato" — isso já indica um formulário com um campo dedicado
  por dado (nome, dia, hora, contato, link), não um campo genérico de
  texto livre reaproveitado por convenção informal.
- `local` continua existindo, sem mudança de comportamento (ver seção
  "Mudança não-breaking — 2026-08-16 (endereço estruturado)" abaixo) e
  seu propósito documentado é endereço (mesmo que hoje também aceite
  texto livre por reunião virtual, por herança do mock antigo do
  frontend). Sobrepor dois significados (endereço presencial e link
  virtual) no mesmo campo é ambíguo para o front renderizar/rotular
  corretamente e para o admin editar sem confundir os dois usos.
- Campo dedicado permite validação de formato própria futura (ex.
  `@URL`) sem colidir com a regra de tamanho/formato de `local`, hoje
  compartilhada por endereço presencial.

`linkReuniao`: `VARCHAR(500)`, nullable, **sem nenhuma obrigatoriedade
de aplicação e sem validação cruzada com `categoria`** — mesmo padrão já
adotado para os 6 campos de endereço estruturado (`V9`). Reunião
presencial pode enviar (ou não) `linkReuniao`; reunião virtual pode
deixar de enviar. O backend não valida que só reunião `virtual` tenha
`linkReuniao` preenchido — essa regra forçaria reunião presencial legada
a nunca poder ter o campo e criaria acoplamento desnecessário, mesmo
raciocínio já registrado para os campos de endereço.

**Dado legado**: reuniões virtuais cadastradas antes desta mudança têm
`linkReuniao` `null` e continuam sendo servidas/exibidas via `local`
(que hoje pode conter o link como texto livre, ex: "Link de acesso via
Google Meet") até que um admin edite a reunião e opte por preencher o
campo dedicado. **Sem backfill nem migração automática** — mesmo
raciocínio dos campos de endereço: não há como distinguir com segurança,
por heurística, se o conteúdo de `local` de uma reunião virtual legada é
de fato uma URL válida sem revisão humana.

`local` não é depreciado nem alterado por esta mudança — ele continua
sendo aceito/retornado exatamente como está.

Migration: `V10__adiciona_link_reuniao.sql`, aditiva, coluna nullable —
mesmo padrão *expand* de `V9`.

```sql
ALTER TABLE reuniao
  ADD COLUMN link_reuniao VARCHAR(500);
```

Sem índice — campo não é usado em filtro/busca (nem client-side, nem
admin), só exibição.

**Pendência explícita para `frontend-services`/`frontend`**: model
`Reuniao`/`CriarReuniaoRequest`/`EditarReuniaoRequest` ganham
`linkReuniao: string | null` (response) / `string | undefined` (request).
Formulário de reunião virtual (`ux-ui`) deve popular esse campo, não
`local`, para o caso de uso "Link da reunião". Campo é opcional em todas
as camadas — envio facultativo, sem quebra de contrato existente.

## Mudança não-breaking — 2026-08-16 (endereço estruturado, coexistência)

**Contexto**: sistema em produção com dados reais — reuniões já
cadastradas hoje têm `local` (texto livre) e, quando presencial,
`latitude`/`longitude` preenchidos. Qualquer mudança de contrato
precisa continuar servindo o frontend em produção sem quebra. Decisão
do cliente, obrigatória: padrão **expand/contract**, sem `DROP` de
`local`/`latitude`/`longitude`, sem parse automático de `local` para os
campos estruturados novos.

`CriarReuniaoRequest`/`EditarReuniaoRequest`/`ReuniaoResponse` ganham 6
campos novos de endereço (`cep`, `numero`, `logradouro`, `bairro`,
`cidade`, `estado`), **todos opcionais, sem nenhuma validação cruzada
com `categoria` ou entre si**. Não existe mais (revertido em relação a
uma versão anterior, nunca chegou a produção) a regra de "presencial
exige todos os 6, virtual exige todos ausentes" — essa regra forçaria
todo `PUT` de uma reunião presencial legada (sem endereço estruturado)
a falhar até o admin preencher os 6 campos, e é isso que este contrato
existe para evitar.

`local`/`latitude`/`longitude` continuam existindo exatamente como
hoje, aceitos e retornados sem nenhuma mudança de comportamento. O
backend **não deriva `local` a partir dos campos estruturados** — o
valor de `local` é sempre o que o client envia (trim, sem alteração de
conteúdo), para presencial e virtual igualmente. `local` deixou de ter
`@NotBlank` (agora `@Size(max = 300)` opcional) porque, no fluxo de
edição, o admin pode futuramente optar por deixar de reenviar `local`
depois de migrar uma reunião para o formato estruturado — mas isso é
decisão do admin caso a caso, o backend não força nem impede.

**Dado legado**: reuniões cadastradas antes desta mudança têm os 6
campos estruturados `null` e continuam sendo servidas/exibidas
normalmente via `local` (e `latitude`/`longitude`, se presencial) até
que um admin edite a reunião manualmente e opte por preencher os campos
estruturados. **Não há backfill nem migração automática de dado** —
parse de endereço livre (`local`) para campos estruturados foi avaliado
e rejeitado deliberadamente (impreciso/inseguro extrair
logradouro/número/bairro/cidade/estado de texto livre sem revisão
humana); a responsabilidade de estruturar o endereço de uma reunião
existente é do admin, ao reeditá-la.

**Intenção futura, não implementada agora**: remover `local` do
contrato (contract phase do expand/contract) só seria cogitado quando
100% das reuniões estiverem com endereço estruturado preenchido e 100%
do código (backend e frontend) parar de ler/escrever `local` — isso
exigiria uma migration `DROP COLUMN` separada, futura, com confirmação
explícita antes de escrever, e está fora do escopo desta entrega.

## Decisão de modelagem — colunas na própria tabela `reuniao`, não entidade `Endereco`

Avaliado: (a) colunas estruturadas direto em `reuniao` vs (b) entidade
`Endereco` própria com FK a partir de `reuniao`. **Escolhido (a).**

Motivos:
- Cardinalidade real é 1:1 — cada reunião presencial tem exatamente um
  endereço, nunca compartilhado entre reuniões. O pedido do cliente de
  "sem duplicação de endereço" é sobre não duplicar a *reunião* em
  múltiplos cadastros, não sobre endereço N:1 reaproveitado entre
  reuniões distintas — não há caso de uso relatado de duas reuniões no
  mesmo endereço físico que precisem ficar sincronizadas.
- "Sem registro órfão" (pedido do cliente) é trivial por construção com
  colunas na própria tabela: não existe FK para ficar órfã. Com
  entidade separada seria necessário `ON DELETE CASCADE` bem calibrado
  e FK nullable (para reunião virtual sem endereço), sem ganho real.
- Busca por CEP/rua/número/bairro/cidade/estado (pedido do cliente,
  usado pelo admin) é atendida com índice composto sobre colunas da
  própria `reuniao`, sem necessidade de JOIN — um JOIN só compensaria
  se endereços precisassem ser consultados independente de reunião, o
  que não é o caso.
- Volume da tabela é baixo (institucional, poucas dezenas de linhas) —
  normalização para ganho de espaço/consistência não se paga aqui.
- Mantém o padrão *expand* de migration: adiciona colunas nullable,
  sem alterar `local`/`latitude`/`longitude` existentes.

Convenções gerais (mesmas do restante do projeto, ver
`contrato-avisos.md`/`contrato-relatos.md`):
- Envelope de resposta sempre `{ success, data, error }`.
- Todos os paths sob `/api/v1`.
- Catálogo completo de `error.code` em `contrato-erro-padrao.md`.
- Nenhum token/identificador sensível em query string.
- `traceId` presente em toda resposta de erro.

---

## Modelo de dados

Tabela `reuniao`: `nome`, `categoria` (`presencial` | `virtual`),
`descricao`, `dia` (texto livre, ex: "Segunda-feira" — mantém o formato
já usado pelo mock do frontend, não é normalizado para enum aqui),
`horario` (texto livre, ex: "19h30"), `local` (endereço em texto livre,
continua existindo e sendo aceito/retornado sem mudança de
comportamento — ver seção "Mudança não-breaking" acima), `whatsapp`
(opcional), `dia_semana` (opcional, usado só por reuniões presenciais
para o filtro client-side), `periodo` (opcional, idem),
`latitude`/`longitude` (`DOUBLE PRECISION`, opcionais), e desde
2026-08-16: `cep`, `numero`, `logradouro`, `bairro`, `cidade`, `estado`
(todos `VARCHAR`, nullable no schema **e sem nenhuma obrigatoriedade de
aplicação** — reunião legada ou nova pode ter todos, alguns ou nenhum
desses 6 campos preenchidos, independente de `categoria`), e também
desde 2026-08-16: `link_reuniao` (`VARCHAR(500)`, nullable, campo
dedicado para o link de reunião virtual — ver seção "Mudança
não-breaking — 2026-08-16 (`linkReuniao`, campo dedicado)").

Tabela `reuniao_horario`: um item por horário de cada reunião, com
`reuniao_id` (`ON DELETE CASCADE`), `dia_semana` canônico e `horario`.
A constraint `UNIQUE (reuniao_id, dia_semana, horario)` impede somente a
duplicação exata do mesmo horário no mesmo dia, permitindo vários horários
por dia. As colunas de agenda da tabela `reuniao` permanecem como
espelho compatível do primeiro item durante a fase *expand*.

**Decisão confirmada com o usuário (herdada, sem mudança)**: sem
geocodificação automática no backend nesta v1. O admin insere
`latitude`/`longitude` — hoje calculados no frontend via Nominatim a
partir do endereço estruturado (`EnderecoLookupService`) e enviados já
prontos — os campos no backend continuam só armazenamento numérico
validado por range, sem chamada a API de geocoding no servidor.

`dia_semana`/`periodo`/`latitude`/`longitude`/`whatsapp` são nullable
porque reuniões virtuais no mock atual não preenchem esses campos (não
fazem sentido para filtro de raio/dia da semana presencial da forma como
a página de mapa usa hoje — ver `reunioes.ts`). Os 6 campos de endereço
seguem a mesma lógica: nullable no schema, sem regra de aplicação
condicionando um ao outro ou à `categoria`.

### `local` — continua sendo o valor enviado pelo client, sem derivação

O service **não deriva nem recompõe `local`** a partir dos campos
estruturados, para nenhuma `categoria`. `local` é sempre o texto que o
client enviou (trim, sem alteração de conteúdo) — não um fallback
calculado a partir de `logradouro`/`numero`/etc. Isso é deliberado:
parse/composição automática de endereço fica fora de escopo (ver
"Mudança não-breaking" acima) e a página pública (`reunioes.ts`)
continua lendo `local` como fonte de exibição até que o frontend seja
adaptado para preferir os campos estruturados quando presentes (fora
do escopo desta entrega, ver "Pendências explícitas").

A coluna `reuniao.local` é `NOT NULL` (ver schema). Diferente dos
demais campos opcionais deste contrato (que normalizam ausente/em
branco para `null`), `local` normaliza ausente/em branco para string
vazia (`""`) — nunca `null`. Isso é o caso normal de reunião virtual
sem endereço físico. Bug histórico (corrigido em 2026-08-17): antes
dessa normalização dedicada, `local` vazio era gravado como `null`
pela mesma normalização genérica usada em campos nullable, violando o
`NOT NULL` e derrubando `POST`/`PUT` de reunião virtual com erro 500.

### Sem validação cruzada de endereço

Os 6 campos novos não têm nenhuma validação de "todos ou nenhum" nem
de obrigatoriedade condicionada a `categoria`. Cada campo é validado
isoladamente por Bean Validation no DTO (`@Size`/`@Pattern`), igual a
qualquer outro campo opcional do request:

`cep`: validado por regex de formato (8 dígitos, com ou sem hífen),
mesma filosofia do `whatsapp` — validação de formato de aplicação, sem
chamada ao ViaCEP no backend (a consulta ao CEP já acontece no
frontend, no preenchimento do formulário). `estado`: exatamente 2
letras, normalizado para maiúsculas no service.

Não existe mais (removido nesta revisão, nunca chegou a produção) o
código de erro `REUNIAO_ENDERECO_INVALIDO` nem a validação cruzada que
o originava — essa regra forçaria reunião presencial legada, sem
endereço estruturado, a falhar em qualquer `PUT` até o admin preencher
os 6 campos, o que quebraria o fluxo de edição hoje em produção.

Sem paginação — volume institucional baixo (reuniões da associação, não
conteúdo gerado por usuário em massa), mesmo raciocínio do módulo
`aviso`. Toda a filtragem por dia/período/raio de geolocalização
continua **client-side** no frontend (`reunioes.ts` já implementa isso
sobre o array completo) — o backend só entrega a lista crua.

Sem coluna de auditoria de autor (`atualizado_por`) nesta entrega —
mesmo raciocínio do `aviso`: não é conteúdo com requisito de auditoria
de autoria como o CMS de páginas institucionais.

---

## 1. `GET /api/v1/reunioes` (público, lista todas)

- **Autenticação**: nenhuma.
- **Idempotente**: sim.
- **Consistência**: pode tolerar poucos segundos de atraso — leitura do
  primário nesta entrega (sem cache dedicado, volume baixo não justifica
  infraestrutura extra agora).
- **Sem filtro no backend**: retorna todas as reuniões cadastradas,
  ordenadas por `nome` (ordem estável e previsível; toda filtragem real
  de dia/período/raio/categoria acontece no cliente, como já é hoje com
  o mock).

**Request**: `GET /api/v1/reunioes`

**Response 200** (`List<ReuniaoResponse>`):
```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "nome": "Grupo Centro",
      "categoria": "presencial",
      "descricao": "string",
      "agenda": [
        { "diaSemana": "SEGUNDA", "horario": "19h30" },
        { "diaSemana": "QUINTA", "horario": "20h00" }
      ],
      "dia": "Segunda-feira",
      "horario": "19h30",
      "local": "Rua das Flores, 123 - Centro, São Paulo - SP",
      "whatsapp": "5599999999999",
      "diaSemana": "Segunda",
      "periodo": "Noite",
      "latitude": -23.5505,
      "longitude": -46.6333,
      "cep": "01310-100",
      "numero": "123",
      "logradouro": "Rua das Flores",
      "bairro": "Centro",
      "cidade": "São Paulo",
      "estado": "SP",
      "linkReuniao": null
    },
    {
      "id": "uuid",
      "nome": "Grupo Virtual Quinta",
      "categoria": "virtual",
      "descricao": "string",
      "agenda": [
        { "diaSemana": "QUINTA", "horario": "20h00" }
      ],
      "dia": "Quinta-feira",
      "horario": "20h00",
      "local": "Link de acesso via Google Meet",
      "whatsapp": "5599999999999",
      "diaSemana": null,
      "periodo": null,
      "latitude": null,
      "longitude": null,
      "cep": null,
      "numero": null,
      "logradouro": null,
      "bairro": null,
      "cidade": null,
      "estado": null,
      "linkReuniao": "https://meet.google.com/abc-defg-hij"
    }
  ],
  "error": null
}
```

**Erros**: nenhum específico (lista vazia é resposta válida, não erro).

---

## 2. `GET /api/v1/admin/reunioes` (admin, lista todas)

- **Autenticação + autorização**: `ROLE_ADMIN`, `@PreAuthorize`.
- **Idempotente**: sim.
- **Consistência**: forte — leitura do primário.
- Mesmo shape do endpoint público (`ReuniaoResponse`) — endpoint próprio
  existe só por consistência de rota com o resto do admin (`/admin/*`),
  não porque o admin vê campos extras.

**Response 200**: mesmo shape do item 1.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 403 | `AUTH_FORBIDDEN` | autenticado sem `ROLE_ADMIN` |

---

## 3. `POST /api/v1/admin/reunioes` (admin cria)

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: não.
- **Consistência**: forte, síncrona.

**Request** (`CriarReuniaoRequest`):
```json
{
  "nome": "string, 1-200 chars",
  "categoria": "presencial | virtual",
  "descricao": "string, 0-2000 chars, obrigatória (não-branca) só quando categoria=presencial",
  "agenda": [
    { "diaSemana": "SEGUNDA", "horario": "19h30" },
    { "diaSemana": "QUINTA", "horario": "20h00" }
  ],
  "dia": "string, 1-100 chars, legado; opcional quando agenda é enviada",
  "horario": "string, 1-50 chars, legado; opcional quando agenda é enviada",
  "local": "string, até 300 chars, opcional (texto livre, salvo exatamente como enviado, sem derivação)",
  "whatsapp": "string, até 20 chars, opcional",
  "diaSemana": "string, até 20 chars, opcional",
  "periodo": "string, até 20 chars, opcional",
  "latitude": -23.5505,
  "longitude": -46.6333,
  "cep": "string, 8-9 chars, opcional",
  "numero": "string, até 20 chars, opcional",
  "logradouro": "string, até 200 chars, opcional",
  "bairro": "string, até 100 chars, opcional",
  "cidade": "string, até 100 chars, opcional",
  "estado": "string, 2 chars (UF), opcional",
  "linkReuniao": "string, até 500 chars, opcional (URL/texto livre da reunião virtual, campo dedicado — não usar `local` para isso, ver seção 'Mudança não-breaking — 2026-08-16 (linkReuniao, campo dedicado)')"
}
```
Todos os 6 campos de endereço estruturado são independentes entre si e
de `categoria` — cliente pode enviar qualquer subconjunto (inclusive
nenhum, caso comum de reunião legada ainda não reeditada). `linkReuniao`
segue a mesma lógica: independente de `categoria`, sem validação
cruzada.

**Mudança não-breaking — 2026-08-17 (`descricao` opcional para
virtual)**: até esta mudança, `descricao` era `@NotBlank` no DTO para
toda `categoria`, bloqueando a criação de reunião virtual pelo
formulário do frontend (que, por spec do `ux-ui`, não tem campo
descrição). `descricao` deixou de ter `@NotBlank` no Bean Validation;
a obrigatoriedade agora é validada no `ReuniaoService`
(`validarEDescricao`), condicionada a `categoria`: presencial exige
texto não-branco (mesma regra de antes, só migrou de anotação para
código de aplicação), virtual aceita ausente/vazio/em branco e persiste
como string vazia (`""`). Coluna `reuniao.descricao` continua
`VARCHAR(2000) NOT NULL` — `NOT NULL` barra `NULL`, não string vazia,
então não foi necessária migration; sem alteração de schema, sem
alteração de dado legado. Reunião presencial legada não é afetada
(sempre teve `descricao` preenchida, continua exigida na próxima
edição). **Contrato para o frontend**: o formulário virtual pode omitir
`descricao` do payload ou enviar `descricao=""` — ambos são aceitos e
tratados de forma idêntica (o service normaliza `null`/blank para
`""`).
`latitude`/`longitude` opcionais; quando informados, validados no range
geográfico válido (`-90..90` / `-180..180`) — validação de aplicação
(`@DecimalMin`/`@DecimalMax`), sem `CHECK` no banco. Campos de texto
opcionais em branco são normalizados para `null` no service (evita
strings vazias poluindo o filtro client-side).

`whatsapp`: exceção à normalização acima — quando ausente/vazio **na
criação ou na edição**, aplica-se o telefone padrão da associação
(`11 99571-6942`, 14 chars, dentro do limite de 20) em vez de `null`.
Decisão de produto: toda reunião sem contato próprio direciona para o
número institucional, inclusive reunião existente editada sem
preencher o campo (evita que uma edição não relacionada a `whatsapp`
apague silenciosamente o padrão já aplicado na criação). Normalização
ocorre no service (`ReuniaoService.normalizarWhatsapp`), usada tanto em
`criar()` quanto em `editar()`, não no banco/DTO.

**Validação de formato** (`CriarReuniaoRequest`/`EditarReuniaoRequest`,
`@Pattern`, adicionada em 2026-08-10):
```
^$|^(\+?55)?\s?\(?\d{2}\)?[\s-]?\d{4,5}[\s-]?\d{4}$
```
Campo continua opcional — `^$` no início da alternância aceita string
vazia (o valor chega vazio quando o admin não preenche; `null` já é
ignorado pelo Bean Validation por padrão, não precisa de tratamento
especial no regex). Quando preenchido, exige DDD de 2 dígitos seguido de
8 ou 9 dígitos do número, com DDI `55` opcional (com ou sem `+`) e
formatação opcional (espaço, hífen, parênteses no DDD).

Válidos: `11994721933`, `+5511994721933`, `5511994721933`,
`(11) 99472-1933`, `11 9947-1933`, `11 3333-4444`, `""` (vazio).

Inválidos: `123` (poucos dígitos), `abc11994721933` (letras),
`11 994721933999` (dígitos demais), `+1 555 123 4567` (DDI não-BR).

Só valida formato de entrada em novos cadastros/edições — não migra nem
revalida os registros já existentes no banco (alguns podem ter ficado
fora desse padrão antes desta validação existir).

**Response 201**: `ReuniaoResponse` criado.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | campo obrigatório ausente/fora do tamanho; `categoria` inválida; `descricao` ausente/em branco quando `categoria=presencial`; lat/long fora do range; `whatsapp` fora do formato de telefone BR; `cep`/`estado` fora do formato quando preenchidos |
| 403 | `AUTH_FORBIDDEN` | sem `ROLE_ADMIN` |

---

## 4. `PUT /api/v1/admin/reunioes/{id}` (admin edita)

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: sim.
- **Consistência**: forte, síncrona.

**Request** (`EditarReuniaoRequest`): mesmo shape do `CriarReuniaoRequest`.

**Response 200**: `ReuniaoResponse` atualizado.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | mesmo do create |
| 403 | `AUTH_FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `REUNIAO_NAO_ENCONTRADA` | id não existe |

**Importante — coexistência**: editar uma reunião legada sem tocar nos
6 campos de endereço (não enviá-los, ou enviá-los como estavam, ou seja
`null`) é uma operação válida e não falha — o admin pode editar
`nome`/`dia`/`horario`/etc de uma reunião presencial antiga sem ser
forçado a preencher endereço estruturado nessa mesma chamada.

---

## 5.1. `POST /api/v1/admin/reunioes/backfill-geolocalizacao` (admin, backfill único)

**Contexto**: 73 reuniões presenciais em produção têm `latitude`/
`longitude` preenchidos mas `estado`/`cidade`/`bairro` `NULL` (dado
legado anterior a `V9`, nunca migrado — ver seção "Mudança
não-breaking — 2026-08-16 (endereço estruturado, coexistência)"). Isso
deixa os filtros públicos Estado→Cidade→Bairro (`reunioes.ts`) vazios
para essas reuniões, porque o filtro agrupa por esses 3 campos. Esta
tarefa é o backfill único e explícito que aquela seção descartou fazer
automaticamente — aqui é acionado manualmente pelo admin, com revisão
possível via resumo de retorno, não uma migration/`CommandLineRunner`
automático no boot.

- **Autenticação + autorização**: `ROLE_ADMIN`, mesmo `@PreAuthorize` da
  classe `ReuniaoAdminController`.
- **Idempotente**: sim — só processa reuniões com `categoria=presencial`
  AND `estado IS NULL` AND `latitude IS NOT NULL` AND
  `longitude IS NOT NULL`. Reunião já preenchida (por edição manual do
  admin ou por execução anterior deste endpoint) nunca é reprocessada.
  Rodar o endpoint de novo depois que todas as pendentes já foram
  preenchidas é uma chamada válida que não faz nada (0 atualizadas, 0
  falhas).
- **Consistência**: forte, síncrona por item — cada reunião é
  atualizada e commitada individualmente (não numa transação única
  cobrindo o lote inteiro), para que uma falha no meio do lote não
  desfaça os itens já processados. Ver seção "Execução e tratamento de
  falha" abaixo.
- **Rate limit de saída**: 1 requisição/segundo ao Nominatim (ver seção
  "Nominatim" abaixo), aplicado durante o processamento do lote.
  Duração esperada para as 73 reuniões: ~73s, request HTTP síncrona (sem
  endpoint de status/polling — volume baixo o suficiente para o admin
  aguardar a resposta).
- **Rate limit de entrada**: 1 execução/5min, **global** (não
  por-admin) — ver detalhe completo abaixo, na tabela de erros. Não é
  para conter abuso de um único admin (o endpoint é idempotente); é para
  impedir que execuções em sequência ou concorrentes sobrecarreguem o
  Nominatim.
- **Não altera** `local`, `latitude`, `longitude`, nem qualquer outro
  campo além de `estado`/`cidade`/`bairro`.
- **Não processa** reunião `categoria=virtual` (não tem lat/long por
  definição, mesmo raciocínio do restante do contrato).

**Request**: `POST /api/v1/admin/reunioes/backfill-geolocalizacao`, sem
corpo.

**Response 200** (`BackfillGeolocalizacaoResponse`):
```json
{
  "success": true,
  "data": {
    "totalElegiveis": 73,
    "atualizadas": 70,
    "falhas": 3,
    "detalhesFalha": [
      { "reuniaoId": "uuid", "motivo": "NOMINATIM_SEM_RESPOSTA" },
      { "reuniaoId": "uuid", "motivo": "NOMINATIM_ENDERECO_INCOMPLETO" },
      { "reuniaoId": "uuid", "motivo": "NOMINATIM_ESTADO_NAO_MAPEADO" }
    ]
  },
  "error": null
}
```
`reuniaoId` no retorno não é dado pessoal (identifica um registro de
reunião institucional, não um usuário) — ok expor para o admin agir
manualmente sobre os casos de falha.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 403 | `AUTH_FORBIDDEN` | sem `ROLE_ADMIN` |
| 429 | `REUNIAO_BACKFILL_RATE_LIMITED` | rate limit global excedido |

Detalhe do rate limit de entrada (bucket
`reuniao-backfill-geolocalizacao:global`, janela de 5min, capacidade 1):
qualquer admin que chame o endpoint dentro da janela de 5min de uma
execução anterior recebe 429, independente de qual admin disparou a
primeira chamada.

Falha de geocode de um item individual **não é erro HTTP** — o lote
inteiro sempre retorna 200 com o resumo; falhas por item aparecem em
`detalhesFalha`, para o admin decidir se corrige manualmente a reunião
específica (endpoint `PUT` já existente).

### Formato de `estado` — UF, consistente com o resto do contrato

Nominatim retorna o nome completo do estado em `address.state` (ex:
`"São Paulo"`), não a UF. O restante do sistema já usa UF de 2 letras
maiúsculas para `estado` (coluna `VARCHAR(2)`, `normalizarEstado()` no
`ReuniaoService`, filtro do front comparando por igualdade estrita em
`reunioes.ts`). O backfill mapeia nome completo → UF reaproveitando
`UnidadeFederativa` (`br.com.jogadoresanonimos.escritorio.entity`, já
usado pelo módulo `escritorio` com as 27 UFs e nome completo) — ganha um
método novo `deNomeCompleto(String)` em vez de duplicar a lista de
estados num segundo lugar do código. Se o nome retornado pelo Nominatim
não bater com nenhuma das 27 UFs (grafia inesperada, país errado etc.),
o item é registrado como falha (`NOMINATIM_ESTADO_NAO_MAPEADO`), a
reunião não é tocada, e o lote segue para a próxima.

### Nominatim — client dedicado, rate limit e política de uso

Endpoint: `GET https://nominatim.openstreetmap.org/reverse?format=json&lat={lat}&lon={lon}&zoom=18&addressdetails=1`.

- `zoom=18` (nível "building/address") para favorecer retorno de
  `suburb`/`neighbourhood` (bairro), que só aparece em zoom alto.
- Header `User-Agent` obrigatório pela
  [policy do Nominatim](https://operations.osmfoundation.org/policies/nominatim/):
  `JogadoresAnonimosBrasil-Backend/1.0 (contato: <e-mail institucional>)`
  — mesmo domínio já liberado na CSP do frontend
  (`nominatim.openstreetmap.org`), aqui é chamada server-to-server, sem
  CSP envolvida (CSP é só para chamadas partindo do browser).
- Client dedicado `NominatimReverseGeocodingClient` (pacote
  `reuniao.service`, ou `reuniao.client` se o pacote crescer),
  reaproveitando o bean `RestTemplate` global já existente em
  `JogadoresAnonimosApplication`. Timeout de leitura/conexão herda o
  timeout padrão do bean (10s) — suficiente para uma chamada individual,
  o rate limit de 1 req/s é controlado no service de backfill (um
  `Thread.sleep(1000)` síncrono entre chamadas é aceitável aqui: é
  execução administrativa pontual, single-threaded, não um endpoint de
  alto tráfego onde bloquear a thread importaria).
- Mapeamento `address` → campos: `city`/`town`/`municipality`/`village`
  (nessa ordem de prioridade, primeiro não-nulo) para `cidade`;
  `suburb`/`neighbourhood`/`city_district` (nessa ordem) para `bairro`;
  `state` (mapeado para UF, ver seção acima) para `estado`. Ausência de
  `cidade`/`bairro` no retorno do Nominatim não é tratada como falha do
  item — só `estado` é obrigatório para considerar o item "atualizado"
  (é o campo usado no primeiro nível do filtro público; cidade/bairro
  populados parcialmente ainda é estritamente melhor que os 3 `NULL`
  atuais). Falha só ocorre por erro de rede/timeout/HTTP não-2xx
  (`NOMINATIM_SEM_RESPOSTA`), corpo sem o objeto `address`
  (`NOMINATIM_ENDERECO_INCOMPLETO`), `state` sem mapeamento de UF
  (`NOMINATIM_ESTADO_NAO_MAPEADO`), ou a reunião ter deixado de ser
  elegível entre a leitura do lote e o processamento do item individual
  — deletada ou já preenchida por edição manual concorrente do admin
  durante a execução do backfill (`REUNIAO_JA_PROCESSADA_OU_REMOVIDA`,
  proteção defensiva contra corrida, não esperada em uso normal já que
  o backfill é uma operação administrativa pontual).

### Execução e tratamento de falha (sem fila, sem retry automático)

Loop sequencial simples no service, sem mensageria: volume (73 itens,
execução única) não justifica fila/worker assíncrono (ver
"Consistência eventual" nas regras do agente `backend` — só se propõe
para casos que toleram atraso ou precisam de retry/DLQ distribuído, não
é o caso aqui). Cada item é processado por um bean dedicado
(`ReuniaoBackfillGeolocalizacaoItemService.processar`, chamado a partir
de `ReuniaoBackfillGeolocalizacaoService.executar`, o orquestrador do
loop) com `@Transactional(propagation = REQUIRES_NEW)` — bean separado
é necessário porque `@Transactional` do Spring depende de proxy AOP, que
não intercepta uma chamada de método dentro da mesma classe
(self-invocation); com dois beans, cada chamada passa pelo proxy de
fato, então uma falha de geocode ou de gravação num item não desfaz os
itens anteriores já commitados. Sem retry automático dentro do lote —
se um item falha (ex: timeout pontual do Nominatim), ele aparece em
`detalhesFalha` e o admin decide se roda o endpoint de novo (idempotente,
reprocessa só quem continua `NULL`) ou edita manualmente via `PUT`.

### Por que endpoint admin (opção A), não script/`CommandLineRunner`

- Roda no ambiente do backend em produção, que já tem rede liberada e
  timeout configurado, sem exigir acesso a servidor/shell separado.
- Rastreável: fica registrado como qualquer outra ação admin (log
  estruturado com contagem, sem dado pessoal), e o resumo de retorno é
  auditável na hora.
- Idempotente por natureza da query (`estado IS NULL`), então não há
  risco real de "esquecer flag ligada" como haveria com
  `CommandLineRunner` guardado por variável de ambiente (risco de rodar
  de novo sem querer num redeploy, se a env não for removida a tempo).
- `ROLE_ADMIN` já é uma barreira suficiente para uma operação
  administrativa pontual — não precisa de nenhuma proteção adicional
  (a natureza idempotente e não-destrutiva do backfill já limita o
  blast radius de uma chamada indevida a, no pior caso, reprocessar
  geocoding sem necessidade).

**Migration**: nenhuma — reaproveita as colunas `estado`/`cidade`/
`bairro` já existentes desde `V9`. Este endpoint não é permanente no
sentido de "feature contínua do produto", mas também não há necessidade
de removê-lo depois de usado (idempotente, sem custo de manutenção,
mesmo padrão de qualquer outro endpoint admin) — decisão de manter no
código, não de remover em migration futura.

---

## 5.2. `DELETE /api/v1/admin/reunioes/{id}` (admin exclui)

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: sim (segunda chamada no mesmo id retorna 404).
- **Consistência**: forte, síncrona. Exclusão física — reunião não é
  referenciada por nenhuma outra entidade.

**Response 200**: `{ "success": true, "data": { "mensagem": "Reunião removida." }, "error": null }`

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 403 | `AUTH_FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `REUNIAO_NAO_ENCONTRADA` | id não existe |

---

## Migration — tabela `reuniao` (schema baseline)

A tabela `reuniao` não tem migration numerada própria: ela faz parte do
schema consolidado em
`backend/src/main/resources/db/migration/V1__baseline_schema.sql`
(DDL de todas as tabelas do projeto), junto com as demais tabelas
existentes na época da consolidação. Não existe (e não deve ser
recriado) um arquivo `V12__cria_tabela_reuniao.sql` — essa numeração é
resquício de um contrato anterior à consolidação de migrations e não
corresponde a nenhum arquivo real no projeto.

Definição relevante, hoje dentro de `V1__baseline_schema.sql`:

```sql
CREATE TABLE reuniao (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nome VARCHAR(200) NOT NULL,
  categoria VARCHAR(20) NOT NULL,
  descricao VARCHAR(2000) NOT NULL,
  dia VARCHAR(100) NOT NULL,
  horario VARCHAR(50) NOT NULL,
  local VARCHAR(300) NOT NULL,
  whatsapp VARCHAR(20),
  dia_semana VARCHAR(20),
  periodo VARCHAR(20),
  latitude DOUBLE PRECISION,
  longitude DOUBLE PRECISION,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT chk_reuniao_categoria CHECK (categoria IN ('presencial', 'virtual'))
);

CREATE INDEX idx_reuniao_categoria ON reuniao(categoria);
```

`categoria` como `VARCHAR` + `CHECK` (em vez de tipo `ENUM` do Postgres):
mesma convenção usada nos outros módulos do projeto até agora — evita a
complexidade de `ALTER TYPE` em migration futura se um novo valor de
categoria for adicionado. Índice em `categoria` cobre o padrão de acesso
mais comum da página pública (usuário sempre escolhe presencial/virtual
antes de ver a lista) — os demais filtros (dia, período, raio) são
client-side sobre um volume pequeno de linhas, não precisam de índice
dedicado agora.

Como faz parte do schema baseline, essa definição não é mais editada
isoladamente: qualquer alteração de coluna/índice da tabela `reuniao`
a partir daqui é uma migration nova e numerada (padrão *expand/contract*
do projeto), como já é o caso de `V9` abaixo.

---

## Migration — `V9__adiciona_endereco_estruturado_reuniao.sql`

```sql
ALTER TABLE reuniao
  ADD COLUMN cep VARCHAR(9),
  ADD COLUMN numero VARCHAR(20),
  ADD COLUMN logradouro VARCHAR(200),
  ADD COLUMN bairro VARCHAR(100),
  ADD COLUMN cidade VARCHAR(100),
  ADD COLUMN estado VARCHAR(2);

CREATE INDEX idx_reuniao_endereco_busca ON reuniao(estado, cidade, bairro);
```

Padrão *expand*: colunas novas, todas nullable no schema e **sem
nenhuma obrigatoriedade de aplicação** (ver seção "Sem validação
cruzada de endereço" acima) — dado legado fica com os 6 campos `null`
indefinidamente até reedição manual do admin, sem backfill. Reservada
como `V9` porque a numeração de migrations já aplicadas segue a sequência
`V1`–`V8` existente em `backend/src/main/resources/db/migration/`,
sendo `V1__baseline_schema.sql` o schema consolidado que já contém a
tabela `reuniao` original (ver seção "Migration — tabela `reuniao`
(schema baseline)" acima).

Índice composto `(estado, cidade, bairro)`, nessa ordem: cobre o padrão
de busca do admin (pedido do cliente) — filtro sempre começa "amplo"
(estado) e afunila (cidade, depois bairro), e a ordem das colunas no
índice B-tree precisa seguir essa hierarquia para o índice ser útil em
buscas parciais (só estado, ou estado+cidade, ou os três). Busca por
`cep`/`numero`/`logradouro` isolados não tem índice dedicado nesta
entrega — volume institucional baixo (poucas dezenas de linhas) não
justifica índice adicional; sequential scan é aceitável nesse volume.

---

## Migration — `V10__adiciona_link_reuniao.sql`

```sql
ALTER TABLE reuniao
  ADD COLUMN link_reuniao VARCHAR(500);
```

Padrão *expand*: coluna nova, nullable, sem obrigatoriedade de aplicação
— mesmo raciocínio de `V9`. Reservada como `V10`, sequência imediata
após `V9` (última migration existente em
`backend/src/main/resources/db/migration/` no momento desta tarefa). Sem
índice — ver seção "Mudança não-breaking — 2026-08-16 (`linkReuniao`,
campo dedicado)" acima.

---

## Migration — `V11__adiciona_reuniao_horario.sql`

Migration *expand* transacional: cria `reuniao_horario`, adiciona FK com
`ON DELETE CASCADE`, unicidade por reunião/dia e faz backfill de uma linha
para cada reunião usando `dia_semana`/`dia` e `horario` legados. Nenhuma
coluna ou constraint da tabela `reuniao` é removida ou relaxada. Antes de
criar a tabela definitiva, uma tabela temporária normaliza e audita todos os
dias legados. Qualquer valor fora do catálogo aborta a migration inteira com
erro e contagem explícita; nenhum dia desconhecido é convertido para `NULL`.
Após a precondição, `reuniao_horario.dia_semana` é `NOT NULL` e protegido por
`CHECK` com os sete valores canônicos.

---

## Migration — `V12__permite_multiplos_horarios_por_dia.sql`

Migration transacional e sem alteração de dados: remove apenas a constraint
`UNIQUE (reuniao_id, dia_semana)` criada pela `V11` e a substitui por
`UNIQUE (reuniao_id, dia_semana, horario)`. A `V11` permanece imutável por já
ter sido aplicada em produção. FK com `ON DELETE CASCADE`, `NOT NULL`, `CHECK`
de dia canônico e todos os registros existentes permanecem inalterados.

---

## Mudança não-breaking sinalizada — 2026-08-16, endereço estruturado

Ver seção "Mudança não-breaking — 2026-08-16" no topo deste documento.
Resumo para `frontend-services`: `Reuniao`/`CriarReuniaoRequest`/
`EditarReuniaoRequest` (model do service Angular) podem ganhar os 6
campos novos (`cep`, `numero`, `logradouro`, `bairro`, `cidade`,
`estado`, todos `string | null` no response e `string | undefined` no
request), **todos opcionais** — envio é sempre facultativo,
independente de `categoria`. O frontend hoje em prod continua
funcionando sem nenhuma mudança porque `local`/`latitude`/`longitude`
não mudaram de contrato; os campos novos só passam a existir quando o
frontend decidir adotá-los.

## Mudança não-breaking sinalizada — 2026-08-16, `linkReuniao`

Ver seção "Mudança não-breaking — 2026-08-16 (`linkReuniao`, campo
dedicado)" no topo deste documento. Resumo para `frontend-services`:
`Reuniao`/`CriarReuniaoRequest`/`EditarReuniaoRequest` ganham
`linkReuniao: string | null` (response) / `string | undefined`
(request), opcional. Sem mudança de endpoint/rota, só de payload.

## Pendências explícitas para outros agentes

- `frontend-services`: atualizar o model/interface `Reuniao` e os
  requests de criação/edição com os 6 campos novos de endereço, todos
  opcionais (ver shape acima). Sem mudança de endpoint/rota, só de
  payload — não é obrigatório para o contrato atual continuar
  funcionando, é pré-requisito só para a tarefa de UI de endereço
  estruturado do admin. Também precisa incluir `linkReuniao` (novo,
  2026-08-16) no mesmo model/requests.
  Também incluir `agenda: { diaSemana, horario }[]`, com fallback temporário
  para os campos legados ao consumir respostas de uma versão anterior.
- `frontend`: adaptar o formulário de `admin-reunioes.ts`/`.html` para
  capturar os 6 campos de endereço estruturado (sem torná-los
  obrigatórios no formulário nem no backend) — **fora do escopo desta
  tarefa**, tarefa separada que depende só deste contrato já estar
  publicado. Idem para os filtros Estado/Cidade/Bairro da página
  pública de reuniões, que precisam continuar tolerando reunião sem
  esses campos preenchidos. Formulário de reunião virtual (spec `ux-ui`:
  Nome, Dia/Data, Hora, Número/contato, Link da reunião) deve usar o
  campo dedicado `linkReuniao`, não `local`.

## Implementação (backend)

- `br.com.jogadoresanonimos.reuniao.controller.ReuniaoController` —
  `GET /api/v1/reunioes`.
- `br.com.jogadoresanonimos.reuniao.controller.ReuniaoAdminController` —
  os 4 endpoints admin, classe inteira sob
  `@PreAuthorize("hasRole('ADMIN')")`.
- `br.com.jogadoresanonimos.reuniao.service.ReuniaoService` —
  `listarTodas`, `criar`, `editar`, `remover`.
- Regra `permitAll` para `GET /api/v1/reunioes` adicionada em
  `SecurityConfig` (sem essa linha, `anyRequest().authenticated()`
  bloquearia o endpoint público por engano — erro já cometido e
  corrigido em módulos anteriores).
- Testes unitários em
  `backend/src/test/java/br/com/jogadoresanonimos/reuniao/` —
  `ReuniaoControllerTest`, `ReuniaoAdminControllerTest` (mockando
  `ReuniaoService`, sem `MockMvc`, mesmo padrão do módulo `aviso`) e
  `ReuniaoServiceTest` (mockando `ReuniaoRepository`, cobre criação com
  trim/normalização de campos opcionais em branco, edição e remoção,
  incluindo caminhos de `RecursoNaoEncontradoException`, e o caso de
  reunião presencial legada editada sem nenhum dos 6 campos de endereço
  estruturado).

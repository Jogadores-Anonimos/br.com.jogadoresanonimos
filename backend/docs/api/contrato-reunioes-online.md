# Contrato de API — Módulo Reunião Online

## Decisão de modelagem — entidade nova `ReuniaoOnline`, não `categoria='virtual'` em `Reuniao`

Decisão de negócio do cliente. Reunião online passou a ser organizada
por grupo próprio (nome do grupo + telefone de contato), com agenda
própria de dia/horário, sem nenhum dos atributos de local físico que
`Reuniao` carrega (endereço, `latitude`/`longitude`, CEP, UF, vínculo
com `GrupoIntergrupo` por região). Modelar como entidade separada evita:

- Campos de endereço estruturado eternamente `null` para todo o
  universo de reuniões online (o que já acontecia informalmente com
  `categoria=virtual` em `Reuniao`, mas agora deixa de ser um
  sub-conjunto de uma tabela pensada para presencial).
- Acoplar o ciclo de vida de reunião online ao fluxo de sincronização
  automática com `GrupoIntergrupo` por UF (`ReuniaoGrupoSincronizacaoService`),
  que não se aplica — reunião online não pertence a nenhum estado.
- Migração de dado: não há necessidade de mover reuniões
  `categoria=virtual` existentes para a tabela nova nesta entrega
  (decisão do cliente, fora de escopo). `Reuniao.categoria=virtual`
  continua existindo como está, sem deprecação formal ainda.

## Modelo de dados

Tabela `reuniao_online`: `nome` (nome do grupo, texto obrigatório),
`telefone` (WhatsApp de contato, mesmo formato/validação de
`Reuniao.whatsapp`), `criado_em`, `atualizado_em`.

Tabela `reuniao_online_horario`: um item por horário de cada grupo, com
`reuniao_online_id` (`ON DELETE CASCADE`), `dia_semana` canônico
(mesmo enum `SEGUNDA..DOMINGO` de `DiaSemanaReuniao`) e `horario`
(texto livre, ex: `"19h30"`). `UNIQUE (reuniao_online_id, dia_semana,
horario)` — mesmo padrão de `reuniao_horario`/`AgendaItemReuniao`,
permitindo múltiplos horários no mesmo dia, mas não repetir a
combinação exata.

Sem: `descricao`, `email`, endereço, `latitude`/`longitude`, UF, link
de reunião — confirmado com o cliente como fora do modelo.

Sem paginação — mesmo raciocínio de baixo volume institucional do
módulo `reuniao`. Toda listagem devolve o array completo, ordenado por
`nome`.

Convenções gerais (mesmas do restante do projeto):
- Envelope de resposta sempre `{ success, data, error }`.
- Todos os paths sob `/api/v1`.
- Nenhum token/identificador sensível em query string.

---

## 1. `GET /api/v1/reunioes-online` (público, lista todas)

- **Autenticação**: nenhuma.
- **Idempotente**: sim.
- **Consistência**: leitura do primário, tolera poucos segundos de
  atraso — mesmo raciocínio do módulo `reuniao`, sem cache dedicado.
- Retorna todas as reuniões online cadastradas, ordenadas por `nome`.

**Request**: `GET /api/v1/reunioes-online`

**Response 200** (`List<ReuniaoOnlineResponse>`):
```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "nome": "Grupo Online Quinta",
      "telefone": "5511994721933",
      "agenda": [
        { "diaSemana": "QUINTA", "horario": "20h00" },
        { "diaSemana": "SABADO", "horario": "10h00" }
      ],
      "atualizadoEm": "2026-08-30T12:00:00Z"
    }
  ],
  "error": null
}
```

**Erros**: nenhum específico (lista vazia é resposta válida, não erro).

---

## 2. `GET /api/v1/admin/reunioes-online` (admin, lista todas)

- **Autenticação + autorização**: `ROLE_ADMIN`, `@PreAuthorize`.
- **Idempotente**: sim.
- **Consistência**: forte — leitura do primário.
- Mesmo shape do endpoint público.

**Response 200**: mesmo shape do item 1.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 403 | `AUTH_FORBIDDEN` | autenticado sem `ROLE_ADMIN` |

---

## 3. `POST /api/v1/admin/reunioes-online` (admin cria)

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: não.
- **Consistência**: forte, síncrona.

**Request** (`CriarReuniaoOnlineRequest`):
```json
{
  "nome": "string, 1-200 chars",
  "telefone": "string, até 20 chars, opcional — mesmo padrão @Pattern de Reuniao.whatsapp",
  "agenda": [
    { "diaSemana": "QUINTA", "horario": "20h00" },
    { "diaSemana": "SABADO", "horario": "10h00" }
  ]
}
```

`agenda`: obrigatória, pelo menos um item, no máximo 336 (mesmo limite
de `reuniao`). `diaSemana` aceita `SEGUNDA`, `TERCA`, `QUARTA`,
`QUINTA`, `SEXTA`, `SABADO`, `DOMINGO`; `horario` texto obrigatório de
até 50 caracteres. Não permite repetir a mesma combinação `diaSemana` +
`horario`. Resposta ordenada de segunda a domingo, e dentro do mesmo
dia por hora/minuto inicial reconhecida (mesmo
`AgendaItemReuniaoComparator` reaproveitado de `reuniao`).

`telefone`: mesma regex de `Reuniao.whatsapp`
(`^$|^(\+?55)?\s?\(?\d{2}\)?[\s-]?\d{4,5}[\s-]?\d{4}$`). Diferente de
`Reuniao.whatsapp`, **não há telefone padrão institucional aplicado
quando ausente** — reunião online é organizada por grupo próprio, sem
fallback para o número da associação; campo fica `null` quando não
informado. Essa é uma divergência deliberada do padrão herdado de
`Reuniao` (que aplica `"11 99571-6942"` como default), porque aqui o
contato é sempre do grupo, nunca da associação.

**Response 201**: `ReuniaoOnlineResponse` criado.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | `nome` ausente/fora do tamanho; `agenda` vazia/ausente/duplicada/além do limite; `telefone` fora do formato de telefone BR |
| 403 | `AUTH_FORBIDDEN` | sem `ROLE_ADMIN` |

---

## 4. `PUT /api/v1/admin/reunioes-online/{id}` (admin edita)

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: sim.
- **Consistência**: forte, síncrona.

**Request** (`EditarReuniaoOnlineRequest`): mesmo shape do
`CriarReuniaoOnlineRequest`. Edição substitui a agenda inteira pela
lista enviada (sem merge parcial — diferente do modo de compatibilidade
legado de `reuniao`, que não se aplica aqui por não existir cliente
legado consumindo este contrato).

**Response 200**: `ReuniaoOnlineResponse` atualizado.

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 400 | `VALIDATION_ERROR` | mesmo do create |
| 403 | `AUTH_FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `REUNIAO_ONLINE_NAO_ENCONTRADA` | id não existe |

---

## 5. `DELETE /api/v1/admin/reunioes-online/{id}` (admin exclui)

- **Autenticação + autorização**: `ROLE_ADMIN`.
- **Idempotente**: sim (segunda chamada no mesmo id retorna 404).
- **Consistência**: forte, síncrona. Exclusão física — reunião online
  não é referenciada por nenhuma outra entidade.

**Response 200**: `{ "success": true, "data": { "mensagem": "Reunião online removida." }, "error": null }`

**Erros**:
| Código HTTP | `error.code` | Motivo |
|---|---|---|
| 403 | `AUTH_FORBIDDEN` | sem `ROLE_ADMIN` |
| 404 | `REUNIAO_ONLINE_NAO_ENCONTRADA` | id não existe |

---

## Migration — `V15__cria_tabela_reuniao_online.sql`

```sql
CREATE TABLE reuniao_online (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(200) NOT NULL,
    telefone VARCHAR(20),
    criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE reuniao_online_horario (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reuniao_online_id UUID NOT NULL,
    dia_semana VARCHAR(10) NOT NULL,
    horario VARCHAR(50) NOT NULL,
    CONSTRAINT reuniao_online_horario_reuniao_online_id_fkey
        FOREIGN KEY (reuniao_online_id) REFERENCES reuniao_online(id) ON DELETE CASCADE,
    CONSTRAINT reuniao_online_horario_dia_semana_check
        CHECK (dia_semana IN ('SEGUNDA', 'TERCA', 'QUARTA', 'QUINTA', 'SEXTA', 'SABADO', 'DOMINGO')),
    CONSTRAINT reuniao_online_horario_reuniao_dia_horario_key
        UNIQUE (reuniao_online_id, dia_semana, horario)
);
```

Tabela e migration inteiramente novas — sem dado legado para migrar,
sem coexistência a preservar. Reservada como `V15`, sequência imediata
após `V14__vincula_reuniao_grupo_intergrupo.sql` (última migration
existente no momento desta tarefa).

Sem índice adicional além da PK/FK/UNIQUE — mesmo raciocínio de volume
baixo institucional do restante do módulo `reuniao`.

## Implementação (backend)

- Pacote `br.com.jogadoresanonimos.reuniao`, mesmo módulo de `Reuniao`
  (mesmo domínio de negócio — reunião —, só que outro tipo de
  entidade), classes sufixadas `Online` para não colidir com as
  existentes: `ReuniaoOnline` (entity), `AgendaItemReuniaoOnline`
  (embeddable, reaproveita `DiaSemanaReuniao`), `ReuniaoOnlineRepository`,
  `ReuniaoOnlineService`, `ReuniaoOnlineController` (público),
  `ReuniaoOnlineAdminController` (admin).
- `br.com.jogadoresanonimos.reuniao.controller.ReuniaoOnlineController`
  — `GET /api/v1/reunioes-online`.
- `br.com.jogadoresanonimos.reuniao.controller.ReuniaoOnlineAdminController`
  — os 3 endpoints admin, classe inteira sob
  `@PreAuthorize("hasRole('ADMIN')")`.
- `br.com.jogadoresanonimos.reuniao.service.ReuniaoOnlineService` —
  `listarTodas`, `criar`, `editar`, `remover`.
- Regra `permitAll` para `GET /api/v1/reunioes-online` adicionada em
  `SecurityConfig`, mesmo padrão de `/api/v1/reunioes`.
- Testes unitários em `backend/src/test/java/br/com/jogadoresanonimos/reuniao/`
  — `ReuniaoOnlineServiceTest` (mockando `ReuniaoOnlineRepository`) e
  `ReuniaoOnlineAdminControllerTest` (mockando `ReuniaoOnlineService`,
  sem `MockMvc`), mesmo padrão de `ReuniaoServiceTest`/
  `ReuniaoAdminControllerTest`.

## Pendências explícitas para outros agentes

- `frontend-services`: novo service Angular consumindo os endpoints
  acima. Model `ReuniaoOnline { id, nome, telefone, agenda: { diaSemana,
  horario }[], atualizadoEm }`.
- `frontend`/`ux-ui`: decidir onde a lista de reuniões online aparece
  na página pública (hoje reunião virtual aparece misturada em
  `reunioes.ts` via `categoria=virtual`) — fora do escopo desta
  entrega de backend.

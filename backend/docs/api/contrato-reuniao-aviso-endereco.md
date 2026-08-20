# Aviso de coerência endereço x local — `PUT /api/v1/admin/reunioes/{id}`

## Contexto
Reunião tem um campo de texto livre (`local`) e campos estruturados de
endereço (`cep`, `logradouro`, `bairro`, `cidade`, `estado`, `numero`).
Nada impede que um admin atualize só os campos estruturados (ex: via
backfill de geolocalização ou edição manual) sem atualizar o texto de
`local` junto, gerando divergência entre o que está escrito e o que está
estruturado.

Decisão (`system-design`, PENDENCIAS.md seção "Sincronismo e admin"):
validação de coerência **não-bloqueante**. Sem constraint de banco, sem
`NOT NULL` novo, sem migration — é regra de aplicação, avaliada só na
edição.

## Endpoint afetado
`PUT /api/v1/admin/reunioes/{id}` (autenticado, `ROLE_ADMIN`).

Endpoints não afetados (não geram nem retornam `avisos`):
- `GET /api/v1/admin/reunioes` (listagem)
- `POST /api/v1/admin/reunioes` (criação — não há estado anterior para
  comparar)
- `GET /api/v1/reunioes` (público)

## Regra de geração do aviso
No momento da edição, compara o estado da reunião **antes** e **depois**
da chamada:

- Se `cidade` e/ou `estado` mudaram (comparação exata, pós-normalização
  — `estado` maiúsculo, campos vazios tratados como `null`), **e**
- o campo `local` permaneceu **idêntico** ao valor anterior (trim-comparado)

então a resposta inclui um aviso. Não bloqueia o salvamento — a reunião é
sempre persistida com os novos valores enviados no request.

Campos como `dia`, `horario`, `diaSemana`, `periodo`, `whatsapp` nunca
entram nessa checagem (são legitimamente independentes do endereço).

`cep`, `logradouro`, `bairro`, `numero` não disparam o aviso sozinhos —
só `cidade`/`estado`, por serem os campos que mais mudam o sentido do
texto livre em `local` (ex: "Rua X, Centro, São Paulo - SP" ficando
desatualizado depois que a reunião muda de cidade).

## Shape da resposta

`ReuniaoResponse` (`data` do `ApiResponse`) ganhou um campo novo:

```jsonc
{
  "success": true,
  "data": {
    "id": "uuid",
    "nome": "...",
    "categoria": "presencial",
    ...,
    "cidade": "Guarulhos",
    "estado": "SP",
    "linkReuniao": null,
    "avisos": [
      {
        "codigo": "ENDERECO_LOCAL_DIVERGENTE",
        "mensagem": "Cidade/estado do endereço estruturado mudaram, mas o campo \"local\" não foi atualizado junto. Confira se o texto ainda reflete o endereço correto."
      }
    ]
  },
  "error": null
}
```

- `avisos`: array, **sempre presente** (nunca `null` — vazio `[]` quando
  não há divergência). Compatibilidade retroativa: quem hoje ignora
  campos desconhecidos no JSON de resposta não quebra; quem faz parsing
  estrito de schema precisa passar a aceitar o campo novo.
- Cada item de `avisos`: `{ codigo: string, mensagem: string }`.
  `codigo` é estável e deve ser usado pelo frontend para lógica (ex:
  exibir ícone/cor), `mensagem` é apenas texto de apoio, pode mudar sem
  aviso prévio.
- Hoje só existe um `codigo` possível: `ENDERECO_LOCAL_DIVERGENTE`. O
  array já nasce genérico para comportar avisos futuros sem novo
  contrato.

## Classificação
- Não é mudança breaking: campo novo aditivo em resposta já consumida.
- Idempotente: chamar `PUT` duas vezes com o mesmo payload produz o
  mesmo resultado (mesmos avisos, se a divergência persistir).
- Não gera efeito colateral assíncrono, não passa por fila.

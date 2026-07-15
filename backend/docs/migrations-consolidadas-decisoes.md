# Consolidação de migrations Flyway (V1-V30 -> V1__baseline.sql)

Data: 2026-07-14. Executada porque não existe produção ainda — não há
ambiente com dado real que dependa do histórico incremental do Flyway.
Todas as migrations `V1` a `V30` (exceto `V26`, que nunca existiu) foram
substituídas por um único arquivo `V1__baseline.sql`, gerado a partir do
schema real (`pg_dump --schema-only`) do banco local após rodar todas as
migrations originais, complementado com os `INSERT`s de seed relevantes
reaproveitados literalmente das migrations originais.

Este documento existe para não perder o *porquê* de três decisões de
negócio que estavam documentadas como comentário SQL nas migrations
apagadas.

## Lista de migrations originais consolidadas (para rastreabilidade)

V1, V2, V3, V4, V5, V6, V7, V8, V9, V10, V11, V12, V13, V14, V15, V16,
V17, V18, V19, V20, V21, V22, V23, V24, V25, V27, V28, V29, V30.
(`V26` nunca existiu no repositório — não é uma lacuna introduzida por
esta consolidação.)

## V19 — correção da role do usuário-sistema do seed de CMS

O usuário `sistema.seed.cms` (UUID fixo
`00000000-0000-0000-0000-000000000001`) foi criado em V9 só para ancorar
a FK de autoria (`atualizado_por`/`alterado_por`) dos registros de seed
do CMS — ele nunca faz login, tem `senha_hash` com um placeholder
inválido (`SEED_SEM_LOGIN_NAO_USAR`).

Esse usuário recebeu `role = 'ADMIN'` por engano em V9. Isso quebrava o
bootstrap real do admin: o `AdminBootstrap` (`CommandLineRunner` que roda
após o Flyway, criando o admin de verdade a partir das env vars
`ADMIN_BOOTSTRAP_*`) verifica se já existe algum usuário com `role =
'ADMIN'` antes de criar o admin — e encontrava o usuário-sistema,
concluindo (erroneamente) que o admin real já existia, e pulava a
criação dele.

V19 corrigiu isso trocando a role do usuário-sistema para
`PARTICIPANTE`, o que não afeta nada (esse usuário nunca autentica) e
libera o `AdminBootstrap` para criar o admin real normalmente.

**Na baseline:** o `INSERT` do usuário-sistema já nasce direto com
`role = 'PARTICIPANTE'` — não há necessidade de reproduzir o
UPDATE corretivo, já que a baseline representa o estado final, não o
histórico de erro.

## V27 — descarte de dados legados de hotline e nova modelagem por UF/cidade

A tabela `hotline_regiao` originalmente guardava um campo `nome` livre
(texto solto, sem separação estruturada entre UF e cidade). Não havia
forma seguro de fazer parsing automático desse texto livre para popular
novas colunas estruturadas (`uf`, `cidade`) sem risco de gerar dado
errado.

Decisão de negócio tomada em V27: **descartar os registros antigos**
(`DELETE FROM hotline_telefone; DELETE FROM hotline_regiao;`) em vez de
migrá-los com um valor placeholder — a alternativa (ex: preencher UF/
cidade com "N/A" ou adivinhar via regex) faria dado falso parecer uma
região real dentro do painel admin, o que era considerado pior do que
começar a tabela vazia e recadastrar as regiões corretamente depois.

A partir de V27, `hotline_regiao` passou a ter `uf VARCHAR(2) NOT NULL`
e `cidade VARCHAR(150) NOT NULL`, com índice único
`idx_hotline_regiao_uf_cidade` em `(uf, lower(cidade))` — permite buscar
hotline por região sem duplicar UF+cidade (case-insensitive).

**Na baseline:** a tabela `hotline_regiao` já nasce direto no formato
novo (`uf`/`cidade`), sem o campo `nome` legado — não há dado de seed
para essa tabela (ela estava vazia após o descarte, e continua vazia na
baseline; cadastro de regiões de hotline é feito via admin em runtime).

## V30 — migração da agenda de grupo/intergrupo de formato livre para dia/horário estruturado

Antes de V30, cada `grupo_intergrupo` tinha um único campo `horario`
(texto livre, ex: "Terças e quintas, 19h") e uma tabela auxiliar
`grupo_intergrupo_dia_semana` (relacionando o grupo aos dias da semana
em que ocorria, mas sem horário por dia — um grupo com encontros em dias
diferentes não conseguia ter horários diferentes por dia).

V30 substituiu esse modelo por uma tabela `grupo_intergrupo_horario`
com granularidade de `(grupo_intergrupo_id, dia_semana) -> horario`,
permitindo horários distintos por dia da semana dentro do mesmo grupo.
Os dados existentes foram migrados via `INSERT ... SELECT` (join entre
`grupo_intergrupo_dia_semana` e o `horario` antigo do grupo, aplicado a
todos os dias cadastrados daquele grupo — não havia horário
diferenciado por dia até então, então a migração de dado é sem perda
de informação real).

Depois da migração de dados, a coluna `grupo_intergrupo.horario` e a
tabela `grupo_intergrupo_dia_semana` foram **dropadas diretamente**
(sem expand/contract em duas migrations separadas). Justificativa
registrada na própria migration: mesmo precedente já usado em V22 nesse
módulo — não há consumidor externo do contrato antigo além do frontend
deste monorepo (atualizado na mesma entrega) e o volume de dado era
baixo, então o custo de manter uma migration extra de "contract"
separada não se justificava.

**Na baseline:** `grupo_intergrupo` já nasce sem a coluna `horario`, e
`grupo_intergrupo_horario` já nasce como a tabela de agenda —
`grupo_intergrupo_dia_semana` não existe na baseline.

## Observação técnica encontrada durante a consolidação (não é decisão de negócio, mas fica registrado)

No banco local incremental (V1..V30 aplicadas em sequência), a tabela
`vinte_questao` acumulou **duas constraints UNIQUE distintas** na coluna
`ordem`: `vinte_questao_numero_key` (nome herdado de quando a coluna se
chamava `numero`, antes do `RENAME COLUMN` em V21 — o Postgres não
renomeia o nome da constraint automaticamente) e
`vinte_questao_ordem_unique` (criada explicitamente em V21 via `ALTER
TABLE ... ADD CONSTRAINT`). Isso era cruft inofensivo (duas constraints,
mesmo efeito).

Ao gerar a baseline como um único `CREATE TABLE` com as duas cláusulas
`UNIQUE (ordem)` inline, o Postgres **colapsa automaticamente as duas em
uma só** constraint (`vinte_questao_numero_key`) — o índice único
subjacente já cobre a segunda, que vira no-op dentro do mesmo `CREATE
TABLE`. Validado comparando `pg_dump --schema-only` do banco
incremental (V1..V30) com o do banco gerado só pela baseline: essa foi
a única diferença estrutural real encontrada (fora de diffs cosméticos
de formatação de `CHECK` gerados pelo `pg_dump`, que são semanticamente
idênticos). Resultado: a baseline fica com uma tabela mais limpa que o
banco incremental teria, sem nenhuma ação manual — não foi decisão
deliberada de limpeza de schema, foi consequência natural de consolidar
os `ALTER TABLE` num único `CREATE TABLE`.

## Seeds de dados incluídos na baseline

Reaproveitados literalmente (mesmo texto/SQL) das migrations originais,
não regerados via `pg_dump --data-only`, para preservar os comentários
de intenção que já existiam nos `INSERT`s originais:

- Usuário-sistema `sistema.seed.cms` (necessário como FK de autoria dos
  seeds abaixo).
- 10 páginas institucionais do CMS (`cms_pagina`/`cms_bloco`): sobre,
  historia, anonimato, privacidade, termos, jogo-compulsivo,
  uma-nova-chance, programa-de-recuperacao, programa-de-unidade,
  contribuir — já com o markup rico restaurado por V18 e a flag
  `oculta_na_listagem_admin` de V24 aplicada diretamente nos dois
  slugs migrados para conteúdo estático (programa-de-recuperacao,
  programa-de-unidade).
- 20 perguntas oficiais de autoavaliação (`vinte_questao`), seed de V23.
- 10 perguntas de FAQ (`faq`), seed de V28.

**Não incluído:** o usuário `admin` (role ADMIN) visto no banco local
não é seed de migration — é criado em runtime pelo
`AdminBootstrap` (`CommandLineRunner`) a partir das env vars
`ADMIN_BOOTSTRAP_APELIDO`/`ADMIN_BOOTSTRAP_EMAIL`/`ADMIN_BOOTSTRAP_SENHA`.
Incluir esse usuário na baseline duplicaria a criação do admin e
poderia hardcodar um hash de senha de ambiente de dev em um arquivo
versionado — nunca fazer isso.

# Segunda consolidação de migrations (V2-V7 -> V1__baseline.sql)

Data: 2026-07-15. Ainda não existe produção — mesma justificativa da
consolidação anterior. Depois da consolidação de 2026-07-14, seis
migrations novas foram criadas em cima da baseline (`V2` a `V7`), e uma
checagem final identificou que a baseline já estava desatualizada em
relação ao schema real: `hotline_telefone.label` existia na baseline mas
havia sido removida por `V4`, entre outras diferenças. Essas seis
migrations foram consolidadas de volta em um único `V1__baseline.sql`,
seguindo o mesmo processo da primeira consolidação: aplicar a cadeia
completa (baseline anterior + `V2` a `V7`) num banco Postgres 16
descartável, gerar `pg_dump --schema-only` do resultado, e escrever a
nova baseline no mesmo estilo (`CREATE TABLE` agrupado, não o formato
`ALTER TABLE` disperso que o `pg_dump` gera). A nova baseline foi
validada aplicando-a sozinha num segundo banco descartável e comparando
o `pg_dump --schema-only` resultante byte a byte (via `diff`) contra o
dump da cadeia original — resultado idêntico, nenhuma divergência.

## Lista de migrations consolidadas desta vez

V2 (`seed_escritorios_servico_reais`), V3 (`seed_regioes_intergrupo_fixas`),
V4 (`remove_label_hotline_telefone`), V5 (`usuario_deve_trocar_senha`),
V6 (`adiciona_mfa_totp_usuario`), V7 (`cria_tabela_admin_auditoria_log`).

V2 e V3 foram incluídas na consolidação (não só V4-V7) porque também já
existiam em cima da baseline anterior e representam seed de dado, não
alteração de schema — mantê-las como migrations separadas não teria
efeito prático diferente de dobrá-las na baseline, e a baseline deve
sempre representar o estado final completo do schema+seed, não só as
migrations de schema.

## O que mudou no schema (V4-V7)

- **V4** — `ALTER TABLE hotline_telefone DROP COLUMN label`. Campo nunca
  usado pelo frontend depois da modelagem de hotline por UF/cidade
  (introduzida em V27 da consolidação anterior); a baseline nova já
  nasce sem essa coluna.
- **V5** — `usuario.deve_trocar_senha BOOLEAN NOT NULL DEFAULT FALSE`.
  Suporte a fluxo de troca de senha obrigatória (ex: senha temporária
  definida por admin).
- **V6** — `usuario.mfa_secret_cifrado TEXT` e
  `usuario.mfa_ativo BOOLEAN NOT NULL DEFAULT false`. Suporte a MFA
  TOTP para contas administrativas (ver `backend/docs/api/contrato-mfa-admin.md`).
- **V7** — nova tabela `admin_auditoria_log` (id, admin_id, entidade,
  entidade_id, acao, status_anterior, status_novo, criado_em), com
  índices em `(entidade, entidade_id)` e em `admin_id`. Log de auditoria
  de ações administrativas.

Nenhuma dessas migrations envolvia decisão de negócio não-óbvia que
precisasse de registro além do que já está descrito acima — são
alterações aditivas simples (uma coluna removida sem uso, três colunas
novas, uma tabela nova), diferente das decisões de V19/V27/V30 da
consolidação anterior, que exigiram justificativa de descarte de dado
ou de mudança de modelagem.

## Migrations futuras

A partir desta consolidação, a próxima migration nova deve se chamar
`V2__<descrição>.sql` (não `V8`) — o número reinicia porque a baseline
absorveu tudo até `V7`.

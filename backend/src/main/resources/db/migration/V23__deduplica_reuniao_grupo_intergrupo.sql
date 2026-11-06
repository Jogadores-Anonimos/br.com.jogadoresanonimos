-- Corrige dados inconsistentes gerados pela V20 + backfill de pareamento (BACKFILL_PAR=apply):
-- o backfill criou um grupo para cada reuniao orfa e uma reuniao para cada grupo orfao sem
-- comparar nomes, gerando pares duplicados (ex.: dois "Grupo Aracaju") no painel e no site.

-- 1. Grupo SLZ - Bequimao fechou: remove reunioes e grupos com esse nome.
CREATE TEMP TABLE v23_grupo_remover (id UUID PRIMARY KEY) ON COMMIT DROP;

INSERT INTO v23_grupo_remover (id)
SELECT g.id FROM grupo_intergrupo g WHERE g.nome ILIKE '%Bequim%'
UNION
SELECT r.grupo_intergrupo_id FROM reuniao r
WHERE r.nome ILIKE '%Bequim%' AND r.grupo_intergrupo_id IS NOT NULL;

DELETE FROM reuniao
WHERE nome ILIKE '%Bequim%'
   OR grupo_intergrupo_id IN (SELECT id FROM v23_grupo_remover);

-- 2. Reunioes presenciais duplicadas (mesmo nome normalizado + mesma UF):
--    mantem a editada mais recentemente; remove as demais e os grupos vinculados a elas.
CREATE TEMP TABLE v23_reuniao_chave ON COMMIT DROP AS
SELECT r.id,
       r.grupo_intergrupo_id,
       lower(trim(regexp_replace(r.nome, '\s+', ' ', 'g'))) AS nome_norm,
       COALESCE(ei.uf::text, upper(trim(r.estado))) AS uf,
       r.atualizado_em
FROM reuniao r
LEFT JOIN grupo_intergrupo g ON g.id = r.grupo_intergrupo_id
LEFT JOIN estado_intergrupo ei ON ei.id = g.estado_id
WHERE r.categoria = 'presencial';

CREATE TEMP TABLE v23_reuniao_remover ON COMMIT DROP AS
SELECT id, grupo_intergrupo_id
FROM (
    SELECT id, grupo_intergrupo_id,
           row_number() OVER (
               PARTITION BY nome_norm, uf
               ORDER BY (grupo_intergrupo_id IS NOT NULL) DESC, atualizado_em DESC, id
           ) AS posicao
    FROM v23_reuniao_chave
    WHERE uf IS NOT NULL
) ranqueadas
WHERE posicao > 1;

INSERT INTO v23_grupo_remover (id)
SELECT grupo_intergrupo_id FROM v23_reuniao_remover
WHERE grupo_intergrupo_id IS NOT NULL
ON CONFLICT DO NOTHING;

DELETE FROM reuniao WHERE id IN (SELECT id FROM v23_reuniao_remover);

-- 2b. Reunioes presenciais legadas sem grupo (muitas sem UF, uma linha por dia da semana) que ja
--     tem uma reuniao vinculada com o mesmo nome: aparecem no painel mas excluir nao altera o site.
DELETE FROM reuniao o
WHERE o.categoria = 'presencial'
  AND o.grupo_intergrupo_id IS NULL
  AND EXISTS (
      SELECT 1 FROM reuniao l
      WHERE l.grupo_intergrupo_id IS NOT NULL
        AND lower(trim(regexp_replace(l.nome, '\s+', ' ', 'g')))
            = lower(trim(regexp_replace(o.nome, '\s+', ' ', 'g')))
  );

-- 3. Grupos duplicados restantes (mesmo nome normalizado no mesmo estado):
--    mantem o que tem reuniao vinculada (aparece no painel); remove os demais.
INSERT INTO v23_grupo_remover (id)
SELECT id
FROM (
    SELECT g.id,
           row_number() OVER (
               PARTITION BY g.estado_id, lower(trim(regexp_replace(g.nome, '\s+', ' ', 'g')))
               ORDER BY EXISTS (SELECT 1 FROM reuniao r WHERE r.grupo_intergrupo_id = g.id) DESC,
                        g.atualizado_em DESC, g.id
           ) AS posicao
    FROM grupo_intergrupo g
    WHERE g.id NOT IN (SELECT id FROM v23_grupo_remover)
) ranqueados
WHERE posicao > 1
ON CONFLICT DO NOTHING;

-- Reunioes ainda vinculadas a grupos removidos no passo 3 (nao deveria haver, mas o FK e RESTRICT).
UPDATE reuniao SET grupo_intergrupo_id = NULL
WHERE grupo_intergrupo_id IN (SELECT id FROM v23_grupo_remover);

DELETE FROM grupo_intergrupo WHERE id IN (SELECT id FROM v23_grupo_remover);

-- 4. Reunioes presenciais ainda sem grupo: toda reuniao presencial do painel precisa aparecer no site.
--    Com UF, local, cidade e agenda validos -> cria o grupo espelho e vincula.
CREATE TEMP TABLE v23_orfa_espelhar ON COMMIT DROP AS
SELECT r.id AS reuniao_id, gen_random_uuid() AS grupo_id, ei.id AS estado_id,
       r.nome, r.local, r.bairro, r.cidade,
       (SELECT COALESCE(max(g.ordem), 0) FROM grupo_intergrupo g WHERE g.estado_id = ei.id)
           + row_number() OVER (PARTITION BY ei.id ORDER BY r.nome, r.id) AS ordem
FROM reuniao r
JOIN estado_intergrupo ei ON ei.uf::text = upper(trim(r.estado))
WHERE r.categoria = 'presencial'
  AND r.grupo_intergrupo_id IS NULL
  AND trim(COALESCE(r.local, '')) <> ''
  AND trim(COALESCE(r.cidade, '')) <> ''
  AND EXISTS (SELECT 1 FROM reuniao_horario h WHERE h.reuniao_id = r.id);

INSERT INTO grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem)
SELECT grupo_id, estado_id, nome, local, bairro, cidade, ordem FROM v23_orfa_espelhar;

INSERT INTO grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario)
SELECT DISTINCT o.grupo_id, h.dia_semana, h.horario
FROM v23_orfa_espelhar o JOIN reuniao_horario h ON h.reuniao_id = o.reuniao_id;

UPDATE reuniao r SET grupo_intergrupo_id = o.grupo_id
FROM v23_orfa_espelhar o WHERE o.reuniao_id = r.id;

--    Sem UF/cidade (cadastros legados, uma linha por dia, nunca exibidos no site e que nao podem
--    ser editados no painel) -> remove. Se o grupo ainda funciona, recadastrar pelo painel.
DELETE FROM reuniao WHERE categoria = 'presencial' AND grupo_intergrupo_id IS NULL;

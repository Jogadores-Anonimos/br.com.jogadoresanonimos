-- Relatório somente-leitura. Não cria vínculos e não altera dados.
-- Execute com psql; revise manualmente qualquer candidato antes de um backfill aprovado.
WITH grupos AS (
    SELECT g.id,
           g.nome,
           e.uf::text AS uf,
           g.cidade,
           g.endereco,
           lower(regexp_replace(translate(g.nome, 'ÁÀÂÃÄÉÈÊËÍÌÎÏÓÒÔÕÖÚÙÛÜÇ', 'AAAAAEEEEIIIIOOOOOUUUUC'), '[^a-z0-9]+', '', 'g')) AS nome_n,
           lower(regexp_replace(translate(g.cidade, 'ÁÀÂÃÄÉÈÊËÍÌÎÏÓÒÔÕÖÚÙÛÜÇ', 'AAAAAEEEEIIIIOOOOOUUUUC'), '[^a-z0-9]+', '', 'g')) AS cidade_n,
           lower(regexp_replace(translate(g.endereco, 'ÁÀÂÃÄÉÈÊËÍÌÎÏÓÒÔÕÖÚÙÛÜÇ', 'AAAAAEEEEIIIIOOOOOUUUUC'), '[^a-z0-9]+', '', 'g')) AS endereco_n
    FROM grupo_intergrupo g
    JOIN estado_intergrupo e ON e.id = g.estado_id
    WHERE NOT EXISTS (SELECT 1 FROM reuniao rv WHERE rv.grupo_intergrupo_id = g.id)
), reunioes AS (
    SELECT r.id,
           r.nome,
           upper(r.estado) AS uf,
           r.cidade,
           r.local,
           lower(regexp_replace(translate(r.nome, 'ÁÀÂÃÄÉÈÊËÍÌÎÏÓÒÔÕÖÚÙÛÜÇ', 'AAAAAEEEEIIIIOOOOOUUUUC'), '[^a-z0-9]+', '', 'g')) AS nome_n,
           lower(regexp_replace(translate(coalesce(r.cidade, ''), 'ÁÀÂÃÄÉÈÊËÍÌÎÏÓÒÔÕÖÚÙÛÜÇ', 'AAAAAEEEEIIIIOOOOOUUUUC'), '[^a-z0-9]+', '', 'g')) AS cidade_n,
           lower(regexp_replace(translate(r.local, 'ÁÀÂÃÄÉÈÊËÍÌÎÏÓÒÔÕÖÚÙÛÜÇ', 'AAAAAEEEEIIIIOOOOOUUUUC'), '[^a-z0-9]+', '', 'g')) AS endereco_n
    FROM reuniao r
    WHERE r.categoria = 'presencial' AND r.grupo_intergrupo_id IS NULL
), candidatos AS (
    SELECT g.id AS grupo_id, r.id AS reuniao_id
    FROM grupos g
    JOIN reunioes r ON r.nome_n = g.nome_n
                    AND r.uf = g.uf
                    AND r.cidade_n = g.cidade_n
                    AND r.endereco_n = g.endereco_n
)
SELECT g.id AS grupo_id,
       g.nome,
       g.uf,
       g.cidade,
       g.endereco,
       CASE count(c.reuniao_id)
           WHEN 0 THEN 'SEM_PAR'
           WHEN 1 THEN 'CANDIDATO_UNICO_REQUER_REVISAO'
           ELSE 'AMBIGUO'
       END AS classificacao,
       array_remove(array_agg(c.reuniao_id ORDER BY c.reuniao_id), NULL) AS reunioes_candidatas
FROM grupos g
LEFT JOIN candidatos c ON c.grupo_id = g.id
GROUP BY g.id, g.nome, g.uf, g.cidade, g.endereco
ORDER BY classificacao, g.uf, g.cidade, g.nome;

SELECT r.id AS reuniao_sem_grupo_id, r.nome, r.estado AS uf, r.cidade, r.local
FROM reuniao r
WHERE r.categoria = 'presencial'
  AND r.grupo_intergrupo_id IS NULL
  AND NOT EXISTS (
      SELECT 1
      FROM grupo_intergrupo g
      JOIN estado_intergrupo e ON e.id = g.estado_id
      WHERE lower(regexp_replace(g.nome, '[^[:alnum:]]+', '', 'g')) =
            lower(regexp_replace(r.nome, '[^[:alnum:]]+', '', 'g'))
        AND e.uf::text = upper(r.estado)
  )
ORDER BY r.estado, r.cidade, r.nome;

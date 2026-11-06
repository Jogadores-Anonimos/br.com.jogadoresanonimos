-- Em producao as reunioes nunca tinham sido pareadas: os grupos da V20 ficaram sem reuniao e a V23
-- criou um grupo espelho para cada reuniao do painel, duplicando o que aparece no site.
-- O painel (reuniao) e a fonte da verdade.

-- 1. Grupo sem reuniao que tem gemeo (mesmo nome normalizado, mesmo estado) vinculado a reuniao: remove.
DELETE FROM grupo_intergrupo o
WHERE NOT EXISTS (SELECT 1 FROM reuniao r WHERE r.grupo_intergrupo_id = o.id)
  AND EXISTS (
      SELECT 1 FROM grupo_intergrupo l
      JOIN reuniao r ON r.grupo_intergrupo_id = l.id
      WHERE l.estado_id = o.estado_id
        AND lower(trim(regexp_replace(l.nome, '\s+', ' ', 'g')))
            = lower(trim(regexp_replace(o.nome, '\s+', ' ', 'g')))
  );

-- 2. Grupo sem reuniao e sem gemeo: cria a reuniao canonica para ele aparecer (e ser editavel) no painel.
CREATE TEMP TABLE v24_grupo_sem_reuniao ON COMMIT DROP AS
SELECT g.id AS grupo_id, gen_random_uuid() AS reuniao_id, g.nome, g.endereco, g.bairro, g.cidade,
       ei.uf::text AS uf,
       (SELECT h.dia_semana FROM grupo_intergrupo_horario h WHERE h.grupo_intergrupo_id = g.id
        ORDER BY array_position(ARRAY['SEGUNDA','TERCA','QUARTA','QUINTA','SEXTA','SABADO','DOMINGO'],
                                h.dia_semana::text), h.horario LIMIT 1) AS dia_semana,
       (SELECT h.horario FROM grupo_intergrupo_horario h WHERE h.grupo_intergrupo_id = g.id
        ORDER BY array_position(ARRAY['SEGUNDA','TERCA','QUARTA','QUINTA','SEXTA','SABADO','DOMINGO'],
                                h.dia_semana::text), h.horario LIMIT 1) AS horario
FROM grupo_intergrupo g
JOIN estado_intergrupo ei ON ei.id = g.estado_id
WHERE NOT EXISTS (SELECT 1 FROM reuniao r WHERE r.grupo_intergrupo_id = g.id);

-- Grupo sem nenhum horario nao pode virar reuniao valida (agenda obrigatoria): remove.
DELETE FROM grupo_intergrupo WHERE id IN (SELECT grupo_id FROM v24_grupo_sem_reuniao WHERE dia_semana IS NULL);
DELETE FROM v24_grupo_sem_reuniao WHERE dia_semana IS NULL;

UPDATE v24_grupo_sem_reuniao SET dia_semana = CASE dia_semana
    WHEN 'SEGUNDA' THEN 'Segunda' WHEN 'TERCA' THEN 'Terça' WHEN 'QUARTA' THEN 'Quarta'
    WHEN 'QUINTA' THEN 'Quinta' WHEN 'SEXTA' THEN 'Sexta' WHEN 'SABADO' THEN 'Sábado'
    ELSE 'Domingo' END;

INSERT INTO reuniao (id, nome, categoria, descricao, dia, horario, local, dia_semana, periodo,
                     bairro, cidade, estado, grupo_intergrupo_id, criado_em, atualizado_em)
SELECT reuniao_id, nome, 'presencial', 'Reunião presencial cadastrada em Endereços', dia_semana, horario,
       endereco, dia_semana,
       CASE WHEN substring(horario FROM '^\s*(\d{1,2})')::int < 12 THEN 'Manhã'
            WHEN substring(horario FROM '^\s*(\d{1,2})')::int < 18 THEN 'Tarde'
            WHEN substring(horario FROM '^\s*(\d{1,2})') IS NOT NULL THEN 'Noite' END,
       bairro, cidade, uf, grupo_id, now(), now()
FROM v24_grupo_sem_reuniao;

INSERT INTO reuniao_horario (reuniao_id, dia_semana, horario)
SELECT DISTINCT s.reuniao_id, h.dia_semana, h.horario
FROM v24_grupo_sem_reuniao s JOIN grupo_intergrupo_horario h ON h.grupo_intergrupo_id = s.grupo_id;

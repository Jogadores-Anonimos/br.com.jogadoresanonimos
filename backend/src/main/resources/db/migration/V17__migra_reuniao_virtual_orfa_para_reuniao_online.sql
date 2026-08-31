ALTER TABLE reuniao_online
    ADD COLUMN descricao VARCHAR(2000);

INSERT INTO reuniao_online (id, nome, descricao, telefone, criado_em, atualizado_em)
SELECT id, nome, NULLIF(descricao, ''), '21 99472-1933', criado_em, atualizado_em
FROM reuniao
WHERE categoria = 'virtual';

INSERT INTO reuniao_online_horario (reuniao_online_id, dia_semana, horario)
SELECT reuniao_id, dia_semana, horario
FROM reuniao_horario
WHERE reuniao_id IN (SELECT id FROM reuniao WHERE categoria = 'virtual');

DELETE FROM reuniao_horario
WHERE reuniao_id IN (SELECT id FROM reuniao WHERE categoria = 'virtual');

DELETE FROM reuniao
WHERE categoria = 'virtual';

UPDATE reuniao_online_horario
SET horario = lpad(split_part(horario, 'h', 1), 2, '0') || ':' || split_part(horario, 'h', 2)
WHERE horario ~ '^[0-9]{1,2}h[0-9]{2}$';

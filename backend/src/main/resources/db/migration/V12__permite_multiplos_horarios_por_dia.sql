ALTER TABLE reuniao_horario
    DROP CONSTRAINT reuniao_horario_reuniao_dia_key,
    ADD CONSTRAINT reuniao_horario_reuniao_dia_horario_key
        UNIQUE (reuniao_id, dia_semana, horario);

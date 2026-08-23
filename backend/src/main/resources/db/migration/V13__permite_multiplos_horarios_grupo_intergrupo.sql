ALTER TABLE grupo_intergrupo_horario
    DROP CONSTRAINT grupo_intergrupo_horario_pkey,
    ADD CONSTRAINT grupo_intergrupo_horario_pkey
        PRIMARY KEY (grupo_intergrupo_id, dia_semana, horario);

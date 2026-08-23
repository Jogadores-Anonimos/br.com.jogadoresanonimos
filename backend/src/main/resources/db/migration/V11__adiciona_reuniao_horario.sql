CREATE TEMPORARY TABLE reuniao_horario_backfill ON COMMIT DROP AS
SELECT id AS reuniao_id,
       CASE COALESCE(dia_semana, dia)
           WHEN 'Segunda' THEN 'SEGUNDA'
           WHEN 'Segunda-feira' THEN 'SEGUNDA'
           WHEN 'Terça' THEN 'TERCA'
           WHEN 'Terça-feira' THEN 'TERCA'
           WHEN 'Quarta' THEN 'QUARTA'
           WHEN 'Quarta-feira' THEN 'QUARTA'
           WHEN 'Quinta' THEN 'QUINTA'
           WHEN 'Quinta-feira' THEN 'QUINTA'
           WHEN 'Sexta' THEN 'SEXTA'
           WHEN 'Sexta-feira' THEN 'SEXTA'
           WHEN 'Sábado' THEN 'SABADO'
           WHEN 'Domingo' THEN 'DOMINGO'
       END AS dia_semana,
       horario
FROM reuniao;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM reuniao_horario_backfill WHERE dia_semana IS NULL) THEN
        RAISE EXCEPTION 'V11 abortada: % reunião(ões) possuem dia legado inválido',
            (SELECT count(*) FROM reuniao_horario_backfill WHERE dia_semana IS NULL);
    END IF;
END
$$;

CREATE TABLE reuniao_horario (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    reuniao_id uuid NOT NULL,
    dia_semana character varying(10) NOT NULL,
    horario character varying(50) NOT NULL,
    CONSTRAINT reuniao_horario_pkey PRIMARY KEY (id),
    CONSTRAINT reuniao_horario_reuniao_dia_key UNIQUE (reuniao_id, dia_semana),
    CONSTRAINT reuniao_horario_dia_semana_check CHECK (dia_semana IN ('SEGUNDA', 'TERCA', 'QUARTA', 'QUINTA', 'SEXTA', 'SABADO', 'DOMINGO')),
    CONSTRAINT reuniao_horario_reuniao_id_fkey FOREIGN KEY (reuniao_id) REFERENCES reuniao(id) ON DELETE CASCADE
);

INSERT INTO reuniao_horario (reuniao_id, dia_semana, horario)
SELECT reuniao_id,
       dia_semana,
       horario
FROM reuniao_horario_backfill;

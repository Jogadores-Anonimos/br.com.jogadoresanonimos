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

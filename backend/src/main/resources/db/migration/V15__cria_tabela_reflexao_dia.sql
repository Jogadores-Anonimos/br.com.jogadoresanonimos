CREATE TABLE reflexao_dia (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  mes INTEGER NOT NULL,
  dia INTEGER NOT NULL,
  texto VARCHAR(2000) NOT NULL,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT chk_reflexao_dia_mes CHECK (mes BETWEEN 1 AND 12),
  CONSTRAINT chk_reflexao_dia_dia CHECK (dia BETWEEN 1 AND 31)
);

CREATE UNIQUE INDEX uq_reflexao_dia_mes_dia ON reflexao_dia(mes, dia);

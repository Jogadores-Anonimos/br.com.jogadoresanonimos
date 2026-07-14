CREATE TABLE reuniao (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nome VARCHAR(200) NOT NULL,
  categoria VARCHAR(20) NOT NULL,
  descricao VARCHAR(2000) NOT NULL,
  dia VARCHAR(100) NOT NULL,
  horario VARCHAR(50) NOT NULL,
  local VARCHAR(300) NOT NULL,
  whatsapp VARCHAR(20),
  dia_semana VARCHAR(20),
  periodo VARCHAR(20),
  latitude DOUBLE PRECISION,
  longitude DOUBLE PRECISION,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT chk_reuniao_categoria CHECK (categoria IN ('presencial', 'virtual'))
);

CREATE INDEX idx_reuniao_categoria ON reuniao(categoria);

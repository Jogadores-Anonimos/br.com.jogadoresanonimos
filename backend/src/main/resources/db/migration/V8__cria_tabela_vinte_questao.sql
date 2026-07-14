CREATE TABLE vinte_questao (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  numero SMALLINT NOT NULL UNIQUE CHECK (numero BETWEEN 1 AND 20),
  texto VARCHAR(500) NOT NULL,
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_por UUID NOT NULL REFERENCES usuario(id)
);

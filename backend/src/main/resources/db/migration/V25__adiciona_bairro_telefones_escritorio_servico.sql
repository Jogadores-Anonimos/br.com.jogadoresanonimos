ALTER TABLE escritorio_servico ADD COLUMN bairro VARCHAR(150);

CREATE TABLE escritorio_servico_telefone (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  escritorio_id UUID NOT NULL REFERENCES escritorio_servico(id) ON DELETE CASCADE,
  label VARCHAR(100),
  telefone VARCHAR(100) NOT NULL,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_escritorio_servico_telefone_escritorio ON escritorio_servico_telefone(escritorio_id);

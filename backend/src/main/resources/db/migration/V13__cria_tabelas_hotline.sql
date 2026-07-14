CREATE TABLE hotline_regiao (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nome VARCHAR(100) NOT NULL,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX idx_hotline_regiao_nome ON hotline_regiao(nome);

CREATE TABLE hotline_telefone (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  regiao_id UUID NOT NULL REFERENCES hotline_regiao(id) ON DELETE CASCADE,
  label VARCHAR(100) NOT NULL,
  telefone VARCHAR(30) NOT NULL,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_hotline_telefone_regiao ON hotline_telefone(regiao_id);

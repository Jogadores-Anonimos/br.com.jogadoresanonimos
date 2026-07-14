CREATE TABLE aviso (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  titulo VARCHAR(200) NOT NULL,
  corpo VARCHAR(2000) NOT NULL,
  fixado BOOLEAN NOT NULL DEFAULT false,
  data_expiracao TIMESTAMPTZ,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_aviso_fixado_criado_em ON aviso(fixado, criado_em);

CREATE TABLE relato (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  texto VARCHAR(4000) NOT NULL,
  nome_exibicao VARCHAR(30),
  status VARCHAR(20) NOT NULL CHECK (status IN ('pending', 'approved', 'rejected')),
  autor_id UUID NOT NULL REFERENCES usuario(id),
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_relato_status_criado_em ON relato(status, criado_em);
CREATE INDEX idx_relato_autor ON relato(autor_id);

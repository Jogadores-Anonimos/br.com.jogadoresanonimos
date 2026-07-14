CREATE TABLE usuario_arquivo (
  usuario_id UUID NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
  arquivo_id UUID NOT NULL REFERENCES arquivo(id) ON DELETE CASCADE,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  PRIMARY KEY (usuario_id, arquivo_id)
);

CREATE INDEX idx_usuario_arquivo_usuario_criado_em ON usuario_arquivo(usuario_id, criado_em DESC);

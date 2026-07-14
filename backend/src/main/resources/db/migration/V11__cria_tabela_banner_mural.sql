CREATE TABLE banner_mural (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  imagem_id UUID NOT NULL REFERENCES imagem(id),
  alt_text VARCHAR(300) NOT NULL,
  legenda VARCHAR(200),
  link_destino VARCHAR(500),
  ativo BOOLEAN NOT NULL DEFAULT true,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_banner_mural_ativo_criado_em ON banner_mural(ativo, criado_em DESC);
CREATE INDEX idx_banner_mural_imagem ON banner_mural(imagem_id);

CREATE TABLE imagem (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nome_original VARCHAR(255) NOT NULL,
  chave_arquivo VARCHAR(255) NOT NULL UNIQUE,
  tamanho_bytes BIGINT NOT NULL,
  alt VARCHAR(300) NOT NULL,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  criado_por UUID NOT NULL REFERENCES usuario(id)
);

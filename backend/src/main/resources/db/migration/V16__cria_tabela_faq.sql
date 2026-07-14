CREATE TABLE faq (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  pergunta VARCHAR(500) NOT NULL,
  resposta VARCHAR(4000) NOT NULL,
  ordem INTEGER NOT NULL DEFAULT 0,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_faq_ordem ON faq(ordem);

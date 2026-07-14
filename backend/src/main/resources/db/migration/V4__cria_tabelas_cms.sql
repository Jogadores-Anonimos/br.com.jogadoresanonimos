CREATE TABLE cms_pagina (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  slug VARCHAR(100) NOT NULL UNIQUE,
  titulo VARCHAR(200) NOT NULL,
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_por UUID NOT NULL REFERENCES usuario(id)
);

CREATE TABLE cms_bloco (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  pagina_id UUID NOT NULL REFERENCES cms_pagina(id) ON DELETE CASCADE,
  ordem INTEGER NOT NULL,
  tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('texto', 'imagem')),
  conteudo TEXT,
  imagem_id UUID REFERENCES imagem(id),
  alt VARCHAR(300),
  UNIQUE (pagina_id, ordem)
);

CREATE TABLE cms_alteracao_log (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  pagina_id UUID NOT NULL REFERENCES cms_pagina(id),
  alterado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  alterado_por UUID NOT NULL REFERENCES usuario(id),
  snapshot_anterior TEXT NOT NULL
);

CREATE INDEX idx_cms_alteracao_log_pagina ON cms_alteracao_log(pagina_id);
CREATE INDEX idx_cms_bloco_imagem ON cms_bloco(imagem_id);

CREATE TABLE regiao_intergrupo (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nome VARCHAR(100) NOT NULL,
  ordem INTEGER NOT NULL DEFAULT 0,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX idx_regiao_intergrupo_nome ON regiao_intergrupo(nome);
CREATE INDEX idx_regiao_intergrupo_ordem ON regiao_intergrupo(ordem);

CREATE TABLE estado_intergrupo (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  regiao_id UUID NOT NULL REFERENCES regiao_intergrupo(id) ON DELETE CASCADE,
  nome VARCHAR(100) NOT NULL,
  ordem INTEGER NOT NULL DEFAULT 0,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_estado_intergrupo_regiao_id ON estado_intergrupo(regiao_id, ordem);
CREATE UNIQUE INDEX idx_estado_intergrupo_regiao_nome ON estado_intergrupo(regiao_id, nome);

CREATE TABLE grupo_intergrupo (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  estado_id UUID NOT NULL REFERENCES estado_intergrupo(id) ON DELETE CASCADE,
  nome VARCHAR(200) NOT NULL,
  endereco VARCHAR(300) NOT NULL,
  bairro VARCHAR(150),
  cidade VARCHAR(150) NOT NULL,
  telefone VARCHAR(100),
  horarios VARCHAR(300) NOT NULL,
  ordem INTEGER NOT NULL DEFAULT 0,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_grupo_intergrupo_estado_id ON grupo_intergrupo(estado_id, ordem);

CREATE TABLE escritorio_servico (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nome VARCHAR(200) NOT NULL,
  endereco VARCHAR(300) NOT NULL,
  complemento VARCHAR(150),
  cidade VARCHAR(150) NOT NULL,
  emails VARCHAR(500),
  ordem INTEGER NOT NULL DEFAULT 0,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_escritorio_servico_ordem ON escritorio_servico(ordem);

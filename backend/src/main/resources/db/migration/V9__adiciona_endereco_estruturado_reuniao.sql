ALTER TABLE reuniao
  ADD COLUMN cep VARCHAR(9),
  ADD COLUMN numero VARCHAR(20),
  ADD COLUMN logradouro VARCHAR(200),
  ADD COLUMN bairro VARCHAR(100),
  ADD COLUMN cidade VARCHAR(100),
  ADD COLUMN estado VARCHAR(2);

CREATE INDEX idx_reuniao_endereco_busca ON reuniao(estado, cidade, bairro);

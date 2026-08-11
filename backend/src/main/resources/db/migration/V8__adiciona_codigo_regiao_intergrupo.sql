ALTER TABLE regiao_intergrupo ADD COLUMN codigo VARCHAR(20);

UPDATE regiao_intergrupo SET codigo = 'NORTE' WHERE nome = 'Norte';
UPDATE regiao_intergrupo SET codigo = 'NORDESTE' WHERE nome = 'Nordeste';
UPDATE regiao_intergrupo SET codigo = 'CENTRO_OESTE' WHERE nome = 'Centro-Oeste';
UPDATE regiao_intergrupo SET codigo = 'SUDESTE' WHERE nome = 'Sudeste';
UPDATE regiao_intergrupo SET codigo = 'SUL' WHERE nome = 'Sul';

ALTER TABLE regiao_intergrupo ALTER COLUMN codigo SET NOT NULL;

CREATE UNIQUE INDEX idx_regiao_intergrupo_codigo ON regiao_intergrupo(codigo);

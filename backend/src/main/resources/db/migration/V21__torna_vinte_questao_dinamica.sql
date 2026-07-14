ALTER TABLE vinte_questao DROP CONSTRAINT vinte_questao_numero_check;
ALTER TABLE vinte_questao RENAME COLUMN numero TO ordem;
ALTER TABLE vinte_questao ALTER COLUMN ordem DROP NOT NULL;
ALTER TABLE vinte_questao ALTER COLUMN ordem TYPE INTEGER USING ordem::INTEGER;

UPDATE vinte_questao SET ordem = sub.rn - 1
  FROM (SELECT id, row_number() OVER (ORDER BY ordem) AS rn FROM vinte_questao) sub
  WHERE vinte_questao.id = sub.id;

ALTER TABLE vinte_questao ALTER COLUMN ordem SET NOT NULL;
ALTER TABLE vinte_questao ADD CONSTRAINT vinte_questao_ordem_unique UNIQUE (ordem);

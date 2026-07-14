ALTER TABLE estado_intergrupo ADD COLUMN uf CHAR(2);

UPDATE estado_intergrupo SET uf = CASE nome
  WHEN 'Acre' THEN 'AC' WHEN 'Alagoas' THEN 'AL' WHEN 'Amapá' THEN 'AP'
  WHEN 'Amazonas' THEN 'AM' WHEN 'Bahia' THEN 'BA' WHEN 'Ceará' THEN 'CE'
  WHEN 'Distrito Federal' THEN 'DF' WHEN 'Espírito Santo' THEN 'ES'
  WHEN 'Goiás' THEN 'GO' WHEN 'Maranhão' THEN 'MA'
  WHEN 'Mato Grosso' THEN 'MT' WHEN 'Mato Grosso do Sul' THEN 'MS'
  WHEN 'Minas Gerais' THEN 'MG' WHEN 'Pará' THEN 'PA'
  WHEN 'Paraíba' THEN 'PB' WHEN 'Paraná' THEN 'PR'
  WHEN 'Pernambuco' THEN 'PE' WHEN 'Piauí' THEN 'PI'
  WHEN 'Rio de Janeiro' THEN 'RJ' WHEN 'Rio Grande do Norte' THEN 'RN'
  WHEN 'Rio Grande do Sul' THEN 'RS' WHEN 'Rondônia' THEN 'RO'
  WHEN 'Roraima' THEN 'RR' WHEN 'Santa Catarina' THEN 'SC'
  WHEN 'São Paulo' THEN 'SP' WHEN 'Sergipe' THEN 'SE'
  WHEN 'Tocantins' THEN 'TO' ELSE NULL END;

ALTER TABLE estado_intergrupo ALTER COLUMN uf SET NOT NULL;
ALTER TABLE estado_intergrupo DROP COLUMN nome;

DROP INDEX IF EXISTS idx_estado_intergrupo_regiao_nome;
CREATE UNIQUE INDEX idx_estado_intergrupo_regiao_uf ON estado_intergrupo(regiao_id, uf);

CREATE TABLE grupo_intergrupo_dia_semana (
  grupo_id UUID NOT NULL REFERENCES grupo_intergrupo(id) ON DELETE CASCADE,
  dia_semana VARCHAR(10) NOT NULL,
  PRIMARY KEY (grupo_id, dia_semana)
);

ALTER TABLE grupo_intergrupo ADD COLUMN horario VARCHAR(50);
UPDATE grupo_intergrupo SET horario = horarios WHERE horario IS NULL;
ALTER TABLE grupo_intergrupo ALTER COLUMN horario SET NOT NULL;
ALTER TABLE grupo_intergrupo DROP COLUMN horarios;

CREATE TABLE grupo_intergrupo_telefone (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  grupo_id UUID NOT NULL REFERENCES grupo_intergrupo(id) ON DELETE CASCADE,
  label VARCHAR(100),
  telefone VARCHAR(100) NOT NULL,
  criado_em TIMESTAMPTZ NOT NULL DEFAULT now(),
  atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_grupo_intergrupo_telefone_grupo ON grupo_intergrupo_telefone(grupo_id);

INSERT INTO grupo_intergrupo_telefone (grupo_id, label, telefone)
SELECT id, NULL, telefone FROM grupo_intergrupo WHERE telefone IS NOT NULL;

ALTER TABLE grupo_intergrupo DROP COLUMN telefone;

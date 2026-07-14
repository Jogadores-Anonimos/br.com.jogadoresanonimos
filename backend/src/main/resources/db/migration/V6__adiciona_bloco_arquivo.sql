ALTER TABLE cms_bloco ADD COLUMN arquivo_id UUID REFERENCES arquivo(id);
CREATE INDEX idx_cms_bloco_arquivo ON cms_bloco(arquivo_id);

ALTER TABLE cms_bloco DROP CONSTRAINT cms_bloco_tipo_check;
ALTER TABLE cms_bloco ADD CONSTRAINT cms_bloco_tipo_check CHECK (tipo IN ('texto', 'imagem', 'arquivo'));

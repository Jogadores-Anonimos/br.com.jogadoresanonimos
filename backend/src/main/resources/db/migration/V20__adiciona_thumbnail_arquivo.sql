-- Capa/preview de PDF de literatura: imagem da primeira página,
-- gerada no upload via PDFBox (já é dependência do projeto, usada em
-- ArquivoProcessor pra validação). Nullable porque arquivos já
-- existentes antes desta migration não têm thumbnail retroativa.
ALTER TABLE arquivo ADD COLUMN thumbnail_chave VARCHAR(255);

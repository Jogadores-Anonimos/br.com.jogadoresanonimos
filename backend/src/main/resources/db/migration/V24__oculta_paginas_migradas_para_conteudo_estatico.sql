-- programa-de-recuperacao e programa-de-unidade foram migradas para
-- conteúdo 100% estático/hardcoded no Angular (12 Passos e 12 Princípios
-- de Unidade não mudam e não precisam de edição via CMS). O registro em
-- cms_pagina/cms_bloco é preservado por ter conteúdo institucional real
-- e por cms_alteracao_log referenciar pagina_id via FK — apagar via
-- DELETE quebraria rastreabilidade sem necessidade. Solução: soft-exclude
-- da listagem admin via flag, mantendo o dado e o endpoint por slug
-- intactos (expand/contract: aditivo, sem remover nada em uso).
ALTER TABLE cms_pagina
  ADD COLUMN oculta_na_listagem_admin BOOLEAN NOT NULL DEFAULT false;

UPDATE cms_pagina
SET oculta_na_listagem_admin = true
WHERE slug IN ('programa-de-recuperacao', 'programa-de-unidade');

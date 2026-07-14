-- V9 achatou <h2>/<ol>/<li>/<strong> em parágrafo simples porque o
-- HtmlSanitizer só permitia b/i/p/br/a na época. O sanitizer foi ampliado
-- (H2/H3/STRONG/UL/OL/LI) — esta migration é UPDATE (expand/contract já
-- foi feito no sanitizer; aqui só corrigimos o dado), restaurando o
-- markup mais fiel ao HTML original do frontend (mesmo texto literal,
-- só tag trocada). Não editar V9 — ela já foi aplicada.
--
-- ==================== sobre ====================
-- Blocos 2 e 6 eram heading de seção ("O único requisito" e "Sobre a
-- doença do jogo") achatados em <p>; viram <h2>.
UPDATE cms_bloco SET conteudo = '<h2>O único requisito</h2>'
WHERE pagina_id = '00000000-0000-0000-0000-000000000101' AND ordem = 2;

UPDATE cms_bloco SET conteudo = '<h2>Sobre a doença do jogo</h2>'
WHERE pagina_id = '00000000-0000-0000-0000-000000000101' AND ordem = 6;

-- ==================== historia ====================
-- Bloco 3 era a nota "data a confirmar", no HTML original tinha classe
-- de destaque (placeholder-note); sem suporte a classes CSS na
-- allowlist, o destaque possível hoje é <strong> envolvendo o texto.
UPDATE cms_bloco SET conteudo = '<p><strong>[Data e história de fundação da Jogadores Anônimos do Brasil a confirmar com o cliente]</strong></p>'
WHERE pagina_id = '00000000-0000-0000-0000-000000000102' AND ordem = 3;

-- ==================== programa-de-recuperacao (12 passos) ====================
-- Bloco 2 era intro da lista ("Aqui estão os passos..."); blocos 3-14
-- eram os 12 passos em <p> soltos numerados manualmente no texto — viram
-- um único bloco <ol><li> com <strong>N.</strong> por item, igual ao
-- HTML original (ol.passos-lista > li.passo-item > p > strong).
UPDATE cms_bloco SET conteudo = '<ol><li><p><strong>1.</strong> Admitimos que éramos impotentes perante o jogo e que nossas vidas haviam se tornado ingovernáveis.</p></li><li><p><strong>2.</strong> Viemos a acreditar que um Poder superior a nós mesmos poderia devolver-nos à sanidade.</p></li><li><p><strong>3.</strong> Decidimos entregar nossa vontade e nossa vida aos cuidados de Deus, na forma em que O concebíamos.</p></li><li><p><strong>4.</strong> Fizemos minucioso e destemido inventário moral de nós mesmos.</p></li><li><p><strong>5.</strong> Admitimos perante Deus, perante nós mesmos e perante outro ser humano a natureza exata de nossas falhas.</p></li><li><p><strong>6.</strong> Prontificamo-nos inteiramente a deixar que Deus removesse todos esses defeitos de caráter.</p></li><li><p><strong>7.</strong> Humildemente pedimos a Ele que removesse nossos defeitos.</p></li><li><p><strong>8.</strong> Fizemos uma relação de todas as pessoas que tínhamos prejudicado e nos dispusemos a reparar os danos a elas causados.</p></li><li><p><strong>9.</strong> Fizemos reparações diretas dos danos causados a tais pessoas, sempre que possível, salvo quando fazê-lo significasse prejudicá-las ou a outrem.</p></li><li><p><strong>10.</strong> Continuamos fazendo o inventário pessoal e, quando estávamos errados, nós o admitíamos prontamente.</p></li><li><p><strong>11.</strong> Procuramos, através da prece e da meditação, melhorar nosso contato consciente com Deus.</p></li><li><p><strong>12.</strong> Tendo experimentado um despertar espiritual, procuramos transmitir esta mensagem a outros jogadores compulsivos.</p></li></ol>'
WHERE pagina_id = '00000000-0000-0000-0000-000000000108' AND ordem = 3;

DELETE FROM cms_bloco
WHERE pagina_id = '00000000-0000-0000-0000-000000000108' AND ordem BETWEEN 4 AND 14;

-- ==================== programa-de-unidade (12 princípios) ====================
UPDATE cms_bloco SET conteudo = '<ol><li><p><strong>1.</strong> O bem-estar comum deve vir em primeiro lugar.</p></li><li><p><strong>2.</strong> Nossos líderes são servidores de confiança; eles não governam.</p></li><li><p><strong>3.</strong> O único requisito para ser membro é o desejo de parar de jogar.</p></li><li><p><strong>4.</strong> Cada grupo deve ser autônomo, exceto em assuntos que afetem outros grupos ou a Jogadores Anônimos como um todo.</p></li><li><p><strong>5.</strong> Cada grupo tem um único propósito primário: levar a mensagem ao jogador compulsivo que ainda sofre.</p></li><li><p><strong>6.</strong> Um grupo não deve endossar, financiar ou emprestar o nome da Jogadores Anônimos a nenhuma entidade externa.</p></li><li><p><strong>7.</strong> Todo grupo deve ser autossustentável, recusando contribuições externas.</p></li><li><p><strong>8.</strong> A Jogadores Anônimos deve permanecer sempre não-profissional.</p></li><li><p><strong>9.</strong> A Jogadores Anônimos nunca deve ter uma estrutura organizacional formal, mas pode criar juntas ou comitês de serviço diretamente responsáveis aos que servem.</p></li><li><p><strong>10.</strong> A Jogadores Anônimos não tem opinião sobre questões externas; o nome da Jogadores Anônimos nunca deve ser envolvido em controvérsias públicas.</p></li><li><p><strong>11.</strong> Nossa política de relações públicas se baseia na atração, não na promoção; mantemos anonimato pessoal perante a mídia.</p></li><li><p><strong>12.</strong> O anonimato é a base espiritual de todas as nossas tradições.</p></li></ol>'
WHERE pagina_id = '00000000-0000-0000-0000-000000000109' AND ordem = 2;

DELETE FROM cms_bloco
WHERE pagina_id = '00000000-0000-0000-0000-000000000109' AND ordem BETWEEN 3 AND 13;

-- ==================== contribuir ====================
-- Bloco 3 era o heading "Formas de contribuição"; blocos 4-6 eram os
-- itens de lista em <p> soltos — viram <h2> + <ul><li><strong>.
UPDATE cms_bloco SET conteudo = '<h2>Formas de contribuição</h2>'
WHERE pagina_id = '00000000-0000-0000-0000-000000000110' AND ordem = 3;

UPDATE cms_bloco SET conteudo = '<ul><li><strong>Pix:</strong> chave [a definir]</li><li><strong>Transferência bancária:</strong> dados bancários [a definir]</li><li><strong>Contribuição em reunião presencial:</strong> converse com o tesoureiro do seu grupo local</li></ul>'
WHERE pagina_id = '00000000-0000-0000-0000-000000000110' AND ordem = 4;

DELETE FROM cms_bloco
WHERE pagina_id = '00000000-0000-0000-0000-000000000110' AND ordem = 5;

DELETE FROM cms_bloco
WHERE pagina_id = '00000000-0000-0000-0000-000000000110' AND ordem = 6;

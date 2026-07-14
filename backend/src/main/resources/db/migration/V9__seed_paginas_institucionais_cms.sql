-- Seed de conteúdo CMS para as páginas institucionais que ainda eram
-- HTML/array hardcoded no Angular. Conteúdo copiado literalmente do
-- frontend (sobre, historia, anonimato, privacidade, termos,
-- jogo-compulsivo, uma-nova-chance, programa-de-recuperacao,
-- programa-de-unidade, contribuir) — nenhum texto novo foi criado.
--
-- `hotlines` e `perguntas-e-respostas` ficam de fora: as páginas não têm
-- texto introdutório separado da lista estruturada (accordion de
-- estados / FAQ), que é módulo próprio fora deste escopo.
--
-- `vinte-perguntas` já tem módulo dedicado (V8), não usa CMS genérico.
--
-- Blocos usam só as tags permitidas pelo HtmlSanitizer do CMS (b, i, p,
-- br, a) — headings e itens de lista numerada do conteúdo original
-- viram blocos de texto simples em sequência (um bloco por
-- parágrafo/item), sem <h2>/<strong>/<ol>/<li>, para não sofrer strip
-- silencioso na primeira edição feita pelo admin via
-- PUT /api/v1/admin/cms/paginas/{slug}.
--
-- `atualizado_por`/`alterado_por` exigem usuario.id (FK NOT NULL), mas
-- o admin real só é criado em runtime pelo AdminBootstrap (após o
-- Flyway rodar) — não há garantia de admin existente neste momento da
-- migration. Criamos um usuário de sistema com UUID fixo só para
-- ancorar a autoria do seed; ele nunca é usado para login (senha_hash é
-- um placeholder inválido, sem hash bcrypt correspondente).
INSERT INTO usuario (id, apelido, email, senha_hash, role)
VALUES (
  '00000000-0000-0000-0000-000000000001',
  'sistema.seed.cms',
  'sistema.seed.cms@jogadoresanonimos.invalid',
  'SEED_SEM_LOGIN_NAO_USAR',
  'ADMIN'
)
ON CONFLICT (id) DO NOTHING;

-- ==================== sobre ====================
INSERT INTO cms_pagina (id, slug, titulo, atualizado_por)
VALUES ('00000000-0000-0000-0000-000000000101', 'sobre', 'Sobre os Jogadores Anônimos', '00000000-0000-0000-0000-000000000001');

INSERT INTO cms_bloco (pagina_id, ordem, tipo, conteudo) VALUES
('00000000-0000-0000-0000-000000000101', 1, 'texto', '<p>Jogadores Anônimos do Brasil é uma fraternidade de homens e mulheres que compartilham entre si experiência, força e esperança, com o objetivo de resolver um problema comum e ajudar outros a se recuperarem da compulsão pelo jogo.</p>'),
('00000000-0000-0000-0000-000000000101', 2, 'texto', '<p>O único requisito</p>'),
('00000000-0000-0000-0000-000000000101', 3, 'texto', '<p>O único requisito para ser membro é o desejo de parar de jogar. Não há taxas nem mensalidades para participar; nós nos mantemos autossuficientes através das nossas próprias contribuições voluntárias.</p>'),
('00000000-0000-0000-0000-000000000101', 4, 'texto', '<p>Jogadores Anônimos não é afiliada a nenhuma seita, denominação religiosa, partido político, organização ou instituição. Não desejamos nos envolver em nenhuma controvérsia, não apoiamos nem nos opomos a nenhuma causa. Nosso propósito principal é parar de jogar e ajudar outros jogadores compulsivos a alcançarem a mesma recuperação.</p>'),
('00000000-0000-0000-0000-000000000101', 5, 'texto', '<p>Na prática, a Jogadores Anônimos funciona através de reuniões onde os próprios membros se revezam para compartilhar suas experiências — não há terapeutas, médicos ou qualquer figura de autoridade conduzindo os encontros. Quem fala é sempre outro jogador em recuperação, e quem escuta aprende com aquilo que já deu certo (ou não) para outra pessoa que viveu algo parecido. Não existe hierarquia entre os membros: todos têm o mesmo espaço para falar ou apenas ouvir, no seu próprio tempo.</p>'),
('00000000-0000-0000-0000-000000000101', 6, 'texto', '<p>Sobre a doença do jogo</p>'),
('00000000-0000-0000-0000-000000000101', 7, 'texto', '<p>A negação é uma das características mais comuns entre pessoas com compulsão por jogo. Muitos de nós, antes de chegar à irmandade, passamos anos negando que tínhamos um problema, atribuindo nossas dificuldades financeiras e emocionais a azar ou circunstâncias externas.</p>'),
('00000000-0000-0000-0000-000000000101', 8, 'texto', '<p>A perda de controle sobre o próprio comportamento é um traço progressivo dessa doença: o que começa como um hábito aparentemente inofensivo pode evoluir para uma obsessão que compromete a vida familiar, profissional e financeira de quem sofre.</p>'),
('00000000-0000-0000-0000-000000000101', 9, 'texto', '<p>Por isso, a recuperação em Jogadores Anônimos envolve a prática diária de princípios espirituais e de mudanças de caráter, através dos Doze Passos, do apoio mútuo entre os membros e do compartilhamento honesto de experiências.</p>');

-- ==================== historia ====================
INSERT INTO cms_pagina (id, slug, titulo, atualizado_por)
VALUES ('00000000-0000-0000-0000-000000000102', 'historia', 'História', '00000000-0000-0000-0000-000000000001');

INSERT INTO cms_bloco (pagina_id, ordem, tipo, conteudo) VALUES
('00000000-0000-0000-0000-000000000102', 1, 'texto', '<p>A irmandade de Jogadores Anônimos nasceu em janeiro de 1957, quando dois homens que sofriam com a compulsão pelo jogo se encontraram e perceberam que, compartilhando suas experiências e apoiando um ao outro, conseguiam permanecer afastados do jogo. Dessa troca nasceu a base do programa de recuperação que existe até hoje.</p>'),
('00000000-0000-0000-0000-000000000102', 2, 'texto', '<p>Em 13 de setembro de 1957 aconteceu a primeira reunião oficial de Jogadores Anônimos, em Los Angeles, Califórnia. A partir desse encontro, novos grupos foram surgindo em diferentes regiões dos Estados Unidos e, com o passar dos anos, a mensagem de recuperação atravessou fronteiras e alcançou diversos países ao redor do mundo.</p>'),
('00000000-0000-0000-0000-000000000102', 3, 'texto', '<p>[Data e história de fundação da Jogadores Anônimos do Brasil a confirmar com o cliente]</p>');

-- ==================== anonimato ====================
INSERT INTO cms_pagina (id, slug, titulo, atualizado_por)
VALUES ('00000000-0000-0000-0000-000000000103', 'anonimato', 'Anonimato', '00000000-0000-0000-0000-000000000001');

INSERT INTO cms_bloco (pagina_id, ordem, tipo, conteudo) VALUES
('00000000-0000-0000-0000-000000000103', 1, 'texto', '<p>O anonimato é a base sobre a qual a Jogadores Anônimos se sustenta. Aqui, ninguém precisa dizer seu nome completo, sua profissão ou qualquer coisa que não queira compartilhar — o que importa é a experiência de quem está tentando parar de jogar, não quem essa pessoa é fora da sala. Essa proteção existe porque, para muita gente, o medo de ser reconhecido ou julgado é exatamente o que impede de pedir ajuda.</p>'),
('00000000-0000-0000-0000-000000000103', 2, 'texto', '<p>Isso significa que o que você ouve aqui, fica aqui — e o que você compartilha também. Ninguém sai de uma reunião contando quem estava lá ou o que foi dito, e essa regra vale para todos, sem exceção. É esse acordo silencioso que permite que as pessoas falem com sinceridade sobre coisas que talvez nunca tenham dito a mais ninguém: dívidas, mentiras, vergonha, recaídas.</p>'),
('00000000-0000-0000-0000-000000000103', 3, 'texto', '<p>O anonimato também nos protege de um jeito mais simples: ele coloca todo mundo no mesmo nível. Não existe hierarquia entre quem está há dez anos sem jogar e quem chegou ontem — todos são, antes de mais nada, iguais diante do mesmo desafio. Isso tira o peso de "aparecer bem" e deixa espaço para a única coisa que realmente importa aqui, que é se recuperar.</p>'),
('00000000-0000-0000-0000-000000000103', 4, 'texto', '<p>Por isso, se você está pensando em participar de uma reunião ou entrar em contato conosco, saiba que nunca vai ser exigido de você mais do que aquilo com que se sente confortável em compartilhar. Anonimato não é uma regra burocrática — é o que torna possível pedir ajuda sem medo.</p>');

-- ==================== privacidade ====================
INSERT INTO cms_pagina (id, slug, titulo, atualizado_por)
VALUES ('00000000-0000-0000-0000-000000000104', 'privacidade', 'Política de Privacidade', '00000000-0000-0000-0000-000000000001');

INSERT INTO cms_bloco (pagina_id, ordem, tipo, conteudo) VALUES
('00000000-0000-0000-0000-000000000104', 1, 'texto', '<p>A Jogadores Anônimos do Brasil está em processo de adequação à Lei Geral de Proteção de Dados (LGPD). O anonimato dos usuários é a prioridade máxima do site: não coletamos nome real, documento ou qualquer dado que permita identificar quem participa da irmandade ou busca informações por aqui.</p>'),
('00000000-0000-0000-0000-000000000104', 2, 'texto', '<p>Em breve, esta página vai apresentar o texto completo da Política de Privacidade, detalhando quais dados são tratados, com qual finalidade, por quanto tempo são mantidos e como qualquer pessoa pode exercer seus direitos previstos na LGPD.</p>');

-- ==================== termos ====================
INSERT INTO cms_pagina (id, slug, titulo, atualizado_por)
VALUES ('00000000-0000-0000-0000-000000000105', 'termos', 'Termos de Uso', '00000000-0000-0000-0000-000000000001');

INSERT INTO cms_bloco (pagina_id, ordem, tipo, conteudo) VALUES
('00000000-0000-0000-0000-000000000105', 1, 'texto', '<p>Este site tem finalidade informativa e de apoio a pessoas que acham que podem ter um problema com o jogo, além de familiares e interessados na irmandade Jogadores Anônimos do Brasil. O conteúdo aqui publicado não substitui acompanhamento profissional de saúde.</p>'),
('00000000-0000-0000-0000-000000000105', 2, 'texto', '<p>Em breve, esta página vai apresentar o texto completo dos Termos de Uso, incluindo condições de utilização do site, limites de responsabilidade e regras sobre o conteúdo publicado.</p>');

-- ==================== jogo-compulsivo ====================
INSERT INTO cms_pagina (id, slug, titulo, atualizado_por)
VALUES ('00000000-0000-0000-0000-000000000106', 'jogo-compulsivo', 'Jogo Compulsivo', '00000000-0000-0000-0000-000000000001');

INSERT INTO cms_bloco (pagina_id, ordem, tipo, conteudo) VALUES
('00000000-0000-0000-0000-000000000106', 1, 'texto', '<p>O jogo compulsivo é reconhecido como uma doença progressiva: começa muitas vezes de forma discreta, como uma distração ou uma tentativa de resolver um problema financeiro, e vai ocupando cada vez mais espaço na vida da pessoa até que jogar deixa de ser uma escolha e passa a ser uma necessidade difícil de controlar. Não é falta de força de vontade — é um padrão que se instala aos poucos, e que a maioria de nós só reconheceu depois de já ter causado bastante dano.</p>'),
('00000000-0000-0000-0000-000000000106', 2, 'texto', '<p>Alguns sinais comuns de que o jogo pode estar deixando de ser apenas um hábito: pensar constantemente em jogar ou planejar a próxima aposta, precisar apostar quantias cada vez maiores para sentir a mesma emoção, tentar parar e não conseguir, usar o jogo para escapar de problemas ou de sentimentos difíceis, mentir para familiares e amigos sobre o quanto se joga ou se perde, e recorrer a empréstimos ou outras formas de conseguir dinheiro para continuar jogando ou para cobrir perdas anteriores.</p>'),
('00000000-0000-0000-0000-000000000106', 3, 'texto', '<p>Reconhecer esses sinais em si mesmo ou em alguém próximo não é fácil, e é comum que venha acompanhado de vergonha ou negação — muitos de nós levamos anos para admitir que o problema existia. Mas reconhecer é também o primeiro passo possível.</p>'),
('00000000-0000-0000-0000-000000000106', 4, 'texto', '<p>A boa notícia é que existe recuperação, e ela acontece principalmente através do apoio mútuo: pessoas que já passaram ou estão passando pela mesma coisa, compartilhando experiências honestas, sem julgamento. Ninguém precisa enfrentar isso sozinho, e dar o primeiro passo — mesmo que seja só o de vir até aqui — já é parte do caminho.</p>');

-- ==================== uma-nova-chance ====================
INSERT INTO cms_pagina (id, slug, titulo, atualizado_por)
VALUES ('00000000-0000-0000-0000-000000000107', 'uma-nova-chance', 'Uma Nova Chance', '00000000-0000-0000-0000-000000000001');

INSERT INTO cms_bloco (pagina_id, ordem, tipo, conteudo) VALUES
('00000000-0000-0000-0000-000000000107', 1, 'texto', '<p>Se você chegou até aqui, talvez esteja no início de uma decisão difícil, ou talvez já tenha dado os primeiros passos e ainda esteja incerto se vai conseguir continuar. De qualquer lugar que você esteja partindo, é possível recomeçar. Não existe um ponto de dano tão grande que torne a recuperação impossível — existe, sim, um processo, e ele começa exatamente de onde você está agora.</p>'),
('00000000-0000-0000-0000-000000000107', 2, 'texto', '<p>Recomeçar não significa apagar o que aconteceu. Significa reconstruir, aos poucos: a confiança de quem foi magoado, a relação com o próprio dinheiro, a autoestima que o jogo compulsivo costuma corroer silenciosamente. Isso não acontece da noite para o dia, e ninguém espera que aconteça — cada dia sem jogar é, por si só, uma conquista real, mesmo quando ainda não parece suficiente.</p>'),
('00000000-0000-0000-0000-000000000107', 3, 'texto', '<p>Muita gente que hoje ajuda outras pessoas na irmandade um dia esteve exatamente onde você está: incerta, cansada, sem saber se valia a pena tentar de novo. O que sustentou essas pessoas não foi uma solução rápida, foi o apoio de quem já tinha passado por isso e a decisão diária de tentar mais um dia.</p>'),
('00000000-0000-0000-0000-000000000107', 4, 'texto', '<p>Não prometemos que vai ser fácil, mas prometemos que você não vai precisar passar por isso sozinho. Uma nova chance começa com um passo simples: pedir ajuda.</p>');

-- ==================== programa-de-recuperacao (12 passos) ====================
INSERT INTO cms_pagina (id, slug, titulo, atualizado_por)
VALUES ('00000000-0000-0000-0000-000000000108', 'programa-de-recuperacao', 'Programa de Recuperação (Os 12 Passos)', '00000000-0000-0000-0000-000000000001');

INSERT INTO cms_bloco (pagina_id, ordem, tipo, conteudo) VALUES
('00000000-0000-0000-0000-000000000108', 1, 'texto', '<p>Os Doze Passos de Jogadores Anônimos são o caminho de recuperação seguido por milhares de membros da irmandade em todo o mundo. Eles representam uma jornada de honestidade, apoio mútuo e transformação pessoal.</p>'),
('00000000-0000-0000-0000-000000000108', 2, 'texto', '<p>Aqui estão os passos que compõem um Programa de Recuperação:</p>'),
('00000000-0000-0000-0000-000000000108', 3, 'texto', '<p>1. Admitimos que éramos impotentes perante o jogo e que nossas vidas haviam se tornado ingovernáveis.</p>'),
('00000000-0000-0000-0000-000000000108', 4, 'texto', '<p>2. Viemos a acreditar que um Poder superior a nós mesmos poderia devolver-nos à sanidade.</p>'),
('00000000-0000-0000-0000-000000000108', 5, 'texto', '<p>3. Decidimos entregar nossa vontade e nossa vida aos cuidados de Deus, na forma em que O concebíamos.</p>'),
('00000000-0000-0000-0000-000000000108', 6, 'texto', '<p>4. Fizemos minucioso e destemido inventário moral de nós mesmos.</p>'),
('00000000-0000-0000-0000-000000000108', 7, 'texto', '<p>5. Admitimos perante Deus, perante nós mesmos e perante outro ser humano a natureza exata de nossas falhas.</p>'),
('00000000-0000-0000-0000-000000000108', 8, 'texto', '<p>6. Prontificamo-nos inteiramente a deixar que Deus removesse todos esses defeitos de caráter.</p>'),
('00000000-0000-0000-0000-000000000108', 9, 'texto', '<p>7. Humildemente pedimos a Ele que removesse nossos defeitos.</p>'),
('00000000-0000-0000-0000-000000000108', 10, 'texto', '<p>8. Fizemos uma relação de todas as pessoas que tínhamos prejudicado e nos dispusemos a reparar os danos a elas causados.</p>'),
('00000000-0000-0000-0000-000000000108', 11, 'texto', '<p>9. Fizemos reparações diretas dos danos causados a tais pessoas, sempre que possível, salvo quando fazê-lo significasse prejudicá-las ou a outrem.</p>'),
('00000000-0000-0000-0000-000000000108', 12, 'texto', '<p>10. Continuamos fazendo o inventário pessoal e, quando estávamos errados, nós o admitíamos prontamente.</p>'),
('00000000-0000-0000-0000-000000000108', 13, 'texto', '<p>11. Procuramos, através da prece e da meditação, melhorar nosso contato consciente com Deus.</p>'),
('00000000-0000-0000-0000-000000000108', 14, 'texto', '<p>12. Tendo experimentado um despertar espiritual, procuramos transmitir esta mensagem a outros jogadores compulsivos.</p>');

-- ==================== programa-de-unidade (12 princípios) ====================
INSERT INTO cms_pagina (id, slug, titulo, atualizado_por)
VALUES ('00000000-0000-0000-0000-000000000109', 'programa-de-unidade', 'Programa da Unidade (12 Passos da Unidade)', '00000000-0000-0000-0000-000000000001');

INSERT INTO cms_bloco (pagina_id, ordem, tipo, conteudo) VALUES
('00000000-0000-0000-0000-000000000109', 1, 'texto', '<p>Para manter a unidade, nossa experiência mostrou que:</p>'),
('00000000-0000-0000-0000-000000000109', 2, 'texto', '<p>1. O bem-estar comum deve vir em primeiro lugar.</p>'),
('00000000-0000-0000-0000-000000000109', 3, 'texto', '<p>2. Nossos líderes são servidores de confiança; eles não governam.</p>'),
('00000000-0000-0000-0000-000000000109', 4, 'texto', '<p>3. O único requisito para ser membro é o desejo de parar de jogar.</p>'),
('00000000-0000-0000-0000-000000000109', 5, 'texto', '<p>4. Cada grupo deve ser autônomo, exceto em assuntos que afetem outros grupos ou a Jogadores Anônimos como um todo.</p>'),
('00000000-0000-0000-0000-000000000109', 6, 'texto', '<p>5. Cada grupo tem um único propósito primário: levar a mensagem ao jogador compulsivo que ainda sofre.</p>'),
('00000000-0000-0000-0000-000000000109', 7, 'texto', '<p>6. Um grupo não deve endossar, financiar ou emprestar o nome da Jogadores Anônimos a nenhuma entidade externa.</p>'),
('00000000-0000-0000-0000-000000000109', 8, 'texto', '<p>7. Todo grupo deve ser autossustentável, recusando contribuições externas.</p>'),
('00000000-0000-0000-0000-000000000109', 9, 'texto', '<p>8. A Jogadores Anônimos deve permanecer sempre não-profissional.</p>'),
('00000000-0000-0000-0000-000000000109', 10, 'texto', '<p>9. A Jogadores Anônimos nunca deve ter uma estrutura organizacional formal, mas pode criar juntas ou comitês de serviço diretamente responsáveis aos que servem.</p>'),
('00000000-0000-0000-0000-000000000109', 11, 'texto', '<p>10. A Jogadores Anônimos não tem opinião sobre questões externas; o nome da Jogadores Anônimos nunca deve ser envolvido em controvérsias públicas.</p>'),
('00000000-0000-0000-0000-000000000109', 12, 'texto', '<p>11. Nossa política de relações públicas se baseia na atração, não na promoção; mantemos anonimato pessoal perante a mídia.</p>'),
('00000000-0000-0000-0000-000000000109', 13, 'texto', '<p>12. O anonimato é a base espiritual de todas as nossas tradições.</p>');

-- ==================== contribuir ====================
-- Texto informativo (visível a membros) preservado literalmente. A
-- lógica de gate "você é membro?" e o modal de acesso restrito são
-- fluxo de UI no componente Angular, não conteúdo — permanecem fora do
-- CMS.
INSERT INTO cms_pagina (id, slug, titulo, atualizado_por)
VALUES ('00000000-0000-0000-0000-000000000110', 'contribuir', 'Contribua com a Jogadores Anônimos do Brasil', '00000000-0000-0000-0000-000000000001');

INSERT INTO cms_bloco (pagina_id, ordem, tipo, conteudo) VALUES
('00000000-0000-0000-0000-000000000110', 1, 'texto', '<p>A Jogadores Anônimos se mantém apenas com as contribuições voluntárias dos próprios membros e apoiadores. Não aceitamos doações de fontes externas, essa é a nossa Sétima Tradição: "todo grupo deve ser autossustentável, recusando contribuições de fora da irmandade".</p>'),
('00000000-0000-0000-0000-000000000110', 2, 'texto', '<p>Sua contribuição ajuda a manter salas de reunião, materiais de recuperação e a estrutura que permite que mais pessoas encontrem ajuda.</p>'),
('00000000-0000-0000-0000-000000000110', 3, 'texto', '<p>Formas de contribuição</p>'),
('00000000-0000-0000-0000-000000000110', 4, 'texto', '<p>Pix: chave [a definir]</p>'),
('00000000-0000-0000-0000-000000000110', 5, 'texto', '<p>Transferência bancária: dados bancários [a definir]</p>'),
('00000000-0000-0000-0000-000000000110', 6, 'texto', '<p>Contribuição em reunião presencial: converse com o tesoureiro do seu grupo local</p>');

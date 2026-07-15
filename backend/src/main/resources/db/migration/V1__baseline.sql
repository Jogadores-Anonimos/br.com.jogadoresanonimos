CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE usuario (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    apelido character varying(30) NOT NULL,
    email character varying(255) NOT NULL,
    senha_hash character varying(255) NOT NULL,
    role character varying(20) NOT NULL,
    criado_em timestamp with time zone DEFAULT now() NOT NULL,
    senha_alterada_em timestamp with time zone DEFAULT now() NOT NULL,
    deve_trocar_senha boolean DEFAULT false NOT NULL,
    mfa_secret_cifrado text,
    mfa_ativo boolean DEFAULT false NOT NULL,
    CONSTRAINT usuario_role_check CHECK (((role)::text = ANY ((ARRAY['PARTICIPANTE'::character varying, 'ADMIN'::character varying])::text[]))),
    CONSTRAINT usuario_pkey PRIMARY KEY (id),
    CONSTRAINT usuario_apelido_key UNIQUE (apelido),
    CONSTRAINT usuario_email_key UNIQUE (email)
);

CREATE TABLE password_reset_token (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    usuario_id uuid NOT NULL,
    token_hash character varying(255) NOT NULL,
    expira_em timestamp with time zone NOT NULL,
    usado_em timestamp with time zone,
    criado_em timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT password_reset_token_pkey PRIMARY KEY (id),
    CONSTRAINT password_reset_token_token_hash_key UNIQUE (token_hash),
    CONSTRAINT password_reset_token_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE INDEX idx_password_reset_token_usuario ON password_reset_token USING btree (usuario_id);

CREATE TABLE imagem (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    nome_original character varying(255) NOT NULL,
    chave_arquivo character varying(255) NOT NULL,
    tamanho_bytes bigint NOT NULL,
    alt character varying(300) NOT NULL,
    criado_em timestamp with time zone DEFAULT now() NOT NULL,
    criado_por uuid NOT NULL,
    CONSTRAINT imagem_pkey PRIMARY KEY (id),
    CONSTRAINT imagem_chave_arquivo_key UNIQUE (chave_arquivo),
    CONSTRAINT imagem_criado_por_fkey FOREIGN KEY (criado_por) REFERENCES usuario(id)
);

CREATE TABLE arquivo (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    nome_original character varying(255) NOT NULL,
    chave_arquivo character varying(255) NOT NULL,
    tamanho_bytes bigint NOT NULL,
    titulo character varying(300) NOT NULL,
    descricao character varying(500),
    criado_em timestamp with time zone DEFAULT now() NOT NULL,
    criado_por uuid NOT NULL,
    thumbnail_chave character varying(255),
    CONSTRAINT arquivo_pkey PRIMARY KEY (id),
    CONSTRAINT arquivo_chave_arquivo_key UNIQUE (chave_arquivo),
    CONSTRAINT arquivo_criado_por_fkey FOREIGN KEY (criado_por) REFERENCES usuario(id)
);

CREATE TABLE usuario_arquivo (
    usuario_id uuid NOT NULL,
    arquivo_id uuid NOT NULL,
    criado_em timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT usuario_arquivo_pkey PRIMARY KEY (usuario_id, arquivo_id),
    CONSTRAINT usuario_arquivo_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT usuario_arquivo_arquivo_id_fkey FOREIGN KEY (arquivo_id) REFERENCES arquivo(id) ON DELETE CASCADE
);

CREATE INDEX idx_usuario_arquivo_usuario_criado_em ON usuario_arquivo USING btree (usuario_id, criado_em DESC);

CREATE TABLE cms_pagina (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    slug character varying(100) NOT NULL,
    titulo character varying(200) NOT NULL,
    atualizado_em timestamp with time zone DEFAULT now() NOT NULL,
    atualizado_por uuid NOT NULL,
    oculta_na_listagem_admin boolean DEFAULT false NOT NULL,
    CONSTRAINT cms_pagina_pkey PRIMARY KEY (id),
    CONSTRAINT cms_pagina_slug_key UNIQUE (slug),
    CONSTRAINT cms_pagina_atualizado_por_fkey FOREIGN KEY (atualizado_por) REFERENCES usuario(id)
);

CREATE TABLE cms_bloco (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    pagina_id uuid NOT NULL,
    ordem integer NOT NULL,
    tipo character varying(20) NOT NULL,
    conteudo text,
    imagem_id uuid,
    alt character varying(300),
    arquivo_id uuid,
    CONSTRAINT cms_bloco_tipo_check CHECK (((tipo)::text = ANY ((ARRAY['texto'::character varying, 'imagem'::character varying, 'arquivo'::character varying])::text[]))),
    CONSTRAINT cms_bloco_pkey PRIMARY KEY (id),
    CONSTRAINT cms_bloco_pagina_id_ordem_key UNIQUE (pagina_id, ordem),
    CONSTRAINT cms_bloco_pagina_id_fkey FOREIGN KEY (pagina_id) REFERENCES cms_pagina(id) ON DELETE CASCADE,
    CONSTRAINT cms_bloco_imagem_id_fkey FOREIGN KEY (imagem_id) REFERENCES imagem(id),
    CONSTRAINT cms_bloco_arquivo_id_fkey FOREIGN KEY (arquivo_id) REFERENCES arquivo(id)
);

CREATE INDEX idx_cms_bloco_imagem ON cms_bloco USING btree (imagem_id);
CREATE INDEX idx_cms_bloco_arquivo ON cms_bloco USING btree (arquivo_id);

CREATE TABLE cms_alteracao_log (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    pagina_id uuid NOT NULL,
    alterado_em timestamp with time zone DEFAULT now() NOT NULL,
    alterado_por uuid NOT NULL,
    snapshot_anterior text NOT NULL,
    CONSTRAINT cms_alteracao_log_pkey PRIMARY KEY (id),
    CONSTRAINT cms_alteracao_log_pagina_id_fkey FOREIGN KEY (pagina_id) REFERENCES cms_pagina(id),
    CONSTRAINT cms_alteracao_log_alterado_por_fkey FOREIGN KEY (alterado_por) REFERENCES usuario(id)
);

CREATE INDEX idx_cms_alteracao_log_pagina ON cms_alteracao_log USING btree (pagina_id);

CREATE TABLE relato (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    texto character varying(4000) NOT NULL,
    nome_exibicao character varying(30),
    status character varying(20) NOT NULL,
    autor_id uuid NOT NULL,
    criado_em timestamp with time zone DEFAULT now() NOT NULL,
    atualizado_em timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT relato_status_check CHECK (((status)::text = ANY ((ARRAY['pending'::character varying, 'approved'::character varying, 'rejected'::character varying])::text[]))),
    CONSTRAINT relato_pkey PRIMARY KEY (id),
    CONSTRAINT relato_autor_id_fkey FOREIGN KEY (autor_id) REFERENCES usuario(id)
);

CREATE INDEX idx_relato_autor ON relato USING btree (autor_id);
CREATE INDEX idx_relato_status_criado_em ON relato USING btree (status, criado_em);

CREATE TABLE vinte_questao (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    ordem integer NOT NULL,
    texto character varying(500) NOT NULL,
    atualizado_em timestamp with time zone DEFAULT now() NOT NULL,
    atualizado_por uuid NOT NULL,
    CONSTRAINT vinte_questao_pkey PRIMARY KEY (id),
    CONSTRAINT vinte_questao_numero_key UNIQUE (ordem),
    CONSTRAINT vinte_questao_ordem_unique UNIQUE (ordem),
    CONSTRAINT vinte_questao_atualizado_por_fkey FOREIGN KEY (atualizado_por) REFERENCES usuario(id)
);

CREATE TABLE aviso (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    titulo character varying(200) NOT NULL,
    corpo character varying(2000) NOT NULL,
    fixado boolean DEFAULT false NOT NULL,
    data_expiracao timestamp with time zone,
    criado_em timestamp with time zone DEFAULT now() NOT NULL,
    atualizado_em timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT aviso_pkey PRIMARY KEY (id)
);

CREATE INDEX idx_aviso_fixado_criado_em ON aviso USING btree (fixado, criado_em);

CREATE TABLE banner_mural (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    imagem_id uuid NOT NULL,
    alt_text character varying(300) NOT NULL,
    legenda character varying(200),
    link_destino character varying(500),
    ativo boolean DEFAULT true NOT NULL,
    criado_em timestamp with time zone DEFAULT now() NOT NULL,
    atualizado_em timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT banner_mural_pkey PRIMARY KEY (id),
    CONSTRAINT banner_mural_imagem_id_fkey FOREIGN KEY (imagem_id) REFERENCES imagem(id)
);

CREATE INDEX idx_banner_mural_ativo_criado_em ON banner_mural USING btree (ativo, criado_em DESC);
CREATE INDEX idx_banner_mural_imagem ON banner_mural USING btree (imagem_id);

CREATE TABLE reuniao (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    nome character varying(200) NOT NULL,
    categoria character varying(20) NOT NULL,
    descricao character varying(2000) NOT NULL,
    dia character varying(100) NOT NULL,
    horario character varying(50) NOT NULL,
    local character varying(300) NOT NULL,
    whatsapp character varying(20),
    dia_semana character varying(20),
    periodo character varying(20),
    latitude double precision,
    longitude double precision,
    criado_em timestamp with time zone DEFAULT now() NOT NULL,
    atualizado_em timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT chk_reuniao_categoria CHECK (((categoria)::text = ANY ((ARRAY['presencial'::character varying, 'virtual'::character varying])::text[]))),
    CONSTRAINT reuniao_pkey PRIMARY KEY (id)
);

CREATE INDEX idx_reuniao_categoria ON reuniao USING btree (categoria);

CREATE TABLE hotline_regiao (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    criado_em timestamp with time zone DEFAULT now() NOT NULL,
    atualizado_em timestamp with time zone DEFAULT now() NOT NULL,
    uf character varying(2) NOT NULL,
    cidade character varying(150) NOT NULL,
    CONSTRAINT hotline_regiao_pkey PRIMARY KEY (id)
);

CREATE UNIQUE INDEX idx_hotline_regiao_uf_cidade ON hotline_regiao USING btree (uf, lower((cidade)::text));

CREATE TABLE hotline_telefone (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    regiao_id uuid NOT NULL,
    telefone character varying(30) NOT NULL,
    criado_em timestamp with time zone DEFAULT now() NOT NULL,
    atualizado_em timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT hotline_telefone_pkey PRIMARY KEY (id),
    CONSTRAINT hotline_telefone_regiao_id_fkey FOREIGN KEY (regiao_id) REFERENCES hotline_regiao(id) ON DELETE CASCADE
);

CREATE INDEX idx_hotline_telefone_regiao ON hotline_telefone USING btree (regiao_id);

CREATE TABLE regiao_intergrupo (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    nome character varying(100) NOT NULL,
    ordem integer DEFAULT 0 NOT NULL,
    criado_em timestamp with time zone DEFAULT now() NOT NULL,
    atualizado_em timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT regiao_intergrupo_pkey PRIMARY KEY (id)
);

CREATE UNIQUE INDEX idx_regiao_intergrupo_nome ON regiao_intergrupo USING btree (nome);
CREATE INDEX idx_regiao_intergrupo_ordem ON regiao_intergrupo USING btree (ordem);

CREATE TABLE estado_intergrupo (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    regiao_id uuid NOT NULL,
    ordem integer DEFAULT 0 NOT NULL,
    criado_em timestamp with time zone DEFAULT now() NOT NULL,
    atualizado_em timestamp with time zone DEFAULT now() NOT NULL,
    uf character varying(2) NOT NULL,
    CONSTRAINT estado_intergrupo_pkey PRIMARY KEY (id),
    CONSTRAINT estado_intergrupo_regiao_id_fkey FOREIGN KEY (regiao_id) REFERENCES regiao_intergrupo(id) ON DELETE CASCADE
);

CREATE INDEX idx_estado_intergrupo_regiao_id ON estado_intergrupo USING btree (regiao_id, ordem);
CREATE UNIQUE INDEX idx_estado_intergrupo_regiao_uf ON estado_intergrupo USING btree (regiao_id, uf);

CREATE TABLE grupo_intergrupo (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    estado_id uuid NOT NULL,
    nome character varying(200) NOT NULL,
    endereco character varying(300) NOT NULL,
    bairro character varying(150),
    cidade character varying(150) NOT NULL,
    ordem integer DEFAULT 0 NOT NULL,
    criado_em timestamp with time zone DEFAULT now() NOT NULL,
    atualizado_em timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT grupo_intergrupo_pkey PRIMARY KEY (id),
    CONSTRAINT grupo_intergrupo_estado_id_fkey FOREIGN KEY (estado_id) REFERENCES estado_intergrupo(id) ON DELETE CASCADE
);

CREATE INDEX idx_grupo_intergrupo_estado_id ON grupo_intergrupo USING btree (estado_id, ordem);

CREATE TABLE grupo_intergrupo_horario (
    grupo_intergrupo_id uuid NOT NULL,
    dia_semana character varying(10) NOT NULL,
    horario character varying(50) NOT NULL,
    CONSTRAINT grupo_intergrupo_horario_pkey PRIMARY KEY (grupo_intergrupo_id, dia_semana),
    CONSTRAINT grupo_intergrupo_horario_grupo_intergrupo_id_fkey FOREIGN KEY (grupo_intergrupo_id) REFERENCES grupo_intergrupo(id) ON DELETE CASCADE
);

CREATE TABLE grupo_intergrupo_telefone (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    grupo_id uuid NOT NULL,
    label character varying(100),
    telefone character varying(100) NOT NULL,
    criado_em timestamp with time zone DEFAULT now() NOT NULL,
    atualizado_em timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT grupo_intergrupo_telefone_pkey PRIMARY KEY (id),
    CONSTRAINT grupo_intergrupo_telefone_grupo_id_fkey FOREIGN KEY (grupo_id) REFERENCES grupo_intergrupo(id) ON DELETE CASCADE
);

CREATE INDEX idx_grupo_intergrupo_telefone_grupo ON grupo_intergrupo_telefone USING btree (grupo_id);

CREATE TABLE escritorio_servico (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    nome character varying(200) NOT NULL,
    endereco character varying(300) NOT NULL,
    complemento character varying(150),
    cidade character varying(150) NOT NULL,
    emails character varying(500),
    ordem integer DEFAULT 0 NOT NULL,
    criado_em timestamp with time zone DEFAULT now() NOT NULL,
    atualizado_em timestamp with time zone DEFAULT now() NOT NULL,
    bairro character varying(150),
    CONSTRAINT escritorio_servico_pkey PRIMARY KEY (id)
);

CREATE INDEX idx_escritorio_servico_ordem ON escritorio_servico USING btree (ordem);

CREATE TABLE escritorio_servico_telefone (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    escritorio_id uuid NOT NULL,
    label character varying(100),
    telefone character varying(100) NOT NULL,
    criado_em timestamp with time zone DEFAULT now() NOT NULL,
    atualizado_em timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT escritorio_servico_telefone_pkey PRIMARY KEY (id),
    CONSTRAINT escritorio_servico_telefone_escritorio_id_fkey FOREIGN KEY (escritorio_id) REFERENCES escritorio_servico(id) ON DELETE CASCADE
);

CREATE INDEX idx_escritorio_servico_telefone_escritorio ON escritorio_servico_telefone USING btree (escritorio_id);

CREATE TABLE reflexao_dia (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    mes integer NOT NULL,
    dia integer NOT NULL,
    texto character varying(2000) NOT NULL,
    criado_em timestamp with time zone DEFAULT now() NOT NULL,
    atualizado_em timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT chk_reflexao_dia_dia CHECK (((dia >= 1) AND (dia <= 31))),
    CONSTRAINT chk_reflexao_dia_mes CHECK (((mes >= 1) AND (mes <= 12))),
    CONSTRAINT reflexao_dia_pkey PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uq_reflexao_dia_mes_dia ON reflexao_dia USING btree (mes, dia);

CREATE TABLE faq (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    pergunta character varying(500) NOT NULL,
    resposta character varying(4000) NOT NULL,
    ordem integer DEFAULT 0 NOT NULL,
    criado_em timestamp with time zone DEFAULT now() NOT NULL,
    atualizado_em timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT faq_pkey PRIMARY KEY (id)
);

CREATE INDEX idx_faq_ordem ON faq USING btree (ordem);

CREATE TABLE admin_auditoria_log (
    id UUID PRIMARY KEY,
    admin_id UUID NOT NULL,
    entidade VARCHAR(50) NOT NULL,
    entidade_id UUID NOT NULL,
    acao VARCHAR(20) NOT NULL,
    status_anterior VARCHAR(20),
    status_novo VARCHAR(20) NOT NULL,
    criado_em TIMESTAMP NOT NULL
);

CREATE INDEX idx_admin_auditoria_log_entidade_entidade_id ON admin_auditoria_log (entidade, entidade_id);
CREATE INDEX idx_admin_auditoria_log_admin_id ON admin_auditoria_log (admin_id);

INSERT INTO usuario (id, apelido, email, senha_hash, role)
VALUES (
  '00000000-0000-0000-0000-000000000001',
  'sistema.seed.cms',
  'sistema.seed.cms@jogadoresanonimos.invalid',
  'SEED_SEM_LOGIN_NAO_USAR',
  'PARTICIPANTE'
);

INSERT INTO cms_pagina (id, slug, titulo, atualizado_por)
VALUES ('00000000-0000-0000-0000-000000000101', 'sobre', 'Sobre os Jogadores Anônimos', '00000000-0000-0000-0000-000000000001');

INSERT INTO cms_bloco (pagina_id, ordem, tipo, conteudo) VALUES
('00000000-0000-0000-0000-000000000101', 1, 'texto', '<p>Jogadores Anônimos do Brasil é uma fraternidade de homens e mulheres que compartilham entre si experiência, força e esperança, com o objetivo de resolver um problema comum e ajudar outros a se recuperarem da compulsão pelo jogo.</p>'),
('00000000-0000-0000-0000-000000000101', 2, 'texto', '<h2>O único requisito</h2>'),
('00000000-0000-0000-0000-000000000101', 3, 'texto', '<p>O único requisito para ser membro é o desejo de parar de jogar. Não há taxas nem mensalidades para participar; nós nos mantemos autossuficientes através das nossas próprias contribuições voluntárias.</p>'),
('00000000-0000-0000-0000-000000000101', 4, 'texto', '<p>Jogadores Anônimos não é afiliada a nenhuma seita, denominação religiosa, partido político, organização ou instituição. Não desejamos nos envolver em nenhuma controvérsia, não apoiamos nem nos opomos a nenhuma causa. Nosso propósito principal é parar de jogar e ajudar outros jogadores compulsivos a alcançarem a mesma recuperação.</p>'),
('00000000-0000-0000-0000-000000000101', 5, 'texto', '<p>Na prática, a Jogadores Anônimos funciona através de reuniões onde os próprios membros se revezam para compartilhar suas experiências — não há terapeutas, médicos ou qualquer figura de autoridade conduzindo os encontros. Quem fala é sempre outro jogador em recuperação, e quem escuta aprende com aquilo que já deu certo (ou não) para outra pessoa que viveu algo parecido. Não existe hierarquia entre os membros: todos têm o mesmo espaço para falar ou apenas ouvir, no seu próprio tempo.</p>'),
('00000000-0000-0000-0000-000000000101', 6, 'texto', '<h2>Sobre a doença do jogo</h2>'),
('00000000-0000-0000-0000-000000000101', 7, 'texto', '<p>A negação é uma das características mais comuns entre pessoas com compulsão por jogo. Muitos de nós, antes de chegar à irmandade, passamos anos negando que tínhamos um problema, atribuindo nossas dificuldades financeiras e emocionais a azar ou circunstâncias externas.</p>'),
('00000000-0000-0000-0000-000000000101', 8, 'texto', '<p>A perda de controle sobre o próprio comportamento é um traço progressivo dessa doença: o que começa como um hábito aparentemente inofensivo pode evoluir para uma obsessão que compromete a vida familiar, profissional e financeira de quem sofre.</p>'),
('00000000-0000-0000-0000-000000000101', 9, 'texto', '<p>Por isso, a recuperação em Jogadores Anônimos envolve a prática diária de princípios espirituais e de mudanças de caráter, através dos Doze Passos, do apoio mútuo entre os membros e do compartilhamento honesto de experiências.</p>');

INSERT INTO cms_pagina (id, slug, titulo, atualizado_por)
VALUES ('00000000-0000-0000-0000-000000000102', 'historia', 'História', '00000000-0000-0000-0000-000000000001');

INSERT INTO cms_bloco (pagina_id, ordem, tipo, conteudo) VALUES
('00000000-0000-0000-0000-000000000102', 1, 'texto', '<p>A irmandade de Jogadores Anônimos nasceu em janeiro de 1957, quando dois homens que sofriam com a compulsão pelo jogo se encontraram e perceberam que, compartilhando suas experiências e apoiando um ao outro, conseguiam permanecer afastados do jogo. Dessa troca nasceu a base do programa de recuperação que existe até hoje.</p>'),
('00000000-0000-0000-0000-000000000102', 2, 'texto', '<p>Em 13 de setembro de 1957 aconteceu a primeira reunião oficial de Jogadores Anônimos, em Los Angeles, Califórnia. A partir desse encontro, novos grupos foram surgindo em diferentes regiões dos Estados Unidos e, com o passar dos anos, a mensagem de recuperação atravessou fronteiras e alcançou diversos países ao redor do mundo.</p>'),
('00000000-0000-0000-0000-000000000102', 3, 'texto', '<p><strong>[Data e história de fundação da Jogadores Anônimos do Brasil a confirmar com o cliente]</strong></p>');

INSERT INTO cms_pagina (id, slug, titulo, atualizado_por)
VALUES ('00000000-0000-0000-0000-000000000103', 'anonimato', 'Anonimato', '00000000-0000-0000-0000-000000000001');

INSERT INTO cms_bloco (pagina_id, ordem, tipo, conteudo) VALUES
('00000000-0000-0000-0000-000000000103', 1, 'texto', '<p>O anonimato é a base sobre a qual a Jogadores Anônimos se sustenta. Aqui, ninguém precisa dizer seu nome completo, sua profissão ou qualquer coisa que não queira compartilhar — o que importa é a experiência de quem está tentando parar de jogar, não quem essa pessoa é fora da sala. Essa proteção existe porque, para muita gente, o medo de ser reconhecido ou julgado é exatamente o que impede de pedir ajuda.</p>'),
('00000000-0000-0000-0000-000000000103', 2, 'texto', '<p>Isso significa que o que você ouve aqui, fica aqui — e o que você compartilha também. Ninguém sai de uma reunião contando quem estava lá ou o que foi dito, e essa regra vale para todos, sem exceção. É esse acordo silencioso que permite que as pessoas falem com sinceridade sobre coisas que talvez nunca tenham dito a mais ninguém: dívidas, mentiras, vergonha, recaídas.</p>'),
('00000000-0000-0000-0000-000000000103', 3, 'texto', '<p>O anonimato também nos protege de um jeito mais simples: ele coloca todo mundo no mesmo nível. Não existe hierarquia entre quem está há dez anos sem jogar e quem chegou ontem — todos são, antes de mais nada, iguais diante do mesmo desafio. Isso tira o peso de "aparecer bem" e deixa espaço para a única coisa que realmente importa aqui, que é se recuperar.</p>'),
('00000000-0000-0000-0000-000000000103', 4, 'texto', '<p>Por isso, se você está pensando em participar de uma reunião ou entrar em contato conosco, saiba que nunca vai ser exigido de você mais do que aquilo com que se sente confortável em compartilhar. Anonimato não é uma regra burocrática — é o que torna possível pedir ajuda sem medo.</p>');

INSERT INTO cms_pagina (id, slug, titulo, atualizado_por)
VALUES ('00000000-0000-0000-0000-000000000104', 'privacidade', 'Política de Privacidade', '00000000-0000-0000-0000-000000000001');

INSERT INTO cms_bloco (pagina_id, ordem, tipo, conteudo) VALUES
('00000000-0000-0000-0000-000000000104', 1, 'texto', '<p>A Jogadores Anônimos do Brasil está em processo de adequação à Lei Geral de Proteção de Dados (LGPD). O anonimato dos usuários é a prioridade máxima do site: não coletamos nome real, documento ou qualquer dado que permita identificar quem participa da irmandade ou busca informações por aqui.</p>'),
('00000000-0000-0000-0000-000000000104', 2, 'texto', '<p>Em breve, esta página vai apresentar o texto completo da Política de Privacidade, detalhando quais dados são tratados, com qual finalidade, por quanto tempo são mantidos e como qualquer pessoa pode exercer seus direitos previstos na LGPD.</p>');

INSERT INTO cms_pagina (id, slug, titulo, atualizado_por)
VALUES ('00000000-0000-0000-0000-000000000105', 'termos', 'Termos de Uso', '00000000-0000-0000-0000-000000000001');

INSERT INTO cms_bloco (pagina_id, ordem, tipo, conteudo) VALUES
('00000000-0000-0000-0000-000000000105', 1, 'texto', '<p>Este site tem finalidade informativa e de apoio a pessoas que acham que podem ter um problema com o jogo, além de familiares e interessados na irmandade Jogadores Anônimos do Brasil. O conteúdo aqui publicado não substitui acompanhamento profissional de saúde.</p>'),
('00000000-0000-0000-0000-000000000105', 2, 'texto', '<p>Em breve, esta página vai apresentar o texto completo dos Termos de Uso, incluindo condições de utilização do site, limites de responsabilidade e regras sobre o conteúdo publicado.</p>');

INSERT INTO cms_pagina (id, slug, titulo, atualizado_por)
VALUES ('00000000-0000-0000-0000-000000000106', 'jogo-compulsivo', 'Jogo Compulsivo', '00000000-0000-0000-0000-000000000001');

INSERT INTO cms_bloco (pagina_id, ordem, tipo, conteudo) VALUES
('00000000-0000-0000-0000-000000000106', 1, 'texto', '<p>O jogo compulsivo é reconhecido como uma doença progressiva: começa muitas vezes de forma discreta, como uma distração ou uma tentativa de resolver um problema financeiro, e vai ocupando cada vez mais espaço na vida da pessoa até que jogar deixa de ser uma escolha e passa a ser uma necessidade difícil de controlar. Não é falta de força de vontade — é um padrão que se instala aos poucos, e que a maioria de nós só reconheceu depois de já ter causado bastante dano.</p>'),
('00000000-0000-0000-0000-000000000106', 2, 'texto', '<p>Alguns sinais comuns de que o jogo pode estar deixando de ser apenas um hábito: pensar constantemente em jogar ou planejar a próxima aposta, precisar apostar quantias cada vez maiores para sentir a mesma emoção, tentar parar e não conseguir, usar o jogo para escapar de problemas ou de sentimentos difíceis, mentir para familiares e amigos sobre o quanto se joga ou se perde, e recorrer a empréstimos ou outras formas de conseguir dinheiro para continuar jogando ou para cobrir perdas anteriores.</p>'),
('00000000-0000-0000-0000-000000000106', 3, 'texto', '<p>Reconhecer esses sinais em si mesmo ou em alguém próximo não é fácil, e é comum que venha acompanhado de vergonha ou negação — muitos de nós levamos anos para admitir que o problema existia. Mas reconhecer é também o primeiro passo possível.</p>'),
('00000000-0000-0000-0000-000000000106', 4, 'texto', '<p>A boa notícia é que existe recuperação, e ela acontece principalmente através do apoio mútuo: pessoas que já passaram ou estão passando pela mesma coisa, compartilhando experiências honestas, sem julgamento. Ninguém precisa enfrentar isso sozinho, e dar o primeiro passo — mesmo que seja só o de vir até aqui — já é parte do caminho.</p>');

INSERT INTO cms_pagina (id, slug, titulo, atualizado_por)
VALUES ('00000000-0000-0000-0000-000000000107', 'uma-nova-chance', 'Uma Nova Chance', '00000000-0000-0000-0000-000000000001');

INSERT INTO cms_bloco (pagina_id, ordem, tipo, conteudo) VALUES
('00000000-0000-0000-0000-000000000107', 1, 'texto', '<p>Se você chegou até aqui, talvez esteja no início de uma decisão difícil, ou talvez já tenha dado os primeiros passos e ainda esteja incerto se vai conseguir continuar. De qualquer lugar que você esteja partindo, é possível recomeçar. Não existe um ponto de dano tão grande que torne a recuperação impossível — existe, sim, um processo, e ele começa exatamente de onde você está agora.</p>'),
('00000000-0000-0000-0000-000000000107', 2, 'texto', '<p>Recomeçar não significa apagar o que aconteceu. Significa reconstruir, aos poucos: a confiança de quem foi magoado, a relação com o próprio dinheiro, a autoestima que o jogo compulsivo costuma corroer silenciosamente. Isso não acontece da noite para o dia, e ninguém espera que aconteça — cada dia sem jogar é, por si só, uma conquista real, mesmo quando ainda não parece suficiente.</p>'),
('00000000-0000-0000-0000-000000000107', 3, 'texto', '<p>Muita gente que hoje ajuda outras pessoas na irmandade um dia esteve exatamente onde você está: incerta, cansada, sem saber se valia a pena tentar de novo. O que sustentou essas pessoas não foi uma solução rápida, foi o apoio de quem já tinha passado por isso e a decisão diária de tentar mais um dia.</p>'),
('00000000-0000-0000-0000-000000000107', 4, 'texto', '<p>Não prometemos que vai ser fácil, mas prometemos que você não vai precisar passar por isso sozinho. Uma nova chance começa com um passo simples: pedir ajuda.</p>');

INSERT INTO cms_pagina (id, slug, titulo, atualizado_por, oculta_na_listagem_admin)
VALUES ('00000000-0000-0000-0000-000000000108', 'programa-de-recuperacao', 'Programa de Recuperação (Os 12 Passos)', '00000000-0000-0000-0000-000000000001', true);

INSERT INTO cms_bloco (pagina_id, ordem, tipo, conteudo) VALUES
('00000000-0000-0000-0000-000000000108', 1, 'texto', '<p>Os Doze Passos de Jogadores Anônimos são o caminho de recuperação seguido por milhares de membros da irmandade em todo o mundo. Eles representam uma jornada de honestidade, apoio mútuo e transformação pessoal.</p>'),
('00000000-0000-0000-0000-000000000108', 2, 'texto', '<p>Aqui estão os passos que compõem um Programa de Recuperação:</p>'),
('00000000-0000-0000-0000-000000000108', 3, 'texto', '<ol><li><p><strong>1.</strong> Admitimos que éramos impotentes perante o jogo e que nossas vidas haviam se tornado ingovernáveis.</p></li><li><p><strong>2.</strong> Viemos a acreditar que um Poder superior a nós mesmos poderia devolver-nos à sanidade.</p></li><li><p><strong>3.</strong> Decidimos entregar nossa vontade e nossa vida aos cuidados de Deus, na forma em que O concebíamos.</p></li><li><p><strong>4.</strong> Fizemos minucioso e destemido inventário moral de nós mesmos.</p></li><li><p><strong>5.</strong> Admitimos perante Deus, perante nós mesmos e perante outro ser humano a natureza exata de nossas falhas.</p></li><li><p><strong>6.</strong> Prontificamo-nos inteiramente a deixar que Deus removesse todos esses defeitos de caráter.</p></li><li><p><strong>7.</strong> Humildemente pedimos a Ele que removesse nossos defeitos.</p></li><li><p><strong>8.</strong> Fizemos uma relação de todas as pessoas que tínhamos prejudicado e nos dispusemos a reparar os danos a elas causados.</p></li><li><p><strong>9.</strong> Fizemos reparações diretas dos danos causados a tais pessoas, sempre que possível, salvo quando fazê-lo significasse prejudicá-las ou a outrem.</p></li><li><p><strong>10.</strong> Continuamos fazendo o inventário pessoal e, quando estávamos errados, nós o admitíamos prontamente.</p></li><li><p><strong>11.</strong> Procuramos, através da prece e da meditação, melhorar nosso contato consciente com Deus.</p></li><li><p><strong>12.</strong> Tendo experimentado um despertar espiritual, procuramos transmitir esta mensagem a outros jogadores compulsivos.</p></li></ol>');

INSERT INTO cms_pagina (id, slug, titulo, atualizado_por, oculta_na_listagem_admin)
VALUES ('00000000-0000-0000-0000-000000000109', 'programa-de-unidade', 'Programa da Unidade (12 Passos da Unidade)', '00000000-0000-0000-0000-000000000001', true);

INSERT INTO cms_bloco (pagina_id, ordem, tipo, conteudo) VALUES
('00000000-0000-0000-0000-000000000109', 1, 'texto', '<p>Para manter a unidade, nossa experiência mostrou que:</p>'),
('00000000-0000-0000-0000-000000000109', 2, 'texto', '<ol><li><p><strong>1.</strong> O bem-estar comum deve vir em primeiro lugar.</p></li><li><p><strong>2.</strong> Nossos líderes são servidores de confiança; eles não governam.</p></li><li><p><strong>3.</strong> O único requisito para ser membro é o desejo de parar de jogar.</p></li><li><p><strong>4.</strong> Cada grupo deve ser autônomo, exceto em assuntos que afetem outros grupos ou a Jogadores Anônimos como um todo.</p></li><li><p><strong>5.</strong> Cada grupo tem um único propósito primário: levar a mensagem ao jogador compulsivo que ainda sofre.</p></li><li><p><strong>6.</strong> Um grupo não deve endossar, financiar ou emprestar o nome da Jogadores Anônimos a nenhuma entidade externa.</p></li><li><p><strong>7.</strong> Todo grupo deve ser autossustentável, recusando contribuições externas.</p></li><li><p><strong>8.</strong> A Jogadores Anônimos deve permanecer sempre não-profissional.</p></li><li><p><strong>9.</strong> A Jogadores Anônimos nunca deve ter uma estrutura organizacional formal, mas pode criar juntas ou comitês de serviço diretamente responsáveis aos que servem.</p></li><li><p><strong>10.</strong> A Jogadores Anônimos não tem opinião sobre questões externas; o nome da Jogadores Anônimos nunca deve ser envolvido em controvérsias públicas.</p></li><li><p><strong>11.</strong> Nossa política de relações públicas se baseia na atração, não na promoção; mantemos anonimato pessoal perante a mídia.</p></li><li><p><strong>12.</strong> O anonimato é a base espiritual de todas as nossas tradições.</p></li></ol>');

INSERT INTO cms_pagina (id, slug, titulo, atualizado_por)
VALUES ('00000000-0000-0000-0000-000000000110', 'contribuir', 'Contribua com a Jogadores Anônimos do Brasil', '00000000-0000-0000-0000-000000000001');

INSERT INTO cms_bloco (pagina_id, ordem, tipo, conteudo) VALUES
('00000000-0000-0000-0000-000000000110', 1, 'texto', '<p>A Jogadores Anônimos se mantém apenas com as contribuições voluntárias dos próprios membros e apoiadores. Não aceitamos doações de fontes externas, essa é a nossa Sétima Tradição: "todo grupo deve ser autossustentável, recusando contribuições de fora da irmandade".</p>'),
('00000000-0000-0000-0000-000000000110', 2, 'texto', '<p>Sua contribuição ajuda a manter salas de reunião, materiais de recuperação e a estrutura que permite que mais pessoas encontrem ajuda.</p>'),
('00000000-0000-0000-0000-000000000110', 3, 'texto', '<h2>Formas de contribuição</h2>'),
('00000000-0000-0000-0000-000000000110', 4, 'texto', '<ul><li><strong>Pix:</strong> chave [a definir]</li><li><strong>Transferência bancária:</strong> dados bancários [a definir]</li><li><strong>Contribuição em reunião presencial:</strong> converse com o tesoureiro do seu grupo local</li></ul>');

INSERT INTO vinte_questao (ordem, texto, atualizado_em, atualizado_por) VALUES
(0, 'Você já perdeu tempo de trabalho ou de escola por causa do jogo?', now(), '00000000-0000-0000-0000-000000000001'),
(1, 'O jogo já tornou sua vida familiar infeliz?', now(), '00000000-0000-0000-0000-000000000001'),
(2, 'O jogo já afetou a sua reputação?', now(), '00000000-0000-0000-0000-000000000001'),
(3, 'Você já sentiu remorso depois de jogar?', now(), '00000000-0000-0000-0000-000000000001'),
(4, 'Você já jogou para conseguir dinheiro com o qual pagar dívidas ou resolver dificuldades financeiras?', now(), '00000000-0000-0000-0000-000000000001'),
(5, 'O jogo já causou uma diminuição da sua ambição ou eficiência?', now(), '00000000-0000-0000-0000-000000000001'),
(6, 'Depois de perder, você sentiu que precisava voltar o quanto antes para recuperar o prejuízo?', now(), '00000000-0000-0000-0000-000000000001'),
(7, 'Depois de ganhar, você teve um forte impulso de voltar a jogar para ganhar mais?', now(), '00000000-0000-0000-0000-000000000001'),
(8, 'Você costumava jogar até gastar o último centavo que tinha?', now(), '00000000-0000-0000-0000-000000000001'),
(9, 'Você já pegou dinheiro emprestado para financiar o jogo?', now(), '00000000-0000-0000-0000-000000000001'),
(10, 'Você já vendeu alguma coisa para financiar o jogo?', now(), '00000000-0000-0000-0000-000000000001'),
(11, 'Você relutava em usar o "dinheiro do jogo" para despesas normais?', now(), '00000000-0000-0000-0000-000000000001'),
(12, 'O jogo já fez você negligenciar o bem-estar seu ou da sua família?', now(), '00000000-0000-0000-0000-000000000001'),
(13, 'Você já jogou por mais tempo do que havia planejado?', now(), '00000000-0000-0000-0000-000000000001'),
(14, 'Você já jogou para escapar de preocupações ou problemas?', now(), '00000000-0000-0000-0000-000000000001'),
(15, 'Você já cometeu, ou considerou cometer, algum ato ilegal para financiar o jogo?', now(), '00000000-0000-0000-0000-000000000001'),
(16, 'O jogo já causou dificuldade para você dormir?', now(), '00000000-0000-0000-0000-000000000001'),
(17, 'Discussões, decepções ou frustrações despertam em você o impulso de jogar?', now(), '00000000-0000-0000-0000-000000000001'),
(18, 'Você já teve vontade de comemorar alguma boa notícia jogando por algumas horas?', now(), '00000000-0000-0000-0000-000000000001'),
(19, 'Você já considerou se autodestruir ou cometer suicídio como resultado do jogo?', now(), '00000000-0000-0000-0000-000000000001');

INSERT INTO faq (pergunta, resposta, ordem) VALUES
('O que é o jogo compulsivo?', 'É um transtorno progressivo caracterizado pela perda de controle sobre o impulso de jogar, mesmo diante de consequências financeiras, familiares e emocionais graves. Não é uma questão de força de vontade ou moral: é reconhecido como uma condição de saúde que pode ser tratada.', 1),
('Como sei se tenho um problema com o jogo?', 'Alguns sinais comuns incluem jogar mais dinheiro ou tempo do que se pretendia, tentar recuperar perdas jogando ainda mais, mentir sobre hábitos de jogo, pedir dinheiro emprestado para jogar e sentir ansiedade ou irritação ao tentar parar. Se você se identifica com isso, vale a pena buscar apoio.', 2),
('Quais são os primeiros passos para buscar recuperação?', 'O primeiro passo costuma ser admitir, para si mesmo, que o jogo se tornou um problema. A partir daí, procurar uma reunião de Jogadores Anônimos, conversar com alguém que já passou por isso e conhecer o programa dos Doze Passos são caminhos concretos para começar a recuperação.', 3),
('Como posso ajudar alguém que pode ter esse problema?', 'Aborde a pessoa com empatia e sem julgamento, evitando cobranças ou ultimatos. Compartilhe informações sobre a Jogadores Anônimos e, se possível, ofereça-se para acompanhá-la a uma reunião. Lembre-se de que a decisão de buscar ajuda precisa partir da própria pessoa.', 4),
('Existe algum custo para participar das reuniões?', 'Não. As reuniões são gratuitas e abertas a qualquer pessoa que tenha o desejo de parar de jogar. A irmandade se mantém apenas com contribuições voluntárias dos próprios membros, sem cobrar taxas ou mensalidades.', 5),
('É preciso ter alguma crença religiosa para participar?', 'Não. Jogadores Anônimos não é afiliada a nenhuma religião, seita, partido político ou instituição. O programa fala em um "Poder Superior" como cada pessoa o compreende, e essa interpretação é livre e pessoal.', 6),
('O anonimato é garantido?', 'Sim. O anonimato é um princípio fundamental da irmandade. O que é dito nas reuniões permanece nas reuniões, e ninguém é obrigado a se identificar além do primeiro nome ou apelido.', 7),
('Preciso me inscrever ou avisar antes de ir a uma reunião?', 'Não é necessário. Você pode simplesmente comparecer a uma reunião aberta quando se sentir pronto. Não há inscrição, cadastro ou aprovação prévia para participar.', 8),
('O que acontece em uma reunião de Jogadores Anônimos?', 'Nas reuniões, os membros compartilham suas experiências, dificuldades e conquistas relacionadas à compulsão pelo jogo, seguindo o formato dos Doze Passos e do Programa de Unidade. O ambiente é acolhedor, respeitoso e livre de julgamentos.', 9),
('A recuperação tem um tempo definido para acontecer?', 'Não existe um prazo fixo. A recuperação é um processo contínuo e individual, vivido um dia de cada vez, com o apoio da irmandade e a prática constante dos princípios do programa.', 10);

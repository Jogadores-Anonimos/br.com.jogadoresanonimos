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

CREATE TABLE jwt_denylist (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    jti character varying(36) NOT NULL,
    expira_em timestamp with time zone NOT NULL,
    criado_em timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT jwt_denylist_pkey PRIMARY KEY (id),
    CONSTRAINT jwt_denylist_jti_key UNIQUE (jti)
);

CREATE INDEX idx_jwt_denylist_expira_em ON jwt_denylist USING btree (expira_em);

CREATE TABLE admin_auditoria_log (
    id uuid NOT NULL,
    admin_id uuid NOT NULL,
    entidade character varying(50) NOT NULL,
    entidade_id uuid NOT NULL,
    acao character varying(20) NOT NULL,
    status_anterior character varying(20),
    status_novo character varying(20) NOT NULL,
    criado_em timestamp NOT NULL,
    CONSTRAINT admin_auditoria_log_acao_check CHECK (((acao)::text = ANY ((ARRAY['APROVAR'::character varying, 'REJEITAR'::character varying, 'REVERTER'::character varying, 'REMOVER'::character varying, 'EDITAR'::character varying])::text[]))),
    CONSTRAINT admin_auditoria_log_status_anterior_check CHECK (((status_anterior IS NULL) OR ((status_anterior)::text = ANY ((ARRAY['pending'::character varying, 'approved'::character varying, 'rejected'::character varying])::text[])))),
    CONSTRAINT admin_auditoria_log_status_novo_check CHECK (((status_novo)::text = ANY ((ARRAY['pending'::character varying, 'approved'::character varying, 'rejected'::character varying])::text[]))),
    CONSTRAINT admin_auditoria_log_pkey PRIMARY KEY (id)
);

CREATE INDEX idx_admin_auditoria_log_entidade_entidade_id ON admin_auditoria_log USING btree (entidade, entidade_id);
CREATE INDEX idx_admin_auditoria_log_admin_id ON admin_auditoria_log USING btree (admin_id);
CREATE INDEX idx_admin_auditoria_log_criado_em ON admin_auditoria_log USING btree (criado_em);

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
    texto character varying(10000) NOT NULL,
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
    imagem_id uuid,
    alt_text character varying(300) NOT NULL,
    legenda character varying(200),
    link_destino character varying(500),
    ativo boolean DEFAULT true NOT NULL,
    criado_em timestamp with time zone DEFAULT now() NOT NULL,
    atualizado_em timestamp with time zone DEFAULT now() NOT NULL,
    video_url character varying(500),
    CONSTRAINT banner_mural_imagem_xor_video_check CHECK ((((imagem_id IS NOT NULL) AND (video_url IS NULL)) OR ((imagem_id IS NULL) AND (video_url IS NOT NULL)))),
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
    CONSTRAINT chk_reuniao_dia_semana CHECK (((dia_semana IS NULL) OR ((dia_semana)::text = ANY ((ARRAY['Segunda'::character varying, 'Terça'::character varying, 'Quarta'::character varying, 'Quinta'::character varying, 'Sexta'::character varying, 'Sábado'::character varying, 'Domingo'::character varying])::text[])))),
    CONSTRAINT chk_reuniao_periodo CHECK (((periodo IS NULL) OR ((periodo)::text = ANY ((ARRAY['Manhã'::character varying, 'Tarde'::character varying, 'Noite'::character varying])::text[])))),
    CONSTRAINT reuniao_pkey PRIMARY KEY (id)
);

CREATE INDEX idx_reuniao_categoria ON reuniao USING btree (categoria);

CREATE TABLE hotline_regiao (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    criado_em timestamp with time zone DEFAULT now() NOT NULL,
    atualizado_em timestamp with time zone DEFAULT now() NOT NULL,
    uf character varying(2) NOT NULL,
    cidade character varying(150) NOT NULL,
    CONSTRAINT hotline_regiao_uf_check CHECK (((uf)::text = ANY ((ARRAY['AC'::character varying, 'AL'::character varying, 'AP'::character varying, 'AM'::character varying, 'BA'::character varying, 'CE'::character varying, 'DF'::character varying, 'ES'::character varying, 'GO'::character varying, 'MA'::character varying, 'MT'::character varying, 'MS'::character varying, 'MG'::character varying, 'PA'::character varying, 'PB'::character varying, 'PR'::character varying, 'PE'::character varying, 'PI'::character varying, 'RJ'::character varying, 'RN'::character varying, 'RS'::character varying, 'RO'::character varying, 'RR'::character varying, 'SC'::character varying, 'SP'::character varying, 'SE'::character varying, 'TO'::character varying])::text[]))),
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
    CONSTRAINT estado_intergrupo_uf_check CHECK (((uf)::text = ANY ((ARRAY['AC'::character varying, 'AL'::character varying, 'AP'::character varying, 'AM'::character varying, 'BA'::character varying, 'CE'::character varying, 'DF'::character varying, 'ES'::character varying, 'GO'::character varying, 'MA'::character varying, 'MT'::character varying, 'MS'::character varying, 'MG'::character varying, 'PA'::character varying, 'PB'::character varying, 'PR'::character varying, 'PE'::character varying, 'PI'::character varying, 'RJ'::character varying, 'RN'::character varying, 'RS'::character varying, 'RO'::character varying, 'RR'::character varying, 'SC'::character varying, 'SP'::character varying, 'SE'::character varying, 'TO'::character varying])::text[]))),
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
    texto character varying(2500) NOT NULL,
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

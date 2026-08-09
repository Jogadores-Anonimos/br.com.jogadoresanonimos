CREATE TABLE email_confirmacao_token (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    usuario_id uuid NOT NULL,
    token_hash character varying(255) NOT NULL,
    expira_em timestamp with time zone NOT NULL,
    usado_em timestamp with time zone,
    criado_em timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT email_confirmacao_token_pkey PRIMARY KEY (id),
    CONSTRAINT email_confirmacao_token_token_hash_key UNIQUE (token_hash),
    CONSTRAINT email_confirmacao_token_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE INDEX idx_email_confirmacao_token_usuario ON email_confirmacao_token USING btree (usuario_id);

ALTER TABLE usuario ADD COLUMN email_confirmado_em timestamp with time zone;

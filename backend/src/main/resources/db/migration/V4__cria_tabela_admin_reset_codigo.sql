CREATE TABLE admin_reset_codigo (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    usuario_id uuid NOT NULL,
    codigo_hash character varying(255) NOT NULL,
    expira_em timestamp with time zone NOT NULL,
    usado_em timestamp with time zone,
    tentativas_invalidas integer DEFAULT 0 NOT NULL,
    criado_em timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT admin_reset_codigo_pkey PRIMARY KEY (id),
    CONSTRAINT admin_reset_codigo_codigo_hash_key UNIQUE (codigo_hash),
    CONSTRAINT admin_reset_codigo_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE INDEX idx_admin_reset_codigo_usuario ON admin_reset_codigo USING btree (usuario_id);

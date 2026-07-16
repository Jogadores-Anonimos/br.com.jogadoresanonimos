CREATE TABLE jwt_denylist (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    jti character varying(36) NOT NULL,
    expira_em timestamp with time zone NOT NULL,
    criado_em timestamp with time zone DEFAULT now() NOT NULL,
    CONSTRAINT jwt_denylist_pkey PRIMARY KEY (id),
    CONSTRAINT jwt_denylist_jti_key UNIQUE (jti)
);

CREATE INDEX idx_jwt_denylist_expira_em ON jwt_denylist (expira_em);

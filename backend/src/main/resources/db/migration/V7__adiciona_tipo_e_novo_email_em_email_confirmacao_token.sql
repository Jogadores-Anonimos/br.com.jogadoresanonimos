ALTER TABLE email_confirmacao_token ADD COLUMN tipo character varying(20) NOT NULL DEFAULT 'CADASTRO';
ALTER TABLE email_confirmacao_token ADD COLUMN novo_email character varying(255);

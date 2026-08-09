ALTER TABLE admin_auditoria_log DROP CONSTRAINT admin_auditoria_log_status_anterior_check;
ALTER TABLE admin_auditoria_log DROP CONSTRAINT admin_auditoria_log_status_novo_check;
ALTER TABLE admin_auditoria_log ALTER COLUMN status_anterior TYPE character varying(30);
ALTER TABLE admin_auditoria_log ALTER COLUMN status_novo TYPE character varying(30);

ALTER TABLE admin_auditoria_log
    ADD CONSTRAINT admin_auditoria_log_acao_check
    CHECK (acao IN ('APROVAR', 'REJEITAR', 'REVERTER', 'REMOVER', 'EDITAR'));

ALTER TABLE admin_auditoria_log
    ADD CONSTRAINT admin_auditoria_log_status_anterior_check
    CHECK (status_anterior IS NULL OR status_anterior IN ('pending', 'approved', 'rejected'));

ALTER TABLE admin_auditoria_log
    ADD CONSTRAINT admin_auditoria_log_status_novo_check
    CHECK (status_novo IN ('pending', 'approved', 'rejected'));

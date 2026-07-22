DROP INDEX idx_faq_ordem;
ALTER TABLE faq ADD CONSTRAINT faq_ordem_key UNIQUE (ordem) DEFERRABLE INITIALLY DEFERRED;

DROP INDEX idx_regiao_intergrupo_ordem;
ALTER TABLE regiao_intergrupo ADD CONSTRAINT regiao_intergrupo_ordem_key UNIQUE (ordem) DEFERRABLE INITIALLY DEFERRED;

DROP INDEX idx_escritorio_servico_ordem;
ALTER TABLE escritorio_servico ADD CONSTRAINT escritorio_servico_ordem_key UNIQUE (ordem) DEFERRABLE INITIALLY DEFERRED;

DROP INDEX idx_estado_intergrupo_regiao_id;
ALTER TABLE estado_intergrupo ADD CONSTRAINT estado_intergrupo_regiao_id_ordem_key UNIQUE (regiao_id, ordem) DEFERRABLE INITIALLY DEFERRED;

DROP INDEX idx_grupo_intergrupo_estado_id;
ALTER TABLE grupo_intergrupo ADD CONSTRAINT grupo_intergrupo_estado_id_ordem_key UNIQUE (estado_id, ordem) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE vinte_questao DROP CONSTRAINT vinte_questao_numero_key;
ALTER TABLE vinte_questao ADD CONSTRAINT vinte_questao_ordem_key UNIQUE (ordem) DEFERRABLE INITIALLY DEFERRED;

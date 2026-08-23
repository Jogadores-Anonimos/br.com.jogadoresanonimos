ALTER TABLE reuniao
    ADD COLUMN grupo_intergrupo_id UUID;

ALTER TABLE reuniao
    ADD CONSTRAINT reuniao_grupo_intergrupo_id_fkey
        FOREIGN KEY (grupo_intergrupo_id)
        REFERENCES grupo_intergrupo(id)
        ON DELETE RESTRICT;

CREATE UNIQUE INDEX reuniao_grupo_intergrupo_id_unique
    ON reuniao (grupo_intergrupo_id)
    WHERE grupo_intergrupo_id IS NOT NULL;

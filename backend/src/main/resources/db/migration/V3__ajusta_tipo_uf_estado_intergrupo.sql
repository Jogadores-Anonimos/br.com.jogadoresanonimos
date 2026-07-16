ALTER TABLE estado_intergrupo
    ALTER COLUMN uf TYPE character varying(2) USING uf::character varying(2);

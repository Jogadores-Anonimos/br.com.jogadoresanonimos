ALTER TABLE hotline_regiao
    ADD CONSTRAINT hotline_regiao_uf_check
    CHECK (uf IN ('AC','AL','AP','AM','BA','CE','DF','ES','GO','MA','MT','MS','MG','PA','PB','PR','PE','PI','RJ','RN','RS','RO','RR','SC','SP','SE','TO'));

ALTER TABLE estado_intergrupo
    ADD CONSTRAINT estado_intergrupo_uf_check
    CHECK (uf IN ('AC','AL','AP','AM','BA','CE','DF','ES','GO','MA','MT','MS','MG','PA','PB','PR','PE','PI','RJ','RN','RS','RO','RR','SC','SP','SE','TO'));

CREATE TABLE reunioes (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descricao VARCHAR(255),
    endereco VARCHAR(255) NOT NULL,
    id_cidade BIGINT NOT NULL,
    data_hora TIMESTAMP NOT NULL,

    FOREIGN KEY (id_cidade) REFERENCES cidade(id)
);
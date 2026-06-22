CREATE TABLE cidade (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    estado VARCHAR(2) NOT NULL,
    UNIQUE (nome, estado)
);
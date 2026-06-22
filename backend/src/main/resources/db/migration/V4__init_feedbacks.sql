CREATE TABLE feedbacks (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    idade INT NOT NULL,
    descricao TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);
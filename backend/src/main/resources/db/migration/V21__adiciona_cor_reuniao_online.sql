ALTER TABLE reuniao_online
    ADD COLUMN cor VARCHAR(20) NOT NULL DEFAULT 'ROXO';

UPDATE reuniao_online
SET cor = 'LARANJA'
WHERE nome = 'Grupo Salvador';

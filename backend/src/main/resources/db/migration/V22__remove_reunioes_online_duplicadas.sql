DELETE FROM reuniao_online_horario
WHERE reuniao_online_id IN (
    SELECT id FROM reuniao_online
    WHERE nome IN (
        'Um dia de Cada Vez',
        'Vida Nova SP',
        'SalvandO Vidas CG',
        'São Paulo',
        'Reconectando Vidas',
        'Novo Começo',
        'Recomeço SPH',
        'Rio de Janeiro',
        'Só por Hoje',
        'Só Por Hoje SP',
        'Por mais 24 Horas',
        'Renascer',
        'GRUPO SALVADOR'
    )
);

DELETE FROM reuniao_online
WHERE nome IN (
    'Um dia de Cada Vez',
    'Vida Nova SP',
    'SalvandO Vidas CG',
    'São Paulo',
    'Reconectando Vidas',
    'Novo Começo',
    'Recomeço SPH',
    'Rio de Janeiro',
    'Só por Hoje',
    'Só Por Hoje SP',
    'Por mais 24 Horas',
    'Renascer',
    'GRUPO SALVADOR'
);

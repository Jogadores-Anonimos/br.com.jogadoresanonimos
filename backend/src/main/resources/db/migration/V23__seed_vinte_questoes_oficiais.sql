-- Seed das 20 perguntas oficiais de autoavaliação (Jogadores Anônimos),
-- que nunca haviam sido inseridas desde a criação do módulo em V8/V21.
-- `atualizado_por` reaproveita o usuário-sistema criado em V9
-- (sistema.seed.cms), mesmo padrão usado para ancorar FK de autoria em
-- dados de seed sem admin real garantido neste ponto da migration.
INSERT INTO vinte_questao (ordem, texto, atualizado_em, atualizado_por) VALUES
(0, 'Você já perdeu tempo de trabalho ou de escola por causa do jogo?', now(), '00000000-0000-0000-0000-000000000001'),
(1, 'O jogo já tornou sua vida familiar infeliz?', now(), '00000000-0000-0000-0000-000000000001'),
(2, 'O jogo já afetou a sua reputação?', now(), '00000000-0000-0000-0000-000000000001'),
(3, 'Você já sentiu remorso depois de jogar?', now(), '00000000-0000-0000-0000-000000000001'),
(4, 'Você já jogou para conseguir dinheiro com o qual pagar dívidas ou resolver dificuldades financeiras?', now(), '00000000-0000-0000-0000-000000000001'),
(5, 'O jogo já causou uma diminuição da sua ambição ou eficiência?', now(), '00000000-0000-0000-0000-000000000001'),
(6, 'Depois de perder, você sentiu que precisava voltar o quanto antes para recuperar o prejuízo?', now(), '00000000-0000-0000-0000-000000000001'),
(7, 'Depois de ganhar, você teve um forte impulso de voltar a jogar para ganhar mais?', now(), '00000000-0000-0000-0000-000000000001'),
(8, 'Você costumava jogar até gastar o último centavo que tinha?', now(), '00000000-0000-0000-0000-000000000001'),
(9, 'Você já pegou dinheiro emprestado para financiar o jogo?', now(), '00000000-0000-0000-0000-000000000001'),
(10, 'Você já vendeu alguma coisa para financiar o jogo?', now(), '00000000-0000-0000-0000-000000000001'),
(11, 'Você relutava em usar o "dinheiro do jogo" para despesas normais?', now(), '00000000-0000-0000-0000-000000000001'),
(12, 'O jogo já fez você negligenciar o bem-estar seu ou da sua família?', now(), '00000000-0000-0000-0000-000000000001'),
(13, 'Você já jogou por mais tempo do que havia planejado?', now(), '00000000-0000-0000-0000-000000000001'),
(14, 'Você já jogou para escapar de preocupações ou problemas?', now(), '00000000-0000-0000-0000-000000000001'),
(15, 'Você já cometeu, ou considerou cometer, algum ato ilegal para financiar o jogo?', now(), '00000000-0000-0000-0000-000000000001'),
(16, 'O jogo já causou dificuldade para você dormir?', now(), '00000000-0000-0000-0000-000000000001'),
(17, 'Discussões, decepções ou frustrações despertam em você o impulso de jogar?', now(), '00000000-0000-0000-0000-000000000001'),
(18, 'Você já teve vontade de comemorar alguma boa notícia jogando por algumas horas?', now(), '00000000-0000-0000-0000-000000000001'),
(19, 'Você já considerou se autodestruir ou cometer suicídio como resultado do jogo?', now(), '00000000-0000-0000-0000-000000000001');

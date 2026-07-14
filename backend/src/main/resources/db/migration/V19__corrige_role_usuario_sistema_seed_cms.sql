-- O usuário-sistema criado em V9 (sistema.seed.cms) só existe pra
-- ancorar FK de autoria do seed de CMS — nunca loga (hash inválido).
-- Ele recebeu role ADMIN por engano, o que fazia AdminBootstrap
-- (CommandLineRunner) achar que já existia um admin e pular a criação
-- do admin real vindo de app.admin-bootstrap.* (env vars). Corrige pra
-- PARTICIPANTE, que não afeta nada (esse usuário nunca autentica).
UPDATE usuario
SET role = 'PARTICIPANTE'
WHERE apelido = 'sistema.seed.cms';

INSERT INTO public.usuario (id, apelido, email, senha_hash, role, criado_em, senha_alterada_em, deve_trocar_senha, mfa_secret_cifrado, mfa_ativo) VALUES ('00000000-0000-0000-0000-000000000001', 'sistema.seed.cms', 'sistema.seed.cms@jogadoresanonimos.invalid', 'SEED_SEM_LOGIN_NAO_USAR', 'PARTICIPANTE', '2026-07-17 13:59:38.892435+00', '2026-07-17 13:59:38.892435+00', false, NULL, false);
INSERT INTO public.usuario (id, apelido, email, senha_hash, role, criado_em, senha_alterada_em, deve_trocar_senha, mfa_secret_cifrado, mfa_ativo) VALUES ('00000000-0000-0000-0000-000000000002', 'sistema.seed.relatos', 'sistema.seed.relatos@jogadoresanonimos.invalid', 'SEED_SEM_LOGIN_NAO_USAR', 'PARTICIPANTE', '2026-07-17 21:49:55.3483+00', '2026-07-17 21:49:55.3483+00', false, NULL, false);
INSERT INTO public.cms_pagina (id, slug, titulo, atualizado_em, atualizado_por, oculta_na_listagem_admin) VALUES ('00000000-0000-0000-0000-000000000101', 'sobre', 'Sobre os Jogadores Anônimos', '2026-07-17 13:59:38.892435+00', '00000000-0000-0000-0000-000000000001', false);
INSERT INTO public.cms_pagina (id, slug, titulo, atualizado_em, atualizado_por, oculta_na_listagem_admin) VALUES ('00000000-0000-0000-0000-000000000102', 'historia', 'História', '2026-07-17 13:59:38.892435+00', '00000000-0000-0000-0000-000000000001', false);
INSERT INTO public.cms_pagina (id, slug, titulo, atualizado_em, atualizado_por, oculta_na_listagem_admin) VALUES ('00000000-0000-0000-0000-000000000103', 'anonimato', 'Anonimato', '2026-07-17 13:59:38.892435+00', '00000000-0000-0000-0000-000000000001', false);
INSERT INTO public.cms_pagina (id, slug, titulo, atualizado_em, atualizado_por, oculta_na_listagem_admin) VALUES ('00000000-0000-0000-0000-000000000104', 'privacidade', 'Política de Privacidade', '2026-07-17 13:59:38.892435+00', '00000000-0000-0000-0000-000000000001', false);
INSERT INTO public.cms_pagina (id, slug, titulo, atualizado_em, atualizado_por, oculta_na_listagem_admin) VALUES ('00000000-0000-0000-0000-000000000105', 'termos', 'Termos de Uso', '2026-07-17 13:59:38.892435+00', '00000000-0000-0000-0000-000000000001', false);
INSERT INTO public.cms_pagina (id, slug, titulo, atualizado_em, atualizado_por, oculta_na_listagem_admin) VALUES ('00000000-0000-0000-0000-000000000106', 'jogo-compulsivo', 'Jogo Compulsivo', '2026-07-17 13:59:38.892435+00', '00000000-0000-0000-0000-000000000001', false);
INSERT INTO public.cms_pagina (id, slug, titulo, atualizado_em, atualizado_por, oculta_na_listagem_admin) VALUES ('00000000-0000-0000-0000-000000000108', 'programa-de-recuperacao', 'Programa de Recuperação (Os 12 Passos)', '2026-07-17 13:59:38.892435+00', '00000000-0000-0000-0000-000000000001', true);
INSERT INTO public.cms_pagina (id, slug, titulo, atualizado_em, atualizado_por, oculta_na_listagem_admin) VALUES ('00000000-0000-0000-0000-000000000109', 'programa-de-unidade', 'Programa da Unidade (12 Passos da Unidade)', '2026-07-17 13:59:38.892435+00', '00000000-0000-0000-0000-000000000001', true);
INSERT INTO public.cms_pagina (id, slug, titulo, atualizado_em, atualizado_por, oculta_na_listagem_admin) VALUES ('00000000-0000-0000-0000-000000000110', 'contribuir', 'Contribua com a Jogadores Anônimos do Brasil', '2026-07-17 13:59:38.892435+00', '00000000-0000-0000-0000-000000000001', false);
INSERT INTO public.cms_pagina (id, slug, titulo, atualizado_em, atualizado_por, oculta_na_listagem_admin) VALUES ('00000000-0000-0000-0000-000000000107', 'uma-nova-chance', 'Uma Outra Chance', '2026-07-17 13:59:38.892435+00', '00000000-0000-0000-0000-000000000001', false);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('a36dd259-c010-436b-b837-f91da5fcda8e', '00000000-0000-0000-0000-000000000106', 1, 'texto', '<h2>O Jogo Compulsivo</h2><p>Existem muitas e variadas interpretações a respeito do jogo compulsivo. Um jogador compulsivo é descrito como uma pessoa, onde o jogo causou contínuos e crescentes problemas nos aspectos da sua vida.</p><p>Trata-se de uma doença, progressiva por natureza, que não pode ser curada, mas pode ser detida.</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('d3dd2186-2c03-4323-9953-2372f5b260ec', '00000000-0000-0000-0000-000000000105', 1, 'texto', '<p>Ao acessar e utilizar este site, você concorda com estes Termos de Uso. Se você não concorda com algum ponto, pedimos que não utilize o site.</p><p>Este site tem finalidade informativa e de apoio a pessoas que acham que podem ter um problema com o jogo, além de familiares e interessados na irmandade Jogadores Anônimos do Brasil.</p><h2>Cadastro e conta</h2><p>Para algumas funcionalidades, é necessário criar uma conta usando um apelido (nunca seu nome real) e uma senha. Você é responsável por manter sua senha em sigilo e por qualquer atividade realizada com o seu login.</p><p>O e-mail informado no cadastro é usado exclusivamente para recuperação de conta e para envio de itens da loja, quando aplicável. Ele nunca é vinculado publicamente à sua participação na irmandade.</p><h2>Conduta esperada</h2><p>Relatos e qualquer outro conteúdo enviado por você passam por moderação antes de serem publicados. Não é permitido enviar conteúdo que exponha a identidade de terceiros ou que, de qualquer forma, viole o princípio do anonimato que sustenta a irmandade Jogadores Anônimos.</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('0f0cec78-02bd-4cbf-8579-c3000ccef233', '00000000-0000-0000-0000-000000000103', 1, 'texto', '<h2>O Anonimato</h2><p>O anonimato é importante para manter a unidade em nossa Irmandade. Pela prática do anonimato em nível de imprensa, rádio, filmes e televisão, temos eliminado a possibilidade da fama e reconhecimento dados a membros individuais; por isso, não nos defrontamos com qualquer grande disputa interna por poder e prestígio, o que poderia se tornar altamente prejudicial à nossa unidade.</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('f0d750d9-41d8-4d09-81a9-c7574de609f9', '00000000-0000-0000-0000-000000000103', 2, 'texto', '<p>O anonimato também tem um grande valor para atrair novos membros, que inicialmente poderiam sentir que há um estigma ligado ao problema. Portanto, oferecemos aos ingressantes o que escolherem em termos de anonimato.</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('43d97f8e-b78c-49f5-8782-fafd3caa2638', '00000000-0000-0000-0000-000000000103', 3, 'texto', '<p>Mais importante ainda, vemos que o anonimato tem um significado espiritual extraordinário. Ele representa um poderoso lembrete de que sempre precisamos colocar os princípios acima das personalidades.</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('b024b98a-7e6f-41f0-b732-dc4bf8d5e4f7', '00000000-0000-0000-0000-000000000103', 4, 'texto', '<p>Nossa sobrevivência como indivíduos exige que renunciemos à glorificação pessoal. Assim sendo, a Irmandade de Jogadores Anônimos advoga a humildade, e foi através de maior humildade que os membros encontraram a paz e a serenidade para todos os anos que virão.</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('a063e2ab-cc93-4cf1-ab66-96403cb8ee6c', '00000000-0000-0000-0000-000000000108', 2, 'texto', '<p>Aqui estão os Passos que formam o Programa de Recuperação:</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('c12e3d40-92a0-4131-a40d-050d74d7b35d', '00000000-0000-0000-0000-000000000109', 1, 'texto', '<p>O Programa de Unidade de JA é composto por 12 (doze) Passos e serve para proteger uma Irmandade de desmembramento. Os membros atuam em união sob os princípios da Unidade, com intuito de preservar e perpetuar uma oportunidade para si e para outros jogadores compulsivos, se recuperar e crescer dentro da Irmandade.</p><p>Aqui estão os Passos que formam o Programa de Unidade:</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('b483c3e4-7f54-4289-9461-b95b7f5dac5e', '00000000-0000-0000-0000-000000000109', 2, 'texto', '<ol><li><p><strong>PASSO 1</strong> Nosso bem-estar comum deve ser o primeiro lugar; uma recuperação individual depende da unidade do grupo.</p></li><li><p><strong>PASSO 2</strong> Nossos líderes são apenas servidores de confiança; eles não governam.</p></li><li><p><strong>PASSO 3</strong> O único requisito para tornar-se membro de Jogadores Anônimos é o desejo de parar de jogar.</p></li><li><p><strong>PASSO 4</strong> Todo grupo deve ser autônomo, exceto nos assuntos que afetam outros grupos ou Jogadores Anônimos como um todo.</p></li><li><p><strong>PASSO 5</strong> Jogadores Anônimos tem apenas um objetivo primordial: levar sua mensagem ao jogador compulsivo que ainda sofre.</p></li><li><p><strong>PASSO 6</strong> Jogadores anônimos não devem permitir, financiar ou emprestar seu nome a órgãos administrados ou empreendimentos externos, evitar assim problemas de dinheiro, propriedade e prestígio afastados de nosso propósito primordial.</p></li><li><p><strong>PASSO 7</strong> Todo o Grupo de Jogadores Anônimos deve ser totalmente auto-sustentável, recusando contribuições externas.</p></li><li><p><strong>PASSO 8</strong> Jogadores Anônimos devem permanecer para sempre não profissional, mas nossos centros de serviço podem empregar funcionários.</p></li><li><p><strong>PASSO 9</strong> Jogadores Anônimos, como tal, nunca deveriam ser organizados; mas podemos criar juntas ou comitês de serviço sendo diretamente responsáveis por aqueles que servem.</p></li><li><p><strong>PASSO 10</strong> Jogadores Anônimos não tem opinião sobre assuntos externos; dessa forma ou nome de Jogadores Anônimos nunca deve ser lançado na controvérsia pública.</p></li><li><p><strong>PASSO 11</strong> Nossa política de relações-públicas está mais baseada na atração do que na promoção; precisamos sempre manter o anonimato pessoal em nível de imprensa, rádio, filmes e televisão.</p></li><li><p><strong>PASSO 12</strong> O anonimato é uma base espiritual do programa de Jogadores Anônimos, sempre nos lembrando de colocar os princípios acima das personalidades.</p></li></ol>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('f44af16e-275f-4b4f-9c91-24813230676c', '00000000-0000-0000-0000-000000000106', 2, 'texto', '<p>Antes de chegar aos Jogadores Anônimos, muitos jogadores compulsivos consideram-se moralmente fracos.</p><p>A visão de Jogadores Anônimos é que jogadores compulsivos são pessoas que podem se recuperar, se derem o melhor de si para seguir um Programa que já provou ser um sucesso para milhares de homens e mulheres com problemas no jogo.</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('2d6eeaab-ae7e-4f44-8c7d-c163540b4bfe', '00000000-0000-0000-0000-000000000106', 3, 'texto', '<p>Lamentavelmente, o indivíduo desconhece que a maior parte do tempo está sendo gasta com o jogo. O jogador é a última pessoa a se dar conta do problema. Familiares e amigos, geralmente são incentivados na busca de ajuda e recuperação.</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('41daf44d-abdb-448f-a798-307a889a55f1', '00000000-0000-0000-0000-000000000106', 4, 'texto', '<p>Se você tem algum conhecido, com problemas de jogo, encaminhe-o para Jogadores Anônimos.</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('647b3254-e258-468c-a242-02be85118a1a', '00000000-0000-0000-0000-000000000107', 1, 'texto', '<p>Companheiro jogador, aceite a minha mão;<br>Sou seu amigo, eu o compreendo.<br>Conheço a sua culpa, sua vergonha, seu remorso;<br>Carreguei o peso de sua cruz.</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('ef7da685-1a1d-4573-88b1-3ad6953a4f8f', '00000000-0000-0000-0000-000000000107', 2, 'texto', '<p>Encontrei um amigo que me ofereceu ajuda;<br>Ele também sofreu com essa doença.<br>Embora ele não tivesse a cura mágica,<br>Mostrou-me como eu poderia resistir.</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('cd8ab467-a449-4c9a-9e59-068bd79b1cd6', '00000000-0000-0000-0000-000000000107', 3, 'texto', '<p>Conversamos, lado a lado;<br>Falamos de coisas que precisávamos esconder.<br>Conversamos sobre as noites em claro e as dívidas,<br>Sobre os lares desfeitos, mentiras e ameaças.</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('bcf26b13-29b3-42b6-a801-198a8cbfc829', '00000000-0000-0000-0000-000000000107', 4, 'texto', '<p>E assim, meu tão sofrido amigo jogador,<br>Por favor, aceite esta mão que lhe estendo.<br>Aceite uma outra chance em algo novo,<br>É outro jogador que está lhe ajudando.</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('d15893cb-8ebb-475d-b3c0-72646d8c2725', '00000000-0000-0000-0000-000000000110', 1, 'texto', '<p>Os grupos de J.A. devem ser autossustentáveis quanto à sua subsistência, não aceitando contribuições de fora. Nossas despesas (aluguel dos escritórios, impressão de literatura, telefones de linha de ajuda, internet, folhetos de divulgação e outras) são pagas com o dinheiro recebido de membros de J.A. espontaneamente. Assim, pedimos para quem desejar oferecer sua contribuição, que faça seu depósito nas contas abaixo.</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('f32fa028-d740-4864-87f6-dd0894a5f566', '00000000-0000-0000-0000-000000000110', 2, 'texto', '<h2>Banco Itaú</h2><p><strong>Agência:</strong> 7190</p><p><strong>Conta Corrente:</strong> 20358-3</p><p><strong>Beneficiário:</strong> JAERJ</p><p><strong>CNPJ (PIX):</strong> 01.584.133/0001-15</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('d4114c1f-f4f6-49a7-a564-16e7f58cc37c', '00000000-0000-0000-0000-000000000110', 3, 'texto', '<h2>Banco Bradesco</h2><p><strong>Agência:</strong> 0114-7</p><p><strong>Conta Corrente:</strong> 0331562-2</p><p><strong>Beneficiário:</strong> ERSJAB</p><p><strong>CNPJ (PIX):</strong> 09.343.472/0001-11</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('a64aada2-3133-42b7-9ee4-78e61f542aa8', '00000000-0000-0000-0000-000000000105', 2, 'texto', '<h2>Propriedade intelectual</h2><p>O conteúdo institucional publicado neste site — incluindo literatura, materiais do programa de recuperação e demais textos próprios da irmandade — pertence à Jogadores Anônimos do Brasil ou é utilizado com autorização. Esse conteúdo pode ser usado apenas para fins pessoais e não comerciais.</p><h2>Limitação de responsabilidade</h2><p>Este site é mantido de boa-fé pela irmandade Jogadores Anônimos do Brasil, sem garantia de disponibilidade ininterrupta ou livre de falhas. A associação não se responsabiliza por decisões tomadas unicamente com base no conteúdo publicado aqui.</p><h2>Privacidade</h2><p>O tratamento de dados pessoais no uso deste site é detalhado na nossa <a href="/privacidade">Política de Privacidade</a>, que faz parte integrante destes Termos de Uso.</p><h2>Alterações destes termos</h2><p>Estes Termos de Uso podem ser alterados a qualquer momento. Ao publicarmos uma alteração, esta página passa a valer com uma nova data de vigência.</p><h2>Legislação aplicável</h2><p>Estes termos são regidos pelas leis da República Federativa do Brasil. Eventuais controvérsias serão submetidas ao foro do domicílio do usuário, quando cabível.</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('d28bdfb5-6ddb-443a-b894-009362a38e45', '00000000-0000-0000-0000-000000000104', 1, 'texto', '<p>A Jogadores Anônimos do Brasil é a controladora dos dados tratados neste site, nos termos da Lei Geral de Proteção de Dados (LGPD, Lei nº 13.709/2018).</p><h2>Nosso compromisso com o anonimato</h2><p>Não coletamos nome real, documento ou qualquer dado que permita identificar quem participa da irmandade ou busca informações por aqui. Dados relacionados à sua participação — como relatos enviados ou presença em reuniões — nunca ficam vinculados a qualquer informação que possa identificar você.</p><h2>Quais dados tratamos e por quê</h2><ul><li><strong>Apelido e senha</strong>, usados para autenticação. A senha é armazenada com hash (Argon2id) e nunca em texto puro.</li><li><strong>E-mail</strong>, coletado como exceção deliberada à nossa regra geral de anonimato, apenas para recuperação de conta e envio de itens físicos da loja. O e-mail nunca é exposto junto de dado de participação ou de relato.</li><li><strong>Conteúdo enviado voluntariamente</strong> por você, como relatos e mensagens, que passa por moderação antes de ser publicado.</li></ul><h2>Base legal</h2><p>Tratamos seus dados com base no seu consentimento (art. 7º, I, da LGPD) e para a execução do serviço que você mesmo solicitou (art. 7º, V, da LGPD).</p><h2>Compartilhamento</h2><p>Não compartilhamos seus dados com terceiros para fins comerciais, não utilizamos ferramentas de analytics ou rastreamento de terceiros e nenhum dado é vendido.</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('3b123c45-05ef-4e89-86de-1fd11fa95a2d', '00000000-0000-0000-0000-000000000104', 2, 'texto', '<h2>Armazenamento e segurança</h2><p>Sua senha é protegida por hash (Argon2id) e nunca é armazenada em texto puro. A sessão de login fica salva no armazenamento local do seu próprio navegador, não em cookie de rastreamento. Todo o tráfego de dados de conta é criptografado (HTTPS).</p><h2>Retenção de dados</h2><p>Dados de conta são mantidos enquanto o cadastro estiver ativo. Logs técnicos de operação são mantidos por até 30 dias. Logs de auditoria de ações administrativas são mantidos por até 24 meses, sem qualquer dado que identifique participantes.</p><h2>Seus direitos</h2><p>Nos termos do art. 18 da LGPD, você pode solicitar: confirmação da existência de tratamento, acesso aos dados, correção de dados incompletos ou desatualizados, anonimização ou eliminação de dados desnecessários, portabilidade, eliminação de dados tratados com base no seu consentimento, informação sobre com quem compartilhamos seus dados e revogação do consentimento a qualquer momento.</p><p>Para exercer qualquer um desses direitos, entre em contato pelo e-mail <strong>jogadoresanonimossaopaulo@gmail.com</strong>.</p><h2>Cookies</h2><p>Não utilizamos cookies de rastreamento ou de terceiros. A sessão de login usa armazenamento local do navegador (localStorage/sessionStorage), controlado inteiramente por você — por exemplo, ao limpar os dados do navegador, a sessão é encerrada.</p><h2>Alterações desta política</h2><p>Esta Política de Privacidade pode ser alterada a qualquer momento. Ao publicarmos uma alteração, esta página passa a valer com uma nova data de vigência.</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('066b5699-2238-4b7c-92c5-c3bad3ea2380', '00000000-0000-0000-0000-000000000101', 1, 'texto', '<h2>Como Funciona</h2>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('e85ff576-2770-461c-b61d-9bbf5c6d4460', '00000000-0000-0000-0000-000000000101', 2, 'texto', '<p>Jogadores Anônimos é uma irmandade cujos membros compartilham suas experiências, energia e esperança, com o objetivo de parar de jogar, manter-se abstinente e ajudar outros jogadores compulsivos a fazerem o mesmo.</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('a6c8e9ad-b513-42c2-892c-db95bc5b4e58', '00000000-0000-0000-0000-000000000101', 3, 'texto', '<p>O único requisito para fazer parte da Irmandade é o desejo de parar de jogar. Não há mensalidades ou taxas para tornar-se membro de Jogadores Anônimos; somos auto-sustentáveis através das nossas contribuições.</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('d1894acb-8ff8-41c2-b5de-8f9b67ede406', '00000000-0000-0000-0000-000000000101', 4, 'texto', '<p>Jogadores Anônimos não está associado a qualquer entidade, denominação, partido político, organização ou instituição; não almeja envolver-se em qualquer controvérsia; nem endossa ou se opõe a qualquer causa.</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('76a3a59c-f1c9-431e-8f64-ed7c8dcd78c2', '00000000-0000-0000-0000-000000000101', 5, 'texto', '<p>Nosso objetivo primordial é parar de jogar e ajudar outros jogadores a fazer o mesmo.</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('3400b3a6-a04a-465c-9cf6-e7b002f3ba53', '00000000-0000-0000-0000-000000000101', 6, 'texto', '<p>Jogadores Anônimos é formado por milhares de homens e mulheres, no mundo todo. Esses homens e mulheres se encontram, semanalmente, em grupos, para compartilhar suas experiências.</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('13ab6041-5523-458c-b294-b2fe93bc2f7f', '00000000-0000-0000-0000-000000000101', 7, 'texto', '<p>Nas grandes áreas metropolitanas, há vários grupos, que realizam reuniões semanais regulares.</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('5387b762-557a-40a0-84a2-757c24938588', '00000000-0000-0000-0000-000000000102', 1, 'texto', '<h2>Nossa História</h2><p>Jogadores anônimos é uma irmandade composta de pessoas que compartilham suas experiências, energia e esperança, com o objetivo de parar de jogar, manter-se abstinente e ajudar outros jogadores compulsivos a fazerem o mesmo.</p><p>Ao longo da História, indescritíveis sofrimentos e humilhações foi a sina de milhares de homens e mulheres, devido à sua adicção ao jogo. Em nossos dias, com a formação da Irmandade de Jogadores Anônimos, abriu-se uma nova e brilhante página na luta contra essa doença insidiosa.</p><p>A Irmandade de Jogadores Anônimos surgiu de um encontro casual entre dois homens no mês de janeiro de 1957. Estes dois homens tinham uma história verdadeiramente frustrante e cheia de problemas e sofrimentos causados pela obsessão pelo jogo. Os dois começaram a se reunir regularmente e à medida que os meses se passavam nenhum deles voltou a jogar.</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('ec69544d-6d0d-45a0-812f-39d857254eb8', '00000000-0000-0000-0000-000000000102', 2, 'texto', '<p>Eles concluíram, através de suas discussões, que para evitar uma recaída era necessário fazer algumas mudanças profundas de caráter. A fim de alcançar essas mudanças utilizaram certos princípios espirituais como orientação, que já haviam sido utilizados por milhares de pessoas que estavam se recuperando de outros hábitos compulsivos. Além disso, a fim de manter sua própria abstinência eles achavam que era crucialmente importante que levassem a mensagem de esperança a outros jogadores compulsivos.</p><p>Como resultado de uma publicidade favorável feita por um colunista de prestígio de um jornal e um comentarista de televisão, o primeiro encontro do Grupo de Jogadores Anônimos ocorreu em uma sexta-feira, no dia 13 de setembro de 1957, em Los Angeles, Califórnia. Desde aquele dia, a Irmandade tem crescido e novos grupos são formados em todo o mundo.</p><p>A primeira expansão de Jogadores Anônimos ocorreu quando uma mulher de San Francisco fez contato com Jogadores Anônimos, em Los Angeles. Devido ao problema de jogo de seu marido, haviam-se separado e ela estava morando com seus pais, em Los Angeles. Depois de ir a uma reunião de Jogadores Anônimos, ela teve a certeza de que seu marido poderia recuperar-se com a ajuda de Jogadores Anônimos. O marido veio de avião de San Francisco a Los Angeles (com dinheiro emprestado) e, depois de participar de uma reunião de Jogadores Anônimos, voltou a San Francisco com sua família, para iniciar o primeiro Grupo do Norte da Califórnia.</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('49fe987e-32fb-4f59-a29c-3e6566706b03', '00000000-0000-0000-0000-000000000101', 8, 'texto', '<p>Se você acredita que tem problemas com o jogo ou conhece alguém que tenha, procure um grupo de JA, em um endereço ou dia mais conveniente e participe de uma reunião!</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('5be8bcfc-6c20-485f-bb8f-ee8b11f952c3', '00000000-0000-0000-0000-000000000102', 3, 'texto', '<p>Pouco tempo depois, foi dado o início à terceira área, quando quatro membros do setor de Los Angeles viajaram a Las Vegas, para começar um grupo. Tendo estado em Jogadores Anônimos e livres do jogo por 18 meses, eles próprios puderam arcar com as despesas da viagem.</p><p>Resultantes dos grupos originais de Los Angeles e San Francisco, agora existem grupos em muitas cidades dos Estados Unidos, Canadá, Austrália, Nova Zelândia, Grã-Bretanha, Uganda, Israel, Argentina e outros países pelo mundo todo.</p><p>No Brasil, a primeira reunião de Jogadores Anônimos ocorreu no Rio de Janeiro, RJ, no dia 23 de março de 1993.</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('0353d4c9-7bfe-47d0-9019-00a9717a2a85', '00000000-0000-0000-0000-000000000108', 1, 'texto', '<p>O Programa de Recuperação é composto de 12 (doze) Passos. Esses passos não surgiram na Irmandade de Jogadores Anônimos. Ele reflete a aplicação de princípios espirituais da forma como foram registrados por pensadores: homens e mulheres de várias épocas. Através da sua prática em nossas vidas diárias, fomos capazes de parar de jogar e despertar mudanças de personalidade em nosso interior.</p>', NULL, NULL, NULL);
INSERT INTO public.cms_bloco (id, pagina_id, ordem, tipo, conteudo, imagem_id, alt, arquivo_id) VALUES ('57dcabcf-1c23-406b-8ae2-324e84f69de5', '00000000-0000-0000-0000-000000000108', 3, 'texto', '<ol><li><strong>PASSO 1</strong> Admitimos que éramos impotentes perante o jogo, e que nossas vidas haviam se tornado ingovernáveis.</li><li><strong>PASSO 2</strong> Passamos a acreditar que um PODER SUPERIOR a nós mesmos poderia trazer-nos de volta a um modo normal de pensar e viver.</li><li><strong>PASSO 3</strong> Tomamos a decisão de entregar nossa vontade e nossas vidas aos cuidados deste Poder de nosso entendimento.</li><li><strong>PASSO 4</strong> Fizemos um minucioso e um destemido inventário moral e financeiro de nós mesmos.</li><li><strong>PASSO 5</strong> Admitimos a nós mesmos e a um outro ser humano a natureza exata de nossas falhas.</li><li><strong>PASSO 6</strong> Ficamos inteiramente dispostos a ter esses defeitos de caráter removidos.</li><li><strong>PASSO 7</strong> Humildemente pedimos ao DEUS (de nosso entendimento) que removesse as nossas imperfeições.</li><li><strong>PASSO 8</strong> Fizemos uma lista de todas as pessoas a quem prejudicamos e nos tornamos dispostos a fazer reparações a todos pelo mal causado.</li><li><strong>PASSO 9</strong> Reparamos os danos causados diretamente a essas pessoas sempre que possível, exceto quando a reparação implicasse em prejudicá-las ou a outras.</li><li><strong>PASSO 10</strong> Continuamos a fazer um inventário pessoal e quando estávamos errados, prontamente o admitimos.</li><li><strong>PASSO 11</strong> Procuramos através da oração e meditação melhorar nosso contato consciente com DEUS como O entendíamos, pedindo somente pelo conhecimento de Sua vontade perante a nós e a capacidade de realizá-la.</li><li><strong>PASSO 12</strong> Tendo feito um esforço para praticar estes princípios em todas as nossas questões, procuramos levar esta mensagem a outros jogadores compulsivos.</li></ol>', NULL, NULL, NULL);
INSERT INTO public.vinte_questao (id, ordem, texto, atualizado_em, atualizado_por) VALUES ('d53945cc-6d77-42b4-a38d-61c7061ddd03', 0, 'Você já perdeu horas de trabalho ou da escola devido ao jogo?', '2026-07-17 21:37:39.378755+00', '00000000-0000-0000-0000-000000000001');
INSERT INTO public.vinte_questao (id, ordem, texto, atualizado_em, atualizado_por) VALUES ('047c94a4-cdd8-4195-ac0e-cec2a5718503', 1, 'Alguma vez o jogo já causou infelicidade na sua vida familiar?', '2026-07-17 21:37:39.378755+00', '00000000-0000-0000-0000-000000000001');
INSERT INTO public.vinte_questao (id, ordem, texto, atualizado_em, atualizado_por) VALUES ('973f22ce-47ea-4d2a-ac64-320fdcad9ade', 2, 'O jogo afetou a sua reputação?', '2026-07-17 21:37:39.378755+00', '00000000-0000-0000-0000-000000000001');
INSERT INTO public.vinte_questao (id, ordem, texto, atualizado_em, atualizado_por) VALUES ('24aa375f-ceac-4b17-bcb8-e9bc8514d7b1', 3, 'Você já sentiu remorso após jogar?', '2026-07-17 21:37:39.378755+00', '00000000-0000-0000-0000-000000000001');
INSERT INTO public.vinte_questao (id, ordem, texto, atualizado_em, atualizado_por) VALUES ('eb245355-d492-48ef-b609-fb0a012badfe', 4, 'Alguma vez você já jogou para obter dinheiro para pagar dívidas ou então resolver dificuldades financeiras?', '2026-07-17 21:37:39.378755+00', '00000000-0000-0000-0000-000000000001');
INSERT INTO public.vinte_questao (id, ordem, texto, atualizado_em, atualizado_por) VALUES ('8440aa45-04d6-422f-b39c-2b57ea005ba7', 5, 'O jogo causou uma diminuição na sua ambição ou eficiência?', '2026-07-17 21:37:39.378755+00', '00000000-0000-0000-0000-000000000001');
INSERT INTO public.vinte_questao (id, ordem, texto, atualizado_em, atualizado_por) VALUES ('3858163f-74df-4f3f-9f8b-c4197f2b725f', 6, 'Após ter perdido você sentiu como se necessitasse voltar o mais cedo possível a recuperar as suas perdas?', '2026-07-17 21:37:39.378755+00', '00000000-0000-0000-0000-000000000001');
INSERT INTO public.vinte_questao (id, ordem, texto, atualizado_em, atualizado_por) VALUES ('e74ed1fc-c12b-422d-9112-00712d8362ab', 7, 'Após um ganho você sentiu uma forte vontade de ganhar mais?', '2026-07-17 21:37:39.378755+00', '00000000-0000-0000-0000-000000000001');
INSERT INTO public.vinte_questao (id, ordem, texto, atualizado_em, atualizado_por) VALUES ('33936538-57e8-4dbd-bbfe-0576923c132d', 8, 'Você geralmente jogava até que seu último centavo acabasse?', '2026-07-17 21:37:39.378755+00', '00000000-0000-0000-0000-000000000001');
INSERT INTO public.vinte_questao (id, ordem, texto, atualizado_em, atualizado_por) VALUES ('8b8185e7-d557-4334-ad47-2d0731ec3516', 9, 'Você relutava em usar o "dinheiro do jogo" para as despesas normais?', '2026-07-17 21:37:39.378755+00', '00000000-0000-0000-0000-000000000001');
INSERT INTO public.vinte_questao (id, ordem, texto, atualizado_em, atualizado_por) VALUES ('f6d669eb-e1e9-46bc-9f5a-187efdf75e92', 10, 'Alguma vez você já vendeu alguma coisa para financiar seu jogo?', '2026-07-17 21:37:39.378755+00', '00000000-0000-0000-0000-000000000001');
INSERT INTO public.vinte_questao (id, ordem, texto, atualizado_em, atualizado_por) VALUES ('cd726097-300d-41f3-bb0c-a909a5eea58e', 11, 'Você já pediu dinheiro emprestado para financiar seu jogo?', '2026-07-17 21:37:39.378755+00', '00000000-0000-0000-0000-000000000001');
INSERT INTO public.vinte_questao (id, ordem, texto, atualizado_em, atualizado_por) VALUES ('e2bc764a-4a7e-4fc8-9f1d-a08f8c3ce6ee', 12, 'O jogo o tornou descuidado com seu bem-estar e o da sua família?', '2026-07-17 21:37:39.378755+00', '00000000-0000-0000-0000-000000000001');
INSERT INTO public.vinte_questao (id, ordem, texto, atualizado_em, atualizado_por) VALUES ('bc748b46-2cf2-4005-b6a0-b1b16aa0e01c', 13, 'Alguma vez você jogou por mais tempo do que planejava?', '2026-07-17 21:37:39.378755+00', '00000000-0000-0000-0000-000000000001');
INSERT INTO public.vinte_questao (id, ordem, texto, atualizado_em, atualizado_por) VALUES ('3a9ba704-83f9-4363-89b3-1cb6ec85618f', 14, 'Alguma vez você já jogou para fugir das preocupações ou problemas?', '2026-07-17 21:37:39.378755+00', '00000000-0000-0000-0000-000000000001');
INSERT INTO public.vinte_questao (id, ordem, texto, atualizado_em, atualizado_por) VALUES ('a502fffa-fca4-4cee-b7e3-5ad7367991bf', 15, 'Alguma vez você já cometeu, ou pensou em cometer um ato ilegal para financiar o jogo?', '2026-07-17 21:37:39.378755+00', '00000000-0000-0000-0000-000000000001');
INSERT INTO public.vinte_questao (id, ordem, texto, atualizado_em, atualizado_por) VALUES ('82989607-1ea8-4016-9750-b4fb1c2dea0e', 16, 'O jogo fez com que você tivesse dificuldades para dormir?', '2026-07-17 21:37:39.378755+00', '00000000-0000-0000-0000-000000000001');
INSERT INTO public.vinte_questao (id, ordem, texto, atualizado_em, atualizado_por) VALUES ('f03f5493-cc8b-4036-90f6-7ec2adc41370', 17, 'As discussões, desapontamentos ou frustrações fizeram com que você tivesse vontade de jogar?', '2026-07-17 21:37:39.378755+00', '00000000-0000-0000-0000-000000000001');
INSERT INTO public.vinte_questao (id, ordem, texto, atualizado_em, atualizado_por) VALUES ('84bb414f-88e0-420d-a2cf-a9a20702b30b', 18, 'Alguma vez você já teve vontade de celebrar alguma boa sorte com algumas horas de jogo?', '2026-07-17 21:37:39.378755+00', '00000000-0000-0000-0000-000000000001');
INSERT INTO public.vinte_questao (id, ordem, texto, atualizado_em, atualizado_por) VALUES ('6e75bd5f-41e6-4bae-811a-669bf2bb237a', 19, 'Alguma vez você já pensou em se autodestruir como resultado do seu jogo?', '2026-07-17 21:37:39.378755+00', '00000000-0000-0000-0000-000000000001');
INSERT INTO public.faq (id, pergunta, resposta, ordem, criado_em, atualizado_em) VALUES ('627e6796-9f71-4c8a-97a4-1a9fead3e414', 'Qual é a primeira coisa que um jogador compulsivo deve fazer para parar de jogar?', 'O jogador compulsivo precisa estar disposto a aceitar o fato de que está sob o domínio de uma doença e ter o desejo de ficar bem. Nossa experiência tem demonstrado que o programa de Jogadores Anônimos sempre funcionará para qualquer pessoa que tenha o desejo de parar de jogar.', 0, '2026-07-17 13:59:39.572696+00', '2026-07-17 13:59:39.572696+00');
INSERT INTO public.faq (id, pergunta, resposta, ordem, criado_em, atualizado_em) VALUES ('b88d7b50-8b28-46c6-a423-f08488018904', 'É importante saber por que jogávamos?', 'Talvez, entretanto, com relação a parar de jogar, muitos membros de Jogadores Anônimos abstiveram-se do jogo sem o benefício do conhecimento da razão porque jogavam.', 1, '2026-07-17 13:59:39.572696+00', '2026-07-17 13:59:39.572696+00');
INSERT INTO public.faq (id, pergunta, resposta, ordem, criado_em, atualizado_em) VALUES ('c9ad1d55-d644-4c00-b096-05cf64a0c3ed', 'Quais são algumas das características da pessoa que é jogador compulsivo?', '1. Incapacidade e resistência em aceitar a realidade. Daí a fuga para o mundo de sonhos do jogo.

2. Insegurança emocional. O jogador compulsivo sente-se tranquilo emocionalmente somente quando está "na ativa". Não é raro ouvir-se um membro de Jogadores Anônimos dizer: "O único lugar no qual eu realmente me sentia à vontade era quando estava sentado à uma mesa de poker. Lá eu me sentia seguro e confortável. Não eram feitas nenhumas grandes exigências de mim. Eu sabia que estava me destruindo, mas mesmo assim sentia ao mesmo tempo um certo sentimento de segurança."

3. Imaturidade. O desejo de ter todas as boas coisas da vida sem qualquer grande esforço de sua parte parece ser uma característica comum na personalidade dos jogadores com problema. Muitos membros de Jogadores Anônimos aceitam o fato de que não desejavam crescer. Inconscientemente sentiam que poderiam evitar uma responsabilidade madura através da aposta, pelo girar de uma roleta ou virada de uma carta, e assim a luta para fugir à responsabilidade finalmente tornou-se uma obsessão inconsciente. Além disso, o jogador compulsivo parece ter um forte desejo interior de ser o "manda chuva" e precisa sentir-se como o todo poderoso. O jogador compulsivo está disposto a fazer qualquer coisa (geralmente de natureza anti-social) para manter a imagem que deseja que os outros vejam.', 2, '2026-07-17 13:59:39.572696+00', '2026-07-17 13:59:39.572696+00');
INSERT INTO public.faq (id, pergunta, resposta, ordem, criado_em, atualizado_em) VALUES ('bea93733-6bf3-4855-8e93-64db608248c2', 'Qual é o maior sonho do jogador compulsivo?', 'Sonhar alto é outra característica comum dos jogadores compulsivos. Gastam um tempo enorme criando imagens das coisas grandes e maravilhosas que irão fazer logo que ganharem um grande prêmio. Eles sempre se vêem como pessoas bem filantrópicas e charmosas. Podem sonhar em presentear membros da família e amigos. Tristemente, eles sonharão mais sonhos e é claro terão mais sofrimento. Ninguém pode convencê-los de que um dia os sonhos que criaram não se tornarão realidade.', 3, '2026-07-17 13:59:39.572696+00', '2026-07-17 13:59:39.572696+00');
INSERT INTO public.faq (id, pergunta, resposta, ordem, criado_em, atualizado_em) VALUES ('dc2b565d-4663-448a-89ee-13a39c579a73', 'O jogo compulsivo não é basicamente um problema financeiro?', 'Não, o jogo compulsivo é um problema emocional. A pessoa que está dominada por esta doença inventa montanhas de problemas aparentemente insolúveis. É claro que criam problemas financeiros, mas também têm que enfrentar problemas legais, de emprego e matrimoniais. Jogadores compulsivos descobrem que perderam amigos e que são rejeitados por parentes. Dentre as sérias dificuldades que aparecem, os problemas financeiros parecem ser os mais fáceis de resolver. Quando um jogador compulsivo ingressa em Jogadores Anônimos e para de jogar, a renda normalmente aumenta e não há mais o desperdício financeiro que era causado pelo jogo. Rapidamente as pressões financeiras começam a ser atenuadas. Os membros de Jogadores Anônimos descobriram que o melhor caminho para a recuperação financeira é através do trabalho árduo e do pagamento de nossas dívidas. Pedir emprestado ou emprestar dinheiro (fazer fianças) é prejudicial à nossa recuperação e não deveria ocorrer dentro de Jogadores Anônimos. O problema mais difícil e demorado que terão que enfrentar é conseguir efetuar uma mudança de caráter neles mesmos. A maioria dos membros de Jogadores Anônimos consideram isso como seu maior desafio e que deveria ser trabalhado imediatamente e continuamente por toda a sua vida.', 4, '2026-07-17 13:59:39.572696+00', '2026-07-17 13:59:39.572696+00');
INSERT INTO public.faq (id, pergunta, resposta, ordem, criado_em, atualizado_em) VALUES ('ae4bb1d3-07e8-498a-9f17-2b34813cb1e1', 'Por que o jogador compulsivo não pode simplesmente usar a força de vontade para parar de jogar?', 'Acreditamos que a maioria das pessoas, se são honestas, reconhecerá sua falta de poder para solucionar certos problemas. Quando se trata do jogo, conhecemos muitos jogadores problemáticos que eram capazes de ficar em abstinência por longos períodos, mas sucumbiam ao jogo em virtude de um número de circunstâncias especiais. Começavam a jogar sem pensar nas consequências. As defesas com que contavam, somente por intermédio da força de vontade, eram vencidas por qualquer motivo banal para fazer uma aposta. Descobrimos que a força de vontade e o auto-conhecimento não são de ajuda nos momentos em que nos falta a razão, mas sim o apego aos princípios espirituais que parecem solucionar nossos problemas. A maioria de nós sente que a crença em um Poder superior a nós é necessária para que possamos refrear o desejo de jogar.', 5, '2026-07-17 13:59:39.572696+00', '2026-07-17 13:59:39.572696+00');
INSERT INTO public.faq (id, pergunta, resposta, ordem, criado_em, atualizado_em) VALUES ('9cd605cf-a108-41e0-9ecb-0c4ecaf7bc67', 'O que é o Programa de Recuperação?', 'É um Programa de 12 Passos usado pelos membros de JA para o seu crescimento pessoal. Pelo uso do programa em suas atividades diárias, jogadores compulsivos não apenas diminuíram o desejo de jogar, como também melhoraram outros aspectos de suas vidas.', 6, '2026-07-17 13:59:39.572696+00', '2026-07-17 13:59:39.572696+00');
INSERT INTO public.faq (id, pergunta, resposta, ordem, criado_em, atualizado_em) VALUES ('e4d172a1-a200-4243-9a49-1394150e48df', 'Jogadores Anônimos é uma sociedade religiosa?', 'Não. Jogadores Anônimos é composto de pessoas de muitas crenças religiosas e também de agnósticos e ateus. Para fazer parte de JA não se requer qualquer crença religiosa como condição, a Irmandade não pode ser descrita como uma sociedade religiosa.', 7, '2026-07-17 13:59:39.572696+00', '2026-07-17 13:59:39.572696+00');
INSERT INTO public.faq (id, pergunta, resposta, ordem, criado_em, atualizado_em) VALUES ('b2e34c70-27c2-4b1a-864f-8644f83ba07f', 'Jogadores Anônimos quer abolir o jogo?', 'Não. Jogadores Anônimos não emite opiniões sobre questões externas. Assim sendo, não se manifesta sobre a extinção do jogo.', 8, '2026-07-17 13:59:39.572696+00', '2026-07-17 13:59:39.572696+00');
INSERT INTO public.aviso (id, titulo, corpo, fixado, data_expiracao, criado_em, atualizado_em) VALUES ('322c4839-7d20-49a8-9248-d56bf1ca2bc7', 'Aviso - Grupo Campo Grande (RJ)', 'O Grupo Campo Grande - RJ informa que não haverá reunião nos dias 08, 15 e 22/07/2026, por conta das festividades na igreja.', true, '2026-07-24 02:59:59+00', '2026-07-17 21:20:50.010748+00', '2026-07-17 21:29:34.502474+00');
INSERT INTO public.imagem (id, nome_original, chave_arquivo, tamanho_bytes, alt, criado_em, criado_por) VALUES ('b3f6f577-8136-4c2c-a924-ec7fe69062e6', 'cartaz-ja.jpeg', 'ad090442-397c-431d-abde-e65f181e2a56.jpg', 69931, 'cartaz-ja.jpeg', '2026-07-17 23:08:23.799225+00', '00000000-0000-0000-0000-000000000001');
INSERT INTO public.imagem (id, nome_original, chave_arquivo, tamanho_bytes, alt, criado_em, criado_por) VALUES ('0fe9da93-cbd0-4c00-8370-0fa16404bf7b', 'Grupo-Cuiaba.jpg', '7cc24726-6339-4e29-979d-825ce78a3737.jpg', 96071, 'Grupo-Cuiaba.jpg', '2026-07-17 23:08:42.185059+00', '00000000-0000-0000-0000-000000000001');
INSERT INTO public.imagem (id, nome_original, chave_arquivo, tamanho_bytes, alt, criado_em, criado_por) VALUES ('15fdd703-6778-4885-8cca-723f7e078e11', 'GRUPO-IPSET-RECIFE.jpg', 'e688d793-3631-4e13-a88c-fc5e8ba45a9b.jpg', 113670, 'GRUPO-IPSET-RECIFE.jpg', '2026-07-17 23:08:50.99399+00', '00000000-0000-0000-0000-000000000001');
INSERT INTO public.imagem (id, nome_original, chave_arquivo, tamanho_bytes, alt, criado_em, criado_por) VALUES ('7cdb4ae8-e403-46ba-bd4d-cb08a1bed61e', 'Grupo-Sao-Luis-2-768x1226.jpg', 'a5ba7620-b7f1-4aad-8369-003f6ffea551.jpg', 123206, 'Grupo-Sao-Luis-2-768x1226.jpg', '2026-07-17 23:08:59.87692+00', '00000000-0000-0000-0000-000000000001');
INSERT INTO public.imagem (id, nome_original, chave_arquivo, tamanho_bytes, alt, criado_em, criado_por) VALUES ('fc735e5e-e08b-439b-9518-adcd58e7c507', 'Grupo-Taubate-768x1226.jpg', 'ddb8c5cc-6919-4ac3-a2d0-ed9ad6d82116.jpg', 154387, 'Grupo-Taubate-768x1226.jpg', '2026-07-17 23:09:07.672213+00', '00000000-0000-0000-0000-000000000001');
INSERT INTO public.banner_mural (id, imagem_id, alt_text, legenda, link_destino, ativo, criado_em, atualizado_em, video_url) VALUES ('06f0ff17-38cc-4546-934b-9bea4d1bdc16', 'b3f6f577-8136-4c2c-a924-ec7fe69062e6', 'cartaz ja', NULL, NULL, true, '2026-07-17 23:08:36.643866+00', '2026-07-17 23:08:36.643867+00', NULL);
INSERT INTO public.banner_mural (id, imagem_id, alt_text, legenda, link_destino, ativo, criado_em, atualizado_em, video_url) VALUES ('223ebaa0-82f4-4143-9121-67a574235280', '0fe9da93-cbd0-4c00-8370-0fa16404bf7b', 'ja cuiaba', NULL, NULL, true, '2026-07-17 23:08:47.296092+00', '2026-07-17 23:08:47.296093+00', NULL);
INSERT INTO public.banner_mural (id, imagem_id, alt_text, legenda, link_destino, ativo, criado_em, atualizado_em, video_url) VALUES ('f8282ed7-15ed-414e-8d0b-a2a4ebc66e7a', '15fdd703-6778-4885-8cca-723f7e078e11', 'ja ipsep', NULL, NULL, true, '2026-07-17 23:08:55.696633+00', '2026-07-17 23:08:55.696634+00', NULL);
INSERT INTO public.banner_mural (id, imagem_id, alt_text, legenda, link_destino, ativo, criado_em, atualizado_em, video_url) VALUES ('2096c103-eacb-40b3-9fe1-c092ba84254e', '7cdb4ae8-e403-46ba-bd4d-cb08a1bed61e', 'ja slz', NULL, NULL, true, '2026-07-17 23:09:03.982361+00', '2026-07-17 23:09:03.982361+00', NULL);
INSERT INTO public.banner_mural (id, imagem_id, alt_text, legenda, link_destino, ativo, criado_em, atualizado_em, video_url) VALUES ('39ca46c0-fbc0-4870-a507-cff54dffd08a', 'fc735e5e-e08b-439b-9518-adcd58e7c507', 'ja taubate sp', NULL, NULL, true, '2026-07-17 23:09:13.128096+00', '2026-07-17 23:09:13.128097+00', NULL);
INSERT INTO public.banner_mural (id, imagem_id, alt_text, legenda, link_destino, ativo, criado_em, atualizado_em, video_url) VALUES ('91faea15-f3e2-404d-ada2-a824480436c2', NULL, 'video institucional ja', NULL, NULL, true, '2026-07-17 23:16:32.078655+00', '2026-07-17 23:21:27.612492+00', 'https://www.youtube.com/watch?v=W04Aiy4UhEg&t=1s');
INSERT INTO public.regiao_intergrupo (id, nome, ordem, criado_em, atualizado_em) VALUES ('052d78dd-4f96-4205-9164-b4e4ae5c89dc', 'Centro-Oeste', 2, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.regiao_intergrupo (id, nome, ordem, criado_em, atualizado_em) VALUES ('6e5f826a-2407-4416-afdc-b97f53e2d98e', 'Sudeste', 3, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.regiao_intergrupo (id, nome, ordem, criado_em, atualizado_em) VALUES ('a0ead63e-a6ef-4501-ac6a-89cc2780503c', 'Sul', 4, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.regiao_intergrupo (id, nome, ordem, criado_em, atualizado_em) VALUES ('f0394ee9-80e2-4e56-9814-0d9a9c5fe1e2', 'Norte', 0, '2026-07-17 19:31:59.390463+00', '2026-07-17 21:30:02.637912+00');
INSERT INTO public.regiao_intergrupo (id, nome, ordem, criado_em, atualizado_em) VALUES ('c94fc183-5195-4b69-bf7f-2b08ee36d758', 'Nordeste', 1, '2026-07-17 19:31:59.390463+00', '2026-07-17 21:30:02.637913+00');
INSERT INTO public.estado_intergrupo (id, regiao_id, ordem, criado_em, atualizado_em, uf) VALUES ('88e539da-00c3-4b89-9fc3-c2fb22a0dedf', 'f0394ee9-80e2-4e56-9814-0d9a9c5fe1e2', 1, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00', 'PA');
INSERT INTO public.estado_intergrupo (id, regiao_id, ordem, criado_em, atualizado_em, uf) VALUES ('746df419-d347-4314-b477-3e7e34807527', 'f0394ee9-80e2-4e56-9814-0d9a9c5fe1e2', 0, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00', 'AM');
INSERT INTO public.estado_intergrupo (id, regiao_id, ordem, criado_em, atualizado_em, uf) VALUES ('5f3f9167-5d9d-408c-b4bf-2598a65a6349', 'c94fc183-5195-4b69-bf7f-2b08ee36d758', 8, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00', 'SE');
INSERT INTO public.estado_intergrupo (id, regiao_id, ordem, criado_em, atualizado_em, uf) VALUES ('7b9834ad-68f9-40aa-aa0d-4c836199a43e', 'c94fc183-5195-4b69-bf7f-2b08ee36d758', 7, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00', 'RN');
INSERT INTO public.estado_intergrupo (id, regiao_id, ordem, criado_em, atualizado_em, uf) VALUES ('1685f798-89db-42e6-98b1-b096dc138150', 'c94fc183-5195-4b69-bf7f-2b08ee36d758', 6, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00', 'PI');
INSERT INTO public.estado_intergrupo (id, regiao_id, ordem, criado_em, atualizado_em, uf) VALUES ('01ec7ca1-9534-44b6-b70f-c4909cc4eb22', 'c94fc183-5195-4b69-bf7f-2b08ee36d758', 5, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00', 'PE');
INSERT INTO public.estado_intergrupo (id, regiao_id, ordem, criado_em, atualizado_em, uf) VALUES ('cd4967e0-b269-4feb-8e65-a30260eb5e9c', 'c94fc183-5195-4b69-bf7f-2b08ee36d758', 4, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00', 'PB');
INSERT INTO public.estado_intergrupo (id, regiao_id, ordem, criado_em, atualizado_em, uf) VALUES ('7284f7fe-f336-4edc-ba45-f7d507482f06', 'c94fc183-5195-4b69-bf7f-2b08ee36d758', 3, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00', 'MA');
INSERT INTO public.estado_intergrupo (id, regiao_id, ordem, criado_em, atualizado_em, uf) VALUES ('37cdab7f-adf5-4e04-b5ec-29038c1a64c6', 'c94fc183-5195-4b69-bf7f-2b08ee36d758', 2, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00', 'CE');
INSERT INTO public.estado_intergrupo (id, regiao_id, ordem, criado_em, atualizado_em, uf) VALUES ('0a02b04b-e183-4a7d-a9b7-b324f799a365', 'c94fc183-5195-4b69-bf7f-2b08ee36d758', 1, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00', 'BA');
INSERT INTO public.estado_intergrupo (id, regiao_id, ordem, criado_em, atualizado_em, uf) VALUES ('53c414aa-d175-4d4a-9ee9-2750910d5404', 'c94fc183-5195-4b69-bf7f-2b08ee36d758', 0, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00', 'AL');
INSERT INTO public.estado_intergrupo (id, regiao_id, ordem, criado_em, atualizado_em, uf) VALUES ('d795ce02-f245-43be-b4cc-f8303b850572', '052d78dd-4f96-4205-9164-b4e4ae5c89dc', 3, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00', 'MT');
INSERT INTO public.estado_intergrupo (id, regiao_id, ordem, criado_em, atualizado_em, uf) VALUES ('52d764ae-29a0-4ac9-9a3d-5fa364f6b5ec', '052d78dd-4f96-4205-9164-b4e4ae5c89dc', 2, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00', 'MS');
INSERT INTO public.estado_intergrupo (id, regiao_id, ordem, criado_em, atualizado_em, uf) VALUES ('fd3e36c7-3616-42c8-aef5-a7919aa546f8', '052d78dd-4f96-4205-9164-b4e4ae5c89dc', 1, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00', 'GO');
INSERT INTO public.estado_intergrupo (id, regiao_id, ordem, criado_em, atualizado_em, uf) VALUES ('20c92879-2db2-4543-afef-573c5cda295a', '052d78dd-4f96-4205-9164-b4e4ae5c89dc', 0, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00', 'DF');
INSERT INTO public.estado_intergrupo (id, regiao_id, ordem, criado_em, atualizado_em, uf) VALUES ('1478b423-4afa-4920-abe0-137d54f2b480', '6e5f826a-2407-4416-afdc-b97f53e2d98e', 3, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00', 'SP');
INSERT INTO public.estado_intergrupo (id, regiao_id, ordem, criado_em, atualizado_em, uf) VALUES ('338db64f-380d-4b9b-a247-b4223a77f70b', '6e5f826a-2407-4416-afdc-b97f53e2d98e', 2, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00', 'RJ');
INSERT INTO public.estado_intergrupo (id, regiao_id, ordem, criado_em, atualizado_em, uf) VALUES ('21b9d754-2c11-4726-b35b-e999b14a23c4', '6e5f826a-2407-4416-afdc-b97f53e2d98e', 1, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00', 'MG');
INSERT INTO public.estado_intergrupo (id, regiao_id, ordem, criado_em, atualizado_em, uf) VALUES ('5dbce62c-51b4-4abe-a3b3-6d5563ded8ad', '6e5f826a-2407-4416-afdc-b97f53e2d98e', 0, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00', 'ES');
INSERT INTO public.estado_intergrupo (id, regiao_id, ordem, criado_em, atualizado_em, uf) VALUES ('b84589df-21ba-4662-8b27-b9ce0b7b1fc7', 'a0ead63e-a6ef-4501-ac6a-89cc2780503c', 2, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00', 'SC');
INSERT INTO public.estado_intergrupo (id, regiao_id, ordem, criado_em, atualizado_em, uf) VALUES ('590f85fa-d9a9-4eab-af8b-b506fa41d37a', 'a0ead63e-a6ef-4501-ac6a-89cc2780503c', 1, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00', 'RS');
INSERT INTO public.estado_intergrupo (id, regiao_id, ordem, criado_em, atualizado_em, uf) VALUES ('a3c9088a-9779-4b66-9c0a-6f699d44f512', 'a0ead63e-a6ef-4501-ac6a-89cc2780503c', 0, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00', 'PR');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('770bd0e3-a5af-4c8d-9a62-fcec52c7acfa', '88e539da-00c3-4b89-9fc3-c2fb22a0dedf', 'Grupo Belém', 'Av. Ceará, 595 - Bairro Canudos, Belém - PA', 'Canudos', 'Belém', 1, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('8b563344-7988-4baa-bab8-72e5b4ed8b33', '746df419-d347-4314-b477-3e7e34807527', 'Grupo Manaus', 'R. Libertador, 55 - Paróquia N. Sra. das Graças, Manaus - AM', NULL, 'Manaus', 1, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('7baede98-40fb-472d-8d64-9faae2fe7325', '5f3f9167-5d9d-408c-b4bf-2598a65a6349', 'Grupo Aracaju', 'R. Renato Fonseca de Oliveira, 545 - Coroa do Meio, Aracaju - SE', 'Coroa do Meio', 'Aracaju', 1, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('664cccd3-15dc-4549-9e79-9472c3c1b521', '7b9834ad-68f9-40aa-aa0d-4c836199a43e', 'Grupo Mossoró', 'R. Sebastião Saraíva, 42 - UEI Júlio Galdino Neto, Mossoró - RN', NULL, 'Mossoró', 2, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('bed826e4-5d31-442c-8105-c25b455f9077', '7b9834ad-68f9-40aa-aa0d-4c836199a43e', 'Grupo Natal', 'R. Domingos Amado, 3393, Natal - RN', NULL, 'Natal', 1, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('b460e459-d577-4388-b5c4-658f3c91d980', '1685f798-89db-42e6-98b1-b096dc138150', 'Grupo Teresina', 'BR 316, 7.200 - Parque Piauí, Teresina - PI', 'Parque Piauí', 'Teresina', 1, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('0f2d8b20-c349-4b9d-902e-cd701c59892e', '01ec7ca1-9534-44b6-b70f-c4909cc4eb22', 'Grupo Ipsep', 'R. Jean Emile Frave - Escola Eleanor Roosevelt - Ipsep, Recife - PE', 'Ipsep', 'Recife', 5, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('95429078-ceed-4fe7-8332-6be2bee6f574', '01ec7ca1-9534-44b6-b70f-c4909cc4eb22', 'Grupo Petrolina', 'R. São José, 121 - Centro, Petrolina - PE', 'Centro', 'Petrolina', 4, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('498bf443-8184-4fe0-83c6-f98e4f84b5a8', '01ec7ca1-9534-44b6-b70f-c4909cc4eb22', 'Grupo Caruaru', 'Av. Prof. José Leão, 567, Caruaru - PE', NULL, 'Caruaru', 3, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('d40ed17f-a3d2-4e3a-8ebd-eb7ab3209297', '01ec7ca1-9534-44b6-b70f-c4909cc4eb22', 'Grupo Pina', 'Av. Herculano Bandeira - Pastoral João Paulo II, Recife - PE', 'Pina', 'Recife', 2, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('93d9983b-affb-411a-a3aa-acb840ca33a3', '01ec7ca1-9534-44b6-b70f-c4909cc4eb22', 'Grupo Boa-Vista', 'Condomínio Edifício Tereza Cristina, Recife - PE', 'Boa Vista', 'Recife', 1, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('0070b410-a24e-4270-a222-a6bf3cbf528d', 'cd4967e0-b269-4feb-8e65-a30260eb5e9c', 'Grupo Paraíba', 'R. Mal. Almeida Barreto, 222 - Centro, João Pessoa - PB', 'Centro', 'João Pessoa', 1, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('2dee93c9-f898-453f-8580-02a74dde4564', '7284f7fe-f336-4edc-ba45-f7d507482f06', 'Grupo SLZ - Bequimão', 'Igreja do Sagrado Coração de Jesus - Matriz, São Luís - MA', 'Bequimão', 'São Luís', 2, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('2b4a19fe-fd94-4714-8b92-eb21bae4dee9', '7284f7fe-f336-4edc-ba45-f7d507482f06', 'Grupo São Luís', 'Praça Gonçalves Dias, 696 - Par. N. Sra. dos Remédios, São Luís - MA', NULL, 'São Luís', 1, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('2f9fd608-f6db-4343-817d-5062321858a9', '37cdab7f-adf5-4e04-b5ec-29038c1a64c6', 'Grupo Fortaleza', 'R. São Paulo, 32 - sala 316, Fortaleza - CE', NULL, 'Fortaleza', 2, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('01aeda02-2587-4c3a-95e4-c30c09ed6201', '37cdab7f-adf5-4e04-b5ec-29038c1a64c6', 'Grupo Iracema', 'Av. Almirante Barroso, 949, Fortaleza - CE', 'Praia de Iracema', 'Fortaleza', 1, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('f9a9d689-79db-4a1a-98fe-8c586224ff0d', '0a02b04b-e183-4a7d-a9b7-b324f799a365', 'Grupo Salvador', 'Alameda Verona, 158 - Pituba, Salvador - BA', 'Pituba', 'Salvador', 1, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('1ba1fb00-c6de-422f-aa61-a8b6c5c35a4b', '53c414aa-d175-4d4a-9ee9-2750910d5404', 'Grupo Maceió', 'Fernandes Lima, 869 - Farol, Maceió - AL', 'Farol', 'Maceió', 1, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('4066347f-dab8-4fca-aad7-3abddc18a6aa', 'd795ce02-f245-43be-b4cc-f8303b850572', 'Grupo Cuiabá', 'Av. Des. Antônio Quirinoi de Araújo, 1126 - Poção, Cuiabá - MT', 'Poção', 'Cuiabá', 1, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('e3026fcd-b8ca-4593-a776-db64812b1298', '52d764ae-29a0-4ac9-9a3d-5fa364f6b5ec', 'Grupo Nova Consciência', 'R. Maracaju, 249 - Centro, Campo Grande - MS', 'Centro', 'Campo Grande', 1, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('8425287f-62b4-4a1f-b5a3-cc528e3d6ee6', 'fd3e36c7-3616-42c8-aef5-a7919aa546f8', 'Grupo Central Goiânia', 'Rua DF-2 - Setor Chácara do Governador - Capela N. Sra. da Guia, Goiânia - GO', 'Chácara do Governador', 'Goiânia', 1, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('e56ae9d1-997c-4374-be93-a09aa8938649', '20c92879-2db2-4543-afef-573c5cda295a', 'Grupo Brasília', 'Paróquia do Verbo Divino, Brasília - DF', NULL, 'Brasília', 1, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('5a8f1ced-45c0-4ff7-8b90-f0fb7513cf84', '1478b423-4afa-4920-abe0-137d54f2b480', 'Grupo Renascer Campinas', 'R. Paulo Lacerda, 309, Campinas - SP', NULL, 'Campinas', 20, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('4806a1c4-4f6e-4e42-a601-d96be2f90f16', '1478b423-4afa-4920-abe0-137d54f2b480', 'Grupo Centro - São José dos Campos', 'Av. São José, 646 - Jardim Bela Vista, São José dos Campos - SP', 'Jardim Bela Vista', 'São José dos Campos', 19, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('4cdf494e-8015-4ffe-9f9d-655c580325b2', '1478b423-4afa-4920-abe0-137d54f2b480', 'Grupo Taubaté', 'Av. Maria Teresa de Moura, 150, Taubaté - SP', NULL, 'Taubaté', 18, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('169cfc8a-6aa6-4ba5-b61d-2ca8d0017028', '1478b423-4afa-4920-abe0-137d54f2b480', 'Grupo Pindamonhangaba', 'R. Barão Homem de Melo - Centro, Pindamonhangaba - SP', 'Centro', 'Pindamonhangaba', 17, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('f649eaa5-dd46-4658-9df2-a5470742256d', '1478b423-4afa-4920-abe0-137d54f2b480', 'Grupo Coragem & Sabedoria - Zona Leste', 'Av. Imperador, 5831 - Vila Jacuí, São Paulo - SP', 'Vila Jacuí', 'São Paulo', 16, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('4e318837-033a-424d-9a8e-13b3495a4544', '1478b423-4afa-4920-abe0-137d54f2b480', 'Grupo Bertioga', 'Av. Anchieta, 1450 - sala 08 - Centro, Bertioga - SP', 'Centro', 'Bertioga', 15, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('6a27e46e-5dac-415d-89f7-fb6d789da3dd', '1478b423-4afa-4920-abe0-137d54f2b480', 'Grupo São José do Rio Preto', 'R. José Martins Romero, 590, São José do Rio Preto - SP', NULL, 'São José do Rio Preto', 14, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('a0519592-3471-46eb-93c4-d040d1d25fa2', '1478b423-4afa-4920-abe0-137d54f2b480', 'Grupo Passos de Luz - Artur Nogueira', 'R. Nossa Senhora das Dores - Igreja Matriz, Artur Nogueira - SP', NULL, 'Artur Nogueira', 13, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('5f21454d-ff91-4a5b-bc40-8839d2d30d68', '1478b423-4afa-4920-abe0-137d54f2b480', 'Grupo Atibaia', 'Atibaia, SP', NULL, 'Atibaia', 12, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('cb0e6181-e951-44dd-9c04-84d68fcc90d7', '1478b423-4afa-4920-abe0-137d54f2b480', 'Grupo Limeira', 'R. Oscar Buzolin, 1-63 - Paróquia N. Sra. do Rosário, Limeira - SP', NULL, 'Limeira', 11, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('c8779d38-7d32-4707-9f01-8b01872201f8', '1478b423-4afa-4920-abe0-137d54f2b480', 'Grupo São José dos Campos', 'R. Bertioga, 191, São José dos Campos - SP', NULL, 'São José dos Campos', 10, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('949601a6-956a-4999-9341-4925ceff32f5', '1478b423-4afa-4920-abe0-137d54f2b480', 'Grupo São Bernardo do Campo', 'Estr. dos Casa, 3230, São Bernardo do Campo - SP', NULL, 'São Bernardo do Campo', 9, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('62394669-0940-41cc-8724-0032d905edac', '1478b423-4afa-4920-abe0-137d54f2b480', 'Grupo Santa Ifigênia', 'Largo de Santa Ifigênia, 30, São Paulo - SP', 'Santa Ifigênia', 'São Paulo', 8, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('b0ae5e6e-0a01-48d6-ad4b-fdcad26fa284', '1478b423-4afa-4920-abe0-137d54f2b480', 'Grupo Nova Chance', 'Av. João Dias, 822 - biblioteca, São Paulo - SP', NULL, 'São Paulo', 7, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('2ead037e-339e-4e27-abb1-cdda9c0b5cbb', '1478b423-4afa-4920-abe0-137d54f2b480', 'Grupo Santos', 'Av. Dr. Pedro Lessa, 2262, Santos - SP', NULL, 'Santos', 6, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('83dbff65-dc49-473d-8676-8a163690307f', '1478b423-4afa-4920-abe0-137d54f2b480', 'Grupo Buscando a Serenidade', 'R. Pedreira de Magalhães, 100, São Paulo - SP', NULL, 'São Paulo', 5, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('91d67350-eb71-4991-9415-599be0ed5837', '1478b423-4afa-4920-abe0-137d54f2b480', 'Grupo Jabaquara', 'Santuário São Judas Tadeu, São Paulo - SP', 'Jabaquara', 'São Paulo', 4, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('9376f10a-08da-4adb-8e1d-ebcb4e9cb322', '1478b423-4afa-4920-abe0-137d54f2b480', 'Grupo Itaim', 'Rua Clodomiro Amazonas, São Paulo - SP', 'Itaim Bibi', 'São Paulo', 3, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('cba9b7aa-ff8f-4530-9d4a-31d7b389124b', '1478b423-4afa-4920-abe0-137d54f2b480', 'Grupo Mauá', 'Praça Monsenhor Alexandre Venâncio Arminas, 01, Mauá - SP', NULL, 'Mauá', 2, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('34062e6a-a5b8-4622-ac29-271c82208c56', '1478b423-4afa-4920-abe0-137d54f2b480', 'Grupo Pioneiro', 'R. Guaporé, 335, São Paulo - SP', NULL, 'São Paulo', 1, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('92789b29-abf0-4997-ad46-f1970a8d3b0a', '338db64f-380d-4b9b-a247-b4223a77f70b', 'Grupo Petrópolis', 'R. Visc. de Souza Franco, 474, Petrópolis - RJ', NULL, 'Petrópolis', 14, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('7c599783-c32b-4a30-a664-cd12239e69f9', '338db64f-380d-4b9b-a247-b4223a77f70b', 'Grupo Nova Iguaçu', 'Nova Iguaçu, RJ', NULL, 'Nova Iguaçu', 13, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('538d622b-bedb-4e5f-b3fa-b6be4e8412da', '338db64f-380d-4b9b-a247-b4223a77f70b', 'Grupo Neves', 'Catedral Metropolitana de N. Sra. das Neves, Rio de Janeiro - RJ', 'Centro', 'Rio de Janeiro', 12, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('f012ab8c-20e5-4ead-a591-113398011c36', '338db64f-380d-4b9b-a247-b4223a77f70b', 'Grupo Arpoador', 'R. Francisco Otaviano, 99 - Ipanema, Rio de Janeiro - RJ', 'Ipanema', 'Rio de Janeiro', 11, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('d0872d19-3a2b-486c-b0a1-d09322a3ee5c', '338db64f-380d-4b9b-a247-b4223a77f70b', 'Grupo Tijuca', 'Igreja de Santo Afonso, Rio de Janeiro - RJ', 'Tijuca', 'Rio de Janeiro', 10, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('f01d879d-6eed-4e90-89c1-56eafb2e16c7', '338db64f-380d-4b9b-a247-b4223a77f70b', 'Grupo São Cristóvão', 'Santuário Arquidiocesano Santa Edwiges, Rio de Janeiro - RJ', 'São Cristóvão', 'Rio de Janeiro', 9, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('8b8ed545-a12d-4a00-8ebf-92731710853c', '338db64f-380d-4b9b-a247-b4223a77f70b', 'Grupo Flamengo', 'R. Sen. Vergueiro, 141, Rio de Janeiro - RJ', 'Flamengo', 'Rio de Janeiro', 8, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('e6452058-67e4-4c5b-bcc4-59fc2cc24c21', '338db64f-380d-4b9b-a247-b4223a77f70b', 'Grupo Pilares', 'Paróquia São Benedito, Rio de Janeiro - RJ', 'Pilares', 'Rio de Janeiro', 7, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('4067060b-c41a-459a-aa38-76b66319c3b7', '338db64f-380d-4b9b-a247-b4223a77f70b', 'Grupo Copacabana', 'R. Francisco Otaviano, 99 - Ipanema, Rio de Janeiro - RJ', 'Ipanema', 'Rio de Janeiro', 6, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('b82d109c-df3a-406c-9685-447f112077fa', '338db64f-380d-4b9b-a247-b4223a77f70b', 'Grupo Nilópolis', 'R. Comendador Rodrigues Alves, 1544, Nilópolis - RJ', NULL, 'Nilópolis', 5, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('73ea6b39-379b-467d-a084-748dc2904a52', '338db64f-380d-4b9b-a247-b4223a77f70b', 'Grupo Campo Grande', 'Paróquia de Sant''Ana, Rio de Janeiro - RJ', 'Campo Grande', 'Rio de Janeiro', 4, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('3f6b4c31-ed9c-4d85-8233-fc9cb1981c8e', '338db64f-380d-4b9b-a247-b4223a77f70b', 'Grupo Barra', 'Praça Embaixador Gualberto de Oliveira, 10, Rio de Janeiro - RJ', 'Barra da Tijuca', 'Rio de Janeiro', 3, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('0e9a557e-8173-4672-a029-d19dc4b10446', '338db64f-380d-4b9b-a247-b4223a77f70b', 'Grupo Niterói', 'Av. Ernani do Amaral Peixoto, 171 - sala 804, Niterói - RJ', 'Centro', 'Niterói', 2, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('a49d6c90-e4c5-439e-8793-1da34b9fe004', '338db64f-380d-4b9b-a247-b4223a77f70b', 'Grupo Acre-RJ', 'Rio de Janeiro, RJ', NULL, 'Rio de Janeiro', 1, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('9679e1f5-59e2-479b-afe7-960745b45a7e', '21b9d754-2c11-4726-b35b-e999b14a23c4', 'Grupo Juiz de Fora', 'R. São Mateus, 629 - sala 306 - Igreja de São Mateus, Juiz de Fora - MG', 'São Mateus', 'Juiz de Fora', 5, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('3b8d2ea8-e700-4454-b7c5-3c87a40c27bd', '21b9d754-2c11-4726-b35b-e999b14a23c4', 'Grupo Uberlândia', 'R. Geraldo Garcia de Almeida, 160 - São Jorge, Uberlândia - MG', 'São Jorge', 'Uberlândia', 4, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('4edd0613-1a3b-4d25-ae0c-cd49f1e4f06b', '21b9d754-2c11-4726-b35b-e999b14a23c4', 'Grupo São Lourenço', 'R. Coronel José Justino, 50, São Lourenço - MG', NULL, 'São Lourenço', 3, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('0ff75536-e0c3-4158-8731-6f3dd816724f', '21b9d754-2c11-4726-b35b-e999b14a23c4', 'Grupo Varginha', 'Praça Quintino Bocaiúva, 58 - sala 301, Varginha - MG', NULL, 'Varginha', 2, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('8ced9904-3ba5-4de2-b005-133bf677c961', '21b9d754-2c11-4726-b35b-e999b14a23c4', 'Grupo Vida Nova (MG)', 'Av. do Contorno, 4777, Belo Horizonte - MG', NULL, 'Belo Horizonte', 1, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('19b3060c-96c4-4d9a-9278-9076f8cf1932', '5dbce62c-51b4-4abe-a3b3-6d5563ded8ad', 'Grupo Vitória (ES)', 'Paróquia São José - Maruípe, Vitória - ES', 'Maruípe', 'Vitória', 1, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('4f0abfe2-22f0-4db0-83cc-1d5353ef9468', 'b84589df-21ba-4662-8b27-b9ce0b7b1fc7', 'Grupo Vida Nova (SC)', 'R. Vítor Konder, 344, Florianópolis - SC', NULL, 'Florianópolis', 1, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('6232e091-9caa-4e5f-984b-3ec4a61c0885', '590f85fa-d9a9-4eab-af8b-b506fa41d37a', 'Grupo Novo Hamburgo', 'R. Dr. João Daniel Hillebrand, 1349 - Rondônia, Novo Hamburgo - RS', 'Rondônia', 'Novo Hamburgo', 2, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('b256ec3d-c99e-402f-a59a-fe167f8554df', '590f85fa-d9a9-4eab-af8b-b506fa41d37a', 'Grupo Esperança', 'Av. Farrapos, 2611, Porto Alegre - RS', NULL, 'Porto Alegre', 1, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('6561d693-0342-4ac0-bb08-1eb21c4edb17', 'a3c9088a-9779-4b66-9c0a-6f699d44f512', 'Grupo Maringá', 'Praça Monsenhor Bernardo Cnudde, 07 - Zona 7, Maringá - PR', 'Zona 7', 'Maringá', 4, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('44ce2300-1ca3-4f8d-a23d-72421fffccff', 'a3c9088a-9779-4b66-9c0a-6f699d44f512', 'Grupo Londrina', 'Londrina, PR', NULL, 'Londrina', 3, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('c4da5a97-7b8b-4262-8218-136a7b28273a', 'a3c9088a-9779-4b66-9c0a-6f699d44f512', 'Grupo Renovação', 'Av. N. Sra. Aparecida, 1637, Curitiba - PR', NULL, 'Curitiba', 2, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo (id, estado_id, nome, endereco, bairro, cidade, ordem, criado_em, atualizado_em) VALUES ('aced841e-3b17-4fd7-9eab-f9aa13001993', 'a3c9088a-9779-4b66-9c0a-6f699d44f512', 'Grupo Vitória (SC)', 'Rua Theodoro Makiolka, 64, Curitiba - PR', NULL, 'Curitiba', 1, '2026-07-17 19:31:59.390463+00', '2026-07-17 19:31:59.390463+00');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('3f6b4c31-ed9c-4d85-8233-fc9cb1981c8e', 'QUINTA', '19h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('770bd0e3-a5af-4c8d-9a62-fcec52c7acfa', 'DOMINGO', '9h às 11h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('0070b410-a24e-4270-a222-a6bf3cbf528d', 'QUARTA', '19:30h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('8b563344-7988-4baa-bab8-72e5b4ed8b33', 'SEXTA', '19h às 21h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('d40ed17f-a3d2-4e3a-8ebd-eb7ab3209297', 'QUINTA', '19h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('aced841e-3b17-4fd7-9eab-f9aa13001993', 'QUINTA', '19:30h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('1ba1fb00-c6de-422f-aa61-a8b6c5c35a4b', 'QUARTA', '19h às 21h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('e3026fcd-b8ca-4593-a776-db64812b1298', 'QUINTA', '19h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('0070b410-a24e-4270-a222-a6bf3cbf528d', 'SEGUNDA', '19:30h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('4edd0613-1a3b-4d25-ae0c-cd49f1e4f06b', 'TERCA', '18h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('e6452058-67e4-4c5b-bcc4-59fc2cc24c21', 'SEXTA', '18h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('92789b29-abf0-4997-ad46-f1970a8d3b0a', 'TERCA', '19h às 21h (desde 31/10/25)');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('d0872d19-3a2b-486c-b0a1-d09322a3ee5c', 'QUINTA', '19h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('01aeda02-2587-4c3a-95e4-c30c09ed6201', 'SABADO', '10h às 12h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('62394669-0940-41cc-8724-0032d905edac', 'DOMINGO', '9h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('7c599783-c32b-4a30-a664-cd12239e69f9', 'SEXTA', '19h (desde 15/08/25)');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('34062e6a-a5b8-4622-ac29-271c82208c56', 'QUINTA', '20h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('8425287f-62b4-4a1f-b5a3-cc528e3d6ee6', 'SEGUNDA', '19h às 21h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('2dee93c9-f898-453f-8580-02a74dde4564', 'QUINTA', '19h às 21h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('83dbff65-dc49-473d-8676-8a163690307f', 'QUARTA', '20h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('3b8d2ea8-e700-4454-b7c5-3c87a40c27bd', 'TERCA', '19:30h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('b256ec3d-c99e-402f-a59a-fe167f8554df', 'TERCA', '19h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('4066347f-dab8-4fca-aad7-3abddc18a6aa', 'QUARTA', '19:30h (inaugural desde 29/07/26)');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('f9a9d689-79db-4a1a-98fe-8c586224ff0d', 'QUINTA', '19h às 21h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('5f21454d-ff91-4a5b-bc40-8839d2d30d68', 'QUINTA', '19h às 21h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('6232e091-9caa-4e5f-984b-3ec4a61c0885', 'DOMINGO', '17h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('c8779d38-7d32-4707-9f01-8b01872201f8', 'SABADO', '16h (desde 16/11/24)');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('01aeda02-2587-4c3a-95e4-c30c09ed6201', 'QUINTA', '19:30h às 21:30h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('a49d6c90-e4c5-439e-8793-1da34b9fe004', 'QUINTA', '18h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('b82d109c-df3a-406c-9685-447f112077fa', 'SEGUNDA', '19h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('169cfc8a-6aa6-4ba5-b61d-2ca8d0017028', 'QUARTA', '19h às 21h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('73ea6b39-379b-467d-a084-748dc2904a52', 'QUARTA', '19h (suspenso até 29/07/26)');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('8ced9904-3ba5-4de2-b005-133bf677c961', 'QUINTA', '19:15h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('0e9a557e-8173-4672-a029-d19dc4b10446', 'QUINTA', '19h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('91d67350-eb71-4991-9415-599be0ed5837', 'SEXTA', '19h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('0e9a557e-8173-4672-a029-d19dc4b10446', 'SABADO', '9:30h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('8b8ed545-a12d-4a00-8ebf-92731710853c', 'DOMINGO', '18:30h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('a49d6c90-e4c5-439e-8793-1da34b9fe004', 'SABADO', '10h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('f012ab8c-20e5-4ead-a591-113398011c36', 'QUARTA', '18h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('44ce2300-1ca3-4f8d-a23d-72421fffccff', 'TERCA', '19h (desde 19/08/25)');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('4f0abfe2-22f0-4db0-83cc-1d5353ef9468', 'SABADO', '16h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('f649eaa5-dd46-4658-9df2-a5470742256d', 'TERCA', '20h às 22h (desde 04/10/25)');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('e56ae9d1-997c-4374-be93-a09aa8938649', 'QUINTA', '19h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('0f2d8b20-c349-4b9d-902e-cd701c59892e', 'TERCA', '19h às 21h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('2ead037e-339e-4e27-abb1-cdda9c0b5cbb', 'SABADO', '10h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('4f0abfe2-22f0-4db0-83cc-1d5353ef9468', 'QUINTA', '19h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('9376f10a-08da-4adb-8e1d-ebcb4e9cb322', 'QUINTA', '20h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('b0ae5e6e-0a01-48d6-ad4b-fdcad26fa284', 'SABADO', '16h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('2b4a19fe-fd94-4714-8b92-eb21bae4dee9', 'TERCA', '19h às 21h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('f9a9d689-79db-4a1a-98fe-8c586224ff0d', 'TERCA', '19h às 21h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('19b3060c-96c4-4d9a-9278-9076f8cf1932', 'SEGUNDA', '19h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('2f9fd608-f6db-4343-817d-5062321858a9', 'QUARTA', '19:30h às 21:30h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('4806a1c4-4f6e-4e42-a601-d96be2f90f16', 'TERCA', '19h às 21h (desde 02/06/26)');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('93d9983b-affb-411a-a3aa-acb840ca33a3', 'DOMINGO', '10h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('6a27e46e-5dac-415d-89f7-fb6d789da3dd', 'SEXTA', '19h às 21h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('2f9fd608-f6db-4343-817d-5062321858a9', 'SEGUNDA', '19:30h às 21:30h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('664cccd3-15dc-4549-9e79-9472c3c1b521', 'TERCA', '19h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('d0872d19-3a2b-486c-b0a1-d09322a3ee5c', 'TERCA', '19h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('b82d109c-df3a-406c-9685-447f112077fa', 'QUINTA', '19h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('a49d6c90-e4c5-439e-8793-1da34b9fe004', 'SEGUNDA', '12:30h e 18h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('6561d693-0342-4ac0-bb08-1eb21c4edb17', 'QUARTA', '20h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('6232e091-9caa-4e5f-984b-3ec4a61c0885', 'QUINTA', '19:30h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('4cdf494e-8015-4ffe-9f9d-655c580325b2', 'QUARTA', '19h às 21h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('cb0e6181-e951-44dd-9c04-84d68fcc90d7', 'SEGUNDA', '19:30h às 21:30h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('0e9a557e-8173-4672-a029-d19dc4b10446', 'SEGUNDA', '19h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('4e318837-033a-424d-9a8e-13b3495a4544', 'QUINTA', 'Horário a confirmar');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('e3026fcd-b8ca-4593-a776-db64812b1298', 'TERCA', '19h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('cba9b7aa-ff8f-4530-9d4a-31d7b389124b', 'TERCA', '19:30h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('e6452058-67e4-4c5b-bcc4-59fc2cc24c21', 'TERCA', '18h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('0ff75536-e0c3-4158-8731-6f3dd816724f', 'SABADO', '19h às 20:30h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('9679e1f5-59e2-479b-afe7-960745b45a7e', 'SEGUNDA', '19h às 20:30h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('a49d6c90-e4c5-439e-8793-1da34b9fe004', 'QUARTA', '18h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('b460e459-d577-4388-b5c4-658f3c91d980', 'TERCA', '19h às 21h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('498bf443-8184-4fe0-83c6-f98e4f84b5a8', 'TERCA', '18:30h às 20h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('7baede98-40fb-472d-8d64-9faae2fe7325', 'QUINTA', '19h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('95429078-ceed-4fe7-8332-6be2bee6f574', 'SEXTA', '19:30h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('34062e6a-a5b8-4622-ac29-271c82208c56', 'SEGUNDA', '20h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('4f0abfe2-22f0-4db0-83cc-1d5353ef9468', 'TERCA', '19h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('9376f10a-08da-4adb-8e1d-ebcb4e9cb322', 'TERCA', '20h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('83dbff65-dc49-473d-8676-8a163690307f', 'SEXTA', '20h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('8b8ed545-a12d-4a00-8ebf-92731710853c', 'SEXTA', '18:30h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('3f6b4c31-ed9c-4d85-8233-fc9cb1981c8e', 'SEGUNDA', '19h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('1ba1fb00-c6de-422f-aa61-a8b6c5c35a4b', 'SABADO', '16h às 18h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('f01d879d-6eed-4e90-89c1-56eafb2e16c7', 'SABADO', '10h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('4067060b-c41a-459a-aa38-76b66319c3b7', 'QUINTA', '18h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('949601a6-956a-4999-9341-4925ceff32f5', 'QUINTA', '19:30h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('bed826e4-5d31-442c-8105-c25b455f9077', 'SEGUNDA', '19h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('8ced9904-3ba5-4de2-b005-133bf677c961', 'TERCA', '19:15h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('c4da5a97-7b8b-4262-8218-136a7b28273a', 'TERCA', '19:30h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('5a8f1ced-45c0-4ff7-8b90-f0fb7513cf84', 'DOMINGO', '10h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('538d622b-bedb-4e5f-b3fa-b6be4e8412da', 'QUARTA', '18:30h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('a49d6c90-e4c5-439e-8793-1da34b9fe004', 'TERCA', '18h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('a0519592-3471-46eb-93c4-d040d1d25fa2', 'QUINTA', '20h');
INSERT INTO public.grupo_intergrupo_horario (grupo_intergrupo_id, dia_semana, horario) VALUES ('f01d879d-6eed-4e90-89c1-56eafb2e16c7', 'QUINTA', '18h');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('529d921b-de30-45a5-8863-9f93d0254258', '0f2d8b20-c349-4b9d-902e-cd701c59892e', NULL, '(81) 98693-3867', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('ad2ed95c-9d6c-48c8-ab04-e0b04fc0f81f', '93d9983b-affb-411a-a3aa-acb840ca33a3', NULL, '(81) 99183-8297', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('eb322ed2-2932-48c8-b8ea-06e5d6cb3da4', '2f9fd608-f6db-4343-817d-5062321858a9', NULL, '(85) 98929-5529', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('a5b3742a-4745-46ca-ace1-5f62183e069e', '6232e091-9caa-4e5f-984b-3ec4a61c0885', NULL, '(51) 98030-2002', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('0ec8ce29-e18d-4f6d-b28c-109b301c0a93', 'bed826e4-5d31-442c-8105-c25b455f9077', NULL, '(84) 99834-8964', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('58d025bf-489b-4e77-b68c-526d8a8142ca', '770bd0e3-a5af-4c8d-9a62-fcec52c7acfa', NULL, '(91) 99603-0369', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('2347fc1d-e098-4c34-be10-96aeaeac8e1d', 'b256ec3d-c99e-402f-a59a-fe167f8554df', NULL, '(51) 98030-2002', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('e03d14f2-f05d-4e26-b0a2-f7145c48335b', '498bf443-8184-4fe0-83c6-f98e4f84b5a8', NULL, '(81) 99921-0731', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('8469e9bf-92b0-496e-b8c7-2544aec5b28f', '9679e1f5-59e2-479b-afe7-960745b45a7e', NULL, '(32) 98877-7088', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('9a853528-cdd2-49a1-a083-2e122bae6776', '01aeda02-2587-4c3a-95e4-c30c09ed6201', NULL, '(85) 98929-5529', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('15ebf3da-ee85-4ecc-a413-d277a160e34f', 'b460e459-d577-4388-b5c4-658f3c91d980', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('a8250ef9-5c89-4e4a-95b4-3143b0166e8f', '2dee93c9-f898-453f-8580-02a74dde4564', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('7e0b7e19-4d56-458c-a301-07b23c6e0634', '7c599783-c32b-4a30-a664-cd12239e69f9', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('836de06b-aa95-480e-846b-34f183aac5b4', 'aced841e-3b17-4fd7-9eab-f9aa13001993', NULL, '(41) 99684-8004', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('c61b16ee-4093-4fb8-9ce2-aa487ecd059f', '2b4a19fe-fd94-4714-8b92-eb21bae4dee9', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('6ce83d9c-037e-4679-9a3d-e0732d127a3a', 'f9a9d689-79db-4a1a-98fe-8c586224ff0d', NULL, '(71) 98624-0512', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('ae011cc4-467f-46fc-8df3-9d2fb15ea3f9', '44ce2300-1ca3-4f8d-a23d-72421fffccff', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('c9eb1db2-5ee7-40db-8a4e-d87cf8202853', '95429078-ceed-4fe7-8332-6be2bee6f574', NULL, '(74) 99122-2660', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('5aa0f3ad-df96-4588-ba42-2e894abe5d6e', '4066347f-dab8-4fca-aad7-3abddc18a6aa', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('e19fd763-260f-491d-a65f-4c87bb036415', 'f01d879d-6eed-4e90-89c1-56eafb2e16c7', NULL, '(21) 97063-8290', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('c6cbbe9e-92e5-429f-9f0b-6ac6d151fafa', '1ba1fb00-c6de-422f-aa61-a8b6c5c35a4b', NULL, '(82) 99620-2998', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('27040ddf-e879-4afd-b4ac-51c93d4df234', 'c4da5a97-7b8b-4262-8218-136a7b28273a', NULL, '(41) 99893-6054', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('108d5723-e313-410b-a0b2-bec2b8e95111', '0070b410-a24e-4270-a222-a6bf3cbf528d', NULL, '(83) 98872-6572', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('1b11780a-dfb9-4aa6-b754-08f93333a35a', 'd40ed17f-a3d2-4e3a-8ebd-eb7ab3209297', NULL, '(81) 93159-357', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('4fbaaa13-6d7d-4069-ab02-4eb1cfba3bd6', '7baede98-40fb-472d-8d64-9faae2fe7325', NULL, '(11) 99571-6942', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('d11dc075-c08a-41df-822b-67efc697b7cc', '3b8d2ea8-e700-4454-b7c5-3c87a40c27bd', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('b0875475-c1d1-48a8-a0b8-425c91399881', '8ced9904-3ba5-4de2-b005-133bf677c961', NULL, '(31) 99206-2501', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('baf30c1d-784f-4e3e-b90b-038da4cb984c', '0ff75536-e0c3-4158-8731-6f3dd816724f', NULL, '(35) 99989-9997', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('baac2fef-9560-4a1f-8042-ab73004c2296', 'e3026fcd-b8ca-4593-a776-db64812b1298', NULL, '(67) 98473-1321', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('dbf07cf3-6567-4c67-9789-18c28c45f055', '8b563344-7988-4baa-bab8-72e5b4ed8b33', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('1cdfd5a6-b8ec-4ce0-b453-e0564a9ed26f', '664cccd3-15dc-4549-9e79-9472c3c1b521', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('5b728d1b-89b2-4315-961f-3b61dbad6cc7', '8425287f-62b4-4a1f-b5a3-cc528e3d6ee6', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('2eadab07-7179-4cd4-873f-67224e02bd41', 'e56ae9d1-997c-4374-be93-a09aa8938649', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('52bb037f-41e2-4264-9773-f6b71cff6664', '5a8f1ced-45c0-4ff7-8b90-f0fb7513cf84', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('a089ff9a-a086-45ed-a17b-a9ff6ed23405', '4806a1c4-4f6e-4e42-a601-d96be2f90f16', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('c55378dc-5463-4e89-840d-21b4cf34dd3a', '4cdf494e-8015-4ffe-9f9d-655c580325b2', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('3af800a3-225e-403c-a716-137bf674cd5e', '169cfc8a-6aa6-4ba5-b61d-2ca8d0017028', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('772c6622-bc65-466b-afa1-4bd66551b7a7', 'f649eaa5-dd46-4658-9df2-a5470742256d', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('c2547471-b47a-4249-bdbd-e179effc5132', '4e318837-033a-424d-9a8e-13b3495a4544', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('2fe54c9a-2d3b-4fe2-a952-10931fa8bb45', '6a27e46e-5dac-415d-89f7-fb6d789da3dd', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('f0afb48e-e3c3-4475-89a6-2cfa8b249abe', 'a0519592-3471-46eb-93c4-d040d1d25fa2', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('2a0753f9-7028-4946-b46f-707dee27f7f1', '5f21454d-ff91-4a5b-bc40-8839d2d30d68', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('ccbadf2c-ded7-457a-99d9-8495f307bc21', 'cb0e6181-e951-44dd-9c04-84d68fcc90d7', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('c56c410e-7725-412b-918f-dbe5a33cfa92', 'c8779d38-7d32-4707-9f01-8b01872201f8', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('3ad723a7-c30a-4e04-89d4-c873fdccd3f6', '949601a6-956a-4999-9341-4925ceff32f5', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('2e46ca98-07c2-46b9-a44a-db867c134252', '62394669-0940-41cc-8724-0032d905edac', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('c29cacf6-b426-42a9-9770-8532a6880cf7', 'b0ae5e6e-0a01-48d6-ad4b-fdcad26fa284', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('e5dbb542-6225-41f4-acb2-0e0b0679f1de', '2ead037e-339e-4e27-abb1-cdda9c0b5cbb', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('0cf6c53e-e214-4519-857f-e2b3dd053c47', '83dbff65-dc49-473d-8676-8a163690307f', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('b3464873-6ccb-4beb-a80e-c47727dbef0f', '91d67350-eb71-4991-9415-599be0ed5837', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('3dfd42e1-3ff4-48a6-b41d-ed1b98e99095', '9376f10a-08da-4adb-8e1d-ebcb4e9cb322', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('9a2bb4da-06f5-4c8b-84b5-4f8a28d610f6', 'cba9b7aa-ff8f-4530-9d4a-31d7b389124b', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('943aa363-8f3a-43a0-9448-3e62b5d289be', '34062e6a-a5b8-4622-ac29-271c82208c56', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('8e3fe4e7-51e6-4af3-9067-3be6814629e2', '92789b29-abf0-4997-ad46-f1970a8d3b0a', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('88393570-4cbf-4a7f-be6c-7a02c3d94069', '538d622b-bedb-4e5f-b3fa-b6be4e8412da', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('eb81bbf0-f45e-4943-ba0f-43ef1aa84e2b', 'f012ab8c-20e5-4ead-a591-113398011c36', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('a1f1942e-0fd3-4475-a310-139634353c3f', 'd0872d19-3a2b-486c-b0a1-d09322a3ee5c', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('69ceaa07-804b-43ee-aa40-5a802303b438', '8b8ed545-a12d-4a00-8ebf-92731710853c', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('3505a42f-cb51-496b-8c39-eb2d40e6be8d', 'e6452058-67e4-4c5b-bcc4-59fc2cc24c21', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('053dfdcb-df3c-4c9b-b80c-f093efcdac8e', '4067060b-c41a-459a-aa38-76b66319c3b7', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('7ec66f4a-f79a-466e-ab6c-7eeeb47e030e', 'b82d109c-df3a-406c-9685-447f112077fa', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('99042827-cf7e-4bdb-86e4-eb8a1f481f22', '73ea6b39-379b-467d-a084-748dc2904a52', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('3949eb9b-ea0f-422a-90f2-ada295d9f48c', '3f6b4c31-ed9c-4d85-8233-fc9cb1981c8e', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('be47df21-5183-4bd2-b6b9-31a6a0e804c7', '0e9a557e-8173-4672-a029-d19dc4b10446', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('c56b9967-7ec1-42dc-adaf-82c36e512690', 'a49d6c90-e4c5-439e-8793-1da34b9fe004', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('304b466f-d594-4f95-a602-c0afbd53739e', '4edd0613-1a3b-4d25-ae0c-cd49f1e4f06b', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('dfaca057-5064-4c20-a64e-e81db2fbe696', '19b3060c-96c4-4d9a-9278-9076f8cf1932', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('1a3bb4b0-35cb-40f1-80e4-5d17189170cf', '4f0abfe2-22f0-4db0-83cc-1d5353ef9468', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.grupo_intergrupo_telefone (id, grupo_id, label, telefone, criado_em, atualizado_em) VALUES ('7d0c4d61-d801-4dc3-87f9-5f31f40f04f1', '6561d693-0342-4ac0-bb08-1eb21c4edb17', NULL, '(21) 99472-1933', '2026-07-17 19:36:48.691397+00', '2026-07-17 19:36:48.691397+00');
INSERT INTO public.hotline_regiao (id, criado_em, atualizado_em, uf, cidade) VALUES ('78033b64-ebf5-43fc-9a2d-b90d1fbc9eb8', '2026-07-17 19:17:56.826927+00', '2026-07-17 19:17:56.826927+00', 'SP', 'São Paulo');
INSERT INTO public.hotline_regiao (id, criado_em, atualizado_em, uf, cidade) VALUES ('6ef05683-e5dc-4ec4-bc39-c5f221a23890', '2026-07-17 19:17:56.826927+00', '2026-07-17 19:17:56.826927+00', 'RJ', 'Rio de Janeiro');
INSERT INTO public.hotline_telefone (id, regiao_id, telefone, criado_em, atualizado_em) VALUES ('b20bfb73-fd7e-410c-8b8f-0fdcc6d703b9', '78033b64-ebf5-43fc-9a2d-b90d1fbc9eb8', '(11) 3229-1023', '2026-07-17 19:17:56.826927+00', '2026-07-17 19:17:56.826927+00');
INSERT INTO public.hotline_telefone (id, regiao_id, telefone, criado_em, atualizado_em) VALUES ('5a9daf8a-4eef-4452-81c2-fdb33b052461', '78033b64-ebf5-43fc-9a2d-b90d1fbc9eb8', '(11) 99571-6942', '2026-07-17 19:17:56.826927+00', '2026-07-17 19:17:56.826927+00');
INSERT INTO public.hotline_telefone (id, regiao_id, telefone, criado_em, atualizado_em) VALUES ('19c3dc55-bea0-48fa-8c29-7ea9a0d7718b', '6ef05683-e5dc-4ec4-bc39-c5f221a23890', '(21) 99750-3174', '2026-07-17 19:17:56.826927+00', '2026-07-17 19:17:56.826927+00');
INSERT INTO public.escritorio_servico (id, nome, endereco, complemento, cidade, emails, ordem, criado_em, atualizado_em, bairro) VALUES ('b50d5c54-45d3-4893-a608-f40a9f4fb960', 'ERSJAB - Escritório Regional de Serviço de Jogadores Anônimos do Brasil', 'Rua Paulino Guimarães, 93', 'Sala 12', 'São Paulo - SP, CEP 01109-020', 'jogadoresanonimossaopaulo@uol.com.br', 1, '2026-07-17 21:31:58.14311+00', '2026-07-17 21:31:58.14311+00', 'Luz (Centro)');
INSERT INTO public.escritorio_servico (id, nome, endereco, complemento, cidade, emails, ordem, criado_em, atualizado_em, bairro) VALUES ('2d1d6136-b010-43c0-8ec8-a7f9f9f49024', 'JAERJ - Jogadores Anônimos Escritório do Rio de Janeiro', 'Av. Presidente Vargas, 542', 'Sala 1407', 'Rio de Janeiro - RJ, CEP 20071-000', 'jogadoresanonimos@yahoo.com.br', 2, '2026-07-17 21:31:58.14311+00', '2026-07-17 21:31:58.14311+00', 'Centro');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('fd0be79a-8734-446c-8649-e8c7d64fd792', 'Grupo Acre-RJ', 'presencial', '', 'Terça', '18h', 'Rio de Janeiro, RJ - Rio de Janeiro', '(21) 99472-1933', 'Terça', 'Noite', -22.8991875, -43.1812619, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('637a340a-f2d4-4122-88c3-a14e448549ca', 'Grupo Novo Hamburgo', 'presencial', '', 'Domingo', '17h', 'R. Dr. João Daniel Hillebrand, 1349 - Rondônia, Novo Hamburgo - RS - Rondônia - Novo Hamburgo', '(51) 98030-2002', 'Domingo', 'Tarde', -29.7080648, -51.1196357, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('e0dcb326-370d-4d04-bd79-d3f0091efe64', 'Grupo Mossoró', 'presencial', '', 'Terça', '19h', 'R. Sebastião Saraíva, 42 - UEI Júlio Galdino Neto, Mossoró - RN - Mossoró', '(21) 99472-1933', 'Terça', 'Noite', -5.1886609, -37.3684413, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('b0b9dced-139f-4415-ba3e-410d03d35cec', 'Grupo Jabaquara', 'presencial', '', 'Sexta', '19h', 'Santuário São Judas Tadeu, São Paulo - SP - Jabaquara - São Paulo', '(21) 99472-1933', 'Sexta', 'Noite', -23.6271568, -46.6431253, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('7f534206-ee19-46eb-b613-f74934d9483c', 'Grupo Nova Consciência', 'presencial', '', 'Quinta', '19h', 'R. Maracaju, 249 - Centro, Campo Grande - MS - Centro - Campo Grande', '(67) 98473-1321', 'Quinta', 'Noite', -20.4586422, -54.6186393, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('0e6f16ef-2150-4a94-a5e6-e16bb20a4181', 'Grupo Salvador', 'presencial', '', 'Quinta', '19h às 21h', 'Alameda Verona, 158 - Pituba, Salvador - BA - Pituba - Salvador', '(71) 98624-0512', 'Quinta', 'Noite', -13.0018199, -38.4603532, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('d1b6e414-02ae-42a7-af63-a198d0d36343', 'Grupo Acre-RJ', 'presencial', '', 'Quarta', '18h', 'Rio de Janeiro, RJ - Rio de Janeiro', '(21) 99472-1933', 'Quarta', 'Noite', -22.8991875, -43.1812619, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('6d304a9a-72e2-4446-9906-a684349d50b9', 'Grupo SLZ - Bequimão', 'presencial', '', 'Quinta', '19h às 21h', 'Igreja do Sagrado Coração de Jesus - Matriz, São Luís - MA - Bequimão - São Luís', '(21) 99472-1933', 'Quinta', 'Noite', -2.5302514, -44.2463521, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('0dfca769-b613-486d-baf3-c3e6a8781163', 'Grupo Atibaia', 'presencial', '', 'Quinta', '19h às 21h', 'Atibaia, SP - Atibaia', '(21) 99472-1933', 'Quinta', 'Noite', -23.0421462, -46.5474197, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('e1312358-90b8-4567-9efb-a41f8290dc23', 'Grupo Centro - São José dos Campos', 'presencial', '', 'Terça', '19h às 21h (desde 02/06/26)', 'Av. São José, 646 - Jardim Bela Vista, São José dos Campos - SP - Jardim Bela Vista - São José dos Campos', '(21) 99472-1933', 'Terça', 'Noite', -23.1795922, -45.8887711, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('895cdb0a-385a-4d94-ba14-3a76762245a3', 'Grupo São Lourenço', 'presencial', '', 'Terça', '18h', 'R. Coronel José Justino, 50, São Lourenço - MG - São Lourenço', '(21) 99472-1933', 'Terça', 'Noite', -22.1165701, -45.0500401, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('c347647b-679f-4563-96b0-211d8dbe4e29', 'Grupo Vida Nova (MG)', 'presencial', '', 'Quinta', '19:15h', 'Av. do Contorno, 4777, Belo Horizonte - MG - Belo Horizonte', '(31) 99206-2501', 'Quinta', 'Noite', -19.9358466, -43.92498, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('5d7d9a3e-c28c-4ac5-ad3e-00958656426c', 'Grupo Vida Nova (SC)', 'presencial', '', 'Terça', '19h', 'R. Vítor Konder, 344, Florianópolis - SC - Florianópolis', '(21) 99472-1933', 'Terça', 'Noite', -27.587725, -48.5435032, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('02dcfeca-1226-4a5b-8b3a-72fc8b28e1c6', 'Grupo Buscando a Serenidade', 'presencial', '', 'Sexta', '20h', 'R. Pedreira de Magalhães, 100, São Paulo - SP - São Paulo', '(21) 99472-1933', 'Sexta', 'Noite', -23.5430499, -46.4832799, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('d5451ce3-61aa-475c-b2c9-519470675fa5', 'Grupo Itaim', 'presencial', '', 'Quinta', '20h', 'Rua Clodomiro Amazonas, São Paulo - SP - Itaim Bibi - São Paulo', '(21) 99472-1933', 'Quinta', 'Noite', -23.5878243, -46.6790223, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('bf4deb07-7969-4c7a-930e-4533ca8c044e', 'Grupo Niterói', 'presencial', '', 'Sábado', '9:30h', 'Av. Ernani do Amaral Peixoto, 171 - sala 804, Niterói - RJ - Centro - Niterói', '(21) 99472-1933', 'Sábado', 'Manhã', -22.8933377, -43.1220548, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('58803628-0e3e-4b9e-86b9-2b569bda56bf', 'Grupo Pioneiro', 'presencial', '', 'Quinta', '20h', 'R. Guaporé, 335, São Paulo - SP - São Paulo', '(21) 99472-1933', 'Quinta', 'Noite', -23.5233379, -46.6270746, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('8e6ed67a-8685-47c0-8255-1b30a8706df4', 'Grupo Caruaru', 'presencial', '', 'Terça', '18:30h às 20h', 'Av. Prof. José Leão, 567, Caruaru - PE - Caruaru', '(81) 99921-0731', 'Terça', 'Noite', -8.2747015, -35.9651512, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('2dbdcd91-d276-487a-b3bf-483fae916835', 'Grupo Vitória (ES)', 'presencial', '', 'Segunda', '19h', 'Paróquia São José - Maruípe, Vitória - ES - Maruípe - Vitória', '(21) 99472-1933', 'Segunda', 'Noite', -20.2954947, -40.3184854, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('3f02245f-5a49-40ed-bfb6-8da172fd3665', 'Grupo Tijuca', 'presencial', '', 'Quinta', '19h', 'Igreja de Santo Afonso, Rio de Janeiro - RJ - Tijuca - Rio de Janeiro', '(21) 99472-1933', 'Quinta', 'Noite', -22.9229904, -43.2333324, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('adcc527d-3b6c-4ba9-847a-69620859e663', 'Grupo Ipsep', 'presencial', '', 'Terça', '19h às 21h', 'R. Jean Emile Frave - Escola Eleanor Roosevelt - Ipsep, Recife - PE - Ipsep - Recife', '(81) 98693-3867', 'Terça', 'Noite', -8.1082126, -34.916595, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('f0ecbd0e-9d0a-454b-8dff-8b915f2ccdcc', 'Grupo Varginha', 'presencial', '', 'Sábado', '19h às 20:30h', 'Praça Quintino Bocaiúva, 58 - sala 301, Varginha - MG - Varginha', '(35) 99989-9997', 'Sábado', 'Noite', -21.5537768, -45.4361834, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('69df59b0-e468-4053-be4b-39a00fe9cae5', 'Grupo Coragem & Sabedoria - Zona Leste', 'presencial', '', 'Terça', '20h às 22h (desde 04/10/25)', 'Av. Imperador, 5831 - Vila Jacuí, São Paulo - SP - Vila Jacuí - São Paulo', '(21) 99472-1933', 'Terça', 'Noite', -23.5135346, -46.4475477, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('ad14f1df-2bbb-4324-99f9-a775a55390b6', 'Grupo Nilópolis', 'presencial', '', 'Segunda', '19h', 'R. Comendador Rodrigues Alves, 1544, Nilópolis - RJ - Nilópolis', '(21) 99472-1933', 'Segunda', 'Noite', -22.8121647, -43.4070709, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('18a304ba-64ca-4e7c-82c4-d37b34d1b371', 'Grupo Maringá', 'presencial', '', 'Quarta', '20h', 'Praça Monsenhor Bernardo Cnudde, 07 - Zona 7, Maringá - PR - Zona 7 - Maringá', '(21) 99472-1933', 'Quarta', 'Noite', -23.4165811, -51.92687, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('6cd9da07-84cf-4bce-875e-380673691852', 'Grupo Pina', 'presencial', '', 'Quinta', '19h', 'Av. Herculano Bandeira - Pastoral João Paulo II, Recife - PE - Pina - Recife', '(81) 93159-357', 'Quinta', 'Noite', -8.0882126, -34.8857706, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('cea483a3-cd37-4df8-b417-85d95d4b98fe', 'Grupo Barra', 'presencial', '', 'Quinta', '19h', 'Praça Embaixador Gualberto de Oliveira, 10, Rio de Janeiro - RJ - Barra da Tijuca - Rio de Janeiro', '(21) 99472-1933', 'Quinta', 'Noite', -23.008036, -43.4287191, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('c9690e42-feca-4ecb-b219-ddba89517d42', 'Grupo Niterói', 'presencial', '', 'Quinta', '19h', 'Av. Ernani do Amaral Peixoto, 171 - sala 804, Niterói - RJ - Centro - Niterói', '(21) 99472-1933', 'Quinta', 'Noite', -22.8933377, -43.1220548, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('ab533448-8f91-4e31-b2a6-8564a8952fa3', 'Grupo Tijuca', 'presencial', '', 'Terça', '19h', 'Igreja de Santo Afonso, Rio de Janeiro - RJ - Tijuca - Rio de Janeiro', '(21) 99472-1933', 'Terça', 'Noite', -22.9229904, -43.2333324, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('e3a1c3fb-dd7f-4786-8f04-48653a56ef7d', 'Grupo Nova Iguaçu', 'presencial', '', 'Sexta', '19h (desde 15/08/25)', 'Nova Iguaçu, RJ - Nova Iguaçu', '(21) 99472-1933', 'Sexta', 'Noite', -22.7590014, -43.4527247, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('b76c176a-aad8-4e52-b297-e0b4bec1a243', 'Grupo Londrina', 'presencial', '', 'Terça', '19h (desde 19/08/25)', 'Londrina, PR - Londrina', '(21) 99472-1933', 'Terça', 'Noite', -23.3194673, -51.1561301, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('2a1ddf17-e215-4ac5-be8f-1491cd343089', 'Grupo Vida Nova (SC)', 'presencial', '', 'Quinta', '19h', 'R. Vítor Konder, 344, Florianópolis - SC - Florianópolis', '(21) 99472-1933', 'Quinta', 'Noite', -27.587725, -48.5435032, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('acf56df5-7be4-40a7-8bac-1eac11906394', 'Grupo Vida Nova (MG)', 'presencial', '', 'Terça', '19:15h', 'Av. do Contorno, 4777, Belo Horizonte - MG - Belo Horizonte', '(31) 99206-2501', 'Terça', 'Noite', -19.9358466, -43.92498, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('fe42f6a0-7c02-4555-80f8-f1f750ac0f77', 'Grupo Arpoador', 'presencial', '', 'Quarta', '18h', 'R. Francisco Otaviano, 99 - Ipanema, Rio de Janeiro - RJ - Ipanema - Rio de Janeiro', '(21) 99472-1933', 'Quarta', 'Noite', -22.9873486, -43.191855, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('6b54bb21-502b-4d37-9a81-c23cf2f35464', 'Grupo Itaim', 'presencial', '', 'Terça', '20h', 'Rua Clodomiro Amazonas, São Paulo - SP - Itaim Bibi - São Paulo', '(21) 99472-1933', 'Terça', 'Noite', -23.5878243, -46.6790223, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('2c19af93-fe73-4a14-a9e8-ee4a4e0b3f05', 'Grupo Aracaju', 'presencial', '', 'Quinta', '19h', 'R. Renato Fonseca de Oliveira, 545 - Coroa do Meio, Aracaju - SE - Coroa do Meio - Aracaju', '(11) 99571-6942', 'Quinta', 'Noite', -10.9716575, -37.0450777, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('65ac0936-62a6-42c9-9a1d-25971f1444bd', 'Grupo Renovação', 'presencial', '', 'Terça', '19:30h', 'Av. N. Sra. Aparecida, 1637, Curitiba - PR - Curitiba', '(41) 99893-6054', 'Terça', 'Noite', -25.4497228, -49.312704, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('b9a0e172-7265-486c-b90f-a398eff46d1e', 'Grupo Flamengo', 'presencial', '', 'Sexta', '18:30h', 'R. Sen. Vergueiro, 141, Rio de Janeiro - RJ - Flamengo - Rio de Janeiro', '(21) 99472-1933', 'Sexta', 'Noite', -22.9373498, -43.175719, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('5064dec5-36da-46d3-a399-3162a72dc87a', 'Grupo Vitória (SC)', 'presencial', '', 'Quinta', '19:30h', 'Rua Theodoro Makiolka, 64, Curitiba - PR - Curitiba', '(41) 99684-8004', 'Quinta', 'Noite', -25.3739455, -49.2233142, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('b7000ef1-11e2-47a4-b44d-1281411ad5d1', 'Grupo Flamengo', 'presencial', '', 'Domingo', '18:30h', 'R. Sen. Vergueiro, 141, Rio de Janeiro - RJ - Flamengo - Rio de Janeiro', '(21) 99472-1933', 'Domingo', 'Noite', -22.9373498, -43.175719, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('ac6f2f3c-def5-4022-b1a4-18aef70abbd9', 'Grupo Salvador', 'presencial', '', 'Terça', '19h às 21h', 'Alameda Verona, 158 - Pituba, Salvador - BA - Pituba - Salvador', '(71) 98624-0512', 'Terça', 'Noite', -13.0018199, -38.4603532, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('94758e8e-bb41-4f6a-a5af-85eefee6298c', 'Grupo Nova Consciência', 'presencial', '', 'Terça', '19h', 'R. Maracaju, 249 - Centro, Campo Grande - MS - Centro - Campo Grande', '(67) 98473-1321', 'Terça', 'Noite', -20.4586422, -54.6186393, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('05135a5d-c222-41c5-8b7f-ebe5447f5b6a', 'Grupo São Cristóvão', 'presencial', '', 'Quinta', '18h', 'Santuário Arquidiocesano Santa Edwiges, Rio de Janeiro - RJ - São Cristóvão - Rio de Janeiro', '(21) 97063-8290', 'Quinta', 'Noite', -22.9028886, -43.2216555, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('f9dc2d07-c668-4579-8bf0-551a0c6858e4', 'Grupo Pindamonhangaba', 'presencial', '', 'Quarta', '19h às 21h', 'R. Barão Homem de Melo - Centro, Pindamonhangaba - SP - Centro - Pindamonhangaba', '(21) 99472-1933', 'Quarta', 'Noite', -22.9279439, -45.4621505, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('569d1a9b-1ee2-45fa-8832-64b01d1ce268', 'Grupo Nilópolis', 'presencial', '', 'Quinta', '19h', 'R. Comendador Rodrigues Alves, 1544, Nilópolis - RJ - Nilópolis', '(21) 99472-1933', 'Quinta', 'Noite', -22.8121647, -43.4070709, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('416c14d2-0fb3-4e31-b056-dfc1f5e19a82', 'Grupo Neves', 'presencial', '', 'Quarta', '18:30h', 'Catedral Metropolitana de N. Sra. das Neves, Rio de Janeiro - RJ - Centro - Rio de Janeiro', '(21) 99472-1933', 'Quarta', 'Noite', -22.8533317, -43.0977765, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('428301b6-7432-4ce3-82b1-6c4be919eec2', 'Grupo Niterói', 'presencial', '', 'Segunda', '19h', 'Av. Ernani do Amaral Peixoto, 171 - sala 804, Niterói - RJ - Centro - Niterói', '(21) 99472-1933', 'Segunda', 'Noite', -22.8933377, -43.1220548, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('95be375e-330d-4f69-a4e7-85bf915f35c5', 'Grupo Passos de Luz - Artur Nogueira', 'presencial', '', 'Quinta', '20h', 'R. Nossa Senhora das Dores - Igreja Matriz, Artur Nogueira - SP - Artur Nogueira', '(21) 99472-1933', 'Quinta', 'Noite', -22.5853793, -47.1517637, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('39c4290b-1986-42f8-8572-a22eb0967332', 'Grupo Barra', 'presencial', '', 'Segunda', '19h', 'Praça Embaixador Gualberto de Oliveira, 10, Rio de Janeiro - RJ - Barra da Tijuca - Rio de Janeiro', '(21) 99472-1933', 'Segunda', 'Noite', -23.008036, -43.4287191, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('07a0e8da-a71b-4b3e-ba13-5d6936d276a2', 'Grupo São Cristóvão', 'presencial', '', 'Sábado', '10h', 'Santuário Arquidiocesano Santa Edwiges, Rio de Janeiro - RJ - São Cristóvão - Rio de Janeiro', '(21) 97063-8290', 'Sábado', 'Manhã', -22.9028886, -43.2216555, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('53a8e80f-00fe-4a93-8e46-df260bbd2fb5', 'Grupo Petrolina', 'presencial', '', 'Sexta', '19:30h', 'R. São José, 121 - Centro, Petrolina - PE - Centro - Petrolina', '(74) 99122-2660', 'Sexta', 'Noite', -9.3993148, -40.4966844, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('ffb7fde0-3779-4f5f-8365-b3dda774e5fe', 'Grupo Fortaleza', 'presencial', '', 'Quarta', '19:30h às 21:30h', 'R. São Paulo, 32 - sala 316, Fortaleza - CE - Fortaleza', '(85) 98929-5529', 'Quarta', 'Noite', -3.7264063, -38.5261686, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('d5ec0840-76fd-4f30-8e71-f6f0b9d1b640', 'Grupo Boa-Vista', 'presencial', '', 'Domingo', '10h', 'Condomínio Edifício Tereza Cristina, Recife - PE - Boa Vista - Recife', '(81) 99183-8297', 'Domingo', 'Manhã', -8.062238, -34.8823747, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('c2bb796d-e203-468a-8c88-5b28ec0976f6', 'Grupo Natal', 'presencial', '', 'Segunda', '19h', 'R. Domingos Amado, 3393, Natal - RN - Natal', '(84) 99834-8964', 'Segunda', 'Noite', -5.8354378, -35.2206243, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('d70935d3-724a-4183-aeb3-bbcf7fe83bea', 'Grupo Manaus', 'presencial', '', 'Sexta', '19h às 21h', 'R. Libertador, 55 - Paróquia N. Sra. das Graças, Manaus - AM - Manaus', '(21) 99472-1933', 'Sexta', 'Noite', -3.110322, -60.0216558, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('58dccac8-8829-4ca5-96f1-dfdf87a3f799', 'Grupo São Luís', 'presencial', '', 'Terça', '19h às 21h', 'Praça Gonçalves Dias, 696 - Par. N. Sra. dos Remédios, São Luís - MA - São Luís', '(21) 99472-1933', 'Terça', 'Noite', -2.5240438, -44.2961289, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('d7a31100-919e-4101-bfe6-aa47b5cee294', 'Grupo Novo Hamburgo', 'presencial', '', 'Quinta', '19:30h', 'R. Dr. João Daniel Hillebrand, 1349 - Rondônia, Novo Hamburgo - RS - Rondônia - Novo Hamburgo', '(51) 98030-2002', 'Quinta', 'Noite', -29.7080648, -51.1196357, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('3b4684b1-1fe2-4536-834d-b48d0cc2a3f3', 'Grupo Acre-RJ', 'presencial', '', 'Segunda', '12:30h e 18h', 'Rio de Janeiro, RJ - Rio de Janeiro', '(21) 99472-1933', 'Segunda', 'Tarde', -22.8991875, -43.1812619, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('c9049b62-46fe-4f47-86a7-6dc0496e8f2b', 'Grupo Renascer Campinas', 'presencial', '', 'Domingo', '10h', 'R. Paulo Lacerda, 309, Campinas - SP - Campinas', '(21) 99472-1933', 'Domingo', 'Manhã', -22.9251218, -47.0769687, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('a04067d3-b0e9-4696-ad4c-853745e501d6', 'Grupo Mauá', 'presencial', '', 'Terça', '19:30h', 'Praça Monsenhor Alexandre Venâncio Arminas, 01, Mauá - SP - Mauá', '(21) 99472-1933', 'Terça', 'Noite', -23.669916, -46.4621, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('c0263454-e3cc-4af5-9980-802f64720e1f', 'Grupo Pilares', 'presencial', '', 'Sexta', '18h', 'Paróquia São Benedito, Rio de Janeiro - RJ - Pilares - Rio de Janeiro', '(21) 99472-1933', 'Sexta', 'Noite', -22.8820057, -43.293602, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('4b551ac0-eac4-4dcb-b344-dcd9f3c16276', 'Grupo Santos', 'presencial', '', 'Sábado', '10h', 'Av. Dr. Pedro Lessa, 2262, Santos - SP - Santos', '(21) 99472-1933', 'Sábado', 'Manhã', -23.9686014, -46.3124117, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('e7a17930-a43c-4ae7-95af-37023aee387b', 'Grupo Central Goiânia', 'presencial', '', 'Segunda', '19h às 21h', 'Rua DF-2 - Setor Chácara do Governador - Capela N. Sra. da Guia, Goiânia - GO - Chácara do Governador - Goiânia', '(21) 99472-1933', 'Segunda', 'Noite', -16.7288243, -49.2213727, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('6bb95963-838a-4475-99b1-93b8fc946367', 'Grupo São José do Rio Preto', 'presencial', '', 'Sexta', '19h às 21h', 'R. José Martins Romero, 590, São José do Rio Preto - SP - São José do Rio Preto', '(21) 99472-1933', 'Sexta', 'Noite', -20.7716843, -49.3903536, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('69578f1c-519b-4fa0-9521-9a5e5314ca74', 'Grupo Fortaleza', 'presencial', '', 'Segunda', '19:30h às 21:30h', 'R. São Paulo, 32 - sala 316, Fortaleza - CE - Fortaleza', '(85) 98929-5529', 'Segunda', 'Noite', -3.7264063, -38.5261686, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('d67d19f8-f857-4fe0-93d1-4d50cc86459e', 'Grupo Vida Nova (SC)', 'presencial', '', 'Sábado', '16h', 'R. Vítor Konder, 344, Florianópolis - SC - Florianópolis', '(21) 99472-1933', 'Sábado', 'Tarde', -27.587725, -48.5435032, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('1c77c497-d74c-429f-a4f0-071f0e73e941', 'Grupo Juiz de Fora', 'presencial', '', 'Segunda', '19h às 20:30h', 'R. São Mateus, 629 - sala 306 - Igreja de São Mateus, Juiz de Fora - MG - São Mateus - Juiz de Fora', '(32) 98877-7088', 'Segunda', 'Noite', -21.7741955, -43.3527994, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('8d3423a5-d564-40ba-b4e0-a0f1d5190382', 'Grupo Uberlândia', 'presencial', '', 'Terça', '19:30h', 'R. Geraldo Garcia de Almeida, 160 - São Jorge, Uberlândia - MG - São Jorge - Uberlândia', '(21) 99472-1933', 'Terça', 'Noite', -18.9499184, -48.2320253, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('85d01568-d94e-4fb9-b2a4-ffcebfa29c38', 'Grupo Campo Grande', 'presencial', '', 'Quarta', '19h (suspenso até 29/07/26)', 'Paróquia de Sant''Ana, Rio de Janeiro - RJ - Campo Grande - Rio de Janeiro', '(21) 99472-1933', 'Quarta', 'Noite', -22.8870582, -43.5589104, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('75db626c-90ed-4e68-b6a1-bfd4cfc9985b', 'Grupo Brasília', 'presencial', '', 'Quinta', '19h', 'Paróquia do Verbo Divino, Brasília - DF - Brasília', '(21) 99472-1933', 'Quinta', 'Noite', -15.7307064, -47.7503739, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('d848e520-8b64-4d98-8f59-a5520ed13b14', 'Grupo Bertioga', 'presencial', '', 'Quinta', 'Horário a confirmar', 'Av. Anchieta, 1450 - sala 08 - Centro, Bertioga - SP - Centro - Bertioga', '(21) 99472-1933', 'Quinta', 'Noite', -23.8430953, -46.1349148, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('2a2f03af-0562-442c-8cd0-29c8fc40e098', 'Grupo Petrópolis', 'presencial', '', 'Terça', '19h às 21h (desde 31/10/25)', 'R. Visc. de Souza Franco, 474, Petrópolis - RJ - Petrópolis', '(21) 99472-1933', 'Terça', 'Noite', -22.5118237, -43.1714001, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('fdc92dec-5d5b-4794-885f-dd5cda44c658', 'Grupo Iracema', 'presencial', '', 'Quinta', '19:30h às 21:30h', 'Av. Almirante Barroso, 949, Fortaleza - CE - Praia de Iracema - Fortaleza', '(85) 98929-5529', 'Quinta', 'Noite', -3.7199542, -38.5144095, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('2dedf82e-97b2-402d-ab53-008f04f78a7e', 'Grupo Paraíba', 'presencial', '', 'Segunda', '19:30h', 'R. Mal. Almeida Barreto, 222 - Centro, João Pessoa - PB - Centro - João Pessoa', '(83) 98872-6572', 'Segunda', 'Noite', -7.123421, -34.8825664, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('a88c7cf6-1f7f-4eab-9e63-ac24cd3368ab', 'Grupo Belém', 'presencial', '', 'Domingo', '9h às 11h', 'Av. Ceará, 595 - Bairro Canudos, Belém - PA - Canudos - Belém', '(91) 99603-0369', 'Domingo', 'Manhã', -1.4473735, -48.4597378, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('6b3ae6e1-5c27-4615-9745-b93781116c93', 'Grupo Esperança', 'presencial', '', 'Terça', '19h', 'Av. Farrapos, 2611, Porto Alegre - RS - Porto Alegre', '(51) 98030-2002', 'Terça', 'Noite', -30.0098885, -51.20197, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('b304083b-17a2-4423-985d-0479b72a869e', 'Grupo Iracema', 'presencial', '', 'Sábado', '10h às 12h', 'Av. Almirante Barroso, 949, Fortaleza - CE - Praia de Iracema - Fortaleza', '(85) 98929-5529', 'Sábado', 'Manhã', -3.7199542, -38.5144095, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('56c921c7-e91b-4452-a517-fae5c1553e82', 'Grupo Limeira', 'presencial', '', 'Segunda', '19:30h às 21:30h', 'R. Oscar Buzolin, 1-63 - Paróquia N. Sra. do Rosário, Limeira - SP - Limeira', '(21) 99472-1933', 'Segunda', 'Noite', -22.5337959, -47.3154085, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('ef2aff43-4e3e-4f68-b4c7-80646ba37a37', 'Grupo Copacabana', 'presencial', '', 'Quinta', '18h', 'R. Francisco Otaviano, 99 - Ipanema, Rio de Janeiro - RJ - Ipanema - Rio de Janeiro', '(21) 99472-1933', 'Quinta', 'Noite', -22.9874144, -43.1916282, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('47cf0518-9b3a-4afe-bf78-cd5bf03e3220', 'Grupo Pioneiro', 'presencial', '', 'Segunda', '20h', 'R. Guaporé, 335, São Paulo - SP - São Paulo', '(21) 99472-1933', 'Segunda', 'Noite', -23.5233379, -46.6270746, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('5096312f-2c57-4bc2-bd5d-518161ba4012', 'Grupo São Bernardo do Campo', 'presencial', '', 'Quinta', '19:30h', 'Estr. dos Casa, 3230, São Bernardo do Campo - SP - São Bernardo do Campo', '(21) 99472-1933', 'Quinta', 'Noite', -23.7411989, -46.5717426, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('b9a4f45d-6142-4aa8-b518-5fa3f94652d1', 'Grupo Buscando a Serenidade', 'presencial', '', 'Quarta', '20h', 'R. Pedreira de Magalhães, 100, São Paulo - SP - São Paulo', '(21) 99472-1933', 'Quarta', 'Noite', -23.5430499, -46.4832799, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('c7e5346b-a844-4aa2-ab10-02d3acfa6ef9', 'Grupo Cuiabá', 'presencial', '', 'Quarta', '19:30h (inaugural desde 29/07/26)', 'Av. Des. Antônio Quirinoi de Araújo, 1126 - Poção, Cuiabá - MT - Poção - Cuiabá', '(21) 99472-1933', 'Quarta', 'Noite', -15.6073364, -56.087237, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('b534b2bd-752b-469b-ab0f-cfb61368e0df', 'Grupo Maceió', 'presencial', '', 'Quarta', '19h às 21h', 'Fernandes Lima, 869 - Farol, Maceió - AL - Farol - Maceió', '(82) 99620-2998', 'Quarta', 'Noite', -9.6431402, -35.7351786, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('88925e2c-a226-4afb-886a-5b79ddc5528a', 'Grupo Santa Ifigênia', 'presencial', '', 'Domingo', '9h', 'Largo de Santa Ifigênia, 30, São Paulo - SP - Santa Ifigênia - São Paulo', '(21) 99472-1933', 'Domingo', 'Manhã', -23.5436919, -46.6345542, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('99527b19-454e-4d78-aebe-8f773fe26a52', 'Grupo Acre-RJ', 'presencial', '', 'Sábado', '10h', 'Rio de Janeiro, RJ - Rio de Janeiro', '(21) 99472-1933', 'Sábado', 'Manhã', -22.8991875, -43.1812619, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('a8ef7ad7-0211-456e-9fd4-74c51b5af443', 'Grupo Maceió', 'presencial', '', 'Sábado', '16h às 18h', 'Fernandes Lima, 869 - Farol, Maceió - AL - Farol - Maceió', '(82) 99620-2998', 'Sábado', 'Tarde', -9.6431402, -35.7351786, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('a0871e7a-2000-47b2-b3f6-f6bf43aad87b', 'Grupo Paraíba', 'presencial', '', 'Quarta', '19:30h', 'R. Mal. Almeida Barreto, 222 - Centro, João Pessoa - PB - Centro - João Pessoa', '(83) 98872-6572', 'Quarta', 'Noite', -7.123421, -34.8825664, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('083f45ee-44ad-461c-ba47-0a916cbad66e', 'Grupo Nova Chance', 'presencial', '', 'Sábado', '16h', 'Av. João Dias, 822 - biblioteca, São Paulo - SP - São Paulo', '(21) 99472-1933', 'Sábado', 'Tarde', -23.6461954, -46.7084935, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('7c91a1ec-5f89-4c79-99e0-9cc5e9156ccf', 'Grupo São José dos Campos', 'presencial', '', 'Sábado', '16h (desde 16/11/24)', 'R. Bertioga, 191, São José dos Campos - SP - São José dos Campos', '(21) 99472-1933', 'Sábado', 'Tarde', -23.2348057, -45.8824099, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('9b4585cb-778e-44a2-817b-b7ce76a7e952', 'Grupo Teresina', 'presencial', '', 'Terça', '19h às 21h', 'BR 316, 7.200 - Parque Piauí, Teresina - PI - Parque Piauí - Teresina', '(21) 99472-1933', 'Terça', 'Noite', -5.143238, -42.7797881, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('60f3f57d-fc3e-4239-a4cc-d42864506562', 'Grupo Pilares', 'presencial', '', 'Terça', '18h', 'Paróquia São Benedito, Rio de Janeiro - RJ - Pilares - Rio de Janeiro', '(21) 99472-1933', 'Terça', 'Noite', -22.8820057, -43.293602, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('2e4a4e71-03be-40f0-bae7-3e5e5456d5f2', 'Grupo Acre-RJ', 'presencial', '', 'Quinta', '18h', 'Rio de Janeiro, RJ - Rio de Janeiro', '(21) 99472-1933', 'Quinta', 'Noite', -22.8991875, -43.1812619, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reuniao (id, nome, categoria, descricao, dia, horario, local, whatsapp, dia_semana, periodo, latitude, longitude, criado_em, atualizado_em) VALUES ('7c4cf21d-8ba7-403a-b610-fc1e8ab13ae4', 'Grupo Taubaté', 'presencial', '', 'Quarta', '19h às 21h', 'Av. Maria Teresa de Moura, 150, Taubaté - SP - Taubaté', '(21) 99472-1933', 'Quarta', 'Noite', -23.0171067, -45.5769342, '2026-07-17 20:24:47.174502+00', '2026-07-17 20:24:47.174502+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('9da4fce4-3494-4bc3-86c5-58f31b000ed6', 2, 24, 'Posso expulsar o medo através da consciência da verdade. Temo ficar sozinho? Este medo pode ser eliminado se eu tiver consciência de que nunca estarei sozinho, que Deus sempre estará comigo aonde quer que eu vá, e em qualquer coisa que eu faça. Tenho medo de não ter dinheiro suficiente para suprir minhas necessidades? Este medo pode ser eliminado através da consciência de que Deus é meu recurso inesgotável e infalível, agora e sempre.

Hoje tenho a capacidade de transformar o medo em fé. Posso dizer com segurança – Eu confiarei e não terei medo?

Hoje eu Rezo
Que eu não tema qualquer mal porque Deus está comigo. Que eu aprenda a recorrer ao meu Poder Superior quando sentir medo. Peço veementemente que minha fé em Deus – e confiança no que Ele reserva para mim – seja forte o suficiente para eliminar os medos que minam minha coragem.

Hoje Eu Me Lembrarei
Transforme o medo em fé.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('71f8978a-cfcd-45ca-824f-d77cf7e64c27', 3, 5, 'Antes de entrar para Jogadores Anônimos, eu jogava a culpa de meus problemas em outras pessoas, lugares e coisas. Agora estou aprendendo a olhar objetivamente cada dificuldade, sem procurar alguém para ”culpar”, mas sim  descobrir como minha atitude ajudou a criar meu problema ou agravá-lo. Também preciso aprender a encarar as conseqüências de minhas próprias atitudes e palavras e me corrigir quando estiver errado.

Pratico o Décimo Passo, continuando a fazer meu inventário pessoal? Quando estou errado, aceito isso de imediato?

Hoje Eu Rezo
Que eu reconheça o alívio e a libertação abençoada que surgem quando admito que fiz algo errado. Que eu aprenda – talvez pela primeira vez em minha vida – a assumir responsabilidade pelos meus próprios atos e encarar as conseqüências. Que eu reaprenda a equilibrar atitudes com  conseqüências.

Hoje Eu Me Lembrarei
Assumir a responsabilidade de minhas próprias ações.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('5a017426-12c7-4ca7-96dd-f243fc1f1eb8', 2, 29, 'Só por hoje não terei medo de nada. Se minha mente estiver obscurecida por medos sem nome, eu os identificarei e os revelarei mostrando sua falta de realidade. Eu me lembrarei de que Deus cuida de mim e de minha vida, e que só preciso aceitar a Sua proteção e orientação. O que aconteceu ontem não precisa me atormentar hoje.

Aceito o fato de que tenho o poder de tornar o dia de hoje um bom dia, simplesmente pela forma como penso e ajo a respeito dele.

Hoje Eu Rezo
Que eu torne o dia de hoje um bom dia. Que eu saiba que depende de mim atribuir bons adjetivos a este dia, através de atitudes positivas em relação ao que o presente me proporciona. Que eu não fique perturbado pelos vestígios do ontem. Por favor, Deus, continue próximo de mim por todo este dia.

Hoje Eu Me Lembrarei
Fazer com que este dia seja bom.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('43feb45c-7b09-4faa-857d-447d72f06c1f', 4, 19, 'À medida que continuamos a fazer nossas escolhas vitais para realizar essas altas aspirações, recuperamos nossa realidade e nossas compulsões desaparecem. Aprendemos, nas palavras de Plutarco, que “Uma vida agradável e feliz não surge de coisas externas. O homem extrai o prazer e a alegria de dentro de si, como se os tirasse de uma fonte.

Estou aprendendo a “viajar de primeira classe” dentro de mim mesmo?

Hoje Eu Rezo
A graça de Deus mostrou-me como ser feliz novamente. Que a sabedoria de Deus me ensine que a fonte dessa felicidade está dentro de mim, em meus novos valores, em meu novo senso de auto-estima e em minha nova e aberta comunicação com o meu Poder Superior.

Hoje Eu Me Lembrarei
A felicidade vem de dentro.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('55e975e8-2822-4f6a-ae03-85e99ccd99cf', 7, 7, 'Quantas coisas maravilhosas poderiam acontecer em minha vida se eu pudesse me livrar do impulso natural de justificar minhas atitudes. A honestidade está assim tão reprimida dentro das camadas da culpa que eu nem posso liberá-la para entender os meus motivos? Ser honesto comigo mesmo não é fácil. É difícil entender por que eu tive este ou aquele impulso, e, mais importante, por que eu agi de determinada maneira. Nada nos torna tão vulneráveis quanto desistir da tentação do “álibi”, mesmo assim a minha disposição para ser vulnerável chegará ao ponto de me ajudar a crescer no Programa de Jogadores Anônimos.

Estou me tornando mais consciente de que a minha ilusão pessoal multiplicará  meus problemas?

Hoje Eu Rezo
Que Deus remova de mim a vontade de inventar desculpas. Ajudai-me a encarar as realidades que emergirem quando eu for honesto comigo mesmo. Ajudai-me a saber que, tão certo quanto o surgimento do dia após o nascer do sol, minhas dificuldades diminuirão se eu apenas acreditar na vontade de Deus.

Hoje Eu Me Lembrarei
Estarei disposto a realizar a vontade de Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('96be2731-0bc4-4a44-9bbf-6d3dc07edcca', 3, 4, 'É possível que não conheçamos os detalhes sobre as atividades do dia de hoje e que não saibamos se estaremos sozinhos ou na companhia de outras pessoas. Podemos achar que o dia tem tempo demais – ou não o suficiente. Podemos nos deparar com tarefas que estaremos ansiosos para completar ou tarefas que relutaremos em fazer. Embora os detalhes dos dias de cada pessoa sejam diferentes, cada dia apresenta uma semelhança para todos nós. Todos nós temos a chance de optar por ter pensamentos positivos. Esta escolha depende muito mais de nossas reflexões interiores do que de nossas atividades externas.

Consigo aceitar o fato de que somente eu tenho o poder de controlar minhas atitudes?

Hoje Eu Rezo
Que eu mantenha vivo meu compromisso interior durante todo este glorioso dia sem me importar se minhas atividades serão uma sucessão de tarefas monótonas ou variadas e criativas . Que eu escolha fazer deste, um bom dia para mim e para todos que me cercam.

Hoje Eu Me Lembrarei
Manter o compromisso', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('74f89dce-8078-4c85-808f-6d54f10298da', 5, 6, 'Muitos de nós sofrem desesperando-se. Mesmo assim, não nos conscientizamos de que o desespero é simplesmente a ausência da fé. Se estivermos dispostos a pedir a ajuda de Deus para nossas dificuldades, não precisaremos nos desesperar. Quando temos problemas e não conseguimos ver uma saída, isso somente ocorre porque imaginamos que todas as soluções dependem de nós. O Programa de Jogadores Anônimos nos ensina a deixar que esses problemas enormes tomem os seus cursos e deixar que Deus os solucione por nós.

Quando conscientemente rendo minha vontade à vontade de Deus, vejo a fé funcionando em minha vida?

Hoje Eu Rezo
Sendo uma pessoa em recuperação, devo conseguir me libertar do desespero e da depressão, duas palavras negativas iniciadas com a letra “D”, resultantes dos sentimentos de abandono. Que eu saiba que a ajuda de Deus nunca me faltará; que nunca ficarei desamparado quando Deus estiver ao meu lado. Se tiver fé, nunca estarei “desamparado e sem esperanças”.

Hoje Eu Me Lembrarei
O desespero é a ausência da fé.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('76317ec7-b6c9-4ad9-a1a5-09d631848f8b', 5, 29, 'Quando chegamos a Jogadores Anônimos, e também pela primeira vez em nossas vidas,  ficamos entre pessoas que pareciam entender o sentido de integração, foi emocionante. Achamos que o problema de isolamento havia sido resolvido. Entretanto, logo descobrimos, que apesar de não estarmos sozinhos no sentido social, ainda sofríamos muito dos antigos surtos de ansiedade pelo isolamento. Até que tivéssemos falado com sinceridade total de nossos conflitos e tivéssemos escutado outras pessoas fazendo a mesma coisa,  não nos sentíamos integrados. O Passo Cinco foi a resposta.

Descobri através do meu Passo Cinco o início da verdadeira comunhão com meus companheiros e Deus?

Hoje Eu Rezo
Que Deus me ajude a aprender a compartilhar comigo mesmo, minhas qualidades  e minhas fraquezas, não somente como eu interpreto o Quinto Passo mas em um contínuo processo de troca com meus amigos. Que eu cultive uma atitude de abertura mental e honestidade com os outros, agora que comecei a ser honesto comigo mesmo. Que eu me lembre de quem eu era – a  criança em um jogo de esconde-esconde, que se escondia tão bem que ninguém a encontrava e então todos desistiam de procurá-la e voltavam para casa.

Hoje Eu Me Lembrarei
Estarei aberto à amizade.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('60e16f95-2bdf-49b5-9d96-45df9e9ea250', 3, 23, 'Jogadores Anônimos nos ensina, através da experiência, força e esperança de sua fraternidade, que a pior situação imaginável não é motivo para um retorno ao jogo. Por pior que seja uma situação em particular, ou uma série de circunstâncias, o retorno aos antigos hábitos, mesmo que seja por apenas um minuto, certamente tornará a situação ainda pior.

Sou grato pelo cuidado e comunhão do Programa?

Hoje Eu Rezo
Que eu continue a acreditar que nenhuma pedra pode ser tão pesada,  que me leve de volta ao fundo do poço de minha compulsão. Nenhum problema, decepção, golpe no meu orgulho, ou perda do amor humano,  vale o preço da volta ao meu antigo modo de vida. Quando alimento os pensamentos de que a vida é “muito ruim” para mim, de que ninguém precisa suportar tanto e ainda assim permanecer são, ou de que sou o “derrotado”; faça com que eu preste atenção ao tom de minhas lamentações. Que eu me lembre de que ouvi essas mesmas lamentações anteriormente – antes de admitir que era impotente perante o jogo e me submeter à vontade de Deus. Essas lamentações me levam de volta ao jogo. Que Deus mantenha meus ouvidos abertos contra o tom de minhas próprias lamentações.

Hoje Eu Me Lembrarei
Ouvir as minhas próprias lamentações.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('cdba3eaa-e162-4102-a523-43efbc613f0f', 9, 14, 'Antes de chegarmos ao Programa de Jogadores Anônimos, vivíamos fugindo da dor e dos problemas. A fuga através do jogo compulsivo sempre foi nossa solução temporária. Então começamos a freqüentar as reuniões. Observávamos e ouvíamos, geralmente com espanto.Onde quer que olhássemos, víamos fracasso e sofrimento transformados em qualidades inestimáveis pela humildade. Para os que progrediram no Programa de Recuperação de JA, a humildade é simplesmente o reconhecimento claro do que e de quem geralmente somos – seguida de um  esforço sincero de nos tornarmos o que podemos ser.

O Programa de JA está me mostrando o que eu posso ser?

Hoje Eu Rezo
Peço para que eu tenha humildade, que é um dos significados da palavra perspectiva – a visão do meu verdadeiro eu e de minha posição em relação a Deus e às outras pessoas. Que eu seja grato pela humildade, essa planta transformadora através da qual minhas dores e desilusões são refinadas, transformando-se em coragem e sensibilidade renovadas.

Hoje Eu Me Lembrarei
A humildade restaura minha “visão”.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('4df7c085-5dcd-4838-8647-6a46f762cdfd', 3, 16, 'Jogadores Anônimos nos ensina que somos emocional e mentalmente diferentes de nossos semelhantes. Somos lembrados de que a grande obsessão de todo jogador compulsivo é provar, de alguma forma, que algum dia, seremos capazes de controlar nosso jogo. A persistência dessa ilusão é surpreendente, e muitos a perseguem até os limites da prisão, insanidade ou morte.

Admiti ao meu eu interior que uma aposta é demais e mil não são suficientes?

Hoje Eu Rezo
Que eu não tenha qualquer ilusão de que algum dia me tornarei um jogador controlado após ter sido um jogador compulsivo. Que eu abafe qualquer vestígio de voz que se manifeste, oriunda de meu  orgulho destruidor, dizendo-me que agora posso voltar a minha antiga adicção e controlá-la. Este é um programa sem volta e agradeço a Deus por isso.

Hoje Eu Me Lembrarei
Meu objetivo precisa ser a abstinência para toda a vida – um dia de cada vez.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('9d3546dc-ef18-4826-b0bf-76ef19e34043', 7, 9, 'Samuel Johnson escreveu: “&#8230; aquele que abriga tão pouco conhecimento da natureza humana a ponto de procurar a felicidade nas mudanças diferentes de suas próprias,  estará desperdiçando a sua vida em esforços infrutíferos e multiplicando as tristezas que pretende remover”. Hoje eu acredito que não sou aquela pessoa horrível que pensava que era, apenas entendi que cometi erros na minha vida que causaram muita dor e tristeza a mim mesmo e àqueles a quem amo.. Mudando a mim mesmo, hoje eu posso encarar o meu passado inconseqüente como um aprendizado. Espero que meus entes queridos aprendam a respeitar as escolhas saudáveis que hoje estou fazendo, ao invés de se apegarem a todas as escolhas erradas que fiz no passado.

A Oração da Serenidade ensinou-me a esforçar-me para mudar somente as coisas que posso – principalmente as coisas sobre mim mesmo?

Hoje  Eu Rezo
Ajude-me a entender que devo procurar as respostas para as mudanças em mim mesmo. Que eu escolha as coisas que me trarão felicidade e serenidade, e que eu evite as coisas que me trarão dor e incertezas. Se eu permitir que o meu Poder Superior guie a minha vida, a mim será dado tudo que preciso para fazer as escolhas certas.

Hoje Eu Me Lembrarei
Com a ajuda de Deus, opto por modificar a mim mesmo.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('1895569d-6f8b-4679-960e-c9db070b49fe', 9, 3, 'Algumas vezes, mesmo quando nossos amigos no Programa de Jogadores Anônimos ou fora dele, nos dizem que estamos indo bem, no fundo sabemos que não estamos tão bem assim. Ainda temos dificuldade em lidar com a vida e encarar a realidade em seus termos. Nessas horas, suspeitamos que deve haver um dano sério em nosso desenvolvimento e prática espiritual. São grandes as chances de que nossos problemas residam no pouco entendimento ou na negligência do Passo Onze – oração, meditação e orientação de Deus. Os outros Passos podem manter a maioria de nós limpos e atuando, mas o  Passo Onze pode nos manter em crescimento – à medida que  tentamos com afinco e trabalhamos o Passo continuamente.

Confio mais no Deus infinito do que no “eu” limitado?

Hoje Eu Rezo
Rezo por um aprofundamento em meu conhecimento espiritual, por uma fé mais forte no Invisível, por uma comunhão mais próxima com meu Poder Superior. Que eu me dê conta de que meu crescimento no Programa de JA depende de meu desenvolvimento espiritual. Devo me dedicar mais à minha crença na eterna sabedoria de Deus.

Hoje Eu Me Lembrarei
Não cederei ou desistirei; ao invés disso me renderei ao poder de Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('68157fbc-01c1-4519-ab6e-76b47d712ecf', 8, 20, 'Todos os Doze Passos nos pedem para controlarmos nossas tendências e desejos naturais: todos esses Passos espetam, espremem e finalmente desinflam nossos egos. Quando se trata de esvaziar o ego,  poucos Passos são mais difíceis do que o Quinto Passo, que sugere que “admitamos a nós mesmos e a outro ser humano a natureza exata de nossas falhas”. É verdade que poucos Passos serão tão difíceis de fazer quanto esse, mas dificilmente qualquer outro Passo será tão necessário para que se alcançe uma liberdade duradoura longe da adicção do jogo.

Deixei de viver sozinho com os fantasmas atormentadores do passado?

Hoje Eu Rezo
Que Deus me proporcione força para encarar o  grande esmagador de egos – o Quinto Passo. Que eu não hesite em chamar um ouvinte confiável para o Quinto Passo, peça uma reunião e compartilhe este Passo.. Ao aceitar a responsabilidade de meu comportamento e depois compartilhar meu relato do mesmo com outra pessoa, estou na verdade tirando o peso de minhas costas.

Hoje Eu Me Lembrarei
A dor de meu Quinto Passo também significa minha liberdade.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('13463ed6-16f9-4e08-bd0c-e6bced98746e', 11, 15, 'Quando eu ainda era um recém-chegado em Jogadores Anônimos, disseram-me que a aceitação de minha impotência perante o jogo seria meu primeiro passo em direção a libertação de seu controle mortal; logo passei a acreditar na verdade desse fato. Para que isso ocorresse, a aceitação era uma extrema necessidade. Entretanto, para mim, isso representava somente um pequeno início para adquirir humildade. Em Jogadores Anônimos, aprendi que estar disposto a trabalhar em busca da humildade – como alguma coisa que deve ser almejada por si só – exige da maioria de nós um tempo muito longo.

Acredito que uma vida toda de egocentrismo não pode ser virada ao contrário em uma fração de segundo?

Hoje Eu Rezo
Que eu procure por minha própria humildade considerando-a como a qualidade que preciso cultivar para minha sobrevivência, e não somente como a aceitação de minha impotência perante o jogo compulsivo. O Passo Um é exatamente isso – o primeiro passo em direção à uma atitude de humildade, Que eu seja bastante realista para saber que isso pode levar metade de uma vida.

Hoje Eu Me Lembrarei
O orgulho a destruiu; dê uma chance à humildade.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('85e8ac26-69a3-40c9-b797-dc4e633fcab7', 10, 19, 'Há um número ilimitado de maneiras pelas quais se pode medir meu progresso e crescimento no Programa de Jogadores Anônimos. Uma das mais importantes  é minha consciência de que não mais me sinto compelido, de maneira quase obsessiva, a sair julgando tudo e todos. Minha única tarefa agora é fazer minha mudança, ao invés de tentar mudar outras pessoas, lugares e coisas. À sua maneira, a obsessão de ser sempre o crítico era um peso para mim da mesma forma que era  minha obsessão pelo meu jogo. Sou agradecido por ter conseguido  tirar esses dois pesos de meus ombros.

Quando me tornar crítico, irei me lembrar de que estou invadindo o território de Deus?

Hoje eu Rezo
Perdoe minhas transgressões, quando me transformei no auto-proclamado juiz e jurado de meus companheiros. Quando sou crítico, ultrapasso os direitos dos outros de julgar a si mesmos – e os direitos de Deus na Corte mais Suprema de todas. Que eu jogue fora todas as minhas ferramentas de crítica – minha própria régua e fita métrica, minhas próprias comparações, meus padrões inacessíveis – e aceite cada pessoa como um indivíduo acima de qualquer comparações.

Hoje Eu Me Lembrarei
Jogue fora os antigos instrumentos de medição – principalmente as fitas métricas.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('8bcebd97-6e0a-4c91-909f-81cffd6d8304', 12, 19, 'O Programa de Jogadores Anônimos me ensina a buscar progresso e não a perfeição. A simples lembrança disso me dá um grande conforto pois ela essencialmente mostra de que maneira minha vida hoje é tão diferente da passada. Em minha antiga vida – a perfeição, com toda a sua impossibilidade – era com muita freqüência o meu objetivo número um. Hoje consigo acreditar que se algumas vezes fracassar, não serei um fracassado – e se algumas vezes cometer erros, não serei eu próprio um erro. Posso aplicar essas mesmas idéias tanto para os Doze Passos de Recuperação quanto para a minha vida em geral.

Acredito que somente posso praticar com perfeição o Primeiro Passo, e que os Passos restantes representam ideais perfeitos?

Hoje Eu Rezo
Deus,ensine-me a deixar meus antigos objetivos de perfeição sobre humana em tudo o que eu fazia e dizia. Agora sei que na verdade estava fadado ao fracasso porque nunca poderia atingir aqueles alvos impossíveis que havia estabelecido para mim mesmo. Agora que entendo esse padrão não preciso mais programar meus próprios fracassos.

Hoje Eu Me Lembrarei
Devo me esforçar para ser uma pessoa melhor ao invés de uma super-pessoa.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('4aefa90f-2bf5-4889-8eda-5b2ed252f7c5', 6, 24, 'O propósito primordial do Programa de Jogadores Anônimos é a libertação do jogo compulsivo; sem essa libertação, não temos nada. Mas isso não significa que eu possa dizer, por exemplo: - O jogo compulsivo é minha única preocupação. Sem ele sou realmente uma pessoa excelente, assim, conceda-me a libertação do jogo e eu estarei bem. Se me enganar com tal afirmação sem sentido,  terei muito pouco progresso nos problemas e responsabilidades de minha vida real e provavelmente retornarei à minha adicção. Essa é a razão porque os Doze Passos do Programa nos pede para “praticar esses princípios em todas as nossas atividades.”

Estou vivendo simplesmente para me livrar da compulsão do jogo, ou também para aprender, servir e amar?

Hoje Eu Rezo
Que eu usufrua e seja agradecido à minha abstinência, que se encontra onde as boas coisas começam. Mas não permita que eu me atenha a isso e pare de tentar compreender a mim mesmo, a natureza de Deus e a humanidade. A libertação da adicção é a primeira libertação. Que eu tenha a convicção de que há mais por vir – a libertação da mente fechada, do sofrimento dos sentimentos guardados, da dependência exagerada às outras pessoas, da existência sem Deus. Que o Programa que atendeu às minhas necessidades mais intensas, também atenda às crônicas.

Hoje Eu Me Lembrarei
A libertação do jogo é só o começo.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('0b419107-d6c2-48b5-83ea-9232dfc761d2', 8, 26, 'Qual é a causa do jogo compulsivo? Jogamos por causa de algum defeito pessoal,  falha ou situação de vida? O livro Novo Começo de Jogadores Anônimos diz: “Não sabemos e &#8230; não podemos nos dar ao luxo de nos importarmos com isso. Aqueles que chegam a nós precisam de ajuda imediatamente &#8230; Os benefícios da busca interna e da especulação são insignificantes quando comparados com as recompensas que recebemos por ajudarmos outras pessoas a recuperarem suas vidas.” Embora todos queiramos nos livrar de nossos defeitos de caráter, ao simplesmente compartilharmos nossas histórias honestamente com outros no Programa, algumas de nossas falhas virão à  tona. Descobrimos,  freqüentemente, através da graça de Deus e ajudando nossos  companheiros da Irmandade, que defeitos tais como o egocentrismo e a insensibilidade são suprimidos.

Descobri que partilhar com os outros no Programa me ajuda a conhecer a mim mesmo?

Hoje Eu Rezo
Em Jogadores Anônimos, quando faço minha busca interna, não devo ficar preocupado demais com meus defeitos a ponto de me tornar incapaz de me aproximar dos outros. Que  meus defeitos, que se tornaram tão exagerados em meus dias de ativa como jogador compulsivo, sejam revelados suavemente ao longo do tempo e à medida que continuo a trabalhar o Programa com convicção e sinceridade.

Hoje Eu Me Lembrarei
O compartilhar torna meus defeitos menos flagrantes.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('90ef3a5c-fe0e-48d4-a059-696476bb4963', 9, 7, 'Dizem que “Se você não se sente bem como é, é preciso muito esforço para melhorar. Ache que você já está bem e você melhorará sem perceber.” Às vezes nos encontramos em situações tão difíceis que nos parecem impossíveis de serem resolvidas. Quanto mais pensamos sobre isso, mais nos achamos incapazes de superar essa situação – e nos entregamos à depressão. Nesse momento, convém relembrar alguma frase, ditado ou dizeres filosóficos, repetindo-os até que eles substituam os pensamentos tão atormentadores.

Esqueço-me, às vezes,  de que nas roseiras há mais do que somente  espinhos?

Hoje Eu Rezo
Que eu veja que Deus nos dá padrões para que aceitemos os opostos – o  dia se segue à noite; o silêncio ao ruídos; o amor à solidão; a alegria ao sofrimento. Se não conseguir fazer isso, que eu esteja consciente  e tente fazer alguma coisa construtiva. Se continuar insensível, que meus amigos me questionem para que eu me torne mais sensível.

Hoje Eu Me Lembrarei
As nuvens têm contornos e limites. Os problemas têm fim.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('9cdb3e26-e239-4b4a-8eba-e8278dab4b22', 2, 19, 'Quando alguém diz coisas duras ou desagradáveis, algumas vezes dizemos que essa pessoa está “esquecendo de si mesma”, no sentido de estar esquecendo de dar o melhor de si, num momento de explosão momentânea de fúria descontrolada. Se eu me lembrar da pessoa que quero ser, é possível que eu não “esqueça de mim mesmo”, deixando-me levar por um acesso de fúria. Acreditarei que o positivo sempre derrota o negativo: a coragem se sobrepõe ao medo, a paciência à raiva e à irritação e o amor  ao ódio.

Estou sempre me esforçando para melhorar?

Hoje Eu Rezo
Hoje eu peço a Deus, a quem tudo é possível, que me ajude a transformar o negativo em positivo – a raiva em grande energia, o medo na oportunidade de ser corajoso, o ódio em amor. Que eu tire um tempo para me lembrar dos exemplos dessas transformações de negativo em positivo em toda a minha vida. Acima de tudo está o milagre de Deus: minha libertação da escravidão de minha adicção ao jogo.

Hoje Eu Me Lembrarei
Transforme os negativos em positivos.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('8262eef8-3f90-465e-aa0f-241f9725b164', 8, 25, 'Sem a libertação do jogo compulsivo, não conseguimos nada. Mesmo assim não podemos nos libertar de nossa obsessão pelo jogo até que nos tornemos dispostos a lidar com os defeitos de caráter que nos colocaram de joelhos. Se nos recusarmos a trabalhar nossos flagrantes defeitos, é quase certo que retornaremos à nossa adicção. Se ficarmos em abstinência com um mínimo de crescimento pessoal, talvez, durante algum tempo,  nos acomodemos a um tipo de limbo confortável, mas perigoso. Melhor ainda seria se trabalhássemos continuamente os Passos, pois certamente alcançaríamos uma libertação verdadeira e duradoura com a ajuda de Deus.

Estou caminhando com a certeza de que finalmente estou no caminho certo?

Hoje Eu Rezo
Que Deus me mostre que a libertação da adicção do jogo é um estado inseguro se eu também não me livrar de minhas compulsões. Que Deus me impeça de me dedicar apenas parcialmente ao Programa conscientizando-me de que não posso tornar-me espiritualmente completo, se ainda estiver dilacerado por minha desonestidade e egoísmo.

Hoje Eu Me Lembrarei
Dedicando-me parcialmente, não me tornarei completo.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('bc185a65-b22f-4f4a-af65-581f13ef3fe8', 11, 13, 'Pode-se resumir e medir todo o progresso com somente duas palavras: humildade e responsabilidade. Comenta-se que  todo o nosso desenvolvimento espiritual pode ser medido com precisão pelo índice de adesão a esses padrões. Somente abandonando meu egocentrismo e mantendo contato com um Poder Superior poderei alcançar a verdadeira humildade. Só poderei desenvolver o senso de responsabilidade quando recuperar o contato com a realidade.

Estou tentando viver, com a maior honestidade possível, pelos padrões da humildade e da responsabilidade?

Hoje eu Rezo
Oro para que  eu me lembre principalmente de duas palavras, dentre todas as boas palavras, frases chamativas e momentos de inspiração que me venham à cabeça: humildade e responsabilidade. Estas talvez sejam as palavras mais difíceis de serem lembradas – humildade, porque ela significa expulsar meu orgulho; responsabilidade, porque tenho o hábito de usar meu vício do jogo como uma desculpa pobre para me livrar das obrigações. Oro para que eu possa quebrar esses antigos padrões.

Hoje Eu Me Lembrarei
Primeiro a humildade, e depois a responsabilidade.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('dbc099a2-c3ea-4996-85aa-f45668104a4d', 10, 24, 'Dag Hammerskjold escreveu que “você não pode brincar com o animal que se encontra dentro de você, sem  transformar-se completamente em um animal; brincar com a falsidade sem privar-se de seu direito à verdade; brincar com a crueldade sem perder sua sensibilidade. Aquele que desejar ter o jardim perfeito não poderá deixar um cantinho para as ervas daninhas”. Se eu quiser que meu jardim fique livre das ervas daninhas, preciso ter o cuidado de não deixar espaço para que as mesmas ervas cresçam, e eu seja apanhado de surpresa. Se me colocar em situações em que posso ser tentado, seguir acompanhando os resultados de jogo, verificar os prêmios da loteria, ou mesmo escutar meu antigos amigos falarem, posso estar reservando um pedaço de terra para as ervas daninhas crescerem.

Sei  agora,  que se de repente, eu receber algum dinheiro sem ter responsabilidade para lidar com ele, somente ervas daninhas surgirão dessa situação?

Hoje Eu Rezo
Que eu passe mais tempo tirando as ervas daninhas, fertilizando e expandindo os terrenos que possibilitam colheitas de felicidade, alegria, paz e serenidade, ao invés de deixar espaço de terra para que ali brotem a miséria, a dor e o sofrimento. Que eu não me esqueça de que essas ervas daninhas sempre estarão prontas para crescer a não ser que eu seja um jardineiro atento.

Hoje Eu Me Lembrarei
Colhemos o que semeamos.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('b1188761-d79e-4994-874b-dbb8c2907dda', 4, 8, 'Cada vez mais aprendemos em Jogadores Anônimos que os recém-chegados tentam guardar para si as “piores coisas” de suas vidas. Ao  tentar evitar vivenciar a experiência da humildade do Quinto Passo, eles optam por uma solução aparentemente mais fácil e menos sofrida. Quase que invariavelmente, acabam tendo recaídas. Ao continuarem com o resto do programa, se  perguntam por que recaíram. A provável razão é que raramente completam a limpeza da casa. Conseguem fazer o inventário de maneira satisfatória, mas ficam presos a algumas das piores partes que guardam dentro de si.

Admiti a mim mesmo e a outro ser humano a natureza exata de meus erros?

Hoje Eu Rezo
Que eu possa incluir todos os deslizes  de meu passado, minhas crueldades e minhas desonestidades, em um completo inventário moral e financeiro de mim mesmo. Que eu não esconda nada por vergonha ou por orgulho, porque a “natureza exata” de meus erros pode simplesmente representar isso – a história exata e detalhada de meus erros passados e defeitos de caráter. Recebemos a graça desse “depósito de lixo”. Que eu o use da maneira correta. Que todo o lixo jogado fora e as fachadas ultrapassadas do passado, funcionem como uma fundação sólida sobre a qual uma nova vida será construída.

Hoje Eu Me Lembrarei
O lixo pode se transformar em base para os tesouros.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('9c567007-25ea-4e50-9c02-7aa971afce09', 6, 19, 'O Programa de Jogadores Anônimos nos ensina que, deveríamos ter em mente somente uma ponderação que justificasse a nossa decisão  de não revelar todo o mal que causamos. Isto seria o caso de uma completa revelação prejudicar seriamente a pessoa a quem desejássemos fazer as reparações – ou ainda com a mesma importância, as outras pessoas próximas a essa mesma pessoa. Dificilmente, por exemplo, poderíamos fazer um relato detalhado de  nossas  erradas aventuras extra-conjugais, jogando esse peso nas costa dos cônjuge que confiava  em nós. Quando, irresponsavelmente, aumentamos a carga dos outros, tais ações certamente não poderão aliviar nosso próprio peso. Assim, algumas vezes, “contar tudo” pode ser quase uma auto-indulgência. Ao fazer reparações, deveríamos ser cuidadosos, sensatos, atenciosos e humildes – sem nos tornarmos servis.

Como filho de Deus, permaneço de pé, não rastejando perante ninguém?

Hoje Eu Rezo
Que Deus me mostre, que quando fazemos reparações, não deve existir nem ódio por nós mesmos, nem a pretensão da auto-indulgência. Peço, muito humildemente, pela orientação de meu Poder Superior à medida que luto para manter um equilíbrio amadurecido nas relações inter-pessoais, mesmo nas mais casuais ou frágeis.

Hoje Eu Me Lembrarei
Fazer reparações é reparar.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('64ed0ba3-74ba-44a6-83de-f14722c78b0c', 12, 24, 'Chegamos a Jogadores Anônimos como suplicantes, literalmente, quando já não tínhamos mais a quem recorrer. Cedo ou tarde, através da prática dos Doze Passos de JA descobrimos, dentro de nós mesmos, uma coisa muito valiosa. Desvendamos uma  serenidade que nos deixa à vontade em todos os lugares e situações. Conseguimos força e crescimento – com a ajuda de Deus como O entendemos, com o Programa da Irmandade de Jogadores Anônimos e através da aplicação dos Doze Passos de Recuperação em nossas vidas.

Alguém poderá tirar minha nova vida de mim?

Hoje Eu Rezo
Que minhas orações de súplica desesperada, que eu trouxe comigo quando ingressei no Programa de Jogadores Anônimos, se transformem em rendição serena. Agora que sei o que pode ser alcançado através do Programa e da capacidade infinita de um Poder Superior, transmitirei aos outros esta dádiva da convicção total. Rezo, para que aqueles a quem amo, tenham fé para descobrirem suas próprias experiências espirituais e a bençãos da paz.

Hoje Eu Me Lembrarei
A Paz – interior e exterior – é a maior benção.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('084117f2-0d40-4734-a777-1dc57228d626', 7, 14, 'Da maneira como somos condicionados às antigas idéias e antigos modos de pensar, é natural que tenhamos a tendência de resistir a certas sugestões que nos são feitas, quando ingressamos no Programa de Jogadores Anônimos. Se este for o caso, não há necessidade de rejeitarmos estas sugestões  permanentemente: descobrimos que é melhor deixá-las de lado por um tempo. O ponto principal, é que não existe uma “maneira certa” ou “errada” que funcione imediatamente e rapidamente. Cada um de nós escolhe o que é melhor para si em uma determinada hora, mas mantendo uma mente aberta sobre outras formas de ajuda que poderíamos achar válidas em outro momento..

Estou tentando manter a mente aberta?

Hoje Eu Rezo
Que eu me esclareça sobre o verdadeiro significado da mente aberta, sabendo que a definição “mente aberta” não significa “aceitar tudo”. Que eu mantenha sempre a minha mente aberta às sugestões das muitas pessoas sensatas que chegaram ao Programa antes de mim. O que funcionou para eles pode funcionar para mim, não importando o quanto isso pareça irreal ou óbvio.

Hoje Eu Me Lembrarei
Apenas uma mente aberta pode ser recuperada.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('b5fa20a4-a411-4459-a80d-4dbda1122b87', 9, 9, 'Quanto mais tempo estou no Programa de Jogadores Anõnimos mais eu tento praticar seus princípios em todas as áreas de minha vida, e com menor freqüência, fico desanimado ou deprimido. Talvez, também, haja alguma relação com o antigo ditado irônico que diz que “Abençoados sejam aqueles que não têm expectativas na vida, pois assim não se desapontarão, ficando diariamente satisfeitos com as novas e recentes evidências do amor de Deus e da amizade entre os homens e mulheres.”

Hoje existe alguém, em algum lugar, que precisa de mim? Procurarei essa pessoa e tentarei compartilhar com ela o que recebi no Programa de JA?

Hoje Eu Rezo
Que eu seja completamente agradecido a Deus por diminuir minha depressão. Que eu saiba que minha depressão sempre diminuirá se eu não tiver expectativas grandes demais. Que eu saiba que o carinho que recebo de meus amigos podem preencher o vazio do desespero. Que eu dê esse carinho  a outro alguém.

Hoje Eu Me Lembrarei
Procurar alguém para compartilhar.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('607a1aea-bb49-42c9-9d96-2d979fe67ebc', 11, 21, 'Um poeta disse uma vez que a adversidade leva o homem a si mesmo. Isso também acontece comigo mesmo em relação a adversidades imaginadas. Se esperar que uma pessoa reaja de determinada forma em uma situação específica – e ela não atende minhas expectativas – então eu raramente terei o direito de ficar desapontado ou aborrecido. Mesmo assim, ocasionalmente, ainda sinto frustrações quando os outros não agem ou reagem da forma que espero que façam. Através dessas projeções – ou melhor ainda, adversidades criadas por mim mesmo, começo a encarar meu antigo eu, o mesmo que queria comandar todo o espetáculo.

Está finalmente na hora de parar com as expectativas e começar a aceitar?

Hoje Eu Rezo
Que eu pare de colocar palavras na boca das outras pessoas, programando-as – em minha mente – para reagir como espero que façam. Essas expectativas me enganaram antes: eu esperava amor ilimitado e proteção das pessoas mais íntimas, perfeição de mim mesmo, e atenção total dos relacionamentos superficiais. Vendo somente o lado negativo, eu esperava o meu próprio fracasso e a rejeição dos outros. Que eu pare de esperar problemas – ou até sucessos – do futuro.

Hoje Eu Me Lembrarei
Aceitar. Parar de antecipar.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('9b678d6d-c3fd-4182-abf5-07bfad829d71', 7, 3, 'Mudanças são partes do fluxo da vida. Às vezes nos frustramos porque as mudanças parecem lentas. Outras vezes, também, nos tornamos resistentes às mudanças que nos são impostas. Devemos nos lembrar que as mudanças em si não nos tolhem nem nos libertam. É apenas a nossa atitude em relação a elas que podem nos tolher ou nos libertar. À medida que vamos aprendendo a seguir com os fluxos da vida, pedindo por orientação quanto às mudanças que se apresentem a nós – pedindo também, por orientação,  quando queremos fazer uma mudança e nada parece ocorrer – é que nos tornamos dispostos.

Estou disposto a deixar que Deus tenha o controle, me guiando através das mudanças que eu devo fazer e nas decisões que eu devo tomar?

Hoje Eu Rezo
Quando as mudanças ocorrerem rápido demais – ou não tão rápido quanto eu queira – peço que eu me adapte a elas apropriadamente, para que eu possa fazer uso da liberdade que Jogadores Anônimos está me oferecendo. Rezo pela orientação de meu Poder Superior quando as mudanças ocorrerem – ou quando não ocorressem e eu quisesse que tivessem ocorrido. Que eu esteja atento às orientações desse Poder.

Hoje Eu Me Lembrarei
Deus está no comando.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('b81c79ae-916e-4877-932e-41aac107a3e0', 3, 1, 'Agora que somos livres e que não jogamos mais, temos muito mais controle sobre nossos pensamentos. Podemos mudar nossas atitudes, mais do que qualquer outra coisa. Na verdade, alguns membros de Jogadores Anônimos preferem pensar na sigla JA como uma abreviação para “Justa Atitude”. Nos dias passados e ruins, eu quase sempre respondia a qualquer afirmação otimista ou positiva com um – Sim, mas &#8230; Em contraste, hoje, estou aprendendo a eliminar essa frase negativa de meu vocabulário.

Estou fazendo esforços para mudar minha atitude? Estou determinado a dar “ênfase ao que é positivo”?

Hoje Eu Rezo
Que eu alcance a paz e a força que  Deus proporciona a quem está próximo Dele. Que eu siga as orientações do Programa de Jogadores Anônimos, analisando e aceitando os Passos – um a um –  e praticando-os repetidamente. É nisto que reside minha salvação.

Hoje Eu Me Lembrarei
Praticar pelo menos um Passo.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('9c2bdb16-db41-45e1-9aa4-be6831cab8ea', 8, 27, 'Ao olhar duramente para esses defeitos , estando disposto ou relutante em removê-los, eu deveria esquecer as linhas rígidas que tracei. Em alguns casos, talvez, serei então capaz de dizer: - Deste aqui eu ainda não posso me livrar &#8230; A única coisa que eu não deveria dizer é: - Deste aqui eu jamais me livrarei. No momento em que falamos, - Não, nunca - nossas mentes se fecham à graça de Deus. Tal  rebeldia, a mesma que vimos nas experiências de outras pessoas, pode tornar-se fatal. Deveríamos, ao invés disso, esquecer nossos objetivos limitados e começar a nos mover na direção da vontade de Deus para conosco.

Estou aprendendo a nunca dizer nunca?

Hoje Eu Rezo
Que Deus elimine todos os bloqueios desafiadores que fazem com que eu me negue a modificar minhas indesejáveis características. Em função da falsa noção de que sou “único” e “especial’, e de algum modo imune às conseqüências, confesso a Deus que tenho desafiado as leis naturais da saúde, da sanidade e as leis Divinas da bondade humana. Que Deus elimine a rebeldia que é um sintoma camuflado de minha adiccção.

Hoje Eu Me Lembrarei
A rebeldia é o resultado da ilusão.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('a8351f66-149e-4b93-8225-3812d66ee06f', 11, 5, 'Durante vários meses após ter chegado a Jogadores Anônimos, dei pouca atenção ao Passo Onze, à prática da meditação séria e da oração. Achava que ela poderia me ajudar a resolver alguma emergência – como uma súbita vontade de voltar ao jogo – mas ela continuava no nível mais baixo em minha lista de prioridades. Naqueles dias, eu colocava a oração e a meditação no mesmo nível dos mistérios e até da hipocrisia. A partir de então descobri que os resultados da oração e da meditação são mais recompensadores do que eu jamais poderia ter imaginado. Hoje, para mim, a colheita é incrivelmente abundante e continuo a adquirir paz de espírito e força muito além de minhas limitações humanas.

A minha antiga dor está sendo substituída pela tranqüilidade?

Hoje Eu Rezo
Que eu encontre minha melhor maneira de chegar a Deus, minha melhor técnica de meditação – não importa se usando um mantra oriental, o nome de Jesus Cristo, ou que simplesmente permita que o espírito de Deus, como eu O compreendo,  chegue a mim e me traga paz. Sejam quais forem os caminhos usados para se chegar a Deus, que eu aprenda a conhecê-LO bem e sentir a Sua presença – não somente nessas horas de tranqüilidade, mas em tudo o que eu faça.

Hoje Eu Me Lembrarei
Através da meditação me torno receptivo ao espírito de Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('6fa85797-cbba-4e59-81af-7d7d5d33dcfe', 3, 22, 'Uma vez, após muito tempo encontro-me pensando que talvez as coisas não estivessem tão ruins quanto pareciam. Em tais momentos, esforço-me para perceber que minha doença está falando comigo, tentando me fazer negar que sou realmente portador de uma doença. Um dos passos chaves do Programa de Jogadores Anônimos é aquele em que entregamos nossa doença a Deus como O entendemos, aceitando, nossa impotência na presença de Seu Poder superior.

Acredito que a graça de Deus pode fazer  o que eu jamais poderia fazer por mim mesmo?

Hoje Eu Rezo
Que eu reconheça que muito de nossas vidas depende da fé. Não podemos conhecer os limites do tempo e do espaço – ou explicar os mistérios da vida e da morte. Mas quando vemos Deus trabalhar através de nós, e de outros que descobriram uma nova vida no Programa de JA - esta é a evidência que precisamos para saber que Deus existe.

Hoje Eu Me Lembrarei
A grande roda gira graças á fé.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('73e6011f-9fc7-41cf-a801-d84d59dcb6c3', 4, 28, 'Tomarei a decisão de ver com  novo interesse até as coisas mais banais que acontecem hoje. Se eu aprender a ver tudo com um novo olhar, talvez descubra que tenho razões de sobra para me sentir grato e satisfeito. Quanto me encontrar preso na areia movediça de meus pensamentos negativos, me afastarei deles – e me prenderei à força salvadora da comunhão com os outros do Programa.

Reconheço a minha importância como um elo fundamental na corrente mundial de Jogadores Anônimos?

Hoje Eu Rezo
Rezo para que Deus abra meus olhos para as mínimas maravilhas do cotidiano. Que eu perceba e enumere entre as minhas bênçãos, coisas como simplesmente me sentir bem e ser capaz de pensar com clareza. Mesmo quando faço uma simples escolha, como gastar meu tempo de folga  praticando um esporte, ir a um concerto, ou me reunir com amigos abstinentes, que eu me lembre de que o poder de escolha é uma dádiva de Deus.

Hoje Eu Me Lembrarei
Sou abençoado com a liberdade de escolha.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('b940b449-7dd3-4345-8784-0453b8e7f414', 1, 29, 'Costumava imaginar minha vida como uma grotesca pintura abstrata: uma montagem de crises emolduradas por consecutivas catástrofes. Todos os meus dias eram cinzas e meus pensamentos ainda mais. Eu era assombrado por medos terríveis e sem nome. Minha cabeça estava cheia de auto-acusações. Não tinha idéia de quem eu era, o que eu era ou por que eu era. Não sinto falta de nenhuma dessas sensações. Hoje, passo a passo, estou me descobrindo e aprendendo que posso ser livre para ser eu mesmo.

Sou grato por minha nova vida? Tirei uma hora para agradecer a Deus por estar limpo e vivo?

Hoje Eu Rezo
Que a calmaria chegue até mim, após as perturbações e pesadelos do passado. À medida que meus medos e o ódio de mim mesmo se dissipam, que as coisas do espírito os substituam.  Tanto no mundo espiritual, quanto no mundo material não há espaços vazios. Que o espírito de meu Poder Superior me preencha.

Hoje Eu Me Lembrarei
Uma manhã ensolarada dissipa os pesadelos.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('6271a73c-27bc-4129-8213-0292ec411f2f', 4, 30, 'Aprendemos que a “fé sem esforço está morta”. Isso se aplica bem ao jogador compulsivo. Assim, se não conseguirmos aperfeiçoar ou melhorar nossas vidas espirituais através do trabalho e do sacrifício pessoal pelos outros, não nos preparamos para resistir às tentações e percalços que encontraremos. Se não praticarmos o Programa de Jogadores Anônimos, certamente voltaremos jogar; e se voltarmos a jogar, provavelmente morreremos. Então a fé de fato morrerá.

Acredito, através da fé, que posso ser especialmente útil aos que ainda sofrem?

Hoje Eu Rezo
Que minha fé no Poder Superior e na influência de  JA seja em mim multiplicada quando a transmito àqueles que estão superando a compulsão pelo jogo. Que eu tenha certeza de que ajudar os outros não significa simplesmente pagar as minhas dívidas; ela é a única maneira que conheço para continuar meu crescimento espiritual e manter minha própria abstinência.

Hoje Eu Me Lembrarei
Quanto mais fé consigo transmitir, mais eu a terei.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('4072b334-4bc9-4e42-9472-69e016699de2', 7, 22, 'Jogadores Anônimos me ensinou que a essência para o crescimento é ter boa vontade de mudar para melhor. A seguir, necessito também de boa vontade para assumir a responsabilidade necessária sobre o que isso acarretar, e ter coragem de tomar todas as atitudes necessárias.
                  “Eu sou e sei e serei
                  Eu estou ciente e querendo
                  Eu sei quem sou e quem serei
      Desejo ser e conhecer”
                                            Santo Agostinho
A boa vontade é o ingrediente principal de minha vida e da forma como pratico o Programa de JA?
Hoje Eu rezo
Eu peço pela boa vontade para fazer o que posso, boa vontade para ser o que puder ser, e – alguma vezes, o mais difícil – boa vontade para ser o que sou. Peço também, para que minha boa vontade seja carregada de energia em tudo que eu fizer, para que eu possa crescer da forma desejada por Deus e praticar os princípios do Programa em todos os meus afazeres.
Hoje Eu Me Lembrarei
- Eu sou, eu sei e eu serei.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('0a85777e-88a6-4636-896c-e57189c743de', 1, 6, 'Jogadores Anônimos, escreveu Dr. Robert I. Custer,  no prefácio do Livro Azul de Jogadores Anônimos, é um Programa de Doze Passos, “que proporciona uma visão de esperança, estrutura e amizade” para aqueles que escolheram o caminho da “adaptação bem sucedida a uma vida sem jogo. Este caminho pode ser tranqüilo ou acidentado, mas  em qualquer das alternativas, nunca será uma jornada sem sofrimento &#8230;”  Sendo um jogador compulsivo em recuperação, posso enfrentar quaisquer aborrecimentos hoje, sabendo que a dor da recuperação nunca será tão forte e desesperada quanto  a dor dos meus dias de jogo.

Estou preparado para ver cada novo dia no Programa de Jogadores Anônimos como um momento de aprendizagem, crescimento e  de melhores escolhas?

Hoje Eu Rezo
Que eu faça uso consciente do poder de escolha que Deus me concedeu, para planejar com sabedoria, um dia de cada vez, sem me tornar um escravo da apreensão, arrependimento ou ansiedade. Rezo para que a vontade de Deus seja feita através do exercício de minha própria vontade, que Ele, em Sua bondade, me concedeu.

Hoje Eu Me Lembrarei
A vontade de Deus prevalecerá.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('6c9f1e22-b469-4764-9296-a1f88b58d0b9', 5, 30, 'Desde que cheguei a Jogadores Anônimos aprendi a redefinir o amor. Passei a entender, por exemplo, que algumas vezes é necessário colocar o amor antes da “verdadeira honestidade”. Não posso mais magoar os outros cruelmente e desnecessariamente em nome da “honestidade total”. Hoje sempre preciso indagar a mim mesmo – qual é a coisa melhor e mais amável que posso fazer.

Comecei a plantar a semente do amor em minha vida diária?

Hoje eu Rezo
Que o amor de Deus me mostre como dar amor. Que eu nutra primeiro os sentimentos do amor e carinho dentro de mim e então descubra formas de demonstrar esses sentimentos. Que eu me lembre das inúmeras vezes em que me privei de relacionamentos porque não sabia ou não me permitia sentir o amor ou demonstrar o que eu realmente sentia.

Hoje Eu Me Lembrarei
Quando sentir amor, estarei amando.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('d19548ad-a232-4b3f-966f-161d6322ba34', 8, 11, 'Quando fico remoendo e me aborrecendo com coisas sem importância – e elas geram ressentimentos que  crescem cada vez mais como ervas daninhas – esqueço-me de que poderia estar ampliando o meu mundo e a  minha visão dele. Para mim essa é a maneira ideal de reduzir os problemas  à sua dimensão exata. Quando alguém ou alguma coisa está me causando problemas, eu deveria tentar  ver o fato em relação ao resto de minha vida – principalmente a parte boa e pela qual eu deveria ser grato.

Desejo desperdiçar minha vida me preocupando com pequenas coisas que drenam minha energia espiritual?

Hoje Eu Rezo
Que Deus me afaste da preocupação indevida acerca de coisas pequenas. Que, ao invés disso, Ele abra meus olhos para a grandeza de Seu universo e as maravilhas infindas de Seu mundo. Que Deus me conceda a chance da visão que pode reduzir quaisquer preocupações pequenas que me tirem a tranqüilidade, ao tamanho de uma mosca no vitral de uma catedral.

Hoje Eu Me Lembrarei
As irritações microscópicas podem arruinar a minha visão.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('5c624c80-12fa-4608-a3a1-5f879ddc73de', 9, 23, 'Quando estudamos os Doze Passos de Recuperação, muitos de nós, ainda novatos em Jogadores Anônimos, exclamavam: - Que pedido! Não posso fazer isso! Não se desencoraje, nos disseram reunião após reunião. – Ninguém aqui foi capaz de manter uma adesão a esses princípios a um nível sequer perto da perfeição. Não somos santos. O importante é que estamos dispostos a crescer em termos espirituais. Os princípios que estipulamos são orientações para atingir o progresso. Procuramos o progresso espiritual e não a perfeição espiritual.

Posso acreditar que, segundo as palavras de Browning, minha obrigação não é me reconstruir, mas fazer o melhor possível com o que Deus me deu?

Hoje Eu Rezo
Mesmo estando há muito tempo no Programa de JA, não devo me esquecer de que os Doze Passos de Recuperação não representam uma simples conquista que pode ser verificada através de minha lista de “coisas a serem feitas”. Ao invés disso, esses Passos representam um esforço por um ideal, um guia para se chegar lá. Que eu mantenha minha mente aberta para aprofundar minha interpretação desses princípios.

Hoje Eu Me Lembrarei
Progresso, ao invés de perfeição.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('d20a1089-f4dc-4258-a5bc-480ccb076830', 1, 8, 'Hoje é o dia que pedi e pelo qual recebi coragem, o que por si  só é um milagre. O fato de eu estar vivo é um grande milagre, do qual todos os outros milagres surgirão, desde que eu continue a fazer as coisas que me levaram a essa nova vida.

Estou agradecido pelo que recebi hoje?

Hoje Eu rezo
Que a bondade e a piedade de Deus me acompanhem todos os dias de minha vida. Que eu jamais pare de me maravilhar com o maior milagre de minha vida – o fato de estar vivo, aqui neste Planeta Verde, tornando-me mais saudável com as ferramentas de preservação de vida que me foram concedidas. Já que Deus optou por me dar e preservar a minha vida, mesmo que através dos perigos de minha compulsão com o jogo, que eu sempre continue a ouvir Seus planos para mim. Que eu sempre creia em milagres.

Hoje Eu Me Lembrarei
Minha vida é um milagre', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('d6dc2ede-e153-4d43-8e07-c2d25e0efd3f', 3, 14, 'O que me mantém hoje no caminho certo é o sentimento de lealdade aos outros membros de Jogadores Anônimos, onde quer que estejam. Dependemos uns dos outros. Sei, por exemplo, que os desapontaria se alguma vez voltasse a jogar. Quando cheguei a Jogadores Anônimos, encontrei um grupo de pessoas que não estavam apenas ajudando umas às outras a se absterem, mas também estavam sendo leais umas às outras através da própria abstinência.

Sou leal ao meu grupo e aos meus amigos em JA?

Hoje Eu Rezo
Agradeço a Deus pela lealdade e irmandade do grupo, e pelo compromisso mútuo que nos une. Que eu dê ao grupo, o que recebo dele,  na mesma proporção. Tendo sido um recebedor durante tantos anos, minha doação não era mais do que um bem material, pelo qual eu esperava ser pago com aprovação, amor ou favores. Que eu aprenda a ter o prazer da simples dádiva, sem laços de obrigação ou expectativas de recompensa.

Hoje Eu Me Lembrarei
A dádiva perfeita não exige nada em troca.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('16d193c0-c646-4d1a-964c-04cadb14f0a2', 2, 5, 'Quando estou confuso, preocupado, exasperado ou frustrado, tenho a tendência a racionalizar a situação e colocar a culpa em outra pessoa? Quando fico nesse estado, minha conversa é marcada por “ Ele fez&#8230;”,  “Ela  disse&#8230;”, “ Eles fizeram&#8230;”? Ou consigo honestamente admitir que talvez eu esteja errado? Minha paz de espírito depende da superação de minhas atitudes negativas e de minha tendência a racionalizar.

Tentarei, dia a dia, ser rigorosamente honesto comigo mesmo?

Hoje Eu Rezo
Que eu perceba quando estiver utilizando a terceira pessoa, - Ele disse&#8230;, ou - Eles prometeram&#8230;, ou - Ela disse que iria &#8230;;  e que eu tenha consciência de que essas acusações se tornaram um padrão de comportamento para mim que perpetua a ilusão. Que eu mude minha posição e, ao invés disso, encare a mim mesmo.

Hoje Eu Me Lembrarei
A honestidade é a única orientação.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('2f412d2a-364c-4b4b-9c08-b0e20e52d681', 6, 21, 'A palavra “direta” é a palavra chave no Nono Passo. Infelizmente, algumas vezes, muitos de nós esperam que as reparações indiretas sejam suficientes, poupando-nos da dor e da suposta humilhação de procurar pessoalmente as pessoas e contar a elas sobre nossos erros. Isto é uma fuga,  e nunca nos proporcionará a verdadeira sensação de acabar com os erros do passado. Isso nos mostra que ainda estamos tentando defender algo que não vale a pena, nos  atendo a uma conduta que deveríamos abandonar. As razões mais comuns para não realizarmos as reparações diretas são:  orgulho e medo.

À medida que faço reparações  aos outros, tenho consciência de que esses verdadeiros e duradouros benefícios caberão à minha própria pessoa.

Hoje Eu Rezo
Que eu me assegure de que, afinal de contas, serei eu a pessoa mais beneficiada pelos meus acertos, enquanto tento reparar os danos causados. Que eu  evite fazer reparações simplesmente para me beneficiar dela – para ser perdoado, readmitido ou ostentar o meu “novo eu”. O ego inflado e a bajulação não fazem parte do verdadeiro “novo eu”. Deus me livre do oportunismo.

Hoje Eu Me Lembrarei
Sem bajulação ou agrados exagerados.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('6c324015-9e6d-4fe0-8b69-7ffa098a2250', 8, 18, 'A auto-piedade é um dos mais terríveis e destruidores sentimentos que eu conheço. Por causa de suas incontáveis exigências por atenção e simpatia, minha auto-piedade corta o meu canal de comunicação com as outras pessoas, principalmente a comunicação com meu Poder Superior. Quando vejo o problema desta maneira, vejo que a auto-piedade limita o meu progresso espiritual. Ela é também uma verdadeira forma de martírio, que é um luxo que simplesmente  não posso ter. Aprendi que o remédio para isso é olhar intensamente para mim mesmo e mais intensamente ainda para os Doze Passos do Programa de Recuperação de Jogadores Anônimos.

Peço ao meu Poder Superior para me livrar desta servidão para comigo mesmo?

Hoje Eu Rezo
Que eu possa saber por experiência própria que autocomiseração não desperta sentimento de pena de ninguém. Ninguém – nem mesmo Deus – pode suprir sua descabida necessidade de pena. Que eu reconheça os dissabores da auto-piedade quando ela tentar roubar minha serenidade. Que Deus me mantenha ciente dos truques sorrateiros da auto-piedade.

Hoje Eu Me Lembrarei
Meu captor sou eu mesmo.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('fc363eab-bc77-48ef-851c-f1e34f2387af', 9, 4, 'Mesmo tendo rezado várias vezes em minha vida, percebi, após vários meses em Jogadores Anônimos, que na verdade nunca havia rezado de maneira correta. Sempre tentava fazer acordos com Deus, bem semelhante ao ateu que tenta se proteger, eu sempre pedia – Realize os meus desejos, ao invés de – Que a Sua Vontade – não a minha – seja feita. Em conseqüência disso, eu permanecia enganando a mim mesmo e dessa forma era incapaz de receber graça o bastante para retomar um modo de vida mais saudável.

Reconheço que quando eu rezava a Deus no passado,  eu geralmente pedia  que dois mais dois  não fossem quatro?

Hoje Eu Rezo
Que eu olhe para o passado e relembre como eu costumava rezar, pedindo por soluções específicas que eu – visando vantagem própria – achava que eram melhores. Olhando o passado a longo prazo, que eu me questione, se essas soluções teriam dado certo se Deus tivesse escolhido fazer as coisas à minha maneira. Em retrospectiva, que eu me conscientize de que esses meus pedidos nem sempre eram feitos com sabedoria. Que eu me contente em confiar em Deus.

Hoje Eu Me Lembrarei
Deus poderá não fazer  as coisas à minha maneira.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('421a3ed2-db22-4ce6-84cc-5125baa1c4a0', 2, 21, 'Desperdiço meu tempo e energia,  lutando contra situações que na verdade não merecem um segundo de minha atenção? Fico imaginando  moinhos de vento  gigantes e ameaçadores ficando em combate com eles até tombar de exaustão, como Don Quixote, o legendário herói da literatura espanhola? Hoje não permitirei que minha imaginação transforme pequenos em grandes problemas. Tentarei perceber claramente cada situação, dando à mesma somente o valor e a atenção que ela merece.

Passei a acreditar, como me é sugerido no segundo Passo, que um Poder superior a mim mesmo pode me trazer de volta a um modo normal de pensar e viver?

Hoje Eu Rezo
Deus, mantenha sã a minha perspectiva. Ajude-me a não mais aumentar problemas insignificantes, dando sentido exagerado a conversas casuais, transformando um monte de brasas em Vesúvio. Impeça que meu medo assuma uma proporção exagerada, como as sombras na parede. Restaure meus valores, que se tornaram distorcidos durante os dias de meu jogo compulsivo.

Hoje Eu Me Lembrarei
Sanidade é perspectiva.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('63d101b7-52c0-4e1a-be3a-209e3978fb7b', 6, 5, 'O Programa de Jogadores Anônimos me ensina que poucas são as pessoas que podem verdadeiramente dizer que amam a todos. A maioria de nós precisa admitir que somente amou algumas pessoas e que foi bem indiferente a muitos outras. Quanto ao resto, bem, realmente não gostávamos ou até os odiávamos. Em jogadores Anônimos descobrimos que precisamos de algo muito melhor do que isso para manter nosso equilíbrio. Precisamos esquecer a noção, mesmo que aos poucos,  de  que podemos amar algumas poucas pessoas de forma possessiva, ignorar muitas delas e continuar a temer ou odiar qualquer pessoa.

Nas reuniões concentro-me na mensagem ao invés de concentrar-me no mensageiro?

Hoje Eu Rezo
Que eu aceite que não há lugar em minha recuperação do jogo compulsivo, ou em toda minha vida, para o ódio intoxicante ou a indiferença escravizadora. Uma das noções positivas mais importantes que preciso levar comigo é que todos os seres humanos, como filhos de Deus, podem ser amigos amorosos. Que eu não consiga odiar meus irmãos.

Hoje Eu Me Lembrarei
Ouça a mensagem. Não julgue o mensageiro.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('6abc0342-21f8-494c-80e3-75fb5a095e20', 4, 29, 'À medida que melhoro no Programa de Jogadores Anônimos – compartilhando, me importando com os outros e  tornando-me cada vez mais envolvido – descubro que está ficando cada vez mais fácil viver o AGORA. Até mesmo o meu vocabulário está mudando. Não mais uso frases como “eu poderia ter feito”, “eu deveria ter feito”, “eu teria feito” ou “é provável que”, que outrora permeavam o meu discurso.

O que foi feito, está feito, e o que será, será. O único momento que realmente importa é o agora. Estou começando a sentir o verdadeiro prazer, serenidade e paz no Programa?

Hoje Eu Rezo
Que eu possa reunir todas as minhas lembranças difusas do passado; os esquemas altamente elaborados e os receios exacerbados do futuro, compactando-os dentro dos limites mais sadios  do dia de hoje. Somente vivendo o AGORA posso manter o equilíbrio, sem me deixar levar pelo passado ou antecipar o futuro. Que eu pare de tentar abraçar a  minha vida inteira incontrolavelmente, levando-a comigo em um saco minado a todos os lugares aonde vou.

Hoje Eu Me Lembrarei
Crie tempo para o dia de hoje.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('56d29f26-249d-4def-bac0-ab29f55d9b98', 1, 30, 'Consegui a liberdade simplesmente porque estava fraco um dia e de repente no outro dia tornei-me forte? Mudei de uma pessoa desamparada e sem esperanças que eu parecia ser, simplesmente porque tomei a decisão de que – de agora em diante as coisas serão diferentes? O fato de eu estar melhor hoje do que em qualquer outra época é resultado de minha própria força de vontade? Posso me considerar responsável por minha mudança para melhor por que eu mesmo trabalhei para isso? Sei a resposta, porque eu procurei refúgio num Poder maior do que eu – um Poder que ainda está além da minha capacidade de visualização.

Considero a mudança em minha vida um milagre muito superior ao trabalho de qualquer poder humano?

Hoje Eu Rezo
À medida que os dias de abstinência se prolongam, e o momento da decisão fica cada vez mais para trás, que eu jamais esqueça do Poder que mudou minha vida. Que eu me lembre que minha abstinência é um constante milagre, e não uma simples transformação que acontece uma só vez na vida.

Hoje Eu Me Lembrarei
A vida é um constante milagre.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('62398b63-f73d-4fcd-829a-8ab298b48b92', 11, 19, 'Não discuto mais com as pessoas que acreditam que a satisfação de nossos desejos naturais seja o principal propósito da vida. O propósito de Jogadores Anônimos não é acabar com o progresso material. Quando pensamos sobre isso, chegamos à conclusão que nenhum outro grupo de pessoas fez mais confusão ao tentar viver de acordo com a fórmula da “dolce vita” do que nós. Sempre tentamos ter mais do que podíamos – em todas as áreas. E mesmo quando parecíamos estar ganhando, isso somente aumentava nossa compulsão e ficávamos sonhando em obter ganhos cada vez maiores. Nunca conseguíamos satisfazer nossa compulsão.

Estou aprendendo que as satisfações materiais são apenas sub-produtos e não o principal propósito de nossas vidas?

Hoje Eu Rezo
Que eu veja que nunca consegui lidar muito bem com os excessos, em virtude de minhas experiências passadas. Eu só “queria mais” de tudo que eu tinha – amor, ganhos, dinheiro, propriedades e coisas. Que o Programa de Jogadores Anônimos me ensine que preciso me concentrar no que é espiritual e não no que é material ou recompensador.

Hoje Eu Me Lembrarei
É certo ter ganância pela espiritualidade.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('6ecf1893-3a4d-43e2-ab88-f48ce8b27ecb', 4, 6, 'O que dizemos  a uma pessoa que recaiu ou a alguém que pede ajuda? Podemos transmitir a mensagem, se essa pessoa estiver disposta a escutar; podemos compartilhar com ela nossas experiências, energia e esperança. Entretanto, talvez a coisa mais importante que possamos dizer, é que estamos sinceramente felizes que ela esteja de volta;  e que queremos dar a maior ajuda possível. E precisamos realmente acreditar nisso.

Eu ainda posso “voltar à escola” e continuar a aprender através dos erros e adversidades de outras pessoas?

Hoje Eu Rezo
Que eu sempre tenha amor suficiente para receber de volta ao grupo alguém que tenha recaído. Que eu ouça humildemente a história de sofrimento daquela pessoa, pois é para lá que eu irei sem o meu Poder Superior. Que eu aprenda com os erros dos outros e ore para que não  volte a agir como eles.

Hoje Eu Me Lembrarei
A abstinência não é à prova de falhas.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('dee441e4-0c34-4f67-9ee3-9e72b78d7b8a', 1, 28, 'Agora que estou em Jogadores Anônimos, não estou mais escravizado pela compulsão para fazer a próxima aposta. Livre, finalmente livre da invenção de álibis elaborados – e da espera que os mesmos não sejam descobertos. Livre da culpa e da vergonha. Livre da preocupação com os bicheiros e credores de empréstimos. Livre da interminável montanha de dívidas.

Valorizo minha libertação do jogo compulsivo?

Hoje Eu Rezo
Louvo a Deus por estar livre da urgência de jogar. Esta é a minha primeira liberdade, da qual outras liberdades se originarão – liberdade para avaliar meu comportamento de forma saudável e construtiva, liberdade para crescer como pessoa, liberdade para manter relacionamentos com outros de maneira sólida, Nunca deixarei de agradecer ao meu Poder Superior por guiar-me para longe de minha escravidão.

Hoje Eu Me Lembrarei
Louvo a Deus por minha liberdade.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('8df0a4cf-6c3e-4223-b6e4-ae137d07bf49', 10, 15, 'Com muita freqüência e com pouca sabedoria, estabeleci padrões para outras pessoas no Programa de Jogadores Anônimos. Pior do que isso, esperei que esses padrões fossem atendidos. Chego ao ponto, de algumas vezes, decidir sobre o progresso que outras pessoas deveriam ter em suas recuperações e como suas atitudes e ações deveriam mudar. Como seria de se esperar, quando as coisas não funcionam da maneira esperada, torno-me frustrado e até aborrecido. Preciso aprender a deixar suas vidas nas mãos de Deus. Preciso aprender a não exigir ou esperar mudanças nos outros, concentrando-me somente em meus próprios pontos fracos. Finalmente, não posso esperar perfeição em outro ser humano, mais do que poderia esperar em mim mesmo.

Posso chegar a perfeição algum dia?

Hoje Eu Rezo
Que Deus me peça para imediatamente recuar um degrau, se começar a tentar escalar um desses lugares elevados: no meu pódio, como se fosse um especialista em tudo; no caixote, como o líder que pretende modificar o mundo; em meu púlpito, como se fosse o mensageiro mais abençoado por Deus do que qualquer outro jamais poderia ser; na cadeira do julgamento, como o grande dono da lei. Por favor, Deus,  impeça que eu me invista na autoridade indevida e mantenha minha humildade.

Hoje Eu Me Lembrarei
Uma mão pesada não é uma mão que ajuda.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('99369c40-d38d-4d7c-a458-6305040b7ac2', 8, 17, 'O Quarto Passo sugere que façamos um minucioso e destemido inventário moral e financeiro – não um inventário imoral de nós mesmos. Os Passos são a base para a recuperação, não um tronco de açoite para a auto-flagelação. Fazer um inventário não significa concentrar-me em meus pontos negativos até que todos pontos positivos não possam ser identificados. Da mesma forma, o reconhecimento das qualidades não precisa ser um ato de orgulho ou de supervalorização. Se reconhecer minhas qualidades como graças de Deus, posso fazer um inventário com verdadeira humildade e ao mesmo tempo vivenciar a satisfação de ver o que é agradável, amável e generoso em mim.

Tendo como base as palavras de Walt Whitman tentarei acreditar que  “Sou maior e melhor do que eu achava; não sabia que tinha tanta bondade em mim &#8230;”

Hoje Eu Rezo
Quando descubro coisas boas sobre mim mesmo, à medida que faço uma busca arqueológica interior, que eu dê o crédito a quem é devido – a Deus, que é o provedor de todo o bem. Que eu aprecie tudo o que é bom sobre mim com humildade, como uma graça de Deus.

Hoje Eu Me Lembrarei
O bem é uma graça de Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('d7d775a1-887b-447f-a75c-028d676b3b1a', 3, 7, 'É hora de perceber que minha postura - em relação à vida que estou levando e às pessoas que fazem parte dela -  pode ter um efeito tangível, mensurável e profundo sobre o que me acontece no dia a dia. Se esperar coisas boas então elas certamente acontecerão. Se eu tentar, todos os dias, nortear minhas atitudes e pontos de vista em uma base espiritual sólida, sei que isto mudará todas as circunstâncias de minha vida para melhor.

Aceito o fato de que somente  recebo uma dose de alívio diário em função de minha condição espiritual?

Hoje Eu Rezo
Sendo minha doença espiritual e emocional, que eu me recupere espiritualmente, através do contato diário com Deus. Que eu descubra um lugar tranqüilo dentro de mim, que me possibilite passar alguns momentos com Deus. Que eu tenha o conhecimento de Sua vontade..

Hoje Eu Me Lembrarei
Passar um momento tranqüilo com Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('da7e1171-fe3b-4e9a-9d77-e4fb9ac600fc', 12, 15, 'Algumas pessoas são tão preocupadas que reclamam quando não têm nada com que se preocuparem. Os novatos no Programa de Jogadores Anônimos algumas vezes sentem, por exemplo, que “isto é bom demais para durar”. Entretanto,a maioria de nós tem uma série  de coisas reais com que nos preocuparmos – dívidas esquecidas, saúde, morte e impostos, são apenas algumas. Mas, JA nos ensina que o antídoto para a preocupação e o medo é a confiança – confiança, não em nós mesmos, mas em nosso Poder Superior.

Continuarei a crer que Deus pode e irá  afastar o desespero de passar noites e dias  angustiado? Acreditarei que, se o acontece um desastre, Deus me ajudará a perceber o seu verdadeiro significado?

Hoje Eu Rezo
Que eu perceba que levarei mais tempo para controlar o hábito da preocupação – que surge a partir dos grandes medos geralmente não identificados. Como muitas outras pessoas, tenho cultivado essas preocupações por tanto tempo que elas se tornaram um companheiro constante e regulador de meus passos. Que meu Poder Superior me ensine a me livrar das preocupações que causam o desgaste de minha energia e minam minhas horas de criatividade.

Hoje Eu Me Lembrarei
Acabe com  o hábito da preocupação.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('24c4c648-b6c9-49fd-850b-727915005be7', 8, 31, 'De tempos em tempos, começo a achar que conheço a vontade de Deus para com as outras pessoas. Digo para mim mesmo, - Esta pessoa deveria ficar curada de sua doença., ou – Aquela pessoa deveria se livrar do abuso que está sofrendo; e começo a orar pedindo por essas coisas específicas. Meu coração está certo quando começo a rezar dessa forma, mas essas orações são baseadas na suposição de que conheço a vontade de Deus para com a pessoa por quem rezo. O Programa de Jogadores Anônimos me ensina que, ao invés disso, eu deveria pedir para que a vontade de Deus fosse feita – não importa como – tanto para os outros como para mim mesmo.

Lembrarei que Deus está disposto a ser meu amigo, mas somente na extensão de minha crença nele?

Hoje Eu Me Lembrarei
Louvo a Deus pela oportunidade de ajudar outras pessoas. Da mesma forma, agradeço a Deus por me fazer querer ajudar outras pessoas, por me tirar da torre onde me encontrava, para que eu pudesse encontrar as pessoas, compartilhar e me importar com elas. Ensine-me a orar “Seja feita a Vossa Vontade” em nome do espírito do amor, que Deus inspira em mim.

Hoje Eu Me Lembrarei
Colocarei minha fé na vontade de Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('f5705cbe-d6db-40c1-905c-ba56359bef0c', 2, 13, '.
Às vezes ouvimos alguém dizer – ele tem luz própria. Uma imagem mental revela claramente que muitos de nós tendem a fazer sombra à nossa própria felicidade, devido a idéias erradas. Precisamos ficar de lado para que a luz possa brilhar sobre tudo  que fazemos. Somente assim, podemos nos ver e ver as circunstâncias com  verdadeira clareza. Com o Programa de Recuperação de Jogadores Anônimos não precisamos mais ocultar nossa própria luz e tentar resolver, sozinhos, nossos problemas na escuridão.

Quando me deparo com um problema aparentemente insolúvel me  pergunto se estou ofuscando minha própria luz?

Hoje Eu Rezo
Que eu não seja um obstáculo no meu próprio caminho, obscurecendo minha própria clareza de pensamento, tropeçando em meus próprios pés, bloqueando meu caminho para a recuperação. Se eu achar que estou ofuscando minha luz, que eu peça ao meu Poder Superior e aos meus amigos do Grupo para me mostrarem um novo ponto de equilíbrio.

Hoje Eu Me Lembrarei
Se tudo o que vejo é minha sombra, estou escondendo minha  própria luz.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('c0036020-bdc4-48f8-875e-a39718e90b78', 9, 16, 'Aprendemos com os outros no Programa de Jogadores Anônimos, que a melhor maneira de lidar com situações sofridas, é encará-la de frente, com honestidade e com realismo, tentando aprender com elas, e usá-las como base para nosso crescimento. Através do Programa de Jogadores Anônimos e de nosso contato com um Poder Superior, podemos encontrar a coragem para usar a dor para um crescimento triunfante.

Acreditarei que qualquer dor que vier a sentir, será um preço pequeno a pagar pela alegria de me tornar a pessoa que deveria ser?

Hoje Eu Rezo
Que meu Poder Superior me conceda a coragem para parar de fugir das situações de sofrimento.Se antes eu usava o jogo como uma válvula de escape para fugir da dor, que eu saiba muito bem que o próprio jogo se tornou a dor, da qual não houve fuga até eu encontrar o Programa de JA. Agora, que eu enfrente a dor – do passado e do presente – e aprenda com ela.

Hoje Eu Me Lembrarei
Jogo: antes a saída de incêndio, depois o próprio incêndio.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('e22ab25c-ade1-4705-a489-91fcc7282e90', 3, 3, 'Comecei a me entender melhor desde que entrei  para Jogadores Anônimos. Uma das coisas mais importantes que aprendi é que opiniões não são fatos. O fato de eu achar que algo é assim, não significa necessariamente que seja dessa forma. Segundo o filósofo grego Epictetus, “Os homens não se preocupam com as coisas, mas com suas idéias a respeito delas. Quando encontramos dificuldades, nos tornamos ansiosos ou conturbados, não devemos culpar os outros e sim a nós mesmos. Ou seja, ao que pensamos sobre as coisas.”

Acredito que jamais poderei perder completamente o que aprendi durante minha recuperação?

Hoje Eu Rezo
Que eu aprenda a separar a realidade de minhas idéias sobre ela. Que eu compreenda que as situações, coisas - e até pessoas - adquirem as cores e as dimensões de acordo com minhas atitudes sobre elas.

Hoje Eu Me Lembrarei
Distinguir o real do irreal.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('5d7429fd-4b89-4be7-91d6-0b8889a915f9', 7, 1, 'Originalmente, para alguns de nós, o medo pode ter sido a razão por termos procurado Jogadores Anônimos. No começo, o medo pode ter sido a única razão para que alguns de nós continuássemos longe do jogo e daquele sentimento horroroso de “eu vou começar a ter sorte” (mesmo sabendo que no final o jogo sempre se voltaria contra nós). Mas o estado de medo dificilmente nos conduz à tranqüilidade e felicidade – e se o faz, isso dura pouco. Nós temos que achar alternativas para o medo, para que possamos vencer aquelas vazias primeiras horas sem jogar, ou primeiros dias ou até mesmo primeiras semanas. Para muitos de nós, a resposta está em se tornar um membro ativo de Jogadores Anônimos. Em pouco tempo, sentimos que estamos fazendo parte de algo; pela primeira vez em muito tempo, começar a ter a impressão de que “fazemos parte”, ao invés de estarmos  “a parte”.

Estou disposto a tomar a iniciativa?

Hoje Eu Rezo
Que Deus me ajude a encontrar alternativas para o medo – esse cão de guarda de minha abstinência inicial. Agradeço ao Poder Superior por me conduzir a um lugar onde eu encontrei outras pessoas que também sofreram as mesmas compulsões e medos. Sou agradecido por ter encontrado um lugar ao qual eu pertenço.

Hoje Eu Me Lembrarei
Eu sou “parte de algo”, não estou “a parte”.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('5301c1d5-b1e8-47f8-8266-bf92e4d66e43', 2, 2, 'Olhando para trás, me dou conta de quanto tempo de minha vida foi gasto em função dos erros dos outros.  Com certeza, isso me trazia muita satisfação pessoal, mas vejo agora o quanto esse processo se tornou sutil e na verdade perverso. Depois de tudo o que era dito e feito, o efeito em rede dessa preocupação com os chamados erros alheios era permitir que eu vivesse confortavelmente ignorando os meus próprios defeitos.

Ainda aponto meu dedo para os outros e dessa forma ignoro de maneira errônea meus próprios erros?

Hoje Eu Rezo
Que eu veja,  que minha preocupação com os erros dos outros,  é na verdade um filtro de fumaça que me impede de olhar honestamente para meus próprios erros, e também abafa meu próprio ego imperfeito. Que eu investigue os “por quês” de minhas acusações.

Hoje Eu Me Lembrarei
Culpar os outros é jogar.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('5a8e8ec8-1c69-4fbc-b443-e969ee54bd1f', 6, 13, 'Não acredito que o Programa de Jogadores Anônimos e os Doze Passos funcionem somente porque li isso em um livro, ou porque ouço outras pessoas dizerem. Acredito, porque vejo outras pessoas se recuperarem, e porque sei que também estou me recuperando. Não acredito mais que eu seja ‘irrecuperável” ou sem esperanças. Quando vejo as mudanças nos outros e em mim mesmo, sei que o programa funciona. Quando certa vez, um repórter da TV perguntou ao filósofo Jung se ele acreditava em Deus, Jung respondeu pausadamente “Eu não acredito. Eu sei”.

Sei que o Programa funciona?

Hoje Eu Rezo
Mostre-me finais felizes, vidas consertadas, pessoas recuperadas, pontes reconstruídas, e eu não terei que aceitar sem questionamentos que o Programa funciona. Que eu veja o Programa em funcionamento – para os outros e para mim. Que eu seja grato pela realidade já documentada do sucesso do Programa. Que esta certeza ajude-me a encontrar a fé que preciso para seguir os Doze Passos.

Hoje Eu Me Lembrarei
O Programa de Jogadores Anônimos funciona.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('7befb949-469b-404c-b03b-81cb4eff6e4a', 6, 15, 'Aprender a viver em paz e em comunidade – com todos os homens e mulheres – é uma aventura fascinante e muitas vezes emocionante. Mas, cada um de nós em Jogadores Anônimos, descobriu não ser capaz de fazer muito progresso em nossa nova aventura na vida, até que primeiro dediquemos um tempo para fazer uma busca minuciosa e imparcial da destruição humana que deixamos para trás em nosso despertar.

Fiz uma lista das pessoas a quem prejudiquei, como o Passo Oito sugere, e me tornei disposto a fazer as reparações a todos?

Hoje Eu Rezo
Deus, concedei-me a honestidade que preciso, não somente para olhar dentro de mim  e descobrir como sou realmente, mas também para ver de que maneira meu comportamento doentio e irresponsável afetou aqueles à minha volta. Que eu compreenda que minha adicção não é – como eu costumava pensar – a doença de um solitário. Independente de quanto eu me sentisse solitário, minhas mentiras e invenções espalharam-se à minha volta em grandes círculos de sofrimento.

Hoje Eu Me Lembrarei
As mentiras espalham-se sem limites.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('fd5ce7b5-34e1-48b8-a3ba-b56c7b749f65', 7, 23, 'Hoje, tentarei aceitar menos do que achava ser possível, ficando disposto não somente a aceitar, mas também a apreciar. Hoje, não vou esperar demasiado de alguém – muito menos de mim. Vou tentar lembrar que a satisfação vem de aceitar graciosamente as coisas boas que recebemos, e não ficar furioso com a vida  porque ela não é melhor.

Já entendi a diferença entre resignação e aceitação realista?

Hoje Eu rezo
Que eu não tenha expectativas altamente irrealistas, esperando demais. Que eu olhe bem para o meu passado e veja que os objetivos que tracei para mim era impossíveis, transformando-se em armadilhas para a minha adicçao. Com muita freqüência eu ficava na metade do caminho, confrontado pelo meu próprio fracasso. Aqueles sentimentos de que “você foi derrotado”, ou de que “você fracassou novamente”,  tornaram-se desculpas monumentais para que eu me entregasse à minha compulsão pelo jogo, encobrindo meus sofrimentos. Que eu evite esse antigo padrão doente. Que eu seja realista..

Hoje Eu Me Lembrarei
O bom é bom o bastante.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('98e78897-4e87-402f-b37f-ee7b42a394c6', 2, 15, 'Quando fico com raiva, posso admiti-la e aceitá-la como um fato, sem no entanto permitir que ela cresça e surja de repente de maneira inadequada? Hoje, finalmente, começo a ver que a raiva acumulada acaba rapidamente com a paz de espírito, que é tão fundamental à minha recuperação em  andamento? Quando fico furioso perco o controle, transfiro,  sem refletir, esse controle para as outras pessoas, lugares ou coisas que me deixam nervoso.

Quando estiver com raiva, tentarei lembrar que estou me expondo ao perigo? Contarei até dez, ligando para algum companheiro de Jogadores Anônimos ou falando em voz alta a Oração da Serenidade.

Hoje Eu Rezo
Que eu reconheça os sentimentos de raiva e os elimine pouco a pouco, aceitando os mesmos como um fato, ao invés de permitir que se transformem em ódio e se manifestem descontroladamente.

Hoje Eu Me Lembrarei
A raiva existe, o ódio não precisa existir.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('6a9af92d-15a1-4544-abc0-e3a738098ced', 11, 24, 'Embora tenha vindo a Jogadores Anônimos para lidar com um problema específico, logo me conscientizei de  que encontraria não somente a libertação da adicção, mas também a liberdade de viver em um mundo real sem medos ou frustrações. Aprendemos que as soluções estão dentro de nós. Com a ajuda de meu Poder Superior, posso enriquecer minha vida com conforto, alegria e completa serenidade.

Estou deixando de ser meu pior inimigo para me transformar em meu melhor amigo?

Hoje Eu Rezo
Que eu louve meu Poder Superior por minha libertação – da adicção do jogo, da falência espiritual, da solidão, do medo, da gangorra entre orgulho e humilhação, do desespero, das fantasias, da superficialidade, do destino trágico. Dou graças pelo modo de vida que essas libertações me proporcionam, preenchendo os espaços vazios com bondade adicional e paz de espírito.

Hoje Eu Me Lembrarei
Dar graças por todas as minhas libertações.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('2c58dc82-c528-4e63-87d4-5ef033cb2309', 8, 12, 'Alguém uma vez perguntou a um Mestre Zen:  – Como você mantém tamanha serenidade e paz? Ele respondeu: - Nunca abandono meu lugar de meditação. Embora meditasse cedo pela manhã, ele levava consigo a paz daqueles momentos pelo resto do dia. Ficar calmo e diminuir o ritmo é uma das tarefas mais difíceis para um jogador compulsivo em recuperação. A ação foi durante tanto tempo o meu modo de vida, que preciso aprender de novo a diminuir o ritmo e escutar. Começar o dia com orações e meditação pode ser a maior recompensa de meu dia. Quando opto por levar a paz e a serenidade comigo ao longo do dia, o próprio mundo parece desacelerar movendo-se no meu ritmo, ao invés de girar tão rápido que me obrigue a estar sempre correndo para alcançá-lo.

Desfrutarei da paz gloriosa que advém da meditação?

Hoje Eu Rezo
Que meus dias comecem lentamente, em paz, e assim permaneçam, à medida que mantenho o foco no que se apresenta a mim no momento, ao invés de planejar   atividades indefinidas e desestabilizadoras. Da mesma forma que a ação frenética era um sintoma de minha compulsão, a serenidade é um sinal de minha recuperação.

Hoje Eu Me Lembrarei
Permitir que a serenidade faça parte de minha vida.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('5ee3b9c2-20c0-481b-9075-18231a73bf0d', 8, 10, 'Fomos  nossos  piores inimigos na maior parte de nossas vidas, e freqüentemente,  nos prejudicamos seriamente em virtude de um ressentimento “justificado”  acerca de um  pequeno erro. Há sem dúvidas, muitas razões para ressentimentos nesta vida, e a maioria delas serve como “justificativas”. Todavia, não podemos começar a resolver todas as agruras  do mundo ou mesmo resolver  coisas para agradar a todos. Se outras pessoas nos trataram injustamente,  podemos sanar  nossas dificuldades ao perdoarmos completamente as pessoas envolvidas e ao abandonarmos o hábito destrutivo de relembrar nossas mágoas e humilhações.

Posso acreditar que a dor de ontem é a compreensão de hoje, transportada para o  amor de amanhã?

Hoje Eu Rezo
Se me tratam injustamente ou simplesmente acho que sou tratado assim, devo tentar não me transformar em alguém cheio de ressentimentos, remoendo os males passados. Se conseguir identificar a fonte da emoção causadora de meu ressentimento, que eu seja grande o bastante para perdoar a pessoa envolvida,  e sábio o bastante para esquecer tudo o que aconteceu.

Hoje Eu Me Lembrarei
Não se pode reparar todas as injustiças.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('b401b647-ebb2-48ca-854e-779a20a49590', 10, 13, 'Meu progresso na recuperação depende em grande escala de minha atitude; e minha atitude depende de mim mesmo; e da  maneira como decido analisar as coisas. Ninguém pode me forçar a tomar uma atitude. Para mim, a melhor atitude é ter uma visão sem auto-piedade e sem ressentimentos. Sem dúvida alguma, surgirão obstáculos em meu caminho. Mas o Programa de Jogadores Anônimos me ensinou que esses obstáculos podem ser transformados em degraus para chegar ao crescimento.

Acredito, nas palavras Tennyson, que “o homem poderá alcançar coisas maiores a partir de seus próprios escombros”?

Hoje Eu Rezo
Que Deus me ajude a cultivar uma atitude saudável comigo mesmo, com o Programa de Jogadores Anônimos e com outras pessoas. Deus, impeça-me de perder meus estabilizadores espirituais, que me mantêm equilibrado em propósito e visão de vida. Faça com que eu não tenha auto-piedade, que não me sinta desencorajado, e que eu perca minha tendência a dramatizar demais. Não permita que algum fardo do passado tire o meu equilíbrio.

Hoje Eu Me Lembrarei
Com Deus ao meu lado, não me sentirei desencorajado.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('8352edc9-8326-4553-819d-91a776ae6d7c', 12, 13, 'Um amigo em Jogadores Anônimos me falou de um trecho que lembrava de sua infância – “Abra meus olhos para que eu possa enxergar os sinais da verdade que o Senhor me mostra.” Isso é, na verdade, o que o Programa de JA tem feito por mim – ele abre meus olhos para que eu comece a enxergar o verdadeiro sentido de minha adicção ao jogo, bem como o verdadeiro significado da  vida feliz que poderei ter se praticar os princípios transmitidos pelos Doze Passos de Recuperação.

Através da oração e da meditação também estou melhorando minha visão interior, para que eu possa  ver melhor o Amor de Deus e a força de seu trabalho atuando  em mim e através de mim?

Hoje Eu Rezo
À medida que vou praticando os Passos, que cada sinal de verdade que eu perceba, faça com que eu comece a me orientar pelo brilho forte de uma estrela permanente. Que eu saiba que essas estrelas representam o que preciso para direcionar meu curso e navegar com segurança. Que eu não mais sinta a necessidade urgente de me desviar de meu caminho para portos desconhecidos em busca de alternativas. Essas estrelas serão para sempre minhas para que eu me oriente por elas.

Hoje Eu Me Lembrarei
Procure as estrelas permanentes e guie-se através delas.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('6ffaf8cc-9d69-4faf-8d80-e1f7f592b227', 7, 4, 'Está na hora de me tornar disposto a fazer, custe o que custar, o que for necessário para reassumir minhas responsabilidades pessoais, mesmo que isto signifique entregar a outros as minhas rédeas financeiras. Assim como há paradoxos nos Passos - admitir que a minha vida se tornou ingovernável para que ela possa se tornar governável de novo, me render a um Poder Superior para que eu possa me libertar de novo – haverá outros paradoxos em minha recuperação. Permitir que um membro de confiança de minha família ou um profissional tome conta da minha vida financeira para que eu possa me tornar responsável por ela novamente pode ser um desses paradoxos. Eu provei que sou impotente perante o jogo – e perante os altos e baixos emocionais que o acompanham. Agora é hora de abandonar meu caminho desastroso e solitário e começar a interagir com outras pessoas, aceitando qualquer ajuda que o meu Poder Superior me proporciona.

Aceitei o fato de que, apesar de ter uma recuperação toda minha, às vezes preciso da ajuda e encorajamento de outras pessoas?

Hoje Eu Rezo
Que o Programa de Jogadores Anônimos, com a ajuda de Deus, me dê a chance de viver uma vida estável, criativa e mais proveitosa. Que eu aceite a força que outras pessoas me oferecem, à medida que me mostro disposto à compartilhar minha força com outros. Que eu entenda, nesta Declaração do Dia da Independência, que eu também tenho uma celebração de liberdade – de meu vício do jogo..

Hoje Eu Me Lembrarei
Celebrar  minha liberdade pessoal.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('800e8190-d589-421e-8c07-2cc99aee9088', 5, 28, 'Todos temos momentos em que nos sentimos alienados, quando parece que não temos nenhum lugar ou pessoa a  quem recorrer. Quando não sabemos que caminho tomar, quando não parece haver ninguém para nos ajudar, mesmo nesse momento não estamos sozinhos ou sem ajuda; a presença de Deus está sempre conosco. Quando precisamos de força, coragem ou conforto, Deus está lá com a ajuda que precisamos. Mesmo antes de nos voltarmos a Deus, Seu amor chega até nós; Seu Espírito amoroso dentro de nós escuta nosso grito e nos responde.

Eu realmente acredito que não preciso mais ficar sozinho?

Hoje Eu Rezo
Que eu nunca mais me sinta sozinho, mesmo que esteja desacompanhado em algum lugar, se reservar um momento para falar com meu Poder Superior. Que Deus seja meu companheiro, minha alegria, minha ajuda eterna em momentos de dificuldades. Que a certeza dessa presença constante encha-me de paz, para que eu não tema a solidão de meu próprio quarto, nem a alienação quando estou rodeado de pessoas em uma sala.

Hoje Eu Me Lembrarei
Ficar atento à presença de Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('72035734-f27a-41ec-aa28-c8f831b2b644', 9, 22, 'Por um período de tempo considerável, após chegar a Jogadores Anônimos, deixava que as coisas que eu não podia fazer me impedissem de fazer as que eu podia. Quando me sentia incomodado com o que era dito por um membro na terapia ou por outras pessoas, eu recuava, escondendo-me em minha concha. Agora, ao invés de ficar na defensiva ou irritado quando alguém vai direto na minha ferida, tento aceitar bem isso– porque isso me possibilita trabalhar minhas atitudes e percepção de Deus, de mim mesmo e de outras pessoas, além de minha condição de vida.

Hoje Eu Rezo
Que Deus me dê coragem para testar minhas novas asas – mesmo que seja somente uma pena de cada vez. Não devo esperar me tornar um ser completo antes de reingressar no mundo das oportunidades de cada dia, pois a recuperação está em curso, e o crescimento vem através dos desafios. Que eu não busque mais as desesperadas tentativas em busca da perfeição e mantenha meus objetivos em perspectivas e desenvolvendo-os à medida que vou vivendo – um dia de cada vez.

Hoje Eu Me Lembrarei
O que não posso fazer não deve me impedir de fazer o que posso', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('77a414d6-d35a-417f-a513-f4941a27aab1', 7, 16, 'A vasta experiência tem provado que o Programa de Jogadores Anônimos com os Doze Passos funcionará para qualquer pessoa que esteja disposta a fazê-lo com uma mente aberta. Devemos lembrar que não podemos esperar que os milagres aconteçam da noite para o dia; afinal de contas, demoramos anos para criar a situação na qual nos encontramos hoje. Tentarei ser menos precipitado ao tirar conclusões em meu julgamento . Vou me apegar à expectativa de que o Programa de JA. pode mudar a minha vida inteira, desde que eu dê uma chance a ele.

Já comecei a entender que a minha satisfação plena não depende de que eu faça as coisas à minha maneira?

Hoje eu Rezo
Rezo para que eu tenha uma atitude mais receptiva, com um pouco mais de paciência, menos pressa e mais humildade nos meus julgamentos. Que eu sempre entenda que as mudanças virão&#8230;isto sempre acontece&#8230;se eu escutar a vontade de Deus. Que Deus me dê perseverança, pois muitas vezes vou ter que esperar um pouco para ver os Doze Passos surtirem efeito.

Hoje Eu Me Lembrarei
Paciência.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('cb41d531-1644-4def-8e20-c4ed8fb208de', 3, 9, 'Aprendemos em Jogadores Anônimos que, à medida que crescemos espiritualmente, descobrimos que nossas antigas atitudes para com nossas motivações instintivas, precisam passar por revisões drásticas.  Nossas necessidades de segurança emocional e riqueza, de prestígio pessoal e poder, tudo precisa ser moderado e redirecionado. Aprendemos que a satisfação plena dessas demandas não pode se tornar o único objetivo e razão de nossa vida. Mas, quando estamos dispostos a colocar o crescimento espiritual em primeiro lugar – então, somente então, temos a verdadeira chance de crescer em termos de conscientização saudável e amor verdadeiro.

Estou disposto a colocar o crescimento espiritual em primeiro lugar?’’

Hoje Eu Rezo
Que meu desenvolvimento como ser espiritual controle minha ânsia habitual em busca de segurança material. Que eu entenda que a única segurança verdadeira desta vida é a espiritual. Se eu tiver fé em meu Poder Superior, essas revisões de minhas atitudes se concretizarão. Que eu melhore primeiro o meu conhecimento espiritual.

Hoje Eu Me Lembrarei
Valorizar a vida espiritual.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('daae51b9-5ac6-46a3-8904-85e4d30367d6', 10, 5, 'Pouco tempo depois de chegar a Jogadores Anônimos, descobri um Poder Superior a quem escolhi chamar de Deus. Passei a acreditar que Ele tem todo o poder; e se eu ficar perto Dele e fizer o Seu trabalho bem; Ele me dará não somente o que acho que quero, mas também o que preciso. Gradualmente, estou me tornando menos interessado em mim mesmo e em meus pequenos esquemas; e ao mesmo tempo, estou me tornando mais interessado em ver de que forma posso contribuir para os outros e para a vida.

À medida que me torno mais consciente da presença de Deus, começo a perder os temores que centralizei em mim mesmo?

Hoje Eu Rezo
Que eu perceba que a única mudança mais evidente em mim mesmo –  além de minha própria noção de paz – é que deixei de me esconder atrás das paredes de meu castelo de fantasias, baixei a ponte móvel que levava à minha vila real e atravessei-a. Estou de volta entre as pessoas, interessando-me por elas, preocupando-me com elas, importando-me com o que lhes acontece. Que eu encontre minha alegria aqui nesta realidade povoada por outros seres humanos, agora que deixei para trás os antigos medos que me protegiam e as ilusões de minha própria singularidade.

Hoje Eu Me Lembrarei
O que é a vida sem as outras pessoas?', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('3c131e7d-9417-4a00-8691-0289ab791750', 12, 5, 'Segundo N. Eldon Tanner  “A prestação de serviço é o aluguel que pagamos para termos o privilégio de viver nesta terra.” Para a maioria dos membros em recuperação a prestação de serviço é por si só um privilégio, que recebemos junto com uma nova maneira de viver. O apadrinhamento, telefonemas a novos membros, ou o atendimento aos telefonemas de novos membros, algumas vezes exige muita energia e tempo. Quando estou relutante, preciso lembrar da segurança que senti quando ainda era novo em Jogadores Anônimos e reconhecer que havia pessoas dispostas a me ajudar se eu precisasse delas.

Tenho conhecimento de que meu serviço hoje é uma parte vital de minha recuperação constante?

Hoje Eu Rezo
Que eu nunca pare de lembrar que o serviço daqueles que me antecederam tornou o Programa de Jogadores Anônimos possível para mim em minha recuperação hoje. Que o serviço a qual estou disposto a prestar hoje ajude na recuperação de outros amanhã.

Hoje Eu Me Lembrarei
A prestação de serviço no Programa é um privilégio.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('b3a422c1-a9d6-47cd-a785-592e18233166', 10, 21, 'Há um mundo de diferenças entre a idéia de amor-próprio e amor  por mim. O amor-próprio é o reflexo de um ego inflado, em torno do qual – em nossa visão distorcida de nossa própria importância – tudo deve girar. O amor próprio é a base propícia para a hostilidade, arrogância, e o receptáculo para outros defeitos de caráter que nos cegam com relação a aceitar outros pontos de vista que não os nossos. Em contraste, o amor a si mesmo é uma apreciação de nossa dignidade e valor como seres humanos. O amor a si mesmo é uma expressão da auto-realização, da qual surge a humildade.

Acredito que posso amar os outros melhor quando consigo ter amor por mim?

Hoje Eu Rezo
Que Deus, que nutre o amor por mim, me ensine a ter amor por mim mesmo. Que eu perceba que na realidade,  os seres humanos mais arrogantes e intrometidos não têm confiança em si mesmos. Ao invés disso, eles  têm a tendência a ter uma dolorosa baixa auto-imagem, e uma insegurança que encobertam com a pompa e armadilhas principescas. Que Deus me mostre que quando consigo gostar de mim mesmo, estou dando o devido crédito a Ele, já que todos os seres vivos são criados por Deus.

Hoje Eu Me Lembrarei
Tentarei gostar de mim mesmo.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('3a2286db-5197-4423-845e-f22c0509adcc', 9, 1, 'Ao explicar o Passo Onze, o  Livro Vermelho de Jogadores Anônimos diz “Um contato consciente parece iniciar-se com a oração diária. O que é a oração? Para alguns ela é uma conversa pessoal com o seu poder Superior. Assim, cada um de nós pode orar como achar apropriado.” Se a primeira oração parecer difícil, você pode  “fazer de conta”, até saber como fazê-la. “Com o tempo, você passará a aceitar a prece com entusiasmo  e conhecerá as recompensas que isso lhe trará&#8230; A oração aumenta a capacidade de  lidar com” os seus problemas.

Aceito a oração e a meditação como parte de minha vida?

Hoje Eu Rezo
Louvo meu Poder Superior por minha liberdade de poder ter meu próprio entendimento de Deus. Que minha vida pertença a Deus, não importa se O considero o Pai cujas mãos e espírito eu posso tocar ao estender minhas mãos  a meu modo;  como o Espírito universal ao qual posso integrar-me à medida que os contornos duros de meu “ser” começam a se dissipar;  ou como a essência da bondade Divina e absoluta, dentro de mim. Que eu O conheça bem, não importa se o encontro dentro de mim, independente de mim, ou em todas as coisas existentes.

Hoje Eu Me Lembrarei
Agradeço a Deus, como eu O entendo, pelo entendimento que tenho Dele.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('31109b1b-99c9-47e4-8b2f-942e8a673028', 5, 8, 'No Programa de Jogadores Anônimo,  aprendi que não preciso me desculpar com ninguém por depender de Deus como eu o concebo. Na verdade, agora tenho boas razões para duvidar daqueles que acham que a espiritualidade é sinal de fraqueza. Para mim, ela é sinal de fortalecimento. Durante várias gerações, o veredicto era que raramente falta coragem a homens e mulheres de fé.Eles crêem em seu Deus.  Por isso nunca me desculpo pela minha fé em Deus, ao contrário, tento deixar que Ele  demonstre, através de minha pessoa e através daqueles ao meu redor, o que Ele pode fazer.

Faço o que digo?

Hoje Eu Rezo
Que a fé seja confirmada à medida que vejo como Deus age através de outras pessoas, desde os primórdios da humanidade. Que eu perceba que as pessoas corajosas, os criadores de milagres, as pessoas felizes, são aquelas que confessaram abertamente sua espiritualidade. Que eu perceba, mesmo agora ao olhar à minha volta, como Deus age naqueles que crêem Nele.

Hoje Eu Me Lembrarei
Observar a obra de Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('e5a3504c-0e39-4556-9a9c-73fd89f2e15b', 12, 21, 'No Programa de Jogadores Anônimos todos podemos, em nosso próprio ritmo e maneira, despertar nossa própria espiritualidade. Com ela surge não só o conhecimento profundo de que não estamos mais sozinhos e desamparados, mas também a verdadeira consciência de que conhecemos algumas verdades que agora podemos transmitir a outras pessoas, para que, talvez, essas mesmas pessoas também possam ser ajudadas.

Mantenho minha mente constantemente aberta para a consciência espiritual que certamente adquirirei à medida que pratico os Passos?

Hoje Eu Rezo
Que eu seja dedicado, mas sem ficar esperando que minha recente espiritualidade me acorde como um relógio despertador para a conscientização imediata de um Poder Superior. Isso pode surgir tão sutilmente que poderei não reconhecê-la quando a mesma acontecer. O sinal pode aparecer no desejo de praticar o Décimo Segundo Passo com alguém. Que eu saiba que quando isso acontecer eu já terei aceito os princípios do Programa de JA e terei realmente feito esforços para aplicá-lo em todas as minhas atividades diárias.

Hoje Eu Me Lembrarei
Pratique os princípios – e passe-os adiante.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('e19b02c1-c439-4a62-acc8-03f2e4787e8b', 8, 2, 'Quando começo a comparar a minha vida com a vida das outras pessoas, começo a dirigir-me ao pântano criado pela auto-piedade. Por outro lado, se eu sentir que o que estou fazendo é certo e bom, eu não estarei tão dependente da admiração e aprovação das outras pessoas. Aplausos são bons e fazem bem, mas não são essenciais para a minha satisfação interior.. Estou no Programa de Jogadores Anônimos para me livrar da auto-piedade, não para deixar que seu poder me destrua.

Estou aprendendo como os outros lidaram com os seus problemas, para que eu possa aplicar estas lições à minha própria vida?

Hoje Eu Rezo
Deus, me mantenha sempre em contato com o lugar de onde vim e os meus novos objetivos que fui encorajado a traçar. Que eu pare de agir para que o público me aplauda, já que eu mesmo sou capaz de aplaudir e aprovar os meus próprios atos se eu sentir que mereço. Ajude-me a ser atraente por dentro para que isto possa transparecer naturalmente, ao invés de ficar me enfeitando por fora, só para causar efeito. Estou cansado das fantasias e maquiagens do palco, Deus, me ajude a ser eu mesmo.

Hoje Eu Me Lembrarei
Alguém já ME viu?', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('d7404afb-d1ec-4171-b8ed-f07789d5a658', 12, 11, 'Antes de chegar ao Programa de Jogadores Anônimos – na verdade, antes de saber de sua existência – deixava-me levar de uma crise à outra. Ocasionalmente, tentava usar minha vontade para traçar um novo plano; entretanto, como um barco sem rumo, eu, mais uma vez, inevitavelmente, encalhava nas rochas de meu próprio desespero. Em contraste, hoje, recebo a orientação de meu Poder Superior. Algumas vezes, a única resposta que recebo é um sentimento de paz ou o conforto de que tudo vai bem.

Apesar de haver momentos de espera antes que eu possa ter resultados, ou antes que alguma ajuda direta ocorra, tentarei permanecer confiante de que as coisas estão dando certo da melhor maneira possível para todos os envolvidos.

Hoje Eu Rezo
Que eu não espere uma comunicação verbal imediata com meu Poder Superior, como acontece com cartões postais já auto-endereçados e selados. Que eu tenha paciência, ouça e sinta a presença de Deus. Que eu aceite meu novo sentimento de calor contagiante e de serenidade, como uma das formas usadas por Deus para me garantir que, finalmente, estou fazendo as escolhas certas.

Hoje Eu Me Lembrarei
Paciência: a mensagem de Deus virá.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('4adf23c8-96e2-4c80-8e01-fc53f392fe2d', 3, 30, 'Completa humildade significa libertação de mim mesmo, e libertação das exigências tão pesadamente impostas a mim pelos meus defeitos de caráter. Humildade significa o desejo de conhecer e realizar a vontade de Deus. Embora eu não ache que serei capaz de atingir tal sabedoria, somente manter este pensamento em meu coração, ajuda-me a descobrir onde me situo no percurso em busca da humildade. Reconheço que minha jornada em busca de Deus mal começou. À medida que reduzo minha importância, é possível que eu até ache graça em meu antigo orgulho e divagações do meu ego.

Levo-me demasiadamente a sério?

Hoje Eu Rezo
Que eu seja capaz de dar a  proporção exata à grande dimensão de um sintoma de adicção, pela simples comparação da minha impotência com o poder de Deus. Que eu avalie o significado do Poder Superior comparando-O com a minha fragilidade humana. Que eu reduza o meu ego ao seu devido tamanho, para que possa  me livrar de minhas necessidades de luxos ou rompantes ou idéias secretas de grandezas.

Hoje Eu Me Lembrarei
Humildade é liberdade.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('91023bd3-3f91-4665-bf5d-1547c9edf977', 10, 11, 'Quando eu rezo a Oração da Serenidade, algumas vezes repetidamente, deixo ocasionalmente de perceber o significado da oração mesmo quando estou repetindo suas palavras. Assim, preciso refletir sobre o significado de cada frase à medida que faço a oração, seja em voz alta ou silenciosamente. À medida que me concentro no significado, meu entendimento se amplia, juntamente com minha capacidade de compreender a diferença entre o que posso modificar e o que não posso.

Percebo que a maioria das melhorias em minha vida surgirá da mudança de minhas próprias atitudes e do meu comportamento?

Hoje Eu Rezo
Que o meu Poder Superior me mostre novos e mais profundos significados na Oração da Serenidade cada vez que a rezo. À medida que a aplico nas situações de minha vida, que a sua verdade seja repetidamente clara para mim. Que eu me conscientize de que a serenidade, a coragem e a sabedoria representam tudo que necessito para lidar com a vida, mas que nenhuma das três terá valor, a não ser que surjam de minha crença em um Poder Superior.

Hoje Eu Me Lembrarei
A fórmula de Deus para a vida: serenidade, coragem e sabedoria.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('29fe89a7-198f-4e1e-bdbd-8cc0fac7c170', 5, 7, 'Se eu acreditar que é inútil esperar qualquer melhora em minha vida, estarei duvidando do poder de Deus. Se eu acreditar que tenho motivos para me desesperar, estarei confessando meu fracasso pessoal, porque eu realmente tenho o poder de mudar a mim mesmo; nada poderá impedir que eu o faça, a não ser minha própria falta de vontade. No Programa de Jogadores Anônimos, posso aprender a fazer uso do imenso e inesgotável poder de Deus – se estiver disposto a sempre ficar consciente da presença de Deus.

Ainda penso que a minha satisfação na vida depende do que outra pessoa possa fazer por mim?

Hoje Eu Rezo
Que eu submeta a minha vida à vontade de Deus, não aos caprichos e indiferença de outros. Quando contava exclusivamente com o que as outras pessoas realizassem, pensassem ou sentissem em relação à minha própria felicidade, não era mais do que um espelho ordinário refletindo a vida dos outros. Que eu permaneça próximo de Deus em todas as coisas. Valorizo a  mim mesmo, porque Deus me valoriza. Que eu dependa exclusivamente de meu Poder Superior.

Hoje Eu Me Lembrarei
Estar próximo de Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('9e813e9a-74e2-4da0-a143-02692bca810a', 7, 29, 'O sentimento de auto-piedade, pelo qual já passamos uma vez ou outra, é uma das emoções mais feias que um ser humano pode experimentar. Nem podemos nos dar o direito de admitir à alguém que estamos mergulhados em auto-piedade. Odiamos quando nos dizem que demonstramos isso; logo argumentamos que temos um outro sentimento diferente. Chegamos ao ponto de esconder – espertamente – de nós mesmos, o fato de que estamos passando por um estado de “carência espiritual”. Mas ao mesmo tempo, rapidamente achamos mil e uma razões “válidas” para sentirmos pena de nós mesmos.

Às vezes gosto de esfregar sal nas minhas próprias  feridas?

Hoje Eu Rezo
Que eu reconheça as emoções que estou sentindo pelo que elas são. Se não puder identificá-las por mim mesmo, que eu conte com outras pessoas  que sabem o que é ser um acumulador de sentimentos. Que eu possa entrar em contato com os meus sentimentos através do contato com o meu Poder Superior e com os outros no meu grupo.

Hoje Eu Me Lembrarei
Manter o contato.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('9e7f89af-df26-4aaf-9311-bdc18e508e78', 4, 22, 'Que meus olhos fiquem cada vez mais abertos à medida que freqüento as reuniões do Programa de Jogadores Anônimos. Os problemas das outras pessoas fazem com que os meus pareçam pequenos, mas mesmo assim elas estão enfrentando esses problemas com coragem e confiança. Há pessoas que estão presas em situações tão ruins quanto as minhas, mas elas resistem aos seus problemas com maior firmeza. Ao freqüentar as reuniões encontro muitos motivos para estar agradecido. Meu fardo  começou a ficar mais leve.

Espero soluções fáceis para os meus problemas, ou peço somente a orientação para um modo melhor?

Hoje Eu Rezo
Deus, faça com que o Programa de JA seja o meu modo de vida. Suas metas são as minhas. Seus membros são meus verdadeiros amigos. Que eu transmita aos outros a capacidade para lidar com a vida que aprendi no programa. Que a reviravolta e a transformação ocorridas  em minha vida, inspire outras pessoas, como outras me inspiraram antes.

Hoje Eu Me Lembrarei
Que eu seja grato.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('01b8e92e-d41c-4b9e-8420-9ff898728b3d', 3, 28, 'Precisamos pensar seriamente nas pessoas doentes que ainda estão por chegar a Jogadores Anônimos. À medida que tentam voltar a ter fé e a viver, queremos que encontrem tudo o que encontramos no Programa – e até mais, se possível. Nenhum bem, vigília ou esforço para preservar a eficácia constante e a força espiritual do Programa poderá ser considerado demasiado, se puder nos colocar completamente disponíveis para o dia de sua chegada.

Até onde vai o meu respeito pelos Passos do Programa?

Hoje Eu Rezo
Que Deus me ajude a fazer a minha parte com relação a tornar o Grupo coeso para aqueles que ainda sofrem do jogo compulsivo, respeitando os Passos de Recuperação e de Unidade, que farão Programa funcionar para aqueles que ainda virão. Que o Programa seja uma “volta ao lar” para os que sofrem da doença do jogo compulsivo. Que encontremos soluções em comum para os problemas em comum causados por essa doença.

Hoje Eu Me Lembrarei
Fazer a minha parte.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('98f2ea05-b27f-411b-9d34-530d810a8bb7', 7, 6, 'Algumas pessoas no Programa de Jogadores Anônimos sentem que  não podem fazer aquilo que querem fazer. Duvidam de sua própria capacidade. Mas na verdade, todas as pessoas têm habilidades escondidas. Nós somos filhos de Deus, o que deveria nos dar uma forte idéia sobre a natureza infinita das nossas capacidades. Como seres espirituais, somos ilimitados. Podemos considerar isto como verdadeiro em relação a pessoas que brilham  em uma área em particular. Posso comparar  minhas próprias conquistas com a de outra pessoa e me sentir desencorajado. Mas a única comparação que preciso ou devo fazer é comigo mesmo.

Sou uma pessoa melhor e mais produtiva nos dias de hoje?

Hoje Eu Rezo
Que eu entenda que sou filho de Deus. E que Esse pai amoroso promete me dar o que preciso, não o que gostaria de ter, pois essa é a Sua maneira de ensinar-me a ser o que sou, não o que eu sonho que deveria ser. Como ser espiritual, posso realmente me tornar um ser produtivo, quem sabe até fazer coisas que no passado achava-me incapaz de fazer, sem a grandeza de jogador, levando-me a nutrir uma falsa confiança.

Hoje Eu Me Lembrarei
Comparar-me com o meu ”eu” dos velhos tempos.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('5e3e30ac-1088-4b5a-a387-3fb9615bcebf', 4, 1, 'Se não quiser recair, evitarei lugares e situações que me levam à recaída. Para o jogador isto significa evitar rodas de poker  e hipódromos,  ou qualquer outro lugar de jogo. Algumas situações emocionais também podem servir de motivos para recaídas, da mesma forma que a indulgência a antigos pensamentos, tais como cultivar o ressentimento até que tome proporções incontroláveis.

Levo os princípios do Programa de Jogadores Anônimos a todos os lugares aonde vou?

Hoje Eu Rezo
Que eu aprenda a não me testar demasiadamente, como se estivesse “querendo que a recaída acontecesse”; dando uma "passadinha" no Cassino, no Bingo ou nas corridas. Esse “teste” pode ser perigoso, principalmente se estiver disposto a fazê-lo, não somente por desejar o antigo objeto de desejo de minhas adicções, mas também por outros; por ainda estar prisioneiro de uma adicção cuja responsabilidade moral ficou reduzida a zero.

Hoje Eu Me Lembrarei
Evitar lugares que levem à recaída.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('60ac1a24-b7eb-4bb7-a249-80aa67fb4fe4', 6, 2, 'No processo de aprender a amar a mim mesmo e, por outro lado, amar os outros voluntariamente sem qualquer condição, comecei a entender as palavras de Sto. Agostinho “O amor destrói o que somos, para que possamos ser o que não fôramos” Cada vez mais percebo a enorme força desse tipo de amor no Programa de Jogadores Anônimos; para mim as palavras “nós nos importamos” também significam “nós amamos”.

Só por hoje, tentarei ter apreço em todos os pensamentos e ações?

Hoje Eu Rezo
Rezo para que eu perceba a grandeza e a força do amor que encontro em Jogadores Anônimos. Que meu próprio amor seja acrescentado a essa grande energia do amor que pertence a todos nós. Que eu me importe de todo o meu coração que meus companheiros se mantenham abstinentes do jogo e que estejam aprendendo a viver tranqüilamente e com criatividade. Que eu nunca duvide que eles se importem comigo da mesma forma.

Hoje Eu Me Lembrarei
Seu eu me importar, acontecerá', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('345c4609-3345-4dc1-bea9-79411edd3952', 1, 3, 'Minha compulsão pelo jogo tem um peso triplo, pois me afeta fisicamente, mentalmente e espiritualmente. Como jogador compulsivo, eu estava totalmente sem contato, não só comigo mesmo, mas com a realidade. Um dia infeliz atrás do outro,  como um animal preso a um moinho de pedra, eu repetia meu auto-destrutivo padrão de vida.

Comecei a me distanciar de minhas  idéias antigas? Só por hoje posso me ajustar ao que é real, ao invés de tentar ajustar tudo  aos meus próprios desejos?

Hoje Eu rezo
Rezo para que eu não seja novamente arrastado para baixo, nessa espiral destrutiva que me afastou de mim mesmo e da realidade do mundo ao meu redor. Rezo para que eu possa  me ajustar às pessoas e às situações como elas são, ao invés de sempre  sem sucesso e com frustrações intermináveis, tentar amoldá-las aos meus próprios desejos.

Hoje Eu Me Lembrarei
Só posso mudar a mim mesmo.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('4ca4c096-3eae-4852-9eaa-0e90b2b6e9d0', 6, 12, 'Muitos de nós encontramos dificuldades para nos livrarmos dos estragos  da culpa. Nos primeiros dias no Programa de Jogadores Anônimos, ou eu entendia mal alguns Passos, ou tentava aplicá-los depressa e ansiosamente demais. Por causa disso, aumentei meus sentimentos de culpa e de baixa estima, ao invés de me libertar, que é a intenção dos Passos. Logo, entretanto,  pelo menos passei a querer perdoar a mim mesmo, buscando um recomeço. Trabalhei todos os Passos de busca e limpeza em nosso Programa, como devem ser feitos, ao invés de fazê-los a partir de uma visão inferior de ódio e culpa castradores.

Fiz as reparações a mim mesmo?

Hoje eu Rezo
Que eu perdoe a mim mesmo, como Deus me perdoou. Que eu tenha consciência de que se estiver me atendo a um baú cheio de culpas, não estarei seguindo os exemplos que  Deus tem me mostrado. Se meu Poder Superior, que tem me ensinado o ato de perdoar, encaminhando-me a este lugar para minha melhora, pode perdoar-me, eu também posso. Não devo relutar em dar a mim mesmo o que Deus tão  generosamente me ofereceu.

Hoje Eu Me Lembrarei
Deus perdoa. Eu também devo perdoar.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('80769c00-6b00-4be1-b192-b985a351b6a8', 2, 18, 'Aprendemos em Jogadores Anônimos que não se pode punir os outros sem antes punirmos a nós mesmos, A descarga das tensões, mesmo que justificadas, através de formas punitivas, deixa um rastro de amargura e dor. Essa foi a minha monótona história antes de chegar a Jogadores Anônimos. Então, em minha nova vida, farei o possível para levar em consideração os benefícios a longo prazo que recebi ao assumir e identificar minhas emoções, para só depois expressá-las.

A voz de Deus tem alguma chance de ser ouvida em meio aos meus gritos de censura?

Hoje eu Rezo
Que eu evite as acusações e os diálogos destruidores do ego. Se estiver com raiva, que eu tente dirigir minha raiva ao que a pessoa fez, e não ao que a pessoa é. Que eu evite as degradações, tripudiar os defeitos  de caráter ou o abuso impensado. Que eu procure o meu Poder Superior para me mostrar a saída.

Hoje Eu Me Lembrarei
Lidar com a raiva de forma adequada', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('fe82f2d5-a40c-4dd8-8474-a585a8283261', 6, 10, 'Quando menos esperar, minha mente aguçada de adicto tentará me levar de volta às minhas antigas idéias e modos de vida. Na verdade, minha mente é  especializada em criar e alimentar dentro de mim sentimentos negativos – como a inveja, o medo, a ansiedade ou a culpa. No momento em que eu detectar sinais de quaisquer desses sentimentos nocivos, tenho que lidar com eles. Caso contrário, quanto mais eu pensar neles mais fortes eles se tornarão; e quanto mais fortes se tornarem mais pensarei neles – até chegar à obsessão.

Quando  os sentimentos negativos surgem, consigo  identificá-los, assumí-los como meus,  e  livrar-me deles?

Hoje Eu Rezo
Eu deveria saber – e nunca esquecer disso – que a melhor maneira  de deixar  que meus sentimentos  afetem o que há de melhor em mim é fingir que eles não existem. Agindo como uma criança mimada, eles “aprontam” quando são ignorados. Mas também como crianças mimadas,  esses sentimentos existem, são meus e sou responsável por eles. Que eu aprenda a prestar atenção aos meus sentimentos, mesmo que, algumas vezes, eu prefira acreditar que eles não me pertençam.

Hoje Eu Me Lembrarei
Identifique-os,  assuma-os como seus e livre-se  deles.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('8591fbaf-3afd-44cb-acdd-7af443af9e8e', 5, 9, '“A verdadeira coragem é quando fazemos sem testemunhas algo que faríamos se o mundo  inteiro estivesse nos observando,” escreveu La Rochefocauld. À medida que progredimos no Programa de Jogadores Anônimos, reconhecemos o medo persistente da forma real, e nos tornamos capazes de lidar com ele. Começamos a identificar cada uma das adversidades como uma oportunidade dada por Deus para desenvolvermos um tipo de coragem gerada não pela pretensa coragem, mas pela humildade.

Tenho consciência de que assobiar para manter minha coragem é meramente uma boa prática do próprio assobio?

Hoje Eu Rezo
Que eu encontre coragem em meu Poder Superior. Como todas as coisas são possíveis graças a Deus, preciso ser capaz de superar os medos insidiosos que me perseguem – medos muito freqüentes de perder alguém ou alguma coisa que se tornou importante em minha vida. Rezo para que minha própria disposição de deixar que as coisas tomem o seu rumo, supere esses medos.

Hoje Eu Me Lembrarei
Orar significa mais do que assobiar no escuro.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('bddbae14-f8a9-4ad3-b618-fda9833402a0', 2, 12, 'Sou grato aos meus amigos do Programa de Jogadores Anônimos. Neste exato momento tenho consciência das bênçãos da amizade – as bênçãos das reuniões, da comunhão, do sorriso, do aprendizado e de estar disponível quando necessário. Neste exato momento sei que, para  ter um amigo, preciso ser um amigo.

Eu me dedicarei, hoje, a ser melhor amigo de mais pessoas? Eu me empenharei, neste dia – em pensamentos, palavras e atitudes -  em descobrir que tipo de amigo eu sou?

Hoje Eu Rezo
Que eu retribua, na mesma moeda,  a amizade que busquei tão intensamente na Irmandade de Jogadores Anônimos. Após anos levando minha solitária existência com relacionamentos superficiais, que eu novamente aprenda as alegrias recíprocas da atenção e da comunhão.

Hoje Eu Me Lembrarei
Seja um amigo.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('fe423a7d-73b8-4084-be83-110de9491a2f', 11, 20, 'Passei a medir o sucesso de forma totalmente nova. Meu sucesso hoje não se limita às posições que escalei na sociedade ou ao sucesso econômico. O sucesso hoje é meu, não importa onde ocorra, no momento em que chego ao poder de Deus dentro de mim e permito que seja aberto um canal para a expressão de Sua obra. O espírito do sucesso expressa-se em mim sob a forma de um maior entendimento e compreensão, de idéias criativas e da prestação de serviços – com o uso eficiente de meu tempo e energia e o esforço para cooperar com as outras pessoas.

Tentarei manter minha mente focalizada na realização daquilo que dentro de mim representa o poder legado por Deus para se chegar ao sucesso?

Hoje Eu Rezo
Que eu desenvolva uma nova concepção de sucesso baseado nas qualidades que são produzidas pelo banco cheio de tesouros de Deus. Para sacar desse banco, só preciso olhar para dentro de mim. Que eu perceba que as riquezas de Deus são as únicas que têm garantia absoluta, porque são infinitas. Que eu procure por minha segurança no banco de Deus.

Hoje Eu Me Lembrarei
O sucesso “espiritual” é a minha garantia.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('f61f08fa-2279-4fc7-b3d7-70946c5b20dd', 6, 18, 'Hoje, eu acredito que tenha o direito ao progresso espiritual. Tenho o direito de amadurecer emocionalmente. Tenho o direito de ter prazer com minha própria companhia, o que me torna uma companhia mais agradável para os outros. Também tenho o direito de ficar profundamente e inteiramente disposto a fazer as reparações a todos aqueles a quem prejudiquei. Como agora consigo me aceitar da forma como sou, posso aceitar as outras pessoas como são – não inteiramente, mas muito mais do que no passado.

Comecei a me tornar amigo de Deus e em conseqüência de mim mesmo?

Hoje Eu Rezo
Deus, mostre-me que está certo gostar de mim mesmo, mesmo enquanto tento reparar meus antigos erros e recomeçar a partir de ruínas. Que eu continue a me convencer de que agora sou diferente. Mudei. Sou melhor, mais sábio e mais saudável. Fiz algumas escolhas corretas. Como uma “nova pessoa”, que eu tenha mais facilidade para fazer as reparações pelo que ocorreu há muito tempo atrás e em outra fase espiritual. Que aqueles a quem causei danos também tenham maior facilidade para aceitar minhas reparações.

Hoje Eu Me Lembrarei
Está certo gostar de mim mesmo.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('4fd0dbb9-e0f3-49e8-8d30-7a4f0afa7486', 10, 17, 'Em toda minha vida procurei por aquilo que me faria feliz, viajando por muitas estradas, depressa ou devagar. A maioria das estradas que escolhi eram as mais fáceis, caminhos mais leves, mas os destinos não eram satisfatórios. Sempre voltava e escolhia uma outra – mais uma vez a estrada mais fácil de percorrer. Eu achava que essas estradas eram atalhos, mas ao invés disso, elas me levavam para distante de meus objetivos. Quando finalmente cheguei a Jogadores Anônimos, esse foi o único caminho que me restou.

Apesar dos buracos ou lombadas ocasionais, estou convencido de que estou finalmente no caminho certo? Viajo no Programa de JA por minha própria vontade?

Hoje Eu Rezo
Hoje eu me desperto com uma escolha. Minhas ações e pensamentos estão me levando para o caminho da recaída, ou estou tomando o caminho da estrada da recuperação? Que eu peça orientação ao meu Poder Superior, porque não sou mais o único guia de minha vida. Que eu não siga qualquer outra placa sinalizando progresso, além da do sorriso que eu possa honestamente esboçar, do olhar sincero e da mente que possa, finalmente, chegar à realidade. Que minha própria alegria seja a resposta para minha pergunta: - escolhi o caminho certo?

Hoje Eu Me Lembrarei
Os milagres marcam nosso progresso.  Quem necessita algo além disso?', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('2d0e1fb5-c8a8-4257-86c3-980f97ecedc1', 2, 10, 'O fundador da IBM, T. J. Watson, disse: “O sucesso está no  extremo oposto do fracasso.” Para a maioria dos jogadores compulsivos, nada poderia ser mais verdadeiro. Sem um fracasso após outro, jamais seríamos capazes de perceber que perder uma aposta não era a causa dos problemas em nossas vidas mas, sim, fazer a primeira aposta. Somente após havermos atingido ou superado nosso doloroso limite para suportar  fracassos, tornamo-nos aptos a perceber que, por outro lado, ter sucesso não significa ganhar: ter sucesso significa não apostar. Assim, o sucesso depende muito mais da ação – a opção de não jogar – do que da reação.

Tenho fé que, se meus fracassos ficarem para trás, meu sucesso com certeza estará por vir?

Hoje Eu Rezo
Que eu compreenda que somente através de meus fracassos terei a chance do ter sucesso. Que eu perceba com os exemplos de outros no Programa, que quando encaramos e corrigimos os fracassos do passado, isso pode se transformar em uma mais base mais sólida para uma nova vida, do que qualquer chance de ganhos.

Hoje Eu Me Lembrarei
Os fracassos podem servir de alicerce para a recuperação.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('ed3222fb-fa82-4d0f-86cf-1903ac5fa6fc', 3, 8, 'A simples mudança de meu comportamento e o que digo ou faça, não prova que tenha havido uma mudança em minha  verdadeira atitude interior. Estou me enganando se acreditar que eu possa, de alguma forma, mascarar completamente meus sentimentos verdadeiros. Eles, de alguma forma, virão à tona, prolongando minhas dificuldades de me relacionar com outras pessoas. Tenho que evitar meias-medidas para livrar-me das emoções conflitantes que tenho tentado esconder.

Fiz um inventário honesto de mim mesmo?

Hoje Eu Rezo
Que eu saiba que os sentimentos virão à tona, de alguma forma – às vezes mal disfarçados - como comportamentos que nem sempre consigo entender, mas que talvez sejam mais aceitáveis para mim do que a emoção que deu origem a eles. Que eu seja, completa e vigilantemente honesto, comigo mesmo. Que eu adquira essa perspectiva que surge, quando nos submetemos a um Poder Superior.

Hoje Eu Me Lembrarei
Os sentimentos podem surgir por “caminhos tortuosos”.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('0778d98a-3f01-4372-9239-ddff5828f8c6', 12, 17, 'Atualmente, cada vez mais,  à medida que progrido em minha recuperação, pareço ficar calmamente esperando ouvir a voz inconfundível de meu Poder Superior dentro de mim. A oração está se tornando uma rua de duas mãos – de procurar e escutar, de procurar e encontrar. Para mim, uma das passagens favoritas da Escritura nos diz ! “Espere e descubra que eu sou Deus.”

Presto atenção de forma serena e carregada de amor ao meu Poder Superior, certo de que o conhecimento consciente de Sua vontade poderá chegar até a mim?

Hoje Eu Rezo
À medida que conheço meu Poder Superior, que eu aprenda as melhores maneiras – para mim – de alcançá-Lo e ouvi-Lo. Que eu comece a sentir a oração, sem simplesmente escutar o som de minha própria voz verbalizando-a. Que eu sinta os fortes sintomas de minha fraqueza humana à medida que  Sua Bondade se torna parte de mim. Que eu perceba que sou único com Ele.

Hoje Eu Me Lembrarei
Sinta a quietude de Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('a914edc2-7c2f-4dd3-9044-3577c6f2fba7', 1, 14, 'Admiti ser incapaz de vencer sozinho a luta contra o Jogo. Então, finalmente, comecei a aceitar o fato crucialmente importante de que depender de um Poder Superior poderia me ajudar a alcançar o que sempre me pareceu impossível. Parei de correr. Parei de lutar. Pela primeira vez, comecei a aceitar. E pela primeira vez também, comecei a ser realmente livre.

Tenho consciência de que não importa o tipo de sapatos que estou usando quando estou fugindo?

Hoje Eu Rezo
Que eu conheça a liberdade que advém da rendição a um Poder Superior – o mais importante tipo de rendição -  que não significa  “ceder”, ou “desistir”, mas sim submeter minha vontade à vontade de Deus. Como um cansado fugitivo da ordem espiritual, que eu pare de me esconder, de me  esquivar, de correr. Que eu encontre a paz na rendição, no conhecimento de que Deus deseja que eu seja um ser  completo e saudável; e Ele me mostrará o caminho.

Hoje Eu Me Lembrarei
Primeiro a rendição, depois a serenidade.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('94bf7cf7-e384-4a5c-bbb7-2b84bb1fce54', 8, 15, 'Geralmente comenta-se que não se pode saber o conteúdo de um livro pela capa. Para muitos de nós a “capa”  ou os relatos superficiais não pareciam tão ruins. À princípio pensávamos,  que faríamos o inventário “de estalo”. Entretanto, à medida que prosseguíamos, nos decepcionávamos ao descobrir que nossas “capas” pareciam relativamente isentas de culpa,  somente porque cobríamos nossos defeitos  com camadas auto-enganadoras. Por este motivo, uma busca pessoal pode se tornar um longo processo, que precisa continuar enquanto permanecermos cegos com relação aos defeitos que funcionavam como armadilhas, levando-nos à adicção e ao sofrimento.

Tentarei encarar a mim mesmo como realmente sou, corrigindo o que me impede de ser a pessoa que desejo ser?

Hoje Eu Rezo
Que Deus me ajude em minha busca por minha alma, já que escondi meus defeitos tão bem de meus amigos, família e especialmente de mim mesmo. Se me sentir como se tivessem pecado comigo mais do que pequei com os outros,  devo considerar isto como a pista de que preciso para realizar uma busca maior de meu verdadeiro eu.

Hoje Eu Me Lembrarei
Fazer o inventário de mim mesmo é construir algo para o meu futuro.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('f3e94ca8-a6ee-4ac2-9dc4-369d31ea9dc7', 2, 11, 'Sempre poderei alcançar força e satisfação ao saber que pertenço a uma Irmandade de alcance mundial. Milhares de pessoas como eu, trabalhando juntas com o mesmo propósito. Nenhum de nós jamais precisará estar sozinho novamente, porque cada um de nós trabalha à sua maneira, para o bem dos outros. Estamos unidos por um problema comum, que pode ser solucionado através do amor, da compreensão e da prestação de  serviço mútuo. O Programa de Jogadores Anônimos – como diz o refrão de hino antigo – funciona pela graça de Deus.
Agradeci hoje a Deus por haver me ajudado a encontrar JA, que está me mostrando o caminho para uma nova vida?

Hoje Eu Rezo
Que meus agradecimentos cheguem todos os dias a Deus, por dissipar a  solidão que eu mesmo causei,  por amenizar o meu estoicismo, por guiar-me a uma fonte ilimitada de amizade no Programa de Jogadores Anônimos.
Hoje Eu Me Lembrarei
Eu tenho um mundo de amigos.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('7c748e70-5886-42c7-927b-a1b55fdaa811', 12, 31, 'Deus, concedei-me a SERENIDADE para aceitar as coisas que não posso modificar; CORAGEM para mudar aquelas que posso; e SABEDORIA para perceber a diferença – vivendo um dia de cada vez; aceitando as agruras da vida como um caminho para a paz; aceitando este mundo cheio de imperfeições da  forma como é, e não como eu gostaria que fosse; acreditando que meu Poder Superior transformará  as coisas ruins em boas se eu submeter  minha vontade a uma Vontade Divina.

Esforço-me acima de tudo para alcançar essas qualidades – serenidade,coragem e sabedoria – que representam as bases de minha nova vida?

Hoje Eu Rezo
Que me recorde de meus anos como uma pessoa boa, quando nada que disse ou fiz foi em vão. Nenhuma experiência – mesmo que tenha parecido insignificante – foi em vão. O sofrimento me proporcionou a capacidade de sentir felicidade; os tempos ruins fizeram-me apreciar os bons; aquilo que via como fraquezas se tornaram minhas maiores qualidades. Agradeço a Deus pelo ano de crescimento.

Hoje Eu Me Lembrarei
A esperança é meu “saldo positivo a ser levado adiante” – para um novo ano onde as coisas boas e ruins serão computadas.

Tradução oficial do original em inglês com a aprovação do Comitê Nacional de Jogadores Anônimos do Brasil.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('a5e1a420-95e1-42a3-b369-92d884df4c90', 10, 31, 'O mistério do ego: sofrido quando inflado e sofrido para desinflar, sempre me impedindo de trabalhar com total dedicação ao Programa de Jogadores Anônimos. Mesmo tendo conhecimento da verdade, eu volto, às vezes com demasiada freqüência, às antigas e conhecidas idéias que me levaram aos limites do desespero. É necessário muito trabalho para encolher o ego e algumas vezes ele pode inflar-se sem meu conhecimento. Sempre pensei que meus esquemas de jogo funcionariam; mas eles nunca funcionaram. Eu duvidava que JA funcionaria; e ele funciona – um dia de cada vez.

Estou disposto, só por hoje, a abandonar aquelas antigas idéias e depender do modo de JA?

Hoje Eu Rezo
Que eu saiba que o ego inflado não é adequado para um jogador compulsivo como eu em recuperação. Ele esconde de mim os meus defeitos. Ele afasta as pessoas e fica em meu caminho impedindo–me de ajudar outras pessoas. Ele interrompe meu progresso porque me faz pensar que já fiz bastante exame pessoal e que estou “curado”. Rezo ao meu Poder Superior para que eu seja bastante realista para aceitar meu sucesso no Programa de JA sem deixar que o orgulho tome conta de mim.

Hoje Eu Me Lembrarei
O orgulho pode interromper o progresso.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('fda11019-061a-4463-92f6-26975ae40fd3', 12, 18, 'Estou aprendendo – lentamente demais algumas vezes – que quando abandono a batalha fadada à derrota por levar a vida à minha maneira, recebo como resultado a paz e muita serenidade. Para muitos de nós, este processo de aprendizagem é dolorosamente lento. Eventualmente, entretanto, percebo que há somente duas vontades neste mundo, a minha e a de Deus. Tudo que esteja sob o meu  controle direto representa minha vontade, e o que esteja além desse controle é a vontade de Deus. Assim, tento aceitar o que está além de meu controle, como a vontade de Deus para mim.

Estou começando a perceber que ao entregar a minha vontade à Vontade Divina, estou pela primeira vez vivendo sem sofrimento e sem ansiedade?

Hoje Eu Rezo
Devo esperar que minha vontade esteja de acordo com a vontade maior de Deus. Rezo para que eu perceba imediatamente quando minha vontade trava uma batalha inútil contra a Sua Vontade Divina. Que eu agora confie em Deus para orientar minha vontade de acordo com o Seu Plano de Mestre – e faça meu o Seu propósito.

Hoje Eu Me Lembrarei
Desejo que minha vontade esteja em harmonia com a vontade de Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('6ed52ba2-b9ce-44b2-a80f-6e829ebacec4', 10, 18, 'Nem em meus sonhos mais estranhos eu poderia imaginar que eu receberia as recompensas no momento em que pensei pela primeira vez em entregar minha vida ao cuidados de Deus como eu o compreendia. Agora posso deleitar-me não só com a bênção de minha recuperação, mas também com a recuperação de muitas outras pessoas que encontraram a esperança e um novo modo de vida no Programa de Jogadores Anônimos. Após todos aqueles anos de desperdício e terror, hoje me dou conta de que Deus sempre esteve do meu lado e junto a mim.

O meu melhor entendimento da vontade de Deus é uma das melhores coisas que já aconteceram comigo?

Hoje Eu Rezo
Que eu seja grato pelo contraste abençoado entre a maneira como minha vida costumava ser (Parte I) e a maneira como é agora (Parte II). Na parte I, eu era um jogador na ativa, com uma fome insaciável de estar em atividade, à deriva entre meus medos e desilusões. Na Parte II, sou o jogador compulsivo em recuperação, redescobrindo minhas emoções, aceitando minhas responsabilidades, aprendendo o que o mundo real tem a oferecer. Sem o contraste, nunca poderia sentir a alegria que sinto hoje ou sentir a tranqüila proximidade de meu Poder Superior.

Hoje Eu Me Lembrarei
Sou grato por esse contraste.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('3366e494-9438-40b0-b8e7-c1fecb4ebd4a', 6, 17, 'Tomar a decisão de assumir todas as conseqüências dos atos de nosso passado e ao mesmo tempo, assumir a responsabilidade pelo bem estar dos outros,é o verdadeiro espírito do Passo Nono. Se, por um lado,  a desculpa casual é raramente suficiente para fazer as reparações a alguém a quem prejudicamos, por outro lado, uma verdadeira mudança de atitude pode fazer maravilhas para compensar os maus tratos passados. Se privamos alguém de alguma coisa material, reconheceremos a dívida, pagando-a o mais rápido possível.

Esquecerei meu orgulho e tomarei as primeiras decisões em busca da reconciliação?

Hoje Eu Rezo
Deus, mostre-me as melhores maneiras de “fazer reparações diretas” . Algumas vezes, a simples admissão de meus erros pode ser uma reparação a alguém, diminuindo minha própria carga de culpa. Outras vezes, a restituição pode exigir alguma idéia criativa. Que eu fique totalmente consciente de que não posso fazer o Nono Passo enquanto não desenvolver alguma noção, alguma preocupação genuína sobre como os outros se sentem, junto com as mudanças em meu comportamento.

Hoje Eu Me Lembrarei
Primeiro me importo, depois peço desculpas.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('3a3fff1f-3c70-497c-aad7-26ce937983c8', 2, 17, 'Se eu ficar com raiva hoje, farei uma pausa e pensarei antes de dizer qualquer coisa, lembrando que minha raiva pode se voltar contra  mim mesmo e piorar minhas dificuldades. Tentarei me lembrar, também, de que o silêncio na hora certa pode me dar mais controle sobre as situações estressantes, o que as reações de raiva nunca poderão fazer. Nos momentos de tensões, eu me lembrarei de que não tenho poder algum sobre os outros e que somente Deus é Todo-Poderoso.

Aprendi que eu  posso sozinho destruir minha própria paz de espírito?

Hoje Eu Rezo
Que eu aprenda que posso escolher como lidar com minha raiva – em silêncio ou com explosões de raiva, com ódio, com uma briga de socos, de travesseiros, um discurso, ou um plano elaborado de “vingança” contra quem a causou, um olhar frio, uma declaração de ódio – ou uma simples declaração objetiva – Estou com raiva de você porque &#8230; (usando cerca de 25 palavras). Ou ainda, caso necessário, que eu transforme essa raiva em energia, fazendo uma caminhada, jogando boliche, jogando tênis ou limpando a casa. Rezo para que Deus me mostre maneiras apropriadas de lidar com minha raiva.

Hoje Eu Me Lembrarei
Eu estou com raiva porque &#8230;', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('4ec6e8a6-b98f-4bbd-b35a-2c3005f8d5d1', 10, 10, 'Quando permitimos que o Poder Superior assuma o controle, sem reservas de nossa parte, deixamos de ficar “ansiosos”. Quando não estamos ansiosos em relação a alguém ou a alguma situação, isso não significa que nos tornamos desinteressados ou paramos de nos importar. A verdade é justamente o oposto. Podemos nos interessar e nos importar sem ficarmos ansiosos ou temerosos. A pessoa calma, confiante, e cheia de fé traz com ela alguma coisa de positivo para qualquer situação. Ele ou ela é capaz de fazer coisas necessárias e úteis.

Estou consciente de que estarei muito melhor preparado para fazer as coisas certas e benéficas se eu eliminar os pensamentos de ansiedade  e me convencer de que Deus está no comando?

Hoje Eu Rezo
Rezo para que eu me livre da ansiedade preenchendo minha mente com uma preocupação real com as pessoas, Que eu saiba que a ansiedade não é um item que pode ser usado e descartado como um boné. Que eu aprenda que preciso ter serenidade e confiança de que Deus pode fazer o trabalho melhor do que eu – e dessa forma minha ansiedade diminuirá.

Hoje Eu Me Lembrarei
A ansiedade nunca resolveu qualquer coisa.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('b6a554fd-4b70-482d-a4d6-35bcff7a0c09', 9, 29, 'Durante as primeiras semanas ou meses em Jogadores Anônimos, nossa  instável condição emocional algumas vezes afetava nossos sentimentos com relação aos antigos amigos e à família. Para muitos de nós, esses relacionamentos cicatrizam-se rapidamente nos estágios iniciais de nossa recuperação. Para outros, o tempo de “extrema sensibilidade” parece continuar. Agora que não mais jogamos compulsivamente, precisamos reavaliar nossos sentimentos para com nossos cônjuges, filhos, parentes, empregadores, companheiros de trabalho e até vizinhos. A experiência no Programa de JA ao longo dos anos nos ensina que deveríamos evitar tomar decisões importantes no início de nossa recuperação – principalmente decisões carregadas de emoção envolvendo pessoas,

Estou me tornando mais equipado para me relacionar de forma madura com outras pessoas?

Hoje eu Rezo
Que Deus me ajude no processo de aparar as arestas, na confusão de tornar a ter sentimentos e na reavaliação de meus relacionamentos, e em todos os estágios de recomposição de minha recuperação. Que eu não me apresse em começar novos relacionamentos ou novas situações que exijam um investimento de minhas emoções – ainda não.

Hoje Eu Me Lembrarei
Nada de alianças comprometedoras cedo demais.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('240fede5-5cc8-4aff-a10d-3f121443d8c1', 10, 12, 'Muitas das pessoas que encontramos em Jogadores Anônimos irradiam um brilho especial – na alegria de viver que vemos em seus rostos e em suas posturas. Essas pessoas deixaram de lado sua obsessão pelo jogo e alcançaram a sublimação em suas vidas. Sua confiança e entusiasmo são contagiosos – principalmente para os recém chegados no programa. A coisa mais surpreendente para os novatos é que essas mesmas pessoas felizes também já se sentiram em algum período duramente oprimidas. O milagre de suas histórias de antes e de depois e de sua nova visão é a prova viva de que o Programa funciona.

O meu progresso no Programa de Jogadores Anônimos está servindo para levar a mensagem a outras pessoas?

Hoje Eu Rezo
Oro para que minha própria transformação através de JA,  da opressão à libertação, da derrota à vitória, da indiferença à preocupação, da tirania do jogo à libertação dele, sirva de inspiração aos recém-chegados, tanto quanto as drásticas mudanças nas vidas de outras pessoas serviram para mim. Que eu – como essas outras pessoas felizes na Irmandade – aprenda como ficar “nas alturas” na vida.

Hoje Eu Me Lembrarei
A vida é a maior  “altura” de todas.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('70ebae9c-531c-4f4a-b859-b31ed5476430', 12, 12, 'Nos dias de hoje, se passo por uma experiência nova e desafiadora, posso vivê-la com o espírito de confiança e fé. Graças a Jogadores Anônimos e aos Doze Passos de Recuperação, estou ciente de que Deus está comigo em todos os lugares e em todos os meus esforços. Seu Espírito está não só em mim, mas também em todas as pessoas que me cercam. Como resultado, sinto-me à vontade mesmo em novas situações e, como se estivesse em casa, mesmo entre estranhos.

Continuarei a seguir meu caminho e a crescer enquanto pratico os Programa, acreditando no amor de Deus atuando em mim e em minha vida?

Hoje Eu Rezo
Que a paz de Deus esteja comigo em todas as situações, familiares ou novas. Que Ele reconstrua a ponte instável de minha confiança. Que eu reconheça a existência de Deus em mim e em todos à minha volta. Que essa identidade recíproca com Deus me ajude para que me comunique com as pessoas com honestidade. Se posso aprender a acreditar em Deus, posso aprender a acreditar nas  pessoas que compartilham este planeta comigo

Hoje Eu Me Lembrarei
Deus me ensina como confiar.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('eabfd9a8-9431-470d-a9ed-30131507ec53', 9, 6, 'Como jogadores compulsivos na ativa, estávamos familiarizados demais com a depressão, essa grande pilha de sentimentos obscuros que pareciam obscurecer nossas mentes a intervalos regulares. Mesmo agora, quando não consigo progredir como queria, quando espero uma mudança radical em minha parte espiritual, da noite para o dia, esses sentimentos de tristeza a mim familiares podem voltar a me perturbar, se eu deixar a porta aberta para eles.

Reconheço que minhas metas de perfeição estão diretamente relacionadas à minha depressão? Admito que hoje a depressão em minha recuperação é mais fraca e mais compatível em relação ao meu poder de mudança?

Hoje Eu Rezo
Quando estiver imobilizado pela depressão, que eu vise objetivos menores, racionais – pequeninas coisas, como dizer “oi” a uma criança, lavar minha própria xícara de café, limpar minha mesa, fazer uma pequena oração. Que eu reveja meu próprio roteiro para o fracasso, para que não me leve a uma depressão ainda maior.

Hoje Eu Me Lembrarei
Visar objetivos demasiadamente altos causa o meu retrocesso.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('c795b7ce-ca2f-43df-93dd-7697bb93b3af', 12, 10, 'Já parei para pensar que o impulso de “explodir” e dizer algo rude ou até de desejar algo ruim a alguém, atingirá muito mais gravemente a mim do que à pessoa a quem dirigi o insulto, se isso for concretizado? Preciso sempre tentar acalmar meus ânimos antes de agir com impaciência ou hostilidade, porque minha mente pode tornar-se - de uma maneira particularmente verdadeira – a inimiga mais poderosa que jamais conheci.

Olharei atentamente antes de pular, pensarei antes de falar, e farei o maior esforço possível para não agir por vontade própria?

Hoje Eu Rezo
Que eu me lembre de que meus atos intempestivos e explosões, sejam elas torrentes de acusações ou insultos, ferem tanto a mim quanto à outra pessoa. Que eu evite deixar que a raiva me leve ao ponto de explodir, pelo simples reconhecimento da mesma durante os acontecimentos considerando-a como um sentimento real.

Hoje Eu Me Lembrarei
Diminua a pressão da tampa  sobre chaleira.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('62928a28-b416-4298-925b-df40f73b7fc9', 4, 9, 'A fé representa mais do que nosso maior bem; e nossa grande responsabilidade é compartilhá-la com outros. Que sempre busquemos em JA a sabedoria e a boa vontade com a qual poderemos corresponder à imensa confiança que o Doador de todas as perfeitas bênçãos colocou em nossas mãos.

Se você reza, por que se preocupa? Preocupando-se, por que reza?

Hoje Eu Rezo
Nosso Deus é uma fortaleza poderosa, um baluarte que nunca nos faltará. Que eu reconheça o valor de nossa existência e de nossa proteção. Deus nos dá o Dom da fé para compartilhá-la. Que eu a transmita a outras pessoas da melhor forma que possa e com o sentido de amor que nos foi legado.

Hoje Eu Me Lembrarei
Deus não nos faltará.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('21d43c37-9662-4dc9-b452-743533720a75', 11, 25, 'Epictetus escreveu que “se alguma coisa lhe parecer pequena; você quererá muito mais. Como as crianças,  quando colocam as mãos dentro de um pote com o gargalo estreito; esforçando-se para retirar as balas. Se enchem a mão, não podem retirá-la e então começam a chorar. Quando deixam cair algumas balas, conseguem pegar o resto. Com você também, deixe que seu desejo diminua; não cobice demais.” Não devo esperar demais de alguém, principalmente de mim mesmo. Devo aprender a aceitar menos do que achar possível; ficar disposto a aceitar e a apreciar o que recebo.

Aceito com agradecimento e satisfação o bem que me chega às mãos através do Programa de Jogadores Anônimos?

Hoje eu Rezo
Que eu procure identificar em minha alma essas pequenas frustrações de coisas não obtidas, pois as mesmas podem impedir-me de apreciar o que tenho. Se eu simplesmente puder aprender a não querer demais, não esperar demais, então quando essas expectativas não forem satisfeitas, não me decepcionarei. Que eu aceite com satisfação o que a graça de Deus me proporcionou.

Hoje Eu Me Lembrarei
Somente eu posso proporcionar a mim mesmo a “libertação do querer”.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('4a08245e-37ab-470d-8db1-0feedb9f7cd6', 1, 9, 'No passado, e mesmo algumas vezes, agora, eu automaticamente digo – Por quê eu? Principalmente, quando estou tentando aprender que meu problema principal é aceitar minhas circunstâncias atuais como realmente são;  eu mesmo como sou; e as pessoas que me cercam como realmente são. Da mesma forma que eu finalmente aceitei minha impotência perante o jogo, também preciso aceitar minha impotência perante as pessoas, lugares e coisas.

Estou aprendendo a aceitar a vida nos termos em que ela se me apresenta?

Hoje Eu Rezo
Que eu aprenda a administrar minha necessidade de controle; e minha compulsão para controlar, melhorar, organizar e rotular a vida dos outros. Que eu aprenda a aceitar as situações e as pessoas como elas são, ao invés de querer que sejam como eu gostaria que fossem. Assim, devo ficar longe das constantes frustrações que uma pessoa controladora por natureza enfrenta continuamente. Que eu esteja inteiramente disposto a eliminar este defeito de caráter de minha pessoa.

Hoje Eu Me Lembrarei
Controlar a quem controla (a mim mesmo).', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('af502371-1508-4f2d-ba5c-e95124876825', 8, 13, 'O Quarto Passo do Programa de Jogadores Anônimos sugere que façamos um minucioso e destemido inventário moral e financeiro de nós mesmos. Para alguns de nós, nenhum desafio parece ser maior; nem nada  mais difícil, do que encararmos a nós mesmos como realmente somos. Passávamos de um ato errado a outro à medida que aconteciam, sempre achando desculpas, sempre jurando que nossas qualidades em outras áreas superavam em muito os nossos defeitos. Mesmo assim, no momento em que nos tornamos dispostos a olhar diretamente e inquisitivamente para dentro de nós mesmos, fomos capazes de iluminar o lado escuro e negativo de nossas naturezas, com outra visão, determinação e graça.

Desejo abrir meus olhos e dar um passo em direção à luz do dia?

Hoje Eu Rezo
Que meu Poder Superior me impeça de seguir meus próprios caminhos se eles estiverem me afastando de mim mesmo. Nunca me recuperarei de meus erros ou dos defeitos de caráter que os motivaram, deixando que me dominem. Que eu diminua meu ritmo, dê uma reviravolta para enfrentá-los com a arma em que mais posso confiar – a verdade.

Hoje Eu Me Lembrarei
Não serei o fugitivo de mim mesmo.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('f61eba98-11ea-4624-96d3-7e8f87b765e3', 4, 16, 'Uma vez ouvi que a “mente é a aniquiladora da realidade”.  Relembrando a loucura daqueles dias de jogo, sei exatamente qual é o significado desta frase. Um dos benefícios adicionais que são importantes para mim  hoje, é o  crescente conhecimento do mundo ao meu redor, pois posso ver e absorver a realidade. Só isso já me ajuda a diminuir as dificuldades, que muitas vezes  valorizo demasiadamente,  gerando a minha própria infelicidade neste processo.

Estou adquirindo o sentido da realidade, que é absolutamente essencial à serenidade?

Hoje Eu rezo
Que eu seja beneficiado com um forte senso da realidade, ávido por ver, pela primeira vez, desde os tempos da névoa de meus piores momentos, as maravilhas e oportunidades de meu mundo. Vejo, emergindo da indiferente névoa da adicção, os objetos e rostos adquirindo novamente  foco,   e as cores, maior brilho.Que eu me encante com esta recém descoberta nitidez.

Hoje Eu Me Lembrarei
Colocar minhas realidades em foco.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('18c9512a-fdbb-47eb-8131-5c4291a13a80', 5, 14, '“Um erro muito comum – ter coragem de agir por convicção própria, é na verdade uma questão de se ter coragem para ir contra a convicção de alguém” escreve Nietzsche. O Programa de Jogadores Anônimos está me ajudando a me libertar de minhas velhas idéias, através da comunhão com outras pessoas e do trabalho dos Doze Passos. Tendo feito um destemido e minucioso inventário moral e financeiro de mim mesmo; admitido a mim mesmo e a outro ser humano a natureza exata de meus erros; e tendo me prontificado inteiramente a eliminar todos os meus defeitos de caráter – pedirei humildemente a Deus para remover todas as minhas imperfeições.

Estou tentando seguir o Programa exatamente como ele é?

Hoje Eu Rezo
Rezo para que eu continue a praticar os Doze Passos, repetindo-os sempre, se assim for necessário. O Programa tem sido eficaz na recuperação de milhares de jogadores compulsivos em todo o mundo. Ele pode funcionar comigo. Que eu faça regularmente uma pausa para verificar se estou realmente praticando o Programa de JA, como é sugerido.

Hoje Eu Me Lembrarei
Passo a passo.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('ef864147-a76d-42d0-8902-45c4fb7bc4c4', 11, 26, 'Em nossos primeiros dias de Jogadores Anônimos, nos livramos das armadilhas e dos ambientes de jogatina. Tínhamos que ficar livre deles porque sabíamos que eles certamente nos matariam. Nos livramos das situações, mas não conseguimos nos livrar de nosso vício enquanto não continuamos as mudanças em nossas atitudes. Assim, também tivemos que atirar pela janela nossa auto-piedade, nossa auto-justificativa, nossas certezas, nosso falso orgulho e nossa obstinação. Tivemos que abandonar a escada bamba que funcionaria pretensamente como uma maneira fácil de ganhar dinheiro, propriedades e prestígio. Além disso tivemos que assumir responsabilidades. Para continuarmos simplesmente vivos, tivemos que abrir mão de nossos mais conhecidos dotes – nossa ambição cega e nosso orgulho irrealista, para podemos adquirir humildade suficiente e respeito próprio.

Estou realmente livre dos pesos e das correntes que no passado me prendiam?

Hoje Eu Rezo
Devo dar crédito ao meu Poder Superior não somente por ter removido minha compulsão pelo jogo mas também por ter me ensinado a eliminar minha antiga personalidade exigente que me afastava de minha espiritualidade de meus relacionamentos pessoais. Sou completa e profundamente agradecido por todas as coisas que aprendi que ainda preciso aprender, por minha própria fé e pela Graça de Deus.

Hoje Eu Me Lembrarei
Gratidão pela graça de Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('48f3e54e-ea70-4e7c-bf4c-67e408bf20d4', 6, 11, 'A culpa é uma arma afiada no arsenal do Jogador Compulsivo. Podemos usar essa arma contra nós mesmos de muitas maneiras sutis, empunhando-a com destreza, por exemplo, tentando nos convencer de que o Programa de Jogadores Anônimos não funciona de verdade. Preciso sempre me proteger contra a culpa e as auto-acusações relativas ao meu passado. Se necessário, precisarei “me perdoar” novamente com mais freqüência aceitando-me como um misto do bem e do mal.

Estou tentando atingir o desenvolvimento espiritual, ou optei por não aceitar nada menos do que a  impossibilidade humana da perfeição espiritual?

Hoje Eu Rezo
Ocasionalmente, devo fazer uma busca interna por quaisquer vestígios de culpa, que estejam aos poucos minando-me por dentro , pois isso poderá arruinar os meus propósitos se não eu não estiver atento Que eu pare de me punir e relembrar minhas próprias imperfeições – todas as características negativas que me afastam do meu “eu” ideal e “perfeito”. Que eu pare de tentar me tornar um ser inatingível e perfeito de forma não humana, e,  ao invés disso,  tornar-me somente um ser espiritualmente  completo.

Hoje Eu Me Lembrarei
Sou humano – parcialmente bom, parcialmente não-tão-bom.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('71fd3955-b2ff-435c-a7ba-7829c267e939', 5, 3, '“Apoiar-me em uma só perna e provar a existência de Deus são coisas muito diferentes de ajoelhar-me para  agradecê-Lo, ” escreveu Soren Kiekegard. É  minha confiança no Poder Superior que,  atuando em mim, hoje me liberta e ativa minha capacidade de tornar a minha vida uma experiência mais satisfatória e proveitosa. Não posso realizar isto dependendo somente de mim e de minhas próprias idéias limitadas.

Comecei a agradecer a Deus todos os dias?

Hoje Eu Rezo
Que eu constantemente me lembre que é minha crença no Poder Superior que aciona o dispositivo que libera este poder em mim. Sempre que esqueço minha fé, esse poder é anulado. Rezo para que minha fé nunca esmoreça, para que este poder – concedido por Deus e regenerado por minha própria crença – esteja sempre disponível a mim como fonte de energia.

Hoje Eu Me Lembrarei
A fé regenera o poder dado por Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('d3ce656f-f6c9-4bf4-85db-9c93a3596711', 4, 23, 'Não importa  o que pareça ser nossa necessidade ou problema pois podemos encontrar um motivo de satisfação ou alguma coisa pela qual somos gratos. Não é a Deus que precisamos agradecer e sim sermos gratos. A gratidão abre novas portas para o bem em nossa vida. Ela cria um novo coração e um novo espírito em nós.

Continuo consciente das muitas bênçãos que me chegam a cada dia? Lembro-me de ser agradecido por elas?

Hoje Eu Rezo
Que Deus me conceda o sentimento de gratidão. Quando expresso meu agradecimento, mesmo que de forma sutil, a Deus ou a outro ser humano, não estou sendo grato somente a Deus ou a quem está me ajudando; estou também dando a mim mesmo a maior de todas as recompensas – um coração agradecido. Que eu não me esqueça do verbo “agradecer”, dirigido a alguém, nem da expressão “ser agradecido”, pois isso satisfaz à minha própria grande necessidade.

Hoje Eu Me Lembrarei
Agradeça e seja agradecido.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('b51cbd67-a0fd-42c5-b31c-e7d00819597a', 11, 27, 'O Programa de Jogadores Anônimos me ensina como transformar os sonhos irrealizáveis do passado em uma realidade tranqüila e propósito verdadeiro, aliado a uma crescente conscientização do poder de Deus em nossas vidas. Aprendemos que é bom manter nossas cabeças nas nuvens com Ele, mas nossos pés deveriam ficar plantados aqui na terra. Aqui é o lugar onde as outras pessoas estão; aqui é o lugar para fazermos o nosso trabalho.

Vejo alguma incompatibilidade entre a minha consciência de Deus e uma vida útil aqui e agora?

Hoje Eu Rezo
Que a minha nova “realidade” inclua não somente porcas e parafusos e panelas e frigideiras do dia a dia, mas também minha realidade espiritual e meu maior conhecimento da presença de Deus. Que essa nova realidade também deixe espaço para meus sonhos – não as fantasias do passado que me afastavam da realidade ou os vestígios de minhas ilusões - mas o produto de uma imaginação saudável. Que eu respeite esses sonhos, ligando-os às possibilidades da vida e transformando-os em uma criatividade proveitosa.

Hoje Eu Me Lembrarei
O paraíso pode estar aqui e agora.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('f420d492-49ca-4be3-8849-f0722eaf8f3e', 1, 7, 'Estou   começando a ver  agora como  a minha  vida antiga não era na verdade uma vida natural , e como foi piorando  à medida que minha doença progredia. Quanto mais tempo estou no  Programa de Jogadores Anônimos, mais tranqüilo esse novo modo vida me parece. No início, eu achava que seria impossível  estender a minha mão a um ingressante; pois essa atitude não me parecia completamente natural. Mas  está  ficando cada vez mais fácil para mim  aproximar-me de outra pessoa. Compartilhar minha experiência, energia e esperança está se  tornando uma parte natural da vida diária.

Já aprendi que não posso preservar o que consegui sem que o transmita  a uma outra pessoa”? Hoje arranjarei  tempo para compartilhar?

Hoje Eu Rezo
Que eu compartilhe o meu amor, minha alegria, minha felicidade, meu tempo, minha hospitalidade, meu conhecimento das coisas na terra e  minha fé em um Poder Superior. Mesmo que não possa  ver os resultados desses meus atos, devo  ficar feliz pelos próprios atos. Que eu compartilhe segundo a vontade de Deus, e isso se torne tão natural para mim como falar ou respirar.

Hoje Eu Me Lembrarei
Nunca serei mesquinho na atenção e comunhão', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('6f74d6a2-cef4-4252-95ea-5892671730f4', 8, 5, 'Uma das coisas mais importantes que aprendemos em Jogadores Anônimos é sermos bons para nós mesmos. Para muitos de nós, mesmo assim, isto é uma coisa surpreendentemente difícil de fazermos. Muitos de nós alimentam o sofrimento de tal maneira, que colocamos fora de proporção cada acontecimento na hora de vivê-los e contá-los a outras pessoas. Quem sofre de auto-piedade é atraído ao martírio por um poderoso imã – até que os prazeres da serenidade e satisfação lhe cheguem através do Programa de JA e dos Doze Passos.

Estou aprendendo gradualmente a ser eu mesmo?

Hoje Eu Rezo
Que eu aprenda a perdoar a mim mesmo. Eu tenho pedido – e recebido – o perdão de Deus e de outros, então por que é tão difícil perdoar a mim mesmo? Por que ainda aumento o meu sofrimento? Por que continuo lambendo minhas feridas emocionais? Que eu siga o exemplo do perdão de Deus, continue com o Programa e aprenda a ser bom para mim mesmo.

Hoje Eu Me Lembrarei
Martírio;  estúpido mártir.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('efa791ee-0ab9-48e4-9162-7e34e6c6211c', 2, 27, 'Se eu viver somente um dia de cada vez, logo não terei medo do que poderia acontecer amanhã. Enquanto me concentrar nas atividades de hoje, não haverá, em minha mente, espaço para preocupações. Tentarei preencher cada minuto deste dia com algo bom que eu tenha visto, ouvido ou realizado. Então, quando o dia terminar, poderei pensar nele com satisfação, serenidade e gratidão.

Algumas vezes acalento maus sentimentos para que possa sentir pena de mim mesmo?

Hoje Eu Rezo

Que eu me livre da atitude de auto-piedade e viva o dia de hoje. Que eu preste atenção a todas as boas coisas, desde o amanhecer até à noite, aprenda a falar delas e agradeça a Deus por elas. Que eu perceba se aparentemente estiver alimentando minhas lamentações e reclamações com mais freqüência do que apreciando as dádivas de minha vida.

Hoje Eu Me Lembrarei
O hoje é bom.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('3cb00dbd-4df7-4c8d-8676-b18eb3d9d35a', 10, 2, 'O ditado “Viva e Deixe Viver” condensa toda uma filosofia de vida. A princípio somos levados a viver de maneira completa, rica e feliz – a cumprirmos o nosso destino com a alegria que advém de fazermos tudo bem. Depois surge um desafio maior: deixar viver. Isto significa aceitar o direito que todas as outras pessoas têm de viver como desejarem, sem receberem críticas ou serem julgadas por nós. Esse ditado descarta o desrespeito por aqueles que não pensam como nós. Ele também nos adverte contra ressentimentos, lembrando-nos que não devemos analisar as atitudes dos outros como ataques intencionais contra nós.

Estou me tornando menos propenso a confundir minha mente com atitudes ou formas de vida de outras pessoas?

Hoje Eu Rezo
Que eu viva minha vida em toda a sua plenitude, compreendendo que a pura busca do prazer não significará encontrá-lo, e que a Graça de Deus está presente para ser compartilhada. Que eu desfrute disso. Que eu aprenda a não assumir a responsabilidade pela decisão de outras pessoas adultas, porque isso seria o meu antigo “eu” tentando, mais uma vez, agir como o diretor executivo da vida dos outros.

Hoje Eu meu Lembrarei
Viva e Deixe Viver', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('986bdc95-82a9-4808-b4ca-223e136a887f', 4, 4, 'Outro denominador comum entre aqueles que recaem é a incapacidade de usar as ferramentas do Programa de Jogadores Anônimos – Os Doze Passos. Os comentários que se ouve mais freqüentemente são – Eu nunca fiz o trabalho dos Passos; - Eu nunca saí do Primeiro Passo; - Eu fiz os Passos devagar demais, ou depressa demais; ou cedo demais. A verdade é que essas pessoas aceitavam os Passos, mas não os aplicavam às suas vidas com consciência e sinceridade.

Estou aprendendo a forma de me proteger e de ajudar os outros?

Hoje Eu Rezo
Que eu faça o trabalho dos Passos e não seja somente um ouvinte. Que eu tenha consciência de algumas falhas que costumam ocorrer, que levam a uma recaída: ser orgulhoso demais para aceitar o Passo 1; ficar preso demais à vida diária tornando-me incapaz de sentir  a presença de um Poder Superior;  sentir-me sobrecarregado demais pela idéia de preparar o Passo 4,  um completo inventário moral e financeiro; ficar receoso demais para poder compartilhar esse inventário. Por favor, Deus, guie-me enquanto eu faço o trabalho dos Doze Passos.

Hoje Eu Me Lembrarei
Ficar atento aos meus Passos.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('def683b6-6f01-46aa-bbd8-219499d3ac1f', 12, 2, 'Uma vez em uma reunião da igreja, vi escrito em um vitral “Deus é amor”.  Por alguma razão,  minha mente transpôs essas palavras para  “Amor é Deus”. Cheguei à conclusão, que de uma forma ou outra isso é certo e verdadeiro,  ao olhar à minha volta e tornando-me ainda mais consciente da atmosfera de amor e Poder  daquela pequena sala de reuniões. Continuarei a procurar esse amor e poder, seguindo o Programa de Jogadores Anônimos como se minha vida dependesse dele – como na verdade, ela depende.

A vida hoje para mim significa viver – ativamente – de forma feliz e tranqüila?

Hoje eu Rezo
Que eu sinta o espírito do amor que dá energia às nossas orações. Que eu sinta a unidade desta sala,  esta concentração de amor que dá poder ao grupo. Que eu assimile o amor exemplar de um Poder Superior , do qual nosso amor é o eco.

Hoje Eu Me Lembrarei
Amor é Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('b19d37fc-d990-4a8c-8fa2-3e177e30169b', 8, 21, 'Após ter feito o inventário, determinar e admitir a natureza exata de nossos erros, ficamos “inteiramente dispostos” como o Sexto Passo sugere,  a “eliminar esses defeitos de caráter.” Certamente, será fácil nos sentirmos assim e ficarmos  “inteiramente dispostos’ na manhã após o fiasco do jogo; mas sabemos, que nesses momentos desesperados, nossos motivos podem estar condicionados pela infelicidade do momento. Quanto mais nos afastamos de nossas últimas apostas indulgentes, mais  inocentes elas parecem e possivelmente, até ainda mais tentadoras.

Estou REALMENTE pronto a remover esses defeitos de caráter?

Hoje Eu Rezo
Que eu esteja “inteiramente disposto” a ter meus defeitos de caráter removidos. Que as palavras “inteiramente disposto” reafirmem minha determinação caso ela esmoreça com o tempo e abstinência. Que Deus seja a minha força, já que sozinho não posso apagar os meus erros.

Hoje Eu Me Lembrarei
Estou “inteiramente disposto”.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('6e029304-790e-41fe-9e18-eadda3940f0a', 2, 25, 'Antes de entrarmos para Jogadores Anônimos o medo governava nossas vidas. Temíamos tudo e todos devido à tirania de nossas adicções e obsessões. Tínhamos medo de nós mesmos e, talvez acima de tudo, temíamos o próprio medo. Nos dias de hoje estou pronto para aceitar a ajuda do meu Poder Superior. Sinto-me capaz de fazer qualquer coisa que seja necessária. Estou vencendo meus medos e adquirindo uma confiança nova e tranqüila.

Consigo acreditar  que “a coragem é o medo que já fez suas orações&#8230;?”

Hoje Eu Rezo
Que Deus permita que, através da fé, eu consiga vencer meus medos obsessivos. Tenho seguido amedrontado por tanto tempo, que isso se tornou um hábito. Que Deus me ajude a perceber que posso estar intencionalmente me apegando ao medo para impedir que eu tome decisões,  ou até esquivar-me da responsabilidade do sucesso.

Hoje Eu Me Lembrarei
O medo se esconde atrás da indecisão.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('f4ee7c4f-2eea-4ae6-be87-fd419e7e7ebb', 1, 16, 'Quando chegamos a Jogadores Anônimos pela primeira vez, por iniciativa própria ou sob pressão de outros, estávamos fartos do conceito de “rendição”. Admitir nossa derrota ia contra nossas crenças de uma vida inteira. Alguns de nós lembramos dos apelos imortalizados de Churchill para a arregimentação de tropas em Dunkerke, ou de Franklin Delano Roosevelt  após o ataque de Pearl Harbor. Então, à princípio, nos atemos avidamente à idéia de que a rendição seria impensável.

Passei realmente a acreditar que só através da derrota derradeira serei capaz de dar o primeiro passo para a liberdade e fortalecimento? Ou ainda tenho reservas quanto ao princípio de “deixar acontecer  e deixar por conta de Deus?”

Hoje Eu Rezo
Que eu realmente acredite que o caminho para a serenidade seja a completa rendição de todo meu ser a um Poder Superior. Afinal, eu só posso ser completo Nele, porque só Ele tem o Poder de tornar-me um ser completo. Que eu desaprenda o antigo refrão que me diz para “jamais me render”, percebendo  que tal orgulho pode impedir minha recuperação.

Hoje Eu Me Lembrarei
De completamente Dele, ao ser completo.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('dc240d6c-bf8a-4901-844a-1b6a0b4bfdd0', 6, 27, 'Pouco a pouco, estou superando minha tendência à procrastinação. Sempre costumava adiar as coisas para o dia seguinte e, é claro, nunca conseguia fazê-las. Ao invés do “faça agora” meu lema era “amanhã será outro dia”. Quando eu estava na ativa tinha planos grandiosos; quando caí do pedestal, fiquei ocupado demais superando minha depressão para começar qualquer coisa. Aprendi no Programa de Jogadores Anônimos que é muito melhor cometer um erro ocasional do que nunca fazer algo.

Estou aprendendo a “fazer agora” o que tem que ser feito agora?

Hoje eu Rezo
Que Deus me ajude a curar as prorrogações habituais e me faça “chegar na igreja na hora.” Que eu me liberte do caos auto-infligido de uma procrastinação eterna: livros da biblioteca com prazo vencido, compromissos feito pela metade, tarefas entregues tardiamente, compromissos não cumpridos, refeições mal cozidas. Devo me certificar,  de que, como jogador compulsivo, eu levava uma vida desordenada, e, como  jogador compulsivo em recuperação, preciso de ordem. Que Deus me dê a serenidade que necessito para restaurar a ordem e a organização em minha vida diária.

Hoje Eu Me Lembrarei
Não me atrasarei por causa de minha tendência em adiar.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('32b9fcff-7638-404f-895d-bfd79fcf3868', 9, 30, 'Não importa o que outras pessoas façam ou não, temos que permanecer abstinentes do jogo por nós mesmos. Quando o programa de recuperação fica condicionado às atitudes ou falta de atitude de outra pessoa  - principalmente alguém com quem estejamos emocionalmente envolvidos – os resultados são invariavelmente desastrosos. Também precisamos nos lembrar que um grande desinteresse é um envolvimento emocional tanto quanto um recém-descoberto amor romântico. Em suma, precisamos ficar serenos em qualquer  tipo de envolvimento emocional, nos primeiros meses de nossa recuperação, quando ainda estamos tentando aceitar o fato de que nossos sentimentos podem variar rapidamente e dramaticamente. Nossas palavras chaves precisam ser “As Primeiras Coisas Primeiro”, concentrando-nos em nosso problema principal, a recuperação do jogo compulsivo, antes de qualquer outra coisa.

Estou construindo uma base firme ao mesmo tempo que estou me afastando das áreas emocionais de risco?

Hoje Eu Rezo
Que eu sempre me lembre que um relacionamento saudável com as pessoas é importante para a minha recuperação, mas por outro lado, substituir uma obsessão por um objeto de amor ou de ódio é tão perigoso para o meu bem estar quanto qualquer outra adicção.

Hoje Eu Me Lembrarei
A dependência é uma dependência, é uma dependência', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('340e6aa5-a4b3-41c2-9a16-c561bd78ddea', 9, 28, 'Agora que estamos livres de nossa compulsão ao jogo, e vivendo um dia de cada vez, podemos começar a parar de fazer exigências irracionais para com aqueles que amamos. Podemos mostrar carinho onde não havíamos mostrado nenhum; podemos arrumar tempo e iniciativa para sermos atenciosos, termos consideração e compaixão. Mesmo com as pessoas que desgostamos, podemos pelo menos tentarmos ser educados e algumas vezes, literalmente, deixar de fazer o que queremos para compreendê-los e ajudá-los.

Só por hoje, tentarei entender ao invés de ser compreendido, ser educado e respeitador com todas as pessoas com as quais tenha contato?

Hoje Eu Rezo
Que eu nunca esqueça de minha antiga  maneira de ser, semelhante a uma esponja que sugava toda e qualquer gota de afeição e atenção que  minha família ou amigos podiam me dar, até que ficassem completamente secos. Que eu aprenda a doar, ao invés de ser um eterno tomador. Que eu, como prática, ofereça interesse, gentileza, consideração e compaixão até que a sensibilidade com relação aos outros se torne uma segunda natureza para mim.

Hoje Eu Me Lembrarei
Dar é parte de ser.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('b35f120c-b774-4926-aab5-28454b585f3f', 1, 18, 'Se estamos determinados a parar de jogar, não deverá haver reservas quanto a isso, nem qualquer idéia secreta de que algum dia nossa obsessão se reverterá sozinha. Nossa regeneração vem através do surpreendente paradoxo dos Doze Passos: o fortalecimento vem da derrota derradeira e,  a perda de uma vida antiga, é a condição para se encontrar uma nova.

Estou convencido de que o poder vem da falta de poder? Tenho convicção de que libertando minha vida e vontade, estarei livre?

Hoje Eu Rezo
Que eu conheça o poder através da impotência, a vitória através da rendição e o triunfo através da derrota. Que eu aprenda a eliminar, por meus próprios meios, quaisquer traços de orgulho oculto. Que eu me dedique e seja estimulado pela vontade onipotente de Deus.

Hoje Eu Me Lembrarei
Renda-se e deixe que Deus assuma o controle.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('c64b200a-0f39-4ac7-833c-dc88237e0679', 11, 11, 'O que é exatamente a humildade? Ela significa que devemos ser submissos, aceitando tudo com que nos deparamos, independente de quanta humilhação ocorrer? Isso significa render-nos aos horrores e a um modo destrutivo de vida? Ao contrário. O ingrediente básico de toda a humildade é simplesmente o desejo de procurar e satisfazer a vontade de Deus.

Estou começando a entender que adoção da verdadeira humildade proporciona-me dignidade e graça, fortalecendo-me para a tomar a atitude espiritual  inteligente na solução de meus problemas?

Hoje Eu Rezo
Que eu descubra que a humildade não implica em dobrar-me, arrastar-me, ajoelhar-me aos pés dos outros ou deixar que me pisem – coisas que, de algum modo,  representam expectativas de reconhecimento pessoal, como a aprovação ou simpatia. A verdadeira humildade é o conhecimento do imenso amor e da infindável grandeza de Deus. Ela é a perspectiva que explica como eu, como ser humano, me relaciono com esse Poder Divino.

Hoje Eu Me Lembrarei
A humildade é a consciência da existência de Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('f739b2a9-3d8a-483e-9d50-04bd935305ec', 6, 25, 'Se alguma vez eu tirar a conclusão complacente de que não mais preciso do Programa de Jogadores Anônimos, que eu rapidamente me lembre que isso pode fazer muito mais do que simplesmente levar-me de volta à angústia de viver na servidão da adicção. Que eu também me lembre que posso fazer ainda maiores progressos em meu crescimento pessoal, porque o Programa dos Doze Passos é uma filosofia – um modo de vida.

Algum dia superarei minha necessidade do Programa de JA?

Hoje Eu Rezo
Que meu Poder Superior me conduza aos Doze Passos, não somente uma vez, mas outras e outras, até que eles se tornem os princípios orientadores de minha existência. O Programa não é um rápido seminário sobre a melhoria da qualidade de minha vida; ele é a minha vida que recuperei graças ao Poder Divino e à amizade de meus companheiros jogadores, que, como eu, estão se recuperando da melhor maneira que se conhece.

Hoje Eu Me Lembrarei
Passo a Passo, da servidão à vida plena.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('50cbbcda-27c4-4af4-b5a0-6ab491380b0a', 2, 26, '“E se &#8230;” quantas vezes ouvimos estas palavras dos recém chegados ao Programa de Jogadores Anônimos. Quantas vezes, na verdade, tendemos a dizê-las a nós mesmos. – E se eu perder meu emprego? E se meu carro enguiçar? E se eu ficar doente e não puder trabalhar? E se meus filhos se viciarem em drogas? E se – qualquer coisa que nossa imaginação desesperada puder criar? São só duas palavras pequenas, mas totalmente carregadas de medo e ansiedade. A resposta a “E se &#8230;” é, pura e simplesmente, “Não projete”. Só podemos conviver com nossos problemas à medida que surgem, vivendo um dia de cada vez.

Estou mantendo meus pensamentos positivos?

Hoje Eu Rezo
Que eu cresça espiritualmente, sem me prender a ansiedades. Que os medos projetados não entravem meus esforços ou impeçam-me de dar o melhor de mim hoje. Que eu transforme o medo em fé. Só terei dentro de mim um lugar para Deus, e a presença deste poder Superior removerá meus medos.

Hoje Eu Me Lembrarei
Os problemas que pegar emprestado vêm com juros muito altos.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('8fdc92df-202f-4800-b160-4b945c876170', 11, 17, 'Como jogadores compulsivos em recuperação muitos de nós teimosamente se prenderam às falsas idéias e posições simplesmente porque temíamos ficar indefesos se admitíssemos que estávamos errados. Para algum de nós, a idéia  de “voltar atrás” ainda nos parece detestável. Entretanto, passamos a entender que nossa auto-estima aumenta quando nos tornamos capazes de colocar o orgulho em seu devido lugar e realmente encaramos os fatos. É muito provável que as pessoas com verdadeira humildade possuam uma mais genuína auto-estima do que aqueles que como nós são muitas vezes vítimas do orgulho.

O orgulho, de maneira óbvia ou tortuosa, me impede de prestar uma atenção constante  e profunda ao Décimo Passo?

Hoje Eu Rezo
Que eu me afaste do orgulho agora que encontrei o caminho certo a trilhar. Que eu evite esse ciclo de orgulho tão conhecido e destrutivo – esse ego que infla sem  proporções e depois desinfla após um fracasso. Que eu aprenda o valor de “voltar atrás”.

Hoje Eu Me Lembrarei
O orgulho é inimigo número um da auto-estima.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('a1560489-8b20-4a92-99bf-38e20926cc33', 5, 22, 'Quando ouvi pela primeira vez as pessoas falando aberta e honestamente  sobre si mesmas em Jogadores Anônimos, fiquei surpreso. Suas histórias das escapadas para o jogo, de seus próprios medos secretos, de sua solidão devastadora, soavam  literalmente avassaladoras para mim. Descobri – e quase que não ousava acreditar a princípio – que eu não estava sozinho, que não era de forma alguma diferente de qualquer outra pessoa e, na verdade, que éramos todos muitos parecidos. Comecei a sentir que eu realmente pertencia a algum lugar e que minha solidão começava a se dissipar.

Tento dar aos outros o mesmo que recebi sem qualquer ônus?

Hoje Eu rezo
Que eu comece a ver, à medida que fico a par das histórias de meus amigos em JA, que nossas semelhanças são muito mais surpreendentes do que nossas diferenças. À medida que ouço seus relatos de jogo compulsivo e de recuperação, que eu freqüentemente reviva o choque passageiro de consciência do sentimento – Hei! Esse sou eu! – que logo me afasta de meu isolamento. Que eu me torne um membro totalmente dedicado ao grupo, dando e recebendo na mesma proporção.

Hoje Eu Me Lembrarei
Semelhanças, ao invés de diferenças.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('45fbe630-6615-4aed-b636-c5094b4b839d', 1, 4, 'Durante grande parte de minha vida, eu via tudo quase sempre de forma negativa. Tudo era sério, difícil, ou simplesmente horrível. Talvez agora eu possa realmente mudar minha atitude, procurando os vencedores no Programa de Jogadores Anônimos, aqueles que aprenderam como viver satisfatoriamente no mundo real – sem jogar.

Se as coisas piorarem hoje, posso tirar um momento de tranqüilidade e dizer a mim mesmo, como disse certa vez o filósofo Homero, “Agüente pacientemente, meu coração – pois você já sofreu coisas piores”?

Hoje Eu rezo
Que a paz de Deus, que vai além de  toda a compreensão humana, preencha esse espaço em mim que antes guardava meu desespero. Que o gosto pela vida – e até por suas provações – anule minhas  antigas atitudes negativas. Ajudai-me a lembrar que meu coração já sofreu muito mais no passado.

Hoje Eu Me Lembrarei
Sou um vencedor – no melhor sentido da palavra', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('b55f2e93-f1cf-490e-84b4-49866cad663d', 6, 26, 'Quantos de nós tiveram a presunção de dizer – Bem, estou quase vencendo o vício de jogar. O que mais desejo, ou quero fazer? Estou bem desta forma. A experiência nos ensinou que o preço de tamanha complacência ou mais sutilmente, a auto satisfação é a recaída inevitável, marcada – mais cedo ou mais tarde – por um duro despertar. Precisamos crescer, senão nos deterioraremos. Para nós, o “status quo” só vale para o dia de hoje, nunca para o dia de amanhã. Precisamos mudar; não podemos permanecer imutáveis.

Algumas vezes fico tentado a repousar sobre meu louros?

Hoje Eu Rezo
Que eu olhe à minha volta e veja que todas as coisas vivas ou estão crescendo, ou estão se deteriorando; nada que seja vivo é estático. A vida flui. Que eu seja levado pela corrente da vida, sem medo de mudar, livrando-me das amarras que encontro em meu caminho, que me detêm e interrompem o meu progresso.

Hoje Eu Me Lembrarei
Viver é mudar.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('86fdecf5-ca29-4edf-94e3-eaad5733ff34', 4, 7, 'Nosso crescimento espiritual e emocional no Programa de Jogadores Anônimos depende muito mais de nossos fracassos e retrocessos do que de nosso sucesso. Se tivermos isso em mente uma recaída poderá ter o efeito de nos levantar ao invés de nos derrubar. A melhor lição que recebemos do programa vem de uma Velha Amiga chamada Adversidade, exceto quando não percebemos o seu ensinamento.

Tento permanecer sempre receptivo aos ensinamentos?

Hoje Eu Rezo
Que eu respeite o Programa como um todo, com todas as suas infindáveis possibilidades de crescimento espiritual  e emocional, para que eu seja capaz de encarar uma recaída como uma experiência de aprendizado e não como o ‘fim do mundo”. Que a recaída sirva para ensinar, não somente à pessoa que recai, mas a todos nós na Irmandade. Que a recaída fortaleça nosso objetivo em comum.

Hoje Eu Me Lembrarei
Se escorregar, erga-se.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('e612bc04-63ca-4ce6-867a-f8a79aac1d9e', 12, 20, 'Quando procuramos alcançar compulsivamente a perfeição, invariavelmente magoamos a nós mesmos. Não só acabamos transformando pequenos em grandes problemas, como também nos tornamos frustrados e cheios de desespero quando somos incapazes de atingir as metas impossíveis que estabelecemos para nós mesmos. Finalmente, diminuímos nossa capacidade de lidar com a vida e a realidade como ela realmente é.

Consigo aprender a ceder um pouquinho aqui e outro lá? Consigo me esforçar calmamente para atingir somente o que é possível e atingível?

Hoje Eu Rezo
Que eu me conscientize que lutar por realizações impossíveis leva-me a ter sempre uma desculpa pronta para não fazê-las. Isso é também um sinal de minha falta de senso da realidade, que precisa envolver o conhecimento do que posso fazer e depois realizá-lo. Com a ajuda de meu Grupo de Jogadores Anônimos e de meu Poder Superior devo aprender a estabelecer “objetivos viáveis”. Os mesmos poderão parecer ridiculamente pequenos para mim, após anos e anos de aspirações grandiosas. Entretanto, ao desmembrar meus projetos em vários projetos menores, que eu descubra que na verdade posso atingir alguns de meus objetivos.

Hoje Eu Me Lembrarei
Desmembre objetivos grandiosos em objetivos menores.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('2fd38d09-dd85-4a68-bef5-b095c41fc29c', 10, 20, 'Antes de admitir minha impotência perante o jogo, tinha tão pouca auto-estima quanto um “zero à esquerda”. Cheguei a Jogadores Anônimos como um João Ninguém que queria desesperadamente ser alguém. Em retrospectiva, minha auto-estima estava espatifada, aparentemente sem conserto. Gradualmente, o Programa foi me dando condições de adquirir uma noção de auto-estima cada vez maior. Passei a aceitar a mim mesmo, me dando conta de que não era tão ruim quanto eu achava.

Estou aprendendo que a minha auto-estima não depende da aprovação dos outros, sendo ao contrário um verdadeiro “trabalho interior”.

Hoje Eu Rezo
Quando estou me sentido deprimido e sem valor, que meu Poder Superior e meus amigos no Grupo me ajudem a perceber que, embora eu estivesse “caído” não estava “vencido”. Por mais doente que estivesse em meus dias de jogo, com a auto-estima de um verme, que eu saiba que ainda tinha o poder da escolha - e eu escolho fazer algo para mim mesmo. Que essa boa escolha seja a base para a reativação de minha auto-estima.

Hoje Eu Me Lembrarei
Eu não chutarei a mim mesmo se estiver caído.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('31eee73f-bcb0-43f2-a948-55786d5db799', 1, 23, 'Nunca devemos nos cegar pela vã filosofia de que somos apenas vítimas ocasionais de nosso legado, nossas experiências de vida e de nosso ambiente – de que essas são as únicas forças que nos levam às decisões. Essa não é a estrada para a liberdade. Precisamos acreditar que realmente podemos escolher. Como pessoas adictas, perdemos nossa habilidade de escolher entre seguir ou não com nossa doença. Mesmo assim,  finalmente fizemos as escolhas que nos trouxeram à recuperação.

Acredito que, ao “me tornar disposto” fiz a melhor de todas as escolhas?

Hoje Eu Rezo
Que eu abandone a idéia de que sou a vítima do mundo, uma criatura desafortunada pega pela teia das circunstâncias, achando que os outros devam me “compensar” porque recebi uma fatia ruim nesta Terra. Sempre tivemos escolhas. Que Deus me ajude a escolher bem.

Hoje Eu Me Lembrarei
Não somos marionetes nas mãos de Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('ac24353b-a9c6-4226-91f5-f5fd5603866c', 9, 8, 'Dizem que não há situação sem esperança. No princípio, é claro, achamos difícil crer nisto. Os opostos – esperança e desespero – são atitudes emocionais.  Mas nós é que somos sem esperança, não a condição de nossas vidas. Quando desistimos de ter esperança e nos deprimimos, é porque ainda somos incapazes de acreditar na possibilidade de uma mudança para melhor.

Consigo aceitar que “Nem tudo que temos que enfrentar pode ser mudado, mas que nada pode ser mudado sem que seja enfrentado?

Hoje Eu Rezo
Que eu me lembre que sendo humano, posso fazer escolhas e nunca perder a esperança. A situação em que me encontro pode me parecer desesperadora, o que pode me levar a um estado de forte depressão quando vejo que minhas escolhas estão ficando sem saída. Que eu me lembre, também, que mesmo quando não vejo solução, posso escolher pedir a ajuda de Deus.

Hoje Eu Me Lembrarei
Posso optar por não deixar de ter esperança.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('4fde9090-27a4-4da5-98dd-a87baabcaa86', 8, 19, 'Algumas vezes os ingressantes indagam - Como o Programa de Jogadores Anônimos funciona? As duas respostas mais freqüentes são: “muito bem” e “lentamente”. Gosto das duas respostas, mesmo que pareçam ambíguas a princípio, porque minha auto-análise tende a ser errônea. Algumas vezes deixei de compartilhar meus defeitos com as pessoas certas; outras vezes falei sobre os defeitos delas ao invés de falar sobre os meus; e ainda outras vezes, falei de meus defeitos  mais como uma forma de reclamação sobre os meus problemas. A verdade é que nenhum de nós gosta de se auto-avaliar, de mostrar nosso orgulho e de confessar as fraquezas que o Quarto Passo exige. Mas, eventualmente chegamos à conclusão de que o Programa de JA realmente funciona.

Fiz uso do jogo simples de ferramentas espirituais colocadas à minha disposição?

Hoje Eu Rezo
Que Deus me impeça de falar sobre meus defeitos para compará-los com os defeitos de outra pessoa. Somos, por natureza, adeptos ao princípio da relatividade e fazemos comparações pensando em termos de “pior que”, “não tão ruim quanto”, ou “melhor que”. Que eu veja que meus erros são erros, sendo ou não “melhores” dos que os dos “outros”.

Hoje Eu Me Lembrarei
Defeitos são defeitos, mesmo quando são “melhores do que”.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('194786c6-7cd7-4cf5-bf11-0beff6d6f178', 5, 1, 'Para àqueles entre nós que perderam a fé, ou que sempre precisaram lutar sem a mesma, é muitas vezes benéfico simplesmente aceitar cegamente e sem qualquer reservas. Não é necessário que acreditemos de imediato; não precisamos que nos convençam. Se conseguirmos simplesmente aceitar, estaremos gradualmente nos tornando conscientes da existência de uma força do bem que estará sempre presente para nos ajudar.
Escolhi o caminho da fé?

Hoje Eu Rezo
Que eu elimine minha necessidade de saber os “por quês” e os “para onde” de minha fé no Poder Superior. Que eu não intelectualize sobre a fé, já que por sua própria natureza isso exige uma análise. Que eu tenha consciência de que aqueles devaneios eram  sintomas de minha doença, uma vez que  teci – com bastante esperteza – álibis baseados em desculpas, que por sua vez eram baseados na racionalização. Que eu aprenda a aceitação e a fé surgirá.
Hoje Eu Me Lembrarei
A fé surge com a aceitação.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('7e7285ef-5b31-4e06-9120-6e0db48a4aaa', 12, 26, 'Ninguém pode realmente dizer que consegue de alguma forma entender nosso Poder Superior . Mas de uma coisa eu tenho certeza: existe um poder acima de minha vontade humana que pode fazer as coisas boas e maravilhosas para mim, que eu sozinho não consigo fazer. Percebo esse poder glorioso funcionando em todo o meu ser, e vejo os resultados milagrosos desse mesmo poder nas vidas de milhares de outros jogadores compulsivos, que são meus amigos,  em recuperação no Programa de Jogadores Anônimos.

Preciso menos da graça de Deus e de sua compreensão generosa  agora do que quando iniciei minha recuperação?

Hoje Eu Rezo
Que eu não nunca me esqueça de que minhas necessidades espirituais são hoje tão grandes quanto quando cheguei a Jogadores Anônimos. É tão fácil olhar para outros, novos  no processo de recuperação, e considerá-los como os necessitados. À medida que me considero cada vez mais independente, que eu nunca subestime minha dependência a meu Poder Superior.

Hoje Eu Me Lembrarei
Nunca superarei minha necessidade de Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('f543a3c8-7c60-4663-be0b-0dee0b644cdf', 4, 3, 'Em quase todos os casos, o jogador reincidente que retorna ao grupo diz – Eu parei de freqüentar as reuniões; – Eu me cansei das mesmas histórias e de ver as mesmas caras; - Tinha tantos compromissos que precisei parar de ir às reuniões; ou - Eu achava que já tinha tirado o máximo de proveito das reuniões; assim procurei outro tipo de ajuda em atividades que tinham mais sentido para mim. Em suma, essas pessoas simplesmente pararam de freqüentar as reuniões. Uma frase que ouvi em Jogadores Anônimos acerta na mosca: “Aqueles que param de ir às reuniões não estão lá para ouvir sobre o que acontece com aqueles que param de freqüentá-las.”

Estou freqüentando  número de reuniões  suficiente para mim?

Hoje Eu Rezo
Deus, mantenha-me no caminho do Programa de JÁ. Que eu nunca fique cansado demais, ocupado demais, condescendente demais, entediado demais para ir `as reuniões. Essas reclamações são quase sempre modificadas durante a reunião se simplesmente chego a uma delas. Meu desconforto dissipa-se através da serenidade. Minha vida atarefada reduz-se à sua proporção exata. Minha complacência dá lugar mais uma vez à vigília. Como posso me sentir entediado em um lugar onde há tanto companheirismo e alegria?

Hoje Eu Me Lembrarei
Freqüentar às reuniões.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('f7159fa8-190a-4bf3-9abb-73038febc4ef', 5, 23, 'Quando os ingressantes de Jogadores Anônimos têm a surpresa da primeira noção  de que estão realmente entre amigos, eles também se perguntam – com um sentimento quase de terror – se este sentimento é real e se isso perdurará. Aqueles dentre nós que estão no Programa há alguns anos, podem garantir ao novo membro em uma reunião que isso é  verdadeiro e  realmente perdura. Não é simplesmente mais um falso começo, nem um sentimento de felicidade efêmero antes que de um inevitável desapontamento destruidor.

Estou convencido de que estou tendo uma recuperação genuína e duradoura da solidão de meu jogo compulsivo?

Hoje Eu Rezo
Por favor Deus, não permita que eu me atenha ao medo de que a solidão volte. Que eu me conscientize de  que esses braços abertos que me recebem no grupo não se fecharão de repente, deixando-me de fora. Que eu seja paciente com o meu medo, que é marcado pelos desapontamentos e perdas passadas. Que eu me conscientize de que a Irmandade do Grupo, com o tempo, me convencerá que a solidão não é nunca incurável.

Hoje Eu Me Lembrarei
A solidão tem cura.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('8ead89dc-af40-4334-a139-8b07b12ea7ad', 10, 26, 'De tempos em tempos, quando  vejo a frase “Para lá,  irei, sem a graça de Deus.”. Lembro-me como proferi essas palavras quando via outras pessoas cuja compulsão pelo jogo os havia levado a um estado que eu considerava “sem esperança e sem saída.” Essa frase há muito havia se tornado uma válvula de escape para mim, reforçando a negação de minha compulsão,  levando-me a acreditar que poderia apontar os outros que me pareciam piores do que eu. O refrão que eu repetia sempre era: – Se eu alguma vez ficar daquela maneira, pararei de jogar. Ao invés disso, hoje a frase “Para lá irei, sem a graça de Deus” passou a ser minha oração de agradecimento, lembrando-me de que devo ser grato ao meu Poder Superior por minha recuperação, minha vida, e o modo de vida que encontrei no Programa de Jogadores Anônimos.

Alguém alguma vez já foi mais “desamparado e desesperançado”  do que eu?

Hoje Eu Rezo
Que eu saiba, que se não fosse pela Graça de Deus,  eu agora poderia estar morto ou insano, porque outros que se iniciaram no caminho do jogo compulsivo quando eu também o fiz, não mais estão aqui entre nós. Que essa mesma graça de Deus ajude aqueles que ainda estão sendo levados para baixo no redemoinho, indo de encontro a um desastre, tão certo quanto a gravidade.

Hoje Eu Me Lembrarei
Vi a maravilhosa graça de Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('48815093-e94c-4f59-9ea7-4d8c7005f589', 12, 25, 'Hoje é um dia especial em mais de uma maneira. É um dia que Deus criou,  e eu estou vivo neste mundo de Deus. Sei que, de  todas as coisas em minha vida, este dia representa a expressão do amor de Deus – o fato de que estou vivo, de que estou me recuperando, e de que sou capaz de me sentir da maneira como me sinto neste exato momento. Para mim, este é um dia de gratidão.

Sou profundamente agradecido pelo raiar deste dia especial, e por todas as minhas bênçãos?

Hoje Eu Rezo
Neste dia em que me lembro da dádiva de Deus, que eu compreenda que dar e receber significa a mesma coisa. Um é parte do outro. Seu eu der, recebo a alegria de dar. Se eu recebo, dou a outra pessoa essa mesma alegria de dar. Rezo para que eu mesmo possa doar – meu amor e minhas energias – graciosamente. Que eu também receba graciosamente o amor e as energias de outra pessoa. Que Deus seja o nosso exemplo.

Hoje Eu Me Lembrarei
Dar e receber são bênçãos iguais.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('44b2d610-c856-4d6c-90bd-14adb429c957', 1, 1, 'Nos velhos tempos, eu via tudo em termos de “para sempre”. Passava longas horas repensando  erros passados. Tentava buscar consolo na triste esperança de que o amanhã “seria diferente”. O resultado é que eu  vivia uma vida de fantasias, onde a felicidade era tudo que eu queria, porém  não existia. Não é de se admirar que eu raramente sorrisse e quase nunca desse gargalhadas.
Ainda penso em termos de “para sempre”?
Hoje Eu rezo
Que eu planeje minhas metas para o Ano Novo,  não como se fossem para o ano inteiro, e sim, para um dia de cada vez. Minhas costumeiras decisões para o  Ano Novo têm sido muito bem planejadas, mas logo esquecidas. Que eu não  enfraqueça meus planos considerando-os como “para sempre” -  ou mesmo para o ano inteiro. Que eu reafirme o meu propósito a cada novo dia. Que eu aprenda a não rotular meus erros passados com a expressão indelével –   “para sempre”.  Ao invés disso, que cada dia de cada Ano Novo, seja revitalizado com minha esperança recém adquirida.
Hoje Eu Me Lembrarei
Feliz Novo Dia.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('cd55cc5e-ef4d-4c54-a866-a8be99589b77', 10, 25, 'Minha compulsão pelo jogo era como se eu fosse um ladrão em um sem número de maneiras. Ela roubou-me não somente dinheiro, propriedade, e outras coisas materiais, mas também dignidade e respeito próprio, ao mesmo tempo que minha família sofria comigo. O jogo também me roubou a capacidade de tratar de mim mesmo da maneira apropriada como Deus o faria. Hoje, em um completo contraste, sou capaz de amar de verdade a mim mesmo – a ponto de até ser capaz de dar mais amor a mim mesmo do que realmente preciso. Assim, dou esse amor às outras pessoas no Programa de Jogadores Anônimos, da mesma forma que deram seu amor a mim.

Agradeço a Deus por me trazer a um  Programa onde as pessoas doentes são amadas até chegarem à sanidade?

Hoje Eu Rezo
Agradeço a Deus pelo modo de vida que gera tanto amor e carinho, que nós no Programa de JA, não podemos deixar de aprender a amar a nós mesmos. Quando vejo que alguém se importa comigo, torno-me mais capaz de me convencer de que afinal de contas, talvez eu mereça isso. Que eu sempre esteja consciente do amor que eu agora sou capaz de dar – e dê esse amor.

Hoje Eu Me Lembrarei
Alguém que se importa comigo faz com que eu me sinta merecedor dessa atenção.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('d18bea2d-461f-4f46-98c5-ce76783190ed', 1, 22, 'Num sentido muito real, ficamos aprisionados por nossa falta de capacidade ou vontade para buscar a ajuda de um Poder superior a nós mesmos. Mas, ainda a tempo, rezamos para sermos libertados da servidão a nós mesmos, para podermos melhor satisfazer a vontade de Deus. Nas palavras de Ramakrishna, “O sol e a lua não se espelham em águas turvas; assim,  o Todo-Poderoso não pode ser espelhado num coração obcecado com a idéia do “eu e meu”.

Libertei-me da prisão da  vontade-própria  e do orgulho que eu mesmo construí? Aceitei  a liberdade?

Hoje Eu Rezo
Que a palavra liberdade passe a ter novos significados para mim, de não só “estar livre” de minha adicção, mas “ter liberdade” para vencê-la. Não só liberdade da escravidão da vontade-própria, mas liberdade para ouvir e realizar a vontade de Deus.

Hoje Eu Me Lembrarei
Liberdade de, significa liberdade para.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('1d7757ee-f348-4b39-a1d0-e664c625833f', 5, 4, 'Muitas pessoas rezam como se estivessem sobrepondo-se à vontade de um Deus indeciso, ao invés de se agarrarem à vontade de um Deus amoroso. Nos últimos níveis de nossa compulsão ao jogo, a vontade de resistir já havia nos escapado. Mesmo assim, quando admitimos a derrota total, e quando nos tornamos inteiramente dispostos a tentar a prática os princípios do Programa de Jogadores Anônimos, nossa obsessão nos libera e ingressamos em uma nova dimensão – a liberdade sob um Deus como o concebemos.

Meu crescimento no Programa está me convencendo de que somente Deus pode remover as minhas obsessões?

Hoje Eu Rezo
Que eu não reze como se fosse uma criança choramingando frente a um pai severo, como se rezar sempre significasse implorar, geralmente nos momentos de desespero onde não se encontra outra saída. Ao invés disso, que eu reze a partir de um desejo pessoal de me aproximar de Deus, já que Ele está sempre disposto a vir a mim. Que eu veja meu Poder Superior como um Deus disposto.

Hoje Eu Me Lembrarei
Deus está disposto.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('2e513568-5cd3-4977-a3c5-21ee0596f8f2', 3, 15, 'Houve dias em minha recuperação, em que quase tudo parecia perdido e até mesmo sem esperança. Eu me permitia ficar deprimido e  com raiva.  Vejo que não importa o que eu ache ou como me sinta. O que conta é o que faço. Então, quando fico ansioso ou aborrecido, tento continuar minha recuperação indo às reuniões, participando e trabalhando com outros membros no Programa de Jogadores Anônimos.

Se Deus parece distante, quem se afastou?

Hoje Eu Rezo
Que eu não fique imobilizado pela tristeza ou raiva, ao ponto de me desesperar. Que eu procure as raízes do desespero em meu emaranhado de emoções, entenda  esse emaranhado, elimine os meus sentimentos de culpa e reconheça que eles são parte de mim. Somente assim entenderei a engrenagem, agirei  e começarei a obter resultados. Que eu aprenda a usar a energia que é gerada pela raiva, para fortalecer minha vontade e atingir meus objetivos.

Hoje Eu Me Lembrarei
Entender meu sentimentos.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('31597dd5-b5f4-4ee9-b432-dfb189d20077', 7, 30, 'Assim que chegamos a Jogadores Anônimos inicia-se uma variedade de auto-piedade comuns: - Pobre de mim, por que não posso jogar de vez em quando, como todo mundo.  Por que logo eu? Esta atitude, se deixarmos que continue, é o convite ideal para nos afastarmos do porto – o que certamente nos levará de volta ao caos em que nos encontrávamos antes de entrar para J.A. Quando continuamos no Programa por um bom período, logo entendemos que não eu não sou o único; encontramos pessoas, com diferentes histórias de vida, que estão exatamente no mesmo barco que nós.

Já estou perdendo interesse em  minha confortável e familiar  atitude de auto-piedade?

Hoje Eu rezo
Quando a auto-piedade tiver me deixando tonto e inerte, que eu possa olhar para cima,  para os lados, e tome uma nova atitude. A vontade de Deus é que essa auto-piedade, se dissipe na luz dos problemas compartilhados com  outras pessoas. Que eu sempre queira ter amigos bem honestos para me confrontar quando virem que estou cavando meu caminho  de volta ao poço da antiga piedade.

Hoje Eu Me Lembrarei
Transformar auto-envolvimento em envolvimento.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('2653a9d5-3884-41fb-8dd2-df917e8c2da6', 11, 10, 'Quando cheguei pela primeira a Jogadores Anônimos achava que a humildade era simplesmente um sinônimo de fraqueza. Entretanto, gradualmente, aprendi que não haverá incompatibilidade entre a humildade e a inteligência, se eu simplesmente colocar a humildade em primeiro lugar. Disseram que no momento em que eu começasse a fazer isto eu receberia o dom da fé – uma fé que funcionaria para mim da mesma forma como funcionou e continua funcionando para inúmeras outras pessoas que se livraram da adicção do jogo, e descobriram um novo modo de vida no Programa de JA .

Passei a acreditar, nas palavras de Heine, que as “atitudes dos homens são como o índice de um livro que apontam para  o que é mais importante”?

Hoje Eu Rezo
Que eu nunca permita que minha inteligência seja uma desculpa para a falta de humildade. É muito fácil, quando me considero razoavelmente inteligente e capaz de tomar decisões, cuidando de minha própria vida, menosprezar a humildade, considerando-a como característica das pessoas menos inteligentes,. Que eu me lembre de que a inteligência e a humildade são, as duas, concedidas por Deus.

Hoje Eu Me Lembrarei
Se eu não tiver humildade, não terei inteligência.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('fd921b2c-0539-4bf8-9385-57963588b848', 8, 24, 'Alguns de nós, após termos feito o Quarto, Quinto, Sexto e depois o Sétimo Passo, nos acomodamos e simplesmente esperamos que o Poder Superior remova as nossas falhas. O Programa de Jogadores Anônimos nos faz lembrar a história de São Francisco trabalhando em um lindo jardim. Um passante comentou: - Você deve ter orado muito para fazer com que plantas tão lindas cresçam. O bom santo respondeu: - Sim, eu rezei. Mas a todo instante em eu que começava a rezar, pegava a enxada. No momento em que nossa “espera” transforma-se em “cavar”, a promessa do Sétimo Passo passa a se transformar em realidade.

Fico esperando que o meu Poder Superior faça todo o trabalho?

Hoje Eu Rezo
Que eu não fique somente rezando e esperando que o meu Poder Superior faça tudo por mim. Ao invés disso, devo rezar, e ao mesmo tempo  pegar as ferramentas que o Programa me proporciona. Que eu agora peça a orientação de como posso melhor utilizar essas preciosas ferramentas.

Hoje Eu Me Lembrarei
Orar e agir.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('74ebdb18-44f2-4f65-801c-2437d98d87a6', 11, 12, 'Há algumas “verdades inegáveis” nos Doze Passos de Recuperação de Jogadores Anônimos. Temos liberdade para começar a qualquer momento que pudermos, ou quisermos. Deus, como O entendemos, pode ser definido simplesmente como um “Poder Superior”. Para muitos de nós no Programa, o próprio Grupo foi considerado como o  primeiro “Poder Superior”. Esta aceitação é relativamente fácil para um  recém-chegado, se o mesmo souber que a maioria dos membros está livre do jogo e que ele, ou ela, ainda não está. Esta aceitação é o começo da humildade. Talvez, pela primeira vez, o recém-chegado esteja pelo menos disposto a renunciar à noção de que ele – ou ela – seja o próprio Deus.

Meu comportamento é mais convincente para os recém-chegados do que minhas palavras?

Hoje Eu Rezo
Que eu defina e descubra o meu próprio Poder Superior. À medida que essa definição se tornar cada vez mais clara e mais próxima de mim, que eu me lembre de que não devo teimar que minha interpretação é a correta. Cada um de nós precisa encontrar seu próprio Poder Superior. Se um recém-chegado sentir que não possui um Deus e está sozinho, o poder do grupo pode ser o bastante por enquanto. Que eu nunca desmereça o poder do Grupo.

Hoje Eu Me Lembrarei
O poder do Grupo pode ser o Poder Superior.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('d5526c90-f398-43a9-83e0-bda95be957fb', 2, 20, 'À medida que recuperamos nossa saúde e nos tornamos cada vez mais capazes de viver bem no mundo real, sem jogar, começamos a ver muitas coisas sob um outro prisma. Muitos de nós, por exemplo, passaram a perceber que a raiva vem disfarçada de várias formas e cores: intolerância, sarcasmo, desconfiança, ansiedade, malícia, cinismo, descontentamento, auto-piedade, maldade, suspeita, inveja. Quanto mais descobertas nos são permitidas, mais começamos a ver a freqüência com que a raiva se torna a semente de muitas de nossas sensações de desconforto e inquietação. Tais descobertas nos possibilitam compreender nossa raiva e buscar novas formas para lidar com ela.

Permito que meus sentimentos despertem o melhor de mim?

Hoje eu Rezo
Que eu reconheça que minha raiva, como num baile de máscaras,  manifesta-se de várias formas e caras. Que eu retire essas várias máscaras de minha face e a reconheça pelo que ela realmente é.

Hoje Eu Me Lembrarei
A raiva usa milhares de máscaras.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('91184de0-177f-4ecf-82d3-93b72a2c6e7f', 11, 18, '“Nada será suficiente para quem o bastante é muito pouco” escreveu o filósofo grego Epicurus. Agora que nos livramos do jogo, estamos construindo nosso respeito próprio e recuperando o respeito da família e amigos, não podemos nos tornar presunçosos por causa de nosso recém alcançado sucesso. Para a maioria de nós o sucesso sempre foi o resultado de uma combinação perigosa; assim, mesmo em nossa nova vida, é possível que ainda caiamos na perigosa armadilha de nos considerarmos os “maiorais.” Para que isso não ocorra precisamos nos lembrar que estamos livres hoje somente pela graça de Deus.

Lembrar-me-ei de que qualquer sucesso que alcançar hoje não é somente meu, mas sim de Deus?

Hoje Eu Rezo
Que eu mantenha um pequeno barbante à volta de meu dedo para relembrar-me de que encontrei a libertação através da graça de Deus – somente assim não deixarei que meu orgulho tente me convencer de que fiz tudo por minha conta. Que eu aprenda a lidar com o sucesso,  creditando-o a um Poder Superior, e não à minha própria e questionável superioridade.

Hoje Eu Me Lembrarei
Aprender a lidar com o sucesso.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('c03bd3c3-c604-4304-8f94-a6ef2546b6cb', 3, 29, 'Por que as pessoas jogam compulsivamente. Dr. Robert Custer, um pioneiro no tratamento do jogo compulsivo, acreditava que temos quatro necessidades humanas básicas – afeição, aprovação, reconhecimento e autoconfiança. Quando essas necessidades não são satisfeitas, as pessoas se sentem inadequadas e sobrecarregadas pela vida. Por algum tempo, o jogo parece preencher essas necessidades. Mas, por qualquer que seja a razão, os princípios da Irmandade de Jogadores Anônimos têm se mostrado eficazes em ajudar milhares de jogadores compulsivos a superarem a compulsão pelo jogo.

Sou grato pela conscientização e pela Irmandade de Jogadores Anônimos?

Hoje Eu Rezo
Que Deus aumente a minha compreensão sobre a doença do jogo compulsivo, através das histórias de vida compartilhadas no porto seguro que as reuniões de JA representam. Nessas reuniões aprendemos o verdadeiro significado do termo “ganhar”, ao superar a adicção do jogo e todos os seus efeitos negativos à nossa vida. Que Deus continue a me mostrar as vantagens que posso ter  ao seguir o Programa com honestidade e sinceridade.

Hoje Eu Me Lembrarei
O Programa tem as respostas', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('440ba380-01ef-47c1-aaed-f707f668fa7a', 7, 28, 'Aprendemos o valor da meditação no Programa de Jogadores Anônimos. Como é sugerido no começo do Passo Onze, “Procuramos através da meditação melhorar nosso contato consciente com Deus como O entendemos”. Uma das grandes vantagens da meditação  é que ela clareia a mente. E, à medida que a mente se torna mais clara, ela se torna mais capaz e disposta a reconhecer a verdade. Pede-se menos sofrimento para reconhecer honestamente os defeitos e seus resultados. As verdadeiras necessidades de uma pessoa completa são reveladas.

A  meditação e a oração fazem parte da minha vida diária?

Hoje Eu Rezo
Que as verdades de Deus sejam reveladas a mim, através da meditação e de pequenas orações, e do contato com o meu grupo, que me mantém ciente da minha necessidade de manter a mente limpa através de meditação. Apenas uma mente sem empecilhos pode receber Deus, e apenas uma mente livre de interesse pode reconhecer a verdade.

Hoje Eu Me Lembrarei
A meditação purifica a mente.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('67cfb727-6e58-4f1d-8c8d-1d4c5676c476', 6, 20, 'Quando fazemos o Nono Passo, precisamos estar dispostos a ser inteiramente honestos, embora essa indiscriminada “completa honestidade” obviamente possa destelhar muitas casas e destruir completamente alguns relacionamentos. Não podemos esconder nada através da mentira e do orgulho, mas talvez precisemos guardar algo por discrição e consideração pelos outros. É justamente quando e como dizemos a verdade – ou nos silenciamos – que podemos freqüentemente dizer a diferença entre a integridade autêntica ou a completa falta dela.

Sou grato pelo resultado da verdade que, através das bênçãos de Deus, tive o privilégio de receber?

Hoje Eu Rezo
Que eu tenha a sabedoria de perceber a tênue margem de diferença entre ter tato ou ser desonesto. Em minha ânsia de fazer as reparações, que eu não faça o papel do encantador, do bajulador, ou do rastejador que insiste em dizer -  Você é tão bom, e eu sou tão mau. Todas essas são formas de desonestidade e nos revertem aos dias de encenação de minha adicção ao jogo na ativa. Que eu consiga identificá-las.

Hoje Eu Me Lembrarei
Tato é a seleção honesta.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('8b230b80-4973-4304-8dc6-859ec0f092b8', 3, 6, 'Não há vantagens nem lucro algum e certamente nenhum crescimento, quando estou enganando a mim mesmo somente para fugir das conseqüências de meus próprios erros. Quando percebo isso, sei que estou progredindo. “Precisamos ser verdadeiros por dentro, honestos para conosco, antes de podermos conhecer a verdade que nos rodeia,” escreveu Thomas Merton em Ninguém é Uma Ilha. “mas, tornamo-nos verdadeiros interiormente quando expressamos a verdade como a vemos.”

Sou honesto comigo mesmo?

Hoje Eu Rezo
Devo contar com meu Poder Superior para me ajudar a aceitar a verdade quando me dou conta dela. Que eu nunca mais me esquive de uma conseqüência. A fuga das conseqüências tornou-se um jogo de salão para jogadores adictos como eu, até que perdemos todo o sentido da relação entre ação e conseqüência. Por favor, Deus, agora que estou sarando, recupere meu equilíbrio.

Hoje Eu Me Lembrarei
Combinar  a ação com  a conseqüência.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('2d78af96-1ba9-4d31-a620-b8f665d9cfe2', 11, 2, 'Quanto mais reflexões fazemos, mais nos damos conta da freqüência com que reagimos negativamente porque nosso “orgulho foi ferido”. O orgulho é a raiz da maior parte de meus problemas pessoais. Quando meu orgulho é “ferido”, por exemplo, quase que invariavelmente, sinto ressentimento e raiva – algumas vezes ao ponto de ficar incapaz de falar ou pensar racionalmente. Quando me encontro nesse tipo de lodo emocional, preciso lembrar-me de que meu orgulho – e nada além dele – foi ferido. Preciso parar, tentar me acalmar até que surja o momento de poder avaliar o problema de forma realista.

Se meu orgulho ficar ferido ou ameaçado, rezarei para ter humildade para poder ficar acima de mim mesmo.

Hoje Eu Rezo
Que eu saiba que se meu orgulho for ferido, a outra parte de mim pode ter sido totalmente poupada. Que eu saiba que se eu vencer meu orgulho uma vez  ainda assim ele poderá voltar ainda mais forte do que nunca. Que eu saiba que todas as vezes que meu orgulho for vencido ele poderá se colocar na defensiva, tornando-se mais prejudicial, mais irracional e mais feroz. Que eu aprenda a manter meu forte orgulho em um lugar onde ele não seja tão facilmente ferido – ou não fique tão desejoso de se sentir ferido.

Hoje Eu Me Lembrarei
A humildade é a única autoridade contra o orgulho.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('63b7b245-f82b-45e4-bd1e-092c24581bee', 12, 27, 'As duas palavras de que tomei total consciência mesmo durante os meus dias de jogo são as mesmas as quais preciso me apegar em minha recuperação: parar e começar. Preciso parar de jogar e começar minha recuperação. Preciso me olhar dentro de mim e parar de culpar os outros. Preciso começar a me sintonizar com à vontade de meu Poder Superior e impedir que minha própria vontade me guie. Quanto mais começo a ver  os resultados positivos do Programa de Jogadores Anônimos, mais honestamente posso enxergar as ações negativas do passado que me levavam à miséria e desespero.

A recuperação me proporcionou um espelho para que eu me veja  refletido nos outros?

Hoje Eu Rezo
Que eu me lembre de que sempre tive a escolha de parar o meu jogo destrutivo a qualquer momento, mas a não ser que eu comece o processo de recuperação através do Programa de JA e os Doze Passos, não posso permanece “parado”. Se eu mantiver a recuperação iniciada,  poderei manter meu jogo parado.

Hoje Eu Me Lembrarei
O significado de “transformação-de-vida” dessas duas palavras: parar e começar.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('e29e8f87-33fe-4e72-be32-cca70c6d1012', 10, 27, 'O Quarto Passo do Programa de Jogadores Anônimos sugere que façamos um destemido inventário moral e financeiro de nós mesmos. Para muitos de nós, principalmente os recém-chegados, a tarefa parece impossível. Cada vez que pegamos o lápis e tentamos olhar para nosso interior, o Orgulho nos diz zombeteiramente: - Você não precisa se preocupar em procurar. E o medo nos adverte: - É melhor não fazer isso! Eventualmente, descobrimos que o orgulho e o medo são apenas nuvens de fumaça, as linhas de fumaça nas quais havíamos tecido a mitologia de nossas antigas ilusões. Quando colocamos o orgulho e o medo de lado e finalmente fazemos um destemido inventário, sentimos alívio e um novo e indescritível sentimento de confiança.

Fizemos um inventário? Compartilhei seus resultados com outros  para encorajá-los?

Hoje Eu Rezo
Que eu não fique paralisado por minhas inibições quando for a hora de fazer um inventário moral de mim mesmo. Que eu não chegue ao Quarto Passo e pare ali porque a tarefa parece ser grande demais. Que eu saiba que meu inventário hoje, mesmo que eu tente  ser minucioso e honesto, pode não ser tão completo quanto ficará se eu o repetir várias e várias vezes, porque o processo de auto-conhecimento continua para sempre.

Hoje Eu Me Lembrarei
Louvai a Deus pelo progresso.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('c70f862b-8f06-4b42-a235-bbdbbee89123', 5, 16, 'Muitos de nós, no Programa de Jogadores Anônimos, temos a mesma lembrança de que costumávamos jogar para nos “integrarmos”, para sermos os “todos poderosos”, ou para “fazermos parte de um grupo”. Outros alimentavam as adicções para “serem aceitos” – para sentirem, pelo menos por um curto espaço de tempo, que se enquadravam no resto da raça humana. Algumas vezes, nosso jogo surtia o efeito desejado, temporariamente apaziguando nossos sentimentos de solidão. Mas quando a agitação da ação passava, ficávamos nos sentindo ainda mais sozinhos, mais isolados, mais “diferentes” do que nunca.

Algumas vezes acho que “o meu caso é diferente”?

Hoje Eu Rezo
Deus, que eu supere o sentimento de “ser diferente”,  ou de algum modo “único”, por não me situar. Talvez tenha sido esta noção que me levou ao jogo em primeiro lugar. Isso também me impediu de ver a gravidade de minha adicção, já que eu achava que “era diferente e que poderia resolver tudo”. Que eu agora me conscientize de que realmente “pertenço” a uma grande Irmandade formada por pessoas iguais a mim. Através de cada experiência compartilhada minha “singularidade” vai desaparecendo.

Hoje Eu Me Lembrarei
Não sou único.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('7f210a2f-3169-42e8-85cf-b33127effec0', 4, 14, 'Todo homem ou mulher, que tenha ingressado em JA,  e que pretenda ficar, mesmo sem se dar conta disso, está começando a fazer o Passo Três. Não é verdade que, em todos os assuntos relacionados com seu jogo, todos tenham decidido entregar sua vida aos cuidados, proteção e orientação do Programa? Então, já existe o desejo de submeter a sua vontade e idéias sobre a adicção ao que é sugerido na Programação. Se isto não significa entregar a sua vontade e vida a uma recém descoberta “Providência” então o que isto quer dizer?

Estou tendo um despertar espiritual como resultado dos Passos.

Hoje Eu Rezo
Para meu próprio bem, rezo por uma vida voltada a Deus. Freqüentemente, agradeço a Deus pelo despertar espiritual que tenho desfrutado desde que entreguei minha vida a Ele. Que a expressão “despertar espiritual” seja um indício para outros de que já existe uma dose gratuita de poder espiritual dentro de cada um de nós. E preciso somente que ela seja descoberta.

Hoje Eu Me Lembrarei
Tentarei me voltar a Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('41180146-b842-494e-a592-44fa2163c775', 7, 8, 'Quando falarmos com um amigo ou amiga de Jogadores Anônimos, não deveremos hesitar em lembrá-los da nossa necessidade de privacidade. A comunicação íntima entre os membros é normalmente tão aberta e fácil que às vezes um amigo ou padrinho se esquece de que esperamos que mantenham silêncio.  Tal “comunicação privilegiada” tem importantes vantagens. Por um lado, achamos que conversando com eles temos a oportunidade de sermos tão honestos quanto podemos ser. Por outro lado, não precisamos nos preocupar com a possibilidade de prejudicarmos outra pessoa, nem termos medo de sermos ridicularizados ou condenados. Ao mesmo tempo, esta pode ser a melhor maneira  de detectar a auto-ilusão.

Sou digno da confiança que depositam em mim?

Hoje Eu Rezo
Peço que Deus me oriente para tornar-me um confidente confiável. Preciso ser uma pessoa com quem os outros estejam dispostos a contar suas experiências. Preciso saber ouvir bem, ao invés de ser somente aquele que fala. Hoje eu rezo.para tentar confiar e ter bastante confiança, para que eu  me torne um amigo melhor e mais receptivo àqueles que escolhem confiar em mim.

Hoje Eu Me Lembrarei
De ser um bom ouvinte.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('48b5bafa-6212-49f1-90de-94da0b229b51', 1, 11, 'A experiência de milhares e milhares de pessoas é a prova de que a aceitação e a fé podem causar a libertação do vício do jogo. Entretanto, quando aplicamos os mesmos princípios de aceitação e fé aos nossos problemas emocionais, descobrimos que somente resultados relativos são possíveis. Por exemplo, é óbvio que ninguém pode jamais ficar totalmente livre do medo, raiva ou orgulho. Nenhum de nós jamais alcançará o amor,  a harmonia, ou a serenidade perfeita. Teremos que nos contentar com o progresso gradual, marcado ocasionalmente por enormes retrocessos.

Abandonei minha antiga atitude de “ou tudo ou nada”?

Hoje Eu Rezo
Que Deus me conceda paciência para aplicar os mesmos princípios de fé e aceitação, que são as respostas para a minha recuperação, a toda minha vida emocional. Que eu aprenda a reconhecer a podridão de minha raiva humana, minha dor, minha frustração e minha tristeza. Que com a ajuda do meu Poder Superior, eu possa encontrar os caminhos certos para lidar com estes sentimentos, sem causar  danos a mim mesmo e aos outros.

Hoje Eu Me Lembrarei
Os sentimentos são reais – eu os aceitarei como tal.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('c928ce80-8ad0-433f-8140-881a127b9dc6', 9, 19, 'Para mim, não é ainda uma “moleza” aceitar a dor ocasional de hoje e a ansiedade, com certo grau de serenidade, mas estou ficando cada vez mais agradecido por receber algum nível de dor. No Programa de Jogadores Anônimos, passamos a querer fazer isso ao retomar as lições aprendidas com os sofrimentos do passado – lições que nos proporcionaram as benções que hoje desfrutamos. Podemos nos lembrar como as agonias de nossos dias de jogo – dor da rebeldia e do orgulho ferido – geralmente nos proporcionaram a graça de Deus, e como resultado disso, uma nova liberdade.

 Agradeci ao meu Poder Superior pelo milagre de minha vida hoje?

Hoje Eu Rezo
Quando me senti desamparado, recorri a Deus. Quando me senti desesperançado, busquei a Sua esperança. Quando fiquei impotente perante meu jogo compulsivo, pedi-Lhe para compartilhar do Seu poder. Agora, posso honestamente agradecer a Deus por ter me tornado desamparado, sem esperanças e sem forças, porque assim vi o milagre.

Hoje Eu Me Lembrarei
Das inconstâncias da impotência ao Poder Superior.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('e1f6d8aa-04f1-49a3-b153-23349d605a74', 8, 8, 'A auto-piedade é um dos mais terríveis e destruidores sentimentos que eu conheço. Por causa de suas incontáveis exigências por atenção e simpatia, minha auto-piedade corta o meu canal de comunicação com as outras pessoas, principalmente a comunicação com meu Poder Superior. Quando vejo o problema desta maneira, vejo que a auto-piedade limita o meu progresso espiritual. Ela é também uma verdadeira forma de martírio, que é um luxo que simplesmente  não posso ter. Aprendi que o remédio para isso é olhar intensamente para mim mesmo e mais intensamente ainda para os Doze Passos do Programa de Recuperação de Jogadores Anônimos.

Peço ao meu Poder Superior para me livrar desta servidão para comigo mesmo?

Hoje Eu Rezo
Que eu possa saber por experiência própria que autocomiseração não desperta sentimento de pena de ninguém. Ninguém – nem mesmo Deus – pode suprir sua descabida necessidade de pena. Que eu reconheça os dissabores da auto-piedade quando ela tentar roubar minha serenidade. Que Deus me mantenha ciente dos truques sorrateiros da auto-piedade.

Hoje Eu Me Lembrarei
Meu captor sou eu mesmo.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('71c47904-812f-4035-ad19-048ca0cf3ed0', 5, 2, 'Quando eu estava jogando, tinha certeza de que minha inteligência, apoiada por minha força de vontade, poderia controlar minha vida interior de forma adequada e garantir meu sucesso no mundo que me rodeava. Esta filosofia destemida e grandiosa, na qual eu fazia o papel de Deus, parecia boa em teoria, mas precisava passar a prova derradeira: será que ela realmente funcionava bem? Uma boa olhada no espelho era a resposta imediata.

Comecei a pedir força a Deus diariamente?

Hoje Eu Rezo
Que eu pare de contar com as minhas antigas reservas, minha “inteligência superior” e minha “força de vontade”, para controlar minha vida. Eu costumava pensar, que com esses atributos fabulosos, eu era o todo poderoso. Que eu não esqueça, à medida que recupero minha auto-imagem, que somente através da submissão ao um Poder Superior poderei receber o poder que poderá me tornar uma pessoa inteira.

Hoje Eu Me Lembrarei
Ficar atento às ”viagens” da mente.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('5d2566d8-d0c0-481b-9082-c174d17a9b63', 2, 7, 'Honestidade é uma palavra com a qual tive que me relacionar de novo. Antes de chegar a Jogadores Anônimos eu oscilava entre mentiras e meias verdades em minha mente com tanta freqüência,  que acreditava que todas elas fossem  verdades. Hoje me esforço para ser rigorosamente honesto – comigo mesmo e com os outros. Acima de tudo preciso sempre ser honesto comigo mesmo, quanto a quem seja o verdadeiro responsável por minha recuperação,  – com meu Poder Superior e com meus companheiros de Jogadores Anônimos.

Cheguei à conclusão de que o auto-engano só pode me prejudicar, criando uma visão irrealista e obscura da pessoa que realmente sou?

Hoje Eu Rezo
Que Deus permita que eu abra minha cortina de mentiras, álibis, racionalizações, justificativas, distorções e desculpas esfarrapadas,  e deixe entrar a luz nas verdades a meu respeito. Que eu descubra a pessoa que realmente sou e encontre conforto na pessoa  em que posso me transformar.

Hoje Eu Me Lembrarei
Olá, eu mesmo. Conheça o meu verdadeiro Eu.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('57963996-4918-45b8-9f47-98972bdd282b', 4, 25, 'Tenho muito mais a agradecer do que tenho consciência. Muito freqüentemente, esqueço-me de analisar todas as coisas em minha vida que poderia aproveitar e apreciar. Talvez eu não tire um tempo para esta importante meditação, porque estou preocupado demais com as minhas supostas aflições. Deixo que minha mente transborde de lamentações. Quanto mais penso nelas, mais monumentais elas se parecem. Ao invés de me render a Deus e à Sua bondade, deixo que os pensamentos negativos me dominem, com o perigo de se perderem; a não ser que  eu os direcione com firmeza em direção a caminhos mais iluminados.

Tento cultivar uma atitude de gratidão?

Hoje Eu Rezo
Que Deus me afaste de um acúmulo de pensamentos negativos, que me desviam do caminho do crescimento pessoal. Que eu interrompa o hábito do pobre coitado que só lembra e espera pelo pior. Que eu direcione meus pensamentos para um mundo completamente novo no futuro. Que eu crie condições para que possa vislumbrar a glória de Deus.

Hoje Eu Me Lembrarei
Manter uma atitude de gratidão.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('7d07f59e-e38f-48eb-9242-f1d6762c5b30', 10, 3, 'Em Jogadores Anônimos, aprendi que sou completamente impotente perante minha compulsão. Finalmente aceitei minha impotência; e em função disso, minha vida deu uma guinada de 180 graus para melhor. Entretanto, realmente tenho o poder, concedido por Deus, de mudar minha vida. Aprendi que a aceitação não significa submissão a uma situação desagradável ou degradante, mas sim a aceitação da realidade da situação, para em função disso depois decidir o que posso fazer e o que farei se necessário.

Parei de tentar controlar o incontrolável? Estou conseguindo coragem para modificar as coisas que posso modificar?

Hoje Eu Rezo
Peço a orientação de meu Poder Superior à medida que aprendo a separar as coisas que posso modificar daquelas que não posso, porque esse processo de seleção realmente necessita da sabedoria concedida por Deus. Que “as coisas que eu posso modificar” não incluam o controle da vida de outras pessoas. Que eu comece a entender  minha própria realidade.

Hoje Eu Me Lembrarei
Aceitação não significa estagnação. Mudança não significa domínio.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('8f049695-8b79-4865-bf4c-abfd19e136f3', 11, 16, 'Algumas vezes ouvimos a palavra humildade sendo definida como o estado de “estar aberto à aprendizagem”.  Nesse sentido, a maioria de nós em Jogadores Anônimos, que consegue ficar longe do jogo, já adquiriu pelo menos a noção do que seja humildade, ou nunca teria aprendido a ficar longe da primeira aposta. Passei a entender que a humildade implica em estarmos dispostos a ouvir outras pessoas, permanentemente abertos à aprendizagem.

Entendo a humildade como o caminho para a melhoria contínua?

Hoje Eu Rezo
Agora que dei início ao desenvolvimento da humildade, que eu mantenha esse desenvolvimento. Que eu tenha a abertura mental à vontade de Deus e às sugestões de meus amigos do grupo. Que continue aberto à aprendizagem, aceitando questionamentos, sendo receptivo e consciente de que preciso continuar nesse caminho para alcançar a sanidade.

Hoje Eu Me Lembrarei
Permanecer aberto aos questionamentos.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('f97b44a2-a47c-41ef-aac6-c903d6693f62', 4, 26, 'Quando cheguei a Jogadores Anônimos pela primeira vez, fiquei abismado com o barulho constante das risadas. Hoje, percebo que o entusiasmo e a alegria são úteis. Algumas vezes, as pessoas de fora se surpreendem quando caímos na risada com algumas histórias tristes – e até mesmo trágicas – do passado. Mas por que não deveríamos nos rir delas? Descobrimos o caminho para a recuperação, bem como o caminho para ajudar outras pessoas iguais a nós. Que outra causa melhor do que esta poderia haver para  nos  alegrar?

Já comecei a recuperar o meu senso de humor?

Hoje Eu Rezo
Que Deus me restitua o meu senso de humor. Que eu aprecie o riso honesto que serve como música de fundo para o regozijo comum de nossa abstinência. Que eu  ria muito, não um riso de auto-defesa, que ridiculariza a fraqueza do outro, nem o riso perverso que nos diminui, e sim o riso saudável que coloca as situações em sua verdadeira perspectiva. Que eu nunca considere este tipo de riso como irreverente. Ao contrário, aprendi que irreverência é levar-me a sério demais.

Hoje Eu Me Lembrarei
O senso de humor é sinal de sanidade.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('d714d6b2-f898-43ad-9b08-d86546883213', 6, 7, 'Poucos de nós estão completamente isentos do sentimento de culpa. Podemos nos sentir culpados por coisas que dissemos ou fizemos e pelas coisas que deixamos de dizer ou fazer. Estamos tão acostumados a esses sentimentos de culpa, que mesmo quando somos acusados injustamente nos sentimos culpados. Quando estou carregado de culpa, certamente não posso dedicar ao dia toda a energia de que sou capaz. Por isso preciso me livrar dessa culpa, não colocando-a de lado ou ignorando-a, mas sim identificando-a e corrigindo sua causa.

Comecei finalmente a aprender a “simplificar tudo”?

Hoje eu Rezo
Já que a culpa é um sentimento conhecido dos Jogadores Anônimos, que eu aprenda a identificar quando meus sentimentos representam um arrependimento natural com relação ao que aconteceu, ou quando estou assumindo a postura de culpa perpétua. Conto com Deus para ajudar-me a analisar as coisas e livrar-me das ferroadas e agonias de culpa, que precisa ser reconhecida e descarregada.

Hoje Eu Me Lembrarei
O veredicto para a culpa não é perpétuo.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('e08ef525-4fc6-47fc-bee6-93a415bbd407', 12, 3, 'Nossa velha inimiga, a vontade própria, veste uma máscara, que confronta-me com a seguinte racionalização: - Por que tenho que depender de Deus? Ele já não me deu a inteligência para que possa pensar por mim mesmo? Preciso me conter quando tais pensamentos aparecem, lembrando-me,  de que na verdade nunca fui realmente capaz de conseguir os resultados que desejava, dependendo unicamente de meus próprios recursos. Não sou auto-suficiente, nem sei todas as respostas;  minha própria amarga experiência  me ensina isso.

Sei que preciso da orientação de Deus? Estou disposto a aceitá-la?

Hoje Eu Rezo
À medida que eu me torno mais forte em minha convicção e em minha abstinência, rezo para não começar a menosprezar minha dependência em um Poder Superior. Que eu continue a pedir por orientação, mesmo quando as coisas parecerem estar transcorrendo tranqüilamente. Que eu saiba que preciso de meu poder Superior, tanto nas horas de triunfo quanto nas horas de traumas.

Hoje Eu Me Lembrarei
A auto-suficiência é um mito sem Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('15090e17-790a-4404-9aff-8ce9a050cb8c', 7, 24, 'Como, exatamente, pode uma pessoa entregar a sua vida e sua vontade aos cuidados de um Poder Superior? Tudo o que é preciso é começar, não importa de que maneira. No minuto em que colocarmos a chave da boa vontade dentro da fechadura, a porta se abrirá. E continuará se abrindo, mesmo que um pouquinho de cada vez. Com o tempo descobriremos que podemos abri-la ainda mais. Com vontade própria poderemos fechar a porta novamente, e isto sempre acontece. Mas a porta pode ser sempre reaberta quando necessário, desde que usemos a nossa chave da boa vontade.

Reafirmei a minha decisão de entregar a minha vontade e a minha vida ao cuidados  de Deus, como assim eu o entendo?

Hoje Eu rezo
Que eu renove a minha decisão de entregar a minha vontade e a minha vida a um Poder Superior. Que a minha fé seja forte o suficiente para me manter ciente de que existe, de verdade, um poder maior do que eu. Que eu entregue a minha vida a este Poder, simplesmente por estar disposto a humildemente caminhar ao lado Dele.

Hoje Eu Me Lembrarei
Vontade própria menos “própria” é igual à vontade.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('75e090e2-b3d1-41bf-9e1f-d62b432072bf', 8, 30, 'Começarei hoje com  uma oração – a oração em meu coração, a oração em minha mente e as palavras da oração em meus lábios. Através da oração hoje estarei voltado para Deus hoje, procurando tornar-me o que aspiro ser. A oração redirecionará minha mente, ajudando-me a crescer em termos de consciência até  entender que não há separação entre Deus e mim. À medida que permito que o poder de Deus flua em todo o meu ser, todas as limitações terminarão.

Sei que nada pode ser maior que o poder de Deus em minha vida?

Hoje Eu Rezo
Que eu hoje possa oferecer uma oração constante ao meu Poder Superior, não somente uma “uma oração matinal única que eu considere suficiente”. Que eu pense em meu Poder Superior de madrugada, nos intervalos do café, almoço, à tardinha, ou durante uma noite serena – e durante todos os momentos de intervalo. Que minha conscientização aumente e dissipe as linhas de separação, para que o Poder Superior seja parte de mim e eu seja parte Dele.

Hoje Eu Me Lembrarei
Viver a oração em toda a extensão do dia.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('bf7908c7-53d3-494b-91b4-54b73d9c6622', 1, 17, 'Tenho sido cada vez mais advertido para que me esforce bastante para abandonar minhas antigas idéias - é fácil falar, pensei algumas vezes. Fui programado como um computador: “inputs” específicos deram origem a reações previsíveis. Minha mente ainda tende a reagir como um computador, mas estou aprendendo a destruir os arquivos antigos e literalmente me reprogramar.

Estou completamente disposto a abandonar minhas antigas idéias? Estou sendo corajoso e minucioso  diariamente?

Hoje Eu Rezo
Por favor, Deus, ajude-me a fazer um inventário  de meu estoque de novos  e saudáveis pensamentos a cada dia, jogando fora os antigos sem arrependimento ou nostalgia à medida que eu os analiso. Afinal, eu superei as idéias antigas, que são tão gastas quanto um velho par de sapatos. Agora, sob a luz, posso ver que estão cheios de buracos.

Hoje Eu Me Lembrarei
O programa reprograma.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('4f62ae3f-3e1b-4a61-a45c-9ab91823489c', 12, 14, 'Alguns de nós em Jogadores Anônimos temos a tendência de cometer o erro de achar que os poucos momentos que gastamos em uma oração e meditação – “conversando com Deus” – é só o que precisamos. Na verdade, a atitude que tomamos ao longo do dia tem a mesma importância. Quando nos colocamos nas mãos de Deus pela manhã, e ao longo do dia nos tornamos dispostos a aceitar Sua vontade como ela nos é trazida através dos acontecimentos diários, nossa atitude de aceitação torna-se uma oração constante.

Consigo tentar cultivar uma atitude de total aceitação a cada dia?

Hoje Eu Rezo
Que eu mantenha contato com o meu Poder Superior durante o dia inteiro, ao invés de fazer uma oração esporádica. Que minha comunhão com Deus nunca se torne um recurso ocasional. Que eu seja capaz de perceber, que todas as vezes que faço alguma coisa que esteja de acordo com a vontade de Deus, estarei fazendo uma oração.

Hoje Eu Me Lembrarei
A oração é uma atitude.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('fa73d060-0803-4ba8-9871-e6c75657699c', 11, 23, 'Ante de chegar a Jogadores Anônimos, eu agia como um ator insistindo em escrever o “script”, produzir, dirigir e, em suma, comandar todo o espetáculo. Eu tinha que fazer as coisas à minha maneira, sempre tentando arrumar e re-arrumar as luzes, as falas, os cenários e, acima de tudo, o desempenho dos outros atores. Para que o espetáculo fosse fantástico só era preciso que meus arranjos ficassem como eu queria e as pessoas se comportassem como eu desejasse. Minhas fantasias levavam-me a acreditar que se todos simplesmente se amoldassem à  minha maneira, tudo daria certo. É claro que isso funcionou daquela maneira.

Não é surpreendente como os outros parecem “se amoldar” agora que parei de tentar controlar  tudo e todos?

Hoje Eu Rezo
Que me convença a abandonar minha antiga necessidade premente de controlar tudo e todos. Passou a época, em que se eu não pudesse dirigir tudo de forma direta, eu o faria de forma indireta, através de manipulações, arranjos secretos, e interferências. Que eu saiba que, se eu continuar sendo a pessoa que está sempre controlando os cordões das marionetes, também serei a pessoa que ficará frustrada quando os mesmos fracassarem ou caírem do palco.

Hoje Eu Me Lembrarei
Somente posso “amoldar” a mim mesmo.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('678170ae-81a3-4c65-8d28-627e6314cf85', 8, 28, 'Soren Kiekegaard escreveu que “As orações não modificam Deus” , “mas àquele que ora’. Aqueles que no Programa de Jogadores Anônimos aprenderam a fazer uso constante da oração não poderiam mais viver sem ela, da mesma forma que não poderiam viver sem o calor do sol, sem ar puro ou sem  comida. Da mesma forma que o corpo pode definhar por falta de nutrição, a alma também pode. Todos necessitamos da luz da realidade de Deus, da nutrição de Sua força e da atmosfera de Sua graça.

Agradeço ao Deus de meu entendimento, por tudo que Ele já me proporcionou, por tudo que Ele já tirou de mim, e por tudo o que Ele deixou para mim?

Hoje Eu Rezo
Querido Poder Superior: quero agradecê-Lhe por ter espalhado a paz sobre a minha confusão, por ter trazido de volta a harmonia às desafinadas cordas  de meus relacionamentos humanos, por ter juntado as pequenas peças  de meu pobre ser confuso, por ter me presenteado com a abstinência, e todo um grande mundo pleno de maravilhas e oportunidades. Que eu permaneça verdadeiramente Seu, Seu verdadeiramente.

Hoje Eu Me Lembrarei
A oração, mesmo a mais simples, nutre a alma.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('e294a4c6-75b8-45dc-be3a-ded2a50221c9', 10, 14, 'Alfred North Whitehead escreveu que “o verdadeiro progresso tem a ver com a re-interpretação das idéias básicas”. Quando revemos os altos e baixos de nossa recuperação em Jogadores Anônimos, podemos ver a verdade dessa frase. Progredimos todas as vezes que nos livramos de uma idéia antiga, todas as vezes que revelamos um defeito de caráter, todas as vezes que nos tornamos dispostos a ter esses defeitos removidos. Progredimos, um dia de cada vez, quando evitamos a primeira aposta, esse primeiro ato de compulsão,  que poderia rapidamente nos desviar do caminho do crescimento levando-nos ao caminho do desespero.

Avalio o progresso que fiz desde de que cheguei a Jogadores Anônimos?

Hoje Eu Rezo
Que eu me lembre de que há poucas idéias novas neste mundo - somente as antigas que são re-interpretadas e expressadas de outra forma. Que eu tenha sempre consciência de que as grandes coisas da vida – como o amor, a fraternidade, Deus, a abstinência do comportamento compulsivo – tornam-se mais amplamente definidas em cada vida humana. Da mesma maneira,  devemos redefinir os Doze Passos de Recuperação na vida de cada um de nós, se tivermos em mente que eles são princípios que foram testados ao longo do tempo e que funcionam!

Hoje Eu Me Lembrarei
Os Doze Passos funcionam.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('b6cc2d96-be2a-4622-a429-7a22cc5dee98', 5, 18, 'Nos dias de jogatina, eu me considerava um ‘solitário”. Embora freqüentemente estivesse com outras pessoas  - eu as via, ouvia, tocava – a maior parte de meus importantes diálogos eram com o meu ser interior. Tinha certeza de que ninguém jamais poderia me entender. Se considerasse minha antiga opinião sobre mim mesmo, é provável que eu não quisesse que alguém me entendesse. Eu sorria mostrando os dentes, mesmo que estivesse morrendo por dentro.

Meus sentimentos interiores começaram a equiparar-se às minhas expressões exteriores  desde que ingressei no Programa de Jogadores Anônimos?

Hoje Eu Rezo
Que meu ser espiritual, intelectual, emocional e físico novamente se transforme em um só, ou seja, em  um ser completo. Agradeço ao meu Poder Superior por me mostrar como equiparar meu exterior com o meu interior, para rir quando sentir vontade, chorar quando estiver triste e reconhecer minha própria raiva, medo ou culpa. Rezo para me tornar completo.

Hoje Eu Me Lembrarei
Estou me tornando um ser completo.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('6e2424ed-6b21-40a2-8c3e-07ad64d04083', 2, 9, 'O slogan “Viva e deixe Viver” pode ser muito útil quando temos dificuldade para aceitar o comportamento dos outros. Sabemos, com certeza, que o comportamento de nenhuma pessoa – não importa o quão ofensivo, desagradável ou mau – merece o preço de uma recaída. Nossa própria recuperação é mais importante, e ao mesmo tempo em que não devemos ter medo de nos afastarmos das pessoas ou situações que nos desagradam, precisamos também tentar fazer um esforço adicional para entender os outros – principalmente aqueles que nos tiram do sério.

Posso aceitar o fato de que, em minha recuperação, é mais importante compreender do que ser compreendido?

Hoje Eu Rezo
Quando eu me irritar com o comportamento desagradável de alguém, que eu primeiro tente dar o melhor de mim para compreendê-lo. Então, se minha abstinência do jogo parecer ameaçada, que eu tenha a coragem de me afastar da situação.

Hoje Eu Me Lembrarei
Viva e deixe viver.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('958c3835-f1f7-4e6b-8b28-aec4cf3af085', 1, 31, 'Uma das coisas mais construtivas que posso fazer é aprender a me ouvir e a entrar em contato com meus verdadeiros sentimentos. Durante anos não dei atenção a isso, continuando ao invés disso, a dar atenção ao que os outros diziam e sentiam. Ainda hoje, tenho, às vezes, a impressão de que eles sabem o que fazem, enquanto ainda estou tropeçando por aí. Felizmente, começo a entender que o agradar a todos assume diversas formas. Lenta e efetivamente, também começo a perceber que é possível mudar meus velhos padrões de comportamento.

Insisto em me sintonizar com o meu verdadeiro eu? Ouvirei atentamente minha própria voz interior, esperando que esta me diga algumas coisas maravilhosas?

Hoje Eu Rezo
Rogo para que eu respeite a mim mesmo, a ponto de ouvir meus verdadeiros sentimentos, as mesmas emoções que durante tanto tempo me recusei a ouvir, nomear ou possuir; emoções estas que se alojaram dentro mim como um veneno. Que eu me conscientize de que preciso parar freqüentemente, para analisar meus sentimentos e ouvir o meu eu interior.

Hoje Eu Me Lembrarei
Serei dono de meus sentimentos.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('521e0a23-3673-48ac-a718-fca85f2d30c1', 4, 27, 'Tenho toda a certeza de estar fazendo todo o possível para tornar  a minha nova vida um sucesso? Estou usando bem minhas capacidades? Reconheço e valorizo tudo pelo que devo ser grato? O Programa de Jogadores Anônimos e seus Doze Passos de Recuperação me ensinam que estou de posse de recursos ilimitados. Quanto mais realizo com eles, mais esses recursos aumentarão – para superar e eliminar todos os sentimentos difíceis e dolorosos que agora exigem tanto de minha atenção.

Estou menos sensível hoje do que quando cheguei ao Programa pela primeira vez?

Hoje Eu Rezo
Que eu dê o máximo de mim todos os dias. Que eu comece a dar atenção a outras pessoas e oportunidades e aos maravilhosos recursos à minha volta. À medida que me torno menos voltado para mim mesmo e me compreendo melhor em relação aos outros, que eu me torne menos vulnerável e menos sensível. Que eu me livre do sentimento de que todos estão “querendo o meu mal” e veja este mesmo mundo como a minha “arca do tesouro” legada a mim por Deus e sem limites.

Hoje Eu Me Lembrarei
Meus recursos são ilimitados.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('514a9b0e-dbdb-454b-8eb6-d42cd5c60523', 11, 4, 'Nas palavras de Teilhard de Chardin “Não somos seres humanos possuidores de experiências espirituais, mas sim seres espirituais possuidores de experiências humanas.” Apesar de em teoria reconhecermos que somos seres espirituais, a maioria dos jogadores compulsivos precisou ter algum tipo de despertar espiritual antes que se dispusesse a entregar sua vida a um Poder Superior. Só assim então  finalmente pudemos dizer que nos tornamos seres espirituais. Para alguns de nós era como se tivéssemos tido contato com a espiritualidade pela primeira vez em nossas vidas. Mas, independente de termos tido ou não uma base espiritual anterior, no momento em que descobrimos essa nova noção de espiritualidade, ela tornou-se uma experiência e um sentimento que tão logo não iríamos querer abandonar.

Sou grato pelo “despertar” que me colocou em contato com meu Poder Superior – e com minha própria essência espiritual?

Hoje Eu Rezo
Que eu nunca me esqueça de como minha visão da vida, de mim mesmo, de meus amigos, de meus entes queridos, e de Deus estava deteriorada antes de chegar a Jogadores Anônimos. Que eu faça o que for necessário para impedir que minha vida espiritual chegue ao fundo do poço mais uma vez. Que eu mantenha o meu crescimento espiritual – um dia de cada vez.

Hoje Eu Me Lembrarei
Deixar minha espiritualidade guiar minha condição humana.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('ef326704-d222-45cf-8a15-190bfd263b31', 5, 12, 'Minha coragem precisa surgir a cada dia, assim como meu desejo de evitar uma única aposta, um único ato compulsivo. Ela precisa ser uma coragem contínua, sem desvios e procrastinação, sem precipitação, sem medos de obstáculos. Na verdade, isso poderia parecer uma grande ordem , se não fosse pelo fato de que está confinada a um único dia, e que para esse único dia recebi todo o poder.

Estendo a Oração da Serenidade para toda a minha vida?

Hoje Eu Rezo
Que cada nova manhã me ofereça um suprimento de coragem que dure todo o dia para mim. Se minha coragem for renovada a cada dia e eu souber que preciso dela somente para um dia, essa coragem sempre estará viva e seu suprimento não se esgotará. Que eu me dê conta, à medida que os dias passam, que o que eu temia durante os primeiros dias de minha recuperação eu não mais temo e que a minha coragem diária está agora me ajudando a lidar com os problemas maiores.

Hoje Eu Me Lembrarei
Deus concedei-me coragem – somente para o dia de hoje.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('66ece784-e340-4c64-9ff3-75d96f17880b', 5, 10, 'À medida que o descrente tenta o processo da oração, ele deveria também somar os resultados. Persistindo, é quase certo que tenha mais serenidade, mais tolerância, menos medo e menos raiva. Ele conseguirá uma coragem tranqüila – não um tipo de coragem que disfarça a tensão. Ele será capaz de olhar para os “erros” e os “acertos” da forma como realmente são. Os problemas e as calamidades começarão a fazer parte de seu aprendizado, ao invés de sua destruição. Ele se sentirá mais livre e mais são.

Coisas maravilhosas e inexplicáveis começaram a ocorrer em minha nova vida?

Hoje Eu rezo
Que eu comece a ver a minha vida se resolver através da oração e comunhão com um Poder Superior. Que eu me torne menos tenso,  mais saudável, mais aberto, mais corajoso, mais amável, menos envolvido em problemas, menos temeroso de perder e de viver. Que eu saiba que Deus também quer essas coisas para mim. Seja feita a vontade de Deus.

Hoje Eu Me Lembrarei
Ficar sereno e saber que Ele é Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('7591e28b-88a7-476b-87e3-58ee3440893c', 6, 9, 'Alguns de nós ainda novos em Jogadores Anônimos, não conseguíamos deixar de contar a alguém que estivesse disposto a escutar, como éramos simplesmente “terríveis.” Assim como por orgulho, costumávamos exagerar sobre nossos feitos modestos, também exagerávamos sobre nossos defeitos por culpa. Como se estivéssemos em uma disputa, e “confessando tudo”, achávamos de alguma forma que o relato exagerado de nossos pecados era sinal de verdadeira humildade, dando a esse relato uma qualidade espiritual. Somente quando progredimos no Programa, conseguimos nos dar conta de que a nossa atitude teatral e as histórias eram meras formas de exibicionismo. Com essa conscientização, começamos a ter um pouco de humildade.

Estou começando a me tornar consciente de que afinal de contas não sou tão importante assim?

Hoje Eu Rezo
Que eu aprenda que existe uma grande diferença entre a verdadeira humildade e a dramática auto-depreciação. Que eu me questione, se com minhas histórias de jogo,  queira inconscientemente me colocar no centro das atrações por achar que fiz as piores coisas ou corri mais riscos que outras pessoas. Que eu tenha o cuidado de não dar uma conotação de grandeza “épica” de feitos  heróicos, aos relatos de meus maus feitos de jogo. Que eu evite chegar aos limites do orgulhoso “monólogo do jogo”.

Hoje Eu Me Lembrarei
Não serei o astro de meu próprio “monólo-jo-go”.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('042b5866-7e57-4d96-a510-0d6993147ae8', 5, 31, 'O ato de dar amor é por si só uma realização. Não é importante se somos ou não correspondidos. Se eu der amor somente para recebê-lo de volta, nos meus termos, esse amor será anulado em virtude de minha motivação. Se eu tiver a capacidade de dar amor, então qualquer retorno que tiver será um bônus extra. É dando amor, livremente e sem esperar retorno, que encontramos a nós mesmos e crescemos espiritualmente.

Comecei a acreditar, segundo Goethe, que o “amor não domina”,  cultiva &#8230;?”

Hoje Eu Rezo
Que eu, sendo um inveterado adulador e carente de aprovação, saiba que o  verdadeiro amor não exige nada em troca. Que Deus seja paciente enquanto tento praticar este princípio. Que eu me livre do orgulho que se interpõe ao amor. Que eu me livre dos jogos infantis de gato-e-rato que não têm lugar no amor.verdadeiro.

Hoje Eu Me Lembrarei
Não darei amor para recebê-lo de volta.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('0c72b51a-87d1-4c71-9d55-33c869e19d05', 6, 14, 'Em algum ponto ao longo do caminho, à medida que ficamos mais envolvidos com o Programa de Jogadores Anônimos, atingimos a consciência exata do verdadeiro valor da honestidade e sinceridade. Quando isso acontece, uma das primeiras coisas que conseguimos admitir é que nosso comportamento anterior estava muito aquém da sanidade ou da racionalidade. Assim que nos tornamos capazes de admitir isso – sem vergonha ou constrangimento – descobrimos também uma outra dimensão para a liberdade.

Em minha recuperação gradual, espero que minha vida algum dia se torne mais plena e mais serena?

Hoje Eu Rezo
Que eu saiba, mesmo quando aceito  esse Primeiro Passo grandioso, que este deve ser o primeiro passo verdadeiramente honesto que tenho que dar depois de muito tempo e que a honestidade exige prática. Minha antiga maneira de  ser, ilusória e enganadora, é diferente da pessoa honesta que preciso me tornar, que é tão diferente quanto a noite é do dia. Que eu me conscientize da necessidade desta mudança e que isso levará mais tempo do que somente uma madrugada sombria.

Hoje Eu Me Lembrarei
A honestidade requer prática.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('f7da778e-1a4b-46ed-b9ef-8751f4a46143', 1, 12, 'Quando me sento calmamente e comparo minha vida de hoje com a maneira como ela costumava ser, a diferença é quase inacreditável. Mas as coisas não são sempre um mar de rosas. Alguns dias são bem melhores que outros. Eu tendo a aceitar mais facilmente os dias ruins racionalmente do que emocionalmente, ou segundo meus instintos. Não há uma única resposta certa, mas parte da solução está, com certeza, no constante esforço da prática dos Doze Passos.

Aceito o fato de que meu poder Superior jamais me dará mais do que eu possa suportar –  um dia de cada vez?

Hoje Eu Rezo
Que eu me fortaleça através do conhecimento de que Deus nunca nos dá mais do que podemos suportar, e que eu sempre consiga,  de alguma forma, suportar a dor do presente, já que as provações de uma vida inteira, reunidas em um momento desastroso, seriam, certamente, demais para mim. Agradeço a Deus por nos dar apenas as atribulações, que são proporcionais à nossa capacidade, jamais nos destruindo em nossa fragilidade. Que eu me lembre que a resistência aumenta através do  sofrimento.

Hoje Eu Me Lembrarei
A dor do presente é suportável.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('b39ddcf8-eced-4a8f-af89-33c890345db0', 1, 10, 'Desde que cheguei a Jogadores Anônimos, tornei-me cada vez mais ciente da Oração da Serenidade. Eu a vejo nas capas dos livros, nas paredes das salas de reunião e nas casas dos novos amigos. “Deus concedei-me a serenidade para aceitar as coisas que não posso modificar, coragem para modificar aquelas que posso e sabedoria para perceber a diferença.”

Entendo a Oração da Serenidade? Acredito em seu poder e a repito freqüentemente? Está se tornando mais fácil para mim aceitar o que não posso modificar?

Hoje Eu Rezo
Que Deus faça com que as palavras da Oração da Serenidade nunca se tornem mecânicas para mim ou percam seu significado, no ritmo sereno da repetição. Rezo para que estas palavras continuem a ter  significados cada vez mais fortes à medida que ajusto a realidade da vida a elas. Acredito que eu possa encontrar nesta oração as soluções que necessito, pois em sua simplicidade, ela abrange todas as situações de vida.

Hoje Eu Me Lembrarei
Compartilhar a oração', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('0bd1b452-75e0-4d8f-a815-6bd2debdb8ae', 7, 19, 'Muitos de nós vêm a Jogadores Anônimos afirmando que somos agnósticos ou ateus. Como alguém já disse uma vez, o nosso desejo de desacreditar é tão forte que preferimos um encontro com algo menor do que uma procura experimental e de mente aberta por um Poder Superior. Felizmente, para aqueles dentre nós que possuem a mente fechada, os preceitos construtivos do Programa de JA quase sempre acabam vencendo nossa obstinação. Não muito tempo depois, descobrimos um mundo abundante de fé e confiança. Ele estava presente o tempo todo, mas faltava-nos boa vontade e mente aberta para aceitá-lo.

A obstinação ainda algumas vezes me cega ao poder do bem que reside na fé?

Hoje Eu rezo
Quero agradecer ao meu Poder Superior por esta oportunidade de abrir minha mente; de aprender novamente o que é fé e confiança; de entender que as minhas escapadas da honestidade e realidade não mudaram o lugar de Deus dentro de mim ou a preocupação de Deus para comigo. Que eu saiba que fui eu quem perdeu a fé. Obrigada a Deus por dar-me outra chance para  recuperá-la.

Hoje Eu Me Lembrarei
Descartar a vontade de não acreditar.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('02ac74cc-72cd-4b84-b2a2-a941d5cb9568', 2, 14, 'Ao listar as coisas às quais sou grato, passo de uma atitude negativa a uma atitude positiva. Hoje, passarei parte de meu tempo fazendo uma lista dos aspectos positivos de minha nova vida e das bênçãos que acompanham os milagres de minha recuperação. Sou grato por coisas aparentemente simples: a capacidade de adormecer tranqüilo, de acordar com alegria de estar vivo, de encarar a vida seguindo as suas regras – com paz de espírito e respeitando a mim mesmo.

Esqueci que todas as minhas necessidades estão sendo atendidas hoje? Faço uma lista diária de minhas bênçãos?

Hoje Eu Rezo
Que neste dia de amor, eu leve em conta todas as coisas boas de minha vida e seja grato por elas. Que eu leve em consideração todas as bênçãos recebidas, inclusive meus batimentos cardíacos e a atual sensação de estar respirando um novo ar.

Hoje Eu Me Lembrarei
De listar – e considerar – as minhas bênçãos.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('89dea8ef-6e6e-4624-9f9a-ead5be662fb6', 11, 22, 'Tocqueville escreveu que “temos sucesso nos empreendimentos que exigem as qualidades positivas que temos, mas nos superamos naqueles que também podem fazer uso de nossos defeitos.” Em Jogadores Anônimos aprendemos que nossos defeitos também têm valor – se os usarmos como base para as mudanças e como caminho para atingir coisas melhores. O medo pode funcionar como ponto inicial para a prudência, por exemplo, bem como para que sintamos respeito por outras pessoas. O medo também pode nos afastar do ódio e nos levar à compreensão. Da mesma forma, o orgulho pode nos levar em direção à humildade.

Conheço a minha direção hoje? Sei da importância de saber para onde estou me dirigindo?

Hoje Eu Rezo
Oro para que meu Poder Superior me mostre como usar meus defeitos de maneira positiva, pois nada – nem mesmo o medo, o egoísmo ou a ganância – é de todo ruim. Que eu acredite que todas as qualidades que me levam a ter problemas possuem o outro lado, o que pode me ajudar a resolvê-los. O orgulho, por exemplo, não pode se expandir de forma indevida sem aflorar e mostrar que é, em sua essência, um mero vapor quente. Que eu aprenda com minhas fraquezas.

Hoje Eu Me Lembrarei
Boas notícias surgem das ruins.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('2ace75a1-912e-41e4-ba37-e4e2710158cb', 10, 9, 'No Programa de Jogadores Anônimos, lembro-me de ter ouvido alguém dizer que a “Vida é uma série de coisas com que concordamos ou não em relação ao universo”. Há muita verdade nesta frase, porque sou apenas uma pequena engrenagem na máquina do universo. Quando tento fazer as coisas à minha maneira, tenho somente frustrações e um sentimento de fracasso. Se, por outro lado, aprendo a deixar as coisas acontecerem, o sucesso será certamente meu. Assim, terei tempo para contar minhas bênçãos, para trabalhar os meus pontos fracos e para viver de forma rica e completa o Agora.

Acredito que o que me foi dado saber, saberei, se praticar o Passo Onze – pedindo somente pelo conhecimento da Sua vontade em relação a mim e o poder de realizá-la?

Hoje Eu Rezo
Que eu aprenda a me direcionar com o Passo Onze – e não caia no meu hábito usual de preparar listas pormenorizadas para Deus de todos os meus pedidos, súplicas e reclamações. Que não mais faça previsões do que Deus faria com minhas soluções específicas, e simplesmente ore para que a Sua vontade seja feita. Que, ao invés de contar meus pedidos urgentes,  eu conte minhas bênçãos

Hoje Eu Me Lembrarei
Parar de preparar listas por Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('0cd60d1d-b411-4024-9cfa-3a9f3695d18e', 11, 1, 'As pessoas que mais respeito no Programa de Jogadores Anônimos e – que, por extensão – são aqueles com quem mais aprendi – parecem estar convencidos de que o orgulho é, como alguém o definiu, a própria origem do pecado. Na teologia moral, o orgulho é  o primeiro dos sete pecados capitais. Também é considerado o mais sério, ficando destacado dos outros em função de sua característica peculiar. O orgulho afeta diretamente as nossas realizações espirituais. Ela se insinua em todos os nossos sucessos e realizações, mesmo quando os atribuímos a Deus.

Luto contra o orgulho ao praticar o Décimo Passo com regularidade, encarando sempre a mim mesmo e fazendo as coisas certas exatamente onde  elas deram errado?

Hoje Eu Rezo
Que eu fique constantemente em alerta contra as armadilhas do orgulho, que podem traiçoeiramente afetar todas as realizações, todos os triunfos e todos os afetos correspondidos. Que eu saiba que quando as coisas derem certo para mim, meu orgulho ficará sempre à espreita pronto para receber os méritos. Que eu fique atento a isso.

Hoje Eu Me Lembrarei
Coloque o orgulho em seu devido lugar.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('3c0d2228-3314-401f-b3b6-debbb624c991', 12, 9, 'Beecher escreveu que “as dificuldades são encargos de Deus; e quando nos defrontamos com elas, devemos considerá-las como provas da confiança Dele”. Cheguei à conclusão de que na verdade eu mesmo criei meus problemas do passado. Embora eu mal pudesse me dar conta disso naquele tempo, fui o exemplo perfeito daquilo que o Programa de Jogadores Anônimos descreve como um desastre causado pela própria vontade. Hoje aceitarei minhas dificuldades como prenúncios de crescimento e como evidência da confiança de Deus em mim.

Acredito que Deus nunca me dará mais do que eu puder suportar?

Hoje Eu Rezo
Que eu acredite piamente que Deus acredita que eu possa resolver meus problemas, que as dificuldades que tiver pela frente serão diretamente proporcionais à minha força e capacidade de suportá-las e manter a cabeça fria em uma crise. Que eu compreenda também que é a minha fé em Deus que me impede de me destruir totalmente.

Hoje Eu Me Lembrarei
Deus tem fé em mim, porque eu tenho fé nele.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('91d2e0bb-e0d7-42b0-9187-9852a4839f48', 3, 21, 'O Programa de Jogadores Anônimos nos ensina que temos uma doença incurável. Sempre pioraremos, nunca o contrário. Mas somos privilegiados no sentido de que nossa doença incurável pode ser detida, um dia de cada vez, desde que não façamos a primeira aposta. Mesmo que a pesquisa científica séria e estudos importantes digam o contrário, sabemos por experiência própria que não podemos controlar nosso jogo assim como  não podemos controlar as marés dos oceanos.

Tenho dúvidas de que sou impotente perante o jogo?

Hoje Eu Rezo
Que eu nunca me torne  uma vítima de resultados de estudos a curto prazo, que me dizem que o jogo compulsivo tem cura, que seria seguro começar a jogar novamente, de uma forma supostamente responsável. Minha experiência – e a experiência de outros membros em Jogadores Anônimos – coloca por terra tais teorias. Que eu saiba que se me envolver novamente com o jogo recomeçarei o processo de onde estava – cada vez mais perto da prisão, insanidade ou morte.

Hoje Eu Me Lembrarei
Duvidar das novas teorias.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('250cb80a-fc80-4604-b140-1a35ce3497bc', 5, 17, 'Se nos sentíamos culpado, diminuídos ou envergonhados por nossa própria adicção ou pelas coisas que fizemos enquanto estávamos “na ativa”, isso servia para aumentar nossa sensação de sermos excluídos. De vez em quando receávamos secretamente ou realmente acreditávamos que merecíamos todo e qualquer sentimento de dor. Algumas vezes pensávamos que realmente éramos estranhos a tudo. O túnel escuro de nossas vidas parecia longo e interminável. Não conseguíamos expressar nossos sentimentos e muito menos suportar a idéia deles. Assim, em pouco tempo voltávamos a jogar.

Lembro-me bem de como era o passado?

Hoje Eu Rezo
Que eu me recorde da freqüência com que me sentia sozinho com a minha vergonha e culpa em meus dias de jogo. A falsa alegria de uma rodada de jogo ou os relacionamentos superficiais iniciados em cassinos, não conseguiam evitar que eu me sentisse  um estranho.Que eu valorize a oportunidade de fazer novos amigos através da Irmandade. Que eu perceba que meus relacionamentos agora serão mais saudáveis, menos dependentes e mais maduros.

Hoje Eu Me Lembrarei
Agradecer a Deus pelos novos amigos.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('fe9f9225-181d-4f36-8b72-df2ef51d0510', 9, 24, 'Goethe escreveu que “Todos querem ser alguém, mas ninguém quer crescer.” Como todos, às vezes me pergunto: - Quem sou eu? Onde estou? Para onde vou? Para que serve tudo isso? O processo de aprendizagem e  crescimento é geralmente lento. Eventualmente, o resultado de nossa procura sempre nos traz uma descoberta. E o que parece ser um grande mistério, freqüentemente,  desponta envolvido por completa simplicidade.

Aceitei o fato de que minha vontade de crescer é a essência de meu desenvolvimento espiritual?

Hoje Eu Rezo
Deus, concedei-me paciência e perseverança para continuar preparando esse longo caminho, mesmo que o seu fim esteja fora de vista. Os princípios do Programa de Jogadores Anônimos são o livro a que recorro para o crescimento, mais ainda do que a colheita. Essa colheita virá, abundante o bastante  para ser compartilhada, se eu cuidar de meu quintal.

Hoje Eu Me Lembrarei
Chegar lá, não estar lá.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('218d93ad-b95a-4e40-9df0-3a4be8d95ebc', 4, 20, '“Se alguém continuar a ver somente gigantes”, escreveu Anais Nin “isso significa que  ainda está vendo o mundo através dos olhos de uma criança”. Durante este período de vinte e quatro horas, não permitirei ficar me sobrecarregando de pensamentos sobre gigantes e monstros – de coisas que já passaram. Não me preocuparei com o dia de amanhã até que ele se transforme no meu dia de hoje. Quanto melhor eu usar o dia de hoje, mais provável será que o amanhã seja mais claro.

Estendi a mão amiga a outra pessoa hoje?

Hoje Eu Rezo
Deus, por favor, permita que eu cresça. Que eu não veja mais monstros e gigantes em minhas paredes, aquelas projeções da imaginação de uma criança. Que eu sepulte os meus  duendes malvados e acredite que os monstros épicos de nossos sonhos sejam distorções de meus medos atuais. Que eles desapareçam com os meus medos, no despertar de minha serenidade.

Hoje Eu Me Lembrarei
Dissiparei meus medos infantis.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('4fa3a206-9085-4244-a1da-f95f79fa9c44', 5, 11, 'Agora que sei que não posso mais fazer uso  de uma  coragem totalmente falsa, busco e peço por uma coragem  de vinte-e-quatro-horas,  para modificar aquilo que posso. É óbvio que esse não é o tipo de coragem que me tornará uma pessoa corajosa e forte por toda a vida, capaz de lidar corajosamente com alguma ou todas as situações. Ao invés disso, preciso de uma coragem persistente e sábia, que a cada dia que passa, se transporta  para um novo dia –  fazendo hoje somente o que pode ser feito hoje e evitando qualquer medo e preocupação quanto ao resultado final.

O que significa coragem para mim hoje?

Hoje Eu Rezo
Que eu me concentre somente nas coisas que tenho chance de modificar, começando essa mudança em mim mesmo, um dia de cada vez. Que eu saiba que a aceitação é geralmente uma forma de coragem. Rezo, não por uma super coragem, mas simplesmente pela persistência de lidar  com o  que a vida me oferecer,  sem me sentir sobrecarregado com isso.

Hoje Eu Me Lembrarei
Coragem é enfrentar um dia de cada vez.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('7768e1f1-fffc-45d9-8177-1f48109da785', 10, 7, 'Quando li a Oração da Serenidade pela primeira vez,  a própria palavra “serenidade” parecia uma impossibilidade. Naquele momento, a palavra englobava imagens de letargia, apatia, resignação, e resistência forçada; mal parecendo ser um objetivo desejável. Todavia, passei a entender que a serenidade não tem relação com qualquer uma dessas coisas. Para mim, hoje, serenidade é simplesmente uma maneira clara e realista de ver o mundo, combinada com uma paz interior e força. Minha definição favorita é: “a serenidade é como um giroscópio que nos faz manter nosso equilíbrio, não importando o tipo de turbulência que esteja ocorrendo à nossa volta”.

Esse é um estado de espírito que valha pena ser alcançado?

Hoje Eu Rezo
Que eu perceba que “serenidade” vem em primeiro lugar, antes de “coragem” e “sabedoria’ na seqüência da Oração da Serenidade. Que eu creia que a “serenidade” também precisa vir antes em minha vida. Preciso ter equilíbrio, uma visão realista, e a aceitação de que ela é parte da benção da serenidade, antes que eu possa partir para o tipo de tomada de decisão que trará  ordem para minha existência.

Hoje Eu Me Lembrarei
A serenidade vem primeiro.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('d48a0c05-f4b7-4f4f-84ce-9749c7ab90f6', 3, 13, 'Toda minha vida, tentei encontrar em outras pessoas o apoio, a segurança e todas as outras coisas que compõem o que agora chamo de serenidade. Mas agora percebo que estava sempre procurando no lugar errado. A fonte da serenidade não está fora de nós, mas dentro de nós mesmos. O reino está dentro de mim, e eu já tenho a chave dele. A única coisa que preciso fazer é estar disposto a usá-la.

Estou usando as ferramentas do Programa de Jogadores Anônimos em uma base diária? Estou disposto a fazê-lo?

Hoje Eu Rezo
Que Deus me dê coragem para procurar o reino dentro de mim mesmo, para descobrir  a fonte de bem-estar, que tem sua origem no rio de Deus, infinito e provedor da vida  Que minha alma seja restaurada nele. Que eu encontre a serenidade que procuro.

Hoje Eu Me Lembrarei
Procurar o reino interior.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('0faa9e4e-fa93-4c72-a00c-2b491cf95cb3', 12, 7, 'Enquanto teimosamente mantiver a convicção de que posso viver unicamente por conta de minha força e inteligência pessoal, será impossível adquirir uma fé operante em meu Poder Superior. Isto é verdade, independente de minha fé na existência de Deus. Não importa o quanto minhas crenças espirituais sejam sinceras; elas permanecerão eternamente sem sentido se eu continuar tentando fazer o papel de Deus. Enquanto eu colocar minha auto-dependência em primeiro lugar, será impossível existir uma verdadeira dependência a um Poder Superior.

Até que ponto  desejo  realizar a vontade de Deus?

Hoje Eu Rezo
Rezo para não colocar minha auto-dependência acima da dependência à Deus. Que eu saiba que não existe oposição entre assumir as responsabilidade de meus próprios atos, pois aprendi que isso representa a essência do maturidade, e pedir a ajuda de Deus. Que eu me lembre de que se eu me prender à regra do “eu mesmo decido”, isso seria como se me recusasse a pedir o mapa em um balcão de informações turísticas – e perambulasse perdido para sempre.

Hoje Eu Me Lembrarei
Maturidade é saber aonde procurar ajuda.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('6fd8b2b2-d306-4454-a2f7-59d2fe74e23d', 6, 3, 'Thomas Merton escreveu que “o despertar do amor representa deixar, que aqueles que amamos, sejam eles mesmos completamente sem que os modifiquemos  para que reflitam a nossa própria imagem; do contrário, amaremos neles somente o reflexo de nossa própria imagem que vemos neles” À medida que substituo minha adicção auto-destrutiva por uma dependência saudável ao Programa de Jogadores Anônimos e seus Doze Passos, descubro que as barreiras do silêncio e do ódio estão se dissipando. Ao aceitarmos uns aos outros como somos, aprendemos a amar de novo.

Importo-me o bastante com os outros no Programa de JA para continuar trabalhando com eles pelo tempo que se fizer necessário?

Hoje Eu Rezo
Que eu me afaste de meu próprio “eu” o bastante para amar as pessoas como elas são e não como eu quero que sejam, como se fossem o reflexo de mim mesmo ou a alimentação de meu ego. Que eu diminua a minha ânsia de amar – agora que sou novamente capaz de sentir amor – e indague a mim mesmo se estou realmente amando alguém ou somente a minha própria idéia de mim mesmo. Que eu elimine o “eu” de meus sentimentos.

Hoje Eu Me Lembrarei
O amor é incondicional.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('402d56d1-a388-48d6-a70e-5930afc7537b', 2, 3, 'Jogadores Anônimos nos torna capazes de descobrir dois obstáculos que nos impedem de ver o valor e o conforto do enfoque espiritual: auto-justificativa  e auto-confiança em excesso. A primeira hipocritamente me dá garantias de que eu sempre esteja certo. A Segunda me conforta erroneamente com a ilusão de que eu seja melhor do que as outras pessoas – “mais perfeito do que você”.

Só por hoje farei uma pausa momentânea quando estiver racionalizando e me perguntarei porque estou fazendo isso, e se minha auto-justificativa é realmente honesta?

Hoje Eu Rezo
Que eu supere a necessidade de estar “sempre certo” e conheça a sensação purificadora de alívio que vem da franca admissão de  um erro. Que eu saiba colocar-me como exemplo de auto-controle e resistência, dando crédito a quem merece – a um Poder Superior.

Hoje Eu Me Lembrarei
Errar é humano, mas é preciso admitir esse erro.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('b087d333-8e5b-4aa2-a479-5cf7b34e9a01', 1, 2, 'Antes de chegar ao Programa de Jogadores Anônimos eu não tinha a menor  idéia do que significava “Viver o Presente”.  Geralmente ficava  obcecado com coisas que haviam acontecido no dia anterior, na semana anterior ou cinco anos atrás. Pior ainda, passava  muitas de minhas horas de lucidez tentando impedir  “o desastre do futuro”. “Para mim”, escreveu Walt Whitman, “cada hora do dia e da noite é um milagre perfeito e inexplicável”.

Posso realmente acreditar nisso do fundo do meu coração?

Hoje Eu rezo
Permita-me carregar somente o peso de vinte-e-quatro horas de cada vez, sem o grande fardo adicional dos arrependimentos do passado ou das ansiedades do futuro. Deixe-me usufruir somente das bênçãos de cada dia, por elas mesmas,  e manter meus fardos humanos em uma  perspectiva diária. Que eu aprenda  que o equilíbrio da alma vem através da proximidade a Deus.

Hoje Eu Me Lembrarei
Não pedir  emprestado do amanhã.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('e753b250-288a-4621-af41-33accd460694', 5, 26, 'Hoje sei que não mais precisarei caminhar sozinho. Aprendi que é mais seguro, mais sensato e mais garantido ir avante  com amigos que caminham na mesma direção que eu. Nenhum de nós precisa sentir vergonha de pedir ajuda, já que todos nos ajudamos uns aos outros. Não é  sinal de fraqueza pedir ajuda na recuperação de minha adicção, da mesma forma que não é ao usarmos uma muleta quando quebramos a perna. A muleta é uma boa coisa para aqueles que necessitam dela e para aqueles que vêm sua utilidade.

Algumas vezes ainda recuso aceitar a ajuda obtida com facilidade?

Hoje Eu Rezo
Deus,  faça com que eu veja que não é sinal de fraqueza pedir ajuda; que é  a camaradagem do grupo que faz com que o mesmo funcione para cada um de nós. Como uma vacina contra a difteria ou pólio, o Programa de Jogadores Anônimos e a força do grupo já provaram sua eficácia como preventivos contra recaídas e deslizes. Agradeça a Deus pelas ferramentas da recuperação.

Hoje Eu Me Lembrarei
A ajuda está tão próxima quanto meu telefone.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('6ee9d742-12bb-4a9e-b737-8d6bd816e3c4', 10, 16, 'Alguém uma vez me disse que a direção da mente é mais importante do que seu progresso. Se minha direção estiver certa, então o progresso certamente virá. Viemos a Jogadores Anônimos,  primeiramente para receber algo para nós mesmos, mas logo aprendemos que recebemos mais abundantemente quando passamos aos outros. Se a direção de minha mente é dar ao invés de receber, então me beneficiarei muito além de minhas expectativas. Quanto mais dou de mim mesmo e abro meu coração com generosidade aos outros, mais crescimento e progresso alcançarei.

Estou aprendendo a não medir minha benevolência em relação ao que alcanço, aceitando que o ato de dar é por si só a sua própria recompensa?

Hoje Eu Rezo
Que eu não perca de vista os Pilares do Programa: ajudar a mim mesmo através da ajuda aos outros, em nosso propósito de alcançar a abstinência tranqüila do jogo. A partir do momento que faço o Primeiro Passo de Recuperação, que eu sinta a grandeza de dar e receber e dar de volta uma vez mais. Que eu me preocupe profundamente que as outras pessoas conservem sua libertação do jogo, e que eu saiba que eles se importam comigo. É uma troca singela – e bela .

Hoje Eu Me Lembrarei
Dar e receber e dar de volta mais uma vez.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('668f4fc8-a18f-4107-970e-e66700b16c0e', 7, 5, 'Sou livre para ser, fazer, aceitar, rejeitar. Sou livre para ser a pessoa sábia, amante, gentil e  paciente que desejo ser. Sou livre para fazer aquilo que acho certo – e que de maneira alguma irá machucar ou magoar alguém. Sou livre para fazer aquilo que me levará aos caminhos da paz e da satisfação. Sou livre para decidir a favor ou contra, dizer sim ou não. Sou livre para viver a vida de maneira produtiva e para contribuir com o que puder para a vida.

Eu estou chegando à conclusão de que sou livre para ser o melhor que posso ser?

Hoje Eu Rezo
Que a liberdade que eu estou agora experimentando continue a fluir ao longo de minha vida de maneira produtiva, até convencer-me de que há coisas boas na vida que sempre desejei usufruir. Que eu aceite esta liberdade com a benção de Deus – e a use com sabedoria.

Hoje Eu me  Lembrarei
Deixe a liberdade soar de verdade.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('acdec561-32c4-4761-8155-d4f252355b45', 11, 3, 'O Livro Vermelho de Jogadores Anônimos nos diz que  “a palavra espiritual pode ser usada para  descrever as características da mente humana que representam as melhores e mais elevadas qualidades, tais como gentileza, generosidade, honestidade e humildade. Da mesma forma, a Irmandade de Jogadores Anônimos nos pede que consideremos esses princípios como um modo de vida, sendo ela própria uma  Irmandade espiritual”. Comecei a entender que minha espiritualidade tem a ver com a minha realização como um ser humano completo – a congruência  saudável das verdades, como eu as percebo, e meu ser interior.

Continuo a procurar por qualidades que me trarão uma grande felicidade duradoura?

Hoje Eu Rezo
Que eu continue a procurar para mim as  “melhores e mais elevadas qualidades” que definem o meu ser espiritual. Que eu conheça as alegrias que surgem da vivência do modo de vida de JA, até que toda a minha vida se torne uma celebração compartilhada, principalmente com aqueles que, como eu, estão tentando viver com base nesses princípios inspirados por Deus.

Hoje Eu Me Lembrarei
Dos vazios espirituais à espiritualidade completa.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('6a7ccb9e-c38c-4d50-ad01-9ef9d10b9af0', 12, 16, 'Algumas vezes, durante as épocas ruins, que todos temos de vez em quando, até parece que Deus não deseja que sejamos felizes neste mundo; e para os que acreditam em vida após a morte, que Ele  espera que tenhamos dor e sofrimento nesta vida como pagamento pela felicidade na outra. O Programa de Jogadores Anônimos me ensina que é justamente o contrário. Deus quer que eu seja feliz bem aqui neste mundo – neste exato momento Se eu permitir, Deus até me indicará o caminho.

Algumas vezes  recuso-me teimosamente a olhar para onde Deus está apontando?

Hoje Eu Rezo
Rezo para que eu não faça o jogo do eterno sofredor, arrastando-me pelos caminhos da tragédia e agindo como se o sofrimento fosse a única forma de ingressar no paraíso. Que eu olhe à minha volta, e veja o que há de bom e de verde na terra, testemunho suficiente de que nossa vida aqui é feita para ser mais do que simplesmente uma armadilha após outra. Que nenhuma concepção errada de Deus, vendo-o como o mestre das armadilhas, esperando atrás cada arbusto para nos agarrar, distorça minha relação com um Poder Superior  magnânimo e pleno de amor.

Hoje Eu Me Lembrarei
A vida nos oferece mais do que apenas sofrimento.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('cda2a0a9-0c35-445d-9e19-495374cebacb', 7, 21, 'Quando nos deparamos com uma situação ou condição da qual não gostamos, como podemos manter a crença de que as coisas estão se encaminhando para o melhor? Talvez devêssemos nos perguntar o que é fé. A fé tem a sua fundação na verdade e no amor. Podemos ter fé, se assim o desejarmos, não importa a situação. E, se escolhemos ter fé, poderemos esperar que o derradeiro bem aconteça.

Já fiz a minha escolha?

Hoje Eu rezo
Que eu seja agradecido por ter recebido a capacidade de fazer escolhas. Desta gratidão e do meu sentimento de estar perto de Deus, eu escolhi a fé. Que esta fé, da maneira que eu a escolhi, se torne forte o suficiente para remover montanhas, forte o suficiente para me manter livre de minha compulsão do jogo, poderosa o suficiente para segurar a onda das tentações que me ameaçam, otimista o suficiente para olhar além de minha dor atual para a  felicidade final.

Hoje Eu Me Lembrarei
Com fé, nada é impossível.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('f0627fad-5423-4332-a077-faf2762ca8f5', 4, 10, 'A mudança é a característica de todo crescimento, da atitude impetuosa àquela bem pensada, da desonestidade à honestidade, do conflito à serenidade, da dependência infantil à responsabilidade adulta – tudo isso e infinitamente mais, representa uma mudança para melhor. Somente Deus é imutável; somente Deus tem toda a verdade possível.

Aceito a noção de que a falta de poder foi o meu dilema? Encontrei o poder pelo qual posso viver? – um Poder superior a mim mesmo?

Hoje Eu Rezo
Eu rezo para que o Programa de jogadores Anônimos seja para mim um modelo de mudança – para minha mudança. Estes dias de transição do jogo compulsivo para a abstinência, da fraqueza ao poder através de Deus, podem  ser difíceis, como as mudanças podem ser. Que a minha inconstância adquira uma natureza sólida através da natureza imutável de Deus, na qual deposito minha fé. Somente Deus é completo, perfeito e previsível.

Hoje Eu Me Lembrarei
Posso depender do meu Poder Superior.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('476b3ca4-4d5b-402e-9be6-493c731cb667', 6, 4, 'A aceitação é a resposta. Com demasiada freqüência fui incapaz de aceitar quem eu era e, dessa forma, era impossível aceitar os outros. Antes de vir para Jogadores Anônimos, escondia-me da verdade por temê-la. Agora, com a ajuda de meus irmãos de JA posso superar isso. Hoje realmente encontro conforto em conhecer a verdade. Estou tentando seguir na direção certa – e isso é o bastante para mim.

Aceito quem eu era, com todos os meus defeitos, e quem estou tentando me tornar?

Hoje Eu Rezo
Deus, concedei-me serenidade para aceitar as coisas que não posso modificar – o passado com seus sofrimentos e  inquietudes e o fardo que restou de meus dias de jogo. Que minha aceitação das coisas passadas sirva de base para a construção de uma nova vida – uma vida que eu não somente aceite, mas que também celebre.

Hoje Eu Me Lembrarei
A aceitação é a chave da recuperação.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('3fbc6e76-8076-4c79-b836-803d2d6d19a6', 4, 12, 'Se tentarmos entender, ao invés de sermos compreendidos, podemos mais facilmente assegurar a um novo membro que não temos qualquer desejo de convencer alguém, de que há somente uma maneira possível para adquirir a fé. Todos nós, não importa a raça, gênero, credo, cor ou herança genética, somos filhos de um Criador presente, com o qual podemos ter uma forma de relação baseada em termos simples e de fácil entendimento – no momento em que demonstrarmos essa disposição e formos  honestos o bastante para tentar.

Sei  a diferença entre solidariedade e empatia? Sou capaz de me colocar no lugar do recém-chegado?

Hoje Eu Rezo
Que eu tente amar toda a humanidade como filhos de um Deus presente. Que eu respeite  as diferentes formas pelas quais eles encontram e adoram um  Deus. Que eu nunca seja tão rígido a ponto de  desconsiderar os caminhos pelos quais outras pessoas chegam a Deus ou tão insensível que  use os membros do grupo como um bandeira para defender  minhas crenças religiosas como sendo a única forma possível. Só posso conhecer o que funciona para mim.

Hoje Eu Me Lembrarei
Somos todos filhos de Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('639cbae6-5b5c-4de3-935c-986b198126b0', 5, 27, 'Quando tenho somente a mim mesmo para conversar, a conversa tem uma única visão. Tentar me convencer a não fazer uma  “pequena aposta” ou somente jogar “uma  rodada” é como tentar a auto-hipnose. Isso simplesmente não funciona; a maioria das vezes isso é tão ineficiente como tentar convencer a mim mesmo a não pegar uma gripe. Quando meu coração está pesado e minha resistência está baixa, sempre poderei encontrar algum consolo compartilhando isso com um amigo compreensivo e verdadeiro no Programa de Jogadores anônimos.

Sei quem são meus amigos?

Hoje Eu Rezo
Que eu me convença de que, como parte do plano mestre de Deus, fomos colocados aqui para ajudarmos uns aos outros. Que eu esteja tão disposto a pedir ajuda quanto a dá-la, independente de quanto tempo esteja no Programa. Que as experiências de muitos outros  membros seja suficiente para provar a mim mesmo que “tentar me convencer a não fazer a aposta” raramente funciona;   enquanto por outro lado, a troca de idéias que surge do ato de compartilhar com um amigo, geralmente o faz.

Hoje Eu Me Lembrarei
Quando peço ajuda, estou ajudando.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('45c454e7-0783-4c8c-8ac6-cfa25f4cae45', 9, 15, 'Ninguém recebe a dor de braços abertos, mas ela tem sua utilidade. Assim como a dor serve de aviso de que podemos estar sofrendo de alguma doença física, a dor emocional pode ser um sinal útil de que alguma coisa está errada – servindo, também, para nos alertar de que precisamos fazer mudanças. Quando pudermos sentir dor sem pânico, poderemos aprender a  lidar com a sua causa, ao invés de continuarmos fugindo.

Enfrento os desconfortos emocionais? Sou menos frágil do que antes acreditava ser?

Hoje Eu Rezo
Rezo para que eu tenha maior capacidade de enfrentar o sofrimento ou a dor, agora que estou começando a conhecer a realidade – boa e ruim. Oro com sinceridade, para que minha tendência a ser hipersensível desapareça, e que as pessoas não se sintam como se precisassem tratar-me como um sopro de vidro, que se despedaçaria com uma pequena crítica.

Hoje Eu Me Lembrarei
Jogar fora o rótulo: Frágil – Manuseie com Cuidado.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('c1d60f0a-1a50-42a6-8089-a178dc560d5e', 3, 19, 'Fazer minha última aposta nunca mais será para mim apenas alguns minutos gastos e umas moedas que deixo no jogo. Em lugar da primeira aposta eu colocarei esse dinheiro em minha conta bancária, minha família, nossa casa, meu carro, meu trabalho, minha sanidade e provavelmente minha vida. O preço é alto demais, e o risco é imenso.

Você se lembra de sua última aposta?

Hoje Eu Rezo
Que eu tenha a certeza de que o espírito de Deus está comigo em todos os momentos. Que eu aprenda a sentir Sua presença espiritual. Que eu saiba que nada se esconde de Deus. Ao contrário  do mundo, que aprova ou desaprova meu comportamento desregrado, Deus vê tudo o que eu faço, penso ou sinto. Se eu buscar a Sua vontade, sempre poderei contar com uma recompensa para mim – paz de espírito.

Hoje Eu Me Lembrarei
Deus sabe de tudo.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('d3433d4f-dc84-4941-bb48-da5e254b0279', 4, 18, 'Em Jogadores anônimos conhecemos muito bem a inutilidade de tentar superar nossa adicção somente através da força de vontade. Ao mesmo tempo, realmente aprendemos que é necessário muita disposição para adotar os Doze Passos do Programa como uma forma de vida, pois isso pode nos trazer de volta um modo normal de pensar e viver. Não importa a gravidade de nossa adicção, descobrimos com alívio que ainda podemos fazer escolhas. Por exemplo, podemos optar por admitir que somos impotentes perante o jogo compulsivo; e que a dependência a um Poder Superior é uma necessidade, mesmo que  seja simplesmente a dependência ao nosso grupo dentro do Programa.

Optei por tentar uma vida de honestidade e humildade, servindo com desprendimento a meus companheiros e a Deus como eu o concebo?

Hoje Eu Rezo
Deus, concedei-me sabedoria para perceber a diferença entre “força de vontade” (que antes me faltou) e “disposição” de buscar ajuda para o meu jogo, através de Deus e  de outras pessoas que também estão se recuperando. Que eu saiba que há alternativas para mim, assim como para meus companheiros sofredores, que ainda sofrem nos estágios mais sombrios da adicção. Que eu escolha a forma de vida que Deus deseja para mim.

Hoje Eu Me Lembrarei
Disposição, mais do que força de vontade, é a resposta para a recuperação.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('355b39f3-c005-482e-a50f-d8644216b87c', 4, 2, 'O que causa a recaída? O que acontece a uma pessoa que aparentemente parece compreender e seguir o programa dos Doze Passos e, ainda assim, decide jogar novamente? O que posso fazer para impedir que isto aconteça comigo? Existe algum padrão para aqueles que recaem, alguns denominadores comuns que parecem se encaixar? Cada um de nós pode tirar nossa própria conclusão, mas aprendemos no Programa de Jogadores Anônimos que a falta de atitudes também causarão uma eventual recaída.

Quando uma pessoa que recaiu é bastante afortunada para voltar ao programa, ouço bem atentamente o que ela diz  sobre sua recaída?

Hoje Eu Rezo
Que meu Poder Superior me mostre se estou me encaminhando novamente para o jogo. Que eu me ilumine com as experiências dos outros, descobrindo que as razões para essa quebra de promessa ou acidente, surgem com maior freqüência do que não fiz do que fiz. Que eu continue a “voltar” às reuniões.

Hoje Eu Me Lembrarei
Continuar voltando às reuniões.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('c7e43fc3-dbe7-4ab5-9abc-c765fc67ffa4', 1, 27, 'Somente através da dependência de um Poder maior, o qual é grande e bom, bem acima de qualquer coisa que eu possa imaginar ou entender, poderei alcançar a verdadeira dignidade, importância e individualidade. Farei o maior esforço possível para recorrer a esse Poder, ao tomar todas as minhas decisões. Embora minha mente humana não consiga prever qual será o resultado, tentarei ficar confiante  de que isto será o melhor para mim.

Só por hoje, tentarei viver somente este dia sem tentar resolver todos os problemas de minha vida de uma só vez?

Hoje eu Rezo
Que eu não tome qualquer decisão, ou projete qualquer mudança ao longo de minha vida, sem recorrer ao meu Poder Superior. Que eu tenha fé que o plano de Deus para mim é melhor do que qualquer esquema que eu possa armar para mim mesmo.

Hoje Eu Me Lembrarei
Deus é o arquiteto. Eu sou o construtor.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('165ef547-d337-4edf-80f5-81036edd0d69', 7, 13, 'Atualmente, eu vou aos encontros de Jogadores Anônimos para ouvir as similaridades entre mim e meus amigos – não as diferenças. E quando procuro semelhanças, é incrível a quantidade delas, principalmente no que diz respeito a sentimentos. Hoje eu vou às reuniões pensando que eu estou aqui não por causa da adicção de alguém, mas por minha própria adicção, e mais importante ainda, pelo que minha adicção fez ao meu espírito e corpo. Estou aqui por que não tenho outra maneira de escapar da minha adicção sozinho. Eu preciso do Programa de JA e do meu Poder Superior.

Estou ficando menos duro no meu julgamento sobre as outras pessoas?

Hoje Eu Rezo
Que eu esteja sempre alerta enquanto escuto, mais uma vez, o João, o Gil, o Frederico, o Samuel ou a Marta dar o seu depoimento de sofrimentos. Que eu aceite, quando escuto tão atentamente quanto possível estes depoimentos, que cada um tem alguma coisa a oferecer e a somar a história de minha própria vida. Que eu seja influenciado mais uma vez pela semelhança. Que cada semelhança nos faça ficar cada vez mais próximos das necessidades uns dos outros.

Hoje Eu Me Lembrarei
Existe força na semelhança.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('9f7a5828-5dc7-48e3-90ee-9c49b340d972', 12, 4, 'A maioria de nós em Jogadores Anônimos se sente mais à vontade com a determinação de que não faremos a primeira aposta hoje, do que com o “juramento” de que nunca mais jogarei. Dizer – Não pretendo jogar mais – é bem diferente do que  dizer – Nunca mais jogarei. A segunda afirmação é uma reflexão exagerada da vontade própria; pois ela não deixa muito espaço para a noção  de que nossa obsessão ao jogo será removida,  se praticarmos os Doze Passos de Recuperação de JA,  um dia de cada vez.

Continuarei a lutar contra a complacência, vendo que sempre estarei somente a uma aposta do desastre?

Hoje Eu Rezo
“Nunca mais” exige  um compromisso pesado demais, mesmo para os mais fortes de nós. Nossa vida passada era cheia de promessas de “nunca mais” e “jamais farei” que não eram quebradas antes do próximo amanhecer. Que no momento eu aprenda a fixar minhas metas em somente um dia de abstinência de cada vez.

Hoje Eu Me Lembrarei
Nunca dizer “nunca mais”.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('ee4a36a1-a67a-46f8-9e81-403bc2425438', 10, 4, 'A aceitação de que somos impotentes perante o jogo nos trouxe ao Programa de Jogadores Anônimos, onde, através da rendição incondicional, aprendemos que há vitória na derrota. Após algum tempo aprendemos, no trabalho do Passo Doze, que não somos somente impotentes perante a nossa própria dependência, mas também em relação à dependência dos outros. Não podemos fazer com que outra pessoa queira a abstinência do jogo, por exemplo, da mesma forma que não podemos impedir o por do sol. Podemos administrar as necessidades emocionais ou fisiológicas de outras pessoas; podemos compartilhar os seus problemas;  chorar com elas; e levá-las às reuniões. Mas não podemos entrar em sua mente e apertar um tipo qualquer de botão mágico que fará com que ele – ou ela – dê o passo mais importante – o Primeiro Passo.

Algumas vezes ainda tento fazer o papel de Deus?

Hoje Eu Rezo
Que eu entenda a minha necessidade verdadeiramente humana de querer ser o chefe, ter a mão poderosa, e ser a autoridade derradeira – mesmo nesse negócio humilde que é minha própria dependência. Que eu perceba como seria fácil tornar-me o maioral em relação ao modo de fazer o Passo Doze. Que eu também perceba, que não importa o quanto eu queira ajudar, não tenho qualquer controle sobre a dependência de outra pessoa – da mesma forma que ninguém tem controle sobre a minha.

Hoje Eu Me Lembrarei
Não posso gerenciar a recuperação de outra pessoa.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('1197913b-fa52-4b54-8f29-b2cdef4eff83', 6, 1, 'Devagar, mas verdadeiramente, estou me tornando capaz de aceitar tantos os erros quantos as virtudes dos outros. O Programa de Jogadores Anônimos está me ensinando a sempre “amar o que há de melhor nas pessoas – e nunca temer o que há de pior nelas.” Esta não é de forma alguma uma transição fácil em função de minha antiga maneira de pensar, mas estou começando a ver que todas as pessoas – incluindo a mim mesmo – estão de alguma forma tão emocionalmente doentes quanto freqüentemente errados.

Estou começando a ver o que o amor verdadeiro realmente significa?

Hoje Eu Rezo
Que Deus me conceda a tolerância com todas as fraquezas, sinais de doença ou falta de sensibilidade dos outros, para que eu possa amar o que seja bom neles. Que Deus me ensine o verdadeiro significado do amor – que também precisa incluir a paciência. Que eu não ignore as faltas de quem amo, mas tente entendê-las.

Hoje Eu Me Lembrarei
Amor é compreensão.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('f157255f-2beb-4458-82cc-cdefb320461e', 2, 1, 'Quanto mais tempo estou em Jogadores Anônimos, cada vez mais claramente vejo porque é importante para mim entender por que faço o que faço e digo o que digo. Estou  começando a perceber, nesse processo, que tipo de pessoa sou realmente. Vejo agora, por exemplo, como é muito mais fácil ser honesto com os outros do que comigo mesmo. Também estou aprendendo que todos somos prejudicados pela necessidade de justificar nossas ações e palavras.

Fiz um inventário de mim mesmo, conforme é sugerido nos Doze Passos? Admiti meus erros  a mim mesmo e a outro ser humano?

Hoje Eu Rezo
Que eu não fique paralisado, em meu processo de recuperação, pela grandiosidade do Quarto Passo – fazer um inventário moral e financeiro de mim mesmo, ou admitir meus erros para mim mesmo e para outro ser humano. Que eu saiba  que a honestidade para comigo mesmo é muito importante.

Hoje Eu Me Lembrarei
Não conseguirei melhorar se distorcer a verdade.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('9a47504f-ef2b-4dc2-86bf-f47a7ff137e9', 12, 23, 'Como posso saber se já comecei a ter consciência de minha espiritualidade? Algumas vezes a descoberta de que somos seres espirituais manifesta-se, não de forma complicada, mas através de simples sinais: maturidade emocional;  fim dos ressentimentos constantes que corroíam a alma; capacidade de amar e de ser amado de volta; e a crença, mesmo sem compreendê-la, de que um Poder acima de nós controla o nascer e o por do sol, dá e acaba com a vida; e alegria aos corações humanos.

Tenho conhecimento agora, à medida que pratico os princípios de Jogadores Anônimos em todas as minhas atividades, que minha perspectiva de vida é mais ampla e que minha visão da humanidade é mais benévola do que durante a minha miséria egocentrada de meus dias de jogo?

Hoje Eu Rezo
Que minha confiança espiritual comece a levar minhas atitudes a outros, principalmente durante os dias de festas, quando as antecipações e ansiedades estão exacerbadas. Como jogador compulsivo não lidava bem com minhas emoções durante esses períodos. Rezo por serenidade para lidar com o emaranhado de emoções que afloram nesses momentos de festividade.

Hoje Eu Me Lembrarei
As perspectivas surgem através da prática dos princípios.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('07084062-d6cc-410e-80e1-4b17a01b9277', 2, 22, 'Quando entrei para Jogadores Anônimos, encontrei pessoas que sabiam exatamente o que eu queria dizer quando finalmente falei dos meus medos. Eles haviam estado onde eu também tinha estado, eles compreendiam. Desde então, aprendi que muitos de meus medos tinham a ver com a projeção. É normal, por exemplo, sentir-se incomodado por ter um pouquinho de medo  de que a pessoa que eu ame me abandone. Mas, quando esse medo surge antes do relacionamento atual com a pessoa que  estou temendo perder, então isto se torna um problema sério. Minha responsabilidade para comigo mesmo inclui isto: não preciso ter medo do que não existe.

Estou mudando de uma pessoa temerosa para uma pessoa destemida?

Hoje Eu Rezo
Peço a Deus que me ajude a livrar-me de meus medos – aquelas invenções, fantasias, pensamentos monstruosos, projeções de desastres que não têm fundamento no presente. Que eu dê sentido à minha imaginação e me concentre no aqui e agora, porque tenho tendência a ver o futuro com lentes de aumento.

Hoje Eu Me Lembrarei
Medos projetados, assim como as sombras, são maiores do que a vida.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('9d9ee379-02bf-405d-9134-bf30135b4132', 11, 14, 'Primeiro adquira um pouco de humildade, insistiu meu padrinho. Ele falou que se eu não fizesse isso aumentaria enormemente o risco de voltar ao vício mais uma vez. Após algum tempo, apesar da rebeldia característica de toda minha vida, segui o seu conselho; comecei a tentar a praticar a humildade, simplesmente porque eu acreditei ser aquela a coisa certa a fazer. Espero sinceramente que haverá um dia em que a maior parte de minha rebeldia ficará somente na lembrança; e que eu estarei praticando a humildade porque a quero sinceramente como um modo de vida.

Estou disposto a tentar a humildade hoje, mesmo que só por um instante? Aprenderei a desejar ardentemente a sensação que conseguirei através dela?

Hoje Eu Rezo
Já que sou um rebelde, como tantos outros jogadores compulsivos, que eu aprenda que precisarei praticar a humildade. Que eu reconheça que a humildade não surge facilmente para alguém de natureza rebelde, não importa se sou completamente arrogante, negativo,  ou mais sutilmente, se estou determinado a mudar tudo a minha volta de maneira drástica,  exceto a mim mesmo. Oro para que com a prática da humildade isso se torne algo instintivo para mim.

Hoje Eu Me Lembrarei
Adquira o hábito da humildade.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('36450365-f649-4782-9aeb-a2e26fd8ac02', 1, 20, 'Entendo que, quando me permito concentrar no problema, o problema piora.  Mas, se me dedico à solução, a situação melhora. Meus problemas financeiros hoje têm muito menor importância quando me concentro em minha recuperação e na abstinência contínua, ao invés de ficar gastando meu tempo desejando que não tivesse dívidas. Minhas grandes ilusões passadas de uma situação sem dívidas são hoje apenas ilusões. Minha felicidade de hoje depende de minha aceitação dos fardos financeiros que eu mesmo criei – e dos planos que tenho para quitá-los de alguma maneira razoável.

Entendo que minha paciência nestes assuntos é um importante fator para a minha recuperação?

Hoje Eu Rezo
Que eu me lembre de que, durante um longo período de tempo, minha vida deu uma reviravolta para pior. Minha recuperação será um processo similar ao contrário, dando muitas reviravoltas para melhor – durante um longo período de tempo.

Hoje Eu Me Lembrarei
Minhas fraquezas se perpetuarão somente se eu permitir que elas permaneçam.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('3578d607-5baa-4a59-a4e1-a9a3a0884a62', 10, 23, 'Um renomado filósofo uma vez escreveu que “a própria pessoa se esconde muito bem de si mesma. Dentre todas as minas de tesouro, a mina da própria pessoa é a última a ser cavada.” Os Doze Passos de Recuperação de Jogadores Anônimos me deram condição de revelar o “meu próprio ser”, esse ser que ficou enterrado pela minha necessidade desesperada de ter a aprovação dos outros. Graças ao Programa de Jogadores Anônimos e de meu Poder Superior, comecei a adquirir uma noção verdadeira do meu ser e uma  noção serena de confiança. Não mais preciso agir como um camaleão, mudando minha cor de uma hora para outra, tentando sem resultado ser todas as coisas para todas as pessoas.

Esforço-me o tempo todo para ser verdadeiro comigo mesmo?

Hoje Eu Rezo
Oro para ser honesto comigo mesmo e para que eu continue – com a ajuda de Deus e de meus amigos – a tentar conhecer o meu verdadeiro eu. Que eu saiba que não posso de repente me transformar em uma pessoa centrada, completamente definida, com uma personalidade totalmente consistente; levará algum tempo para que a minha personalidade se desenvolva dessa forma, e para que eu trabalhe meus valores e minhas prioridades. Que eu saiba agora que estou no caminho certo para me transformar na pessoa que quero ser.

Hoje Eu Me Lembrarei
Estou me transformando em quem desejo ser.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('4503c2f8-a30f-4342-801b-39239657aa23', 5, 25, 'Quando ainda estamos há pouco tempo em Jogadores Anônimos,   não adquirimos experiência na tarefa  de  procurar fazer amigos – ou até mesmo aceitar  amizade quando é ela nos é oferecida. Algumas vezes não estamos bem certos de como fazê-lo ou, de fato, se isso funcionará de verdade. Gradualmente, entretanto, vamos nos recuperando, tornando-nos abertos aos ensinamentos. Aprendemos, por exemplo, o que Molière escreveu, que  “Quanto mais amamos nossos amigos, menos os adulamos.”

Só por hoje, estenderei a mão se precisar de um amigo?

Hoje Eu Rezo
Que Deus nos ajude a descobrir o significado da verdadeira amizade. Rezo para que eu não me torne tão ansioso por aprovação em meus relacionamentos; permitindo a mim mesmo ser desonesto – através da adulação, meias verdades, falso entusiasmo, mentiras brancas protecionistas.

Hoje Eu Me Lembrarei
Quem é amigo,  é honesto.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('d920cf96-aa95-49a8-8960-b479b1bd421e', 6, 22, 'No momento em que refletimos sobre o fim ou enfraquecimento de um relacionamento com outra pessoa, nossas emoções partem para a defensiva. Com o intuito de evitar encarar nossos erros, fazemos outro: com ressentimentos nos concentramos no mal que ele ou ela nos causou. Com um espírito de triunfo, usamos o seu menor deslize como a desculpa perfeita para minimizar ou esquecer o nosso. Precisamos nos lembrar de que não somos as únicas pessoas a serem dominadas pelas emoções doentias. Freqüentemente, estamos realmente lidando com companheiros sofredores, incluindo aqueles cujo sofrimento aumentamos.

Se estou a ponto de pedir perdão, porque não deveria também perdoar  a outra pessoa?

Hoje Eu Rezo
Quando culpo alguém ou encontro algum defeito, que meu Poder Superior me oriente a procurar meu próprio sentimento de culpa que cuidadosamente varri para debaixo do tapete. Que eu reconheça essas pistas de comportamento como elas realmente são.

Hoje Eu Me Lembrarei
O ressentimento é o outro lado da culpa.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('dad8d65d-1b22-4829-8e01-bc4e3fa78f06', 9, 5, 'Freqüentemente nos dizem que jogadores compulsivos são perfeccionistas e impacientes com qualquer erro – principalmente os próprios. Tendemos a planejar objetivos impossíveis para nós mesmos, lutando com todas as forças para alcançar nossos ideais inatingíveis. Assim, é claro - já que nenhuma pessoa poderia atender a padrões extremamente altos que exigíamos de nós mesmos - ficávamos frustrados. Conseqüentemente, ocorria o desencorajamento e a depressão; nos puníamos por não sermos sobre-humanos. Então, ao invés de buscar metas mais realistas, sonhávamos ainda mais alto. E caíamos novamente, nos punindo mais duramente ainda.

Chegou a hora de deixar  de projetar objetivos impossíveis para mim e para os que me cercam?

Hoje Eu Rezo
Que Deus reduza a imagem que tenho de mim mesmo como uma super-pessoa. Não devo esperar perfeição de mim mesmo ou de ninguém. Somente Deus é perfeito, e eu sou limitado por ser humano.

Hoje Eu Me Lembrarei
Não sou Deus; sou apenas humano.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('78396ad1-4ef3-4870-9c54-99fc2c0529e8', 5, 20, 'O jogo compulsivo é uma doença solitária. Embora parecesse que fazíamos parte  da multidão de luzes e ação, freqüentemente nos torturávamos pela solidão. Mesmo antes de nossos dias de jogo findarem – antes que as dívidas e a culpa nos mostrassem como nossa vida havia se tornado ingovernável – a maioria de nós sentia que na realidade não era parte daquilo. Ou éramos naturalmente tímidos, ou éramos bons e barulhentos companheiros, buscando a atenção e aprovação, mas raramente conseguindo-as. Embora procurássemos através da aparente sociabilidade do jogo superar nossa solidão, o risco impensado era sempre solitário. No final, até o próprio Jogo nos traía; ficávamos encurralados e abandonados em um isolamento aterrorizante.

Comecei a atingir a paz interior?

Hoje Eu Rezo
Que eu conheça a ternura de um relacionamento íntimo com Deus e a calma que sinto quando alcanço o espírito de Deus. Que eu transfira essa ternura e calma a meus relacionamentos com outras pessoas. Que Deus me liberte de meu eterno sentimento de solidão mostrando-me como ser  amigo.

Hoje Eu Me Lembrarei
Deus pode ensinar-me a ser amigo.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('c024013a-0e51-4927-8e14-b7a3a8bbebff', 1, 26, 'A liberdade pessoal é minha por direito. Não importa quão estreitos sejam os laços de amor e respeito que me liguem à minha família e amigos, sempre precisarei me lembrar de que sou um indivíduo, uma pessoa livre para viver  minha própria vida e ser eu mesmo com serenidade e alegria. A palavra-chave desta realização é pessoal. Assim posso me livrar dos muitos compromissos que julgar desnecessários. Através do Programa de Jogadores Anônimos, estou aprendendo a desenvolver minha própria personalidade.

Estou reforçando minha liberdade pessoal ao permitir que os outros sejam livres para controlar suas ações e seus destinos?

Hoje Eu Rezo
Que eu encontre minha liberdade pessoal, através da reavaliação dos relacionamentos, estabelecendo novas prioridades, conquistando o respeito por minha própria individualidade. Que eu proporcione aos outros o mesmo espaço para que encontrem suas próprias opções de liberdade pessoal.

Hoje Eu Me Lembrarei
Tome a liberdade, ela é sua.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('b7a926e9-050b-46dc-b828-930b401956b0', 2, 4, 'É raro encontrar um jogador compulsivo em recuperação que contestará agora  o fato de que a negação é o principal sintoma da doença. O Programa de Jogadores Anônimos nos mostra que o jogo compulsivo realmente transmite à pessoa afetada que ela, na verdade, não está de forma alguma doente. Não nos surpreende, então, que nossa vida como jogadores compulsivos fosse caracterizada por intermináveis racionalizações e desonestidade e, em suma, por uma eterna má vontade para aceitar o fato de que éramos, sem dúvida alguma, emocionalmente e mentalmente diferentes de outras pessoas.

Admiti com a maior honestidade que sou verdadeiramente impotente perante minha compulsão pelo jogo?

Hoje eu Rezo
Que o Primeiro Passo não seja parcialmente assimilado por mim, mas que seja uma completa admissão de minha impotência perante minha adicção. Que eu me livre deste primeiro sintoma – a negação – que impede que eu reconheça qualquer outro sintoma de minha doença.

Hoje Eu Me Lembrarei
Negue a negação.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('7cae2e14-9fb3-4365-9ede-9d5796ccda62', 10, 1, 'Podemos ficar rodeados de pessoas e ainda assim nos sentirmos solitários. Podemos estar sozinhos e ainda assim nos sentirmos felizes e satisfeitos. O que faz a diferença? Sentimo-nos solitários quando esperamos algo de alguém e essa pessoa não pode nos dar. Ninguém mais pode NOS proporcionar paz de espírito, a visão interna de aceitação e a serenidade. Quando nos encontramos sozinhos, não precisamos nos sentir solitários.  Deus está conosco; a Sua presença é como o calor nos envolvendo. Quanto mais ficamos conscientes de que somos amados por Ele, mais nos tornamos capazes de sentir satisfação e segurança – independente de estarmos ou não na companhia de outras pessoas.

Estou vivenciando a sensação da presença de Deus em todos os momentos e em todos os lugares?

Hoje Eu Rezo
Que eu entenda que cada um de nós tem a sua própria forma de solidão – se somos jovens e não temos amigos, se somos idosos e estamos aguardando a hora da morte, carentes, abandonados, fugindo, ou simplesmente nos sentindo isolados estando em uma multidão. Que a minha solidão diminua um pouco com base no fato de que a solidão é realmente um sentimento universal que todos já  experimentaram – apesar de algumas vidas parecerem mais vazias do que outras. Que eu – e todas as pessoas solitárias – tenhamos conforto na presença de Deus.

Hoje Eu Me Lembrarei
A solidão compartilhada é menos solitária.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('6e752524-1a82-4f1d-9a99-d6c6876bdb0c', 9, 21, 'Já ouvi dizer que quando Deus fecha uma porta, abre uma janela. Desde que comecei a trabalhar os Doze Passos de Recuperação, parte do medo e da dor que ameaçava minha vida desapareceu. Alguns de meus defeitos foram eliminados, embora eu ainda esteja lutando contra os outros. Creio que, se continuar trabalhando os Doze Passos cada vez mais, minha vida continuará a melhorar – física, mental e espiritualmente?

Estou mais disposto a ajudar e melhor preparado para ajudar outras pessoas a trabalhar os Doze Passos?

Hoje Eu Rezo
Sou agradecido a Deus por me mostrar que os Doze Passos de Recuperação de Jogadores Anônimos são o caminho para uma vida mais saudável. À medida que refaço os Doze Passos conscientemente, minha vida realmente melhora, tornando-se mais saudável e mais próxima do Poder Superior. Se continuar a viver assim, que eu continue a sentir a mesma gratidão e exaltação do espírito como aqueles que estão descobrindo agora os Doze Passos.

Hoje Eu Me Lembrarei
Passo a Passo, dia a dia.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('0b97171b-dffb-43a7-a99e-552c0f238169', 11, 28, 'Nossa fé no poder de Deus –  atuando em nós e em nossas vidas – não nos livra das responsabilidades. Ao invés disso, ela fortalece nossos esforços, nos torna confiantes e seguros e nos capacita a agir com convicção e sabedoria. Não temos mais medo de tomar decisões; não temos mais medo de dar os passos necessários para agirmos de maneira acertada em determinadas situações.

Acredito que Deus está em ação acima de meus esforços humanos  e que minha fé e crença Nele trarão  resultados muito superiores às minhas expectativas?

Hoje Eu Rezo
Que minha fé em meu Poder Superior nunca me falte. Que a fé nesse Poder continue a servir de esteio para meu otimismo, minha confiança e minha fé em meu próprio poder de decisão. Que eu nunca feche meus olhos às maravilhas do trabalho de Deus ou desmereça a sabedoria de Suas soluções.

Hoje Eu Me Lembrarei
Nossa esperança do passado, ajuda-nos nos anos que estarão por vir.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('41fe8766-cc4c-4662-9369-64c4800905a8', 11, 9, 'À medida que o tempo passa, a comunhão diária com Deus vai se tornando tão essencial para mim quanto a minha respiração. Não necessito um lugar especial para orar, porque Deus sempre escuta o meu chamado. Não preciso de palavras especiais para as minhas orações, porque Deus já conhece meus pensamentos e minhas necessidades. Somente preciso direcionar minha atenção a Deus, porque sei que Sua atenção está sempre direcionada a mim.

Sei que somente boas coisas poderão acontecer a mim se eu confiar completamente em Deus?

Hoje Eu Rezo
Que minha comunhão com Deus se torne uma parte regular de minha vida, tão natural, quanto os batimentos de meu coração. Que eu descubra, à medida que me acostumo com o hábito de rezar, que não é tão importante descobrir um canto na sala, o lado de uma cama, um banco de igreja, ou mesmo uma hora especial do dia para a oração. Que meus pensamentos freqüentemente se voltem automaticamente para Deus, todas as vezes em que houver um intervalo em meu dia ou uma necessidade de direcionamento.

Hoje Eu Me Lembrarei
Deixar a oração se transformar em hábito.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('182f4d26-b3bb-490a-92e5-adfd74835ecd', 1, 25, 'Mesmo com um crescente entendimento do Programa de Jogadores Anônimos e seus Doze Passos, às vezes achamos difícil acreditar que nosso novo modo de vida leve à liberdade pessoal. Suponhamos, por exemplo, que eu me sinta aprisionado a um trabalho desagradável ou a um relacionamento pessoal conturbado. O que estou fazendo com relação a isso? No passado, minha reação imediata era tentar manipular as coisas e as pessoas ao meu redor para que fossem mais aceitáveis para mim. Hoje percebo que não se pode conseguir a felicidade deste jeito.

Estou aprendendo que só se consegue o alívio do desespero e da frustração através das mudanças que eu mesmo fizer nas atitudes que perpetuam as condições que causam o meu sofrimento?

Hoje Eu Rezo
Que eu tenha os olhos límpidos para enxergar – e então me conter – quando estiver manipulando as vidas das pessoas à minha volta, meus colegas de trabalho do dia a dia, meus amigos e minha família.  Que eu sempre tenha consciência de que a mudança precisa começar dentro de mim mesmo.

Hoje Eu Me Lembrarei
Mude de dentro para fora.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('64bc9e82-1c75-44d0-ad08-1074489da3cb', 12, 1, 'Aristóteles escreveu que “Somos o que fazemos com freqüência; assim, a excelência não é uma ação, mas um hábito”. Ao repetir minhas ações de jogo, tornei-me progressivamente mais propenso a fazê-las repetidamente. Assistindo as reuniões com assiduidade, conversando com meu Poder Superior, compartilhando meus sentimentos e mantendo-me ligado aos membros de  Jogadores Anônimos entre as reuniões, tornei-me parte do que estou fazendo: parte da Irmandade de JA. Se continuar a repetir essas ações, tornar-me-ei progressivamente mais propenso a fazê-las repetidamente.

Reconheço que qualquer pequena contribuição que fizer em uma reunião me dará um retorno muito maior como investimento do que qualquer aposta que tenha feito em minha vida, mesmo a de  maior ganho?

Hoje Eu Rezo
Que as ações que eu repita hoje estejam de acordo com a vontade de meu Poder Superior. Que eu entenda que, embora a perfeição não seja o meu objetivo, o progresso é possível, e a excelência no que estou fazendo para minha recuperação é viável. Ajude-me a livrar-me de meus hábitos de  auto-destruição substituindo-os por outros que ajudarão em minha recuperação..

Hoje Eu Me Lembrarei
A recuperação não é uma ação, é um hábito.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('81fac3cb-65b7-4b6e-92b1-61999ca05eb9', 10, 22, 'La Rochefoucauld escreveu que “nem todos aqueles que conhecem sua mentes também conhecem seus corações.’ O programa de Jogadores Anônimos tem um valor inestimável para  nós  jogadores compulsivos, que queremos nos conhecer, sendo corajosos o bastante para buscar o crescimento através da auto-avaliação e melhora pessoal. Se continuar honesto, com a mente aberta e com boa vontade, o Programa de JA me capacitará a livrar-me de minhas atitudes auto-enganadoras e defeitos de caráter que por tanto tempo me impediram de me transformar no tipo de pessoa que quero ser.

Tento ajudar os outros a entenderem os Doze Passos de Recuperação de JA? Transmito a mensagem através do exemplo.

Hoje Eu Rezo
Peço a benção de Deus para a Irmandade de Jogadores Anônimos, que me mostrou tão bem como no passado eu não estava disposto a encarar as coisas sozinho. Que eu tenha a coragem de enfrentar e ser enfrentado, não somente para ser honesto em prol da própria honestidade – o que já seria razão suficiente – mas para possibilitar o desenvolvimento do meu próprio auto-conhecimento e para o crescimento de outras pessoas no grupo

Hoje Eu Me Lembrarei
Somos os espelhos uns dos outros.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('7d000162-6611-467d-b8c1-903edfe1c690', 2, 23, 'Os Doze Passos nos ensinam que, assim como cresce a fé, também cresce a confiança. O medo aterrorizador do nada começa a se dissipar. À medida  que seguimos o Programa, percebemos que o principal antídoto para o medo é o despertar espiritual. Perdemos o medo de tomar decisões porque percebemos que, se nossa opção for errada, poderemos aprender com a experiência. Se tomarmos a decisão certa, poderemos agradecer a Deus por nos ter dado a coragem e a graça, que resultaram em nossa atitude.

Estou grato pela coragem e pela graça que recebo de meu Poder Superior?

Hoje Eu Rezo
Peço que me seja concedida a capacidade de agir, sabendo que tenho pelo menos a metade das chances de acertar, e que eu possa aprender através dos erros. Durante muito tempo tomar  decisões  parecia estar além de minha capacidade. Agora sou capaz de encontrar prazer no ato de fazer escolhas. Agradeço a Deus pela coragem.

Hoje Eu Me Lembrarei
Liberdade é  escolher.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('4e8d0ab4-dc28-409b-bc45-1a8b63cc8c0c', 12, 22, '“Não conheço motivo mais encorajador do que a habilidade inquestionável que o homem possui de melhorar a sua própria vida através do esforço consciente,” disse David Thoreau. Sempre tivemos o poder de mudar nossas vidas e a nós mesmos, mas antes de chegarmos a Jogadores Anônimos, não conseguíamos progredir muito na busca de objetivos realistas. Agora que encontramos o Programa de JA, não só temos a capacidade, mas também um plano – uma série de esquemas que funcionam.

Acredito que ninguém chega cedo demais ao Programa de JA, da mesma forma que ninguém retorna tarde demais? Acredito que nenhum crescimento me parecerá inatingível se eu tiver meu Poder Superior como guia, e o estímulo de meus companheiros de JA?

Hoje Eu Rezo
Não posso me esquecer de que embora minhas intenções de melhorar minha vida e a mim mesmo tenham sido verdadeiras, minha capacidade de realização sem um plano realista estava destinada ao fracasso. Que eu seja grato por ter encontrado este Programa e pela existência do milagre de JA em minha vida. Que eu honestamente me lembre de como era minha vida anteriormente e comemore, com meus irmãos e irmãs desta grande Irmandade, a minha vida como ela é  hoje e como poderá  ser.

Hoje Eu Me Lembrarei
JA nos dá os caminhos.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('4576b900-4471-4c35-bdb6-fd3d5c82944a', 6, 23, 'A complacência é minha inimiga, fácil de reconhecer nos outros, mas difícil de identificar e aceitar em mim mesmo. Complacência simplesmente significa ter certeza de que temos razão – assumindo que não haveria a menor chance de estarmos errados. Mais ainda, isto significa julgar os outros pelo que consideramos certo. Isso bloqueia a compreensão e a gentileza, e parece justificar, em nós mesmos, as qualidades que consideramos completamente inaceitáveis nos outros.

Tenho tendência a achar que minhas opiniões estão sempre certas?

Hoje eu Rezo
Deus, por favor ajude-me a superar a complacência, esse estado de espírito que nos deixa tolhidos. Quando me sinto satisfeito, não saio à procura de algo. Se achar que sempre estou certo, nunca ficarei atento aos meus erros, que podem continuar a me acompanhar. Mantenha-me aberto aos ensinamentos. Mantenha-me em crescimento, em coração, mente e espírito.

Hoje Eu Me Lembrarei
A auto-complacência impede o crescimento.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('7d03adac-7e8f-4442-a88e-224de5e37315', 7, 15, 'Deparando-nos com a destruição quase total causada por nossa adicção ao jogo, eventualmente só poderíamos passar a ter a mente aberta nos assuntos espirituais. Neste sentido, as inúmeras maneiras que usávamos para jogar e apostar foram muito convincentes pois,  finalmente conseguiram nos torturar a ponto de nos levar a um estado de irracionalidade. Aprendemos, que quando teimosamente fechamos as portas de nossas mentes, deixamos muito mais coisas para fora do que guardamos para nós.

Eu imediatamente rejeito idéias novas? Ou pacientemente procuro mudar o meu velho modo de vida?

Hoje Eu Rezo
Que eu mantenha uma mente aberta, especialmente quando se tratar de assuntos espirituais, lembrando que “espiritual” é uma palavra mais ampla do que “religioso”. (Eu nasci do Espírito, mas me ensinaram Religião). Que eu me lembre de que uma mente fechada é um sintoma da minha adicção e uma mente aberta é essencial para a minha recuperação.

Hoje Eu Me Lembrarei
Se eu deixar de fora mais do que guardo dentro de mim, o que estarei protegendo?', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('9442bb04-a53a-47d7-a39d-62a7126b678b', 4, 11, 'Passei; Passei a; Passei a acreditar. Jogadores Anônimos me deu condições de aprender, que a concepção básica de  Deus, está enraizada em cada homem, mulher ou criança. A mesma pode ficar obscurecida pela pompa, calamidade, ou veneração a outras coisas, mas, de alguma forma ou outra, ela permanece lá. Pois a fé em um Poder Superior a nós mesmos e as demonstrações miraculosas desse Poder nas vidas humanas, são fatos tão antigos quanto o próprio homem.

De que maneira estou compartilhando as dádivas que recebi sem qualquer ônus?

Hoje Eu Rezo
Rezo para que possa continuar a procurar – e encontrar – essa crença  em Deus, que existe em mim e em todas as outras pessoas, independente do quanto esteja obscurecida. Que eu tenha certeza de que a consciência da existência de um Poder Superior sempre esteve presente em cada ser.humano desde o instante em que recebeu pela primeira vez a capacidade de raciocinar, independente de que nome ele tenha dado a isso, ou como procurou alcançá-Lo. Que minha própria fé no Poder Superior seja reforçada pela experiência de toda a humanidade – e pelo trabalho de Deus em Seus misericordiosos milagres em minha própria vida.

Hoje Eu Me Lembrarei
Deus está em todos nós.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('ad16f5f5-8fb6-42ea-9d7b-4805bd962fc4', 9, 13, 'Freqüentemente ouvimos,em nossas reuniões de Jogadores Anônimos, que a dor é o ponto inicial do progresso espiritual. Eventualmente, chegaremos a conclusão de que, assim como a dor do jogo compulsivo precisava ocorrer antes de nossa abstinência, o descontrole emocional vem antes da serenidade. Já  não temos pena de todos que sofrem, mas somente de quem sofre em ignorância – os que não compreendem o propósito e a verdadeira utilidade da dor. Como disse Proust  “À bondade e à sabedoria somente fazemos promessas; à dor, obedecemos.

Acredito que a dor é a maneira que Deus usa para chamar a minha atenção?

Hoje Eu Rezo
Que eu veja o valor da dor em minha vida, principalmente se estiver me encaminhando para a destruição. Que eu saiba que a dor é a maneira que Deus usa para parar o trem em que estou, antes que ele chegue a uma ponte em ruínas. Devo agradecer pelo fato de a dor haver me forçado a desviar o trem para outra linha a tempo.

Hoje Eu Me Lembrarei
A dor salva vidas.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('3eae2dc8-39a6-4271-aa47-80c0ade1bc4c', 2, 16, 'A raiva é justificável? Se alguém nos engana ou age de maneira inaceitável, não temos o direito de ficarmos furiosos? As sofridas experiências de vida de muitos outros membros do Programa de Jogadores Anônimos nos ensinam que os episódios de raiva são realmente extremamente perigosos. Então, mesmo que precisemos reconhecer a raiva a ponto de dizer ‘estou com raiva”, não podemos permitir que esta raiva se acumule, por mais justificável que seja.

Posso aceitar o fato de que, se quero viver, tenho que me livrar da raiva?

Hoje eu Rezo
Mesmo que eu consiga me afastar dos problemas, que eu saiba que sempre haverá certas situações ou pessoas que me aborrecerão. Quando minha raiva não parecer justificável -  existindo motivos justificáveis para ela -  que eu possa contestá-la, comigo mesmo. Que eu reconheça minha raiva,  justificável ou não, antes de enterrá-la viva.

Hoje Eu Me Lembrarei
Não há nada de errado em se sentir raiva.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('688a69f0-86e9-46f2-886a-60206925a5e9', 11, 7, 'No Programa de Jogadores Anônimos há pessoas que, no começo, rejeitaram  a oração e a meditação como se estivessem evitando um poço cheio de cascavéis. Entretanto, quando finalmente fazem a primeira tentativa dando o passo inicial, coisas inesperadas começam a acontecer, e eles começam a se sentir diferentes. Invariavelmente, o começo dessas tentativas leva  a uma verdadeira aceitação, a tal ponto que aqueles que no passado menosprezaram a oração e a meditação, geralmente acabam se tornando os maiores divulgadores de seus benefícios. No Programa de JA ouvimos que “só rejeitam a oração aqueles que na verdade nunca recorreram a ela.”

Há dentro de mim uma parte obstinada  que ainda nega?

Hoje Eu Rezo
Que eu aprenda, que apesar de minha irreverência, não se deve menosprezar a oração; vejo o poder da oração realizando milagres à minha volta, e fico maravilhado com isso. Se tenho me recusado a rezar, que eu verifique se o orgulho está se interpondo em meu caminho – aquele antigo orgulho que insiste em fazer as coisas à sua maneira. Agora que descobri um lugar para a oração em minha vida, que eu reserve um lugar para ela – religiosamente.

Hoje Eu Me Lembrarei
Quem aprende a orar continua a orar.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('af857ddc-b130-4e5c-ab39-b4bf6015dab3', 2, 6, 'Eu costumava ser um especialista na auto-avaliação fantasiosa. Algumas vezes, olhava apenas para as partes de minha vida que pareciam boas. Assim, exaltava quaisquer virtudes reais ou imaginárias que eu tivesse. Aí então me congratulava pelo grande trabalho que estava fazendo na Irmandade. Naturalmente, isso deu origem a um desejo de maiores “realizações” e ainda maior aprovação. Não era esse o padrão de minha época de adicção ativa? Entretanto, a diferença agora é que eu posso utilizar o melhor álibi do mundo – o álibi espiritual.

Justifico algumas boas ações e comportamento inadequados em nome dos “objetivos espirituais” ?

Hoje eu Rezo
Deus, ajude-me a perceber se ainda procuro  atenção e aprovação a ponto de exagerar minhas próprias virtudes e minhas realizações dentro do Programa ou em qualquer outro lugar. Que eu tenha uma perspectiva realista sobre meus pontos positivos, mesmo neste momento que estou aprendendo a respeitar a mim mesmo.

Hoje Eu Me Lembrarei
Aprenda a controlar o ego inflado.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('7b4299b0-58ae-4ef1-a0a0-d0baf3a50ccf', 6, 16, 'O Nono Passo do Programa de Jogadores Anônimos diz: “Faça reparações diretas às pessoas, sempre que possível, exceto quando isso puder prejudicá-los ou prejudicar outras pessoas.” Fazer as reparações pelo mal causado pode ser no mínimo extremamente difícil. e, quando não é o caso, esvazia os nossos egos e abate o nosso orgulho. Mesmo assim, isso é por si só, uma recompensa, podendo também trazer recompensas ainda maiores. Quando chegamos a alguém e nos desculpamos, a reação é quase que invariavelmente positiva. Certamente é necessário coragem mas, os resultados justificam a atitude.

Dei o melhor de mim para fazer todas as reparações possíveis?

Hoje Eu Rezo
Que eu conte com meu Poder Superior para impedir-me de começar a me arrastar evitando a responsabilidade do Nono Passo. Que eu usufrua da abençoada e libertadora limpeza de alívio que surge quando se fala em voz alta para alguém a quem prejudicamos – eu estava errado, eu cometi erros. Lamento profundamente. Que eu não me preocupe com a destruição da frágil camada  que cobre meu ego, porque o interior ficará mais amadurecido.

Hoje Eu Me Lembrarei
A reparação é abençoada.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('fc4cfc91-c254-4964-a4b1-c0afa88bd55a', 3, 24, 'Todos nós enfrentamos preocupações e problemas da vida diária não importa se estamos em Jogadores Anônimos há dois dias ou vinte anos. Algumas vezes, preferimos acreditar que poderíamos dar conta de nossos problemas de imediato, mas isso raramente acontece assim. Se lembrarmos do lema “Um dia de Cada Vez”, quando estivermos a ponto de entrar em pânico, podemos descobrir que a melhor maneira de lidar com algum problema é ”reverter a situação”. Colocamos um pé à frente do outro, da melhor maneira possível. Dizemos “Um Dia de Cada Vez”, e  agimos – um   dia de cada vez.

Os lemas do Programa estão crescendo comigo, à medida que vou crescendo no Programa?

Hoje Eu Rezo
Que as próprias palavras “Um Dia de Cada Vez” sirvam para diminuir minha ansiedade de realizar coisas demais, rápido demais. Que estas palavras sejam suficientes para me fazer tirar o pé do acelerador,  que me leva a novas situações,  sem refletir o bastante, fazendo com que eu diminua o tempo gasto na busca de realização material. Que eu me atenha ao provérbio de que Roma não foi construída de um dia para outro. Da mesma forma, não posso encontrar soluções imediatas para todos os meus problemas.

Hoje Eu Me Lembrarei
Um Dia de Cada Vez.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('d5a5cdd1-35a5-475f-ba4d-7b91e8a92eb2', 4, 17, 'O Programa de Jogadores Anônimos ensina a me proteger contra a impaciência, surtos de auto-piedade e ressentimentos das palavras e ações dos outros. Embora eu não deva me esquecer nunca de como foi no passado, também não devo me deixar levar em incursões atormentadoras ao passado – meramente em prol de uma morbidade indulgente. Agora que estou atento aos sinais de perigo, sei que estou melhorando a cada dia.

Se surgir uma crise, ou qualquer problema me atormentar, analiso este problema à luz da Oração da Serenidade?

Hoje Eu Rezo
À medida que relembro o passado rezo por uma outra perspectiva. Que eu refreie meus impulsos de  crescer mais  e ser melhor que outros membros de meu grupo, revelando a eles os horrores de minha adicção. Que eu não use mais o passado para justificar minha auto-piedade ou me afundar em minha culpa. Que as memórias daqueles primeiros dias infelizes me sirvam somente como sentinelas, protegendo-me contra situações perigosas ou processos mentais doentios.

Hoje Eu Me Lembrarei
Não posso mudar o passado.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('a3bcee4e-7adf-4666-94d6-3e98e5ef3a27', 8, 6, 'Algumas vezes, através de experiências amargas e lições dolorosas, aprendemos na nossa irmandade de Jogadores Anônimos que o ressentimento é o nosso inimigo número um. Ele destrói mais a gente do que qualquer outra coisa. Do ressentimento nascem todas as formas de doença espiritual, pois não estamos apenas doentes fisicamente ou mentalmente, mas também espiritualmente. À medida que nos recuperamos e nossa doença vai sendo medicada, melhoramos fisicamente e mentalmente.

Estou ciente de que poucas coisas são piores do que meu próprio rancor? Consigo ver que o meu veneno é mais letal para mim mesmo do que para a minha vítima?

Hoje Eu Rezo
Peço ajuda para remover a pilha de ressentimentos que colecionei. Que eu entenda que ressentimentos são atores também: eles podem representar o medo: de perder o emprego, um amor, uma oportunidade; eles podem ser mágoas ou sentimentos de culpa. Que eu saiba que Deus é o meu remédio. Que eu admita minha necessidade.

Hoje, Eu Lembrarei
Ressentimentos são o lixo: livre-se deles.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('3c0b8338-b080-4208-becb-d35888f86c7a', 8, 29, 'A oração pode trazer muitas recompensas. Uma das maiores recompensas que ela me traz  é a noção de estar fazendo parte de alguma coisa. Não mais vivo como um estranho em uma terra estranha e como um extra-terrestre em um mundo completamente hostil. Não me sinto mais perdido, assustado e sem propósito. Eu faço parte de algo. Em Jogadores Anônimos, descobrimos que no momento em que vislumbramos a vontade de Deus – no momento em que começamos a ver a verdade, a justiça e o amor como coisas reais e eternas da vida – não mais nos sentimos profundamente aborrecidos com a aparente evidência do contrário,  comum nas atividades puramente humanas que nos rodeiam.

Acredito que Deus gentilmente toma conta de mim?

Hoje Eu Rezo
Que eu seja grato pelo conforto e tranqüilidade de fazer parte de algo – de Deus o derradeiro e sábio “pai” e de Sua família na terra. Que eu não mais necessite de rótulos chamativos ou grupos animados que me forneçam uma identidade. Através da oração, pertenço a Deus.

Hoje Eu Me Lembrarei
Encontro minha identidade através da oração.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('701e9bb8-6c7e-4af2-858e-71d078f6fbec', 8, 16, 'Fazer um inventário não significa sempre fazê-lo com a caneta vermelha. É raro não haver um dia em que não tenhamos feito algo certo. À medida que revelo minhas fraquezas, muitas de minhas qualidades serão reveladas também para mim, lembrando-me de que elas são tão reais quanto os meus erros. Mesmo quando tentamos muito e falhamos, por exemplo, podemos registrar isso como os maiores créditos de todos. Tentarei dar valor às minhas boas qualidades, porque elas não somente se justapõem aos erros, mas também servem de base para o meu crescimento. É tão auto-enganador descartar o que temos de bom como justificar o que não é.

Posso aceitar com tranqüilidade meus pontos positivos, aceitando a mim mesmo como amigo?

Hoje Eu Rezo
Se encontrar somente defeitos quando me examino no espelho do Quarto Passo, que eu tenha certeza de que alguma coisa está me escapando – principalmente os meus pontos positivos. Embora a sociedade aprove a minha grande modéstia , devo aprender que isso é tão desonesto quanto a racionalização para diminuir os meus defeitos. Mesmo um fracasso total, quando analisado em todos os seus ângulos, pode se transformar em um ponto positivo junto com os óbvios pontos negativos.

Hoje Eu Me Lembrarei
Se não der uma nota  “A”  a mim mesmo pelo esforço, pelo menos darei um “B - (B menos)“.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('6d02f243-12ec-43b4-b665-acf4c6cbe5d4', 7, 2, 'Durante os dias de nossa adicção na ativa, muitos de nós demonstraram um poder de imaginação muito fértil. Sem perder tempo, a gente imaginava mais razões – ou mais desculpas - para prosseguirmos com nossa adicção do que qualquer pessoa poderia imaginar em sua vida inteira. Quando chegamos a Jogadores Anônimos a nossa imaginação tão criativa pareceu se tornar letárgica e até anestesiada: - E agora, o que faço?, muitos se perguntavam. Entretanto, gradualmente a letargia foi desaparecendo. Começamos a aplicar a nossa inventiva imaginação em novos e saudáveis desafios. Nós nos tornamos tão ligados a  vida de maneiras jamais imaginadas possíveis..

Estou entendendo que agora posso apreciar atividades que nos velhos tempos jamais imaginaria  que apreciaria?

Hoje Eu Rezo
Que Deus me dê uma nova fonte de energia direcionada para “estar ligado na vida” ao invés de arranjar desculpas para não assumir minhas responsabilidades. Que o meu Poder Superior permita que a minha tão alquebrada imaginação seja restaurada – não para a hiper-atividade esfuziante e desculpas de meus dias de jogo, mas para uma abertura saudável para uma vida de possibilidades ilimitadas.

Hoje Eu Me Lembrarei
Ligue-se à vida.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('3148dcce-d239-4621-908e-3c4d9fca143b', 4, 13, 'Independente do número, jogadores compulsivos geralmente ficam influenciados negativamente pela idéia,  de que se alguma vez chegarem perto do Programa de Jogadores Anônimos – quer indo a uma reunião, ou conversando pessoalmente com um membro - serão pressionados a aceitar algum tipo de fé ou religião. Eles não se dão conta, de que  a fé nunca  é imposta no Programa, para que se tornem membros. A libertação do jogo pode ser alcançada somente com um mínimo,  facilmente aceitável, disso; pois nossa concepção de um Poder Superior e de Deus – como o entendemos – possibilita que qualquer pessoa tenha uma escolha quase que ilimitada de crença espiritual e atitude.

Estou me fortalecendo por compartilhar com os novos membros?

Hoje Eu Rezo
Que eu nunca afugente ingressantes ou afaste aqueles que estão considerando seu ingresso em JA ao impor a eles a minha visão pessoal de um Poder Superior. Que cada um deles descubra sua própria identidade espiritual. Que todos encontrem em si mesmos uma conexão com algum grande Ser ou Espírito universal cujo poder seja maior do que o seu próprio. Que eu cresça, tanto em tolerância quanto em espiritualidade, todos os dias.

Hoje Eu Me Lembrarei
Eu me aproximarei, ao invés de fazer pregações.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('853b03ac-c34e-42ec-a2f0-02d851848197', 6, 6, 'Uma das coisas mais importantes que busco e que posso encontrar em Jogadores Anônimos é me ajustar às coisas como elas são, e ser capaz de amar sem tentar interferir na vida de alguém ou controlá-lo. Às vezes, o aprendizado é sofrido, mas a recompensa é a própria vida – completa e serena.

O Programa está me ajudando a ter um modo de pensar saudável e razoável para que eu possa lidar com meus relacionamentos interpessoais com amor e compreensão?

Hoje Eu Rezo
Que eu respeite aqueles que amo o bastante para que tenham liberdade – evitando controlá-los, manipulá-los, usando ardis ou resolvendo os seus problemas. Que eu os ame bastante para deixá-los livres para cometer seus próprios erros e assumir a responsabilidade pelos mesmos. Que eu aprenda a deixá-los viver.

Hoje Eu Me Lembrarei
Amar é deixar viver.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('9b2a5935-23cf-491f-bd61-0e060197b7d4', 3, 20, 'Quanto mais tempo estou em Jogadores Anônimos, mais o slogan “as primeiras coisas primeiro”, torna-se importante. Eu costumava acreditar que minha família, minha vida em casa e meu emprego estavam em primeiro lugar. Mas hoje sei, do fundo de meu coração, que se não conseguir ficar em abstinência, não terei nada disso. “As primeiras coisas primeiro” significam que tudo em minha vida depende de minha abstinência do jogo.

Sou grato por estar em abstinência do jogo hoje?

Hoje Eu Rezo
Que minha primeira prioridade, o primeiro item de minha lista de preocupações, seja minha abstinência do jogo – conservando-a, aprendendo a viver tranqüilamente com ela, compartilhando os meios pelos quais eu a mantenho. Quando outras coisas invadem minha vida e me vejo divagando, que eu mantenha este objetivo como o mais importante de todos -  ficar longe do jogo.

Hoje Eu Me Lembrarei
As primeiras coisas primeiro.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('a6790644-b94f-4ff0-a9ef-25eedd55d247', 6, 29, 'Quando admitimos nossa derrota e chegamos ao Programa de  Jogadores Anônimos, muitos de nós imaginávamos o que faríamos com todo aquele tempo em nossas mãos. Todas as horas que antes passávamos planejando, escondendo, criando álibis, perdendo nossas camisas, pedindo emprestado, inventando histórias – e todo o resto – estava ameaçado de ficar perdido no vácuo do tempo, que de alguma forma teria que ser preenchido. Precisávamos de novas formas para usar a energia que era anteriormente absorvida pela nossa adicção. Logo, concluímos que preencher este vácuo com uma nova e diferente atividade era muito mais fácil do que não colocar nada em seu lugar.

Estou redirecionando minha mente e energia?

Hoje Eu Rezo
Rezo para que, uma vez livre do peso de minha adicção, eu possa recorrer ao meu Poder Superior para descobrir por mim  como preencher o meu tempo construtiva e criativamente. Que esse mesmo Poder Superior,  que faz com que os caminhos se cruzem e que liga algumas pessoas a situações específicas, me levem a novos e bons lugares através de novos e bons caminhos.

Hoje Eu Me Lembrarei
As coisas podem acontecer por razões outras que o acaso.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('ab18ec3c-1ca3-43dc-aa6f-0814cecee506', 11, 8, 'Meu contato consciente com Deus depende inteiramente de mim e de meu desejo em relação a isso. O poder de Deus está à minha disposição a todo o momento; é minha escolha fazer uso dele ou não. Sempre se diz que “Deus está presente em todas as Suas criaturas, mas nem todas elas estão conscientes de Sua presença  da mesma maneira.“ Tentarei me lembrar todos os dias da importância de minha consciência da influência de Deus em minha vida. Tentarei aceitar a Sua ajuda em tudo que faço.

Lembrar-me-ei de que Deus sabe como me ajudar, de que Ele pode me ajudar, e de que Ele quer me ajudar.

Hoje Eu Rezo
Que eu tenha sempre a consciência de que o poder de Deus e a Sua paz são fontes inesgotáveis dentro de mim. Posso tirar balde após balde dessa fonte para refrescar e purificar a minha vida. Só preciso providenciar os baldes e a corda. A água é minha – sem custo, fresca, cicatrizante, e límpida.

Hoje Eu Me Lembrarei
A fonte é de Deus; eu providencio os baldes.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('ce868afb-3c1b-42f6-9a32-1645bf13b7cb', 9, 17, 'Quando vejo novos membros chegando a Jogadores Anônimos, lembro-me, primeiro do sofrimento e da confusão incontrolável em que me encontrava, e depois da esperança que senti quando me dispus a aceitar o Programa de Jogadores Anônimos. Quando encontro e ouço os Veteranos no Programa, alguns com vinte anos ou mais em JA, lembro-me de que se eu quiser continuar minha recuperação, preciso tornar este Programa parte de minha vida diária. Mesmo não conseguindo esquecer o ontem, o HOJE deve ser meu foco principal.

Sei que se tornar o Programa de JA parte de minha vida hoje, não preciso me preocupar com o futuro ou me aborrecer com arrependimentos do passado?

Hoje Eu Rezo
Faça com que eu fique duplamente disposto – a compartilhar minhas experiências com os novos membros e a escutar os que possuem mais experiência do que eu. Tanto os Novatos quanto os Veteranos têm valiosas lições a me oferecer, se eu simplesmente ouvir e aprender. Ajude-me a aprender mais, para que eu possa oferecer mais.

Hoje Eu Me Lembrarei
Um bom professor permanece aberto ao aprendizado.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('504bd0ef-ad51-408d-996a-ffac49ac53b2', 4, 21, 'Consigo ser grato de todo o meu coração pelo dia de hoje? Se conseguir, estarei abrindo as portas para um bem cada vez maior. E se não consigo ser grato pela “chuva” que despencou em minha vida – os chamados tempos ruins, então o que acontece? Posso começar dizendo graças pelos dias em que posso lembrar que o sol nasceu; e por todas as bênçãos que chegaram a mim. Talvez então eu possa ser capaz de ver os períodos chuvosos de minha vida passada com um novo enfoque, julgando-os como necessários;  talvez então,  as bênçãos ignoradas, porque não lhes dei a devida importância, sejam dignas de minha atenção.

Sou grato por tudo na vida – tanto pelo nascer do sol quanto pela chuva?

Hoje Eu Rezo
Que eu seja agradecido por tudo que aconteceu comigo, bom ou ruim. O mal ajuda a definir o bem. O sofrimento intensifica a felicidade. A humildade traz espiritualidade. A doença transforma a saúde em paraíso. A solidão torna o amor, tanto humano quanto divino, a maior dádiva de todas. Agradeço a Deus pelos contrastes que me fizeram conhecê-LO  melhor.

Hoje Eu Me Lembrarei
Sou grato pela plenitude da vida.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('d621b981-bbb9-4994-b844-8596bca156af', 8, 4, 'Uma das melhores maneiras de sair da auto-piedade é fazer uma “contabilidade instantânea.”. Para cada registro de sofrimento, na coluna de débitos de nosso livro, certamente poderemos encontrar uma benção para marcar na coluna dos créditos: a saúde que gozamos, as doenças que não temos, os amigos que nos amam e que permitem que nós os amemos, vinte e quatro horas sem jogar, um bom dia de trabalho. Se simplesmente tentarmos, podemos rapidamente listar um série de créditos, que superarão em muito os débitos que só nos trazem auto-piedade.

O meu equilíbrio emocional está na coluna dos créditos hoje?

Hoje Eu Rezo
Que eu aprenda separar  meus débitos e créditos e fazer um balanço deles. Que eu liste minhas incontáveis bênçãos na coluna dos créditos. Que o total me mostre a grande quantidade de coisas boas que consegui.

Hoje Eu Me Lembrarei
Eu tenho bênçãos em meus registros.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('c066e027-0a86-4dcc-a8ec-98380ccb6a63', 5, 24, 'Superar anos de suspeita e outros mecanismos de auto-proteção, dificilmente poderá ser um processo da noite para o dia. Nós nos tornamos condicionados a nos sentirmos mal-amados e mal-compreendidos em nossos sentimentos e emoções – não importa se realmente éramos ou não. Alguns de nós podem precisar de tempo e prática para nos livrarmos de nossas couraças e da aparentemente confortável familiaridade com a solidão. Mesmo quando começamos a acreditar e a saber que não estamos mais sozinhos, tendemos,  às vezes, a nos sentirmos e agirmos como nos velhos tempos.

Estou levando as coisas menos a sério? Estou aprendendo a “usar” o Programa de Jogadores Anônimos e a vida como uma vestimenta mais solta?

Hoje Eu Rezo
Que eu não espere uma mudança completa e imediata de todas as minhas antigas características. Minha abstinência do jogo é somente um início. Que eu me conscientize de que os sintomas de minha compulsão desaparecerão gradualmente. Se eu retroceder, de vez em quando, ao meu antigo fardo de auto-piedade ou minha mania de grandeza, não devo me sentir desencorajado, mas sim agradecido. Finalmente, posso encarar a mim mesmo com   honestidade não deixando que meus delírios me dominem.

Hoje Eu Me Lembrarei
Vá com calma.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('d6a0f488-1833-4ac0-8ff0-17c875e5bb53', 4, 5, 'Uma outra tendência que invariavelmente se vê entre os que recaem é que muitos deles se sentem insatisfeitos com o dia de hoje. – Eu esqueci que vivemos um dia de cada vez; – Eu comecei a antecipar o futuro; ou – Eu comecei a antecipar resultados e não somente planejá-los. Eles pareciam esquecer que tudo que temos é o AGORA. A vida continuou a melhorar para eles e, como a maioria de nós faz, eles esqueceram de como ela havia sido ruim. Ao invés disso, eles começaram a pensar em quanto a vida era desagradável comparando-a com o que ela poderia ser.

Comparo o hoje com o ontem, entendendo que, por contraste, hoje tenho grandes  graças e benefícios?

Hoje Eu Rezo
Se me sentir desencorajado com o hoje, que eu me lembre dos sofrimentos e dos aborrecimentos de ontem. Se eu ficar impaciente com o futuro, deixe-me apreciar o hoje vendo que a vida que tenho é muito melhor do que a vida que deixei para trás. Que eu nunca me esqueça o princípio de “um dia de cada vez.”

Hoje Eu Me Lembrarei
A insensatez de ontem.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('1e3c422b-960b-4257-9fa7-45388d2319fc', 8, 23, 'No Programa de Jogadores Anônimos ouvi dizer que alguém uma vez leu o seguinte: “Coloque em sua cabeça que qualquer pessoa pode ficar bem, não importa quem seja.  A única condição é que essa pessoa acredite em Deus e faça a limpeza da casa.” Isso é o que o Passo Sete significa para mim – que eu irei fazer a limpeza da casa e terei toda a ajuda necessária.

Tenho consciência de que ao fazer o Sétimo Passo não estarei abrindo mão de nada, e sim me livrando das coisas  que poderiam me levar de volta à adicção ao jogo e me afastar de minha paz de espírito?

Hoje Eu Rezo
Que eu saiba que se ignorar a palavra chave “humildade”, que resume toda a minha humildade, meu grande respeito e minha fé, estarei mais uma vez colocando peso demais sobre meus ombros e assumindo esse poder como meu. Que Deus em Sua sabedoria faça de Sua vontade a minha, de Sua força a minha, de Sua bondade a minha. Sendo beneficiado com esses dons Divinos, haverá em mim muito pouco espaço para os grandes  defeitos.

Hoje Eu Me Lembrarei
Creia em Deus e faça a limpeza da casa.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('31714a11-7689-4c7d-9e56-2dec3de9a8c2', 7, 18, 'Muitos poucos de nós sabem exatamente o que queremos, e nenhum de nós sabe o que é melhor para nós. Este conhecimento está nas mãos de Deus. Este é um fato que eu tenho que aceitar definitivamente, a despeito da minha rebeldia e resistência teimosa. De hoje em diante, vou limitar minhas orações aos pedidos por orientação, mente aberta para recebê-la e energia para colocá-la em prática. Usando a minha capacidade da melhor forma possível, vou adiar todas as minhas decisões, até que um contato com meu Poder Superior faça com que eu perceba quais  são as  melhores decisões para mim.

Fico “negociando” com o meu Poder Superior assumindo que eu sei o que é melhor para mim?

Hoje Eu Me Lembrarei
Que eu não tente fazer pactos com o meu Poder Superior. Ao invés disso, que eu seja um receptáculo, aberto a qualquer inspiração que Deus possa querer depositar em mim. Rezo para que eu possa lembrar que as decisões de Deus são melhores para mim do que os meus planos atrapalhados, e elas chegarão a mim sempre que eu precisar.

Hoje Eu Me Lembrarei
Eu não farei barganhas&#8230;ou apostas&#8230;com Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('11316d6f-ad91-4c4c-a7a5-7fa4e9f0636f', 9, 11, 'O amor, mais do que qualquer outra coisa, pode aliviar meu sentimento de depressão ocasional. Preciso continuar a ser uma pessoal que possa ser amada, com sentido de ser capaz de amar os outros, ao invés de estar preocupado se os outros me amam. Ao perder-me, de alguma forma, nos outros, emocionalmente ou espiritualmente, eu geralmente me encontro. Hoje compreendo o que me diziam nas primeiras reuniões mal compreendidas de Jogadores Anônimos, quando falavam que eu era a pessoa mais importante naquela sala.

Sei dizer a mesma coisa hoje, a  outros novos membros, dando um significado verdadeiro às palavras?

Hoje Eu Rezo
Que eu saiba,  que se puder amar as pessoas, sem esperar amor em troca, terei grandes chances de receber um quinhão de amor de volta. Somente minha expectativa por aprovação é que anula o valor de meu amor.

Hoje Eu Me Lembrarei
O amor não é um investimento, mas sim uma contribuição generosa.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('50b262ca-324b-43a8-9a07-d6bfed910468', 7, 10, 'O Programa de Jogadores Anônimos é uma estrada, não um lugar de descanso. Antes de chegarmos a este Programa – e, para alguns de nós muitas vezes depois de chegarmos – a maioria procurou as respostas para os  problemas na religião, na filosofia, na psicologia, e nas teorias de auto-controle e crescimento pessoal. Geralmente as nossas buscas visavam objetivos que eram exatamente o que queríamos: liberdade, calma, confiança, e alegria. Mas elas raramente ofereciam qualquer método prático de como se chegar lá – de como acabaríamos com a tristeza do desespero em que nos encontrávamos para chegarmos aonde queríamos estar.

Eu realmente acredito que através dos Doze Passos posso achar tudo o que preciso e tudo  que realmente almejo?

Hoje Eu Rezo
Que eu possa entender que, ao chegar ao Passo Doze, não me encontrarei em uma superfície plana, pois a vida não é um terreno plano, e sim uma ladeira íngreme. Essa subida dos Passos precisam ser tomadas repetidamente e serem relembradas. Que eu me certifique de que quando tiver me familiarizado com eles completamente, eles me levarão a qualquer lugar aonde eu queira ir.

Hoje Eu Me Lembrarei
Os  Passos são uma estrada, não um lugar de descanso.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('c3969da9-361e-46e5-8fe4-2c81415ca1c3', 11, 30, 'Se você for uma pessoa negativa e ainda não estiver pronto a dar uma guinada em sua vida, aqui estão algumas normas que poderão ajudá-lo a continuar infeliz pelo tempo que desejar. Primeiro, não freqüente as reuniões de Jogadores Anônimos. Se, de algum modo, você aparecer em uma reunião, mantenha sua boca (e sua mente) fechada e suas mãos nos bolsos. Não tente resolver qualquer dos seus problemas, nunca ria de si mesmo, e não confie em outras pessoas no Programa. Acima de tudo, em hipótese alguma, tente viver no Agora – simplesmente continue a alimentar suas fantasias sobre um futuro irreal.

Estou consciente de que o pensamento negativo leva-me a ser perigosamente sério em todas as situações, não me dando tempo para rir ou viver?

Hoje Eu Rezo
Se estiver me sentindo negativo, devo me olhar  no espelho representado pelo próprio grupo procurando por sintomas de uma mente fechada; lábios cerrados, sorriso falso, mandíbula tensa, e olhar fixo – e nem um sinal de humor. Deus, concedei-me a capacidade de rir de mim mesmo – freqüentemente – porque necessito desse riso para lidar com a comoção diária da vida.

Hoje Eu Me Lembrarei
Rir de mim mesmo.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('14637845-6bca-4830-badb-63674ac891af', 1, 19, 'Foi muito mais fácil para mim aceitar minha impotência perante o jogo, do que aceitar a noção de que algum tipo de Poder Superior poderia conseguir o que eu havia sido incapaz de conseguir sozinho. Somente através da procura por ajuda e da aceitação da companhia de outros que sentiam uma aflição semelhante à minha, a forte vontade me abandonou. Percebi que, se eu estava fazendo o que tinha sido incapaz de fazer sozinho, com certeza, então, haveria algum Poder que não o meu, que era obviamente superior.

Entreguei minha vida nas mãos de Deus?

Hoje Eu Rezo
Que Deus dissipe em mim o orgulho arrogante que me impede de ouví-Lo. Que minha dependência doentia do jogo e a dependência daqueles que me cercam sejam transformadas em confiança em Deus. Somente com esse tipo de dependência/confiança em um Poder Superior, encontrarei minha própria transformação.

Hoje Eu Me Lembrarei
Sou dependente de Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('c1ed4e78-c5f0-40de-bb69-41a320424ae3', 7, 12, 'De muitas maneiras, a Irmandade de Jogadores Anônimos é como um cruzeiro relativamente feliz, ou, em períodos difíceis, como um comboio. Mas, a longo prazo, cada um de nós deve traçar o seu curso através da vida. Quando as águas estiverem calmas, podemos nos tornar desatentos. Quando negligenciamos o Passo Dez,  podemos esquecer o hábito de verificar nossa posição. Mas, se estivermos atentos ao Passo Dez, então raramente sairemos do curso a ponto de não podermos fazer algumas correções que nos coloquem de volta ao curso novamente..

Entendo que a prática regular do Passo Dez pode me trazer serenidade e um estado de alma mais feliz?

Hoje Eu Rezo
Que o Passo Dez seja o Sextante pelo qual oriento meu barco no mar da vida, para que eu possa corrigir meu curso e refaça o roteiro quando estiver entrando em águas rasas. Que eu tenha em mente que, se não fosse pelo Comandante conhecedor de tudo e a vigilância dos meus amigos tripulantes da Irmandade, este navio poderia ficar à deriva e eu poderia facilmente entrar em pânico.

Hoje Eu Me Lembrarei
Velejar guiado por uma estrela permanente.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('ec606fff-99f7-46cc-ac6e-65498fec2e48', 7, 31, 'Uma das mais sérias conseqüências da síndrome do eu-eu-eu, é que perdemos contato com praticamente todo mundo à nossa volta – sem falar na própria realidade. A essência da auto-piedade é total auto-absorção, e ela se alimenta de si mesma. Ao invés de ignorar tamanho estado emocional – ou negar que estamos nele – devemos botar a cabeça para fora de nossa auto-absorção, dar um passo atrás, e dar uma boa e honesta olhada dentro de nós mesmos. Assim que reconhecermos auto-piedade pelo o que ela realmente é, podemos começar a fazer alguma coisa sobre isso.

Estou vivendo no problema ao invés de buscar a solução?

Hoje Eu rezo
Que a minha preocupação comigo mesmo, que está tão arraigada como uma hera, possa se desgarrar e deixar que seus galhos cresçam em direção a outras pessoas em quem eu me apegue e permaneça apegado. Que o eterno e familiar “eu-eu-eu” se transforme em um coro de “nós-nós-nós”, à medida que, na Irmandade, desmembremos nossa totalidade e façamos uma análise dela juntos.

Hoje Eu Me Lembrarei
Mudar “eu-eu-eu” para “nós-nós-nós”.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('40980eae-4311-4b79-b6e4-0555214200d8', 9, 2, 'Quando acordar, pensarei silenciosamente sobre as vinte-e-quatro horas a seguir. Pedirei a Deus para direcionar o meu pensamento, pedindo em especial que ele esteja desvinculado da auto-piedade e dos motivos desonestos ou interesseiros. Se tiver que escolher qual dos vários rumos a tomar, pedirei a inspiração, intuição ou até mesmo a decisão a Deus. Depois relaxarei e ficarei tranqüilo, confiante de que tudo terminará bem.

Posso acreditar que, quando me esquecer de meus “direitos”, conhecerei a liberdade?

Hoje Eu Rezo
Louvo a Deus por ser capaz de louvá-lo, por escolher os momentos em que devo procurá-Lo, por encontrar minhas próprias palavras quando converso com Ele da maneira que considerar melhor para mim. Que eu espere que Ele, por outro lado, independente de minhas expectativas, e aja em minha vida como Lhe parecer melhor.

Hoje Eu Me Lembrarei
Quem sou eu para tentar dizer a Deus o que fazer?', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('e814dd3e-6e83-479f-bc14-0d7ee21dd433', 3, 27, 'Guardar memórias desagradáveis não é  só uma perda de tempo, mas também uma perda da vida que poderia ser vivida com maior satisfação. Se faço uma contabilidade  das “opressões e injustiças”, estou somente trazendo-as de volta para criar uma realidade dolorosa. Em Através da Lente, de Lewis Carroll, “O horror daquele momento” disse o Rei, “nunca, nunca esquecerei”, “entretanto,  você irá fazê-lo”, disse a rainha, “se não ficar relembrando-o.”

Estou mantendo um estoque secreto de desgraças de meu passado?

Hoje Eu Rezo
Que Deus me impeça de ficar me apegando à lama do passado – sofrimentos, aborrecimentos, reclamações, opressões, erros, injustiças, humilhações, futilidades e mágoas, pois estes me aborrecerão e consumirão meu tempo relembrando o que eu possa ter dito ou feito, até que eu possa analisar cada um deles, identificar a emoção que eles produzem em mim, acomodá-los da melhor maneira possível, - para então esquecê-los. Que eu esvazie meu depósito de velhas lamentações.

Hoje  Eu me Lembrarei
Não se prender aos fantasmas do passado.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('61bdd29f-f935-44ca-9bbf-7125c6eba94c', 5, 19, '“O que ficou para trás e o que nos espera são coisas insignificantes quando comparadas ao que está dentro de nós,” escreveu Oliver Wendell Holmes. Nunca havia sido capaz de olhar para dentro de mim até chegar a Jogadores Anônimos e escutar a minha própria história sendo contada. A partir daí, não mais tive a “singularidade” para guardar para mim. Além disso, a pessoa parecia  muito mais feliz contando essa história do que eu ouvindo-a. Eu tinha inveja dos vencedores – sempre tive – assim, comecei mesmo que aos poucos, a contar minha própria história.

Fico surpreso hoje com o fato de que tudo que guardei para mim pudesse ter sido mantido em  segredo para o mundo exterior durante tanto tempo?

Hoje Eu Rezo
Independente do que estiver por acontecer ou do que aconteceu no passado, devo lembrar-me que preciso ter Deus dentro de mim para que Ele possa guiar-me em situações difíceis. Devo também agradecer a Deus quando não me encontro em uma situação difícil – e saber que Ele é a razão por eu estar onde me encontro hoje.

Hoje Eu Me Lembrarei
Criar um espaço para Deus dentro de mim.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('d87fd5e5-940b-4689-b0c1-6ad53ce827c5', 9, 10, 'Anos atrás, Dr. Alfred Adler prescreveu este remédio a um paciente com depressão: “Você pode ser curar,  se todos os dias tiver como primeira tarefa da manhã, o pensamento de como poderá trazer a felicidade a alguém. Se continuar com esse propósito por duas semanas, você não mais necessitará de terapia” A “prescrição” de Adler, como se vê, não é muito diferente da sugestão de que trabalhemos mais intensamente o Passo Doze para nos livrarmos da depressão.

Quando estou deprimido guardo para mim  meus sentimentos, ou faço o que meus amigos de Jogadores Anônimos já me sugeriram?

Hoje Eu Rezo
Que eu coloque para fora o que está dentro de mim, livrando-me da depressão que ficou bem guardada e que substitua-a pela sensação agradável de que meus amigos verdadeiros se importam comigo; e que depois,  eu transmita essa boa sensação a outros que estejam sendo consumidos pelo mesmo desespero.

Hoje Eu Me Lembrarei
O único desespero real é  a solidão – e a solidão pode ser tratada.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('3647ca9d-e9a3-45d1-9acd-2d7fe16bf12a', 8, 1, 'A auto-piedade é um dos mais terríveis e destruidores sentimentos que eu conheço. Por causa de suas incontáveis exigências por atenção e simpatia, minha auto-piedade corta o meu canal de comunicação com as outras pessoas, principalmente a comunicação com meu Poder Superior. Quando vejo o problema desta maneira, vejo que a auto-piedade limita o meu progresso espiritual. Ela é também uma verdadeira forma de martírio, que é um luxo que simplesmente  não posso ter. Aprendi que o remédio para isso é olhar intensamente para mim mesmo e mais intensamente ainda para os Doze Passos do Programa de Recuperação de Jogadores Anônimos.

Peço ao meu Poder Superior para me livrar desta servidão para comigo mesmo?

Hoje Eu Rezo
Que eu possa saber por experiência própria que autocomiseração não desperta sentimento de pena de ninguém. Ninguém – nem mesmo Deus – pode suprir sua descabida necessidade de pena. Que eu reconheça os dissabores da auto-piedade quando ela tentar roubar minha serenidade. Que Deus me mantenha ciente dos truques sorrateiros da auto-piedade.

Hoje Eu Me Lembrarei
Meu captor sou eu mesmo.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('44a43308-8892-4c01-a9f3-e39ccdd3eea3', 1, 24, 'Dentre as muitas dádivas que recebemos no Programa de Jogadores Anônimos está o dom da liberdade. Entretanto, paradoxalmente, a dádiva da liberdade tem o seu preço: a liberdade só pode ser alcançada quando se paga o preço da “aceitação”. Da mesma forma, para podermos nos render à orientação de Deus, isso nos custará nossa vontade própria, este “bem” tão precioso para os que dentre nós sempre acreditaram que poderiam e deveriam comandar o espetáculo.

Hoje minha liberdade vale o preço da aceitação?

Hoje Eu Rezo
Que Deus me ensine a aceitar -  a ter a capacidade de aceitar as coisas que não posso modificar. Que Deus também me conceda coragem para modificar as coisas que posso. Que Deus me ajude a aceitar a doença da minha adicção e me dê coragem para modificar o meu comportamento de adicto.

Hoje Eu Me Lembrarei
Aceite a adicção. Mude o comportamento.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('7c8f7ff4-2e5d-47ed-9aad-2ab098db26a0', 8, 22, 'No passado,  freqüentemente rezamos pedindo coisas, tal como um acontecimento fortuito,  ou um golpe de sorte. Aprendi em Jogadores Anônimos que a verdadeira oração começa – ao invés de terminar – quando peço a Deus para  mudar-me. Na verdade, isso é exatamente o que o Sétimo Passo sugere: humildemente pedimos a Deus (de nosso entendimento) que removesse nossos defeitos. Pedimos a ajuda de Deus por meio de Sua graça e, surpreendentemente,  essa oração é ouvida se realmente desejarmos que ela o seja. Nossos próprios desejos formam uma parte tão necessária para o resultado, que quase temos a impressão de que nós mesmos o conseguimos. Mas, a ajuda de Deus é ainda mais necessária; sem Ele, provavelmente não conseguiríamos fazer isso sozinhos.

Pedi a Deus para que me ajude a mudar a mim mesmo?

Hoje Eu Rezo
Que eu aprenda a orar de maneira mais ampla – que seja feita a vontade de Deus, que Ele remova meus defeitos. Não há necessidade de especificar quais seriam esses defeitos; Deus, que sabe tudo, os conhece. Que eu aprenda que em minhas orações os detalhes não são necessários.  O que importa é minha humildade e minha fé de que Deus, realmente, tem o Poder de mudar minha vida.

Hoje Eu Me Lembrarei
Pedir a Deus para modificar-me.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('46754355-99ee-4af5-8e7b-2f50fc684979', 7, 17, 'Para o meu próprio bem, eu irei às reuniões e participarei das discussões com uma mente aberta que estará pronta para aceitar qualquer idéia nova. Para o meu conforto espiritual e paz, eu vou determinantemente aplicar estas novas idéias à minha própria vida. Lembrarei de que os Jogadores Anônimos me oferecem o suporte necessário o qual não encontrarei em nenhum outro lugar. Procurarei por outros que tenham os mesmos problemas que eu, e aceitarei os seus conselhos em assuntos que podem me causar desconforto e confusão.

Estarei disposto a ser um bom ouvinte – e a dividir?

Hoje Eu Rezo
Obrigado Senhor, por ter trazido o Programa de JA para a minha vida, aliado a uma melhor compreensão do Poder Divino. Ajude-me a me lembrar de que freqüência e atenção durante as reuniões são primordiais para que eu continue nesta feliz descoberta de um modo de vida. Que eu escute e compartilhe com honestidade, mente aberta e  boa vontade.

Hoje Eu Me Lembrarei
Aqui está a fórmula: Honestidade, Mente Aberta,  e Boa Vontade', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('90ca97b1-5fa4-4ffd-aff8-e898ac9bb08c', 3, 26, 'Hoje sei que ser ativo significa tentar viver os Passos do Programa da melhor maneira possível. Isto significa procurar alguma forma de honestidade, primeiro comigo mesmo, e depois, com os outros. Isso representa uma atividade voltada para dentro de mim mesmo, para que eu seja capaz de tanto me avaliar, quanto avaliar mais claramente o meu relacionamento com o meu Poder Superior.

Deixo todo o trabalho das reuniões para os outros? Faço a minha parte?

Hoje Eu Rezo
Que eu perceba que “me render e deixar que Deus aja”, não significa que eu não faça nada pelo Programa. É minha tarefa seguir os Doze Passos, para aprender algo que pode ser totalmente novo para mim – a honestidade. Que eu saiba fazer a distinção entre a atividade por si só – trabalho para me afastar dos maus pensamentos – e a atividade consciente que me ajuda a crescer.

Hoje Eu Me Lembrarei
“Render-me à vontade de Deus” significa deixar Deus nos mostrar como.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('9f6577ad-f1dc-478c-ae7d-efa3e41e4498', 3, 25, 'Se um jogador compulsivo quiser ter sucesso na sociedade, ele precisa substituir o poder do jogo sobre ele pelo poder de uma outra coisa – de preferência, positiva, ou que seja pelo menos neutra, mas não negativa. Por isso dizemos ao ingressante agnóstico: se você não consegue acreditar em Deus, procure uma força positiva que seja tão grandiosa quanto o poder de sua compulsão,  e dê a ela o poder e dependência que você deu à compulsão. Em Jogadores Anônimos o agnóstico fica à vontade para descobrir o seu Poder Superior, e pode usar os princípios do Programa e a terapia das reuniões para ajudá-lo a reconstruir sua vida.

Abrirei mão de minhas convicções para trabalhar com os recém-chegados?

Hoje Eu Rezo
Que o Programa de Jogadores Anônimos, com o seu poder, realize seus milagres de forma semelhante com aqueles que acreditam em um Deus pessoal, em um espírito Universal, na força do próprio Grupo, ou com aqueles que descrevem seu Poder Superior em seus próprios termos, sejam os mesmos religiosos ou não. Se os recém-chegados sentirem-se incomodados com a religiosidade do Programa, que eu os receba respeitando suas próprias formas de espiritualidade. Que eu reconheça que todos somos seres espirituais.

Hoje Eu Me Lembrarei
A cada um sua própria espiritualidade.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('a86ab56e-1200-4e07-bc42-accebcd87435', 4, 15, 'É raro encontrar jogadores compulsivos na ativa, que tendo uma idéia de como são irracionais, ou que vejam sua irracionalidade, consigam enfrentá-la. Um dicionário dá a definição de sanidade como “equilíbrio emocional”. Mesmo assim, nenhum jogador compulsivo que analise racionalmente seu comportamento destrutivo, pode alegar verdadeiramente que esteja de posse de seu equilíbrio emocional.

Passei a acreditar, como sugere o Segundo Passo, que um Poder Superior a mim pode me trazer de volta um modo normal de pensar e viver?

Hoje Eu Rezo
Que eu me conscientize que meu próprio comportamento como jogador compulsivo poderia ser descrito como “anormal” ou “insano”. Para os adictos ainda ativos é praticamente impossível admitir o próprio comportamento “insano”. Rezo para que eu continue a rejeitar as insanidades e inconseqüências de meus dias de adicção. Que outros como eu reconheçam seus problemas de adicção, encontrem ajuda em Jogadores Anônimos e comecem a acreditam que um Poder Superior possa lhes trazer de volta um modo normal de pensar e viver.

Hoje Eu Me Lembrarei
Ele recuperou a minha alma.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('e8b13b3e-1107-4217-ae07-5afcc339f67c', 7, 11, 'Alguém já descreveu o ego como “uma soma de todas as falsas idéias que eu posso ter sobre mim mesmo”. O trabalho constante dos Doze Passos ajuda-me a gradualmente a me livrar da visão falsa que tenho sobre mim. Isto permite um crescimento quase imperceptível, mas estável, do entendimento da verdade sobre mim mesmo. E, em conseqüência, leva –me a um crescimento no entendimento sobre Deus e as outras pessoas.

Procuro a auto-honestidade, admitindo prontamente quando estou  errado?

Hoje Eu Rezo
Deus, ensine-me a entender; ajude-me a ver a verdade, quando com ela me deparar; ensine-me a importância de ser honesto comigo mesmo, para que eu possa dizer, sinceramente:  - Errei, juntamente com – Perdoe-me. Ensine-me que existe uma coisa chamada “ego saudável”, que não exige que os sentimentos sejam acompanhados por grandes atitudes. Que eu – devagar, dentro das minhas condições – procure o equilíbrio ideal, para que eu possa ir diminuindo minhas tramas de falsidade e compulsão.

Hoje Eu Me Lembrarei
Manter meu equilíbrio.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('7fa590f4-7403-4323-997c-1af590ac2d69', 9, 18, 'Em todas as histórias que ouvimos em Jogadores Anônimos, a dor foi o preço da rendição para uma nova vida. Com essa rendição adquirimos muito mais do que esperávamos. Ela nos proporcionou a humildade, que logo descobrimos ser a cura para a dor. Com o tempo, começamos a sentir menos dor e desejar mais e mais a humildade.

Estou aprendendo a ser humilde ficando calmamente “em berço esplêndido” tirando o máximo de proveito do que vem e o mínimo do que vai?

Hoje Eu Rezo
Se o plano de Deus para nós é o crescimento espiritual, uma aliança mais selada com os Seus princípios do que é bom e do que é verdade, então devo crer que todas as minhas experiências me ajudaram na formação desta pessoa nova e melhorada em que me tornei. Que eu saiba que preciso continuar a crescer tanto através da dor quanto das alegrias.

Hoje Eu Me Lembrarei
Eu sofro, logo existo.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('ae9f63a7-de0e-4c25-8b43-e17987a72d03', 9, 12, 'Coleridge escreveu que “Em certos momentos, um único  e quase que insignificante momento de tristeza, pode por associação, trazer de volta todas as pequenas lembranças de sofrimento e desconforto, tanto físico como mental, que vivenciamos ainda que isso tenha ocorrido em nossa infância. ”O Programa de Jogadores Anônimos não nos ensina a fingir que os sofrimentos e o arrependimento não têm importância. Todos os tipos de sentimentos marcam, como também outras formas de sofrimento. Mas, agora que estamos livres de nosso jogo compulsivo, temos mais controle sobre esses pensamentos. O que escolhemos para pensar em um dia específico, pode influenciar o conjunto dos nossos sentimentos para aquele dia.

Estou encontrando melhores e diferentes formas de usar minha razão?

Hoje Eu Rezo
Que eu agradeça a Deus pela dor que conjuga uma série de mágoas antigas em uma só, possibilitando que eu as identifique e avalie para depois livrar-me delas, criando espaço para as preocupações novas e atuais. Devo agradecer a Deus por recuperar minha sensibilidade à dor, após o torpor incômodo de meus dias de jogo.

Hoje Eu Me Lembrarei
Posso agradecer a Deus por haver restaurado meus sentimentos.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('7e946cc2-0084-4603-ab6a-54c8ff1478c5', 12, 6, 'Quando finalmente me convenço a abandonar um problema que está me torturando – quando tomo a decisão de colocar de lado  minha vontade e de deixar que Deus resolva o problema – meu tormento diminui imediatamente.  Se eu continuar a ficar de lado em meu próprio caminho, então as soluções começam a aparecer e se revelam por  conta própria. Cada vez mais, começo a aceitar as limitações de minha compreensão e poder humano. Cada vez mais, estou aprendendo a deixar que as coisas aconteçam e confiar em meu Poder Superior para as respostas e  ajuda.

Mantenho claro em minha mente o fato de que somente Deus é completamente sábio e todo poderoso?

Hoje Eu Rezo
Se encontrar um obstáculo, que eu aprenda a ficar de lado deixando que Deus o remova. Que eu conheça a minhas limitações humanas para resolver problemas, já que nunca serei capaz de antecipar as soluções de Deus até que eu as veja acontecendo. Que eu saiba que não importa a resposta que eu encontre, Deus poderá ter uma melhor.

Hoje Eu Me Lembrarei
Deus tem a melhor resposta.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('bd644f97-29b8-4141-b657-08a2435b72e1', 10, 29, 'Todos nós virtualmente sofremos o defeito do orgulho quando procuramos por ajuda através do Programa, dos Doze Passos, e da Irmandade de jogadores compulsivos que estavam se recuperando e que realmente entendiam o que sentíamos e onde haviam chegado. Aprendemos sobre os nossos pontos fracos – e o orgulho em especial – e começamos a substituir a satisfação pessoal com a gratidão pelo milagre de nossa recuperação, gratidão pelo privilégio de estar trabalhando em conjunto, e gratidão pela graça de Deus, o que nos capacitou a transformar a catástrofe em crescimento e bons presságios.

Comecei a me dar conta de que o “orgulho está para o caráter,  como o sótão está para a casa, a parte mais alta, mas geralmente a mais vazia.”

Hoje Eu Rezo
Deus, por favor me diga se estou dando caneladas no meu orgulho. Felizmente, o Programa de Jogadores Anônimos tem o seu próprio teste para defeitos como esse – uma visão mais clara do grupo que vê em mim o que algumas vezes não consigo enxergar em mim mesmo. Que eu saiba que todos os tipos de sucesso sempre chegaram primeiramente em minha mente e que devo estar atento a isso à medida que começo a reconstruir minha confiança.

Hoje Eu Me Lembrarei
O sucesso “presunçoso” pode se tornar um retrocesso.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('7619564e-81a3-4cb9-a886-9577543fca2b', 12, 29, 'Aprendi que o  sucesso do Programa de Jogadores Anônimos, reside em grande parte na prontidão e desejo de seus membros de tentar a qualquer preço ajudar outras pessoas tiranizadas pela adicção ao jogo. Se minha prontidão e vontade amainarem, então correrei o risco de perder o que alcancei. Devo estar sempre disposto a dar o  que tenho,  porque ao fazê-lo me tornarei um privilegiado podendo conservá-lo.

Levo o Passo Doze a sério? Que mensagem do Décimo Segundo Passo transmiti hoje – seja através da ajuda direta ou através de meu exemplo?

Hoje Eu Rezo
Que eu nunca esteja ocupado demais para atender ao pedido de ajuda de um companheiro jogador compulsivo. Que eu nunca chegue perto de esquecer que a continuação de minha própria recuperação depende dessa ajuda – uma meia-hora  ou mais ao telefone, uma visita pessoal, um encontro para almoço, ou qualquer outra situação que se apresente. Que eu saiba quais deverão ser as minhas prioridades.

Hoje Eu Me Lembrarei
Ajudar, me ajuda.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('6f1d7e48-64e8-4ab0-a8c5-8ef38e09e395', 10, 6, 'À medida que “continuamos a voltar” às reuniões, tornamo-nos capazes de reconhecer aqueles que demonstram serenidade em abundância. Somos atraídos para essas pessoas. Para nossa surpresa, algumas vezes, descobrimos que aqueles que parecem estar mais agradecidos pelas bênçãos do dia de hoje, são os que têm os maiores e mais contínuos problemas,  em casa ou no trabalho. Ainda assim, eles têm a coragem de ignorar os problemas, procurando ativamente aprender e ajudar outras pessoas no Programa de Jogadores Anônimos. Como essas pessoas conseguiram tal serenidade? Isso deve acontecer porque elas dependem menos de si mesmas e de seus próprios e limitados recursos  – e mais de um Poder Superior  em quem  confiam.

Estou conseguindo a graça da serenidade? As minhas atitudes começaram a refletir minha fé interior?

Hoje Eu Rezo
Que eu nunca deixe de ficar admirado com a serenidade que vejo nas outras pessoas do meu grupo – a serenidade que mostra sua rendição a um Poder Superior. Que eu aprenda com eles que a paz de espírito é possível mesmo quando os maiores problemas estão ocorrendo. Que eu aprenda também que ocasionalmente precisarei me colocar à parte de meus problemas e recorrer ao lago de serenidade dentro de mim que Deus me concedeu.

Hoje Eu Me Lembrarei
A serenidade é a rendição ao plano de  Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('2fc71c32-e4b7-4780-be89-e7f40405acb6', 5, 5, 'Eu sabia que precisava recomeçar e o começo teria que ser aqui. Não poderia recomeçar em qualquer outro lugar. Precisava abandonar o passado e esquecer o futuro. Enquanto segurasse o  passado com uma mão e o futuro com a outra, não me restaria nada com que eu pudesse agarrar o presente. Assim, tive que começar aqui, agora.

Pratico o Décimo Primeiro Passo pedindo somente pelo conhecimento da vontade de Deus para  comigo e a capacidade de conseguir pô-la em prática?

Hoje Eu Rezo
Que eu não me preocupe em relação ao modo como verbalizo meus desejos e necessidades em minhas orações ao Poder Superior. Que eu não tenha medo da maneira como uso as palavras em minhas orações, pois Deus não precisa de palavras; a comunicação com Ele está além delas. Que o Décimo Primeiro Passo me guie todas as vezes em minhas orações.

Hoje Eu Me Lembrarei
Seja feita a vontade de Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('3a2cd80a-d55e-4573-9ea5-a93b10bd2e54', 9, 20, 'Schopenhauer escreveu que “Quando o homem atinge uma condição em que acredita que alguma coisa precisa ocorrer – sem desejar que isso aconteça – e que o que deseja jamais poderá ocorrer, este é realmente o estado que chamamos de desespero.” A verdadeira dor das dificuldades emocionais é algumas vezes muito difícil de suportar no período em que  estamos tentando manter a abstinência. Mesmo assim, aprendemos com o tempo, que superar tais problemas é o verdadeiro teste  do modo de vida de Jogadores Anônimos.

Acredito que as adversidades me dão mais oportunidades de crescer, do que o conforto e o  sucesso?

Hoje Eu Rezo
Que eu acredite fielmente que Deus, em Sua sabedoria infinita, não envia-me esses momentos ocasionais de tensão emocional para perturbar a minha abstinência., e sim para desafiar-me a aumentar meu controle e minha convicção. Que eu aprenda a não temer os grandes altos e baixos emocionais, pois o Programa de Jogadores Anônimos me preparou para todos os tipos de terrenos.

Hoje Eu Me Lembrarei
Força através da adversidade.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('ba50c7f0-73e6-4c8a-a9da-daa2765442b2', 4, 24, 'No Programa de Jogadores Anônimos descobrimos que não há maior satisfação nem maior alegria, do que fazer um  Passo Doze bem feito. Ver a expressão de descoberta nos olhos de alguém à medida que sai da escuridão para a luz, ver sua vida rapidamente ser preenchida com um novo propósito e sentido e, acima de tudo, vê-lo despertar para a presença de um Deus amoroso em sua vida – todas são coisas que representam a essência do que recebemos, quando transmitimos a mensagem do programa a outras pessoas.

Estou aprendendo, através da vivência do Passo Doze, que a gratidão deveria ser passada adiante, ao invés do contrário?

Hoje Eu Rezo
Que meu Passo Doze seja tão completamente verdadeiro, convincente e construtivo, como foi para mim a maneira como os outros o fizeram. Que eu me conscientize de que a grandeza do Programa e a sua eficácia para todos nós, surge de sua “transmissão a outros”. Quando ajudo uma outra pessoa à chegar à abstinência, minha própria abstinência é enfatizada e reforçada. Humildemente, peço a orientação de Deus antes de cada Passo Doze.

Hoje Eu Me Lembrarei
Transmitir a mensagem a outros.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('7cbf7afd-c1e9-4a54-a67a-e60a12aae1b7', 3, 17, '“Não nos deixei cair em tentação” -  oramos assim pois temos a certeza de que a tentação nos espreita na próxima esquina. A tentação é insistente, poderosa e paciente. Nunca sabemos quando nos pegará indefesos. A tentação pode vir através da música de chamada de uma propaganda colorida,  um comercial de radio, o neón ou o barulho do cassino, ou mais obviamente, através do impulso urgente,  ou através de outra pessoa. Precisamos permanecer para sempre vigilantes, lembrando que a primeira aposta, o primeiro contato com uma máquina de apostas,  ou o primeiro lance de dados pode destruir nossas vidas.

Estou consciente de minha prioridade número um?

Hoje Eu Rezo
Deus, afaste-me da tentação – seja do ruído dos dados da roleta, da virada de uma carta numa rodada de poker, ou da fumaça de um salão de Bingo. Que eu conheça os limites de minha resistência e permaneça bem dentro desses limites. Que minha rendição à vontade de Deus dê um significado completamente novo àquela velha expressão – “Entre no espírito da coisa”.

Hoje Eu Me Lembrarei
Entrar no espírito.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('38551b72-0faf-4e15-a0df-825f2cfcd429', 10, 8, 'Determinação – nossa mandíbula cerrada refletindo a convicção de que podemos fazer praticamente tudo – é talvez o maior impedimento para alcançarmos a serenidade. Nossas vozes antigas nos dizem que ‘Podemos fazer o que é difícil imediatamente; o impossível levará um pouco mais de tempo.” Assim comprimíamos nosso corpo e a nós mesmos preparando-nos para a batalha, mesmo sabendo por larga experiência que nossa vontade própria nos levaria antecipadamente ao fracasso. Repetidamente, nos dizem no  Programa de Jogadores Anônimos, que precisamos “Deixar as coisas acontecerem e deixar Deus agir.” Eventualmente, realmente encontramos a serenidade quando deixamos de lado a nossa vontade própria, ao mesmo tempo em que aceitamos a vontade de Deus para nós.

Estou aprendendo a relaxar meu pulso determinado? Permito que as  soluções se revelem por sua própria conta?

Hoje Eu Rezo
Que eu relaxe a minha mandíbula tensa, meus punhos cerrados e minha tensão global – indícios externos da síndrome do “eu faço tudo sozinho” que antes me causou tantos problemas. Que eu saiba por experiência que a atitude – “tenha um pulso forte com você mesmo e também com todas as outras pessoas” – acompanha a  impaciência e vem seguida da frustração. Que eu funda minha vontade própria com a vontade maior de Deus.

Hoje Eu Me Lembrarei
Afrouxe o nó que o estrangula.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('52920377-636c-45e0-b47a-99c257c4333a', 12, 8, 'Geralmente vemos pessoas em Jogadores Anônimos – que com  aparente devoção e sinceridade – pedem pela orientação de Deus em  assuntos desde as maiores crises até coisas insignificantes,  como o que deveriam servir no jantar. Embora possam ser bem intencionadas, essas pessoas tendem a forçar suas decisões em todos os tipos de situações – sentindo-se relativamente seguros de que estão seguindo orientações específicas de Deus. Na verdade, este tipo de oração serve somente para atender os propósitos da própria pessoa que exige “respostas” de Deus;  tendo muito pouco a ver com o que é sugerido no Passo Onze do Programa de Jogadores Anônimos.

Procuro com freqüência estudar cada um dos Passos e praticá-los em todos as minhas atividades?

Hoje Eu Rezo
Que eu não cometa o erro comum de listar minhas próprias soluções como se fossem de Deus e então pedir pelo carimbo de aprovação Divina. Que eu me conscientize, se realmente não estiver com a mente aberta para receber a orientação de Deus, mas simplesmente adotando uma atitude dependente de minhas próprias respostas a perguntas como – O que o Senhor acha disso?

Hoje Eu Me Lembrarei
Estou esperando pelo carimbo de aprovação de Deus?', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('63bc5420-2a13-4386-b332-43fd4c0452bf', 5, 21, '“A linguagem da amizade não é transmitida através de palavras, mas de significados,” escreveu Thoreau. De fato, a vida adquire novos significados,  como também um novo significado  no Programa de Jogadores Anônimos. Ver a recuperação das pessoas, vê-las ajudando outros, ver a solidão desaparecer, ver uma Irmandade crescer aos seus olhos, ser acolhido por amigos – esta é uma experiência que não se pode perder.

Recordo-me de minhas reações iniciais quando cheguei a Jogadores anônimos? Acredito que finalmente  estou em casa?

Hoje Eu Rezo
Assim como  o Programa de JA deu novos significados  a minha vida, que eu transmita aos outros a mesma oportunidade de reavaliar suas vidas à luz da recuperação, propósitos comuns, amizades e crescimento espiritual. Que eu dê graças a Deus por minha nova visão humana de vida e por me dar de volta os valores e  propósitos de  vida.

Hoje Eu Me Lembrarei
Valorizar minha vida.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('5eb91efb-ea8e-4123-8324-009c0316430e', 3, 31, 'Minha doença é diferente da maioria das outras doenças, porque a negação de que eu seja doente representa o seu principal sintoma. Entretanto, a  minha doença é caracterizada por recaídas como muitas outras doenças incuráveis. No Programa de Jogadores Anônimos tais recaídas são consideradas “fugas”.  A única coisa que tenho certeza é que somente eu posso causar a minha própria recaída.

Sempre me lembro de que o pensamento vem antes da ação? Procuro evitar o “pensamento ruim”?

Hoje Eu Rezo
Que Deus me dê a capacidade de resistir às tentações. Que eu reconheça que é somente minha a responsabilidade de não resistir e ter uma recaída. Que eu consiga prever com antecedência que estou me preparando para uma recaída ao culpar os outros, esquivando-me de minha responsabilidade, tornando-me mais uma vez uma pobre marionete do mundo. A volta às minhas velhas atitudes representa uma recaída  tanto quanto o ato de fazer uma aposta.

Hoje Eu Me Lembrarei
Ninguém é à prova de recaídas.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('914a5d14-a974-4f4a-b0ec-b1573f43c251', 7, 27, 'Cada vez mais eu vejo que aqueles que estão fazendo mais progresso dentro do Programa de Jogadores Anônimos, são aqueles que prontamente aceitaram a ajuda do Poder Superior dentro de suas vidas. No momento que conseguem fazer isso, acham que é mais fácil se afastar de seu antigo modo de vida. Seus problemas parecem que são resolvidos de uma maneira que vai além da compreensão humana.

Já entendi que a eficiência com a qual eu uso a minha consciência de Deus na minha vida diária não depende Dele, mas de mim?

Hoje Eu rezo
Que eu saiba que o meu crescimento e recuperação dependem de estar em contato com o meu Poder Superior, não só de vez em quando, mas sempre. Isso significa pedir a Ele várias vezes ao dia por força e pelo conhecimento de Sua vontade. Quando eu entender que a minha vida é parte de um Plano Maior, eu serei menos susceptível a esbarrar e cair, andar na direção errada, ou  apenas me sentar todo teso e assistir à vida passar.

Hoje Eu Me Lembrarei
Ter consciência de Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('94bdedc1-bb03-4d42-b332-c60f9d715a3f', 8, 3, 'Os Doze Passos foram criados especificamente para gente como nós, como um atalho para chegar a Deus. Os Passos são como um remédio que pode nos curar da doença do desespero, frustração e auto-piedade. Mesmo assim algumas vezes nós nos recusamos a usar os Passos. Por quê? Talvez porque tenhamos um desejo profundo de nos martirizarmos. Conscientemente e intelectualmente sabemos que precisamos de uma ajuda interna, mas, algum tipo de sentimento de culpa escondido nos faz ansiar por punição mais do que por alívio.

Posso tentar ser alegre quando tudo em volta parece me levar ao desespero? Entendo que o desespero geralmente é uma máscara para a auto-piedade?

Hoje Eu Rezo
Que eu possa arrancar a culpa secreta que me faz querer punir a mim mesmo. Que eu investigue o meu desespero e descubra que ele é na verdade um impostor – a auto-piedade usando uma máscara. Agora que eu sei que os Doze Passos podem trazer alívio, que eu possa usá-los ao invés de me chafurdar nos meus problemas.

Hoje Eu Me Lembrarei
Os Doze Passos são a escada de Deus', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('fd08a96c-ade3-4543-9fd6-e6410375caec', 12, 30, 'Antes de chegar a Jogadores Anônimos minha vida era parecida com a vida de muitos de nós que foram cruelmente massacrados e atormentados pelo poder de nossa adicção. Durante anos e anos continuei doente e cansado. Quando me tornei doente e cansado de estar doente e cansado, finalmente me rendi e procurei o Programa de Jogadores Anônimos. Agora sei que durante todo o tempo  recebi a ajuda de um  Poder Superior; na verdade foi esse Poder  que permitiu que eu vivesse, para que eventualmente, pudesse encontrar um novo modo de vida.

Descobri que encontrei um quinhão de serenidade no Programa de JA outrora desconhecido para mim?

Hoje Eu Rezo
Que eu me dê conta de que meu Poder Superior não surgiu em minha vida subitamente,  como um estranho abrindo a porta quando eu batia. Verei que esse Poder tem estado presente todo o tempo, se somente lembrar a quantos episódios de desastres sobrevivi por uma fração de segundo. Agora que comecei a conhecer melhor meu Poder Superior, sei que devo ter sido salvo por alguma coisa – talvez por ter ajudado outros como eu.

Hoje Eu Me Lembrarei
Sou grato por estar vivo e em recuperação.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('33431907-1543-4310-8811-4787174999dc', 10, 30, 'Quando estou motivado pelo meu orgulho – pela servidão a mim mesmo – torno-me parte ou completamente cego a minhas susceptibilidades e defeitos. Nesse momento, a última coisa de que preciso é conforto. Ao invés disso, preciso de um amigo compreensivo no Programa de Jogadores Anônimos – um amigo que sem hesitação abrirá um buraco através da parede que meu ego construiu, para que a luz da razão possa mais uma vez penetrá-la.

Tiro um tempo para rever meu progresso, para checar a mim mesmo em uma base diária, tentando prontamente consertar os meus erros?

Hoje Eu Rezo
Oro para que o grupo – ou somente um amigo – seja honesto o bastante para ver meus sinais  de recaída em meu orgulho,  e corajoso o bastante para falar disso comigo. Minha auto-estima ficou faminta por tanto tempo, que com os meus primeiros sucessos no Programa de Jogadores Anônimos, ela pode inflar a grandes proporções de satisfação pessoal. Que uma visão independente de mim mostre-me a visão correta de como estou lidando com o triunfo de minha abstinência – com uma gratidão humilde ou com orgulho.

Hoje Eu Me Lembrarei
A auto-estima ou satisfação pessoal?', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('d43a8af9-a99e-45b2-b2de-c26d6145e9e9', 5, 13, 'Quando uma pessoa acorda pela manhã sentindo-se infeliz e com os nervos à flor da pele, tendo que enfrentar uma realidade assustadora; quando uma pessoa tem dificuldades para enfrentar o  dia,  completamente desesperada, desejando morrer, mas recusando-se a fazer isso; quando uma pessoa acordo no outro dia e repete isso consecutivamente – bem, isso requer coragem. Isso requer um tipo de coragem de sobrevivência básica e real, uma coragem que pode ser usada para o bem se a pessoa conseguir encontrar o seu caminho para Jogadores Anônimos. Essa pessoa aprendeu a ter a coragem da maneira mais difícil e, quando ela chega ao Programa de Jogadores Anônimos,  encontra novas e melhores maneiras de utilizá-la.

Tenho coragem para  continuar tentando, um dia de cada vez?

Hoje Eu Rezo
Que eu use a “coragem para a sobrevivência”, o tipo de coragem que herdei de meus dias de jogo, fazendo bom uso dela no Programa. Se eu fui capaz de me “segurar” o bastante para suportar as desgraças de minha adicção, posso utilizar essa mesma vontade de sobreviver em meu programa de recuperação. Que eu use minha coragem de forma nova e construtiva.

Hoje Eu Me Lembrarei
Deus me preservou para ajudá-lo a realizar o Seu propósito.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('23f80428-8c7b-4d1f-8e49-435dff339d38', 3, 11, 'Desde que cheguei a Jogadores Anônimos, comecei a reconhecer minha antiga incapacidade de criar uma parceria verdadeira com outra pessoa. Parece que minha ego mania criou duas crateras desastrosas. Ou eu insistia em dominar as pessoas que conhecia, ou dependia demasiadamente delas. Meus amigos do Programa me ensinaram que minha dependência representava uma necessidade – a necessidade de posse e controle das pessoas e das condições à minha volta.

Eu ainda tento encontrar segurança emocional seja pela dominação ou sendo dependente dos outros?

Hoje Eu Rezo
Que eu primeiro recorra a Deus para satisfazer minha fome de amor, sabendo que tudo que Ele me pede é minha fé. Que eu não mais jogue redes emocionais sobre aqueles a quem amo, seja dominando-os ou sendo excessivamente dependente deles – o que não passa de uma outra forma de dominação. Que eu dê aos outros o espaço que necessitam para que sejam eles mesmos. Que Deus me mostre o caminho para relacionamentos humanos amadurecidos.

Hoje Eu Me Lembrarei
Ter fé no amor de Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('d7eaa7e9-a4f2-4c52-a61e-7573769232d1', 8, 14, 'O Quarto Passo permite que eu veja a mim mesmo como realmente sou – minhas características, minhas motivações, atitudes e comportamentos. Em Jogadores Anônimos aprendi a procurar meus erros com determinação.  Quando, por exemplo,  fui egoísta, desonesto, interesseiro ou medroso? Também aprendi que o meu forte hábito de me auto-justificar pode me tentar a achar “explicações” para cada erro que eu revele, culpando os outros por minhas próprias fraquezas.

Acreditarei que a honestidade pessoal pode alcançar o que o conhecimento maior  geralmente não consegue?

Hoje Eu Rezo
Que eu não trabalhe o Quarto Passo de maneira rápida e superficial  mas, com afinco e exercitando a auto-avaliação. Que eu me conscientize de que ao trabalhar este Passo, precisarei revê-lo várias vezes até que  se torne, da mesma forma que os outros onze Passos, um modo de vida para mim. Que eu dê o devido valor ao meu Quarto Passo distanciando-me de meu antigo hábito de me “auto-enganar” e transferir aos outros minhas responsabilidades.

Hoje Eu Me Lembrarei
A honestidade pessoal é o pavimento do caminho para a recuperação.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('f607b736-12d6-4803-8372-287589251787', 10, 28, 'Nunca parei de me surpreender com o fato dos tópicos das reuniões que freqüento parecerem ser tão apropriados à minha própria vida naquele momento específico. Só podemos nos tornar alunos na presença de um professor - e quando o aluno fica pronto, o professor aparecerá. Só podemos aprender se formos ensinados, e só podemos ensinar após termos aprendido. Passei a entender que, embora Deus seja o maior professor de todos, com muita freqüência aprendemos com os outros que Ele ensinou.

Aprendo quando ouço? Compartilho com os outros o que aprendo?

Hoje Eu Rezo
Que eu não me esqueça das lições mais importantes que aprendi em Jogadores Anônimos. Em JA  somos todos alunos, e somos todos professores. Que eu saiba que se não continuo a aprender, deve ser porque não estou disposto a ser um aluno. Se não estou compartilhando com os outros o que aprendo, devo estar precisando  me abrir a um maior aprendizado.

Hoje Eu Me Lembrarei
Se eu permanecer disposto a aprender , continuarei a encontrar professores.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('9e1d57c0-6f4d-4bff-a65b-5ef5ca64c91b', 1, 15, 'Não devo jamais me esquecer de quem sou, do que sou; e de onde venho. Devo me lembrar da origem de minha doença e de como eu era antes de meu ingresso em Jogadores Anônimos. Tentarei manter minha memória viva, sem perder meu tempo remoendo morbidamente o passado. Não terei medo de usufruir o que é belo, e de acreditar que, assim como eu dou aos outros, os outros darão a mim.

Posso me permitir o esquecimento de como era o passado, mesmo que por um minuto?

Hoje Eu Rezo
Que eu nunca esqueça dos sofridos dias de minha compulsão pelo jogo. Que eu jamais esqueça que a mesma desgraça espera por mim se voltar ao antigo comportamento. Que, ao mesmo tempo, essa visão do passado sirva somente para que eu reforce minha atual resistência e a de outros como eu. Por favor, Deus, não permita que eu traga essas lembranças de volta com o objetivo de suplantar meus companheiros com minhas “histórias de luta”. Assim como outros jogadores compulsivos, devo desconfiar de  minha vontade de ser a atração principal.

Hoje Eu Me Lembrarei
Realizo mais quando não tento “ser melhor” do que os outros.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('49766bd7-7092-4a32-aeb5-f7d7090cf528', 12, 28, 'Para mim o  Programa de Jogadores Anônimos não é somente um lugar seguro e uma filosofia, mas também uma rota para a liberdade. Esta rota me leva a meu objetivo espiritual de tornar-me a pessoa que desejo ser com o trabalho dos Passos. Algumas vezes essa rota não me leva ao objetivo tão rápido quanto gostaria, mas tento me lembrar de que Deus e eu trabalhamos em ritmos diferentes. Mas o objetivo está lá e eu sei que os Doze Passos de Recuperação me ajudarão a alcançá-lo.

Comecei a me dar conta de que eu – ou qualquer outra pessoa – posso  fazer agora o que sempre imaginei ser impossível?

Hoje Eu Rezo
A medida que pratico o Programa de JA, devo saber cada vez mais que ele representa um meio para um fim e não o próprio fim. Que eu tenha em mente que o tipo de espiritualidade que ele exige nunca se completará, mas é a essência da mudança e crescimento, a representação mais aproximada de um estado ideal. Que eu tenha cuidado em procurar não estabelecer prazos para a realização de meus objetivos buscando medir o meu progresso espiritual.

Hoje Eu Me Lembrarei
As agendas são invenções humanas.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('21a09198-5753-4714-b686-1345131b4dfa', 7, 26, 'Agora que estou familiarizado com o tripé sugerido pelo Programa de Jogadores Anônimos – Honestidade, Boa Vontade e Mente Aberta –  vejo as coisas sob um, novo prisma. De maneiras que eu jamais teria imaginado e certamente jamais esperado, passei a ver as coisas de forma muito diferente do que via antes de entrar para JA. Na maioria dos dias, sinto-me bem, raramente estou mal, e se estou, nunca é por muito tempo. Certamente não tão mal como eu costumava me sentir na maior parte do tempo.

O pior dos meus dias atualmente é infinitamente melhor do que o melhor dos meus dias de antes?

Hoje Eu rezo
Que eu me lembre de dizer “obrigado” ao meu Poder Superior, aos meus amigos do grupo e à Irmandade de Jogadores Anônimos como um todo, por me fazerem acreditar que as coisas realmente melhoram. Agradeço também por aquelas palavras amigas, pelos rótulos e slogans que freqüentemente entram na minha mente exatamente no momento que mais preciso deles, redefinindo meus propósitos, restaurando minha paciência, fazendo-me lembrar do meu Deus.

Hoje Eu Me Lembrarei
Como era antes.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('9fd5333f-acc9-4f0c-9f7c-f854d1e5c656', 7, 25, 'A linguagem usada em Jogadores Anônimos é aparentemente simples e clara. Mesmo assim, ela pode ter diferentes significados para diferentes pessoas, de acordo com suas próprias experiências e reações às palavras e às idéias. Por exemplo, o slogan: “Renda-se e deixe que Deus assuma”. Para algumas pessoas, isso pode sugerir que só precisamos ignorar os desafios que encontramos, e que de alguma maneira Deus irá fazer todo o serviço. Devemos lembrar que Deus nos dá o livre arbítrio, inteligência, e bom senso – é óbvio que Ele quer que usemos estes atributos. Se eu sou receptivo, Deus fará com que eu conheça a Sua vontade, passo a passo, mas sou eu que devo executá-la.

Algumas vezes ajo como se a minha rendição à vontade de Deus fosse um passaporte para a inércia?

Hoje Eu rezo
Que o meu “passaporte” seja carimbado e esteja pronto para me levar adiante. Que as minhas viagens sejam motivadas por desafios que eu prontamente reconheça como coisas a fazer, não como coisas para serem olhadas. Que eu possa fazer o melhor uso dos talentos recebidos de Deus, dos que eu já tenho consciência e dos que ainda estou por descobrir. Que eu não “me renda e desista”, continuando a aprender, crescendo, fazendo, servindo, rezando, aplicando a palavra de Deus como eu a entendo.

Hoje Eu Me Lembrarei
Deus quer que eu dê o melhor de mim.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('c588675b-945b-4046-8418-190b8456b605', 6, 28, 'Devagar, mas verdadeiramente, estou me tornando capaz de aceitar tantos os erros quantos as virtudes dos outros. O Programa de Jogadores Anônimos está me ensinando a sempre “amar o que há de melhor nas pessoas – e nunca temer o que há de pior nelas.” Esta não é de forma alguma uma transição fácil em função de minha antiga maneira de pensar, mas estou começando a ver que todas as pessoas – incluindo a mim mesmo – estão de alguma forma tão emocionalmente doentes quanto freqüentemente errados.

Estou começando a ver o que o amor verdadeiro realmente significa?

Hoje Eu Rezo
Que Deus me conceda a tolerância com todas as fraquezas, sinais de doença ou falta de sensibilidade dos outros, para que eu possa amar o que seja bom neles. Que Deus me ensine o verdadeiro significado do amor – que também precisa incluir a paciência. Que eu não ignore as faltas de quem amo, mas tente entendê-las.

Hoje Eu Me Lembrarei
Amor é compreensão.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('acdc04a4-daa0-4f53-82fe-45ead1aad4dc', 7, 20, '“É privilégio da sabedoria saber ouvir”, escreveu Olliver Wendell Holmes. Se eu tentar o mais que puder cultivar a arte de escutar – sem críticas e sem fazer julgamentos prematuros – haverá grandes chances que eu progrida mais rapidamente em minha recuperação. Se eu fizer o maior esforço possível para ouvir os sentimentos e pensamentos exprimidos – ao invés de simplesmente ser o “orador” – eu posso ser abençoado com uma idéia útil e inesperada. A qualidade essencial de bem ouvir é a humildade, a qual reflete a noção de que a voz de Deus fala mesmo através do mais inarticulado de Seus filhos.

A minha atitude daquele que sempre sabe mais, às vezes fecha minha mente às sugestões que  outros compartilham conosco?

Hoje Eu rezo
Que o meu Poder Superior me impeça de ser o “sabe-tudo” diante daqueles que têm maneiras, linguagem, pontos de vista diferentes do meu ou uma aparente falta de conhecimento, fazendo com que eu não dê atenção ao que dizem. Que eu possa sempre escutar a voz de Deus,  que pode ser ouvida através da fala de qualquer um de nós.

Hoje Eu Me Lembrarei
Ouça as palavras, não o orador.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('45eef22a-4a9d-41ce-9010-1b49b983baf2', 2, 28, 'Aprendemos com os companheiros de Jogadores Anônimos, que o principal agente ativador de nossos defeitos era o medo direcionado a nós mesmos – principalmente o medo de perdermos algo que tínhamos,  ou de que falharíamos na busca por algo que desejávamos. Quando vivíamos centrados nos desejos não satisfeitos, estávamos obviamente em estado de constante perturbação e frustração. Assim, aprendemos que não teremos paz a não ser que encontremos algum modo de reduzir essas exigências.

Tornei-me realmente disposto a aceitar que Deus remova todos os meus defeitos de caráter?

Hoje Eu Rezo
Que eu não faça exigências irrealistas em minha vida, porque devido a esses exageros, elas não poderão ser satisfeitas. Que eu não exija demais das outras pessoas, porque o não atendimento de minha expectativas me deixará desapontado e deprimido.

Hoje Eu Me Lembrarei
O desapontamento pode estar sendo preparado.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('58e1a85a-728c-4590-b207-79c2f20814c7', 6, 30, 'Aprendi no programa de Jogadores Anônimos que o truque, para mim, não é parar de jogar, mas continuar sem jogar e aprender a não jogar de novo. Deus sabe que eu tentei parar uma porção de vezes, dizendo a mim mesmo o quanto o jogo afetava não só a minha vida, mas todos os aspectos de meu comportamento. Jogar estava, na realidade, mudando quem eu parecia ser. Para continuar sem jogar, eu tive que desenvolver um programa de ações positivas e constantes. Eu tive que aprender a viver sem a adicção, cultivando novos padrões de comportamento, novos interesses e novas atitudes.

Permaneço  flexível na minha nova vida? Estou exercitando a minha liberdade para abandonar objetivos limitados?

Hoje Eu Rezo
Eu rezo para que a minha nova vida seja preenchida por novos padrões, novos amigos, novas atividades, novas maneiras de olhar para as coisas. Eu preciso da ajuda de Deus para preencher meu estilo de vida de maneira que ela acomode todas essas novidades. Também necessito algumas idéias próprias. Que a minha independência do jogo me ajude a fazer minhas escolhas com mente aberta, olhar aguçado e apreciativo.

Hoje Eu Me Lembrarei
Parar é Começar.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('94dd0f98-4ea1-4874-90e7-b10a7c670b3c', 1, 13, 'O Programa de Jogadores Anônimos e meus amigos na Irmandade, têm me proporcionado todo um novo conjunto de ferramentas para a vida. Se eu utilizar todas estas ferramentas regularmente,  e bem, elas também me ajudarão a livrar-me dos sentimentos negativos como a culpa, a ansiedade, a revolta e o orgulho.

Quando me sinto deprimido, uso as ferramentas que mostraram ser eficazes, ou trinco os dentes e sofro em doloroso silêncio?

Hoje Eu Rezo
Louvo o maravilhoso trabalho de meu Poder Superior por ter me concedido as ferramentas para a recuperação, após eu ter admitido minha impotência perante o jogo,  e ter me submetido à vontade de Deus, como eu O entendo. Agradeço pelo Doze Passos e pela fraternidade do grupo, que permite que eu me veja honestamente. Agradeço por todas estas palavras e frases, que se tornam, à medida que nós as compreendemos mais completamente, estandartes em nossa celebração da vida longe do jogo.

Hoje Eu Me Lembrarei
Transmitir aos outros as palavras que levam à recuperação.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('1e9535f6-3e82-4d03-8ccf-d0871fd31e4a', 8, 9, 'Em muitas ocasiões, encontrei uma forte relação entre meus medos e meus ressentimentos. Se eu secretamente sentir que sou inadequado, por exemplo, eu tenho a tendência de ressentir-me com qualquer pessoa ou coisa que coloque a minha inadequação à mostra. Mas geralmente é muito doloroso admitir que os meus próprios medos e dúvidas sobre mim mesmo são a causa dos meus ressentimentos. Ë muito mais fácil jogar a culpa no comportamento de outra pessoa, ou nos motivos “egoístas” dela – e usar isto como justificativa para o meu ressentimento.

Entendo que nutrir ressentimento por alguém permite que esta pessoa tome conta da minha cabeça?

Hoje Eu Rezo
Que Deus me ajude a superar meus sentimentos de inadequação. Que eu me conscientize de que quando repetidamente me coloco  um  ou dois níveis abaixo de uma outra pessoa não estou dando o crédito devido ao meu Criador, que deu a cada um de nós uma combinação valiosa e especial de qualidades. Assim, estou na  verdade mostrando minha insatisfação acerca do Plano Divino. Que eu avalie a minha antiga carga de ressentimentos e que isso sirva para meu próprio questionamento.

Hoje Eu Me Lembrarei
À medida que vou crescendo, vou acabando com meus ressentimentos.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('e8929311-88ca-4ef8-9271-2087312983e5', 3, 2, 'Por que não passar parte deste dia pensando no que possuo ao invés de minhas dívidas? Por que não pensar nas vitórias, ao invés das minhas derrotas e sobre como sou gentil e bom? A minha tendência tem sido sempre cair numa espécie de auto-hipnose hipócrita, criando adjetivos negativos para praticamente tudo que tenho feito, dito ou sentido. Só por hoje, passarei meia-hora em paz tentando criar uma perspectiva de vida mais positiva.

Tenho coragem de mudar aquilo que posso?

Hoje Eu Rezo
Que eu desenvolva, através do silêncio e de minha própria reavaliação, uma atitude mais positiva. Se sou filho de Deus, criado à sua imagem e semelhança, deve haver bondade em mim. Pensarei nessa bondade e nas maneiras como ela se manifesta. Evitarei me depreciar, mesmo nos meus mais secretos pensamentos. Respeitarei o que é de Deus. Respeitarei a mim mesmo.

Hoje Eu Me Lembrarei
Respeito próprio é respeito  a Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('a1fbfa8e-9216-462e-ae0e-5a0e968d9583', 3, 10, '“Precisamos ser verdadeiros por dentro, verdadeiros para conosco, antes de podermos conhecer a verdade que nos cerca&#8230;. Tornarmo-nos verdadeiros por dentro quando manifestamos a verdade como a vemos,” escreveu Thomas Merton em Ninguém é uma Ilha.  Como grande parte de nossa recuperação é baseada no progresso espiritual, é essencial que eu comece criando dentro de mim uma pequena área de verdade absoluta e amor, onde Deus poderá criar a sua raiz. À medida que eu aumentar essa área de verdade e amor, Deus crescerá dentro de mim. Quando cheguei a Jogadores Anônimos,  minha vida espiritual estava em um plano muito inferior.

Pelo menos por hoje, não permitirei que minhas compulsões se interponham entre mim e minha verdade interior?

Hoje Eu Rezo
Que eu reconheça a diferença entre a minha presente sensação de ter Deus dentro de mim, e o vazio que existia quando eu jogava. Ajude-me a compreender que minha recuperação espiritual só progredirá através da busca contínua e do confronto das verdades sobre mim mesmo.

Hoje Eu Me Lembrarei
A verdade interior permite a entrada de Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('bad3e4e6-a532-4f39-a33b-50e7ed3b8b6e', 9, 27, 'No passado, mesmo na idade adulta, muitos de nós infantilmente desejávamos que as pessoas nos protegessem, defendessem e cuidassem de nós. Agíamos como se o mundo devesse uma vida a NÓS. E aí, quando as pessoas que mais amávamos não agüentavam mais, deixavam-nos de lado ou talvez nos abandonavam completamente, ficávamos desnorteados. Não conseguíamos ver que nossa super-dependência das pessoas falhava porque todos seres humanos são sujeitos a falhas; mesmo o melhor deles irá algumas vezes nos decepcionar, principalmente quando nossas exigências forem absurdas. Em contraste, hoje, dependemos de Deus, contamos com Ele, ao invés de exclusivamente de nós mesmos ou de outras pessoas.

Tento fazer o que acho que Deus esperaria que eu fizesse, confiando nos desígnios de  Sua vontade?

Hoje eu Rezo
Que eu saiba, que com base nas dependências do passado, sou uma pessoa dependente. Como eu dependia do jogo compulsivo, também tinha a tendência a depender de outras pessoas, esperando mais do que elas poderiam me dar. Que eu, pelo menos, passe dessas dependências adolescentes para uma dependência madura e saudável ao meu Poder Superior.

Hoje Eu Me Lembrarei
Não tenho somente uma dependência. Tenho mais do que isso.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('0550159f-faec-401d-ac45-d17ffa3b1001', 5, 15, 'Relembrando aqueles últimos e desesperados dias antes de chegar a Jogadores Anônimos, lembro-me,  mais do que qualquer outra coisa,  da sensação de solidão e isolamento. Mesmo quando estava cercado de pessoas, incluindo minha própria família, a sensação de solidão era insuportável. Mesmo quando tentava agir de forma sociável, usando a máscara do entusiasmo, eu geralmente sentia uma raiva terrível por não conseguir me situar.

Será que um dia esquecerei a agonia de sentir-me como se estivesse “sozinho na multidão”?

Hoje Eu Rezo
Agradeço a Deus por essa especial grande alegria  que sinto graças à minha abstinência do jogo – a sensação de não estar mais sozinho. Que eu não espere que a solidão desaparecerá da noite para o dia. Que eu perceba que haverá algum momento de solidão ao longo de minha recuperação, principalmente porque estarei afastado de meus antigos companheiros de jogo. Rezo para que  encontre novos amigos em recuperação. Agradeço a Deus pela Irmandade de Jogadores Anônimos.

Hoje Eu Me Lembrarei
Não estou só.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('766aa5a1-3bb8-4f45-9f15-3588f9878763', 11, 6, 'Não há limites para a meditação. Ela não pode ser medida em termos de largura, profundidade ou altura. Isso significa que ela sempre poderá ser aprimorada, sem qualquer limitação. A meditação é pessoal; poucos de nós meditam da mesma maneira; e neste sentido ela é uma verdadeira aventura pessoal. Entretanto, para aqueles dentre nós que levam a meditação a sério, o propósito é um só: melhorar nosso contato consciente com Deus. Apesar de sua  ausência de propósitos específicos, ou de sua tangibilidade, a meditação é, na verdade, a coisa mais intensamente prática que podemos fazer. Uma de suas primeiras recompensas, por exemplo, é o equilíbrio emocional. O que poderia ser mais prático do que isso?

Estou ampliando e aprofundando o canal de comunicação entre Deus e mim?

Hoje Eu Rezo
À medida que procuro por Deus através da oração diária e da meditação, que eu encontre a paz que transmite o entendimento, esse equilíbrio que me assegura uma perspectiva para toda a vida. Que eu me centralize em Deus.

Hoje Eu Me Lembrarei
Meu equilíbrio vem de Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('74bd57d2-8da3-4fad-a93b-f528aa2c138e', 3, 18, 'Nos velhos dias, costumávamos ter experiências tão devastadoras que jurávamos com fervor – Nunca mais. Éramos totalmente sinceros naqueles momentos de desespero. Mesmo assim, apesar de nossas boas intenções, o resultado era inevitavelmente o mesmo. Eventualmente, a memória de nosso sofrimento se apagava, assim como a memória de nossas “promessas”. Então fazíamos tudo de novo, terminando em um estado ainda pior do que aquele do momento em que fizemos o nosso último “juramento”. “Para sempre” passou a ser somente uma semana, um dia, ou menos. No Programa de Jogadores Anônimos, aprendemos que só precisamos nos preocupar com o dia de hoje, este período específico de vinte e quatro horas.

Vivo minha vida somente vinte e quatro horas de cada vez?

Hoje Eu Rezo
Que as frases que exijam compromissos de longo prazo, como “nunca mais”,  “nunca em minha vida”, “para sempre”, ou “nunca mais farei uma aposta”, não enfraqueçam minhas decisões. Quando a promessa do “para sempre” é fragmentada em dias únicos – ou até mesmo em simplesmente partes dos dias – ela não parece ser tão impossivelmente longa. Que eu acorde todos os dias com o meu objetivo programado de maneira realista em períodos de somente vinte e quatro horas.

Hoje Eu Me Lembrarei
Vinte e quatro horas de cada vez.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('0c72c953-4ac8-4b35-b84a-61c292b2245a', 6, 8, 'Um amigo em Jogadores Anônimos me ensinou a ver a culpa em excesso de uma maneira completamente nova, sugerindo que a culpa não é nada mais do que um tipo de orgulho ao contrário. Segundo ele, um arrependimento devido pelo que aconteceu é aceitável, mas a culpa não. Com base nisso aprendi que nos condenarmos pelos erros passados é tão ruim quanto condenar os outros por seus erros. Na verdade não estamos preparados para fazer julgamentos,  nem de nós mesmos.

Algumas vezes “açoito a mim mesmo até a morte” quando parece que estou errando?

Hoje Eu Rezo
Que eu esteja atento para não manter uma atitude de culpa no presente, quando já deveria tê-la relegado ao passado há muito tempo. Que eu saiba a diferença entre arrependimento e culpa. Que eu reconheça que a culpa a longo prazo pode implicar em uma noção exagerada de minha própria importância, bem como uma atual necessidade de auto-correção. Que somente Deus seja o meu juiz.

Hoje Eu Me Lembrarei
A culpa pode ser o orgulho ao inverso.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('ac87e75a-bbbf-4a14-9b9f-9a592303c26a', 11, 29, 'A contrário do que algumas pessoas pensam, o ditado “Deixe que as coisas aconteçam, deixe à mercê Deus” não é um sinal de apatia, uma atitude de derrota ou uma falta de disposição para assumir responsabilidades. Aqueles que dão as costas aos seus problemas não estão “deixando que as coisas aconteçam ou fiquem à mercê de Deus”. Na verdade estão ignorando seu comprometimento para agir guiados pela inspiração e orientação de Deus. Essas pessoas não pedem e nem esperam por ajuda; elas querem que Deus faça tudo por elas.

Ao procurar a orientação de Deus, sei  realmente que a responsabilidade final é minha?

Hoje Eu Rezo
Que eu não me transforme em uma pessoa preguiçosa simplesmente porque acho que, de qualquer maneira,  Deus irá fazer tudo. (Essa apatia me faz lembrar da antiga pessoa impotente que eu era, alguém que lamentava que o mundo acabaria em fumaça, que a civilização estava indo para o brejo e que nada poderia se feito com relação a isso). Também não devo usar a frase  “deixe à mercê de Deus” como desculpa para ignorar meu problemas sem ao menos tentar resolvê-los. Que Deus seja a minha inspiração; que eu seja o Seu instrumento.

Hoje Eu Me Lembrarei
Deus ajuda a quem se ajuda.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('12a1c84f-15cb-4b41-962c-32bc484c8d9c', 2, 8, 'Quando paramos de jogar pela primeira vez, foi um grande alívio descobrir que as pessoas que encontramos em Jogadores Anônimos pareciam bem diferentes daquela massa aparentemente hostil que conhecíamos como “eles” . Deparamo-nos, não com críticas e suspeitas, mas com compreensão e interesse. Entretanto, ainda encontramos pessoas que nos irritam, tanto dentro como fora da Irmandade. Obviamente, precisamos começar a aceitar o fato de que existem pessoas que às vezes dizem coisas com que discordamos ou fazem coisas de que não gostamos.

Estou começando a ver que aprender a conviver com diferenças é essencial para minha tranqüilidade e , ao mesmo tempo, para minha recuperação contínua?

Hoje Eu Rezo
Que eu reconheça que as diferenças entre as pessoas é que fazem o mundo girar e aceite as pessoas que “me tiram do sério.” Que eu compreenda que é preciso dar a elas o espaço necessário e que minhas atitudes hostis para com os outros podem ser vestígios dos dias ruins quando eu tinha tendência  a ver os outros como se estivessem todos mobilizados contra mim.

Hoje Eu Me Lembrarei
Aprenda a viver com as diferenças.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('a4d0360b-c7e4-4ed7-aa1b-f8751c3f645c', 3, 12, 'Se examinarmos cada uma de nossas perturbações,  grandes ou pequenas, descobriremos nelas as raízes de algumas das dependências doentias, e  suas resultantes urgências doentias. Que, com a ajuda de Deus, possamos superar continuamente essas obrigações castradoras. Estaremos desta forma, livres para viver e amar. Poderemos então ser capazes de praticar tanto o Passo Doze quantos os outros,  alcançando o bem-estar emocional.

Tento transmitir a mensagem do Programa de Jogadores Anônimos?

Hoje Eu Rezo
Que eu coloque primeiro a minha casa emocional  e espiritual em ordem,  antes de procurar e assumir compromissos sérios nos relacionamentos humanos. Que eu examine longa e minuciosamente minha “dependência” - da emoção do jogo e de outras pessoas – e a reconheça como a raiz de minha inquietação. Que eu transfira minha dependência para Deus, como eu O concebo.

Hoje Eu Me Lembrarei
Eu sou dependente de Deus.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('a872ce7f-20d6-475a-8ceb-9314ed8cf143', 1, 5, '- Hoje é meu dia de sorte. Quantas vezes dissemos isso no passado, tornando-se uma frase em vão. Hoje,  estas palavras fazem sentido; estou tendo uma segunda chance. Em meus dias de jogo,  eu sacrificava todo o “hoje” pelo sonho de algum amanhã distante. De tudo que  perdi, lamento mais por todos aqueles “hojes” – não posso trazê-los de volta. Mas hoje – este dia – eu possuo. Não o sacrificarei nem o desperdiçarei.

 Será que realmente acredito, que o dia de hoje é meu, que hoje posso optar por ser feliz, crescer e aprender a viver, ao invés de ficar contando  com um pássaro voando no céu em um futuro distante?

Hoje Eu rezo
Rezo para que as cores deste dia não sejam ofuscadas pelos devaneios mudos do futuro ou apagadas pelos vestígios da  tempestade sombria  do passado. Rezo para que meu Poder Superior me ajude a escolher minhas ações e pensamentos independente da abundância de possibilidades que cada dia oferece.

Hoje Eu Me Lembrarei
Não perderei o hoje se optar por ele.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('82043fc2-ff78-4572-9a64-059b139a087c', 9, 26, 'Tudo que esperamos é a libertação do jogo compulsivo a partir de um despertar espiritual? É claro que não. A libertação de nossa compulsão é apenas um simples começo; é a nossa primeira dádiva de nosso crescimento inicial. Obviamente, se tivermos que receber maiores dádivas, nosso despertar precisará continuar. À medida que esse despertar realmente continua, descobrimos que poderemos superar a antiga vida – a que não funcionou -  lenta, porém certamente, por uma nova vida que possa funcionar e funcione sob quaisquer condições.

Estou disposto a continuar me despertando através da prática dos Doze Passos de Recuperação?

Hoje Eu Rezo
Que eu me lembre de como eu era quando meu único objetivo na vida era ficar livre da compulsão do jogo. Todas as palavras e frases que eu usava eram freios – “desistir, abandonar, me desligar” No momento em que me libertei, comecei a ver que minha liberdade dependia mais de um “começo” do que da “parada”. Que eu agora continue a pensar em termos de “inícios” - “expandir, despertar, crescer, aprender, tornar-se &#8230;”

Hoje Eu Me Lembrarei
Minha parada foi o ponto de partida.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('96c4e451-3a58-492b-a7f6-18bb4f9a9b1b', 8, 7, 'O que podemos fazer sobre os nossos ressentimentos? A experiência nos demonstra que a melhor coisa a fazer é listá-los, descrevendo as pessoas, instituições ou princípios que são objetos de nosso ressentimento ou raiva. Quando listo os meus ressentimentos e me pergunto o porquê de estar ressentido, descubro que na maioria dos casos a minha auto-estima, minhas finanças, minhas ambições ou minhas relações pessoais foram atingidas ou ameaçadas.

Algum dia vou aprender que a pior coisa para o meu ressentimento é ficar eternamente ensaiando a vingança?

Hoje  Eu Rezo
Que Deus me ajude a encontrar uma maneira de me livrar dos meus ressentimentos. Que eu desista de ficar horas maquinando pequenas peças, nas quais eu sou o ator principal e represento um homem ou mulher que inteligentemente arrasa com aqueles que me ameaçaram. Já que estas peças jamais serão produzidas, que eu então liste os meus ressentimentos e descubra a causa de cada um. Que isto sirva para me livrar deles.

Hoje  Eu  me Lembrarei
Ressentimentos causam violência; ressentimentos causam doenças em pessoas não violentas.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('597419de-bbff-40c2-b53b-1a66a5bf90a0', 1, 21, 'Toda pessoa é parte da economia Divina. Somos todos filhos de Deus, e é pouco provável que Ele queira favorecer alguns mais do que outros. Assim, é preciso que todos  aceitemos com grande humildade todas as dádivas positivas que nos são legadas. Temos que ter sempre em mente que nossas atitudes negativas foram necessárias para que fôssemos levados a um estado em que, através da experiência da conversão, ficássemos prontos para a dádiva das atitudes positivas.

Aceito o fato de que minha adicção e o fundo do poço a que cheguei são o alicerce para a concretização de minha espiritualização?

Hoje Eu Rezo
Que eu saiba que o Poder de Deus foi concedido a mim a partir do instante em que admiti minha impotência. Cada passo a partir do momento de derrota foi um passo na direção correta. O Primeiro Passo é um passo enorme. Apesar de ser dado geralmente em desespero, que eu perceba que é preciso estar totalmente destituído de esperança para poder tê-la renovada, estar minado de força de vontade antes de poder sentir a vontade de Deus.

Hoje Eu Me Lembrarei
O poder é alcançado através da falta de poder.', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.reflexao_dia (id, mes, dia, texto, criado_em, atualizado_em) VALUES ('7360a08c-a8e3-4396-998a-ac12f567c46a', 9, 25, 'Por sugestão de um veterano em Jogadores Anônimos, comecei a fazer “inventários de recuperação” periodicamente. Os resultados me mostraram – clara e indubitavelmente – que as promessas do Programa de  JA se concretizaram para mim. Não sou mais a pessoa doente que era no passado; não estou mais na falência em todas as áreas; tenho uma nova vida e um caminho a trilhar;  e estou em paz comigo mesmo a maior parte do tempo. Isso está muito distante do período de minha vida em que eu ficava apavorado ao ter que enfrentar cada novo dia. Talvez todos nós devêssemos escrever inventários de recuperação de tempos em tempos, mostrando como o Programa está funcionando  para cada um de nós.

Só por hoje, tentarei substituir o medo pela fé?

Hoje Eu Rezo
Deus, que eu compare minha nova vida com a antiga – para que eu simplesmente veja como as coisas estão diferentes para mim. Que eu ocasionalmente escreva relatórios para mim mesmo, e mais tarde – para os que são mais novos em JA. Que esses relatórios mostrem o - entusiasmo – sobre “o que estou fazendo” ao invés de – presunção – sobre “ o que realizei”.

Hoje Eu Me Lembrarei
O Programa manteve sua promessa? E eu – mantive a minha?', '2026-07-17 21:56:00.04228+00', '2026-07-17 21:56:00.04228+00');
INSERT INTO public.relato (id, texto, nome_exibicao, status, autor_id, criado_em, atualizado_em) VALUES ('99dc0f13-2222-47cd-a742-badd0261c788', 'RENASCIDO DAS CINZAS

Em 1986, quando eu tinha apenas 17 anos de idade, joguei pela primeira vez e ganhei com uma única cartela de bingo um valor muito alto em dinheiro, em uma festa japonesa chamada Kaikan em Guarulhos, nossa foi uma felicidade tão grande, que pulava de alegrias, quando cheguei em casa e entreguei todo dinheiro a minha mãe. Desconfiada ela foi até o local para confirmar se era verdade, o dinheiro deu para comprar alimentos e roupas para familiar toda.

Era o inicio da minha compulsão, pois naquela época comecei a jogar baralho, dominó, loterias em geral com apostas pequenas, que foi aumentando com o tempo. O meu primeiro fundo de poço foi no ano 2000, quando perdi praticamente tudo na minha vida, um carro semi novo, uma dívida enorme em bancos, separação do primeiro casamento e desempregado. Foram momentos difíceis, pois fiquei muito deprimido e com pensamentos de tirar a minha própria vida.

No ano de 2002, o Presidente da empresa em que eu tinha trabalhado me deu uma nova oportunidade de emprego e no ano de 2003, voltei a ter a carteira registrada e prometi para mim mesmo que nunca mais voltaria a jogar. Aquele foi ano mais importante da minha vida, pois voltei ao trabalho com muita disposição e tive muita progressão. Ganhei muito dinheiro e paguei todas as minhas dívidas, mesmo não conhecendo os Jogadores Anônimos, fiquei noivo novamente e a vida começou a melhorar muito. Mas em 2005, tive uma recaída e voltei ao tormento dos jogos. Em junho de 2007, a coisa ficou muito séria, pois tinha pedido para sair da empresa, com ilusão para trabalhar em outra empresa e ganhar mais dinheiro, mas a mudança demorou horrivelmente 6 meses e nesse tempo gastei todo o meu dinheiro da rescisão e FGTS no jogo. Fiquei praticamente sem dinheiro e desesperado e comecei pedir dinheiro emprestado para parentes e amigos para viajar, após fazer ligações para o meu futuro chefe.

Chegando na nova cidade, a empresa alugou uma casa e eu tive que fazer uma divida de R$ 4.100,00 no cartão de crédito para comprar alguns móveis, mesmo sabendo que não poderia pagar e nem tinha recebido o primeiro salário ainda. Nesta cidade eu não jogava, pois tinha medo de ser descoberto pelo meu chefe, pois a cidade é muito pequena e todos se conheciam, por isso não tive interesse em procurar casa de jogos. Mas quando ia visitar a minha noiva na época, a primeira coisa que eu fazia era procurar uma casa de jogos antes de chegar ao meu destino. Foi durante uma das minhas visitas em abril de 2008 que a minha noiva ficou grávida, para piorar ainda mais a minha situação financeira. Fiquei assustado com a noticia que eu iria ser pai pela primeira vez e fiquei pensando como seria a minha vida após o nascimento da minha filha, pois era um jogador compulsivo e não tinha controle sobre mim quanto mais de uma criança inocente que estava prestes a chegar ao mundo.

A partir deste momento as mentiras aumentaram, dizendo para a mãe da minha filha que eu tinha uma reserva de R$ 10.000,00, mas na verdade era o contrário, pois eu estava devendo mais R$ 30.000,00 na praça. Chegando o dia 19 de janeiro de 2009, nasceu a menina mais linda deste mundo, pois já tinha pedido para colocar o nome dela de Vitória. A noiva, mesmo com raiva, aceitou, pois naquele momento, ela já estava sabendo que eu era um jogador compulsivo e não tinha nada de dinheiro. Diante disso fiz outra loucura e pedir a minha dispensa da empresa no interior, para acompanhar o crescimento da Vitoria e pagar as dividas do hospital particular. Naquela ocasião paguei a entrada com o dinheiro que eu tinha emprestado do meu pai e depois um amigo empresário meu deu R$ 1.000,00 de presente e deixei 4 cheques pré datados para quitação das demais despesas. Naquele dia saí do Hospital, sem saber o que iria fazer da minha vida, como iria pagar as dividas, desempregado, sem crédito na praça, mas nessa data ainda joguei no jogo do bicho os números da hora que a minha filha nasceu e joguei baralho também.

Após tudo isso no ato do meu medo do futuro, pedi ao meu amigo um trabalho e um local para morar, pela nossa amizade. Ele disse que iria me ligar, pois estava abrindo uma micro empresa de materiais importados. Na semana o mesmo me ligou, convidando para ir até o local onde eu iria trabalhar e morar, num sitio isolado em Itaquá, chegando já tinha pedreiros e ajudantes trabalhando, demolindo algumas partes de uma casa antiga, para reconstrução do depósito de materiais, local onde eu iria trabalhar e morar numa pequena casa ao lado. Imediatamente já comecei a trabalhar como ajudante para reforma da casa. Durante a demolição encontramos vários compartimentos de vidros com dezenas de cobras mortas e mantida em um liquido, de cara fiquei assustado, pois tinha muito medo de cobras, ai pensei será que essas cobras foram pegas no mato que ficava atrás da casa. Um dos trabalhadores confirmou a minha dúvida, ai o meu coração começou acelerar, pois iria morar naquele local sozinho e sem vizinhos. Morei ali, isolado, durante 1 ano e graças a Deus, por um milagre, não fui picado pelas cobras que encontrei durante a minha estadia naquele lugar.

Durante o mês de maio de 2009, eu estava assistindo TV no programa "Casos de Família" e a história daquele dia era sobre um jogador compulsivo que deixava seus filhos passar fome e logo fiquei interessado e assistir até o final. No final o psicólogo do programa informou a Irmandade de Jogadores Anônimos, passando o site e o telefone do escritório do JA. Anotei o numero e no outro dia liguei para o escritório e não tinha ninguém para atender naquele momento, foi quando deixei o recado na secretaria eletrônica no outro dia, ligou uma pessoa atenciosa que hoje chamo de amigo e irmão, o mesmo informou os dias das reuniões do grupo Armênia e o grupo São Miguel Paulista (atual Arthur Alvim). Mesmo assim continuei a jogar. Chegando dia 31/05/2009 (domingo) "dia em que caiu o avião da Air France", foi um dia muito difícil, pois tinha perdido todo dinheiro que tinha emprestado para pagar a ultima parcela do hospital. Fiquei desesperado, chorei muito naquela noite e pensei em tirar a minha própria vida e não consegui dormir. Quando amanheceu o dia tomei uma decisão de procurar ajuda, fui procurar o grupo de JA em São Miguel Paulista na zona Leste, quando cheguei as portas estavam fechadas, fiquei pensando o que faria naquele momento, de repente veio o rapaz dizendo que a reunião do JA seria na quarta feira, voltei para casa triste, mas pensando em voltar e no dia 03/06/2009 cheguei ao grupo totalmente arrasado emocionalmente, moralmente, financeiramente, profissionalmente e familiar. Quando começou a reunião e o coordenador daquela noite me apresentou e disse "Você é a pessoa mais importante da sala" não me contive e comecei a chorar muito e a cada depoimentos que eu ouvia, escutava a mesma frase. Foi quando naquele instante, eu decidi que ali seria o lugar que salvaria a minha vida. Aceitei o programa de imediato e Só Por Hoje, pois no dia 03/06/2020, completei 11 anos de abstinência. Nesse período, consegui pagar 90% das minhas dividas com bancos, agiotas, familiares e amigos, voltei trabalhar no mesmo setor profissional como encarregado, consegui financiar um carro zero. Eu sei, se não estivesse encontrado o JA, muito provavelmente eu teria virado um mendigo, um criminoso atrás das grades e o mais certo de todos seria a morte.

Agradeço profundamente do meu coração a Deus, todos os companheiros e companheiras de JA e JOG ANON e especial a minha filha por ter me dado inspiração para me transformar em uma nova pessoa com muita esperança, prosperidade e felicidade.

24 HORAS DE PAZ, SERENIDADE E ABSTINÊNCIA.', 'Anônimo', 'approved', '00000000-0000-0000-0000-000000000002', '2026-07-17 21:49:55.3483+00', '2026-07-17 21:49:55.3483+00');
INSERT INTO public.relato (id, texto, nome_exibicao, status, autor_id, criado_em, atualizado_em) VALUES ('6df4e075-ee49-4385-9222-5e520b605e3f', 'Uma luz no fim do túnel

Foi através de um programa de TV que cheguei arrastado como uma cobra, ao JA, em 03/06/2009, completamente arrasado moral e financeiramente e, com pensamento de tirar a minha própria vida.

Chegando à sala fui recebido por um dos irmãos que me explicou o que significava aquela sala de Jogadores Anônimos. Logo em seguida começaram os depoimentos e, logo me dei conta que as histórias faladas eram iguais às minhas e que, aquele lugar seria a única luz na escuridão para a minha vida. Hoje, estou completando 1 ano, 2 meses e 10 dias abstinente e vivendo um dia de cada vez para não cair no abismo novamente. Agora estou colhendo as coisas ruins que eu plantei no passado durante a trajetória de jogador compulsivo. Mas, se depender de mim e se eu conseguir seguir os Passos de JA, em breve colherei bons frutos.

Atualmente estou seguindo assiduamente o décimo segundo passo de recuperação para levar a mensagem aos jogadores compulsivos e outros adictos que sofrem.

Muito obrigado aos irmãos dos grupos de JA pela força que transmitem! Sei que a estrada é cheia de espinhos e de obstáculos, mas, durante a caminhada encontraremos flores.

A todos, eu desejo 24 horas de paz, serenidade e abstinência.

EFS – São Paulo – Enviado em agosto de 2010.', 'EFS – São Paulo', 'approved', '00000000-0000-0000-0000-000000000002', '2026-07-17 21:49:55.3483+00', '2026-07-17 21:49:55.3483+00');
INSERT INTO public.relato (id, texto, nome_exibicao, status, autor_id, criado_em, atualizado_em) VALUES ('fe1d2826-f726-4796-8fc4-63f3a35b4584', 'É preciso eliminar defeitos de caráter

Tinha uma dificuldade grande em entender e perceber a importância da prática dos passos de recuperação.

Até que em um determinado momento, muito incomodado com o vício do cigarro, comentei com um companheiro de outra irmandade de Anônimos sobre esse incômodo. Ele simplesmente me disse: Aplique o 6º passo de recuperação!

A partir desse momento, eu li e reli várias vezes o 6º passo, tentando entender e ver como colocar em prática; encarei como um defeito que tinha que ser removido, falei a respeito com um companheiro, então senti que estava pronto para livrar-me do defeito. Era questão de tempo. Eu não só consegui livrar-me desse defeito, como também, consegui ter mais clareza sobre o 6º passo de recuperação.

Apesar de ter muitos defeitos de caráter que ainda precisam ser removidos, no processo de recuperação eles vão se tornando empecilhos em nossas vidas. Concentrar esforço na prática do 6º passo significa caminhar em direção ao crescimento espiritual.

Mateus – São Paulo – Enviado em julho de 2010.', 'Mateus – São Paulo', 'approved', '00000000-0000-0000-0000-000000000002', '2026-07-17 21:49:55.3483+00', '2026-07-17 21:49:55.3483+00');
INSERT INTO public.relato (id, texto, nome_exibicao, status, autor_id, criado_em, atualizado_em) VALUES ('7535e58f-c0f5-45fb-bb79-6e9dfb1cf96c', 'CINCO ANOS… 20/06/2016

Meu nome é R., jogador compulsivo em recuperação a exatos cinco anos! Agradecido ao Meu Poder Superior que compreendo como Jesus Cristo, minha Família, Irmandade e Amigos.

Na condição de compulsivo e já compreendendo que só posso viver um dia de cada vez, não ouso a me questionar se cinco anos já é um bom tempo para me sentir mais forte em relação a minha doença. Prefiro manter minha vigilância atenta ás armadinhas emocionais que a vida me apresenta, pois, hoje me defino e aceito que tenho instalado em mim uma grave enfermidade, que fazendo movimentos contrários com tudo que aprendi durante esse período, me dará a paz buscada de uma vida normal, coisa que a escravidão pelo vício me tirou durante anos.

Quando falo que DEVAGAR SE VAI AO LONGE é porque vivi muito tempo o contrário dessa frase, vivia numa velocidade sem destino, sem respeito, sem obediência, enfim… sem Vida! Hoje enfrento enormes dificuldades em muitos setores de minha caminhada, mas o importante mesmo é que tenho consciência que preciso ficar de frente com as situação e usando a honestidade e certo que sempre existirão problemas, e meu crescimento como ser humano e espiritual serão compensados.

Meus dias sem fazer apostas são contados com entendimento, pois se for dar uma passadinha no meu passado verei o quanto foi difícil dar o PRIMEIRO PASSO, ficar um dia sem apostar? Nunca imaginei que conseguiria, não acreditava que houvesse essa chance, não tinha força, não tinha Fé! Por isso que sou vigilante, sou atuante em minha Irmandade da forma que posso, hoje posso dizer que conheci a Imundice e hoje vivo em lençóis limpos.

O jogo em "parte de minha Vida" foi progressivo como é todo vício, a ferida emocional, física, financeira e espiritual seguirá aberta por muito tempo, mais farei os curativos necessários para que também um dia de cada vez seja fechado, e sempre aceitando minhas imperfeições com consciência e usar minha boa vontade pra sempre estar com a mão aberta para ajudar quem sofre nas garras dessa maldita doença.

A Irmandade de Jogadores Anônimos é o melhor hospital e os membros os melhores médicos. Lá que encontro meu passado e meu presente, lá onde faço minhas escolhas, lá onde tomo meu medicamento, é lá que verbalizo minhas dúvidas e recebo meus diagnósticos.

A minha Gratidão é tamanha que não achei a palavra assertiva pra pronunciar!

Obrigado Poder Superior, Família, Irmandade e Amigos!

R.', 'R. – 2016', 'approved', '00000000-0000-0000-0000-000000000002', '2026-07-17 21:49:55.3483+00', '2026-07-17 21:49:55.3483+00');
INSERT INTO public.relato (id, texto, nome_exibicao, status, autor_id, criado_em, atualizado_em) VALUES ('53dc7787-31c1-4f97-a832-86deb8f7f03a', 'O Programa de Recuperação de JA

Meu nome é Paula. Sou uma jogadora compulsiva em recuperação.

Cheguei a esta irmandade em 1997, arrasada moral, emocional e financeiramente. Tinha perdido completamente o controle sobre a minha vida.

Minha recuperação não foi fácil, pois tudo estava muito confuso dentro de mim. Já não sabia o que mais me atormentava, se o sofrimento moral ou se as perdas materiais, pois cheguei quando havia acabado com todas as reservas feitas durante uma vida. Cheguei para parar de jogar, mas encontrei um Programa que não era só para parar de jogar! Encontrei um Programa espiritual profundo. Foi difícil parar de jogar, por ter demorado a entender o Programa. Brigava muito com as perdas financeiras.

Só entrei no Programa quando admiti as perdas materiais. E entreguei minha vida a um Poder Superior, que para mim é Deus. Só então entrei em recuperação e consegui parar de jogar e resgatar minha vida e meus antigos valores.

Quando cheguei logo nas primeiras reuniões, depois de desabafar e colocar para fora todo o lixo que estava pesando e me incomodando, quis logo praticar o 12º Passo. Apaixonei-me por ele logo na primeira noite em que li toda a apostila. Ledo engano. Na verdade eu queria pular todos os outros passos, e ir logo praticar o último. Só Deus sabe quantas vezes fui do 1º ao 12º Passo, sem ter de fato feito o 1º Passo. Foi minha filha que nunca frequentou uma sala de J.A. ou Jog Anon, que me alertou: Sabe por que você recaiu? Porque você foi do 1º ao 12º Passo, mas ainda não aceitou o 1º. Estava certa, mas, continuei insistindo.

Passei por muitas dificuldades emocionais. Aqui descobri como o meu emocional sempre esteve mal, e como isto me levava às recaídas. Mas, com a ajuda de Deus e dos irmãos e irmãs em JA, fui encontrando o equilíbrio.

Sou profundamente agradecida aos irmãos e irmãs em JA: Aos que permaneceram na irmandade e aos que só passaram por ela e não permaneceram; mas, muito contribuíram com seus depoimentos para a minha recuperação. Penso nessas pessoas e Rogo a Deus por elas.

Graças a todos aprendi a conjugar o verbo na 1ª pessoa do plural, pois cada irmão ou irmã que chega a JA faz parte de nós, pois somos uma irmandade. Como uma irmã costuma dizer; os irmãos se desentendem, brigam, mas se algum precisar, os irmãos estão sempre presentes para ajudar.

Desejo a vocês, meus irmãos e irmãs em JA, o mesmo que desejo para mim: vinte e quatro horas de Paz, Serenidade e Abstinência.

Aqui encontrei Deus e com Deus encontrei a Paz, Serenidade e mantenho minha abstinência do jogo

PAULA – São Paulo – Enviado em fevereiro de 2009.', 'Paula – São Paulo', 'approved', '00000000-0000-0000-0000-000000000002', '2026-07-17 21:49:55.3483+00', '2026-07-17 21:49:55.3483+00');
INSERT INTO public.relato (id, texto, nome_exibicao, status, autor_id, criado_em, atualizado_em) VALUES ('f04a11b3-2464-4a0d-8abf-d1c26ae274ff', 'Sou (R), 47 anos, joguei progressivamente uns 20 anos, e só por hoje em recuperação a exatos 6 anos, sem nenhum tipo de aposta; muito agradecido a meu também progressivo novo modo de viver e de pensar.

Essa data é mais importante do que o dia que vim ao mundo, porque através de minha irmã mais velha, que a tenho como meu alicerce espiritual, me disse com simplicidade que eu estava doente, e precisava ser tratado; daí, totalmente sem rumo, aquilo soou como uma chance de continuar a viver, fui em busca sem nenhuma ESPERANÇA habitada em mim e conheci o LIXO humano criado também por mim durante a minha vida de jogatina, minha vida de egoísmo, minha vida de mentiras e trapaças que o jogo me levou… Cheguei ao fundo do poço. Cavei muito meu buraco, machuquei intensamente quem não merecia e perdi a melhor oferta da vida: VIVER; mas, existia uma instituição que poderia me acolher, fui sem saber pra onde estava indo, sabe aquela coisa sem jeito? Era exatamente assim! Na verdade queria fugir de tudo e de todos… Como um fugitivo, um criminoso! Na época me perguntava: O que procurava? O que queria da Vida? O que fiz a mim e aos próximos? Certo que naquele momento não teria resposta, pois estava tomado pela minha compulsão e minha insanidade; ai preferir ficar na gaiola; protegido dos problemas, das cobranças, das pessoas e me oportunizando a minha ÚLTIMA chance de voltar a normalidade de viver. Sabe aquilo que a vida nos mostra ao longo de nossa passagem: tudo que acionamos tem bons e ruins resultados… Comecei a acreditar que poderia fazer UM NOVO COMEÇO e por consequência um melhor FIM. Conheci minha doença e acreditei que ela era digna de respeito; a compulsão era uma realidade na minha rotina, fui apresentado aos meus iguais, retomei meu contato com meu Poder Superior e segui passo a passo a minha caminhada.

Minha Vida tomou um novo rumo, recheada de problemas que outrora nem ligava, situações adversas a minha vontade, que na jogatina não me deixava sofrer. Passei a enfrentar reais situações com retidão e entendimento, aceitando o que não poderia modificar e tendo coragem para modificar o que estava ao meu alcance. Nada mais quem minha OBRIGAÇÃO, ficar distante de minha primeira aposta!

Só por hoje, completo 6 anos de realidade de Vida! (R)', 'R. – 47 anos', 'approved', '00000000-0000-0000-0000-000000000002', '2026-07-17 21:49:55.3483+00', '2026-07-17 21:49:55.3483+00');
INSERT INTO public.relato (id, texto, nome_exibicao, status, autor_id, criado_em, atualizado_em) VALUES ('b4232113-aede-4b26-a2d0-ccf506207175', 'Libertando-se da Compulsão

Eu devia ter uns seis aninhos quando fiz um jogo de bicho, pela primeira vez, e ganhei! Fiquei radiante de alegria e imediatamente comprei bola, pipa e carretel de linha. Lembro que naquela época, minha mãe lavava e passava muita roupa para o sustento da casa. No tanque, acendia um cigarro atrás do outro, enquanto os unheiros purgavam nas mãos doloridas. Eu, pequeno, já entendia tudo e não gostava nem um pouco de ser pobre. Queria ter bola, brinquedos e comer maçã – fruta que na época custava muito caro. Por isso vibrei ao ganhar a primeira aposta.

Minha mãe também jogava e ganhava muitas vezes no bicho – pelo menos era essa a impressão que eu e toda a vizinhança tinha. Quando ganhava no bicho ela comprava doces e distribuía para as crianças. Como ela não sabia escrever, recorria a mim para fazer seu jogo. Então, logo cedo, decorei a tabela dos bichos.

O tempo passou e eu vez ou outra jogava, sem exagero, até porque adolescente nem tinha dinheiro para isso. Fiquei adulto e comecei a trabalhar. Continuei jogando pouco porque pouco ganhava.

Quando me casei, no início dos anos 80, meu jogo já não era normal. Além do bicho, jogava nas loterias esportivas e loterias de número. Tinha a esperança de ganhar uma bolada e resolver todos os problemas financeiros que sempre me acompanharam. Nessa época, surguiram as primeiras maquininhas de vídeo poker que foram instaladas nos botequins do Rio de Janeiro. Comecei a perder instantâneamente todo dinheiro que tinha no bolso jogando vídeo poker e respirei aliviado quando aquelas maquininhas foram recolhidas pela polícia. No entanto, continuei jogando nas loterias e jogo de bicho…

Ao entrar para o serviço público ganhei conta bancária e talões de cheque. Ganhei também o assédio de agiotas que, naquela época, circulavam na repartição. Comecei a me endividar com eles para manter o volume de jogo que crescia cada vez mais. Chegaram os filhos: o primeiro em 87 e dois gêmeos em 89 – eu estava encrencado! Embora ganhasse um pouco melhor, já estava jogando muito mais! Fazia estrepolias pegando dinheiro com um agiota para pagar ao outro e vice-versa! Quanto mais desesperado ficava, mais aumentava meu jogo! Nos finais de semana eu não ficava em casa e sim na praça, jogando cartas com os idosos ou jogando tranca (a valer) com outros jogadores. Minha mulher, repetidas vezes, aparecia na esquina já à noite, de mãos na cintura e perguntando se eu não iria almoçar! Envergonhado ía para casa ouvindo poucas e boas e prometendo a mim mesmo que não faria mais aquilo, até que no próximo fim de semana tudo se repetisse… Eu não sabia lidar com os meus problemas, por isso ficava jogando na praça, agravando-os ainda mais.

Outras modalidades de jogos surgiram no cardápio dos jogadores compulsivos brasileiros: raspadinhas, senas, novas maquininhas… levando-os à bancarrota mais rapidamente. Em 1991, desesperado procurei ajuda em Neuróticos Anônimos, depois de ligar para os Alcóolicos Anônimos pedindo uma suposta ajuda. O atendente de AA falou ao telefone: "Você deve procurar os Neuróticos Anônimos que eles vão lhe ajudar!" – naquela época não havia grupos de Jogadores Anônimos em funcionamento no Brasil. Depois de muita relutância procurei o N/A.

Antes de chegar ao N/A estava fortemente compulsivo. Não podia pegar em dinheiro que iria jogar. Por mais que eu prometesse a mim mesmo – e eu não queria mais jogar – mas não conseguia. Estava perdendo todo meu salário no dia do pagamento. Já mentira dizendo que tinha sido assaltado no dia do pagamento. Já não tinha mais desculpas quando, certo dia, joguei de novo todo o salário e não sabia como encarar a mulher e três filhos, que me esperavam com as contas a pagar e o supermercado a fazer. Naquele dia, quase me joguei embaixo de um carro na Av. Pres. Vargas, mas num arroubo de esperança tive a ídeia de procurar o Pinel. Lá me deram uma injeção e me mandaram para casa, recomendando-me que procurasse um psicólogo. Em casa atirei-me na cama e contei, sabe Deus como(!), à mulher assustada que perdera o dinheiro no jogo e que precisava de ajuda. Meu irmão chegou para me socorrer dizendo que aquilo ía passar e se eu tivesse força de vontade, não voltaria a jogar. Eu sabia que não era assim: estava tentando ter forças à muito e nada adiantava. Então fui procurar o N/A.

Em Neuróticos Anônimos fui muito bem recebido, apesar de não haver nenhum jogador compulsivo naquele grupo formado para pessoas que têm depressão, fobia, pãnico e outros sintomas emocionais. Tive contato com o Programa de Recuperação dos 12 Passos e já podia falar de meus problemas emocionais e de jogo. Deu certo e eu parei de jogar – o mais importante: não tive mais vontade de jogar com menos de três meses frequentando o grupo. Ali continuei me aprofundando no conhecimento do Programa de Recuperação e participando ativamente. Sentía-me aliviado e feliz. Passei a acompanhar os membros de Neuróticos Anônimos nas divulgações da irmandade junto aos médicos.

A partir de 1993, já com dois anos de abstinência do jogo, dei sequência à minha recuperação em Jogadores Anônimos. Por isso, posso afirmar àqueles jogadores compulsivos que estão distantes dos grupos de JA que é possível parar de jogar frequentando qualquer grupo anônimos baseado nos 12 Passos de recuperação – desde que este grupo aceite o jogador como frequentador. Basta ao jogador que possua o desejo de parar de jogar e não meça esforços para obter este intento.

Hoje, sinto-me recuperado da compulsão e venho tendo uma vida normal feliz – longe do jogo.

Abraços!

Rio de Janeiro', 'Anônimo – Rio de Janeiro', 'approved', '00000000-0000-0000-0000-000000000002', '2026-07-17 21:49:55.3483+00', '2026-07-17 21:49:55.3483+00');
INSERT INTO public.relato (id, texto, nome_exibicao, status, autor_id, criado_em, atualizado_em) VALUES ('bb2d6041-942d-41e8-9533-9dd15d539d35', 'Um novo modo de pensar e viver longe do jogo.

Sou um jogador compulsivo em recuperação, não faço a primeira aposta há 4 anos e 1 mês. Manterei o anonimato, um dos princípios básicos de Jogadores Anônimos.

Do fundo do coração digo que minha vida pode ser dividida em antes de Jogadores Anônimos e depois de Jogadores Anônimos. Aprendi e compreendi em JA que é possível encontrar um novo modo de pensar e viver! Viver sem o jogo parecia impossível, mas agora um dia de cada vez venho desfrutando dessa liberdade maravilhosa!

O jogo em um primeiro momento parecia inofensivo. Era um passatempo, uma diversão. Aqueles dois reais, cinco reais não faziam mal a ninguém! E a adrenalina, prazer que dava na hora? E aquela sensação indescritível quando tinha um grande ganho? E aquela sensação de que por alguns momentos eu era poderoso ou poderia me tornar poderoso?

O jogo me enganou! Eu atravessei a linha invisível do jogo fora do controle! Os dois reais viraram dez, cinquenta. E depois já apostava cem, quinhentos reais. Mil reais em uma aposta! Jogava dinheiro que não tinha. E o passatempo? Que passatempo? Eu pensava quase que 24 horas por dia em jogo! Isso é passatempo? O prazer era falso! Virou angústia, ansiedade, tensão! Todo dinheiro que ganhava no jogo voltava para o jogo! Se eu perdia no jogo, jogava novamente para tentar recuperar, ou seja, passei a ser escravo dessa compulsão. Não agia mais com razão, discernimento. Cheguei ali no fundo do poço (cada um sabe qual é o seu), meus defeitos de caráter aflorados, deprimido, sem dinheiro, com três empréstimos para pagar por dívidas contraídas por causa do jogo, com meu casamento à beira da destruição…

Por um desses milagres da vida, fui a uma reunião de Jogadores Anônimos. Cheguei descrente e achando que ia ouvir um monte de baboseira, sermão, etc. Que arrogante eu era! Logo vi que os membros não faziam julgamentos uns dos outros. Logo me identifiquei com os depoimentos de outras pessoas. As modalidades de jogo variavam (a minha era apostas esportivas), mas o padrão de comportamento do jogador compulsivo era o mesmo! Todos ali eram como eu e dividiam o propósito comum de ficarem longe da primeira aposta! Aos poucos fui me soltando, escutava com atenção os depoimentos dos demais companheiros e contava minha história para eles, meus problemas, o que o jogo tinha feito com minha vida, etc. Contava também sobre minhas conquistas diárias, problemas diários também (eles fazem parte da vida). Tirava um caminhão das minhas costas a cada reunião e não me sentia mais sozinho! Passei também a identificar meus defeitos como ser humano e tentar me tornar uma versão melhor de mim mesmo. E ali nas reuniões, nessa troca de experiências, força, fé e esperança, um dia de cada vez, fui ganhando tempo de abstinência.

Gente, quantas vezes eu tentei parar de jogar sozinho!!! Até jurei de pé junto (várias vezes) que ia parar de jogar! Fazia inúmeras promessas! Mas logo voltava a jogar. Agora em um Grupo de mútua ajuda eu estou conseguindo!

Fechada a torneira dos gastos com o jogo o problema financeiro foi se acertando aos poucos. Meu casamento foi reconstruído e hoje fomos abençoados com o nascimento da minha primeira filha! Aprendi a valorizar a família! Aprendi a gostar de coisas novas. Aprendi muito com meus erros do passado, mas ao mesmo tempo me perdoei e passei a desenhar uma nova história para minha vida. Existe uma frase na literatura de Jogadores Anônimos que é simples, mas é muito sábia: "Faça coisas boas, que boas coisas vão lhe acontecer"! E é isso que venho experimentando.

Sim, há vida sem o jogo! Se você está lendo esse depoimento e acha que o jogo se tornou um problema maior na sua vida, se dê a oportunidade de conhecer Jogadores Anônimos! Você será muito bem acolhido como nunca imaginou ser. Honestidade, boa vontade e mente aberta! É possível parar de jogar!

Abraços fraternos.', 'Anônimo', 'approved', '00000000-0000-0000-0000-000000000002', '2026-07-17 21:49:55.3483+00', '2026-07-17 21:49:55.3483+00');
INSERT INTO public.relato (id, texto, nome_exibicao, status, autor_id, criado_em, atualizado_em) VALUES ('75aacadc-7468-429f-beec-1cdaa4a3ef59', 'Primeiro Passo de Recuperação

Para mim, não foi complicado dar o 1º passo. Antes mesmo de descer aqueles degraus da igreja Sta. Efigênia em direção à minha primeira reunião, já havia admitido e aceito minha total derrota e perda de domínio de minha vida.

Cheguei literalmente de joelhos, pois ao adentrar a igreja procurei pela imagem de S.Judas Tadeu, a quem se atribui ser o santo das causas impossíveis e de casos desesperados como o meu. Talvez não tenha identificado sua imagem, mas creio que mesmo assim o santo diante do qual me ajoelhei e orei deve ter passado o recado.

Quando cheguei a J.A. não havia mais portas em que bater, nem mesmo batentes haviam sobrado, para dar uma dimensão da realidade que me cercava. Precisava de uma janela, uma fresta que fosse, de esperança de resgate de minha sanidade, de minha integridade, diria mesmo do meu EU, de minha personalidade. O jogo havia me transformado em uma "COISA".

Hoje, decorridos, 02 anos e 02 meses, J.A vem resgatando minha verdadeira essência, sei que os rescaldos de tanta destruição ainda demandarão tempo considerável para serem sanados, muitas pontes que detonei com minha compulsão não poderão ser reconstruídas, resta-me hoje, tentar lidar com sentimentos de culpa, auto-piedade, frustração e ressentimentos que vez ou outra afloram em meus pensamentos e abalam minha estrutura emocional.

Situações altamente constrangedoras e humilhantes também tive que passar nesse período de abstinência, afinal, são as consequências. Seria irreal imaginar que poderia me safar delas, de todo esse doloroso processo de recuperação. Mas como jogador que continuo sendo, hoje eu aposto no programa de Jogadores Anônimos, uma programação de VIDA e de ESPERANÇA, acima de tudo!

24 Horas.

JG – São Paulo – Enviado em dezembro de 2008.', 'JG – São Paulo', 'approved', '00000000-0000-0000-0000-000000000002', '2026-07-17 21:49:55.3483+00', '2026-07-17 21:49:55.3483+00');
INSERT INTO public.relato (id, texto, nome_exibicao, status, autor_id, criado_em, atualizado_em) VALUES ('4db3dad9-20f8-49d4-9edd-5bacd12bb584', 'Enfrentando a Compulsividade

Há 10 anos entrei para o J.A.. Fui recebida com muito carinho, embora estivesse muito tensa e sofrida, pois não conseguia me livrar do jogo e achava que, para mim não existia mais solução. O Passo lido foi o 1º de Recuperação. Ao ouvir e entender que, o que eu tinha, era uma doença chamada compulsividade, senti um enorme alívio. Eu só ouvia dos meus familiares que, eu era uma sem-vergonha, mentirosa, viciada e que eu não tinha mais limites para distinguir o que era certo ou errado. Que eu não passava de uma pessoa sem caráter. Isto me fazia muito mal, porque na realidade eu não sabia o que se passava comigo. Eu não queria mais jogar e, no entanto, todo dia eu saia para jogar e voltava de madrugada com enorme peso na consciência. Por que eu fazia aquilo? Por que eu não conseguia parar?

Naquela primeira reunião percebi que eu precisava de ajuda e que eu poderia deter a doença, mas que nunca me livraria da compulsividade, uma vez que, ela fazia parte de mim. E, isto eu percebi com o tempo, que eu tinha que estar sempre alerta para que a compulsividade não migrasse para outras coisas. Depois de uma semana no J.A., entrei nas Lojas Americanas e comprei duas bolsas cheias de mercadorias. E estava feliz da vida, pois tinha gasto o dinheiro em algo útil. Dois dias depois, novamente cheguei com duas bolsas repletas e a minha mãe me olhou e perguntou se eu estava precisando de todas aquelas coisas. Surpresa eu respondi que não, mas que, finalmente, eu estava gastando o dinheiro com coisas e não com o jogo. Ela me olhou seriamente e me alertou que eu tivesse cuidado para não me tornar uma compradora compulsiva e que era melhor que eu colocasse o dinheiro na poupança para equilibrar o meu orçamento. Foi um alerta que me ajudou e me ajuda até hoje. Fico me vigiando constantemente e a qualquer indício de compulsividade eu recuo imediatamente. E, isto, já me aconteceu várias vezes.

Nestes dez anos, aprendi a crer num Poder Superior, entregar-me a Ele e pedir que eu aceite a minha doença, e com a ajuda Dele trabalhe e me livre dos meus defeitos de caráter e, sempre, peço a este Poder Superior que me dê a Serenidade para que eu possa receber bem aquele que ainda esteja sofrendo, pois ele também é parte importante de minha recuperação.

Os Passos de Recuperação, os Passos de Unidade e a prestação de serviço foram e, sempre, serão o caminho para a minha libertação do jogo, que tanto mal me causou.

Eu só posso agradecer ao Poder Superior, a esta Irmandade e a todos os Irmãos, que através de seus depoimentos, me ajudaram nesta caminhada de dez anos, longe do jogo.

Susie – Rio de Janeiro – Enviado em agosto de 2009.', 'Susie – Rio de Janeiro', 'approved', '00000000-0000-0000-0000-000000000002', '2026-07-17 21:49:55.3483+00', '2026-07-17 21:49:55.3483+00');
INSERT INTO public.relato (id, texto, nome_exibicao, status, autor_id, criado_em, atualizado_em) VALUES ('cf9a0f5a-c36a-4f77-92ee-b5ac6d02461b', 'A ajuda veio à distância

Há algum tempo, o jogo do bicho era apenas uma diversão em minha vida, não jogava todos os dias e as apostas eram baixas, mas, esta diversão foi se tornando terrível e incontrolável. Daí eu descobri as máquinas de caça níquel e também comecei a jogar. Como uma droga, logo me viciei e, em menos de três meses já tinha perdido muito dinheiro, jogando compulsivamente. Fiz empréstimos bancários em minha conta e na conta de parentes, além dos agiotas com quem eu também buscava dinheiro para manter a compulsão do jogo.

Sou casado, minha esposa estava grávida e quando minha filha nasceu eu continuei jogando. Ao completar um ano de jogo, no dia de meu aniversario, justo neste dia eu cometi a maior insanidade, perdi muito dinheiro de uma só vez, com um total de divida que ultrapassava o limite de minhas possibilidades. Foi aí que resolvi contar para a minha esposa. A tristeza e desânimo com o casamento tomaram conta dela, mas mesmo assim ela concordou em me ajudar e vendemos a nossa moto. Mas, isso não resolveu todas as dívidas.

Pensando já ter me livrado do problema, voltei a jogar no bicho, mas dessa vez, o volume de dinheiro aumentou muito, proporcionalmente à minha divida e infelicidade em casa, prejudicando, cada vez mais, a família. Minha mãe soube da situação e se dispôs a vender a casa para pagar minhas dividas e a minha esposa não quis me perdoar ao saber que pessoas da cidade, já comentavam sobre o meu caso de jogo.

Comecei então, a pesquisar na internet, meios para entender o problema e tentar me livrar dele. Ao encontrar o site de Jogadores Anônimos, percebi que se tratava de uma doença incurável. Enviei um e-mail ao ERSJAB e no dia seguinte, recebi resposta de alguém, a quem chamo de padrinho, com quem até hoje mantenho contato, o que está sendo fundamental em minha recuperação. Recebi também, por correio, alguma literatura em que constam os 12 Passos de Recuperação que, tento praticar em minha nova vida, já que, infelizmente na cidade do Girau do Ponciano, interior de Alagoas não há grupo de JA.

As dificuldades ainda não acabaram, considerando também as dívidas que, aos poucos estão sendo ressarcidas. A convivência com a família está melhorando aos poucos. Embora ainda não tenha estabelecido à confiança, todos já estão percebendo as diferenças em meu cotidiano. Sei que a batalha ainda será árdua, mas com fé e os 12 Passos de Recuperação eu vencerei e viverei feliz valorizando o que a vida tem de melhor a me oferecer. Hoje estou completando 6 meses e 28 dias sem aposta e estou vivendo um dia após o outro. Desejo a todos 24 horas de paz, serenidade e abstinência.

JRS – Girau do Ponciano – AL – Enviado em maio de 2012', 'JRS – Girau do Pon., AL', 'approved', '00000000-0000-0000-0000-000000000002', '2026-07-17 21:49:55.3483+00', '2026-07-17 21:49:55.3483+00');
INSERT INTO public.relato (id, texto, nome_exibicao, status, autor_id, criado_em, atualizado_em) VALUES ('f790e1c0-7900-47b5-91ee-e6b02fbf4ee4', 'Sofrimento e Recuperação

Conheci o bingo no ano de 1999, e foi com o pai da minha filha, me lembro que naquela noite ficamos por volta de uma hora, jogamos nas máquinas e ganhamos, era tanto dinheiro que achei que era o local exato para eu ganhar dinheiro e melhorar de vida. No início ia a cada 20 dias e às vezes, uma vez por mês. Até que no mesmo ano saí de férias e como não tinha nada para fazer resolvi ir até o Bingo Cruzeiro do Sul (Santana) e comecei a jogar até perder todo o dinheiro das férias. Achei um absurdo, pois trabalhei o ano todo e em questão de meia hora já havia perdido tudo, na ânsia de recuperar meu dinheiro, passei a frequentar mais vezes, até o momento em que já não buscava mais o prazer do jogo e sim recuperar o perdido. Perdi a noção do dinheiro, tempo, auto-estima, dignidade, amor próprio, dentre outros que joguei dentro do buraco daquelas máquinas. Já não me alimentava, não trabalhava, cheguei a receber duas cartas de advertência da empresa que trabalho, mas nada do que aconteceu fez com que eu parasse de jogar.

Lembro que deixei minha filha, na época menor de idade, do lado de fora do bingo para jogar, peguei dinheiro com agiotas, empréstimos nos bancos e com pessoas conhecidas, simulei assaltos, menti, manipulei, virei um trapo humano.

Eu achava que meu problema era financeiro e não o jogo, que, aliás, aquelas máquinas me fascinavam pelas luzes, era como se eu estivesse dopada e não percebia o tempo passar, quando jogava eu esquecia, deixava meus problemas fora de questão. Aliás, sempre tinha uma máquina preferida para eu jogar e não gostava quando tinha alguém no meu lugar, ou quando ficavam parados ao meu lado, eu achava que estavam gorando a máquina. Loucura total de uma doente.

Ingressei em Jogadores Anônimos no dia 25/11/02 e desde então nunca mais joguei. Cheguei cheia de dívidas e totalmente derrotada como pessoa, aprendi que tenho que viver um dia de cada vez, foi muito difícil no início ficar sem jogar, mas estou conseguindo aos poucos, hoje são quase 07 anos sem fazer uma aposta. No início da recuperação ia a todas as reuniões de São Paulo, fiz 90 dias/90 reuniões e se não tivesse conhecido Jogadores Anônimos hoje talvez eu estivesse louca, porque o jogo estava me levando à loucura ou quem sabe já estivesse morta ou presa e é exatamente isso que faz a doença da compulsão: Loucura, morte ou prisão.

Hoje levo uma vida totalmente diferente, já não preciso do jogo para resolver meus problemas, vivo com qualidade de vida e o que ganho em dinheiro é oriundo do meu trabalho. Hoje já paguei todas as dívidas relativas ao jogo. Vale à pena ficar sem jogar, conheci pessoas maravilhosas e assisto duas reuniões por semana, vale à pena entrar em recuperação e sempre agradeço ao meu Poder Superior (Deus como eu acredito) por mais 24 horas.

MA – São Paulo – Enviado em junho de 2009.', 'MA – São Paulo', 'approved', '00000000-0000-0000-0000-000000000002', '2026-07-17 21:49:55.3483+00', '2026-07-17 21:49:55.3483+00');
INSERT INTO public.relato (id, texto, nome_exibicao, status, autor_id, criado_em, atualizado_em) VALUES ('7b957fb2-4dcd-424c-ab9f-2138a8482c5c', 'A importância do Grupo

Meu nome é Carlos Alberto, sou um jogador compulsivo em recuperação, de Florianópolis – SC.

Minha adicção foi pelo jogo do bicho. Comecei a jogar com a idade de 17 anos em 1962, quando fui para o Rio de Janeiro. Solteiro ainda e, distante de minha família, comecei a sair com um grupo de amigos e, entre uma cerveja e outra, fazia uma aposta para passar o tempo. Essas apostas foram tomando proporções maiores e, quando dei por mim, já estava tomado pelo vício. Aí, juntou o jogo, a bebida e o cigarro.

Em 1991, retornei para Florianópolis, minha terra natal, onde continuei com os malditos vícios: Jogo, bebida e cigarro. Juntamente com o jogo do bicho vieram também as máquinas; foi quando conheci o fundo do poço. Estava no fundo do poço, quando assistindo a um canal de televisão, deparei-me com uma reportagem que falava sobre Jogadores Anônimos. Procurei saber o telefone da pessoa responsável e, foi essa a minha salvação.

O grupo estava em seu início e pela força do Poder Superior, a pessoa que me atendeu me deu o endereço e disse que seria a terceira reunião do grupo. Isso aconteceu no dia 26 de janeiro de 2002. O grupo teve a sua primeira reunião no dia 22 de janeiro do mesmo ano. Nesse tempo de grupo eu tive 04 recaídas.

No dia 02 de julho de 2008, o Poder Superior se manifestou outra vez, por meio de um dono de casa de jogo de bicho. Ele chegou até a minha pessoa e disse para eu sair dessa vida que, não dava camisa a ninguém e que eu não ganharia nunca. Aí, no dia 03, eu procurei novamente o grupo, de onde estava afastado há quase dois anos. Portando, com a graça do Poder Superior, no dia 3 de julho de 2009 completarei 07 anos, 05 meses e 07 dias de grupo, e 01 ano de abstinência, desde a minha última recaída.

Quero dizer também que, com a minha boa vontade e com a ajuda do Poder Superior, deixei também de fumar e de beber. Agradeço em primeiro lugar ao Poder Superior e depois à minha esposa que, não deixou, em nenhum momento, de me dar força para eu controlar a minha doença.

Aos meus irmãos de grupo, também agradeço por me escutarem todas as vezes que nas reuniões dou o meu depoimento. Essa força é essencial para a minha recuperação.

Por fim, quero dizer aos irmãos que desejam se afastar de seu grupo, que não o façam, pois o afastamento da sala é uma prévia para a recaída.

Obrigado por terem lido esse meu depoimento e desejo a todos, 24 horas de Paz, Serenidade e Abstinência.

CA – Florianópolis – Enviado em junho de 2009.', 'Carlos A. – Floripa', 'approved', '00000000-0000-0000-0000-000000000002', '2026-07-17 21:49:55.3483+00', '2026-07-17 21:49:55.3483+00');
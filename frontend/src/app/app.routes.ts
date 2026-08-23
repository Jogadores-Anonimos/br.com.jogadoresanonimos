import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { adminGuard } from './guards/admin.guard';

export const routes: Routes = [
  {
    path: '',
    component: Home,
    data: {
      seo: {
        title: 'Jogadores Anônimos do Brasil | Apoio para jogo compulsivo',
        description:
          'Associação de apoio a pessoas com problema de jogo compulsivo e suas famílias. Reuniões, relatos e informação em todo o Brasil, sem julgamento.',
        ogTitle: 'Jogadores Anônimos do Brasil',
        ogDescription:
          'Apoio a quem sofre com jogo compulsivo e a suas famílias. Encontre uma reunião perto de você.',
      },
    },
  },
  {
    path: 'admin',
    canActivate: [adminGuard],
    loadComponent: () =>
      import('./pages/admin/admin-layout/admin-layout').then((m) => m.AdminLayout),
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./pages/admin/admin-entrada/admin-entrada').then((m) => m.AdminEntrada),
      },
      {
        path: 'avisos',
        loadComponent: () =>
          import('./pages/admin/admin-avisos/admin-avisos').then((m) => m.AdminAvisos),
        data: { titulo: 'Avisos' },
      },
      {
        path: 'reflexao-do-dia',
        loadComponent: () =>
          import('./pages/admin/admin-reflexao-do-dia/admin-reflexao-do-dia').then(
            (m) => m.AdminReflexaoDoDia,
          ),
        data: { titulo: 'Reflexão do Dia' },
      },
      {
        path: 'mural-de-apoio',
        loadComponent: () =>
          import('./pages/admin/admin-mural-de-apoio/admin-mural-de-apoio').then(
            (m) => m.AdminMuralDeApoio,
          ),
        data: { titulo: 'Mural de Apoio' },
      },
      {
        path: 'relatos',
        loadComponent: () =>
          import('./pages/admin/admin-relatos/admin-relatos').then((m) => m.AdminRelatos),
        data: { titulo: 'Relatos' },
      },
      {
        path: 'paginas',
        loadComponent: () =>
          import('./pages/admin/paginas-lista/paginas-lista').then((m) => m.PaginasLista),
        data: { titulo: 'Páginas do site' },
      },
      {
        path: 'paginas/:slug',
        loadComponent: () =>
          import('./pages/admin/pagina-editor/pagina-editor').then((m) => m.PaginaEditor),
        data: { titulo: 'Editar página' },
      },
      {
        path: 'reunioes',
        loadComponent: () =>
          import('./pages/admin/admin-reunioes/admin-reunioes').then((m) => m.AdminReunioes),
        data: { titulo: 'Reuniões' },
      },
      {
        path: 'escritorios-regionais',
        loadComponent: () =>
          import('./pages/admin/admin-escritorios-regionais/admin-escritorios-regionais').then(
            (m) => m.AdminEscritoriosRegionais,
          ),
        data: { titulo: 'Endereços' },
      },
      {
        path: 'hotlines',
        loadComponent: () =>
          import('./pages/admin/admin-hotlines/admin-hotlines').then((m) => m.AdminHotlines),
        data: { titulo: 'Linhas de Ajuda' },
      },
      {
        path: 'perguntas-e-respostas',
        loadComponent: () =>
          import('./pages/admin/admin-faq/admin-faq').then((m) => m.AdminFaq),
        data: { titulo: 'Perguntas e Respostas' },
      },
      {
        path: 'literatura',
        loadComponent: () =>
          import('./pages/admin/admin-literatura/admin-literatura').then(
            (m) => m.AdminLiteratura,
          ),
        data: { titulo: 'Literatura' },
      },
      {
        path: 'usuarios',
        loadComponent: () =>
          import('./pages/admin/admin-usuarios/admin-usuarios').then((m) => m.AdminUsuarios),
        data: { titulo: 'Usuários' },
      },
      {
        path: 'seguranca',
        loadComponent: () =>
          import('./pages/admin/admin-seguranca/admin-seguranca').then((m) => m.AdminSeguranca),
        data: { titulo: 'Segurança' },
      },
      {
        path: 'auditoria',
        loadComponent: () =>
          import('./pages/admin/admin-auditoria/admin-auditoria').then((m) => m.AdminAuditoria),
        data: { titulo: 'Auditoria' },
      },
    ],
  },
  {
    path: 'sobre',
    loadComponent: () => import('./pages/sobre/sobre').then((m) => m.Sobre),
    data: {
      seo: {
        title: 'Quem Somos | Jogadores Anônimos do Brasil',
        description:
          'Conheça a Jogadores Anônimos do Brasil: uma irmandade de apoio mútuo para quem quer parar de jogar, sem custos e sem julgamento.',
        ogTitle: 'Quem Somos | JA Brasil',
        ogDescription: 'Uma irmandade de apoio mútuo para quem quer parar de jogar.',
      },
    },
  },
  {
    path: 'historia',
    loadComponent: () => import('./pages/historia/historia').then((m) => m.Historia),
    data: {
      seo: {
        title: 'Nossa História | Jogadores Anônimos do Brasil',
        description:
          'A história da irmandade Jogadores Anônimos, seu surgimento e a chegada ao Brasil para apoiar quem enfrenta o jogo compulsivo.',
        ogTitle: 'Nossa História | JA Brasil',
        ogDescription: 'Como a irmandade Jogadores Anônimos surgiu e chegou ao Brasil.',
      },
    },
  },
  {
    path: 'anonimato',
    loadComponent: () => import('./pages/anonimato/anonimato').then((m) => m.Anonimato),
    data: {
      seo: {
        title: 'Anonimato | Jogadores Anônimos do Brasil',
        description:
          'Entenda por que o anonimato é a base da Jogadores Anônimos e como ele protege quem busca apoio para o jogo compulsivo.',
        ogTitle: 'Anonimato na JA Brasil',
        ogDescription: 'Por que o anonimato é a base de tudo o que fazemos.',
      },
    },
  },
  {
    path: 'jogo-compulsivo',
    loadComponent: () =>
      import('./pages/jogo-compulsivo/jogo-compulsivo').then((m) => m.JogoCompulsivo),
    data: {
      seo: {
        title: 'O que é Jogo Compulsivo | JA Brasil',
        description:
          'Entenda o que é jogo compulsivo, seus sinais e por que buscar apoio pode ser o primeiro passo para uma vida mais equilibrada.',
        ogTitle: 'O que é Jogo Compulsivo',
        ogDescription: 'Sinais do jogo compulsivo e como buscar apoio.',
      },
    },
  },
  {
    path: 'uma-nova-chance',
    loadComponent: () =>
      import('./pages/uma-nova-chance/uma-nova-chance').then((m) => m.UmaNovaChance),
    data: {
      seo: {
        title: 'Uma Nova Chance | Jogadores Anônimos do Brasil',
        description:
          'Relatos e reflexões sobre recomeçar após o jogo compulsivo, com o apoio da irmandade Jogadores Anônimos.',
        ogTitle: 'Uma Nova Chance | JA Brasil',
        ogDescription: 'Recomeçar é possível, um dia de cada vez, com apoio.',
      },
    },
  },
  {
    path: 'programa-de-recuperacao',
    loadComponent: () =>
      import('./pages/programa-de-recuperacao/programa-de-recuperacao').then(
        (m) => m.ProgramaDeRecuperacao,
      ),
    data: {
      seo: {
        title: 'Programa de Recuperação | JA Brasil',
        description:
          'Conheça o programa de recuperação da Jogadores Anônimos, baseado em passos e no apoio mútuo entre membros da irmandade.',
        ogTitle: 'Programa de Recuperação | JA Brasil',
        ogDescription: 'Um caminho de recuperação baseado em passos e apoio mútuo.',
      },
    },
  },
  {
    path: 'relatos',
    loadComponent: () => import('./pages/relatos/relatos').then((m) => m.Relatos),
    data: {
      seo: {
        title: 'Relatos | Jogadores Anônimos do Brasil',
        description:
          'Histórias reais de superação de membros da Jogadores Anônimos que enfrentaram o jogo compulsivo e encontraram apoio.',
        ogTitle: 'Relatos de Membros | JA Brasil',
        ogDescription: 'Histórias reais de quem enfrentou o jogo compulsivo.',
      },
    },
  },
  { path: 'depoimentos', redirectTo: 'relatos', pathMatch: 'full' },
  {
    path: 'programa-de-unidade',
    loadComponent: () =>
      import('./pages/programa-de-unidade/programa-de-unidade').then(
        (m) => m.ProgramaDeUnidade,
      ),
    data: {
      seo: {
        title: 'Programa da Unidade | JA Brasil',
        description:
          'Os princípios que mantêm os grupos da Jogadores Anônimos unidos e sustentados pelas contribuições dos próprios membros.',
        ogTitle: 'Programa da Unidade | JA Brasil',
        ogDescription: 'Os 12 passos que mantêm nossos grupos unidos.',
      },
    },
  },
  {
    path: 'perguntas-e-respostas',
    loadComponent: () =>
      import('./pages/perguntas-e-respostas/perguntas-e-respostas').then(
        (m) => m.PerguntasERespostas,
      ),
    data: {
      seo: {
        title: 'Perguntas e Respostas | JA Brasil',
        description:
          'Tire dúvidas comuns sobre a Jogadores Anônimos, como funcionam as reuniões e como buscar apoio para o jogo compulsivo.',
        ogTitle: 'Perguntas Frequentes | JA Brasil',
        ogDescription: 'Dúvidas comuns sobre a irmandade e como participar.',
      },
    },
  },
  {
    path: 'vinte-perguntas',
    loadComponent: () =>
      import('./pages/vinte-perguntas/vinte-perguntas').then((m) => m.VintePerguntas),
    data: {
      seo: {
        title: '20 Perguntas | Jogadores Anônimos do Brasil',
        description:
          'Um questionário de autorreflexão sobre jogo compulsivo, usado pela irmandade Jogadores Anônimos para ajudar a identificar sinais.',
        ogTitle: '20 Perguntas sobre Jogo Compulsivo',
        ogDescription: 'Um questionário de autorreflexão da Jogadores Anônimos.',
      },
    },
  },
  {
    path: 'literatura',
    loadComponent: () => import('./pages/literatura/literatura').then((m) => m.Literatura),
    data: {
      seo: {
        title: 'Literatura | Jogadores Anônimos do Brasil',
        description:
          'Catálogo de literatura oficial da Jogadores Anônimos: materiais de apoio para membros e familiares sobre o jogo compulsivo.',
        ogTitle: 'Literatura | JA Brasil',
        ogDescription: 'Materiais de apoio sobre jogo compulsivo para membros e famílias.',
      },
    },
  },
  {
    path: 'avisos',
    loadComponent: () => import('./pages/avisos/avisos').then((m) => m.Avisos),
    data: {
      seo: {
        title: 'Avisos | Jogadores Anônimos do Brasil',
        description:
          'Comunicados e avisos oficiais da Jogadores Anônimos do Brasil para membros, grupos e visitantes.',
        ogTitle: 'Avisos | JA Brasil',
        ogDescription: 'Comunicados oficiais da Jogadores Anônimos do Brasil.',
      },
    },
  },
  {
    path: 'enderecos',
    loadComponent: () =>
      import('./pages/enderecos/enderecos').then((m) => m.Enderecos),
    data: {
      seo: {
        title: 'Endereços | Jogadores Anônimos do Brasil',
        description:
          'Encontre endereços de grupos e escritórios da Jogadores Anônimos no Brasil para buscar apoio presencial.',
        ogTitle: 'Endereços dos Grupos | JA Brasil',
        ogDescription: 'Endereços de grupos e escritórios da JA Brasil.',
      },
    },
  },
  { path: 'escritorios-regionais', redirectTo: 'enderecos', pathMatch: 'full' },
  {
    path: 'reflexao-do-dia',
    loadComponent: () =>
      import('./pages/reflexao-do-dia/reflexao-do-dia').then((m) => m.ReflexaoDoDiaPage),
    data: {
      seo: {
        title: 'Reflexão do Dia | Jogadores Anônimos do Brasil',
        description:
          'Uma reflexão diária da Jogadores Anônimos para apoiar quem está em recuperação do jogo compulsivo, um dia de cada vez.',
        ogTitle: 'Reflexão do Dia | JA Brasil',
        ogDescription: 'Uma reflexão diária para quem está em recuperação.',
      },
    },
  },
  {
    path: 'reunioes',
    loadComponent: () => import('./pages/reunioes/reunioes').then((m) => m.Reunioes),
    data: {
      seo: {
        title: 'Encontre uma Reunião | Jogadores Anônimos do Brasil',
        description:
          'Encontre reuniões da Jogadores Anônimos perto de você, presenciais ou online, em todo o Brasil.',
        ogTitle: 'Encontre uma Reunião | JA Brasil',
        ogDescription: 'Reuniões presenciais e online em todo o Brasil.',
      },
    },
  },
  {
    path: 'hotlines',
    loadComponent: () => import('./pages/hotlines/hotlines').then((m) => m.Hotlines),
    data: {
      seo: {
        title: 'Linhas de Ajuda | Jogadores Anônimos do Brasil',
        description:
          'Contatos de linhas de ajuda da Jogadores Anônimos para quem precisa conversar agora sobre jogo compulsivo.',
        ogTitle: 'Linhas de Ajuda | JA Brasil',
        ogDescription: 'Contatos para conversar sobre jogo compulsivo.',
      },
    },
  },
  {
    path: 'contribuir',
    loadComponent: () => import('./pages/contribuir/contribuir').then((m) => m.Contribuir),
    data: {
      seo: {
        title: 'Contribuir | Jogadores Anônimos do Brasil',
        description:
          'A Jogadores Anônimos se sustenta com contribuições voluntárias de membros e amigos. Saiba como ajudar a manter essa mensagem viva.',
        ogTitle: 'Contribuir | JA Brasil',
        ogDescription: 'Ajude a manter viva a mensagem da Jogadores Anônimos.',
      },
    },
  },
  {
    path: 'loja',
    loadComponent: () => import('./pages/loja/loja').then((m) => m.Loja),
    data: {
      seo: {
        title: 'Loja | Jogadores Anônimos do Brasil',
        description:
          'Em breve, materiais de recuperação e itens da irmandade Jogadores Anônimos. Enquanto isso, veja nossa literatura ou encontre uma reunião.',
        ogTitle: 'Loja | JA Brasil',
        ogDescription: 'Materiais de recuperação e itens da irmandade, em breve.',
      },
    },
  },
  {
    path: 'mural-de-apoio',
    loadComponent: () => import('./pages/mural-de-apoio/mural-de-apoio').then((m) => m.MuralDeApoio),
    data: {
      seo: {
        title: 'Mural de Apoio | Jogadores Anônimos do Brasil',
        description:
          'Mensagens e materiais de apoio da Jogadores Anônimos para quem busca ajuda com o jogo compulsivo.',
        ogTitle: 'Mural de Apoio | JA Brasil',
        ogDescription: 'Mensagens de apoio para quem busca ajuda.',
      },
    },
  },
  {
    path: 'minha-conta',
    loadComponent: () => import('./pages/minha-conta/minha-conta').then((m) => m.MinhaConta),
  },
  {
    path: 'esqueci-senha',
    loadComponent: () => import('./pages/esqueci-senha/esqueci-senha').then((m) => m.EsqueciSenha),
  },
  {
    path: 'redefinir-senha',
    loadComponent: () =>
      import('./pages/redefinir-senha/redefinir-senha').then((m) => m.RedefinirSenha),
  },
  {
    path: 'confirmar-email',
    loadComponent: () =>
      import('./pages/confirmar-email/confirmar-email').then((m) => m.ConfirmarEmail),
  },
  {
    path: 'admin/redefinir-senha',
    loadComponent: () =>
      import('./pages/admin/admin-redefinir-senha/admin-redefinir-senha').then(
        (m) => m.AdminRedefinirSenha,
      ),
  },
  {
    path: 'privacidade',
    loadComponent: () => import('./pages/privacidade/privacidade').then((m) => m.Privacidade),
    data: {
      seo: {
        title: 'Política de Privacidade | Jogadores Anônimos do Brasil',
        description:
          'Como a Jogadores Anônimos do Brasil trata dados pessoais, com foco em anonimato e conformidade com a LGPD.',
        ogTitle: 'Política de Privacidade | JA Brasil',
        ogDescription: 'Como tratamos dados pessoais, com foco em anonimato.',
      },
    },
  },
  {
    path: 'termos',
    loadComponent: () => import('./pages/termos/termos').then((m) => m.Termos),
    data: {
      seo: {
        title: 'Termos de Uso | Jogadores Anônimos do Brasil',
        description:
          'Termos de uso do site da Jogadores Anônimos do Brasil, associação de apoio a pessoas com jogo compulsivo.',
        ogTitle: 'Termos de Uso | JA Brasil',
        ogDescription: 'Termos de uso do site da Jogadores Anônimos do Brasil.',
      },
    },
  },
  {
    path: '**',
    loadComponent: () =>
      import('./pages/pagina-nao-encontrada/pagina-nao-encontrada').then(
        (m) => m.PaginaNaoEncontrada,
      ),
    data: {
      seo: {
        title: 'Página não encontrada | Jogadores Anônimos do Brasil',
        description:
          'A página que você procurou não foi encontrada. Volte ao início ou encontre uma reunião da Jogadores Anônimos.',
        ogTitle: 'Página não encontrada | JA Brasil',
        ogDescription: 'Volte ao início ou encontre uma reunião da Jogadores Anônimos.',
        robots: 'noindex, follow',
      },
    },
  },
];

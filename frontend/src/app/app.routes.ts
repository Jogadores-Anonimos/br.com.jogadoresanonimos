import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { adminGuard } from './guards/admin.guard';

export const routes: Routes = [
  { path: '', component: Home },
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
    ],
  },
  {
    path: 'sobre',
    loadComponent: () => import('./pages/sobre/sobre').then((m) => m.Sobre),
  },
  {
    path: 'historia',
    loadComponent: () => import('./pages/historia/historia').then((m) => m.Historia),
  },
  {
    path: 'anonimato',
    loadComponent: () => import('./pages/anonimato/anonimato').then((m) => m.Anonimato),
  },
  {
    path: 'jogo-compulsivo',
    loadComponent: () =>
      import('./pages/jogo-compulsivo/jogo-compulsivo').then((m) => m.JogoCompulsivo),
  },
  {
    path: 'uma-nova-chance',
    loadComponent: () =>
      import('./pages/uma-nova-chance/uma-nova-chance').then((m) => m.UmaNovaChance),
  },
  {
    path: 'programa-de-recuperacao',
    loadComponent: () =>
      import('./pages/programa-de-recuperacao/programa-de-recuperacao').then(
        (m) => m.ProgramaDeRecuperacao,
      ),
  },
  {
    path: 'relatos',
    loadComponent: () => import('./pages/relatos/relatos').then((m) => m.Relatos),
  },
  { path: 'depoimentos', redirectTo: 'relatos', pathMatch: 'full' },
  {
    path: 'programa-de-unidade',
    loadComponent: () =>
      import('./pages/programa-de-unidade/programa-de-unidade').then(
        (m) => m.ProgramaDeUnidade,
      ),
  },
  {
    path: 'perguntas-e-respostas',
    loadComponent: () =>
      import('./pages/perguntas-e-respostas/perguntas-e-respostas').then(
        (m) => m.PerguntasERespostas,
      ),
  },
  {
    path: 'vinte-perguntas',
    loadComponent: () =>
      import('./pages/vinte-perguntas/vinte-perguntas').then((m) => m.VintePerguntas),
  },
  {
    path: 'literatura',
    loadComponent: () => import('./pages/literatura/literatura').then((m) => m.Literatura),
  },
  {
    path: 'avisos',
    loadComponent: () => import('./pages/avisos/avisos').then((m) => m.Avisos),
  },
  {
    path: 'enderecos',
    loadComponent: () =>
      import('./pages/enderecos/enderecos').then((m) => m.Enderecos),
  },
  { path: 'escritorios-regionais', redirectTo: 'enderecos', pathMatch: 'full' },
  {
    path: 'reflexao-do-dia',
    loadComponent: () =>
      import('./pages/reflexao-do-dia/reflexao-do-dia').then((m) => m.ReflexaoDoDiaPage),
  },
  {
    path: 'reunioes',
    loadComponent: () => import('./pages/reunioes/reunioes').then((m) => m.Reunioes),
  },
  {
    path: 'hotlines',
    loadComponent: () => import('./pages/hotlines/hotlines').then((m) => m.Hotlines),
  },
  {
    path: 'contribuir',
    loadComponent: () => import('./pages/contribuir/contribuir').then((m) => m.Contribuir),
  },
  {
    path: 'loja',
    loadComponent: () => import('./pages/loja/loja').then((m) => m.Loja),
  },
  {
    path: 'mural-de-apoio',
    loadComponent: () => import('./pages/mural-de-apoio/mural-de-apoio').then((m) => m.MuralDeApoio),
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
    path: 'privacidade',
    loadComponent: () => import('./pages/privacidade/privacidade').then((m) => m.Privacidade),
  },
  {
    path: 'termos',
    loadComponent: () => import('./pages/termos/termos').then((m) => m.Termos),
  },
];

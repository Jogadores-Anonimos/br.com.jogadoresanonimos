export interface SearchIndexItem {
  titulo: string;
  rota: string;
  keywords: string[];
}

export const NAVBAR_SEARCH_INDEX: SearchIndexItem[] = [
  {
    titulo: 'Sobre os Jogadores Anônimos',
    rota: '/sobre',
    keywords: ['sobre', 'quem somos', 'o que é', 'irmandade', 'associação', 'grupo de apoio']
  },
  {
    titulo: 'História',
    rota: '/historia',
    keywords: ['história', 'origem', 'fundação', 'como surgiu']
  },
  {
    titulo: 'Anonimato',
    rota: '/anonimato',
    keywords: ['anonimato', 'privacidade', 'sigilo', 'identidade', 'anônimo']
  },
  {
    titulo: 'Jogo Compulsivo',
    rota: '/jogo-compulsivo',
    keywords: [
      'jogo compulsivo',
      'ludopatia',
      'vício em jogo',
      'jogo patológico',
      'sou viciado',
      'aposta',
      'apostas'
    ]
  },
  {
    titulo: 'Uma Nova Chance',
    rota: '/uma-nova-chance',
    keywords: ['uma nova chance', 'recomeçar', 'esperança', 'segunda chance', 'nova vida']
  },
  {
    titulo: 'Programa de Recuperação (Os 12 Passos)',
    rota: '/programa-de-recuperacao',
    keywords: ['programa de recuperação', '12 passos', 'doze passos', 'como se recuperar', 'tratamento']
  },
  {
    titulo: 'Relatos',
    rota: '/relatos',
    keywords: ['relatos', 'depoimentos', 'histórias de superação', 'testemunhos', 'experiências']
  },
  {
    titulo: 'Programa da Unidade (12 Passos da Unidade)',
    rota: '/programa-de-unidade',
    keywords: ['programa da unidade', '12 passos da unidade', 'doze passos da unidade', 'unidade do grupo']
  },
  {
    titulo: 'Perguntas Frequentes',
    rota: '/perguntas-e-respostas',
    keywords: ['perguntas e respostas', 'faq', 'dúvidas', 'perguntas frequentes']
  },
  {
    titulo: '20 Perguntas',
    rota: '/vinte-perguntas',
    keywords: ['20 perguntas', 'vinte perguntas', 'sou jogador', 'sou jogador compulsivo', 'teste', 'autoavaliação']
  },
  {
    titulo: 'Literatura',
    rota: '/literatura',
    keywords: ['literatura', 'livros', 'materiais', 'publicações', 'textos', 'folhetos']
  },
  {
    titulo: 'Avisos',
    rota: '/avisos',
    keywords: ['avisos', 'comunicados', 'notícias', 'informativos']
  },
  {
    titulo: 'Endereços',
    rota: '/enderecos',
    keywords: [
      'endereços',
      'escritórios regionais',
      'regional',
      'contato regional',
      'onde fica',
      'grupos de apoio',
      'reuniões',
      'encontrar reunião',
      'encontrar uma reunião',
      'grupo',
      'grupos',
      'onde tem reunião',
      'reunião perto de mim'
    ]
  },
  {
    titulo: 'Escritórios',
    rota: '/escritorios',
    keywords: ['escritórios', 'escritório regional', 'sede', 'administrativo', 'contato regional']
  },
  {
    titulo: 'Reflexão do Dia',
    rota: '/reflexao-do-dia',
    keywords: ['reflexão do dia', 'reflexão diária', 'pensamento do dia', 'mensagem do dia']
  },
  {
    titulo: 'Linhas de Ajuda',
    rota: '/hotlines',
    keywords: [
      'hotlines',
      'linha de ajuda',
      'linhas de ajuda',
      'socorro',
      'emergência',
      'telefone de ajuda',
      'preciso de ajuda',
      'ajuda urgente'
    ]
  },
  {
    titulo: 'Contribuição',
    rota: '/contribuir',
    keywords: ['contribuir', 'contribuição', 'doação', 'doar', 'ajudar financeiramente']
  },
  {
    titulo: 'Loja de Livros',
    rota: '/loja',
    keywords: ['loja', 'livros', 'comprar', 'material impresso']
  },
  {
    titulo: 'Mural de Apoio',
    rota: '/mural-de-apoio',
    keywords: ['mural de apoio', 'mural', 'mensagens de apoio', 'apoio mútuo']
  }
];

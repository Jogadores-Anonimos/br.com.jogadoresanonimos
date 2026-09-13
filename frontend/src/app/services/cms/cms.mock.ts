import { CmsPaginaPublica } from './cms.model';

export const CMS_PAGINAS_MOCK: CmsPaginaPublica[] = [
  {
    slug: 'sobre-nos',
    titulo: 'Sobre Nós',
    blocos: [
      {
        ordem: 1,
        tipo: 'texto',
        conteudo: '<p>Somos uma associação de apoio a jogadores compulsivos, formada por <b>membros que se ajudam mutuamente</b>.</p>',
      },
      {
        ordem: 2,
        tipo: 'imagem',
        imagemId: 'mock-imagem-1',
        url: '/images/home/reuniao-discussao.jpg',
        alt: 'Grupo de apoio reunido em círculo',
      },
      {
        ordem: 3,
        tipo: 'arquivo',
        arquivoId: 'mock-arquivo-1',
        titulo: 'As Doze Etapas',
        descricao: 'Texto de referência do programa de recuperação.',
        urlDownload: '/api/v1/arquivos/mock-arquivo-1/download',
      },
    ],
  },
  {
    slug: 'literatura',
    titulo: 'Literatura',
    blocos: [
      {
        ordem: 1,
        tipo: 'arquivo',
        arquivoId: 'mock-arquivo-1',
        titulo: 'As Doze Etapas',
        descricao: 'Texto de referência do programa de recuperação, em PDF para leitura ou impressão.',
        urlDownload: '/api/v1/arquivos/mock-arquivo-1/download',
      },
    ],
  },
  {
    slug: 'como-funciona',
    titulo: 'Como Funciona',
    blocos: [
      {
        ordem: 1,
        tipo: 'texto',
        conteudo: '<p>As reuniões acontecem semanalmente, presenciais e on-line. Veja o <a href="/reunioes">calendário de reuniões</a>.</p>',
      },
    ],
  },
];

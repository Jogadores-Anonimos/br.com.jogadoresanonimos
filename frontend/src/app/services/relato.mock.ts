// TODO: substituir por chamada real quando o endpoint estiver disponível
// Mocks centralizados do domínio Relato — ver contrato em backend/docs/api/contrato-relatos.md
import { RelatoAdmin } from './relato.model';

// relatos fictícios e genéricos, sem nenhum dado sensível real
export const RELATOS_MOCK: RelatoAdmin[] = [
  {
    id: 'relato-1',
    texto:
      'Cheguei ao grupo sem acreditar que conseguiria parar. Hoje, com a ajuda dos encontros semanais e do apoio dos outros membros, completei alguns meses de recuperação. Cada reunião me lembra que não preciso enfrentar isso sozinho.',
    nomeExibicao: 'Marcos',
    status: 'approved',
    dataCriacao: '2026-07-05T13:20:00.000Z',
    dataAtualizacao: '2026-07-05T13:25:00.000Z',
  },
  {
    id: 'relato-2',
    texto:
      'Demorei anos para admitir que precisava de ajuda. Encontrar um espaço onde eu podia falar abertamente, sem julgamento, mudou minha forma de encarar a recuperação. Ainda é um processo diário, mas hoje sinto que tenho ferramentas para seguir em frente.',
    nomeExibicao: null,
    status: 'approved',
    dataCriacao: '2026-07-02T09:45:00.000Z',
    dataAtualizacao: '2026-07-02T09:50:00.000Z',
  },
  {
    id: 'relato-3',
    texto:
      'O primeiro passo foi o mais difícil, mas participar das reuniões me deu uma rotina e pessoas em quem confiar. Reconstruir a confiança da minha família está sendo gradual, e sei que não estou sozinho nessa caminhada.',
    nomeExibicao: 'J.',
    status: 'approved',
    dataCriacao: '2026-06-27T18:10:00.000Z',
    dataAtualizacao: '2026-06-27T18:15:00.000Z',
  },
  {
    id: 'relato-4',
    texto:
      'Nunca imaginei que dividir minha história com estranhos pudesse trazer tanto alívio. O grupo me ensinou que recaídas fazem parte do processo e que o importante é continuar tentando, um dia de cada vez.',
    nomeExibicao: 'Ana',
    status: 'approved',
    dataCriacao: '2026-06-18T11:00:00.000Z',
    dataAtualizacao: '2026-06-18T11:05:00.000Z',
  },
  {
    id: 'relato-5',
    texto:
      'Ainda estou no começo dessa jornada, mas escrever sobre o que estou sentindo já está me ajudando a organizar os pensamentos antes mesmo de participar de uma reunião presencial.',
    nomeExibicao: null,
    status: 'pending',
    dataCriacao: '2026-07-10T08:30:00.000Z',
    dataAtualizacao: '2026-07-10T08:30:00.000Z',
  },
];

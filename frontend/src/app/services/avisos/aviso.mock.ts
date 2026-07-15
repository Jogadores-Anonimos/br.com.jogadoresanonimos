import { AvisoAdmin } from './aviso.model';

export const AVISOS_MOCK: AvisoAdmin[] = [
  {
    id: 'aviso-1',
    titulo: 'Canal de apoio disponível 24 horas',
    corpo:
      'Lembramos que nosso canal de apoio por chat está disponível 24 horas por dia, todos os dias, para quem precisar conversar com alguém a qualquer momento.',
    dataPublicacao: '2026-07-01T09:00:00.000Z',
    fixado: true,
    dataExpiracao: null,
    expirado: false,
  },
  {
    id: 'aviso-2',
    titulo: 'Novo grupo de apoio na região Sul',
    corpo:
      'Um novo grupo de apoio presencial foi criado na região Sul do país. Entre em contato pelos nossos canais para saber o horário e o local das reuniões.',
    dataPublicacao: '2026-06-28T14:30:00.000Z',
    fixado: false,
    dataExpiracao: null,
    expirado: false,
  },
  {
    id: 'aviso-3',
    titulo: 'Mudança no horário das reuniões de quarta-feira',
    corpo:
      'As reuniões que aconteciam às 19h nas quartas-feiras passam a acontecer às 20h a partir da próxima semana, para facilitar a participação de mais pessoas.',
    dataPublicacao: '2026-06-20T11:00:00.000Z',
    fixado: false,
    dataExpiracao: null,
    expirado: false,
  },
  {
    id: 'aviso-4',
    titulo: 'Manutenção programada do site',
    corpo:
      'No próximo sábado, das 2h às 4h da manhã, o site passará por uma manutenção programada e poderá ficar indisponível por alguns minutos.',
    dataPublicacao: '2026-06-15T08:00:00.000Z',
    fixado: false,
    dataExpiracao: '2026-07-15T00:00:00.000Z',
    expirado: true,
  },
  {
    id: 'aviso-5',
    titulo: 'Encontro regional de recuperação em setembro',
    corpo:
      'Estamos organizando um encontro regional de recuperação para o mês de setembro, reunindo diversos grupos de apoio. Mais informações em breve.',
    dataPublicacao: '2026-06-10T16:45:00.000Z',
    fixado: false,
    dataExpiracao: null,
    expirado: false,
  },
  {
    id: 'aviso-6',
    titulo: 'Reforço: seu anonimato é prioridade',
    corpo:
      'Reforçamos que nenhuma informação pessoal é solicitada ou compartilhada em nossos grupos e canais de apoio. Sua identidade está sempre protegida.',
    dataPublicacao: '2026-05-30T10:15:00.000Z',
    fixado: false,
    dataExpiracao: null,
    expirado: false,
  },
];

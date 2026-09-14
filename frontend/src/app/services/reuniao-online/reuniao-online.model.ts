import { DiaSemanaAgendaReuniao } from '../reuniao/reuniao.model';

export interface AgendaItemReuniaoOnline {
  diaSemana: DiaSemanaAgendaReuniao;
  horario: string;
}

export type CorReuniaoOnline =
  | 'roxo'
  | 'laranja'
  | 'amarelo'
  | 'azul'
  | 'verde'
  | 'branco'
  | 'magenta'
  | 'cinza'
  | 'rosa'
  | 'preto'
  | 'vinho'
  | 'vermelho';

export interface ReuniaoOnline {
  id: string;
  nome: string;
  telefone: string | null;
  descricao: string | null;
  videoUrl: string | null;
  panfletoImagemId: string | null;
  panfletoImagemUrl: string | null;
  agenda: AgendaItemReuniaoOnline[];
  cor: CorReuniaoOnline;
  atualizadoEm: string;
}

export interface CriarReuniaoOnlineRequest {
  nome: string;
  descricao?: string;
  videoUrl?: string;
  panfletoImagemId?: string;
  agenda: AgendaItemReuniaoOnline[];
  cor?: CorReuniaoOnline;
}

export type EditarReuniaoOnlineRequest = CriarReuniaoOnlineRequest;

export type CorReuniaoOnlineApi =
  | 'ROXO'
  | 'LARANJA'
  | 'AMARELO'
  | 'AZUL'
  | 'VERDE'
  | 'BRANCO'
  | 'MAGENTA'
  | 'CINZA'
  | 'ROSA'
  | 'PRETO'
  | 'VINHO'
  | 'VERMELHO';

export interface ReuniaoOnlineApi extends Omit<ReuniaoOnline, 'cor'> {
  cor: CorReuniaoOnlineApi;
}

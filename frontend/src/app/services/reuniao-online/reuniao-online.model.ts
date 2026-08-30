import { DiaSemanaAgendaReuniao } from '../reuniao/reuniao.model';

export interface AgendaItemReuniaoOnline {
  diaSemana: DiaSemanaAgendaReuniao;
  horario: string;
}

export interface ReuniaoOnline {
  id: string;
  nome: string;
  telefone: string | null;
  agenda: AgendaItemReuniaoOnline[];
  atualizadoEm: string;
}

export interface CriarReuniaoOnlineRequest {
  nome: string;
  telefone?: string;
  agenda: AgendaItemReuniaoOnline[];
}

export type EditarReuniaoOnlineRequest = CriarReuniaoOnlineRequest;

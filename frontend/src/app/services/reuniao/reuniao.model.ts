export type CategoriaReuniao = 'presencial' | 'virtual';

export interface Reuniao {
  id: string;
  nome: string;
  categoria: CategoriaReuniao;
  descricao: string;
  dia: string;
  horario: string;
  local: string;
  whatsapp: string | null;
  diaSemana: string | null;
  periodo: string | null;
  latitude: number | null;
  longitude: number | null;
}

export interface CriarReuniaoRequest {
  nome: string;
  categoria: CategoriaReuniao;
  descricao: string;
  dia: string;
  horario: string;
  local: string;
  whatsapp?: string;
  diaSemana?: string;
  periodo?: string;
  latitude?: number;
  longitude?: number;
}

export type EditarReuniaoRequest = CriarReuniaoRequest;

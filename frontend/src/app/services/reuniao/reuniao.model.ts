export type CategoriaReuniao = 'presencial' | 'virtual';
export type DiaSemanaAgendaReuniao = 'DOMINGO' | 'SEGUNDA' | 'TERCA' | 'QUARTA' | 'QUINTA' | 'SEXTA' | 'SABADO';

export interface AgendaItemReuniao {
  diaSemana: DiaSemanaAgendaReuniao;
  horario: string;
}

export interface AvisoReuniao {
  codigo: string;
  mensagem: string;
}

export interface Reuniao {
  id: string;
  nome: string;
  categoria: CategoriaReuniao;
  descricao: string;
  agenda: AgendaItemReuniao[];
  dia: string;
  horario: string;
  local: string;
  whatsapp: string | null;
  diaSemana: string | null;
  periodo: string | null;
  latitude: number | null;
  longitude: number | null;
  cep: string | null;
  numero: string | null;
  logradouro: string | null;
  bairro: string | null;
  cidade: string | null;
  estado: string | null;
  linkReuniao: string | null;
  avisos: AvisoReuniao[];
}

export interface CriarReuniaoRequest {
  nome: string;
  categoria: CategoriaReuniao;
  descricao: string;
  agenda?: AgendaItemReuniao[];
  dia?: string;
  horario?: string;
  local: string;
  whatsapp?: string;
  diaSemana?: string;
  periodo?: string;
  latitude?: number;
  longitude?: number;
  cep?: string;
  numero?: string;
  logradouro?: string;
  bairro?: string;
  cidade?: string;
  estado?: string;
  linkReuniao?: string;
}

export type EditarReuniaoRequest = CriarReuniaoRequest;

export interface BackfillGeolocalizacaoFalha {
  reuniaoId: string;
  motivo: string;
}

export interface BackfillGeolocalizacaoResponse {
  totalElegiveis: number;
  atualizadas: number;
  falhas: number;
  detalhesFalha: BackfillGeolocalizacaoFalha[];
}

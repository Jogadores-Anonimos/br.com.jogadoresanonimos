export type CategoriaReuniao = 'presencial' | 'virtual';
export type DiaSemanaAgendaReuniao = 'DOMINGO' | 'SEGUNDA' | 'TERCA' | 'QUARTA' | 'QUINTA' | 'SEXTA' | 'SABADO';

export interface AgendaItemReuniao {
  diaSemana: DiaSemanaAgendaReuniao;
  horario: string;
}

/**
 * Uma agenda pode conter mais de um item para o mesmo dia, desde que os
 * horários sejam diferentes.
 */
export type AgendaReuniao = AgendaItemReuniao[];

export interface AvisoReuniao {
  codigo: string;
  mensagem: string;
}

export interface Reuniao {
  id: string;
  nome: string;
  categoria: CategoriaReuniao;
  descricao: string;
  agenda: AgendaReuniao;
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
  /** Ausente ou nulo para reuniões legadas e virtuais não vinculadas. */
  grupoIntergrupoId?: string | null;
  /** Versão canônica usada no controle de concorrência otimista. */
  atualizadoEm?: string | null;
}

export interface CriarReuniaoRequest {
  nome: string;
  categoria: CategoriaReuniao;
  descricao: string;
  agenda?: AgendaReuniao;
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
  grupoIntergrupoId?: string;
}

export type EditarReuniaoRequest = Omit<CriarReuniaoRequest, 'grupoIntergrupoId'> & {
  atualizadoEm?: string;
};

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

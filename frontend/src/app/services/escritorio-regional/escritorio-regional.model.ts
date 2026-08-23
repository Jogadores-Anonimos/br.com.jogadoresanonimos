export interface UnidadeFederativa {
  sigla: string;
  nome: string;
}

export interface UnidadeFederativaComRegiao {
  uf: string;
  nome: string;
  codigoRegiao: string;
}

export const UNIDADES_FEDERATIVAS: UnidadeFederativa[] = [
  { sigla: 'AC', nome: 'Acre' },
  { sigla: 'AL', nome: 'Alagoas' },
  { sigla: 'AP', nome: 'Amapá' },
  { sigla: 'AM', nome: 'Amazonas' },
  { sigla: 'BA', nome: 'Bahia' },
  { sigla: 'CE', nome: 'Ceará' },
  { sigla: 'DF', nome: 'Distrito Federal' },
  { sigla: 'ES', nome: 'Espírito Santo' },
  { sigla: 'GO', nome: 'Goiás' },
  { sigla: 'MA', nome: 'Maranhão' },
  { sigla: 'MT', nome: 'Mato Grosso' },
  { sigla: 'MS', nome: 'Mato Grosso do Sul' },
  { sigla: 'MG', nome: 'Minas Gerais' },
  { sigla: 'PA', nome: 'Pará' },
  { sigla: 'PB', nome: 'Paraíba' },
  { sigla: 'PR', nome: 'Paraná' },
  { sigla: 'PE', nome: 'Pernambuco' },
  { sigla: 'PI', nome: 'Piauí' },
  { sigla: 'RJ', nome: 'Rio de Janeiro' },
  { sigla: 'RN', nome: 'Rio Grande do Norte' },
  { sigla: 'RS', nome: 'Rio Grande do Sul' },
  { sigla: 'RO', nome: 'Rondônia' },
  { sigla: 'RR', nome: 'Roraima' },
  { sigla: 'SC', nome: 'Santa Catarina' },
  { sigla: 'SP', nome: 'São Paulo' },
  { sigla: 'SE', nome: 'Sergipe' },
  { sigla: 'TO', nome: 'Tocantins' },
];

export type DiaSemana = 'DOMINGO' | 'SEGUNDA' | 'TERCA' | 'QUARTA' | 'QUINTA' | 'SEXTA' | 'SABADO';

export interface AgendaItemGrupo {
  diaSemana: DiaSemana;
  horario: string;
}

export interface GrupoIntergrupoTelefone {
  id: string;
  label: string | null;
  telefone: string;
}

export interface EscritorioTelefone {
  id: string;
  label: string | null;
  telefone: string;
}

export interface EscritorioServico {
  id: string;
  nome: string;
  endereco: string;
  complemento: string | null;
  bairro: string | null;
  cidade: string;
  emails: string[];
  telefones: EscritorioTelefone[];
  ordem: number;
  criadoEm: string;
  atualizadoEm: string;
}

export interface GrupoIntergrupo {
  id: string;
  estadoId: string;
  nome: string;
  endereco: string;
  bairro: string | null;
  cidade: string;
  telefones: GrupoIntergrupoTelefone[];
  agenda: AgendaItemGrupo[];
  ordem: number;
  criadoEm: string;
  atualizadoEm: string;
}

export interface EstadoIntergrupo {
  id: string;
  regiaoId: string;
  uf: string;
  nome: string;
  ordem: number;
  grupos: GrupoIntergrupo[];
  criadoEm: string;
  atualizadoEm: string;
}

export interface RegiaoIntergrupo {
  id: string;
  codigo: string;
  nome: string;
  ordem: number;
  estados: EstadoIntergrupo[];
  criadoEm: string;
  atualizadoEm: string;
}

export interface EscritoriosRegionais {
  escritorios: EscritorioServico[];
  regioes: RegiaoIntergrupo[];
}

export interface CriarRegiaoRequest {
  nome: string;
}

export type EditarRegiaoRequest = CriarRegiaoRequest;

export interface CriarEstadoRequest {
  uf: string;
}

export type EditarEstadoRequest = CriarEstadoRequest;

export interface CriarGrupoTelefoneRequest {
  label?: string;
  telefone: string;
}

export interface CriarGrupoRequest {
  nome: string;
  endereco: string;
  bairro?: string;
  cidade: string;
  agenda: AgendaItemGrupo[];
  telefones: CriarGrupoTelefoneRequest[];
  cep?: string;
  numero?: string;
  logradouro?: string;
  estado?: string;
  latitude?: number;
  longitude?: number;
}

export interface EditarGrupoRequest extends CriarGrupoRequest {
  cep?: string;
  numero?: string;
  logradouro?: string;
  estado?: string;
  latitude?: number;
  longitude?: number;
  /** Versão canônica da reunião vinculada; opcional para grupos legados. */
  atualizadoEm?: string;
}

export interface CriarEnderecoGrupoRequest {
  uf: string;
  nome: string;
  endereco: string;
  bairro?: string;
  cidade: string;
  agenda: AgendaItemGrupo[];
  telefones: CriarGrupoTelefoneRequest[];
  cep?: string;
  numero?: string;
  logradouro?: string;
  estado?: string;
  latitude?: number;
  longitude?: number;
}

export interface CriarEscritorioTelefoneRequest {
  label?: string;
  telefone: string;
}

export interface CriarEscritorioServicoRequest {
  nome: string;
  endereco: string;
  complemento?: string;
  bairro?: string;
  cidade: string;
  emails: string[];
  telefones: CriarEscritorioTelefoneRequest[];
}

export type EditarEscritorioServicoRequest = CriarEscritorioServicoRequest;

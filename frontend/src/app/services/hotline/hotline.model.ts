import { UnidadeFederativa } from '../escritorio-regional/escritorio-regional.model';

export { UNIDADES_FEDERATIVAS } from '../escritorio-regional/escritorio-regional.model';
export type { UnidadeFederativa };

export interface HotlineTelefone {
  id: string;
  telefone: string;
}

export interface HotlineRegiao {
  id: string;
  uf: string;
  cidade: string;
  telefones: HotlineTelefone[];
}

export interface CriarRegiaoRequest {
  uf: string;
  cidade: string;
}

export type EditarRegiaoRequest = CriarRegiaoRequest;

export interface CriarTelefoneRequest {
  telefone: string;
}

export type EditarTelefoneRequest = CriarTelefoneRequest;

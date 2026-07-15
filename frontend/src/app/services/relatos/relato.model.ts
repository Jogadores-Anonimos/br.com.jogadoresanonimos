export type StatusRelato = 'pending' | 'approved' | 'rejected';

export interface RelatoPublico {
  id: string;
  texto: string;
  nomeExibicao: string | null;
  dataCriacao: string;
}

export interface RelatoAdmin extends RelatoPublico {
  status: StatusRelato;
  dataAtualizacao: string;
}

export interface EnviarRelatoRequest {
  texto: string;
  nomeExibicao?: string;
}

export interface RelatoEnvio {
  id: string;
  status: StatusRelato;
}

export interface EditarRelatoRequest {
  texto: string;
}

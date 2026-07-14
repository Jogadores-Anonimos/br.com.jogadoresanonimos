// Contratos de dados do domínio Hotline (Linhas de Ajuda) — ver
// backend/docs/api/contrato-hotlines.md

export interface HotlineTelefone {
  id: string;
  label: string;
  telefone: string;
}

export interface HotlineRegiao {
  id: string;
  nome: string;
  telefones: HotlineTelefone[];
}

export interface CriarRegiaoRequest {
  nome: string;
}

export interface CriarTelefoneRequest {
  label: string;
  telefone: string;
}

export type EditarTelefoneRequest = CriarTelefoneRequest;

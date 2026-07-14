// Contratos de dados do domínio Aviso — ver backend/docs/api/contrato-avisos.md

export interface AvisoPublico {
  id: string;
  titulo: string;
  corpo: string;
  dataPublicacao: string;
  fixado: boolean;
}

export interface AvisoAdmin extends AvisoPublico {
  dataExpiracao: string | null;
  expirado: boolean;
}

export interface CriarAvisoRequest {
  titulo: string;
  corpo: string;
  fixado?: boolean;
  dataExpiracao?: string | null;
}

export type EditarAvisoRequest = CriarAvisoRequest;

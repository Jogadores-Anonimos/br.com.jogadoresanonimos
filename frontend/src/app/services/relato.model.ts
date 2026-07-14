// Contratos de dados do domínio Relato — ver backend/docs/api/contrato-relatos.md
// Anonimato: nunca existe autorId/apelido/e-mail em nenhum DTO de resposta (decisão de arquitetura, ver contrato).

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

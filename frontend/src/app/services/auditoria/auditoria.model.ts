export type AcaoAuditoria = 'APROVAR' | 'REJEITAR' | 'REVERTER' | 'REMOVER' | 'EDITAR';

export interface AuditoriaLogAdmin {
  id: string;
  adminId: string;
  entidade: string;
  entidadeId: string;
  acao: AcaoAuditoria;
  statusAnterior: string | null;
  statusNovo: string | null;
  criadoEm: string;
}

export interface AuditoriaFiltros {
  entidade?: string;
  acao?: AcaoAuditoria;
  dataInicio?: string;
  dataFim?: string;
}

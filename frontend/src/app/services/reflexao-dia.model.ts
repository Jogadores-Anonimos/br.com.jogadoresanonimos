// Contratos de dados do domínio Reflexão do Dia — ver
// backend/docs/api/contrato-reflexao-do-dia.md

export interface ReflexaoDia {
  mes: number; // 1-12
  dia: number; // 1-31, conforme o mês
  texto: string;
}

export interface ReflexaoDiaDoMes {
  dia: number;
  texto: string | null;
}

export interface SalvarReflexaoDiaRequest {
  texto: string;
}

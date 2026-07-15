export interface ReflexaoDia {
  mes: number;
  dia: number;
  texto: string;
}

export interface ReflexaoDiaDoMes {
  dia: number;
  texto: string | null;
}

export interface SalvarReflexaoDiaRequest {
  texto: string;
}

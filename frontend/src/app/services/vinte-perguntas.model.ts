// Contrato do módulo dedicado de Vinte Perguntas — ver
// backend/docs/api/contrato-cms-paginas-institucionais-vinte-perguntas.md

export interface VinteQuestaoResponse {
  id: string;
  numero: number;
  ordem: number;
  texto: string;
}

export interface VinteQuestaoUpdateRequest {
  texto: string;
}

export interface VinteQuestaoCriarRequest {
  texto: string;
}

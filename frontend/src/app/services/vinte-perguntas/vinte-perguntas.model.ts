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

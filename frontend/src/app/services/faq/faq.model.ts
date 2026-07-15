export interface Faq {
  id: string;
  pergunta: string;
  resposta: string;
  ordem: number;
}

export interface CriarFaqRequest {
  pergunta: string;
  resposta: string;
}

export type EditarFaqRequest = CriarFaqRequest;

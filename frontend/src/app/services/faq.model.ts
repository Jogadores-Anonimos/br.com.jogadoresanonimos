// Contratos de dados do domínio FAQ (Perguntas e Respostas) — ver
// backend/docs/api/contrato-faq.md (Revisão 2026-07-13)

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

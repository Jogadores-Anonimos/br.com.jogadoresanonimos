// TODO: substituir por chamada real quando o endpoint estiver disponível
// Mocks centralizados do domínio Hotline — ver contrato em backend/docs/api/contrato-hotlines.md
// Regiões replicadas do array antes hardcoded em pages/hotlines/hotlines.ts (telefones ainda "(a definir)").
import { HotlineRegiao } from './hotline.model';

const NOMES_REGIOES = [
  'Bahia',
  'Distrito Federal',
  'Goiás',
  'Minas Gerais',
  'Paraná',
  'Pernambuco',
  'Rio de Janeiro',
  'Rio Grande do Sul',
  'Santa Catarina',
  'São Paulo',
];

export const HOTLINES_MOCK: HotlineRegiao[] = NOMES_REGIOES.map((nome, indice) => ({
  id: `mock-regiao-${indice + 1}`,
  nome,
  telefones: [{ id: `mock-telefone-${indice + 1}`, label: 'Linha de apoio', telefone: '(a definir)' }],
}));

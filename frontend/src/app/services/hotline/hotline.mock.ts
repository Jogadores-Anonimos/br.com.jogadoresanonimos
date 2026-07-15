import { HotlineRegiao } from './hotline.model';

const REGIOES_BASE: Array<{ uf: string; cidade: string }> = [
  { uf: 'BA', cidade: 'Salvador' },
  { uf: 'DF', cidade: 'Brasília' },
  { uf: 'GO', cidade: 'Goiânia' },
  { uf: 'MG', cidade: 'Belo Horizonte' },
  { uf: 'PR', cidade: 'Curitiba' },
  { uf: 'PE', cidade: 'Recife' },
  { uf: 'RJ', cidade: 'Rio de Janeiro' },
  { uf: 'RS', cidade: 'Porto Alegre' },
  { uf: 'SC', cidade: 'Florianópolis' },
  { uf: 'SP', cidade: 'São Paulo' },
];

export const HOTLINES_MOCK: HotlineRegiao[] = REGIOES_BASE.map((regiao, indice) => ({
  id: `mock-regiao-${indice + 1}`,
  uf: regiao.uf,
  cidade: regiao.cidade,
  telefones: [{ id: `mock-telefone-${indice + 1}`, telefone: '(a definir)' }],
}));

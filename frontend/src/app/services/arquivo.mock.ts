// TODO: substituir por chamada real quando o endpoint estiver disponível
// Mocks centralizados do domínio Arquivo (literatura em PDF) — ver
// contrato em backend/docs/api/contrato-auth-cms-upload.md (Bloco 4)
import { ArquivoResponse } from './arquivo.model';

export const ARQUIVOS_MOCK: ArquivoResponse[] = [
  {
    id: 'mock-arquivo-1',
    nomeOriginal: 'as-doze-etapas.pdf',
    tamanhoBytes: 512000,
    titulo: 'As Doze Etapas',
    descricao: 'Texto de referência do programa de recuperação.',
    urlDownload: '/api/v1/arquivos/mock-arquivo-1/download',
    thumbnailUrl: null,
    criadoEm: '2026-06-15T10:00:00Z',
  },
];

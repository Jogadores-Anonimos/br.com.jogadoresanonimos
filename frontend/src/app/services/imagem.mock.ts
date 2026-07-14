// TODO: substituir por chamada real quando o endpoint estiver disponível
// Mocks centralizados do domínio Imagem — ver
// backend/src/main/java/br/com/jogadoresanonimos/imagem/controller/ImagemController.java
import { ImagemResponse } from './imagem.model';

export const IMAGENS_MOCK: ImagemResponse[] = [
  {
    id: 'mock-imagem-1',
    nomeOriginal: 'capa-home.jpg',
    tamanhoBytes: 204800,
    alt: 'Grupo de apoio reunido em roda de conversa',
    url: '/mock/imagens/capa-home.jpg',
    criadoEm: '2026-06-15T10:00:00Z',
  },
];

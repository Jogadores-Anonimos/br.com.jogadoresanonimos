// Contrato do módulo de Imagem (upload/remoção para uso em CMS) — ver
// backend/src/main/java/br/com/jogadoresanonimos/imagem/controller/ImagemController.java

export interface ImagemResponse {
  id: string;
  nomeOriginal: string;
  tamanhoBytes: number;
  alt: string;
  url: string;
  criadoEm: string;
}

export interface ImagemUploadRequest {
  arquivo: File;
  alt: string;
}

export interface MensagemResponse {
  mensagem: string;
}

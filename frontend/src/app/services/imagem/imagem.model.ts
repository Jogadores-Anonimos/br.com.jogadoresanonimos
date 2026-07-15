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

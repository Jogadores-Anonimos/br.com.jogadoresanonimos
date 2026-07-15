export interface ArquivoResponse {
  id: string;
  nomeOriginal: string;
  tamanhoBytes: number;
  titulo: string;
  descricao: string | null;
  urlDownload: string;
  thumbnailUrl: string | null;
  criadoEm: string;
}

export interface ArquivoUploadRequest {
  arquivo: File;
  titulo: string;
  descricao?: string;
}

export interface EditarArquivoRequest {
  titulo: string;
  descricao?: string;
}

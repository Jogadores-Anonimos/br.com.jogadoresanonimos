export type CmsBlocoTipo = 'texto' | 'imagem' | 'arquivo';

export interface CmsBlocoTexto {
  ordem: number;
  tipo: 'texto';
  conteudo: string;
}

export interface CmsBlocoImagem {
  ordem: number;
  tipo: 'imagem';
  imagemId: string;
  url?: string;
  alt: string;
}

export interface CmsBlocoArquivo {
  ordem: number;
  tipo: 'arquivo';
  arquivoId: string;
  titulo?: string;
  descricao?: string | null;
  urlDownload?: string;
}

export type CmsBloco = CmsBlocoTexto | CmsBlocoImagem | CmsBlocoArquivo;

export interface CmsPaginaPublica {
  slug: string;
  titulo: string;
  blocos: CmsBloco[];
}

export interface CmsPaginaAdminResumo {
  slug: string;
  titulo: string;
  atualizadoEm: string;
  atualizadoPor: string;
}

export interface CmsPaginaAdminDetalhe extends CmsPaginaPublica {
  atualizadoEm: string;
  atualizadoPor: string;
}

export interface CmsPaginaUpdateRequest {
  blocos: CmsBloco[];
}

export interface CmsPaginaCreateRequest extends CmsPaginaUpdateRequest {
  slug: string;
  titulo: string;
}

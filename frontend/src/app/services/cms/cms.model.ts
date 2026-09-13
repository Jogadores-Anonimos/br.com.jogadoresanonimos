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

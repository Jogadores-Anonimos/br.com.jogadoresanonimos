// Contratos de dados do domínio CMS leve — ver
// backend/docs/api/contrato-auth-cms-upload.md (Bloco 2)

export type CmsBlocoTipo = 'texto' | 'imagem' | 'arquivo';

export interface CmsBlocoTexto {
  ordem: number;
  tipo: 'texto';
  conteudo: string; // HTML sanitizado no backend (allowlist b/i/a/p/br)
}

export interface CmsBlocoImagem {
  ordem: number;
  tipo: 'imagem';
  imagemId: string;
  url?: string; // presente na leitura, ausente ao enviar update (só referência)
  alt: string;
}

export interface CmsBlocoArquivo {
  ordem: number;
  tipo: 'arquivo';
  arquivoId: string;
  // titulo/descricao/urlDownload vêm da entidade Arquivo associada,
  // presentes na leitura, ausentes ao enviar update (só referência)
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
  titulo: string;
  blocos: CmsBloco[];
}

export interface CmsPaginaCreateRequest extends CmsPaginaUpdateRequest {
  slug: string;
}

// Contratos de dados do domínio Mural de Apoio — ver
// backend/docs/api/contrato-mural-de-apoio.md

export interface BannerMuralPublico {
  id: string;
  imagemUrl: string;
  altText: string;
  legenda: string | null;
  linkDestino: string | null;
  dataPublicacao: string;
}

export interface BannerMuralAdmin extends BannerMuralPublico {
  imagemId: string;
  ativo: boolean;
  dataAtualizacao: string;
}

export interface CriarBannerMuralRequest {
  imagemId: string;
  altText: string;
  legenda?: string;
  linkDestino?: string;
}

export interface EditarBannerMuralRequest extends CriarBannerMuralRequest {
  ativo: boolean;
}

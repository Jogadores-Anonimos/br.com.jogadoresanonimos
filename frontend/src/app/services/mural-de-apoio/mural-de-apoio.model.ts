export interface BannerMuralPublico {
  id: string;
  imagemUrl: string | null;
  videoUrl: string | null;
  altText: string;
  legenda: string | null;
  linkDestino: string | null;
  dataPublicacao: string;
}

export interface BannerMuralAdmin extends BannerMuralPublico {
  imagemId: string | null;
  ativo: boolean;
  dataAtualizacao: string;
}

export interface CriarBannerMuralRequest {
  imagemId?: string;
  videoUrl?: string;
  altText: string;
  legenda?: string;
  linkDestino?: string;
}

export interface EditarBannerMuralRequest extends CriarBannerMuralRequest {
  ativo: boolean;
}

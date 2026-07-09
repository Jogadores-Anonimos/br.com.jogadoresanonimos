import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, delay, map } from 'rxjs/operators';

// TODO: substituir por chamada real (this.http.get<MuralDeApoioResponse>('/api/mural-de-apoio')) mantendo o contrato público
//
// Pendência: upload de imagem precisa remover metadados EXIF antes de salvar (ver CLAUDE.md); responsabilidade de backend/security, não deste service.
//
// Anonimato: cartazes trazem apenas dado institucional do grupo (endereço, telefone de contato), nunca de pessoa física.

// sem campo de quem administra/publicou o banner — anonimato proposital
export interface BannerMuralApoio {
  id: string;
  imagemUrl: string;
  altText: string; // obrigatório — requisito de acessibilidade
  // mantido como opcional para uso futuro não-visual; frontend não deve renderizar isso na UI (imagem já é autoexplicativa)
  legenda?: string;
  linkDestino?: string;
  dataPublicacao: string; // ISO
  ativo: boolean;
}

export interface MuralDeApoioResponse {
  success: boolean;
  data?: BannerMuralApoio[];
  error?: string;
}

// dados reais (não placeholder): imagens baixadas do site oficial jogadoresanonimos.com.br em 2026-07-09, versionadas em frontend/public/images/mural-de-apoio/
const MURAL_DE_APOIO_MOCK: BannerMuralApoio[] = [
  {
    id: 'banner-grupo-cuiaba',
    imagemUrl: '/images/mural-de-apoio/banner-grupo-cuiaba.jpg',
    altText:
      'Cartaz do Grupo Cuiabá de J.A. Problemas com o jogo? Já existe ajuda em Cuiabá. ' +
      'Endereço: Av. Des. Antônio Quirino de Araújo, 1126 - Bairro Poções - Cuiabá - MT. ' +
      'Reunião de abertura: quarta-feira, 29/07/2026, às 19h30. ' +
      'Contato: (21) 99472-1933. jogadoresanonimos.com.br',
    legenda: 'Grupo Cuiabá de J.A.',
    dataPublicacao: '2026-07-01T09:00:00.000Z',
    ativo: true,
  },
  {
    id: 'banner-grupo-sao-luis',
    imagemUrl: '/images/mural-de-apoio/banner-grupo-sao-luis.jpg',
    altText:
      'Cartaz do Grupo SLZ-Bequimão. Problemas com o jogo? Existe ajuda. ' +
      'Endereço: Rua 32, nº 1 - Bairro Bequimão - São Luís, MA (Igreja do Sagrado Coração de Jesus - Matriz). ' +
      'Reuniões: quintas-feiras, das 19h às 21h. ' +
      'Contato: (21) 99472-1933. jogadoresanonimos.com.br',
    legenda: 'Grupo SLZ-Bequimão',
    dataPublicacao: '2026-06-20T09:00:00.000Z',
    ativo: true,
  },
  {
    id: 'banner-grupo-ipsep-recife',
    imagemUrl: '/images/mural-de-apoio/banner-grupo-ipset-recife.jpg',
    altText:
      'Cartaz do Grupo IPSEP. Problemas com o jogo? Existe ajuda. ' +
      'Endereço: R. Jean Emile Frave, s/nº - Eren Eleanor Roosevelt, IPSEP, Recife, PE. ' +
      'Reuniões: terças-feiras, das 19h às 21h (a partir de 07/04/2026). ' +
      'Contato: (81) 98697-3867. jogadoresanonimos.com.br',
    legenda: 'Grupo IPSEP - Recife',
    dataPublicacao: '2026-03-15T09:00:00.000Z',
    ativo: true,
  },
  {
    id: 'banner-grupo-taubate',
    imagemUrl: '/images/mural-de-apoio/banner-grupo-taubate.jpg',
    altText:
      'Cartaz do Grupo Taubaté. Problemas com o jogo? Existe ajuda em Taubaté, SP. ' +
      'Endereço: Rua Maria Teresa de Moura, 150 - Estiva (Lar Bom Samaritano), Taubaté, SP. ' +
      'Inauguração: domingo, 25/01, às 9h30. Reuniões: quartas-feiras, das 19h às 21h. ' +
      'Contato: (21) 99472-1933. jogadoresanonimos.com.br',
    legenda: 'Grupo Taubaté',
    dataPublicacao: '2026-01-10T09:00:00.000Z',
    ativo: true,
  },
];

const QUANTIDADE_DESTAQUE_HOME = 3;

@Injectable({ providedIn: 'root' })
export class MuralDeApoioService {
  getBannersDestaque(): Observable<MuralDeApoioResponse> {
    return of<MuralDeApoioResponse>({ success: true, data: MURAL_DE_APOIO_MOCK }).pipe(
      delay(500),
      map((resposta) => {
        if (!resposta.data) {
          return resposta;
        }

        const destaque = resposta.data
          .filter((banner) => banner.ativo)
          .sort(
            (a, b) => new Date(b.dataPublicacao).getTime() - new Date(a.dataPublicacao).getTime(),
          )
          .slice(0, QUANTIDADE_DESTAQUE_HOME);

        return { ...resposta, data: destaque };
      }),
      catchError(() =>
        of<MuralDeApoioResponse>({
          success: false,
          error: 'Não foi possível carregar o mural de apoio no momento.',
        }),
      ),
    );
  }

  getBannersCompleto(): Observable<MuralDeApoioResponse> {
    return of<MuralDeApoioResponse>({ success: true, data: MURAL_DE_APOIO_MOCK }).pipe(
      delay(500),
      map((resposta) => {
        if (!resposta.data) {
          return resposta;
        }

        const ativos = resposta.data
          .filter((banner) => banner.ativo)
          .sort(
            (a, b) => new Date(b.dataPublicacao).getTime() - new Date(a.dataPublicacao).getTime(),
          );

        return { ...resposta, data: ativos };
      }),
      catchError(() =>
        of<MuralDeApoioResponse>({
          success: false,
          error: 'Não foi possível carregar o mural de apoio no momento.',
        }),
      ),
    );
  }
}

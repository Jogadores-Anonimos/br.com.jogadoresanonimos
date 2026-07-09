import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, delay, map } from 'rxjs/operators';

// TODO: substituir por chamada real quando o endpoint estiver disponível
// (ex: this.http.get<MuralDeApoioResponse>('/api/mural-de-apoio')). O
// contrato público deste service (nomes dos métodos, tipo de retorno
// Observable<MuralDeApoioResponse>) não deve mudar nessa troca — apenas
// a implementação interna dos métodos.
//
// Pendência conhecida para quando o backend/admin existir: o upload da
// imagem do banner precisa remover metadados EXIF antes de salvar (regra
// geral do projeto, ver CLAUDE.md — "Uploads de imagem devem ter
// metadados EXIF removidos"). Essa responsabilidade é de backend/security;
// este service só consome `imagemUrl` já processada/final, nunca lida
// com upload ou arquivo bruto.

/**
 * Contrato de um banner do Mural de Apoio.
 *
 * Anonimato: propositalmente não existe nenhum campo que identifique
 * quem administra ou publicou o banner (ex: nome do admin, e-mail,
 * usuário responsável) — apenas dados do conteúdo em si.
 */
export interface BannerMuralApoio {
  id: string;
  imagemUrl: string;
  altText: string; // obrigatório — requisito de acessibilidade
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

// Dados mockados centralizados aqui por simplicidade (arquivo único e
// pequeno), seguindo o mesmo padrão adotado em avisos.ts. Caso a lista
// cresça, mover para mural-de-apoio.mock.ts.
const MURAL_DE_APOIO_MOCK: BannerMuralApoio[] = [
  {
    id: 'banner-1',
    imagemUrl: '/images/mural-de-apoio/canal-de-apoio-24h.jpg',
    altText: 'Ilustração de duas mãos se apoiando, representando acolhimento 24 horas por dia.',
    legenda: 'Canal de apoio disponível 24 horas por dia, todos os dias.',
    linkDestino: '/hotlines',
    dataPublicacao: '2026-07-02T09:00:00.000Z',
    ativo: true,
  },
  {
    id: 'banner-2',
    imagemUrl: '/images/mural-de-apoio/programa-de-recuperacao.jpg',
    altText: 'Grupo de pessoas em círculo conversando em uma reunião de apoio.',
    legenda: 'Conheça o programa de recuperação de 12 passos.',
    linkDestino: '/programa-de-recuperacao',
    dataPublicacao: '2026-06-25T13:00:00.000Z',
    ativo: true,
  },
  {
    id: 'banner-3',
    imagemUrl: '/images/mural-de-apoio/gam-anon.jpg',
    altText: 'Família reunida em um ambiente acolhedor, representando apoio aos familiares.',
    legenda: 'GAM-ANON: apoio para familiares e amigos de jogadores. Conheça o programa de recuperação.',
    linkDestino: '/programa-de-recuperacao',
    dataPublicacao: '2026-06-18T10:30:00.000Z',
    ativo: true,
  },
  {
    id: 'banner-4',
    imagemUrl: '/images/mural-de-apoio/literatura-recomendada.jpg',
    altText: 'Pilha de livros e folhetos informativos sobre recuperação.',
    legenda: 'Literatura recomendada para quem está começando a jornada de recuperação.',
    dataPublicacao: '2026-06-05T08:00:00.000Z',
    ativo: true,
  },
  {
    id: 'banner-5',
    imagemUrl: '/images/mural-de-apoio/campanha-antiga.jpg',
    altText: 'Banner de campanha de conscientização encerrada.',
    legenda: 'Campanha de conscientização (encerrada).',
    dataPublicacao: '2026-04-10T08:00:00.000Z',
    ativo: false,
  },
];

const QUANTIDADE_DESTAQUE_HOME = 3;

@Injectable({ providedIn: 'root' })
export class MuralDeApoioService {
  /**
   * Busca os banners em destaque para a Home: apenas os `ativo: true`,
   * ordenados por dataPublicacao decrescente (mais recentes primeiro),
   * limitados a poucos itens.
   *
   * Mock temporário: retorna dados estáticos simulando latência de rede.
   */
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

  /**
   * Busca a lista completa de banners para a página `/mural-de-apoio`,
   * ordenada por dataPublicacao decrescente (mais recentes primeiro).
   * Inclui apenas banners `ativo: true`.
   *
   * Mock temporário: retorna dados estáticos simulando latência de rede.
   */
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

import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, delay, map } from 'rxjs/operators';

import { environment } from '../../../environments/environment';
import { ApiEnvelope, apiError, apiSuccess } from '../api-envelope';
import {
  BannerMuralAdmin,
  BannerMuralPublico,
  CriarBannerMuralRequest,
  EditarBannerMuralRequest,
} from './mural-de-apoio.model';
import { MURAL_DE_APOIO_MOCK } from './mural-de-apoio.mock';

export interface BannerMuralApoio extends BannerMuralPublico {
  ativo: boolean;
}

export interface MuralDeApoioResponse {
  success: boolean;
  data?: BannerMuralApoio[];
  error?: string;
}

const QUANTIDADE_DESTAQUE_HOME = 5;

function paraLegado(banner: BannerMuralAdmin): BannerMuralApoio {
  const { imagemId: _imagemId, dataAtualizacao: _dataAtualizacao, ...resto } = banner;
  return resto;
}

@Injectable({ providedIn: 'root' })
export class MuralDeApoioService {
  private readonly http = inject(HttpClient);
  private readonly publicaUrl = `${environment.apiBaseUrl}/mural-de-apoio`;
  private readonly adminUrl = `${environment.apiBaseUrl}/admin/mural-de-apoio`;

  private listarAtivos(): Observable<MuralDeApoioResponse> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<BannerMuralPublico[]>>(this.publicaUrl).pipe(
        map((envelope) =>
          envelope.success && envelope.data
            ? { success: true, data: envelope.data.map((b): BannerMuralApoio => ({ ...b, ativo: true })) }
            : { success: false, error: envelope.error?.message ?? 'Não foi possível carregar o mural de apoio.' },
        ),
        catchError(() => of({ success: false, error: 'Não foi possível carregar o mural de apoio no momento.' })),
      );
    }

    const ativos = MURAL_DE_APOIO_MOCK.filter((b) => b.ativo)
      .sort((a, b) => new Date(b.dataPublicacao).getTime() - new Date(a.dataPublicacao).getTime())
      .map(paraLegado);
    return of<MuralDeApoioResponse>({ success: true, data: ativos }).pipe(delay(500));
  }

  getBannersDestaque(): Observable<MuralDeApoioResponse> {
    return this.listarAtivos().pipe(
      map((resposta) =>
        resposta.success && resposta.data
          ? { ...resposta, data: resposta.data.slice(0, QUANTIDADE_DESTAQUE_HOME) }
          : resposta,
      ),
    );
  }

  getBannersCompleto(): Observable<MuralDeApoioResponse> {
    return this.listarAtivos();
  }

  listarTodosAdmin(): Observable<ApiEnvelope<BannerMuralAdmin[]>> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<BannerMuralAdmin[]>>(this.adminUrl).pipe(
        catchError(() => of(apiError<BannerMuralAdmin[]>('UPSTREAM_ERROR', 'Não foi possível carregar os banners.'))),
      );
    }

    const ordenados = [...MURAL_DE_APOIO_MOCK].sort(
      (a, b) => new Date(b.dataPublicacao).getTime() - new Date(a.dataPublicacao).getTime(),
    );
    return of(apiSuccess(ordenados)).pipe(delay(400));
  }

  criarBannerAdmin(req: CriarBannerMuralRequest): Observable<ApiEnvelope<BannerMuralAdmin>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<BannerMuralAdmin>>(this.adminUrl, req).pipe(
        catchError(() => of(apiError<BannerMuralAdmin>('UPSTREAM_ERROR', 'Não foi possível criar o banner agora.'))),
      );
    }

    if (!req.altText?.trim()) {
      return of(apiError<BannerMuralAdmin>('VALIDATION_ERROR', 'Texto alternativo é obrigatório.', [{ campo: 'altText', mensagem: 'obrigatório' }])).pipe(
        delay(300),
      );
    }

    const novo: BannerMuralAdmin = {
      id: `mock-banner-${MURAL_DE_APOIO_MOCK.length + 1}`,
      imagemId: req.videoUrl ? null : (req.imagemId ?? null),
      imagemUrl: req.videoUrl ? null : '/images/mural-de-apoio/placeholder.jpg',
      videoUrl: req.videoUrl ?? null,
      altText: req.altText,
      legenda: req.legenda ?? null,
      linkDestino: req.linkDestino ?? null,
      ativo: true,
      dataPublicacao: new Date().toISOString(),
      dataAtualizacao: new Date().toISOString(),
    };
    MURAL_DE_APOIO_MOCK.push(novo);
    return of(apiSuccess(novo)).pipe(delay(500));
  }

  editarBannerAdmin(id: string, req: EditarBannerMuralRequest): Observable<ApiEnvelope<BannerMuralAdmin>> {
    if (!environment.useMocks) {
      return this.http.put<ApiEnvelope<BannerMuralAdmin>>(`${this.adminUrl}/${id}`, req).pipe(
        catchError(() => of(apiError<BannerMuralAdmin>('UPSTREAM_ERROR', 'Não foi possível salvar as alterações.'))),
      );
    }

    const banner = MURAL_DE_APOIO_MOCK.find((b) => b.id === id);
    if (!banner) {
      return of(apiError<BannerMuralAdmin>('NOT_FOUND', 'Banner não encontrado.')).pipe(delay(300));
    }
    if (!req.altText?.trim()) {
      return of(apiError<BannerMuralAdmin>('VALIDATION_ERROR', 'Texto alternativo é obrigatório.', [{ campo: 'altText', mensagem: 'obrigatório' }])).pipe(
        delay(300),
      );
    }

    banner.imagemId = req.videoUrl ? null : (req.imagemId ?? null);
    banner.imagemUrl = req.videoUrl ? null : banner.imagemUrl;
    banner.videoUrl = req.videoUrl ?? null;
    banner.altText = req.altText;
    banner.legenda = req.legenda ?? null;
    banner.linkDestino = req.linkDestino ?? null;
    banner.ativo = req.ativo;
    banner.dataAtualizacao = new Date().toISOString();
    return of(apiSuccess(banner)).pipe(delay(400));
  }

  excluirBannerAdmin(id: string): Observable<ApiEnvelope<{ mensagem: string }>> {
    if (!environment.useMocks) {
      return this.http.delete<ApiEnvelope<{ mensagem: string }>>(`${this.adminUrl}/${id}`).pipe(
        catchError(() => of(apiError<{ mensagem: string }>('UPSTREAM_ERROR', 'Não foi possível remover o banner agora.'))),
      );
    }

    const indice = MURAL_DE_APOIO_MOCK.findIndex((b) => b.id === id);
    if (indice === -1) {
      return of(apiError<{ mensagem: string }>('NOT_FOUND', 'Banner não encontrado.')).pipe(delay(300));
    }
    MURAL_DE_APOIO_MOCK.splice(indice, 1);
    return of(apiSuccess({ mensagem: 'Banner removido.' })).pipe(delay(400));
  }
}

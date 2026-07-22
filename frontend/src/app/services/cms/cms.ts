import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, delay } from 'rxjs/operators';

import { environment } from '../../../environments/environment';
import { ApiEnvelope, apiError, apiSuccess } from '../api-envelope';
import {
  CmsPaginaAdminDetalhe,
  CmsPaginaAdminResumo,
  CmsPaginaCreateRequest,
  CmsPaginaPublica,
  CmsPaginaUpdateRequest,
} from './cms.model';
import { CMS_PAGINAS_MOCK } from './cms.mock';

@Injectable({ providedIn: 'root' })
export class Cms {
  private readonly http = inject(HttpClient);
  private readonly publicaUrl = `${environment.apiBaseUrl}/cms`;
  private readonly adminUrl = `${environment.apiBaseUrl}/admin/cms`;

  getPaginaPublica(slug: string): Observable<ApiEnvelope<CmsPaginaPublica>> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<CmsPaginaPublica>>(`${this.publicaUrl}/paginas/${slug}`).pipe(
        catchError(() => of(apiError<CmsPaginaPublica>('INTERNAL_ERROR', 'Não foi possível carregar esta página agora.'))),
      );
    }

    const pagina = CMS_PAGINAS_MOCK.find((p) => p.slug === slug);
    if (!pagina) {
      return of(apiError<CmsPaginaPublica>('CMS_PAGINA_NAO_ENCONTRADA', 'Página não encontrada.')).pipe(delay(300));
    }
    const { atualizadoEm: _a, atualizadoPor: _b, ...publica } = pagina;
    return of(apiSuccess<CmsPaginaPublica>(publica)).pipe(delay(400));
  }

  listarPaginasAdmin(): Observable<ApiEnvelope<CmsPaginaAdminResumo[]>> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<CmsPaginaAdminResumo[]>>(`${this.adminUrl}/paginas`).pipe(
        catchError(() => of(apiError<CmsPaginaAdminResumo[]>('INTERNAL_ERROR', 'Não foi possível carregar as páginas.'))),
      );
    }

    const resumos: CmsPaginaAdminResumo[] = CMS_PAGINAS_MOCK.map(({ slug, titulo, atualizadoEm, atualizadoPor }) => ({
      slug,
      titulo,
      atualizadoEm,
      atualizadoPor,
    }));
    return of(apiSuccess(resumos)).pipe(delay(400));
  }

  getPaginaAdmin(slug: string): Observable<ApiEnvelope<CmsPaginaAdminDetalhe>> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<CmsPaginaAdminDetalhe>>(`${this.adminUrl}/paginas/${slug}`).pipe(
        catchError(() => of(apiError<CmsPaginaAdminDetalhe>('INTERNAL_ERROR', 'Não foi possível carregar esta página.'))),
      );
    }

    const pagina = CMS_PAGINAS_MOCK.find((p) => p.slug === slug);
    if (!pagina) {
      return of(apiError<CmsPaginaAdminDetalhe>('CMS_PAGINA_NAO_ENCONTRADA', 'Página não encontrada.')).pipe(delay(300));
    }
    return of(apiSuccess(pagina)).pipe(delay(400));
  }

  atualizarPagina(slug: string, req: CmsPaginaUpdateRequest): Observable<ApiEnvelope<CmsPaginaAdminDetalhe>> {
    if (!environment.useMocks) {
      return this.http.put<ApiEnvelope<CmsPaginaAdminDetalhe>>(`${this.adminUrl}/paginas/${slug}`, req).pipe(
        catchError(() => of(apiError<CmsPaginaAdminDetalhe>('INTERNAL_ERROR', 'Não foi possível salvar as alterações.'))),
      );
    }

    const indice = CMS_PAGINAS_MOCK.findIndex((p) => p.slug === slug);
    if (indice === -1) {
      return of(apiError<CmsPaginaAdminDetalhe>('CMS_PAGINA_NAO_ENCONTRADA', 'Página não encontrada.')).pipe(delay(300));
    }

    const atualizada: CmsPaginaAdminDetalhe = {
      ...CMS_PAGINAS_MOCK[indice],
      blocos: req.blocos,
      atualizadoEm: new Date().toISOString(),
      atualizadoPor: 'mock-admin-1',
    };
    CMS_PAGINAS_MOCK[indice] = atualizada;
    return of(apiSuccess(atualizada)).pipe(delay(500));
  }

  criarPagina(req: CmsPaginaCreateRequest): Observable<ApiEnvelope<CmsPaginaAdminDetalhe>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<CmsPaginaAdminDetalhe>>(`${this.adminUrl}/paginas`, req).pipe(
        catchError(() => of(apiError<CmsPaginaAdminDetalhe>('INTERNAL_ERROR', 'Não foi possível criar a página.'))),
      );
    }

    if (CMS_PAGINAS_MOCK.some((p) => p.slug === req.slug)) {
      return of(apiError<CmsPaginaAdminDetalhe>('CMS_SLUG_EM_USO', 'Já existe uma página com este slug.')).pipe(delay(300));
    }

    const nova: CmsPaginaAdminDetalhe = {
      slug: req.slug,
      titulo: req.titulo,
      blocos: req.blocos,
      atualizadoEm: new Date().toISOString(),
      atualizadoPor: 'mock-admin-1',
    };
    CMS_PAGINAS_MOCK.push(nova);
    return of(apiSuccess(nova)).pipe(delay(500));
  }
}

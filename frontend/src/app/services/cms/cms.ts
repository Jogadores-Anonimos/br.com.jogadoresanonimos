import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, delay } from 'rxjs/operators';

import { environment } from '../../../environments/environment';
import { ApiEnvelope, apiError, apiSuccess } from '../api-envelope';
import { CmsPaginaPublica } from './cms.model';
import { CMS_PAGINAS_MOCK } from './cms.mock';

@Injectable({ providedIn: 'root' })
export class Cms {
  private readonly http = inject(HttpClient);
  private readonly publicaUrl = `${environment.apiBaseUrl}/cms`;

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
    return of(apiSuccess<CmsPaginaPublica>(pagina)).pipe(delay(400));
  }
}

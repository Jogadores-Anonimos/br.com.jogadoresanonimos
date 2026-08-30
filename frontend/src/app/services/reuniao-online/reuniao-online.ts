import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { environment } from '../../../environments/environment';
import { ApiEnvelope, apiError, apiErrorDeHttpError } from '../api-envelope';
import { CriarReuniaoOnlineRequest, EditarReuniaoOnlineRequest, ReuniaoOnline } from './reuniao-online.model';

@Injectable({ providedIn: 'root' })
export class ReuniaoOnlineService {
  private readonly http = inject(HttpClient);
  private readonly publicaUrl = `${environment.apiBaseUrl}/reunioes-online`;
  private readonly adminUrl = `${environment.apiBaseUrl}/admin/reunioes-online`;

  listarReunioesOnline(): Observable<ApiEnvelope<ReuniaoOnline[]>> {
    return this.http.get<ApiEnvelope<ReuniaoOnline[]>>(this.publicaUrl).pipe(
      catchError(() =>
        of(apiError<ReuniaoOnline[]>('INTERNAL_ERROR', 'Não foi possível carregar as reuniões online no momento.')),
      ),
    );
  }

  listarReunioesOnlineAdmin(): Observable<ApiEnvelope<ReuniaoOnline[]>> {
    return this.http.get<ApiEnvelope<ReuniaoOnline[]>>(this.adminUrl).pipe(
      catchError(() =>
        of(apiError<ReuniaoOnline[]>('INTERNAL_ERROR', 'Não foi possível carregar as reuniões online.')),
      ),
    );
  }

  criarReuniaoOnlineAdmin(req: CriarReuniaoOnlineRequest): Observable<ApiEnvelope<ReuniaoOnline>> {
    return this.http.post<ApiEnvelope<ReuniaoOnline>>(this.adminUrl, req).pipe(
      catchError((erro) =>
        of(apiErrorDeHttpError<ReuniaoOnline>(erro, 'INTERNAL_ERROR', 'Não foi possível criar a reunião online agora.')),
      ),
    );
  }

  editarReuniaoOnlineAdmin(id: string, req: EditarReuniaoOnlineRequest): Observable<ApiEnvelope<ReuniaoOnline>> {
    return this.http.put<ApiEnvelope<ReuniaoOnline>>(`${this.adminUrl}/${id}`, req).pipe(
      catchError((erro) =>
        of(apiErrorDeHttpError<ReuniaoOnline>(erro, 'INTERNAL_ERROR', 'Não foi possível salvar as alterações.')),
      ),
    );
  }

  excluirReuniaoOnlineAdmin(id: string): Observable<ApiEnvelope<{ mensagem: string }>> {
    return this.http.delete<ApiEnvelope<{ mensagem: string }>>(`${this.adminUrl}/${id}`).pipe(
      catchError((erro) =>
        of(apiErrorDeHttpError<{ mensagem: string }>(erro, 'INTERNAL_ERROR', 'Não foi possível remover a reunião online agora.')),
      ),
    );
  }
}

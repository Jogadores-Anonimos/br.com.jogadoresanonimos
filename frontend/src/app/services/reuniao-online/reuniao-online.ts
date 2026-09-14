import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import { environment } from '../../../environments/environment';
import { ApiEnvelope, apiError, apiErrorDeHttpError } from '../api-envelope';
import {
  CorReuniaoOnline,
  CriarReuniaoOnlineRequest,
  EditarReuniaoOnlineRequest,
  ReuniaoOnline,
  ReuniaoOnlineApi,
} from './reuniao-online.model';

function normalizarReuniaoOnline(reuniao: ReuniaoOnlineApi): ReuniaoOnline {
  return { ...reuniao, cor: reuniao.cor.toLowerCase() as CorReuniaoOnline };
}

function normalizarEnvelope(envelope: ApiEnvelope<ReuniaoOnlineApi>): ApiEnvelope<ReuniaoOnline> {
  return { ...envelope, data: envelope.data ? normalizarReuniaoOnline(envelope.data) : null };
}

function normalizarEnvelopeLista(envelope: ApiEnvelope<ReuniaoOnlineApi[]>): ApiEnvelope<ReuniaoOnline[]> {
  return { ...envelope, data: envelope.data ? envelope.data.map(normalizarReuniaoOnline) : null };
}

function montarPayload<T extends CriarReuniaoOnlineRequest>(req: T): T {
  return { ...req, cor: req.cor ? (req.cor.toUpperCase() as T['cor']) : undefined };
}

@Injectable({ providedIn: 'root' })
export class ReuniaoOnlineService {
  private readonly http = inject(HttpClient);
  private readonly publicaUrl = `${environment.apiBaseUrl}/reunioes-online`;
  private readonly adminUrl = `${environment.apiBaseUrl}/admin/reunioes-online`;

  listarReunioesOnline(): Observable<ApiEnvelope<ReuniaoOnline[]>> {
    return this.http.get<ApiEnvelope<ReuniaoOnlineApi[]>>(this.publicaUrl).pipe(
      map(normalizarEnvelopeLista),
      catchError(() =>
        of(apiError<ReuniaoOnline[]>('INTERNAL_ERROR', 'Não foi possível carregar as reuniões online no momento.')),
      ),
    );
  }

  listarReunioesOnlineAdmin(): Observable<ApiEnvelope<ReuniaoOnline[]>> {
    return this.http.get<ApiEnvelope<ReuniaoOnlineApi[]>>(this.adminUrl).pipe(
      map(normalizarEnvelopeLista),
      catchError(() =>
        of(apiError<ReuniaoOnline[]>('INTERNAL_ERROR', 'Não foi possível carregar as reuniões online.')),
      ),
    );
  }

  criarReuniaoOnlineAdmin(req: CriarReuniaoOnlineRequest): Observable<ApiEnvelope<ReuniaoOnline>> {
    return this.http.post<ApiEnvelope<ReuniaoOnlineApi>>(this.adminUrl, montarPayload(req)).pipe(
      map(normalizarEnvelope),
      catchError((erro) =>
        of(apiErrorDeHttpError<ReuniaoOnline>(erro, 'INTERNAL_ERROR', 'Não foi possível criar a reunião online agora.')),
      ),
    );
  }

  editarReuniaoOnlineAdmin(id: string, req: EditarReuniaoOnlineRequest): Observable<ApiEnvelope<ReuniaoOnline>> {
    return this.http.put<ApiEnvelope<ReuniaoOnlineApi>>(`${this.adminUrl}/${id}`, montarPayload(req)).pipe(
      map(normalizarEnvelope),
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

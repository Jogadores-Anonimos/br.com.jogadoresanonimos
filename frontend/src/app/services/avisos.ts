import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, delay, map } from 'rxjs/operators';

import { environment } from '../../environments/environment';
import { ApiEnvelope, apiError, apiSuccess } from './api-envelope';
import { AvisoAdmin, AvisoPublico, CriarAvisoRequest, EditarAvisoRequest } from './aviso.model';
import { AVISOS_MOCK } from './aviso.mock';

// Reexport para não quebrar consumidores existentes (pages/avisos, pages/home).
export type Aviso = AvisoPublico;

// Formato legado mantido só para não quebrar os componentes já existentes.
export interface AvisosResponse {
  success: boolean;
  data?: Aviso[];
  error?: string;
}

function ordenar<T extends { fixado: boolean; dataPublicacao: string }>(itens: T[]): T[] {
  return [...itens].sort((a, b) => {
    if (a.fixado !== b.fixado) {
      return a.fixado ? -1 : 1;
    }
    return new Date(b.dataPublicacao).getTime() - new Date(a.dataPublicacao).getTime();
  });
}

@Injectable({ providedIn: 'root' })
export class AvisosService {
  private readonly http = inject(HttpClient);
  private readonly publicaUrl = `${environment.apiBaseUrl}/avisos`;
  private readonly adminUrl = `${environment.apiBaseUrl}/admin/avisos`;

  listarAvisos(): Observable<AvisosResponse> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<AvisoPublico[]>>(this.publicaUrl).pipe(
        map((envelope) =>
          envelope.success && envelope.data
            ? { success: true, data: ordenar(envelope.data) }
            : { success: false, error: envelope.error?.message ?? 'Não foi possível carregar os avisos.' },
        ),
        catchError(() => of({ success: false, error: 'Não foi possível carregar os avisos no momento.' })),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    const ativos = AVISOS_MOCK.filter((a) => !a.expirado).map(
      ({ id, titulo, corpo, dataPublicacao, fixado }): Aviso => ({ id, titulo, corpo, dataPublicacao, fixado }),
    );
    return of<AvisosResponse>({ success: true, data: ordenar(ativos) }).pipe(delay(500));
  }

  // --- Admin ---

  listarTodosAdmin(): Observable<ApiEnvelope<AvisoAdmin[]>> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<AvisoAdmin[]>>(this.adminUrl).pipe(
        catchError(() => of(apiError<AvisoAdmin[]>('UPSTREAM_ERROR', 'Não foi possível carregar os avisos.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    return of(apiSuccess(ordenar(AVISOS_MOCK))).pipe(delay(400));
  }

  criarAvisoAdmin(req: CriarAvisoRequest): Observable<ApiEnvelope<AvisoAdmin>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<AvisoAdmin>>(this.adminUrl, req).pipe(
        catchError(() => of(apiError<AvisoAdmin>('UPSTREAM_ERROR', 'Não foi possível criar o aviso agora.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    if (!req.titulo?.trim() || !req.corpo?.trim()) {
      return of(apiError<AvisoAdmin>('VALIDATION_ERROR', 'Título e corpo são obrigatórios.')).pipe(delay(300));
    }

    const expirado = !!req.dataExpiracao && new Date(req.dataExpiracao).getTime() < Date.now();
    const novo: AvisoAdmin = {
      id: `mock-aviso-${AVISOS_MOCK.length + 1}`,
      titulo: req.titulo,
      corpo: req.corpo,
      fixado: req.fixado ?? false,
      dataExpiracao: req.dataExpiracao ?? null,
      dataPublicacao: new Date().toISOString(),
      expirado,
    };
    AVISOS_MOCK.push(novo);
    return of(apiSuccess(novo)).pipe(delay(500));
  }

  editarAvisoAdmin(id: string, req: EditarAvisoRequest): Observable<ApiEnvelope<AvisoAdmin>> {
    if (!environment.useMocks) {
      return this.http.put<ApiEnvelope<AvisoAdmin>>(`${this.adminUrl}/${id}`, req).pipe(
        catchError(() => of(apiError<AvisoAdmin>('UPSTREAM_ERROR', 'Não foi possível salvar as alterações.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    const aviso = AVISOS_MOCK.find((a) => a.id === id);
    if (!aviso) {
      return of(apiError<AvisoAdmin>('NOT_FOUND', 'Aviso não encontrado.')).pipe(delay(300));
    }
    if (!req.titulo?.trim() || !req.corpo?.trim()) {
      return of(apiError<AvisoAdmin>('VALIDATION_ERROR', 'Título e corpo são obrigatórios.')).pipe(delay(300));
    }

    aviso.titulo = req.titulo;
    aviso.corpo = req.corpo;
    aviso.fixado = req.fixado ?? false;
    aviso.dataExpiracao = req.dataExpiracao ?? null;
    aviso.expirado = !!aviso.dataExpiracao && new Date(aviso.dataExpiracao).getTime() < Date.now();
    return of(apiSuccess(aviso)).pipe(delay(400));
  }

  excluirAvisoAdmin(id: string): Observable<ApiEnvelope<{ mensagem: string }>> {
    if (!environment.useMocks) {
      return this.http.delete<ApiEnvelope<{ mensagem: string }>>(`${this.adminUrl}/${id}`).pipe(
        catchError(() => of(apiError<{ mensagem: string }>('UPSTREAM_ERROR', 'Não foi possível remover o aviso agora.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    const indice = AVISOS_MOCK.findIndex((a) => a.id === id);
    if (indice === -1) {
      return of(apiError<{ mensagem: string }>('NOT_FOUND', 'Aviso não encontrado.')).pipe(delay(300));
    }
    AVISOS_MOCK.splice(indice, 1);
    return of(apiSuccess({ mensagem: 'Aviso removido.' })).pipe(delay(400));
  }
}

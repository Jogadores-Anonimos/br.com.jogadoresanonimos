import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, delay } from 'rxjs/operators';

import { environment } from '../../environments/environment';
import { ApiEnvelope, apiError, apiSuccess } from './api-envelope';
import { CriarReuniaoRequest, EditarReuniaoRequest, Reuniao } from './reuniao.model';
import { REUNIOES_MOCK } from './reuniao.mock';

@Injectable({ providedIn: 'root' })
export class ReuniaoService {
  private readonly http = inject(HttpClient);
  private readonly publicaUrl = `${environment.apiBaseUrl}/reunioes`;
  private readonly adminUrl = `${environment.apiBaseUrl}/admin/reunioes`;

  listarReunioes(): Observable<ApiEnvelope<Reuniao[]>> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<Reuniao[]>>(this.publicaUrl).pipe(
        catchError(() => of(apiError<Reuniao[]>('UPSTREAM_ERROR', 'Não foi possível carregar as reuniões no momento.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    const ordenadas = [...REUNIOES_MOCK].sort((a, b) => a.nome.localeCompare(b.nome));
    return of(apiSuccess(ordenadas)).pipe(delay(400));
  }

  // --- Admin ---

  listarReunioesAdmin(): Observable<ApiEnvelope<Reuniao[]>> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<Reuniao[]>>(this.adminUrl).pipe(
        catchError(() => of(apiError<Reuniao[]>('UPSTREAM_ERROR', 'Não foi possível carregar as reuniões.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    return this.listarReunioes();
  }

  criarReuniaoAdmin(req: CriarReuniaoRequest): Observable<ApiEnvelope<Reuniao>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<Reuniao>>(this.adminUrl, req).pipe(
        catchError(() => of(apiError<Reuniao>('UPSTREAM_ERROR', 'Não foi possível criar a reunião agora.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    if (!req.nome?.trim() || !req.descricao?.trim() || !req.dia?.trim() || !req.horario?.trim() || !req.local?.trim()) {
      return of(apiError<Reuniao>('VALIDATION_ERROR', 'Preencha todos os campos obrigatórios.')).pipe(delay(300));
    }

    const nova: Reuniao = {
      id: `mock-reuniao-${REUNIOES_MOCK.length + 1}`,
      nome: req.nome,
      categoria: req.categoria,
      descricao: req.descricao,
      dia: req.dia,
      horario: req.horario,
      local: req.local,
      whatsapp: req.whatsapp?.trim() || null,
      diaSemana: req.diaSemana?.trim() || null,
      periodo: req.periodo?.trim() || null,
      latitude: req.latitude ?? null,
      longitude: req.longitude ?? null,
    };
    REUNIOES_MOCK.push(nova);
    return of(apiSuccess(nova)).pipe(delay(500));
  }

  editarReuniaoAdmin(id: string, req: EditarReuniaoRequest): Observable<ApiEnvelope<Reuniao>> {
    if (!environment.useMocks) {
      return this.http.put<ApiEnvelope<Reuniao>>(`${this.adminUrl}/${id}`, req).pipe(
        catchError(() => of(apiError<Reuniao>('UPSTREAM_ERROR', 'Não foi possível salvar as alterações.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    const reuniao = REUNIOES_MOCK.find((r) => r.id === id);
    if (!reuniao) {
      return of(apiError<Reuniao>('NOT_FOUND', 'Reunião não encontrada.')).pipe(delay(300));
    }
    if (!req.nome?.trim() || !req.descricao?.trim() || !req.dia?.trim() || !req.horario?.trim() || !req.local?.trim()) {
      return of(apiError<Reuniao>('VALIDATION_ERROR', 'Preencha todos os campos obrigatórios.')).pipe(delay(300));
    }

    reuniao.nome = req.nome;
    reuniao.categoria = req.categoria;
    reuniao.descricao = req.descricao;
    reuniao.dia = req.dia;
    reuniao.horario = req.horario;
    reuniao.local = req.local;
    reuniao.whatsapp = req.whatsapp?.trim() || null;
    reuniao.diaSemana = req.diaSemana?.trim() || null;
    reuniao.periodo = req.periodo?.trim() || null;
    reuniao.latitude = req.latitude ?? null;
    reuniao.longitude = req.longitude ?? null;
    return of(apiSuccess(reuniao)).pipe(delay(400));
  }

  excluirReuniaoAdmin(id: string): Observable<ApiEnvelope<{ mensagem: string }>> {
    if (!environment.useMocks) {
      return this.http.delete<ApiEnvelope<{ mensagem: string }>>(`${this.adminUrl}/${id}`).pipe(
        catchError(() => of(apiError<{ mensagem: string }>('UPSTREAM_ERROR', 'Não foi possível remover a reunião agora.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    const indice = REUNIOES_MOCK.findIndex((r) => r.id === id);
    if (indice === -1) {
      return of(apiError<{ mensagem: string }>('NOT_FOUND', 'Reunião não encontrada.')).pipe(delay(300));
    }
    REUNIOES_MOCK.splice(indice, 1);
    return of(apiSuccess({ mensagem: 'Reunião removida.' })).pipe(delay(400));
  }
}

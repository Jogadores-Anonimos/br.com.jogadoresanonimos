import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, delay } from 'rxjs/operators';

import { environment } from '../../environments/environment';
import { ApiEnvelope, apiError, apiSuccess } from './api-envelope';
import {
  CriarRegiaoRequest,
  CriarTelefoneRequest,
  EditarTelefoneRequest,
  HotlineRegiao,
  HotlineTelefone,
} from './hotline.model';
import { HOTLINES_MOCK } from './hotline.mock';

function ordenarRegioes(regioes: HotlineRegiao[]): HotlineRegiao[] {
  return [...regioes].sort((a, b) => a.nome.localeCompare(b.nome));
}

@Injectable({ providedIn: 'root' })
export class HotlineService {
  private readonly http = inject(HttpClient);
  private readonly publicaUrl = `${environment.apiBaseUrl}/hotlines`;
  private readonly adminUrl = `${environment.apiBaseUrl}/admin/hotlines`;

  listarHotlines(): Observable<ApiEnvelope<HotlineRegiao[]>> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<HotlineRegiao[]>>(this.publicaUrl).pipe(
        catchError(() => of(apiError<HotlineRegiao[]>('UPSTREAM_ERROR', 'Não foi possível carregar as linhas de ajuda.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    return of(apiSuccess(ordenarRegioes(HOTLINES_MOCK))).pipe(delay(400));
  }

  // --- Admin ---

  listarHotlinesAdmin(): Observable<ApiEnvelope<HotlineRegiao[]>> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<HotlineRegiao[]>>(this.adminUrl).pipe(
        catchError(() => of(apiError<HotlineRegiao[]>('UPSTREAM_ERROR', 'Não foi possível carregar as linhas de ajuda.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    return this.listarHotlines();
  }

  criarRegiaoAdmin(req: CriarRegiaoRequest): Observable<ApiEnvelope<HotlineRegiao>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<HotlineRegiao>>(`${this.adminUrl}/regioes`, req).pipe(
        catchError(() => of(apiError<HotlineRegiao>('UPSTREAM_ERROR', 'Não foi possível criar a região agora.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    const nome = req.nome?.trim() ?? '';
    if (!nome) {
      return of(apiError<HotlineRegiao>('VALIDATION_ERROR', 'Nome é obrigatório.', { nome: 'obrigatório' })).pipe(delay(300));
    }
    if (HOTLINES_MOCK.some((r) => r.nome.toLowerCase() === nome.toLowerCase())) {
      return of(apiError<HotlineRegiao>('CONFLICT', 'Já existe uma região com este nome.')).pipe(delay(300));
    }

    const nova: HotlineRegiao = { id: `mock-regiao-${HOTLINES_MOCK.length + 1}`, nome, telefones: [] };
    HOTLINES_MOCK.push(nova);
    return of(apiSuccess(nova)).pipe(delay(500));
  }

  excluirRegiaoAdmin(id: string): Observable<ApiEnvelope<{ mensagem: string }>> {
    if (!environment.useMocks) {
      return this.http.delete<ApiEnvelope<{ mensagem: string }>>(`${this.adminUrl}/regioes/${id}`).pipe(
        catchError(() => of(apiError<{ mensagem: string }>('UPSTREAM_ERROR', 'Não foi possível remover a região agora.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    const indice = HOTLINES_MOCK.findIndex((r) => r.id === id);
    if (indice === -1) {
      return of(apiError<{ mensagem: string }>('NOT_FOUND', 'Região não encontrada.')).pipe(delay(300));
    }
    HOTLINES_MOCK.splice(indice, 1);
    return of(apiSuccess({ mensagem: 'Região removida.' })).pipe(delay(400));
  }

  adicionarTelefoneAdmin(regiaoId: string, req: CriarTelefoneRequest): Observable<ApiEnvelope<HotlineRegiao>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<HotlineRegiao>>(`${this.adminUrl}/regioes/${regiaoId}/telefones`, req).pipe(
        catchError(() => of(apiError<HotlineRegiao>('UPSTREAM_ERROR', 'Não foi possível adicionar o telefone agora.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    const regiao = HOTLINES_MOCK.find((r) => r.id === regiaoId);
    if (!regiao) {
      return of(apiError<HotlineRegiao>('NOT_FOUND', 'Região não encontrada.')).pipe(delay(300));
    }
    if (!req.label?.trim() || !req.telefone?.trim()) {
      return of(apiError<HotlineRegiao>('VALIDATION_ERROR', 'Label e telefone são obrigatórios.')).pipe(delay(300));
    }

    regiao.telefones.push({ id: `mock-telefone-${crypto.randomUUID()}`, label: req.label, telefone: req.telefone });
    return of(apiSuccess(regiao)).pipe(delay(400));
  }

  editarTelefoneAdmin(id: string, req: EditarTelefoneRequest): Observable<ApiEnvelope<HotlineTelefone>> {
    if (!environment.useMocks) {
      return this.http.put<ApiEnvelope<HotlineTelefone>>(`${this.adminUrl}/telefones/${id}`, req).pipe(
        catchError(() => of(apiError<HotlineTelefone>('UPSTREAM_ERROR', 'Não foi possível salvar as alterações.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    for (const regiao of HOTLINES_MOCK) {
      const telefone = regiao.telefones.find((t) => t.id === id);
      if (telefone) {
        if (!req.label?.trim() || !req.telefone?.trim()) {
          return of(apiError<HotlineTelefone>('VALIDATION_ERROR', 'Label e telefone são obrigatórios.')).pipe(delay(300));
        }
        telefone.label = req.label;
        telefone.telefone = req.telefone;
        return of(apiSuccess(telefone)).pipe(delay(400));
      }
    }
    return of(apiError<HotlineTelefone>('NOT_FOUND', 'Telefone não encontrado.')).pipe(delay(300));
  }

  excluirTelefoneAdmin(id: string): Observable<ApiEnvelope<{ mensagem: string }>> {
    if (!environment.useMocks) {
      return this.http.delete<ApiEnvelope<{ mensagem: string }>>(`${this.adminUrl}/telefones/${id}`).pipe(
        catchError(() => of(apiError<{ mensagem: string }>('UPSTREAM_ERROR', 'Não foi possível remover o telefone agora.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    for (const regiao of HOTLINES_MOCK) {
      const indice = regiao.telefones.findIndex((t) => t.id === id);
      if (indice !== -1) {
        regiao.telefones.splice(indice, 1);
        return of(apiSuccess({ mensagem: 'Telefone removido.' })).pipe(delay(400));
      }
    }
    return of(apiError<{ mensagem: string }>('NOT_FOUND', 'Telefone não encontrado.')).pipe(delay(300));
  }
}

import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, delay } from 'rxjs/operators';

import { environment } from '../../../environments/environment';
import { ApiEnvelope, apiError, apiErrorDeHttpError, apiSuccess } from '../api-envelope';
import {
  CriarRegiaoRequest,
  CriarTelefoneRequest,
  EditarRegiaoRequest,
  EditarTelefoneRequest,
  HotlineRegiao,
  HotlineTelefone,
  UNIDADES_FEDERATIVAS,
} from './hotline.model';
import { HOTLINES_MOCK } from './hotline.mock';

let contadorIdMock = 0;
function proximoIdMock(prefixo: string): string {
  contadorIdMock += 1;
  return `mock-${prefixo}-${contadorIdMock}`;
}

function ufValida(uf: string | undefined | null): boolean {
  return !!uf && UNIDADES_FEDERATIVAS.some((u) => u.sigla === uf);
}

function ordenarRegioes(regioes: HotlineRegiao[]): HotlineRegiao[] {
  return [...regioes].sort((a, b) => a.uf.localeCompare(b.uf) || a.cidade.localeCompare(b.cidade));
}

@Injectable({ providedIn: 'root' })
export class HotlineService {
  private readonly http = inject(HttpClient);
  private readonly publicaUrl = `${environment.apiBaseUrl}/hotlines`;
  private readonly adminUrl = `${environment.apiBaseUrl}/admin/hotlines`;

  listarHotlines(): Observable<ApiEnvelope<HotlineRegiao[]>> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<HotlineRegiao[]>>(this.publicaUrl).pipe(
        catchError((erro) => of(apiErrorDeHttpError<HotlineRegiao[]>(erro, 'UPSTREAM_ERROR', 'Não foi possível carregar as linhas de ajuda.'))),
      );
    }

    const comTelefone = HOTLINES_MOCK.filter((r) => r.telefones.length > 0);
    return of(apiSuccess(ordenarRegioes(comTelefone))).pipe(delay(400));
  }


  listarHotlinesAdmin(): Observable<ApiEnvelope<HotlineRegiao[]>> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<HotlineRegiao[]>>(this.adminUrl).pipe(
        catchError((erro) => of(apiErrorDeHttpError<HotlineRegiao[]>(erro, 'UPSTREAM_ERROR', 'Não foi possível carregar as linhas de ajuda.'))),
      );
    }

    return of(apiSuccess(ordenarRegioes(HOTLINES_MOCK))).pipe(delay(400));
  }

  criarRegiaoAdmin(req: CriarRegiaoRequest): Observable<ApiEnvelope<HotlineRegiao>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<HotlineRegiao>>(`${this.adminUrl}/regioes`, req).pipe(
        catchError((erro) => of(apiErrorDeHttpError<HotlineRegiao>(erro, 'UPSTREAM_ERROR', 'Não foi possível criar a região agora.'))),
      );
    }

    const cidade = req.cidade?.trim() ?? '';
    if (!ufValida(req.uf)) {
      return of(apiError<HotlineRegiao>('VALIDATION_ERROR', 'UF inválida.', [{ campo: 'uf', mensagem: 'inválida' }])).pipe(delay(300));
    }
    if (!cidade) {
      return of(apiError<HotlineRegiao>('VALIDATION_ERROR', 'Cidade é obrigatória.', [{ campo: 'cidade', mensagem: 'obrigatório' }])).pipe(delay(300));
    }
    if (HOTLINES_MOCK.some((r) => r.uf === req.uf && r.cidade.toLowerCase() === cidade.toLowerCase())) {
      return of(apiError<HotlineRegiao>('CONFLICT', 'Já existe uma região com esta UF e cidade.')).pipe(delay(300));
    }

    const nova: HotlineRegiao = { id: proximoIdMock('regiao'), uf: req.uf, cidade, telefones: [] };
    HOTLINES_MOCK.push(nova);
    return of(apiSuccess(nova)).pipe(delay(500));
  }

  editarRegiaoAdmin(id: string, req: EditarRegiaoRequest): Observable<ApiEnvelope<HotlineRegiao>> {
    if (!environment.useMocks) {
      return this.http.put<ApiEnvelope<HotlineRegiao>>(`${this.adminUrl}/regioes/${id}`, req).pipe(
        catchError((erro) => of(apiErrorDeHttpError<HotlineRegiao>(erro, 'UPSTREAM_ERROR', 'Não foi possível salvar as alterações.'))),
      );
    }

    const regiao = HOTLINES_MOCK.find((r) => r.id === id);
    if (!regiao) {
      return of(apiError<HotlineRegiao>('NOT_FOUND', 'Região não encontrada.')).pipe(delay(300));
    }
    const cidade = req.cidade?.trim() ?? '';
    if (!ufValida(req.uf)) {
      return of(apiError<HotlineRegiao>('VALIDATION_ERROR', 'UF inválida.', [{ campo: 'uf', mensagem: 'inválida' }])).pipe(delay(300));
    }
    if (!cidade) {
      return of(apiError<HotlineRegiao>('VALIDATION_ERROR', 'Cidade é obrigatória.', [{ campo: 'cidade', mensagem: 'obrigatório' }])).pipe(delay(300));
    }
    const combinacaoMudou = req.uf !== regiao.uf || cidade.toLowerCase() !== regiao.cidade.toLowerCase();
    if (combinacaoMudou && HOTLINES_MOCK.some((r) => r.id !== id && r.uf === req.uf && r.cidade.toLowerCase() === cidade.toLowerCase())) {
      return of(apiError<HotlineRegiao>('CONFLICT', 'Já existe uma região com esta UF e cidade.')).pipe(delay(300));
    }

    regiao.uf = req.uf;
    regiao.cidade = cidade;
    return of(apiSuccess(regiao)).pipe(delay(400));
  }

  excluirRegiaoAdmin(id: string): Observable<ApiEnvelope<{ mensagem: string }>> {
    if (!environment.useMocks) {
      return this.http.delete<ApiEnvelope<{ mensagem: string }>>(`${this.adminUrl}/regioes/${id}`).pipe(
        catchError((erro) => of(apiErrorDeHttpError<{ mensagem: string }>(erro, 'UPSTREAM_ERROR', 'Não foi possível remover a região agora.'))),
      );
    }

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
        catchError((erro) => of(apiErrorDeHttpError<HotlineRegiao>(erro, 'UPSTREAM_ERROR', 'Não foi possível adicionar o telefone agora.'))),
      );
    }

    const regiao = HOTLINES_MOCK.find((r) => r.id === regiaoId);
    if (!regiao) {
      return of(apiError<HotlineRegiao>('NOT_FOUND', 'Região não encontrada.')).pipe(delay(300));
    }
    if (!req.telefone?.trim()) {
      return of(apiError<HotlineRegiao>('VALIDATION_ERROR', 'Telefone é obrigatório.')).pipe(delay(300));
    }

    regiao.telefones.push({ id: proximoIdMock('telefone'), telefone: req.telefone });
    return of(apiSuccess(regiao)).pipe(delay(400));
  }

  editarTelefoneAdmin(id: string, req: EditarTelefoneRequest): Observable<ApiEnvelope<HotlineTelefone>> {
    if (!environment.useMocks) {
      return this.http.put<ApiEnvelope<HotlineTelefone>>(`${this.adminUrl}/telefones/${id}`, req).pipe(
        catchError((erro) => of(apiErrorDeHttpError<HotlineTelefone>(erro, 'UPSTREAM_ERROR', 'Não foi possível salvar as alterações.'))),
      );
    }

    for (const regiao of HOTLINES_MOCK) {
      const telefone = regiao.telefones.find((t) => t.id === id);
      if (telefone) {
        if (!req.telefone?.trim()) {
          return of(apiError<HotlineTelefone>('VALIDATION_ERROR', 'Telefone é obrigatório.')).pipe(delay(300));
        }
        telefone.telefone = req.telefone;
        return of(apiSuccess(telefone)).pipe(delay(400));
      }
    }
    return of(apiError<HotlineTelefone>('NOT_FOUND', 'Telefone não encontrado.')).pipe(delay(300));
  }

  excluirTelefoneAdmin(id: string): Observable<ApiEnvelope<{ mensagem: string }>> {
    if (!environment.useMocks) {
      return this.http.delete<ApiEnvelope<{ mensagem: string }>>(`${this.adminUrl}/telefones/${id}`).pipe(
        catchError((erro) => of(apiErrorDeHttpError<{ mensagem: string }>(erro, 'UPSTREAM_ERROR', 'Não foi possível remover o telefone agora.'))),
      );
    }

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

import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, delay } from 'rxjs/operators';

import { environment } from '../../../environments/environment';
import { ApiEnvelope, apiError, apiSuccess } from '../api-envelope';
import {
  VinteQuestaoCriarRequest,
  VinteQuestaoResponse,
  VinteQuestaoUpdateRequest,
} from './vinte-perguntas.model';
import { VINTE_PERGUNTAS_MOCK } from './vinte-perguntas.mock';

@Injectable({ providedIn: 'root' })
export class VintePerguntasService {
  private readonly http = inject(HttpClient);
  private readonly publicaUrl = `${environment.apiBaseUrl}/vinte-perguntas`;
  private readonly adminUrl = `${environment.apiBaseUrl}/admin/vinte-perguntas`;

  listar(): Observable<ApiEnvelope<VinteQuestaoResponse[]>> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<VinteQuestaoResponse[]>>(this.publicaUrl).pipe(
        catchError(() =>
          of(apiError<VinteQuestaoResponse[]>('UPSTREAM_ERROR', 'Não foi possível carregar as perguntas agora.')),
        ),
      );
    }

    return of(apiSuccess([...VINTE_PERGUNTAS_MOCK])).pipe(delay(400));
  }

  listarAdmin(): Observable<ApiEnvelope<VinteQuestaoResponse[]>> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<VinteQuestaoResponse[]>>(this.adminUrl).pipe(
        catchError(() =>
          of(apiError<VinteQuestaoResponse[]>('UPSTREAM_ERROR', 'Não foi possível carregar as perguntas agora.')),
        ),
      );
    }

    return of(apiSuccess([...VINTE_PERGUNTAS_MOCK])).pipe(delay(400));
  }

  criarPergunta(texto: string): Observable<ApiEnvelope<VinteQuestaoResponse>> {
    const req: VinteQuestaoCriarRequest = { texto };

    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<VinteQuestaoResponse>>(this.adminUrl, req).pipe(
        catchError(() =>
          of(apiError<VinteQuestaoResponse>('UPSTREAM_ERROR', 'Não foi possível adicionar a pergunta agora.')),
        ),
      );
    }

    if (!req.texto?.trim()) {
      return of(apiError<VinteQuestaoResponse>('VALIDATION_ERROR', 'Texto é obrigatório.', [{ campo: 'texto', mensagem: 'obrigatório' }])).pipe(
        delay(300),
      );
    }

    const novaOrdem = VINTE_PERGUNTAS_MOCK.length
      ? Math.max(...VINTE_PERGUNTAS_MOCK.map((p) => p.ordem)) + 1
      : 0;
    const nova: VinteQuestaoResponse = {
      id: `mock-${Date.now()}`,
      numero: VINTE_PERGUNTAS_MOCK.length + 1,
      ordem: novaOrdem,
      texto: req.texto,
    };
    VINTE_PERGUNTAS_MOCK.push(nova);
    return of(apiSuccess(nova)).pipe(delay(400));
  }

  atualizarPergunta(id: string, req: VinteQuestaoUpdateRequest): Observable<ApiEnvelope<VinteQuestaoResponse>> {
    if (!environment.useMocks) {
      return this.http.put<ApiEnvelope<VinteQuestaoResponse>>(`${this.adminUrl}/${id}`, req).pipe(
        catchError(() =>
          of(apiError<VinteQuestaoResponse>('UPSTREAM_ERROR', 'Não foi possível salvar a pergunta agora.')),
        ),
      );
    }

    const pergunta = VINTE_PERGUNTAS_MOCK.find((p) => p.id === id);
    if (!pergunta) {
      return of(apiError<VinteQuestaoResponse>('NOT_FOUND', 'Pergunta não encontrada.')).pipe(delay(300));
    }
    if (!req.texto?.trim()) {
      return of(apiError<VinteQuestaoResponse>('VALIDATION_ERROR', 'Texto é obrigatório.', [{ campo: 'texto', mensagem: 'obrigatório' }])).pipe(
        delay(300),
      );
    }

    pergunta.texto = req.texto;
    return of(apiSuccess(pergunta)).pipe(delay(400));
  }

  removerPergunta(id: string): Observable<ApiEnvelope<{ mensagem: string }>> {
    if (!environment.useMocks) {
      return this.http.delete<ApiEnvelope<{ mensagem: string }>>(`${this.adminUrl}/${id}`).pipe(
        catchError(() =>
          of(apiError<{ mensagem: string }>('UPSTREAM_ERROR', 'Não foi possível remover a pergunta agora.')),
        ),
      );
    }

    const indice = VINTE_PERGUNTAS_MOCK.findIndex((p) => p.id === id);
    if (indice === -1) {
      return of(apiError<{ mensagem: string }>('NOT_FOUND', 'Pergunta não encontrada.')).pipe(delay(300));
    }
    if (VINTE_PERGUNTAS_MOCK.length === 1) {
      return of(
        apiError<{ mensagem: string }>('VALIDATION_ERROR', 'Não é possível remover a última pergunta.'),
      ).pipe(delay(300));
    }

    VINTE_PERGUNTAS_MOCK.splice(indice, 1);
    return of(apiSuccess({ mensagem: 'Pergunta removida.' })).pipe(delay(400));
  }

  moverPerguntaCima(id: string): Observable<ApiEnvelope<VinteQuestaoResponse>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<VinteQuestaoResponse>>(`${this.adminUrl}/${id}/mover-cima`, {}).pipe(
        catchError(() =>
          of(apiError<VinteQuestaoResponse>('UPSTREAM_ERROR', 'Não foi possível mover a pergunta agora.')),
        ),
      );
    }

    return this.moverMock(id, -1);
  }

  moverPerguntaBaixo(id: string): Observable<ApiEnvelope<VinteQuestaoResponse>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<VinteQuestaoResponse>>(`${this.adminUrl}/${id}/mover-baixo`, {}).pipe(
        catchError(() =>
          of(apiError<VinteQuestaoResponse>('UPSTREAM_ERROR', 'Não foi possível mover a pergunta agora.')),
        ),
      );
    }

    return this.moverMock(id, 1);
  }

  private moverMock(id: string, direcao: -1 | 1): Observable<ApiEnvelope<VinteQuestaoResponse>> {
    const ordenadas = [...VINTE_PERGUNTAS_MOCK].sort((a, b) => a.ordem - b.ordem);
    const posicao = ordenadas.findIndex((p) => p.id === id);
    if (posicao === -1) {
      return of(apiError<VinteQuestaoResponse>('NOT_FOUND', 'Pergunta não encontrada.')).pipe(delay(300));
    }
    const posicaoVizinha = posicao + direcao;
    if (posicaoVizinha < 0 || posicaoVizinha >= ordenadas.length) {
      return of(apiSuccess(ordenadas[posicao])).pipe(delay(300));
    }
    const atual = ordenadas[posicao];
    const vizinha = ordenadas[posicaoVizinha];
    const ordemAtual = atual.ordem;
    atual.ordem = vizinha.ordem;
    vizinha.ordem = ordemAtual;
    return of(apiSuccess(atual)).pipe(delay(400));
  }
}

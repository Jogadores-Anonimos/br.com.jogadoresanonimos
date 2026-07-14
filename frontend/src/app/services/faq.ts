import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, delay } from 'rxjs/operators';

import { environment } from '../../environments/environment';
import { ApiEnvelope, apiError, apiErrorDeHttpError, apiSuccess } from './api-envelope';
import { CriarFaqRequest, EditarFaqRequest, Faq } from './faq.model';
import { FAQ_MOCK } from './faq.mock';

function ordenar(itens: Faq[]): Faq[] {
  return [...itens].sort((a, b) => a.ordem - b.ordem);
}

@Injectable({ providedIn: 'root' })
export class FaqService {
  private readonly http = inject(HttpClient);
  private readonly publicaUrl = `${environment.apiBaseUrl}/faq`;
  private readonly adminUrl = `${environment.apiBaseUrl}/admin/faq`;

  listarFaq(): Observable<ApiEnvelope<Faq[]>> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<Faq[]>>(this.publicaUrl).pipe(
        catchError((erro) => of(apiErrorDeHttpError<Faq[]>(erro, 'UPSTREAM_ERROR', 'Não foi possível carregar as perguntas frequentes.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    return of(apiSuccess(ordenar(FAQ_MOCK))).pipe(delay(400));
  }

  // --- Admin ---

  listarFaqAdmin(): Observable<ApiEnvelope<Faq[]>> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<Faq[]>>(this.adminUrl).pipe(
        catchError((erro) => of(apiErrorDeHttpError<Faq[]>(erro, 'UPSTREAM_ERROR', 'Não foi possível carregar as perguntas frequentes.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    return this.listarFaq();
  }

  criarFaqAdmin(req: CriarFaqRequest): Observable<ApiEnvelope<Faq>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<Faq>>(this.adminUrl, req).pipe(
        catchError((erro) => of(apiErrorDeHttpError<Faq>(erro, 'UPSTREAM_ERROR', 'Não foi possível criar a pergunta agora.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    if (!req.pergunta?.trim() || !req.resposta?.trim()) {
      return of(apiError<Faq>('VALIDATION_ERROR', 'Pergunta e resposta são obrigatórias.')).pipe(delay(300));
    }

    const proximaOrdem = FAQ_MOCK.length ? Math.max(...FAQ_MOCK.map((f) => f.ordem)) + 1 : 0;
    const nova: Faq = { id: `mock-faq-${FAQ_MOCK.length + 1}`, pergunta: req.pergunta, resposta: req.resposta, ordem: proximaOrdem };
    FAQ_MOCK.push(nova);
    return of(apiSuccess(nova)).pipe(delay(500));
  }

  editarFaqAdmin(id: string, req: EditarFaqRequest): Observable<ApiEnvelope<Faq>> {
    if (!environment.useMocks) {
      return this.http.put<ApiEnvelope<Faq>>(`${this.adminUrl}/${id}`, req).pipe(
        catchError((erro) => of(apiErrorDeHttpError<Faq>(erro, 'UPSTREAM_ERROR', 'Não foi possível salvar as alterações.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    const faq = FAQ_MOCK.find((f) => f.id === id);
    if (!faq) {
      return of(apiError<Faq>('NOT_FOUND', 'Pergunta não encontrada.')).pipe(delay(300));
    }
    if (!req.pergunta?.trim() || !req.resposta?.trim()) {
      return of(apiError<Faq>('VALIDATION_ERROR', 'Pergunta e resposta são obrigatórias.')).pipe(delay(300));
    }
    faq.pergunta = req.pergunta;
    faq.resposta = req.resposta;
    return of(apiSuccess(faq)).pipe(delay(400));
  }

  excluirFaqAdmin(id: string): Observable<ApiEnvelope<{ mensagem: string }>> {
    if (!environment.useMocks) {
      return this.http.delete<ApiEnvelope<{ mensagem: string }>>(`${this.adminUrl}/${id}`).pipe(
        catchError((erro) => of(apiErrorDeHttpError<{ mensagem: string }>(erro, 'UPSTREAM_ERROR', 'Não foi possível remover a pergunta agora.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    const indice = FAQ_MOCK.findIndex((f) => f.id === id);
    if (indice === -1) {
      return of(apiError<{ mensagem: string }>('NOT_FOUND', 'Pergunta não encontrada.')).pipe(delay(300));
    }
    FAQ_MOCK.splice(indice, 1);
    return of(apiSuccess({ mensagem: 'Pergunta removida.' })).pipe(delay(400));
  }

  moverFaqParaCima(id: string): Observable<ApiEnvelope<Faq>> {
    return this.moverFaq(id, 'cima');
  }

  moverFaqParaBaixo(id: string): Observable<ApiEnvelope<Faq>> {
    return this.moverFaq(id, 'baixo');
  }

  private moverFaq(id: string, direcao: 'cima' | 'baixo'): Observable<ApiEnvelope<Faq>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<Faq>>(`${this.adminUrl}/${id}/mover-${direcao}`, {}).pipe(
        catchError((erro) => of(apiErrorDeHttpError<Faq>(erro, 'UPSTREAM_ERROR', 'Não foi possível reordenar a pergunta agora.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    const ordenados = ordenar(FAQ_MOCK);
    const indice = ordenados.findIndex((f) => f.id === id);
    if (indice === -1) {
      return of(apiError<Faq>('NOT_FOUND', 'Pergunta não encontrada.')).pipe(delay(300));
    }
    const indiceVizinho = direcao === 'cima' ? indice - 1 : indice + 1;
    if (indiceVizinho < 0 || indiceVizinho >= ordenados.length) {
      return of(apiSuccess(ordenados[indice])).pipe(delay(300));
    }
    const item = ordenados[indice];
    const vizinho = ordenados[indiceVizinho];
    const ordemAtual = item.ordem;
    item.ordem = vizinho.ordem;
    vizinho.ordem = ordemAtual;
    return of(apiSuccess(item)).pipe(delay(300));
  }
}

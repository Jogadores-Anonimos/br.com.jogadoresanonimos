import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, delay } from 'rxjs/operators';

import { environment } from '../../environments/environment';
import { ApiEnvelope, apiError, apiSuccess } from './api-envelope';
import { ArquivoResponse } from './arquivo.model';
import { ARQUIVOS_MOCK } from './arquivo.mock';
import { BIBLIOTECA_PESSOAL_IDS_MOCK } from './biblioteca-pessoal.mock';

// Isolamento entre usuários: no backend real, o usuário sempre vem do JWT
// (nunca de path/query param) — este service nunca aceita um id de usuário
// como parâmetro, só o id do arquivo (ver contrato-biblioteca-pessoal.md).
@Injectable({ providedIn: 'root' })
export class BibliotecaPessoalService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/usuarios/me/biblioteca`;

  listarMinhaBiblioteca(): Observable<ApiEnvelope<ArquivoResponse[]>> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<ArquivoResponse[]>>(this.baseUrl).pipe(
        catchError(() => of(apiError<ArquivoResponse[]>('UPSTREAM_ERROR', 'Não foi possível carregar sua biblioteca agora.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    const salvos = BIBLIOTECA_PESSOAL_IDS_MOCK.map((id) => ARQUIVOS_MOCK.find((a) => a.id === id)).filter(
      (a): a is ArquivoResponse => !!a,
    );
    return of(apiSuccess([...salvos].reverse())).pipe(delay(400));
  }

  adicionarABiblioteca(arquivoId: string): Observable<ApiEnvelope<{ mensagem: string }>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<{ mensagem: string }>>(`${this.baseUrl}/${arquivoId}`, {}).pipe(
        catchError(() => of(apiError<{ mensagem: string }>('UPSTREAM_ERROR', 'Não foi possível adicionar o arquivo agora.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    if (!ARQUIVOS_MOCK.some((a) => a.id === arquivoId)) {
      return of(apiError<{ mensagem: string }>('NOT_FOUND', 'Arquivo não encontrado.')).pipe(delay(300));
    }
    if (!BIBLIOTECA_PESSOAL_IDS_MOCK.includes(arquivoId)) {
      BIBLIOTECA_PESSOAL_IDS_MOCK.push(arquivoId);
    }
    return of(apiSuccess({ mensagem: 'Arquivo adicionado à biblioteca.' })).pipe(delay(400));
  }

  removerDaBiblioteca(arquivoId: string): Observable<ApiEnvelope<{ mensagem: string }>> {
    if (!environment.useMocks) {
      return this.http.delete<ApiEnvelope<{ mensagem: string }>>(`${this.baseUrl}/${arquivoId}`).pipe(
        catchError(() => of(apiError<{ mensagem: string }>('UPSTREAM_ERROR', 'Não foi possível remover o arquivo agora.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    // Idempotente: remover item inexistente também retorna sucesso (mesmo padrão do backend real).
    const indice = BIBLIOTECA_PESSOAL_IDS_MOCK.indexOf(arquivoId);
    if (indice !== -1) {
      BIBLIOTECA_PESSOAL_IDS_MOCK.splice(indice, 1);
    }
    return of(apiSuccess({ mensagem: 'Arquivo removido da biblioteca.' })).pipe(delay(400));
  }
}

import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, delay } from 'rxjs/operators';

import { environment } from '../../../environments/environment';
import { ApiEnvelope, apiError, apiSuccess } from '../api-envelope';
import { ImagemResponse, ImagemUploadRequest, MensagemResponse } from './imagem.model';
import { IMAGENS_MOCK } from './imagem.mock';

@Injectable({ providedIn: 'root' })
export class Imagem {
  private readonly http = inject(HttpClient);
  private readonly adminUrl = `${environment.apiBaseUrl}/admin/imagens`;

  uploadImagem(req: ImagemUploadRequest): Observable<ApiEnvelope<ImagemResponse>> {
    if (!environment.useMocks) {
      const formData = new FormData();
      formData.append('arquivo', req.arquivo);
      formData.append('alt', req.alt);
      return this.http.post<ApiEnvelope<ImagemResponse>>(this.adminUrl, formData).pipe(
        catchError(() => of(apiError<ImagemResponse>('INTERNAL_ERROR', 'Não foi possível enviar a imagem agora.'))),
      );
    }

    if (!req.alt?.trim()) {
      return of(apiError<ImagemResponse>('VALIDATION_ERROR', 'Texto alternativo é obrigatório.', [{ campo: 'alt', mensagem: 'obrigatório' }])).pipe(
        delay(300),
      );
    }

    const novo: ImagemResponse = {
      id: `mock-imagem-${IMAGENS_MOCK.length + 1}`,
      nomeOriginal: req.arquivo.name,
      tamanhoBytes: req.arquivo.size,
      alt: req.alt,
      url: `/mock/imagens/mock-imagem-${IMAGENS_MOCK.length + 1}.jpg`,
      criadoEm: new Date().toISOString(),
    };
    IMAGENS_MOCK.push(novo);
    return of(apiSuccess(novo)).pipe(delay(600));
  }

  removerImagem(id: string): Observable<ApiEnvelope<MensagemResponse>> {
    if (!environment.useMocks) {
      return this.http.delete<ApiEnvelope<MensagemResponse>>(`${this.adminUrl}/${id}`).pipe(
        catchError(() => of(apiError<MensagemResponse>('INTERNAL_ERROR', 'Não foi possível remover a imagem agora.'))),
      );
    }

    const indice = IMAGENS_MOCK.findIndex((i) => i.id === id);
    if (indice === -1) {
      return of(apiError<MensagemResponse>('IMAGEM_NAO_ENCONTRADA', 'Imagem não encontrada.')).pipe(delay(300));
    }
    IMAGENS_MOCK.splice(indice, 1);
    return of(apiSuccess({ mensagem: 'Imagem removida.' })).pipe(delay(400));
  }
}

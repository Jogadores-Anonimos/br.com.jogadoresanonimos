import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, delay } from 'rxjs/operators';

import { environment } from '../../environments/environment';
import { ApiEnvelope, apiError, apiErrorDeHttpError, apiSuccess } from './api-envelope';
import { ArquivoResponse, ArquivoUploadRequest, EditarArquivoRequest } from './arquivo.model';
import { ARQUIVOS_MOCK } from './arquivo.mock';

@Injectable({ providedIn: 'root' })
export class Arquivo {
  private readonly http = inject(HttpClient);
  private readonly publicaUrl = `${environment.apiBaseUrl}/arquivos`;
  private readonly adminUrl = `${environment.apiBaseUrl}/admin/arquivos`;

  // Pública — usada pela página /literatura. Mesma fonte de dado que o
  // admin gerencia, sem indireção via CmsPagina/bloco.
  listarArquivos(): Observable<ApiEnvelope<ArquivoResponse[]>> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<ArquivoResponse[]>>(this.publicaUrl).pipe(
        catchError((erro) => of(apiErrorDeHttpError<ArquivoResponse[]>(erro, 'UPSTREAM_ERROR', 'Não foi possível carregar as publicações agora.'))),
      );
    }

    const arquivos = [...ARQUIVOS_MOCK].sort(
      (a, b) => new Date(b.criadoEm).getTime() - new Date(a.criadoEm).getTime(),
    );
    return of(apiSuccess(arquivos)).pipe(delay(400));
  }

  listarArquivosAdmin(): Observable<ApiEnvelope<ArquivoResponse[]>> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<ArquivoResponse[]>>(this.adminUrl).pipe(
        catchError((erro) => of(apiErrorDeHttpError<ArquivoResponse[]>(erro, 'UPSTREAM_ERROR', 'Não foi possível carregar os arquivos agora.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    const arquivos = [...ARQUIVOS_MOCK].sort(
      (a, b) => new Date(b.criadoEm).getTime() - new Date(a.criadoEm).getTime(),
    );
    return of(apiSuccess(arquivos)).pipe(delay(400));
  }

  uploadArquivo(req: ArquivoUploadRequest): Observable<ApiEnvelope<ArquivoResponse>> {
    if (!environment.useMocks) {
      const formData = new FormData();
      formData.append('arquivo', req.arquivo);
      formData.append('titulo', req.titulo);
      if (req.descricao) {
        formData.append('descricao', req.descricao);
      }
      return this.http.post<ApiEnvelope<ArquivoResponse>>(this.adminUrl, formData).pipe(
        catchError((erro) => of(apiErrorDeHttpError<ArquivoResponse>(erro, 'UPSTREAM_ERROR', 'Não foi possível enviar o arquivo agora.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    if (!req.titulo) {
      return of(apiError<ArquivoResponse>('VALIDATION_ERROR', 'Título é obrigatório.', { titulo: 'obrigatório' })).pipe(delay(300));
    }

    const novo: ArquivoResponse = {
      id: `mock-arquivo-${ARQUIVOS_MOCK.length + 1}`,
      nomeOriginal: req.arquivo.name,
      tamanhoBytes: req.arquivo.size,
      titulo: req.titulo,
      descricao: req.descricao ?? null,
      urlDownload: `/api/v1/arquivos/mock-arquivo-${ARQUIVOS_MOCK.length + 1}/download`,
      thumbnailUrl: null,
      criadoEm: new Date().toISOString(),
    };
    ARQUIVOS_MOCK.push(novo);
    return of(apiSuccess(novo)).pipe(delay(600));
  }

  editarArquivo(id: string, req: EditarArquivoRequest): Observable<ApiEnvelope<ArquivoResponse>> {
    if (!environment.useMocks) {
      return this.http.put<ApiEnvelope<ArquivoResponse>>(`${this.adminUrl}/${id}`, req).pipe(
        catchError((erro) => of(apiErrorDeHttpError<ArquivoResponse>(erro, 'UPSTREAM_ERROR', 'Não foi possível salvar as alterações.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    const arquivo = ARQUIVOS_MOCK.find((a) => a.id === id);
    if (!arquivo) {
      return of(apiError<ArquivoResponse>('NOT_FOUND', 'Arquivo não encontrado.')).pipe(delay(300));
    }
    if (!req.titulo?.trim()) {
      return of(apiError<ArquivoResponse>('VALIDATION_ERROR', 'Título é obrigatório.', { titulo: 'obrigatório' })).pipe(delay(300));
    }

    arquivo.titulo = req.titulo;
    arquivo.descricao = req.descricao ?? null;
    return of(apiSuccess(arquivo)).pipe(delay(400));
  }

  deletarArquivo(id: string): Observable<ApiEnvelope<{ mensagem: string }>> {
    if (!environment.useMocks) {
      return this.http.delete<ApiEnvelope<{ mensagem: string }>>(`${this.adminUrl}/${id}`).pipe(
        catchError((erro) => of(apiErrorDeHttpError<{ mensagem: string }>(erro, 'UPSTREAM_ERROR', 'Não foi possível remover o arquivo agora.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    const indice = ARQUIVOS_MOCK.findIndex((a) => a.id === id);
    if (indice === -1) {
      return of(apiError<{ mensagem: string }>('NOT_FOUND', 'Arquivo não encontrado.')).pipe(delay(300));
    }
    ARQUIVOS_MOCK.splice(indice, 1);
    return of(apiSuccess({ mensagem: 'Arquivo removido.' })).pipe(delay(400));
  }
}

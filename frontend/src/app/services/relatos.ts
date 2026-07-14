import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, delay, map } from 'rxjs/operators';

import { environment } from '../../environments/environment';
import { ApiEnvelope, apiError, apiSuccess } from './api-envelope';
import { Pagina } from './pagina.model';
import {
  EditarRelatoRequest,
  EnviarRelatoRequest,
  RelatoAdmin,
  RelatoEnvio,
  RelatoPublico,
  StatusRelato,
} from './relato.model';
import { RELATOS_MOCK } from './relato.mock';

// Reexport para não quebrar consumidores existentes (pages/relatos, pages/home)
// que importam o tipo `Relato` deste módulo.
export type Relato = RelatoAdmin;
export type { StatusRelato } from './relato.model';

const TEXTO_MIN_LENGTH = 50;
const TEXTO_MAX_LENGTH = 4000;
const NOME_EXIBICAO_MAX_LENGTH = 30;
const TAMANHO_PAGINA_PADRAO = 20;

// Formatos legados (pré-contrato real), mantidos só para não quebrar os
// componentes que já consomem este service — internamente tudo já usa
// ApiEnvelope/Pagina<T> conforme backend/docs/api/contrato-relatos.md.
export interface RelatosResponse {
  success: boolean;
  data?: Relato[];
  error?: string;
}

export interface EnviarRelatoResponse {
  success: boolean;
  data?: { recebido: boolean };
  error?: string;
}

@Injectable({ providedIn: 'root' })
export class RelatosService {
  private readonly http = inject(HttpClient);
  private readonly publicaUrl = `${environment.apiBaseUrl}/relatos`;
  private readonly adminUrl = `${environment.apiBaseUrl}/admin/relatos`;

  // API pública nova, alinhada ao contrato — devolve Pagina<RelatoPublico>.
  listarAprovados(pagina = 0, tamanho = TAMANHO_PAGINA_PADRAO): Observable<ApiEnvelope<Pagina<RelatoPublico>>> {
    if (!environment.useMocks) {
      return this.http
        .get<ApiEnvelope<Pagina<RelatoPublico>>>(`${this.publicaUrl}?pagina=${pagina}&tamanho=${tamanho}`)
        .pipe(
          catchError(() =>
            of(apiError<Pagina<RelatoPublico>>('UPSTREAM_ERROR', 'Não foi possível carregar os relatos no momento.')),
          ),
        );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    const aprovados = RELATOS_MOCK.filter((r) => r.status === 'approved').sort(
      (a, b) => new Date(b.dataCriacao).getTime() - new Date(a.dataCriacao).getTime(),
    );
    const conteudo: RelatoPublico[] = aprovados.map(({ id, texto, nomeExibicao, dataCriacao }) => ({
      id,
      texto,
      nomeExibicao,
      dataCriacao,
    }));

    return of(
      apiSuccess<Pagina<RelatoPublico>>({
        conteudo,
        pagina,
        tamanho,
        totalElementos: conteudo.length,
        totalPaginas: 1,
      }),
    ).pipe(delay(500));
  }

  // Mantido por compatibilidade com pages/relatos e pages/home, que hoje
  // esperam um array simples (não Pagina<T>) em `data`.
  listarRelatosAprovados(): Observable<RelatosResponse> {
    return this.listarAprovados(0, TAMANHO_PAGINA_PADRAO).pipe(
      map((envelope) => {
        if (!envelope.success || !envelope.data) {
          return { success: false, error: envelope.error?.message ?? 'Não foi possível carregar os relatos no momento.' };
        }
        return { success: true, data: envelope.data.conteudo as Relato[] };
      }),
    );
  }

  // Requer autenticação (JWT) — ver contrato item 1. O header de auth é
  // adicionado pela infraestrutura HTTP compartilhada quando integrado à
  // API real; este service nunca lida com token diretamente.
  enviarRelato(req: EnviarRelatoRequest): Observable<EnviarRelatoResponse> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<RelatoEnvio>>(this.publicaUrl, req).pipe(
        map((envelope) =>
          envelope.success
            ? { success: true, data: { recebido: true } }
            : { success: false, error: envelope.error?.message ?? 'Não foi possível enviar seu relato agora.' },
        ),
        catchError(() => of({ success: false, error: 'Não foi possível enviar seu relato agora. Tente novamente em instantes.' })),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    const texto = req.texto?.trim() ?? '';
    const nomeExibicao = req.nomeExibicao?.trim();

    if (texto.length < TEXTO_MIN_LENGTH || texto.length > TEXTO_MAX_LENGTH) {
      return of<EnviarRelatoResponse>({
        success: false,
        error: `O relato deve ter entre ${TEXTO_MIN_LENGTH} e ${TEXTO_MAX_LENGTH} caracteres.`,
      }).pipe(delay(300));
    }

    if (nomeExibicao && nomeExibicao.length > NOME_EXIBICAO_MAX_LENGTH) {
      return of<EnviarRelatoResponse>({
        success: false,
        error: `O nome de exibição deve ter no máximo ${NOME_EXIBICAO_MAX_LENGTH} caracteres.`,
      }).pipe(delay(300));
    }

    const novo: RelatoAdmin = {
      id: `mock-relato-${RELATOS_MOCK.length + 1}`,
      texto,
      nomeExibicao: nomeExibicao || null,
      status: 'pending',
      dataCriacao: new Date().toISOString(),
      dataAtualizacao: new Date().toISOString(),
    };
    RELATOS_MOCK.push(novo);

    return of<EnviarRelatoResponse>({ success: true, data: { recebido: true } }).pipe(delay(500));
  }

  // --- Admin (moderação) ---

  listarPorStatusAdmin(
    status: StatusRelato,
    pagina = 0,
    tamanho = TAMANHO_PAGINA_PADRAO,
  ): Observable<ApiEnvelope<Pagina<RelatoAdmin>>> {
    if (!environment.useMocks) {
      return this.http
        .get<ApiEnvelope<Pagina<RelatoAdmin>>>(`${this.adminUrl}?status=${status}&pagina=${pagina}&tamanho=${tamanho}`)
        .pipe(
          catchError(() =>
            of(apiError<Pagina<RelatoAdmin>>('UPSTREAM_ERROR', 'Não foi possível carregar os relatos.')),
          ),
        );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    const filtrados = RELATOS_MOCK.filter((r) => r.status === status).sort((a, b) =>
      status === 'pending'
        ? new Date(a.dataCriacao).getTime() - new Date(b.dataCriacao).getTime()
        : new Date(b.dataAtualizacao).getTime() - new Date(a.dataAtualizacao).getTime(),
    );

    return of(
      apiSuccess<Pagina<RelatoAdmin>>({
        conteudo: filtrados,
        pagina,
        tamanho,
        totalElementos: filtrados.length,
        totalPaginas: 1,
      }),
    ).pipe(delay(400));
  }

  editarRelatoAdmin(id: string, req: EditarRelatoRequest): Observable<ApiEnvelope<RelatoAdmin>> {
    if (!environment.useMocks) {
      return this.http.put<ApiEnvelope<RelatoAdmin>>(`${this.adminUrl}/${id}`, req).pipe(
        catchError(() => of(apiError<RelatoAdmin>('UPSTREAM_ERROR', 'Não foi possível salvar as alterações.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    const texto = req.texto?.trim() ?? '';
    if (texto.length < TEXTO_MIN_LENGTH || texto.length > TEXTO_MAX_LENGTH) {
      return of(
        apiError<RelatoAdmin>('VALIDATION_ERROR', `O texto deve ter entre ${TEXTO_MIN_LENGTH} e ${TEXTO_MAX_LENGTH} caracteres.`),
      ).pipe(delay(300));
    }

    const relato = RELATOS_MOCK.find((r) => r.id === id);
    if (!relato) {
      return of(apiError<RelatoAdmin>('NOT_FOUND', 'Relato não encontrado.')).pipe(delay(300));
    }
    relato.texto = texto;
    relato.dataAtualizacao = new Date().toISOString();
    return of(apiSuccess(relato)).pipe(delay(400));
  }

  aprovarRelatoAdmin(id: string): Observable<ApiEnvelope<RelatoAdmin>> {
    return this.transicionarStatusAdmin(id, 'approved', `${this.adminUrl}/${id}/aprovar`);
  }

  rejeitarRelatoAdmin(id: string): Observable<ApiEnvelope<RelatoAdmin>> {
    return this.transicionarStatusAdmin(id, 'rejected', `${this.adminUrl}/${id}/rejeitar`);
  }

  reverterRelatoAdmin(id: string): Observable<ApiEnvelope<RelatoAdmin>> {
    return this.transicionarStatusAdmin(id, 'pending', `${this.adminUrl}/${id}/reverter`);
  }

  excluirRelatoAdmin(id: string): Observable<ApiEnvelope<{ mensagem: string }>> {
    if (!environment.useMocks) {
      return this.http.delete<ApiEnvelope<{ mensagem: string }>>(`${this.adminUrl}/${id}`).pipe(
        catchError(() => of(apiError<{ mensagem: string }>('UPSTREAM_ERROR', 'Não foi possível remover o relato agora.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    const indice = RELATOS_MOCK.findIndex((r) => r.id === id);
    if (indice === -1) {
      return of(apiError<{ mensagem: string }>('NOT_FOUND', 'Relato não encontrado.')).pipe(delay(300));
    }
    RELATOS_MOCK.splice(indice, 1);
    return of(apiSuccess({ mensagem: 'Relato removido.' })).pipe(delay(400));
  }

  private transicionarStatusAdmin(id: string, novoStatus: StatusRelato, urlReal: string): Observable<ApiEnvelope<RelatoAdmin>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<RelatoAdmin>>(urlReal, {}).pipe(
        catchError(() => of(apiError<RelatoAdmin>('UPSTREAM_ERROR', 'Não foi possível atualizar o status do relato.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    const relato = RELATOS_MOCK.find((r) => r.id === id);
    if (!relato) {
      return of(apiError<RelatoAdmin>('NOT_FOUND', 'Relato não encontrado.')).pipe(delay(300));
    }
    relato.status = novoStatus;
    relato.dataAtualizacao = new Date().toISOString();
    return of(apiSuccess(relato)).pipe(delay(400));
  }
}

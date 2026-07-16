import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, delay } from 'rxjs/operators';

import { environment } from '../../../environments/environment';
import { ApiEnvelope, apiSuccess, apiError } from '../api-envelope';
import { Pagina } from '../relatos/relato-pagina.model';
import { AuditoriaFiltros, AuditoriaLogAdmin } from './auditoria.model';
import { AUDITORIA_MOCK } from './auditoria.mock';

const TAMANHO_PAGINA_PADRAO = 20;

@Injectable({ providedIn: 'root' })
export class AuditoriaService {
  private readonly http = inject(HttpClient);
  private readonly adminUrl = `${environment.apiBaseUrl}/admin/auditoria`;

  listarAdmin(
    filtros: AuditoriaFiltros = {},
    pagina = 0,
    tamanho = TAMANHO_PAGINA_PADRAO,
  ): Observable<ApiEnvelope<Pagina<AuditoriaLogAdmin>>> {
    if (!environment.useMocks) {
      const query = this.montarQuery(filtros, pagina, tamanho);
      return this.http.get<ApiEnvelope<Pagina<AuditoriaLogAdmin>>>(`${this.adminUrl}?${query}`).pipe(
        catchError(() =>
          of(apiError<Pagina<AuditoriaLogAdmin>>('UPSTREAM_ERROR', 'Não foi possível carregar o log de auditoria.')),
        ),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    const filtrados = AUDITORIA_MOCK.filter((log) => this.atendeFiltros(log, filtros)).sort(
      (a, b) => new Date(b.criadoEm).getTime() - new Date(a.criadoEm).getTime(),
    );

    return of(
      apiSuccess<Pagina<AuditoriaLogAdmin>>({
        conteudo: filtrados,
        pagina,
        tamanho,
        totalElementos: filtrados.length,
        totalPaginas: 1,
      }),
    ).pipe(delay(400));
  }

  private atendeFiltros(log: AuditoriaLogAdmin, filtros: AuditoriaFiltros): boolean {
    if (filtros.entidade && log.entidade !== filtros.entidade) {
      return false;
    }
    if (filtros.acao && log.acao !== filtros.acao) {
      return false;
    }
    const criadoEm = new Date(log.criadoEm).getTime();
    if (filtros.dataInicio && criadoEm < new Date(filtros.dataInicio).getTime()) {
      return false;
    }
    if (filtros.dataFim && criadoEm > new Date(filtros.dataFim).getTime()) {
      return false;
    }
    return true;
  }

  private montarQuery(filtros: AuditoriaFiltros, pagina: number, tamanho: number): string {
    const params = new URLSearchParams();
    params.set('pagina', String(pagina));
    params.set('tamanho', String(tamanho));
    if (filtros.entidade) {
      params.set('entidade', filtros.entidade);
    }
    if (filtros.acao) {
      params.set('acao', filtros.acao);
    }
    if (filtros.dataInicio) {
      params.set('dataInicio', filtros.dataInicio);
    }
    if (filtros.dataFim) {
      params.set('dataFim', filtros.dataFim);
    }
    return params.toString();
  }
}

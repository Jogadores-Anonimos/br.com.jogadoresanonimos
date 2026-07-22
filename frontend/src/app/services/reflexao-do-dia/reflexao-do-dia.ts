import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, delay, map } from 'rxjs/operators';

import { environment } from '../../../environments/environment';
import { ApiEnvelope, apiError, apiSuccess } from '../api-envelope';
import { ReflexaoDia, ReflexaoDiaDoMes, SalvarReflexaoDiaRequest } from './reflexao-do-dia.model';
import { DIAS_POR_MES, buscarReflexaoMock } from './reflexao-do-dia.mock';

const REFLEXOES_MOCK_UPSERT = new Map<number, Map<number, string>>();

function buscarReflexaoComUpsert(mes: number, dia: number): ReflexaoDia | null {
  const textoUpsert = REFLEXOES_MOCK_UPSERT.get(mes)?.get(dia);
  if (textoUpsert) {
    return { mes, dia, texto: textoUpsert };
  }
  return buscarReflexaoMock(mes, dia);
}

export type ReflexaoDoDia = ReflexaoDia;

export interface ReflexaoDoDiaResponse {
  success: boolean;
  data?: ReflexaoDoDia;
  error?: string;
}

@Injectable({ providedIn: 'root' })
export class ReflexaoDoDiaService {
  private readonly http = inject(HttpClient);
  private readonly publicaUrl = `${environment.apiBaseUrl}/reflexao-do-dia`;
  private readonly adminUrl = `${environment.apiBaseUrl}/admin/reflexao-do-dia`;

  obterDiasNoMes(): number[] {
    return [...DIAS_POR_MES];
  }

  obterReflexaoDeHoje(): Observable<ReflexaoDoDiaResponse> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<ReflexaoDia>>(`${this.publicaUrl}/hoje`).pipe(
        map((envelope) => this.paraRespostaLegada(envelope)),
        catchError(() => of({ success: false, error: 'Não foi possível carregar a reflexão de hoje.' })),
      );
    }

    const hoje = new Date();
    return this.obterReflexaoPorData(hoje.getMonth() + 1, hoje.getDate());
  }

  obterReflexaoPorData(mes: number, dia: number): Observable<ReflexaoDoDiaResponse> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<ReflexaoDia>>(`${this.publicaUrl}/${mes}/${dia}`).pipe(
        map((envelope) => this.paraRespostaLegada(envelope)),
        catchError(() => of({ success: false, error: 'Não foi possível carregar a reflexão deste dia.' })),
      );
    }

    if (mes < 1 || mes > 12) {
      return of<ReflexaoDoDiaResponse>({ success: false, error: 'Mês inválido.' }).pipe(delay(300));
    }
    if (dia < 1 || dia > DIAS_POR_MES[mes - 1]) {
      return of<ReflexaoDoDiaResponse>({ success: false, error: 'Dia inválido para o mês informado.' }).pipe(delay(300));
    }

    const reflexao = buscarReflexaoComUpsert(mes, dia);
    if (!reflexao) {
      return of<ReflexaoDoDiaResponse>({ success: false, error: 'Ainda não há reflexão cadastrada para este dia.' }).pipe(delay(300));
    }

    return of<ReflexaoDoDiaResponse>({ success: true, data: reflexao }).pipe(delay(500));
  }

  private paraRespostaLegada(envelope: ApiEnvelope<ReflexaoDia>): ReflexaoDoDiaResponse {
    if (envelope.success && envelope.data) {
      return { success: true, data: envelope.data };
    }
    if (envelope.error?.code === 'REFLEXAO_NAO_ENCONTRADA') {
      return { success: false, error: 'Ainda não há reflexão cadastrada para este dia.' };
    }
    return { success: false, error: envelope.error?.message ?? 'Não foi possível carregar a reflexão.' };
  }

  listarMesAdmin(mes: number): Observable<ApiEnvelope<ReflexaoDiaDoMes[]>> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<ReflexaoDiaDoMes[]>>(`${this.adminUrl}?mes=${mes}`).pipe(
        catchError(() => of(apiError<ReflexaoDiaDoMes[]>('INTERNAL_ERROR', 'Não foi possível carregar o calendário do mês.'))),
      );
    }

    if (mes < 1 || mes > 12) {
      return of(apiError<ReflexaoDiaDoMes[]>('REFLEXAO_DATA_INVALIDA', 'Mês inválido.')).pipe(delay(300));
    }
    const totalDias = DIAS_POR_MES[mes - 1];
    const dias: ReflexaoDiaDoMes[] = Array.from({ length: totalDias }, (_, indice) => {
      const dia = indice + 1;
      return { dia, texto: buscarReflexaoComUpsert(mes, dia)?.texto ?? null };
    });
    return of(apiSuccess(dias)).pipe(delay(400));
  }

  salvarReflexaoAdmin(mes: number, dia: number, req: SalvarReflexaoDiaRequest): Observable<ApiEnvelope<ReflexaoDia>> {
    if (!environment.useMocks) {
      return this.http.put<ApiEnvelope<ReflexaoDia>>(`${this.adminUrl}/${mes}/${dia}`, req).pipe(
        catchError(() => of(apiError<ReflexaoDia>('INTERNAL_ERROR', 'Não foi possível salvar a reflexão agora.'))),
      );
    }

    const texto = req.texto?.trim() ?? '';
    if (!texto) {
      return of(apiError<ReflexaoDia>('VALIDATION_ERROR', 'Texto é obrigatório.', [{ campo: 'texto', mensagem: 'obrigatório' }])).pipe(delay(300));
    }
    if (mes < 1 || mes > 12 || dia < 1 || dia > DIAS_POR_MES[mes - 1]) {
      return of(apiError<ReflexaoDia>('REFLEXAO_DATA_INVALIDA', 'Data inválida.')).pipe(delay(300));
    }

    if (!REFLEXOES_MOCK_UPSERT.has(mes)) {
      REFLEXOES_MOCK_UPSERT.set(mes, new Map());
    }
    REFLEXOES_MOCK_UPSERT.get(mes)!.set(dia, texto);
    return of(apiSuccess<ReflexaoDia>({ mes, dia, texto })).pipe(delay(400));
  }
}

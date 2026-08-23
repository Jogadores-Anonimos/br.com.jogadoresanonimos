import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, delay, map } from 'rxjs/operators';

import { environment } from '../../../environments/environment';
import { ApiEnvelope, apiError, apiSuccess } from '../api-envelope';
import {
  AgendaItemReuniao,
  BackfillGeolocalizacaoResponse,
  CriarReuniaoRequest,
  DiaSemanaAgendaReuniao,
  EditarReuniaoRequest,
  Reuniao,
} from './reuniao.model';
import { REUNIOES_MOCK } from './reuniao.mock';

type ReuniaoRespostaCompat = Omit<Reuniao, 'agenda'> & { agenda?: AgendaItemReuniao[] | null };
const ORDEM_DIAS: DiaSemanaAgendaReuniao[] = ['SEGUNDA', 'TERCA', 'QUARTA', 'QUINTA', 'SEXTA', 'SABADO', 'DOMINGO'];
const ROTULOS_DIAS: Record<DiaSemanaAgendaReuniao, string> = {
  DOMINGO: 'Domingo',
  SEGUNDA: 'Segunda',
  TERCA: 'Terça',
  QUARTA: 'Quarta',
  QUINTA: 'Quinta',
  SEXTA: 'Sexta',
  SABADO: 'Sábado',
};

@Injectable({ providedIn: 'root' })
export class ReuniaoService {
  private readonly http = inject(HttpClient);
  private readonly publicaUrl = `${environment.apiBaseUrl}/reunioes`;
  private readonly adminUrl = `${environment.apiBaseUrl}/admin/reunioes`;

  listarReunioes(): Observable<ApiEnvelope<Reuniao[]>> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<ReuniaoRespostaCompat[]>>(this.publicaUrl).pipe(
        map((resposta) => this.normalizarEnvelopeLista(resposta)),
        catchError(() => of(apiError<Reuniao[]>('INTERNAL_ERROR', 'Não foi possível carregar as reuniões no momento.'))),
      );
    }

    const ordenadas = [...REUNIOES_MOCK].sort((a, b) => a.nome.localeCompare(b.nome));
    return of(apiSuccess(ordenadas)).pipe(delay(400));
  }

  listarReunioesAdmin(): Observable<ApiEnvelope<Reuniao[]>> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<ReuniaoRespostaCompat[]>>(this.adminUrl).pipe(
        map((resposta) => this.normalizarEnvelopeLista(resposta)),
        catchError(() => of(apiError<Reuniao[]>('INTERNAL_ERROR', 'Não foi possível carregar as reuniões.'))),
      );
    }

    return this.listarReunioes();
  }

  criarReuniaoAdmin(req: CriarReuniaoRequest): Observable<ApiEnvelope<Reuniao>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<ReuniaoRespostaCompat>>(this.adminUrl, req).pipe(
        map((resposta) => this.normalizarEnvelope(resposta)),
        catchError(() => of(apiError<Reuniao>('INTERNAL_ERROR', 'Não foi possível criar a reunião agora.'))),
      );
    }

    const agenda = this.agendaDoRequest(req);
    if (!req.nome?.trim() || !req.descricao?.trim() || agenda.length === 0 || !req.local?.trim()) {
      return of(apiError<Reuniao>('VALIDATION_ERROR', 'Preencha todos os campos obrigatórios.')).pipe(delay(300));
    }
    const primeiroHorario = agenda[0];
    const nova: Reuniao = {
      id: `mock-reuniao-${REUNIOES_MOCK.length + 1}`,
      nome: req.nome,
      categoria: req.categoria,
      descricao: req.descricao,
      agenda,
      dia: req.dia?.trim() || ROTULOS_DIAS[primeiroHorario.diaSemana],
      horario: req.horario?.trim() || primeiroHorario.horario,
      local: req.local,
      whatsapp: req.whatsapp?.trim() || null,
      diaSemana: req.diaSemana?.trim() || ROTULOS_DIAS[primeiroHorario.diaSemana],
      periodo: req.periodo?.trim() || null,
      latitude: req.latitude ?? null,
      longitude: req.longitude ?? null,
      cep: req.cep?.trim() || null,
      numero: req.numero?.trim() || null,
      logradouro: req.logradouro?.trim() || null,
      bairro: req.bairro?.trim() || null,
      cidade: req.cidade?.trim() || null,
      estado: req.estado?.trim() || null,
      linkReuniao: req.linkReuniao?.trim() || null,
      avisos: [],
    };
    REUNIOES_MOCK.push(nova);
    return of(apiSuccess(nova)).pipe(delay(500));
  }

  editarReuniaoAdmin(id: string, req: EditarReuniaoRequest): Observable<ApiEnvelope<Reuniao>> {
    if (!environment.useMocks) {
      return this.http.put<ApiEnvelope<ReuniaoRespostaCompat>>(`${this.adminUrl}/${id}`, req).pipe(
        map((resposta) => this.normalizarEnvelope(resposta)),
        catchError(() => of(apiError<Reuniao>('INTERNAL_ERROR', 'Não foi possível salvar as alterações.'))),
      );
    }

    const reuniao = REUNIOES_MOCK.find((r) => r.id === id);
    if (!reuniao) {
      return of(apiError<Reuniao>('REUNIAO_NAO_ENCONTRADA', 'Reunião não encontrada.')).pipe(delay(300));
    }
    const agenda = this.agendaDoRequest(req);
    if (!req.nome?.trim() || !req.descricao?.trim() || agenda.length === 0 || !req.local?.trim()) {
      return of(apiError<Reuniao>('VALIDATION_ERROR', 'Preencha todos os campos obrigatórios.')).pipe(delay(300));
    }
    const primeiroHorario = agenda[0];
    const cidadeAnterior = reuniao.cidade;
    const estadoAnterior = reuniao.estado?.toUpperCase() ?? null;
    const localAnterior = reuniao.local.trim();

    reuniao.nome = req.nome;
    reuniao.categoria = req.categoria;
    reuniao.descricao = req.descricao;
    reuniao.agenda = agenda;
    reuniao.dia = req.dia?.trim() || ROTULOS_DIAS[primeiroHorario.diaSemana];
    reuniao.horario = req.horario?.trim() || primeiroHorario.horario;
    reuniao.local = req.local;
    reuniao.whatsapp = req.whatsapp?.trim() || null;
    reuniao.diaSemana = req.diaSemana?.trim() || ROTULOS_DIAS[primeiroHorario.diaSemana];
    reuniao.periodo = req.periodo?.trim() || null;
    reuniao.latitude = req.latitude ?? null;
    reuniao.longitude = req.longitude ?? null;
    reuniao.cep = req.cep?.trim() || null;
    reuniao.numero = req.numero?.trim() || null;
    reuniao.logradouro = req.logradouro?.trim() || null;
    reuniao.bairro = req.bairro?.trim() || null;
    reuniao.cidade = req.cidade?.trim() || null;
    reuniao.estado = req.estado?.trim() || null;
    reuniao.linkReuniao = req.linkReuniao?.trim() || null;

    const enderecoMudou =
      reuniao.cidade !== cidadeAnterior || (reuniao.estado?.toUpperCase() ?? null) !== estadoAnterior;
    const localPermaneceuIgual = reuniao.local.trim() === localAnterior;
    reuniao.avisos =
      enderecoMudou && localPermaneceuIgual
        ? [
            {
              codigo: 'ENDERECO_LOCAL_DIVERGENTE',
              mensagem:
                'Cidade/estado do endereço estruturado mudaram, mas o campo "local" não foi atualizado junto. Confira se o texto ainda reflete o endereço correto.',
            },
          ]
        : [];

    return of(apiSuccess(reuniao)).pipe(delay(400));
  }

  backfillGeolocalizacao(): Observable<ApiEnvelope<BackfillGeolocalizacaoResponse>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<BackfillGeolocalizacaoResponse>>(`${this.adminUrl}/backfill-geolocalizacao`, null).pipe(
        catchError(() =>
          of(apiError<BackfillGeolocalizacaoResponse>('INTERNAL_ERROR', 'Não foi possível preencher a geolocalização agora.')),
        ),
      );
    }

    return of(
      apiSuccess<BackfillGeolocalizacaoResponse>({
        totalElegiveis: 0,
        atualizadas: 0,
        falhas: 0,
        detalhesFalha: [],
      }),
    ).pipe(delay(500));
  }

  excluirReuniaoAdmin(id: string): Observable<ApiEnvelope<{ mensagem: string }>> {
    if (!environment.useMocks) {
      return this.http.delete<ApiEnvelope<{ mensagem: string }>>(`${this.adminUrl}/${id}`).pipe(
        catchError(() => of(apiError<{ mensagem: string }>('INTERNAL_ERROR', 'Não foi possível remover a reunião agora.'))),
      );
    }

    const indice = REUNIOES_MOCK.findIndex((r) => r.id === id);
    if (indice === -1) {
      return of(apiError<{ mensagem: string }>('REUNIAO_NAO_ENCONTRADA', 'Reunião não encontrada.')).pipe(delay(300));
    }
    REUNIOES_MOCK.splice(indice, 1);
    return of(apiSuccess({ mensagem: 'Reunião removida.' })).pipe(delay(400));
  }

  private normalizarEnvelopeLista(resposta: ApiEnvelope<ReuniaoRespostaCompat[]>): ApiEnvelope<Reuniao[]> {
    return { ...resposta, data: resposta.data?.map((reuniao) => this.normalizarReuniao(reuniao)) ?? null };
  }

  private normalizarEnvelope(resposta: ApiEnvelope<ReuniaoRespostaCompat>): ApiEnvelope<Reuniao> {
    return { ...resposta, data: resposta.data ? this.normalizarReuniao(resposta.data) : null };
  }

  private normalizarReuniao(reuniao: ReuniaoRespostaCompat): Reuniao {
    const agendaRecebida = reuniao.agenda?.length ? reuniao.agenda : this.agendaLegada(reuniao.diaSemana, reuniao.dia, reuniao.horario);
    return { ...reuniao, agenda: this.ordenarAgenda(agendaRecebida) };
  }

  private agendaDoRequest(req: CriarReuniaoRequest): AgendaItemReuniao[] {
    if (req.agenda?.length) {
      return this.ordenarAgenda(req.agenda);
    }
    return this.ordenarAgenda(this.agendaLegada(req.diaSemana, req.dia, req.horario));
  }

  private agendaLegada(diaSemana: string | null | undefined, dia: string | null | undefined, horario: string | null | undefined): AgendaItemReuniao[] {
    const diaNormalizado = this.normalizarDiaSemana(diaSemana || dia);
    const horarioNormalizado = horario?.trim();
    return diaNormalizado && horarioNormalizado ? [{ diaSemana: diaNormalizado, horario: horarioNormalizado }] : [];
  }

  private normalizarDiaSemana(valor: string | null | undefined): DiaSemanaAgendaReuniao | null {
    const normalizado = valor
      ?.normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .toUpperCase()
      .replace(/-FEIRA$/, '')
      .trim();
    return ORDEM_DIAS.find((dia) => dia === normalizado) ?? null;
  }

  private ordenarAgenda(agenda: AgendaItemReuniao[]): AgendaItemReuniao[] {
    return [...agenda]
      .map((item) => ({ diaSemana: item.diaSemana, horario: item.horario.trim() }))
      .sort((a, b) => ORDEM_DIAS.indexOf(a.diaSemana) - ORDEM_DIAS.indexOf(b.diaSemana));
  }
}

import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, delay } from 'rxjs/operators';

import { environment } from '../../../environments/environment';
import { ApiEnvelope, apiError, apiErrorDeHttpError, apiSuccess } from '../api-envelope';
import {
  CriarEnderecoGrupoRequest,
  CriarEscritorioServicoRequest,
  CriarEstadoRequest,
  CriarGrupoRequest,
  EditarEscritorioServicoRequest,
  EditarEstadoRequest,
  EditarGrupoRequest,
  EditarRegiaoRequest,
  EscritorioServico,
  EscritoriosRegionais,
  EstadoIntergrupo,
  GrupoIntergrupo,
  RegiaoIntergrupo,
  UNIDADES_FEDERATIVAS,
  UnidadeFederativaComRegiao,
} from './escritorio-regional.model';
import { ESCRITORIOS_REGIONAIS_MOCK } from './escritorio-regional.mock';

let contadorIdMock = 0;
function proximoIdMock(prefixo: string): string {
  contadorIdMock += 1;
  return `mock-${prefixo}-${contadorIdMock}`;
}

function nomeUf(uf: string): string {
  return UNIDADES_FEDERATIVAS.find((u) => u.sigla === uf)?.nome ?? uf;
}

function ufValida(uf: string | undefined | null): boolean {
  return !!uf && UNIDADES_FEDERATIVAS.some((u) => u.sigla === uf);
}

@Injectable({ providedIn: 'root' })
export class EscritorioRegionalService {
  private readonly http = inject(HttpClient);
  private readonly publicaUrl = `${environment.apiBaseUrl}/escritorios-regionais`;
  private readonly adminUrl = `${environment.apiBaseUrl}/admin/escritorios-regionais`;
  private readonly unidadesFederativasUrl = `${environment.apiBaseUrl}/unidades-federativas`;

  listarUnidadesFederativas(): Observable<ApiEnvelope<UnidadeFederativaComRegiao[]>> {
    return this.http.get<ApiEnvelope<UnidadeFederativaComRegiao[]>>(this.unidadesFederativasUrl).pipe(
      catchError((erro) => of(apiErrorDeHttpError<UnidadeFederativaComRegiao[]>(erro, 'INTERNAL_ERROR', 'Não foi possível carregar as unidades federativas.'))),
    );
  }

  listarEstrutura(): Observable<ApiEnvelope<EscritoriosRegionais>> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<EscritoriosRegionais>>(this.publicaUrl).pipe(
        catchError((erro) => of(apiErrorDeHttpError<EscritoriosRegionais>(erro, 'INTERNAL_ERROR', 'Não foi possível carregar os escritórios regionais.'))),
      );
    }

    return of(apiSuccess(ESCRITORIOS_REGIONAIS_MOCK)).pipe(delay(400));
  }


  listarEstruturaAdmin(): Observable<ApiEnvelope<EscritoriosRegionais>> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<EscritoriosRegionais>>(this.adminUrl).pipe(
        catchError((erro) => of(apiErrorDeHttpError<EscritoriosRegionais>(erro, 'INTERNAL_ERROR', 'Não foi possível carregar os escritórios regionais.'))),
      );
    }

    return this.listarEstrutura();
  }

  editarRegiaoAdmin(id: string, req: EditarRegiaoRequest): Observable<ApiEnvelope<RegiaoIntergrupo>> {
    if (!environment.useMocks) {
      return this.http.put<ApiEnvelope<RegiaoIntergrupo>>(`${this.adminUrl}/regioes/${id}`, req).pipe(
        catchError((erro) => of(apiErrorDeHttpError<RegiaoIntergrupo>(erro, 'INTERNAL_ERROR', 'Não foi possível salvar as alterações.'))),
      );
    }

    const regiao = ESCRITORIOS_REGIONAIS_MOCK.regioes.find((r) => r.id === id);
    if (!regiao) {
      return of(apiError<RegiaoIntergrupo>('ESCRITORIO_REGIAO_NAO_ENCONTRADA', 'Região não encontrada.')).pipe(delay(300));
    }
    if (!req.nome?.trim()) {
      return of(apiError<RegiaoIntergrupo>('VALIDATION_ERROR', 'Nome é obrigatório.', [{ campo: 'nome', mensagem: 'obrigatório' }])).pipe(delay(300));
    }
    regiao.nome = req.nome;
    regiao.atualizadoEm = new Date().toISOString();
    return of(apiSuccess(regiao)).pipe(delay(400));
  }

  moverRegiaoParaCima(id: string): Observable<ApiEnvelope<RegiaoIntergrupo>> {
    return this.moverRegiao(id, 'cima');
  }

  moverRegiaoParaBaixo(id: string): Observable<ApiEnvelope<RegiaoIntergrupo>> {
    return this.moverRegiao(id, 'baixo');
  }

  private moverRegiao(id: string, direcao: 'cima' | 'baixo'): Observable<ApiEnvelope<RegiaoIntergrupo>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<RegiaoIntergrupo>>(`${this.adminUrl}/regioes/${id}/mover-${direcao}`, {}).pipe(
        catchError((erro) => of(apiErrorDeHttpError<RegiaoIntergrupo>(erro, 'INTERNAL_ERROR', 'Não foi possível reordenar a região agora.'))),
      );
    }

    const lista = ESCRITORIOS_REGIONAIS_MOCK.regioes;
    const indice = lista.findIndex((r) => r.id === id);
    if (indice === -1) {
      return of(apiError<RegiaoIntergrupo>('ESCRITORIO_REGIAO_NAO_ENCONTRADA', 'Região não encontrada.')).pipe(delay(300));
    }
    const item = trocarOrdem(lista, indice, direcao);
    return of(apiSuccess(item)).pipe(delay(300));
  }

  criarEstadoAdmin(regiaoId: string, req: CriarEstadoRequest): Observable<ApiEnvelope<EstadoIntergrupo>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<EstadoIntergrupo>>(`${this.adminUrl}/regioes/${regiaoId}/estados`, req).pipe(
        catchError((erro) => of(apiErrorDeHttpError<EstadoIntergrupo>(erro, 'INTERNAL_ERROR', 'Não foi possível criar o estado agora.'))),
      );
    }

    const regiao = ESCRITORIOS_REGIONAIS_MOCK.regioes.find((r) => r.id === regiaoId);
    if (!regiao) {
      return of(apiError<EstadoIntergrupo>('ESCRITORIO_REGIAO_NAO_ENCONTRADA', 'Região não encontrada.')).pipe(delay(300));
    }
    if (!ufValida(req.uf)) {
      return of(apiError<EstadoIntergrupo>('ESCRITORIO_UF_INVALIDA', 'UF inválida.', [{ campo: 'uf', mensagem: 'inválida' }])).pipe(delay(300));
    }
    if (regiao.estados.some((e) => e.uf === req.uf)) {
      return of(apiError<EstadoIntergrupo>('ESCRITORIO_ESTADO_UF_EM_USO', 'Já existe um estado com esta UF nesta região.')).pipe(delay(300));
    }
    const agora = new Date().toISOString();
    const novo: EstadoIntergrupo = {
      id: proximoIdMock('estado'),
      regiaoId,
      uf: req.uf,
      nome: nomeUf(req.uf),
      ordem: regiao.estados.length,
      grupos: [],
      criadoEm: agora,
      atualizadoEm: agora,
    };
    regiao.estados.push(novo);
    return of(apiSuccess(novo)).pipe(delay(500));
  }

  criarEnderecoAdmin(regiaoId: string, req: CriarEnderecoGrupoRequest): Observable<ApiEnvelope<GrupoIntergrupo>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<GrupoIntergrupo>>(`${this.adminUrl}/regioes/${regiaoId}/enderecos`, req).pipe(
        catchError((erro) => of(apiErrorDeHttpError<GrupoIntergrupo>(erro, 'INTERNAL_ERROR', 'Não foi possível criar o endereço agora.'))),
      );
    }

    const regiao = ESCRITORIOS_REGIONAIS_MOCK.regioes.find((r) => r.id === regiaoId);
    if (!regiao) {
      return of(apiError<GrupoIntergrupo>('ESCRITORIO_REGIAO_NAO_ENCONTRADA', 'Região não encontrada.')).pipe(delay(300));
    }
    if (!ufValida(req.uf)) {
      return of(apiError<GrupoIntergrupo>('ESCRITORIO_UF_INVALIDA', 'UF inválida.', [{ campo: 'uf', mensagem: 'inválida' }])).pipe(delay(300));
    }
    if (!req.nome?.trim() || !req.endereco?.trim() || !req.cidade?.trim() || !req.agenda?.length) {
      return of(apiError<GrupoIntergrupo>('VALIDATION_ERROR', 'Preencha todos os campos obrigatórios.')).pipe(delay(300));
    }

    const agora = new Date().toISOString();
    let estado = regiao.estados.find((e) => e.uf === req.uf);
    if (!estado) {
      estado = {
        id: proximoIdMock('estado'),
        regiaoId,
        uf: req.uf,
        nome: nomeUf(req.uf),
        ordem: regiao.estados.length,
        grupos: [],
        criadoEm: agora,
        atualizadoEm: agora,
      };
      regiao.estados.push(estado);
    }

    const novo: GrupoIntergrupo = {
      id: proximoIdMock('grupo'),
      estadoId: estado.id,
      nome: req.nome,
      endereco: req.endereco,
      bairro: req.bairro?.trim() || null,
      cidade: req.cidade,
      telefones: (req.telefones ?? []).map((t) => ({ id: proximoIdMock('telefone'), label: t.label?.trim() || null, telefone: t.telefone })),
      agenda: req.agenda,
      ordem: estado.grupos.length,
      criadoEm: agora,
      atualizadoEm: agora,
    };
    estado.grupos.push(novo);
    return of(apiSuccess(novo)).pipe(delay(500));
  }

  editarEstadoAdmin(regiaoId: string, id: string, req: EditarEstadoRequest): Observable<ApiEnvelope<EstadoIntergrupo>> {
    if (!environment.useMocks) {
      return this.http.put<ApiEnvelope<EstadoIntergrupo>>(`${this.adminUrl}/regioes/${regiaoId}/estados/${id}`, req).pipe(
        catchError((erro) => of(apiErrorDeHttpError<EstadoIntergrupo>(erro, 'INTERNAL_ERROR', 'Não foi possível salvar as alterações.'))),
      );
    }

    const regiao = ESCRITORIOS_REGIONAIS_MOCK.regioes.find((r) => r.id === regiaoId);
    const estado = regiao?.estados.find((e) => e.id === id);
    if (!regiao || !estado) {
      return of(apiError<EstadoIntergrupo>('ESCRITORIO_ESTADO_NAO_ENCONTRADO', 'Região ou estado não encontrado.')).pipe(delay(300));
    }
    if (!ufValida(req.uf)) {
      return of(apiError<EstadoIntergrupo>('ESCRITORIO_UF_INVALIDA', 'UF inválida.', [{ campo: 'uf', mensagem: 'inválida' }])).pipe(delay(300));
    }
    if (req.uf !== estado.uf && regiao.estados.some((e) => e.uf === req.uf)) {
      return of(apiError<EstadoIntergrupo>('ESCRITORIO_ESTADO_UF_EM_USO', 'Já existe um estado com esta UF nesta região.')).pipe(delay(300));
    }
    estado.uf = req.uf;
    estado.nome = nomeUf(req.uf);
    estado.atualizadoEm = new Date().toISOString();
    return of(apiSuccess(estado)).pipe(delay(400));
  }

  excluirEstadoAdmin(regiaoId: string, id: string): Observable<ApiEnvelope<{ mensagem: string }>> {
    if (!environment.useMocks) {
      return this.http.delete<ApiEnvelope<{ mensagem: string }>>(`${this.adminUrl}/regioes/${regiaoId}/estados/${id}`).pipe(
        catchError((erro) => of(apiErrorDeHttpError<{ mensagem: string }>(erro, 'INTERNAL_ERROR', 'Não foi possível remover o estado agora.'))),
      );
    }

    const regiao = ESCRITORIOS_REGIONAIS_MOCK.regioes.find((r) => r.id === regiaoId);
    const indice = regiao?.estados.findIndex((e) => e.id === id) ?? -1;
    if (!regiao || indice === -1) {
      return of(apiError<{ mensagem: string }>('ESCRITORIO_ESTADO_NAO_ENCONTRADO', 'Região ou estado não encontrado.')).pipe(delay(300));
    }
    regiao.estados.splice(indice, 1);
    return of(apiSuccess({ mensagem: 'Estado removido.' })).pipe(delay(400));
  }

  moverEstadoParaCima(regiaoId: string, id: string): Observable<ApiEnvelope<EstadoIntergrupo>> {
    return this.moverEstado(regiaoId, id, 'cima');
  }

  moverEstadoParaBaixo(regiaoId: string, id: string): Observable<ApiEnvelope<EstadoIntergrupo>> {
    return this.moverEstado(regiaoId, id, 'baixo');
  }

  private moverEstado(regiaoId: string, id: string, direcao: 'cima' | 'baixo'): Observable<ApiEnvelope<EstadoIntergrupo>> {
    if (!environment.useMocks) {
      return this.http
        .post<ApiEnvelope<EstadoIntergrupo>>(`${this.adminUrl}/regioes/${regiaoId}/estados/${id}/mover-${direcao}`, {})
        .pipe(catchError((erro) => of(apiErrorDeHttpError<EstadoIntergrupo>(erro, 'INTERNAL_ERROR', 'Não foi possível reordenar o estado agora.'))));
    }

    const regiao = ESCRITORIOS_REGIONAIS_MOCK.regioes.find((r) => r.id === regiaoId);
    if (!regiao) {
      return of(apiError<EstadoIntergrupo>('ESCRITORIO_REGIAO_NAO_ENCONTRADA', 'Região não encontrada.')).pipe(delay(300));
    }
    const indice = regiao.estados.findIndex((e) => e.id === id);
    if (indice === -1) {
      return of(apiError<EstadoIntergrupo>('ESCRITORIO_ESTADO_NAO_ENCONTRADO', 'Estado não encontrado.')).pipe(delay(300));
    }
    const item = trocarOrdem(regiao.estados, indice, direcao);
    return of(apiSuccess(item)).pipe(delay(300));
  }

  private encontrarEstadoPorId(estadoId: string): EstadoIntergrupo | undefined {
    for (const regiao of ESCRITORIOS_REGIONAIS_MOCK.regioes) {
      const estado = regiao.estados.find((e) => e.id === estadoId);
      if (estado) {
        return estado;
      }
    }
    return undefined;
  }

  criarGrupoAdmin(estadoId: string, req: CriarGrupoRequest): Observable<ApiEnvelope<GrupoIntergrupo>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<GrupoIntergrupo>>(`${this.adminUrl}/estados/${estadoId}/grupos`, req).pipe(
        catchError((erro) => of(apiErrorDeHttpError<GrupoIntergrupo>(erro, 'INTERNAL_ERROR', 'Não foi possível criar o grupo agora.'))),
      );
    }

    const estado = this.encontrarEstadoPorId(estadoId);
    if (!estado) {
      return of(apiError<GrupoIntergrupo>('ESCRITORIO_ESTADO_NAO_ENCONTRADO', 'Estado não encontrado.')).pipe(delay(300));
    }
    if (!req.nome?.trim() || !req.endereco?.trim() || !req.cidade?.trim() || !req.agenda?.length) {
      return of(apiError<GrupoIntergrupo>('VALIDATION_ERROR', 'Preencha todos os campos obrigatórios.')).pipe(delay(300));
    }
    const agora = new Date().toISOString();
    const novo: GrupoIntergrupo = {
      id: proximoIdMock('grupo'),
      estadoId,
      nome: req.nome,
      endereco: req.endereco,
      bairro: req.bairro?.trim() || null,
      cidade: req.cidade,
      telefones: (req.telefones ?? []).map((t) => ({ id: proximoIdMock('telefone'), label: t.label?.trim() || null, telefone: t.telefone })),
      agenda: req.agenda,
      ordem: estado.grupos.length,
      criadoEm: agora,
      atualizadoEm: agora,
    };
    estado.grupos.push(novo);
    return of(apiSuccess(novo)).pipe(delay(500));
  }

  editarGrupoAdmin(estadoId: string, id: string, req: EditarGrupoRequest): Observable<ApiEnvelope<GrupoIntergrupo>> {
    if (!environment.useMocks) {
      return this.http.put<ApiEnvelope<GrupoIntergrupo>>(`${this.adminUrl}/estados/${estadoId}/grupos/${id}`, req).pipe(
        catchError((erro) => of(apiErrorDeHttpError<GrupoIntergrupo>(erro, 'INTERNAL_ERROR', 'Não foi possível salvar as alterações.'))),
      );
    }

    const estado = this.encontrarEstadoPorId(estadoId);
    const grupo = estado?.grupos.find((g) => g.id === id);
    if (!estado || !grupo) {
      return of(apiError<GrupoIntergrupo>('ESCRITORIO_GRUPO_NAO_ENCONTRADO', 'Estado ou grupo não encontrado.')).pipe(delay(300));
    }
    if (!req.nome?.trim() || !req.endereco?.trim() || !req.cidade?.trim() || !req.agenda?.length) {
      return of(apiError<GrupoIntergrupo>('VALIDATION_ERROR', 'Preencha todos os campos obrigatórios.')).pipe(delay(300));
    }
    grupo.nome = req.nome;
    grupo.endereco = req.endereco;
    grupo.bairro = req.bairro?.trim() || null;
    grupo.cidade = req.cidade;
    grupo.telefones = (req.telefones ?? []).map((t) => ({ id: proximoIdMock('telefone'), label: t.label?.trim() || null, telefone: t.telefone }));
    grupo.agenda = req.agenda;
    grupo.atualizadoEm = new Date().toISOString();
    return of(apiSuccess(grupo)).pipe(delay(400));
  }

  excluirGrupoAdmin(estadoId: string, id: string): Observable<ApiEnvelope<{ mensagem: string }>> {
    if (!environment.useMocks) {
      return this.http.delete<ApiEnvelope<{ mensagem: string }>>(`${this.adminUrl}/estados/${estadoId}/grupos/${id}`).pipe(
        catchError((erro) => of(apiErrorDeHttpError<{ mensagem: string }>(erro, 'INTERNAL_ERROR', 'Não foi possível remover o grupo agora.'))),
      );
    }

    const estado = this.encontrarEstadoPorId(estadoId);
    const indice = estado?.grupos.findIndex((g) => g.id === id) ?? -1;
    if (!estado || indice === -1) {
      return of(apiError<{ mensagem: string }>('ESCRITORIO_GRUPO_NAO_ENCONTRADO', 'Estado ou grupo não encontrado.')).pipe(delay(300));
    }
    estado.grupos.splice(indice, 1);
    return of(apiSuccess({ mensagem: 'Grupo removido.' })).pipe(delay(400));
  }

  moverGrupoParaCima(estadoId: string, id: string): Observable<ApiEnvelope<GrupoIntergrupo>> {
    return this.moverGrupo(estadoId, id, 'cima');
  }

  moverGrupoParaBaixo(estadoId: string, id: string): Observable<ApiEnvelope<GrupoIntergrupo>> {
    return this.moverGrupo(estadoId, id, 'baixo');
  }

  private moverGrupo(estadoId: string, id: string, direcao: 'cima' | 'baixo'): Observable<ApiEnvelope<GrupoIntergrupo>> {
    if (!environment.useMocks) {
      return this.http
        .post<ApiEnvelope<GrupoIntergrupo>>(`${this.adminUrl}/estados/${estadoId}/grupos/${id}/mover-${direcao}`, {})
        .pipe(catchError((erro) => of(apiErrorDeHttpError<GrupoIntergrupo>(erro, 'INTERNAL_ERROR', 'Não foi possível reordenar o grupo agora.'))));
    }

    const estado = this.encontrarEstadoPorId(estadoId);
    if (!estado) {
      return of(apiError<GrupoIntergrupo>('ESCRITORIO_ESTADO_NAO_ENCONTRADO', 'Estado não encontrado.')).pipe(delay(300));
    }
    const indice = estado.grupos.findIndex((g) => g.id === id);
    if (indice === -1) {
      return of(apiError<GrupoIntergrupo>('ESCRITORIO_GRUPO_NAO_ENCONTRADO', 'Grupo não encontrado.')).pipe(delay(300));
    }
    const item = trocarOrdem(estado.grupos, indice, direcao);
    return of(apiSuccess(item)).pipe(delay(300));
  }

  criarEscritorioServicoAdmin(req: CriarEscritorioServicoRequest): Observable<ApiEnvelope<EscritorioServico>> {
    if (!environment.useMocks) {
      return this.http.post<ApiEnvelope<EscritorioServico>>(`${this.adminUrl}/escritorios-servico`, req).pipe(
        catchError((erro) => of(apiErrorDeHttpError<EscritorioServico>(erro, 'INTERNAL_ERROR', 'Não foi possível criar o escritório agora.'))),
      );
    }

    if (!req.nome?.trim() || !req.endereco?.trim() || !req.cidade?.trim()) {
      return of(apiError<EscritorioServico>('VALIDATION_ERROR', 'Preencha todos os campos obrigatórios.')).pipe(delay(300));
    }
    const agora = new Date().toISOString();
    const novo: EscritorioServico = {
      id: proximoIdMock('escritorio'),
      nome: req.nome,
      endereco: req.endereco,
      complemento: req.complemento?.trim() || null,
      bairro: req.bairro?.trim() || null,
      cidade: req.cidade,
      emails: req.emails ?? [],
      telefones: (req.telefones ?? []).map((t) => ({ id: proximoIdMock('telefone'), label: t.label?.trim() || null, telefone: t.telefone })),
      ordem: ESCRITORIOS_REGIONAIS_MOCK.escritorios.length,
      criadoEm: agora,
      atualizadoEm: agora,
    };
    ESCRITORIOS_REGIONAIS_MOCK.escritorios.push(novo);
    return of(apiSuccess(novo)).pipe(delay(500));
  }

  editarEscritorioServicoAdmin(id: string, req: EditarEscritorioServicoRequest): Observable<ApiEnvelope<EscritorioServico>> {
    if (!environment.useMocks) {
      return this.http.put<ApiEnvelope<EscritorioServico>>(`${this.adminUrl}/escritorios-servico/${id}`, req).pipe(
        catchError((erro) => of(apiErrorDeHttpError<EscritorioServico>(erro, 'INTERNAL_ERROR', 'Não foi possível salvar as alterações.'))),
      );
    }

    const escritorio = ESCRITORIOS_REGIONAIS_MOCK.escritorios.find((e) => e.id === id);
    if (!escritorio) {
      return of(apiError<EscritorioServico>('ESCRITORIO_SERVICO_NAO_ENCONTRADO', 'Escritório não encontrado.')).pipe(delay(300));
    }
    if (!req.nome?.trim() || !req.endereco?.trim() || !req.cidade?.trim()) {
      return of(apiError<EscritorioServico>('VALIDATION_ERROR', 'Preencha todos os campos obrigatórios.')).pipe(delay(300));
    }
    escritorio.nome = req.nome;
    escritorio.endereco = req.endereco;
    escritorio.complemento = req.complemento?.trim() || null;
    escritorio.bairro = req.bairro?.trim() || null;
    escritorio.cidade = req.cidade;
    escritorio.emails = req.emails ?? [];
    escritorio.telefones = (req.telefones ?? []).map((t) => ({ id: proximoIdMock('telefone'), label: t.label?.trim() || null, telefone: t.telefone }));
    escritorio.atualizadoEm = new Date().toISOString();
    return of(apiSuccess(escritorio)).pipe(delay(400));
  }

  excluirEscritorioServicoAdmin(id: string): Observable<ApiEnvelope<{ mensagem: string }>> {
    if (!environment.useMocks) {
      return this.http.delete<ApiEnvelope<{ mensagem: string }>>(`${this.adminUrl}/escritorios-servico/${id}`).pipe(
        catchError((erro) => of(apiErrorDeHttpError<{ mensagem: string }>(erro, 'INTERNAL_ERROR', 'Não foi possível remover o escritório agora.'))),
      );
    }

    const indice = ESCRITORIOS_REGIONAIS_MOCK.escritorios.findIndex((e) => e.id === id);
    if (indice === -1) {
      return of(apiError<{ mensagem: string }>('ESCRITORIO_SERVICO_NAO_ENCONTRADO', 'Escritório não encontrado.')).pipe(delay(300));
    }
    ESCRITORIOS_REGIONAIS_MOCK.escritorios.splice(indice, 1);
    return of(apiSuccess({ mensagem: 'Escritório de serviço removido.' })).pipe(delay(400));
  }

  moverEscritorioServicoParaCima(id: string): Observable<ApiEnvelope<EscritorioServico>> {
    return this.moverEscritorioServico(id, 'cima');
  }

  moverEscritorioServicoParaBaixo(id: string): Observable<ApiEnvelope<EscritorioServico>> {
    return this.moverEscritorioServico(id, 'baixo');
  }

  private moverEscritorioServico(id: string, direcao: 'cima' | 'baixo'): Observable<ApiEnvelope<EscritorioServico>> {
    if (!environment.useMocks) {
      return this.http
        .post<ApiEnvelope<EscritorioServico>>(`${this.adminUrl}/escritorios-servico/${id}/mover-${direcao}`, {})
        .pipe(catchError((erro) => of(apiErrorDeHttpError<EscritorioServico>(erro, 'INTERNAL_ERROR', 'Não foi possível reordenar o escritório agora.'))));
    }

    const lista = ESCRITORIOS_REGIONAIS_MOCK.escritorios;
    const indice = lista.findIndex((e) => e.id === id);
    if (indice === -1) {
      return of(apiError<EscritorioServico>('ESCRITORIO_SERVICO_NAO_ENCONTRADO', 'Escritório não encontrado.')).pipe(delay(300));
    }
    const item = trocarOrdem(lista, indice, direcao);
    return of(apiSuccess(item)).pipe(delay(300));
  }
}

export function derivarUfsFaltantesPorRegiao(
  regioes: RegiaoIntergrupo[],
  unidadesFederativas: UnidadeFederativaComRegiao[],
): Map<string, UnidadeFederativaComRegiao[]> {
  const ufsPorRegiao = new Map<string, Set<string>>();
  for (const regiao of regioes) {
    ufsPorRegiao.set(regiao.codigo, new Set(regiao.estados.map((estado) => estado.uf)));
  }
  const faltantesPorRegiao = new Map<string, UnidadeFederativaComRegiao[]>();
  for (const unidade of unidadesFederativas) {
    const ufsCadastradas = ufsPorRegiao.get(unidade.codigoRegiao);
    if (ufsCadastradas?.has(unidade.uf)) {
      continue;
    }
    const listaAtual = faltantesPorRegiao.get(unidade.codigoRegiao) ?? [];
    listaAtual.push(unidade);
    faltantesPorRegiao.set(unidade.codigoRegiao, listaAtual);
  }
  return faltantesPorRegiao;
}

function trocarOrdem<T extends { ordem: number }>(lista: T[], indice: number, direcao: 'cima' | 'baixo'): T {
  const item = lista[indice];
  const indiceVizinho = direcao === 'cima' ? indice - 1 : indice + 1;
  if (indiceVizinho < 0 || indiceVizinho >= lista.length) {
    return item;
  }
  const ordemAtual = item.ordem;
  item.ordem = lista[indiceVizinho].ordem;
  lista[indiceVizinho].ordem = ordemAtual;
  [lista[indice], lista[indiceVizinho]] = [lista[indiceVizinho], lista[indice]];
  return item;
}

import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';

import { EscritorioRegionalService } from './escritorio-regional';
import {
  CriarEstadoRequest,
  CriarGrupoRequest,
  EscritorioServico,
  EscritoriosRegionais,
  EstadoIntergrupo,
  GrupoIntergrupo,
  RegiaoIntergrupo,
} from './escritorio-regional.model';
import { ApiEnvelope, apiSuccess } from '../api-envelope';
import { environment } from '../../../environments/environment';

describe('EscritorioRegionalService', () => {
  let service: EscritorioRegionalService;
  let httpMock: HttpTestingController;

  const adminUrl = `${environment.apiBaseUrl}/admin/escritorios-regionais`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(EscritorioRegionalService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  function regiaoFake(overrides: Partial<RegiaoIntergrupo> = {}): RegiaoIntergrupo {
    return {
      id: 'regiao-fake-1',
      nome: 'Região Fake',
      ordem: 0,
      estados: [],
      criadoEm: '2026-01-01T00:00:00.000Z',
      atualizadoEm: '2026-01-01T00:00:00.000Z',
      ...overrides,
    };
  }

  function estadoFake(overrides: Partial<EstadoIntergrupo> = {}): EstadoIntergrupo {
    return {
      id: 'estado-fake-1',
      regiaoId: 'regiao-fake-1',
      uf: 'SP',
      nome: 'São Paulo',
      ordem: 0,
      grupos: [],
      criadoEm: '2026-01-01T00:00:00.000Z',
      atualizadoEm: '2026-01-01T00:00:00.000Z',
      ...overrides,
    };
  }

  it('criarEstadoAdmin envia o payload correto no corpo da requisição', () => {
    const req: CriarEstadoRequest = { uf: 'SP' };
    const regiaoId = 'regiao-fake-1';

    service.criarEstadoAdmin(regiaoId, req).subscribe();

    const chamada = httpMock.expectOne(`${adminUrl}/regioes/${regiaoId}/estados`);
    expect(chamada.request.method).toBe('POST');
    expect(chamada.request.body).toEqual(req);
    chamada.flush(apiSuccess(estadoFake()));
  });

  it('trata erro CONFLICT do backend ao criar estado com UF já existente na região', async () => {
    const promise = firstValueFrom(service.criarEstadoAdmin('regiao-fake-1', { uf: 'SP' }));

    const chamada = httpMock.expectOne(`${adminUrl}/regioes/regiao-fake-1/estados`);
    chamada.flush(
      { success: false, data: null, error: { code: 'CONFLICT', message: 'Já existe um estado com esta UF nesta região.' } },
      { status: 409, statusText: 'Conflict' },
    );

    const envelope = await promise;
    expect(envelope.success).toBe(false);
  });

  it('nunca deixa uma requisição HTTP sem tratamento de erro (catchError) — falha de rede não deve propagar exceção', async () => {
    const promise = firstValueFrom(service.criarEstadoAdmin('regiao-fake-1', { uf: 'SP' }));

    const chamada = httpMock.expectOne(`${adminUrl}/regioes/regiao-fake-1/estados`);
    chamada.error(new ProgressEvent('network error'));

    const envelope = await promise;
    expect(envelope.success).toBe(false);
    expect(envelope.error?.code).toBe('UPSTREAM_ERROR');
  });

  it('editarEstadoAdmin envia o payload correto com PUT', () => {
    const req: CriarEstadoRequest = { uf: 'RJ' };

    service.editarEstadoAdmin('regiao-fake-1', 'estado-fake-1', req).subscribe();

    const chamada = httpMock.expectOne(`${adminUrl}/regioes/regiao-fake-1/estados/estado-fake-1`);
    expect(chamada.request.method).toBe('PUT');
    expect(chamada.request.body).toEqual(req);
    chamada.flush(apiSuccess(estadoFake({ uf: 'RJ', nome: 'Rio de Janeiro' })));
  });

  it('criarGrupoAdmin envia agenda com múltiplos dias e horários próprios e lista de telefones no payload', () => {
    const req: CriarGrupoRequest = {
      nome: 'Grupo Teste',
      endereco: 'Rua Fake, 100',
      cidade: 'Cidade Fake - UF',
      agenda: [
        { diaSemana: 'SEGUNDA', horario: '19:00' },
        { diaSemana: 'QUARTA', horario: '19:00' },
        { diaSemana: 'SEXTA', horario: '20:30' },
      ],
      telefones: [
        { label: 'Central', telefone: '11-90000-0000' },
        { telefone: '11-91111-1111' },
      ],
    };

    service.criarGrupoAdmin('estado-fake-1', req).subscribe();

    const chamada = httpMock.expectOne(`${adminUrl}/estados/estado-fake-1/grupos`);
    expect(chamada.request.method).toBe('POST');
    expect(chamada.request.body).toEqual(req);
    expect(chamada.request.body.agenda).toHaveLength(3);
    expect(chamada.request.body.telefones).toHaveLength(2);
    chamada.flush(
      apiSuccess({
        id: 'grupo-fake-1',
        estadoId: 'estado-fake-1',
        nome: req.nome,
        endereco: req.endereco,
        bairro: null,
        cidade: req.cidade,
        telefones: req.telefones.map((t, i) => ({ id: `tel-${i}`, label: t.label ?? null, telefone: t.telefone })),
        agenda: req.agenda,
        ordem: 0,
        criadoEm: '2026-01-01T00:00:00.000Z',
        atualizadoEm: '2026-01-01T00:00:00.000Z',
      } satisfies GrupoIntergrupo),
    );
  });

  it('moverRegiaoParaCima chama o endpoint POST mover-cima', () => {
    service.moverRegiaoParaCima('regiao-fake-1').subscribe();

    const chamada = httpMock.expectOne(`${adminUrl}/regioes/regiao-fake-1/mover-cima`);
    expect(chamada.request.method).toBe('POST');
    chamada.flush(apiSuccess(regiaoFake()));
  });

  it('moverEstadoParaBaixo chama o endpoint POST mover-baixo com o id da região no path', () => {
    service.moverEstadoParaBaixo('regiao-fake-1', 'estado-fake-1').subscribe();

    const chamada = httpMock.expectOne(`${adminUrl}/regioes/regiao-fake-1/estados/estado-fake-1/mover-baixo`);
    expect(chamada.request.method).toBe('POST');
    chamada.flush(apiSuccess(estadoFake()));
  });

  it('moverGrupoParaCima e moverEscritorioServicoParaBaixo chamam os endpoints corretos', () => {
    service.moverGrupoParaCima('estado-fake-1', 'grupo-fake-1').subscribe();
    httpMock.expectOne(`${adminUrl}/estados/estado-fake-1/grupos/grupo-fake-1/mover-cima`).flush(
      apiSuccess({
        id: 'grupo-fake-1',
        estadoId: 'estado-fake-1',
        nome: 'Grupo',
        endereco: 'Rua Fake',
        bairro: null,
        cidade: 'Cidade Fake',
        telefones: [],
        agenda: [{ diaSemana: 'SEGUNDA', horario: '19:00' }],
        ordem: 0,
        criadoEm: '2026-01-01T00:00:00.000Z',
        atualizadoEm: '2026-01-01T00:00:00.000Z',
      } satisfies GrupoIntergrupo),
    );

    service.moverEscritorioServicoParaBaixo('escritorio-fake-1').subscribe();
    httpMock.expectOne(`${adminUrl}/escritorios-servico/escritorio-fake-1/mover-baixo`).flush(
      apiSuccess({
        id: 'escritorio-fake-1',
        nome: 'Escritório Fake',
        endereco: 'Rua Fake',
        complemento: null,
        bairro: null,
        cidade: 'Cidade Fake',
        emails: [],
        telefones: [],
        ordem: 1,
        criadoEm: '2026-01-01T00:00:00.000Z',
        atualizadoEm: '2026-01-01T00:00:00.000Z',
      } satisfies EscritorioServico),
    );
  });

  it('listarEstruturaAdmin não loga nenhum dado sensível no console ao processar a resposta', () => {
    const consoleSpy = vi.spyOn(console, 'log');
    const consoleErrorSpy = vi.spyOn(console, 'error');

    service.listarEstruturaAdmin().subscribe();

    const estrutura: EscritoriosRegionais = { escritorios: [], regioes: [regiaoFake()] };
    httpMock.expectOne(adminUrl).flush(apiSuccess(estrutura));

    expect(consoleSpy).not.toHaveBeenCalled();
    expect(consoleErrorSpy).not.toHaveBeenCalled();
  });

  it('listarEstruturaAdmin trata erro de upstream retornando envelope de erro', async () => {
    const promise = firstValueFrom(service.listarEstruturaAdmin());

    httpMock.expectOne(adminUrl).flush('erro', { status: 500, statusText: 'Internal Server Error' });

    const envelope: ApiEnvelope<EscritoriosRegionais> = await promise;
    expect(envelope.success).toBe(false);
    expect(envelope.error?.code).toBe('UPSTREAM_ERROR');
  });
});

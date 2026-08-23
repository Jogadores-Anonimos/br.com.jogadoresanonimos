import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../../environments/environment';
import { apiSuccess } from '../api-envelope';
import { ReuniaoService } from './reuniao';

describe('ReuniaoService', () => {
  let service: ReuniaoService;
  let httpMock: HttpTestingController;
  const publicaUrl = `${environment.apiBaseUrl}/reunioes`;
  const reuniaoLegada = {
    id: 'reuniao-1',
    nome: 'Grupo Centro',
    categoria: 'presencial' as const,
    descricao: 'Descrição',
    dia: 'Terça-feira',
    horario: '19h30',
    local: 'Centro',
    whatsapp: null,
    diaSemana: 'Terça',
    periodo: 'Noite',
    latitude: null,
    longitude: null,
    cep: null,
    numero: null,
    logradouro: null,
    bairro: null,
    cidade: null,
    estado: null,
    linkReuniao: null,
    avisos: [],
  };
  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    service = TestBed.inject(ReuniaoService);
    httpMock = TestBed.inject(HttpTestingController);
  });
  afterEach(() => httpMock.verify());
  it('converte campos legados em agenda durante o rollout', async () => {
    const promise = firstValueFrom(service.listarReunioes());
    httpMock.expectOne(publicaUrl).flush(apiSuccess([reuniaoLegada]));
    const resposta = await promise;
    expect(resposta.data?.[0].agenda).toEqual([{ diaSemana: 'TERCA', horario: '19h30' }]);
  });
  it('preserva ocorrências e ordena a agenda por dia e horário', async () => {
    const promise = firstValueFrom(service.listarReunioes());
    httpMock.expectOne(publicaUrl).flush(
      apiSuccess([
        {
          ...reuniaoLegada,
          agenda: [
            { diaSemana: 'SEGUNDA', horario: '9h' },
            { diaSemana: 'SABADO', horario: '10h00' },
            { diaSemana: 'SEGUNDA', horario: '21h00' },
            { diaSemana: 'SEGUNDA', horario: '09:30' },
            { diaSemana: 'SEGUNDA', horario: '19h30' },
          ],
        },
      ]),
    );
    const resposta = await promise;
    expect(resposta.data?.[0].agenda).toEqual([
      { diaSemana: 'SEGUNDA', horario: '9h' },
      { diaSemana: 'SEGUNDA', horario: '09:30' },
      { diaSemana: 'SEGUNDA', horario: '19h30' },
      { diaSemana: 'SEGUNDA', horario: '21h00' },
      { diaSemana: 'SABADO', horario: '10h00' },
    ]);
  });
  it('envia agenda no payload administrativo sem remover os campos legados', () => {
    const request = {
      nome: 'Grupo Centro',
      categoria: 'presencial' as const,
      descricao: 'Descrição',
      agenda: [
        { diaSemana: 'SEGUNDA' as const, horario: '19h30' },
        { diaSemana: 'SEGUNDA' as const, horario: '21h00' },
      ],
      dia: 'Segunda-feira',
      horario: '19h30',
      local: 'Centro',
      diaSemana: 'Segunda',
      periodo: 'Noite',
    };
    service.criarReuniaoAdmin(request).subscribe();
    const chamada = httpMock.expectOne(`${environment.apiBaseUrl}/admin/reunioes`);
    expect(chamada.request.body).toEqual(request);
    chamada.flush(apiSuccess({ ...reuniaoLegada, ...request, id: 'reuniao-2', agenda: request.agenda }));
  });
  it('envia toda a agenda ao editar e normaliza a resposta administrativa', async () => {
    const request = {
      nome: 'Grupo Centro',
      categoria: 'presencial' as const,
      descricao: 'Descrição atualizada',
      agenda: [
        { diaSemana: 'QUINTA' as const, horario: '20h30' },
        { diaSemana: 'SEGUNDA' as const, horario: '19h30' },
      ],
      dia: 'Segunda-feira',
      horario: '19h30',
      local: 'Centro',
      diaSemana: 'Segunda',
      periodo: 'Noite',
    };
    const promise = firstValueFrom(service.editarReuniaoAdmin('reuniao-1', request));
    const chamada = httpMock.expectOne(`${environment.apiBaseUrl}/admin/reunioes/reuniao-1`);
    expect(chamada.request.method).toBe('PUT');
    expect(chamada.request.body).toEqual(request);
    chamada.flush(apiSuccess({ ...reuniaoLegada, ...request }));
    const resposta = await promise;
    expect(resposta.data?.agenda).toEqual([
      { diaSemana: 'SEGUNDA', horario: '19h30' },
      { diaSemana: 'QUINTA', horario: '20h30' },
    ]);
  });
});

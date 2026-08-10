import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';

import { EnderecoLookupService } from './endereco-lookup';
import { EnderecoViaCep, ResultadoNominatim } from './endereco-lookup.model';

describe('EnderecoLookupService', () => {
  let service: EnderecoLookupService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(EnderecoLookupService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('buscarCep', () => {
    it('retorna o endereço ao consultar o ViaCEP com sucesso', async () => {
      const promise = firstValueFrom(service.buscarCep('01001000'));

      const chamada = httpMock.expectOne('https://viacep.com.br/ws/01001000/json/');
      expect(chamada.request.method).toBe('GET');

      const respostaFake: EnderecoViaCep = {
        logradouro: 'Praça da Sé',
        bairro: 'Sé',
        localidade: 'São Paulo',
        uf: 'SP',
      };
      chamada.flush(respostaFake);

      const resultado = await promise;
      expect(resultado).toEqual(respostaFake);
    });

    it('retorna null quando a chamada ao ViaCEP falha (erro de rede/upstream)', async () => {
      const promise = firstValueFrom(service.buscarCep('99999999'));

      httpMock.expectOne('https://viacep.com.br/ws/99999999/json/').flush('erro', {
        status: 500,
        statusText: 'Internal Server Error',
      });

      const resultado = await promise;
      expect(resultado).toBeNull();
    });

    it('não loga dado nenhum no console ao processar a resposta', async () => {
      const consoleSpy = vi.spyOn(console, 'log');
      const consoleErrorSpy = vi.spyOn(console, 'error');

      const promise = firstValueFrom(service.buscarCep('01001000'));
      httpMock.expectOne('https://viacep.com.br/ws/01001000/json/').flush({
        logradouro: 'Praça da Sé',
        bairro: 'Sé',
        localidade: 'São Paulo',
        uf: 'SP',
      });
      await promise;

      expect(consoleSpy).not.toHaveBeenCalled();
      expect(consoleErrorSpy).not.toHaveBeenCalled();
    });
  });

  describe('geocodificar', () => {
    it('retorna as coordenadas ao consultar o Nominatim com sucesso', async () => {
      const promise = firstValueFrom(service.geocodificar('Praça da Sé, Sé, São Paulo, SP'));

      const chamada = httpMock.expectOne(
        (req) => req.url === 'https://nominatim.openstreetmap.org/search',
      );
      expect(chamada.request.method).toBe('GET');
      expect(chamada.request.params.get('format')).toBe('json');
      expect(chamada.request.params.get('q')).toBe('Praça da Sé, Sé, São Paulo, SP');
      expect(chamada.request.params.get('limit')).toBe('1');

      const respostaFake: ResultadoNominatim[] = [{ lat: '-23.55', lon: '-46.63' }];
      chamada.flush(respostaFake);

      const resultado = await promise;
      expect(resultado).toEqual(respostaFake);
    });

    it('retorna array vazio quando a chamada ao Nominatim falha (erro de rede/upstream)', async () => {
      const promise = firstValueFrom(service.geocodificar('Endereço inexistente, 000'));

      httpMock
        .expectOne((req) => req.url === 'https://nominatim.openstreetmap.org/search')
        .flush('erro', { status: 503, statusText: 'Service Unavailable' });

      const resultado = await promise;
      expect(resultado).toEqual([]);
    });

    it('retorna array vazio quando o Nominatim não encontra nenhum resultado', async () => {
      const promise = firstValueFrom(service.geocodificar('Endereço sem correspondência'));

      httpMock
        .expectOne((req) => req.url === 'https://nominatim.openstreetmap.org/search')
        .flush([]);

      const resultado = await promise;
      expect(resultado).toEqual([]);
    });
  });
});

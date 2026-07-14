import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';

import { FaqService } from './faq';
import { CriarFaqRequest, Faq } from './faq.model';
import { ApiEnvelope, apiSuccess } from './api-envelope';
import { environment } from '../../environments/environment';

describe('FaqService', () => {
  let service: FaqService;
  let httpMock: HttpTestingController;

  const adminUrl = `${environment.apiBaseUrl}/admin/faq`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(FaqService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  function faqFake(overrides: Partial<Faq> = {}): Faq {
    return { id: 'faq-fake-1', pergunta: 'Pergunta fake?', resposta: 'Resposta fake.', ordem: 0, ...overrides };
  }

  it('criarFaqAdmin envia apenas pergunta e resposta, sem campo ordem', () => {
    const req: CriarFaqRequest = { pergunta: 'Pergunta nova?', resposta: 'Resposta nova.' };

    service.criarFaqAdmin(req).subscribe();

    const chamada = httpMock.expectOne(adminUrl);
    expect(chamada.request.method).toBe('POST');
    expect(chamada.request.body).toEqual(req);
    expect(chamada.request.body.ordem).toBeUndefined();
    chamada.flush(apiSuccess(faqFake()));
  });

  it('editarFaqAdmin envia apenas pergunta e resposta, sem campo ordem', () => {
    const req: CriarFaqRequest = { pergunta: 'Pergunta editada?', resposta: 'Resposta editada.' };

    service.editarFaqAdmin('faq-fake-1', req).subscribe();

    const chamada = httpMock.expectOne(`${adminUrl}/faq-fake-1`);
    expect(chamada.request.method).toBe('PUT');
    expect(chamada.request.body).toEqual(req);
    expect(Object.keys(chamada.request.body)).not.toContain('ordem');
    chamada.flush(apiSuccess(faqFake(req)));
  });

  it('moverFaqParaCima chama o endpoint POST mover-cima', () => {
    service.moverFaqParaCima('faq-fake-1').subscribe();

    const chamada = httpMock.expectOne(`${adminUrl}/faq-fake-1/mover-cima`);
    expect(chamada.request.method).toBe('POST');
    chamada.flush(apiSuccess(faqFake()));
  });

  it('moverFaqParaBaixo chama o endpoint POST mover-baixo', () => {
    service.moverFaqParaBaixo('faq-fake-1').subscribe();

    const chamada = httpMock.expectOne(`${adminUrl}/faq-fake-1/mover-baixo`);
    expect(chamada.request.method).toBe('POST');
    chamada.flush(apiSuccess(faqFake()));
  });

  it('trata erro de upstream (ex: rede/500) retornando envelope de erro em vez de lançar exceção', async () => {
    const promise = firstValueFrom(service.criarFaqAdmin({ pergunta: 'X?', resposta: 'Y.' }));

    httpMock.expectOne(adminUrl).flush('erro', { status: 500, statusText: 'Internal Server Error' });

    const envelope: ApiEnvelope<Faq> = await promise;
    expect(envelope.success).toBe(false);
    expect(envelope.error?.code).toBe('UPSTREAM_ERROR');
  });

  it('listarFaqAdmin não loga dado nenhum no console ao processar a resposta', () => {
    const consoleSpy = vi.spyOn(console, 'log');
    const consoleErrorSpy = vi.spyOn(console, 'error');

    service.listarFaqAdmin().subscribe();

    httpMock.expectOne(adminUrl).flush(apiSuccess([faqFake()]));

    expect(consoleSpy).not.toHaveBeenCalled();
    expect(consoleErrorSpy).not.toHaveBeenCalled();
  });
});

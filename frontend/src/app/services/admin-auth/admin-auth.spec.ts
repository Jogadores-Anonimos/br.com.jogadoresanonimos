import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';

import { AdminAuthService } from './admin-auth';
import { AdminRedefinirSenhaRequest } from './admin-auth.model';
import { ApiEnvelope, apiSuccess } from '../api-envelope';
import { environment } from '../../../environments/environment';

describe('AdminAuthService', () => {
  let service: AdminAuthService;
  let httpMock: HttpTestingController;

  const baseUrl = `${environment.apiBaseUrl}/admin/auth`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(AdminAuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('redefinirSenha', () => {
    function requestValido(overrides: Partial<AdminRedefinirSenhaRequest> = {}): AdminRedefinirSenhaRequest {
      return { codigo: '123456', novaSenha: 'senha-fake-123', confirmarNovaSenha: 'senha-fake-123', ...overrides };
    }

    it('envia POST para /admin/auth/redefinir-senha com o payload esperado pelo contrato', () => {
      const req = requestValido();

      service.redefinirSenha(req).subscribe();

      const chamada = httpMock.expectOne(`${baseUrl}/redefinir-senha`);
      expect(chamada.request.method).toBe('POST');
      expect(chamada.request.body).toEqual(req);
      expect(Object.keys(chamada.request.body)).toEqual(['codigo', 'novaSenha', 'confirmarNovaSenha']);
      chamada.flush(apiSuccess({ mensagem: 'Senha redefinida com sucesso.' }));
    });

    it('não chama a API quando o código não tem 6 dígitos numéricos', () => {
      service.redefinirSenha(requestValido({ codigo: '123' })).subscribe((resposta) => {
        expect(resposta.success).toBe(false);
        expect(resposta.error?.code).toBe('VALIDATION_ERROR');
      });

      httpMock.expectNone(`${baseUrl}/redefinir-senha`);
    });

    it('não chama a API quando o código contém caracteres não numéricos', () => {
      service.redefinirSenha(requestValido({ codigo: 'abcdef' })).subscribe((resposta) => {
        expect(resposta.success).toBe(false);
        expect(resposta.error?.code).toBe('VALIDATION_ERROR');
      });

      httpMock.expectNone(`${baseUrl}/redefinir-senha`);
    });

    it('não chama a API quando a nova senha tem menos de 10 caracteres', () => {
      service.redefinirSenha(requestValido({ novaSenha: 'curta12', confirmarNovaSenha: 'curta12' })).subscribe((resposta) => {
        expect(resposta.success).toBe(false);
        expect(resposta.error?.code).toBe('VALIDATION_ERROR');
      });

      httpMock.expectNone(`${baseUrl}/redefinir-senha`);
    });

    it('não chama a API quando a confirmação de senha não bate com a nova senha', () => {
      service.redefinirSenha(requestValido({ confirmarNovaSenha: 'outra-senha-fake' })).subscribe((resposta) => {
        expect(resposta.success).toBe(false);
        expect(resposta.error?.code).toBe('VALIDATION_ERROR');
      });

      httpMock.expectNone(`${baseUrl}/redefinir-senha`);
    });

    it('trata erro de upstream (ex: rede/500) retornando envelope de erro em vez de lançar exceção', async () => {
      const promise = firstValueFrom(service.redefinirSenha(requestValido()));

      httpMock.expectOne(`${baseUrl}/redefinir-senha`).flush('erro', { status: 500, statusText: 'Internal Server Error' });

      const envelope: ApiEnvelope<{ mensagem: string }> = await promise;
      expect(envelope.success).toBe(false);
      expect(envelope.error?.code).toBe('INTERNAL_ERROR');
    });

    it('propaga o erro ADMIN_RESET_CODIGO_INVALIDO (401) vindo da API sem alterar o code', async () => {
      const promise = firstValueFrom(service.redefinirSenha(requestValido()));

      httpMock.expectOne(`${baseUrl}/redefinir-senha`).flush(
        { success: false, data: null, error: { code: 'ADMIN_RESET_CODIGO_INVALIDO', message: 'Código inválido, expirado ou já utilizado.' } },
        { status: 401, statusText: 'Unauthorized' },
      );

      const envelope = await promise;
      expect(envelope.success).toBe(false);
      expect(envelope.error?.code).toBe('ADMIN_RESET_CODIGO_INVALIDO');
    });

    it('propaga erro 429 de rate limit vindo da API', async () => {
      const promise = firstValueFrom(service.redefinirSenha(requestValido()));

      httpMock.expectOne(`${baseUrl}/redefinir-senha`).flush(
        { success: false, data: null, error: { code: 'AUTH_RATE_LIMITED', message: 'Muitas tentativas. Aguarde alguns instantes e tente novamente.' } },
        { status: 429, statusText: 'Too Many Requests' },
      );

      const envelope = await promise;
      expect(envelope.success).toBe(false);
      expect(envelope.error?.code).toBe('AUTH_RATE_LIMITED');
    });

    it('não loga o código nem a senha no console ao processar a resposta', () => {
      const consoleSpy = vi.spyOn(console, 'log');
      const consoleErrorSpy = vi.spyOn(console, 'error');

      service.redefinirSenha(requestValido()).subscribe();

      httpMock.expectOne(`${baseUrl}/redefinir-senha`).flush(apiSuccess({ mensagem: 'ok' }));

      expect(consoleSpy).not.toHaveBeenCalled();
      expect(consoleErrorSpy).not.toHaveBeenCalled();
    });
  });
});

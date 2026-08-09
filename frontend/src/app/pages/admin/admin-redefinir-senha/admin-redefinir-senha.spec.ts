import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { AdminRedefinirSenha } from './admin-redefinir-senha';
import { AdminAuthService } from '../../../services/admin-auth/admin-auth';
import { apiError, apiSuccess } from '../../../services/api-envelope';

describe('AdminRedefinirSenha', () => {
  let component: AdminRedefinirSenha;
  let fixture: ComponentFixture<AdminRedefinirSenha>;
  let adminAuthSpy: Record<string, ReturnType<typeof vi.fn>>;

  function configurar(): void {
    adminAuthSpy = {
      redefinirSenha: vi.fn(),
    };
  }

  async function criarComponente(): Promise<void> {
    await TestBed.configureTestingModule({
      imports: [AdminRedefinirSenha],
      providers: [provideRouter([]), { provide: AdminAuthService, useValue: adminAuthSpy }],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminRedefinirSenha);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  function preencherFormulario(valores: { codigo?: string; novaSenha?: string; confirmarNovaSenha?: string }): void {
    const codigoInput: HTMLInputElement = fixture.nativeElement.querySelector('#admin-redefinir-codigo');
    const novaSenhaInput: HTMLInputElement = fixture.nativeElement.querySelector('#admin-redefinir-nova-senha');
    const confirmarInput: HTMLInputElement = fixture.nativeElement.querySelector('#admin-redefinir-confirmar-senha');

    if (valores.codigo !== undefined) {
      codigoInput.value = valores.codigo;
      codigoInput.dispatchEvent(new Event('input'));
    }
    if (valores.novaSenha !== undefined) {
      novaSenhaInput.value = valores.novaSenha;
      novaSenhaInput.dispatchEvent(new Event('input'));
    }
    if (valores.confirmarNovaSenha !== undefined) {
      confirmarInput.value = valores.confirmarNovaSenha;
      confirmarInput.dispatchEvent(new Event('input'));
    }
    fixture.detectChanges();
  }

  function botaoSubmit(): HTMLButtonElement {
    return fixture.nativeElement.querySelector('button[type="submit"]');
  }

  beforeEach(async () => {
    configurar();
    await criarComponente();
  });

  it('deve criar', () => {
    expect(component).toBeTruthy();
  });

  describe('validação client-side bloqueia o submit antes de chamar a API', () => {
    it('bloqueia quando o código tem menos de 6 dígitos', async () => {
      preencherFormulario({ codigo: '123', novaSenha: 'senha-fake-123', confirmarNovaSenha: 'senha-fake-123' });
      await fixture.whenStable();
      fixture.detectChanges();

      expect(botaoSubmit().disabled).toBe(true);
      expect(adminAuthSpy['redefinirSenha']).not.toHaveBeenCalled();
    });

    it('bloqueia quando o código contém caracteres não numéricos', async () => {
      preencherFormulario({ codigo: 'abcdef', novaSenha: 'senha-fake-123', confirmarNovaSenha: 'senha-fake-123' });
      await fixture.whenStable();
      fixture.detectChanges();

      expect(botaoSubmit().disabled).toBe(true);
      expect(adminAuthSpy['redefinirSenha']).not.toHaveBeenCalled();
    });

    it('bloqueia quando a senha tem menos de 10 caracteres', async () => {
      preencherFormulario({ codigo: '123456', novaSenha: 'curta12', confirmarNovaSenha: 'curta12' });
      await fixture.whenStable();
      fixture.detectChanges();

      expect(botaoSubmit().disabled).toBe(true);
      expect(adminAuthSpy['redefinirSenha']).not.toHaveBeenCalled();
    });

    it('bloqueia quando a confirmação de senha não bate com a nova senha', async () => {
      preencherFormulario({ codigo: '123456', novaSenha: 'senha-fake-123', confirmarNovaSenha: 'senha-fake-999' });
      await fixture.whenStable();
      fixture.detectChanges();

      expect(botaoSubmit().disabled).toBe(true);
      expect(adminAuthSpy['redefinirSenha']).not.toHaveBeenCalled();
    });

    it('habilita o submit quando todos os campos são válidos', async () => {
      preencherFormulario({ codigo: '123456', novaSenha: 'senha-fake-123', confirmarNovaSenha: 'senha-fake-123' });
      await fixture.whenStable();
      fixture.detectChanges();

      expect(botaoSubmit().disabled).toBe(false);
    });
  });

  describe('submit válido', () => {
    it('chama AdminAuthService.redefinirSenha com o payload informado', () => {
      adminAuthSpy['redefinirSenha'].mockReturnValue(of(apiSuccess({ mensagem: 'Senha redefinida com sucesso.' })));
      component.codigo = '123456';
      component.novaSenha = 'senha-fake-123';
      component.confirmarNovaSenha = 'senha-fake-123';

      component.onSubmit();

      expect(adminAuthSpy['redefinirSenha']).toHaveBeenCalledWith({
        codigo: '123456',
        novaSenha: 'senha-fake-123',
        confirmarNovaSenha: 'senha-fake-123',
      });
    });

    it('mostra o CTA "Ir para o login" após sucesso', () => {
      adminAuthSpy['redefinirSenha'].mockReturnValue(of(apiSuccess({ mensagem: 'Senha redefinida com sucesso.' })));
      component.codigo = '123456';
      component.novaSenha = 'senha-fake-123';
      component.confirmarNovaSenha = 'senha-fake-123';

      component.onSubmit();
      fixture.detectChanges();

      expect(component.sucesso).toBe(true);
      const link: HTMLAnchorElement = fixture.nativeElement.querySelector('a[routerlink="/minha-conta"]');
      expect(link.textContent).toContain('Ir para o login');
    });
  });

  describe('tratamento de erro da API', () => {
    it('mostra mensagem genérica única para ADMIN_RESET_CODIGO_INVALIDO, sem diferenciar código errado/expirado/usado', () => {
      adminAuthSpy['redefinirSenha'].mockReturnValue(
        of(apiError('ADMIN_RESET_CODIGO_INVALIDO', 'Código inválido, expirado ou já utilizado.')),
      );
      component.codigo = '123456';
      component.novaSenha = 'senha-fake-123';
      component.confirmarNovaSenha = 'senha-fake-123';

      component.onSubmit();

      expect(component.sucesso).toBe(false);
      expect(component.mensagem).toBe('Código inválido, expirado ou já utilizado.');
    });

    it('mostra a mesma mensagem genérica independente do motivo real de falha do código', () => {
      adminAuthSpy['redefinirSenha'].mockReturnValue(
        of(apiError('ADMIN_RESET_CODIGO_INVALIDO', 'Código inválido, expirado ou já utilizado.')),
      );
      component.codigo = '999999';
      component.novaSenha = 'outra-senha-fake-123';
      component.confirmarNovaSenha = 'outra-senha-fake-123';

      component.onSubmit();

      expect(component.mensagem).toBe('Código inválido, expirado ou já utilizado.');
      expect(component.mensagem).not.toContain('expirado especificamente');
      expect(component.mensagem).not.toContain('já foi usado');
    });

    it('mostra mensagem de rate limit quando a API retorna erro 429 (AUTH_RATE_LIMITED)', () => {
      adminAuthSpy['redefinirSenha'].mockReturnValue(
        of(apiError('AUTH_RATE_LIMITED', 'Muitas tentativas. Aguarde alguns instantes e tente novamente.')),
      );
      component.codigo = '123456';
      component.novaSenha = 'senha-fake-123';
      component.confirmarNovaSenha = 'senha-fake-123';

      component.onSubmit();

      expect(component.sucesso).toBe(false);
      expect(component.mensagem).toBe('Muitas tentativas. Aguarde alguns instantes e tente novamente.');
    });

    it('não mostra o CTA de login quando a API retorna erro', () => {
      adminAuthSpy['redefinirSenha'].mockReturnValue(
        of(apiError('ADMIN_RESET_CODIGO_INVALIDO', 'Código inválido, expirado ou já utilizado.')),
      );
      component.codigo = '123456';
      component.novaSenha = 'senha-fake-123';
      component.confirmarNovaSenha = 'senha-fake-123';

      component.onSubmit();
      fixture.detectChanges();

      expect(fixture.nativeElement.querySelector('a[routerlink="/minha-conta"]')).toBeNull();
    });
  });
});

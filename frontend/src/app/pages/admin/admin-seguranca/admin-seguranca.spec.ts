import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { AdminSeguranca } from './admin-seguranca';
import { Auth } from '../../../services/auth/auth';
import { UsuarioMe } from '../../../services/auth/auth.model';
import { apiError, apiSuccess } from '../../../services/api-envelope';

function usuarioFake(overrides: Partial<UsuarioMe> = {}): UsuarioMe {
  return {
    id: 'usuario-1',
    apelido: 'apelido-fake',
    email: 'fake@example.org',
    role: 'ADMIN',
    deveTrocarSenha: false,
    criadoEm: '2026-01-01T00:00:00.000Z',
    mfaAtivo: false,
    ...overrides,
  };
}

describe('AdminSeguranca', () => {
  let component: AdminSeguranca;
  let fixture: ComponentFixture<AdminSeguranca>;
  let authSpy: Record<string, ReturnType<typeof vi.fn>>;

  function configurar(usuario: UsuarioMe): void {
    authSpy = {
      buscarUsuarioLogado: vi.fn().mockReturnValue(of(apiSuccess(usuario))),
      enrollMfa: vi.fn().mockReturnValue(of(apiSuccess({ otpauthUri: 'otpauth://totp/JA:apelido-fake?secret=ABC' }))),
      confirmarMfa: vi.fn().mockReturnValue(of(apiSuccess({ mensagem: 'MFA confirmado com sucesso.' }))),
      desativarMfa: vi.fn().mockReturnValue(of(apiSuccess({ mensagem: 'MFA desativado com sucesso.' }))),
    };
  }

  async function criarComponente(): Promise<void> {
    await TestBed.configureTestingModule({
      imports: [AdminSeguranca],
      providers: [{ provide: Auth, useValue: authSpy }],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminSeguranca);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  describe('estado inicial', () => {
    beforeEach(async () => {
      configurar(usuarioFake({ mfaAtivo: false }));
      await criarComponente();
    });

    it('deve criar', () => {
      expect(component).toBeTruthy();
    });

    it('carrega o estado do MFA a partir do usuário logado', () => {
      expect(authSpy['buscarUsuarioLogado']).toHaveBeenCalled();
      expect(component.mfaAtivo).toBe(false);
      expect(component.carregandoEstadoInicial).toBe(false);
    });
  });

  describe('quando o MFA já está ativo', () => {
    beforeEach(async () => {
      configurar(usuarioFake({ mfaAtivo: true }));
      await criarComponente();
    });

    it('reflete mfaAtivo verdadeiro', () => {
      expect(component.mfaAtivo).toBe(true);
    });
  });

  describe('quando buscarUsuarioLogado falha', () => {
    beforeEach(async () => {
      authSpy = {
        buscarUsuarioLogado: vi.fn().mockReturnValue(of(apiError('UNAUTHENTICATED', 'Sessão expirada. Faça login novamente.'))),
        enrollMfa: vi.fn(),
        confirmarMfa: vi.fn(),
        desativarMfa: vi.fn(),
      };
      await criarComponente();
    });

    it('encerra o carregamento sem quebrar mesmo sem dados', () => {
      expect(component.carregandoEstadoInicial).toBe(false);
      expect(component.mfaAtivo).toBe(false);
    });
  });

  describe('enrollment de MFA', () => {
    beforeEach(async () => {
      configurar(usuarioFake({ mfaAtivo: false }));
      await criarComponente();
    });

    it('inicia o enrollment com sucesso e preenche o otpauthUri', () => {
      component.iniciarEnrollment();

      expect(authSpy['enrollMfa']).toHaveBeenCalled();
      expect(component.otpauthUri).toBe('otpauth://totp/JA:apelido-fake?secret=ABC');
      expect(component.enrollCarregando).toBe(false);
      expect(component.enrollErro).toBe('');
    });

    it('mostra erro quando o enrollment falha', () => {
      authSpy['enrollMfa'].mockReturnValue(of(apiError('UPSTREAM_ERROR', 'Não foi possível iniciar a configuração do MFA.')));

      component.iniciarEnrollment();

      expect(component.enrollErro).toBe('Não foi possível iniciar a configuração do MFA.');
      expect(component.otpauthUri).toBe('');
    });

    it('cancela o enrollment limpando uri e código digitado', () => {
      component.iniciarEnrollment();
      component.confirmarCodigoTotp = '123456';

      component.cancelarEnrollment();

      expect(component.otpauthUri).toBe('');
      expect(component.confirmarCodigoTotp).toBe('');
      expect(component.confirmarErro).toBe('');
    });

    it('confirma o enrollment com código válido e ativa o MFA', () => {
      component.iniciarEnrollment();
      component.confirmarCodigoTotp = '123456';

      component.confirmarEnrollment();

      expect(authSpy['confirmarMfa']).toHaveBeenCalledWith({ codigoTotp: '123456' });
      expect(component.mfaAtivo).toBe(true);
      expect(component.otpauthUri).toBe('');
      expect(component.confirmarCodigoTotp).toBe('');
    });

    it('mostra erro e mantém o formulário quando o código de confirmação é inválido', () => {
      authSpy['confirmarMfa'].mockReturnValue(of(apiError('VALIDATION_ERROR', 'Código inválido. Tente novamente.')));
      component.iniciarEnrollment();
      component.confirmarCodigoTotp = '000000';

      component.confirmarEnrollment();

      expect(component.confirmarErro).toBe('Código inválido. Tente novamente.');
      expect(component.mfaAtivo).toBe(false);
    });
  });

  describe('desativação de MFA', () => {
    beforeEach(async () => {
      configurar(usuarioFake({ mfaAtivo: true }));
      await criarComponente();
    });

    it('desativa o MFA com senha correta', () => {
      component.desativarSenha = 'senha-correta';

      component.desativarMfa();

      expect(authSpy['desativarMfa']).toHaveBeenCalledWith({ senha: 'senha-correta', codigoTotp: '' });
      expect(component.mfaAtivo).toBe(false);
      expect(component.desativarSenha).toBe('');
      expect(component.desativarMensagem).toBe('MFA desativado com sucesso.');
    });

    it('mostra erro e mantém o MFA ativo quando a senha está incorreta', () => {
      authSpy['desativarMfa'].mockReturnValue(of(apiError('UNAUTHENTICATED', 'Senha atual incorreta.')));
      component.desativarSenha = 'senha-errada';

      component.desativarMfa();

      expect(component.desativarErro).toBe('Senha atual incorreta.');
      expect(component.mfaAtivo).toBe(true);
    });
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { signal } from '@angular/core';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { MinhaConta } from './minha-conta';
import { Auth } from '../../services/auth/auth';
import { UsuarioMe, UsuarioResumo } from '../../services/auth/auth.model';
import { apiError, apiSuccess } from '../../services/api-envelope';
import { BibliotecaPessoalService } from '../../services/biblioteca-pessoal/biblioteca-pessoal';
import { Arquivo as ArquivoService } from '../../services/arquivo/arquivo';

function usuarioResumoFake(overrides: Partial<UsuarioResumo> = {}): UsuarioResumo {
  return {
    id: 'usuario-1',
    apelido: 'apelido-fake',
    role: 'PARTICIPANTE',
    deveTrocarSenha: false,
    ...overrides,
  };
}

function usuarioMeFake(overrides: Partial<UsuarioMe> = {}): UsuarioMe {
  return {
    id: 'usuario-1',
    apelido: 'apelido-fake',
    email: 'email-antigo@example.org',
    role: 'PARTICIPANTE',
    deveTrocarSenha: false,
    criadoEm: '2026-01-01T00:00:00.000Z',
    mfaAtivo: false,
    emailConfirmado: true,
    ...overrides,
  };
}

describe('MinhaConta', () => {
  let component: MinhaConta;
  let fixture: ComponentFixture<MinhaConta>;
  let authSpy: Record<string, ReturnType<typeof vi.fn> | unknown>;

  function configurarAuth(usuarioMe: UsuarioMe): void {
    const usuarioAtualSignal = signal<UsuarioResumo | null>(usuarioResumoFake({
      id: usuarioMe.id,
      apelido: usuarioMe.apelido,
      role: usuarioMe.role,
      deveTrocarSenha: usuarioMe.deveTrocarSenha,
    }));
    const usuarioAtualDetalhadoSignal = signal<UsuarioMe | null>(usuarioMe);

    authSpy = {
      usuarioAtual: usuarioAtualSignal,
      usuarioAtualDetalhado: usuarioAtualDetalhadoSignal,
      buscarUsuarioLogado: vi.fn().mockReturnValue(of(apiSuccess(usuarioMe))),
      atualizarConta: vi.fn().mockReturnValue(of(apiSuccess(usuarioMe))),
      alterarSenha: vi.fn().mockReturnValue(of(apiSuccess({ mensagem: 'Senha alterada com sucesso.' }))),
      solicitarTrocaEmail: vi.fn().mockReturnValue(
        of(apiSuccess({ mensagem: 'Enviamos um link de confirmação para o seu e-mail atual. Clique nele para concluir a troca.' })),
      ),
      logout: vi.fn(),
    };
  }

  async function criarComponente(): Promise<void> {
    const bibliotecaPessoalSpy = { listarMinhaBiblioteca: vi.fn().mockReturnValue(of(apiSuccess([]))) };
    const arquivoServiceSpy = { baixarArquivo: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [MinhaConta],
      providers: [
        provideRouter([]),
        { provide: Auth, useValue: authSpy },
        { provide: BibliotecaPessoalService, useValue: bibliotecaPessoalSpy },
        { provide: ArquivoService, useValue: arquivoServiceSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MinhaConta);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  describe('troca de e-mail', () => {
    beforeEach(async () => {
      configurarAuth(usuarioMeFake({ email: 'email-antigo@example.org' }));
      await criarComponente();
      component.abaAtiva = 'detalhes';
    });

    it('envia a solicitação com o payload correto', () => {
      component.novoEmailDetalhes = 'novo-email@example.org';
      component.senhaAtualParaEmail = 'senha-correta';

      component.onSolicitarTrocaEmail();

      expect(authSpy['solicitarTrocaEmail']).toHaveBeenCalledWith({
        senhaAtual: 'senha-correta',
        novoEmail: 'novo-email@example.org',
      });
    });

    it('mostra mensagem de confirmação pendente e não altera o e-mail exibido', () => {
      component.emailDetalhes = 'email-antigo@example.org';
      component.novoEmailDetalhes = 'novo-email@example.org';
      component.senhaAtualParaEmail = 'senha-correta';

      component.onSolicitarTrocaEmail();

      expect(component.emailSucesso).toBe(true);
      expect(component.emailMensagem).toBe(
        'Enviamos um link de confirmação para o seu e-mail atual. Clique nele para concluir a troca.',
      );
      expect(component.emailDetalhes).toBe('email-antigo@example.org');
      expect(component.emailEnviando).toBe(false);
    });

    it('limpa os campos do formulário após sucesso', () => {
      component.novoEmailDetalhes = 'novo-email@example.org';
      component.senhaAtualParaEmail = 'senha-correta';

      component.onSolicitarTrocaEmail();

      expect(component.novoEmailDetalhes).toBe('');
      expect(component.senhaAtualParaEmail).toBe('');
    });

    it('mostra erro quando a senha atual está incorreta e preserva o e-mail digitado', () => {
      (authSpy['solicitarTrocaEmail'] as ReturnType<typeof vi.fn>).mockReturnValue(
        of(apiError('USUARIO_SENHA_ATUAL_INCORRETA', 'Senha atual incorreta.')),
      );
      component.novoEmailDetalhes = 'novo-email@example.org';
      component.senhaAtualParaEmail = 'senha-errada';

      component.onSolicitarTrocaEmail();

      expect(component.emailSucesso).toBe(false);
      expect(component.emailMensagem).toBe('Senha atual incorreta.');
      expect(component.novoEmailDetalhes).toBe('novo-email@example.org');
      expect(component.senhaAtualParaEmail).toBe('senha-errada');
    });

    it('mostra erro quando o e-mail já está em uso por outra conta', () => {
      (authSpy['solicitarTrocaEmail'] as ReturnType<typeof vi.fn>).mockReturnValue(
        of(apiError('USUARIO_DADOS_EM_USO', 'Não foi possível salvar os dados informados')),
      );
      component.novoEmailDetalhes = 'ja-cadastrado@example.org';
      component.senhaAtualParaEmail = 'senha-correta';

      component.onSolicitarTrocaEmail();

      expect(component.emailSucesso).toBe(false);
      expect(component.emailMensagem).toBe('Não foi possível salvar os dados informados');
    });

    it('mostra erro de validação quando o e-mail é igual ao atual ou tem formato inválido', () => {
      (authSpy['solicitarTrocaEmail'] as ReturnType<typeof vi.fn>).mockReturnValue(
        of(apiError('VALIDATION_ERROR', 'O novo e-mail deve ser diferente do e-mail atual.')),
      );
      component.novoEmailDetalhes = 'email-antigo@example.org';
      component.senhaAtualParaEmail = 'senha-correta';

      component.onSolicitarTrocaEmail();

      expect(component.emailSucesso).toBe(false);
      expect(component.emailMensagem).toBe('O novo e-mail deve ser diferente do e-mail atual.');
    });

    it('usa mensagem padrão quando o erro não traz mensagem', () => {
      (authSpy['solicitarTrocaEmail'] as ReturnType<typeof vi.fn>).mockReturnValue(
        of({ success: false, data: null, error: null }),
      );
      component.novoEmailDetalhes = 'novo-email@example.org';
      component.senhaAtualParaEmail = 'senha-correta';

      component.onSolicitarTrocaEmail();

      expect(component.emailMensagem).toBe('Não foi possível solicitar a troca de e-mail.');
    });
  });

  describe('formulário de detalhes/apelido', () => {
    beforeEach(async () => {
      configurarAuth(usuarioMeFake({ apelido: 'apelido-fake', email: 'email-antigo@example.org' }));
      await criarComponente();
      component.abaAtiva = 'detalhes';
    });

    it('salva apenas o apelido, sem enviar e-mail no payload', () => {
      component.nomeExibicao = 'novo-apelido';

      component.onSalvarDetalhes();

      expect(authSpy['atualizarConta']).toHaveBeenCalledWith({ apelido: 'novo-apelido' });
      expect((authSpy['atualizarConta'] as ReturnType<typeof vi.fn>).mock.calls[0][0]).not.toHaveProperty('email');
    });

    it('mostra sucesso ao salvar apelido sem trocar senha', () => {
      component.nomeExibicao = 'novo-apelido';

      component.onSalvarDetalhes();

      expect(component.detalhesSucesso).toBe(true);
      expect(component.detalhesMensagem).toBe('Dados da conta atualizados com sucesso.');
      expect(authSpy['alterarSenha']).not.toHaveBeenCalled();
    });

    it('mostra erro quando atualizarConta falha e não tenta trocar senha', () => {
      (authSpy['atualizarConta'] as ReturnType<typeof vi.fn>).mockReturnValue(
        of(apiError('VALIDATION_ERROR', 'Apelido inválido.')),
      );
      component.nomeExibicao = '';

      component.onSalvarDetalhes();

      expect(component.detalhesSucesso).toBe(false);
      expect(component.detalhesMensagem).toBe('Apelido inválido.');
      expect(authSpy['alterarSenha']).not.toHaveBeenCalled();
    });
  });

  describe('carregamento inicial dos detalhes', () => {
    it('preenche o e-mail exibido a partir do usuário detalhado em cache', async () => {
      configurarAuth(usuarioMeFake({ email: 'do-cache@example.org' }));
      await criarComponente();

      expect(component.emailDetalhes).toBe('do-cache@example.org');
      expect(authSpy['buscarUsuarioLogado']).not.toHaveBeenCalled();
    });
  });
});

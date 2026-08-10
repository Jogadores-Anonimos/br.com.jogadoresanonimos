import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { AppComponent } from './app';
import { Auth } from './services/auth/auth';
import { UsuarioMe, UsuarioResumo } from './services/auth/auth.model';

function usuarioResumoFake(overrides: Partial<UsuarioResumo> = {}): UsuarioResumo {
  return {
    id: 'usuario-1',
    apelido: 'apelido-fake',
    role: 'PARTICIPANTE',
    deveTrocarSenha: false,
    ...overrides,
  };
}

function usuarioDetalhadoFake(overrides: Partial<UsuarioMe> = {}): UsuarioMe {
  return {
    id: 'usuario-1',
    apelido: 'apelido-fake',
    email: 'fake@example.org',
    role: 'PARTICIPANTE',
    deveTrocarSenha: false,
    criadoEm: '2026-01-01T00:00:00.000Z',
    mfaAtivo: false,
    emailConfirmado: false,
    ...overrides,
  };
}

describe('App', () => {
  let authStub: Record<string, unknown>;

  function configurarAuth(usuario: UsuarioResumo | null, usuarioDetalhado: UsuarioMe | null): void {
    authStub = {
      usuarioAtual: () => usuario,
      usuarioAtualDetalhado: () => usuarioDetalhado,
    };
  }

  async function criarComponente(): Promise<ComponentFixture<AppComponent>> {
    await TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [provideRouter([]), { provide: Auth, useValue: authStub }],
    }).compileComponents();

    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();
    return fixture;
  }

  it('should create the app', async () => {
    configurarAuth(null, null);
    const fixture = await criarComponente();
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('esconde o aviso de e-mail nao confirmado quando deslogado', async () => {
    configurarAuth(null, null);
    const fixture = await criarComponente();
    const aviso = fixture.nativeElement.querySelector('app-aviso-email-nao-confirmado');
    expect(aviso).toBeNull();
  });

  it('esconde o aviso de e-mail nao confirmado quando logado e confirmado', async () => {
    configurarAuth(usuarioResumoFake(), usuarioDetalhadoFake({ emailConfirmado: true }));
    const fixture = await criarComponente();
    const aviso = fixture.nativeElement.querySelector('app-aviso-email-nao-confirmado');
    expect(aviso).toBeNull();
  });

  it('mostra o aviso de e-mail nao confirmado quando logado e nao confirmado', async () => {
    configurarAuth(usuarioResumoFake(), usuarioDetalhadoFake({ emailConfirmado: false }));
    const fixture = await criarComponente();
    const aviso = fixture.nativeElement.querySelector('app-aviso-email-nao-confirmado');
    expect(aviso).not.toBeNull();
  });
});

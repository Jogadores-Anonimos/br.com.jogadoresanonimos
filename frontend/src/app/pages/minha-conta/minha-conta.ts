import {
  ApplicationRef,
  Component,
  ElementRef,
  OnDestroy,
  ViewChild,
  effect,
  inject,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { of, timeout } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';

import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { Auth } from '../../services/auth/auth';
import { LoginMfaRequest, LoginResponse, UsuarioResponse } from '../../services/auth/auth.model';
import { apiError, mapearErrosCampo } from '../../services/api-envelope';
import { Arquivo as ArquivoService } from '../../services/arquivo/arquivo';
import { ArquivoResponse } from '../../services/arquivo/arquivo.model';
import { disparaDownloadDeBlob } from '../../services/arquivo/download-blob.util';
import { BibliotecaPessoalService } from '../../services/biblioteca-pessoal/biblioteca-pessoal';
import { RecaptchaLoader } from '../../services/recaptcha-loader';
import { environment } from '../../../environments/environment';

declare const grecaptcha: any;

type AbaConta = 'dashboard' | 'pedidos' | 'downloads' | 'enderecos' | 'detalhes';

interface PedidoConta {
  numero: string;
  data: string;
  item: string;
  status: 'Processando' | 'Concluído';
  total: string;
}

@Component({
  selector: 'app-minha-conta',
  standalone: true,
  imports: [FormsModule, RouterLink, ResourceLayout],
  templateUrl: './minha-conta.html',
  styleUrl: './minha-conta.css',
})
export class MinhaConta implements OnDestroy {
  readonly bannerSrc = '/images/home/reuniao-discussao.jpg';

  private readonly appRef = inject(ApplicationRef);
  private readonly recaptchaLoader = inject(RecaptchaLoader);
  private readonly bibliotecaPessoal = inject(BibliotecaPessoalService);
  private readonly arquivoService = inject(ArquivoService);
  private readonly router = inject(Router);

  constructor(
    public readonly auth: Auth,
  ) {
    effect(() => {
      const usuario = this.auth.usuarioAtual();
      if (usuario) {
        this.trocaSenhaObrigatoria = usuario.deveTrocarSenha;
        if (usuario.deveTrocarSenha) {
          this.abaAtiva = 'detalhes';
          this.nomeExibicao = usuario.apelido;
          this.carregarDetalhesConta();
          return;
        }
        if (usuario.role === 'ADMIN') {
          this.router.navigateByUrl('/admin');
          return;
        }
        this.nomeExibicao = usuario.apelido;
        this.carregarDownloads();
        this.carregarDetalhesConta();
      } else {
        this.downloads = [];
      }
    });
  }

  private carregarDetalhesConta(): void {
    this.auth.buscarUsuarioLogado().subscribe((resposta) => {
      if (resposta.success && resposta.data) {
        this.nomeExibicao = resposta.data.apelido;
        this.emailDetalhes = resposta.data.email;
      }
    });
  }

  private carregarDownloads(): void {
    this.downloadsCarregando = true;
    this.bibliotecaPessoal.listarMinhaBiblioteca().subscribe((resposta) => {
      this.downloadsCarregando = false;
      if (resposta.success && resposta.data) {
        this.downloads = resposta.data;
      }
    });
  }

  removerDownload(arquivoId: string): void {
    if (this.downloadsRemovendo.has(arquivoId)) {
      return;
    }
    this.downloadsRemovendo.add(arquivoId);
    this.bibliotecaPessoal.removerDaBiblioteca(arquivoId).subscribe((resposta) => {
      this.downloadsRemovendo.delete(arquivoId);
      if (resposta.success) {
        this.downloads = this.downloads.filter((arquivo) => arquivo.id !== arquivoId);
      }
    });
  }

  loginUsuarioOuEmail = '';
  loginSenha = '';
  lembrarDeMim = false;
  loginEnviando = false;
  loginMensagem = '';
  loginSucesso = false;
  captchaLoginOk = false;
  private captchaLoginToken = '';

  loginEtapaMfa = false;
  loginCodigoTotp = '';

  registroApelido = '';
  registroEmail = '';
  registroSenha = '';
  registroConfirmarSenha = '';
  registroEnviando = false;
  registroMensagem = '';
  registroSucesso = false;
  captchaRegistroOk = false;
  registroErrosCampo: Record<string, string> = {};
  private captchaRegistroToken = '';

  private static readonly TIMEOUT_SUBMIT_MS = 5_000;

  onLoginSubmit(): void {
    this.loginEnviando = true;
    this.loginMensagem = '';

    this.auth
      .login({
        identificador: this.loginUsuarioOuEmail,
        senha: this.loginSenha,
        recaptchaToken: this.captchaLoginToken,
        lembrarDeMim: this.lembrarDeMim,
      })
      .pipe(
        timeout(MinhaConta.TIMEOUT_SUBMIT_MS),
        catchError(() =>
          of(apiError<LoginResponse>('TIMEOUT', 'A requisição demorou demais. Tente novamente.')),
        ),
        finalize(() => {
          this.loginEnviando = false;
          if (!this.loginEtapaMfa) {
            this.resetarCaptcha('login');
          }
        }),
      )
      .subscribe((resposta) => {
        if (resposta.success && resposta.data?.mfaRequerido) {
          this.loginEtapaMfa = true;
          this.loginSucesso = false;
          this.loginMensagem = '';
          return;
        }
        this.loginSucesso = resposta.success;
        this.loginMensagem = resposta.success
          ? ''
          : (resposta.error?.message ?? 'Não foi possível entrar. Tente novamente.');
      });
  }

  onLoginMfaSubmit(): void {
    this.loginEnviando = true;
    this.loginMensagem = '';

    const req: LoginMfaRequest = {
      identificador: this.loginUsuarioOuEmail,
      senha: this.loginSenha,
      recaptchaToken: this.captchaLoginToken,
      codigoTotp: this.loginCodigoTotp,
    };

    this.auth
      .loginComMfa(req, this.lembrarDeMim)
      .pipe(
        timeout(MinhaConta.TIMEOUT_SUBMIT_MS),
        catchError(() =>
          of(apiError<LoginResponse>('TIMEOUT', 'A requisição demorou demais. Tente novamente.')),
        ),
        finalize(() => {
          this.loginEnviando = false;
        }),
      )
      .subscribe((resposta) => {
        this.loginSucesso = resposta.success;
        this.loginMensagem = resposta.success
          ? ''
          : (resposta.error?.message ?? 'Código inválido. Tente novamente.');
        if (!resposta.success) {
          this.loginCodigoTotp = '';
        }
      });
  }

  cancelarLoginMfa(): void {
    this.loginEtapaMfa = false;
    this.loginCodigoTotp = '';
    this.loginMensagem = '';
    this.resetarCaptcha('login');
  }

  onRegistroSubmit(): void {
    this.registroEnviando = true;
    this.registroMensagem = '';
    this.registroErrosCampo = {};

    this.auth
      .registrar({
        apelido: this.registroApelido,
        email: this.registroEmail,
        senha: this.registroSenha,
        confirmarSenha: this.registroConfirmarSenha,
        recaptchaToken: this.captchaRegistroToken,
      })
      .pipe(
        timeout(MinhaConta.TIMEOUT_SUBMIT_MS),
        catchError(() =>
          of(apiError<UsuarioResponse>('TIMEOUT', 'A requisição demorou demais. Tente novamente.')),
        ),
        finalize(() => {
          this.registroEnviando = false;
          this.resetarCaptcha('registro');
        }),
      )
      .subscribe((resposta) => {
        this.registroSucesso = resposta.success;
        this.registroErrosCampo = mapearErrosCampo(resposta.error?.fields);
        if (resposta.success) {
          this.registroMensagem = 'Conta criada com sucesso! Faça login abaixo para continuar.';
          this.loginUsuarioOuEmail = this.registroApelido;
          this.registroApelido = '';
          this.registroEmail = '';
          this.registroSenha = '';
          this.registroConfirmarSenha = '';
        } else {
          this.registroMensagem = resposta.error?.message ?? 'Não foi possível criar a conta. Tente novamente.';
        }
      });
  }

  private captchaLoginWidgetId: number | null = null;
  private captchaRegistroWidgetId: number | null = null;
  private captchaLoginTimeoutId: ReturnType<typeof setTimeout> | null = null;
  private captchaRegistroTimeoutId: ReturnType<typeof setTimeout> | null = null;

  @ViewChild('captchaLogin')
  set captchaLoginRef(ref: ElementRef<HTMLDivElement> | undefined) {
    if (ref) {
      this.carregarERenderizarRecaptcha(ref.nativeElement, 'login');
    } else {
      this.captchaLoginWidgetId = null;
      this.captchaLoginOk = false;
    }
  }

  @ViewChild('captchaRegistro')
  set captchaRegistroRef(ref: ElementRef<HTMLDivElement> | undefined) {
    if (ref) {
      this.carregarERenderizarRecaptcha(ref.nativeElement, 'registro');
    } else {
      this.captchaRegistroWidgetId = null;
      this.captchaRegistroOk = false;
    }
  }

  private carregarERenderizarRecaptcha(
    container: HTMLDivElement,
    formulario: 'login' | 'registro',
  ): void {
    this.recaptchaLoader
      .carregar()
      .then(() => this.renderizarRecaptcha(container, formulario))
      .catch(() => {
        this.definirCaptchaToken(formulario, '');
      });
  }

  private renderizarRecaptcha(
    container: HTMLDivElement,
    formulario: 'login' | 'registro',
    tentativa = 0,
  ): void {
    if (typeof grecaptcha === 'undefined' || !grecaptcha.render) {
      if (tentativa > 40) {
        return;
      }
      const timeoutId = setTimeout(
        () => this.renderizarRecaptcha(container, formulario, tentativa + 1),
        250,
      );
      if (formulario === 'login') {
        this.captchaLoginTimeoutId = timeoutId;
      } else {
        this.captchaRegistroTimeoutId = timeoutId;
      }
      return;
    }

    const widgetId = grecaptcha.render(container, {
      sitekey: environment.recaptchaSiteKey,
      callback: (token: string) => {
        this.definirCaptchaToken(formulario, token);
      },
      'expired-callback': () => {
        this.definirCaptchaToken(formulario, '');
      },
      'error-callback': () => {
        this.definirCaptchaToken(formulario, '');
      },
    });

    if (formulario === 'login') {
      this.captchaLoginWidgetId = widgetId;
    } else {
      this.captchaRegistroWidgetId = widgetId;
    }
  }

  private definirCaptchaToken(formulario: 'login' | 'registro', token: string): void {
    if (formulario === 'login') {
      this.captchaLoginToken = token;
      this.captchaLoginOk = token !== '';
    } else {
      this.captchaRegistroToken = token;
      this.captchaRegistroOk = token !== '';
    }
    this.appRef.tick();
  }

  private resetarCaptcha(formulario: 'login' | 'registro'): void {
    const widgetId = formulario === 'login' ? this.captchaLoginWidgetId : this.captchaRegistroWidgetId;
    if (widgetId !== null && typeof grecaptcha !== 'undefined' && grecaptcha.reset) {
      grecaptcha.reset(widgetId);
    }
    this.definirCaptchaToken(formulario, '');
  }

  ngOnDestroy(): void {
    if (this.captchaLoginTimeoutId !== null) {
      clearTimeout(this.captchaLoginTimeoutId);
    }
    if (this.captchaRegistroTimeoutId !== null) {
      clearTimeout(this.captchaRegistroTimeoutId);
    }
  }

  abaAtiva: AbaConta = 'dashboard';
  trocaSenhaObrigatoria = false;

  selecionarAba(aba: AbaConta): void {
    if (this.trocaSenhaObrigatoria) {
      return;
    }
    this.abaAtiva = aba;
  }

  readonly pedidos: PedidoConta[] = [];

  downloads: ArquivoResponse[] = [];
  downloadsCarregando = false;
  downloadsRemovendo = new Set<string>();
  downloadsBaixando = new Set<string>();
  erroDownloadPorId = new Map<string, string>();

  baixarPdf(arquivo: ArquivoResponse): void {
    if (this.downloadsBaixando.has(arquivo.id)) {
      return;
    }
    this.downloadsBaixando.add(arquivo.id);
    this.erroDownloadPorId.delete(arquivo.id);
    this.arquivoService.baixarArquivo(arquivo.id).subscribe({
      next: (blob) => {
        this.downloadsBaixando.delete(arquivo.id);
        disparaDownloadDeBlob(blob, arquivo.nomeOriginal);
      },
      error: () => {
        this.downloadsBaixando.delete(arquivo.id);
        this.erroDownloadPorId.set(arquivo.id, 'Não foi possível baixar o arquivo agora. Tente novamente.');
      },
    });
  }

  nomeExibicao = '';
  emailDetalhes = '';
  senhaAtualDetalhes = '';
  novaSenhaDetalhes = '';
  confirmarNovaSenhaDetalhes = '';
  detalhesMensagem = '';
  detalhesSucesso = false;
  detalhesEnviando = false;

  onSalvarDetalhes(): void {
    this.detalhesEnviando = true;
    this.detalhesMensagem = '';
    this.detalhesSucesso = false;

    if (this.trocaSenhaObrigatoria) {
      this.salvarSenhaSeInformada();
      return;
    }

    this.auth
      .atualizarConta({ apelido: this.nomeExibicao, email: this.emailDetalhes })
      .subscribe((resposta) => {
        if (!resposta.success) {
          this.detalhesEnviando = false;
          this.detalhesMensagem = resposta.error?.message ?? 'Não foi possível salvar os dados informados.';
          return;
        }

        this.salvarSenhaSeInformada();
      });
  }

  private salvarSenhaSeInformada(): void {
    const querTrocarSenha = this.senhaAtualDetalhes || this.novaSenhaDetalhes || this.confirmarNovaSenhaDetalhes;
    if (!querTrocarSenha) {
      this.detalhesEnviando = false;
      if (this.trocaSenhaObrigatoria) {
        this.detalhesSucesso = false;
        this.detalhesMensagem = 'Por segurança, defina uma nova senha antes de continuar.';
        return;
      }
      this.detalhesSucesso = true;
      this.detalhesMensagem = 'Dados da conta atualizados com sucesso.';
      return;
    }

    this.auth
      .alterarSenha({
        senhaAtual: this.senhaAtualDetalhes,
        novaSenha: this.novaSenhaDetalhes,
        confirmarNovaSenha: this.confirmarNovaSenhaDetalhes,
      })
      .subscribe((resposta) => {
        this.detalhesEnviando = false;
        this.detalhesSucesso = resposta.success;
        if (resposta.success) {
          this.senhaAtualDetalhes = '';
          this.novaSenhaDetalhes = '';
          this.confirmarNovaSenhaDetalhes = '';
          this.detalhesMensagem = this.trocaSenhaObrigatoria
            ? 'Senha alterada com sucesso. Faça login novamente com a nova senha.'
            : 'Dados salvos e senha alterada com sucesso. Faça login novamente.';
          this.auth.logout();
        } else {
          this.detalhesMensagem = this.trocaSenhaObrigatoria
            ? (resposta.error?.message ?? 'Não foi possível alterar a senha.')
            : (resposta.error?.message ?? 'Dados salvos, mas não foi possível alterar a senha.');
        }
      });
  }

  sair(): void {
    this.auth.logout();
    this.abaAtiva = 'dashboard';
  }
}

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
import { Auth } from '../../services/auth';
import { LoginResponse, UsuarioResponse } from '../../services/auth.model';
import { apiError } from '../../services/api-envelope';
import { ArquivoResponse } from '../../services/arquivo.model';
import { BibliotecaPessoalService } from '../../services/biblioteca-pessoal';
import { RecaptchaLoader } from '../../services/recaptcha-loader';

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

  private readonly RECAPTCHA_SITE_KEY_TESTE = '6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI';

  private readonly appRef = inject(ApplicationRef);
  private readonly recaptchaLoader = inject(RecaptchaLoader);
  private readonly bibliotecaPessoal = inject(BibliotecaPessoalService);
  private readonly router = inject(Router);

  constructor(
    public readonly auth: Auth,
  ) {
    effect(() => {
      const usuario = this.auth.usuarioAtual();
      if (usuario) {
        // Admin não usa a área de cliente — cai direto no painel.
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

  // nomes de campo mantidos para não quebrar o template (minha-conta.html é
  // domínio do agente frontend); "registroApelido" foi adicionado pois o
  // contrato real de registro passou a exigir apelido — ver ux-ui/frontend
  // para incluir o campo correspondente no formulário.
  loginUsuarioOuEmail = '';
  loginSenha = '';
  lembrarDeMim = false;
  loginEnviando = false;
  loginMensagem = '';
  loginSucesso = false;
  captchaLoginOk = false;

  registroApelido = '';
  registroEmail = '';
  registroSenha = '';
  registroConfirmarSenha = '';
  registroEnviando = false;
  registroMensagem = '';
  registroSucesso = false;
  captchaRegistroOk = false;
  registroErrosCampo: Record<string, string> = {};

  // Timeout de segurança: garante que o botão nunca fica preso em
  // "Entrando..."/"Criando..." indefinidamente, mesmo se algo travar a
  // requisição ou o observable nunca completar por um bug futuro.
  private static readonly TIMEOUT_SUBMIT_MS = 5_000;

  onLoginSubmit(): void {
    this.loginEnviando = true;
    this.loginMensagem = '';

    this.auth
      .login({ identificador: this.loginUsuarioOuEmail, senha: this.loginSenha })
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
          : (resposta.error?.message ?? 'Não foi possível entrar. Tente novamente.');
      });
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
      })
      .pipe(
        timeout(MinhaConta.TIMEOUT_SUBMIT_MS),
        catchError(() =>
          of(apiError<UsuarioResponse>('TIMEOUT', 'A requisição demorou demais. Tente novamente.')),
        ),
        finalize(() => {
          this.registroEnviando = false;
        }),
      )
      .subscribe((resposta) => {
        this.registroSucesso = resposta.success;
        this.registroErrosCampo = resposta.error?.fields ?? {};
        this.registroMensagem = resposta.success
          ? ''
          : (resposta.error?.message ?? 'Não foi possível criar a conta. Tente novamente.');
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
        this.definirCaptchaOk(formulario, false);
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
      sitekey: this.RECAPTCHA_SITE_KEY_TESTE,
      callback: () => {
        this.definirCaptchaOk(formulario, true);
      },
      'expired-callback': () => {
        this.definirCaptchaOk(formulario, false);
      },
      'error-callback': () => {
        this.definirCaptchaOk(formulario, false);
      },
    });

    if (formulario === 'login') {
      this.captchaLoginWidgetId = widgetId;
    } else {
      this.captchaRegistroWidgetId = widgetId;
    }
  }

  private definirCaptchaOk(formulario: 'login' | 'registro', ok: boolean): void {
    if (formulario === 'login') {
      this.captchaLoginOk = ok;
    } else {
      this.captchaRegistroOk = ok;
    }
    this.appRef.tick();
  }

  // evita retries do reCAPTCHA contra um container já removido do DOM
  ngOnDestroy(): void {
    if (this.captchaLoginTimeoutId !== null) {
      clearTimeout(this.captchaLoginTimeoutId);
    }
    if (this.captchaRegistroTimeoutId !== null) {
      clearTimeout(this.captchaRegistroTimeoutId);
    }
  }

  abaAtiva: AbaConta = 'dashboard';

  selecionarAba(aba: AbaConta): void {
    this.abaAtiva = aba;
  }

  // Loja em standby — sem pedido real ainda, lista vazia até reativar.
  readonly pedidos: PedidoConta[] = [];

  downloads: ArquivoResponse[] = [];
  downloadsCarregando = false;
  downloadsRemovendo = new Set<string>();

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
          this.detalhesMensagem = 'Dados salvos e senha alterada com sucesso. Faça login novamente.';
          this.auth.logout();
        } else {
          this.detalhesMensagem = resposta.error?.message ?? 'Dados salvos, mas não foi possível alterar a senha.';
        }
      });
  }

  sair(): void {
    this.auth.logout();
    this.abaAtiva = 'dashboard';
  }
}

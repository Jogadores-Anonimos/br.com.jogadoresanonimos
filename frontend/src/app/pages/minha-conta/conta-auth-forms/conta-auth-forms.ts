import {
  ApplicationRef,
  Component,
  ElementRef,
  ViewChild,
  inject,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { of, timeout } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';

import { Auth } from '../../../services/auth/auth';
import { LoginMfaRequest, LoginResponse, UsuarioResponse } from '../../../services/auth/auth.model';
import { MFA_CODIGO_INVALIDO_MENSAGEM, apiError, mapearErrosCampo, mensagemDeErro } from '../../../services/api-envelope';
import { RecaptchaLoader } from '../../../services/recaptcha-loader';
import { environment } from '../../../../environments/environment';

declare const grecaptcha: any;

@Component({
  selector: 'app-conta-auth-forms',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './conta-auth-forms.html',
  styleUrl: './conta-auth-forms.css',
})
export class ContaAuthForms {
  private readonly appRef = inject(ApplicationRef);
  private readonly recaptchaLoader = inject(RecaptchaLoader);
  private readonly auth = inject(Auth);

  private static readonly TIMEOUT_SUBMIT_MS = 60_000;

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
  captchaMfaOk = false;
  private captchaMfaToken = '';

  registroApelido = '';
  registroEmail = '';
  registroSenha = '';
  registroConfirmarSenha = '';
  registroEnviando = false;
  registroMensagem = '';
  registroSucesso = false;
  captchaRegistroOk = false;
  registroErrosCampo: Record<string, string> = {};
  registroConcluido = false;
  registroApelidoConcluido = '';
  private captchaRegistroToken = '';

  @ViewChild('loginSenhaField')
  set loginSenhaFieldRef(ref: ElementRef<HTMLInputElement> | undefined) {
    if (ref) {
      ref.nativeElement.focus();
    }
  }

  @ViewChild('loginMensagemEl')
  set loginMensagemElRef(ref: ElementRef<HTMLElement> | undefined) {
    this.rolarAteMensagem(ref);
  }

  @ViewChild('loginMfaMensagemEl')
  set loginMfaMensagemElRef(ref: ElementRef<HTMLElement> | undefined) {
    this.rolarAteMensagem(ref);
  }

  @ViewChild('registroMensagemEl')
  set registroMensagemElRef(ref: ElementRef<HTMLElement> | undefined) {
    this.rolarAteMensagem(ref);
  }

  @ViewChild('registroConfirmacaoEl')
  set registroConfirmacaoElRef(ref: ElementRef<HTMLElement> | undefined) {
    this.rolarAteMensagem(ref);
  }

  private rolarAteMensagem(ref: ElementRef<HTMLElement> | undefined): void {
    if (!ref) {
      return;
    }
    ref.nativeElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
  }

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
        timeout(ContaAuthForms.TIMEOUT_SUBMIT_MS),
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
        this.loginMensagem = resposta.success ? '' : mensagemDeErro(resposta.error, 'Não foi possível entrar. Tente novamente.');
      });
  }

  onLoginMfaSubmit(): void {
    this.loginEnviando = true;
    this.loginMensagem = '';

    const req: LoginMfaRequest = {
      identificador: this.loginUsuarioOuEmail,
      senha: this.loginSenha,
      recaptchaToken: this.captchaMfaToken,
      codigoTotp: this.loginCodigoTotp,
    };

    this.auth
      .loginComMfa(req, this.lembrarDeMim)
      .pipe(
        timeout(ContaAuthForms.TIMEOUT_SUBMIT_MS),
        catchError(() =>
          of(apiError<LoginResponse>('TIMEOUT', 'A requisição demorou demais. Tente novamente.')),
        ),
        finalize(() => {
          this.loginEnviando = false;
          this.resetarCaptcha('login-mfa');
        }),
      )
      .subscribe((resposta) => {
        this.loginSucesso = resposta.success;
        this.loginMensagem = resposta.success ? '' : mensagemDeErro(resposta.error, MFA_CODIGO_INVALIDO_MENSAGEM);
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
    this.resetarCaptcha('login-mfa');
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
        timeout(ContaAuthForms.TIMEOUT_SUBMIT_MS),
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
          this.registroApelidoConcluido = this.registroApelido;
          this.loginUsuarioOuEmail = this.registroApelido;
          this.registroApelido = '';
          this.registroEmail = '';
          this.registroSenha = '';
          this.registroConfirmarSenha = '';
          this.registroConcluido = true;
        } else {
          this.registroMensagem = mensagemDeErro(resposta.error, 'Não foi possível criar a conta. Tente novamente.');
        }
      });
  }

  private captchaLoginWidgetId: number | null = null;
  private captchaRegistroWidgetId: number | null = null;
  private captchaMfaWidgetId: number | null = null;

  @ViewChild('captchaLogin')
  set captchaLoginRef(ref: ElementRef<HTMLDivElement> | undefined) {
    if (ref) {
      this.carregarERenderizarRecaptcha(ref.nativeElement, 'login');
    } else {
      this.captchaLoginWidgetId = null;
      this.captchaLoginOk = false;
      this.captchaLoginToken = '';
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

  @ViewChild('captchaMfa')
  set captchaMfaRef(ref: ElementRef<HTMLDivElement> | undefined) {
    if (ref) {
      this.carregarERenderizarRecaptcha(ref.nativeElement, 'login-mfa');
    } else {
      this.captchaMfaWidgetId = null;
      this.captchaMfaOk = false;
      this.captchaMfaToken = '';
    }
  }

  private carregarERenderizarRecaptcha(
    container: HTMLDivElement,
    formulario: 'login' | 'registro' | 'login-mfa',
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
    formulario: 'login' | 'registro' | 'login-mfa',
  ): void {
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
    } else if (formulario === 'registro') {
      this.captchaRegistroWidgetId = widgetId;
    } else {
      this.captchaMfaWidgetId = widgetId;
    }
  }

  private definirCaptchaToken(formulario: 'login' | 'registro' | 'login-mfa', token: string): void {
    if (formulario === 'login') {
      this.captchaLoginToken = token;
      this.captchaLoginOk = token !== '';
    } else if (formulario === 'registro') {
      this.captchaRegistroToken = token;
      this.captchaRegistroOk = token !== '';
    } else {
      this.captchaMfaToken = token;
      this.captchaMfaOk = token !== '';
    }
    this.appRef.tick();
  }

  private resetarCaptcha(formulario: 'login' | 'registro' | 'login-mfa'): void {
    const widgetId =
      formulario === 'login'
        ? this.captchaLoginWidgetId
        : formulario === 'registro'
          ? this.captchaRegistroWidgetId
          : this.captchaMfaWidgetId;
    if (widgetId !== null && typeof grecaptcha !== 'undefined' && grecaptcha.reset) {
      grecaptcha.reset(widgetId);
    }
    this.definirCaptchaToken(formulario, '');
  }
}

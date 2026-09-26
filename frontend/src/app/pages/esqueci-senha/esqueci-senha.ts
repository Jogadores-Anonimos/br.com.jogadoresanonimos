import { ApplicationRef, Component, ElementRef, ViewChild, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { Auth } from '../../services/auth/auth';
import { RecaptchaLoader } from '../../services/recaptcha-loader';
import { environment } from '../../../environments/environment';

declare const grecaptcha: any;

@Component({
  selector: 'app-esqueci-senha',
  standalone: true,
  imports: [FormsModule, RouterLink, ResourceLayout],
  templateUrl: './esqueci-senha.html',
  styleUrl: './esqueci-senha.css',
})
export class EsqueciSenha {
  readonly bannerSrc = '/images/home/reuniao-discussao.webp';

  private readonly appRef = inject(ApplicationRef);
  private readonly recaptchaLoader = inject(RecaptchaLoader);
  private readonly auth = inject(Auth);

  identificador = '';
  enviando = false;
  mensagem = '';
  enviado = false;
  captchaOk = false;
  private captchaToken = '';
  private captchaWidgetId: number | null = null;

  @ViewChild('captchaEsqueciSenha')
  set captchaRef(ref: ElementRef<HTMLDivElement> | undefined) {
    if (ref) {
      this.carregarERenderizarRecaptcha(ref.nativeElement);
    } else {
      this.captchaWidgetId = null;
      this.captchaOk = false;
      this.captchaToken = '';
    }
  }

  onSubmit(): void {
    if (!this.identificador.trim()) {
      return;
    }

    this.enviando = true;
    this.mensagem = '';

    this.auth
      .esqueciSenha({ identificador: this.identificador, recaptchaToken: this.captchaToken })
      .subscribe((resposta) => {
        this.enviando = false;
        this.enviado = resposta.success;
        this.mensagem = resposta.success
          ? (resposta.data?.mensagem ?? 'Se o apelido ou e-mail existir em nossa base, enviaremos um link de redefinição.')
          : (resposta.error?.message ?? 'Não foi possível processar seu pedido. Tente novamente.');
        if (!resposta.success) {
          this.resetarCaptcha();
        }
      });
  }

  private carregarERenderizarRecaptcha(container: HTMLDivElement): void {
    this.recaptchaLoader
      .carregar()
      .then(() => this.renderizarRecaptcha(container))
      .catch(() => {
        this.definirCaptchaToken('');
      });
  }

  private renderizarRecaptcha(container: HTMLDivElement): void {
    this.captchaWidgetId = grecaptcha.render(container, {
      sitekey: environment.recaptchaSiteKey,
      callback: (token: string) => {
        this.definirCaptchaToken(token);
      },
      'expired-callback': () => {
        this.definirCaptchaToken('');
      },
      'error-callback': () => {
        this.definirCaptchaToken('');
      },
    });
  }

  private definirCaptchaToken(token: string): void {
    this.captchaToken = token;
    this.captchaOk = token !== '';
    this.appRef.tick();
  }

  private resetarCaptcha(): void {
    if (this.captchaWidgetId !== null && typeof grecaptcha !== 'undefined' && grecaptcha.reset) {
      grecaptcha.reset(this.captchaWidgetId);
    }
    this.definirCaptchaToken('');
  }
}

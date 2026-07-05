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

import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { Auth } from '../../services/auth';

declare const grecaptcha: any;

type AbaConta = 'dashboard' | 'pedidos' | 'downloads' | 'enderecos' | 'detalhes';

interface PedidoConta {
  numero: string;
  data: string;
  item: string;
  status: 'Processando' | 'Concluído';
  total: string;
}

interface DownloadConta {
  titulo: string;
  disponibilizadoEm: string;
}

@Component({
  selector: 'app-minha-conta',
  standalone: true,
  imports: [FormsModule, ResourceLayout],
  templateUrl: './minha-conta.html',
  styleUrl: './minha-conta.css',
})
export class MinhaConta implements OnDestroy {
  readonly bannerSrc = '/images/home/reuniao-discussao.jpg';

  private readonly RECAPTCHA_SITE_KEY_TESTE = '6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI';

  private readonly appRef = inject(ApplicationRef);

  constructor(
    public readonly auth: Auth,
  ) {
    effect(() => {
      const usuario = this.auth.usuarioAtual();
      if (usuario) {
        this.nomeExibicao = usuario.identificador;
        this.emailDetalhes = usuario.identificador.includes('@') ? usuario.identificador : '';
      }
    });
  }

  // Estado 1
  loginUsuarioOuEmail = '';
  loginSenha = '';
  lembrarDeMim = false;
  loginEnviando = false;
  loginMensagem = '';
  loginSucesso = false;
  captchaLoginOk = false;

  // Bloco de registro
  registroEmail = '';
  registroSenha = '';
  registroEnviando = false;
  registroMensagem = '';
  registroSucesso = false;
  captchaRegistroOk = false;

  onLoginSubmit(): void {
    this.loginEnviando = true;
    this.loginMensagem = '';

    this.auth
      .login({ usuarioOuEmail: this.loginUsuarioOuEmail, senha: this.loginSenha })
      .subscribe((resposta) => {
        this.loginEnviando = false;
        this.loginSucesso = resposta.success;
        this.loginMensagem = resposta.success
          ? ''
          : (resposta.error ?? 'Não foi possível entrar. Tente novamente.');
      });
  }

  onRegistroSubmit(): void {
    this.registroEnviando = true;
    this.registroMensagem = '';

    this.auth
      .register({ email: this.registroEmail, senha: this.registroSenha })
      .subscribe((resposta) => {
        this.registroEnviando = false;
        this.registroSucesso = resposta.success;
        this.registroMensagem = resposta.success
          ? ''
          : (resposta.error ?? 'Não foi possível criar a conta. Tente novamente.');
      });
  }

  // Widgets reCAPTCHA
  private captchaLoginWidgetId: number | null = null;
  private captchaRegistroWidgetId: number | null = null;
  private captchaLoginTimeoutId: ReturnType<typeof setTimeout> | null = null;
  private captchaRegistroTimeoutId: ReturnType<typeof setTimeout> | null = null;

  @ViewChild('captchaLogin')
  set captchaLoginRef(ref: ElementRef<HTMLDivElement> | undefined) {
    if (ref) {
      this.renderizarRecaptcha(ref.nativeElement, 'login');
    } else {
      this.captchaLoginWidgetId = null;
      this.captchaLoginOk = false;
    }
  }

  @ViewChild('captchaRegistro')
  set captchaRegistroRef(ref: ElementRef<HTMLDivElement> | undefined) {
    if (ref) {
      this.renderizarRecaptcha(ref.nativeElement, 'registro');
    } else {
      this.captchaRegistroWidgetId = null;
      this.captchaRegistroOk = false;
    }
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

  ngOnDestroy(): void {
    // evita retries do reCAPTCHA contra um container já removido do DOM
    if (this.captchaLoginTimeoutId !== null) {
      clearTimeout(this.captchaLoginTimeoutId);
    }
    if (this.captchaRegistroTimeoutId !== null) {
      clearTimeout(this.captchaRegistroTimeoutId);
    }
  }

  // Estado 2
  abaAtiva: AbaConta = 'dashboard';

  selecionarAba(aba: AbaConta): void {
    this.abaAtiva = aba;
  }

  readonly pedidos: PedidoConta[] = [
    {
      numero: '#JAB-10432',
      data: '02/06/2026',
      item: 'Livro — Um Novo Começo',
      status: 'Concluído',
      total: 'R$ 45,90',
    },
    {
      numero: '#JAB-10467',
      data: '15/06/2026',
      item: 'Chaveiro Jogadores Anônimos',
      status: 'Concluído',
      total: 'R$ 18,50',
    },
    {
      numero: '#JAB-10501',
      data: '28/06/2026',
      item: 'Folheto — Sua Primeira Reunião (pacote c/10)',
      status: 'Processando',
      total: 'R$ 22,00',
    },
    {
      numero: '#JAB-10528',
      data: '03/07/2026',
      item: 'Guia para o Inventário do Quarto Passo',
      status: 'Processando',
      total: 'R$ 15,90',
    },
  ];

  readonly downloads: DownloadConta[] = [
    { titulo: 'Sua Primeira Reunião', disponibilizadoEm: '02/06/2026' },
    { titulo: 'Um Novo Começo', disponibilizadoEm: '15/06/2026' },
    { titulo: 'Em Direção aos Noventa Dias', disponibilizadoEm: '28/06/2026' },
  ];

  // Aba "Detalhes da Conta"
  nomeExibicao = '';
  emailDetalhes = '';
  senhaAtualDetalhes = '';
  novaSenhaDetalhes = '';
  confirmarNovaSenhaDetalhes = '';
  detalhesMensagem = '';

  onSalvarDetalhes(): void {
    this.detalhesMensagem = 'Alteração de conta ainda não disponível — em breve.';
  }

  sair(): void {
    this.auth.logout();
    this.abaAtiva = 'dashboard';
  }
}

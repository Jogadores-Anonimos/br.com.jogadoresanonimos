import { Component, Input, inject, signal } from '@angular/core';

import { Auth } from '../../services/auth/auth';

@Component({
  selector: 'app-aviso-email-nao-confirmado',
  standalone: true,
  templateUrl: './aviso-email-nao-confirmado.html',
  styleUrl: './aviso-email-nao-confirmado.css',
})
export class AvisoEmailNaoConfirmado {
  @Input() variante: 'banner' | 'inline' = 'banner';
  @Input() mensagemContexto = 'Confirme seu e-mail para continuar.';

  private readonly auth = inject(Auth);

  readonly enviando = signal(false);
  readonly mensagemEnvio = signal('');
  readonly envioComSucesso = signal(false);

  reenviarConfirmacao(): void {
    const usuario = this.auth.usuarioAtualDetalhado();
    if (!usuario || this.enviando()) {
      return;
    }

    this.enviando.set(true);
    this.mensagemEnvio.set('');

    this.auth.reenviarConfirmacaoEmail({ identificador: usuario.email }).subscribe((resposta) => {
      this.enviando.set(false);
      this.envioComSucesso.set(resposta.success);
      this.mensagemEnvio.set(
        resposta.success
          ? (resposta.data?.mensagem ?? 'Se o e-mail ainda não estiver confirmado, enviaremos um novo link.')
          : (resposta.error?.message ?? 'Não foi possível reenviar a confirmação agora. Tente novamente.'),
      );
    });
  }
}

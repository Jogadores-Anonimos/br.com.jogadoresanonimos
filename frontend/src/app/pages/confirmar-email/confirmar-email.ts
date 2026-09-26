import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { Auth } from '../../services/auth/auth';

type EstadoConfirmacaoEmail = 'carregando' | 'sucesso' | 'expirado';

@Component({
  selector: 'app-confirmar-email',
  standalone: true,
  imports: [FormsModule, RouterLink, ResourceLayout],
  templateUrl: './confirmar-email.html',
  styleUrl: './confirmar-email.css',
})
export class ConfirmarEmail {
  readonly bannerSrc = '/images/home/reuniao-discussao.webp';

  private readonly auth = inject(Auth);
  private readonly route = inject(ActivatedRoute);

  readonly estado = signal<EstadoConfirmacaoEmail>('carregando');
  readonly mensagem = signal('');

  identificadorReenvio = '';
  readonly reenviando = signal(false);
  readonly mensagemReenvio = signal('');

  constructor() {
    const token = this.route.snapshot.queryParamMap.get('token')?.trim() ?? '';
    if (!token) {
      this.estado.set('expirado');
      this.mensagem.set('Link de confirmação inválido.');
      return;
    }

    this.auth.confirmarEmail(token).subscribe((resposta) => {
      if (resposta.success) {
        this.estado.set('sucesso');
        this.mensagem.set(resposta.data?.mensagem ?? 'E-mail confirmado com sucesso.');
        return;
      }
      this.estado.set('expirado');
      this.mensagem.set(resposta.error?.message ?? 'Link de confirmação inválido ou expirado.');
    });
  }

  reenviarConfirmacao(): void {
    if (!this.identificadorReenvio.trim() || this.reenviando()) {
      return;
    }

    this.reenviando.set(true);
    this.mensagemReenvio.set('');

    this.auth.reenviarConfirmacaoEmail({ identificador: this.identificadorReenvio }).subscribe((resposta) => {
      this.reenviando.set(false);
      this.mensagemReenvio.set(
        resposta.success
          ? (resposta.data?.mensagem ?? 'Se o apelido ou e-mail existir em nossa base, enviaremos um novo link de confirmação.')
          : (resposta.error?.message ?? 'Não foi possível processar seu pedido. Tente novamente.'),
      );
    });
  }
}

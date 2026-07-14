import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { Auth } from '../../services/auth';

@Component({
  selector: 'app-esqueci-senha',
  standalone: true,
  imports: [FormsModule, RouterLink, ResourceLayout],
  templateUrl: './esqueci-senha.html',
  styleUrl: './esqueci-senha.css',
})
export class EsqueciSenha {
  readonly bannerSrc = '/images/home/reuniao-discussao.jpg';

  constructor(private readonly auth: Auth) {}

  identificador = '';
  enviando = false;
  mensagem = '';
  enviado = false;

  onSubmit(): void {
    if (!this.identificador.trim()) {
      return;
    }

    this.enviando = true;
    this.mensagem = '';

    this.auth.esqueciSenha({ identificador: this.identificador }).subscribe((resposta) => {
      this.enviando = false;
      // resposta é sempre genérica, exista ou não a conta — anti-enumeration (contrato 1.3)
      this.enviado = resposta.success;
      this.mensagem = resposta.success
        ? (resposta.data?.mensagem ?? 'Se o apelido ou e-mail existir em nossa base, enviaremos um link de redefinição.')
        : (resposta.error?.message ?? 'Não foi possível processar seu pedido. Tente novamente.');
    });
  }
}

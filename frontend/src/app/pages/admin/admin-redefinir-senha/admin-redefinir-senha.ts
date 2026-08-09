import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { ResourceLayout } from '../../../components/resource-layout/resource-layout';
import { mensagemDeErro } from '../../../services/api-envelope';
import { AdminAuthService } from '../../../services/admin-auth/admin-auth';

@Component({
  selector: 'app-admin-redefinir-senha',
  standalone: true,
  imports: [FormsModule, RouterLink, ResourceLayout],
  templateUrl: './admin-redefinir-senha.html',
  styleUrl: './admin-redefinir-senha.css',
})
export class AdminRedefinirSenha {
  readonly bannerSrc = '/images/home/reuniao-discussao.jpg';

  private readonly adminAuth = inject(AdminAuthService);

  codigo = '';
  novaSenha = '';
  confirmarNovaSenha = '';
  enviando = false;
  mensagem = '';
  sucesso = false;

  onSubmit(): void {
    this.enviando = true;
    this.mensagem = '';

    this.adminAuth
      .redefinirSenha({
        codigo: this.codigo,
        novaSenha: this.novaSenha,
        confirmarNovaSenha: this.confirmarNovaSenha,
      })
      .subscribe((resposta) => {
        this.enviando = false;
        this.sucesso = resposta.success;
        this.mensagem = resposta.success
          ? (resposta.data?.mensagem ?? 'Senha redefinida com sucesso.')
          : mensagemDeErro(resposta.error, 'Código inválido, expirado ou já utilizado.');
      });
  }
}

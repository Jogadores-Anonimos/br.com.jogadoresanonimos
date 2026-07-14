import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { ResourceLayout } from '../../components/resource-layout/resource-layout';
import { Auth } from '../../services/auth';

@Component({
  selector: 'app-redefinir-senha',
  standalone: true,
  imports: [FormsModule, RouterLink, ResourceLayout],
  templateUrl: './redefinir-senha.html',
  styleUrl: './redefinir-senha.css',
})
export class RedefinirSenha {
  readonly bannerSrc = '/images/home/reuniao-discussao.jpg';

  private readonly auth = inject(Auth);
  private readonly route = inject(ActivatedRoute);

  private token = '';
  tokenAusente = false;

  novaSenha = '';
  confirmarNovaSenha = '';
  enviando = false;
  mensagem = '';
  sucesso = false;

  constructor() {
    const tokenParam = this.route.snapshot.queryParamMap.get('token');
    this.token = tokenParam?.trim() ?? '';
    this.tokenAusente = !this.token;
  }

  onSubmit(): void {
    if (!this.token) {
      this.mensagem = 'Link inválido ou expirado.';
      return;
    }

    this.enviando = true;
    this.mensagem = '';

    this.auth
      .redefinirSenha({
        token: this.token,
        novaSenha: this.novaSenha,
        confirmarNovaSenha: this.confirmarNovaSenha,
      })
      .subscribe((resposta) => {
        this.enviando = false;
        this.sucesso = resposta.success;
        // 401/token inválido também cai aqui com mensagem genérica única (contrato 1.4)
        this.mensagem = resposta.success
          ? (resposta.data?.mensagem ?? 'Senha redefinida com sucesso.')
          : (resposta.error?.message ?? 'Link inválido ou expirado.');
      });
  }
}

import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { Auth } from '../../services/auth';

@Component({
  selector: 'app-minha-conta',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './minha-conta.html',
  styleUrl: './minha-conta.css',
})
export class MinhaConta {
  // Bloco de login
  loginUsuarioOuEmail = '';
  loginSenha = '';
  lembrarDeMim = false;
  loginEnviando = false;
  loginMensagem = '';
  loginSucesso = false;

  // Bloco de registro
  registroEmail = '';
  registroSenha = '';
  registroEnviando = false;
  registroMensagem = '';
  registroSucesso = false;

  constructor(private readonly auth: Auth) {}

  onLoginSubmit(): void {
    this.loginEnviando = true;
    this.loginMensagem = '';

    this.auth
      .login({ usuarioOuEmail: this.loginUsuarioOuEmail, senha: this.loginSenha })
      .subscribe((resposta) => {
        this.loginEnviando = false;
        this.loginSucesso = resposta.success;
        this.loginMensagem = resposta.success
          ? 'Login ainda não disponível — em breve.'
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
          ? 'Cadastro ainda não disponível — em breve.'
          : (resposta.error ?? 'Não foi possível criar a conta. Tente novamente.');
      });
  }
}

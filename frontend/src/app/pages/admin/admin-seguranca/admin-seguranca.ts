import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { Auth } from '../../../services/auth/auth';

@Component({
  selector: 'app-admin-seguranca',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-seguranca.html',
  styleUrl: './admin-seguranca.css',
})
export class AdminSeguranca implements OnInit {
  private readonly auth = inject(Auth);

  mfaAtivo = false;
  carregandoEstadoInicial = true;

  enrollCarregando = false;
  enrollErro = '';
  otpauthUri = '';

  confirmarCodigoTotp = '';
  confirmarEnviando = false;
  confirmarErro = '';

  desativarSenha = '';
  desativarEnviando = false;
  desativarErro = '';
  desativarMensagem = '';

  ngOnInit(): void {
    this.auth.buscarUsuarioLogado().subscribe((resposta) => {
      this.carregandoEstadoInicial = false;
      if (resposta.success && resposta.data) {
        this.mfaAtivo = resposta.data.mfaAtivo;
      }
    });
  }

  selecionarChaveConfiguracao(input: HTMLInputElement): void {
    input.select();
  }

  iniciarEnrollment(): void {
    this.enrollCarregando = true;
    this.enrollErro = '';
    this.otpauthUri = '';

    this.auth.enrollMfa().subscribe((resposta) => {
      this.enrollCarregando = false;
      if (resposta.success && resposta.data) {
        this.otpauthUri = resposta.data.otpauthUri;
      } else {
        this.enrollErro = resposta.error?.message ?? 'Não foi possível iniciar a configuração do MFA.';
      }
    });
  }

  cancelarEnrollment(): void {
    this.otpauthUri = '';
    this.confirmarCodigoTotp = '';
    this.confirmarErro = '';
  }

  confirmarEnrollment(): void {
    this.confirmarEnviando = true;
    this.confirmarErro = '';

    this.auth.confirmarMfa({ codigoTotp: this.confirmarCodigoTotp }).subscribe((resposta) => {
      this.confirmarEnviando = false;
      if (resposta.success) {
        this.mfaAtivo = true;
        this.otpauthUri = '';
        this.confirmarCodigoTotp = '';
      } else {
        this.confirmarErro = resposta.error?.message ?? 'Código inválido. Tente novamente.';
      }
    });
  }

  desativarMfa(): void {
    this.desativarEnviando = true;
    this.desativarErro = '';
    this.desativarMensagem = '';

    this.auth.desativarMfa({ senha: this.desativarSenha }).subscribe((resposta) => {
      this.desativarEnviando = false;
      if (resposta.success) {
        this.mfaAtivo = false;
        this.desativarSenha = '';
        this.desativarMensagem = resposta.data?.mensagem ?? 'MFA desativado com sucesso.';
      } else {
        this.desativarErro = resposta.error?.message ?? 'Não foi possível desativar o MFA.';
      }
    });
  }
}

import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { QRCodeComponent } from 'angularx-qrcode';

import { Auth } from '../../../services/auth/auth';
import { MFA_CODIGO_INVALIDO_MENSAGEM, mensagemDeErro } from '../../../services/api-envelope';

@Component({
  selector: 'app-admin-seguranca',
  standalone: true,
  imports: [CommonModule, FormsModule, QRCodeComponent],
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
  chaveManual = '';
  chaveCopiada = false;
  private timeoutChaveCopiada?: ReturnType<typeof setTimeout>;

  confirmarCodigoTotp = '';
  confirmarEnviando = false;
  confirmarErro = '';

  desativarSenha = '';
  desativarCodigoTotp = '';
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

  private extrairChaveManual(otpauthUri: string): string {
    try {
      const parametros = new URL(otpauthUri.replace('otpauth://', 'https://')).searchParams;
      return parametros.get('secret') ?? '';
    } catch {
      return '';
    }
  }

  iniciarEnrollment(): void {
    this.enrollCarregando = true;
    this.enrollErro = '';
    this.otpauthUri = '';
    this.chaveManual = '';

    this.auth.enrollMfa().subscribe((resposta) => {
      this.enrollCarregando = false;
      if (resposta.success && resposta.data) {
        this.otpauthUri = resposta.data.otpauthUri;
        this.chaveManual = this.extrairChaveManual(resposta.data.otpauthUri);
      } else {
        this.enrollErro = mensagemDeErro(resposta.error, 'Não foi possível iniciar a configuração do MFA.');
      }
    });
  }

  copiarChaveManual(): void {
    if (!this.chaveManual) {
      return;
    }
    navigator.clipboard.writeText(this.chaveManual).then(() => {
      this.chaveCopiada = true;
      clearTimeout(this.timeoutChaveCopiada);
      this.timeoutChaveCopiada = setTimeout(() => {
        this.chaveCopiada = false;
      }, 2000);
    });
  }

  cancelarEnrollment(): void {
    this.otpauthUri = '';
    this.chaveManual = '';
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
        this.chaveManual = '';
        this.confirmarCodigoTotp = '';
      } else {
        this.confirmarErro = mensagemDeErro(resposta.error, MFA_CODIGO_INVALIDO_MENSAGEM);
      }
    });
  }

  desativarMfa(): void {
    this.desativarEnviando = true;
    this.desativarErro = '';
    this.desativarMensagem = '';

    this.auth
      .desativarMfa({ senha: this.desativarSenha, codigoTotp: this.desativarCodigoTotp })
      .subscribe((resposta) => {
        this.desativarEnviando = false;
        if (resposta.success) {
          this.mfaAtivo = false;
          this.desativarSenha = '';
          this.desativarCodigoTotp = '';
          this.desativarMensagem = resposta.data?.mensagem ?? 'MFA desativado com sucesso.';
        } else {
          this.desativarErro = mensagemDeErro(resposta.error, 'Não foi possível desativar o MFA.');
        }
      });
  }
}

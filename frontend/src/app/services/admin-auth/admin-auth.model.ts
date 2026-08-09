export interface AdminRedefinirSenhaRequest {
  codigo: string;
  novaSenha: string;
  confirmarNovaSenha: string;
}

export interface MensagemResponse {
  mensagem: string;
}

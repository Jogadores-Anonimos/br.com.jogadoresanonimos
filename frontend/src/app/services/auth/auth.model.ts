export type Role = 'PARTICIPANTE' | 'ADMIN';

export interface RegistroRequest {
  apelido: string;
  email: string;
  senha: string;
  confirmarSenha: string;
  recaptchaToken: string;
}

export interface UsuarioResumo {
  id: string;
  apelido: string;
  role: Role;
  deveTrocarSenha: boolean;
}

export interface UsuarioResponse extends UsuarioResumo {
  criadoEm: string;
}

export interface LoginRequest {
  identificador: string;
  senha: string;
  recaptchaToken: string;
  lembrarDeMim?: boolean;
}

export interface LoginMfaRequest {
  identificador: string;
  senha: string;
  recaptchaToken: string;
  codigoTotp: string;
}

export interface LoginResponse {
  mfaRequerido: boolean;
  token: string | null;
  expiraEm: string | null;
  usuario: UsuarioResumo | null;
}

export interface MfaEnrollResponse {
  otpauthUri: string;
}

export interface ConfirmarMfaRequest {
  codigoTotp: string;
}

export interface DesativarMfaRequest {
  senha: string;
}

export interface EsqueciSenhaRequest {
  identificador: string;
}

export interface MensagemResponse {
  mensagem: string;
}

export interface RedefinirSenhaRequest {
  token: string;
  novaSenha: string;
  confirmarNovaSenha: string;
}

export interface UsuarioMe extends UsuarioResumo {
  email: string;
  criadoEm: string;
  mfaAtivo: boolean;
}

export interface AtualizarContaRequest {
  apelido: string;
  email: string;
}

export interface AlterarSenhaRequest {
  senhaAtual: string;
  novaSenha: string;
  confirmarNovaSenha: string;
}

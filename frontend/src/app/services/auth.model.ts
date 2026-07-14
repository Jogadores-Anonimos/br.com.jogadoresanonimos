// Contratos de dados do domínio de autenticação/e-mail — ver
// backend/docs/api/contrato-auth-cms-upload.md (Bloco 1)

export type Role = 'PARTICIPANTE' | 'ADMIN';

export interface RegistroRequest {
  apelido: string;
  email: string;
  senha: string;
  confirmarSenha: string;
}

export interface UsuarioResumo {
  id: string;
  apelido: string;
  role: Role;
}

// resposta do registro nunca inclui email (ver contrato 1.1)
export interface UsuarioResponse extends UsuarioResumo {
  criadoEm: string;
}

export interface LoginRequest {
  identificador: string; // apelido OU e-mail
  senha: string;
}

export interface LoginResponse {
  token: string;
  expiraEm: string;
  usuario: UsuarioResumo;
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

// único endpoint de leitura que expõe email (ver contrato 1.5)
export interface UsuarioMe extends UsuarioResumo {
  email: string;
  criadoEm: string;
}

// ver backend/docs/api/contrato-usuario-conta-propria.md (bloco 1)
export interface AtualizarContaRequest {
  apelido: string;
  email: string;
}

// ver backend/docs/api/contrato-usuario-conta-propria.md (bloco 2)
export interface AlterarSenhaRequest {
  senhaAtual: string;
  novaSenha: string;
  confirmarNovaSenha: string;
}

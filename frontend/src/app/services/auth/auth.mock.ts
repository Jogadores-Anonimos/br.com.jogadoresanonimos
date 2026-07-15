import { UsuarioMe, UsuarioResumo } from './auth.model';

interface UsuarioMockInterno extends UsuarioMe {
  senha: string;
}

export const USUARIOS_MOCK: UsuarioMockInterno[] = [
  {
    id: 'mock-usuario-1',
    apelido: 'viajante_anonimo',
    email: 'viajante.mock@example.com',
    role: 'PARTICIPANTE',
    criadoEm: '2026-06-01T10:00:00Z',
    deveTrocarSenha: false,
    mfaAtivo: false,
    senha: 'senhaProvisoria123',
  },
];

export function encontrarUsuarioMockPorIdentificador(identificador: string): UsuarioMockInterno | undefined {
  const normalizado = identificador.trim().toLowerCase();
  return USUARIOS_MOCK.find(
    (u) => u.apelido.toLowerCase() === normalizado || u.email.toLowerCase() === normalizado,
  );
}

export function paraResumo(usuario: UsuarioMockInterno): UsuarioResumo {
  return { id: usuario.id, apelido: usuario.apelido, role: usuario.role, deveTrocarSenha: usuario.deveTrocarSenha };
}

export function gerarTokenMock(usuarioId: string): string {
  return `mock-jwt.${usuarioId}.${Date.now()}`;
}

export function encontrarUsuarioMockPorToken(token: string): UsuarioMockInterno | undefined {
  const partes = token.split('.');
  if (partes.length !== 3 || partes[0] !== 'mock-jwt') {
    return undefined;
  }
  return USUARIOS_MOCK.find((u) => u.id === partes[1]);
}

export const CODIGO_TOTP_MOCK = '123456';
export const OTPAUTH_URI_MOCK = 'otpauth://totp/JogadoresAnonimos:mock?secret=MOCKSECRETMOCKSECRET&issuer=JogadoresAnonimos';

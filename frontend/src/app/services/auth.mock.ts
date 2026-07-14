// TODO: substituir por chamada real quando o endpoint estiver disponível
// Mocks centralizados do domínio de autenticação — ver contrato em
// backend/docs/api/contrato-auth-cms-upload.md (Bloco 1)
import { UsuarioMe, UsuarioResumo } from './auth.model';

interface UsuarioMockInterno extends UsuarioMe {
  senha: string;
}

// base mockada em memória — só existe durante a sessão do navegador
export const USUARIOS_MOCK: UsuarioMockInterno[] = [
  {
    id: 'mock-usuario-1',
    apelido: 'viajante_anonimo',
    email: 'viajante.mock@example.com',
    role: 'PARTICIPANTE',
    criadoEm: '2026-06-01T10:00:00Z',
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
  return { id: usuario.id, apelido: usuario.apelido, role: usuario.role };
}

export function gerarTokenMock(usuarioId: string): string {
  return `mock-jwt.${usuarioId}.${Date.now()}`;
}

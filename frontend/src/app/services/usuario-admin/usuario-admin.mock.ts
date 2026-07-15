import { UsuarioAdmin } from './usuario-admin.model';

export const USUARIOS_ADMIN_MOCK: UsuarioAdmin[] = [
  { id: 'mock-usuario-1', apelido: 'admin', email: 'admin@example.com', role: 'ADMIN', criadoEm: '2026-01-10T09:00:00.000Z' },
  { id: 'mock-usuario-2', apelido: 'participante1', email: 'participante1@example.com', role: 'PARTICIPANTE', criadoEm: '2026-03-05T14:20:00.000Z' },
  { id: 'mock-usuario-3', apelido: 'participante2', email: 'participante2@example.com', role: 'PARTICIPANTE', criadoEm: '2026-05-18T11:45:00.000Z' },
];

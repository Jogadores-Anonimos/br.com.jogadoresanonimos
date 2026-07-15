import { Role } from '../auth/auth.model';

export interface UsuarioAdmin {
  id: string;
  apelido: string;
  email: string;
  role: Role;
  criadoEm: string;
}

export interface EditarRoleRequest {
  role: Role;
}

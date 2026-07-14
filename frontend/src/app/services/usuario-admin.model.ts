// Contratos de dados de Gestão de Usuários (Admin) — ver
// backend/docs/api/contrato-usuario-admin.md
import { Role } from './auth.model';

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

import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, delay } from 'rxjs/operators';

import { environment } from '../../environments/environment';
import { ApiEnvelope, apiError, apiSuccess } from './api-envelope';
import { Auth } from './auth';
import { EditarRoleRequest, UsuarioAdmin } from './usuario-admin.model';
import { USUARIOS_ADMIN_MOCK } from './usuario-admin.mock';

@Injectable({ providedIn: 'root' })
export class UsuarioAdminService {
  private readonly http = inject(HttpClient);
  private readonly auth = inject(Auth);
  private readonly adminUrl = `${environment.apiBaseUrl}/admin/usuarios`;

  listarUsuarios(): Observable<ApiEnvelope<UsuarioAdmin[]>> {
    if (!environment.useMocks) {
      return this.http.get<ApiEnvelope<UsuarioAdmin[]>>(this.adminUrl).pipe(
        catchError(() => of(apiError<UsuarioAdmin[]>('UPSTREAM_ERROR', 'Não foi possível carregar os usuários.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    const ordenados = [...USUARIOS_ADMIN_MOCK].sort((a, b) => new Date(b.criadoEm).getTime() - new Date(a.criadoEm).getTime());
    return of(apiSuccess(ordenados)).pipe(delay(400));
  }

  editarRole(id: string, req: EditarRoleRequest): Observable<ApiEnvelope<UsuarioAdmin>> {
    if (!environment.useMocks) {
      return this.http.put<ApiEnvelope<UsuarioAdmin>>(`${this.adminUrl}/${id}/role`, req).pipe(
        catchError(() => of(apiError<UsuarioAdmin>('UPSTREAM_ERROR', 'Não foi possível alterar a role agora.'))),
      );
    }

    // TODO: substituir por chamada real quando o endpoint estiver disponível
    const usuario = USUARIOS_ADMIN_MOCK.find((u) => u.id === id);
    if (!usuario) {
      return of(apiError<UsuarioAdmin>('NOT_FOUND', 'Usuário não encontrado.')).pipe(delay(300));
    }

    const solicitanteId = this.auth.usuarioAtual()?.id;
    const rebaixandoParaNaoAdmin = usuario.role === 'ADMIN' && req.role !== 'ADMIN';

    if (rebaixandoParaNaoAdmin && solicitanteId === id) {
      return of(apiError<UsuarioAdmin>('CONFLICT', 'Você não pode remover sua própria permissão de administrador.')).pipe(delay(300));
    }

    if (rebaixandoParaNaoAdmin) {
      const totalAdmins = USUARIOS_ADMIN_MOCK.filter((u) => u.role === 'ADMIN').length;
      if (totalAdmins <= 1) {
        return of(apiError<UsuarioAdmin>('CONFLICT', 'Não é possível remover o último administrador do sistema.')).pipe(delay(300));
      }
    }

    usuario.role = req.role;
    return of(apiSuccess(usuario)).pipe(delay(400));
  }
}

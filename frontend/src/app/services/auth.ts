import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';

/**
 * Contrato de requisição de login.
 * Anonimato: aceita usuário/apelido OU e-mail, nunca nome real.
 */
export interface LoginRequest {
  usuarioOuEmail: string;
  senha: string;
}

/**
 * Contrato de requisição de registro.
 * Só e-mail (para recuperação de conta) + senha — sem dado que permita
 * reidentificação do usuário (sem nome real, sem CPF, etc).
 */
export interface RegisterRequest {
  email: string;
  senha: string;
}

/**
 * Formato padrão de resposta de API do projeto: { success, data, error }.
 * Ver CLAUDE.md — "Padrões de arquitetura".
 */
export interface AuthResponse {
  success: boolean;
  data?: { token: string };
  error?: string;
}

// TODO: substituir por chamada real quando o endpoint estiver disponível
@Injectable({ providedIn: 'root' })
export class Auth {
  /**
   * Autentica o usuário. Hoje é um mock local (sem rede); a assinatura
   * pública (Observable<AuthResponse>) é a mesma que a chamada HTTP real
   * terá quando o backend estiver disponível.
   */
  login(req: LoginRequest): Observable<AuthResponse> {
    // TODO: substituir por chamada real quando o endpoint estiver disponível
    // ex: return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/login`, req).pipe(catchError(...));
    if (!req.usuarioOuEmail || !req.senha) {
      return of({ success: false, error: 'Preencha usuário/e-mail e senha.' }).pipe(delay(500));
    }

    return of<AuthResponse>({ success: true, data: { token: 'mock-token' } }).pipe(delay(500));
  }

  /**
   * Registra um novo usuário. Mock local por enquanto, mesma assinatura
   * que a chamada HTTP real terá quando o backend estiver disponível.
   */
  register(req: RegisterRequest): Observable<AuthResponse> {
    // TODO: substituir por chamada real quando o endpoint estiver disponível
    // ex: return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/register`, req).pipe(catchError(...));
    if (!req.email || !req.senha) {
      return of({ success: false, error: 'Preencha e-mail e senha.' }).pipe(delay(500));
    }

    return of<AuthResponse>({ success: true, data: { token: 'mock-token' } }).pipe(delay(500));
  }
}

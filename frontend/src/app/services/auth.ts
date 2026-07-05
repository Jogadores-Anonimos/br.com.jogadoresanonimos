import { Injectable, signal } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay, tap } from 'rxjs/operators';

export interface LoginRequest {
  usuarioOuEmail: string;
  senha: string;
}

export interface RegisterRequest {
  email: string;
  senha: string;
}

export interface AuthResponse {
  success: boolean;
  data?: { token: string };
  error?: string;
}

export interface UsuarioLogado {
  identificador: string;
}

@Injectable({ providedIn: 'root' })
export class Auth {
  private readonly usuarioAtualSignal = signal<UsuarioLogado | null>(null);
  readonly usuarioAtual = this.usuarioAtualSignal.asReadonly();

  login(req: LoginRequest): Observable<AuthResponse> {
    if (!req.usuarioOuEmail || !req.senha) {
      return of({ success: false, error: 'Preencha usuário/e-mail e senha.' }).pipe(delay(500));
    }

    return of<AuthResponse>({ success: true, data: { token: 'mock-token' } }).pipe(
      delay(500),
      tap((resposta) => {
        if (resposta.success) {
          this.usuarioAtualSignal.set({ identificador: req.usuarioOuEmail });
        }
      }),
    );
  }

  register(req: RegisterRequest): Observable<AuthResponse> {
    if (!req.email || !req.senha) {
      return of({ success: false, error: 'Preencha e-mail e senha.' }).pipe(delay(500));
    }

    return of<AuthResponse>({ success: true, data: { token: 'mock-token' } }).pipe(
      delay(500),
      tap((resposta) => {
        if (resposta.success) {
          this.usuarioAtualSignal.set({ identificador: req.email });
        }
      }),
    );
  }

  logout(): void {
    this.usuarioAtualSignal.set(null);
  }
}

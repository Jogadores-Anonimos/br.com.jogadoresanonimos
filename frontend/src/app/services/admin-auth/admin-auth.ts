import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { environment } from '../../../environments/environment';
import { ApiEnvelope, apiError, apiErrorDeHttpError } from '../api-envelope';
import { AdminRedefinirSenhaRequest, MensagemResponse } from './admin-auth.model';

const SENHA_MIN = 10;
const CODIGO_REGEX = /^\d{6}$/;

@Injectable({ providedIn: 'root' })
export class AdminAuthService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/admin/auth`;

  redefinirSenha(req: AdminRedefinirSenhaRequest): Observable<ApiEnvelope<MensagemResponse>> {
    if (!CODIGO_REGEX.test(req.codigo ?? '')) {
      return of(apiError<MensagemResponse>('VALIDATION_ERROR', 'Informe o código de 6 dígitos recebido por e-mail.'));
    }
    if (!req.novaSenha || req.novaSenha.length < SENHA_MIN) {
      return of(apiError<MensagemResponse>('VALIDATION_ERROR', `A nova senha deve ter no mínimo ${SENHA_MIN} caracteres.`));
    }
    if (req.novaSenha !== req.confirmarNovaSenha) {
      return of(apiError<MensagemResponse>('VALIDATION_ERROR', 'As senhas informadas não coincidem.'));
    }

    return this.http.post<ApiEnvelope<MensagemResponse>>(`${this.baseUrl}/redefinir-senha`, req).pipe(
      catchError((erro) =>
        of(apiErrorDeHttpError<MensagemResponse>(erro, 'INTERNAL_ERROR', 'Não foi possível redefinir sua senha. Tente novamente.')),
      ),
    );
  }
}

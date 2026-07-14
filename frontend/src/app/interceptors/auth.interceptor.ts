import { inject } from '@angular/core';
import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

import { environment } from '../../environments/environment';
import { Auth } from '../services/auth';

// Anexa Authorization: Bearer <token> só em chamadas pra própria API.
// Em 401, limpa a sessão local mas não redireciona daqui — cada tela
// decide o que fazer com o erro no seu próprio catchError.
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(Auth);

  const isApiRequest = req.url.startsWith(environment.apiBaseUrl);
  const token = auth.obterToken();

  const requisicao = isApiRequest && token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(requisicao).pipe(
    catchError((erro: unknown) => {
      if (isApiRequest && erro instanceof HttpErrorResponse && erro.status === 401) {
        auth.logout();
      }
      return throwError(() => erro);
    }),
  );
};

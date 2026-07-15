import { inject } from '@angular/core';
import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

import { environment } from '../../environments/environment';
import { Auth } from '../services/auth/auth';

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

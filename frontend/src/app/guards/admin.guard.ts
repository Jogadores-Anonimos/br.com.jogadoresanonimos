import { inject } from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { CanActivateFn, Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { filter, take } from 'rxjs/operators';

import { Auth } from '../services/auth/auth';

export const adminGuard: CanActivateFn = async () => {
  const auth = inject(Auth);
  const router = inject(Router);

  await firstValueFrom(
    toObservable(auth.rehidratacaoConcluida).pipe(
      filter((concluida) => concluida),
      take(1)
    )
  );

  const usuario = auth.usuarioAtual();

  if (!usuario) {
    return router.createUrlTree(['/minha-conta']);
  }

  if (usuario.deveTrocarSenha) {
    return router.createUrlTree(['/minha-conta']);
  }

  if (usuario.role !== 'ADMIN') {
    return router.createUrlTree(['/']);
  }

  return true;
};

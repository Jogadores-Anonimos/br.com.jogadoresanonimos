import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { Auth } from '../services/auth';

// Guard só cuida de UX (esconder/redirecionar no client). A fonte de
// verdade de permissão é @PreAuthorize no backend — ver `security`.
export const adminGuard: CanActivateFn = () => {
  const auth = inject(Auth);
  const router = inject(Router);

  const usuario = auth.usuarioAtual();

  if (!usuario) {
    return router.createUrlTree(['/minha-conta']);
  }

  if (usuario.role !== 'ADMIN') {
    return router.createUrlTree(['/']);
  }

  return true;
};

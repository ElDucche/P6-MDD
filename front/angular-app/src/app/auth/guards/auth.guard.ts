import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../../features/auth/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLoggedIn()) {
    return true; // L'utilisateur est connecté, on autorise l'accès
  } else {
    router.navigate(['/login']); // L'utilisateur n'est pas connecté, redirection vers login
    return false;
  }
};

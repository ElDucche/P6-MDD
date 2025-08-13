import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../auth.service';

export const publicGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLoggedIn()) {
    router.navigate(['/home']); // L'utilisateur est connecté, redirection vers home
    return false;
  } else {
    return true; // L'utilisateur n'est pas connecté, on autorise l'accès
  }
};

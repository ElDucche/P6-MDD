import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AlertService } from '../services/alert.service';

/**
 * Intercepteur d'authentification moderne pour Angular 18+
 * Avec HttpOnly cookies, le navigateur envoie automatiquement le cookie
 * Plus besoin d'ajouter manuellement le header Authorization
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const alertService = inject(AlertService);
  const router = inject(Router);
  
  // Cloner la requête pour inclure withCredentials (important pour les cookies)
  const clonedReq = req.clone({
    withCredentials: true
  });
  
  // Exécuter la requête avec gestion d'erreurs
  return next(clonedReq).pipe(
    catchError((error) => {
      console.error('Erreur HTTP interceptée:', error);
      
      // Gestion spécifique des erreurs 401 (non autorisé)
      if (error.status === 401) {
        alertService.showAlert({
          type: 'error',
          message: 'Session expirée. Veuillez vous reconnecter.'
        });
        // Rediriger vers la page de connexion
        router.navigate(['/auth/login']);
      }
      
      // Gestion des autres erreurs
      let errorMessage = 'Une erreur est survenue';
      if (error.error?.message) {
        errorMessage = error.error.message;
      } else if (typeof error.error === 'string') {
        errorMessage = error.error;
      } else if (error.message) {
        errorMessage = error.message;
      }
      
      return throwError(() => new Error(errorMessage));
    })
  );
};

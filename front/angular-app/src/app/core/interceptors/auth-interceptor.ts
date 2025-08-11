import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../../auth/auth.service';
import { AlertService } from '../services/alert.service';

/**
 * Intercepteur d'authentification moderne pour Angular 18+
 * Ajoute automatiquement le token JWT aux requêtes et gère les erreurs
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const alertService = inject(AlertService);
  
  // Récupérer le token
  const token = authService.getToken();
  
  // Cloner la requête avec le token si disponible
  let clonedReq = req;
  if (token) {
    clonedReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }
  
  // Exécuter la requête avec gestion d'erreurs
  return next(clonedReq).pipe(
    catchError((error) => {
      console.error('Erreur HTTP interceptée:', error);
      
      // Gestion spécifique des erreurs 401 (non autorisé)
      if (error.status === 401) {
        authService.logout();
        alertService.showAlert({
          type: 'error',
          message: 'Session expirée. Veuillez vous reconnecter.'
        });
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

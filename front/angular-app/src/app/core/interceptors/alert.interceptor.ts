import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { AlertService } from '../../core/services/alert.service';

@Injectable()
export class AlertInterceptor implements HttpInterceptor {
  constructor(private alertService: AlertService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      tap({
        next: (event) => {
          if (event instanceof HttpResponse) {
            if (event.status === 200 || event.status === 201) {
              // Afficher uniquement si la réponse contient la propriété 'text'
              if (event.body && typeof event.body === 'object' && 'text' in event.body) {
                this.alertService.showAlert({ type: 'success', message: event.body.text });
              }
            }
          }
        },
        error: (error: HttpErrorResponse) => {
          // Affiche l'erreur avec la classe 'alert-error'
          const message = error.error?.message || error.message || 'Une erreur est survenue';
          this.alertService.showAlert({ type: 'error', message });
        }
      })
    );
  }
}

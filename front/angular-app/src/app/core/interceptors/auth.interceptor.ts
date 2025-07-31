import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpErrorResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, throwError } from "rxjs";
import { catchError } from "rxjs/operators";
import { AuthService } from "../../auth/auth.service";
import { AlertService } from "../services/alert.service";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

    constructor(private authService: AuthService, private alertService: AlertService) {}

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        const token = this.authService.getToken();
        let clonedReq = req;
        if (token) {
            clonedReq = req.clone({
                headers: req.headers.set("Authorization", "Bearer " + token)
            });
        }
        
        return next.handle(clonedReq).pipe(
            catchError((error: HttpErrorResponse) => {
                console.error("Full error response from backend:", error); // Log the full error

                let errorMessage = 'An unknown error occurred!';
                if (error.error instanceof ErrorEvent) {
                    // Client-side errors
                    errorMessage = `Error: ${error.error.message}`;
                } else {
                    // Server-side errors
                    if (error.error && typeof error.error === 'object') {
                        if (error.error.message) {
                            errorMessage = error.error.message;
                        } else if (error.error.error) {
                            errorMessage = error.error.error;
                        } else {
                            errorMessage = JSON.stringify(error.error);
                        }
                    } else if (typeof error.error === 'string') {
                        errorMessage = error.error;
                    } else {
                        errorMessage = `Error Code: ${error.status} - ${error.statusText || 'Unknown Status'}`;
                    }
                }
                this.alertService.showAlert({ message: errorMessage, type: 'error' });
                return throwError(() => new Error(errorMessage));
            })
        );
    }
}

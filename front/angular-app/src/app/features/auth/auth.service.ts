import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { tap, catchError, map } from 'rxjs/operators';
import { ConfigService } from '../../core/services/config.service';

export interface LoginResponse {
  token: string | null;
  message: string;
}

export interface CurrentUser {
  userId: number;
  username: string;
  email: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  // Signal pour suivre l'état de connexion
  private readonly isAuthenticated = signal<boolean>(false);
  // Cache de l'utilisateur courant
  private currentUserCache: CurrentUser | null = null;

  constructor(
    private readonly http: HttpClient,
    private readonly config: ConfigService
  ) {
    // Vérifier si l'utilisateur est déjà connecté au démarrage
    this.checkAuthStatus();
  }

  login(credentials: { identifier: string; password: string }): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(
      this.config.endpoints.auth.login, 
      credentials,
      { withCredentials: true } // Important : envoyer et recevoir les cookies
    ).pipe(
      tap(response => {
        // Le token est maintenant dans un cookie HttpOnly
        // On ne le stocke plus dans localStorage
        if (response?.message === 'Connexion réussie' || response?.token === null) {
          this.isAuthenticated.set(true);
          // Invalider le cache utilisateur
          this.currentUserCache = null;
        } else {
          throw new Error(response.message || 'Erreur de connexion');
        }
      })
    );
  }

  register(userInfo: any): Observable<any> {
    return this.http.post(
      this.config.endpoints.auth.register, 
      userInfo, 
      { 
        responseType: 'json',
        withCredentials: true // Important : envoyer et recevoir les cookies
      }
    ).pipe(
      tap(() => {
        this.isAuthenticated.set(true);
        // Invalider le cache utilisateur
        this.currentUserCache = null;
      })
    );
  }

  logout(): Observable<any> {
    return this.http.post(
      `${this.config.apiUrl}/api/auth/logout`,
      {},
      { withCredentials: true }
    ).pipe(
      tap(() => {
        this.isAuthenticated.set(false);
        this.currentUserCache = null;
      })
    );
  }

  /**
   * Le token n'est plus accessible depuis le frontend (sécurité)
   * Cette méthode retourne null car le token est dans un cookie HttpOnly
   */
  getToken(): string | null {
    // Le token est maintenant dans un cookie HttpOnly inaccessible depuis JavaScript
    // C'est normal et souhaité pour la sécurité
    return null;
  }

  isLoggedIn(): boolean {
    return this.isAuthenticated();
  }

  /**
   * Vérifie le statut d'authentification en interrogeant le backend
   */
  private checkAuthStatus(): void {
    // Appeler l'endpoint /me pour vérifier si le cookie est valide
    this.http.get<any>(this.config.endpoints.users.me, { withCredentials: true })
      .pipe(
        tap(() => this.isAuthenticated.set(true)),
        catchError(() => {
          this.isAuthenticated.set(false);
          return of(null);
        })
      )
      .subscribe();
  }

  /**
   * Récupère les informations de l'utilisateur courant depuis le backend
   */
  getCurrentUser(): Observable<CurrentUser | null> {
    // Si on a déjà l'utilisateur en cache, le retourner
    if (this.currentUserCache) {
      return of(this.currentUserCache);
    }

    // Sinon, appeler le backend
    return this.http.get<any>(this.config.endpoints.users.me, { withCredentials: true })
      .pipe(
        map(response => {
          const user: CurrentUser = {
            userId: response.id,
            username: response.username,
            email: response.email
          };
          this.currentUserCache = user;
          return user;
        }),
        catchError(error => {
          console.error('Erreur lors de la récupération de l\'utilisateur:', error);
          return of(null);
        })
      );
  }

  /**
   * Récupère l'ID de l'utilisateur courant
   */
  getCurrentUserId(): Observable<number | null> {
    return this.getCurrentUser().pipe(
      map(user => user?.userId || null)
    );
  }
}

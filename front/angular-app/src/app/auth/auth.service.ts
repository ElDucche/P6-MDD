import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface CurrentUser {
  userId: number;
  username: string;
  email: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly apiUrl = environment.apiUrl + 'api/auth'; // URL configurable via environment.ts

  constructor(private readonly http: HttpClient) { }

  login(credentials: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        if (response?.token) {
          localStorage.setItem('token', response.token);
        }
      })
    );
  }

  register(userInfo: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, userInfo, { responseType: 'text' });
  }

  logout(): void {
    localStorage.removeItem('token');
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isLoggedIn(): boolean {
    return this.getToken() !== null;
  }

  /**
   * Décode le token JWT pour récupérer les informations de l'utilisateur
   */
  getCurrentUser(): CurrentUser | null {
    const token = this.getToken();
    if (!token) {
      return null;
    }

    try {
      // Decode JWT payload (partie entre les deux points)
      const payload = token.split('.')[1];
      const decodedPayload = JSON.parse(atob(payload));
      
      return {
        userId: decodedPayload.userId,
        username: decodedPayload.username,
        email: decodedPayload.sub
      };
    } catch (error) {
      console.error('Erreur lors du décodage du token:', error);
      return null;
    }
  }

  /**
   * Récupère l'ID de l'utilisateur courant
   */
  getCurrentUserId(): number | null {
    const user = this.getCurrentUser();
    return user?.userId || null;
  }
}

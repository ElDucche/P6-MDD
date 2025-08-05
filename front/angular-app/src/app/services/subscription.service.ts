import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Subscription {
  userId: number;
  themeId: number;
}

@Injectable({
  providedIn: 'root'
})
export class SubscriptionService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}api/subscriptions`;

  /**
   * S'abonner à un thème
   */
  subscribe(themeId: number, userId: number): Observable<Subscription> {
    return this.http.post<Subscription>(this.apiUrl, {
      userId,
      themeId
    });
  }

  /**
   * Se désabonner d'un thème
   */
  unsubscribe(themeId: number, userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${userId}/${themeId}`);
  }

  /**
   * Récupère les abonnements d'un utilisateur
   */
  getUserSubscriptions(userId: number): Observable<Subscription[]> {
    return this.http.get<Subscription[]>(`${this.apiUrl}/user/${userId}`);
  }

  /**
   * Vérifie si l'utilisateur est abonné à un thème
   */
  isSubscribed(themeId: number, subscriptions: Subscription[]): boolean {
    return subscriptions.some(sub => sub.themeId === themeId);
  }
}

import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Subscription {
  id: {
    userId: number;
    themeId: number;
  };
  user: {
    id: number;
    username: string;
    email: string;
  };
  theme: {
    id: number;
    title: string;
    description: string;
  };
  createdAt?: string;
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
      themeId
    });
  }

  /**
   * Se désabonner d'un thème
   */
  unsubscribe(subscriptionId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${subscriptionId}`);
  }

  /**
   * Récupère les abonnements de l'utilisateur connecté
   */
  getUserSubscriptions(): Observable<Subscription[]> {
    return this.http.get<Subscription[]>(this.apiUrl);
  }

  /**
   * Vérifie si l'utilisateur est abonné à un thème
   */
  isSubscribed(themeId: number, subscriptions: Subscription[]): boolean {
    return subscriptions.some(sub => sub.theme.id === themeId);
  }

  /**
   * Trouve l'abonnement pour un thème donné
   */
  findSubscriptionByThemeId(themeId: number, subscriptions: Subscription[]): Subscription | undefined {
    return subscriptions.find(sub => sub.theme.id === themeId);
  }
}

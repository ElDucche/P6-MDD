import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ConfigService } from '../../core/services/config.service';
import { Subscription } from '../interfaces/subscription.interface';

@Injectable({
  providedIn: 'root'
})
export class SubscriptionService {
  private readonly http = inject(HttpClient);
  private readonly config = inject(ConfigService);

  /**
   * S'abonner à un thème
   */
  subscribe(themeId: number, userId: number): Observable<Subscription> {
    return this.http.post<Subscription>(this.config.endpoints.subscriptions.all, { themeId });
  }

  /**
   * Se désabonner d'un thème
   */
  unsubscribe(subscriptionId: number): Observable<void> {
    return this.http.delete<void>(this.config.endpoints.subscriptions.byId(subscriptionId));
  }

  /**
   * Récupère les abonnements de l'utilisateur connecté
   */
  getUserSubscriptions(): Observable<Subscription[]> {
    return this.http.get<Subscription[]>(this.config.endpoints.subscriptions.all);
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

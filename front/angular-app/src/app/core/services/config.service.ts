import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';

/**
 * Service de configuration centralisé pour l'application
 * 
 * Centralise les URLs et configurations pour éviter les duplications
 */
@Injectable({
  providedIn: 'root'
})
export class ConfigService {

  private readonly _apiUrl = environment.apiUrl;

  constructor() {
    // Nettoie l'URL API (enlève le slash final s'il existe)
    this._apiUrl = this._apiUrl.replace(/\/$/, '');
  }

  /**
   * URL de base de l'API
   */
  get apiUrl(): string {
    return this._apiUrl;
  }

  /**
   * URL complète pour un endpoint
   * @param path Chemin de l'endpoint (sans slash initial)
   */
  getApiEndpoint(path: string): string {
    const cleanPath = path.replace(/^\//, ''); // Enlève le slash initial s'il existe
    return `${this._apiUrl}/api/${cleanPath}`;
  }

  /**
   * URLs des endpoints principaux
   */
  get endpoints() {
    return {
      auth: {
        login: this.getApiEndpoint('auth/login'),
        register: this.getApiEndpoint('auth/register')
      },
      users: {
        me: this.getApiEndpoint('users/me'),
        byId: (id: number) => this.getApiEndpoint(`users/${id}`)
      },
      posts: {
        all: this.getApiEndpoint('posts'),
        subscribed: this.getApiEndpoint('posts/subscribed'),
        byTheme: (themeId: number) => this.getApiEndpoint(`posts/theme/${themeId}`),
        byId: (id: number) => this.getApiEndpoint(`posts/${id}`)
      },
      themes: {
        all: this.getApiEndpoint('themes')
      },
      subscriptions: {
        all: this.getApiEndpoint('subscriptions'),
        byId: (id: number) => this.getApiEndpoint(`subscriptions/${id}`)
      },
      comments: {
        all: this.getApiEndpoint('comments'),
        byPost: (postId: number) => this.getApiEndpoint(`comments/post/${postId}`)
      }
    };
  }

  /**
   * Configuration de l'environnement
   */
  get isProduction(): boolean {
    return environment.production;
  }

  /**
   * Configuration pour les logs (désactivés en production)
   */
  get enableLogs(): boolean {
    return !environment.production;
  }
}

import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ConfigService } from '../../core/services/config.service';
import { Theme } from '../interfaces/theme.interface';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private readonly http = inject(HttpClient);
  private readonly config = inject(ConfigService);

  /**
   * Récupère tous les thèmes disponibles
   */
  getAllThemes(): Observable<Theme[]> {
    return this.http.get<Theme[]>(this.config.endpoints.themes.all);
  }

  /**
   * Récupère un thème par son ID
   */
  getThemeById(id: number): Observable<Theme> {
    return this.http.get<Theme>(`${this.config.apiUrl}/api/themes/${id}`);
  }
}

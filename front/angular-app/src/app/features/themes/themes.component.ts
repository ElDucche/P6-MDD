import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ThemeService, Theme } from '../../services/theme.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-themes',
  templateUrl: './themes.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: []
})
export class ThemesComponent {
  private readonly themeService = inject(ThemeService);
  private readonly router = inject(Router);

  protected readonly themes = signal<Theme[]>([]);
  protected readonly isLoading = signal(false);

  constructor() {
    this.loadThemes();
  }

  private loadThemes(): void {
    this.isLoading.set(true);
    this.themeService.getAllThemes().subscribe({
      next: (themes: Theme[]) => {
        this.themes.set(themes);
        this.isLoading.set(false);
      },
      error: (error: any) => {
        console.error('Erreur lors du chargement des thèmes:', error);
        this.isLoading.set(false);
      }
    });
  }

  protected onThemeClick(theme: Theme): void {
    // Navigation vers les articles filtrés par thème
    this.router.navigate(['/articles'], { queryParams: { themeId: theme.id } });
  }
}

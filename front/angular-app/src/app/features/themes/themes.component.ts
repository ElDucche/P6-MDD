import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ThemeService, Theme } from '../../services/theme.service';
import { SubscriptionService, Subscription } from '../../services/subscription.service';
import { AuthService } from '../../auth/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-themes',
  templateUrl: './themes.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: []
})
export class ThemesComponent {
  private readonly themeService = inject(ThemeService);
  private readonly subscriptionService = inject(SubscriptionService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly themes = signal<Theme[]>([]);
  protected readonly subscriptions = signal<Subscription[]>([]);
  protected readonly isLoading = signal(false);
  protected readonly loadingSubscriptions = signal<Set<number>>(new Set());

  constructor() {
    this.loadThemes();
    this.loadUserSubscriptions();
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

  private loadUserSubscriptions(): void {
    this.subscriptionService.getUserSubscriptions().subscribe({
      next: (subscriptions: Subscription[]) => {
        this.subscriptions.set(subscriptions);
      },
      error: (error: any) => {
        console.error('Erreur lors du chargement des abonnements:', error);
      }
    });
  }

  protected isSubscribed(themeId: number): boolean {
    return this.subscriptionService.isSubscribed(themeId, this.subscriptions());
  }

  protected isSubscriptionLoading(themeId: number): boolean {
    return this.loadingSubscriptions().has(themeId);
  }

  protected subscribeToTheme(event: Event, theme: Theme): void {
    event.stopPropagation(); // Empêche la navigation vers les articles
    
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      console.error('Utilisateur non connecté');
      return;
    }

    // Ajouter le thème aux chargements en cours
    const loading = new Set(this.loadingSubscriptions());
    loading.add(theme.id);
    this.loadingSubscriptions.set(loading);

    // S'abonner uniquement
    this.subscriptionService.subscribe(theme.id, userId).subscribe({
      next: (newSubscription: Subscription) => {
        const updatedSubscriptions = [...this.subscriptions(), newSubscription];
        this.subscriptions.set(updatedSubscriptions);
        this.removeFromLoading(theme.id);
      },
      error: (error: any) => {
        console.error('Erreur lors de l\'abonnement:', error);
        this.removeFromLoading(theme.id);
      }
    });
  }

  private removeFromLoading(themeId: number): void {
    const loading = new Set(this.loadingSubscriptions());
    loading.delete(themeId);
    this.loadingSubscriptions.set(loading);
  }

  protected onThemeClick(theme: Theme): void {
    // Navigation vers les articles filtrés par thème
    this.router.navigate(['/articles'], { queryParams: { themeId: theme.id } });
  }
}

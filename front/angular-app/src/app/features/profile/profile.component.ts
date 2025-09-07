import { Component, OnInit, signal, computed } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import { UserService, SubscriptionService, ThemeService } from '@shared/services';
import { AuthService } from '../auth/auth.service';
import { AlertService } from '@core/services/alert.service';
import { User, Theme } from '@shared/interfaces';

// Interface pour les thèmes avec information d'abonnement
interface ThemeWithSubscription extends Theme {
  subscribedAt: Date;
}

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: [],
  imports: [ReactiveFormsModule, DatePipe]
})
export class ProfileComponent implements OnInit {
  protected readonly user = signal<User | undefined>(undefined);
  protected readonly isDeleteConfirmOpen = signal(false);
  protected readonly isLoading = signal(true);
  protected readonly isLoadingSubscriptions = signal(false);
  
  // Signal pour les thèmes abonnés - sera chargé depuis l'API
  protected readonly subscribedThemes = signal<ThemeWithSubscription[]>([]);
  
  protected editForm: FormGroup;

  // Computed pour l'avatar
  protected readonly userInitial = computed(() => {
    const currentUser = this.user();
    return currentUser?.username?.charAt(0).toUpperCase() || '';
  });

  constructor(
    private readonly userService: UserService,
    private readonly authService: AuthService,
    private readonly alertService: AlertService,
    private readonly subscriptionService: SubscriptionService,
    private readonly themeService: ThemeService,
    private readonly router: Router,
    private readonly fb: FormBuilder
  ) {
    // Initialiser le formulaire réactif
    this.editForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: [''] // Optionnel
    });
  }

  ngOnInit(): void {
    this.loadUserProfile();
  }

  private loadUserProfile(): void {
    this.isLoading.set(true);
    
    this.userService.getUser().subscribe({
      next: (user) => {
        this.user.set(user);
        // Mettre à jour le formulaire avec les données utilisateur
        this.editForm.patchValue({
          username: user?.username || '',
          email: user?.email || '',
          password: ''
        });
        
        // Charger les abonnements une fois que l'utilisateur est chargé
        this.loadUserSubscriptions();
        
        // Simulation d'un délai pour voir le loading (à retirer en production)
        setTimeout(() => {
          this.isLoading.set(false);
        }, 800);
      },
      error: (error: unknown) => {
        console.error('Erreur lors du chargement du profil:', error);
        this.alertService.showAlert({
          type: 'error',
          message: 'Impossible de charger le profil utilisateur'
        });
        this.isLoading.set(false);
      }
    });
  }

  private loadUserSubscriptions(): void {
    this.isLoadingSubscriptions.set(true);
    
    this.subscriptionService.getUserSubscriptions().subscribe({
      next: (subscriptions) => {
        if (subscriptions.length === 0) {
          this.subscribedThemes.set([]);
          this.isLoadingSubscriptions.set(false);
          return;
        }

        // Mapper les abonnements vers les thèmes avec la date d'abonnement
        const themesWithSubscription = subscriptions.map(subscription => ({
          ...subscription.theme,
          subscribedAt: subscription.createdAt ? new Date(subscription.createdAt) : new Date()
        } as ThemeWithSubscription));

        this.subscribedThemes.set(themesWithSubscription);
        this.isLoadingSubscriptions.set(false);
      },
      error: (error: unknown) => {
        console.error('Erreur lors du chargement des abonnements:', error);
        this.alertService.showAlert({
          type: 'error',
          message: 'Erreur lors du chargement de vos abonnements'
        });
        this.isLoadingSubscriptions.set(false);
      }
    });
  }

  protected updateProfile(): void {
    if (this.editForm.valid) {
      const formValue = this.editForm.value;
      
      // Préparer les données à envoyer
      const updateData: Partial<User & { password?: string }> = {
        username: formValue.username,
        email: formValue.email
      };

      // Ajouter le mot de passe seulement s'il est fourni
      if (formValue.password?.trim()) {
        updateData.password = formValue.password;
      }

      this.userService.updateUser(updateData).subscribe({
        next: (updatedUser) => {
          this.user.set(updatedUser);
          this.alertService.showAlert({
            type: 'success',
            message: 'Profil mis à jour avec succès. Vous allez être déconnecté pour actualiser votre session.'
          });
          
          // Déconnexion automatique après mise à jour pour régénérer le token
          setTimeout(() => {
            this.authService.logout();
            this.router.navigate(['/auth/login']);
          }, 2000); // Délai de 2 secondes pour que l'utilisateur puisse lire le message
        },
        error: (error: unknown) => {
          console.error('Erreur lors de la mise à jour:', error);
          this.alertService.showAlert({
            type: 'error',
            message: 'Erreur lors de la mise à jour du profil'
          });
        }
      });
    } else {
      this.alertService.showAlert({
        type: 'error',
        message: 'Veuillez corriger les erreurs du formulaire'
      });
    }
  }

  protected openDeleteConfirm(): void {
    this.isDeleteConfirmOpen.set(true);
  }

  protected closeDeleteConfirm(): void {
    this.isDeleteConfirmOpen.set(false);
  }

  protected deleteAccount(): void {
    this.userService.deleteUser().subscribe({
      next: () => {
        this.alertService.showAlert({
          type: 'success',
          message: 'Compte supprimé avec succès'
        });
        // Déconnexion et redirection
        this.authService.logout();
        this.router.navigate(['/auth/login']);
      },
      error: (error: unknown) => {
        console.error('Erreur lors de la suppression:', error);
        this.alertService.showAlert({
          type: 'error',
          message: 'Erreur lors de la suppression du compte'
        });
        this.closeDeleteConfirm();
      }
    });
  }

  /**
   * Se désabonner d'un thème
   */
  protected unsubscribeFromTheme(themeId: number): void {
    const currentThemes = this.subscribedThemes();
    const theme = currentThemes.find(t => t.id === themeId);
    const themeName = theme?.title || 'ce thème';

    // Trouver l'abonnement à supprimer pour récupérer son ID
    const subscriptionToDelete = currentThemes.find(t => t.id === themeId);
    if (!subscriptionToDelete) {
      this.alertService.showAlert({
        type: 'error',
        message: 'Abonnement introuvable'
      });
      return;
    }

    // Utiliser l'ID du thème comme ID d'abonnement pour l'instant
    // Note: Le backend devrait idéalement retourner l'ID réel de l'abonnement
    this.subscriptionService.unsubscribe(themeId).subscribe({
      next: () => {
        // Mettre à jour la liste locale
        const updatedThemes = currentThemes.filter(t => t.id !== themeId);
        this.subscribedThemes.set(updatedThemes);
        
        this.alertService.showAlert({
          type: 'success',
          message: `Vous vous êtes désabonné de "${themeName}"`
        });
      },
      error: (error: unknown) => {
        console.error('Erreur lors du désabonnement:', error);
        this.alertService.showAlert({
          type: 'error',
          message: 'Erreur lors du désabonnement'
        });
      }
    });
  }

  // Getters pour accéder aux contrôles du formulaire
  protected get usernameControl() { return this.editForm.get('username'); }
  protected get emailControl() { return this.editForm.get('email'); }
  protected get passwordControl() { return this.editForm.get('password'); }
}

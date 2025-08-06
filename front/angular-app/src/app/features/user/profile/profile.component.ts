import { Component, OnInit, signal, computed } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import { UserService } from '../user.service';
import { AuthService } from '../../../auth/auth.service';
import { AlertService } from '../../../core/services/alert.service';
import { User } from '../user.model';

// Interface temporaire pour les thèmes (en attendant le backend)
interface Theme {
  id: number;
  title: string;
  description: string;
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
  protected readonly isEditModalOpen = signal(false);
  protected readonly isDeleteConfirmOpen = signal(false);
  protected readonly isLoading = signal(true);
  
  // Signal pour les thèmes abonnés (placeholder)
  protected readonly subscribedThemes = signal<Theme[]>([
    {
      id: 1,
      title: 'Développement Web',
      description: 'Toutes les nouveautés sur le développement web moderne',
      subscribedAt: new Date('2024-12-15')
    },
    {
      id: 2,
      title: 'Intelligence Artificielle',
      description: 'Actualités et discussions sur l\'IA et le machine learning',
      subscribedAt: new Date('2024-12-10')
    },
    {
      id: 3,
      title: 'Design UX/UI',
      description: 'Tendances et bonnes pratiques en design d\'interface',
      subscribedAt: new Date('2024-12-05')
    }
  ]);
  
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

  protected openEditModal(): void {
    // Réinitialiser le formulaire avec les valeurs actuelles
    const currentUser = this.user();
    if (currentUser) {
      this.editForm.patchValue({
        username: currentUser.username,
        email: currentUser.email,
        password: ''
      });
    }
    this.isEditModalOpen.set(true);
  }

  protected closeEditModal(): void {
    this.isEditModalOpen.set(false);
    this.editForm.reset();
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
          this.closeEditModal();
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
    this.closeEditModal();
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
   * Se désabonner d'un thème (placeholder - en attendant le backend)
   */
  protected unsubscribeFromTheme(themeId: number): void {
    const currentThemes = this.subscribedThemes();
    const themeName = currentThemes.find(theme => theme.id === themeId)?.title || 'ce thème';
    
    // Simulation de l'appel API - À remplacer par un vrai service
    // Exemple: this.themeService.unsubscribe(themeId).subscribe(...)
    
    // Mise à jour locale des données (placeholder)
    const updatedThemes = currentThemes.filter(theme => theme.id !== themeId);
    this.subscribedThemes.set(updatedThemes);
    
    this.alertService.showAlert({
      type: 'success',
      message: `Vous vous êtes désabonné de "${themeName}"`
    });
  }

  // Getters pour accéder aux contrôles du formulaire
  protected get usernameControl() { return this.editForm.get('username'); }
  protected get emailControl() { return this.editForm.get('email'); }
  protected get passwordControl() { return this.editForm.get('password'); }
}

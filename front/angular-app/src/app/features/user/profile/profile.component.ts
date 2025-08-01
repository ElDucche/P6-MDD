import { Component, OnInit, signal, computed } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService } from '../user.service';
import { AuthService } from '../../../auth/auth.service';
import { AlertService } from '../../../core/services/alert.service';
import { User } from '../user.model';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: [],
  imports: [ReactiveFormsModule]
})
export class ProfileComponent implements OnInit {
  protected readonly user = signal<User | undefined>(undefined);
  protected readonly isEditModalOpen = signal(false);
  protected readonly isDeleteConfirmOpen = signal(false);
  
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
    this.userService.getUser().subscribe({
      next: (user) => {
        this.user.set(user);
        // Mettre à jour le formulaire avec les données utilisateur
        this.editForm.patchValue({
          username: user?.username || '',
          email: user?.email || '',
          password: ''
        });
      },
      error: (error: unknown) => {
        console.error('Erreur lors du chargement du profil:', error);
        this.alertService.showAlert({
          type: 'error',
          message: 'Impossible de charger le profil utilisateur'
        });
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
            message: 'Profil mis à jour avec succès'
          });
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

  // Getters pour accéder aux contrôles du formulaire
  protected get usernameControl() { return this.editForm.get('username'); }
  protected get emailControl() { return this.editForm.get('email'); }
  protected get passwordControl() { return this.editForm.get('password'); }
}

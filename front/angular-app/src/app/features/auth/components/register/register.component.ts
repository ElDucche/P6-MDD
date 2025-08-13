import { Component, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterModule
  ],
  templateUrl: './register.component.html',
})
export class RegisterComponent {
  form: FormGroup;
  protected readonly errorMessage = signal<string>('');
  protected readonly successMessage = signal<string>('');
  protected readonly isLoading = signal<boolean>(false);

  constructor(private readonly fb: FormBuilder, private readonly authService: AuthService, private readonly router: Router) {
    this.form = this.fb.group({
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8), Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/)]]
    });
  }

  onSubmit() {
    if (this.form.valid) {
      this.isLoading.set(true);
      this.errorMessage.set('');
      this.successMessage.set('');
      
      this.authService.register(this.form.value).subscribe({
        next: () => {
          this.isLoading.set(false);
          this.successMessage.set('Compte créé avec succès ! Redirection vers la connexion...');
          setTimeout(() => this.router.navigate(['/login']), 2000);
        },
        error: (err) => {
          this.isLoading.set(false);
          this.errorMessage.set(err.message || 'Erreur lors de la création du compte');
          console.error('Registration failed', err);
        }
      });
    }
  }
}

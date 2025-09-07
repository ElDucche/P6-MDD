/**
 * Composant de connexion par e-mail et mot de passe
*/
import { Component, signal } from '@angular/core';
import { Validators, ReactiveFormsModule, FormControl, FormGroup } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../auth.service';

@Component({
  selector: 'ngm-dev-block-login-email-password',
  templateUrl: './login.component.html',
  imports: [
    ReactiveFormsModule,
    RouterModule
  ],
  standalone: true,
})
export class LoginEmailPasswordComponent {
  form = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [
      Validators.required,
      Validators.minLength(8),
    ]),
  });

  protected readonly errorMessage = signal<string>('');
  protected readonly isLoading = signal<boolean>(false);

  constructor(private readonly authService: AuthService, private readonly router: Router) {}

  onSubmit() {
    if (this.form.valid) {
      this.isLoading.set(true);
      this.errorMessage.set('');
      
      this.authService.login(this.form.value).subscribe({
        next: () => {
          this.isLoading.set(false);
          this.router.navigate(['/home']);
        },
        error: (err) => {
          this.isLoading.set(false);
          this.errorMessage.set(err.message || 'Erreur de connexion');
          console.error('Login failed', err);
        },
      });
    }
  }
}

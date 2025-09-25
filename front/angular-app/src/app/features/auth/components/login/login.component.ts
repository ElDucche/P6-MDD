/**
 * Composant de connexion par identifiant (email ou username) et mot de passe
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
    identifier: new FormControl('', [Validators.required]),
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
      this.authService.login(this.form.value as {identifier: string; password: string}).subscribe({
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

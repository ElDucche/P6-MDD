/*
	Installed from https://ui.angular-material.dev/api/registry/
	Update this file using `@ngm-dev/cli update free-authentication/login-email-password`
*/

import { NgOptimizedImage } from '@angular/common';
import { Component } from '@angular/core';
import {
  MatCard,
  MatCardActions,
  MatCardContent,
} from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { ReactiveFormsModule } from '@angular/forms';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../../auth/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'ngm-dev-block-login-email-password',
  templateUrl: './login-email-password.component.html',
  imports: [
    MatCard,
    MatCardContent,
    MatCardActions,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    ReactiveFormsModule,
    MatIconModule,
    NgOptimizedImage,
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

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    if (this.form.valid) {
      this.authService.login(this.form.value).subscribe({
        next: () => {
          this.router.navigate(['/']);
        },
        error: (err) => {
          console.error('Login failed', err);
        },
      });
    }
  }
}

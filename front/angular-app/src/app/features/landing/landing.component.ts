import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-landing',
  templateUrl: './landing.component.html',
  standalone: true
})
export class LandingComponent {

  constructor(private readonly router: Router) {}

  /**
   * Navigation vers la page de connexion
   */
  goToLogin(): void {
    this.router.navigate(['/login']);
  }

  /**
   * Navigation vers la page d'inscription
   */
  goToRegister(): void {
    this.router.navigate(['/register']);
  }
}

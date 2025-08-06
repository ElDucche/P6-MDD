import { Component, inject } from '@angular/core';
import { Router, NavigationEnd, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AlertComponent } from './components/alert/alert.component';
import { AuthService } from './auth/auth.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, AlertComponent],
  templateUrl: './app.component.html',
  styleUrls: []
})
export class AppComponent {
  title = 'angular-app';
  showNavbar: boolean = true;

  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  constructor() {
    this.router.events.pipe(
      filter((event): event is NavigationEnd => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      this.showNavbar = !(event.url === '/login' || event.url === '/register');
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}

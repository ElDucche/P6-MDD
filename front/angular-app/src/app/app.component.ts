import { Component } from '@angular/core';
import { Router, NavigationEnd, RouterOutlet } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { SidebarComponent } from './layout/sidebar/sidebar.component';
import { filter } from 'rxjs/operators';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, MatSidenavModule, SidebarComponent],
  templateUrl: './app.component.html',
  styleUrls: []
})
export class AppComponent {
  title = 'angular-app';
  showSidebar: boolean = true;

  constructor(private router: Router) {
    this.router.events.pipe(
      filter((event): event is NavigationEnd => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      this.showSidebar = !(event.url === '/login' || event.url === '/register');
    });
  }
}

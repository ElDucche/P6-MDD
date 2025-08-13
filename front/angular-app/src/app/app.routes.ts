import { Routes } from '@angular/router';
import { 
  LoginEmailPasswordComponent, 
  RegisterComponent, 
  authGuard, 
  publicGuard 
} from '@features/auth';
import { HomeComponent } from './features/home/home.component';
import { ProfileComponent } from './features/profile/profile.component';

export const routes: Routes = [
    { path: 'login', component: LoginEmailPasswordComponent, canActivate: [publicGuard] },
    { path: 'register', component: RegisterComponent, canActivate: [publicGuard] },
    { path: '', component: HomeComponent, canActivate: [authGuard] },
    { path: 'themes', loadComponent: () => import('./features/themes/themes.component').then(m => m.ThemesComponent), canActivate: [authGuard] },
    { path: 'article/:id', loadComponent: () => import('./features/article/article.component').then(m => m.ArticleComponent), canActivate: [authGuard] },
    { path: 'profile', component: ProfileComponent },
    { path: '**', redirectTo: '/' } // Redirection pour les routes non trouvées
];

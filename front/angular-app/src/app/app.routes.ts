import { Routes } from '@angular/router';
import { 
  LoginEmailPasswordComponent, 
  RegisterComponent, 
  authGuard, 
  publicGuard 
} from '@features/auth';
import { HomeComponent } from './features/home/home.component';
import { ProfileComponent } from './features/profile/profile.component';
import { LandingComponent } from './features/landing/landing.component';

export const routes: Routes = [
    // Routes publiques (sans navbar)
    { path: '', component: LandingComponent, canActivate: [publicGuard] },
    { path: 'login', component: LoginEmailPasswordComponent, canActivate: [publicGuard] },
    { path: 'register', component: RegisterComponent, canActivate: [publicGuard] },
    
    // Routes protégées (avec navbar)
    { path: 'home', component: HomeComponent, canActivate: [authGuard] },
    { path: 'themes', loadComponent: () => import('./features/themes/themes.component').then(m => m.ThemesComponent), canActivate: [authGuard] },
    { path: 'article/:id', loadComponent: () => import('./features/article/article.component').then(m => m.ArticleComponent), canActivate: [authGuard] },
    { path: 'create-article', loadComponent: () => import('./features/create-article/create-article.component').then(m => m.CreateArticleComponent), canActivate: [authGuard] },
    { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
    
    // Fallback
    { path: '**', redirectTo: '/' }
];

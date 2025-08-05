import { Routes } from '@angular/router';
import { LoginEmailPasswordComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { authGuard } from './auth/guards/auth.guard';
import { publicGuard } from './auth/guards/public.guard';
import { HomeComponent } from './features/home/home.component';
import { ProfileComponent } from './features/user/profile/profile.component';

export const routes: Routes = [
    { path: 'login', component: LoginEmailPasswordComponent, canActivate: [publicGuard] },
    { path: 'register', component: RegisterComponent, canActivate: [publicGuard] },
    { path: '', component: HomeComponent, canActivate: [authGuard] },
    { path: 'themes', loadComponent: () => import('./features/themes/themes.component').then(m => m.ThemesComponent), canActivate: [authGuard] },
    { path: 'article/:id', loadComponent: () => import('./features/article/article.component').then(m => m.ArticleComponent), canActivate: [authGuard] },
    { path: 'profile', component: ProfileComponent },
    { path: '**', redirectTo: '/' } // Redirection pour les routes non trouvées
];

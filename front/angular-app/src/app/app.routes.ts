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
    { path: 'home', component: HomeComponent, canActivate: [authGuard] },
    { path: 'profile', component: ProfileComponent },
    { path: '', redirectTo: '/home', pathMatch: 'full' },
    { path: '**', redirectTo: '/home' } // Redirection pour les routes non trouvées
];

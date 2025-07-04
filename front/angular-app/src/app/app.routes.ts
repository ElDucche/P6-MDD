import { Routes } from '@angular/router';
import { LoginEmailPasswordComponent } from './components/free-authentication/login-email-password/login-email-password.component';
import { RegisterComponent } from './auth/register/register.component';

export const routes: Routes = [
    { path: 'login', component: LoginEmailPasswordComponent },
    { path: 'register', component: RegisterComponent },
    { path: '', redirectTo: '/login', pathMatch: 'full' }
];

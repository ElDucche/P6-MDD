/**
 * Barrel export pour le module auth
 * 
 * Permet d'importer facilement les éléments d'authentification :
 * import { AuthService, authGuard, LoginEmailPasswordComponent } from '@features/auth';
 */

// Service d'authentification
export * from './auth.service';

// Guards
export * from './guards/auth.guard';
export * from './guards/public.guard';

// Composants
export * from './components/login/login.component';
export * from './components/register/register.component';

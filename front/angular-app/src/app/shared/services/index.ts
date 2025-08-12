/**
 * Barrel export pour tous les services métier partagés
 * 
 * Permet d'importer facilement plusieurs services :
 * import { PostService, ThemeService, UserService } from '@shared/services';
 */

// Services métier
export * from './post.service';
export * from './theme.service';
export * from './comment.service';
export * from './subscription.service';
export * from './user.service';
export * from './notification.service';

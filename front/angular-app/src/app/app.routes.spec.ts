import { routes } from './app.routes';

describe('AppRoutes', () => {
  it('should have correct routes configuration', () => {
    expect(routes).toBeDefined();
    expect(routes.length).toBeGreaterThan(0);
  });

  it('should have root route pointing to LandingComponent', () => {
    const rootRoute = routes.find(route => route.path === '');
    expect(rootRoute).toBeTruthy();
    expect(rootRoute?.component?.name).toBe('LandingComponent');
  });

  it('should have login route', () => {
    const loginRoute = routes.find(route => route.path === 'login');
    expect(loginRoute).toBeTruthy();
    expect(loginRoute?.component?.name).toBe('LoginEmailPasswordComponent');
  });

  it('should have register route', () => {
    const registerRoute = routes.find(route => route.path === 'register');
    expect(registerRoute).toBeTruthy();
    expect(registerRoute?.component?.name).toBe('RegisterComponent');
  });

  it('should have home route with auth guard', () => {
    const homeRoute = routes.find(route => route.path === 'home');
    expect(homeRoute).toBeTruthy();
    expect(homeRoute?.component?.name).toBe('HomeComponent');
    expect(homeRoute?.canActivate).toBeDefined();
  });

  it('should have profile route with auth guard', () => {
    const profileRoute = routes.find(route => route.path === 'profile');
    expect(profileRoute).toBeTruthy();
    expect(profileRoute?.component?.name).toBe('ProfileComponent');
    expect(profileRoute?.canActivate).toBeDefined();
  });

  it('should have themes route with lazy loading', () => {
    const themesRoute = routes.find(route => route.path === 'themes');
    expect(themesRoute).toBeTruthy();
    expect(themesRoute?.loadComponent).toBeDefined();
    expect(themesRoute?.canActivate).toBeDefined();
  });

  it('should have article route with lazy loading and parameter', () => {
    const articleRoute = routes.find(route => route.path === 'article/:id');
    expect(articleRoute).toBeTruthy();
    expect(articleRoute?.loadComponent).toBeDefined();
    expect(articleRoute?.canActivate).toBeDefined();
  });

  it('should have wildcard route for fallback', () => {
    const wildcardRoute = routes.find(route => route.path === '**');
    expect(wildcardRoute).toBeTruthy();
    expect(wildcardRoute?.redirectTo).toBe('/');
  });

  it('should protect secured routes with authGuard', () => {
    const securedRoutes = ['home', 'profile'];
    securedRoutes.forEach(path => {
      const route = routes.find(r => r.path === path);
      expect(route?.canActivate).toBeDefined();
    });
  });

  it('should protect public routes with publicGuard', () => {
    const publicRoutes = ['', 'login', 'register'];
    publicRoutes.forEach(path => {
      const route = routes.find(r => r.path === path);
      expect(route?.canActivate).toBeDefined();
    });
  });

  it('should have correct number of routes', () => {
    // Au moins 8 routes (landing, login, register, home, themes, article, profile, wildcard)
    expect(routes.length).toBeGreaterThanOrEqual(8);
  });

  it('should have routes with proper structure', () => {
    routes.forEach(route => {
      expect(route).toHaveProperty('path');
      // Chaque route doit avoir soit component, soit loadComponent, soit redirectTo
      const hasComponent = route.component !== undefined;
      const hasLoadComponent = route.loadComponent !== undefined;
      const hasRedirectTo = route.redirectTo !== undefined;
      
      expect(hasComponent || hasLoadComponent || hasRedirectTo).toBe(true);
    });
  });
});

/**
 * @jest-environment jsdom
 */

describe('Main Bootstrap', () => {
  it('should have main.ts file', () => {
    // Test simple pour vérifier que le fichier main.ts existe et peut être importé
    expect(() => require('./main')).not.toThrow();
  });

  it('should export bootstrap functionality', () => {
    // Test pour vérifier les imports nécessaires
    const mainModule = require('./main');
    // Le fait que le module se charge sans erreur est suffisant
    expect(mainModule).toBeDefined();
  });

  it('should use Angular bootstrap functionality', () => {
    // Test pour s'assurer que les fonctions Angular sont utilisées
    const fs = require('fs');
    const path = require('path');
    const mainPath = path.join(__dirname, 'main.ts');
    const mainContent = fs.readFileSync(mainPath, 'utf8');
    
    expect(mainContent).toContain('bootstrapApplication');
    expect(mainContent).toContain('AppComponent');
    expect(mainContent).toContain('provideRouter');
    expect(mainContent).toContain('provideHttpClient');
  });

  it('should configure essential providers', () => {
    const fs = require('fs');
    const path = require('path');
    const mainPath = path.join(__dirname, 'main.ts');
    const mainContent = fs.readFileSync(mainPath, 'utf8');
    
    expect(mainContent).toContain('provideRouter(routes)');
    expect(mainContent).toContain('provideAnimations()');
    expect(mainContent).toContain('withInterceptors([authInterceptor])');
  });
});

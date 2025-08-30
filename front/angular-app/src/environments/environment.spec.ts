import { environment } from './environment';

describe('Environment Development', () => {
  it('should have development configuration', () => {
    expect(environment).toBeDefined();
    expect(environment.production).toBe(false);
  });

  it('should have apiUrl defined', () => {
    expect(environment.apiUrl).toBeDefined();
    expect(typeof environment.apiUrl).toBe('string');
  });

  it('should have correct structure', () => {
    expect(environment).toHaveProperty('production');
    expect(environment).toHaveProperty('apiUrl');
  });

  it('should have localhost apiUrl for development', () => {
    expect(environment.apiUrl).toContain('localhost');
  });

  it('should have boolean production flag', () => {
    expect(typeof environment.production).toBe('boolean');
  });
});

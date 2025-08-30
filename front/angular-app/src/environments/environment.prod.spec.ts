import { environment } from './environment.prod';

describe('Environment Production', () => {
  it('should have production configuration', () => {
    expect(environment).toBeDefined();
    expect(environment.production).toBe(true);
  });

  it('should have apiUrl defined', () => {
    expect(environment.apiUrl).toBeDefined();
    expect(typeof environment.apiUrl).toBe('string');
  });

  it('should have correct structure', () => {
    expect(environment).toHaveProperty('production');
    expect(environment).toHaveProperty('apiUrl');
  });

  it('should have production flag set to true', () => {
    expect(environment.production).toBe(true);
  });

  it('should have boolean production flag', () => {
    expect(typeof environment.production).toBe('boolean');
  });

  it('should have valid apiUrl format', () => {
    expect(environment.apiUrl).toMatch(/^https?:\/\/.+/);
  });
});

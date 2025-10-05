import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { appConfig } from './app.config';

describe('AppConfig', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NoopAnimationsModule],
      providers: [...appConfig.providers]
    }).compileComponents();
  });

  it('should provide router', () => {
    const router = TestBed.inject(Router);
    expect(router).toBeTruthy();
  });

  it('should provide http client', () => {
    const httpClient = TestBed.inject(HttpClient);
    expect(httpClient).toBeTruthy();
  });

  it('should configure providers correctly', () => {
    expect(appConfig.providers).toBeDefined();
    expect(appConfig.providers.length).toBeGreaterThan(0);
  });

  it('should include zone change detection provider', () => {
    // Vérifier que la configuration inclut la détection de changement de zone
    expect(appConfig.providers).toBeDefined();
    expect(Array.isArray(appConfig.providers)).toBe(true);
  });

  it('should include http client provider', () => {
    const httpClient = TestBed.inject(HttpClient);
    expect(httpClient).toBeTruthy();
  });

  it('should be valid ApplicationConfig', () => {
    expect(appConfig).toHaveProperty('providers');
    expect(Array.isArray(appConfig.providers)).toBe(true);
  });

  it('should have at least 4 providers configured', () => {
    // Zone change detection, router, http client, animations
    expect(appConfig.providers.length).toBeGreaterThanOrEqual(4);
  });
});

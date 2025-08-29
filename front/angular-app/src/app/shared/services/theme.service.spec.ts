import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ThemeService } from './theme.service';
import { ConfigService } from '../../core/services/config.service';
import { Theme } from '../interfaces/theme.interface';

describe('ThemeService', () => {
  let service: ThemeService;
  let httpMock: HttpTestingController;
  let mockConfigService: any;

  const mockTheme: Theme = {
    id: 1,
    title: 'Angular',
    description: 'Angular development discussions and tutorials',
    createdAt: '2024-01-01T10:00:00Z',
    updatedAt: '2024-01-01T10:00:00Z'
  };

  const mockThemes: Theme[] = [
    mockTheme,
    {
      id: 2,
      title: 'React',
      description: 'React development discussions and tutorials',
      createdAt: '2024-01-01T10:00:00Z',
      updatedAt: '2024-01-01T10:00:00Z'
    },
    {
      id: 3,
      title: 'Vue.js',
      description: 'Vue.js development discussions and tutorials',
      createdAt: '2024-01-01T10:00:00Z',
      updatedAt: '2024-01-01T10:00:00Z'
    }
  ];

  const mockEndpoints = {
    themes: {
      all: '/api/themes'
    }
  };

  beforeEach(() => {
    mockConfigService = {
      endpoints: mockEndpoints,
      apiUrl: 'http://localhost:8080' // Used by getThemeById
    };

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        ThemeService,
        { provide: ConfigService, useValue: mockConfigService }
      ]
    });

    service = TestBed.inject(ThemeService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getAllThemes', () => {
    it('should retrieve all themes', () => {
      service.getAllThemes().subscribe(themes => {
        expect(themes).toEqual(mockThemes);
        expect(themes.length).toBe(3);
        expect(themes[0].title).toBe('Angular');
        expect(themes[1].title).toBe('React');
        expect(themes[2].title).toBe('Vue.js');
      });

      const req = httpMock.expectOne('/api/themes');
      expect(req.request.method).toBe('GET');
      req.flush(mockThemes);
    });

    it('should handle empty themes array', () => {
      service.getAllThemes().subscribe(themes => {
        expect(themes).toEqual([]);
        expect(themes.length).toBe(0);
      });

      const req = httpMock.expectOne('/api/themes');
      req.flush([]);
    });

    it('should handle server error when getting all themes', () => {
      service.getAllThemes().subscribe({
        next: () => fail('should have failed with server error'),
        error: (error) => {
          expect(error.status).toBe(500);
        }
      });

      const req = httpMock.expectOne('/api/themes');
      req.flush({}, { status: 500, statusText: 'Internal Server Error' });
    });

    it('should handle unauthorized access to themes', () => {
      service.getAllThemes().subscribe({
        next: () => fail('should have failed with unauthorized error'),
        error: (error) => {
          expect(error.status).toBe(401);
        }
      });

      const req = httpMock.expectOne('/api/themes');
      req.flush({}, { status: 401, statusText: 'Unauthorized' });
    });

    it('should use correct endpoint from config service', () => {
      service.getAllThemes().subscribe();

      const req = httpMock.expectOne('/api/themes');
      expect(req.request.url).toBe(mockConfigService.endpoints.themes.all);
    });

    it('should handle large number of themes', () => {
      const manyThemes: Theme[] = Array.from({ length: 100 }, (_, i) => ({
        id: i + 1,
        title: `Theme ${i + 1}`,
        description: `Description for theme ${i + 1}`,
        createdAt: '2024-01-01T10:00:00Z',
        updatedAt: '2024-01-01T10:00:00Z'
      }));

      service.getAllThemes().subscribe(themes => {
        expect(themes.length).toBe(100);
        expect(themes[0].title).toBe('Theme 1');
        expect(themes[99].title).toBe('Theme 100');
      });

      const req = httpMock.expectOne('/api/themes');
      req.flush(manyThemes);
    });
  });

  describe('getThemeById', () => {
    it('should retrieve specific theme by ID', () => {
      const themeId = 1;

      service.getThemeById(themeId).subscribe(theme => {
        expect(theme).toEqual(mockTheme);
        expect(theme.id).toBe(themeId);
        expect(theme.title).toBe('Angular');
        expect(theme.description).toBe('Angular development discussions and tutorials');
      });

      const req = httpMock.expectOne('http://localhost:8080/api/themes/1');
      expect(req.request.method).toBe('GET');
      req.flush(mockTheme);
    });

    it('should handle theme not found', () => {
      const themeId = 999;

      service.getThemeById(themeId).subscribe({
        next: () => fail('should have failed with not found error'),
        error: (error) => {
          expect(error.status).toBe(404);
          expect(error.error.message).toBe('Theme not found');
        }
      });

      const req = httpMock.expectOne('http://localhost:8080/api/themes/999');
      req.flush(
        { message: 'Theme not found' }, 
        { status: 404, statusText: 'Not Found' }
      );
    });

    it('should handle invalid theme ID (negative)', () => {
      const themeId = -1;

      service.getThemeById(themeId).subscribe({
        next: () => fail('should have failed with bad request'),
        error: (error) => {
          expect(error.status).toBe(400);
        }
      });

      const req = httpMock.expectOne('http://localhost:8080/api/themes/-1');
      req.flush({}, { status: 400, statusText: 'Bad Request' });
    });

    it('should handle invalid theme ID (zero)', () => {
      const themeId = 0;

      service.getThemeById(themeId).subscribe({
        next: () => fail('should have failed with bad request'),
        error: (error) => {
          expect(error.status).toBe(400);
        }
      });

      const req = httpMock.expectOne('http://localhost:8080/api/themes/0');
      req.flush({}, { status: 400, statusText: 'Bad Request' });
    });

    it('should use correct endpoint with theme ID and base URL', () => {
      const themeId = 42;

      service.getThemeById(themeId).subscribe();

      const req = httpMock.expectOne('http://localhost:8080/api/themes/42');
      expect(req.request.url).toBe('http://localhost:8080/api/themes/42');
    });

    it('should handle unauthorized access to specific theme', () => {
      const themeId = 1;

      service.getThemeById(themeId).subscribe({
        next: () => fail('should have failed with unauthorized error'),
        error: (error) => {
          expect(error.status).toBe(401);
        }
      });

      const req = httpMock.expectOne('http://localhost:8080/api/themes/1');
      req.flush({}, { status: 401, statusText: 'Unauthorized' });
    });

    it('should handle server error for specific theme', () => {
      const themeId = 1;

      service.getThemeById(themeId).subscribe({
        next: () => fail('should have failed with server error'),
        error: (error) => {
          expect(error.status).toBe(500);
        }
      });

      const req = httpMock.expectOne('http://localhost:8080/api/themes/1');
      req.flush({}, { status: 500, statusText: 'Internal Server Error' });
    });
  });

  describe('Integration scenarios', () => {
    it('should handle network errors', () => {
      const networkError = new ProgressEvent('error');

      service.getAllThemes().subscribe({
        next: () => fail('should have failed with network error'),
        error: (error) => {
          expect(error.error).toBe(networkError);
        }
      });

      const req = httpMock.expectOne('/api/themes');
      req.error(networkError);
    });

    it('should handle malformed theme response', () => {
      const malformedTheme = {
        id: 1,
        title: 'Valid Title'
        // Missing required fields: description, createdAt, updatedAt
      };

      service.getThemeById(1).subscribe(theme => {
        expect(theme.id).toBe(1);
        expect(theme.title).toBe('Valid Title');
        expect(theme.description).toBeUndefined();
        expect(theme.createdAt).toBeUndefined();
        expect(theme.updatedAt).toBeUndefined();
      });

      const req = httpMock.expectOne('http://localhost:8080/api/themes/1');
      req.flush(malformedTheme);
    });

    it('should use config service correctly for different endpoints', () => {
      // Test both endpoints to ensure config service integration
      service.getAllThemes().subscribe();
      service.getThemeById(1).subscribe();

      const requests = httpMock.match(() => true);
      expect(requests.length).toBe(2);
      expect(requests[0].request.url).toBe('/api/themes');
      expect(requests[1].request.url).toBe('http://localhost:8080/api/themes/1');

      requests.forEach(req => req.flush(mockTheme));
    });

    it('should handle themes with special characters in title and description', () => {
      const specialTheme: Theme = {
        id: 1,
        title: 'Angular & React 2024 🚀',
        description: 'Développement avec des caractères spéciaux: éàùö & symbols',
        createdAt: '2024-01-01T10:00:00Z',
        updatedAt: '2024-01-01T10:00:00Z'
      };

      service.getThemeById(1).subscribe(theme => {
        expect(theme.title).toBe('Angular & React 2024 🚀');
        expect(theme.description).toBe('Développement avec des caractères spéciaux: éàùö & symbols');
      });

      const req = httpMock.expectOne('http://localhost:8080/api/themes/1');
      req.flush(specialTheme);
    });

    it('should handle themes with very long descriptions', () => {
      const longDescription = 'A'.repeat(2000); // Very long description
      const themeWithLongDescription: Theme = {
        id: 1,
        title: 'Theme with long description',
        description: longDescription,
        createdAt: '2024-01-01T10:00:00Z',
        updatedAt: '2024-01-01T10:00:00Z'
      };

      service.getThemeById(1).subscribe(theme => {
        expect(theme.description).toBe(longDescription);
        expect(theme.description.length).toBe(2000);
      });

      const req = httpMock.expectOne('http://localhost:8080/api/themes/1');
      req.flush(themeWithLongDescription);
    });
  });
});

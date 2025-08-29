import { TestBed } from '@angular/core/testing';
import { HttpRequest, HttpHandlerFn, HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { AuthService } from '../../features/auth/auth.service';
import { AlertService } from '../services/alert.service';
import { authInterceptor } from './auth-interceptor';
import { of, throwError } from 'rxjs';
import { Injector, runInInjectionContext } from '@angular/core';

describe('AuthInterceptor', () => {
  let authService: any;
  let alertService: any;
  let injector: Injector;

  beforeEach(() => {
    const authSpy = {
      getToken: jest.fn(),
      logout: jest.fn()
    };

    const alertSpy = {
      showAlert: jest.fn()
    };

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        { provide: AuthService, useValue: authSpy },
        { provide: AlertService, useValue: alertSpy }
      ]
    });

    authService = TestBed.inject(AuthService);
    alertService = TestBed.inject(AlertService);
    injector = TestBed.inject(Injector);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('Token Injection', () => {
    it('should add authorization header when token exists', () => {
      const testToken = 'test-jwt-token';
      authService.getToken.mockReturnValue(testToken);

      const mockRequest = new HttpRequest('GET', '/api/test');
      const mockHandler = jest.fn().mockReturnValue(of(new HttpResponse()));

      // Execute interceptor within injection context
      runInInjectionContext(injector, () => {
        authInterceptor(mockRequest, mockHandler).subscribe();
      });

      // Verify handler was called
      expect(mockHandler).toHaveBeenCalled();
      
      // Verify the request has authorization header
      const calledRequest = (mockHandler as any).mock.calls[0][0] as HttpRequest<any>;
      expect(calledRequest.headers.get('Authorization')).toBe(`Bearer ${testToken}`);
    });

    it('should not add authorization header when no token exists', () => {
      authService.getToken.mockReturnValue(null);

      const mockRequest = new HttpRequest('GET', '/api/test');
      const mockHandler = jest.fn().mockReturnValue(of(new HttpResponse()));

      // Execute interceptor within injection context
      runInInjectionContext(injector, () => {
        authInterceptor(mockRequest, mockHandler).subscribe();
      });

      // Verify handler was called with original request (no auth header)
      expect(mockHandler).toHaveBeenCalledWith(mockRequest);
    });
  });

  describe('Error Handling', () => {
    it('should handle 401 errors by logging out and showing alert', () => {
      authService.getToken.mockReturnValue('test-token');

      const mockRequest = new HttpRequest('GET', '/api/test');
      const mockError = new HttpErrorResponse({
        status: 401,
        statusText: 'Unauthorized'
      });
      const mockHandler = jest.fn().mockReturnValue(throwError(() => mockError));

      // Execute interceptor and expect error
      runInInjectionContext(injector, () => {
        authInterceptor(mockRequest, mockHandler).subscribe({
          next: () => fail('Should have thrown an error'),
          error: (error) => {
            expect(error.message).toBe('Une erreur est survenue');
            expect(authService.logout).toHaveBeenCalled();
            expect(alertService.showAlert).toHaveBeenCalledWith({
              type: 'error',
              message: 'Session expirée. Veuillez vous reconnecter.'
            });
          }
        });
      });
    });

    it('should handle errors with message from error.error.message', () => {
      authService.getToken.mockReturnValue('test-token');

      const mockRequest = new HttpRequest('GET', '/api/test');
      const mockError = new HttpErrorResponse({
        status: 400,
        error: { message: 'Validation error' }
      });
      const mockHandler = jest.fn().mockReturnValue(throwError(() => mockError));

      // Execute interceptor and expect error
      runInInjectionContext(injector, () => {
        authInterceptor(mockRequest, mockHandler).subscribe({
          next: () => fail('Should have thrown an error'),
          error: (error) => {
            expect(error.message).toBe('Validation error');
            expect(authService.logout).not.toHaveBeenCalled();
            expect(alertService.showAlert).not.toHaveBeenCalled();
          }
        });
      });
    });

    it('should use default error message when no specific message is available', () => {
      authService.getToken.mockReturnValue('test-token');

      const mockRequest = new HttpRequest('GET', '/api/test');
      const mockError = new HttpErrorResponse({
        status: 500
      });
      const mockHandler = jest.fn().mockReturnValue(throwError(() => mockError));

      // Execute interceptor and expect error
      runInInjectionContext(injector, () => {
        authInterceptor(mockRequest, mockHandler).subscribe({
          next: () => fail('Should have thrown an error'),
          error: (error) => {
            expect(error.message).toBe('Une erreur est survenue');
          }
        });
      });
    });
  });

  describe('Successful Requests', () => {
    it('should pass through successful requests unchanged', () => {
      const testToken = 'test-token';
      authService.getToken.mockReturnValue(testToken);

      const mockRequest = new HttpRequest('GET', '/api/test');
      const mockResponse = new HttpResponse({ body: { data: 'test' } });
      const mockHandler = jest.fn().mockReturnValue(of(mockResponse));

      // Execute interceptor
      runInInjectionContext(injector, () => {
        authInterceptor(mockRequest, mockHandler).subscribe(response => {
          expect(response).toBe(mockResponse);
        });
      });

      expect(mockHandler).toHaveBeenCalled();
    });
  });
});

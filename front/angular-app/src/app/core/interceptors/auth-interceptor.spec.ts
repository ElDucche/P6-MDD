import { TestBed } from '@angular/core/testing';
import { HttpRequest, HttpHandlerFn, HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { AlertService } from '../services/alert.service';
import { authInterceptor } from './auth-interceptor';
import { of, throwError } from 'rxjs';
import { Injector, runInInjectionContext } from '@angular/core';

describe('AuthInterceptor', () => {
  let alertService: any;
  let router: any;
  let injector: Injector;

  beforeEach(() => {
    const alertSpy = {
      showAlert: jest.fn()
    };

    const routerSpy = {
      navigate: jest.fn()
    };

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        { provide: AlertService, useValue: alertSpy },
        { provide: Router, useValue: routerSpy }
      ]
    });

    alertService = TestBed.inject(AlertService);
    router = TestBed.inject(Router);
    injector = TestBed.inject(Injector);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('Cookie Configuration', () => {
    it('should set withCredentials to true for all requests', () => {
      const mockRequest = new HttpRequest('GET', '/api/test');
      const mockHandler = jest.fn().mockReturnValue(of(new HttpResponse()));

      // Execute interceptor within injection context
      runInInjectionContext(injector, () => {
        authInterceptor(mockRequest, mockHandler).subscribe();
      });

      // Verify handler was called
      expect(mockHandler).toHaveBeenCalled();
      
      // Verify the request has withCredentials set to true
      const calledRequest = (mockHandler as any).mock.calls[0][0] as HttpRequest<any>;
      expect(calledRequest.withCredentials).toBe(true);
    });

    it('should clone request with withCredentials enabled', () => {
      const mockRequest = new HttpRequest('POST', '/api/test', { data: 'test' });
      const mockHandler = jest.fn().mockReturnValue(of(new HttpResponse()));

      // Execute interceptor within injection context
      runInInjectionContext(injector, () => {
        authInterceptor(mockRequest, mockHandler).subscribe();
      });

      // Verify the cloned request maintains withCredentials
      const calledRequest = (mockHandler as any).mock.calls[0][0] as HttpRequest<any>;
      expect(calledRequest.withCredentials).toBe(true);
      expect(calledRequest.body).toEqual({ data: 'test' });
    });
  });

  describe('Error Handling', () => {
    it('should handle 401 errors by redirecting to login and showing alert', () => {
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
            expect(router.navigate).toHaveBeenCalledWith(['/auth/login']);
            expect(alertService.showAlert).toHaveBeenCalledWith({
              type: 'error',
              message: 'Session expirée. Veuillez vous reconnecter.'
            });
          }
        });
      });
    });

    it('should handle errors with message from error.error.message', () => {
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
            expect(router.navigate).not.toHaveBeenCalled();
            expect(alertService.showAlert).not.toHaveBeenCalled();
          }
        });
      });
    });

    it('should use default error message when no specific message is available', () => {
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
    it('should pass through successful requests with withCredentials', () => {
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
      
      // Verify withCredentials was set
      const calledRequest = (mockHandler as any).mock.calls[0][0] as HttpRequest<any>;
      expect(calledRequest.withCredentials).toBe(true);
    });
  });
});

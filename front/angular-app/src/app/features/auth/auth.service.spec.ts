import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService, LoginResponse, CurrentUser } from './auth.service';
import { ConfigService } from '../../core/services/config.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let configService: any;

  // Données de test
  const mockLoginCredentials = {
    email: 'test@example.com',
    password: 'password123'
  };

  const mockLoginResponse: LoginResponse = {
    token: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoidGVzdHVzZXIiLCJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaWF0IjoxNjM5NzE0NDAwLCJleHAiOjE2Mzk4MDA4MDB9.test-signature',
    message: 'Connexion réussie'
  };

  const mockRegisterData = {
    username: 'testuser',
    email: 'test@example.com',
    password: 'password123'
  };

  const mockExpectedUser: CurrentUser = {
    userId: 1,
    username: 'testuser',
    email: 'test@example.com'
  };

  beforeEach(() => {
    const configSpy = {
      endpoints: {
        auth: {
          login: '/api/auth/login',
          register: '/api/auth/register'
        }
      }
    };

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        AuthService,
        { provide: ConfigService, useValue: configSpy }
      ]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    configService = TestBed.inject(ConfigService);

    // Clear localStorage before each test and setup mock
    Object.defineProperty(window, 'localStorage', {
      value: {
        getItem: jest.fn(),
        setItem: jest.fn(),
        removeItem: jest.fn(),
        clear: jest.fn(),
      },
      writable: true,
    });
  });

  afterEach(() => {
    httpMock.verify();
    jest.clearAllMocks();
  });

  describe('Service Initialization', () => {
    it('should be created', () => {
      expect(service).toBeTruthy();
    });
  });

  describe('Login', () => {
    it('should login successfully and store token', () => {
      service.login(mockLoginCredentials).subscribe(response => {
        expect(response).toEqual(mockLoginResponse);
        expect(localStorage.setItem).toHaveBeenCalledWith('token', mockLoginResponse.token);
      });

      const req = httpMock.expectOne('/api/auth/login');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockLoginCredentials);
      req.flush(mockLoginResponse);
    });

    it('should throw error when login fails with no token', () => {
      const errorResponse: LoginResponse = {
        token: null,
        message: 'Identifiants invalides'
      };

      service.login(mockLoginCredentials).subscribe({
        next: () => fail('Should have thrown an error'),
        error: (error) => {
          expect(error.message).toBe('Identifiants invalides');
          expect(localStorage.setItem).not.toHaveBeenCalled();
        }
      });

      const req = httpMock.expectOne('/api/auth/login');
      req.flush(errorResponse);
    });

    it('should throw default error message when no message provided', () => {
      const errorResponse: LoginResponse = {
        token: null,
        message: ''
      };

      service.login(mockLoginCredentials).subscribe({
        next: () => fail('Should have thrown an error'),
        error: (error) => {
          expect(error.message).toBe('Erreur de connexion');
        }
      });

      const req = httpMock.expectOne('/api/auth/login');
      req.flush(errorResponse);
    });
  });

  describe('Register', () => {
    it('should register user successfully', () => {
      const mockResponse = 'Utilisateur créé avec succès';

      service.register(mockRegisterData).subscribe(response => {
        expect(response).toBe(mockResponse);
      });

      const req = httpMock.expectOne('/api/auth/register');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockRegisterData);
      req.flush(mockResponse);
    });

    it('should handle registration error', () => {
      const errorMessage = 'Email déjà utilisé';

      service.register(mockRegisterData).subscribe({
        next: () => fail('Should have failed'),
        error: (error) => {
          expect(error.status).toBe(400);
        }
      });

      const req = httpMock.expectOne('/api/auth/register');
      req.flush({ message: errorMessage }, { status: 400, statusText: 'Bad Request' });
    });
  });

  describe('Token Management', () => {
    it('should logout and remove token', () => {
      (localStorage.setItem as jest.Mock).mockImplementation();
      (localStorage.removeItem as jest.Mock).mockImplementation();
      
      service.logout();
      
      expect(localStorage.removeItem).toHaveBeenCalledWith('token');
    });

    it('should get token from localStorage', () => {
      const testToken = 'test-token-123';
      (localStorage.getItem as jest.Mock).mockReturnValue(testToken);
      
      const result = service.getToken();
      
      expect(localStorage.getItem).toHaveBeenCalledWith('token');
      expect(result).toBe(testToken);
    });

    it('should return null when no token exists', () => {
      (localStorage.getItem as jest.Mock).mockReturnValue(null);
      
      const result = service.getToken();
      
      expect(result).toBeNull();
    });

    it('should return true when user is logged in', () => {
      (localStorage.getItem as jest.Mock).mockReturnValue('test-token');
      
      const result = service.isLoggedIn();
      
      expect(result).toBe(true);
    });

    it('should return false when user is not logged in', () => {
      (localStorage.getItem as jest.Mock).mockReturnValue(null);
      
      const result = service.isLoggedIn();
      
      expect(result).toBe(false);
    });
  });

  describe('JWT Decoding', () => {
    it('should decode JWT token and return current user', () => {
      (localStorage.getItem as jest.Mock).mockReturnValue(mockLoginResponse.token);
      
      const result = service.getCurrentUser();
      
      expect(result).toEqual(mockExpectedUser);
    });

    it('should return null when no token exists', () => {
      (localStorage.getItem as jest.Mock).mockReturnValue(null);
      
      const result = service.getCurrentUser();
      
      expect(result).toBeNull();
    });

    it('should return null when token is invalid', () => {
      (localStorage.getItem as jest.Mock).mockReturnValue('invalid.token.format');
      
      const result = service.getCurrentUser();
      
      expect(result).toBeNull();
    });

    it('should handle malformed JWT token gracefully', () => {
      (localStorage.getItem as jest.Mock).mockReturnValue('malformed-token');
      
      const result = service.getCurrentUser();
      
      expect(result).toBeNull();
    });

    it('should get current user ID', () => {
      (localStorage.getItem as jest.Mock).mockReturnValue(mockLoginResponse.token);
      
      const result = service.getCurrentUserId();
      
      expect(result).toBe(mockExpectedUser.userId);
    });

    it('should return null for user ID when not logged in', () => {
      (localStorage.getItem as jest.Mock).mockReturnValue(null);
      
      const result = service.getCurrentUserId();
      
      expect(result).toBeNull();
    });
  });

  describe('Edge Cases', () => {
    it('should handle empty localStorage', () => {
      (localStorage.getItem as jest.Mock).mockReturnValue(null);
      
      expect(service.getToken()).toBeNull();
      expect(service.isLoggedIn()).toBe(false);
      expect(service.getCurrentUser()).toBeNull();
      expect(service.getCurrentUserId()).toBeNull();
    });

    it('should handle JWT with missing properties', () => {
      // JWT with incomplete payload
      const incompleteToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjF9.test-signature';
      (localStorage.getItem as jest.Mock).mockReturnValue(incompleteToken);
      
      const result = service.getCurrentUser();
      
      expect(result).toEqual({
        userId: 1,
        username: undefined,
        email: undefined
      });
    });
  });
});

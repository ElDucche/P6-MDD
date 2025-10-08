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
    identifier: 'test@example.com',
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
          register: '/api/auth/register',
          logout: '/api/auth/logout'
        },
        users: {
          me: '/api/user/me'
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

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    configService = TestBed.inject(ConfigService);

    // Le service appelle checkAuthStatus() dans son constructeur
    // On doit mock cette requête initiale
    const req = httpMock.expectOne('/api/user/me');
    req.flush(null, { status: 401, statusText: 'Unauthorized' });
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
    it('should login successfully with cookie authentication', (done) => {
      const successResponse: LoginResponse = {
        token: null, // Le token est dans un cookie HttpOnly maintenant
        message: 'Connexion réussie'
      };

      service.login(mockLoginCredentials).subscribe({
        next: (response) => {
          expect(response).toEqual(successResponse);
          expect(service.isLoggedIn()).toBe(true);
          done();
        },
        error: (err) => done.fail(err)
      });

      const req = httpMock.expectOne('/api/auth/login');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockLoginCredentials);
      expect(req.request.withCredentials).toBe(true); // Vérifier que les cookies sont envoyés
      req.flush(successResponse);
    });

    it('should throw error when login fails with error message', (done) => {
      const errorResponse: LoginResponse = {
        token: 'some-token', // Présence d'un token mais pas le bon message = échec
        message: 'Identifiants invalides'
      };

      service.login(mockLoginCredentials).subscribe({
        next: () => done.fail('Should have thrown an error'),
        error: (error) => {
          expect(error.message).toBe('Identifiants invalides');
          expect(service.isLoggedIn()).toBe(false);
          done();
        }
      });

      const req = httpMock.expectOne('/api/auth/login');
      req.flush(errorResponse);
    });

    it('should throw default error message when no message provided', (done) => {
      const errorResponse: LoginResponse = {
        token: 'some-token',
        message: '' // Message vide = erreur par défaut
      };

      service.login(mockLoginCredentials).subscribe({
        next: () => done.fail('Should have thrown an error'),
        error: (error) => {
          expect(error.message).toBe('Erreur de connexion');
          done();
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
    it('should logout and clear authentication state', (done) => {
      // Le logout appelle maintenant le backend et retourne un Observable
      service.logout().subscribe(() => {
        // Vérifier que l'état d'authentification est réinitialisé
        expect(service.isLoggedIn()).toBe(false);
        done();
      });

      // Vérifier la requête HTTP
      const req = httpMock.expectOne(`${service['config'].apiUrl}/api/auth/logout`);
      expect(req.request.method).toBe('POST');
      expect(req.request.withCredentials).toBe(true);
      req.flush({});
    });

    it('should return null for getToken (token in HttpOnly cookie)', () => {
      // Le token n'est plus accessible depuis le frontend pour des raisons de sécurité
      // Il est stocké dans un cookie HttpOnly
      const result = service.getToken();
      
      expect(result).toBeNull();
    });

    it('should return true when user is logged in', (done) => {
      // Simuler un utilisateur connecté en appelant login
      service.login({ identifier: 'test@example.com', password: 'password' }).subscribe({
        next: () => {
          // Après un login réussi, isLoggedIn() doit retourner true
          expect(service.isLoggedIn()).toBe(true);
          done();
        },
        error: (err) => done.fail(err)
      });

      // Vérifier et répondre à la requête de login avec le message attendu
      const loginReq = httpMock.expectOne('/api/auth/login');
      loginReq.flush({ message: 'Connexion réussie', token: null }); // Message en français comme attendu par le service
    });

    it('should return false when user is not logged in', () => {
      // Par défaut, l'utilisateur n'est pas connecté
      const result = service.isLoggedIn();
      
      expect(result).toBe(false);
    });
  });

  describe('Get Current User', () => {
    it('should fetch and return current user from backend', (done) => {
      const mockUserResponse = {
        id: 1,
        username: 'testuser',
        email: 'test@example.com'
      };
      
      service.getCurrentUser().subscribe(result => {
        expect(result).toEqual(mockExpectedUser);
        done();
      });

      const req = httpMock.expectOne('/api/user/me');
      expect(req.request.method).toBe('GET');
      expect(req.request.withCredentials).toBe(true);
      req.flush(mockUserResponse);
    });

    it('should return null when backend returns error', (done) => {
      service.getCurrentUser().subscribe(result => {
        expect(result).toBeNull();
        done();
      });

      const req = httpMock.expectOne('/api/user/me');
      req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });
    });

    it('should return cached user on subsequent calls', (done) => {
      const mockUserResponse = {
        id: 1,
        username: 'testuser',
        email: 'test@example.com'
      };
      
      // Premier appel - doit appeler le backend
      service.getCurrentUser().subscribe(result => {
        expect(result).toEqual(mockExpectedUser);
        
        // Deuxième appel - doit utiliser le cache
        service.getCurrentUser().subscribe(cachedResult => {
          expect(cachedResult).toEqual(mockExpectedUser);
          done();
        });
        
        // Vérifier qu'il n'y a pas de deuxième requête HTTP
        httpMock.expectNone('/api/user/me');
      });

      const req = httpMock.expectOne('/api/user/me');
      req.flush(mockUserResponse);
    });

    it('should get current user ID', (done) => {
      const mockUserResponse = {
        id: 1,
        username: 'testuser',
        email: 'test@example.com'
      };
      
      service.getCurrentUserId().subscribe(result => {
        expect(result).toBe(mockExpectedUser.userId);
        done();
      });

      const req = httpMock.expectOne('/api/user/me');
      req.flush(mockUserResponse);
    });

    it('should return null for user ID when not logged in', (done) => {
      service.getCurrentUserId().subscribe(result => {
        expect(result).toBeNull();
        done();
      });

      const req = httpMock.expectOne('/api/user/me');
      req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });
    });
  });

  describe('Edge Cases', () => {
    it('should handle user not authenticated', (done) => {
      expect(service.isLoggedIn()).toBe(false);
      
      service.getCurrentUser().subscribe(result => {
        expect(result).toBeNull();
        done();
      });

      const req = httpMock.expectOne('/api/user/me');
      req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });
    });

    it('should handle backend returning incomplete user data', (done) => {
      const incompleteUserResponse = {
        id: 1
        // username et email manquants
      };
      
      service.getCurrentUser().subscribe(result => {
        expect(result).toEqual({
          userId: 1,
          username: undefined,
          email: undefined
        });
        done();
      });

      const req = httpMock.expectOne('/api/user/me');
      req.flush(incompleteUserResponse);
    });
  });
});

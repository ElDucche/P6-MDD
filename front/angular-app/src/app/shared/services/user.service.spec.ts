import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { UserService } from './user.service';
import { ConfigService } from '../../core/services/config.service';
import { User } from '../interfaces/user.interface';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;
  let mockConfigService: any;

  const mockUser: User = {
    id: 1,
    username: 'testuser',
    email: 'test@example.com'
  };

  const mockEndpoints = {
    users: {
      me: '/api/users/me'
    }
  };

  beforeEach(() => {
    mockConfigService = {
      endpoints: mockEndpoints
    };

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        UserService,
        { provide: ConfigService, useValue: mockConfigService }
      ]
    });

    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getUser', () => {
    it('should retrieve current user data', () => {
      service.getUser().subscribe(user => {
        expect(user).toEqual(mockUser);
        expect(user.id).toBe(1);
        expect(user.username).toBe('testuser');
        expect(user.email).toBe('test@example.com');
      });

      const req = httpMock.expectOne('/api/users/me');
      expect(req.request.method).toBe('GET');
      req.flush(mockUser);
    });

    it('should handle HTTP error when getting user', () => {
      const errorMessage = 'User not found';

      service.getUser().subscribe({
        next: () => fail('should have failed with 404 error'),
        error: (error) => {
          expect(error.status).toBe(404);
          expect(error.error.message).toBe(errorMessage);
        }
      });

      const req = httpMock.expectOne('/api/users/me');
      req.flush({ message: errorMessage }, { status: 404, statusText: 'Not Found' });
    });

    it('should handle unauthorized access', () => {
      service.getUser().subscribe({
        next: () => fail('should have failed with 401 error'),
        error: (error) => {
          expect(error.status).toBe(401);
        }
      });

      const req = httpMock.expectOne('/api/users/me');
      req.flush({}, { status: 401, statusText: 'Unauthorized' });
    });
  });

  describe('updateUser', () => {
    it('should update user with partial data', () => {
      const updateData: Partial<User> = {
        username: 'newusername',
        email: 'newemail@example.com'
      };

      const updatedUser: User = {
        ...mockUser,
        ...updateData
      };

      service.updateUser(updateData).subscribe(user => {
        expect(user).toEqual(updatedUser);
        expect(user.username).toBe('newusername');
        expect(user.email).toBe('newemail@example.com');
        expect(user.id).toBe(1); // ID should remain unchanged
      });

      const req = httpMock.expectOne('/api/users/me');
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updateData);
      req.flush(updatedUser);
    });

    it('should update only username', () => {
      const updateData: Partial<User> = {
        username: 'updatedusername'
      };

      const updatedUser: User = {
        ...mockUser,
        username: 'updatedusername'
      };

      service.updateUser(updateData).subscribe(user => {
        expect(user.username).toBe('updatedusername');
        expect(user.email).toBe(mockUser.email); // Email unchanged
        expect(user.id).toBe(mockUser.id); // ID unchanged
      });

      const req = httpMock.expectOne('/api/users/me');
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updateData);
      req.flush(updatedUser);
    });

    it('should update only email', () => {
      const updateData: Partial<User> = {
        email: 'updatedemail@example.com'
      };

      const updatedUser: User = {
        ...mockUser,
        email: 'updatedemail@example.com'
      };

      service.updateUser(updateData).subscribe(user => {
        expect(user.email).toBe('updatedemail@example.com');
        expect(user.username).toBe(mockUser.username); // Username unchanged
        expect(user.id).toBe(mockUser.id); // ID unchanged
      });

      const req = httpMock.expectOne('/api/users/me');
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updateData);
      req.flush(updatedUser);
    });

    it('should handle validation errors on update', () => {
      const updateData: Partial<User> = {
        email: 'invalid-email'
      };

      const errorResponse = {
        message: 'Invalid email format',
        errors: {
          email: ['Email format is invalid']
        }
      };

      service.updateUser(updateData).subscribe({
        next: () => fail('should have failed with validation error'),
        error: (error) => {
          expect(error.status).toBe(400);
          expect(error.error.message).toBe('Invalid email format');
          expect(error.error.errors.email).toContain('Email format is invalid');
        }
      });

      const req = httpMock.expectOne('/api/users/me');
      expect(req.request.method).toBe('PUT');
      req.flush(errorResponse, { status: 400, statusText: 'Bad Request' });
    });

    it('should handle duplicate username error', () => {
      const updateData: Partial<User> = {
        username: 'existinguser'
      };

      const errorResponse = {
        message: 'Username already exists'
      };

      service.updateUser(updateData).subscribe({
        next: () => fail('should have failed with conflict error'),
        error: (error) => {
          expect(error.status).toBe(409);
          expect(error.error.message).toBe('Username already exists');
        }
      });

      const req = httpMock.expectOne('/api/users/me');
      req.flush(errorResponse, { status: 409, statusText: 'Conflict' });
    });

    it('should handle empty update data', () => {
      const updateData: Partial<User> = {};

      service.updateUser(updateData).subscribe(user => {
        expect(user).toEqual(mockUser); // Should return unchanged user
      });

      const req = httpMock.expectOne('/api/users/me');
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual({});
      req.flush(mockUser);
    });
  });

  describe('deleteUser', () => {
    it('should delete user account', () => {
      service.deleteUser().subscribe(response => {
        expect(response).toBeUndefined(); // Void response
      });

      const req = httpMock.expectOne('/api/users/me');
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });

    it('should handle unauthorized deletion', () => {
      service.deleteUser().subscribe({
        next: () => fail('should have failed with 401 error'),
        error: (error) => {
          expect(error.status).toBe(401);
        }
      });

      const req = httpMock.expectOne('/api/users/me');
      expect(req.request.method).toBe('DELETE');
      req.flush({}, { status: 401, statusText: 'Unauthorized' });
    });

    it('should handle user not found on deletion', () => {
      service.deleteUser().subscribe({
        next: () => fail('should have failed with 404 error'),
        error: (error) => {
          expect(error.status).toBe(404);
        }
      });

      const req = httpMock.expectOne('/api/users/me');
      expect(req.request.method).toBe('DELETE');
      req.flush({}, { status: 404, statusText: 'Not Found' });
    });

    it('should handle server error on deletion', () => {
      service.deleteUser().subscribe({
        next: () => fail('should have failed with 500 error'),
        error: (error) => {
          expect(error.status).toBe(500);
        }
      });

      const req = httpMock.expectOne('/api/users/me');
      expect(req.request.method).toBe('DELETE');
      req.flush({}, { status: 500, statusText: 'Internal Server Error' });
    });
  });

  describe('Integration scenarios', () => {
    it('should handle network errors', () => {
      const networkError = new ProgressEvent('error');

      service.getUser().subscribe({
        next: () => fail('should have failed with network error'),
        error: (error) => {
          expect(error.error).toBe(networkError);
        }
      });

      const req = httpMock.expectOne('/api/users/me');
      req.error(networkError);
    });

    it('should use correct endpoint from config service', () => {
      service.getUser().subscribe();

      const req = httpMock.expectOne('/api/users/me');
      expect(req.request.url).toBe(mockConfigService.endpoints.users.me);
    });

    it('should handle malformed response data', () => {
      const malformedResponse = {
        // Missing required fields
        username: 'testuser'
        // Missing id and email
      };

      service.getUser().subscribe(user => {
        expect(user.username).toBe('testuser');
        expect(user.id).toBeUndefined();
        expect(user.email).toBeUndefined();
      });

      const req = httpMock.expectOne('/api/users/me');
      req.flush(malformedResponse);
    });
  });
});

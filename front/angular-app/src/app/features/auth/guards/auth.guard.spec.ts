import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { CanActivateFn } from '@angular/router';
import { AuthService } from '../auth.service';
import { authGuard } from './auth.guard';

describe('authGuard', () => {
  let mockAuthService: any;
  let mockRouter: any;

  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => authGuard(...guardParameters));

  beforeEach(() => {
    mockAuthService = {
      isLoggedIn: jest.fn()
    };

    mockRouter = {
      navigate: jest.fn()
    };

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter }
      ]
    });
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });

  it('should allow access when user is logged in', () => {
    // Arrange
    mockAuthService.isLoggedIn.mockReturnValue(true);

    // Act
    const result = executeGuard({} as any, {} as any);

    // Assert
    expect(result).toBe(true);
    expect(mockAuthService.isLoggedIn).toHaveBeenCalled();
    expect(mockRouter.navigate).not.toHaveBeenCalled();
  });

  it('should deny access and redirect to login when user is not logged in', () => {
    // Arrange
    mockAuthService.isLoggedIn.mockReturnValue(false);

    // Act
    const result = executeGuard({} as any, {} as any);

    // Assert
    expect(result).toBe(false);
    expect(mockAuthService.isLoggedIn).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should handle null return from isLoggedIn', () => {
    // Arrange
    mockAuthService.isLoggedIn.mockReturnValue(null);

    // Act
    const result = executeGuard({} as any, {} as any);

    // Assert
    expect(result).toBe(false);
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should handle undefined return from isLoggedIn', () => {
    // Arrange
    mockAuthService.isLoggedIn.mockReturnValue(undefined);

    // Act
    const result = executeGuard({} as any, {} as any);

    // Assert
    expect(result).toBe(false);
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
  });
});

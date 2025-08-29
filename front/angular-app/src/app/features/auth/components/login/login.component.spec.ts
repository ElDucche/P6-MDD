import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { LoginEmailPasswordComponent } from './login.component';
import { AuthService, LoginResponse } from '../../auth.service';

describe('LoginEmailPasswordComponent', () => {
  let component: LoginEmailPasswordComponent;
  let authService: any;
  let router: any;

  beforeEach(async () => {
    const authServiceMock = {
      login: jest.fn()
    };

    const routerMock = {
      navigate: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule
      ],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    }).compileComponents();

    // Créer le composant manuellement sans template pour éviter les problèmes de routerLink
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
    component = new LoginEmailPasswordComponent(authService, router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with empty values', () => {
    expect(component.form.get('email')?.value).toBe('');
    expect(component.form.get('password')?.value).toBe('');
  });

  it('should validate email field', () => {
    const emailControl = component.form.get('email');
    
    // Test email required
    emailControl?.setValue('');
    emailControl?.markAsTouched();
    expect(emailControl?.hasError('required')).toBe(true);
    
    // Test invalid email format
    emailControl?.setValue('invalid-email');
    expect(emailControl?.hasError('email')).toBe(true);
    
    // Test valid email
    emailControl?.setValue('test@example.com');
    expect(emailControl?.hasError('email')).toBe(false);
    expect(emailControl?.hasError('required')).toBe(false);
  });

  it('should validate password field', () => {
    const passwordControl = component.form.get('password');
    
    // Test password required
    passwordControl?.setValue('');
    passwordControl?.markAsTouched();
    expect(passwordControl?.hasError('required')).toBe(true);
    
    // Test password min length
    passwordControl?.setValue('123');
    expect(passwordControl?.hasError('minlength')).toBe(true);
    
    // Test valid password
    passwordControl?.setValue('password123');
    expect(passwordControl?.hasError('minlength')).toBe(false);
    expect(passwordControl?.hasError('required')).toBe(false);
  });

  it('should call AuthService.login when form is valid', () => {
    const mockResponse: LoginResponse = { token: 'fake-token', message: 'Success' };
    authService.login.mockReturnValue(of(mockResponse));
    
    // Set valid form values
    component.form.patchValue({
      email: 'test@example.com',
      password: 'password123'
    });
    
    component.onSubmit();
    
    expect(authService.login).toHaveBeenCalledWith({
      email: 'test@example.com',
      password: 'password123'
    });
  });

  it('should navigate to /home on successful login', () => {
    const mockResponse: LoginResponse = { token: 'fake-token', message: 'Success' };
    authService.login.mockReturnValue(of(mockResponse));
    
    // Set valid form values
    component.form.patchValue({
      email: 'test@example.com',
      password: 'password123'
    });
    
    component.onSubmit();
    
    expect(router.navigate).toHaveBeenCalledWith(['/home']);
  });

  it('should handle login error', () => {
    const errorMessage = 'Invalid credentials';
    authService.login.mockReturnValue(throwError({ message: errorMessage }));
    
    // Set valid form values
    component.form.patchValue({
      email: 'test@example.com',
      password: 'wrongpassword'
    });
    
    component.onSubmit();
    
    expect((component as any).errorMessage()).toBe(errorMessage);
    expect((component as any).isLoading()).toBe(false);
  });

  it('should not submit when form is invalid', () => {
    // Form with invalid email
    component.form.patchValue({
      email: 'invalid-email',
      password: 'password123'
    });
    
    component.onSubmit();
    
    expect(authService.login).not.toHaveBeenCalled();
  });

  it('should set loading state during login', () => {
    const mockResponse: LoginResponse = { token: 'fake-token', message: 'Success' };
    authService.login.mockReturnValue(of(mockResponse));
    
    // Set valid form values
    component.form.patchValue({
      email: 'test@example.com',
      password: 'password123'
    });
    
    expect((component as any).isLoading()).toBe(false);
    
    component.onSubmit();
    
    // After successful login, loading should be false
    expect((component as any).isLoading()).toBe(false);
  });

  it('should clear error message on new submission', () => {
    // First, set an error
    authService.login.mockReturnValue(throwError({ message: 'Error' }));
    component.form.patchValue({
      email: 'test@example.com',
      password: 'wrongpassword'
    });
    component.onSubmit();
    expect((component as any).errorMessage()).toBe('Error');
    
    // Then, make a successful request
    const mockResponse: LoginResponse = { token: 'fake-token', message: 'Success' };
    authService.login.mockReturnValue(of(mockResponse));
    component.onSubmit();
    expect((component as any).errorMessage()).toBe('');
  });
});

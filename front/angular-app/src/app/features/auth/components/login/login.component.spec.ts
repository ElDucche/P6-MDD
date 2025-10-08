import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';

import { LoginEmailPasswordComponent } from './login.component';
import { AuthService, LoginResponse } from '../../auth.service';

describe('LoginEmailPasswordComponent', () => {
  let component: LoginEmailPasswordComponent;
  let fixture: ComponentFixture<LoginEmailPasswordComponent>;
  let authService: any;
  let router: Router;

  beforeEach(async () => {
    const authServiceMock = {
      login: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [
        LoginEmailPasswordComponent, // Import du composant standalone
        ReactiveFormsModule,
        RouterTestingModule // Fournit tous les services Router nécessaires
      ],
      providers: [
        { provide: AuthService, useValue: authServiceMock }
      ],
      schemas: [NO_ERRORS_SCHEMA] // Ignorer les erreurs de template
    }).compileComponents();

    // Utiliser TestBed.createComponent pour permettre l'injection de DestroyRef
    fixture = TestBed.createComponent(LoginEmailPasswordComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
    
    // Spy sur router.navigate
    jest.spyOn(router, 'navigate');
    
    fixture.detectChanges(); // Déclencher la détection de changements
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with empty values', () => {
    expect(component.form.get('identifier')?.value).toBe('');
    expect(component.form.get('password')?.value).toBe('');
  });

  it('should validate identifier field (required only)', () => {
    const idControl = component.form.get('identifier');
    idControl?.setValue('');
    idControl?.markAsTouched();
    expect(idControl?.hasError('required')).toBe(true);
    idControl?.setValue('anyUserName');
    expect(idControl?.hasError('required')).toBe(false);
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
    component.form.patchValue({
      identifier: 'test@example.com',
      password: 'password123'
    });
    component.onSubmit();
    expect(authService.login).toHaveBeenCalledWith({
      identifier: 'test@example.com',
      password: 'password123'
    });
  });

  it('should navigate to /home on successful login', () => {
    const mockResponse: LoginResponse = { token: 'fake-token', message: 'Success' };
    authService.login.mockReturnValue(of(mockResponse));
    component.form.patchValue({
      identifier: 'test@example.com',
      password: 'password123'
    });
    component.onSubmit();
    expect(router.navigate).toHaveBeenCalledWith(['/home']);
  });

  it('should handle login error', () => {
    const errorMessage = 'Invalid credentials';
    authService.login.mockReturnValue(throwError({ message: errorMessage }));
    component.form.patchValue({
      identifier: 'test@example.com',
      password: 'wrongpassword'
    });
    component.onSubmit();
    expect((component as any).errorMessage()).toBe(errorMessage);
    expect((component as any).isLoading()).toBe(false);
  });

  it('should not submit when form is invalid', () => {
    component.form.patchValue({ identifier: '', password: 'password123' });
    component.onSubmit();
    expect(authService.login).not.toHaveBeenCalled();
  });

  it('should set loading state during login', () => {
    const mockResponse: LoginResponse = { token: 'fake-token', message: 'Success' };
    authService.login.mockReturnValue(of(mockResponse));
    component.form.patchValue({
      identifier: 'test@example.com',
      password: 'password123'
    });
    expect((component as any).isLoading()).toBe(false);
    component.onSubmit();
    expect((component as any).isLoading()).toBe(false);
  });

  it('should clear error message on new submission', () => {
    authService.login.mockReturnValue(throwError({ message: 'Error' }));
    component.form.patchValue({
      identifier: 'test@example.com',
      password: 'wrongpassword'
    });
    component.onSubmit();
    expect((component as any).errorMessage()).toBe('Error');
    const mockResponse: LoginResponse = { token: 'fake-token', message: 'Success' };
    authService.login.mockReturnValue(of(mockResponse));
    component.onSubmit();
    expect((component as any).errorMessage()).toBe('');
  });
});

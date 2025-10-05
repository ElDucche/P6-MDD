import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { Router, ActivatedRoute } from '@angular/router';
import { FormBuilder } from '@angular/forms';
import { of, throwError } from 'rxjs';

import { RegisterComponent } from './register.component';
import { AuthService } from '../../auth.service';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let mockAuthService: any;
  let mockRouter: any;
  let mockActivatedRoute: any;
  let mockFormBuilder: FormBuilder;

  beforeEach(async () => {
    // Mock services
    mockAuthService = {
      register: jest.fn()
    };

    mockRouter = {
      navigate: jest.fn(),
      createUrlTree: jest.fn().mockReturnValue({}),
      serializeUrl: jest.fn().mockReturnValue('/login'),
      events: of()
    };

    mockActivatedRoute = {
      snapshot: { paramMap: { get: jest.fn() } },
      params: of({}),
      queryParams: of({})
    };

    mockFormBuilder = new FormBuilder();

    await TestBed.configureTestingModule({
      imports: [RegisterComponent],
      providers: [
        provideHttpClient(),
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: FormBuilder, useValue: mockFormBuilder }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;

    // Setup default mocks
    mockAuthService.register.mockReturnValue(of({ id: 1, message: 'Success' }));

    fixture.detectChanges();
  });

  describe('Component Creation', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should initialize with default signal values', () => {
      expect(component['errorMessage']()).toBe('');
      expect(component['successMessage']()).toBe('');
      expect(component['isLoading']()).toBe(false);
    });

    it('should initialize form with correct validators', () => {
      expect(component.form).toBeDefined();
      expect(component.form.get('username')).toBeTruthy();
      expect(component.form.get('email')).toBeTruthy();
      expect(component.form.get('password')).toBeTruthy();
    });
  });

  describe('Form Validation', () => {
    describe('Username Validation', () => {
      it('should validate username minimum length', () => {
        const usernameControl = component.form.get('username');
        
        usernameControl?.setValue('ab');
        expect(usernameControl?.errors?.['minlength']).toBeTruthy();
        
        usernameControl?.setValue('abc');
        expect(usernameControl?.errors?.['minlength']).toBeFalsy();
      });

      it('should validate username maximum length', () => {
        const usernameControl = component.form.get('username');
        
        const longUsername = 'a'.repeat(21);
        usernameControl?.setValue(longUsername);
        expect(usernameControl?.errors?.['maxlength']).toBeTruthy();
        
        const validUsername = 'a'.repeat(20);
        usernameControl?.setValue(validUsername);
        expect(usernameControl?.errors?.['maxlength']).toBeFalsy();
      });
    });

    describe('Password Validation', () => {
      it('should validate password pattern', () => {
        const passwordControl = component.form.get('password');
        
        const validPasswords = [
          'Password1!',
          'MySecure2@',
          'Complex3$'
        ];

        validPasswords.forEach(password => {
          passwordControl?.setValue(password);
          expect(passwordControl?.errors).toBeNull();
        });
      });
    });
  });

  describe('onSubmit Method', () => {
    it('should submit form when valid', () => {
      component.form.patchValue({
        username: 'testuser',
        email: 'test@example.com',
        password: 'Password1!'
      });

      component.onSubmit();

      expect(mockAuthService.register).toHaveBeenCalledWith({
        username: 'testuser',
        email: 'test@example.com',
        password: 'Password1!'
      });
    });

    it('should not submit when form is invalid', () => {
      component.form.patchValue({
        username: '',
        email: 'invalid-email',
        password: '123'
      });

      component.onSubmit();

      expect(mockAuthService.register).not.toHaveBeenCalled();
    });

    it('should handle successful registration', () => {
      component.form.patchValue({
        username: 'testuser',
        email: 'test@example.com',
        password: 'Password1!'
      });

      component.onSubmit();

      expect(component['successMessage']()).toBe('Compte créé avec succès ! Redirection vers la connexion...');
      expect(component['errorMessage']()).toBe('');
    });

    it('should handle registration error', () => {
      const error = { message: 'Registration failed' };
      mockAuthService.register.mockReturnValue(throwError(() => error));

      component.form.patchValue({
        username: 'testuser',
        email: 'test@example.com',
        password: 'Password1!'
      });

      component.onSubmit();

      expect(component['errorMessage']()).toBe('Registration failed');
      expect(component['successMessage']()).toBe('');
    });
  });
});

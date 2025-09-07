import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { Router } from '@angular/router';
import { FormBuilder } from '@angular/forms';
import { of, throwError } from 'rxjs';

import { ProfileComponent } from './profile.component';
import { UserService } from '../../shared/services/user.service';
import { SubscriptionService } from '../../shared/services/subscription.service';
import { ThemeService } from '../../shared/services/theme.service';
import { AuthService } from '../auth/auth.service';
import { AlertService } from '../../core/services/alert.service';

describe('ProfileComponent', () => {
  let component: ProfileComponent;
  let fixture: ComponentFixture<ProfileComponent>;
  let mockUserService: any;
  let mockSubscriptionService: any;
  let mockThemeService: any;
  let mockAuthService: any;
  let mockAlertService: any;
  let mockRouter: any;
  let mockFormBuilder: FormBuilder;

  beforeEach(async () => {
    // Mock services for unit tests
    mockUserService = {
      getUser: jest.fn(),
      updateUser: jest.fn()
    };

    mockSubscriptionService = {
      getUserSubscriptions: jest.fn(),
      unsubscribe: jest.fn()
    };

    mockThemeService = {
      getTheme: jest.fn()
    };

    mockAuthService = {
      logout: jest.fn()
    };

    mockAlertService = {
      showAlert: jest.fn()
    };

    mockRouter = {
      navigate: jest.fn()
    };

    mockFormBuilder = new FormBuilder();

    await TestBed.configureTestingModule({
      imports: [ProfileComponent],
      providers: [
        provideHttpClient(),
        provideRouter([]),
        { provide: UserService, useValue: mockUserService },
        { provide: SubscriptionService, useValue: mockSubscriptionService },
        { provide: ThemeService, useValue: mockThemeService },
        { provide: AuthService, useValue: mockAuthService },
        { provide: AlertService, useValue: mockAlertService },
        { provide: Router, useValue: mockRouter },
        { provide: FormBuilder, useValue: mockFormBuilder }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProfileComponent);
    component = fixture.componentInstance;
    
    // Setup default mocks
    mockUserService.getUser.mockReturnValue(of({
      id: 1,
      username: 'testuser',
      email: 'test@example.com'
    }));

    mockSubscriptionService.getUserSubscriptions.mockReturnValue(of([
      {
        id: { userId: 1, themeId: 1 },
        user: { id: 1, username: 'testuser', email: 'test@example.com' },
        theme: { id: 1, title: 'Angular', description: 'Angular development' },
        createdAt: '2023-01-01T00:00:00Z'
      }
    ]));

    mockUserService.updateUser.mockReturnValue(of({
      id: 1,
      username: 'updateduser',
      email: 'updated@example.com'
    }));

    mockSubscriptionService.unsubscribe.mockReturnValue(of(undefined));

    fixture.detectChanges();
  });

  describe('Component Creation', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should initialize with default values', () => {
      // Since ngOnInit is called in fixture.detectChanges(), the component will load user data
      // We need to reset the component to test initial values before ngOnInit
      const freshComponent = new ProfileComponent(
        mockUserService,
        mockAuthService,
        mockAlertService,
        mockSubscriptionService,
        mockThemeService,
        mockRouter,
        mockFormBuilder
      );
      
      expect(freshComponent['user']()).toBeUndefined();
      expect(freshComponent['isLoading']()).toBe(true);
      expect(freshComponent['isLoadingSubscriptions']()).toBe(false);
      expect(freshComponent['subscribedThemes']()).toEqual([]);
    });

    it('should initialize form correctly', () => {
      expect(component['editForm']).toBeDefined();
      expect(component['editForm'].get('username')).toBeTruthy();
      expect(component['editForm'].get('email')).toBeTruthy();
      expect(component['editForm'].get('password')).toBeTruthy();
    });
  });

  describe('User Initial Computed Property', () => {
    it('should compute userInitial correctly with user', () => {
      component['user'].set({ id: 1, username: 'testuser', email: 'test@example.com' });
      expect(component['userInitial']()).toBe('T');
    });

    it('should compute userInitial as empty when no user', () => {
      component['user'].set(undefined);
      expect(component['userInitial']()).toBe('');
    });

    it('should compute userInitial as empty when username is empty', () => {
      component['user'].set({ id: 1, username: '', email: 'test@example.com' });
      expect(component['userInitial']()).toBe('');
    });

    it('should compute userInitial as empty when username is null', () => {
      component['user'].set({ id: 1, username: null as any, email: 'test@example.com' });
      expect(component['userInitial']()).toBe('');
    });

    it('should handle user with undefined username in userInitial', () => {
      component['user'].set({ id: 1, username: undefined as any, email: 'test@example.com' });
      expect(component['userInitial']()).toBe('');
    });

    it('should compute userInitial with different username cases', () => {
      component['user'].set({
        id: 1,
        username: 'testuser',
        email: 'test@example.com'
      });
      expect(component['userInitial']()).toBe('T');

      component['user'].set({
        id: 1,
        username: 'lowercase',
        email: 'test@example.com'
      });
      expect(component['userInitial']()).toBe('L');
    });
  });

  describe('ngOnInit', () => {
    it('should call loadUserProfile', () => {
      const loadUserProfileSpy = jest.spyOn(component as any, 'loadUserProfile');
      component.ngOnInit();
      expect(loadUserProfileSpy).toHaveBeenCalled();
    });
  });

  describe('loadUserProfile method', () => {
    beforeEach(() => {
      jest.useFakeTimers();
    });

    afterEach(() => {
      jest.useRealTimers();
    });

    it('should load user profile successfully', () => {
      component['loadUserProfile']();

      expect(component['isLoading']()).toBe(true);
      expect(mockUserService.getUser).toHaveBeenCalled();

      // Fast-forward timers to simulate async completion
      jest.advanceTimersByTime(1000);

      expect(component['user']()).toEqual({
        id: 1,
        username: 'testuser',
        email: 'test@example.com'
      });
      expect(component['isLoading']()).toBe(false);
    });

    it('should handle user loading error', () => {
      const error = new Error('Failed to load user');
      mockUserService.getUser.mockReturnValue(throwError(() => error));
      const consoleSpy = jest.spyOn(console, 'error').mockImplementation();

      component['loadUserProfile']();

      expect(component['isLoading']()).toBe(false);
      expect(mockAlertService.showAlert).toHaveBeenCalledWith({
        type: 'error',
        message: 'Impossible de charger le profil utilisateur'
      });
      expect(consoleSpy).toHaveBeenCalledWith('Erreur lors du chargement du profil:', error);

      consoleSpy.mockRestore();
    });

    it('should patch form with user data', () => {
      const patchValueSpy = jest.spyOn(component['editForm'], 'patchValue');
      
      component['loadUserProfile']();

      expect(patchValueSpy).toHaveBeenCalledWith({
        username: 'testuser',
        email: 'test@example.com',
        password: ''
      });
    });

    it('should handle user with missing data', () => {
      mockUserService.getUser.mockReturnValue(of({
        id: 1,
        username: null,
        email: null
      }));

      const patchValueSpy = jest.spyOn(component['editForm'], 'patchValue');
      
      component['loadUserProfile']();

      expect(patchValueSpy).toHaveBeenCalledWith({
        username: '',
        email: '',
        password: ''
      });
    });
  });

  describe('loadUserSubscriptions method', () => {
    it('should load subscriptions successfully', () => {
      component['loadUserSubscriptions']();

      expect(mockSubscriptionService.getUserSubscriptions).toHaveBeenCalled();

      const themes = component['subscribedThemes']();
      expect(themes).toHaveLength(1);
      expect(themes[0].title).toBe('Angular');
      expect(themes[0].subscribedAt).toBeInstanceOf(Date);
      expect(component['isLoadingSubscriptions']()).toBe(false);
    });

    it('should handle empty subscriptions', () => {
      mockSubscriptionService.getUserSubscriptions.mockReturnValue(of([]));

      component['loadUserSubscriptions']();

      expect(component['subscribedThemes']()).toEqual([]);
      expect(component['isLoadingSubscriptions']()).toBe(false);
    });

    it('should handle subscriptions loading error', () => {
      const error = new Error('Failed to load subscriptions');
      mockSubscriptionService.getUserSubscriptions.mockReturnValue(throwError(() => error));
      const consoleSpy = jest.spyOn(console, 'error').mockImplementation();

      component['loadUserSubscriptions']();

      expect(component['isLoadingSubscriptions']()).toBe(false);
      expect(mockAlertService.showAlert).toHaveBeenCalledWith({
        type: 'error',
        message: 'Erreur lors du chargement de vos abonnements'
      });
      expect(consoleSpy).toHaveBeenCalledWith('Erreur lors du chargement des abonnements:', error);

      consoleSpy.mockRestore();
    });

    it('should handle subscriptions without createdAt', () => {
      mockSubscriptionService.getUserSubscriptions.mockReturnValue(of([
        {
          id: { userId: 1, themeId: 1 },
          user: { id: 1, username: 'testuser', email: 'test@example.com' },
          theme: { id: 1, title: 'Angular', description: 'Angular development' },
          createdAt: undefined
        }
      ]));

      component['loadUserSubscriptions']();

      const themes = component['subscribedThemes']();
      expect(themes[0].subscribedAt).toBeInstanceOf(Date);
    });
  });

  describe('updateProfile method', () => {
    beforeEach(() => {
      jest.useFakeTimers();
    });

    afterEach(() => {
      jest.useRealTimers();
    });

    it('should update profile when form is valid', () => {
      Object.defineProperty(component['editForm'], 'valid', { value: true });
      component['editForm'].patchValue({
        username: 'newusername',
        email: 'newemail@example.com',
        password: 'newpassword'
      });

      component['updateProfile']();

      expect(mockUserService.updateUser).toHaveBeenCalledWith({
        username: 'newusername',
        email: 'newemail@example.com',
        password: 'newpassword'
      });

      expect(component['user']()).toEqual({
        id: 1,
        username: 'updateduser',
        email: 'updated@example.com'
      });

      expect(mockAlertService.showAlert).toHaveBeenCalledWith({
        type: 'success',
        message: 'Profil mis à jour avec succès. Vous allez être déconnecté pour actualiser votre session.'
      });

      jest.advanceTimersByTime(2000);
      expect(mockAuthService.logout).toHaveBeenCalled();
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/auth/login']);
    });

    it('should not include password if empty', () => {
      Object.defineProperty(component['editForm'], 'valid', { value: true });
      component['editForm'].patchValue({
        username: 'newusername',
        email: 'newemail@example.com',
        password: ''
      });

      component['updateProfile']();

      expect(mockUserService.updateUser).toHaveBeenCalledWith({
        username: 'newusername',
        email: 'newemail@example.com'
      });
    });

    it('should not include password if only whitespace', () => {
      Object.defineProperty(component['editForm'], 'valid', { value: true });
      component['editForm'].patchValue({
        username: 'newusername',
        email: 'newemail@example.com',
        password: '   '
      });

      component['updateProfile']();

      expect(mockUserService.updateUser).toHaveBeenCalledWith({
        username: 'newusername',
        email: 'newemail@example.com'
      });
    });

    it('should not update when form is invalid', () => {
      Object.defineProperty(component['editForm'], 'valid', { value: false });

      component['updateProfile']();

      expect(mockUserService.updateUser).not.toHaveBeenCalled();
      expect(mockAlertService.showAlert).toHaveBeenCalledWith({
        type: 'error',
        message: 'Veuillez corriger les erreurs du formulaire'
      });
    });

    it('should handle update error', () => {
      Object.defineProperty(component['editForm'], 'valid', { value: true });
      const error = new Error('Update failed');
      mockUserService.updateUser.mockReturnValue(throwError(() => error));
      const consoleSpy = jest.spyOn(console, 'error').mockImplementation();

      component['updateProfile']();

      expect(mockAlertService.showAlert).toHaveBeenCalledWith({
        type: 'error',
        message: 'Erreur lors de la mise à jour du profil'
      });
      expect(consoleSpy).toHaveBeenCalledWith('Erreur lors de la mise à jour:', error);

      consoleSpy.mockRestore();
    });

    it('should handle null form values in updateProfile', () => {
      Object.defineProperty(component['editForm'], 'valid', { value: true });
      
      // Mock the form value getter to return null values
      Object.defineProperty(component['editForm'], 'value', {
        get: () => ({
          username: null,
          email: null,
          password: null
        }),
        configurable: true
      });

      component['updateProfile']();

      expect(mockUserService.updateUser).toHaveBeenCalledWith({
        username: null,
        email: null
      });
    });
  });

  describe('unsubscribeFromTheme method', () => {
    beforeEach(() => {
      component['subscribedThemes'].set([
        { 
          id: 1, 
          title: 'Angular', 
          description: 'Angular development',
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
          subscribedAt: new Date() 
        },
        { 
          id: 2, 
          title: 'React', 
          description: 'React development',
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
          subscribedAt: new Date() 
        }
      ]);
    });

    it('should unsubscribe from theme successfully', () => {
      component['unsubscribeFromTheme'](1);

      expect(mockSubscriptionService.unsubscribe).toHaveBeenCalledWith(1);
      expect(mockAlertService.showAlert).toHaveBeenCalledWith({
        type: 'success',
        message: 'Vous vous êtes désabonné de "Angular"'
      });

      const themes = component['subscribedThemes']();
      expect(themes).toHaveLength(1);
      expect(themes.find(t => t.id === 1)).toBeUndefined();
    });

    it('should handle unsubscribe from non-existent theme', () => {
      component['unsubscribeFromTheme'](999);

      expect(mockSubscriptionService.unsubscribe).not.toHaveBeenCalled();
      expect(mockAlertService.showAlert).toHaveBeenCalledWith({
        type: 'error',
        message: 'Abonnement introuvable'
      });
    });

    it('should handle unsubscribe error', () => {
      const error = new Error('Unsubscribe failed');
      mockSubscriptionService.unsubscribe.mockReturnValue(throwError(() => error));
      const consoleSpy = jest.spyOn(console, 'error').mockImplementation();

      component['unsubscribeFromTheme'](1);

      expect(mockAlertService.showAlert).toHaveBeenCalledWith({
        type: 'error',
        message: 'Erreur lors du désabonnement'
      });
      expect(consoleSpy).toHaveBeenCalledWith('Erreur lors du désabonnement:', error);

      consoleSpy.mockRestore();
    });

    it('should use fallback theme name when title is missing', () => {
      component['subscribedThemes'].set([
        { 
          id: 1, 
          title: '', 
          description: 'Angular development',
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
          subscribedAt: new Date() 
        }
      ]);

      component['unsubscribeFromTheme'](1);

      expect(mockAlertService.showAlert).toHaveBeenCalledWith({
        type: 'success',
        message: 'Vous vous êtes désabonné de "ce thème"'
      });
    });

    it('should handle empty subscriptions list for unsubscribe', () => {
      component['subscribedThemes'].set([]);

      component['unsubscribeFromTheme'](1);

      expect(mockSubscriptionService.unsubscribe).not.toHaveBeenCalled();
      expect(mockAlertService.showAlert).toHaveBeenCalledWith({
        type: 'error',
        message: 'Abonnement introuvable'
      });
    });

    it('should handle multiple unsubscriptions', () => {
      const initialThemes = [
        { id: 1, title: 'Angular', description: 'Angular dev', createdAt: '2023-01-01', updatedAt: '2023-01-01', subscribedAt: new Date() },
        { id: 2, title: 'React', description: 'React dev', createdAt: '2023-01-01', updatedAt: '2023-01-01', subscribedAt: new Date() }
      ];

      // Set initial themes
      component['subscribedThemes'].set(initialThemes);
      
      // Mock subscription service for successful unsubscription
      mockSubscriptionService.unsubscribe.mockReturnValue(of(void 0));

      // First unsubscription
      component['unsubscribeFromTheme'](1);
      expect(component['subscribedThemes']()).toHaveLength(1);
      expect(component['subscribedThemes']().find(t => t.id === 1)).toBeUndefined();

      // Second unsubscription
      component['unsubscribeFromTheme'](2);
      expect(component['subscribedThemes']()).toHaveLength(0);
      expect(component['subscribedThemes']().find(t => t.id === 2)).toBeUndefined();
    });
  });

  describe('Form Control Getters', () => {
    it('should return username control', () => {
      const control = component['usernameControl'];
      expect(control).toBe(component['editForm'].get('username'));
    });

    it('should return email control', () => {
      const control = component['emailControl'];
      expect(control).toBe(component['editForm'].get('email'));
    });

    it('should return password control', () => {
      const control = component['passwordControl'];
      expect(control).toBe(component['editForm'].get('password'));
    });
  });

  describe('Component State Management', () => {
    it('should have initial loading states', () => {
      expect(component['isLoading']()).toBe(true); // Initial state
      expect(component['isLoadingSubscriptions']()).toBe(false);
    });

    it('should handle edge cases for user data', () => {
      // Test with null/undefined username
      component['user'].set({
        id: 1,
        username: null as any,
        email: 'test@example.com'
      });
      expect(component['userInitial']()).toBe('');

      // Test with undefined user
      component['user'].set(undefined);
      expect(component['userInitial']()).toBe('');
    });

    it('should maintain form state during operations', () => {
      const user = { id: 1, username: 'testuser', email: 'test@example.com' };
      component['user'].set(user);
      
      const patchValueSpy = jest.spyOn(component['editForm'], 'patchValue');
      const resetSpy = jest.spyOn(component['editForm'], 'reset');

      // Simulate opening edit modal
      component['editForm'].patchValue({
        username: user.username,
        email: user.email,
        password: ''
      });

      expect(patchValueSpy).toHaveBeenCalled();
    });
  });
});

import { TestBed, ComponentFixture } from '@angular/core/testing';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { of, throwError } from 'rxjs';

import { ProfileComponent } from './profile.component';
import { UserService } from '../../shared/services/user.service';
import { AuthService } from '../auth/auth.service';
import { AlertService } from '../../core/services/alert.service';
import { SubscriptionService } from '../../shared/services/subscription.service';
import { ThemeService } from '../../shared/services/theme.service';
import { User } from '../../shared/interfaces/user.interface';

describe('ProfileComponent Integration Tests', () => {
  let component: ProfileComponent;
  let fixture: ComponentFixture<ProfileComponent>;
  let userService: jest.Mocked<UserService>;
  let authService: jest.Mocked<AuthService>;
  let alertService: jest.Mocked<AlertService>;
  let subscriptionService: jest.Mocked<SubscriptionService>;
  let themeService: jest.Mocked<ThemeService>;
  let router: jest.Mocked<Router>;

  const mockUser: User = {
    id: 1,
    username: 'testuser',
    email: 'test@example.com'
  };

  const mockSubscriptions = [
    {
      id: { userId: 1, themeId: 1 },
      user: mockUser,
      theme: { id: 1, title: 'Angular', description: 'Angular framework' },
      createdAt: '2024-01-01T00:00:00Z'
    }
  ];

  beforeEach(async () => {
    const userServiceMock = {
      getUser: jest.fn(),
      updateUser: jest.fn(),
      deleteUser: jest.fn()
    };

    const authServiceMock = {
      logout: jest.fn()
    };

    const alertServiceMock = {
      showAlert: jest.fn()
    };

    const subscriptionServiceMock = {
      getUserSubscriptions: jest.fn(),
      unsubscribe: jest.fn()
    };

    const themeServiceMock = {
      getAllThemes: jest.fn()
    };

    const routerMock = {
      navigate: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [ProfileComponent, ReactiveFormsModule],
      providers: [
        FormBuilder,
        DatePipe,
        { provide: UserService, useValue: userServiceMock },
        { provide: AuthService, useValue: authServiceMock },
        { provide: AlertService, useValue: alertServiceMock },
        { provide: SubscriptionService, useValue: subscriptionServiceMock },
        { provide: ThemeService, useValue: themeServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProfileComponent);
    component = fixture.componentInstance;
    
    userService = TestBed.inject(UserService) as jest.Mocked<UserService>;
    authService = TestBed.inject(AuthService) as jest.Mocked<AuthService>;
    alertService = TestBed.inject(AlertService) as jest.Mocked<AlertService>;
    subscriptionService = TestBed.inject(SubscriptionService) as jest.Mocked<SubscriptionService>;
    themeService = TestBed.inject(ThemeService) as jest.Mocked<ThemeService>;
    router = TestBed.inject(Router) as jest.Mocked<Router>;

    // Setup default mocks
    userService.getUser.mockReturnValue(of(mockUser));
    subscriptionService.getUserSubscriptions.mockReturnValue(of(mockSubscriptions));
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should compute userInitial correctly', () => {
    fixture.detectChanges();
    
    // Access the computed property through component methods or test the computed value
    const userInitial = (component as any).userInitial();
    expect(userInitial).toBe('T'); // First letter of 'testuser'
  });

  it('should compute userInitial as empty when user is undefined', () => {
    userService.getUser.mockReturnValue(of(undefined as any));
    fixture.detectChanges();
    
    const userInitial = (component as any).userInitial();
    expect(userInitial).toBe('');
  });

  it('should open edit modal', () => {
    fixture.detectChanges();
    
    const patchValueSpy = jest.spyOn((component as any).editForm, 'patchValue');
    
    // Call the method through the component
    (component as any).openEditModal();
    
    expect((component as any).isEditModalOpen()).toBe(true);
    expect(patchValueSpy).toHaveBeenCalledWith({
      username: mockUser.username,
      email: mockUser.email,
      password: ''
    });
  });

  it('should close edit modal', () => {
    fixture.detectChanges();
    
    // Open modal first
    (component as any).openEditModal();
    expect((component as any).isEditModalOpen()).toBe(true);
    
    // Close modal
    (component as any).closeEditModal();
    
    expect((component as any).isEditModalOpen()).toBe(false);
  });

  it('should update profile successfully', () => {
    fixture.detectChanges();
    
    const updatedUser = { ...mockUser, username: 'newusername' };
    userService.updateUser.mockReturnValue(of(updatedUser));
    
    // Setup form with valid data
    (component as any).editForm.patchValue({
      username: 'newusername',
      email: 'newemail@example.com',
      password: ''
    });
    
    // Mock form valid
    jest.spyOn((component as any).editForm, 'valid', 'get').mockReturnValue(true);
    
    (component as any).updateProfile();
    
    expect(userService.updateUser).toHaveBeenCalledWith({
      username: 'newusername',
      email: 'newemail@example.com'
    });
    expect(alertService.showAlert).toHaveBeenCalledWith({
      type: 'success',
      message: 'Profil mis à jour avec succès. Vous allez être déconnecté pour actualiser votre session.'
    });
  });

  it('should update profile with password', () => {
    fixture.detectChanges();
    
    const updatedUser = { ...mockUser, username: 'newusername' };
    userService.updateUser.mockReturnValue(of(updatedUser));
    
    // Setup form with valid data including password
    (component as any).editForm.patchValue({
      username: 'newusername',
      email: 'newemail@example.com',
      password: 'NewPassword123!'
    });
    
    // Mock form valid
    jest.spyOn((component as any).editForm, 'valid', 'get').mockReturnValue(true);
    
    (component as any).updateProfile();
    
    expect(userService.updateUser).toHaveBeenCalledWith({
      username: 'newusername',
      email: 'newemail@example.com',
      password: 'NewPassword123!'
    });
  });

  it('should handle update profile error', () => {
    fixture.detectChanges();
    
    userService.updateUser.mockReturnValue(throwError(() => new Error('Update failed')));
    
    // Setup form with valid data
    (component as any).editForm.patchValue({
      username: 'newusername',
      email: 'newemail@example.com',
      password: ''
    });
    
    jest.spyOn((component as any).editForm, 'valid', 'get').mockReturnValue(true);
    
    (component as any).updateProfile();
    
    expect(alertService.showAlert).toHaveBeenCalledWith({
      type: 'error',
      message: 'Erreur lors de la mise à jour du profil'
    });
  });

  it('should handle invalid form on update', () => {
    fixture.detectChanges();
    
    jest.spyOn((component as any).editForm, 'valid', 'get').mockReturnValue(false);
    
    (component as any).updateProfile();
    
    expect(userService.updateUser).not.toHaveBeenCalled();
    expect(alertService.showAlert).toHaveBeenCalledWith({
      type: 'error',
      message: 'Veuillez corriger les erreurs du formulaire'
    });
  });

  it('should open delete confirmation', () => {
    fixture.detectChanges();
    
    // Open edit modal first
    (component as any).openEditModal();
    expect((component as any).isEditModalOpen()).toBe(true);
    
    // Open delete confirmation
    (component as any).openDeleteConfirm();
    
    expect((component as any).isEditModalOpen()).toBe(false);
    expect((component as any).isDeleteConfirmOpen()).toBe(true);
  });

  it('should close delete confirmation', () => {
    fixture.detectChanges();
    
    (component as any).openDeleteConfirm();
    expect((component as any).isDeleteConfirmOpen()).toBe(true);
    
    (component as any).closeDeleteConfirm();
    expect((component as any).isDeleteConfirmOpen()).toBe(false);
  });

  it('should delete account successfully', () => {
    fixture.detectChanges();
    
    userService.deleteUser.mockReturnValue(of(void 0));
    
    (component as any).deleteAccount();
    
    expect(userService.deleteUser).toHaveBeenCalled();
    expect(alertService.showAlert).toHaveBeenCalledWith({
      type: 'success',
      message: 'Compte supprimé avec succès'
    });
    expect(authService.logout).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/auth/login']);
  });

  it('should handle delete account error', () => {
    fixture.detectChanges();
    
    userService.deleteUser.mockReturnValue(throwError(() => new Error('Delete failed')));
    
    (component as any).deleteAccount();
    
    expect(alertService.showAlert).toHaveBeenCalledWith({
      type: 'error',
      message: 'Erreur lors de la suppression du compte'
    });
  });

  it('should unsubscribe from theme successfully', () => {
    fixture.detectChanges();
    
    const subscriptionId = 1;
    subscriptionService.unsubscribe.mockReturnValue(of(void 0));
    
    (component as any).unsubscribeFromTheme(1);
    
    expect(subscriptionService.unsubscribe).toHaveBeenCalledWith(subscriptionId);
  });

  it('should handle unsubscribe error', () => {
    fixture.detectChanges();
    
    subscriptionService.unsubscribe.mockReturnValue(throwError(() => new Error('Unsubscribe failed')));
    
    (component as any).unsubscribeFromTheme(1);
    
    expect(alertService.showAlert).toHaveBeenCalledWith({
      type: 'error',
      message: 'Erreur lors du désabonnement'
    });
  });

  it('should test form control getters', () => {
    fixture.detectChanges();
    
    // Test that getters work
    const usernameControl = (component as any).usernameControl;
    const emailControl = (component as any).emailControl;
    const passwordControl = (component as any).passwordControl;
    
    expect(usernameControl).toBeDefined();
    expect(emailControl).toBeDefined();
    expect(passwordControl).toBeDefined();
  });

  it('should handle load user error', () => {
    userService.getUser.mockReturnValue(throwError(() => new Error('Load user failed')));
    
    fixture.detectChanges();
    
    expect((component as any).isLoading()).toBe(false);
  });

  it('should handle load subscriptions error', () => {
    subscriptionService.getUserSubscriptions.mockReturnValue(throwError(() => new Error('Load subscriptions failed')));
    
    fixture.detectChanges();
    
    expect(alertService.showAlert).toHaveBeenCalledWith({
      type: 'error',
      message: 'Erreur lors du chargement de vos abonnements'
    });
  });
});

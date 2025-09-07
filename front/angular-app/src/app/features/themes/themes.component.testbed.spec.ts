import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ThemesComponent } from './themes.component';
import { ThemeService } from '../../shared/services/theme.service';
import { SubscriptionService } from '../../shared/services/subscription.service';
import { AuthService } from '../auth/auth.service';

describe('ThemesComponent (TestBed)', () => {
  let component: ThemesComponent;
  let fixture: ComponentFixture<ThemesComponent>;
  let themeService: jest.Mocked<ThemeService>;
  let subscriptionService: jest.Mocked<SubscriptionService>;
  let authService: jest.Mocked<AuthService>;
  let router: jest.Mocked<Router>;

  const mockThemes = [
    { id: 1, title: 'Angular', description: 'Framework JavaScript' },
    { id: 2, title: 'React', description: 'Bibliothèque JavaScript' },
    { id: 3, title: 'Vue.js', description: 'Framework progressif' }
  ];

  const mockSubscriptions = [
    { id: 1, userId: 1, themeId: 1 },
    { id: 2, userId: 1, themeId: 3 }
  ];

  beforeEach(async () => {
    const themeServiceMock = {
      getThemes: jest.fn(),
    };

    const subscriptionServiceMock = {
      getUserSubscriptions: jest.fn(),
      subscribe: jest.fn(),
      unsubscribe: jest.fn(),
    };

    const authServiceMock = {
      getCurrentUserId: jest.fn(),
    };

    const routerMock = {
      navigate: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [ThemesComponent],
      providers: [
        { provide: ThemeService, useValue: themeServiceMock },
        { provide: SubscriptionService, useValue: subscriptionServiceMock },
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ThemesComponent);
    component = fixture.componentInstance;
    
    themeService = TestBed.inject(ThemeService) as jest.Mocked<ThemeService>;
    subscriptionService = TestBed.inject(SubscriptionService) as jest.Mocked<SubscriptionService>;
    authService = TestBed.inject(AuthService) as jest.Mocked<AuthService>;
    router = TestBed.inject(Router) as jest.Mocked<Router>;
  });

  describe('Component Creation', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should initialize with correct default values', () => {
      expect(component.themes()).toEqual([]);
      expect(component.subscribedThemeIds()).toEqual([]);
      expect(component.isLoading()).toBe(false);
      expect(component.loadingThemeIds()).toEqual([]);
    });
  });

  describe('ngOnInit', () => {
    it('should load themes and subscriptions on init', () => {
      // Arrange
      themeService.getThemes.mockReturnValue(of(mockThemes));
      subscriptionService.getUserSubscriptions.mockReturnValue(of(mockSubscriptions));

      // Act
      component.ngOnInit();

      // Assert
      expect(themeService.getThemes).toHaveBeenCalled();
      expect(subscriptionService.getUserSubscriptions).toHaveBeenCalled();
      expect(component.themes()).toEqual(mockThemes);
      expect(component.subscribedThemeIds()).toEqual([1, 3]);
      expect(component.isLoading()).toBe(false);
    });

    it('should handle themes loading error', () => {
      // Arrange
      const error = new Error('API Error');
      themeService.getThemes.mockReturnValue(throwError(() => error));
      subscriptionService.getUserSubscriptions.mockReturnValue(of(mockSubscriptions));

      // Act
      component.ngOnInit();

      // Assert
      expect(component.themes()).toEqual([]);
      expect(component.isLoading()).toBe(false);
    });

    it('should handle subscriptions loading error', () => {
      // Arrange
      themeService.getThemes.mockReturnValue(of(mockThemes));
      subscriptionService.getUserSubscriptions.mockReturnValue(throwError(() => new Error('API Error')));

      // Act
      component.ngOnInit();

      // Assert
      expect(component.themes()).toEqual(mockThemes);
      expect(component.subscribedThemeIds()).toEqual([]);
      expect(component.isLoading()).toBe(false);
    });
  });

  describe('Theme Subscription', () => {
    beforeEach(() => {
      authService.getCurrentUserId.mockReturnValue(1);
      themeService.getThemes.mockReturnValue(of(mockThemes));
      subscriptionService.getUserSubscriptions.mockReturnValue(of([]));
      component.ngOnInit();
    });

    it('should subscribe to theme successfully', () => {
      // Arrange
      const theme = mockThemes[0];
      const newSubscription = { id: 4, userId: 1, themeId: theme.id };
      subscriptionService.subscribe.mockReturnValue(of(newSubscription));

      // Act
      component.subscribeToTheme(theme);

      // Assert
      expect(subscriptionService.subscribe).toHaveBeenCalledWith(theme.id);
      expect(component.subscribedThemeIds()).toContain(theme.id);
      expect(component.loadingThemeIds()).not.toContain(theme.id);
    });

    it('should handle subscription error', () => {
      // Arrange
      const theme = mockThemes[0];
      subscriptionService.subscribe.mockReturnValue(throwError(() => new Error('Subscription failed')));

      // Act
      component.subscribeToTheme(theme);

      // Assert
      expect(subscriptionService.subscribe).toHaveBeenCalledWith(theme.id);
      expect(component.subscribedThemeIds()).not.toContain(theme.id);
      expect(component.loadingThemeIds()).not.toContain(theme.id);
    });

    it('should not subscribe when user not logged in', () => {
      // Arrange
      authService.getCurrentUserId.mockReturnValue(null);
      const theme = mockThemes[0];

      // Act
      component.subscribeToTheme(theme);

      // Assert
      expect(subscriptionService.subscribe).not.toHaveBeenCalled();
    });

    it('should not subscribe to already subscribed theme', () => {
      // Arrange
      const theme = mockThemes[0];
      component.subscribedThemeIds.set([theme.id]);

      // Act
      component.subscribeToTheme(theme);

      // Assert
      expect(subscriptionService.subscribe).not.toHaveBeenCalled();
    });
  });

  describe('Theme Unsubscription', () => {
    beforeEach(() => {
      authService.getCurrentUserId.mockReturnValue(1);
      themeService.getThemes.mockReturnValue(of(mockThemes));
      subscriptionService.getUserSubscriptions.mockReturnValue(of(mockSubscriptions));
      component.ngOnInit();
    });

    it('should unsubscribe from theme successfully', () => {
      // Arrange
      const theme = mockThemes[0]; // Theme with ID 1, which is subscribed
      subscriptionService.unsubscribe.mockReturnValue(of({}));

      // Act
      component.unsubscribeFromTheme(theme);

      // Assert
      expect(subscriptionService.unsubscribe).toHaveBeenCalledWith(theme.id);
      expect(component.subscribedThemeIds()).not.toContain(theme.id);
      expect(component.loadingThemeIds()).not.toContain(theme.id);
    });

    it('should handle unsubscription error', () => {
      // Arrange
      const theme = mockThemes[0];
      subscriptionService.unsubscribe.mockReturnValue(throwError(() => new Error('Unsubscription failed')));

      // Act
      component.unsubscribeFromTheme(theme);

      // Assert
      expect(subscriptionService.unsubscribe).toHaveBeenCalledWith(theme.id);
      expect(component.subscribedThemeIds()).toContain(theme.id); // Should still be subscribed
      expect(component.loadingThemeIds()).not.toContain(theme.id);
    });

    it('should not unsubscribe when user not logged in', () => {
      // Arrange
      authService.getCurrentUserId.mockReturnValue(null);
      const theme = mockThemes[0];

      // Act
      component.unsubscribeFromTheme(theme);

      // Assert
      expect(subscriptionService.unsubscribe).not.toHaveBeenCalled();
    });

    it('should not unsubscribe from non-subscribed theme', () => {
      // Arrange
      const theme = mockThemes[1]; // Theme with ID 2, which is not subscribed
      component.subscribedThemeIds.set([1, 3]); // Only themes 1 and 3 are subscribed

      // Act
      component.unsubscribeFromTheme(theme);

      // Assert
      expect(subscriptionService.unsubscribe).not.toHaveBeenCalled();
    });
  });

  describe('Helper Methods', () => {
    it('should correctly identify subscribed themes', () => {
      // Arrange
      component.subscribedThemeIds.set([1, 3]);

      // Act & Assert
      expect(component.isSubscribed({ id: 1, title: 'Test', description: 'Test' })).toBe(true);
      expect(component.isSubscribed({ id: 2, title: 'Test', description: 'Test' })).toBe(false);
      expect(component.isSubscribed({ id: 3, title: 'Test', description: 'Test' })).toBe(true);
    });

    it('should correctly identify loading themes', () => {
      // Arrange
      component.loadingThemeIds.set([2]);

      // Act & Assert
      expect(component.isLoading2({ id: 1, title: 'Test', description: 'Test' })).toBe(false);
      expect(component.isLoading2({ id: 2, title: 'Test', description: 'Test' })).toBe(true);
      expect(component.isLoading2({ id: 3, title: 'Test', description: 'Test' })).toBe(false);
    });

    it('should add and remove themes from loading state', () => {
      // Act
      component.addToLoading(1);
      component.addToLoading(2);

      // Assert
      expect(component.loadingThemeIds()).toContain(1);
      expect(component.loadingThemeIds()).toContain(2);

      // Act
      component.removeFromLoading(1);

      // Assert
      expect(component.loadingThemeIds()).not.toContain(1);
      expect(component.loadingThemeIds()).toContain(2);
    });
  });

  describe('Navigation', () => {
    it('should navigate to profile', () => {
      // Act
      component.navigateToProfile();

      // Assert
      expect(router.navigate).toHaveBeenCalledWith(['/profile']);
    });

    it('should navigate to home', () => {
      // Act
      component.navigateToHome();

      // Assert
      expect(router.navigate).toHaveBeenCalledWith(['/home']);
    });
  });
});

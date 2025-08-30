import { TestBed, ComponentFixture } from '@angular/core/testing';
import { Router } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';

import { ThemesComponent } from './themes.component';
import { ThemeService, SubscriptionService } from '../../shared/services';
import { AuthService } from '../auth/auth.service';
import { ConfigService } from '../../core/services/config.service';
import { Theme, Subscription } from '../../shared/interfaces';

describe.skip('ThemesComponent', () => {
  let component: ThemesComponent;
  let fixture: ComponentFixture<ThemesComponent>;
  let themeService: jest.Mocked<ThemeService>;
  let subscriptionService: jest.Mocked<SubscriptionService>;
  let authService: jest.Mocked<AuthService>;
  let router: jest.Mocked<Router>;

  // Mock data
  const mockThemes: Theme[] = [
    {
      id: 1,
      title: 'Angular',
      description: 'Framework frontend',
      createdAt: '2024-01-01T00:00:00Z',
      updatedAt: '2024-01-01T00:00:00Z'
    },
    {
      id: 2,
      title: 'Spring Boot',
      description: 'Framework backend',
      createdAt: '2024-01-01T00:00:00Z',
      updatedAt: '2024-01-01T00:00:00Z'
    }
  ];

  const mockSubscriptions: Subscription[] = [
    {
      id: {
        userId: 1,
        themeId: 1
      },
      user: {
        id: 1,
        username: 'testuser',
        email: 'test@example.com'
      },
      theme: {
        id: 1,
        title: 'Angular',
        description: 'Framework frontend'
      },
      createdAt: '2024-01-01T00:00:00Z'
    }
  ];

  beforeEach(async () => {
    const themeServiceMock = {
      getAllThemes: jest.fn()
    };

    const subscriptionServiceMock = {
      getUserSubscriptions: jest.fn(),
      subscribe: jest.fn(),
      isSubscribed: jest.fn()
    };

    const authServiceMock = {
      getCurrentUserId: jest.fn()
    };

    const routerMock = {
      navigate: jest.fn()
    };

    const configServiceMock = {
      endpoints: {
        themes: { all: '/api/themes' },
        subscriptions: { all: '/api/subscriptions' }
      }
    };

    await TestBed.configureTestingModule({
      imports: [
        ThemesComponent,
        HttpClientTestingModule
      ],
      providers: [
        { provide: ThemeService, useValue: themeServiceMock },
        { provide: SubscriptionService, useValue: subscriptionServiceMock },
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock },
        { provide: ConfigService, useValue: configServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ThemesComponent);
    component = fixture.componentInstance;
    
    themeService = TestBed.inject(ThemeService) as jest.Mocked<ThemeService>;
    subscriptionService = TestBed.inject(SubscriptionService) as jest.Mocked<SubscriptionService>;
    authService = TestBed.inject(AuthService) as jest.Mocked<AuthService>;
    router = TestBed.inject(Router) as jest.Mocked<Router>;

    // Setup default mock returns
    themeService.getAllThemes.mockReturnValue(of(mockThemes));
    subscriptionService.getUserSubscriptions.mockReturnValue(of(mockSubscriptions));
    subscriptionService.subscribe.mockReturnValue(of(mockSubscriptions[0]));
    subscriptionService.isSubscribed.mockReturnValue(false);
    authService.getCurrentUserId.mockReturnValue(1);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load themes on initialization', () => {
    fixture.detectChanges();
    expect(themeService.getAllThemes).toHaveBeenCalled();
    expect((component as any).themes()).toEqual(mockThemes);
    expect((component as any).isLoading()).toBe(false);
  });

  it('should load user subscriptions on initialization', () => {
    fixture.detectChanges();
    expect(subscriptionService.getUserSubscriptions).toHaveBeenCalled();
    expect((component as any).subscriptions()).toEqual(mockSubscriptions);
  });

  it('should handle themes loading error', () => {
    themeService.getAllThemes.mockReturnValue(throwError(() => new Error('API Error')));
    const consoleSpy = jest.spyOn(console, 'error').mockImplementation();

    fixture.detectChanges();

    expect((component as any).themes()).toEqual([]);
    expect((component as any).isLoading()).toBe(false);
    expect(consoleSpy).toHaveBeenCalledWith('Erreur lors du chargement des thèmes:', expect.any(Error));
    
    consoleSpy.mockRestore();
  });

  it('should handle subscriptions loading error', () => {
    subscriptionService.getUserSubscriptions.mockReturnValue(throwError(() => new Error('API Error')));
    const consoleSpy = jest.spyOn(console, 'error').mockImplementation();

    fixture.detectChanges();

    expect((component as any).subscriptions()).toEqual([]);
    expect(consoleSpy).toHaveBeenCalledWith('Erreur lors du chargement des abonnements:', expect.any(Error));
    
    consoleSpy.mockRestore();
  });

  it('should check if user is subscribed to theme', () => {
    subscriptionService.isSubscribed.mockReturnValue(true);
    fixture.detectChanges();

    expect((component as any).isSubscribed(1)).toBe(true);
    expect(subscriptionService.isSubscribed).toHaveBeenCalledWith(1, mockSubscriptions);
  });

  it('should check if subscription is loading', () => {
    fixture.detectChanges();
    
    // Initially no loading
    expect((component as any).isSubscriptionLoading(1)).toBe(false);

    // Manually set loading state for test
    const loading = new Set([1]);
    (component as any).loadingSubscriptions.set(loading);
    
    expect((component as any).isSubscriptionLoading(1)).toBe(true);
    expect((component as any).isSubscriptionLoading(2)).toBe(false);
  });

  it('should subscribe to theme successfully', () => {
    fixture.detectChanges();
    
    const mockEvent = { stopPropagation: jest.fn() } as any;
    const theme = mockThemes[0];
    const initialSubscriptionsCount = (component as any).subscriptions().length;

    (component as any).subscribeToTheme(mockEvent, theme);

    expect(mockEvent.stopPropagation).toHaveBeenCalled();
    expect(authService.getCurrentUserId).toHaveBeenCalled();
    expect(subscriptionService.subscribe).toHaveBeenCalledWith(theme.id, 1);
    expect((component as any).subscriptions().length).toBe(initialSubscriptionsCount + 1);
  });

  it('should handle subscription error', () => {
    subscriptionService.subscribe.mockReturnValue(throwError(() => new Error('Subscription failed')));
    const consoleSpy = jest.spyOn(console, 'error').mockImplementation();
    fixture.detectChanges();
    
    const mockEvent = { stopPropagation: jest.fn() } as any;
    const theme = mockThemes[0];

    (component as any).subscribeToTheme(mockEvent, theme);

    expect(subscriptionService.subscribe).toHaveBeenCalledWith(theme.id, 1);
    expect((component as any).isSubscriptionLoading(theme.id)).toBe(false);
    expect(consoleSpy).toHaveBeenCalledWith('Erreur lors de l\'abonnement:', expect.any(Error));
    
    consoleSpy.mockRestore();
  });

  it('should not subscribe when user is not logged in', () => {
    authService.getCurrentUserId.mockReturnValue(null);
    const consoleSpy = jest.spyOn(console, 'error').mockImplementation();
    fixture.detectChanges();
    
    const mockEvent = { stopPropagation: jest.fn() } as any;
    const theme = mockThemes[0];

    (component as any).subscribeToTheme(mockEvent, theme);

    expect(subscriptionService.subscribe).not.toHaveBeenCalled();
    expect(consoleSpy).toHaveBeenCalledWith('Utilisateur non connecté');
    
    consoleSpy.mockRestore();
  });

  it('should navigate to articles with theme filter on theme click', () => {
    fixture.detectChanges();
    
    const theme = mockThemes[0];

    (component as any).onThemeClick(theme);

    expect(router.navigate).toHaveBeenCalledWith(['/articles'], {
      queryParams: { themeId: theme.id }
    });
  });

  it('should set loading state properly during subscription', () => {
    fixture.detectChanges();
    
    const mockEvent = { stopPropagation: jest.fn() } as any;
    const theme = mockThemes[0];

    // Initially not loading
    expect((component as any).isSubscriptionLoading(theme.id)).toBe(false);

    // Start subscription to trigger loading state
    (component as any).subscribeToTheme(mockEvent, theme);
    
    expect(subscriptionService.subscribe).toHaveBeenCalledWith(theme.id, 1);
    // After successful subscription, loading should be removed
    expect((component as any).isSubscriptionLoading(theme.id)).toBe(false);
  });

  it('should update subscriptions list after successful subscription', () => {
    const newSubscription: Subscription = {
      id: {
        userId: 1,
        themeId: 3
      },
      user: {
        id: 1,
        username: 'testuser',
        email: 'test@example.com'
      },
      theme: {
        id: 3,
        title: 'New Theme',
        description: 'New description'
      },
      createdAt: '2024-01-01T00:00:00Z'
    };
    
    subscriptionService.subscribe.mockReturnValue(of(newSubscription));
    fixture.detectChanges();
    
    const mockEvent = { stopPropagation: jest.fn() } as any;
    const theme = { ...mockThemes[0], id: 3, title: 'New Theme' };
    
    const initialCount = (component as any).subscriptions().length;
    (component as any).subscribeToTheme(mockEvent, theme);

    expect((component as any).subscriptions().length).toBe(initialCount + 1);
    expect((component as any).subscriptions()).toContain(newSubscription);
  });

  it('should remove theme from loading after subscription', () => {
    fixture.detectChanges();
    
    const mockEvent = { stopPropagation: jest.fn() } as any;
    const theme = mockThemes[0];

    // Start subscription
    (component as any).subscribeToTheme(mockEvent, theme);

    // Verify loading was removed after successful subscription
    expect((component as any).isSubscriptionLoading(theme.id)).toBe(false);
  });

  it('should handle multiple themes in loading state', () => {
    fixture.detectChanges();
    
    // Set multiple themes as loading
    const loading = new Set([1, 2, 3]);
    (component as any).loadingSubscriptions.set(loading);

    expect((component as any).isSubscriptionLoading(1)).toBe(true);
    expect((component as any).isSubscriptionLoading(2)).toBe(true);
    expect((component as any).isSubscriptionLoading(3)).toBe(true);
    expect((component as any).isSubscriptionLoading(4)).toBe(false);
  });
});

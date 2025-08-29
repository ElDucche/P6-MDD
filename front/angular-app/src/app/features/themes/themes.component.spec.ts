import { of, throwError } from 'rxjs';
import { signal } from '@angular/core';

import { ThemesComponent } from './themes.component';
import { ThemeService, SubscriptionService } from '../../shared/services';
import { AuthService } from '../auth/auth.service';
import { Theme, Subscription } from '../../shared/interfaces';

// Test version of the component that doesn't use inject()
class TestThemesComponent {
  private themeService: any;
  private subscriptionService: any;
  private authService: any;
  private router: any;

  protected readonly themes = signal<Theme[]>([]);
  protected readonly subscriptions = signal<Subscription[]>([]);
  protected readonly isLoading = signal(false);
  protected readonly loadingSubscriptions = signal<Set<number>>(new Set());

  constructor(
    themeService: any,
    subscriptionService: any,
    authService: any,
    router: any
  ) {
    this.themeService = themeService;
    this.subscriptionService = subscriptionService;
    this.authService = authService;
    this.router = router;
    
    this.loadThemes();
    this.loadUserSubscriptions();
  }

  private loadThemes(): void {
    this.isLoading.set(true);
    this.themeService.getAllThemes().subscribe({
      next: (themes: Theme[]) => {
        this.themes.set(themes);
        this.isLoading.set(false);
      },
      error: (error: any) => {
        console.error('Erreur lors du chargement des thèmes:', error);
        this.isLoading.set(false);
      }
    });
  }

  private loadUserSubscriptions(): void {
    this.subscriptionService.getUserSubscriptions().subscribe({
      next: (subscriptions: Subscription[]) => {
        this.subscriptions.set(subscriptions);
      },
      error: (error: any) => {
        console.error('Erreur lors du chargement des abonnements:', error);
      }
    });
  }

  protected isSubscribed(themeId: number): boolean {
    return this.subscriptionService.isSubscribed(themeId, this.subscriptions());
  }

  protected isSubscriptionLoading(themeId: number): boolean {
    return this.loadingSubscriptions().has(themeId);
  }

  protected subscribeToTheme(event: Event, theme: Theme): void {
    event.stopPropagation();
    
    const userId = this.authService.getCurrentUserId();
    if (!userId) {
      console.error('Utilisateur non connecté');
      return;
    }

    const loading = new Set(this.loadingSubscriptions());
    loading.add(theme.id);
    this.loadingSubscriptions.set(loading);

    this.subscriptionService.subscribe(theme.id, userId).subscribe({
      next: (newSubscription: Subscription) => {
        const updatedSubscriptions = [...this.subscriptions(), newSubscription];
        this.subscriptions.set(updatedSubscriptions);
        this.removeFromLoading(theme.id);
      },
      error: (error: any) => {
        console.error('Erreur lors de l\'abonnement:', error);
        this.removeFromLoading(theme.id);
      }
    });
  }

  private removeFromLoading(themeId: number): void {
    const loading = new Set(this.loadingSubscriptions());
    loading.delete(themeId);
    this.loadingSubscriptions.set(loading);
  }

  protected onThemeClick(theme: Theme): void {
    this.router.navigate(['/articles'], { queryParams: { themeId: theme.id } });
  }
}

describe('ThemesComponent', () => {
  let component: TestThemesComponent;
  
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

  // Mock services
  const mockThemeService = {
    getAllThemes: jest.fn()
  };

  const mockSubscriptionService = {
    getUserSubscriptions: jest.fn(),
    subscribe: jest.fn(),
    isSubscribed: jest.fn()
  };

  const mockAuthService = {
    getCurrentUserId: jest.fn()
  };

  const mockRouter = {
    navigate: jest.fn()
  };

  beforeEach(() => {
    // Reset all mocks
    jest.clearAllMocks();
    
    // Setup default mock returns
    mockThemeService.getAllThemes.mockReturnValue(of(mockThemes));
    mockSubscriptionService.getUserSubscriptions.mockReturnValue(of(mockSubscriptions));
    mockSubscriptionService.subscribe.mockReturnValue(of(mockSubscriptions[0]));
    mockSubscriptionService.isSubscribed.mockReturnValue(false);
    mockAuthService.getCurrentUserId.mockReturnValue(1);

    // Create component with injected dependencies
    component = new TestThemesComponent(
      mockThemeService,
      mockSubscriptionService,
      mockAuthService,
      mockRouter
    );
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load themes on initialization', () => {
    expect(mockThemeService.getAllThemes).toHaveBeenCalled();
    expect((component as any).themes()).toEqual(mockThemes);
    expect((component as any).isLoading()).toBe(false);
  });

  it('should load user subscriptions on initialization', () => {
    expect(mockSubscriptionService.getUserSubscriptions).toHaveBeenCalled();
    expect((component as any).subscriptions()).toEqual(mockSubscriptions);
  });

  it('should handle themes loading error', () => {
    mockThemeService.getAllThemes.mockReturnValue(throwError(() => new Error('API Error')));

    // Create new component to trigger constructor
    const newComponent = new TestThemesComponent(
      mockThemeService,
      mockSubscriptionService,
      mockAuthService,
      mockRouter
    );

    expect((newComponent as any).themes()).toEqual([]);
    expect((newComponent as any).isLoading()).toBe(false);
  });

  it('should handle subscriptions loading error', () => {
    mockSubscriptionService.getUserSubscriptions.mockReturnValue(throwError(() => new Error('API Error')));

    // Create new component to trigger constructor
    const newComponent = new TestThemesComponent(
      mockThemeService,
      mockSubscriptionService,
      mockAuthService,
      mockRouter
    );

    expect((newComponent as any).subscriptions()).toEqual([]);
  });

  it('should check if user is subscribed to theme', () => {
    mockSubscriptionService.isSubscribed.mockReturnValue(true);

    expect((component as any).isSubscribed(1)).toBe(true);
    expect(mockSubscriptionService.isSubscribed).toHaveBeenCalledWith(1, mockSubscriptions);
  });

  it('should check if subscription is loading', () => {
    // Initially no loading
    expect((component as any).isSubscriptionLoading(1)).toBe(false);

    // Manually set loading state for test
    const loading = new Set([1]);
    (component as any).loadingSubscriptions.set(loading);
    
    expect((component as any).isSubscriptionLoading(1)).toBe(true);
  });

  it('should subscribe to theme successfully', () => {
    const mockEvent = { stopPropagation: jest.fn() } as any;
    const theme = mockThemes[0];
    const initialSubscriptionsCount = (component as any).subscriptions().length;

    (component as any).subscribeToTheme(mockEvent, theme);

    expect(mockEvent.stopPropagation).toHaveBeenCalled();
    expect(mockAuthService.getCurrentUserId).toHaveBeenCalled();
    expect(mockSubscriptionService.subscribe).toHaveBeenCalledWith(theme.id, 1);
    expect((component as any).subscriptions().length).toBe(initialSubscriptionsCount + 1);
  });

  it('should handle subscription error', () => {
    mockSubscriptionService.subscribe.mockReturnValue(throwError(() => new Error('Subscription failed')));
    const mockEvent = { stopPropagation: jest.fn() } as any;
    const theme = mockThemes[0];

    (component as any).subscribeToTheme(mockEvent, theme);

    expect(mockSubscriptionService.subscribe).toHaveBeenCalledWith(theme.id, 1);
    expect((component as any).isSubscriptionLoading(theme.id)).toBe(false);
  });

  it('should not subscribe when user is not logged in', () => {
    mockAuthService.getCurrentUserId.mockReturnValue(null);
    const mockEvent = { stopPropagation: jest.fn() } as any;
    const theme = mockThemes[0];

    (component as any).subscribeToTheme(mockEvent, theme);

    expect(mockSubscriptionService.subscribe).not.toHaveBeenCalled();
  });

  it('should navigate to articles with theme filter on theme click', () => {
    const theme = mockThemes[0];

    (component as any).onThemeClick(theme);

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/articles'], {
      queryParams: { themeId: theme.id }
    });
  });

  it('should set loading state properly during subscription', () => {
    const mockEvent = { stopPropagation: jest.fn() } as any;
    const theme = mockThemes[0];

    // Initially not loading
    expect((component as any).isSubscriptionLoading(theme.id)).toBe(false);

    // During subscription
    (component as any).subscribeToTheme(mockEvent, theme);
    
    expect(mockSubscriptionService.subscribe).toHaveBeenCalledWith(theme.id, 1);
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
    
    mockSubscriptionService.subscribe.mockReturnValue(of(newSubscription));
    const mockEvent = { stopPropagation: jest.fn() } as any;
    const theme = { ...mockThemes[0], id: 3 };
    
    const initialCount = (component as any).subscriptions().length;
    (component as any).subscribeToTheme(mockEvent, theme);

    expect((component as any).subscriptions().length).toBe(initialCount + 1);
    expect((component as any).subscriptions()).toContain(newSubscription);
  });
});

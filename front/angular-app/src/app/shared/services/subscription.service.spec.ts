import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { SubscriptionService } from './subscription.service';
import { ConfigService } from '../../core/services/config.service';
import { Subscription } from '../interfaces/subscription.interface';

describe('SubscriptionService', () => {
  let service: SubscriptionService;
  let httpMock: HttpTestingController;
  let mockConfigService: any;

  const mockSubscription: Subscription = {
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
      description: 'Angular development discussions'
    },
    createdAt: '2024-01-01T10:00:00Z'
  };

  const mockSubscriptions: Subscription[] = [
    mockSubscription,
    {
      id: {
        userId: 1,
        themeId: 2
      },
      user: {
        id: 1,
        username: 'testuser',
        email: 'test@example.com'
      },
      theme: {
        id: 2,
        title: 'React',
        description: 'React development discussions'
      },
      createdAt: '2024-01-01T11:00:00Z'
    }
  ];

  const mockEndpoints = {
    subscriptions: {
      all: '/api/subscriptions',
      byId: (id: number) => `/api/subscriptions/${id}`
    }
  };

  beforeEach(() => {
    mockConfigService = {
      endpoints: mockEndpoints
    };

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        SubscriptionService,
        { provide: ConfigService, useValue: mockConfigService }
      ]
    });

    service = TestBed.inject(SubscriptionService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('subscribe', () => {
    it('should subscribe to a theme successfully', () => {
      const themeId = 1;
      const userId = 1;

      service.subscribe(themeId, userId).subscribe(subscription => {
        expect(subscription).toEqual(mockSubscription);
        expect(subscription.theme.id).toBe(themeId);
        expect(subscription.user.id).toBe(userId);
        expect(subscription.id.themeId).toBe(themeId);
        expect(subscription.id.userId).toBe(userId);
      });

      const req = httpMock.expectOne('/api/subscriptions');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({ themeId });
      req.flush(mockSubscription);
    });

    it('should handle subscription to non-existent theme', () => {
      const themeId = 999;
      const userId = 1;

      service.subscribe(themeId, userId).subscribe({
        next: () => fail('should have failed with theme not found error'),
        error: (error) => {
          expect(error.status).toBe(404);
          expect(error.error.message).toBe('Theme not found');
        }
      });

      const req = httpMock.expectOne('/api/subscriptions');
      expect(req.request.method).toBe('POST');
      req.flush(
        { message: 'Theme not found' }, 
        { status: 404, statusText: 'Not Found' }
      );
    });

    it('should handle duplicate subscription', () => {
      const themeId = 1;
      const userId = 1;

      service.subscribe(themeId, userId).subscribe({
        next: () => fail('should have failed with conflict error'),
        error: (error) => {
          expect(error.status).toBe(409);
          expect(error.error.message).toBe('Already subscribed to this theme');
        }
      });

      const req = httpMock.expectOne('/api/subscriptions');
      req.flush(
        { message: 'Already subscribed to this theme' }, 
        { status: 409, statusText: 'Conflict' }
      );
    });

    it('should handle unauthorized subscription', () => {
      const themeId = 1;
      const userId = 1;

      service.subscribe(themeId, userId).subscribe({
        next: () => fail('should have failed with unauthorized error'),
        error: (error) => {
          expect(error.status).toBe(401);
        }
      });

      const req = httpMock.expectOne('/api/subscriptions');
      req.flush({}, { status: 401, statusText: 'Unauthorized' });
    });

    it('should handle invalid theme ID (negative)', () => {
      const themeId = -1;
      const userId = 1;

      service.subscribe(themeId, userId).subscribe({
        next: () => fail('should have failed with bad request'),
        error: (error) => {
          expect(error.status).toBe(400);
        }
      });

      const req = httpMock.expectOne('/api/subscriptions');
      req.flush({}, { status: 400, statusText: 'Bad Request' });
    });
  });

  describe('unsubscribe', () => {
    it('should unsubscribe from a theme successfully', () => {
      const subscriptionId = 1;

      service.unsubscribe(subscriptionId).subscribe(response => {
        expect(response).toBeUndefined(); // Void response
      });

      const req = httpMock.expectOne('/api/subscriptions/1');
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });

    it('should handle unsubscribe from non-existent subscription', () => {
      const subscriptionId = 999;

      service.unsubscribe(subscriptionId).subscribe({
        next: () => fail('should have failed with not found error'),
        error: (error) => {
          expect(error.status).toBe(404);
          expect(error.error.message).toBe('Subscription not found');
        }
      });

      const req = httpMock.expectOne('/api/subscriptions/999');
      expect(req.request.method).toBe('DELETE');
      req.flush(
        { message: 'Subscription not found' }, 
        { status: 404, statusText: 'Not Found' }
      );
    });

    it('should handle unauthorized unsubscribe', () => {
      const subscriptionId = 1;

      service.unsubscribe(subscriptionId).subscribe({
        next: () => fail('should have failed with unauthorized error'),
        error: (error) => {
          expect(error.status).toBe(401);
        }
      });

      const req = httpMock.expectOne('/api/subscriptions/1');
      req.flush({}, { status: 401, statusText: 'Unauthorized' });
    });

    it('should handle invalid subscription ID (negative)', () => {
      const subscriptionId = -1;

      service.unsubscribe(subscriptionId).subscribe({
        next: () => fail('should have failed with bad request'),
        error: (error) => {
          expect(error.status).toBe(400);
        }
      });

      const req = httpMock.expectOne('/api/subscriptions/-1');
      req.flush({}, { status: 400, statusText: 'Bad Request' });
    });

    it('should use correct endpoint with subscription ID', () => {
      const subscriptionId = 42;

      service.unsubscribe(subscriptionId).subscribe();

      const req = httpMock.expectOne('/api/subscriptions/42');
      expect(req.request.url).toBe('/api/subscriptions/42');
    });
  });

  describe('getUserSubscriptions', () => {
    it('should retrieve user subscriptions', () => {
      service.getUserSubscriptions().subscribe(subscriptions => {
        expect(subscriptions).toEqual(mockSubscriptions);
        expect(subscriptions.length).toBe(2);
        expect(subscriptions[0].theme.title).toBe('Angular');
        expect(subscriptions[1].theme.title).toBe('React');
      });

      const req = httpMock.expectOne('/api/subscriptions');
      expect(req.request.method).toBe('GET');
      req.flush(mockSubscriptions);
    });

    it('should handle empty subscriptions', () => {
      service.getUserSubscriptions().subscribe(subscriptions => {
        expect(subscriptions).toEqual([]);
        expect(subscriptions.length).toBe(0);
      });

      const req = httpMock.expectOne('/api/subscriptions');
      req.flush([]);
    });

    it('should handle unauthorized access to subscriptions', () => {
      service.getUserSubscriptions().subscribe({
        next: () => fail('should have failed with unauthorized error'),
        error: (error) => {
          expect(error.status).toBe(401);
        }
      });

      const req = httpMock.expectOne('/api/subscriptions');
      req.flush({}, { status: 401, statusText: 'Unauthorized' });
    });

    it('should handle server error when getting subscriptions', () => {
      service.getUserSubscriptions().subscribe({
        next: () => fail('should have failed with server error'),
        error: (error) => {
          expect(error.status).toBe(500);
        }
      });

      const req = httpMock.expectOne('/api/subscriptions');
      req.flush({}, { status: 500, statusText: 'Internal Server Error' });
    });
  });

  describe('isSubscribed', () => {
    it('should return true when user is subscribed to theme', () => {
      const themeId = 1;
      const result = service.isSubscribed(themeId, mockSubscriptions);
      
      expect(result).toBe(true);
    });

    it('should return false when user is not subscribed to theme', () => {
      const themeId = 999; // Non-existent theme
      const result = service.isSubscribed(themeId, mockSubscriptions);
      
      expect(result).toBe(false);
    });

    it('should return false with empty subscriptions array', () => {
      const themeId = 1;
      const result = service.isSubscribed(themeId, []);
      
      expect(result).toBe(false);
    });

    it('should handle multiple subscriptions correctly', () => {
      const themeId1 = 1;
      const themeId2 = 2;
      const themeId3 = 3;
      
      expect(service.isSubscribed(themeId1, mockSubscriptions)).toBe(true);
      expect(service.isSubscribed(themeId2, mockSubscriptions)).toBe(true);
      expect(service.isSubscribed(themeId3, mockSubscriptions)).toBe(false);
    });

    it('should handle edge case with zero theme ID', () => {
      const themeId = 0;
      const result = service.isSubscribed(themeId, mockSubscriptions);
      
      expect(result).toBe(false);
    });

    it('should handle negative theme ID', () => {
      const themeId = -1;
      const result = service.isSubscribed(themeId, mockSubscriptions);
      
      expect(result).toBe(false);
    });
  });

  describe('findSubscriptionByThemeId', () => {
    it('should find subscription when user is subscribed to theme', () => {
      const themeId = 1;
      const result = service.findSubscriptionByThemeId(themeId, mockSubscriptions);
      
      expect(result).toEqual(mockSubscriptions[0]);
      expect(result?.theme.id).toBe(themeId);
      expect(result?.theme.title).toBe('Angular');
    });

    it('should return undefined when user is not subscribed to theme', () => {
      const themeId = 999; // Non-existent theme
      const result = service.findSubscriptionByThemeId(themeId, mockSubscriptions);
      
      expect(result).toBeUndefined();
    });

    it('should return undefined with empty subscriptions array', () => {
      const themeId = 1;
      const result = service.findSubscriptionByThemeId(themeId, []);
      
      expect(result).toBeUndefined();
    });

    it('should find correct subscription among multiple subscriptions', () => {
      const themeId2 = 2;
      const result = service.findSubscriptionByThemeId(themeId2, mockSubscriptions);
      
      expect(result).toEqual(mockSubscriptions[1]);
      expect(result?.theme.id).toBe(themeId2);
      expect(result?.theme.title).toBe('React');
    });

    it('should handle edge case with zero theme ID', () => {
      const themeId = 0;
      const result = service.findSubscriptionByThemeId(themeId, mockSubscriptions);
      
      expect(result).toBeUndefined();
    });

    it('should handle negative theme ID', () => {
      const themeId = -1;
      const result = service.findSubscriptionByThemeId(themeId, mockSubscriptions);
      
      expect(result).toBeUndefined();
    });
  });

  describe('Integration scenarios', () => {
    it('should handle network errors', () => {
      const networkError = new ProgressEvent('error');

      service.getUserSubscriptions().subscribe({
        next: () => fail('should have failed with network error'),
        error: (error) => {
          expect(error.error).toBe(networkError);
        }
      });

      const req = httpMock.expectOne('/api/subscriptions');
      req.error(networkError);
    });

    it('should handle malformed subscription response', () => {
      const malformedSubscription = {
        id: {
          userId: 1,
          themeId: 1
        },
        theme: {
          id: 1,
          title: 'Angular'
          // Missing description
        }
        // Missing user and createdAt
      };

      service.getUserSubscriptions().subscribe(subscriptions => {
        expect(subscriptions[0].id.userId).toBe(1);
        expect(subscriptions[0].theme.title).toBe('Angular');
        expect(subscriptions[0].user).toBeUndefined();
        expect(subscriptions[0].createdAt).toBeUndefined();
      });

      const req = httpMock.expectOne('/api/subscriptions');
      req.flush([malformedSubscription]);
    });

    it('should use config service endpoints correctly', () => {
      // Test multiple endpoints to ensure config service integration
      service.getUserSubscriptions().subscribe();
      service.subscribe(1, 1).subscribe();
      service.unsubscribe(1).subscribe();

      const requests = httpMock.match(() => true);
      expect(requests.length).toBe(3);
      expect(requests[0].request.url).toBe('/api/subscriptions');
      expect(requests[0].request.method).toBe('GET');
      expect(requests[1].request.url).toBe('/api/subscriptions');
      expect(requests[1].request.method).toBe('POST');
      expect(requests[2].request.url).toBe('/api/subscriptions/1');
      expect(requests[2].request.method).toBe('DELETE');

      requests[0].flush(mockSubscriptions);
      requests[1].flush(mockSubscription);
      requests[2].flush(null);
    });

    it('should handle subscription workflow (subscribe, check, unsubscribe)', () => {
      const themeId = 3;
      const userId = 1;
      
      // Initial check - not subscribed
      expect(service.isSubscribed(themeId, mockSubscriptions)).toBe(false);
      expect(service.findSubscriptionByThemeId(themeId, mockSubscriptions)).toBeUndefined();

      // After subscription
      const newSubscription: Subscription = {
        id: { userId, themeId },
        user: mockSubscription.user,
        theme: { id: themeId, title: 'Vue.js', description: 'Vue.js discussions' },
        createdAt: '2024-01-01T12:00:00Z'
      };
      
      const updatedSubscriptions = [...mockSubscriptions, newSubscription];
      
      expect(service.isSubscribed(themeId, updatedSubscriptions)).toBe(true);
      expect(service.findSubscriptionByThemeId(themeId, updatedSubscriptions)).toEqual(newSubscription);
    });
  });
});

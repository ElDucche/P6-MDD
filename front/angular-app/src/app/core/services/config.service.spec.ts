import { TestBed } from '@angular/core/testing';
import { ConfigService } from './config.service';

describe('ConfigService', () => {
  let service: ConfigService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ConfigService]
    });

    service = TestBed.inject(ConfigService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('API URL functionality', () => {
    it('should return API URL', () => {
      expect(service.apiUrl).toBeDefined();
      expect(typeof service.apiUrl).toBe('string');
      expect(service.apiUrl.length).toBeGreaterThan(0);
    });

    it('should build correct API endpoint path', () => {
      const endpoint = service.getApiEndpoint('users');
      expect(endpoint).toBe(`${service.apiUrl}/api/users`);
    });

    it('should handle endpoint path with leading slash', () => {
      const endpoint = service.getApiEndpoint('/users');
      expect(endpoint).toBe(`${service.apiUrl}/api/users`);
    });

    it('should handle endpoint path with trailing slash', () => {
      const endpoint = service.getApiEndpoint('users/');
      expect(endpoint).toBe(`${service.apiUrl}/api/users/`);
    });

    it('should handle complex endpoint paths', () => {
      const endpoint = service.getApiEndpoint('users/profile/settings');
      expect(endpoint).toBe(`${service.apiUrl}/api/users/profile/settings`);
    });

    it('should handle empty endpoint path', () => {
      const endpoint = service.getApiEndpoint('');
      expect(endpoint).toBe(`${service.apiUrl}/api/`);
    });
  });

  describe('Environment properties', () => {
    it('should provide production flag', () => {
      expect(typeof service.isProduction).toBe('boolean');
    });

    it('should provide logs flag', () => {
      expect(typeof service.enableLogs).toBe('boolean');
    });

    it('should have opposite values for production and logs in development', () => {
      // In development: isProduction = false, enableLogs = true
      // In production: isProduction = true, enableLogs = false
      expect(service.isProduction).toBe(!service.enableLogs);
    });
  });

  describe('Auth endpoints', () => {
    it('should provide correct login endpoint', () => {
      expect(service.endpoints.auth.login).toBe(`${service.apiUrl}/api/auth/login`);
    });

    it('should provide correct register endpoint', () => {
      expect(service.endpoints.auth.register).toBe(`${service.apiUrl}/api/auth/register`);
    });
  });

  describe('User endpoints', () => {
    it('should provide correct user me endpoint', () => {
      expect(service.endpoints.users.me).toBe(`${service.apiUrl}/api/users/me`);
    });

    it('should provide correct user by ID endpoint', () => {
      expect(service.endpoints.users.byId(123)).toBe(`${service.apiUrl}/api/users/123`);
    });

    it('should handle zero user ID', () => {
      expect(service.endpoints.users.byId(0)).toBe(`${service.apiUrl}/api/users/0`);
    });

    it('should handle negative user ID', () => {
      expect(service.endpoints.users.byId(-1)).toBe(`${service.apiUrl}/api/users/-1`);
    });

    it('should handle large user ID numbers', () => {
      expect(service.endpoints.users.byId(999999)).toBe(`${service.apiUrl}/api/users/999999`);
    });
  });

  describe('Post endpoints', () => {
    it('should provide correct posts all endpoint', () => {
      expect(service.endpoints.posts.all).toBe(`${service.apiUrl}/api/posts`);
    });

    it('should provide correct posts subscribed endpoint', () => {
      expect(service.endpoints.posts.subscribed).toBe(`${service.apiUrl}/api/posts/subscribed`);
    });

    it('should provide correct posts by theme endpoint', () => {
      expect(service.endpoints.posts.byTheme(5)).toBe(`${service.apiUrl}/api/posts/theme/5`);
    });

    it('should provide correct post by ID endpoint', () => {
      expect(service.endpoints.posts.byId(42)).toBe(`${service.apiUrl}/api/posts/42`);
    });

    it('should handle large post ID numbers', () => {
      expect(service.endpoints.posts.byId(999999)).toBe(`${service.apiUrl}/api/posts/999999`);
    });

    it('should handle zero theme ID', () => {
      expect(service.endpoints.posts.byTheme(0)).toBe(`${service.apiUrl}/api/posts/theme/0`);
    });
  });

  describe('Theme endpoints', () => {
    it('should provide correct themes all endpoint', () => {
      expect(service.endpoints.themes.all).toBe(`${service.apiUrl}/api/themes`);
    });
  });

  describe('Subscription endpoints', () => {
    it('should provide correct subscriptions all endpoint', () => {
      expect(service.endpoints.subscriptions.all).toBe(`${service.apiUrl}/api/subscriptions`);
    });

    it('should provide correct subscription by ID endpoint', () => {
      expect(service.endpoints.subscriptions.byId(10)).toBe(`${service.apiUrl}/api/subscriptions/10`);
    });

    it('should handle zero subscription ID', () => {
      expect(service.endpoints.subscriptions.byId(0)).toBe(`${service.apiUrl}/api/subscriptions/0`);
    });
  });

  describe('Comment endpoints', () => {
    it('should provide correct comments all endpoint', () => {
      expect(service.endpoints.comments.all).toBe(`${service.apiUrl}/api/comments`);
    });

    it('should provide correct comments by post endpoint', () => {
      expect(service.endpoints.comments.byPost(7)).toBe(`${service.apiUrl}/api/comments/post/7`);
    });

    it('should handle zero post ID for comments', () => {
      expect(service.endpoints.comments.byPost(0)).toBe(`${service.apiUrl}/api/comments/post/0`);
    });
  });

  describe('Endpoint Function Parameters', () => {
    it('should handle string numbers in endpoint functions', () => {
      expect(service.endpoints.users.byId(Number('123'))).toBe(`${service.apiUrl}/api/users/123`);
    });

    it('should handle decimal numbers in endpoint functions', () => {
      expect(service.endpoints.posts.byId(42.5)).toBe(`${service.apiUrl}/api/posts/42.5`);
    });

    it('should handle very large numbers', () => {
      const largeNumber = Number.MAX_SAFE_INTEGER;
      expect(service.endpoints.users.byId(largeNumber)).toBe(`${service.apiUrl}/api/users/${largeNumber}`);
    });
  });

  describe('Integration and Consistency', () => {
    it('should maintain consistency across all endpoint methods', () => {
      const baseUrl = service.apiUrl;
      
      // Verify all endpoints start with the same base URL
      expect(service.endpoints.auth.login.startsWith(baseUrl)).toBe(true);
      expect(service.endpoints.users.me.startsWith(baseUrl)).toBe(true);
      expect(service.endpoints.posts.all.startsWith(baseUrl)).toBe(true);
      expect(service.endpoints.themes.all.startsWith(baseUrl)).toBe(true);
      expect(service.endpoints.subscriptions.all.startsWith(baseUrl)).toBe(true);
      expect(service.endpoints.comments.all.startsWith(baseUrl)).toBe(true);
    });

    it('should maintain consistent API path structure', () => {
      // All endpoints should have /api/ in their path
      expect(service.endpoints.auth.login.includes('/api/')).toBe(true);
      expect(service.endpoints.users.me.includes('/api/')).toBe(true);
      expect(service.endpoints.posts.all.includes('/api/')).toBe(true);
      expect(service.endpoints.themes.all.includes('/api/')).toBe(true);
      expect(service.endpoints.subscriptions.all.includes('/api/')).toBe(true);
      expect(service.endpoints.comments.all.includes('/api/')).toBe(true);
    });

    it('should provide all required endpoint categories', () => {
      const endpoints = service.endpoints;
      
      expect(endpoints).toHaveProperty('auth');
      expect(endpoints).toHaveProperty('users');
      expect(endpoints).toHaveProperty('posts');
      expect(endpoints).toHaveProperty('themes');
      expect(endpoints).toHaveProperty('subscriptions');
      expect(endpoints).toHaveProperty('comments');
    });

    it('should provide all required endpoint functions', () => {
      const endpoints = service.endpoints;
      
      // Auth endpoints
      expect(endpoints.auth).toHaveProperty('login');
      expect(endpoints.auth).toHaveProperty('register');
      
      // User endpoints
      expect(endpoints.users).toHaveProperty('me');
      expect(typeof endpoints.users.byId).toBe('function');
      
      // Post endpoints
      expect(endpoints.posts).toHaveProperty('all');
      expect(endpoints.posts).toHaveProperty('subscribed');
      expect(typeof endpoints.posts.byTheme).toBe('function');
      expect(typeof endpoints.posts.byId).toBe('function');
      
      // Theme endpoints  
      expect(endpoints.themes).toHaveProperty('all');
      
      // Subscription endpoints
      expect(endpoints.subscriptions).toHaveProperty('all');
      expect(typeof endpoints.subscriptions.byId).toBe('function');
      
      // Comment endpoints
      expect(endpoints.comments).toHaveProperty('all');
      expect(typeof endpoints.comments.byPost).toBe('function');
    });

    it('should build endpoints correctly using getApiEndpoint method', () => {
      // Test that endpoints use the same base URL as direct getApiEndpoint calls
      expect(service.endpoints.auth.login).toBe(service.getApiEndpoint('auth/login'));
      expect(service.endpoints.users.me).toBe(service.getApiEndpoint('users/me'));
      expect(service.endpoints.posts.all).toBe(service.getApiEndpoint('posts'));
      expect(service.endpoints.themes.all).toBe(service.getApiEndpoint('themes'));
      expect(service.endpoints.subscriptions.all).toBe(service.getApiEndpoint('subscriptions'));
      expect(service.endpoints.comments.all).toBe(service.getApiEndpoint('comments'));
    });

    it('should provide consistent parameterized endpoints', () => {
      // Test that parameterized endpoints follow the same pattern
      expect(service.endpoints.users.byId(1)).toBe(service.getApiEndpoint('users/1'));
      expect(service.endpoints.posts.byId(1)).toBe(service.getApiEndpoint('posts/1'));
      expect(service.endpoints.posts.byTheme(1)).toBe(service.getApiEndpoint('posts/theme/1'));
      expect(service.endpoints.subscriptions.byId(1)).toBe(service.getApiEndpoint('subscriptions/1'));
      expect(service.endpoints.comments.byPost(1)).toBe(service.getApiEndpoint('comments/post/1'));
    });
  });

  describe('Edge cases and error handling', () => {
    it('should handle special characters in paths', () => {
      const endpoint = service.getApiEndpoint('special-path_with.chars');
      expect(endpoint).toBe(`${service.apiUrl}/api/special-path_with.chars`);
    });

    it('should handle paths with multiple slashes', () => {
      const endpoint = service.getApiEndpoint('users//profile');
      expect(endpoint).toBe(`${service.apiUrl}/api/users//profile`);
    });

    it('should handle very long paths', () => {
      const longPath = 'a'.repeat(100);
      const endpoint = service.getApiEndpoint(longPath);
      expect(endpoint).toBe(`${service.apiUrl}/api/${longPath}`);
    });

    it('should handle numeric strings in paths', () => {
      const endpoint = service.getApiEndpoint('123/456');
      expect(endpoint).toBe(`${service.apiUrl}/api/123/456`);
    });
  });
});

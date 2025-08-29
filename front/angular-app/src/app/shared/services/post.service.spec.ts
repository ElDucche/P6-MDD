import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { PostService } from './post.service';
import { ConfigService } from '../../core/services/config.service';
import { Post, PostCreateRequest } from '../interfaces/post.interface';

describe('PostService', () => {
  let service: PostService;
  let httpMock: HttpTestingController;
  let mockConfigService: any;

  const mockPost: Post = {
    id: 1,
    title: 'Test Post',
    content: 'This is a test post content',
    author: {
      id: 1,
      username: 'testuser',
      email: 'test@example.com'
    },
    theme: {
      id: 1,
      title: 'Angular',
      description: 'Angular development discussions'
    },
    createdAt: '2024-01-01T10:00:00Z',
    updatedAt: '2024-01-01T10:00:00Z'
  };

  const mockPosts: Post[] = [
    mockPost,
    {
      ...mockPost,
      id: 2,
      title: 'Second Post',
      content: 'Another test post',
      theme: {
        id: 2,
        title: 'React',
        description: 'React development discussions'
      }
    }
  ];

  const mockEndpoints = {
    posts: {
      all: '/api/posts',
      subscribed: '/api/posts/subscribed',
      byTheme: (themeId: number) => `/api/posts/theme/${themeId}`,
      byId: (id: number) => `/api/posts/${id}`
    }
  };

  beforeEach(() => {
    mockConfigService = {
      endpoints: mockEndpoints
    };

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        PostService,
        { provide: ConfigService, useValue: mockConfigService }
      ]
    });

    service = TestBed.inject(PostService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getAllPosts', () => {
    it('should retrieve all posts', () => {
      service.getAllPosts().subscribe(posts => {
        expect(posts).toEqual(mockPosts);
        expect(posts.length).toBe(2);
        expect(posts[0].title).toBe('Test Post');
        expect(posts[1].title).toBe('Second Post');
      });

      const req = httpMock.expectOne('/api/posts');
      expect(req.request.method).toBe('GET');
      req.flush(mockPosts);
    });

    it('should handle empty posts array', () => {
      service.getAllPosts().subscribe(posts => {
        expect(posts).toEqual([]);
        expect(posts.length).toBe(0);
      });

      const req = httpMock.expectOne('/api/posts');
      req.flush([]);
    });

    it('should handle server error when getting all posts', () => {
      service.getAllPosts().subscribe({
        next: () => fail('should have failed with server error'),
        error: (error) => {
          expect(error.status).toBe(500);
        }
      });

      const req = httpMock.expectOne('/api/posts');
      req.flush({}, { status: 500, statusText: 'Internal Server Error' });
    });
  });

  describe('getPostsByTheme', () => {
    it('should retrieve posts by theme ID', () => {
      const themeId = 1;
      const themePosts = [mockPost]; // Only posts with theme ID 1

      service.getPostsByTheme(themeId).subscribe(posts => {
        expect(posts).toEqual(themePosts);
        expect(posts.length).toBe(1);
        expect(posts[0].theme.id).toBe(themeId);
        expect(posts[0].theme.title).toBe('Angular');
      });

      const req = httpMock.expectOne('/api/posts/theme/1');
      expect(req.request.method).toBe('GET');
      req.flush(themePosts);
    });

    it('should handle empty theme posts', () => {
      const themeId = 999; // Non-existent theme

      service.getPostsByTheme(themeId).subscribe(posts => {
        expect(posts).toEqual([]);
      });

      const req = httpMock.expectOne('/api/posts/theme/999');
      req.flush([]);
    });

    it('should handle invalid theme ID', () => {
      const themeId = -1;

      service.getPostsByTheme(themeId).subscribe({
        next: () => fail('should have failed with bad request'),
        error: (error) => {
          expect(error.status).toBe(400);
        }
      });

      const req = httpMock.expectOne('/api/posts/theme/-1');
      req.flush({}, { status: 400, statusText: 'Bad Request' });
    });

    it('should use correct endpoint with theme ID', () => {
      const themeId = 42;

      service.getPostsByTheme(themeId).subscribe();

      const req = httpMock.expectOne('/api/posts/theme/42');
      expect(req.request.url).toBe('/api/posts/theme/42');
    });
  });

  describe('getPostById', () => {
    it('should retrieve specific post by ID', () => {
      const postId = 1;

      service.getPostById(postId).subscribe(post => {
        expect(post).toEqual(mockPost);
        expect(post.id).toBe(postId);
        expect(post.title).toBe('Test Post');
        expect(post.author.username).toBe('testuser');
        expect(post.theme.title).toBe('Angular');
      });

      const req = httpMock.expectOne('/api/posts/1');
      expect(req.request.method).toBe('GET');
      req.flush(mockPost);
    });

    it('should handle post not found', () => {
      const postId = 999;

      service.getPostById(postId).subscribe({
        next: () => fail('should have failed with not found error'),
        error: (error) => {
          expect(error.status).toBe(404);
          expect(error.error.message).toBe('Post not found');
        }
      });

      const req = httpMock.expectOne('/api/posts/999');
      req.flush(
        { message: 'Post not found' }, 
        { status: 404, statusText: 'Not Found' }
      );
    });

    it('should handle unauthorized access to post', () => {
      const postId = 1;

      service.getPostById(postId).subscribe({
        next: () => fail('should have failed with unauthorized error'),
        error: (error) => {
          expect(error.status).toBe(401);
        }
      });

      const req = httpMock.expectOne('/api/posts/1');
      req.flush({}, { status: 401, statusText: 'Unauthorized' });
    });

    it('should use correct endpoint with post ID', () => {
      const postId = 123;

      service.getPostById(postId).subscribe();

      const req = httpMock.expectOne('/api/posts/123');
      expect(req.request.url).toBe('/api/posts/123');
    });
  });

  describe('createPost', () => {
    it('should create a new post successfully', () => {
      const createRequest: PostCreateRequest = {
        title: 'New Post',
        content: 'This is a new post content',
        themeId: 1
      };

      const createdPost: Post = {
        id: 3,
        title: createRequest.title,
        content: createRequest.content,
        author: mockPost.author,
        theme: mockPost.theme,
        createdAt: '2024-01-01T12:00:00Z',
        updatedAt: '2024-01-01T12:00:00Z'
      };

      service.createPost(createRequest).subscribe(post => {
        expect(post).toEqual(createdPost);
        expect(post.id).toBe(3);
        expect(post.title).toBe('New Post');
        expect(post.content).toBe('This is a new post content');
        expect(post.theme.id).toBe(1);
      });

      const req = httpMock.expectOne('/api/posts');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(createRequest);
      req.flush(createdPost);
    });

    it('should handle validation errors on post creation', () => {
      const invalidCreateRequest: PostCreateRequest = {
        title: '', // Empty title
        content: 'Content',
        themeId: 1
      };

      const errorResponse = {
        message: 'Validation failed',
        errors: {
          title: ['Title is required', 'Title must not be empty']
        }
      };

      service.createPost(invalidCreateRequest).subscribe({
        next: () => fail('should have failed with validation error'),
        error: (error) => {
          expect(error.status).toBe(400);
          expect(error.error.message).toBe('Validation failed');
          expect(error.error.errors.title).toContain('Title is required');
        }
      });

      const req = httpMock.expectOne('/api/posts');
      expect(req.request.method).toBe('POST');
      req.flush(errorResponse, { status: 400, statusText: 'Bad Request' });
    });

    it('should handle invalid theme ID on post creation', () => {
      const createRequest: PostCreateRequest = {
        title: 'Valid Title',
        content: 'Valid Content',
        themeId: 999 // Non-existent theme
      };

      const errorResponse = {
        message: 'Theme not found'
      };

      service.createPost(createRequest).subscribe({
        next: () => fail('should have failed with theme not found error'),
        error: (error) => {
          expect(error.status).toBe(404);
          expect(error.error.message).toBe('Theme not found');
        }
      });

      const req = httpMock.expectOne('/api/posts');
      req.flush(errorResponse, { status: 404, statusText: 'Not Found' });
    });

    it('should handle unauthorized post creation', () => {
      const createRequest: PostCreateRequest = {
        title: 'Valid Title',
        content: 'Valid Content',
        themeId: 1
      };

      service.createPost(createRequest).subscribe({
        next: () => fail('should have failed with unauthorized error'),
        error: (error) => {
          expect(error.status).toBe(401);
        }
      });

      const req = httpMock.expectOne('/api/posts');
      req.flush({}, { status: 401, statusText: 'Unauthorized' });
    });

    it('should handle long content creation', () => {
      const longContent = 'A'.repeat(5000); // Very long content
      const createRequest: PostCreateRequest = {
        title: 'Post with long content',
        content: longContent,
        themeId: 1
      };

      const createdPost: Post = {
        ...mockPost,
        id: 4,
        title: createRequest.title,
        content: longContent
      };

      service.createPost(createRequest).subscribe(post => {
        expect(post.content).toBe(longContent);
        expect(post.content.length).toBe(5000);
      });

      const req = httpMock.expectOne('/api/posts');
      req.flush(createdPost);
    });
  });

  describe('getPostsFromSubscribedThemes', () => {
    it('should retrieve posts from subscribed themes', () => {
      const subscribedPosts = [mockPost]; // User subscribed to Angular theme

      service.getPostsFromSubscribedThemes().subscribe(posts => {
        expect(posts).toEqual(subscribedPosts);
        expect(posts.length).toBe(1);
        expect(posts[0].theme.title).toBe('Angular');
      });

      const req = httpMock.expectOne('/api/posts/subscribed');
      expect(req.request.method).toBe('GET');
      req.flush(subscribedPosts);
    });

    it('should handle empty subscribed posts', () => {
      service.getPostsFromSubscribedThemes().subscribe(posts => {
        expect(posts).toEqual([]);
        expect(posts.length).toBe(0);
      });

      const req = httpMock.expectOne('/api/posts/subscribed');
      req.flush([]);
    });

    it('should handle unauthorized access to subscribed posts', () => {
      service.getPostsFromSubscribedThemes().subscribe({
        next: () => fail('should have failed with unauthorized error'),
        error: (error) => {
          expect(error.status).toBe(401);
        }
      });

      const req = httpMock.expectOne('/api/posts/subscribed');
      req.flush({}, { status: 401, statusText: 'Unauthorized' });
    });

    it('should use correct endpoint for subscribed posts', () => {
      service.getPostsFromSubscribedThemes().subscribe();

      const req = httpMock.expectOne('/api/posts/subscribed');
      expect(req.request.url).toBe('/api/posts/subscribed');
    });
  });

  describe('Integration scenarios', () => {
    it('should handle network errors', () => {
      const networkError = new ProgressEvent('error');

      service.getAllPosts().subscribe({
        next: () => fail('should have failed with network error'),
        error: (error) => {
          expect(error.error).toBe(networkError);
        }
      });

      const req = httpMock.expectOne('/api/posts');
      req.error(networkError);
    });

    it('should handle malformed post response', () => {
      const malformedPost = {
        id: 1,
        title: 'Valid Title'
        // Missing required fields: content, author, theme, timestamps
      };

      service.getPostById(1).subscribe(post => {
        expect(post.id).toBe(1);
        expect(post.title).toBe('Valid Title');
        expect(post.content).toBeUndefined();
        expect(post.author).toBeUndefined();
        expect(post.theme).toBeUndefined();
      });

      const req = httpMock.expectOne('/api/posts/1');
      req.flush(malformedPost);
    });

    it('should use config service endpoints correctly', () => {
      // Test multiple endpoints to ensure config service integration
      service.getAllPosts().subscribe();
      service.getPostById(1).subscribe();
      service.getPostsByTheme(2).subscribe();
      service.getPostsFromSubscribedThemes().subscribe();

      const requests = httpMock.match(() => true);
      expect(requests.length).toBe(4);
      expect(requests[0].request.url).toBe('/api/posts');
      expect(requests[1].request.url).toBe('/api/posts/1');
      expect(requests[2].request.url).toBe('/api/posts/theme/2');
      expect(requests[3].request.url).toBe('/api/posts/subscribed');

      requests.forEach(req => req.flush([]));
    });
  });
});

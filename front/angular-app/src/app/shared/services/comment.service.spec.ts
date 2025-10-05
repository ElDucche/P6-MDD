import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CommentService } from './comment.service';
import { ConfigService } from '../../core/services/config.service';
import { Comment, CreateCommentRequest } from '../interfaces/comment.interface';

describe('CommentService', () => {
  let service: CommentService;
  let httpMock: HttpTestingController;
  let mockConfigService: any;

  const mockComment: Comment = {
    id: 1,
    content: 'This is a test comment',
    author: {
      id: 1,
      username: 'testuser',
      email: 'test@example.com'
    },
    post: {
      id: 1,
      title: 'Test Post'
    },
    createdAt: '2024-01-01T10:00:00Z',
    updatedAt: '2024-01-01T10:00:00Z'
  };

  const mockComments: Comment[] = [
    mockComment,
    {
      id: 2,
      content: 'Second test comment',
      author: {
        id: 2,
        username: 'anotheruser',
        email: 'another@example.com'
      },
      post: {
        id: 1,
        title: 'Test Post'
      },
      createdAt: '2024-01-01T11:00:00Z',
      updatedAt: '2024-01-01T11:00:00Z'
    }
  ];

  const mockEndpoints = {
    comments: {
      all: '/api/comments',
      byPost: (postId: number) => `/api/comments/post/${postId}`
    }
  };

  beforeEach(() => {
    mockConfigService = {
      endpoints: mockEndpoints,
      apiUrl: 'http://localhost:8080' // Used by deleteComment
    };

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        CommentService,
        { provide: ConfigService, useValue: mockConfigService }
      ]
    });

    service = TestBed.inject(CommentService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getCommentsByPostId', () => {
    it('should retrieve comments for a specific post', () => {
      const postId = 1;

      service.getCommentsByPostId(postId).subscribe(comments => {
        expect(comments).toEqual(mockComments);
        expect(comments.length).toBe(2);
        expect(comments[0].content).toBe('This is a test comment');
        expect(comments[1].content).toBe('Second test comment');
        expect(comments[0].post.id).toBe(postId);
        expect(comments[1].post.id).toBe(postId);
      });

      const req = httpMock.expectOne('/api/comments/post/1');
      expect(req.request.method).toBe('GET');
      req.flush(mockComments);
    });

    it('should handle empty comments for a post', () => {
      const postId = 999; // Post with no comments

      service.getCommentsByPostId(postId).subscribe(comments => {
        expect(comments).toEqual([]);
        expect(comments.length).toBe(0);
      });

      const req = httpMock.expectOne('/api/comments/post/999');
      req.flush([]);
    });

    it('should handle post not found', () => {
      const postId = 999;

      service.getCommentsByPostId(postId).subscribe({
        next: () => fail('should have failed with not found error'),
        error: (error) => {
          expect(error.status).toBe(404);
          expect(error.error.message).toBe('Post not found');
        }
      });

      const req = httpMock.expectOne('/api/comments/post/999');
      req.flush(
        { message: 'Post not found' }, 
        { status: 404, statusText: 'Not Found' }
      );
    });

    it('should handle invalid post ID (negative)', () => {
      const postId = -1;

      service.getCommentsByPostId(postId).subscribe({
        next: () => fail('should have failed with bad request'),
        error: (error) => {
          expect(error.status).toBe(400);
        }
      });

      const req = httpMock.expectOne('/api/comments/post/-1');
      req.flush({}, { status: 400, statusText: 'Bad Request' });
    });

    it('should handle unauthorized access to comments', () => {
      const postId = 1;

      service.getCommentsByPostId(postId).subscribe({
        next: () => fail('should have failed with unauthorized error'),
        error: (error) => {
          expect(error.status).toBe(401);
        }
      });

      const req = httpMock.expectOne('/api/comments/post/1');
      req.flush({}, { status: 401, statusText: 'Unauthorized' });
    });

    it('should use correct endpoint with post ID', () => {
      const postId = 42;

      service.getCommentsByPostId(postId).subscribe();

      const req = httpMock.expectOne('/api/comments/post/42');
      expect(req.request.url).toBe('/api/comments/post/42');
    });
  });

  describe('createComment', () => {
    it('should create a new comment successfully', () => {
      const createRequest: CreateCommentRequest = {
        content: 'This is a new comment',
        postId: 1
      };

      const createdComment: Comment = {
        id: 3,
        content: createRequest.content,
        author: mockComment.author,
        post: mockComment.post,
        createdAt: '2024-01-01T12:00:00Z',
        updatedAt: '2024-01-01T12:00:00Z'
      };

      service.createComment(createRequest).subscribe(comment => {
        expect(comment).toEqual(createdComment);
        expect(comment.id).toBe(3);
        expect(comment.content).toBe('This is a new comment');
        expect(comment.post.id).toBe(1);
      });

      const req = httpMock.expectOne('/api/comments');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(createRequest);
      req.flush(createdComment);
    });

    it('should handle validation errors on comment creation', () => {
      const invalidCreateRequest: CreateCommentRequest = {
        content: '', // Empty content
        postId: 1
      };

      const errorResponse = {
        message: 'Validation failed',
        errors: {
          content: ['Content is required', 'Content must not be empty']
        }
      };

      service.createComment(invalidCreateRequest).subscribe({
        next: () => fail('should have failed with validation error'),
        error: (error) => {
          expect(error.status).toBe(400);
          expect(error.error.message).toBe('Validation failed');
          expect(error.error.errors.content).toContain('Content is required');
        }
      });

      const req = httpMock.expectOne('/api/comments');
      expect(req.request.method).toBe('POST');
      req.flush(errorResponse, { status: 400, statusText: 'Bad Request' });
    });

    it('should handle comment creation on non-existent post', () => {
      const createRequest: CreateCommentRequest = {
        content: 'Valid comment',
        postId: 999 // Non-existent post
      };

      const errorResponse = {
        message: 'Post not found'
      };

      service.createComment(createRequest).subscribe({
        next: () => fail('should have failed with post not found error'),
        error: (error) => {
          expect(error.status).toBe(404);
          expect(error.error.message).toBe('Post not found');
        }
      });

      const req = httpMock.expectOne('/api/comments');
      req.flush(errorResponse, { status: 404, statusText: 'Not Found' });
    });

    it('should handle unauthorized comment creation', () => {
      const createRequest: CreateCommentRequest = {
        content: 'Valid comment',
        postId: 1
      };

      service.createComment(createRequest).subscribe({
        next: () => fail('should have failed with unauthorized error'),
        error: (error) => {
          expect(error.status).toBe(401);
        }
      });

      const req = httpMock.expectOne('/api/comments');
      req.flush({}, { status: 401, statusText: 'Unauthorized' });
    });

    it('should handle long comment content', () => {
      const longContent = 'A'.repeat(2000); // Very long content
      const createRequest: CreateCommentRequest = {
        content: longContent,
        postId: 1
      };

      const createdComment: Comment = {
        ...mockComment,
        id: 4,
        content: longContent
      };

      service.createComment(createRequest).subscribe(comment => {
        expect(comment.content).toBe(longContent);
        expect(comment.content.length).toBe(2000);
      });

      const req = httpMock.expectOne('/api/comments');
      req.flush(createdComment);
    });

    it('should handle comment with special characters', () => {
      const specialContent = 'Commentaire avec des caractères spéciaux: éàùö & émojis 🚀 💯';
      const createRequest: CreateCommentRequest = {
        content: specialContent,
        postId: 1
      };

      const createdComment: Comment = {
        ...mockComment,
        id: 5,
        content: specialContent
      };

      service.createComment(createRequest).subscribe(comment => {
        expect(comment.content).toBe(specialContent);
      });

      const req = httpMock.expectOne('/api/comments');
      req.flush(createdComment);
    });
  });

  describe('deleteComment', () => {
    it('should delete a comment successfully', () => {
      const commentId = 1;

      service.deleteComment(commentId).subscribe(response => {
        expect(response).toBeUndefined(); // Void response
      });

      const req = httpMock.expectOne('http://localhost:8080/api/comments/1');
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });

    it('should handle delete non-existent comment', () => {
      const commentId = 999;

      service.deleteComment(commentId).subscribe({
        next: () => fail('should have failed with not found error'),
        error: (error) => {
          expect(error.status).toBe(404);
          expect(error.error.message).toBe('Comment not found');
        }
      });

      const req = httpMock.expectOne('http://localhost:8080/api/comments/999');
      expect(req.request.method).toBe('DELETE');
      req.flush(
        { message: 'Comment not found' }, 
        { status: 404, statusText: 'Not Found' }
      );
    });

    it('should handle unauthorized comment deletion', () => {
      const commentId = 1;

      service.deleteComment(commentId).subscribe({
        next: () => fail('should have failed with unauthorized error'),
        error: (error) => {
          expect(error.status).toBe(401);
        }
      });

      const req = httpMock.expectOne('http://localhost:8080/api/comments/1');
      req.flush({}, { status: 401, statusText: 'Unauthorized' });
    });

    it('should handle forbidden comment deletion (not owner)', () => {
      const commentId = 1;

      service.deleteComment(commentId).subscribe({
        next: () => fail('should have failed with forbidden error'),
        error: (error) => {
          expect(error.status).toBe(403);
          expect(error.error.message).toBe('You can only delete your own comments');
        }
      });

      const req = httpMock.expectOne('http://localhost:8080/api/comments/1');
      req.flush(
        { message: 'You can only delete your own comments' }, 
        { status: 403, statusText: 'Forbidden' }
      );
    });

    it('should handle invalid comment ID (negative)', () => {
      const commentId = -1;

      service.deleteComment(commentId).subscribe({
        next: () => fail('should have failed with bad request'),
        error: (error) => {
          expect(error.status).toBe(400);
        }
      });

      const req = httpMock.expectOne('http://localhost:8080/api/comments/-1');
      req.flush({}, { status: 400, statusText: 'Bad Request' });
    });

    it('should use correct endpoint with comment ID and base URL', () => {
      const commentId = 42;

      service.deleteComment(commentId).subscribe();

      const req = httpMock.expectOne('http://localhost:8080/api/comments/42');
      expect(req.request.url).toBe('http://localhost:8080/api/comments/42');
    });
  });

  describe('Integration scenarios', () => {
    it('should handle network errors', () => {
      const networkError = new ProgressEvent('error');

      service.getCommentsByPostId(1).subscribe({
        next: () => fail('should have failed with network error'),
        error: (error) => {
          expect(error.error).toBe(networkError);
        }
      });

      const req = httpMock.expectOne('/api/comments/post/1');
      req.error(networkError);
    });

    it('should handle malformed comment response', () => {
      const malformedComment = {
        id: 1,
        content: 'Valid content',
        author: {
          id: 1
          // Missing username and email
        }
        // Missing post, createdAt, updatedAt
      };

      service.getCommentsByPostId(1).subscribe(comments => {
        expect(comments[0].id).toBe(1);
        expect(comments[0].content).toBe('Valid content');
        expect(comments[0].author.id).toBe(1);
        expect(comments[0].author.username).toBeUndefined();
        expect(comments[0].post).toBeUndefined();
      });

      const req = httpMock.expectOne('/api/comments/post/1');
      req.flush([malformedComment]);
    });

    it('should use config service endpoints correctly', () => {
      // Test multiple endpoints to ensure config service integration
      service.getCommentsByPostId(1).subscribe();
      service.createComment({ content: 'Test', postId: 1 }).subscribe();
      service.deleteComment(1).subscribe();

      const requests = httpMock.match(() => true);
      expect(requests.length).toBe(3);
      expect(requests[0].request.url).toBe('/api/comments/post/1');
      expect(requests[0].request.method).toBe('GET');
      expect(requests[1].request.url).toBe('/api/comments');
      expect(requests[1].request.method).toBe('POST');
      expect(requests[2].request.url).toBe('http://localhost:8080/api/comments/1');
      expect(requests[2].request.method).toBe('DELETE');

      requests[0].flush(mockComments);
      requests[1].flush(mockComment);
      requests[2].flush(null);
    });

    it('should handle comment workflow (create, get, delete)', () => {
      const postId = 1;
      const createRequest: CreateCommentRequest = {
        content: 'Test workflow comment',
        postId
      };

      // Create comment
      service.createComment(createRequest).subscribe(comment => {
        expect(comment.content).toBe(createRequest.content);
        expect(comment.post.id).toBe(postId);
      });

      const createReq = httpMock.expectOne('/api/comments');
      expect(createReq.request.method).toBe('POST');
      expect(createReq.request.body).toEqual(createRequest);
      
      const createdComment = { ...mockComment, content: createRequest.content };
      createReq.flush(createdComment);

      // Get comments for post
      service.getCommentsByPostId(postId).subscribe(comments => {
        expect(comments).toContain(jasmine.objectContaining({
          content: createRequest.content
        }));
      });

      const getReq = httpMock.expectOne('/api/comments/post/1');
      expect(getReq.request.method).toBe('GET');
      getReq.flush([createdComment]);

      // Delete comment
      service.deleteComment(createdComment.id).subscribe(response => {
        expect(response).toBeUndefined();
      });

      const deleteReq = httpMock.expectOne('http://localhost:8080/api/comments/1');
      expect(deleteReq.request.method).toBe('DELETE');
      deleteReq.flush(null);
    });
  });
});

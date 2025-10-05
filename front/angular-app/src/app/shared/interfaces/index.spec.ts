import { 
  User, 
  Post, 
  Theme, 
  Comment, 
  Subscription,
  PostCreateRequest,
  CreateCommentRequest
} from './index';

describe('Interfaces', () => {
  describe('User interface', () => {
    it('should create a valid User object', () => {
      const user: User = {
        id: 1,
        username: 'testuser',
        email: 'test@example.com'
      };

      expect(user.id).toBe(1);
      expect(user.username).toBe('testuser');
      expect(user.email).toBe('test@example.com');
    });
  });

  describe('Post interface', () => {
    it('should create a valid Post object', () => {
      const post: Post = {
        id: 1,
        title: 'Test Post',
        content: 'Test content',
        author: {
          id: 1,
          username: 'testuser',
          email: 'test@example.com'
        },
        theme: {
          id: 1,
          title: 'Test Theme',
          description: 'Test description'
        },
        createdAt: '2023-01-01T00:00:00Z',
        updatedAt: '2023-01-01T00:00:00Z'
      };

      expect(post.id).toBe(1);
      expect(post.title).toBe('Test Post');
      expect(post.content).toBe('Test content');
      expect(post.author.username).toBe('testuser');
      expect(post.theme.title).toBe('Test Theme');
    });
  });

  describe('Theme interface', () => {
    it('should create a valid Theme object', () => {
      const theme: Theme = {
        id: 1,
        title: 'Angular',
        description: 'Framework Angular',
        createdAt: '2023-01-01T00:00:00Z',
        updatedAt: '2023-01-01T00:00:00Z'
      };

      expect(theme.id).toBe(1);
      expect(theme.title).toBe('Angular');
      expect(theme.description).toBe('Framework Angular');
    });
  });

  describe('Comment interface', () => {
    it('should create a valid Comment object', () => {
      const comment: Comment = {
        id: 1,
        content: 'Great post!',
        author: {
          id: 1,
          username: 'testuser',
          email: 'test@example.com'
        },
        post: {
          id: 1,
          title: 'Test Post'
        },
        createdAt: '2023-01-01T00:00:00Z',
        updatedAt: '2023-01-01T00:00:00Z'
      };

      expect(comment.id).toBe(1);
      expect(comment.content).toBe('Great post!');
      expect(comment.author.username).toBe('testuser');
      expect(comment.post.title).toBe('Test Post');
    });
  });

  describe('Subscription interface', () => {
    it('should create a valid Subscription object', () => {
      const subscription: Subscription = {
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
          title: 'Angular',
          description: 'Framework description'
        },
        createdAt: '2023-01-01T00:00:00Z'
      };

      expect(subscription.id.userId).toBe(1);
      expect(subscription.id.themeId).toBe(2);
      expect(subscription.user.username).toBe('testuser');
      expect(subscription.theme.title).toBe('Angular');
    });
  });

  describe('Request interfaces', () => {
    it('should create a valid PostCreateRequest', () => {
      const request: PostCreateRequest = {
        title: 'New Post',
        content: 'Content here',
        themeId: 1
      };

      expect(request.title).toBe('New Post');
      expect(request.content).toBe('Content here');
      expect(request.themeId).toBe(1);
    });

    it('should create a valid CreateCommentRequest', () => {
      const request: CreateCommentRequest = {
        content: 'Nice comment',
        postId: 1
      };

      expect(request.content).toBe('Nice comment');
      expect(request.postId).toBe(1);
    });
  });

  describe('Type validation', () => {
    it('should validate User properties', () => {
      const user: User = {
        id: 1,
        username: 'test',
        email: 'test@test.com'
      };

      expect(typeof user.id).toBe('number');
      expect(typeof user.username).toBe('string');
      expect(typeof user.email).toBe('string');
    });

    it('should validate nested object structures', () => {
      const post: Post = {
        id: 1,
        title: 'Test',
        content: 'Content',
        author: {
          id: 1,
          username: 'author',
          email: 'author@test.com'
        },
        theme: {
          id: 1,
          title: 'Theme',
          description: 'Description'
        },
        createdAt: '2023-01-01',
        updatedAt: '2023-01-01'
      };

      expect(post.author).toBeDefined();
      expect(post.theme).toBeDefined();
      expect(typeof post.author.id).toBe('number');
      expect(typeof post.theme.title).toBe('string');
    });

    it('should validate complex subscription structure', () => {
      const subscription: Subscription = {
        id: {
          userId: 1,
          themeId: 2
        },
        user: {
          id: 1,
          username: 'user',
          email: 'user@test.com'
        },
        theme: {
          id: 2,
          title: 'Theme',
          description: 'Description'
        }
      };

      expect(typeof subscription.id.userId).toBe('number');
      expect(typeof subscription.id.themeId).toBe('number');
      expect(typeof subscription.user.username).toBe('string');
      expect(typeof subscription.theme.title).toBe('string');
    });
  });
});

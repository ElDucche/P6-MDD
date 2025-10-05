import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import { of, throwError } from 'rxjs';
import { signal } from '@angular/core';

import { ArticleComponent } from './article.component';
import { PostService, ThemeService, CommentService } from '../../shared/services';
import { Post, Theme, Comment } from '../../shared/interfaces';

describe('ArticleComponent', () => {
  let component: ArticleComponent;
  let fixture: ComponentFixture<ArticleComponent>;
  
  // Mock data
  const mockPost: Post = {
    id: 1,
    title: 'Article de Test',
    content: 'Contenu de l\'article de test',
    author: {
      id: 1,
      username: 'testuser',
      email: 'test@example.com'
    },
    theme: {
      id: 1,
      title: 'Angular',
      description: 'Framework frontend'
    },
    createdAt: '2024-01-01T10:00:00Z',
    updatedAt: '2024-01-01T10:00:00Z'
  };

  const mockTheme: Theme = {
    id: 1,
    title: 'Angular',
    description: 'Framework frontend',
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z'
  };

  const mockComments: Comment[] = [
    {
      id: 1,
      content: 'Premier commentaire',
      author: {
        id: 2,
        username: 'commenter1',
        email: 'commenter1@example.com'
      },
      post: {
        id: 1,
        title: 'Article de Test'
      },
      createdAt: '2024-01-01T12:00:00Z',
      updatedAt: '2024-01-01T12:00:00Z'
    },
    {
      id: 2,
      content: 'Deuxième commentaire',
      author: {
        id: 3,
        username: 'commenter2',
        email: 'commenter2@example.com'
      },
      post: {
        id: 1,
        title: 'Article de Test'
      },
      createdAt: '2024-01-01T11:00:00Z',
      updatedAt: '2024-01-01T11:00:00Z'
    }
  ];

  // Mock services
  const mockPostService = {
    getPostById: jest.fn()
  };

  const mockThemeService = {
    getThemeById: jest.fn()
  };

  const mockCommentService = {
    getCommentsByPostId: jest.fn(),
    createComment: jest.fn()
  };

  const mockActivatedRoute = {
    snapshot: {
      paramMap: {
        get: jest.fn()
      }
    }
  };

  const mockRouter = {
    navigate: jest.fn()
  };

  const mockLocation = {
    back: jest.fn()
  };

  beforeEach(async () => {
    // Reset all mocks
    jest.clearAllMocks();
    
    // Setup default mock returns
    mockPostService.getPostById.mockReturnValue(of(mockPost));
    mockThemeService.getThemeById.mockReturnValue(of(mockTheme));
    mockCommentService.getCommentsByPostId.mockReturnValue(of(mockComments));
    mockCommentService.createComment.mockReturnValue(of(mockComments[0]));
    mockActivatedRoute.snapshot.paramMap.get.mockReturnValue('1');

    await TestBed.configureTestingModule({
      imports: [ArticleComponent],
      providers: [
        { provide: PostService, useValue: mockPostService },
        { provide: ThemeService, useValue: mockThemeService },
        { provide: CommentService, useValue: mockCommentService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: Router, useValue: mockRouter },
        { provide: Location, useValue: mockLocation }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ArticleComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load article, theme and comments on init with valid ID', () => {
    component.ngOnInit();

    expect(mockActivatedRoute.snapshot.paramMap.get).toHaveBeenCalledWith('id');
    expect(mockPostService.getPostById).toHaveBeenCalledWith(1);
    expect(mockThemeService.getThemeById).toHaveBeenCalledWith(1);
    expect(mockCommentService.getCommentsByPostId).toHaveBeenCalledWith(1);
    
    expect((component as any).post()).toEqual(mockPost);
    expect((component as any).theme()).toEqual(mockTheme);
    expect((component as any).comments()).toEqual(mockComments);
    expect((component as any).isLoading()).toBe(false);
  });

  it('should handle invalid article ID', () => {
    mockActivatedRoute.snapshot.paramMap.get.mockReturnValue('invalid');

    component.ngOnInit();

    expect((component as any).error()).toBe('ID d\'article invalide');
    expect((component as any).isLoading()).toBe(false);
    expect(mockPostService.getPostById).not.toHaveBeenCalled();
  });

  it('should handle missing article ID', () => {
    mockActivatedRoute.snapshot.paramMap.get.mockReturnValue(null);

    component.ngOnInit();

    expect((component as any).error()).toBe('ID d\'article invalide');
    expect((component as any).isLoading()).toBe(false);
    expect(mockPostService.getPostById).not.toHaveBeenCalled();
  });

  it('should handle article loading error', () => {
    mockPostService.getPostById.mockReturnValue(throwError(() => new Error('Article not found')));

    component.ngOnInit();

    expect((component as any).error()).toBe('Article non trouvé');
    expect((component as any).isLoading()).toBe(false);
  });

  it('should handle theme loading error gracefully', () => {
    mockThemeService.getThemeById.mockReturnValue(throwError(() => new Error('Theme not found')));

    component.ngOnInit();

    expect((component as any).theme()).toBeNull();
    expect((component as any).isLoading()).toBe(false);
    // L'article doit toujours être chargé même si le thème échoue
    expect((component as any).post()).toEqual(mockPost);
  });

  it('should handle comments loading error gracefully', () => {
    mockCommentService.getCommentsByPostId.mockReturnValue(throwError(() => new Error('Comments not found')));

    component.ngOnInit();

    expect((component as any).comments()).toEqual([]);
    expect((component as any).isLoadingComments()).toBe(false);
  });

  it('should sort comments correctly', () => {
    component.ngOnInit();

    // Test descending order (default)
    expect((component as any).comments()[0].id).toBe(1); // Plus récent en premier
    expect((component as any).comments()[1].id).toBe(2);

    // Test ascending order
    (component as any).changeSortOrder('asc');
    expect((component as any).comments()[0].id).toBe(2); // Plus ancien en premier
    expect((component as any).comments()[1].id).toBe(1);
  });

  it('should change sort order', () => {
    component.ngOnInit();
    
    (component as any).changeSortOrder('asc');
    
    expect((component as any).sortOrder()).toBe('asc');
  });

  it('should handle sort order change event', () => {
    const mockEvent = {
      target: { value: 'asc' }
    } as unknown as Event;

    (component as any).onSortOrderChange(mockEvent);

    expect((component as any).sortOrder()).toBe('asc');
  });

  it('should submit comment successfully', () => {
    component.ngOnInit();
    (component as any).commentControl.setValue('Nouveau commentaire');

    const newComment: Comment = {
      id: 3,
      content: 'Nouveau commentaire',
      author: {
        id: 1,
        username: 'testuser',
        email: 'test@example.com'
      },
      post: {
        id: 1,
        title: 'Article de Test'
      },
      createdAt: '2024-01-01T13:00:00Z',
      updatedAt: '2024-01-01T13:00:00Z'
    };

    mockCommentService.createComment.mockReturnValue(of(newComment));

    (component as any).submitComment();

    expect(mockCommentService.createComment).toHaveBeenCalledWith({
      content: 'Nouveau commentaire',
      postId: 1
    });
    expect((component as any).commentControl.value).toBeNull();
    expect((component as any).isSubmittingComment()).toBe(false);
  });

  it('should not submit comment when invalid', () => {
    component.ngOnInit();
    (component as any).commentControl.setValue(''); // Invalid empty comment

    (component as any).submitComment();

    expect(mockCommentService.createComment).not.toHaveBeenCalled();
  });

  it('should not submit comment when already submitting', () => {
    component.ngOnInit();
    (component as any).commentControl.setValue('Valid comment');
    (component as any).isSubmittingComment.set(true);

    (component as any).submitComment();

    expect(mockCommentService.createComment).not.toHaveBeenCalled();
  });

  it('should not submit comment when no post loaded', () => {
    (component as any).post.set(null);
    (component as any).commentControl.setValue('Valid comment');

    (component as any).submitComment();

    expect(mockCommentService.createComment).not.toHaveBeenCalled();
  });

  it('should handle comment submission error', () => {
    component.ngOnInit();
    (component as any).commentControl.setValue('Valid comment');
    mockCommentService.createComment.mockReturnValue(throwError(() => new Error('Submission failed')));

    (component as any).submitComment();

    expect((component as any).isSubmittingComment()).toBe(false);
  });

  it('should navigate back', () => {
    component.goBack();

    expect(mockLocation.back).toHaveBeenCalled();
  });

  it('should navigate to theme when theme is loaded', () => {
    component.ngOnInit();

    component.goToTheme();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/themes']);
  });

  it('should not navigate to theme when no theme loaded', () => {
    (component as any).theme.set(null);

    component.goToTheme();

    expect(mockRouter.navigate).not.toHaveBeenCalled();
  });

  it('should set loading states correctly', () => {
    // Initially loading
    expect((component as any).isLoading()).toBe(true);
    expect((component as any).isLoadingComments()).toBe(false);
    expect((component as any).isSubmittingComment()).toBe(false);

    component.ngOnInit();

    // After initialization
    expect((component as any).isLoading()).toBe(false);
    expect((component as any).isLoadingComments()).toBe(false);
  });

  it('should validate comment form correctly', () => {
    const commentControl = (component as any).commentControl;

    // Test required validation
    commentControl.setValue('');
    expect(commentControl.invalid).toBe(true);

    // Test valid comment
    commentControl.setValue('Valid comment');
    expect(commentControl.valid).toBe(true);

    // Test max length validation (assuming 1000 char limit)
    const longComment = 'x'.repeat(1001);
    commentControl.setValue(longComment);
    expect(commentControl.invalid).toBe(true);
  });
});

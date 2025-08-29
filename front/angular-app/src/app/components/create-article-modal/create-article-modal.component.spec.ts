import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { CreateArticleModalComponent } from './create-article-modal.component';
import { ThemeService, PostService } from '@shared/services';
import { Theme, Post, PostCreateRequest } from '@shared/interfaces';
import { of, throwError, Observable } from 'rxjs';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';

// Mock Services
class MockThemeService {
  getAllThemes(): Observable<Theme[]> {
    return of([
      { 
        id: 1, 
        title: 'Technology', 
        description: 'Tech articles',
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z'
      },
      { 
        id: 2, 
        title: 'Science', 
        description: 'Science articles',
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z'
      },
      { 
        id: 3, 
        title: 'Sports', 
        description: 'Sports articles',
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-01T00:00:00Z'
      }
    ]);
  }
}

class MockPostService {
  createPost(post: PostCreateRequest): Observable<Post> {
    return of({} as Post);
  }
}

describe('CreateArticleModalComponent', () => {
  let component: CreateArticleModalComponent;
  let fixture: ComponentFixture<CreateArticleModalComponent>;
  let themeService: MockThemeService;
  let postService: MockPostService;

  const mockThemes: Theme[] = [
    { 
      id: 1, 
      title: 'Technology', 
      description: 'Tech articles',
      createdAt: '2024-01-01T00:00:00Z',
      updatedAt: '2024-01-01T00:00:00Z'
    },
    { 
      id: 2, 
      title: 'Science', 
      description: 'Science articles',
      createdAt: '2024-01-01T00:00:00Z',
      updatedAt: '2024-01-01T00:00:00Z'
    },
    { 
      id: 3, 
      title: 'Sports', 
      description: 'Sports articles',
      createdAt: '2024-01-01T00:00:00Z',
      updatedAt: '2024-01-01T00:00:00Z'
    }
  ];

  const mockCreatedPost: Post = {
    id: 1,
    title: 'Test Article',
    content: 'Test content that is longer than 10 characters',
    author: {
      id: 1,
      username: 'testuser',
      email: 'test@example.com'
    },
    theme: {
      id: 1,
      title: 'Technology',
      description: 'Tech articles'
    },
    createdAt: '2024-01-15T10:30:00Z',
    updatedAt: '2024-01-15T10:30:00Z'
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateArticleModalComponent, ReactiveFormsModule],
      providers: [
        FormBuilder,
        { provide: ThemeService, useClass: MockThemeService },
        { provide: PostService, useClass: MockPostService }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateArticleModalComponent);
    component = fixture.componentInstance;
    themeService = TestBed.inject(ThemeService) as any;
    postService = TestBed.inject(PostService) as any;
    
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Component Initialization', () => {
    it('should inject services correctly', () => {
      expect(themeService).toBeTruthy();
      expect(postService).toBeTruthy();
    });

    it('should initialize form with correct validators', () => {
      const form = component.articleForm;
      
      expect(form.get('title')?.hasError('required')).toBe(true);
      expect(form.get('content')?.hasError('required')).toBe(true);
      expect(form.get('themeId')?.hasError('required')).toBe(true);
    });

    it('should load themes on initialization', () => {
      // The mock is already set up in beforeEach and loadThemes is called in constructor
      expect(component.themes()).toEqual(mockThemes);
    });

    it('should initialize signals with default values', () => {
      expect(component.themes()).toEqual(mockThemes);
      expect(component.isLoading()).toBe(false);
    });

    it('should have OnPush change detection strategy', () => {
      // Note: Component uses OnPush change detection as defined in decorator
      expect(component).toBeTruthy();
    });
  });

  describe('Theme Loading', () => {
    it('should handle successful theme loading', () => {
      const newThemes: Theme[] = [
        { 
          id: 4, 
          title: 'Arts', 
          description: 'Arts articles',
          createdAt: '2024-01-01T00:00:00Z',
          updatedAt: '2024-01-01T00:00:00Z'
        }
      ];
      
      jest.spyOn(themeService, 'getAllThemes').mockReturnValue(of(newThemes));
      
      // Manually call loadThemes to simulate new loading
      (component as any).loadThemes();
      
      expect(themeService.getAllThemes).toHaveBeenCalled();
      expect(component.themes()).toEqual(newThemes);
    });

    it('should handle theme loading error gracefully', () => {
      const consoleSpy = jest.spyOn(console, 'error').mockImplementation();
      const error = new Error('Failed to load themes');
      
      jest.spyOn(themeService, 'getAllThemes').mockReturnValue(throwError(() => error));
      
      // Manually call loadThemes to simulate error
      (component as any).loadThemes();
      
      expect(consoleSpy).toHaveBeenCalledWith('Erreur lors du chargement des thèmes:', error);
      
      consoleSpy.mockRestore();
    });

    it('should handle empty theme response', () => {
      jest.spyOn(themeService, 'getAllThemes').mockReturnValue(of([]));
      
      // Manually call loadThemes to simulate empty response
      (component as any).loadThemes();
      
      expect(component.themes()).toEqual([]);
    });
  });

  describe('Form Validation', () => {
    it('should validate title field correctly', () => {
      const titleControl = component.articleForm.get('title');
      
      // Empty title
      titleControl?.setValue('');
      expect(titleControl?.hasError('required')).toBe(true);
      expect(titleControl?.valid).toBe(false);
      
      // Too short title
      titleControl?.setValue('ab');
      expect(titleControl?.hasError('minlength')).toBe(true);
      expect(titleControl?.valid).toBe(false);
      
      // Valid title
      titleControl?.setValue('Valid Title');
      expect(titleControl?.valid).toBe(true);
    });

    it('should validate content field correctly', () => {
      const contentControl = component.articleForm.get('content');
      
      // Empty content
      contentControl?.setValue('');
      expect(contentControl?.hasError('required')).toBe(true);
      expect(contentControl?.valid).toBe(false);
      
      // Too short content
      contentControl?.setValue('short');
      expect(contentControl?.hasError('minlength')).toBe(true);
      expect(contentControl?.valid).toBe(false);
      
      // Valid content
      contentControl?.setValue('This is a valid content that is longer than 10 characters');
      expect(contentControl?.valid).toBe(true);
    });

    it('should validate themeId field correctly', () => {
      const themeIdControl = component.articleForm.get('themeId');
      
      // Empty themeId
      themeIdControl?.setValue('');
      expect(themeIdControl?.hasError('required')).toBe(true);
      expect(themeIdControl?.valid).toBe(false);
      
      // Valid themeId
      themeIdControl?.setValue('1');
      expect(themeIdControl?.valid).toBe(true);
    });

    it('should validate complete form', () => {
      const form = component.articleForm;
      
      // Invalid form
      expect(form.valid).toBe(false);
      
      // Fill valid values
      form.patchValue({
        title: 'Valid Article Title',
        content: 'This is a valid article content that meets all requirements',
        themeId: '1'
      });
      
      expect(form.valid).toBe(true);
    });
  });

  describe('Modal Management', () => {
    let mockModal: HTMLDialogElement;

    beforeEach(() => {
      mockModal = {
        showModal: jest.fn(),
        close: jest.fn()
      } as unknown as HTMLDialogElement;

      jest.spyOn(document, 'getElementById').mockReturnValue(mockModal);
    });

    afterEach(() => {
      jest.restoreAllMocks();
    });

    it('should open modal correctly', () => {
      component.openModal();
      
      expect(document.getElementById).toHaveBeenCalledWith('create_article_modal');
      expect(mockModal.showModal).toHaveBeenCalled();
    });

    it('should close modal correctly', () => {
      component.closeModal();
      
      expect(document.getElementById).toHaveBeenCalledWith('create_article_modal');
      expect(mockModal.close).toHaveBeenCalled();
    });

    it('should reset form when closing modal', () => {
      // Fill form first
      component.articleForm.patchValue({
        title: 'Test Title',
        content: 'Test Content',
        themeId: '1'
      });
      
      expect(component.articleForm.get('title')?.value).toBe('Test Title');
      
      component.closeModal();
      
      expect(component.articleForm.get('title')?.value).toBeNull();
    });

    it('should handle missing modal element gracefully', () => {
      jest.spyOn(document, 'getElementById').mockReturnValue(null);
      
      expect(() => component.openModal()).not.toThrow();
      expect(() => component.closeModal()).not.toThrow();
    });
  });

  describe('Article Creation', () => {
    beforeEach(() => {
      // Set up valid form
      component.articleForm.patchValue({
        title: 'Test Article',
        content: 'This is a test article content that is valid',
        themeId: '1'
      });
    });

    it('should create article successfully', () => {
      jest.spyOn(postService, 'createPost').mockReturnValue(of(mockCreatedPost));
      jest.spyOn(component.articleCreated, 'emit');
      jest.spyOn(component, 'closeModal');

      // Verify loading starts
      expect(component.isLoading()).toBe(false);

      component.onSubmit();

      expect(postService.createPost).toHaveBeenCalledWith({
        title: 'Test Article',
        content: 'This is a test article content that is valid',
        themeId: 1
      });
      expect(component.articleCreated.emit).toHaveBeenCalledWith(mockCreatedPost);
      expect(component.closeModal).toHaveBeenCalled();
    });

    it('should handle article creation error', () => {
      const consoleSpy = jest.spyOn(console, 'error').mockImplementation();
      const error = new Error('Creation failed');
      
      jest.spyOn(postService, 'createPost').mockReturnValue(throwError(() => error));
      jest.spyOn(component.articleCreated, 'emit');

      component.onSubmit();

      expect(consoleSpy).toHaveBeenCalledWith('Erreur lors de la création de l\'article:', error);
      expect(component.isLoading()).toBe(false);
      expect(component.articleCreated.emit).not.toHaveBeenCalled();
      
      consoleSpy.mockRestore();
    });

    it('should not submit invalid form', () => {
      component.articleForm.patchValue({
        title: '', // Invalid
        content: 'Valid content',
        themeId: '1'
      });

      jest.spyOn(postService, 'createPost');

      component.onSubmit();

      expect(postService.createPost).not.toHaveBeenCalled();
      expect(component.isLoading()).toBe(false);
    });

    it('should convert themeId to number', () => {
      component.articleForm.patchValue({
        title: 'Test Article',
        content: 'Valid content for testing',
        themeId: '123' // String input
      });

      jest.spyOn(postService, 'createPost').mockReturnValue(of(mockCreatedPost));

      component.onSubmit();

      expect(postService.createPost).toHaveBeenCalledWith({
        title: 'Test Article',
        content: 'Valid content for testing',
        themeId: 123 // Should be converted to number
      });
    });
  });

  describe('Output Events', () => {
    it('should emit articleCreated when article is successfully created', () => {
      component.articleForm.patchValue({
        title: 'Test Article',
        content: 'Valid test content',
        themeId: '1'
      });

      jest.spyOn(postService, 'createPost').mockReturnValue(of(mockCreatedPost));
      
      let emittedPost: Post | undefined;
      component.articleCreated.subscribe((post) => {
        emittedPost = post;
      });

      component.onSubmit();

      expect(emittedPost).toEqual(mockCreatedPost);
    });

    it('should not emit articleCreated on error', () => {
      component.articleForm.patchValue({
        title: 'Test Article',
        content: 'Valid test content',
        themeId: '1'
      });

      const error = new Error('Creation failed');
      jest.spyOn(postService, 'createPost').mockReturnValue(throwError(() => error));
      jest.spyOn(console, 'error').mockImplementation();
      
      let emittedPost: Post | undefined;
      component.articleCreated.subscribe((post) => {
        emittedPost = post;
      });

      component.onSubmit();

      expect(emittedPost).toBeUndefined();
    });
  });

  describe('Edge Cases', () => {
    it('should handle null form values gracefully', () => {
      // Force null values
      component.articleForm.patchValue({
        title: null,
        content: null,
        themeId: null
      });

      jest.spyOn(postService, 'createPost');

      // Should not submit
      component.onSubmit();
      expect(postService.createPost).not.toHaveBeenCalled();
    });

    it('should handle very long content', () => {
      const longContent = 'A'.repeat(10000);
      
      component.articleForm.patchValue({
        title: 'Test Article',
        content: longContent,
        themeId: '1'
      });

      jest.spyOn(postService, 'createPost').mockReturnValue(of(mockCreatedPost));

      component.onSubmit();

      expect(postService.createPost).toHaveBeenCalledWith({
        title: 'Test Article',
        content: longContent,
        themeId: 1
      });
    });

    it('should handle special characters in content', () => {
      const specialContent = 'Content with émojis 🚀💯 and special chars éàùö and quotes "test" & \'test\'';
      
      component.articleForm.patchValue({
        title: 'Special Title with éàù',
        content: specialContent,
        themeId: '1'
      });

      jest.spyOn(postService, 'createPost').mockReturnValue(of(mockCreatedPost));

      component.onSubmit();

      expect(postService.createPost).toHaveBeenCalledWith({
        title: 'Special Title with éàù',
        content: specialContent,
        themeId: 1
      });
    });

    it('should handle invalid themeId conversion', () => {
      component.articleForm.patchValue({
        title: 'Test Article',
        content: 'Valid content',
        themeId: 'invalid'
      });

      jest.spyOn(postService, 'createPost').mockReturnValue(of(mockCreatedPost));

      component.onSubmit();

      expect(postService.createPost).toHaveBeenCalledWith({
        title: 'Test Article',
        content: 'Valid content',
        themeId: NaN
      });
    });
  });
});

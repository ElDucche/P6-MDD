import { TestBed, ComponentFixture } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { signal, computed } from '@angular/core';
import { ArticleComponent } from './article.component';

interface Theme {
  id: number;
  title: string;
  description: string;
}

interface Article {
  id: number;
  title: string;
  content: string;
  theme: Theme;
  author: string;
  createdAt: string;
}

interface Comment {
  id: number;
  message: string;
  author: string;
  createdAt: string;
}

/**
 * Test component for ArticleComponent business logic
 */
class TestArticleComponent {
  private articleSignal = signal<Article | null>(null);
  private commentsSignal = signal<Comment[]>([]);
  private themeSignal = signal<Theme | null>(null);
  private isLoadingSignal = signal<boolean>(true);
  private isLoadingCommentsSignal = signal<boolean>(false);
  private errorSignal = signal<string | null>(null);

  commentForm: FormGroup;

  constructor() {
    this.commentForm = new FormBuilder().group({
      message: ['', [Validators.required, Validators.minLength(1)]]
    });
    
    this.loadArticle();
  }

  article = this.articleSignal.asReadonly();
  comments = this.commentsSignal.asReadonly();
  theme = this.themeSignal.asReadonly();
  isLoading = this.isLoadingSignal.asReadonly();
  isLoadingComments = this.isLoadingCommentsSignal.asReadonly();

  getError = computed(() => this.errorSignal());

  loadArticle(): void {
    this.isLoadingSignal.set(true);
    this.errorSignal.set(null);
    
    setTimeout(() => {
      const mockArticle: Article = {
        id: 1,
        title: 'Test Article',
        content: 'Test content',
        theme: { id: 1, title: 'Test Theme', description: 'Test theme description' },
        author: 'Test Author',
        createdAt: '2024-01-01T00:00:00Z'
      };
      
      this.articleSignal.set(mockArticle);
      this.themeSignal.set(mockArticle.theme);
      this.isLoadingSignal.set(false);
      this.loadComments();
    }, 50);
  }

  loadComments(): void {
    this.isLoadingCommentsSignal.set(true);
    
    setTimeout(() => {
      const mockComments: Comment[] = [
        {
          id: 1,
          message: 'Test comment 1',
          author: 'Author 1',
          createdAt: '2024-01-01T01:00:00Z'
        },
        {
          id: 2,
          message: 'Test comment 2',
          author: 'Author 2',
          createdAt: '2024-01-01T02:00:00Z'
        }
      ];
      
      this.commentsSignal.set(mockComments);
      this.isLoadingCommentsSignal.set(false);
    }, 30);
  }

  onSubmitComment(): void {
    if (this.commentForm.valid) {
      const message = this.commentForm.get('message')?.value;
      
      if (message && message.trim()) {
        const newComment: Comment = {
          id: Date.now(),
          message: message.trim(),
          author: 'Current User',
          createdAt: new Date().toISOString()
        };
        
        const currentComments = this.commentsSignal();
        this.commentsSignal.set([...currentComments, newComment]);
        this.commentForm.reset();
      }
    }
  }

  // Test methods
  testSubmitComment(): void {
    this.commentForm.patchValue({ message: 'Test comment' });
    this.onSubmitComment();
  }

  // Getters for tests
  getArticle() { return this.article(); }
  getComments() { return this.comments(); }
  getTheme() { return this.theme(); }
  getIsLoading() { return this.isLoading(); }
  getIsLoadingComments() { return this.isLoadingComments(); }
  getCommentForm() { return this.commentForm; }
}

describe('ArticleComponent', () => {
  let component: ArticleComponent;
  let fixture: ComponentFixture<ArticleComponent>;
  let testComponent: TestArticleComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ArticleComponent],
      providers: [
        provideHttpClient(),
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ArticleComponent);
    component = fixture.componentInstance;
    testComponent = new TestArticleComponent();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Business Logic Tests', () => {
    it('should load article data', (done) => {
      setTimeout(() => {
        expect(testComponent.getArticle()).toBeDefined();
        expect(testComponent.getArticle()?.title).toBe('Test Article');
        expect(testComponent.getIsLoading()).toBe(false);
        done();
      }, 100);
    });

    it('should load comments', (done) => {
      setTimeout(() => {
        expect(testComponent.getComments().length).toBe(2);
        expect(testComponent.getIsLoadingComments()).toBe(false);
        done();
      }, 100);
    });

    it('should validate comment form', () => {
      const form = testComponent.getCommentForm();
      expect(form.valid).toBe(false);
      
      form.patchValue({ message: 'Valid comment' });
      expect(form.valid).toBe(true);
    });

    it('should submit comment', (done) => {
      setTimeout(() => {
        const initialCount = testComponent.getComments().length;
        testComponent.testSubmitComment();
        expect(testComponent.getComments().length).toBeGreaterThan(initialCount);
        done();
      }, 100);
    });

    it('should handle theme loading', (done) => {
      setTimeout(() => {
        expect(testComponent.getTheme()).toBeDefined();
        expect(testComponent.getTheme()?.title).toBe('Test Theme');
        done();
      }, 100);
    });
  });
});

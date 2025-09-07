import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { CreateArticleComponent } from './create-article.component';
import { ThemeService, PostService } from '@shared/services';
import { AlertService } from '@core/services/alert.service';

describe('CreateArticleComponent', () => {
  let component: CreateArticleComponent;
  let fixture: ComponentFixture<CreateArticleComponent>;
  let mockThemeService: jest.Mocked<ThemeService>;
  let mockPostService: jest.Mocked<PostService>;
  let mockAlertService: jest.Mocked<AlertService>;
  let mockRouter: jest.Mocked<Router>;

  const mockThemes = [
    { 
      id: 1, 
      title: 'Tech', 
      description: 'Technology themes',
      createdAt: '2024-01-01',
      updatedAt: '2024-01-01'
    },
    { 
      id: 2, 
      title: 'Science', 
      description: 'Science themes',
      createdAt: '2024-01-01',
      updatedAt: '2024-01-01'
    }
  ];

  const mockPost = {
    id: 1,
    title: 'Test Article',
    content: 'Test content',
    themeId: 1,
    createdAt: '2024-01-01',
    updatedAt: '2024-01-01',
    theme: mockThemes[0],
    author: { id: 1, username: 'test', email: 'test@example.com' },
    comments: []
  };

  beforeEach(async () => {
    mockThemeService = {
      getAllThemes: jest.fn().mockReturnValue(of(mockThemes))
    } as any;

    mockPostService = {
      createPost: jest.fn()
    } as any;

    mockAlertService = {
      showAlert: jest.fn()
    } as any;

    mockRouter = {
      navigate: jest.fn()
    } as any;

    await TestBed.configureTestingModule({
      imports: [CreateArticleComponent, ReactiveFormsModule],
      providers: [
        { provide: ThemeService, useValue: mockThemeService },
        { provide: PostService, useValue: mockPostService },
        { provide: AlertService, useValue: mockAlertService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CreateArticleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load themes on init', () => {
    expect(mockThemeService.getAllThemes).toHaveBeenCalled();
    expect(component.themes()).toEqual(mockThemes);
  });

  it('should handle theme loading error', () => {
    // Reset the mock to return an error for this test
    mockThemeService.getAllThemes.mockReturnValue(throwError(() => new Error('API Error')));
    
    const consoleSpy = jest.spyOn(console, 'error').mockImplementation(() => {});
    
    // Create a new component instance to test error handling during initialization
    const fixture2 = TestBed.createComponent(CreateArticleComponent);
    fixture2.detectChanges();

    expect(mockAlertService.showAlert).toHaveBeenCalledWith({
      type: 'error',
      message: 'Erreur lors du chargement des thèmes'
    });
    
    consoleSpy.mockRestore();
  });

  it('should submit form successfully', () => {
    mockPostService.createPost.mockReturnValue(of(mockPost));

    component.articleForm.patchValue({
      title: 'Test Article',
      content: 'Test content for article',
      themeId: '1'
    });

    component.onSubmit();

    expect(mockPostService.createPost).toHaveBeenCalledWith({
      title: 'Test Article',
      content: 'Test content for article',
      themeId: 1
    });
    expect(mockAlertService.showAlert).toHaveBeenCalledWith({
      type: 'success',
      message: 'Article créé avec succès !'
    });
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/home']);
  });

  it('should handle form submission error', () => {
    mockPostService.createPost.mockReturnValue(throwError(() => new Error('API Error')));

    const consoleSpy = jest.spyOn(console, 'error').mockImplementation(() => {});

    component.articleForm.patchValue({
      title: 'Test Article',
      content: 'Test content for article',
      themeId: '1'
    });

    component.onSubmit();

    expect(mockAlertService.showAlert).toHaveBeenCalledWith({
      type: 'error',
      message: 'Erreur lors de la création de l\'article'
    });
    
    consoleSpy.mockRestore();
  });

  it('should not submit invalid form', () => {
    component.onSubmit(); // Form is empty/invalid

    expect(mockPostService.createPost).not.toHaveBeenCalled();
    expect(mockRouter.navigate).not.toHaveBeenCalled();
  });

  it('should navigate back to home', () => {
    component.goBack();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/home']);
  });

  it('should validate form fields', () => {
    const form = component.articleForm;
    
    expect(form.valid).toBe(false);
    
    form.patchValue({
      title: '',
      content: '',
      themeId: ''
    });
    
    expect(form.get('title')?.hasError('required')).toBe(true);
    expect(form.get('content')?.hasError('required')).toBe(true);
    expect(form.get('themeId')?.hasError('required')).toBe(true);
    
    form.patchValue({
      title: 'Valid Title',
      content: 'Valid content that is long enough',
      themeId: '1'
    });
    
    expect(form.valid).toBe(true);
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';

import { HomeComponent } from './home.component';
import { ThemeService } from '../../shared/services/theme.service';
import { PostService } from '../../shared/services/post.service';
import { Theme } from '../../shared/interfaces/theme.interface';
import { Post } from '../../shared/interfaces/post.interface';

// Mock components for testing
@Component({
  selector: 'app-article-card',
  template: '',
  standalone: true
})
class MockArticleCardComponent {
  @Input() post: any;
}

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;
  let themeService: jest.Mocked<ThemeService>;
  let postService: jest.Mocked<PostService>;
  let router: jest.Mocked<Router>;

  const mockThemes: Theme[] = [
    { 
      id: 1, 
      title: 'Angular', 
      description: 'Framework JavaScript',
      createdAt: '2024-01-01T10:00:00Z',
      updatedAt: '2024-01-01T10:00:00Z'
    },
    { 
      id: 2, 
      title: 'React', 
      description: 'Library JavaScript',
      createdAt: '2024-01-02T10:00:00Z',
      updatedAt: '2024-01-02T10:00:00Z'
    }
  ];

  const mockPosts: Post[] = [
    {
      id: 1,
      title: 'Article 1',
      content: 'Content of article 1',
      author: { id: 1, username: 'john', email: 'john@example.com' },
      theme: { id: 1, title: 'Angular', description: 'Framework JavaScript' },
      createdAt: '2024-01-01T10:00:00Z',
      updatedAt: '2024-01-01T10:00:00Z'
    },
    {
      id: 2,
      title: 'Article 2',
      content: 'Content of article 2',
      author: { id: 2, username: 'jane', email: 'jane@example.com' },
      theme: { id: 2, title: 'React', description: 'Library JavaScript' },
      createdAt: '2024-01-02T10:00:00Z',
      updatedAt: '2024-01-02T10:00:00Z'
    }
  ];

  const mockMyFeedPosts: Post[] = [
    {
      id: 1,
      title: 'Article 1',
      content: 'Content of article 1',
      author: { id: 1, username: 'john', email: 'john@example.com' },
      theme: { id: 1, title: 'Angular', description: 'Framework JavaScript' },
      createdAt: '2024-01-01T10:00:00Z',
      updatedAt: '2024-01-01T10:00:00Z'
    }
  ];

  beforeEach(async () => {
    const mockThemeService = {
      getAllThemes: jest.fn(),
    };

    const mockPostService = {
      getPostsFromSubscribedThemes: jest.fn(),
      getAllPosts: jest.fn(),
    };

    const mockRouter = {
      navigate: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [HomeComponent, MockArticleCardComponent],
      providers: [
        { provide: ThemeService, useValue: mockThemeService },
        { provide: PostService, useValue: mockPostService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    themeService = TestBed.inject(ThemeService) as jest.Mocked<ThemeService>;
    postService = TestBed.inject(PostService) as jest.Mocked<PostService>;
    router = TestBed.inject(Router) as jest.Mocked<Router>;
  });

  describe('Component Initialization', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should load data on init', () => {
      // Arrange
      themeService.getAllThemes.mockReturnValue(of(mockThemes));
      postService.getPostsFromSubscribedThemes.mockReturnValue(of(mockMyFeedPosts));
      postService.getAllPosts.mockReturnValue(of(mockPosts));

      // Act
      component.ngOnInit();

      // Assert
      expect(themeService.getAllThemes).toHaveBeenCalled();
      expect(postService.getPostsFromSubscribedThemes).toHaveBeenCalled();
      expect(postService.getAllPosts).toHaveBeenCalled();
    });
  });

  describe('Data Loading', () => {
    it('should handle themes loading error', () => {
      // Arrange
      const consoleSpy = jest.spyOn(console, 'error').mockImplementation();
      themeService.getAllThemes.mockReturnValue(throwError(() => new Error('Themes error')));
      postService.getPostsFromSubscribedThemes.mockReturnValue(of(mockMyFeedPosts));
      postService.getAllPosts.mockReturnValue(of(mockPosts));

      // Act
      component.ngOnInit();

      // Assert
      expect(consoleSpy).toHaveBeenCalledWith('Erreur lors du chargement des thèmes:', expect.any(Error));
      
      consoleSpy.mockRestore();
    });

    it('should handle myFeed posts loading error', () => {
      // Arrange
      const consoleSpy = jest.spyOn(console, 'error').mockImplementation();
      themeService.getAllThemes.mockReturnValue(of(mockThemes));
      postService.getPostsFromSubscribedThemes.mockReturnValue(throwError(() => new Error('MyFeed error')));
      postService.getAllPosts.mockReturnValue(of(mockPosts));

      // Act
      component.ngOnInit();

      // Assert
      expect(consoleSpy).toHaveBeenCalledWith('Erreur lors du chargement de Mon Fil:', expect.any(Error));
      
      consoleSpy.mockRestore();
    });

    it('should handle all posts loading error', () => {
      // Arrange
      const consoleSpy = jest.spyOn(console, 'error').mockImplementation();
      themeService.getAllThemes.mockReturnValue(of(mockThemes));
      postService.getPostsFromSubscribedThemes.mockReturnValue(of(mockMyFeedPosts));
      postService.getAllPosts.mockReturnValue(throwError(() => new Error('All posts error')));

      // Act
      component.ngOnInit();

      // Assert
      expect(consoleSpy).toHaveBeenCalledWith('Erreur lors du chargement de tous les posts:', expect.any(Error));
      
      consoleSpy.mockRestore();
    });
  });

  describe('Navigation', () => {
    it('should navigate to create article page', () => {
      // Act
      component.openCreateArticleModal();

      // Assert
      expect(router.navigate).toHaveBeenCalledWith(['/create-article']);
    });
  });

  describe('Sorting Functionality', () => {
    beforeEach(() => {
      themeService.getAllThemes.mockReturnValue(of(mockThemes));
      postService.getPostsFromSubscribedThemes.mockReturnValue(of(mockMyFeedPosts));
      postService.getAllPosts.mockReturnValue(of(mockPosts));
      fixture.detectChanges();
    });

    it('should have initial sort order for my feed as desc', () => {
      expect(component.sortOrderMyFeed()).toBe('desc');
    });

    it('should have initial sort order for all posts as desc', () => {
      expect(component.sortOrderAllPosts()).toBe('desc');
    });

    it('should refresh posts', () => {
      // Clear initial calls
      postService.getPostsFromSubscribedThemes.mockClear();
      postService.getAllPosts.mockClear();

      // Act
      component.refreshPosts();

      // Assert
      expect(postService.getPostsFromSubscribedThemes).toHaveBeenCalled();
      expect(postService.getAllPosts).toHaveBeenCalled();
    });
  });

  describe('Component State', () => {
    it('should have initial loading states', () => {
      expect(component.isLoadingMyFeed()).toBe(false);
      expect(component.isLoadingAllPosts()).toBe(false);
    });

    it('should have initial sort orders', () => {
      expect(component.sortOrderMyFeed()).toBe('desc');
      expect(component.sortOrderAllPosts()).toBe('desc');
    });
  });
});

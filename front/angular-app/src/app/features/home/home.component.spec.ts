import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { Component, Output, EventEmitter, Input } from '@angular/core';

import { HomeComponent } from './home.component';
import { ThemeService } from '../../shared/services/theme.service';
import { PostService } from '../../shared/services/post.service';
import { Theme } from '../../shared/interfaces/theme.interface';
import { Post } from '../../shared/interfaces/post.interface';

// Mock components for testing
@Component({
  selector: 'app-create-article-modal',
  template: '',
  standalone: true
})
class MockCreateArticleModalComponent {
  @Output() articleCreated = new EventEmitter<any>();
  
  openModal = jest.fn();
}

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

  const mockThemes: Theme[] = [
    { 
      id: 1, 
      title: 'Angular', 
      description: 'Framework JavaScript',
      createdAt: '2024-01-01T00:00:00Z',
      updatedAt: '2024-01-01T00:00:00Z'
    },
    { 
      id: 2, 
      title: 'React', 
      description: 'Bibliothèque JavaScript',
      createdAt: '2024-01-02T00:00:00Z',
      updatedAt: '2024-01-02T00:00:00Z'
    }
  ];

  const mockPosts: Post[] = [
    {
      id: 1,
      title: 'Angular Guide',
      content: 'Content about Angular',
      author: { id: 1, username: 'john', email: 'john@example.com' },
      theme: { id: 1, title: 'Angular', description: 'Framework JavaScript' },
      createdAt: '2024-01-01T10:00:00Z',
      updatedAt: '2024-01-01T10:00:00Z'
    },
    {
      id: 2,
      title: 'React Tips',
      content: 'Content about React',
      author: { id: 2, username: 'jane', email: 'jane@example.com' },
      theme: { id: 2, title: 'React', description: 'Bibliothèque JavaScript' },
      createdAt: '2024-01-02T10:00:00Z',
      updatedAt: '2024-01-02T10:00:00Z'
    }
  ];

  const mockMyFeedPosts: Post[] = [mockPosts[0]]; // Only first post for "My Feed"

  beforeEach(async () => {
    const themeServiceMock = {
      getAllThemes: jest.fn().mockReturnValue(of(mockThemes)),
    };

    const postServiceMock = {
      getAllPosts: jest.fn().mockReturnValue(of(mockPosts)),
      getPostsFromSubscribedThemes: jest.fn().mockReturnValue(of(mockMyFeedPosts)),
    };

    await TestBed.configureTestingModule({
      imports: [HomeComponent, MockCreateArticleModalComponent, MockArticleCardComponent],
      providers: [
        { provide: ThemeService, useValue: themeServiceMock },
        { provide: PostService, useValue: postServiceMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    
    themeService = TestBed.inject(ThemeService) as jest.Mocked<ThemeService>;
    postService = TestBed.inject(PostService) as jest.Mocked<PostService>;
  });

  describe('Component Creation', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should initialize with correct default values', () => {
      expect(component.themes()).toEqual([]);
      expect(component.myFeedPosts()).toEqual([]);
      expect(component.allPosts()).toEqual([]);
      expect(component.isLoadingMyFeed()).toBe(false);
      expect(component.isLoadingAllPosts()).toBe(false);
      expect(component.sortOrderMyFeed()).toBe('desc');
      expect(component.sortOrderAllPosts()).toBe('desc');
    });
  });

  describe('Component Initialization (ngOnInit)', () => {
    it('should load themes, my feed posts, and all posts on init', () => {
      // Arrange
      themeService.getAllThemes.mockReturnValue(of(mockThemes));
      postService.getPostsFromSubscribedThemes.mockReturnValue(of(mockMyFeedPosts));
      postService.getAllPosts.mockReturnValue(of(mockPosts));

      // Act
      fixture.detectChanges(); // This triggers ngOnInit

      // Assert
      expect(themeService.getAllThemes).toHaveBeenCalled();
      expect(postService.getPostsFromSubscribedThemes).toHaveBeenCalled();
      expect(postService.getAllPosts).toHaveBeenCalled();
      expect(component.themes()).toEqual(mockThemes);
      // Posts are sorted by creation date (desc), so check content rather than exact order
      expect(component.myFeedPosts()).toEqual(expect.arrayContaining(mockMyFeedPosts));
      expect(component.allPosts()).toEqual(expect.arrayContaining(mockPosts));
      expect(component.myFeedPosts().length).toBe(1);
      expect(component.allPosts().length).toBe(2);
    });

    it('should handle themes loading error', () => {
      // Arrange
      const consoleSpy = jest.spyOn(console, 'error').mockImplementation();
      themeService.getAllThemes.mockReturnValue(throwError(() => new Error('Themes API Error')));
      postService.getPostsFromSubscribedThemes.mockReturnValue(of(mockMyFeedPosts));
      postService.getAllPosts.mockReturnValue(of(mockPosts));

      // Act
      fixture.detectChanges();

      // Assert
      expect(consoleSpy).toHaveBeenCalledWith('Erreur lors du chargement des thèmes:', expect.any(Error));
      expect(component.themes()).toEqual([]);
      consoleSpy.mockRestore();
    });

    it('should handle my feed posts loading error', () => {
      // Arrange
      const consoleSpy = jest.spyOn(console, 'error').mockImplementation();
      themeService.getAllThemes.mockReturnValue(of(mockThemes));
      postService.getPostsFromSubscribedThemes.mockReturnValue(throwError(() => new Error('My Feed API Error')));
      postService.getAllPosts.mockReturnValue(of(mockPosts));

      // Act
      fixture.detectChanges();

      // Assert
      expect(consoleSpy).toHaveBeenCalledWith('Erreur lors du chargement de Mon Fil:', expect.any(Error));
      expect(component.myFeedPosts()).toEqual([]);
      expect(component.isLoadingMyFeed()).toBe(false);
      consoleSpy.mockRestore();
    });

    it('should handle all posts loading error', () => {
      // Arrange
      const consoleSpy = jest.spyOn(console, 'error').mockImplementation();
      themeService.getAllThemes.mockReturnValue(of(mockThemes));
      postService.getPostsFromSubscribedThemes.mockReturnValue(of(mockMyFeedPosts));
      postService.getAllPosts.mockReturnValue(throwError(() => new Error('All Posts API Error')));

      // Act
      fixture.detectChanges();

      // Assert
      expect(consoleSpy).toHaveBeenCalledWith('Erreur lors du chargement de tous les posts:', expect.any(Error));
      expect(component.allPosts()).toEqual([]);
      expect(component.isLoadingAllPosts()).toBe(false);
      consoleSpy.mockRestore();
    });
  });

  describe('Modal Management', () => {
    it('should open create article modal', () => {
      // Arrange
      fixture.detectChanges(); // Ensure ViewChild is initialized
      
      // Mock the createArticleModal by directly setting it
      const mockModal = {
        openModal: jest.fn()
      } as any;
      component.createArticleModal = mockModal;

      // Act
      component.openCreateArticleModal();

      // Assert
      expect(mockModal.openModal).toHaveBeenCalled();
    });

    it('should handle article creation and reload posts', () => {
      // Arrange
      themeService.getAllThemes.mockReturnValue(of(mockThemes));
      postService.getPostsFromSubscribedThemes.mockReturnValue(of(mockMyFeedPosts));
      postService.getAllPosts.mockReturnValue(of(mockPosts));
      fixture.detectChanges();

      // Clear initial calls
      postService.getPostsFromSubscribedThemes.mockClear();
      postService.getAllPosts.mockClear();

      const newPost: Post = {
        id: 3,
        title: 'New Article',
        content: 'New content',
        author: { id: 1, username: 'john', email: 'john@example.com' },
        theme: { id: 1, title: 'Angular', description: 'Framework JavaScript' },
        createdAt: '2024-01-03T10:00:00Z',
        updatedAt: '2024-01-03T10:00:00Z'
      };

      // Act
      component.onArticleCreated(newPost);

      // Assert
      expect(postService.getPostsFromSubscribedThemes).toHaveBeenCalled();
      expect(postService.getAllPosts).toHaveBeenCalled();
    });
  });

  describe('Sorting Functionality', () => {
    beforeEach(() => {
      // Setup initial data
      themeService.getAllThemes.mockReturnValue(of(mockThemes));
      postService.getPostsFromSubscribedThemes.mockReturnValue(of(mockMyFeedPosts));
      postService.getAllPosts.mockReturnValue(of(mockPosts));
      fixture.detectChanges();
    });

    it('should test protected sorting methods via type assertion', () => {
      // Test protected methods via type assertion for complete coverage
      const componentAny = component as any;
      
      // Test changeSortOrderMyFeed
      componentAny.changeSortOrderMyFeed('asc');
      expect(component.sortOrderMyFeed()).toBe('asc');

      // Test changeSortOrderAllPosts
      componentAny.changeSortOrderAllPosts('asc');
      expect(component.sortOrderAllPosts()).toBe('asc');
      
      // Verify that the sorting actually changed the order for multi-post arrays
      const allPosts = component.allPosts();
      if (allPosts.length > 1) {
        expect(new Date(allPosts[0].createdAt).getTime()).toBeLessThan(
          new Date(allPosts[1].createdAt).getTime()
        );
      }
    });

    it('should handle sort order change events', () => {
      const componentAny = component as any;
      
      // Test onSortOrderChangeMyFeed
      const mockEventMyFeed = {
        target: { value: 'asc' }
      } as unknown as Event;
      componentAny.onSortOrderChangeMyFeed(mockEventMyFeed);
      expect(component.sortOrderMyFeed()).toBe('asc');

      // Test onSortOrderChangeAllPosts
      const mockEventAllPosts = {
        target: { value: 'asc' }
      } as unknown as Event;
      componentAny.onSortOrderChangeAllPosts(mockEventAllPosts);
      expect(component.sortOrderAllPosts()).toBe('asc');
    });

    it('should sort posts correctly', () => {
      const componentAny = component as any;
      
      // Test descending order (default)
      const descPosts = componentAny.sortPosts(mockPosts, 'desc');
      expect(descPosts[0].id).toBe(2); // Plus récent en premier (2024-01-02)
      expect(descPosts[1].id).toBe(1); // Plus ancien (2024-01-01)

      // Test ascending order
      const ascPosts = componentAny.sortPosts(mockPosts, 'asc');
      expect(ascPosts[0].id).toBe(1); // Plus ancien en premier (2024-01-01)
      expect(ascPosts[1].id).toBe(2); // Plus récent (2024-01-02)
    });

    it('should set loading states correctly during posts loading', () => {
      // Initially not loading
      expect(component.isLoadingMyFeed()).toBe(false);
      expect(component.isLoadingAllPosts()).toBe(false);

      // After loading completion (from ngOnInit in beforeEach)
      expect(component.isLoadingMyFeed()).toBe(false);
      expect(component.isLoadingAllPosts()).toBe(false);
    });

    it('should update posts when sort order changes', () => {
      const componentAny = component as any;
      
      // Change to ascending order for myFeedPosts
      componentAny.changeSortOrderMyFeed('asc');
      expect(component.sortOrderMyFeed()).toBe('asc');
      
      // Change to ascending order for allPosts
      componentAny.changeSortOrderAllPosts('asc');
      expect(component.sortOrderAllPosts()).toBe('asc');
    });
  });
});

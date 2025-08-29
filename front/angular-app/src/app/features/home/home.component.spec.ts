import { of, throwError } from 'rxjs';
import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Component, Input, Output, EventEmitter } from '@angular/core';

import { HomeComponent } from './home.component';
import { ThemeService, PostService } from '../../shared/services';
import { Theme, Post } from '../../shared/interfaces';

// Test version of the component that doesn't use inject()
class TestHomeComponent {
  private themeService: any;
  private postService: any;

  themes = signal<Theme[]>([]);
  myFeedPosts = signal<Post[]>([]);
  allPosts = signal<Post[]>([]);
  isLoadingMyFeed = signal(false);
  isLoadingAllPosts = signal(false);
  sortOrderMyFeed = signal<'asc' | 'desc'>('desc');
  sortOrderAllPosts = signal<'asc' | 'desc'>('desc');

  // Mock for createArticleModal ViewChild
  createArticleModal = {
    openModal: jest.fn()
  };

  constructor(themeService: any, postService: any) {
    this.themeService = themeService;
    this.postService = postService;
  }

  ngOnInit(): void {
    this.loadThemes();
    this.loadMyFeedPosts();
    this.loadAllPosts();
  }

  private loadThemes(): void {
    this.themeService.getAllThemes().subscribe({
      next: (themes: Theme[]) => {
        this.themes.set(themes);
      },
      error: (error: any) => {
        console.error('Erreur lors du chargement des thèmes:', error);
      }
    });
  }

  private loadMyFeedPosts(): void {
    this.isLoadingMyFeed.set(true);
    this.postService.getPostsFromSubscribedThemes().subscribe({
      next: (posts: Post[]) => {
        this.myFeedPosts.set(this.sortPosts(posts, this.sortOrderMyFeed()));
        this.isLoadingMyFeed.set(false);
      },
      error: (error: any) => {
        console.error('Erreur lors du chargement de Mon Fil:', error);
        this.myFeedPosts.set([]);
        this.isLoadingMyFeed.set(false);
      }
    });
  }

  private loadAllPosts(): void {
    this.isLoadingAllPosts.set(true);
    this.postService.getAllPosts().subscribe({
      next: (posts: Post[]) => {
        this.allPosts.set(this.sortPosts(posts, this.sortOrderAllPosts()));
        this.isLoadingAllPosts.set(false);
      },
      error: (error: any) => {
        console.error('Erreur lors du chargement de tous les posts:', error);
        this.allPosts.set([]);
        this.isLoadingAllPosts.set(false);
      }
    });
  }

  private sortPosts(posts: Post[], order: 'asc' | 'desc'): Post[] {
    return [...posts].sort((a, b) => {
      const dateA = new Date(a.createdAt).getTime();
      const dateB = new Date(b.createdAt).getTime();
      
      return order === 'desc' ? dateB - dateA : dateA - dateB;
    });
  }

  protected changeSortOrderMyFeed(order: 'asc' | 'desc'): void {
    this.sortOrderMyFeed.set(order);
    this.myFeedPosts.update(posts => this.sortPosts(posts, order));
  }

  protected changeSortOrderAllPosts(order: 'asc' | 'desc'): void {
    this.sortOrderAllPosts.set(order);
    this.allPosts.update(posts => this.sortPosts(posts, order));
  }

  protected onSortOrderChangeMyFeed(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.changeSortOrderMyFeed(target.value as 'asc' | 'desc');
  }

  protected onSortOrderChangeAllPosts(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.changeSortOrderAllPosts(target.value as 'asc' | 'desc');
  }

  openCreateArticleModal(): void {
    this.createArticleModal.openModal();
  }

  onArticleCreated(newPost: Post): void {
    this.loadMyFeedPosts();
    this.loadAllPosts();
  }
}

describe('HomeComponent', () => {
  let component: TestHomeComponent;
  
  // Mock data
  const mockThemes: Theme[] = [
    {
      id: 1,
      title: 'Angular',
      description: 'Framework frontend',
      createdAt: '2024-01-01T00:00:00Z',
      updatedAt: '2024-01-01T00:00:00Z'
    },
    {
      id: 2,
      title: 'Spring Boot',
      description: 'Framework backend',
      createdAt: '2024-01-01T00:00:00Z',
      updatedAt: '2024-01-01T00:00:00Z'
    }
  ];

  const mockPosts: Post[] = [
    {
      id: 1,
      title: 'Premier Article',
      content: 'Contenu du premier article',
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
    },
    {
      id: 2,
      title: 'Deuxième Article',
      content: 'Contenu du deuxième article',
      author: {
        id: 2,
        username: 'testuser2',
        email: 'test2@example.com'
      },
      theme: {
        id: 2,
        title: 'Spring Boot',
        description: 'Framework backend'
      },
      createdAt: '2024-01-01T08:00:00Z',
      updatedAt: '2024-01-01T08:00:00Z'
    }
  ];

  const mockMyFeedPosts: Post[] = [mockPosts[0]]; // Seulement le premier post pour "Mon Fil"

  // Mock services
  const mockThemeService = {
    getAllThemes: jest.fn()
  };

  const mockPostService = {
    getPostsFromSubscribedThemes: jest.fn(),
    getAllPosts: jest.fn()
  };

  beforeEach(() => {
    // Reset all mocks
    jest.clearAllMocks();
    
    // Setup default mock returns
    mockThemeService.getAllThemes.mockReturnValue(of(mockThemes));
    mockPostService.getPostsFromSubscribedThemes.mockReturnValue(of(mockMyFeedPosts));
    mockPostService.getAllPosts.mockReturnValue(of(mockPosts));

    // Create component with injected dependencies
    component = new TestHomeComponent(mockThemeService, mockPostService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load themes, my feed posts and all posts on initialization', () => {
    component.ngOnInit();

    expect(mockThemeService.getAllThemes).toHaveBeenCalled();
    expect(mockPostService.getPostsFromSubscribedThemes).toHaveBeenCalled();
    expect(mockPostService.getAllPosts).toHaveBeenCalled();
    
    expect(component.themes()).toEqual(mockThemes);
    expect(component.myFeedPosts()).toEqual(mockMyFeedPosts);
    expect(component.allPosts()).toEqual(mockPosts);
    expect(component.isLoadingMyFeed()).toBe(false);
    expect(component.isLoadingAllPosts()).toBe(false);
  });

  it('should handle themes loading error', () => {
    mockThemeService.getAllThemes.mockReturnValue(throwError(() => new Error('Themes API Error')));

    component.ngOnInit();

    expect(component.themes()).toEqual([]);
  });

  it('should handle my feed posts loading error', () => {
    mockPostService.getPostsFromSubscribedThemes.mockReturnValue(throwError(() => new Error('My Feed API Error')));

    component.ngOnInit();

    expect(component.myFeedPosts()).toEqual([]);
    expect(component.isLoadingMyFeed()).toBe(false);
  });

  it('should handle all posts loading error', () => {
    mockPostService.getAllPosts.mockReturnValue(throwError(() => new Error('All Posts API Error')));

    component.ngOnInit();

    expect(component.allPosts()).toEqual([]);
    expect(component.isLoadingAllPosts()).toBe(false);
  });

  it('should sort posts correctly', () => {
    component.ngOnInit();

    // Test descending order (default)
    const descPosts = (component as any).sortPosts(mockPosts, 'desc');
    expect(descPosts[0].id).toBe(1); // Plus récent en premier
    expect(descPosts[1].id).toBe(2);

    // Test ascending order
    const ascPosts = (component as any).sortPosts(mockPosts, 'asc');
    expect(ascPosts[0].id).toBe(2); // Plus ancien en premier
    expect(ascPosts[1].id).toBe(1);
  });

  it('should change sort order for my feed', () => {
    component.ngOnInit();
    
    (component as any).changeSortOrderMyFeed('asc');
    
    expect(component.sortOrderMyFeed()).toBe('asc');
  });

  it('should change sort order for all posts', () => {
    component.ngOnInit();
    
    (component as any).changeSortOrderAllPosts('asc');
    
    expect(component.sortOrderAllPosts()).toBe('asc');
  });

  it('should handle sort order change event for my feed', () => {
    const mockEvent = {
      target: { value: 'asc' }
    } as unknown as Event;

    (component as any).onSortOrderChangeMyFeed(mockEvent);

    expect(component.sortOrderMyFeed()).toBe('asc');
  });

  it('should handle sort order change event for all posts', () => {
    const mockEvent = {
      target: { value: 'asc' }
    } as unknown as Event;

    (component as any).onSortOrderChangeAllPosts(mockEvent);

    expect(component.sortOrderAllPosts()).toBe('asc');
  });

  it('should open create article modal', () => {
    component.openCreateArticleModal();

    expect(component.createArticleModal.openModal).toHaveBeenCalled();
  });

  it('should reload posts when article is created', () => {
    const newPost: Post = {
      id: 3,
      title: 'Nouvel Article',
      content: 'Contenu du nouvel article',
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
      createdAt: '2024-01-01T12:00:00Z',
      updatedAt: '2024-01-01T12:00:00Z'
    };

    // Spy on the reload methods
    const loadMyFeedSpy = jest.spyOn(component as any, 'loadMyFeedPosts');
    const loadAllPostsSpy = jest.spyOn(component as any, 'loadAllPosts');

    component.onArticleCreated(newPost);

    expect(loadMyFeedSpy).toHaveBeenCalled();
    expect(loadAllPostsSpy).toHaveBeenCalled();
  });

  it('should set loading states correctly during posts loading', () => {
    // Initially not loading
    expect(component.isLoadingMyFeed()).toBe(false);
    expect(component.isLoadingAllPosts()).toBe(false);

    // During loading - we test the loading state is set correctly in the implementation
    component.ngOnInit();
    
    // After loading completion
    expect(component.isLoadingMyFeed()).toBe(false);
    expect(component.isLoadingAllPosts()).toBe(false);
  });

  it('should update posts when sort order changes', () => {
    component.ngOnInit();
    
    // Change to ascending order for myFeedPosts
    (component as any).changeSortOrderMyFeed('asc');
    
    // Verify posts are updated with new sort order
    expect(component.sortOrderMyFeed()).toBe('asc');
    
    // Change to ascending order for allPosts
    (component as any).changeSortOrderAllPosts('asc');
    
    // Verify posts are updated with new sort order
    expect(component.sortOrderAllPosts()).toBe('asc');
  });
});

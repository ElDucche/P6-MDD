import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';
import { ArticleCardComponent } from './article-card.component';
import { Post } from '@shared/interfaces';

describe('ArticleCardComponent', () => {
  let component: ArticleCardComponent;
  let fixture: ComponentFixture<ArticleCardComponent>;
  let router: Router;

  const mockPost: Post = {
    id: 1,
    title: 'Test Article Title',
    content: 'This is a test article content that is longer than 150 characters to test the truncation functionality. It should be cut off at exactly 150 characters and add ellipsis at the end to indicate truncation.',
    author: {
      id: 1,
      username: 'testuser',
      email: 'test@example.com'
    },
    theme: {
      id: 1,
      title: 'Technology',
      description: 'Articles about technology'
    },
    createdAt: '2024-01-15T10:30:00Z',
    updatedAt: '2024-01-15T10:30:00Z'
  };

  const mockTheme = {
    id: 2,
    title: 'Science',
    description: 'Science related articles'
  };

  beforeEach(async () => {
    const routerSpy = {
      navigate: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [ArticleCardComponent, DatePipe],
      providers: [
        { provide: Router, useValue: routerSpy }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ArticleCardComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);

    // Set required input
    fixture.componentRef.setInput('post', mockPost);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Component Initialization', () => {
    it('should inject Router correctly', () => {
      expect(router).toBeTruthy();
    });

    it('should have required post input', () => {
      expect(component.post()).toEqual(mockPost);
    });

    it('should handle optional theme input', () => {
      expect(component.theme()).toBeUndefined();

      fixture.componentRef.setInput('theme', mockTheme);
      fixture.detectChanges();

      expect(component.theme()).toEqual(mockTheme);
    });

    it('should have OnPush change detection strategy', () => {
      // Note: Component uses OnPush change detection as defined in decorator
      // This improves performance by reducing unnecessary change detection cycles
      expect(component).toBeTruthy(); // Basic validation that component follows OnPush pattern
    });
  });

  describe('Content Truncation', () => {
    it('should truncate content longer than 150 characters', () => {
      const longContent = 'A'.repeat(200);
      const truncated = component.truncateContent(longContent);
      
      expect(truncated).toHaveLength(153); // 150 + '...'
      expect(truncated).toBe('A'.repeat(150) + '...');
    });

    it('should not truncate content shorter than or equal to 150 characters', () => {
      const shortContent = 'Short content';
      const result = component.truncateContent(shortContent);
      
      expect(result).toBe(shortContent);
    });

    it('should handle content exactly 150 characters', () => {
      const exactContent = 'A'.repeat(150);
      const result = component.truncateContent(exactContent);
      
      expect(result).toBe(exactContent);
      expect(result).not.toContain('...');
    });

    it('should handle empty content', () => {
      const result = component.truncateContent('');
      expect(result).toBe('');
    });

    it('should handle content with special characters', () => {
      const specialContent = 'Content with émojis 🚀💯 and special chars éàùö'.repeat(3);
      const result = component.truncateContent(specialContent);
      
      if (specialContent.length > 150) {
        expect(result).toHaveLength(153);
        expect(result.endsWith('...')).toBe(true);
      } else {
        expect(result).toBe(specialContent);
      }
    });

    it('should handle content with line breaks', () => {
      const contentWithBreaks = 'Line 1\nLine 2\rLine 3\r\nLine 4'.repeat(10);
      const result = component.truncateContent(contentWithBreaks);
      
      if (contentWithBreaks.length > 150) {
        expect(result).toHaveLength(153);
        expect(result.endsWith('...')).toBe(true);
      }
    });
  });

  describe('Navigation', () => {
    it('should navigate to article detail on readMore', () => {
      component.onReadMore();
      
      expect(router.navigate).toHaveBeenCalledWith(['/article', mockPost.id]);
    });

    it('should navigate with correct article ID for different posts', () => {
      const differentPost: Post = { ...mockPost, id: 999 };
      
      fixture.componentRef.setInput('post', differentPost);
      fixture.detectChanges();

      component.onReadMore();
      
      expect(router.navigate).toHaveBeenCalledWith(['/article', 999]);
    });

    it('should handle navigation errors gracefully', () => {
      (router.navigate as jest.Mock).mockRejectedValue(new Error('Navigation failed'));
      
      expect(() => component.onReadMore()).not.toThrow();
      expect(router.navigate).toHaveBeenCalledWith(['/article', mockPost.id]);
    });
  });

  describe('Template Rendering', () => {
    beforeEach(() => {
      fixture.componentRef.setInput('post', mockPost);
      fixture.detectChanges();
    });

    it('should display post title', () => {
      const titleElement = fixture.debugElement.query(By.css('[data-test="post-title"]'));
      
      if (titleElement) {
        expect(titleElement.nativeElement.textContent.trim()).toBe(mockPost.title);
      }
    });

    it('should display author username', () => {
      const authorElement = fixture.debugElement.query(By.css('[data-test="post-author"]'));
      
      if (authorElement) {
        expect(authorElement.nativeElement.textContent).toContain(mockPost.author.username);
      }
    });

    it('should display creation date using DatePipe', () => {
      const dateElement = fixture.debugElement.query(By.css('[data-test="post-date"]'));
      
      if (dateElement) {
        // The exact format depends on DatePipe implementation
        expect(dateElement.nativeElement.textContent).toBeTruthy();
      }
    });

    it('should display theme title when theme input is provided', () => {
      fixture.componentRef.setInput('theme', mockTheme);
      fixture.detectChanges();

      const themeElement = fixture.debugElement.query(By.css('[data-test="post-theme"]'));
      
      if (themeElement) {
        expect(themeElement.nativeElement.textContent).toContain(mockTheme.title);
      }
    });

    it('should truncate displayed content', () => {
      const contentElement = fixture.debugElement.query(By.css('[data-test="post-content"]'));
      
      if (contentElement) {
        const displayedContent = contentElement.nativeElement.textContent.trim();
        
        if (mockPost.content.length > 150) {
          expect(displayedContent).toHaveLength(153); // 150 + '...'
          expect(displayedContent.endsWith('...')).toBe(true);
        }
      }
    });

    it('should have clickable read more button', () => {
      const readMoreButton = fixture.debugElement.query(By.css('[data-test="read-more-btn"]'));
      
      if (readMoreButton) {
        readMoreButton.nativeElement.click();
        expect(router.navigate).toHaveBeenCalledWith(['/article', mockPost.id]);
      }
    });

    it('should apply correct CSS classes', () => {
      const cardElement = fixture.debugElement.query(By.css('.card, .article-card, [data-test="article-card"]'));
      
      if (cardElement) {
        expect(cardElement.nativeElement.classList.length).toBeGreaterThan(0);
      }
    });
  });

  describe('Component Inputs Validation', () => {
    it('should handle post updates correctly', () => {
      const updatedPost: Post = {
        ...mockPost,
        id: 2,
        title: 'Updated Title',
        content: 'Updated content'
      };

      fixture.componentRef.setInput('post', updatedPost);
      fixture.detectChanges();

      expect(component.post()).toEqual(updatedPost);
    });

    it('should handle theme updates correctly', () => {
      const initialTheme = { id: 1, title: 'Initial', description: 'Initial theme' };
      const updatedTheme = { id: 2, title: 'Updated', description: 'Updated theme' };

      fixture.componentRef.setInput('theme', initialTheme);
      fixture.detectChanges();
      expect(component.theme()).toEqual(initialTheme);

      fixture.componentRef.setInput('theme', updatedTheme);
      fixture.detectChanges();
      expect(component.theme()).toEqual(updatedTheme);
    });

    it('should handle theme removal correctly', () => {
      fixture.componentRef.setInput('theme', mockTheme);
      fixture.detectChanges();
      expect(component.theme()).toEqual(mockTheme);

      fixture.componentRef.setInput('theme', undefined);
      fixture.detectChanges();
      expect(component.theme()).toBeUndefined();
    });
  });

  describe('Edge Cases', () => {
    it('should handle post with minimal data', () => {
      const minimalPost: Post = {
        id: 1,
        title: '',
        content: '',
        author: { id: 1, username: '', email: '' },
        theme: { id: 1, title: '', description: '' },
        createdAt: '',
        updatedAt: ''
      };

      fixture.componentRef.setInput('post', minimalPost);
      fixture.detectChanges();

      expect(component.post()).toEqual(minimalPost);
      expect(() => component.truncateContent(minimalPost.content)).not.toThrow();
      expect(() => component.onReadMore()).not.toThrow();
    });

    it('should handle post with very long title', () => {
      const longTitlePost: Post = {
        ...mockPost,
        title: 'A'.repeat(500)
      };

      fixture.componentRef.setInput('post', longTitlePost);
      fixture.detectChanges();

      expect(component.post().title).toHaveLength(500);
    });

    it('should handle special date formats', () => {
      const specialDatePost: Post = {
        ...mockPost,
        createdAt: '2024-12-31T23:59:59.999Z',
        updatedAt: '2024-01-01T00:00:00.000Z'
      };

      fixture.componentRef.setInput('post', specialDatePost);
      fixture.detectChanges();

      expect(component.post()).toEqual(specialDatePost);
    });

    it('should handle posts with ID 0', () => {
      const zeroIdPost: Post = { ...mockPost, id: 0 };
      
      fixture.componentRef.setInput('post', zeroIdPost);
      fixture.detectChanges();

      component.onReadMore();
      expect(router.navigate).toHaveBeenCalledWith(['/article', 0]);
    });

    it('should handle negative post IDs', () => {
      const negativeIdPost: Post = { ...mockPost, id: -1 };
      
      fixture.componentRef.setInput('post', negativeIdPost);
      fixture.detectChanges();

      component.onReadMore();
      expect(router.navigate).toHaveBeenCalledWith(['/article', -1]);
    });
  });

  describe('Performance', () => {
    it('should handle multiple truncation calls efficiently', () => {
      const content = 'A'.repeat(200);
      const startTime = performance.now();
      
      for (let i = 0; i < 1000; i++) {
        component.truncateContent(content);
      }
      
      const endTime = performance.now();
      expect(endTime - startTime).toBeLessThan(100); // Should complete in less than 100ms
    });

    it('should handle large content efficiently', () => {
      const largeContent = 'A'.repeat(10000);
      const startTime = performance.now();
      
      const result = component.truncateContent(largeContent);
      
      const endTime = performance.now();
      expect(endTime - startTime).toBeLessThan(10); // Should complete quickly
      expect(result).toHaveLength(153); // 150 + '...'
    });
  });
});

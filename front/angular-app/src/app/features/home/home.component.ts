import { Component, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ThemeService, Theme } from '../../services/theme.service';
import { PostService, Post } from '../../services/post.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
})
export class HomeComponent implements OnInit {
  private readonly themeService = inject(ThemeService);
  private readonly postService = inject(PostService);
  
  timeRanges = [
    { value: 'day', viewValue: 'Today' },
    { value: 'week', viewValue: 'This Week' },
    { value: 'month', viewValue: 'This Month' },
    { value: 'year', viewValue: 'This Year' },
  ];

  themes = signal<Theme[]>([]);
  posts = signal<Post[]>([]);
  selectedTimeRange = signal(this.timeRanges[0].value);
  selectedTheme = signal<number | null>(null);
  isLoadingPosts = signal(false);

  ngOnInit(): void {
    this.loadThemes();
  }

  private loadThemes(): void {
    this.themeService.getAllThemes().subscribe({
      next: (themes) => {
        this.themes.set(themes);
        // Sélectionner le premier thème par défaut
        if (themes.length > 0) {
          this.selectedTheme.set(themes[0].id);
          this.loadPostsByTheme(themes[0].id);
        }
      },
      error: (error) => {
        console.error('Erreur lors du chargement des thèmes:', error);
      }
    });
  }

  onThemeChange(themeId: number): void {
    this.selectedTheme.set(themeId);
    this.loadPostsByTheme(themeId);
  }

  private loadPostsByTheme(themeId: number): void {
    this.isLoadingPosts.set(true);
    this.postService.getPostsByTheme(themeId).subscribe({
      next: (posts) => {
        this.posts.set(posts);
        this.isLoadingPosts.set(false);
        console.log(`${posts.length} posts trouvés pour le thème ${themeId}`);
      },
      error: (error) => {
        console.error('Erreur lors du chargement des posts:', error);
        this.posts.set([]);
        this.isLoadingPosts.set(false);
      }
    });
  }
}

import { Component, signal, inject, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ThemeService, Theme } from '../../services/theme.service';
import { PostService, Post } from '../../services/post.service';
import { ArticleCardComponent } from '../../components/article-card/article-card.component';
import { CreateArticleModalComponent } from '../../components/create-article-modal/create-article-modal.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, ArticleCardComponent, CreateArticleModalComponent],
  templateUrl: './home.component.html',
})
export class HomeComponent implements OnInit {
  private readonly themeService = inject(ThemeService);
  private readonly postService = inject(PostService);
  
  @ViewChild('createArticleModal') createArticleModal!: CreateArticleModalComponent;
  
  timeRanges = [
    { value: 'day', viewValue: 'Aujourd\'hui' },
    { value: 'week', viewValue: 'Cette semaine' },
    { value: 'month', viewValue: 'Ce mois-ci' },
    { value: 'year', viewValue: 'Cette année' },
  ];

  themes = signal<Theme[]>([]);
  posts = signal<Post[]>([]);
  allPosts = signal<Post[]>([]);
  selectedTimeRange = signal(this.timeRanges[0].value);
  selectedTheme = signal<number | null>(null);
  isLoadingPosts = signal(false);
  isLoadingAllPosts = signal(false);

  ngOnInit(): void {
    this.loadThemes();
    this.loadAllPosts();
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

  private loadAllPosts(): void {
    this.isLoadingAllPosts.set(true);
    this.postService.getAllPosts().subscribe({
      next: (posts) => {
        this.allPosts.set(posts);
        this.isLoadingAllPosts.set(false);
        console.log(`${posts.length} posts trouvés au total`);
      },
      error: (error) => {
        console.error('Erreur lors du chargement de tous les posts:', error);
        this.allPosts.set([]);
        this.isLoadingAllPosts.set(false);
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

  getThemeById(themeId: number): Theme | undefined {
    return this.themes().find(theme => theme.id === themeId);
  }

  openCreateArticleModal(): void {
    this.createArticleModal.openModal();
  }

  /**
   * Méthode appelée quand un nouvel article est créé
   * Recharge les listes d'articles pour afficher le nouveau post
   */
  onArticleCreated(newPost: Post): void {
    console.log('Nouvel article créé:', newPost);
    
    // Recharger tous les posts
    this.loadAllPosts();
    
    // Si le nouvel article correspond au thème sélectionné, recharger aussi les posts filtrés
    if (this.selectedTheme() === newPost.themeId) {
      this.loadPostsByTheme(newPost.themeId);
    }
  }
}

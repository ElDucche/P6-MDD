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
  myFeedPosts = signal<Post[]>([]);
  allPosts = signal<Post[]>([]);
  selectedTimeRange = signal(this.timeRanges[0].value);
  selectedTheme = signal<number | null>(null);
  isLoadingMyFeed = signal(false);
  isLoadingAllPosts = signal(false);

  ngOnInit(): void {
    this.loadThemes();
    this.loadMyFeedPosts();
    this.loadAllPosts();
  }

  private loadThemes(): void {
    this.themeService.getAllThemes().subscribe({
      next: (themes) => {
        this.themes.set(themes);
      },
      error: (error) => {
        console.error('Erreur lors du chargement des thèmes:', error);
      }
    });
  }

  private loadMyFeedPosts(): void {
    this.isLoadingMyFeed.set(true);
    this.postService.getPostsFromSubscribedThemes().subscribe({
      next: (posts) => {
        this.myFeedPosts.set(posts);
        this.isLoadingMyFeed.set(false);
        console.log(`${posts.length} posts trouvés dans Mon Fil (thèmes abonnés)`);
      },
      error: (error) => {
        console.error('Erreur lors du chargement de Mon Fil:', error);
        this.myFeedPosts.set([]);
        this.isLoadingMyFeed.set(false);
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
    // Note: cette méthode peut être supprimée si elle n'est plus utilisée
    // car Mon Fil ne dépend plus d'un thème sélectionné
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
    
    // Recharger Mon Fil et tous les posts
    this.loadMyFeedPosts();
    this.loadAllPosts();
  }
}

import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DatePipe, Location } from '@angular/common';
import { Post, PostService } from '../../services/post.service';
import { Theme, ThemeService } from '../../services/theme.service';

@Component({
  selector: 'app-article',
  templateUrl: './article.component.html',
  styleUrl: './article.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [DatePipe]
})
export class ArticleComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly location = inject(Location);
  private readonly postService = inject(PostService);
  private readonly themeService = inject(ThemeService);

  protected readonly post = signal<Post | null>(null);
  protected readonly theme = signal<Theme | null>(null);
  protected readonly isLoading = signal(true);
  protected readonly error = signal<string | null>(null);

  ngOnInit(): void {
    const articleId = this.route.snapshot.paramMap.get('id');
    
    if (!articleId || isNaN(Number(articleId))) {
      this.error.set('ID d\'article invalide');
      this.isLoading.set(false);
      return;
    }

    this.loadArticle(Number(articleId));
  }

  /**
   * Charge l'article et son thème associé
   */
  private loadArticle(id: number): void {
    this.isLoading.set(true);
    this.error.set(null);

    this.postService.getPostById(id).subscribe({
      next: (post) => {
        this.post.set(post);
        this.loadTheme(post.themeId);
      },
      error: (error) => {
        console.error('Erreur lors du chargement de l\'article:', error);
        this.error.set('Article non trouvé');
        this.isLoading.set(false);
      }
    });
  }

  /**
   * Charge les informations du thème
   */
  private loadTheme(themeId: number): void {
    this.themeService.getThemeById(themeId).subscribe({
      next: (theme) => {
        this.theme.set(theme);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.warn('Thème non trouvé (ID:', themeId, '):', error);
        // On continue sans le thème, ce n'est pas bloquant pour l'affichage de l'article
        this.theme.set(null);
        this.isLoading.set(false);
      }
    });
  }

  /**
   * Retour à la page précédente
   */
  goBack(): void {
    this.location.back();
  }

  /**
   * Navigation vers la liste des articles du même thème
   */
  goToTheme(): void {
    if (this.theme()) {
      this.router.navigate(['/themes']);
    }
  }
}

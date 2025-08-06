import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DatePipe, Location } from '@angular/common';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { Post, PostService } from '../../services/post.service';
import { Theme, ThemeService } from '../../services/theme.service';
import { Comment } from '../../shared/interfaces/comment.interface';
import { CommentService } from '../../services/comment.service';

@Component({
  selector: 'app-article',
  templateUrl: './article.component.html',
  styleUrl: './article.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [DatePipe, ReactiveFormsModule]
})
export class ArticleComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly location = inject(Location);
  private readonly postService = inject(PostService);
  private readonly themeService = inject(ThemeService);
  private readonly commentService = inject(CommentService);

  protected readonly post = signal<Post | null>(null);
  protected readonly theme = signal<Theme | null>(null);
  protected readonly comments = signal<Comment[]>([]);
  protected readonly isLoading = signal(true);
  protected readonly isLoadingComments = signal(false);
  protected readonly isSubmittingComment = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly sortOrder = signal<'asc' | 'desc'>('desc');
  
  // Formulaire pour nouveau commentaire
  protected readonly commentControl = new FormControl('', [
    Validators.required,
    Validators.minLength(1),
    Validators.maxLength(1000)
  ]);

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
        this.loadComments(post.id);
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
   * Charge les commentaires de l'article
   */
  private loadComments(postId: number): void {
    this.isLoadingComments.set(true);
    
    this.commentService.getCommentsByPostId(postId).subscribe({
      next: (comments) => {
        this.comments.set(this.sortComments(comments));
        this.isLoadingComments.set(false);
      },
      error: (error) => {
        console.warn('Erreur lors du chargement des commentaires:', error);
        this.comments.set([]);
        this.isLoadingComments.set(false);
      }
    });
  }

  /**
   * Trie les commentaires selon l'ordre sélectionné
   */
  private sortComments(comments: Comment[]): Comment[] {
    return [...comments].sort((a, b) => {
      const dateA = new Date(a.createdAt).getTime();
      const dateB = new Date(b.createdAt).getTime();
      
      return this.sortOrder() === 'desc' ? dateB - dateA : dateA - dateB;
    });
  }

  /**
   * Change l'ordre de tri des commentaires
   */
  protected changeSortOrder(order: 'asc' | 'desc'): void {
    this.sortOrder.set(order);
    this.comments.update(comments => this.sortComments(comments));
  }

  /**
   * Gère le changement d'ordre de tri via l'événement select
   */
  protected onSortOrderChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.changeSortOrder(target.value as 'asc' | 'desc');
  }

  /**
   * Soumet un nouveau commentaire
   */
  protected submitComment(): void {
    if (this.commentControl.invalid || !this.post() || this.isSubmittingComment()) {
      return;
    }

    const content = this.commentControl.value!.trim();
    if (!content) {
      return;
    }

    this.isSubmittingComment.set(true);

    const commentData = {
      content,
      postId: this.post()!.id
    };

    this.commentService.createComment(commentData).subscribe({
      next: (newComment) => {
        // Ajouter le nouveau commentaire et trier la liste
        this.comments.update(comments => this.sortComments([newComment, ...comments]));
        this.commentControl.reset();
        this.isSubmittingComment.set(false);
        console.log('Commentaire créé avec succès:', newComment);
      },
      error: (error) => {
        console.error('Erreur lors de la création du commentaire:', error);
        this.isSubmittingComment.set(false);
        // Ici on pourrait ajouter une notification d'erreur
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

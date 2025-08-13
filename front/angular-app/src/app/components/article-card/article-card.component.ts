import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import { Post } from '@shared/interfaces';

@Component({
  selector: 'app-article-card',
  templateUrl: './article-card.component.html',
  styleUrl: './article-card.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [DatePipe]
})
export class ArticleCardComponent {
  readonly post = input.required<Post>();
  readonly theme = input<{ id: number; title: string; description: string }>();

  constructor(private readonly router: Router) {}

  /**
   * Tronque le contenu à 150 caractères maximum
   */
  truncateContent(content: string): string {
    if (content.length <= 150) {
      return content;
    }
    return content.substring(0, 150) + '...';
  }

  /**
   * Navigation vers la page de détail de l'article
   */
  onReadMore(): void {
    this.router.navigate(['/article', this.post().id]);
  }
}

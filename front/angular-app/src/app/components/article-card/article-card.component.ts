import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { DatePipe } from '@angular/common';
import { Post } from '../../services/post.service';
import { Theme } from '../../services/theme.service';

@Component({
  selector: 'app-article-card',
  templateUrl: './article-card.component.html',
  styleUrl: './article-card.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [DatePipe]
})
export class ArticleCardComponent {
  readonly post = input.required<Post>();
  readonly theme = input<Theme>();
}

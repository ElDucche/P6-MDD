import { ChangeDetectionStrategy, Component, inject, signal, output } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { ThemeService, Theme } from '../../services/theme.service';
import { PostService, Post } from '../../services/post.service';

@Component({
  selector: 'app-create-article-modal',
  templateUrl: './create-article-modal.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule]
})
export class CreateArticleModalComponent {
  private readonly fb = inject(FormBuilder);
  private readonly themeService = inject(ThemeService);
  private readonly postService = inject(PostService);

  readonly themes = signal<Theme[]>([]);
  readonly isLoading = signal(false);

  // Événement émis quand un article est créé avec succès
  readonly articleCreated = output<Post>();

  readonly articleForm = this.fb.group({
    title: ['', [Validators.required, Validators.minLength(3)]],
    content: ['', [Validators.required, Validators.minLength(10)]],
    themeId: ['', Validators.required]
  });

  constructor() {
    this.loadThemes();
  }

  private loadThemes(): void {
    this.themeService.getAllThemes().subscribe({
      next: (themes) => this.themes.set(themes),
      error: (error) => console.error('Erreur lors du chargement des thèmes:', error)
    });
  }

  openModal(): void {
    const modal = document.getElementById('create_article_modal') as HTMLDialogElement;
    modal?.showModal();
  }

  closeModal(): void {
    const modal = document.getElementById('create_article_modal') as HTMLDialogElement;
    modal?.close();
    this.articleForm.reset();
  }

  onSubmit(): void {
    if (this.articleForm.valid) {
      this.isLoading.set(true);
      
      const formValue = this.articleForm.value;
      const newPost = {
        title: formValue.title!,
        content: formValue.content!,
        themeId: parseInt(formValue.themeId!)
      };

      this.postService.createPost(newPost).subscribe({
        next: (post) => {
          this.articleCreated.emit(post);
          this.articleForm.reset();
          this.closeModal();
        },
        error: (error) => {
          console.error('Erreur lors de la création de l\'article:', error);
          this.isLoading.set(false);
        }
      });
    }
  }
}

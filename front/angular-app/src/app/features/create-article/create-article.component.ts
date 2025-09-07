import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ThemeService, PostService } from '@shared/services';
import { AlertService } from '@core/services/alert.service';
import { Theme } from '@shared/interfaces';

@Component({
  selector: 'app-create-article',
  templateUrl: './create-article.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule]
})
export class CreateArticleComponent {
  private readonly fb = inject(FormBuilder);
  private readonly themeService = inject(ThemeService);
  private readonly postService = inject(PostService);
  private readonly alertService = inject(AlertService);
  private readonly router = inject(Router);

  readonly themes = signal<Theme[]>([]);
  readonly isLoading = signal(false);

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
      error: (error) => {
        console.error('Erreur lors du chargement des thèmes:', error);
        this.alertService.showAlert({
          type: 'error',
          message: 'Erreur lors du chargement des thèmes'
        });
      }
    });
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
          this.alertService.showAlert({
            type: 'success',
            message: 'Article créé avec succès !'
          });
          this.router.navigate(['/home']);
        },
        error: (error) => {
          console.error('Erreur lors de la création de l\'article:', error);
          this.alertService.showAlert({
            type: 'error',
            message: 'Erreur lors de la création de l\'article'
          });
          this.isLoading.set(false);
        }
      });
    } else {
      this.alertService.showAlert({
        type: 'error',
        message: 'Veuillez corriger les erreurs du formulaire'
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/home']);
  }
}

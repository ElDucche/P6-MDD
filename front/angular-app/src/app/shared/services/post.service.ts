import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ConfigService } from '../../core/services/config.service';
import { Post, PostCreateRequest } from '../interfaces/post.interface';

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private readonly http = inject(HttpClient);
  private readonly config = inject(ConfigService);

  /**
   * Récupère tous les posts
   */
  getAllPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(this.config.endpoints.posts.all);
  }

  /**
   * Récupère les posts d'un thème spécifique
   */
  getPostsByTheme(themeId: number): Observable<Post[]> {
    return this.http.get<Post[]>(this.config.endpoints.posts.byTheme(themeId));
  }

  /**
   * Récupère un post par son ID
   */
  getPostById(id: number): Observable<Post> {
    return this.http.get<Post>(this.config.endpoints.posts.byId(id));
  }

  /**
   * Crée un nouveau post
   * L'authorId sera automatiquement extrait du token JWT côté backend
   */
  createPost(post: PostCreateRequest): Observable<Post> {
    return this.http.post<Post>(this.config.endpoints.posts.all, post);
  }

  /**
   * Récupère les posts des thèmes auxquels l'utilisateur courant est abonné
   */
  getPostsFromSubscribedThemes(): Observable<Post[]> {
    return this.http.get<Post[]>(this.config.endpoints.posts.subscribed);
  }
}

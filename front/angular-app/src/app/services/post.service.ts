import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ConfigService } from '../core/services/config.service';

export interface Post {
  id: number;
  title: string;
  content: string;
  author: {
    id: number;
    username: string;
    email: string;
  };
  theme: {
    id: number;
    title: string;
    description: string;
  };
  createdAt: string;
  updatedAt: string;
}

export interface PostCreateRequest {
  title: string;
  content: string;
  themeId: number;
}

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

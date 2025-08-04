import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Post {
  id: number;
  title: string;
  content: string;
  authorId: number;
  themeId: number;
  createdAt: string;
  updatedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}api/posts`;

  /**
   * Récupère tous les posts
   */
  getAllPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(this.apiUrl);
  }

  /**
   * Récupère les posts d'un thème spécifique
   */
  getPostsByTheme(themeId: number): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/theme/${themeId}`);
  }

  /**
   * Récupère un post par son ID
   */
  getPostById(id: number): Observable<Post> {
    return this.http.get<Post>(`${this.apiUrl}/${id}`);
  }

  /**
   * Crée un nouveau post
   * L'authorId sera automatiquement extrait du token JWT côté backend
   */
  createPost(post: Omit<Post, 'id' | 'authorId' | 'createdAt' | 'updatedAt'>): Observable<Post> {
    return this.http.post<Post>(this.apiUrl, post);
  }
}

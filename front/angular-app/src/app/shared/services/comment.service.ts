import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Comment, CreateCommentRequest } from '../interfaces/comment.interface';
import { ConfigService } from '../../core/services/config.service';

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private readonly http = inject(HttpClient);
  private readonly config = inject(ConfigService);

  /**
   * Récupère tous les commentaires d'un post
   */
  getCommentsByPostId(postId: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(this.config.endpoints.comments.byPost(postId));
  }

  /**
   * Crée un nouveau commentaire
   */
  createComment(commentData: CreateCommentRequest): Observable<Comment> {
    return this.http.post<Comment>(this.config.endpoints.comments.all, commentData);
  }

  /**
   * Supprime un commentaire
   */
  deleteComment(commentId: number): Observable<void> {
    return this.http.delete<void>(`${this.config.apiUrl}/api/comments/${commentId}`);
  }
}

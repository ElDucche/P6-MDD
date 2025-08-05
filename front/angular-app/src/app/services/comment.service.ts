import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Comment, CreateCommentRequest } from '../shared/interfaces/comment.interface';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}api/comments`;

  /**
   * Récupère tous les commentaires d'un post
   */
  getCommentsByPostId(postId: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.apiUrl}/post/${postId}`);
  }

  /**
   * Crée un nouveau commentaire
   */
  createComment(commentData: CreateCommentRequest): Observable<Comment> {
    return this.http.post<Comment>(this.apiUrl, commentData);
  }

  /**
   * Supprime un commentaire
   */
  deleteComment(commentId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${commentId}`);
  }
}

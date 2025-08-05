export interface Comment {
  id: number;
  content: string;
  authorId: number;
  authorUsername?: string;
  postId: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateCommentRequest {
  content: string;
  postId: number;
}

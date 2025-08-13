export interface Comment {
  id: number;
  content: string;
  author: {
    id: number;
    username: string;
    email: string;
  };
  post: {
    id: number;
    title: string;
  };
  createdAt: string;
  updatedAt: string;
}

export interface CreateCommentRequest {
  content: string;
  postId: number;
}

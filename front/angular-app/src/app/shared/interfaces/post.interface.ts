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

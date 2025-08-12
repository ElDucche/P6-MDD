export interface Subscription {
  id: {
    userId: number;
    themeId: number;
  };
  user: {
    id: number;
    username: string;
    email: string;
  };
  theme: {
    id: number;
    title: string;
    description: string;
  };
  createdAt?: string;
}

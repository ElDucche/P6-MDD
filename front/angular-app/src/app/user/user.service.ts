import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { User } from './user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private apiUrl = 'http://localhost:8080/api/user'; // Replace with your actual API URL

  constructor(private http: HttpClient) { }

  getUser(id: number): Observable<User> {
    // Replace with actual API call
    const mockUser: User = { id: 1, username: 'testuser', email: 'test@example.com' };
    return of(mockUser);
  }
}

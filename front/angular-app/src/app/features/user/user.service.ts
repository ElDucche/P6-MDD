import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from './user.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly apiUrl = `${environment.apiUrl}api/users`;

  constructor(private readonly http: HttpClient) { }

  getUser(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/me`);
  }

  updateUser(user: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/me`, user);
  }

  deleteUser(): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/me`);
  }
}

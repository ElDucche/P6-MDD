import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../interfaces/user.interface';
import { ConfigService } from '../../core/services/config.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(
    private readonly http: HttpClient,
    private readonly config: ConfigService
  ) { }

  getUser(): Observable<User> {
    return this.http.get<User>(this.config.endpoints.users.me);
  }

  updateUser(user: Partial<User>): Observable<User> {
    return this.http.put<User>(this.config.endpoints.users.me, user);
  }

  deleteUser(): Observable<void> {
    return this.http.delete<void>(this.config.endpoints.users.me);
  }
}

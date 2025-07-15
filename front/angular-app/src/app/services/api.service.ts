import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // Exemple d'appel GET
  get(path: string) {
    return this.http.get(`${this.apiUrl}/${path}`);
  }

  // Exemple d'appel POST
  post(path: string, body: any) {
    return this.http.post(`${this.apiUrl}/${path}`, body);
  }
}

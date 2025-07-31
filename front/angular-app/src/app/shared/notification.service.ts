import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  readonly message = signal<string>('');

  setMessage(msg: string) {
    this.message.set(msg);
  }

  clear() {
    this.message.set('');
  }
}

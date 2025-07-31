import { Injectable, signal } from '@angular/core';
import { Alert } from '../models/alert.model';

@Injectable({
  providedIn: 'root'
})
export class AlertService {
  public alert = signal<Alert | null>(null);

  constructor() { }

  showAlert(alert: Alert) {
    this.alert.set(alert);
  }

  clearAlert() {
    this.alert.set(null);
  }
}

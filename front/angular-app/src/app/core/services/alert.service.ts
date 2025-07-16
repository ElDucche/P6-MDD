import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { Alert } from '../models/alert.model';

@Injectable({
  providedIn: 'root'
})
export class AlertService {
  private alertSubject = new Subject<Alert | null>();
  alert$ = this.alertSubject.asObservable();

  constructor() { }

  showAlert(alert: Alert) {
    this.alertSubject.next(alert);
  }

  clearAlert() {
    this.alertSubject.next(null);
  }
}

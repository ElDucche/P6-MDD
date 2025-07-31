import { Component, effect, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlertService } from '../../core/services/alert.service';
import { Alert } from '../../core/models/alert.model';

@Component({
  selector: 'app-alert',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './alert.component.html',
})
export class AlertComponent {
  private alertService = inject(AlertService);
  public alert = this.alertService.alert;

  public alertClass = computed(() => {
    const alert = this.alert();
    if (!alert) {
      return '';
    }
    switch (alert.type) {
      case 'success':
        return 'alert-success';
      case 'error':
        return 'alert-error';
      case 'warning':
        return 'alert-warning';
      case 'info':
        return 'alert-info';
    }
  });

  constructor() {
    effect(() => {
      if (this.alert()) {
        const timer = setTimeout(() => this.closeAlert(), 5000);
      }
    });
  }

  closeAlert(): void {
    this.alertService.clearAlert();
  }
}

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

  private autoCloseTimer: number | null = null;

  constructor() {
    effect(() => {
      // Clear existing timer
      if (this.autoCloseTimer) {
        clearTimeout(this.autoCloseTimer);
        this.autoCloseTimer = null;
      }

      // Set new timer if alert exists
      if (this.alert()) {
        this.autoCloseTimer = window.setTimeout(() => {
          this.closeAlert();
          this.autoCloseTimer = null;
        }, 5000);
      }
    });
  }

  closeAlert(): void {
    this.alertService.clearAlert();
  }
}

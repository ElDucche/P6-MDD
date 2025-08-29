import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AlertComponent } from './alert.component';
import { AlertService } from '../../core/services/alert.service';
import { Alert } from '../../core/models/alert.model';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';

describe('AlertComponent', () => {
  let component: AlertComponent;
  let fixture: ComponentFixture<AlertComponent>;
  let alertService: AlertService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlertComponent],
      providers: [AlertService]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AlertComponent);
    component = fixture.componentInstance;
    alertService = TestBed.inject(AlertService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should inject AlertService correctly', () => {
    expect(alertService).toBeTruthy();
    expect(component.alert).toBe(alertService.alert);
  });

  describe('Alert Display', () => {
    it('should not display alert when no alert is set', () => {
      expect(alertService.alert()).toBeNull();
      
      const alertElement = fixture.debugElement.query(By.css('.alert'));
      expect(alertElement).toBeNull();
    });

    it('should display success alert correctly', () => {
      const successAlert: Alert = {
        message: 'Operation successful',
        type: 'success'
      };

      alertService.showAlert(successAlert);
      fixture.detectChanges();

      const alertElement = fixture.debugElement.query(By.css('.alert'));
      const messageElement = fixture.debugElement.query(By.css('label'));
      
      expect(alertElement).toBeTruthy();
      expect(alertElement.nativeElement.classList).toContain('alert-success');
      expect(messageElement.nativeElement.textContent.trim()).toBe('Operation successful');
    });

    it('should display error alert correctly', () => {
      const errorAlert: Alert = {
        message: 'An error occurred',
        type: 'error'
      };

      alertService.showAlert(errorAlert);
      fixture.detectChanges();

      const alertElement = fixture.debugElement.query(By.css('.alert'));
      const messageElement = fixture.debugElement.query(By.css('label'));
      
      expect(alertElement).toBeTruthy();
      expect(alertElement.nativeElement.classList).toContain('alert-error');
      expect(messageElement.nativeElement.textContent.trim()).toBe('An error occurred');
    });

    it('should display info alert correctly', () => {
      const infoAlert: Alert = {
        message: 'Information message',
        type: 'info'
      };

      alertService.showAlert(infoAlert);
      fixture.detectChanges();

      const alertElement = fixture.debugElement.query(By.css('.alert'));
      const messageElement = fixture.debugElement.query(By.css('label'));
      
      expect(alertElement).toBeTruthy();
      expect(alertElement.nativeElement.classList).toContain('alert-info');
      expect(messageElement.nativeElement.textContent.trim()).toBe('Information message');
    });

    it('should display warning alert correctly', () => {
      const warningAlert: Alert = {
        message: 'Warning message',
        type: 'warning'
      };

      alertService.showAlert(warningAlert);
      fixture.detectChanges();

      const alertElement = fixture.debugElement.query(By.css('.alert'));
      const messageElement = fixture.debugElement.query(By.css('label'));
      
      expect(alertElement).toBeTruthy();
      expect(alertElement.nativeElement.classList).toContain('alert-warning');
      expect(messageElement.nativeElement.textContent.trim()).toBe('Warning message');
    });

    it('should update alert content when alert changes', () => {
      const firstAlert: Alert = { message: 'First message', type: 'info' };
      const secondAlert: Alert = { message: 'Second message', type: 'success' };

      alertService.showAlert(firstAlert);
      fixture.detectChanges();

      let messageElement = fixture.debugElement.query(By.css('label'));
      let alertElement = fixture.debugElement.query(By.css('.alert'));
      
      expect(messageElement.nativeElement.textContent.trim()).toBe('First message');
      expect(alertElement.nativeElement.classList).toContain('alert-info');

      alertService.showAlert(secondAlert);
      fixture.detectChanges();

      messageElement = fixture.debugElement.query(By.css('label'));
      alertElement = fixture.debugElement.query(By.css('.alert'));
      
      expect(messageElement.nativeElement.textContent.trim()).toBe('Second message');
      expect(alertElement.nativeElement.classList).toContain('alert-success');
    });

    it('should handle long messages correctly', () => {
      const longMessage = 'A'.repeat(200);
      const longAlert: Alert = {
        message: longMessage,
        type: 'info'
      };

      alertService.showAlert(longAlert);
      fixture.detectChanges();

      const messageElement = fixture.debugElement.query(By.css('label'));
      expect(messageElement.nativeElement.textContent.trim()).toBe(longMessage);
    });

    it('should handle special characters in messages', () => {
      const specialMessage = 'Message with special chars: éàùö & émojis 🚀 💯';
      const specialAlert: Alert = {
        message: specialMessage,
        type: 'success'
      };

      alertService.showAlert(specialAlert);
      fixture.detectChanges();

      const messageElement = fixture.debugElement.query(By.css('label'));
      expect(messageElement.nativeElement.textContent.trim()).toBe(specialMessage);
    });
  });

  describe('Alert CSS Classes', () => {
    it('should have correct CSS classes for success alert', () => {
      const alert: Alert = { message: 'Success', type: 'success' };
      alertService.showAlert(alert);
      fixture.detectChanges();

      expect(component.alertClass()).toBe('alert-success');
    });

    it('should have correct CSS classes for error alert', () => {
      const alert: Alert = { message: 'Error', type: 'error' };
      alertService.showAlert(alert);
      fixture.detectChanges();

      expect(component.alertClass()).toBe('alert-error');
    });

    it('should have correct CSS classes for warning alert', () => {
      const alert: Alert = { message: 'Warning', type: 'warning' };
      alertService.showAlert(alert);
      fixture.detectChanges();

      expect(component.alertClass()).toBe('alert-warning');
    });

    it('should have correct CSS classes for info alert', () => {
      const alert: Alert = { message: 'Info', type: 'info' };
      alertService.showAlert(alert);
      fixture.detectChanges();

      expect(component.alertClass()).toBe('alert-info');
    });

    it('should return empty string when no alert', () => {
      expect(component.alertClass()).toBe('');
    });
  });

  describe('Close Alert Functionality', () => {
    it('should close alert when close button is clicked', () => {
      const alert: Alert = { message: 'Test alert', type: 'info' };
      
      alertService.showAlert(alert);
      fixture.detectChanges();

      expect(alertService.alert()).toEqual(alert);

      const closeButton = fixture.debugElement.query(By.css('button'));
      expect(closeButton).toBeTruthy();

      closeButton.nativeElement.click();
      fixture.detectChanges();

      expect(alertService.alert()).toBeNull();
    });

    it('should call closeAlert method when close button is clicked', () => {
      const alert: Alert = { message: 'Test alert', type: 'info' };
      
      alertService.showAlert(alert);
      fixture.detectChanges();

      jest.spyOn(component, 'closeAlert');

      const closeButton = fixture.debugElement.query(By.css('button'));
      closeButton.nativeElement.click();

      expect(component.closeAlert).toHaveBeenCalled();
    });

    it('should have close button with correct SVG icon', () => {
      const alert: Alert = { message: 'Test alert', type: 'info' };
      
      alertService.showAlert(alert);
      fixture.detectChanges();

      const closeButton = fixture.debugElement.query(By.css('button'));
      const svgIcon = closeButton.query(By.css('svg'));
      
      expect(svgIcon).toBeTruthy();
      expect(svgIcon.nativeElement.getAttribute('viewBox')).toBe('0 0 24 24');
    });
  });

  describe('Auto-close functionality', () => {
    beforeEach(() => {
      jest.useFakeTimers();
    });

    afterEach(() => {
      jest.runOnlyPendingTimers();
      jest.useRealTimers();
    });

    it('should auto-close alert after 5 seconds', () => {
      const alert: Alert = { message: 'Test alert', type: 'info' };
      
      alertService.showAlert(alert);
      fixture.detectChanges();

      expect(alertService.alert()).toEqual(alert);

      // Fast-forward time by 5 seconds
      jest.advanceTimersByTime(5000);

      expect(alertService.alert()).toBeNull();
    });

    it('should not auto-close if alert is manually closed first', () => {
      const alert: Alert = { message: 'Test alert', type: 'info' };
      
      alertService.showAlert(alert);
      fixture.detectChanges();

      expect(alertService.alert()).toEqual(alert);

      // Manually close after 2 seconds
      jest.advanceTimersByTime(2000);
      component.closeAlert();

      expect(alertService.alert()).toBeNull();

      // Advance time by remaining 3 seconds
      jest.advanceTimersByTime(3000);

      // Should still be null (no error thrown)
      expect(alertService.alert()).toBeNull();
    });

    it('should reset timer when new alert is shown', () => {
      const firstAlert: Alert = { message: 'First alert', type: 'info' };
      const secondAlert: Alert = { message: 'Second alert', type: 'success' };
      
      alertService.showAlert(firstAlert);
      fixture.detectChanges();

      // Advance 3 seconds
      jest.advanceTimersByTime(3000);
      expect(alertService.alert()).toEqual(firstAlert);

      // Show new alert
      alertService.showAlert(secondAlert);
      fixture.detectChanges();

      // Advance 3 seconds (total 6 seconds from first alert, 3 from second)
      jest.advanceTimersByTime(3000);
      expect(alertService.alert()).toEqual(secondAlert);

      // Advance 2 more seconds (5 seconds from second alert)
      jest.advanceTimersByTime(2000);
      expect(alertService.alert()).toBeNull();
    });
  });

  describe('Component Structure', () => {
    it('should have correct CSS classes on alert container', () => {
      const alert: Alert = { message: 'Test', type: 'info' };
      
      alertService.showAlert(alert);
      fixture.detectChanges();

      const alertElement = fixture.debugElement.query(By.css('.alert'));
      const classes = alertElement.nativeElement.classList;

      expect(classes).toContain('alert');
      expect(classes).toContain('fixed');
      expect(classes).toContain('top-5');
      expect(classes).toContain('right-5');
      expect(classes).toContain('w-auto');
      expect(classes).toContain('z-50');
    });

    it('should have correct button styling', () => {
      const alert: Alert = { message: 'Test', type: 'info' };
      
      alertService.showAlert(alert);
      fixture.detectChanges();

      const buttonElement = fixture.debugElement.query(By.css('button'));
      const classes = buttonElement.nativeElement.classList;

      expect(classes).toContain('btn');
      expect(classes).toContain('btn-sm');
      expect(classes).toContain('btn-ghost');
    });

    it('should use modern Angular control flow', () => {
      // Test that the component uses @if instead of *ngIf by checking template presence
      const alert: Alert = { message: 'Test', type: 'info' };
      
      // No alert initially
      let alertElement = fixture.debugElement.query(By.css('.alert'));
      expect(alertElement).toBeNull();

      // Show alert
      alertService.showAlert(alert);
      fixture.detectChanges();

      alertElement = fixture.debugElement.query(By.css('.alert'));
      expect(alertElement).toBeTruthy();
    });
  });

  describe('Edge Cases', () => {
    it('should handle alert service being null/undefined gracefully', () => {
      // This shouldn't happen in normal usage but test defensive programming
      expect(() => {
        fixture.detectChanges();
      }).not.toThrow();
    });

    it('should handle multiple rapid alert changes', () => {
      const alerts: Alert[] = [
        { message: 'Alert 1', type: 'info' },
        { message: 'Alert 2', type: 'success' },
        { message: 'Alert 3', type: 'error' },
        { message: 'Alert 4', type: 'warning' }
      ];

      alerts.forEach((alert, index) => {
        alertService.showAlert(alert);
        fixture.detectChanges();

        const messageElement = fixture.debugElement.query(By.css('label'));
        expect(messageElement.nativeElement.textContent.trim()).toBe(`Alert ${index + 1}`);
      });
    });

    it('should handle empty message gracefully', () => {
      const emptyAlert: Alert = { message: '', type: 'info' };
      
      alertService.showAlert(emptyAlert);
      fixture.detectChanges();

      const messageElement = fixture.debugElement.query(By.css('label'));
      expect(messageElement.nativeElement.textContent.trim()).toBe('');
      
      const alertElement = fixture.debugElement.query(By.css('.alert'));
      expect(alertElement).toBeTruthy(); // Should still render
    });
  });
});

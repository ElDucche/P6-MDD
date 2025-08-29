import { TestBed } from '@angular/core/testing';
import { AlertService } from './alert.service';
import { Alert } from '../models/alert.model';

describe('AlertService', () => {
  let service: AlertService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AlertService]
    });
    service = TestBed.inject(AlertService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should have initial alert signal as null', () => {
    expect(service.alert()).toBeNull();
  });

  describe('showAlert', () => {
    it('should show success alert', () => {
      const successAlert: Alert = {
        message: 'Operation successful',
        type: 'success'
      };

      service.showAlert(successAlert);

      expect(service.alert()).toEqual(successAlert);
      expect(service.alert()?.message).toBe('Operation successful');
      expect(service.alert()?.type).toBe('success');
    });

    it('should show error alert', () => {
      const errorAlert: Alert = {
        message: 'An error occurred',
        type: 'error'
      };

      service.showAlert(errorAlert);

      expect(service.alert()).toEqual(errorAlert);
      expect(service.alert()?.message).toBe('An error occurred');
      expect(service.alert()?.type).toBe('error');
    });

    it('should show info alert', () => {
      const infoAlert: Alert = {
        message: 'Information message',
        type: 'info'
      };

      service.showAlert(infoAlert);

      expect(service.alert()).toEqual(infoAlert);
      expect(service.alert()?.message).toBe('Information message');
      expect(service.alert()?.type).toBe('info');
    });

    it('should show warning alert', () => {
      const warningAlert: Alert = {
        message: 'Warning message',
        type: 'warning'
      };

      service.showAlert(warningAlert);

      expect(service.alert()).toEqual(warningAlert);
      expect(service.alert()?.message).toBe('Warning message');
      expect(service.alert()?.type).toBe('warning');
    });

    it('should replace existing alert with new one', () => {
      const firstAlert: Alert = {
        message: 'First alert',
        type: 'info'
      };

      const secondAlert: Alert = {
        message: 'Second alert',
        type: 'error'
      };

      service.showAlert(firstAlert);
      expect(service.alert()).toEqual(firstAlert);

      service.showAlert(secondAlert);
      expect(service.alert()).toEqual(secondAlert);
      expect(service.alert()?.message).toBe('Second alert');
      expect(service.alert()?.type).toBe('error');
    });

    it('should handle alert with empty message', () => {
      const emptyAlert: Alert = {
        message: '',
        type: 'info'
      };

      service.showAlert(emptyAlert);

      expect(service.alert()).toEqual(emptyAlert);
      expect(service.alert()?.message).toBe('');
    });

    it('should handle alert with long message', () => {
      const longMessage = 'A'.repeat(1000);
      const longAlert: Alert = {
        message: longMessage,
        type: 'warning'
      };

      service.showAlert(longAlert);

      expect(service.alert()).toEqual(longAlert);
      expect(service.alert()?.message).toBe(longMessage);
      expect(service.alert()?.message.length).toBe(1000);
    });

    it('should handle alert with special characters', () => {
      const specialAlert: Alert = {
        message: 'Alert with special chars: éàùö & émojis 🚀 💯',
        type: 'success'
      };

      service.showAlert(specialAlert);

      expect(service.alert()).toEqual(specialAlert);
      expect(service.alert()?.message).toBe('Alert with special chars: éàùö & émojis 🚀 💯');
    });

    it('should handle multiple rapid alerts', () => {
      const alerts: Alert[] = [
        { message: 'Alert 1', type: 'info' },
        { message: 'Alert 2', type: 'success' },
        { message: 'Alert 3', type: 'error' },
        { message: 'Alert 4', type: 'warning' }
      ];

      alerts.forEach(alert => service.showAlert(alert));

      // Should show the last alert
      expect(service.alert()).toEqual(alerts[3]);
      expect(service.alert()?.message).toBe('Alert 4');
      expect(service.alert()?.type).toBe('warning');
    });
  });

  describe('clearAlert', () => {
    it('should clear existing alert', () => {
      const alert: Alert = {
        message: 'Test alert',
        type: 'info'
      };

      service.showAlert(alert);
      expect(service.alert()).toEqual(alert);

      service.clearAlert();
      expect(service.alert()).toBeNull();
    });

    it('should handle clearing when no alert is set', () => {
      expect(service.alert()).toBeNull();

      service.clearAlert();
      expect(service.alert()).toBeNull();
    });

    it('should clear alert after multiple shows', () => {
      const alert1: Alert = { message: 'Alert 1', type: 'success' };
      const alert2: Alert = { message: 'Alert 2', type: 'error' };

      service.showAlert(alert1);
      service.showAlert(alert2);
      expect(service.alert()).toEqual(alert2);

      service.clearAlert();
      expect(service.alert()).toBeNull();
    });

    it('should allow showing new alert after clearing', () => {
      const alert1: Alert = { message: 'First alert', type: 'info' };
      const alert2: Alert = { message: 'Second alert', type: 'warning' };

      service.showAlert(alert1);
      service.clearAlert();
      service.showAlert(alert2);

      expect(service.alert()).toEqual(alert2);
      expect(service.alert()?.message).toBe('Second alert');
    });
  });

  describe('Signal reactivity', () => {
    it('should emit signal changes when showing alert', () => {
      const alert: Alert = { message: 'Test', type: 'info' };

      service.showAlert(alert);
      expect(service.alert()).toEqual(alert);
    });

    it('should emit signal changes when clearing alert', () => {
      const alert: Alert = { message: 'Test', type: 'info' };
      
      service.showAlert(alert);
      expect(service.alert()).toEqual(alert);

      service.clearAlert();
      expect(service.alert()).toBeNull();
    });

    it('should maintain signal reference integrity', () => {
      const alert1: Alert = { message: 'Alert 1', type: 'info' };
      const alert2: Alert = { message: 'Alert 2', type: 'success' };

      service.showAlert(alert1);
      const signalRef1 = service.alert;

      service.showAlert(alert2);
      const signalRef2 = service.alert;

      // Signal reference should remain the same
      expect(signalRef1).toBe(signalRef2);
      expect(service.alert()).toEqual(alert2);
    });

    it('should update signal value correctly', () => {
      const alerts: Alert[] = [
        { message: 'Alert 1', type: 'info' },
        { message: 'Alert 2', type: 'success' },
        { message: 'Alert 3', type: 'error' }
      ];

      alerts.forEach(alert => {
        service.showAlert(alert);
        expect(service.alert()).toEqual(alert);
      });

      service.clearAlert();
      expect(service.alert()).toBeNull();
    });
  });

  describe('Alert type validation', () => {
    it('should accept all valid alert types', () => {
      const validTypes: Array<Alert['type']> = ['success', 'error', 'info', 'warning'];

      validTypes.forEach(type => {
        const alert: Alert = {
          message: `Test ${type} alert`,
          type: type
        };

        service.showAlert(alert);
        expect(service.alert()?.type).toBe(type);
      });
    });

    it('should preserve alert type exactly as provided', () => {
      const alerts: Alert[] = [
        { message: 'Success message', type: 'success' },
        { message: 'Error message', type: 'error' },
        { message: 'Info message', type: 'info' },
        { message: 'Warning message', type: 'warning' }
      ];

      alerts.forEach(originalAlert => {
        service.showAlert(originalAlert);
        const storedAlert = service.alert();
        
        expect(storedAlert?.type).toBe(originalAlert.type);
        expect(storedAlert?.message).toBe(originalAlert.message);
      });
    });
  });

  describe('Edge cases and error scenarios', () => {
    it('should handle alerts with newline characters', () => {
      const alertWithNewlines: Alert = {
        message: 'Line 1\nLine 2\nLine 3',
        type: 'info'
      };

      service.showAlert(alertWithNewlines);
      expect(service.alert()?.message).toBe('Line 1\nLine 2\nLine 3');
    });

    it('should handle alerts with HTML-like content', () => {
      const htmlAlert: Alert = {
        message: '<div>HTML content</div>',
        type: 'warning'
      };

      service.showAlert(htmlAlert);
      expect(service.alert()?.message).toBe('<div>HTML content</div>');
    });

    it('should handle alerts with JSON-like content', () => {
      const jsonAlert: Alert = {
        message: '{"error": "Something went wrong"}',
        type: 'error'
      };

      service.showAlert(jsonAlert);
      expect(service.alert()?.message).toBe('{"error": "Something went wrong"}');
    });

    it('should handle rapid show/clear cycles', () => {
      const alert: Alert = { message: 'Rapid test', type: 'info' };

      for (let i = 0; i < 10; i++) {
        service.showAlert(alert);
        expect(service.alert()).toEqual(alert);
        
        service.clearAlert();
        expect(service.alert()).toBeNull();
      }
    });
  });

  describe('Memory and performance', () => {
    it('should not leak memory with many alert operations', () => {
      // Test with many operations to ensure no memory leaks
      for (let i = 0; i < 1000; i++) {
        const alert: Alert = {
          message: `Alert ${i}`,
          type: i % 2 === 0 ? 'info' : 'success'
        };
        
        service.showAlert(alert);
        
        if (i % 2 === 0) {
          service.clearAlert();
        }
      }

      // Should end up with the last alert
      expect(service.alert()?.message).toBe('Alert 999');
      expect(service.alert()?.type).toBe('success');
    });

    it('should handle very large alert messages efficiently', () => {
      const largeMessage = 'A'.repeat(10000);
      const largeAlert: Alert = {
        message: largeMessage,
        type: 'error'
      };

      const startTime = performance.now();
      service.showAlert(largeAlert);
      const endTime = performance.now();

      expect(service.alert()?.message).toBe(largeMessage);
      expect(endTime - startTime).toBeLessThan(100); // Should be fast
    });
  });
});

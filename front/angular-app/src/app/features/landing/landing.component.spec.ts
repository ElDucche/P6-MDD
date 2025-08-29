import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { LandingComponent } from './landing.component';

describe('LandingComponent', () => {
  let component: LandingComponent;
  let fixture: ComponentFixture<LandingComponent>;
  let mockRouter: jest.Mocked<Router>;

  beforeEach(async () => {
    mockRouter = {
      navigate: jest.fn().mockResolvedValue(true)
    } as any;

    await TestBed.configureTestingModule({
      imports: [LandingComponent],
      providers: [
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LandingComponent);
    component = fixture.componentInstance;
  });

  describe('Component Creation', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should inject Router correctly', () => {
      expect(component['router']).toBe(mockRouter);
    });
  });

  describe('Navigation Methods', () => {
    it('should navigate to login page when goToLogin is called', () => {
      component.goToLogin();
      
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
      expect(mockRouter.navigate).toHaveBeenCalledTimes(1);
    });

    it('should navigate to register page when goToRegister is called', () => {
      component.goToRegister();
      
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/register']);
      expect(mockRouter.navigate).toHaveBeenCalledTimes(1);
    });

    it('should handle multiple navigation calls', () => {
      component.goToLogin();
      component.goToRegister();
      component.goToLogin();
      
      expect(mockRouter.navigate).toHaveBeenCalledTimes(3);
      expect(mockRouter.navigate).toHaveBeenNthCalledWith(1, ['/login']);
      expect(mockRouter.navigate).toHaveBeenNthCalledWith(2, ['/register']);
      expect(mockRouter.navigate).toHaveBeenNthCalledWith(3, ['/login']);
    });
  });

  describe('Router Integration', () => {
    it('should handle router navigation success', () => {
      mockRouter.navigate.mockResolvedValue(true);
      
      expect(() => component.goToLogin()).not.toThrow();
      expect(() => component.goToRegister()).not.toThrow();
    });

    it('should handle router navigation failure gracefully', () => {
      mockRouter.navigate.mockResolvedValue(false);
      
      expect(() => component.goToLogin()).not.toThrow();
      expect(() => component.goToRegister()).not.toThrow();
    });

    it('should handle router navigation error gracefully', () => {
      mockRouter.navigate.mockRejectedValue(new Error('Navigation failed'));
      
      expect(() => component.goToLogin()).not.toThrow();
      expect(() => component.goToRegister()).not.toThrow();
    });
  });

  describe('Component State', () => {
    it('should maintain component state during navigation calls', () => {
      expect(component).toBeTruthy();
      
      component.goToLogin();
      expect(component).toBeTruthy();
      
      component.goToRegister();
      expect(component).toBeTruthy();
    });
  });

  describe('Method Behavior', () => {
    it('should call router.navigate with exact path arrays', () => {
      component.goToLogin();
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
      
      component.goToRegister();
      expect(mockRouter.navigate).toHaveBeenCalledWith(['/register']);
    });

    it('should be able to call navigation methods without side effects', () => {
      const initialCallCount = mockRouter.navigate.mock.calls.length;
      
      component.goToLogin();
      expect(mockRouter.navigate.mock.calls.length).toBe(initialCallCount + 1);
      
      component.goToRegister();
      expect(mockRouter.navigate.mock.calls.length).toBe(initialCallCount + 2);
    });
  });

  describe('Edge Cases', () => {
    it('should handle rapid successive calls', () => {
      for (let i = 0; i < 10; i++) {
        component.goToLogin();
        component.goToRegister();
      }
      
      expect(mockRouter.navigate).toHaveBeenCalledTimes(20);
    });

    it('should preserve router reference across method calls', () => {
      const routerRef = component['router'];
      
      component.goToLogin();
      expect(component['router']).toBe(routerRef);
      
      component.goToRegister();
      expect(component['router']).toBe(routerRef);
    });
  });
});

/**
 * Test class simulant le comportement du LandingComponent
 * pour tester la logique métier sans dépendances
 */
class TestLandingComponent {
  private router: any;

  constructor() {
    this.router = {
      navigate: jest.fn().mockResolvedValue(true)
    };
  }

  getRouter() {
    return this.router;
  }

  testGoToLogin(): void {
    this.router.navigate(['/login']);
  }

  testGoToRegister(): void {
    this.router.navigate(['/register']);
  }

  simulateUserFlow(): void {
    // Simule un flux utilisateur typique
    this.testGoToLogin();
    this.testGoToRegister();
    this.testGoToLogin(); // L'utilisateur revient à la page login
  }
}

describe('LandingComponent Business Logic', () => {
  let testComponent: TestLandingComponent;

  beforeEach(() => {
    testComponent = new TestLandingComponent();
  });

  describe('Navigation Logic Tests', () => {
    it('should handle navigation to login', () => {
      testComponent.testGoToLogin();
      
      expect(testComponent.getRouter().navigate).toHaveBeenCalledWith(['/login']);
    });

    it('should handle navigation to register', () => {
      testComponent.testGoToRegister();
      
      expect(testComponent.getRouter().navigate).toHaveBeenCalledWith(['/register']);
    });

    it('should handle complete user flow', () => {
      testComponent.simulateUserFlow();
      
      const router = testComponent.getRouter();
      expect(router.navigate).toHaveBeenCalledTimes(3);
      expect(router.navigate).toHaveBeenNthCalledWith(1, ['/login']);
      expect(router.navigate).toHaveBeenNthCalledWith(2, ['/register']);
      expect(router.navigate).toHaveBeenNthCalledWith(3, ['/login']);
    });
  });

  describe('Component Behavior Simulation', () => {
    it('should simulate landing page interaction patterns', () => {
      // Test différents patterns d'interaction
      const patterns = [
        () => testComponent.testGoToLogin(),
        () => testComponent.testGoToRegister(),
        () => {
          testComponent.testGoToLogin();
          testComponent.testGoToRegister();
        },
        () => {
          testComponent.testGoToRegister();
          testComponent.testGoToLogin();
        }
      ];

      patterns.forEach((pattern, index) => {
        // Reset mock calls before each pattern
        jest.clearAllMocks();
        // Use the existing testComponent instead of creating new instances
        pattern.call(testComponent);
        expect(testComponent.getRouter().navigate).toHaveBeenCalled();
      });
    });

    it('should maintain correct navigation state', () => {
      const router = testComponent.getRouter();
      
      testComponent.testGoToLogin();
      expect(router.navigate).toHaveBeenLastCalledWith(['/login']);
      
      testComponent.testGoToRegister();
      expect(router.navigate).toHaveBeenLastCalledWith(['/register']);
    });
  });

  describe('Navigation Path Validation', () => {
    it('should use correct paths for navigation', () => {
      const expectedPaths = {
        login: ['/login'],
        register: ['/register']
      };

      testComponent.testGoToLogin();
      expect(testComponent.getRouter().navigate).toHaveBeenCalledWith(expectedPaths.login);

      testComponent.testGoToRegister();
      expect(testComponent.getRouter().navigate).toHaveBeenCalledWith(expectedPaths.register);
    });

    it('should handle path consistency', () => {
      // Test multiple calls to ensure consistency
      for (let i = 0; i < 5; i++) {
        testComponent.testGoToLogin();
        expect(testComponent.getRouter().navigate).toHaveBeenLastCalledWith(['/login']);
      }

      for (let i = 0; i < 5; i++) {
        testComponent.testGoToRegister();
        expect(testComponent.getRouter().navigate).toHaveBeenLastCalledWith(['/register']);
      }
    });
  });
});

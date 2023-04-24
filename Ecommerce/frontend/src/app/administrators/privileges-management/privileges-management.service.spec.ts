import { TestBed } from '@angular/core/testing';

import { PrivilegesManagementService } from './privileges-management.service';

describe('PrivilegesManagementService', () => {
  let service: PrivilegesManagementService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PrivilegesManagementService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

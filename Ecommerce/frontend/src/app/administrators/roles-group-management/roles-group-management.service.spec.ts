import { TestBed } from '@angular/core/testing';

import { RolesGroupManagementService } from './roles-group-management.service';

describe('RolesGroupManagementService', () => {
  let service: RolesGroupManagementService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RolesGroupManagementService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

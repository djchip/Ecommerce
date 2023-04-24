import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailRolePrivilegesComponent } from './detail-role-privileges.component';

describe('DetailRolePrivilegesComponent', () => {
  let component: DetailRolePrivilegesComponent;
  let fixture: ComponentFixture<DetailRolePrivilegesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DetailRolePrivilegesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailRolePrivilegesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

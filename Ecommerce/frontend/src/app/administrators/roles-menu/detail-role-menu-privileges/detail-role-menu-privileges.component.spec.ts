import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailRoleMenuPrivilegesComponent } from './detail-role-menu-privileges.component';

describe('DetailRoleMenuPrivilegesComponent', () => {
  let component: DetailRoleMenuPrivilegesComponent;
  let fixture: ComponentFixture<DetailRoleMenuPrivilegesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DetailRoleMenuPrivilegesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailRoleMenuPrivilegesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

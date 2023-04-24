import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddRoleMenuPrivilegesComponent } from './add-role-menu-privileges.component';

describe('AddRoleMenuPrivilegesComponent', () => {
  let component: AddRoleMenuPrivilegesComponent;
  let fixture: ComponentFixture<AddRoleMenuPrivilegesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddRoleMenuPrivilegesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddRoleMenuPrivilegesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

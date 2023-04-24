import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditRoleMenuPrivilegesComponent } from './edit-role-menu-privileges.component';

describe('EditRoleMenuPrivilegesComponent', () => {
  let component: EditRoleMenuPrivilegesComponent;
  let fixture: ComponentFixture<EditRoleMenuPrivilegesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EditRoleMenuPrivilegesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditRoleMenuPrivilegesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

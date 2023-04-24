import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditRolePrivilegesComponent } from './edit-role-privileges.component';

describe('EditRolePrivilegesComponent', () => {
  let component: EditRolePrivilegesComponent;
  let fixture: ComponentFixture<EditRolePrivilegesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EditRolePrivilegesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditRolePrivilegesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

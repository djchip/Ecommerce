import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddRolePrivilegesComponent } from './add-role-privileges.component';

describe('AddRolePrivilegesComponent', () => {
  let component: AddRolePrivilegesComponent;
  let fixture: ComponentFixture<AddRolePrivilegesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddRolePrivilegesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddRolePrivilegesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

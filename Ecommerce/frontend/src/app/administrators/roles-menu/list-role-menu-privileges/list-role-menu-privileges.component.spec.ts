import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListRoleMenuPrivilegesComponent } from './list-role-menu-privileges.component';

describe('ListRoleMenuPrivilegesComponent', () => {
  let component: ListRoleMenuPrivilegesComponent;
  let fixture: ComponentFixture<ListRoleMenuPrivilegesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ListRoleMenuPrivilegesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ListRoleMenuPrivilegesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

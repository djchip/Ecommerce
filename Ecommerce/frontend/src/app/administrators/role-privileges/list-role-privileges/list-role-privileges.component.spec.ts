import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListRolePrivilegesComponent } from './list-role-privileges.component';

describe('ListRolePrivilegesComponent', () => {
  let component: ListRolePrivilegesComponent;
  let fixture: ComponentFixture<ListRolePrivilegesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ListRolePrivilegesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ListRolePrivilegesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

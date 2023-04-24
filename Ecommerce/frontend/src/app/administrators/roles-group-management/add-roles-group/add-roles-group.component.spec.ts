import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddRolesGroupComponent } from './add-roles-group.component';

describe('AddRolesGroupComponent', () => {
  let component: AddRolesGroupComponent;
  let fixture: ComponentFixture<AddRolesGroupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddRolesGroupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddRolesGroupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

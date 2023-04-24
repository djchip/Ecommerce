import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditRolesGroupComponent } from './edit-roles-group.component';

describe('EditRolesGroupComponent', () => {
  let component: EditRolesGroupComponent;
  let fixture: ComponentFixture<EditRolesGroupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EditRolesGroupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditRolesGroupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

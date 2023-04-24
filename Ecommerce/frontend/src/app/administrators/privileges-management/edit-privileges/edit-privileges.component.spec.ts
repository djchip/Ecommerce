import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditPrivilegesComponent } from './edit-privileges.component';

describe('EditPrivilegesComponent', () => {
  let component: EditPrivilegesComponent;
  let fixture: ComponentFixture<EditPrivilegesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EditPrivilegesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditPrivilegesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

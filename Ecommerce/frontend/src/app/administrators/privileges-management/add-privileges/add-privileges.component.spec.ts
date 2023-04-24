import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddPrivilegesComponent } from './add-privileges.component';

describe('AddPrivilegesComponent', () => {
  let component: AddPrivilegesComponent;
  let fixture: ComponentFixture<AddPrivilegesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddPrivilegesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddPrivilegesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

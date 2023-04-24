import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditAuthenticationComponent } from './edit-authentication.component';

describe('EditAuthenticationComponent', () => {
  let component: EditAuthenticationComponent;
  let fixture: ComponentFixture<EditAuthenticationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EditAuthenticationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditAuthenticationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

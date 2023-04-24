import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditExhCodeComponent } from './edit-exh-code.component';

describe('EditExhCodeComponent', () => {
  let component: EditExhCodeComponent;
  let fixture: ComponentFixture<EditExhCodeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EditExhCodeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditExhCodeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

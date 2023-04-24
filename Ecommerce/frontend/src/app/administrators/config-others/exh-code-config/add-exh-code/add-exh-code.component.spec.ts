import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddExhCodeComponent } from './add-exh-code.component';

describe('AddExhCodeComponent', () => {
  let component: AddExhCodeComponent;
  let fixture: ComponentFixture<AddExhCodeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddExhCodeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddExhCodeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

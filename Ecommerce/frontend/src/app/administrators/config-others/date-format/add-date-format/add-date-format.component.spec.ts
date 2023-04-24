import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddDateFormatComponent } from './add-date-format.component';

describe('AddDateFormatComponent', () => {
  let component: AddDateFormatComponent;
  let fixture: ComponentFixture<AddDateFormatComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddDateFormatComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddDateFormatComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditDateFormatComponent } from './edit-date-format.component';

describe('EditDateFormatComponent', () => {
  let component: EditDateFormatComponent;
  let fixture: ComponentFixture<EditDateFormatComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EditDateFormatComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditDateFormatComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

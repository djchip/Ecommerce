import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListDateFormatComponent } from './list-date-format.component';

describe('ListDateFormatComponent', () => {
  let component: ListDateFormatComponent;
  let fixture: ComponentFixture<ListDateFormatComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ListDateFormatComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ListDateFormatComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

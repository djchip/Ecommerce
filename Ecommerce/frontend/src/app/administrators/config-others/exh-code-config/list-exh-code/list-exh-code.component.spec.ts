import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListExhCodeComponent } from './list-exh-code.component';

describe('ListExhCodeComponent', () => {
  let component: ListExhCodeComponent;
  let fixture: ComponentFixture<ListExhCodeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ListExhCodeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ListExhCodeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

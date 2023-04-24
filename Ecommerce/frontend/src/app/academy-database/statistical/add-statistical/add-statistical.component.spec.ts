import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddStatisticalComponent } from './add-statistical.component';

describe('AddStatisticalComponent', () => {
  let component: AddStatisticalComponent;
  let fixture: ComponentFixture<AddStatisticalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddStatisticalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddStatisticalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

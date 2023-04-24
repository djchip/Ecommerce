import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailStatisticalComponent } from './detail-statistical.component';

describe('DetailStatisticalComponent', () => {
  let component: DetailStatisticalComponent;
  let fixture: ComponentFixture<DetailStatisticalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DetailStatisticalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailStatisticalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

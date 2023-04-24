import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailAssessmentComponent } from './detail-assessment.component';

describe('DetailAssessmentComponent', () => {
  let component: DetailAssessmentComponent;
  let fixture: ComponentFixture<DetailAssessmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DetailAssessmentComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailAssessmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

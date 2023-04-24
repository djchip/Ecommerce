import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExportStatisticalComponent } from './export-statistical.component';

describe('ExportStatisticalComponent', () => {
  let component: ExportStatisticalComponent;
  let fixture: ComponentFixture<ExportStatisticalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExportStatisticalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ExportStatisticalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CopyStatisticalComponent } from './copy-statistical.component';

describe('CopyStatisticalComponent', () => {
  let component: CopyStatisticalComponent;
  let fixture: ComponentFixture<CopyStatisticalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CopyStatisticalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CopyStatisticalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailSoftwareLogComponent } from './detail-software-log.component';

describe('DetailSoftwareLogComponent', () => {
  let component: DetailSoftwareLogComponent;
  let fixture: ComponentFixture<DetailSoftwareLogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DetailSoftwareLogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailSoftwareLogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

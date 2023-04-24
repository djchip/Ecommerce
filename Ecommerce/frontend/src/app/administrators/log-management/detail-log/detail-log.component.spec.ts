import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailLogComponent } from './detail-log.component';

describe('DetailLogComponent', () => {
  let component: DetailLogComponent;
  let fixture: ComponentFixture<DetailLogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DetailLogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailLogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

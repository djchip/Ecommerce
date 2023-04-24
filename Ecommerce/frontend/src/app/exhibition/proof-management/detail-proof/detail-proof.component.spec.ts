import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailProofComponent } from './detail-proof.component';

describe('DetailProofComponent', () => {
  let component: DetailProofComponent;
  let fixture: ComponentFixture<DetailProofComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DetailProofComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailProofComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CopyProofComponent } from './copy-proof.component';

describe('CopyProofComponent', () => {
  let component: CopyProofComponent;
  let fixture: ComponentFixture<CopyProofComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CopyProofComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CopyProofComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

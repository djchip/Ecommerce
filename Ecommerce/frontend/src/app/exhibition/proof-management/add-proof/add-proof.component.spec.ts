import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddProofComponent } from './add-proof.component';

describe('AddProofComponent', () => {
  let component: AddProofComponent;
  let fixture: ComponentFixture<AddProofComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddProofComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddProofComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

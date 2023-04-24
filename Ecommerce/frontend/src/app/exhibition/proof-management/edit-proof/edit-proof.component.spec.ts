import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditProofComponent } from './edit-proof.component';

describe('EditProofComponent', () => {
  let component: EditProofComponent;
  let fixture: ComponentFixture<EditProofComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EditProofComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditProofComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

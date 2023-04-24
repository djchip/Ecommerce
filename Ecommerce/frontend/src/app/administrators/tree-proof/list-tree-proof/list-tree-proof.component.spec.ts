import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListTreeProofComponent } from './list-tree-proof.component';

describe('ListTreeProofComponent', () => {
  let component: ListTreeProofComponent;
  let fixture: ComponentFixture<ListTreeProofComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ListTreeProofComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ListTreeProofComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

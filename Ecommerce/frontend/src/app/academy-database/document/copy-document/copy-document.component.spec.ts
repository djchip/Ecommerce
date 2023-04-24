import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CopyDocumentComponent } from './copy-document.component';

describe('CopyDocumentComponent', () => {
  let component: CopyDocumentComponent;
  let fixture: ComponentFixture<CopyDocumentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CopyDocumentComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CopyDocumentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

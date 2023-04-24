import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AutoUploadComponent } from './auto-upload.component';

describe('AutoUploadComponent', () => {
  let component: AutoUploadComponent;
  let fixture: ComponentFixture<AutoUploadComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AutoUploadComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AutoUploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

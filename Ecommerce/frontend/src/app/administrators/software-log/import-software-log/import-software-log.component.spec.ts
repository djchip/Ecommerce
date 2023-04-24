import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImportSoftwareLogComponent } from './import-software-log.component';

describe('ImportSoftwareLogComponent', () => {
  let component: ImportSoftwareLogComponent;
  let fixture: ComponentFixture<ImportSoftwareLogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ImportSoftwareLogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ImportSoftwareLogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddSoftwareLogComponent } from './add-software-log.component';

describe('AddSoftwareLogComponent', () => {
  let component: AddSoftwareLogComponent;
  let fixture: ComponentFixture<AddSoftwareLogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddSoftwareLogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddSoftwareLogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

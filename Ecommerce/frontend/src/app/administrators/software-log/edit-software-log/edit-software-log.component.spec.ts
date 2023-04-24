import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditSoftwareLogComponent } from './edit-software-log.component';

describe('EditSoftwareLogComponent', () => {
  let component: EditSoftwareLogComponent;
  let fixture: ComponentFixture<EditSoftwareLogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EditSoftwareLogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditSoftwareLogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

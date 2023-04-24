import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListSoftwareLogComponent } from './list-software-log.component';

describe('ListSoftwareLogComponent', () => {
  let component: ListSoftwareLogComponent;
  let fixture: ComponentFixture<ListSoftwareLogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ListSoftwareLogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ListSoftwareLogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

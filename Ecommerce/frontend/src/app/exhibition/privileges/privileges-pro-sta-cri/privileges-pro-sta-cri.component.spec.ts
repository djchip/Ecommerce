import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrivilegesProStaCriComponent } from './privileges-pro-sta-cri.component';

describe('PrivilegesProStaCriComponent', () => {
  let component: PrivilegesProStaCriComponent;
  let fixture: ComponentFixture<PrivilegesProStaCriComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PrivilegesProStaCriComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PrivilegesProStaCriComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailPrivilegesComponent } from './detail-privileges.component';

describe('DetailPrivilegesComponent', () => {
  let component: DetailPrivilegesComponent;
  let fixture: ComponentFixture<DetailPrivilegesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DetailPrivilegesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailPrivilegesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

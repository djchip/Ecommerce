import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailRolesGroupComponent } from './detail-roles-group.component';

describe('DetailRolesGroupComponent', () => {
  let component: DetailRolesGroupComponent;
  let fixture: ComponentFixture<DetailRolesGroupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DetailRolesGroupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailRolesGroupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

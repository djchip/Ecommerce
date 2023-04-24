import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListRolesGroupComponent } from './list-roles-group.component';

describe('ListRolesGroupComponent', () => {
  let component: ListRolesGroupComponent;
  let fixture: ComponentFixture<ListRolesGroupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ListRolesGroupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ListRolesGroupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

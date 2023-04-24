import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListPrivilegesComponent } from './list-privileges.component';

describe('ListPrivilegesComponent', () => {
  let component: ListPrivilegesComponent;
  let fixture: ComponentFixture<ListPrivilegesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ListPrivilegesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ListPrivilegesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

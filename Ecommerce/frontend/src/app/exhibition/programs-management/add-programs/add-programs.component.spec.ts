import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddProgramsComponent } from './add-programs.component';

describe('AddProgramsComponent', () => {
  let component: AddProgramsComponent;
  let fixture: ComponentFixture<AddProgramsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddProgramsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddProgramsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

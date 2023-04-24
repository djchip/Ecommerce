       import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailProgramsComponent } from './detail-programs.component';

describe('DetailProgramsComponent', () => {
  let component: DetailProgramsComponent;
  let fixture: ComponentFixture<DetailProgramsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DetailProgramsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailProgramsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

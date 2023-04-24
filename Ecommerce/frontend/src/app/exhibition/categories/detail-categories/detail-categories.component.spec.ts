import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailCategoriesComponent } from './detail-categories.component';

describe('DetailCategoriesComponent', () => {
  let component: DetailCategoriesComponent;
  let fixture: ComponentFixture<DetailCategoriesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DetailCategoriesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailCategoriesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

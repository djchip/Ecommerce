import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListConfigComponent } from './list-config.component';

describe('ListConfigComponent', () => {
  let component: ListConfigComponent;
  let fixture: ComponentFixture<ListConfigComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ListConfigComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ListConfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectAdComponent } from './select-ad.component';

describe('SelectAdComponent', () => {
  let component: SelectAdComponent;
  let fixture: ComponentFixture<SelectAdComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SelectAdComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectAdComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

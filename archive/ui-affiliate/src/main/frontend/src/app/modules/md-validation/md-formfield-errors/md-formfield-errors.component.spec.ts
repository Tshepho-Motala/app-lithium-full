import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MdFormfieldErrorsComponent } from './md-formfield-errors.component';

describe('MdFormfieldErrorsComponent', () => {
  let component: MdFormfieldErrorsComponent;
  let fixture: ComponentFixture<MdFormfieldErrorsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MdFormfieldErrorsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MdFormfieldErrorsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

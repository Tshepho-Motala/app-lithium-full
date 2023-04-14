import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FullscreenCenteredComponent } from './fullscreen-centered.component';

describe('FullscreenCenteredComponent', () => {
  let component: FullscreenCenteredComponent;
  let fixture: ComponentFixture<FullscreenCenteredComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FullscreenCenteredComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FullscreenCenteredComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

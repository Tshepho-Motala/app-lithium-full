import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EmbedAdComponent } from './embed-ad.component';

describe('EmbedAdComponent', () => {
  let component: EmbedAdComponent;
  let fixture: ComponentFixture<EmbedAdComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EmbedAdComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EmbedAdComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

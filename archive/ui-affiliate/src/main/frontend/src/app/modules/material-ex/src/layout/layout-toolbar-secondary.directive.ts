import { TemplateRef, Directive, AfterViewInit, OnDestroy} from '@angular/core';
import { LayoutComponent } from './layout.component';

@Directive({ selector: '[mtexLayoutToolbarSecondaryStart]' })
export class LayoutToolbarSecondaryDirectiveStart implements AfterViewInit, OnDestroy {

  constructor(private layout: LayoutComponent, private templateRef: TemplateRef<any>) { }

  ngAfterViewInit() {
    this.layout.setSecondaryToolbarStartTemplateRef(this.templateRef);
  }
  ngOnDestroy() {
    this.layout.setSecondaryToolbarStartTemplateRef(null);
  }
}

@Directive({ selector: '[mtexLayoutToolbarSecondaryEnd]' })
export class LayoutToolbarSecondaryDirectiveEnd implements AfterViewInit, OnDestroy {

  constructor(private layout: LayoutComponent, private templateRef: TemplateRef<any>) { }

  ngAfterViewInit() {
    this.layout.setSecondaryToolbarEndTemplateRef(this.templateRef);
  }

  ngOnDestroy() {
    this.layout.setSecondaryToolbarEndTemplateRef(null);
  }
}
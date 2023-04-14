import { TemplateRef, Directive, AfterViewInit, OnDestroy} from '@angular/core';
import { LayoutComponent } from './layout.component';

@Directive({ selector: '[mtexLayoutToolbarPrimaryStart]' })
export class LayoutToolbarPrimaryDirectiveStart implements AfterViewInit, OnDestroy {

  constructor(private layout: LayoutComponent, private templateRef: TemplateRef<any>) { }

  ngAfterViewInit() {
    this.layout.setPrimaryToolbarStartTemplateRef(this.templateRef);
  }
  ngOnDestroy() {
    this.layout.setPrimaryToolbarStartTemplateRef(null);
  }
}

@Directive({ selector: '[mtexLayoutToolbarPrimaryEnd]' })
export class LayoutToolbarPrimaryDirectiveEnd implements AfterViewInit, OnDestroy {

  constructor(private layout: LayoutComponent, private templateRef: TemplateRef<any>) { }

  ngAfterViewInit() {
    this.layout.setPrimaryToolbarEndTemplateRef(this.templateRef);
  }

  ngOnDestroy() {
    this.layout.setPrimaryToolbarEndTemplateRef(null);
  }
}
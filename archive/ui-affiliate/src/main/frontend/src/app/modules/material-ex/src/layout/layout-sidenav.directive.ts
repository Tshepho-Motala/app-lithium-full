import { TemplateRef, Directive, AfterViewInit, OnDestroy, Input } from '@angular/core';
import { LayoutComponent } from './layout.component';

@Directive({
  selector: '[mtexLayoutSidenav]'
})
export class LayoutSidenavDirective implements AfterViewInit, OnDestroy {

  constructor(private layout: LayoutComponent, private templateRef: TemplateRef<any>) { }

  @Input('mtexLayoutSidenav') name: string;

  ngAfterViewInit() {
    // console.log(this.name + ' ngAfterViewInit');
    this.layout.setSidenavContent(this.templateRef);
  }

  ngOnDestroy() {
    // console.log(this.name + ' ngOnDestroy');
    this.layout.setSidenavContent(null);
  }
}

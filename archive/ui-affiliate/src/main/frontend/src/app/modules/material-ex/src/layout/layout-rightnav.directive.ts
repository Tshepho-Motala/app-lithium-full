import { TemplateRef, Directive, AfterViewInit, OnDestroy} from '@angular/core';
import { LayoutComponent } from './layout.component';

@Directive({
  selector: '[mtexLayoutRightnav]'
})
export class LayoutRightnavDirective implements AfterViewInit, OnDestroy {

  constructor(private layout: LayoutComponent, private templateRef: TemplateRef<any>) { }

  ngAfterViewInit() {
    this.layout.setRightnavContent(this.templateRef);
  }

  ngOnDestroy() {
    this.layout.setRightnavContent(null);
  }
}
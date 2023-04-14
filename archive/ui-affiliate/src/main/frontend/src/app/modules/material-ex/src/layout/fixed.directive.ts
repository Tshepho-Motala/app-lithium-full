import { Directive, ElementRef, Renderer } from '@angular/core';

@Directive({
  selector: '[mtexFixed]'
})
export class FixedDirective {

  constructor(private renderer: Renderer, private el: ElementRef) { 
    renderer.setElementClass(el.nativeElement, "mtex-fixed", true);
  }

}

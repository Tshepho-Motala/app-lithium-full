import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { transition, style, trigger, animate, state, group } from "@angular/core";
import { LayoutComponent } from '../layout.component';
import { ActivatedRoute } from '@angular/router';

let slideUpAnimation = trigger('slideUp', [
  transition('void => *', [
    style({ opacity: 0 }),
    animate('400ms ease-in', style({ opacity: 1 }))
  ])
]);

@Component({
  selector: 'mtex-layout-page',
  templateUrl: './layout-page.component.html',
  styleUrls: ['./layout-page.component.scss'],
  host: {
    '[@slideUp]': 'true'
  },
  animations: [ slideUpAnimation ]
})
export class LayoutPageComponent implements OnInit, OnDestroy {

  constructor(
    private layout: LayoutComponent,
    public route: ActivatedRoute
  ) { }

  @Input() public fixed: boolean = false;

  ngOnInit() {
    this.layout.pushPage(this);
  }

  ngOnDestroy() {
    if (this.layout.popPage() != this) {
      throw "Page popped is not me";
    }
  }

}

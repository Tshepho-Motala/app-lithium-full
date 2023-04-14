import { Component, OnInit, Input, ContentChildren, QueryList, AfterViewInit, AfterContentInit, SkipSelf, Optional } from '@angular/core';
import { MenuComponent } from '../menu.component';
import { Router, ActivatedRoute, RouterLinkActive, RouterLink, NavigationEnd } from '@angular/router';

@Component({
  selector: 'mtex-menu-item',
  templateUrl: './menu-item.component.html',
  styleUrls: ['./menu-item.component.scss']
})
export class MenuItemComponent implements AfterViewInit, OnInit {

  @Input() title: string = "No title";
  @Input() link: string[] = null;
  @Input() icon: string = "circle";
  @Input() open: boolean = false;

  active: boolean = false;

  @ContentChildren(MenuItemComponent, {descendants: true}) subMenuItems: QueryList<MenuItemComponent>;

  constructor(private menu: MenuComponent, private router: Router, private route: ActivatedRoute, @SkipSelf() @Optional() private parent: MenuItemComponent) { }

  checkActive() {
    this.active = false;
    if (this.link != null && this.link.length > 0) {
      this.active = this.router.isActive(this.router.createUrlTree(this.link, { relativeTo: this.route }), false);
    }
    if (!this.open && this.active) this.open = true;
  }

  ngOnInit() {
    this.router.events.subscribe((val) => {
      if (val instanceof NavigationEnd) { this.checkActive()} 
    });
  }

  ngAfterContentInit() {
    this.checkActive();
  }

  ngAfterViewInit() {
  }

}

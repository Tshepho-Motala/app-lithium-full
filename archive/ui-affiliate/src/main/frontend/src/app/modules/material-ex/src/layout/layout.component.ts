import { ComponentRef, ContentChild, Component, OnInit, QueryList, ContentChildren, TemplateRef, ViewChild, ViewContainerRef, ElementRef, ComponentFactoryResolver, ReflectiveInjector, Injector } from '@angular/core';
import { ObservableMedia } from '@angular/flex-layout';
import { MdSidenav, MdToolbar } from '@angular/material';
import { Router, ActivatedRoute } from '@angular/router';
import { LayoutPageComponent } from './layout-page/layout-page.component';
import { transition, style, trigger, animate, state, group } from "@angular/core";

let fadeOutAnimation = trigger('fadeOutAnimation', [
  transition('* => void', [
    style({ opacity: 1 }),
    animate('500ms', style({ opacity: 0 }))
  ]),
]);

let rotateInAnimation = trigger('rotateInAnimation', [
  transition('void => *', [
    style({ transform: 'rotate(-180deg)', opacity: 0 }),
    animate('500ms', style({ transform: 'rotate(0deg)', opacity: 1 }))
  ]),
  transition('* => void', [
    style({ transform: 'rotate(0deg)', opacity: 1 }),
    animate('500ms', style({ transform: 'rotate(-180deg)', opacity: 0 }))
  ]),
]);

let rotateOutAnimation = trigger('rotateOutAnimation', [
  transition('void => *', [
    style({ transform: 'rotate(180deg)', opacity: 0 }),
    animate('500ms', style({ transform: 'rotate(0deg)', opacity: 1 }))
  ]),
  transition('* => void', [
    style({ transform: 'rotate(0deg)', opacity: 1 }),
    animate('500ms', style({ transform: 'rotate(180deg)', opacity: 0 }))
  ]),
]);

@Component({
  selector: 'mtex-layout',
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.scss'],
  animations: [ rotateInAnimation, rotateOutAnimation ]
})
export class LayoutComponent implements OnInit {
  
  @ViewChild('sidenav') sidenav: MdSidenav;
  @ViewChild('sidenavContent', { read: ViewContainerRef }) private sidenavContent: ViewContainerRef;

  @ViewChild('rightnav') rightnav: MdSidenav;
  @ViewChild('rightnavContent', { read: ViewContainerRef }) private rightnavContent: ViewContainerRef;

  @ViewChild('primaryToolbar') private primaryToolbar: MdToolbar;
  @ViewChild('primaryToolbarStart', { read: ViewContainerRef }) private primaryToolbarStart: ViewContainerRef;
  @ViewChild('primaryToolbarEnd', { read: ViewContainerRef }) private primaryToolbarEnd: ViewContainerRef;
  @ViewChild('primaryToolbarStartMobile', { read: ViewContainerRef }) private primaryToolbarStartMobile: ViewContainerRef;
  @ViewChild('primaryToolbarEndMobile', { read: ViewContainerRef }) private primaryToolbarEndMobile: ViewContainerRef;

  @ViewChild('secondaryToolbar') private secondaryToolbar: MdToolbar;
  @ViewChild('secondaryToolbarStart', { read: ViewContainerRef }) private secondaryToolbarStart: ViewContainerRef;
  @ViewChild('secondaryToolbarEnd', { read: ViewContainerRef }) private secondaryToolbarEnd: ViewContainerRef;

  private primaryToolbarStartTemplates: TemplateRef<any>[] = [];
  private primaryToolbarEndTemplates: TemplateRef<any>[] = [];
  private primaryToolbarStartMobileTemplates: TemplateRef<any>[] = [];
  private primaryToolbarEndMobileTemplates: TemplateRef<any>[] = [];
  private secondaryToolbarStartTemplates: TemplateRef<any>[] = [];
  private secondaryToolbarEndTemplates: TemplateRef<any>[] = [];
  private sidenavContentTemplates: TemplateRef<any>[] = [];
  private rightnavContentTemplates: TemplateRef<any>[] = [];
  private pageStack: LayoutPageComponent[] = [];

  constructor(
    public media: ObservableMedia, 
    public resolver: ComponentFactoryResolver, 
    private injector: Injector, 
    private route: ActivatedRoute,
    private router: Router) { }

  get hasSidenavContent(): boolean { return this.sidenavContentTemplates.length > 0; }
  get hasRightnavContent(): boolean { return this.rightnavContentTemplates.length > 0; }
  get hasPrimaryToolbarContent(): boolean { return this.primaryToolbarStartTemplates.length > 0 || this.primaryToolbarEndTemplates.length > 0; }
  get hasSecondaryToolbarContent(): boolean { return this.secondaryToolbarStartTemplates.length > 0 || this.secondaryToolbarEndTemplates.length > 0; }
  get hasNestedPages(): boolean { return this.pageStack.length > 1; };

  ngOnInit() {
  }

  private setTemplateRef(templateArray: TemplateRef<any>[], viewContainer: ViewContainerRef, templateRef: TemplateRef<any>) {
    if (templateRef) {
      this.pushTemplateRef(templateArray, viewContainer, templateRef);
    } else {
      this.popTemplateRef(templateArray, viewContainer);
    }
  }

  private pushTemplateRef(templateArray: TemplateRef<any>[], viewContainer: ViewContainerRef, templateRef: TemplateRef<any>) {
    viewContainer.clear();
    viewContainer.createEmbeddedView(templateRef);
    templateArray.push(templateRef);
  }

  private popTemplateRef(templateArray: TemplateRef<any>[], viewContainer: ViewContainerRef) {
    viewContainer.clear();
    if (templateArray.length > 0) templateArray.pop();
    if (templateArray.length > 0) {
      let templateRef = templateArray[templateArray.length-1];
      viewContainer.createEmbeddedView(templateRef);
    }
  }

  setPrimaryToolbarStartTemplateRef(templateRef: TemplateRef<any>) {
    this.setTemplateRef(this.primaryToolbarStartTemplates, this.primaryToolbarStart, templateRef);
    this.setTemplateRef(this.primaryToolbarStartMobileTemplates, this.primaryToolbarStartMobile, templateRef);
  }

  setPrimaryToolbarEndTemplateRef(templateRef: TemplateRef<any>) {
    this.setTemplateRef(this.primaryToolbarEndTemplates, this.primaryToolbarEnd, templateRef);
    this.setTemplateRef(this.primaryToolbarEndMobileTemplates, this.primaryToolbarEndMobile, templateRef);
  }

  setSecondaryToolbarStartTemplateRef(templateRef: TemplateRef<any>) {
    this.setTemplateRef(this.secondaryToolbarStartTemplates, this.secondaryToolbarStart, templateRef);
  }

  setSecondaryToolbarEndTemplateRef(templateRef: TemplateRef<any>) {
    this.setTemplateRef(this.secondaryToolbarEndTemplates, this.secondaryToolbarEnd, templateRef);
  }

  setSidenavContent(templateRef: TemplateRef<any>) {
    this.setTemplateRef(this.sidenavContentTemplates, this.sidenavContent, templateRef);
  }

  setRightnavContent(templateRef: TemplateRef<any>) {
    this.setTemplateRef(this.rightnavContentTemplates, this.rightnavContent, templateRef);
  }

  sidenavClose(event: Event) {
    if (this.sidenav.mode != 'over') return;
    if (
      (event.srcElement.parentElement.nodeName == 'A') ||
      (event.srcElement.nodeName == 'A')
    ) { this.sidenav.close(); }
  }

  public pushPage(page: LayoutPageComponent) {
    this.pageStack.push(page);
  }

  public popPage(): LayoutPageComponent {
    return this.pageStack.pop();
  }

  public back() {
    if (this.hasNestedPages) {
      this.router.navigate(['..'], { relativeTo: this.pageStack[this.pageStack.length-1].route });
    }
  }
  
}

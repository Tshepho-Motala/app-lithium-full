import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PagerComponent } from './pager/pager.component';
import { MaterialModule } from "@angular/material";
import { FlexLayoutModule } from "@angular/flex-layout";
import { DataTableService } from './data-table/data-table.service';
import { SearchComponent } from './search/search.component';
import { LoadingDialogComponent } from './loading-dialog/loading-dialog.component';
import { LoadingDialogService } from './loading-dialog/loading-dialog.service';
import { LoadingOverlayComponent } from './loading-overlay/loading-overlay.component';
import { FilterArchivedComponent } from './filter-archived/filter-archived.component';
import { LayoutComponent } from './layout/layout.component';
import { LayoutSidenavDirective } from './layout/layout-sidenav.directive';
import { LayoutRightnavDirective } from './layout/layout-rightnav.directive';
import { LayoutToolbarPrimaryDirectiveStart, LayoutToolbarPrimaryDirectiveEnd } from './layout/layout-toolbar-primary.directive';
import { LayoutToolbarSecondaryDirectiveStart, LayoutToolbarSecondaryDirectiveEnd } from './layout/layout-toolbar-secondary.directive';
import { MenuComponent } from './menu/menu.component';
import { MenuItemComponent } from './menu/menu-item/menu-item.component';
import { RouterModule } from '@angular/router';
import { ToggleComponent } from './toggle/toggle.component';
import { LayoutPageComponent } from './layout/layout-page/layout-page.component';
import { FixedDirective } from './layout/fixed.directive';
import { FullscreenCenteredComponent } from './fullscreen-centered/fullscreen-centered.component';

@NgModule({
  imports: [
    CommonModule,
    MaterialModule,
    FlexLayoutModule,
    RouterModule
  ],
  declarations: [
    PagerComponent,
    SearchComponent,
    LoadingDialogComponent,
    LoadingOverlayComponent,
    FilterArchivedComponent,
    LayoutComponent,
    LayoutSidenavDirective,
    LayoutRightnavDirective,
    LayoutToolbarPrimaryDirectiveStart,
    LayoutToolbarPrimaryDirectiveEnd,
    LayoutToolbarSecondaryDirectiveStart,
    LayoutToolbarSecondaryDirectiveEnd,
    LayoutPageComponent,
    MenuComponent,
    MenuItemComponent,
    ToggleComponent,
    FixedDirective,
    FullscreenCenteredComponent
  ],
  exports: [
    PagerComponent,
    SearchComponent,
    LoadingOverlayComponent,
    FilterArchivedComponent,
    LayoutComponent,
    LayoutSidenavDirective,
    LayoutRightnavDirective,
    LayoutToolbarPrimaryDirectiveStart,
    LayoutToolbarPrimaryDirectiveEnd,
    LayoutToolbarSecondaryDirectiveStart,
    LayoutToolbarSecondaryDirectiveEnd,
    LayoutPageComponent,
    MenuComponent,
    MenuItemComponent,
    FixedDirective,
    FullscreenCenteredComponent
  ],
  providers: [
    DataTableService,
    LoadingDialogService
  ],
  bootstrap: [ LoadingDialogComponent ]
})
export class MaterialExModule { }

/*
  The MaterialModule from core is being deprecated in favour of application
  specific modules that import what is needed from Material so as to
  keep the builds as small as possible.
  See https://github.com/angular/material2/releases/tag/2.0.0-beta.3

  This file was copied from https://github.com/angular/material2/blob/2.0.0-beta.3/src/lib/module.ts
  and adapted to import from '@angular/material'. This will allow
  us to comment what we don't need.
*/

//TODO comment what we don't need.

import {NgModule, ModuleWithProviders} from '@angular/core';

import {
  MdRippleModule,
  RtlModule,
  ObserveContentModule,
  PortalModule,
  OverlayModule,
  A11yModule,
  CompatibilityModule,
} from '@angular/material';

import {MdButtonToggleModule} from '@angular/material';
import {MdButtonModule} from '@angular/material';
import {MdCheckboxModule} from '@angular/material';
import {MdRadioModule} from '@angular/material';
import {MdSelectModule} from '@angular/material';
import {MdSlideToggleModule} from '@angular/material';
import {MdSliderModule} from '@angular/material';
import {MdSidenavModule} from '@angular/material';
import {MdListModule} from '@angular/material';
import {MdGridListModule} from '@angular/material';
import {MdCardModule} from '@angular/material';
import {MdChipsModule} from '@angular/material';
import {MdIconModule} from '@angular/material';
import {MdProgressSpinnerModule} from '@angular/material';
import {MdProgressBarModule} from '@angular/material';
import {MdInputModule} from '@angular/material';
import {MdSnackBarModule} from '@angular/material';
import {MdTabsModule} from '@angular/material';
import {MdToolbarModule} from '@angular/material';
import {MdTooltipModule} from '@angular/material';
import {MdMenuModule} from '@angular/material';
import {MdDialogModule} from '@angular/material';
import {PlatformModule} from '@angular/material';
import {MdAutocompleteModule} from '@angular/material';
import {StyleModule} from '@angular/material';

const MATERIAL_MODULES = [
  MdAutocompleteModule,
  MdButtonModule,
  MdButtonToggleModule,
  MdCardModule,
  MdChipsModule,
  MdCheckboxModule,
  MdDialogModule,
  MdGridListModule,
  MdIconModule,
  MdInputModule,
  MdListModule,
  MdMenuModule,
  MdProgressBarModule,
  MdProgressSpinnerModule,
  MdRadioModule,
  MdRippleModule,
  MdSelectModule,
  MdSidenavModule,
  MdSliderModule,
  MdSlideToggleModule,
  MdSnackBarModule,
  MdTabsModule,
  MdToolbarModule,
  MdTooltipModule,
  OverlayModule,
  PortalModule,
  RtlModule,
  StyleModule,
  A11yModule,
  PlatformModule,
  CompatibilityModule,
  ObserveContentModule
];

@NgModule({
  imports: [
    MdAutocompleteModule.forRoot(),
    MdButtonModule.forRoot(),
    MdCardModule.forRoot(),
    MdChipsModule.forRoot(),
    MdCheckboxModule.forRoot(),
    MdGridListModule.forRoot(),
    MdInputModule.forRoot(),
    MdListModule.forRoot(),
    MdProgressBarModule.forRoot(),
    MdProgressSpinnerModule.forRoot(),
    MdRippleModule.forRoot(),
    MdSelectModule.forRoot(),
    MdSidenavModule.forRoot(),
    MdTabsModule.forRoot(),
    MdToolbarModule.forRoot(),
    PortalModule.forRoot(),
    RtlModule.forRoot(),
    ObserveContentModule.forRoot(),

    // These modules include providers.
    A11yModule.forRoot(),
    MdButtonToggleModule.forRoot(),
    MdDialogModule.forRoot(),
    MdIconModule.forRoot(),
    MdMenuModule.forRoot(),
    MdRadioModule.forRoot(),
    MdSliderModule.forRoot(),
    MdSlideToggleModule.forRoot(),
    MdSnackBarModule.forRoot(),
    MdTooltipModule.forRoot(),
    PlatformModule.forRoot(),
    OverlayModule.forRoot(),
    CompatibilityModule.forRoot(),
  ],
  exports: MATERIAL_MODULES,
})
export class MaterialRootModule { }

@NgModule({
  imports: MATERIAL_MODULES,
  exports: MATERIAL_MODULES,
})
export class MaterialModule {
  static forRoot(): ModuleWithProviders {
    return {ngModule: MaterialRootModule};
  }
}
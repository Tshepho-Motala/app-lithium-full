import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MdFormfieldErrorsComponent } from './md-formfield-errors/md-formfield-errors.component';
import { MaterialModule } from "@angular/material";
import { FormsModule } from '@angular/forms';

@NgModule({
  imports: [
    CommonModule,
    MaterialModule,
    FormsModule
  ],
  declarations: [
    MdFormfieldErrorsComponent
  ],
  exports: [
    MdFormfieldErrorsComponent
  ]
})
export class MdValidationModule { }

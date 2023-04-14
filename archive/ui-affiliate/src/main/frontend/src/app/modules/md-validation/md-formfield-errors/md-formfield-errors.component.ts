import { Component, OnInit, Input, DoCheck } from '@angular/core';
import { NgModel } from '@angular/forms';

let nextUniqueId = 0;

@Component({
  selector: 'md-formfield-errors',
  templateUrl: './md-formfield-errors.component.html',
  styleUrls: ['./md-formfield-errors.component.scss'],
  host: {
    '[class.mat-right]': 'align == "end"',
    '[attr.id]': 'id',
  }
})
export class MdFormfieldErrorsComponent implements DoCheck {

  @Input() field: NgModel;
  @Input() align: 'start' | 'end' = 'end';
  @Input() id: string = `md-formfield-errors-${nextUniqueId++}`;

  errorMessage: string = '';

  errorLookup: { [id: string]: string; } = {
    "required": "This field is required",
    "equalTo": "The values do not match"
  }

  constructor() {
  }

  ngDoCheck() {

    if (!(this.field.errors && (Object.keys(this.field.errors).length > 0) && (this.field.touched /* || this.field.dirty */))) {
      this.errorMessage = '';
      return;
    }

    let firstKey: string = Object.keys(this.field.errors)[0];
    this.errorMessage = firstKey;

    let message = this.errorLookup[firstKey];
    if (message) {
      this.errorMessage = message;
    } else {
      this.errorMessage = "Invalid value ("+firstKey+")";
    }

  }



}

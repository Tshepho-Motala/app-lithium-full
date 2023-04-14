import { Component, OnInit, Output, Input, EventEmitter } from '@angular/core';
import { Subject } from "rxjs/Subject";
import "rxjs/add/operator/debounceTime";
import "rxjs/add/operator/distinctUntilChanged";

@Component({
  selector: 'mtex-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit {

  searchUpdated: Subject<string> = new Subject<string>();

  _value: string = "";

  @Input() get value(): string { return this._value; }
  @Output() valueChanged = new EventEmitter;
  set value(val: string) {
    console.log("set value: " + val);
    if (!val) val = '';
    this._value = val;
    this.valueChanged.emit('' + val);
  }

  constructor() { }

  ngOnInit() {
    this.searchUpdated
      .debounceTime(400).distinctUntilChanged()
      .subscribe(v => {
        this.value = v;
      });
  }

  onKeyUp(val: string) {
    this.searchUpdated.next(val);
  }

  clear() {
    this.value = "";
  }

}

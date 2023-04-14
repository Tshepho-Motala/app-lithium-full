import { Component, OnInit, Output, EventEmitter, Input } from '@angular/core';

@Component({
  selector: 'mtex-filter-archived',
  templateUrl: './filter-archived.component.html',
  styleUrls: ['./filter-archived.component.scss']
})
export class FilterArchivedComponent implements OnInit {

  _archived: boolean = false;

  @Input()
  get archived(): boolean {
    return this._archived;
  }

  @Output() archivedChange = new EventEmitter();
  set archived(val: boolean) {
    this._archived = val;
    this.archivedChange.emit(this._archived);
  }

  constructor() { }

  ngOnInit() { }

}

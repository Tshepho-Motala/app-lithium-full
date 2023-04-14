import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { PagerState } from './pager-state';

@Component({
  selector: 'mdex-pager',
  templateUrl: './pager.component.html',
  styleUrls: ['./pager.component.scss']
})
export class PagerComponent implements OnInit {

  @Output() currentPageChanged = new EventEmitter();

  pages: number[];

  _state: PagerState = new PagerState(this);

  @Input()
  get state(): PagerState { return this._state; }
  set state(val: PagerState) {
    val.pager = this;
    this._state = val;
    this.refresh();
  }

  constructor() { }

  set currentPage(value: number) {
    this._state.currentPage = value;
    this.refresh();
    this.currentPageChanged.emit(this);
  }

  get currentPage() {
    return this._state.currentPage;
  }

  ngOnInit() {
    this.refresh();
  }

  getButtonText(b: number) {
    var t: string = b.toString();
    if ((b == this._state.startPage) && (this._state.startPage != 1)) t = "...";
    if ((b == this._state.endPage) && (this._state.endPage != this._state.totalPages)) t = "...";
    return t;
  }

  refresh() {
    this.pages = [];
    for (var i = this._state.startPage; i <= this._state.endPage; i++) {
      this.pages.push(i);
    }

  }

}

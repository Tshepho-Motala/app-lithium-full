import { PagerComponent } from './pager.component';

export class PagerState {

  pager: PagerComponent;
  records: number = 0;
  recordsPerPage: number = 10;
  currentPage: number = 1;
  totalPages: number;
  startPage: number;
  endPage: number;
  lastUrl: string;

  constructor (pager?: PagerComponent) {
    this.pager = pager;
  }

  get startRecord(): number {
    return (this.currentPage - 1) * this.recordsPerPage;
  }

  get endRecord(): number {
    return 0;
  }

  public update() {
    this.totalPages = Math.ceil(this.records / this.recordsPerPage);

    if (this.currentPage > this.totalPages) this.currentPage = 1;

    this.startPage = this.currentPage - 3;
    if (this.startPage < 1) this.startPage = 1;

    this.endPage = this.startPage + 6;
    if (this.endPage > this.totalPages) this.endPage = this.totalPages;

    if (this.endPage - this.startPage < 6) {
      this.startPage = this.endPage - 6;
      if (this.startPage < 1) this.startPage = 1;
    }

    if (this.pager) this.pager.refresh();

  }
}

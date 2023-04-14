import { Component, OnInit } from '@angular/core';
import { AdsService } from '../../../services/ads.service';
import { Ad } from '../../../classes/ad';
import { Brand } from '../../../classes/brand';
import { DataTableResponse, PagerState } from 'material-ex';
import { MdCheckboxChange, MdDialogRef } from '@angular/material';

@Component({
  selector: 'app-select-ad',
  templateUrl: './select-ad.component.html',
  styleUrls: ['./select-ad.component.scss']
})
export class SelectAdComponent implements OnInit {

  ads: Ad[];
  brand: Brand;
  selected: Ad[] = [];
  pagerState: PagerState = new PagerState();

  constructor(private service: AdsService, private dialogRef: MdDialogRef<SelectAdComponent>) { 
//    this.pagerState.recordsPerPage = 2;
  }

  ngOnInit() {
    console.log(this.dialogRef.config.data);
    this.brand = this.dialogRef.config.data;
    this.load();
  }

  load() {
    this.service.list((this.brand)? this.brand.machineName: null, null, null, this.pagerState)
      .subscribe(response => {
        this.ads = response;
      }, error => {
        console.log(error);
      }, () => {
      }
    );
  }

  onSelect(ad: Ad) {
    if (!this.isSelected(ad)) {
      this.selected.push(ad);
    } else {
      this.selected = this.selected.filter((value:Ad) => (value.id != ad.id));
    }
  }

  isSelected(ad: Ad) {
    return ! this.selected.every((value:Ad) => (value.id != ad.id));
  }

  onSelectClicked() {
    this.dialogRef.close(this.selected);
  }

}

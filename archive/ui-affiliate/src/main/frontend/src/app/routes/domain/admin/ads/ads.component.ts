import { Component, OnInit, ViewChild } from '@angular/core';
import { AdsService } from '../../../../data/services/ads.service';
import { Ad } from '../../../../data/entities/ad';
import { Brand } from '../../../../data/entities/brand';
import { DataTableService, DataTableResponse, PagerState, PagerComponent } from '../../../../modules/material-ex';
import { Observable } from 'rxjs/Observable';


@Component({
  selector: 'app-ads',
  templateUrl: './ads.component.html',
  styleUrls: ['./ads.component.scss']
})
export class AdsComponent implements OnInit {

  @ViewChild(PagerComponent) pager: PagerComponent;

  isLoading: boolean = true;
  adType: string = "0";
  brand: Brand;
  searchText: string = "";

  ads: Ad[];

  constructor(private service: AdsService) { }

  ngOnInit() {
    this.load();
  }

  load() {
    this.isLoading = true;

    this.service.list(
        (this.brand)? this.brand.machineName: null,
        parseInt(this.adType), this.searchText, this.pager.state
      )
      .subscribe(response => {
        response.forEach(ad => {
          switch(ad.type) {
            case 0:
              ad.code = `<a href="${ad.targetUrl}">Link text</a>`;
              break;
            case 1:
              ad.code = `<a href="${ad.targetUrl}"><img src="${ad.entryPointUrlSystem}">Link text</a>`;
              break;
            case 2:
            ad.code = `<a href="${ad.targetUrl}"><iframe src="${ad.entryPointUrlSystem}"></iframe></a>`;
            break;
          }
        });
        this.ads = response;
      }, error => {
        console.log(error);
      }, () => {
        this.isLoading = false;
      }
    );

  }

  search(val: string) {
    this.searchText = val;
    this.load();
  }


}

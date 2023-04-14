import { Injectable } from '@angular/core';
import { Http, RequestOptions, Headers, Response, URLSearchParams } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';

import { Ad } from '../entities/ad';
import { DataTableService, DataTableResponse, PagerState } from '../../modules/material-ex';

@Injectable()
export class AdsService {

  constructor(private dataTableService: DataTableService) {}

  list(brandMachineName?: string, adType?: number, search?: string, pagerState?: PagerState): Observable<Ad[]> {

    let url: string = `api/affiliate/ads`;

    url += '?1=1';

    if (brandMachineName != undefined) url += `&brandMachineName=${brandMachineName}`;
    if (adType != undefined) url += `&adType=${adType}`;

    return this.dataTableService.get(url, search, pagerState)
    .map((response: DataTableResponse<Ad>) => {
        let ads: Ad[] = response.data;
        ads.forEach(ad => {
          switch(ad.type) {
            case 0: ad.icon = "link"; break;
            case 1: ad.icon = "image"; break;
            case 2: ad.icon = "video_library"; break;
          }
          ad.resourceUrlSystem = 'api/affiliate/ads/' + ad.id + '/resources';
          ad.entryPointUrlSystem = ad.resourceUrlSystem + '/' + ad.entryPoint;
        });
        return ads;
      });
  }
}

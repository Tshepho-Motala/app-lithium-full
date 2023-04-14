import { Injectable } from '@angular/core';
import { Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';

import { AuthHttp } from 'angular2-jwt';
import { Campaign } from '../entities/campaign';
import { Ad } from '../entities/ad';
import { CampaignAd } from '../entities/campaignAd';
import { DataTableService, DataTableResponse, PagerState } from '../../modules/material-ex';

export * from '../entities/campaign';
export * from '../entities/ad';

@Injectable()
export class CampaignsService {

  private baseUrl: string = `api/affiliate/campaigns`;

  constructor(private dataTableService: DataTableService, private http: AuthHttp) { }

  list(archived?: boolean, brandMachineName?: string, search?: string, pagerState?: PagerState): Observable<Campaign[]> {

    let url = this.baseUrl + '?1=1';

    if (brandMachineName != undefined) url += `&brandMachineName=${brandMachineName}`;
    if (archived != undefined) url += `&archived=${archived}`;

    return this.dataTableService.get(url, search, pagerState)
    .map((response: DataTableResponse<Campaign>) => {
        return response.data;
    });
  }

  create(campaign: Campaign): Observable<Campaign> {

    let url = this.baseUrl + '?1=1';
    return this.http.post(url, campaign)
    .map((response:any) => {
        return response.data;
    });

  }

  get(campaignId: number): Observable<Campaign> {
    let url = this.baseUrl + '/' + campaignId;
    return this.http.get(url).map((response: Response) => {
        return response.json().data;
    });
  }

  save(campaign: Campaign): Observable<Campaign> {
    let url = this.baseUrl + '/' + campaign.id;
    return this.http.put(url, campaign).map((response: Response) => {
        return response.json().data;
    });
  }

  addAd(campaignId: number, ad: Ad): Observable<CampaignAd> {
    let url = this.baseUrl + '/' + campaignId + '/ads';
    return this.http.post(url, ad).map((response:any) => response.data);
  }

  listAds(campaignId: number, pagerState?: PagerState): Observable<DataTableResponse<CampaignAd[]>> {
    let url = this.baseUrl + '/' + campaignId + '/ads?1=1';
    return this.dataTableService.get(url, "", pagerState);
  }

}

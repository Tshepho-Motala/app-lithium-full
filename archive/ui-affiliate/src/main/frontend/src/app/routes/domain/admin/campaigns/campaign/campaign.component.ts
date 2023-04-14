import { Component, OnInit, trigger, style, animate, transition } from '@angular/core';
import { MdDialog } from '@angular/material';
import { ActivatedRoute } from '@angular/router';
import { CampaignsService } from '../../../../../data/services/campaigns.service';
import { Campaign } from '../../../../../data/entities/campaign';
import { CampaignAd } from '../../../../../data/entities/campaignAd';
import { Ad } from '../../../../../data/entities/ad';
import { Subscription, Subject } from 'rxjs';
import { LoadingDialogService, LoadingDialog } from '../../../../../modules/material-ex';
import { DataTableService, DataTableResponse, PagerState, PagerComponent } from '../../../../../modules/material-ex';


let floatUpAnimation = trigger('floatUpAnimation', [
  transition('void => *', [
    style({ position: 'relative', opacity: 1 }),
    animate('400ms ease-in', style({ top: '0' }))
  ])
]);

let fadeAnimation = trigger('fadeAnimation', [
  transition('void => *', [
    style({ opacity: 0 }),
    animate('1400ms ease-in-out', style({ opacity: 1 }))
  ])
]);

@Component({
  selector: 'app-campaign',
  templateUrl: './campaign.component.html',
  styleUrls: ['./campaign.component.scss'],
  // host: {
  //   '[@fadeInAnimation]': 'true'
  // },
  animations: [ fadeAnimation, floatUpAnimation ]
})
export class CampaignComponent implements OnInit {

  pagerState: PagerState = new PagerState();
  params: Subscription;
  campaign: Campaign;
  loadingDialog: LoadingDialog;

  adSubject: Subject<Ad> = new Subject<Ad>();
  adSubjectQueue: Ad[];

  ads: DataTableResponse<CampaignAd[]>;

  constructor(
      public dialog: MdDialog,
      private route: ActivatedRoute,
      private service: CampaignsService,
      private loadingService: LoadingDialogService) {
    this.loadingDialog = loadingService.create({});
    this.adSubject.subscribe((ad:Ad) => {
      this.service.addAd(this.campaign.id, ad).subscribe((result: CampaignAd) => {
        if (this.adSubjectQueue.length > 0) {
          this.adSubject.next(this.adSubjectQueue.pop());
        } else {
          this.loadingDialog.dismiss();
          this.load();
        }
      });
    });
  }

  ngOnInit() {
    this.params = this.route.params.subscribe(params => {
      this.loadingDialog.present();
      this.service.get(params['campaignId']).retry(5).subscribe((campaign: Campaign) => {
        this.campaign = campaign;
        this.load();
      });
    })
  }

  load() {
    if (this.campaign)
    this.service.listAds(this.campaign.id, this.pagerState).subscribe((ads: DataTableResponse<CampaignAd[]>) => {
      this.ads = ads;
      this.loadingDialog.dismiss();
    });
  }

  ngOnDestroy() {
    this.params.unsubscribe();
  }

  selectAd() {
    // this.dialog.open(SelectAdComponent, { data: this.campaign.brand }).afterClosed().subscribe((result: Ad[]) => {
    //   if (result && result.length > 0) {
    //     this.loadingDialog.present();
    //     this.adSubjectQueue = result;
    //     this.adSubject.next(this.adSubjectQueue.pop());
    //   }
    // });
  }

  embedAd(ca: CampaignAd) {
    // this.dialog.open(EmbedAdComponent, { data: ca });
  }

  archive() {
    this.campaign.archived = true;
    this.save();
  }

  save() {
    this.loadingDialog.present();
    this.service.save(this.campaign).subscribe((result: Campaign) => {
      this.loadingDialog.dismiss();
    });
  }


}

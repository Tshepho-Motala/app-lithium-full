import { Component, OnInit, ViewChild } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { CampaignAddComponent } from '../campaign-add/campaign-add.component';
import { MdDialog, MdDialogRef } from '@angular/material';
import { CampaignsService } from '../../services/campaigns.service';
import { Campaign } from '../../classes/campaign';
import { Brand } from '../../classes/brand';
import { PagerComponent, LoadingDialogService, LoadingDialog } from 'material-ex';

@Component({
  selector: 'app-campaigns',
  templateUrl: './campaigns.component.html',
  styleUrls: ['./campaigns.component.scss']
})
export class CampaignsComponent implements OnInit {

  @ViewChild(PagerComponent) pager: PagerComponent;

  campaigns: Campaign[] = [];
  isLoading: boolean = true;
  brand: Brand;
  searchText: string = "";
  archived: boolean = false;
  filtered: boolean = false;

  constructor(public dialog: MdDialog, public service: CampaignsService, public loadingDialogService: LoadingDialogService, 
    public router: Router, public route: ActivatedRoute) { }

  ngOnInit() {
    this.load();
  }

  load() {

    this.filtered = false;
    if (this.searchText.length > 0) this.filtered = true;
    if (this.archived) this.filtered = true;

    this.isLoading = true;

    this.service.list(this.archived,
        (this.brand)? this.brand.machineName: null,
        this.searchText, this.pager.state
      )
      .subscribe(response => {
        this.campaigns = response
        this.isLoading = false;
      }
    );
  }

  add() {
    let dialogRef = this.dialog.open(CampaignAddComponent);
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.service.create(result).subscribe(campaign => {
          this.load();
        });
      }
    });
  }

  open(campaign: Campaign) {
    this.router.navigate([ '..' , 'campaign', campaign.id], {relativeTo: this.route});
  }

  search(event: string) {
    this.searchText = event;
    this.load();
  }

}

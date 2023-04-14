import { Component, OnInit, ViewChild } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { transition, style, trigger, animate, state, group } from "@angular/core";
import { CampaignsService } from '../../../../data/services/campaigns.service';
import { Campaign } from '../../../../data/entities/campaign';
import { Brand } from '../../../../data/entities/brand';
import { PagerComponent, LoadingDialogService, LoadingDialog } from '../../../../modules/material-ex';
import { MdDialog, MdDialogRef } from '@angular/material';
import { CampaignAddComponent } from './campaign-add/campaign-add.component';
import { MdSnackBar } from '@angular/material';

let fadeAnimation = trigger('fadeAnimation', [
  transition('void => *', [
    style({ opacity: 0 }),
    animate('1000ms ease-in-out', style({ opacity: 1 }))
  ])
]);

let openAnimation = trigger('openAnimation', [
  transition('void => *', [
    style({ opacity: 0, position: 'absolute', top: 0 }),
    animate('1000ms ease-in-out', style({ opacity: 1 }))
  ]),
  transition('* => void', [
    animate('1000ms ease-in-out', style({ opacity: 0 }))
  ])
]);


@Component({
  selector: 'app-campaigns',
  templateUrl: './campaigns.component.html',
  styleUrls: ['./campaigns.component.scss'],
  // host: {
  //   '[@fadeInAnimation]': 'true'
  // },
   animations: [ fadeAnimation, openAnimation ]
})
export class CampaignsComponent implements OnInit {

  @ViewChild(PagerComponent) pager: PagerComponent;

  campaigns: Campaign[] = [];
  isLoading: boolean = true;
  brand: Brand;
  searchText: string = "";
  archived: boolean = false;
  filtered: boolean = false;

  constructor(
    public dialog: MdDialog,
    public service: CampaignsService,
    public loadingDialogService: LoadingDialogService,
    public router: Router,
    public snackBar: MdSnackBar,
    public route: ActivatedRoute) { }

  ngOnInit() {
    this.pager.state.recordsPerPage = 2;
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
        console.log("Got a result from the dialog: ", result);
        this.isLoading = true;
        this.service.create(result)
          .catch(err => {
            this.isLoading = false;
            this.snackBar.open('Campaign could not be added.', "Dismiss", { duration: 10000 });
            console.error(err);
            throw(err);
          })
          .subscribe(campaign => {
          this.load();
          this.snackBar.open('Campaign added', "Dismiss", { duration: 4000 });
        });
      }
    });
  }

  open(id: number) {
    this.router.navigate([ id ], {relativeTo: this.route});
  }

  search(event: string) {
    this.searchText = event;
    this.load();
  }

}

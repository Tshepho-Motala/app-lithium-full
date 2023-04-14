import { Component, OnInit } from '@angular/core';
import { MdDialog, MdDialogRef } from '@angular/material';
import { Campaign } from '../../../../../data/entities/campaign';
import { Brand } from '../../../../../data/entities/brand';

@Component({
  selector: 'app-campaign-add',
  templateUrl: './campaign-add.component.html',
  styleUrls: ['./campaign-add.component.scss']
})
export class CampaignAddComponent implements OnInit {

  private model: Campaign;
  private brand: Brand;

  constructor(public dialogRef: MdDialogRef<Campaign>) { }

  ngOnInit() {
    this.model = new Campaign(-2);
  }

  ok() {
    this.model.brand = this.brand;
    this.dialogRef.close(this.model);
  }

}

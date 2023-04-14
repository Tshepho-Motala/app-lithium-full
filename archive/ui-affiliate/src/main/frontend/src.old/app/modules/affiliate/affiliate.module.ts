import { NgModule } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { MaterialModule } from "@angular/material";
import { FlexLayoutModule } from "@angular/flex-layout";
import { SidenavService } from "../../core/sidenav/sidenav.service";
import { BrandsComponent } from './pages/brands/brands.component';
import { AdsComponent } from './pages/ads/ads.component';
import { RevenueComponent } from './pages/revenue/revenue.component';
import { CampaignsComponent } from './pages/campaigns/campaigns.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { BrandsService } from './services/brands.service';
import { AdsService } from './services/ads.service';
import { CampaignsService } from './services/campaigns.service';
import { ProfileService } from './services/profile.service';
import { FormsModule } from '@angular/forms';
import { FilterBrandComponent } from './components/filter-brand/filter-brand.component';
import { SelectBrandComponent } from './components/select-brand/select-brand.component';
import { MaterialExModule } from 'material-ex';
import { CampaignAddComponent } from './pages/campaign-add/campaign-add.component';
import { MdValidationModule } from '../md-validation';
import { CustomFormsModule } from 'ng2-validation';
import { CampaignComponent } from './pages/campaign/campaign.component';
import { RouterModule } from '@angular/router';
import { SelectAdComponent } from './pages/campaign/select-ad/select-ad.component';
import { PerfectScrollbarModule, PerfectScrollbarConfigInterface } from "ngx-perfect-scrollbar";
import { EmbedAdComponent } from './pages/campaign/embed-ad/embed-ad.component';
import { HighlightModule } from "../../core/highlightjs/highlight.module";
import { ProfilePasswordComponent } from './pages/profile-password/profile.password.component';

@NgModule({
  imports: [
    CommonModule,
    MaterialModule,
    FlexLayoutModule,
    FormsModule,
    MaterialExModule,
    MdValidationModule,
    RouterModule,
    PerfectScrollbarModule,
    CustomFormsModule,
    HighlightModule
  ],
  declarations: [
    BrandsComponent, 
    AdsComponent, 
    RevenueComponent, 
    CampaignsComponent, 
    FilterBrandComponent, 
    SelectBrandComponent,
    CampaignAddComponent, 
    CampaignComponent, 
    SelectAdComponent, 
    EmbedAdComponent,
    ProfileComponent, 
    ProfilePasswordComponent
  ],
  providers: [
    BrandsService, 
    AdsService, 
    CampaignsService,
    ProfileService,
	DatePipe
  ],
  entryComponents: [
    CampaignAddComponent, 
    SelectAdComponent, 
    EmbedAdComponent,
    ProfilePasswordComponent
  ]
})
export class AffiliateModule {
  constructor (sidenavService: SidenavService) {
    sidenavService.addItem("Brands", "attach_money", "affiliate/brands", 20);
    sidenavService.addItem("Revenue", "attach_money", "affiliate/revenue", 20);
    sidenavService.addItem("Ads", "image", "affiliate/ads", 21);
    sidenavService.addItem("Campaigns", "poll", "affiliate/campaigns", 21);
    sidenavService.addItem("Accounting", "history", "affiliate/accounting", 22);
  }
}

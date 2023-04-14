import { BrandsComponent } from './pages/brands/brands.component';
import { AdsComponent } from './pages/ads/ads.component';
import { CampaignsComponent } from './pages/campaigns/campaigns.component';
import { CampaignComponent } from './pages/campaign/campaign.component';
import { ProfileComponent } from './pages/profile/profile.component';

export const AFFILIATE_ROUTES = [
  { path: 'affiliate/brands', component: BrandsComponent },
  { path: 'affiliate/ads', component: AdsComponent },
  { path: 'affiliate/campaigns', component: CampaignsComponent },
  { path: 'affiliate/campaign/:campaignId', component: CampaignComponent },
  { path: 'affiliate/profile', component: ProfileComponent }
];

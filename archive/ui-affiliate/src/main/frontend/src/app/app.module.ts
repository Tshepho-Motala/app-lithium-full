/* External Dependencies */

import { NgModule, Type } from '@angular/core';
import { BrowserModule, Title }  from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule } from '@angular/forms';
import { Http, RequestOptions } from '@angular/http';
import { FlexLayoutModule } from '@angular/flex-layout';
import { CovalentCommonModule } from '@covalent/core';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { CustomFormsModule } from 'ng2-validation';
import { AuthHttp, AuthConfig } from 'angular2-jwt';

/* Internal Modules */

import { MaterialModule } from './modules/material';
import { MaterialExModule } from './modules/material-ex';
import { MdValidationModule } from './modules/md-validation';

/* Main App and Routes */

import { AppComponent } from './app.component';
import { appRoutes, appRoutingProviders } from './app.routes';

/* App Route Components */

import { DomainComponent } from './routes/domain/domain.component';
import { LoginComponent } from './routes/domain/login/login.component';
import { ForgotPasswordComponent } from './routes/domain/forgot-password/forgot-password.component';
import { ForgotPasswordResetComponent } from './routes/domain/forgot-password-reset/forgot-password-reset.component';
import { RegisterComponent } from './routes/domain/register/register.component';
import { RegisterSuccessComponent } from './routes/domain/register-success/register-success.component';
import { TermsComponent } from './routes/domain/terms/terms.component';
import { AdminComponent } from './routes/domain/admin/admin.component';
import { DashboardComponent } from './routes/domain/admin/dashboard/dashboard.component';
import { NotFoundComponent } from './routes/not-found/not-found.component';
import { CampaignsComponent } from './routes/domain/admin/campaigns/campaigns.component';
import { CampaignComponent } from './routes/domain/admin/campaigns/campaign/campaign.component';
import { CampaignAddComponent } from './routes/domain/admin/campaigns/campaign-add/campaign-add.component';
import { LinkadsComponent } from './routes/domain/admin/campaigns/campaign/linkads/linkads.component';
import { SettingsComponent } from './routes/domain/admin/settings/settings.component';

/* App Utility Components */

import { FilterBrandComponent } from './components/filter-brand/filter-brand.component';
import { SelectBrandComponent } from './components/select-brand/select-brand.component';

/* App Services and Providers */

import { DomainGuard } from './services/domain.guard';
import { GeoService } from './services/geo.service';
import { ForgotPasswordService } from './services/forgot-password.service';
import { RegisterService } from './services/register.service';
import { AuthService } from './services/auth.service';
import { AuthGuard } from './services/auth.guard';
import { UniqueUsernameValidator } from './validators/unique-username';
import { UniqueEmailValidator } from './validators/unique-email';

/* Data Services and Providers */

import { CampaignsService } from './data/services/campaigns.service';
import { DomainsService } from './data/services/domains.service';
import { BrandsService } from './data/services/brands.service';

export function authHttpServiceFactory(http: Http, options: RequestOptions) {
	return new AuthHttp(new AuthConfig({
			tokenName: 'access_token',
			tokenGetter: (() => localStorage.getItem('access_token')),
			globalHeaders: [{ 'Content-Type':'application/json' }]
		}), http, options);
}


@NgModule({
  declarations: [
    AppComponent,
    DomainComponent,
    LoginComponent,
    ForgotPasswordComponent,
    ForgotPasswordResetComponent,
    RegisterComponent,
    RegisterSuccessComponent,
    TermsComponent,
    AdminComponent,
    DashboardComponent,
    NotFoundComponent,
    CampaignsComponent,
    CampaignAddComponent,
    CampaignComponent,
    LinkadsComponent,
    SettingsComponent,
    FilterBrandComponent,
    SelectBrandComponent,
    UniqueUsernameValidator,
    UniqueEmailValidator
  ],
  imports: [
    MdValidationModule,
    CustomFormsModule,
    FormsModule,
    FlexLayoutModule,
    MaterialModule.forRoot(),
    BrowserModule,
    BrowserAnimationsModule,
    MaterialExModule,
    CovalentCommonModule,
    NgxChartsModule,
    appRoutes
  ],
  providers: [
    appRoutingProviders,

    /* Guards */

    AuthGuard,
    DomainGuard,

    /* Data Services */

    CampaignsService,
    DomainsService,
    BrandsService,

    /* Utility Services */

    Title,
    GeoService,
    ForgotPasswordService,
    AuthService,
    {
      provide: AuthHttp,
      useFactory: authHttpServiceFactory,
      deps: [Http, RequestOptions]
    },

  ],
  entryComponents: [
    CampaignAddComponent,
    TermsComponent
  ],
  bootstrap: [ AppComponent ],
})
export class AppModule {}

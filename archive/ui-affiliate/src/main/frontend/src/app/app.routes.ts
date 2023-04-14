import { Routes, RouterModule } from '@angular/router';

import { NotFoundComponent } from './routes/not-found/not-found.component';
import { DomainComponent } from './routes/domain/domain.component';
import { LoginComponent } from './routes/domain/login/login.component';
import { ForgotPasswordComponent } from './routes/domain/forgot-password/forgot-password.component';
import { ForgotPasswordResetComponent } from './routes/domain/forgot-password-reset/forgot-password-reset.component';
import { RegisterComponent } from './routes/domain/register/register.component';
import { RegisterSuccessComponent } from './routes/domain/register-success/register-success.component';
import { AdminComponent } from './routes/domain/admin/admin.component';
import { DashboardComponent } from './routes/domain/admin/dashboard/dashboard.component';
import { CampaignsComponent } from './routes/domain/admin/campaigns/campaigns.component';
import { CampaignComponent } from './routes/domain/admin/campaigns/campaign/campaign.component';
import { LinkadsComponent } from './routes/domain/admin/campaigns/campaign/linkads/linkads.component';
import { DomainGuard } from './services/domain.guard';
import { AuthGuard } from './services/auth.guard';

const routes: Routes = [
  { path: ':domain', component: DomainComponent, canActivate: [ DomainGuard ], children: [
    { path: '', redirectTo: 'login', pathMatch: 'full' },
    { path: 'login', component: LoginComponent },
    { path: 'forgot-password', component: ForgotPasswordComponent },
    { path: 'forgot-password-reset', component: ForgotPasswordResetComponent },
    { path: 'register', component: RegisterComponent },
    { path: 'register-success', component: RegisterSuccessComponent },
    { path: 'admin', component: AdminComponent, canActivate: [ AuthGuard ], children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'campaigns', component: CampaignsComponent, children: [
        { path: ':campaignId', component: CampaignComponent, children: [
          { path: 'linkads', component: LinkadsComponent }
        ] },
      ]}
    ] }
  ] },
  { path: '**', component: NotFoundComponent }
];

export const appRoutingProviders: any[] = [

];

export const appRoutes: any = RouterModule.forRoot(routes, { useHash: false });

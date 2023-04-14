import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Http, HttpModule, RequestOptions } from '@angular/http';
import { MaterialModule, MdIconRegistry } from "@angular/material";
import { FlexLayoutModule } from "@angular/flex-layout";
import { RouterModule } from '@angular/router';

import { CustomFormsModule } from 'ng2-validation';

import { AuthHttp, AuthConfig } from 'angular2-jwt';

import { AuthRoutingModule } from './auth-routing.module';
import { AuthGuard } from './auth.guard';
import { AuthService } from './auth.service';
import { AuthHttpService } from './auth-http.service';

import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { ForgotPasswordComponent } from './pages/forgot-password/forgot-password.component';
import { ForgotPasswordResetComponent } from './pages/forgot-password-reset/forgot-password-reset.component'; 

import { TermsComponent } from './pages/terms/terms.component';
import { MdValidationModule } from '../md-validation';
import { MaterialExModule } from 'material-ex';

export function authHttpServiceFactory(http: Http, options: RequestOptions) {
	return new AuthHttp(new AuthConfig({
			tokenName: 'access_token',
			tokenGetter: (() => localStorage.getItem('access_token')),
			globalHeaders: [{ 'Content-Type':'application/json' }]
		}), http, options);
}

@NgModule({
	imports: [
		MaterialExModule,
		CommonModule,
		AuthRoutingModule,
		FormsModule,
		CustomFormsModule,
		MaterialModule,
		HttpModule,
		FlexLayoutModule,
		RouterModule.forChild([]),
		MdValidationModule
	],
	declarations: [
		LoginComponent,
		RegisterComponent,
		ForgotPasswordComponent,
		ForgotPasswordResetComponent,
		TermsComponent
	],
	providers: [
		AuthService,
		AuthGuard,
		{
			provide: AuthHttp,
			useFactory: authHttpServiceFactory,
			deps: [Http, RequestOptions]
		},
		AuthHttpService
	],
	entryComponents: [TermsComponent]
})
export class AuthModule {
	constructor() {
	}
}
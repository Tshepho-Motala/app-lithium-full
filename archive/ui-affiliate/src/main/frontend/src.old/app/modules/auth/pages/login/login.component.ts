import { Component, OnInit } from '@angular/core';
import { Router } from "@angular/router";
import { ActivatedRoute } from "@angular/router";
import { fadeInAnimation } from "../../route.animation";

import { AuthService } from "../../auth.service";
import { DomainService } from "../../../../shared/domain/domain.service";


@Component({
	selector: 'ms-login',
	templateUrl: './login.component.html',
	styleUrls: ['./login.component.scss'],
	host: {
		'[@fadeInAnimation]': 'true'
	},
	animations: [fadeInAnimation]
})
export class LoginComponent implements OnInit {
	isLoading: boolean = false;
	errorMessage: string;
	
	domain: string;
	username: string;
	password: string;
	returnUrl: string;
	
	constructor(
		private router: Router,
		private route: ActivatedRoute,
		private authService: AuthService,
		private domainService: DomainService
	) { }
	
	ngOnInit() {
		this.domain = this.domainService.domain;
	}
	
	login() {
		this.errorMessage = '';
		this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/' + this.domain + '/';
		this.isLoading = true;
		this.authService.login(this.domain + '/' + this.username, this.password)
			.subscribe(
				data => {
					this.authService.setupAuth(data);
					this.router.navigate([this.returnUrl]);
				},
				error => {
					setTimeout(() => {
						this.errorMessage = error;
						this.isLoading = false;
					}, 5000);
				}
			);
	}
}
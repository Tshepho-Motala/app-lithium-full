import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from "@angular/router";
import { fadeInAnimation } from "../../route.animation";
import { ForgotPasswordService } from "../../../services/forgot-password.service";
import { MdSnackBar } from '@angular/material';
import { DomainGuard } from '../../../services/domain.guard';

@Component({
	selector: 'ms-forgot-password',
	templateUrl: './forgot-password.component.html',
	styleUrls: ['./forgot-password.component.scss'],
	host: {
		'[@fadeInAnimation]': 'true'
	},
	animations: [fadeInAnimation],
	providers: [ForgotPasswordService]
})
export class ForgotPasswordComponent implements OnInit {
	isLoading: boolean = false;
	errorMessage: string;
	
	usernameOrEmail: string;

	constructor(
    	private domainGuard: DomainGuard,
		private router: Router,
		private route: ActivatedRoute,
		private forgotPasswordService: ForgotPasswordService,
		private mdSnackBar: MdSnackBar
	) { }

	ngOnInit() {
	}

	step1() {
		this.isLoading = true;
		this.forgotPasswordService.step1(this.usernameOrEmail).subscribe(
			data => {
				console.log(data);
				if (!data.successful) {
					this.errorMessage = 'There was a problem trying to reset your password. Please check your username or e-mail address and try again';
				} else {
					this.errorMessage = null;
					this.mdSnackBar.open('An e-mail has been sent to you containing instructions on how to proceed further', 'Close', { duration: 5000 });
					this.router.navigate(['/', this.domainGuard.domainMachineName, 'login']);
				}
				this.isLoading = false;
			},
			error => {
				console.error('Problem resetting password (step1)!');
				this.errorMessage = error;
				this.isLoading = false;
			}
		);
	}
}

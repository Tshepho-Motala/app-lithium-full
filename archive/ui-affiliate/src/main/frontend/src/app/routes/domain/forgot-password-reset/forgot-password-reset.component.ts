import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { fadeInAnimation } from '../../route.animation';
import { ForgotPasswordService } from "../../../services/forgot-password.service";
import { MdSnackBar } from '@angular/material';
import { DomainGuard } from '../../../services/domain.guard';

@Component({
	selector: 'ms-forgot-password-reset',
	templateUrl: './forgot-password-reset.component.html',
	styleUrls: ['./forgot-password-reset.component.scss'],
	host: {
		'[@fadeInAnimation]': 'true'
	},
	animations: [fadeInAnimation],
	providers: [ForgotPasswordService]
})
export class ForgotPasswordResetComponent implements OnInit {
	isLoading: boolean = false;
	errorMessage: string;

	usernameOrEmail: string;
	passwordResetToken: string;
	password: string;

	constructor(
    private domainGuard: DomainGuard,
		private router: Router,
		private route: ActivatedRoute,
		private forgotPasswordService: ForgotPasswordService,
		private mdSnackBar: MdSnackBar
	) { }

	ngOnInit() {
		this.route.queryParams.subscribe(params => {
			this.usernameOrEmail = params['user'];
			this.passwordResetToken = params['token'];
		});
	}

	step2() {
		this.forgotPasswordService.step2(this.usernameOrEmail, this.password, this.passwordResetToken).subscribe(
			data => {
				console.log(data);
				if (!data.successful) {
					this.errorMessage = 'Your password could not be reset. The token is no longer valid.';
				} else {
					this.errorMessage = null;
					this.mdSnackBar.open('Your password has been successfully reset', 'Close', { duration: 5000 });
					this.router.navigate(['/', this.domainGuard.domainMachineName, 'login']);
				}
			},
			error => {
				console.error('Problem resetting password (step2)!');
				this.errorMessage = error;
			}
		);
	}
}

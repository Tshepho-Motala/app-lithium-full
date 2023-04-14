import { Component } from '@angular/core';
import { MdDialog, MdDialogRef } from '@angular/material';
import { ProfileService } from '../../../../data/services/profile.service';
import { NewPassword } from '../../../../data/entities/newPassword';

@Component({
	selector: 'profile-password-dialog',
	templateUrl: './profile.password.component.html',
	styleUrls: ['./profile.password.component.scss']
})
export class ProfilePasswordComponent {
	errorMessage: string;
	model: NewPassword = new NewPassword();

	constructor(public dialogRef: MdDialogRef<ProfilePasswordComponent>, public profileService: ProfileService) {
	}

	save() {
		this.errorMessage = null;
		this.profileService.changePassword(this.model).subscribe(
			data => {
				this.dialogRef.close(data);
			},
			error => {
				this.errorMessage = error;
				console.error(error);
			}
		);
	}

	close() {
		this.dialogRef.close();
	}
}

import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { DatePipe } from '@angular/common';
import { AuthService } from '../../../../services/auth.service';
import { ProfileService } from '../../../../data/services/profile.service';
import { User } from '../../../../data/entities/user';
import { Address } from '../../../../data/entities/address';
import { MdDialog, MdDialogRef, MdSnackBar } from '@angular/material';
import { ProfilePasswordComponent } from '../profile-password/profile.password.component';
import { NgForm, NgModel } from '@angular/forms';
import { Observable } from 'rxjs/Observable';
import { GeoService } from '../../../../services/geo.service';

@Component({
	selector: 'app-profile',
	templateUrl: './profile.component.html',
	styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
	@ViewChild('residentialAddressCity') residentialAddressCity: NgModel;
	@ViewChild('postalAddressCity') postalAddressCity: NgModel;

	isLoading: boolean = false;
	model: User = new User();

	raFilteredCities: Observable<any>;
	paFilteredCities: Observable<any>;

	constructor (
		public auth: AuthService,
		public profileService: ProfileService,
		public dialog: MdDialog,
		public datePipe: DatePipe,
		public mdSnackBar: MdSnackBar,
		public geoService: GeoService
	) { }

	ngOnInit() {
		this.load();
		this.raFilterCities();
		this.paFilterCities();
	}

	ngAfterViewInit(): void {
		this.raFilterCities();
		this.paFilterCities();
	}

	raFilterCities() {
		this.residentialAddressCity.valueChanges
			.subscribe(
				val => {
					if (typeof val === 'string') {
						this.raFilteredCities = this.geoService.filteredCities(val);
					}
				}
			);
	}

	raDisplayFn(city: any): string {
		if (typeof city === 'string') return city;
		if (city) {
			this.model.residentialAddress.city = city.name;
			this.model.residentialAddress.country = city.country;
		}
		return city ? city.name : '';
	}

	onRaCityBlur() {
		if (typeof this.model.residentialAddress.cityObject != 'object') {
			this.model.residentialAddress.cityObject = null;
			this.model.residentialAddress.city = null;
			this.model.residentialAddress.country = null;
		}
	}

	paFilterCities() {
		this.postalAddressCity.valueChanges
			.subscribe(
				val => {
					if (typeof val === 'string') {
						this.paFilteredCities = this.geoService.filteredCities(val);
					}
				}
			);
	}

	paDisplayFn(city: any): string {
		if (typeof city === 'string') return city;
		if (city) {
			this.model.postalAddress.city = city.name;
			this.model.postalAddress.country = city.country;
		}
		return city ? city.name : '';
	}

	onPaCityBlur() {
		if (typeof this.model.postalAddress.cityObject != 'object') {
			this.model.postalAddress.cityObject = null;
			this.model.postalAddress.city = null;
			this.model.postalAddress.country = null;
		}
	}

	load() {
		this.isLoading = true;
		this.profileService.getUser().subscribe(
			data =>	{
				console.log(data);
				let companyName = '';
				let websiteUrl = '';
				let paymentDetails = '';
				if (data.labels != null) {
					for (let i = 0; i < data.labels.length; i++) {
						let label = data.labels[i];
						if (label.label.name === 'companyName') {
							companyName = label.value;
							continue;
						} else if (label.label.name === 'websiteUrl') {
							websiteUrl = label.value;
							continue;
						} else if (label.label.name === 'paymentDetails') {
							paymentDetails = label.value;
							continue;
						}
					}
				}
				this.model.setUser(
					data.id,
					data.username,
					data.email,
					data.firstName,
					data.lastName,
					data.telephoneNumber,
					data.cellphoneNumber,
					data.residentialAddress != null? data.residentialAddress:new Address(),
					data.postalAddress != null? data.postalAddress:new Address(),
					data.socialSecurityNumber,
					data.dateOfBirth,
					(data.passwordUpdated != null)? this.datePipe.transform(new Date(data.passwordUpdated), 'EEE, dd MMM yyyy HH:mm:ss z'):'Never',
					(data.passwordUpdatedBy != null)? data.passwordUpdatedBy:'N/A',
					companyName,
					websiteUrl,
					paymentDetails,
					data.dobYear,
					data.dobMonth,
					data.dobDay
				);
				this.isLoading = false;
			},
			error => {
				console.log('Problem getting user!');
			}
		);
	}

	refresh() {
		this.load();
	}

	changePassword() {
		let dialogRef = this.dialog.open(ProfilePasswordComponent);
		dialogRef.afterClosed().subscribe(result => {
			if (result != undefined) {
				this.refresh();
				this.mdSnackBar.open('Profile saved successfully!', 'Close', { duration: 3000 });
			}
		});
	}

	save(form: NgForm) {
		if (!form.valid) return;
		this.isLoading = true;
		if (this.model.residentialAddress != null) {
			if (!this.profileService.validateAddress(this.model.residentialAddress)) {
				this.isLoading = false;
				this.mdSnackBar.open('Residential address validation failed!', 'Close', { duration: 3000 });
				return;
			}
		}
		if (this.model.postalAddress != null) {
			if (!this.profileService.validateAddress(this.model.postalAddress)) {
				this.isLoading = false;
				this.mdSnackBar.open('Postal address validation failed!', 'Close', { duration: 3000 });
				return;
			}
		}
		this.profileService.save(this.model).subscribe(
			data => {
				this.refresh();
				this.isLoading = false;
				this.mdSnackBar.open('Profile saved successfully!', 'Close', { duration: 3000 });
			},
			error => {
				console.error('Problem saving user!');
			}
		);
	}
}

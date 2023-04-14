import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { fadeInAnimation } from "../../route.animation";
import { Router, ActivatedRoute } from "@angular/router";
import { MdDialog, MdDialogRef } from '@angular/material';
import { TermsComponent } from '../terms/terms.component';
import { FormControl, NgModel } from '@angular/forms';
import { Http, RequestOptions, Headers, Response, URLSearchParams } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { User } from '../../../data/entities/user';
import { RegisterService } from '../../../services/register.service';
import { MdSnackBar } from '@angular/material';
import { GeoService } from '../../../services/geo.service';
import { DomainGuard } from '../../../services/domain.guard';

@Component({
	selector: 'ms-register',
	templateUrl: './register.component.html',
	styleUrls: ['./register.component.scss'],
	host: {
		'[@fadeInAnimation]': 'true'
	},
	animations: [fadeInAnimation],
	providers: [RegisterService]
})
export class RegisterComponent implements OnInit {
	@ViewChild('residentialAddressCity') residentialAddressCity: NgModel;

	isLoading: boolean = false;
	errorMessage: string;

	model = new User();
	filteredCities: Observable<any[]>;

	constructor(
		private http: Http,
		private router: Router,
		private route: ActivatedRoute,
		private dialog: MdDialog,
		private registerService: RegisterService,
		private mdSnackBar: MdSnackBar,
		private geoService: GeoService,
		private domainGuard: DomainGuard
	) { }

	ngOnInit() {
		this.model.optIn = true;
		this.raFilterCities();
	}

	ngAfterViewInit(): void {
		this.raFilterCities();
	}

	raFilterCities() {
		this.residentialAddressCity.valueChanges
			.subscribe(
				val => {
					if (typeof val === 'string') {
						this.filteredCities = this.geoService.filteredCities(val);
					}
				}
			);
	}

	onRaCityBlur() {
		if (typeof this.model.residentialAddress.cityObject != 'object') {
			this.model.residentialAddress.cityObject = null;
			this.model.residentialAddress.city = null;
			this.model.residentialAddress.country = null;
		}
	}

	displayFn(city: any): string {
		if (city) {
			this.model.residentialAddress.city = city.name;
			this.model.residentialAddress.country = city.country;
		}
		return city ? city.name : '';
	}

	register() {
		let modelCopy = Object.assign({}, this.model);
		if (modelCopy.residentialAddress.isNull()) {
			modelCopy.residentialAddress = null;
		} else {
			let success = modelCopy.residentialAddress.validate();
			if (!success) {
				this.errorMessage = 'Address validation failed! Please make sure Address Line 1, City, and Postal Code/ZIP is filled in.';
				this.mdSnackBar.open('Address validation failed!', 'Close', { duration: 5000 });
				return;
			}
		}
		if (modelCopy.postalAddress.isNull()) {
			modelCopy.postalAddress = null;
		} else {
			let success = modelCopy.postalAddress.validate();
			if (!success) {
				this.errorMessage = 'Address validation failed! Please make sure Address Line 1, City, and Postal Code/ZIP is filled in.';
				this.mdSnackBar.open('Address validation failed!', 'Close', { duration: 5000 });
				return;
			}
		}
		console.log(modelCopy);
		this.registerService.register(modelCopy).subscribe(
			data => {
				console.log(data);
				if (!data.successful) {
					this.errorMessage = 'Registration failed';
				} else {
					this.errorMessage = null;
					this.router.navigate([this.domainGuard.domainMachineName, 'register-success']);
				}

			},
			error => {
				console.error('Problem registering user!');
				this.errorMessage = error;
			}
		);
	}

	terms() {
		let dialogRef = this.dialog.open(TermsComponent);
		dialogRef.afterClosed().subscribe(result => {
			console.log(result);
		});
	}
}

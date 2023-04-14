import { Injectable } from '@angular/core';
import { Response, Headers, RequestOptions } from '@angular/http';
import { AuthHttp } from 'angular2-jwt';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import { NewPassword } from '../entities/newPassword';
import { User } from '../entities/user';
import { Address } from '../entities/address';

@Injectable()
export class ProfileService {
	constructor(private authHttp: AuthHttp) { };

	getUser(): Observable<any> {
		return this.authHttp.get('api/affiliate/affiliates/luckyaffiliates/')
			.map((response: Response) => response.json().data);
	}

	changePassword(newPassword: NewPassword): Observable<any> {
		return this.authHttp.post('api/affiliate/affiliates/luckyaffiliates/changepassword', newPassword.newPassword)
			.map((response: Response) => response.json().data);
	}

	save(user: User): Observable<any> {
		user.passwordUpdated = null;
		return this.authHttp.post('api/affiliate/affiliates/luckyaffiliates/save', user)
			.map((response: Response) => response.json().data);
	};

	validateAddress(address: Address): boolean {
		if (
				(address.addressLine1 != null && address.addressLine1 != '')
			 || (address.addressLine2 != null && address.addressLine2 != '')
			 || (address.addressLine3 != null && address.addressLine3 != '')
			 || (address.city != null && address.city != '')
			 || (address.country != null && address.country != '')
			 || (address.postalCode != null && address.postalCode != '')
		) {
			if (address.addressLine1 == null || address.addressLine1 == '') return false;
			if (address.city == null || address.city == '') return false;
			if (address.postalCode == null || address.postalCode == '') return false;
		}
		return true;
	}
}

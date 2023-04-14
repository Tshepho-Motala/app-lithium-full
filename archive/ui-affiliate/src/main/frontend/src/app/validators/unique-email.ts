import { Directive, forwardRef } from '@angular/core';
import { NG_ASYNC_VALIDATORS, AsyncValidator, AbstractControl } from '@angular/forms';
import { Observable } from 'rxjs/Observable';
import { Http } from '@angular/http';
import { DomainGuard } from '../services/domain.guard';

@Directive({
	selector: '[uniqueEmail][ngModel]',
	providers: [{ provide: NG_ASYNC_VALIDATORS, useExisting: forwardRef(() => UniqueEmailValidator), multi: true }]
})
export class UniqueEmailValidator implements AsyncValidator {
	constructor(
		private http: Http,
		private domainGuard: DomainGuard
	) { }
	
	validate(c: AbstractControl): Observable<{ [key: string]: any } | null> {
		return this.http.get('api/affiliate/affiliates/'+this.domainGuard.domainMachineName+'/isunique/'+c.value+'/email')
			.map(response => {
				let value: boolean = response.json().data;
				if (value == true) {
					return;
				} else {
					return { 'The email address you entered is already in use. If you have forgotten your password, please try a password reset': true };
				}
			});
	}
}
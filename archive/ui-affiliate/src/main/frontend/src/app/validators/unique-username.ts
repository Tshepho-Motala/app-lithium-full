import { Directive, forwardRef } from '@angular/core';
import { NG_ASYNC_VALIDATORS, AsyncValidator, AbstractControl } from '@angular/forms';
import { Observable } from 'rxjs/Observable';
import { Http } from '@angular/http';
import { DomainGuard } from '../services/domain.guard';

@Directive({
	selector: '[uniqueUsername][ngModel]',
	providers: [{ provide: NG_ASYNC_VALIDATORS, useExisting: forwardRef(() => UniqueUsernameValidator), multi: true }]
})
export class UniqueUsernameValidator implements AsyncValidator {
	constructor(
		private http: Http,
		private domainGuard: DomainGuard
	) { }
	
	validate(c: AbstractControl): Observable<{ [key: string]: any } | null> {
		return this.http.get('api/affiliate/affiliates/'+this.domainGuard.domainMachineName+'/isunique/'+c.value+'/username')
			.map(response => {
				let value: boolean = response.json().data;
				if (value == true) {
					return;
				} else {
					return { 'The username you entered is already in use. Please try a different username': true };
				}
			});
	}
}
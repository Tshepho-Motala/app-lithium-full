import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import { UserPasswordReset } from '../data/entities/userPasswordReset';
import { DomainGuard } from './domain.guard';

@Injectable()
export class ForgotPasswordService {
	constructor(
		private http: Http,
		private domainGuard: DomainGuard
	) { };

	step1(username: string): Observable<any> {
		return this.http.post('api/user/'+this.domainGuard.domainMachineName+'/'+username+'/passwordreset/step1', {})
			.map((response: Response) => response.json());
	}

	step2(username: string, password: string, token: string): Observable<any> {
		let userPasswordReset = new UserPasswordReset(token, password);
		return this.http.post('api/user/'+this.domainGuard.domainMachineName+'/'+username+'/passwordreset/step2', userPasswordReset)
			.map((response: Response) => response.json());
	}
}

import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import { User } from '../data/entities/user';
import { DomainGuard } from './domain.guard';

@Injectable()
export class RegisterService {
	constructor(
		private http: Http,
		private domainGuard: DomainGuard
	) { };

	register(user: User): Observable<any> {
		let headers = new Headers({ 'Content-Type': 'application/json' });
		let options = new RequestOptions({ headers: headers });
		return this.http.post('api/affiliate/affiliates/'+this.domainGuard.domainMachineName+'/create', user, options)
			.map((response: Response) => response.json());
	}
}

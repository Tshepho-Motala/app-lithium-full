import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';

@Injectable()
export class GeoService {
	constructor(public http: Http) { };
	
	filteredCities(filter: string): Observable<any> {
		return this.http.get('api/geo/geo/cities/'+((filter != null)? filter:'a'))
			.map((response: Response) => response.json().data);
	}
}
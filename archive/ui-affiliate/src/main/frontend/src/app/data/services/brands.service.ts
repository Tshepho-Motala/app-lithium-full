import { Injectable } from '@angular/core';
import { Http, RequestOptions, Headers, Response, URLSearchParams } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';

import { Brand } from '../entities/brand';

@Injectable()
export class BrandsService {

  constructor(private http: Http) {}

  findBrands(): Observable<Brand[]> {
    return this.http.get('api/affiliate/brands')
    .map((response: Response) => {
        let json = response.json();
        if (!json) throw "No json in response...";
        if (!json.successful) throw "Request success but data marked unsuccessful";
        if (!json.data) throw "Request success but no data";
        return json.data;
      });
  }

}

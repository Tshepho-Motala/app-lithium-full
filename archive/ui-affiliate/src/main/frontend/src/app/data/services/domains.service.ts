import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import { Domain } from '../entities/domain';
import { DataTableService, DataTableResponse, PagerState } from '../../modules/material-ex';

@Injectable()
export class DomainsService {

  private baseUrl: string = `api/affiliate/domains`;

  constructor(private http: Http) { }

  get(machineName: string): Observable<Domain> {
    let url = this.baseUrl + '/' + machineName;
    return this.http.get(url).map((response: Response) => {
        return response.json().data;
    });
  }

}

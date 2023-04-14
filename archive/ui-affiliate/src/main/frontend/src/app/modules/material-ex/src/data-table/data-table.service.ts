import { Injectable } from '@angular/core';
import { Http, RequestOptions, Headers, Response, URLSearchParams } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/retry';
import { DataTableResponse } from './data-table-response';
import { PagerState } from '../pager/pager-state';
import { AuthHttp } from 'angular2-jwt';

@Injectable()
export class DataTableService {

  constructor(private http: AuthHttp) {
  }

  get<T>(url: string, search?: string, state?: PagerState): Observable<DataTableResponse<T>> {
    if (!search) search = "";
    if (!state) state = new PagerState();
    if (url != state.lastUrl) state.currentPage = 1;
    state.lastUrl = url;
    return this.http.get(url + `&draw=draw&start=${state.startRecord}&length=${state.recordsPerPage}&search[value]=${search}`)
    .retry(10)
    .map((response: Response) => {
        let json = response.json();
        if (json == undefined) throw "No json in response...";
        if (json.data == undefined) throw "Request success but no data";
        var r: DataTableResponse<T> = json;
        state.records = r.recordsFiltered;
        state.update();
        return r;
      });
  }

}

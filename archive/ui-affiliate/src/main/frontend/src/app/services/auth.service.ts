import { Injectable } from '@angular/core';
import { Http, URLSearchParams, RequestOptions, Headers, Response } from '@angular/http';
import { Router } from '@angular/router';
import { JwtHelper, tokenNotExpired } from 'angular2-jwt';
import { Subject, Observable } from 'rxjs';
import { AuthenticatedUser } from '../data/entities/authenticatedUser';
import { DomainGuard } from "./domain.guard";
import * as pako from 'pako';

@Injectable()
export class AuthService {

	tokenEndpoint: string = 'api/oauth2/oauth/token';
	clientUsername: string = 'acme';
	clientPassword: string = 'acmesecret';

	private jwtHelper: JwtHelper = new JwtHelper();

  private _accessToken: string;
  private _refreshToken: string;
  private _expiresIn: number;
  private _user: AuthenticatedUser;

  get accessToken(): string {
    if (!this._accessToken)
    this._accessToken = localStorage.getItem('access_token');
    return this._accessToken;
  }

  get refreshToken(): string {
    if (!this._refreshToken)
    this._refreshToken = localStorage.getItem('refresh_token');
    return this._refreshToken;
  }

  get authenticatedUser(): AuthenticatedUser {
    if (!this._user) {
      let userString: string = localStorage.getItem('user');
      if (userString != null) this._user = JSON.parse(userString);
    }
    return this._user;
  }

  set accessToken(value: string) {
    this._accessToken = value;
    if (value != null) {
      localStorage.setItem('access_token', value);
    } else {
      localStorage.removeItem('access_token');
    }
  }

  set refreshToken(value: string) {
    this._refreshToken = value;
    if (value != null) {
      localStorage.setItem('refresh_token', value);
    } else {
      localStorage.removeItem('refresh_token');
    }
  }

  set authenticatedUser(value: AuthenticatedUser) {
    this._user = value;
    if (value != null) {
      localStorage.setItem('user', JSON.stringify(value));
    } else {
      localStorage.removeItem('user');
    }
  }

  constructor(
      private http: Http,
      private router: Router,
      private domainGuard: DomainGuard) {
    if (this.refreshToken) {
      this.refresh();
    }
  }

  authenticated() {
    return tokenNotExpired('access_token');
  }

  logout() {
    this.accessToken = null;
    this.refreshToken = null;
    this.authenticatedUser = null;
    this.router.navigate(["/", this.domainGuard.domainMachineName, "login"]);
  }

  login(username: string, password: string): Observable<AuthenticatedUser> {
    let data = new URLSearchParams();
    data.append('grant_type', 'password');
    data.append('username', this.domainGuard.domainMachineName + "/" + username);
    data.append('password', password);
    let options = new RequestOptions({
      headers: new Headers(
        { 'Authorization': 'Basic ' + btoa(this.clientUsername + ':' + this.clientPassword) }
      )
    })
    return this.http.post(this.tokenEndpoint, data, options)
      .map((response: Response) => {
        let o = response.json();
        this.accessToken = o.access_token;
        this.refreshToken = o.refresh_token;
        // this.expiresIn = o.expires_in;
        this.decodeToken(o);
        return this.authenticatedUser;
      })
      .catch(this.handleError);
	}

  private handleError (error: Response | any) {
    let errMsg: string;
    if (error instanceof Response) {
      if (error.status == 500) {
        return Observable.throw("An unexpected server error occurred. Please try again later.");
      }
      if (error.status == 401) {
        return Observable.throw("Invalid username or password.");
      }
      const body = error.json() || '';
      const err = body.error || JSON.stringify(body);
      errMsg = `${error.status} - ${error.statusText || ''} ${err}`;
    } else {
      errMsg = error.message ? error.message : error.toString();
    }
    console.error(errMsg);
    return Observable.throw(errMsg);
  }

  private refresh() {
    let data = new URLSearchParams();
    data.append('grant_type', 'refresh_token');
    data.append('refresh_token', this.refreshToken);
    let options = new RequestOptions({
      headers: new Headers(
        { 'Authorization': 'Basic ' + btoa(this.clientUsername + ':' + this.clientPassword) }
      )
    })
    return this.http.post(this.tokenEndpoint, data, options).subscribe((response: Response) => {
      console.log(response.json());
    });
  }

	private decodeToken(tokenPayload) {
		if (tokenPayload && tokenPayload.access_token) {
			try {
				let jwtHelper = new JwtHelper();
				let decryptedToken = jwtHelper.decodeToken(tokenPayload.access_token);
				console.log(decryptedToken);
				let strData = atob(decryptedToken.jwtUser);
				let charData = strData.split('').map(function(x){return x.charCodeAt(0);});
				let binData = new Uint8Array(charData);
				let data = pako.inflate(binData);
				strData = String.fromCharCode.apply(null, new Uint16Array(data));
				let u = JSON.parse(strData);
				this.authenticatedUser = new AuthenticatedUser(u.i, u.u, u.f, u.l, u.e, u.di, u.dn, u.a);
			} catch (error) {
				console.error(error);
			}
		}
	}


}

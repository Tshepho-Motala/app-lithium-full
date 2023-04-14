import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { DomainGuard } from './domain.guard';
import { AuthService } from './auth.service';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class AuthGuard implements CanActivate {
	constructor(
		private router: Router,
		private domainGuard: DomainGuard,
		private authService: AuthService
	) { }

	canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
		if (this.authService.authenticated()) {
			return true;
		}
		this.router.navigate(['/', this.domainGuard.domainMachineName, 'login'], { queryParams: { returnUrl: state.url } });
		return false;
	}
}

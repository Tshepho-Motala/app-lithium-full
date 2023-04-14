import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { DomainsService } from '../data/services/domains.service';
import { Domain } from '../data/entities/domain';

@Injectable()
export class DomainGuard implements CanActivate {

  public domain: Domain;
  public domainMachineName: string;

  constructor(private router: Router, private domainsService: DomainsService) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    this.domainMachineName = route.params['domain'];
    return this.domainsService.get(this.domainMachineName).map((result) => {
      this.domain = result;
      if (result == null) this.router.navigate(["/"]);
      return (result != null);
    });
  }
}

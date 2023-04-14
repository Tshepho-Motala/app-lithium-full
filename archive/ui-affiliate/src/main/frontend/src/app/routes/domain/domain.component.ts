import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';
import { ReplaySubject } from 'rxjs/ReplaySubject';
import { Observable } from 'rxjs/Observable';

@Component({
  selector: 'app-domain',
	templateUrl: './domain.component.html'
})
export class DomainComponent implements OnInit, OnDestroy {

  constructor(private route: ActivatedRoute) { }

  private paramsSubscription: Subscription;
  public domainMachineName: string;
  private readySubject: ReplaySubject<boolean>;

  ngOnInit() {
    this.readySubject = new ReplaySubject<boolean>();
    this.paramsSubscription = this.route.params.subscribe(params => {
      this.domainMachineName = params['domain'];
      this.readySubject.next(true);
    });
  }

  ngOnDestroy() {
    this.paramsSubscription.unsubscribe();
  }

  public ready(): Observable<boolean> {
    return this.readySubject.asObservable();
  }

}

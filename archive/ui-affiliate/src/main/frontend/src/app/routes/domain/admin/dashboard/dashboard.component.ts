import { Component, OnInit, OnDestroy } from '@angular/core';
import { TdDigitsPipe } from '@covalent/core';
import * as shape from 'd3-shape';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, OnDestroy {

  single: any = [
    { 'name': 'Hits',       'value': 382941 },
    { 'name': 'Clicks',     'value': 152294 },
    { 'name': 'Signups',    'value': 123000 },
    { 'name': 'Depositors', 'value':  82892 }
  ];

  dataHits = [{
    'name': 'Hits',
    'series': [ 
      { 'value': 6194, 'name': new Date('2016-09-15T19:25:07.773Z') },
      { 'value': 5422, 'name': new Date('2016-09-16T19:25:07.773Z') },
      { 'value': 7821, 'name': new Date('2016-09-17T19:25:07.773Z') },
      { 'value': 9281, 'name': new Date('2016-09-18T19:25:07.773Z') },
      { 'value': 4212, 'name': new Date('2016-09-19T19:25:07.773Z') },
      { 'value': 9984, 'name': new Date('2016-09-20T19:25:07.773Z') },
      { 'value': 5001, 'name': new Date('2016-09-21T19:25:07.773Z') },
      { 'value': 6879, 'name': new Date('2016-09-22T19:25:07.773Z') },
      { 'value': 6194, 'name': new Date('2016-09-23T19:25:07.773Z') }
    ]
  }];

  curve: any = shape.curveCardinal.tension(0.3);

  blueScheme: any = {
    domain: ['#0D47A1', '#1976D2', '#039BE5', '#29B6F6', '#81D4FA', '#B2EBF2'],
  };

  orangeScheme: any = {
    domain: ['#BF360C', '#EF6C00', '#FB8C00', '#FFB300', '#FFCA28', '#FFF176'],
  };

  constructor() {}

  ngOnInit() {
  }

  ngOnDestroy() {
  }


  axisDigits(val: any): any {
    return new TdDigitsPipe().transform(val, 3);
  }

}

import { Component, OnInit } from '@angular/core';
import { BrandsService } from '../../../../data/services/brands.service';
import { Brand } from '../../../../data/entities/brand';
import { Observable } from 'rxjs/Observable';

@Component({
  selector: 'app-brands',
  templateUrl: './brands.component.html',
  styleUrls: ['./brands.component.scss']
})
export class BrandsComponent implements OnInit {

  brands: Observable<Brand[]>;
  isLoading: boolean = true;

  constructor(private brandsService: BrandsService) { }

  ngOnInit() {
    this.isLoading = true;
    this.brands = this.brandsService.findBrands().map((list) => {
      this.isLoading = false;
      return list;
    });
  }

}

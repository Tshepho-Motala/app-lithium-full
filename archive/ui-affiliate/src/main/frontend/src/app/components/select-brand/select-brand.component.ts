import { Component, OnInit, Output, EventEmitter, Input } from '@angular/core';
import { BrandsService } from '../../data/services/brands.service';
import { Brand } from '../../data/entities/brand';

@Component({
  selector: 'select-brand',
  templateUrl: './select-brand.component.html',
  styleUrls: ['./select-brand.component.scss']
})
export class SelectBrandComponent implements OnInit {

  brands: Brand[];

  _brand: Brand;
  _brandId: number;

  @Input()
  get brandId(): number {
    return (this._brandId)? this._brandId: null;
  }

  @Output() brandIdChange = new EventEmitter;
  set brandId(val: number) {
    this._brandId = val;
    if (this._brandId && this.brands) {
      this._brand = this.brands.find(brand => brand.id == this._brandId);
    }
    this.brandIdChange.emit(this._brandId);
    this.brandChange.emit(this._brand);
  }

  @Input()
  get brand(): Brand {
    return this._brand;
  }

  @Output() brandChange = new EventEmitter;
  set brand(val: Brand) {
    this._brand = val;
    if (val) this._brandId = val.id;
    this.brandIdChange.emit(this._brandId);
    this.brandChange.emit(this._brand);
  }

  constructor(private service: BrandsService) { }

  ngOnInit() {
    this.service.findBrands().subscribe((list) => {
      this.brands = list;
      if (this._brandId && this.brands) {
        this._brand = this.brands.find(brand => brand.id == this._brandId);
      }
    })
  }

  clear() {
    this.brand = null;
    this.brandId = null;
  }

  select(brand: Brand) {
    this.brand = brand;
    this.brandId = brand.id;
  }

}

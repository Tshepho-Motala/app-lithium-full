import { Component } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { MdIconRegistry } from '@angular/material';

@Component({
  selector: 'app',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {

  constructor(private registry: MdIconRegistry,
              private sanitizer: DomSanitizer) {
    this.registry.addSvgIconInNamespace('assets', 'logo',
      this.sanitizer.bypassSecurityTrustResourceUrl('assets/icons/logo.svg'));
  }

}

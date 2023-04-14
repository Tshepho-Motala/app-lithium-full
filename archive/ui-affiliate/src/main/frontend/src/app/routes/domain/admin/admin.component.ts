import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../services/auth.service';
import { DomainGuard } from '../../../services/domain.guard';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss']
})
export class AdminComponent implements OnInit {

  constructor(private domainGuard: DomainGuard, private authService: AuthService) { }

  ngOnInit() {

  }

}

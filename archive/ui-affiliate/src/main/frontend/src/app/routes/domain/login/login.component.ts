import { Component, OnInit } from '@angular/core';
import { Router } from "@angular/router";
import { ActivatedRoute } from "@angular/router";
import { fadeInAnimation } from "../../route.animation";
import { DomainGuard } from "../../../services/domain.guard";
import { AuthService } from "../../../services/auth.service";

@Component({
  selector: 'ms-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  host: {
    '[@fadeInAnimation]': 'true'
  },
  animations: [fadeInAnimation]
})
export class LoginComponent implements OnInit {
  isLoading: boolean = false;
  errorMessage: string;

  username: string;
  password: string;
  returnUrl: string;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private authService: AuthService,
    private domainGuard: DomainGuard
  ) { }

  ngOnInit() {
  }

  login() {
    this.errorMessage = '';
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'];
    this.isLoading = true;
    this.authService.login(this.username, this.password)
      .subscribe(
      data => {
        if (this.returnUrl) {
          this.router.navigateByUrl(this.returnUrl);
        } else {
          this.router.navigate(['../admin'], { relativeTo: this.route });
        }
      },
      error => {
        setTimeout(() => {
          this.errorMessage = error;
          this.isLoading = false;
        }, 5000);
      }
      );
  }
}

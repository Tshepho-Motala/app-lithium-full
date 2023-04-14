import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Routes, RouterModule } from '@angular/router';

import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { ForgotPasswordComponent } from './pages/forgot-password/forgot-password.component';
import { ForgotPasswordResetComponent } from './pages/forgot-password-reset/forgot-password-reset.component';

export const AUTH_ROUTES = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'forgot-password-reset', component: ForgotPasswordResetComponent }
];

@NgModule({
  imports: [
    CommonModule,
//    RouterModule.forChild()
  ],
  declarations: []
})
export class AuthRoutingModule { }

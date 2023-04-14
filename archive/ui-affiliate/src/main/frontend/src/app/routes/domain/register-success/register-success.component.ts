import { Component } from '@angular/core';
import { fadeInAnimation } from '../../route.animation';
import { DomainGuard } from '../../../services/domain.guard';

@Component({
	selector: 'ms-register',
	templateUrl: './register-success.component.html',
	styleUrls: ['./register-success.component.scss'],
	host: {
		'[@fadeInAnimation]': 'true'
	},
	animations: [fadeInAnimation]
})
export class RegisterSuccessComponent {
    constructor(
		private domainGuard: DomainGuard
	) {
	}
}
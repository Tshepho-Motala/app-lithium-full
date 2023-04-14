import { Address } from '../../affiliate/classes/address';

export class User {
	username: string;
	password: string;
	passwordConfirm: string;
	firstName: string;
	lastName: string;
	email: string;
	emailConfirm: string;
	cellphoneNumber: string;
	residentialAddress: Address = new Address();
	companyName: string;
	websiteURL: string;
	paymentDetails: string;
	optIn: boolean;
	acceptTerms: boolean;
}
import { Address } from './address';

export class User {
	id: number;
	username: string;
	email: string;
	firstName: string;
	lastName: string;
	telephoneNumber: string;
	cellphoneNumber: string;
	residentialAddress: Address = new Address();
	postalAddress: Address = new Address();
	socialSecurityNumber: string;
	dateOfBirth: Date;
	passwordUpdated: string;
	passwordUpdatedBy: string;
	companyName: string;
	websiteURL: string;
	paymentDetails: string;
	dobYear: number;
	dobMonth: number;
	dobDay: number;
  optIn: boolean;
	acceptTerms: boolean;

	setUser(
		id: number,
		username: string,
		email: string,
		firstName: string,
		lastName: string,
		telephoneNumber: string,
		cellphoneNumber: string,
		residentialAddress: Address,
		postalAddress: Address,
		socialSecurityNumber: string,
		dateOfBirth: Date,
		passwordUpdated: string,
		passwordUpdatedBy: string,
		companyName: string,
		websiteURL: string,
		paymentDetails: string,
		dobYear: number,
		dobMonth: number,
		dobDay: number
	) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.telephoneNumber = telephoneNumber;
		this.cellphoneNumber = cellphoneNumber;
		this.residentialAddress = residentialAddress;
		if (this.residentialAddress.city != null)
			this.residentialAddress.cityObject = this.residentialAddress.city;
		this.postalAddress = postalAddress;
		if (this.postalAddress.city != null)
			this.postalAddress.cityObject = this.postalAddress.city;
		this.socialSecurityNumber = socialSecurityNumber;
		this.dateOfBirth = dateOfBirth;
		this.passwordUpdated = passwordUpdated;
		this.passwordUpdatedBy = passwordUpdatedBy;
		this.companyName = companyName;
		this.websiteURL = websiteURL;
		this.paymentDetails = paymentDetails;
		this.dobYear = dobYear;
		this.dobMonth = dobMonth;
		this.dobDay = dobDay;
		if (this.dobYear != null && this.dobMonth != null && this.dobDay != null) {
			this.dateOfBirth = new Date(dobYear, dobMonth-1, dobDay);
		}
	};
}

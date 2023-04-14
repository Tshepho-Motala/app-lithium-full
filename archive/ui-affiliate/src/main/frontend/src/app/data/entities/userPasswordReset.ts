export class UserPasswordReset {
	constructor(public token: string, public password: string) {
		this.token = token;
		this.password = password;
	};
}
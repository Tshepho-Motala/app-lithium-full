export class AuthenticatedUser {
	constructor(
		public id: number,
		public username: string,
		public firstName: string,
		public lastName: string,
		public email: string,
		public domainId: number,
		public domainName: string,
		public apiToken: string
	) { }
}
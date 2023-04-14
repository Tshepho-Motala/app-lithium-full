export class Address {
	addressLine1: string;
	addressLine2: string;
	addressLine3: string;
	cityObject: any;
	city: string;
	cityCode: string;
	adminLevel1: string;
	adminLevel1Code: string;
	country: string;
	countryCode: string;
	postalCode: string;
	
	isNull(): boolean {
		if (
				(this.addressLine1 != null && this.addressLine1 != '')
			 || (this.addressLine2 != null && this.addressLine2 != '')
			 || (this.addressLine3 != null && this.addressLine3 != '')
			 || (this.city != null && this.city != '')
			 || (this.country != null && this.country != '')
			 || (this.postalCode != null && this.postalCode != '')
		) {
			return false;
		} else {
			return true;
		}
	}

	validate(): boolean {
		if (!this.isNull()) {
			if (this.addressLine1 == null || this.addressLine1 == '') return false;
			if (this.city == null || this.city == '') return false;
			if (this.postalCode == null || this.postalCode == '') return false;
		}
		return true;
	}
}
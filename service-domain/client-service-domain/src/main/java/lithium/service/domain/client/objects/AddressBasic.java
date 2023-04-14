package lithium.service.domain.client.objects;

import lombok.Data;

@Data
public class AddressBasic {
	private Long id;
	private String addressType;
	private String addressLine1;
	private String addressLine2;
	private String addressLine3;
	private String city;
	private String cityCode;
	private String adminLevel1;
	private String adminLevel1Code;
	private String country;
	private String countryCode;
	private String postalCode;
	
	public boolean isPhysicalAddress() {
		return (addressType.equalsIgnoreCase("physicalAddress"));
	}
	public boolean isPostalAddress() {
		return (addressType.equalsIgnoreCase("postalAddress"));
	}
}
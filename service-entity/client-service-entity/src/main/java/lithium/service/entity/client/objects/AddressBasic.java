package lithium.service.entity.client.objects;

import lombok.Data;

@Data
public class AddressBasic {
	private Long id;
	private Long entityId;
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
	public boolean isBillingAddress() {
		return (addressType.equalsIgnoreCase("billingAddress"));
	}
}
package lithium.service.user.client.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lithium.service.user.client.validators.safetext.SafeTextConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AddressBasic {
	private Long id;
	private Long userId;

	@SafeTextConstraint
	private String addressType;

	@SafeTextConstraint
	private String addressLine1;

	@SafeTextConstraint
	private String addressLine2;

	@SafeTextConstraint
	private String addressLine3;

	@SafeTextConstraint
	private String city;

	@SafeTextConstraint
	private String cityCode;

	@SafeTextConstraint
	private String adminLevel1;

	@SafeTextConstraint
	private String adminLevel1Code;

	@SafeTextConstraint
	private String country;

	@SafeTextConstraint
	private String countryCode;

	@SafeTextConstraint
	private String postalCode;

	private Boolean manualAddress; 	// Flag users who enters address manually

	@JsonIgnore
	public boolean isPostalAddress() {
		if (addressType == null || addressType.trim().isEmpty()) return false;
		return (addressType.equalsIgnoreCase("postalAddress"));
	}
	@JsonIgnore
	public boolean isResidentialAddress() {
		if (addressType == null || addressType.trim().isEmpty()) return false;
		return (addressType.equalsIgnoreCase("residentialAddress"));
	}
}

package lithium.service.access.client.objects;

import lithium.service.user.client.objects.PlayerBasic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizationRequest {
	private String ipAddress;
	private String country;
	private String claimedCountry;
	private String state;
	private String claimedState;
	private String city;
	private String claimedCity;
	private String browser;
	private String os;
	private String deviceId; //blackbox
	private String userGuid;
	private Boolean overrideValidateOnce;
	private String postCode;
	private PlayerBasic playerBasic; // Used in pre-registration checking, since user lookups by guid will return null
	private Map<String, String> additionalData;
}

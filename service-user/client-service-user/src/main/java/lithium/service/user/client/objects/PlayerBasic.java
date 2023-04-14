package lithium.service.user.client.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lithium.service.user.client.enums.Status;
import lithium.service.user.client.enums.StatusReason;
import lithium.service.user.client.validators.safetext.SafeTextConstraint;
import lithium.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import java.util.Map;

@Data
@Builder
@ToString(exclude = {"password"})
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerBasic {
	@SafeTextConstraint
	private String username;
	private String password;

	private String email;

//	@SafeTextConstraint
	private String firstName;

//	@SafeTextConstraint
	private String lastNamePrefix;

//	@SafeTextConstraint
	private String lastName;

	@SafeTextConstraint
	private String countryCode;

	@SafeTextConstraint
	private String placeOfBirth;

	@SafeTextConstraint
	private String telephoneNumber;

//	@SafeTextConstraint
	private String cellphoneNumber;

	@Valid
	private AddressBasic residentialAddress;

	@Valid
	private AddressBasic postalAddress;

	@SafeTextConstraint
	private String affiliateGuid;

	@SafeTextConstraint
	private String affiliateSecondaryGuid1;

	@SafeTextConstraint
	private String affiliateSecondaryGuid2;

	@SafeTextConstraint
	private String affiliateSecondaryGuid3;

	@SafeTextConstraint
	private String comments;

	@SafeTextConstraint
	private String bonusCode;

	private Integer dobYear;
	private Integer dobMonth;
	private Integer dobDay;
	private Long id;

	@SafeTextConstraint
	private String domainName;
	private Long bonusId;

	@SafeTextConstraint
	private String referrerGuid;

	@SafeTextConstraint
	private String timezone;
	private String deviceId;

	@SafeTextConstraint
	private String gender;
 	private Integer stage;

 	//LIVESCORE-83
	private boolean emailOptOut;
	private boolean postOptOut;
	private boolean smsOptOut;
	private boolean callOptOut;
	private boolean pushOptOut;
	private boolean leaderboardOptOut;
  private boolean testUser;

	//Need this to be false by default /LSPLAT-4774
	private boolean promotionsOptOut;

	private String depositLimitDaily;
	private String depositLimitWeekly;
	private String depositLimitMonthly;

	private String balanceLimit;

	//Need to be presented if PlayTimeLimit service is activated
	private String timeCap;
	private Long timeCapAmount;

	//These need to be presented in UTC format 'HH:MM' eg: 09:15
	//then will internally be converted to timestamps (Presented only if Domain TimeFrameLimit service is activated) v4-Reg
	private String timeSlotLimitStart;
	private String timeSlotLimitEnd;

	// Used as a check to allow registration in an ecosystem on additional brands, this is the user_api_token.token in db
	private String uuid;

	private Boolean emailValidated;
	private Boolean cellphoneValidated;

	// Used to pass non brand specific information on a lithium user object
	private Map<String, String> additionalData;

	// The status and statusReason should be populated when IBAN details mismatch from externalValidations call before calling register/v4
	private Status status;
	private StatusReason statusReason;

	// To allow players on to be registered if under aged
	private boolean underAged;

	private boolean channelsOptOut;

	private List<CollectionData> collectionData;

	public String getLastNamePrefix() {
		return StringUtil.removeExtraSpacesBetweenWords(lastNamePrefix);
	}

	public void setLastNamePrefix(String lastNamePrefix) {
		this.lastNamePrefix = StringUtil.removeExtraSpacesBetweenWords(lastNamePrefix);
	}
}

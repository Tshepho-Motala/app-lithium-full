package lithium.service.user.client.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class FrontendPlayerBasic {

	/**
	 * LSPLAT-2985 - Lack of server side validation
	 *
	 * Decision: FrontendPlayerBasic provides subset of PlayerBasic
	 * Email will not be updatable on the Frontend
	 */

	@SafeTextConstraint
	private String firstName;

	@SafeTextConstraint
	private String lastNamePrefix;

	@SafeTextConstraint
	private String lastName;

	@SafeTextConstraint
	private String countryCode;

	@SafeTextConstraint
	private String placeOfBirth;

	@SafeTextConstraint
	private String telephoneNumber;

	@SafeTextConstraint
	private String cellphoneNumber;

	private Integer dobYear;
	private Integer dobMonth;
	private Integer dobDay;


	@SafeTextConstraint
	private String timezone;

	@SafeTextConstraint
	private String gender;

	private Boolean emailOptOut;
	private Boolean postOptOut;
	private Boolean smsOptOut;
	private Boolean callOptOut;
	private Boolean pushOptOut;
	private Boolean leaderboardOptOut;
	//Need this to be false by default /LSPLAT-4774
	private boolean promotionsOptOut;

	//PLAT-2835
	private Boolean parentEmailOptOut;
	private Boolean parentPostOptOut;
	private Boolean parentSmsOptOut;
	private Boolean parentCallOptOut;
	private Boolean parentPushOptOut;
	private Boolean parentLeaderboardOptOut;
}

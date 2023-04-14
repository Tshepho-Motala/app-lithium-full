package lithium.service.user.client.objects;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lithium.service.user.client.enums.BiometricsStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.joda.time.DateTime;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"passwordHash", "passwordPlaintext", "lastLogin", "session", "newPassword1", "newPassword2", "current"})
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class User implements UserDetails {

  public static final String EXTERNAL_GUID = "External";
  private static final long serialVersionUID = -4794486225082431245L;

	public static final String SYSTEM_GUID = "system";

	public static final String SYSTEM_FULL_NAME = "System";
	
	private Long id;
	
	private Domain domain;
	
	private String username;
	private String externalUsername;
	private String guid;

	@JsonIgnore
	private String passwordHash;
	@JsonIgnore
	private String passwordPlaintext;
	
	// @JsonView(UserViews.Internal.class)
	private List<Group> groups;
	
	private List<LabelValue> labels;
	private Map<String, String> labelAndValue;
	
	private String email;
	private String deletedEmail;
	
	private boolean emailValidated;
	
	private boolean cellphoneValidated;
	
	private String firstName;
	
	private String lastName;

	private String lastNamePrefix;
	
	@Builder.Default
	private boolean enabled = true;
	@Builder.Default
	private boolean deleted = false;
	
	private String telephoneNumber;
	private String deletedTelephoneNumber;
	
	private String cellphoneNumber;
	private String deletedCellphoneNumber;
	
	private String comments;
	@Builder.Default
	private Date createdDate = new Date();
	@Builder.Default
	private Date updatedDate = new Date();
	
	private String apiToken;

	private Address residentialAddress;

	private Address postalAddress;
	
	private String socialSecurityNumber;
	
	private Integer dobYear;

	private Integer dobMonth;
	
	private Integer dobDay;

	private BiometricsStatus biometricsStatus;

	public DateTime getDateOfBirth() {
		if ((dobYear != null) && (dobMonth != null) && (dobDay != null)) {
			return new DateTime(dobYear, dobMonth, dobDay, 0, 0);
		}
		return null;
	}
	
	private Status status;
	private StatusReason statusReason;

	private String bonusCode;
	private String countryCode;
	private String placeOfBirth;

	private Boolean testAccount;

	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> list = new HashSet<GrantedAuthority>();
		if (groups != null) {
			for (Group group : groups) {
				for (GRD grd : group.getGrds()) {
					list.add(grd.getRole());
				}
			}
		}
		return list;
	}

	@Override
	public String getPassword() {
		return passwordPlaintext;
	}

	public String guid() {
		return guid;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public String getName() {
		return firstName + " " + lastName;
	}

	public boolean isAccountNonExpired() {
		return true;
	}

	public boolean isAccountNonLocked() {
		return true;
	}

	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	private String newPassword1;
	
	private String newPassword2;
	
	private Date passwordUpdated;
	
	private String passwordUpdatedBy;
	
	private boolean acceptTerms;
	private boolean optIn; // Historical label value on user, not sure it is even used anymore
	private String companyName;
	private String websiteURL;
	private String paymentDetails;
	private LoginEvent lastLogin;
	private LoginEvent session; // Be careful with these. User may have multiple active sessions/login events.
	
	// Promotions Opt Out
	private Boolean emailOptOut;
	private Boolean smsOptOut;
	private Boolean callOptOut;
	private Boolean pushOptOut;
	private Boolean postOptOut;
	private Boolean promotionsOptOut;
	private Boolean leaderboardOptOut;

	private String referrerGuid;

	private String shortGuid;
	
	private UserApiToken userApiToken;

	private List<UserCategory> userCategories;

	private Boolean hasSelfExcluded;

	private Boolean autoWithdrawalAllowed;

	private Integer failedResetCount;

	private Long verificationStatus;

	private String termsAndConditionsVersion;

	private String protectionOfCustomerFundsVersion;

	private String gender;

	@JsonManagedReference
	private UserRevision current;

	@Builder.Default
	private Boolean ageVerified = false;

	@Builder.Default
	private Boolean addressVerified = false;

	public Integer getFailedResetCount() {
		if (failedResetCount == null) failedResetCount = 0;
		return failedResetCount;
	}

	@Builder.Default
	private String timezone = ZoneId.systemDefault().getId();

	public void clearPassword() {
		passwordHash = "";
		passwordPlaintext = "";
	}

	private Boolean commsOptInComplete; // Will be null or false if there was not at least one user action to communication opt

	public static LocalDate getDobAsLocalDate(User user){
		if (Stream.of(user.dobYear, user.dobMonth, user.dobDay).noneMatch(Objects::isNull)){
			return LocalDate.of(user.dobYear, user.dobMonth, user.dobDay);
		}
		return null;
	}
}

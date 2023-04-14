package lithium.service.user.data.entities;

import static java.util.Objects.isNull;
import static lithium.service.user.client.enums.BiometricsStatus.NOT_REQUIRED;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lithium.service.user.converter.EnumConverter.BiometricsStatusConverter;
import lithium.service.user.validators.CustomPattern;
import lithium.services.LithiumServiceApplication;
import lithium.service.user.client.enums.BiometricsStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Entity(name = "user.User")
@Builder(toBuilder = true)
@ToString(exclude = {"current", "userEvents", "passwordPlaintext", "passwordHash", "userCategories"})
@EqualsAndHashCode(exclude = {"current", "userEvents"})
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Table(
    catalog = "lithium_user",
    name = "user",
    indexes = {
        @Index(name = "idx_u_all", columnList = "domain_id, username", unique = true),
        @Index(name = "idx_u_d_email", columnList = "domain_id, email"),
        @Index(name = "idx_u_d_cellphone", columnList = "domain_id, cellphoneNumber"),
        @Index(name = "idx_u_created_date", columnList = "createdDate", unique = false),
        @Index(name = "idx_u_username", columnList = "username", unique = false),
        @Index(name = "idx_u_first_name", columnList = "firstName", unique = false),
        @Index(name = "idx_u_last_name", columnList = "lastName", unique = false),
        @Index(name = "idx_u_email", columnList = "email", unique = false),
        @Index(name = "idx_u_dob_day", columnList = "dobDay", unique = false),
        @Index(name = "idx_u_dob_month", columnList = "dobMonth", unique = false),
        @Index(name = "idx_u_dob_year", columnList = "dobYear", unique = false),
        @Index(name = "idx_u_verification_status", columnList = "verification_status", unique = false),
        @Index(name = "idx_u_status", columnList = "status_id", unique = false),
        @Index(name = "idx_u_cellphone_number", columnList = "cellphoneNumber", unique = false),
        @Index(name = "idx_u_username_first_name_last_name", columnList = "username, firstName, lastName", unique = false)
    }
)
@CustomPattern.List({
    @CustomPattern(message = "validation failed", email = "email", telephoneNumber = "telephoneNumber", cellphoneNumber = "cellphoneNumber", domain = "domain", firstName = "firstName", lastName = "lastName")
})
public class User implements Serializable {

  private static final long serialVersionUID = 1011190172750630560L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Version
  int version;

  @Column(name = "guid", updatable = true, nullable = true, unique = true)
  private String guid;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "domain_id")
  @JsonManagedReference("user_domain")
  private Domain domain;

  @Column(nullable = false)
  @Size(min = 1, max = 256, message = "No more than 256 and no less than 1 characters")
  @Pattern(regexp = "^[-_A-Za-z0-9\\._ØÆÅøæå]+$", message = "Only numbers, letters, underscore dots and dashes allowed")
  private String username;

  private String externalUsername;

  private boolean requireSowDocument;

  @Column(nullable = true)
  @JsonIgnore
  private String passwordPlaintext;

  @Column(nullable = true)
  @JsonIgnore
  private String passwordHash;

  @Column
  private Date passwordUpdated;

  @Column
  private String passwordUpdatedBy;

  @Column
  private String gender;

  @Singular
  @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  @JoinTable(
      catalog = "lithium_user",
      name = "user_groups",
      joinColumns = {@JoinColumn(name = "user_id", nullable = false, updatable = false)},
      inverseJoinColumns = {@JoinColumn(name = "group_id", nullable = false, updatable = false)},
      indexes = {
          @Index(name = "idx_urr_id", columnList = "user_id,group_id", unique = true)
      }
  )
  @JsonManagedReference("user_groups")
  private List<Group> groups;

  @Singular
  @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinTable(
      catalog = "lithium_user",
      name = "user_categories",
      joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)},
      inverseJoinColumns = {@JoinColumn(name = "user_category_id", referencedColumnName = "id", nullable = false)},
      indexes = {
          @Index(name = "idx_urr_id", columnList = "user_id,user_category_id", unique = true)
      }
  )
  @JsonIgnoreProperties("users")
  private List<UserCategory> userCategories;

  //	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
//	@JoinTable(
//			name = "user_label_value",
//			joinColumns = {	@JoinColumn(name = "user_id", nullable = false, updatable = false) },
//			inverseJoinColumns = { @JoinColumn(name = "label_value_id", nullable = false, updatable = false) },
//			indexes = {
//				@Index(name="idx_user_id", columnList="user_id", unique=false),
//				@Index(name="idx_label_value_id", columnList="label_value_id", unique=false)
//			}
//	)

  /**
   * When user is within an ecosystem:
   * The last user within an ecosystem who has made a modification to the user revision label values;
   * - in order to retrieve all revisions for a user within an ecosystem, you will first need to find
   *   all user_revision WHERE user_id in(linkedEcosystemUserIds)
   *
   * When not inside an ecosystem, all revisions will be attached to the same user_id
   */
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = true)
  @JsonManagedReference
  private UserRevision current;
//  @CustomPattern(regexp = "^$|[_A-Za-z0-9-\\+]+(\\.[_+A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message = "Invalid email address")
  @Column(name = "EMAIL")
  private String email;
  private String deletedEmail;
//  @CustomPattern(regexp = "^[\\p{L}\\p{Pd}\\p{Po}\\p{Sk}+ ]*$", message = "Invalid characters in First Name")
  @Column(nullable = false)
  @Size(max = 255, message = "No more than 255 characters")
  /**
   * If updating this, remember to update SignupService.
   * lithium.service.user.services.SignupService
   */
  private String firstName;
//  @CustomPattern(regexp = "^[\\p{L}\\p{Pd}\\p{Po}\\p{Sk}+ ]*$", message = "Invalid characters in Last Name")
  @Column(nullable = false)
//	@Size(min=2, max=35, message="No more than 30 and no less than 2 characters")
  /**
   * If updating this, remember to update SignupService.
   * lithium.service.user.services.SignupService
   */
  @Size(max = 255, message = "No more than 255 characters")
  private String lastName;
  @Pattern(regexp = "^[a-zA-Z ]*", message = "Invalid characters in lastNamePrefix")
  private String lastNamePrefix;
  @Column(nullable = false)
  private boolean deleted;
  @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "residential_address_id")
  @JsonManagedReference("user_address")
  @JsonInclude(Include.NON_NULL)
  private Address residentialAddress;
  @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "postal_address_id")
  @JsonManagedReference("user_address")
  @JsonInclude(Include.NON_NULL)
  private Address postalAddress;
  //	@Column(nullable=false)
//	private boolean enabled;
  @Column
//  @CustomPattern(regexp = "^((\\+[0-9]{1,3}){0,1}[0-9\\ ]{4,14}(?:x.+)?){0,1}$", message = "Invalid telephone number. Please specify the number in international format (+47 xx xxx xxx) or local format (xx xxx xxx). Spaces are allowed. An extension using x is also allowed.")
  private String telephoneNumber;
  private String deletedTelephoneNumber;
  @Column
//  @CustomPattern(regexp = "^((\\+[0-9]{1,3}){0,1}[0-9\\ ]{4,14}(?:x.+)?){0,1}$", message = "Invalid cellphone number. Please specify the number in international format (+47 xx xxx xxx) or local format (xx xxx xxx). Spaces are allowed.")
  private String cellphoneNumber;
  private String deletedCellphoneNumber;
  private String countryCode;
  private String placeOfBirth;
  @Column(length = 1000)
  @Size(max = 1000, message = "The comment may not exceed 1000 characters")
  private String comments;
  @Builder.Default
  @Column(nullable = false)
  private Date createdDate = new Date();
  @Builder.Default
  @Column(nullable = false)
  private Date updatedDate = new Date();
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "status_id")
//	@JsonManagedReference("user_address")
  private Status status;
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = true)
  private StatusReason statusReason;
  @Column(nullable = false)
  private boolean emailValidated;
  private String socialSecurityNumber;
  @Column(nullable = true)
  @Size(max = 20, message = "No more than 20 characters")
  private String bonusCode;
  @Column(nullable = true)
  private Integer dobYear;
  @Column(nullable = true)
  private Integer dobMonth;
  @Column(nullable = true)
  private Integer dobDay;
  @Column(nullable = true)
  private boolean welcomeEmailSent;
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = true)
  @JsonBackReference("user_loginevent")
  private LoginEvent lastLogin;
  @Transient
  @JsonIgnore
  private LoginEvent session;
  @JsonIgnore
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
  // @Fetch(FetchMode.SUBSELECT)
  private List<UserEvent> userEvents;
  // Promotions Opt Out
  @Column(nullable = false)
  private boolean emailOptOut;
  @Column(nullable = false)
  private boolean postOptOut;
  @Column(nullable = false)
  private boolean smsOptOut;

//	@Transient
//	private String lastLoginClientType;

  //	@Transient
//	private Date lastLoginDateTime;
  @Column(nullable = false)
  private boolean callOptOut;
  @Column(nullable = false)
  private boolean pushOptOut;
  @Column(nullable = false)
  private boolean leaderboardOptOut;
  @Column(nullable = false)
  private boolean promotionsOptOut;
  @Column(nullable = false)
  private boolean cellphoneValidated;
  @Column(nullable = true)
  private boolean welcomeSmsSent;
  @Column(nullable = true)
  private String referrerGuid;
  @Column(name = "timezone", nullable = true)
  private String timezone;
  @JsonManagedReference("user")
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = true)
  private UserApiToken userApiToken;
  @Column(nullable = true)
  private Boolean excessiveFailedLoginBlock; //Flag will be true if the user is blocked due to incorrect login attempts
  /**
   * Could possibly be a label added to the user. However, labels are inside a current revision of the user. Will the labels be copied over if there
   * is ever a new revision of the user? Like with domain settings? Just adding a new boolean flag rather until those questions can be answered.
   */
  @Column(nullable = true)
  private Boolean hasSelfExcluded;
  @Column(nullable = true)
  private Boolean autoWithdrawalAllowed;
  @Builder.Default
  @Column(nullable = true)
  private Integer failedResetCount = 0;
  @Column(name = "verification_status", nullable = true)
  private Long verificationStatus = 1L;
  // FIXME: Initial version for existing users?
  //        ...assuming they have already accepted one. Nullable would mean everyone needs to accept again.
  @Column(nullable = true)
  private String termsAndConditionsVersion;
  @Builder.Default
  @Column(nullable = false)
  private Boolean ageVerified = false;
  @Builder.Default
  @Column(nullable = false)
  private Boolean addressVerified = false;
  //  @Builder.Default
  private Boolean commsOptInComplete; //This indicates whether the user explicitly opted in to communications at least once
  @Builder.Default
  @Column(nullable = true)
  private Boolean testAccount = false;
  //    @Transient
//    private String verificationStatusString;
  @Column(nullable = true)
  private String protectionOfCustomerFundsVersion;
  @Column(nullable = true)
  private Long currentCollectionDataRevId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = true)
  private UserFavourites userFavourites;

  @Builder.Default
  @Column(nullable = false)
  @Convert(converter = BiometricsStatusConverter.class)
  private BiometricsStatus biometricsStatus = NOT_REQUIRED;

  public void addUserCategory(UserCategory userCategory) {
    userCategories.add(userCategory);
    userCategory.getUsers().add(this);
  }

  public void removeUserCategory(UserCategory userCategory) {
    userCategories.remove(userCategory);
    userCategory.getUsers().remove(this);
  }

  public String userCategoriesList() {
    String list = "";
    for (UserCategory uc : userCategories) {
      list += uc.getName() + ",";
    }
    if (list.endsWith(",")) {
      list = list.substring(0, list.length() - 1);
    }
    return list;
  }

  public List<UserCategory> userCategories() {
    return userCategories;
  }

  public Long getVerificationStatus() {
    if (isNull(verificationStatus) || verificationStatus < 1) {
      return 1L;
    }
    return verificationStatus;
  }

  public void incFailedResetCount() {
    failedResetCount += 1;
  }

  public Integer getFailedResetCount() {
    if (failedResetCount == null) {
      failedResetCount = 0;
    }
    return failedResetCount;
  }

  public void clearPassword() {
    passwordHash = "";
    passwordPlaintext = "";
  }

  public String domainName() {
    return domain.getName();
  }

  public String guid() {
//		log.info(""+UUID.randomUUID().hashCode());
    if (guid != null) {
      return guid;
    }
    if (LithiumServiceApplication.GUID_STRATEGY == null) {
      return null;
    }
    switch (LithiumServiceApplication.GUID_STRATEGY) {
      case ID:
        if (domain == null || id == null) {
          return null;
        }
        setGuid(domain.getName() + "/" + id);
        break;
      default:
      case USERNAME:
        if (domain == null || username == null) {
          return null;
        }
        setGuid(domain.getName() + "/" + username);
        break;
    }
    return guid;
  }

  public String getGuid() {
    return guid();
  }

  // Just passing along last login date since this.lastLogin is back referenced here, because of the relationship in login event.
  public Date getLastLoggedInDate() {
    if (this.lastLogin == null) {
      return null;
    }
    return lastLogin.getDate();
  }

  public String getProviderAuthClient() {
    if (this.lastLogin == null) {
      return null;
    }
    return lastLogin.getProviderAuthClient();
  }

  public Date getLoggedOutDate() {
    if (this.lastLogin == null) {
      return null;
    }
    return lastLogin.getLogout();
  }

  public Long getDuration() {
    if (this.lastLogin == null) {
      return null;
    }
    return lastLogin.getDuration();
  }

  public Date getCurrentLoggedInDate() {
    if (this.session == null) {
      return null;
    }
    return session.getDate();
  }

  @PrePersist
  @PreUpdate
  public void cleanup() {
    if (username != null) {
      username = username.toLowerCase().trim();
    }
    if (externalUsername != null) {
      externalUsername = externalUsername.toLowerCase().trim();
    }
    if (email != null) {
      email = email.toLowerCase().trim();
    }
    if (dobDay != null && dobDay == -1) {
      dobDay = null;
    }
    if (dobMonth != null && dobMonth == -1) {
      dobMonth = null;
    }
    if (dobYear != null && dobYear == -1) {
      dobYear = null;
    }
    if (timezone != null && "-1".equalsIgnoreCase(timezone)) {
      timezone = null;
    }
    if (gender != null && "-1".equalsIgnoreCase(gender)) {
      gender = null;
    }
    if (email != null && "".equalsIgnoreCase(email)) {
      email = null;
    }
    if (countryCode != null && "".equalsIgnoreCase(countryCode.trim())) {
      countryCode = null;
    }
    if (firstName != null && "".equalsIgnoreCase(firstName.trim())) {
      firstName = null;
    }
    if (lastName != null && "".equalsIgnoreCase(lastName.trim())) {
      lastName = null;
    }
    if (passwordHash != null && passwordHash.length() > 0) {
      passwordPlaintext = null;
    }
    if (firstName != null) {
      firstName = firstName.trim();
    }
    if (lastName != null) {
      lastName = lastName.trim();
    }
    if (failedResetCount == null) {
      failedResetCount = 0;
    }
    guid = guid();
  }

  public void clearPersonalInfo() {
    // FIXME: 7/13/2022 LSPLAT-6028 as new columns are added to User will need to update here for the deletion
    this.deleted = true;
    this.placeOfBirth = null;
    this.username = String.valueOf(Instant.now().toEpochMilli());
    this.firstName = null;
    this.lastName = null;
    this.lastNamePrefix = null;
    this.countryCode = null;
    this.telephoneNumber = null;
    this.email = null;
    this.cellphoneNumber = null;
    this.deletedEmail = null;
    this.deletedCellphoneNumber = null;
    this.postalAddress = null;
    this.residentialAddress = null;
    this.socialSecurityNumber = null;
    this.timezone = null;
    this.dobDay = null;
    this.dobMonth = null;
    this.dobYear = null;
    this.gender = null;
    this.current = null;
    this.currentCollectionDataRevId = null;
    this.statusReason = null;
    this.clearPassword();
  }

  public boolean getEmailOptOut() {
    return Optional.ofNullable(emailOptOut).orElse(false);
  }

  public boolean getLeaderboardOptOut() {
    return Optional.ofNullable(leaderboardOptOut).orElse(false);
  }

  public boolean getPostOptOut() {
    return Optional.ofNullable(postOptOut).orElse(false);
  }

  public boolean getSmsOptOut() {
    return Optional.ofNullable(smsOptOut).orElse(false);
  }

  public boolean getCallOptOut() {
    return Optional.ofNullable(callOptOut).orElse(false);
  }

  public boolean getPushOptOut() {
    return Optional.ofNullable(pushOptOut).orElse(false);
  }

  public boolean getPromotionsOptOut() {
    return Optional.ofNullable(promotionsOptOut).orElse(false);
  }

  public Boolean getNullableEmailOptOut() {
    return emailOptOut;
  }

  public Boolean getNullablePromotionsOptOut() {
    return promotionsOptOut;
  }

  public Boolean getNullablePostOptOut() {
    return postOptOut;
  }

  public Boolean getNullableSmsOptOut() {
    return smsOptOut;
  }

  public Boolean getNullableCallOptOut() {
    return callOptOut;
  }

  public Boolean getNullablePushOptOut() {
    return pushOptOut;
  }

  public Boolean getNullableLeaderboardOptOut() {
    return leaderboardOptOut;
  }
}

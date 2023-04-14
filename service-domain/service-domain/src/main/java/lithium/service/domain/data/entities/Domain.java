package lithium.service.domain.data.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.URL;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude={"parent"})
@EqualsAndHashCode(of={"id", "name"})
public class Domain implements Serializable {
	private static final long serialVersionUID = -2627674110240559624L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Version
	private int version;

	@Column(nullable=false, unique=true)
	@Size(min=2, max=35)
	@Pattern(regexp="^[_a-z0-9\\.]+$")
	private String name;

	@Column(nullable=false)
	@Size(min=2, max=65, message="No more than 30 and no less than 2 characters")
	private String displayName;

	@Column(nullable=true)
	private String description;
	
	@Column(nullable=false)
	private Boolean enabled;

	@Column(nullable=false)
	private Boolean deleted;
	
	@Column(nullable=false)
	private Boolean players;

  @Builder.Default
  @Column(nullable=false)
  private Boolean playerDepositLimits = true;

  @Builder.Default
  @Column(nullable=false)
  private Boolean playerTimeSlotLimits = true;

  @Builder.Default
  @Column(nullable=false)
  private Boolean playtimeLimit = true;

  @Builder.Default
  @Column(nullable=false)
  private Boolean playerBalanceLimit = false;

  @URL(message="Invalid URL")
  private String url;

  @URL(message="Invalid support URL")
  private String supportUrl;

  @Pattern(regexp="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$",
  message="Invalid email address")
    private String supportEmail;

  @ManyToOne
  @JoinColumn(name="parent_id")
  private Domain parent;

  @Column()
  private String preSignupAccessRule;

  @Column()
  private String signupAccessRule;

  @Column()
  private String loginAccessRule;

  @Column()
  private String preLoginAccessRule;

  @Column()
  private String userDetailsUpdateAccessRule;

  @Column()
  private String firstDepositAccessRule;

  @Column()
  private String ipblockList;

  @Transient
  private Long superId;

  @Transient
  private String superName;

  @Column(nullable=false)
  private String currency;

  @Column(nullable=false)
  private String currencySymbol;

  @ManyToOne(fetch = FetchType.EAGER, cascade=CascadeType.PERSIST)
  @JoinColumn(name = "physical_address_id")
  private Address physicalAddress;

  @ManyToOne(fetch = FetchType.EAGER, cascade=CascadeType.PERSIST)
  @JoinColumn(name = "postal_address_id")
  private Address postalAddress;

  @ManyToOne(fetch = FetchType.EAGER, cascade=CascadeType.PERSIST)
  @JoinColumn(name = "banking_details_id")
  private BankingDetails bankingDetails;

  @Column(nullable=false)
  private String defaultLocale;

  @Column(nullable=true)
  private String defaultTimezone;

  @Transient
  private String timeout;

  @Column(length = 3)
  private String defaultCountry;

  @Column(nullable=false)
  private Boolean bettingEnabled;
  @ManyToOne(fetch=FetchType.EAGER)
  @JoinColumn()
  @JsonManagedReference
  private DomainRevision current;

  @Column()
  private String failedLoginIpList;

  @PrePersist
  public void prePersist() {
    if (bettingEnabled == null) bettingEnabled = true;
    if (playerDepositLimits == null) playerDepositLimits = true;
    if (playtimeLimit == null) playtimeLimit = false;
    if (playerTimeSlotLimits == null) playerTimeSlotLimits = false;
    if (playerBalanceLimit == null) playerBalanceLimit = false;
  }

}

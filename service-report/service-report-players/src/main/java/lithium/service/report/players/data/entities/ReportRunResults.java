package lithium.service.report.players.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes={
		@Index(name="idx_created", columnList="createdDate"),
		@Index(name="idx_dob", columnList="dateOfBirth"),
		@Index(name="idx_dob_day", columnList="dateOfBirthDay"),
		@Index(name="idx_dob_month", columnList="dateOfBirthMonth"),
		@Index(name="idx_dob_year", columnList="dateOfBirthYear"),
})
@JsonIgnoreProperties(value={"reportRun"})
public class ReportRunResults implements Serializable {

	private static final long serialVersionUID = 1L;

	@Version
	int version;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=true)
	ReportRun reportRun;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue username;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue email;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue firstName;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue lastName;

	Boolean enabled;

	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue status;
	
	Boolean emailValidated;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue residentialAddressLine1;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue residentialAddressLine2;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue residentialAddressLine3;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue residentialAddressCity;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue residentialAddressAdminLevel1;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue residentialAddressCountry;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue residentialAddressPostalCode;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue postalAddressLine1;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue postalAddressLine2;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue postalAddressLine3;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue postalAddressCity;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue postalAddressAdminLevel1;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue postalAddressCountry;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue postalAddressPostalCode;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue telephoneNumber;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue cellphoneNumber;
	
	Date createdDate;
	Date updatedDate;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue signupBonusCode;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue affiliateGuid;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue bannerGuid;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true) 
	StringValue campaignGuid;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(nullable=true)
	StringValue referralCode;
	
	Date dateOfBirth;
	Integer dateOfBirthDay;
	Integer dateOfBirthMonth;
	Integer dateOfBirthYear;
	
	Long currentBalanceCents;
	Long currentBalanceCasinoBonusCents;
	Long currentBalanceCasinoBonusPendingCents;
	Long currentBalancePendingWithdrawalCents;
	
	Long periodOpeningBalanceCents;
	Long periodClosingBalanceCents;
	
	Long periodOpeningBalanceCasinoBonusCents;
	Long periodClosingBalanceCasinoBonusCents;
	
	Long periodOpeningBalanceCasinoBonusPendingCents;
	Long periodClosingBalanceCasinoBonusPendingCents;

	Long periodOpeningBalancePendingWithdrawalCents;
	Long periodClosingBalancePendingWithdrawalCents;
	
	Long depositAmountCents;
	Long depositCount;
	
	Long depositFeeCents;
	
	Long payoutAmountCents;
	Long payoutCount;
	
	Long balanceAdjustAmountCents;
	Long balanceAdjustCount;
	
	Long casinoBetAmountCents;
	Long casinoBetCount;
	Long casinoWinAmountCents;
	Long casinoWinCount;
	Long casinoNetAmountCents;
	
	Long casinoBonusBetAmountCents;
	Long casinoBonusBetCount;
	Long casinoBonusWinAmountCents;
	Long casinoBonusWinCount;
	Long casinoBonusNetAmountCents;
	
	Long casinoBonusPendingAmountCents;
	Long casinoBonusTransferToBonusPendingAmountCents;
	Long casinoBonusTransferFromBonusPendingAmountCents;
	Long casinoBonusPendingCancelAmountCents;
	Long casinoBonusPendingCount;
	
	Long casinoBonusActivateAmountCents;
	Long casinoBonusTransferToBonusAmountCents;
	Long casinoBonusTransferFromBonusAmountCents;
	
	Long casinoBonusCancelAmountCents;
	Long casinoBonusExpireAmountCents;
	Long casinoBonusMaxPayoutExcessAmountCents;

	Long virtualBetAmountCents;
	Long virtualBetCount;
	Long virtualWinAmountCents;
	Long virtualWinCount;
	Long virtualBetVoidAmountCents;
	Long virtualBetVoidCount;
	Long virtualLossCount;

	Long transferToPlayerBalancePendingWithdrawalAmountCents;
	Long transferFromPlayerBalancePendingWithdrawalAmountCents;
	
	Boolean emailOptOut;
	Boolean smsOptOut;
	Boolean callOptOut;
	
	Long userId;

	String userGuid;

	String gamstopStatus;
	
	@PrePersist
	void defaults() {
	}

}

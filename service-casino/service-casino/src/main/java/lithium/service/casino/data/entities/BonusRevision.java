package lithium.service.casino.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude={"dependsOnBonus", "bonus", "bonusExternalGameConfigs"})
@EqualsAndHashCode(exclude={"dependsOnBonus", "bonus"})
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
public class BonusRevision implements Serializable {
	private static final long serialVersionUID = 6502658514781789733L;
	private static SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
	
	public static final int PLAYTHROUGH_TYPE_BONUS_VALUE = 0; 
	public static final int PLAYTHROUGH_TYPE_FREESPIN_WINS = 1;
	
	public static final int BONUS_TYPE_SIGNUP = 0;
	public static final int BONUS_TYPE_DEPOSIT = 1;
	public static final int BONUS_TYPE_TRIGGER = 2;
	public static final int BONUS_TYPE_BONUS_TOKEN = 3;

	public static final int TRIGGER_TYPE_MANUAL = 0;
	public static final int TRIGGER_TYPE_LOGIN = 2;
	public static final int TRIGGER_TYPE_RAF = 3;
	public static final int TRIGGER_TYPE_XP = 4;
	public static final int TRIGGER_TYPE_REWARD = 5;
	public static final int TRIGGER_TYPE_CONSECUTIVE = 6;
	public static final int TRIGGER_TYPE_PRODUCT = 7;
	public static final int TRIGGER_TYPE_LEADERBOARD = 8;
	public static final int TRIGGER_TYPE_HOURLY = 9;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String bonusCode;
	private String bonusName;
	private boolean enabled;
	private Long maxPayout;
	private Integer maxRedeemable;
	private Integer maxRedeemableGranularity;
	private Integer validDays;  //valid for how many days
	private Integer forDepositNumber;
	private Integer playThroughRequiredType;
	private Integer bonusType;//0:signup/1:deposit/2:trigger/3:bonustoken
	private Integer bonusTriggerType; //0:manual/1:deposit/2:login/3:raf/4:xp/5:reward/6:consecutive/7:product/8:leaderboard/9:hourly
	private Long triggerAmount; //deposit/login # ?
	private Integer triggerGranularity; //1:year/2:month/3:day/4:week/5:total/6:hour
	private boolean visibleToPlayer;
	private boolean playerMayCancel;
	private Long cancelOnDepositMinimumAmount;
	private boolean cancelOnBetBiggerThanBalance;
	private boolean deleted;

	private Long freeMoneyAmount;
	private Integer freeMoneyWagerRequirement;

	@JsonBackReference("bonus")
	@ManyToOne
	private Bonus bonus;

	private String activeDays; // only mondays, or mondays and wednesdays. comma separated list from 1 (Monday) to 7 (Sunday).
	@Temporal(TemporalType.TIME)
	private Date activeStartTime;
	@Temporal(TemporalType.TIME)
	private Date activeEndTime;
	private String activeTimezone;

	@ManyToOne
	@JoinColumn
	private Bonus dependsOnBonus;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date startingDate;
	private String startingDateTimezone;
	@Temporal(TemporalType.TIMESTAMP)
	private Date expirationDate;
	private String expirationDateTimezone;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable=false)
	private Domain domain;
	
	private boolean publicView;
	
	@ManyToOne(fetch= FetchType.EAGER)
	@JoinColumn(nullable=true)
	private Graphic graphic;
	
	private String bonusDescription;
	
	@OneToMany(fetch=FetchType.EAGER, mappedBy = "bonusRevision")
	@Fetch(value = FetchMode.SUBSELECT)
	@JsonManagedReference("bonusRevision")
	private List<BonusFreeMoney> bonusFreeMoney;

	@OneToMany(fetch=FetchType.LAZY, mappedBy = "bonusRevision")
	@JsonManagedReference("bonusRevision")
	private List<BonusExternalGameConfig> bonusExternalGameConfigs;
	
	private String activationNotificationName;

	@OneToMany(fetch=FetchType.EAGER, mappedBy = "bonusRevision")
	@Fetch(value = FetchMode.SUBSELECT)
	@JsonManagedReference("bonusRevision")
	private List<BonusToken> bonusTokens;

	public String getDependsOnBonusCode() {
		return (dependsOnBonus!=null)?dependsOnBonus.getCurrent().getBonusCode():"";
	}
	public String getExpirationDateFormatted() {
		return (expirationDate==null)?"":dateFormatter.format(expirationDate);
	}
	public String getStartingDateFormatted() {
		return (startingDate==null)?"":dateFormatter.format(startingDate);
	}
	public String getActiveStartTimeFormatted() {
		return (activeStartTime==null)?"":timeFormatter.format(activeStartTime);
	}
	public String getActiveEndTimeFormatted() {
		return (activeEndTime==null)?"":timeFormatter.format(activeEndTime);
	}
	public String getFreeMoneyAmountFormatted() {
		return (freeMoneyAmount==null)?"":currencyFormatter.format(freeMoneyAmount/100);
	}
}

package lithium.service.casino.client.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BonusRevision {
	
	public static final int PLAYTHROUGH_TYPE_BONUS_VALUE = 0; 
	public static final int PLAYTHROUGH_TYPE_FREESPIN_WINS = 1;
	
	public static final int BONUS_TYPE_SIGNUP = 0;
	public static final int BONUS_TYPE_DEPOSIT = 1;
	public static final int BONUS_TYPE_TRIGGER = 2;

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
	private Integer bonusType;
	private boolean visibleToPlayer;
	private boolean playerMayCancel;
	private Long cancelOnDepositMinimumAmount;
	private boolean cancelOnBetBiggerThanBalance;

	private Long freeMoneyAmount;
	private Integer freeMoneyWagerRequirement;
	private List<BonusFreeMoney> bonusFreeMoney;
	
	private String activationNotificationName;

	private String activeDays; // only mondays, or mondays and wednesdays. comma separated list from 1 (Monday) to 7 (Sunday).

	private Date activeStartTime;

	private Date activeEndTime;
	private String activeTimezone;
	
	private Date startingDate;
	private String startingDateTimezone;

	private Date expirationDate;
	private String expirationDateTimezone;

	private String bonusDescription;
	private Graphic graphic;
}

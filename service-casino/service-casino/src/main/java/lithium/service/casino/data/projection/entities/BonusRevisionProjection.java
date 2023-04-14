package lithium.service.casino.data.projection.entities;

import java.util.Date;

import org.springframework.data.rest.core.config.Projection;

import lithium.service.casino.data.entities.BonusRevision;
import lithium.service.casino.data.entities.Domain;

@Projection(name = "bonusRevisionProjection", types = { BonusRevision.class })
public interface BonusRevisionProjection {
	Long getId();
	
	String getBonusCode();
	String getBonusName();
	boolean isEnabled();
//	Long getMaxPayout();
//	Integer getMaxRedeemable();
//	Integer getMaxRedeemableGranularity();
	Integer getValidDays();  //valid for how many days
//	Integer forDepositNumber;
//	Integer playThroughRequiredType;
//	Integer bonusType;
//	boolean visibleToPlayer;
//	boolean playerMayCancel;
//	Long cancelOnDepositMinimumAmount;
//	boolean cancelOnBetBiggerThanBalance;

//	Long freeMoneyAmount;
//	Integer freeMoneyWagerRequirement;

//	Bonus bonus;

//	String activeDays; // only mondays, or mondays and wednesdays. comma separated list from 1 (Monday) to 7 (Sunday).
//	Date activeStartTime;
//	Date activeEndTime;
//	String activeTimezone;
//	Bonus dependsOnBonus;
	
//	Date startingDate;
//	String startingDateTimezone;
	Date getExpirationDate();
	String getExpirationDateTimezone();
	
	Domain getDomain();
	
}

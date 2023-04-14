package lithium.service.casino.service;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.math.CurrencyAmount;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.casino.client.data.BonusAllocate;
import lithium.service.casino.client.data.BonusAllocatev2;
import lithium.service.casino.client.data.SourceSystem;
import lithium.service.casino.data.entities.Bonus;
import lithium.service.casino.data.entities.BonusRevision;
import lithium.service.casino.data.entities.Domain;
import lithium.service.casino.data.entities.PlayerBonus;
import lithium.service.casino.data.entities.PlayerBonusHistory;
import lithium.service.casino.data.repositories.BonusRepository;
import lithium.service.casino.exceptions.Status411InvalidUserGuidException;
import lithium.service.casino.exceptions.Status412InvalidCustomFreeMoneyAmountException;
import lithium.service.casino.exceptions.Status413NoValidBonusFoundForCodeException;
import lithium.service.casino.exceptions.Status414NoValidBonusRevisionFoundException;
import lithium.service.casino.exceptions.Status415BonusPrerequisitesNotMetException;
import lithium.service.casino.exceptions.Status416BonusUptakeLimitExceededException;
import lithium.service.casino.exceptions.Status417BonusIsNotValidForPlayerException;
import lithium.service.casino.exceptions.Status418FailedToAllocateFreeMoneyException;
import lithium.service.casino.exceptions.Status419FailedToAllocateExternalBonusException;
import lithium.service.casino.exceptions.Status420BonusCompleteCheckException;
import lithium.service.casino.exceptions.Status422InvalidGrantBonusException;
import lithium.service.casino.exceptions.Status422InvalidParameterProvidedException;
import lithium.service.casino.exceptions.Status425FailedToAllocateBonusTokenException;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.stats.client.objects.LabelValue;
import lithium.service.stats.client.objects.Period.Granularity;
import lithium.service.stats.client.objects.StatSummary;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.objects.UserEvent;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class CasinoTriggerBonusService {
	@Autowired CasinoBonusService casinoBonusService;
	@Autowired ChangeLogService changeLogService;
	@Autowired CasinoMailSmsService casinoMailSmsService;
	@Autowired DomainService domainService;
	@Autowired CasinoBonusFreespinService casinoBonusFreespinService;
	@Autowired CasinoBonusInstantRewardService casinoBonusInstantRewardService;
	@Autowired CasinoBonusInstantRewardFreespinService casinoBonusInstantRewardFreespinService;
	@Autowired
	CasinoBonusCasinoChipService casinoBonusCasinoChipService;
	@Autowired CasinoBonusUnlockGamesService casinoBonusUnlockGamesService;
	@Autowired BonusRepository bonusRepository;
	@Autowired BonusService bonusService;
	@Autowired CachingDomainClientService cachingDomainClientService;
	@Autowired UserService userService;
	@Autowired LimitInternalSystemService limitInternalSystemService;
	@Autowired BonusHistoryService bonusHistoryService;

	@TimeThisMethod
	public void processLoginStats(StatSummary ss) throws Exception {
		//Checking for promotions eligibility before proceeding
		limitInternalSystemService.checkPromotionsAllowed(ss.getStat().getOwner().getGuid());

		Domain domain = domainService.findOrCreate(ss.getStat().getDomain().getName());
		List<Bonus> bonusList = bonusRepository.findByCurrentBonusTypeAndCurrentDomainNameAndCurrentEnabledTrueAndCurrentBonusTriggerTypeAndCurrentTriggerAmountAndCurrentTriggerGranularity(2, domain.getName(), 2, ss.getCount(), ss.getPeriod().getGranularity());

		if (bonusList.isEmpty()) log.trace("No login bonus found for login #"+ss.getCount()+" of the "+Granularity.fromGranularity(ss.getPeriod().getGranularity()).type());

//		if (ss.getPeriod().getGranularity() == Period.GRANULARITY_DAY) {
		LabelValue labelValue = null;
		if (ss.getLabelValues() != null) {
			labelValue = ss.getLabelValues().stream()
					.filter(lv -> "consecutive-logins".equalsIgnoreCase(lv.getLabel().getName()))
					.findAny()
					.orElse(null);
		}
		if (labelValue != null) {
			Long value = Long.valueOf(labelValue.getValue());
			List<Bonus> consecutiveBonusList = bonusRepository.findByCurrentBonusTypeAndCurrentDomainNameAndCurrentEnabledTrueAndCurrentBonusTriggerTypeAndCurrentTriggerAmountAndCurrentTriggerGranularity(2, domain.getName(), 6, value, ss.getPeriod().getGranularity());
			if (consecutiveBonusList.isEmpty()) {
				log.trace("No consecutive bonus found for #"+value+" of the "+Granularity.fromGranularity(ss.getPeriod().getGranularity()).type());
				log.trace("Searching for previous consecutive bonusses.");

				while (--value > 0) {
					consecutiveBonusList = bonusRepository.findByCurrentBonusTypeAndCurrentDomainNameAndCurrentEnabledTrueAndCurrentBonusTriggerTypeAndCurrentTriggerAmountAndCurrentTriggerGranularity(2, domain.getName(), 6, value, ss.getPeriod().getGranularity());
					log.trace("#"+value+" :: "+consecutiveBonusList);
					if (!consecutiveBonusList.isEmpty()) break;
				}
			}
			bonusList.addAll(consecutiveBonusList);
		}
		if (!bonusList.isEmpty()) {
			log.trace("processLoginStats bonus list empty, no bonusses found.");
		}
		for (Bonus bonus:bonusList) {
			log.trace("Found Consecutive Bonus: "+bonus);
			try {
				triggerBonus(bonus, ss.getStat().getOwner().guid(), domain.getName());
			} catch (Exception e) {
				log.debug("Could not trigger bonus: "+bonus.getCurrent().getBonusCode()+", domain: "+domain.getName()+" for StatSummary: "+ss.toShortString(), e);
			}
		}
	}

	@TimeThisMethod
	public void processTriggerBonus(BonusAllocate bonusAllocate) throws Exception {
		log.trace("Processing trigger bonus: " + bonusAllocate.toString());
		String[] domainAndPlayer = bonusAllocate.getPlayerGuid().split("/");
		Bonus bonus = bonusRepository.findByCurrentBonusCodeAndCurrentDomainNameAndCurrentBonusTypeAndCurrentEnabledTrue(
				bonusAllocate.getBonusCode(),
				domainAndPlayer[0],
				BonusRevision.BONUS_TYPE_TRIGGER
		);
		if (bonus == null) {
			log.trace("No bonus found");
		} else {
			log.trace("Found: " + bonus);
			try {
				triggerBonus(bonus, bonusAllocate.getPlayerGuid());
			} catch (Exception e) {
				log.debug("Could not trigger bonus: "+bonusAllocate.toString(), e);
			}
		}
	}

	@TimeThisMethod
	public void processTriggerBonusOptIn(String bonusCode, LithiumTokenUtil tokenUtil) throws Exception {
		log.debug("Processing trigger bonus: " + bonusCode + ", userGuid: " + tokenUtil.guid());
		Bonus bonus = bonusRepository.findByCurrentBonusCodeAndCurrentDomainNameAndCurrentBonusTypeAndCurrentEnabledTrue(bonusCode,
				tokenUtil.domainName(),
				BonusRevision.BONUS_TYPE_TRIGGER
		);
		if (bonus == null) {
			log.debug("No bonus found for bonusCode: " + bonusCode + ", userGuid: " + tokenUtil.guid());
			throw new Status413NoValidBonusFoundForCodeException(bonusCode);
		} else {
			log.debug("Found: bonusCode" +  bonusCode + ", userGuid: " + tokenUtil.guid());
			triggerBonus(bonus, tokenUtil.guid());
		}
	}

	@TimeThisMethod
	public boolean isTriggerBonusOptIn(String bonusCode, String playerGuid) throws Status413NoValidBonusFoundForCodeException {
		log.debug("Check if trigger bonus exist for bonusCode: " + bonusCode + " on userGuid: " + playerGuid);
		Bonus bonus = bonusRepository.findByCurrentBonusCodeAndCurrentDomainNameAndCurrentBonusTypeAndCurrentEnabledTrue(bonusCode,
				playerGuid.split("/")[0],
				BonusRevision.BONUS_TYPE_TRIGGER
		);
		if (bonus == null) {
			log.debug("No bonus found for bonusCode: " + bonusCode + ", userGuid: " + playerGuid);
			throw new Status413NoValidBonusFoundForCodeException(bonusCode);
		}

		return bonusHistoryService.bonusCodeExistOnPlayerGuid(bonusCode, playerGuid);
	}

	private void triggerBonus(Bonus bonus, String playerGuid) throws Exception {
		String[] domainAndPlayer = playerGuid.split("/");
		if (domainAndPlayer.length != 2) {
			log.debug("Invalid playerGuid : "+playerGuid+" could not trigger bonus : "+bonus);
		} else {
			triggerBonus(bonus, playerGuid, domainAndPlayer[0]);
		}
	}

	private void triggerBonus(Bonus bonus, String playerGuid, String domainName) throws Exception {
		triggerBonusv2(bonus, playerGuid, domainName, null, null, null, null, null, null, null, null, null,null);
	}

	private void triggerBonusv2(Bonus bonus, String playerGuid, String domainName, Double customAmount, Integer customAmountNotMoney, Long bonusRevisionId, String description, Long requestId, String clientId, Long sessionId, String noteText, SourceSystem sourceSystem, LithiumTokenUtil tokenUtil
	) throws Status414NoValidBonusRevisionFoundException,
			Status415BonusPrerequisitesNotMetException,
			Status416BonusUptakeLimitExceededException,
			Status417BonusIsNotValidForPlayerException,
			Status419FailedToAllocateExternalBonusException,
			Status418FailedToAllocateFreeMoneyException,
			Status420BonusCompleteCheckException,
			Status425FailedToAllocateBonusTokenException, Status422InvalidGrantBonusException, Status550ServiceDomainClientException {
		log.debug("Allocating "+bonus+" to "+playerGuid);
		log.trace("Allocating "+bonus.getCurrent().getBonusCode()+" to "+playerGuid);

		TriggerType triggerType = TriggerType.TRIGGER_MANUAL;
		if (bonus.getCurrent().getBonusTriggerType() != null) {
			triggerType = TriggerType.fromType(bonus.getCurrent().getBonusTriggerType());
		}

		BonusRevision bonusRevision = bonus.getCurrent();
		if (bonusRevision == null) {
			log.debug("No bonus revision found on bonus: " + bonus);
			throw new Status414NoValidBonusRevisionFoundException("Bonus id: " + bonus.getId());
		}

		if (casinoBonusService.bonusValidForPlayer(bonus, playerGuid)) {
			log.debug("Bonus is Valid For Player: " + bonus + " player: " + playerGuid);
			Bonus dependsOnBonus = bonusRevision.getDependsOnBonus();
			if (dependsOnBonus != null) {
				log.debug("This bonus is dependant on another bonus.: " + bonus);
				SW.start("listPlayerParentBonusHistory");
				List<PlayerBonusHistory> playerParentBonusHistory = casinoBonusService.findPlayerBonusHistory(playerGuid, dependsOnBonus.getCurrent().getBonusCode());
				SW.stop();
				if (playerParentBonusHistory.size() == 0) {
					log.trace("Bonus code prerequisites not completed yet. ("+playerGuid+") :: "+bonus);
					throw new Status415BonusPrerequisitesNotMetException(dependsOnBonus.getCurrent().getBonusName());
				}
			}

			SW.start("checkMaxRedeemableValid");
			if (!casinoBonusService.checkMaxRedeemableValid(bonusRevision, playerGuid)) {
				log.trace("Bonus code usage exceeded for player. ("+playerGuid+") :: "+bonus);
				throw new Status416BonusUptakeLimitExceededException(bonusRevision.getMaxRedeemable().toString());
			}
			SW.stop();

			SW.start("activatePlayerBonus");
			activatePlayerBonus(bonus, triggerType, playerGuid, domainName, customAmount, customAmountNotMoney, bonusRevisionId, description, requestId, sessionId, clientId, noteText, sourceSystem, tokenUtil);
			SW.stop();
		} else {
			//This error is way too generic, but it will do for now
			throw new Status417BonusIsNotValidForPlayerException("The bonus is not valid for the player");
		}
	}

	//TODO: Accounting transactions needs to rollback ...
	// @Retryable(maxAttempts=5,backoff=@Backoff(delay=10))
	// @Transactional(rollbackFor=Exception.class)
	private void activatePlayerBonus(Bonus bonus, TriggerType triggerType, String playerGuid,
									 String domainName, Double customFreeMoneyAmount, Integer customAmountNotMoney, Long bonusRevisionId,
									 String description, Long requestId, Long sessionId, String clientId, String noteText, SourceSystem sourceSystem, LithiumTokenUtil tokenUtil
	) throws Status418FailedToAllocateFreeMoneyException,
			Status419FailedToAllocateExternalBonusException,
			Status420BonusCompleteCheckException, Status425FailedToAllocateBonusTokenException, Status422InvalidGrantBonusException, Status550ServiceDomainClientException {
		//Lookup correct bonus revision for historical bonus allocations
		BonusRevision bonusRevisionToUse = bonus.getCurrent();
		if (bonusRevisionId != null && bonusRevisionId > 0L) {
			SW.start("fetchBonusRevisionById");
			BonusRevision br = casinoBonusService.findBonusRevisionById(bonusRevisionId);
			SW.stop();
			if (br != null) {
				bonusRevisionToUse = br;
			}
		}
		// #chrisfix
		SW.start("fetchPlayerActiveBonus");
		PlayerBonus currentActiveBonus = casinoBonusService.findCurrentBonus(playerGuid);
		SW.stop();
		boolean isCustomAmountUsed = (customFreeMoneyAmount != null && !customFreeMoneyAmount.isNaN());
		CurrencyAmount customAmountCurrency = null;
		if (isCustomAmountUsed) {
			customAmountCurrency = CurrencyAmount.fromAmount(customFreeMoneyAmount);
		}
		log.debug("currentActiveBonus : "+currentActiveBonus);
		if (currentActiveBonus != null && triggerType == TriggerType.TRIGGER_RAF) {
			SW.start("savePlayerBonusPending");
			casinoBonusService.savePlayerBonusPending(bonusRevisionToUse.getBonus(), 0L, 0L, 0, playerGuid, 0L, 0L, bonusRevisionId);
			SW.stop();
			return;
		}

		if (triggerType == TriggerType.TRIGGER_MANUAL) {
			if (currentActiveBonus != null &&
					(bonusRevisionToUse.getFreeMoneyWagerRequirement() != null &&
							bonusRevisionToUse.getFreeMoneyWagerRequirement() > 0)) {
				SW.start("savePlayerBonusPending");
				casinoBonusService.savePlayerBonusPending(
						bonusRevisionToUse.getBonus(),
						0L,
						0L,
						0,
						playerGuid,
						0L,
						isCustomAmountUsed ? customAmountCurrency.toCents() : null,
						bonusRevisionId);
				SW.stop();
				return;
			}
		}
		//Since we are in the trigger bonus service, all bonuses should be instant
		boolean instantBonus = true;
		//RAF bonus, we treat it as non-instant. I am starting to see why we should just do the wager req check and deal with the fallout of incorrect setup.
		// TODO: 2019/09/17 Maybe fix this to just look at wager req and mail support if some weird trigger was required.
		if (triggerType == TriggerType.TRIGGER_RAF) {
			if (bonusRevisionToUse.getFreeMoneyWagerRequirement() > 0) {
				instantBonus = false;
			}
		}
		if (bonusRevisionToUse.getFreeMoneyWagerRequirement() != null &&
				bonusRevisionToUse.getFreeMoneyWagerRequirement() > 0) {
			if (bonusRevisionToUse.getFreeMoneyWagerRequirement() > 0) {
				instantBonus = false;
			}
		}
		PlayerBonusHistory pbh = null;
		if (isCustomAmountUsed) {
			SW.start("savePlayerBonusHistory");
			pbh = casinoBonusService.savePlayerBonusHistory(bonusRevisionToUse, instantBonus, customAmountCurrency.toCents(), description, requestId, sessionId, clientId, noteText);
			SW.stop();
		} else {
			SW.start("savePlayerBonusHistory");
			pbh = casinoBonusService.savePlayerBonusHistory(bonusRevisionToUse, instantBonus, description, requestId, sessionId, clientId, noteText);
			SW.stop();
		}

		SW.start("updatePlayerBonusCurrent");
		PlayerBonus pb = casinoBonusService.updatePlayerBonusCurrent(pbh, playerGuid, instantBonus);
		SW.stop();
		SW.start("updatePlayerBonusHistory");
		pbh = casinoBonusService.updatePlayerBonusHistory(pbh.getId(), pb, 0L);
		SW.stop();

		log.debug("PlayerBonus : "+pb);
		log.debug("PlayerBonusHistory : "+pbh);
		List<ChangeLogFieldChange> clfc = null;
		String defaultDomainCurrency = cachingDomainClientService.retrieveDomainFromDomainService(domainName).getCurrencySymbol();
		String pbhDescription = pbh.getDescription() != null && ! pbh.getDescription().isEmpty() ? " (" + pbh.getDescription() + ")" : "";
		String pbhNoteText = pbh.getNoteText() != null && ! pbh.getNoteText().isEmpty() ? " " + pbh.getNoteText() : "";
		String text;
		if (tokenUtil != null && bonusService.isCashBonus(domainName, pbh.getBonus().getBonusCode())) {
			try {
				BigDecimal cashAmount = new BigDecimal(0.00);
				if (pbh.getCustomFreeMoneyAmountCents() != null) {
					cashAmount = new BigDecimal(pbh.getCustomFreeMoneyAmountCents()).movePointLeft(2);
				}
				text = "Cash Bonus was granted: " + defaultDomainCurrency + " " + cashAmount + " - " + pbh.getBonus().getBonusCode()
						+ pbhDescription + pbhNoteText;
				clfc = changeLogService.copy(pbh, new PlayerBonusHistory(), new String[]{"startedDate", "bonus", "description", "noteText"});
				SW.start("fetchUser");
				User user = userService.findUserByGuid(playerGuid);
				SW.stop();
				SW.start("registerChangesForNotesWithFullNameAndDomain");
				changeLogService.registerChangesForNotesWithFullNameAndDomain("user.bonus", "create", user.getId(), tokenUtil.guid(), tokenUtil, text, "cashbonus", clfc, Category.BONUSES, SubCategory.BONUS_GRANT, 10, user.getDomain().getName());
				SW.stop();
			} catch (UserClientServiceFactoryException | Exception ex) {
				log.warn("Problem adding changelog on cash bonus: PlayerBonusHistory -> {}, exception -> {}", pbh, ex);
			}
		} else {
			try {
				text = "Bonus was granted: " + pbh.getBonus().getBonusCode() + pbhDescription + pbhNoteText;
				String author = "default/system";
				SW.start("fetchUser");
				User user = userService.findUserByGuid(playerGuid);
				SW.stop();
				clfc = changeLogService.copy(pbh, new PlayerBonusHistory(), new String[]{"startedDate", "bonus", "description","noteText","clientId", "sessionId"});
				if ((sourceSystem!=null) && (sourceSystem.equals(SourceSystem.SERVICE_USER_MASS_ACTION))) {
					try {
						SW.start("registerChangesForNotesWithFullNameAndDomain");
						changeLogService.registerChangesForNotesWithFullNameAndDomain("user.bonus", "create", user.getId(), author, tokenUtil, text, "cashbonus", clfc, Category.BONUSES, SubCategory.BONUS_GRANT, 10, user.getDomain().getName());
						SW.stop();
					} catch (Exception ex) {
						log.warn("Problem adding changelog on cash bonus: PlayerBonusHistory -> {}, exception -> {}", pbh, ex);
					}
				} else {
					//TODO: This is a hack to make reward trigger bonus work for Squads.
					String tokenUtilGuid = (tokenUtil==null)?"system":tokenUtil.guid();
					try {
						SW.start("registerChangesWithDomain");
						changeLogService.registerChangesWithDomain("user.bonus", "create", user.getId(), tokenUtilGuid, text, null, clfc, Category.BONUSES, SubCategory.BONUS_GRANT, 0, playerGuid.substring(0, playerGuid.indexOf('/')));
						SW.stop();
					} catch (Exception ex) {
						log.warn("Problem adding changelog on cash bonus: PlayerBonusHistory -> {}, exception -> {}", pbh, ex);
					}
				}
			} catch (UserClientServiceFactoryException |Exception ex) {
				log.warn("Problem adding changelog on trigger bonus: PlayerBonusHistory -> {}, exception -> {}", pbh, ex);
			}

		}

//		casinoBonusService.triggerOnDeposit(pb);
		try {
			SW.start("triggerFreeSpins");
			casinoBonusFreespinService.triggerFreeSpins(pbh, instantBonus, customAmountNotMoney);
			SW.stop();
		} catch (Status422InvalidGrantBonusException e) {
			SW.start("removeFailedPlayerBonusHistory");
			casinoBonusService.removeFailedPlayerBonusHistory(pbh);
			SW.stop();
			log.error("Could not grant bonus {}", pbh, e);
			throw e;
		}
		log.debug("Triggered FreeSpins: " + pbh);

		try{
			SW.start("triggerInstantRewards");
			casinoBonusInstantRewardService.triggerInstantRewards(pbh, instantBonus, customAmountNotMoney);
			SW.stop();
		} catch (Status422InvalidGrantBonusException e) {
			SW.start("removeFailedPlayerBonusHistory");
			casinoBonusService.removeFailedPlayerBonusHistory(pbh);
			SW.stop();
			log.error("Could not grant bonus {}", pbh, e);
			throw e;
		}
		log.debug("Triggered InstantRewards: " + pbh);

		try{
			SW.start("triggerCasinoChips");
			casinoBonusCasinoChipService.triggerCasinoChips(pbh, instantBonus);
			SW.stop();
		} catch (Status422InvalidGrantBonusException e) {
			SW.start("removeFailedPlayerBonusHistory");
			casinoBonusService.removeFailedPlayerBonusHistory(pbh);
			SW.stop();
			log.error("Could not grant bonus {}", pbh, e);
			throw e;
		}
		log.debug("Triggered CasinoChip: " + pbh);

		try{
			SW.start("triggerInstantRewardFreespins");
			casinoBonusInstantRewardFreespinService.triggerInstantRewardFreespins(pbh, instantBonus, customAmountNotMoney);
			SW.stop();
		} catch (Status422InvalidGrantBonusException e) {
			SW.start("removeFailedPlayerBonusHistory");
			casinoBonusService.removeFailedPlayerBonusHistory(pbh);
			SW.stop();
			log.error("Could not grant bonus {}", pbh, e);
			throw e;
		}
		log.debug("Triggered InstantRewards: " + pbh);

		SW.start("triggerUnlockGames");
		casinoBonusUnlockGamesService.triggerUnlockGames(pbh);
		SW.stop();
		log.debug("Triggered UnlockGames: " + pbh);
		try {
			SW.start("triggerFreeMoney");
			casinoBonusService.triggerFreeMoney(pbh, instantBonus);
			SW.stop();
		} catch (Exception e) {
			log.error("Problem adding free money to player trigger bonus: " + pbh, e);
			throw new Status418FailedToAllocateFreeMoneyException(e.getMessage());
		}
		try {
			SW.start("triggerAdditionalFreeMoney");
			casinoBonusService.triggerAdditionalFreeMoney(pbh, instantBonus);
			SW.stop();
		} catch (Exception e) {
			log.error("Problem adding additional free money to player trigger bonus: " + pbh, e);
			throw new Status418FailedToAllocateFreeMoneyException(e.getMessage());
		}
		try {
			SW.start("triggerExternalBonusGame");
			instantBonus = casinoBonusService.triggerExternalBonusGame(pbh);
			SW.stop();
		} catch (Exception e) {
			log.error("Problem adding external bonus to player trigger bonus: " + pbh, e);
			throw new Status419FailedToAllocateExternalBonusException(e.getMessage());
		}
		try {
			SW.start("triggerBonusTokenAllocation");
			casinoBonusService.triggerBonusTokenAllocation(pbh);
			SW.stop();
		} catch (Exception e) {
			log.error("Problem adding bonus token to player instant bonus: " + pbh, e);
			throw new Status425FailedToAllocateBonusTokenException(e.getMessage());
		}
		log.debug("Triggered Free Money: " + pbh);

		String activationNotificationName = bonusRevisionToUse.getActivationNotificationName();
		if (activationNotificationName != null && !activationNotificationName.isEmpty()) {
			SW.start("streamBonusNotification");
			casinoBonusService.streamBonusNotification(playerGuid, activationNotificationName);
			SW.stop();
		}

		try {
			SW.start("registerUserEventPlayerBonus");
			UserEvent userEventPlayerBonus = casinoBonusService.registerUserEventPlayerBonus(domainName, playerGuid.split("/")[1], casinoBonusService.playerBonusDisplay(pb.getPlayerGuid(), pbh));
			SW.stop();
			log.debug("User event player bonus (" + userEventPlayerBonus + ")");
		} catch (Exception e) {
			log.error("Failed to register user event for player bonus (" + pb + "), " + e.getMessage(), e);
		}

		try {
			SW.start("sendBonusMail");
			casinoMailSmsService.sendBonusMail(CasinoMailSmsService.BONUS_STATE_ACTIVATE, pbh, null);
			SW.stop();
		} catch (Exception e) {
			log.error("Failed to send bonus activate email " + pb, e);
		}

		try {
			SW.start("sendBonusSms");
			casinoMailSmsService.sendBonusSms(CasinoMailSmsService.BONUS_STATE_ACTIVATE, pbh, null);
			SW.stop();
		} catch (Exception e) {
			log.error("Failed to send bonus activate sms " + pb, e);
		}

		try {
			boolean isComplete = (instantBonus) ? true : casinoBonusService.isBonusCompleted(pb); // Instant bonus is instantly completed <-- CasinoBonusService.java ln 623
			log.debug("Checking complete : "+isComplete + " pbh: " + pbh);
		} catch (Exception e) {
			log.error("Player trigger bonus complete check failed: " + e.getMessage(), e);
			throw new Status420BonusCompleteCheckException(e.getMessage());
		}
	}

	@TimeThisMethod
	public void processTriggerOrTokenBonusWithCustomMoney(
			BonusAllocatev2 bonusAllocatev2,
			int bonusType,
			LithiumTokenUtil tokenUtil
	) throws
			Status411InvalidUserGuidException,
			Status412InvalidCustomFreeMoneyAmountException,
			Status413NoValidBonusFoundForCodeException,
			Status422InvalidParameterProvidedException,
			Status415BonusPrerequisitesNotMetException,
			Status425FailedToAllocateBonusTokenException,
			Status420BonusCompleteCheckException,
			Status414NoValidBonusRevisionFoundException,
			Status416BonusUptakeLimitExceededException,
			Status419FailedToAllocateExternalBonusException,
			Status417BonusIsNotValidForPlayerException,
			Status418FailedToAllocateFreeMoneyException, Status422InvalidGrantBonusException {

		log.trace("Processing trigger bonus with custom free money: " + bonusAllocatev2.toString());
		String[] domainAndPlayer = bonusAllocatev2.getPlayerGuid().split("/");
		if (domainAndPlayer.length != 2) {
			log.debug("Invalid user guid supplied for trigger bonus: " + bonusAllocatev2.getPlayerGuid());
			throw new Status411InvalidUserGuidException(bonusAllocatev2.getPlayerGuid());
		}


		if (bonusAllocatev2.getCustomAmountDecimal() != null &&
				bonusAllocatev2.getCustomAmountDecimal().isNaN()) {
			log.debug("Invalid custom free money amount supplied for trigger bonus: " + bonusAllocatev2.getCustomAmountDecimal());
			throw new Status412InvalidCustomFreeMoneyAmountException(
					bonusAllocatev2.getCustomAmountDecimal().toString());
		}

		SW.start("fetchCurrentBonusByCode");
		Bonus bonus = bonusRepository.findByCurrentBonusCodeAndCurrentDomainNameAndCurrentBonusTypeAndCurrentEnabledTrue(
				bonusAllocatev2.getBonusCode(),
				domainAndPlayer[0],
				bonusType
		);
		SW.stop();

		if (bonus == null) {
			log.debug("No bonus found for code: " + bonusAllocatev2.getBonusCode());
			throw new Status413NoValidBonusFoundForCodeException(bonusAllocatev2.getBonusCode());
		} else {
			log.debug("Found bonus: " + bonus);
			try {
				triggerBonusv2(bonus,
						bonusAllocatev2.getPlayerGuid(),
						domainAndPlayer[0],
						bonusAllocatev2.getCustomAmountDecimal(),
						bonusAllocatev2.getCustomAmountNotMoney(),
						bonusAllocatev2.getBonusRevisionId(),
						bonusAllocatev2.getDescription(),
						bonusAllocatev2.getRequestId(),
						bonusAllocatev2.getClientId(),
						bonusAllocatev2.getSessionId(),
						bonusAllocatev2.getNoteText(),
						bonusAllocatev2.getSourceSystem(),
						tokenUtil);
			} catch (Status414NoValidBonusRevisionFoundException |
					 Status415BonusPrerequisitesNotMetException |
					 Status416BonusUptakeLimitExceededException |
					 Status417BonusIsNotValidForPlayerException |
					 Status419FailedToAllocateExternalBonusException |
					 Status418FailedToAllocateFreeMoneyException |
					 Status420BonusCompleteCheckException |
					 Status422InvalidGrantBonusException |
					 Status425FailedToAllocateBonusTokenException errorCodeException
			) {
				throw errorCodeException;
			} catch (Exception e) {
				log.debug("Could not trigger bonus: " + bonusAllocatev2.toString(), e);
			}
		}
	}
}

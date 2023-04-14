package lithium.service.xp.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.accounting.client.AccountingClient;
import lithium.service.accounting.client.AccountingDomainCurrencyClient;
import lithium.service.accounting.client.stream.event.ICompletedTransactionProcessor;
import lithium.service.accounting.objects.AdjustmentTransaction;
import lithium.service.accounting.objects.CompleteTransaction;
import lithium.service.accounting.objects.DomainCurrency;
import lithium.service.casino.CasinoTranType;
import lithium.service.casino.CasinoTransactionLabels;
import lithium.service.casino.client.data.BonusAllocate;
import lithium.service.casino.client.stream.TriggerBonusStream;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.gateway.client.stream.GatewayExchangeStream;
import lithium.service.notifications.client.objects.InboxMessagePlaceholderReplacement;
import lithium.service.notifications.client.objects.UserNotification;
import lithium.service.notifications.client.stream.NotificationStream;
import lithium.service.user.client.objects.User;
import lithium.service.xp.data.entities.Level;
import lithium.service.xp.data.entities.LevelBonus;
import lithium.service.xp.data.entities.LevelNotification;
import lithium.service.xp.data.entities.Scheme;
import lithium.service.xp.messagehandlers.api.IXPMeter;
import lithium.service.xp.messagehandlers.api.IXPMeterResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static lithium.service.client.objects.placeholders.PlaceholderBuilder.XP_BONUS_CODE;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.XP_IS_MILESTONE;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.XP_PLAYING_TO_LEVEL;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.XP_PROGRESS;

@Slf4j
@Service
public class XPService implements ICompletedTransactionProcessor {
	@Autowired LithiumServiceClientFactory services;
	@Autowired TriggerBonusStream triggerBonusStream;
	@Autowired GatewayExchangeStream gatewayExchangeStream;
	@Autowired SchemeService schemeService;
	@Autowired CachingDomainClientService cachingDomainClientService;
	@Autowired ReferralService referralService;
	@Autowired NotificationStream notificationStream;

	private static final String CURRENCY_CODE_XP = "XP";

	private AccountingDomainCurrencyClient getAccountingDomainCurrencyClient() {
		AccountingDomainCurrencyClient client = null;
		try {
			client = services.target(AccountingDomainCurrencyClient.class, "service-accounting-provider-internal", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error(e.getMessage(), e);
		}
		return client;
	}

	private AccountingClient getAccountingService() {
		AccountingClient client = null;
		try {
			client = services.target(AccountingClient.class, "service-accounting-provider-internal", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error(e.getMessage(), e);
		}
		return client;
	}

	public Scheme getActiveScheme(String domainName) {
		Scheme scheme = null;
		try {
			scheme = schemeService.findActiveScheme(domainName);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return scheme;
	}

	private boolean xpDomainCurrencyExists(String domainName) {
		boolean found = false;
		List<DomainCurrency> currencies = new ArrayList<>();
		try {
			currencies = getAccountingDomainCurrencyClient().list(domainName).getData();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		for (DomainCurrency domainCurrency: currencies) {
			if (domainCurrency.getCurrency().getCode().equalsIgnoreCase("XP")) {
				found = true;
				break;
			}
		}
		return found;
	}

	private void processXp(String domainName, String userGuid, Long betAccTranId, Long betAmountCents) {
		if (xpDomainCurrencyExists(domainName)) {
			Scheme scheme = getActiveScheme(domainName);

			if (scheme != null) {
				List<String> labelList = new ArrayList<>();
				labelList.add(CasinoTransactionLabels.BET_ACCOUNTING_TRANSACTION_ID+"="+betAccTranId);
				labelList.add(CasinoTransactionLabels.XP_SCHEME_ID+"="+scheme.getId());
				Long xpGainCents = (scheme.getWagerPercentage() != null)
					? new BigDecimal(betAmountCents)
						.multiply(new BigDecimal(scheme.getWagerPercentage())
						.divide(new BigDecimal(100)))
						.longValue()
					: betAmountCents;
				Response<AdjustmentTransaction> accTran = null;
				try {
					accTran = getAccountingService().adjust(
						Math.abs(xpGainCents),
						new DateTime().toDateTimeISO().toString(),
						CasinoTranType.CASINO_XP_GAIN.toString(),
						CasinoTranType.CASINO_XP_GAIN.toString(),
						CasinoTranType.CASINO_XP_GAIN.toString(),
						labelList.toArray(new String[labelList.size()]), 
						CURRENCY_CODE_XP, 
						domainName, 
						userGuid,
						User.SYSTEM_GUID,
						false
					);
				} catch (Exception e) {
					log.error("Could not process xp gain: userGuid " + userGuid + " betAmountCents: " + betAmountCents + " betAccTranId: " + betAccTranId, e);
				}
				if (accTran != null && accTran.getStatus() == Status.OK && accTran.getData().getStatus() != AdjustmentTransaction.AdjustmentResponseStatus.ERROR) {
					if (!scheme.getLevels().isEmpty()) {
						try {
							Long pb = xp(domainName, userGuid);
							Long pbBefore = pb - xpGainCents;
							List<Level> levels = scheme.getLevels().stream().sorted(Comparator.comparing(Level::getNumber)).collect(Collectors.toList());
							Level playingToLevel = null;
							for (Level level: levels) {
								if (pbBefore < level.getRequiredXp() && pb >= level.getRequiredXp()) {
									if (level.getBonus() != null && level.getBonus().getBonusCode() != null && !level.getBonus().getBonusCode().isEmpty()) {
										try {
											triggerBonusStream.process(BonusAllocate.builder()
											.playerGuid(userGuid)
											.bonusCode(level.getBonus().getBonusCode())
											.build());
										} catch (Exception e) {
											log.error("Could not trigger bonus", e);
										}
									}
									//TODO: Promo integration
									//TODO: Should come through as completed tran event from accounting>?
//									missionService.streamStat(userGuid, Type.TYPE_XP, Action.ACTION_LEVEL, null, null);


									try {
										referralService.triggerRAFConversion(userGuid, level.getNumber());
										log.debug("Queued XP level trigger for RAF conversion | playerGuid: " + userGuid + ", xpLevel: " + level.getNumber());
									} catch (Exception e) {
										log.error("Could not queue XP level trigger for RAF conversion | playerGuid: " + userGuid + ", xpLevel: " + level.getNumber());
									}
								}
								if (pb < level.getRequiredXp()) {
									playingToLevel = level;
									break;
								}
							}
							if (playingToLevel != null && playingToLevel.getNotifications() != null && !playingToLevel.getNotifications().isEmpty()) {
								final Level finalPlayerLevel = playingToLevel;
								Long sumOfRequiredXpBeforeLevel = levels.stream()
								.filter(level -> level.getNumber() < finalPlayerLevel.getNumber())
								.collect(Collectors.summingLong(Level::getRequiredXp));

								BigDecimal pbPercBeforeBetOnThisLevel = BigDecimal.ZERO;
								if ((pbBefore - sumOfRequiredXpBeforeLevel) > 0) {
									pbPercBeforeBetOnThisLevel = new BigDecimal(pbBefore - sumOfRequiredXpBeforeLevel)
									.divide(new BigDecimal(playingToLevel.getRequiredXp() - sumOfRequiredXpBeforeLevel), 4, RoundingMode.CEILING)
									.multiply(new BigDecimal(100))
									.setScale(2, RoundingMode.CEILING);
								}

								BigDecimal pbPercAfter = new BigDecimal(pb - sumOfRequiredXpBeforeLevel)
								.divide(new BigDecimal(playingToLevel.getRequiredXp() - sumOfRequiredXpBeforeLevel), 4, RoundingMode.CEILING)
								.multiply(new BigDecimal(100))
								.setScale(2, RoundingMode.CEILING);

								LevelNotification levelNotification = null;
								for (LevelNotification notification: playingToLevel.getNotifications()) {
									if (pbPercBeforeBetOnThisLevel.longValue() < notification.getTriggerPercentage() &&
										pbPercAfter.longValue() >= notification.getTriggerPercentage()) {
											levelNotification = notification;
									}
								}

								if (levelNotification != null) {
									sendNotification(userGuid, levelNotification.getNotificationName(), playingToLevel, pbPercAfter.longValue());
								}
							}
							streamPlayerXP(userGuid, scheme, pb);
						} catch (Exception e) {
							log.error("Could not process xp scheme levels", e);
						}
					}
				}
			}
		}
	}

	private void sendNotification(String userGuid, String notificationName, Level playingToLevel, Long progress) {
		log.debug("sendNotification [userGuid="+userGuid+", notificationName="+notificationName+"]");
		List<InboxMessagePlaceholderReplacement> phReplacements = new ArrayList<>();
		phReplacements.add(InboxMessagePlaceholderReplacement.fromPlaceholder(XP_PLAYING_TO_LEVEL.from(String.valueOf(playingToLevel.getNumber()))));
		phReplacements.add(InboxMessagePlaceholderReplacement.fromPlaceholder(XP_PROGRESS.from(String.valueOf(progress))));
		phReplacements.add(InboxMessagePlaceholderReplacement.fromPlaceholder(XP_BONUS_CODE.from(Optional.ofNullable(playingToLevel.getBonus()).map(LevelBonus::getBonusCode).orElse("N/A"))));
		phReplacements.add(InboxMessagePlaceholderReplacement.fromPlaceholder(XP_IS_MILESTONE.from(BooleanUtils.isNotTrue(playingToLevel.getMilestone()) ? "Yes" : "No")));
		notificationStream.process(
			UserNotification.builder()
			.userGuid(userGuid)
			.notificationName(notificationName)
			.phReplacements(phReplacements)
			.build()
		);
	}

	public Long xp(String domainName, String playerGuid) throws Exception {

		AccountingClient  accountingClient = getAccountingService();
		Response<Long> response = accountingClient.get(CURRENCY_CODE_XP, domainName, playerGuid);
		Long pb = response.getData();

		return pb;
	}

	public void streamPlayerXP(String userGuid, Scheme scheme, Long pb) {
		try {
			Level currentLevel = null;
			Level nextLevel = null;
			List<Level> sortedLevels = scheme.getLevels().stream()
			.sorted((l1, l2) -> Integer.compare(l1.getNumber(), l2.getNumber()))
			.collect(Collectors.toList());
			for (Level l: sortedLevels) {
				if (pb >= l.getRequiredXp()) currentLevel = l;
				else break;
			}
			Integer currentLevelNumber = 0;
			boolean currentLevelIsMilestone = false;
			Long currentLevelPointsRequired = 0L;
			if (currentLevel!=null) {
				currentLevelNumber = currentLevel.getNumber();
				currentLevelPointsRequired = currentLevel.getRequiredXp();
				currentLevelIsMilestone = (currentLevel.getMilestone()!=null)?currentLevel.getMilestone():false;
			}
			nextLevel = scheme.findLevel(currentLevelNumber+1);

			Long nextLevelPointsRequired = 0L;
			boolean nextLevelIsMilestone = false;
			if (nextLevel != null) {
				nextLevelPointsRequired = nextLevel.getRequiredXp();
				nextLevelIsMilestone = ((nextLevel.getMilestone()!=null)?nextLevel.getMilestone():false);
			}

			String xpMeterResult = new ObjectMapper().writeValueAsString(
				IXPMeterResult.builder()
				.meter(
					IXPMeter.builder()
					.currentLevelIsMilestone(currentLevelIsMilestone)
					.currentPoints(pb)
					.currentLevel(currentLevelNumber)
					.currentLevelPointsRequired(currentLevelPointsRequired)
					.nextLevelPointsRequired(nextLevelPointsRequired)
					.nextLevelIsMilestone(nextLevelIsMilestone)
					.build()
				)
				.build()
			);
			log.info("target : "+"playerroom/"+userGuid+" event : xp");
			log.info("Streaming :: "+xpMeterResult);
			gatewayExchangeStream.process("playerroom/"+userGuid, "xp", xpMeterResult);
		} catch (Exception e) {
			log.error("Could not stream xp update to player queue.", e);
		}
	}

	@Override
	public void processCompletedTransaction(CompleteTransaction request) throws Exception {
		log.debug("The completed transaction. " + request);
		// Bet transaction
		if (request.getTransactionType().equalsIgnoreCase(CasinoTranType.CASINO_BET.toString())) {
			String tranCurrencyCode = request.getTransactionEntryList().get(0).getAccount().getCurrency().getCode();
			String domainDefaultCurrency = cachingDomainClientService.getDomainClient()
					.findByName(request.getTransactionEntryList().get(0).getAccount().getDomain().getName()).getData().getCurrency();
			if (tranCurrencyCode.contentEquals(domainDefaultCurrency)) {
				processXp(request.getTransactionEntryList().get(0).getAccount().getDomain().getName(),
						request.getTransactionEntryList().get(0).getAccount().getOwner().getGuid(),
						request.getTransactionId(),
						Math.abs(request.getTransactionEntryList().get(0).getAmountCents()));
			}
		}
	}


	public Level getLevelByPlayerGuid(String userGuid, String domainName) throws Exception {
		Scheme scheme = getActiveScheme(domainName);
		Long playerBalance = xp(domainName, userGuid);
		List<Level> sortedLevels = scheme.getLevels().stream()
				.sorted((l1, l2) -> Integer.compare(l1.getNumber(), l2.getNumber()))
				.collect(Collectors.toList());
		for (Level level: sortedLevels) {
			if (playerBalance >= level.getRequiredXp())
				return level;
			else break;
		}
		return Level.builder().number(0).build();
	}
}

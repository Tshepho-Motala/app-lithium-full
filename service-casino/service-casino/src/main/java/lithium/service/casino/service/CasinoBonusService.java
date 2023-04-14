package lithium.service.casino.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.metrics.LithiumMetricsService;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.accounting.client.AccountingClient;
import lithium.service.accounting.client.AccountingSummaryTransactionTypeClient;
import lithium.service.accounting.objects.AdjustmentTransaction;
import lithium.service.accounting.objects.AdjustmentTransaction.AdjustmentResponseStatus;
import lithium.service.accounting.objects.Period;
import lithium.service.accounting.objects.SummaryAccountLabelValueType;
import lithium.service.accounting.objects.SummaryAccountTransactionType;
import lithium.service.casino.CasinoTranType;
import lithium.service.casino.client.CasinoExternalBonusGameClient;
import lithium.service.casino.client.data.BonusAllocate;
import lithium.service.casino.client.data.BonusHourly;
import lithium.service.casino.client.objects.request.BetRequest;
import lithium.service.casino.client.objects.request.GetBonusInfoRequest;
import lithium.service.casino.client.objects.response.GetBonusInfoResponse;
import lithium.service.casino.controllers.FreeRoundBonusController;
import lithium.service.casino.data.entities.Bonus;
import lithium.service.casino.data.entities.BonusExternalGameConfig;
import lithium.service.casino.data.entities.BonusRequirementsDeposit;
import lithium.service.casino.data.entities.BonusRequirementsSignup;
import lithium.service.casino.data.entities.BonusRevision;
import lithium.service.casino.data.entities.BonusRulesCasinoChip;
import lithium.service.casino.data.entities.BonusRulesCasinoChipGames;
import lithium.service.casino.data.entities.BonusRulesFreespinGames;
import lithium.service.casino.data.entities.BonusRulesFreespins;
import lithium.service.casino.data.entities.BonusRulesGamesPercentages;
import lithium.service.casino.data.entities.BonusRulesInstantReward;
import lithium.service.casino.data.entities.BonusRulesInstantRewardFreespin;
import lithium.service.casino.data.entities.BonusRulesInstantRewardFreespinGames;
import lithium.service.casino.data.entities.BonusRulesInstantRewardGames;
import lithium.service.casino.data.entities.BonusUnlockGames;
import lithium.service.casino.data.entities.Domain;
import lithium.service.casino.data.entities.GameCategory;
import lithium.service.casino.data.entities.Graphic;
import lithium.service.casino.data.entities.PlayerBonus;
import lithium.service.casino.data.entities.PlayerBonusExternalGameLink;
import lithium.service.casino.data.entities.PlayerBonusHistory;
import lithium.service.casino.data.entities.PlayerBonusPending;
import lithium.service.casino.data.enums.ActiveBonusStatus;
import lithium.service.casino.data.objects.ActiveBonus;
import lithium.service.casino.data.objects.BonusCreate;
import lithium.service.casino.data.objects.BonusEdit;
import lithium.service.casino.data.objects.BonusEdit.DepositRequirements;
import lithium.service.casino.data.objects.BonusEdit.FreespinRules;
import lithium.service.casino.data.objects.BonusEdit.FreespinRules.FreespinGame;
import lithium.service.casino.data.objects.BonusEdit.GamePercentages;
import lithium.service.casino.data.objects.BonusEdit.UnlockGames;
import lithium.service.casino.data.objects.BonusEdit.UnlockGamesList;
import lithium.service.casino.data.objects.TranProcessResponse;
import lithium.service.casino.data.projection.entities.BonusRulesFreespinGamesProjection;
import lithium.service.casino.data.projection.entities.PlayerBonusDisplay;
import lithium.service.casino.data.projection.entities.PlayerBonusFreespinHistoryProjection;
import lithium.service.casino.data.projection.entities.PlayerBonusHistoryActivationProjection;
import lithium.service.casino.data.projection.entities.PlayerBonusNoLongerAProjection;
import lithium.service.casino.data.projection.entities.PlayerBonusPendingProjection;
import lithium.service.casino.data.projection.entities.PlayerBonusProjection;
import lithium.service.casino.data.projection.repositories.PlayerBonusHistoryActivationProjectionRepository;
import lithium.service.casino.data.projection.repositories.PlayerBonusHistoryProjectionRepository;
import lithium.service.casino.data.projection.repositories.PlayerBonusPendingProjectionRepository;
import lithium.service.casino.data.projection.repositories.PlayerBonusProjectionRepository;
import lithium.service.casino.data.repositories.BonusExternalGameConfigRepository;
import lithium.service.casino.data.repositories.BonusFreeMoneyRepository;
import lithium.service.casino.data.repositories.BonusRepository;
import lithium.service.casino.data.repositories.BonusRequirementsDepositRepository;
import lithium.service.casino.data.repositories.BonusRequirementsSignupRepository;
import lithium.service.casino.data.repositories.BonusRevisionRepository;
import lithium.service.casino.data.repositories.BonusRulesCasinoChipGamesRepository;
import lithium.service.casino.data.repositories.BonusRulesCasinoChipRepository;
import lithium.service.casino.data.repositories.BonusRulesFreespinGamesRepository;
import lithium.service.casino.data.repositories.BonusRulesFreespinsRepository;
import lithium.service.casino.data.repositories.BonusRulesGamesPercentagesRepository;
import lithium.service.casino.data.repositories.BonusRulesInstantRewardFreespinGamesRepository;
import lithium.service.casino.data.repositories.BonusRulesInstantRewardFreespinRepository;
import lithium.service.casino.data.repositories.BonusRulesInstantRewardGamesRepository;
import lithium.service.casino.data.repositories.BonusRulesInstantRewardRepository;
import lithium.service.casino.data.repositories.DomainRepository;
import lithium.service.casino.data.repositories.GameCategoryRepository;
import lithium.service.casino.data.repositories.PlayerBonusExternalGameLinkRepository;
import lithium.service.casino.data.repositories.PlayerBonusHistoryRepository;
import lithium.service.casino.data.repositories.PlayerBonusPendingRepository;
import lithium.service.casino.data.repositories.PlayerBonusRepository;
import lithium.service.casino.data.specifications.BonusRevisionSpecification;
import lithium.service.casino.data.specifications.BonusSpecification;
import lithium.service.casino.data.specifications.PlayerBonusHistorySpecification;
import lithium.service.casino.exceptions.InvalidBonusException;
import lithium.service.casino.exceptions.Status404BonusNotFoundException;
import lithium.service.casino.exceptions.Status405BonusDeleteNotAllowedException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.page.SimplePageImpl;
import lithium.service.client.util.LabelManager;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.promo.client.stream.MissionStatsStream;
import lithium.service.notifications.client.objects.UserNotification;
import lithium.service.notifications.client.stream.NotificationStream;
import lithium.service.stats.client.enums.Event;
import lithium.service.stats.client.objects.StatEntry;
import lithium.service.stats.client.stream.QueueStatEntry;
import lithium.service.stats.client.stream.StatsStream;
import lithium.service.user.client.UserEventClient;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.objects.UserEvent;
import lithium.tokens.LithiumTokenUtil;
import lithium.tokens.LithiumTokenUtilService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CasinoBonusService {
	protected static final String PLAYER_BONUS_HISTORY_ID = "player_bonus_history_id";
	
	private static final String DEPOSIT = "CASHIER_DEPOSIT";
	private static final String BONUS_REVISION_ID = "bonus_revision_id";

	@Value("${lithium.service.casino.service.xprequiredforhourlybonus:5020}")
	@Setter
	private Long xpRequiredForHourlyBonus;

	@Setter @Autowired private CasinoBonusFreespinService casinoBonusFreespinService;
	@Setter @Autowired private LithiumMetricsService metrics;
	@Setter @Autowired private BonusRepository bonusRepository;
	@Setter @Autowired private PlayerBonusRepository playerBonusRepository;
	@Setter @Autowired private PlayerBonusProjectionRepository playerBonusProjectionRepository;
	@Setter @Autowired private PlayerBonusHistoryActivationProjectionRepository playerBonusHistoryActivationProjectionRepository;
	@Setter @Autowired private PlayerBonusHistoryRepository playerBonusHistoryRepository;
	@Setter @Autowired private BonusRequirementsDepositRepository bonusRequirementsDepositRepository;
	@Setter @Autowired private BonusRulesFreespinsRepository bonusRulesFreespinsRepository;
	@Setter @Autowired private BonusRulesCasinoChipRepository bonusRulesCasinoChipRepository;
	@Setter @Autowired private BonusRulesCasinoChipGamesRepository bonusRulesCasinoChipGamesRepository;
	@Setter @Autowired private BonusRulesFreespinGamesRepository bonusRulesFreespinGamesRepository;
	@Setter @Autowired private BonusRulesGamesPercentagesRepository bonusRulesGamesPercentagesRepository;
	@Setter @Autowired private BonusRulesInstantRewardRepository bonusRulesInstantRewardRepository;
	@Setter @Autowired private BonusRulesInstantRewardGamesRepository bonusRulesInstantRewardGamesRepository;
	@Setter @Autowired private BonusRulesInstantRewardFreespinRepository bonusRulesInstantRewardFreespinRepository;
	@Setter @Autowired private BonusRulesInstantRewardFreespinGamesRepository bonusRulesInstantRewardFreespinGamesRepository;
	@Setter @Autowired private LithiumServiceClientFactory services;
	@Setter @Autowired private CasinoService casinoService;
	@Setter @Autowired private BonusRevisionRepository bonusRevisionRepository;
	@Setter @Autowired private DomainRepository domainRepository;
	@Setter @Autowired private BonusRequirementsSignupRepository bonusRequirementsSignupRepository;
	@Setter @Autowired private FreeRoundBonusController freeRoundBonusController;
	@Setter @Autowired private GameCategoryRepository gameCategoryRepository;
	@Setter @Autowired private ChangeLogService changeLogService;
	@Setter @Autowired private NotificationStream notificationStream;
	@Setter @Autowired private PlayerBonusPendingRepository playerBonusPendingRepository;
	@Setter @Autowired private PlayerBonusPendingProjectionRepository playerBonusPendingProjectionRepository;
	@Setter @Autowired private GraphicsService graphicsService;
	@Setter @Autowired private CasinoMailSmsService casinoMailSmsService;
	@Setter @Autowired private CasinoGeoService casinoGeoService;
	@Setter @Autowired private WinnerFeedService winnerFeedService;
	@Setter @Autowired private BonusRoundTrackService bonusRoundTrackService;
	@Setter @Autowired private CasinoBonusUnlockGamesService casinoBonusUnlockGamesService;
	@Setter @Autowired private BonusFreeMoneyRepository bonusFreeMoneyRepository;
	@Setter @Autowired private MissionStatsStream missionStatsStream;
	@Setter @Autowired private StatsStream statsStream;
	@Setter @Autowired private BonusExternalGameConfigRepository bonusExternalGameConfigRepository;
	@Setter @Autowired private PlayerBonusExternalGameLinkRepository playerBonusExternalGameLinkRepository;
	@Setter @Autowired private PlayerBonusHistoryProjectionRepository playerBonusHistoryProjectionRepository;
	@Setter @Autowired private CasinoTriggerBonusService casinoTriggerBonusService;
	@Setter @Autowired private ModelMapper mapper;
	@Setter @Autowired private BonusTokenService bonusTokenService;
	@Setter @Autowired private BonusService bonusService;
	@Setter @Autowired private CachingDomainClientService cachingDomainClientService;
	@Setter @Autowired private LithiumTokenUtilService tokenService;
	@Autowired private TokenStore tokenStore;
	@Autowired UserService userService;


	private Domain domain(String domainName) {
		Domain domain = domainRepository.findByName(domainName);
		if (domain == null) {
			domain = domainRepository.save(Domain.builder().name(domainName).build());
		}
		return domain;
	}
	
	public Iterable<GameCategory> gameCategories() {
		return gameCategoryRepository.findAll();
	}
	
	public GameCategory findByCasinoCategory(String category) {
		return gameCategoryRepository.findByCasinoCategory(category);
	}
	
	public List<BonusRulesGamesPercentages> bonusRulesGamesPercentageCategories(Long bonusRevisionId) {
		return bonusRulesGamesPercentagesRepository.findByBonusRevisionIdAndGameGuidIsNullAndGameCategoryIsNotNull(bonusRevisionId);
	}
	
	public void deleteBonusRulesGamesPercentage(Long id) {
		bonusRulesGamesPercentagesRepository.deleteById(id);
	}
	
	public List<BonusRulesGamesPercentages> bonusRulesGamesPercentages(Long bonusRevisionId) {
		return bonusRulesGamesPercentagesRepository.findByBonusRevisionIdAndGameGuidIsNotNullOrderByPercentageDescGameGuidDesc(bonusRevisionId);
	}
	
	public PlayerBonusDisplay playerBonusDisplay(String playerGuid, PlayerBonusHistory playerBonusHistory) {
		PlayerBonusNoLongerAProjection pbnlap = null;
		if (playerBonusHistory == null) {
			PlayerBonusProjection pbh = findCurrentBonusProjection(playerGuid);
			if (pbh != null) {
				pbnlap = PlayerBonusNoLongerAProjection.builder()
						.current(pbh.getCurrent())
						.id(pbh.getId())
						.playerGuid(playerGuid)
						.build();
			}
		} else {
			pbnlap = PlayerBonusNoLongerAProjection.builder()
					.current(playerBonusHistoryProjectionRepository.getById(playerBonusHistory.getId()))
					.id(playerBonusHistory.getPlayerBonus().getId())
					.playerGuid(playerBonusHistory.getPlayerBonus().getPlayerGuid())
					.build();
		}
		log.debug("PlayerBonus: " + pbnlap);
		PlayerBonusFreespinHistoryProjection playerBonusFreespinHistoryProjection = null;
		List<BonusRulesFreespinGamesProjection> bonusRulesFreespinGamesProjection = null;
		List<String> playerBonusExternalGameLinks = null;
		if ((pbnlap != null) && (pbnlap.getCurrent() != null)) {
			playerBonusFreespinHistoryProjection = casinoBonusFreespinService.playerBonusFreespinHistoryProjection(pbnlap.getCurrent().getId());
			bonusRulesFreespinGamesProjection = casinoBonusFreespinService.bonusRulesFreespinGamesProjection(pbnlap.getCurrent().getBonus().getId());
			for (PlayerBonusExternalGameLink link : playerBonusExternalGameLinkRepository.findByPlayerBonusHistoryId(pbnlap.getCurrent().getId())) {
				if (playerBonusExternalGameLinks == null) {
					playerBonusExternalGameLinks = new ArrayList<>();
				}
				playerBonusExternalGameLinks.add(link.getExternalGameUrl());
			}
		}
		log.debug("PlayerBonusFreespinHistoryProjection : "+playerBonusFreespinHistoryProjection);
		return PlayerBonusDisplay.builder()
			.playerBonusProjection(pbnlap)
			.playerBonusFreespinHistoryProjection(playerBonusFreespinHistoryProjection)
			.bonusRulesFreespinGamesProjection(bonusRulesFreespinGamesProjection)
			.playerBonusExternalGameLinks(playerBonusExternalGameLinks)
			.build();
	}
	
	public BonusRevision findBonusRevisionById(Long id) {
		return bonusRevisionRepository.findOne(id);
	}
	
	public BonusRevision findLastBonusRevision(String bonusCode, String domainName, Integer bonusType) {
		log.info("Searching for bonusRevision Code: " + bonusCode + " on " + domainName + " of type " + bonusType + " and enabled true and deleted false");
		return bonusRevisionRepository.findTop1ByBonusCodeAndBonusTypeAndDomainNameAndEnabledAndDeletedOrderByIdDesc(bonusCode, bonusType, domainName, true, false);
	}
	
	public Bonus findBonus(String bonusCode, String domainName, Integer bonusType) {
		log.info("Searching for bonusCode: "+bonusCode+" on "+domainName+" type : "+bonusType);
		return bonusRepository.findByCurrentBonusCodeAndCurrentDomainNameAndCurrentBonusTypeAndCurrentEnabledTrue(bonusCode.toUpperCase(), domainName, bonusType);
	}
	public Bonus findDepositBonus(String bonusCode, String domainName) {
		log.debug("Searching for deposit bonusCode: "+bonusCode);
		return findBonus(bonusCode.toUpperCase(), domainName, BonusRevision.BONUS_TYPE_DEPOSIT);
	}
	public Bonus findSignupBonus(String bonusCode, String domainName) {
		log.debug("Searching for signup bonusCode: "+bonusCode);
		return findBonus(bonusCode.toUpperCase(), domainName, BonusRevision.BONUS_TYPE_SIGNUP);
	}
	
	public boolean bonusValidForPlayer(Bonus bonus, String playerGuid) {
		return bonusValidForPlayer(bonus.getCurrent(), playerGuid);
	}
	public boolean bonusValidForPlayer(BonusRevision bonusRevision, String playerGuid) {
		if (bonusValid(bonusRevision)) {
			String domainName = bonusRevision.getDomain().getName();
			log.info("Domain :"+domainName+" Player: "+playerGuid+" Bonus: "+bonusRevision);
			if (playerGuid.startsWith(domainName)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean activeDaysValid(BonusRevision bonusRevision) {
		boolean hasActiveStart = false;
		if (bonusRevision.getActiveStartTime()!=null) hasActiveStart = true;
		boolean hasActiveEnd = false;
		if (bonusRevision.getActiveEndTime()!=null) hasActiveEnd = true;
		DateTimeZone activeTimezone = DateTimeZone.forID(bonusRevision.getActiveTimezone());
		DateTime serverTimeActiveTimezone = new DateTime(activeTimezone);
		if (hasActiveStart) {
			DateTime activeStart = new LocalTime(bonusRevision.getActiveStartTime()).toDateTimeToday(activeTimezone);
			if (!activeStart.isBefore(serverTimeActiveTimezone)) {
				return false;
			}
		}
		if (hasActiveEnd) {
			DateTime activeEnd = new LocalTime(bonusRevision.getActiveEndTime()).toDateTimeToday(activeTimezone);
			if (!activeEnd.isAfter(serverTimeActiveTimezone)) {
				return false;
			}
		}
		String currentDayOfWeek = serverTimeActiveTimezone.getDayOfWeek()+"";
		if (bonusRevision.getActiveDays() != null) {
			boolean foundValidDay = false;
			String[] activeDays = bonusRevision.getActiveDays().split(",");
			for (String ad:activeDays) {
				if (ad.equalsIgnoreCase(currentDayOfWeek)) {
					foundValidDay = true;
				}
			}
			if (!foundValidDay) {
				return false;
			}
		}
		return true;
	}
	
	public boolean bonusValid(Bonus bonus) {
		if (bonus == null) return false;
		return bonusValid(bonus.getCurrent());
	}
	//TODO: Need to rework this to throw exceptions, don't want to impact current system now.
	public boolean bonusValid(BonusRevision bonusRevision) {
		if (bonusRevision == null) {
			return false;
		}
		if (!bonusRevision.isEnabled()) return false;
		
		DateTimeZone dtzStart = DateTimeZone.forID(bonusRevision.getStartingDateTimezone());
		DateTimeZone dtzExpire = DateTimeZone.forID(bonusRevision.getExpirationDateTimezone());
		
		DateTime serverTimeLocal = new DateTime();
		DateTime serverTimeInStartTZ = new DateTime(dtzStart);
		DateTime serverTimeInExpireTZ = new DateTime(dtzExpire);
		
		boolean hasStartDate = false;
		DateTime bonusStartWithTZ = new LocalDateTime(bonusRevision.getStartingDate()).toDateTime(dtzStart);
		if (bonusRevision.getStartingDate()!=null) hasStartDate = true;
		boolean hasEndDate = false;
		DateTime bonusExpireWithTZ = new LocalDateTime(bonusRevision.getExpirationDate()).toDateTime(dtzExpire);
		if (bonusRevision.getExpirationDate()!=null) hasEndDate = true;
		
		log.debug("serverTimeLocal : "+serverTimeLocal);
		log.debug("serverTimeInStartTZ : "+serverTimeInStartTZ);
		log.debug("serverTimeInExpireTZ : "+serverTimeInExpireTZ);
		
		log.debug("bonusStartWithTZ : "+bonusStartWithTZ);
		log.debug("bonusExpireWithTZ : "+bonusExpireWithTZ);
		
		if (!activeDaysValid(bonusRevision)) {
			return false;
		}
		if (!hasStartDate) {
			//no start date, check if enddate specified
			if (!hasEndDate) {
				// no start/end date specified.
				return true;
			} else {
				//check for end date valid
				if (bonusExpireWithTZ.isAfter(serverTimeInExpireTZ)) {
					log.info("Found Valid Bonus : "+bonusRevision);
					return true;
				}
				return false;
			}
		} else {
			//has start date, first check start date, then check if enddate specified
			if (!bonusStartWithTZ.isBefore(serverTimeInStartTZ)) {
				log.info("Not valid, hasn't started yet. : "+bonusRevision);
				return false;
			}
			if (!hasEndDate) {
				// start date valid, no end date specified.
				log.info("Found Valid Bonus : "+bonusRevision);
				return true;
			} else {
				//check for end date valid
				if (bonusExpireWithTZ.isAfter(serverTimeInExpireTZ)) {
					log.info("Found Valid Bonus : "+bonusRevision);
					return true;
				}
				return false;
			}
		}
	}
	
	public BonusRequirementsDeposit bonusRequirementsDeposit(Long bonusRevisionId, Long depositCents, boolean manualDepositType) {
		BonusRequirementsDeposit bonusRequirementsDeposit = bonusRequirementsDepositRepository.findByBonusRevisionIdAndMaxDepositGreaterThanAndMinDepositLessThanEqual(
			bonusRevisionId,
			depositCents,
			depositCents
		);
		if (bonusRequirementsDeposit == null) {
			bonusRequirementsDeposit = bonusRequirementsDepositRepository.findByBonusRevisionIdAndMinDepositLessThanEqualAndMaxDepositIsNull(bonusRevisionId, depositCents);
		}
		
		if (manualDepositType && bonusRequirementsDeposit == null) {
			List<BonusRequirementsDeposit> brdList = bonusRequirementsDepositRepository.findByBonusRevisionId(bonusRevisionId);
			if (!brdList.isEmpty()) {
				bonusRequirementsDeposit = brdList.get(0);
			}
		}
		return bonusRequirementsDeposit;
	}

	@TimeThisMethod
	public boolean checkMaxRedeemableValid(BonusRevision bonusRevision, String playerGuid) {
		Integer maxRedeemable = bonusRevision.getMaxRedeemable();
		Integer maxRedeemableGranularity = bonusRevision.getMaxRedeemableGranularity();
		if (maxRedeemableGranularity==null) maxRedeemableGranularity = Period.GRANULARITY_TOTAL;
		
		if (maxRedeemable == null) { 
			return true;
		} else {
			if (maxRedeemableGranularity == Period.GRANULARITY_TOTAL) {
				List<PlayerBonusHistory> history = playerBonusHistoryRepository.findByPlayerBonusPlayerGuidAndBonusBonusCode(playerGuid, bonusRevision.getBonusCode());
				if (history.size() >= maxRedeemable) {
					log.info("Bonus code usage exceeded for player. ("+playerGuid+") :: "+bonusRevision);
					return false;
				}
				return true;
			} else {
				DateTimeZone dtzStart = DateTimeZone.forID(bonusRevision.getStartingDateTimezone());
				DateTime rangeStart = new DateTime();
				DateTime rangeEnd = new DateTime();
				switch (maxRedeemableGranularity) {
					case Period.GRANULARITY_YEAR:
						rangeStart = new DateTime(dtzStart).dayOfYear().withMinimumValue().withTimeAtStartOfDay();
						rangeEnd = rangeStart.plusYears(1).dayOfMonth().withMinimumValue().withTimeAtStartOfDay();
						break;
					case Period.GRANULARITY_MONTH:
						rangeStart = new DateTime(dtzStart).dayOfMonth().withMinimumValue().withTimeAtStartOfDay();
						rangeEnd = rangeStart.plusMonths(1).dayOfMonth().withMinimumValue().withTimeAtStartOfDay();
						break;
					case Period.GRANULARITY_WEEK:
						rangeStart = new DateTime(dtzStart).dayOfWeek().withMinimumValue().withTimeAtStartOfDay();
						rangeEnd = rangeStart.plusWeeks(1).dayOfWeek().withMinimumValue().withTimeAtStartOfDay();
						break;
					case Period.GRANULARITY_DAY:
						rangeStart = new DateTime(dtzStart).withTimeAtStartOfDay();
						rangeEnd = rangeStart.plusDays(1).withTimeAtStartOfDay();
						break;
					default:
						return false;
				}
				rangeStart = rangeStart.toDateTime(DateTimeZone.forID(null));
				rangeEnd = rangeEnd.toDateTime(DateTimeZone.forID(null));
				List<PlayerBonusHistory> history = playerBonusHistoryRepository.findByPlayerBonusPlayerGuidAndStartedDateBetweenAndBonusBonusCode(playerGuid, rangeStart.toDate(), rangeEnd.toDate(), bonusRevision.getBonusCode());
				if (history.size() >= maxRedeemable) {
					log.info("Bonus code usage exceeded for player. ("+playerGuid+") :: "+bonusRevision);
					return false;
				}
				return true;
			}
		}
	}

	public lithium.service.casino.client.data.PlayerBonusHistory findPlayerBonusHistoryById(Long playerBonusHistoryId) {
		PlayerBonusHistory history = playerBonusHistoryRepository.findOne(playerBonusHistoryId);
		if (history == null) return null;
		return mapper.map(history, lithium.service.casino.client.data.PlayerBonusHistory.class);
	}
	
	public List<lithium.service.casino.client.data.PlayerBonusHistory> findBonusHistoryByDateRange(String playerGuid, Date rangeStart, Date rangeEnd) {
		List<PlayerBonusHistory> history = playerBonusHistoryRepository.findByPlayerBonusPlayerGuidAndStartedDateBetween(playerGuid, rangeStart, rangeEnd);
		List<lithium.service.casino.client.data.PlayerBonusHistory> resultHistory = new ArrayList<>();
		history.forEach(historyData -> { 
			lithium.service.casino.client.data.PlayerBonusHistory h = new lithium.service.casino.client.data.PlayerBonusHistory(); 
			mapper.map(historyData, h); 
			resultHistory.add(h);
		} );
		
		return resultHistory;
	}
	
	public List<Bonus> findTop50ByBonusCodeOrBonusName(Integer bonusType, String search) {
		return bonusRepository.findTop50ByCurrentBonusTypeAndCurrentBonusCodeIgnoreCaseContainingOrCurrentBonusNameIgnoreCaseContainingOrderByCurrentBonusCode(bonusType, search, search);
	}
	
	public List<BonusRequirementsDeposit> findDepositBonusRequirements(Long bonusRevisionId) {
		return bonusRequirementsDepositRepository.findByBonusRevisionId(bonusRevisionId);
	}
	
	public List<BonusRequirementsSignup> findSignupBonusRequirements(Long bonusRevisionId) {
		return bonusRequirementsSignupRepository.findByBonusRevisionId(bonusRevisionId);
	}
	
	public BonusRequirementsDeposit findTop1BonusRequirementsDeposit(Long bonusRevisionId) {
//		BonusRequirementsDeposit bonusRequirementsDeposit = 
		return bonusRequirementsDepositRepository.findTop1ByBonusRevisionIdOrderByMaxDepositDesc(bonusRevisionId);
	}
	
	public Page<Bonus> findBonusList(DataTableRequest request, List<Boolean> status, List<String> domainNames, List<Integer> bonusType) {
		List<Domain> domains = new ArrayList<>();
		for (String domainName:domainNames) {
			Domain domain = domainRepository.findByName(domainName);
			if (domain != null) domains.add(domain);
		}
		if (domains.size() == 0) domains.add(Domain.builder().id(-1L).build());
		if (status.size() == 0) status.add(null);
		if (bonusType.size() == 0) bonusType.add(-1);
		
		return bonusRepository.findAll(BonusSpecification.table(request.getSearchValue(), domains, status, bonusType, false), request.getPageRequest());
	}
	
	public Page<PlayerBonusPendingProjection> findPendingBonusList(DataTableRequest request, String playerGuid) {
		Page<PlayerBonusPendingProjection> findByPlayerGuidProjection = playerBonusPendingProjectionRepository.findByPlayerGuid(playerGuid, request.getPageRequest());
		
		return findByPlayerGuidProjection;
	}
	
	public Page<PlayerBonusPendingProjection> findPendingBonusList(DataTableRequest request, BonusRevision bonusRevision) {
		Page<PlayerBonusPendingProjection> findByBonusRevisionProjection = playerBonusPendingProjectionRepository.findByBonusRevision(bonusRevision, request.getPageRequest());
		
		return findByBonusRevisionProjection;
	}

	public Page<PlayerBonusHistory> findBonusHistoryList(DataTableRequest request, String playerGuid) {
		Page<PlayerBonusHistory> findByPlayerGuidPage = playerBonusHistoryRepository.findByPlayerBonusPlayerGuid(playerGuid, request.getPageRequest());

		return findByPlayerGuidPage;
	}
	
	public Page<PlayerBonusHistoryActivationProjection> findBonusActivationList(DataTableRequest request, Long bonusRevisionId, List<String> statuses) {
		boolean completed = false;
		boolean cancelled = false;
		boolean expired = false;
		boolean active = false;
		if (statuses!=null) {
			for (String status:statuses) {
				switch (status) {
					case "completed":
						completed = true;
						break;
					case "cancelled":
						cancelled = true;
						break;
					case "expired":
						expired = true;
						break;
					case "active":
						active = true;
						break;
					default:
						break;
				}
			}
		} else {
			completed = true;
			cancelled = true;
			expired = true;
			active = true;
		}
		log.info("completed:"+completed+", cancelled:"+cancelled+", expired:"+expired+" active:"+active);
		
		Page<PlayerBonusHistoryActivationProjection> findByBonusIdProjection = playerBonusHistoryActivationProjectionRepository.findByBonusRevisionIdProjection(bonusRevisionId, request.getSearchValue()+"%", completed, cancelled, expired, active, request.getPageRequest());
		log.info("findByBonusIdProjection: "+findByBonusIdProjection);
		
		return findByBonusIdProjection;
//		return playerBonusHistoryActivationProjectionRepository.findByBonusIdAndPlayerBonusPlayerGuidIgnoreCaseContainingAndCompletedOrCancelledOrExpired(bonusRevisionId, request.getSearchValue()+"%", completed, cancelled, expired, request.getPageRequest());
//		return playerBonusHistoryRepository.findAll(PlayerBonusHistorySpecification.byBonusRevisionId(bonusRevisionId), request.getPageRequest());
	}
	
	public Page<BonusRevision> findBonusRevisionsByBonusId(DataTableRequest request, Long bonusId) {
//		Page<BonusRevision> findBonusRevisionsByBonusId = bonusRevisionRepository.findByBonusIdOrderByIdDesc(request.getPageRequest(), bonusId);
		Page<BonusRevision> findBonusRevisionsByBonusId = bonusRevisionRepository.findAll(BonusRevisionSpecification.byBonusId(request.getSearchValue(), bonusId), request.getPageRequest());
		log.info("findBonusRevisionsByBonusId ("+bonusId+") : "+findBonusRevisionsByBonusId);
		return findBonusRevisionsByBonusId;
	}
	
	public Bonus findBonus(Long bonusId) {
		return bonusRepository.findOne(bonusId);
	}
	public Bonus findCurrentBonus(Long bonusRevisionId) {
		return bonusRepository.findByCurrentId(bonusRevisionId);
	}
	public PlayerBonus findCurrentBonus(String playerGuid) {
		return playerBonusRepository.findByPlayerGuidAndCurrentNotNull(playerGuid);
	}
	public PlayerBonusProjection findCurrentBonusProjection(String playerGuid) {
		return playerBonusProjectionRepository.findByPlayerGuidAndCurrentNotNull(playerGuid);
	}
	public List<PlayerBonusHistory> findPlayerBonusHistory(String playerGuid, String bonusCode) {
		return playerBonusHistoryRepository.findByPlayerBonusPlayerGuidAndBonusBonusCode(playerGuid, bonusCode);
	}
	public Page<PlayerBonusHistory> findPlayerBonusHistory(String playerGuid, PageRequest pageRequest) {
		return playerBonusHistoryRepository.findByPlayerBonusPlayerGuid(playerGuid, pageRequest);
	}

	public List<String> findBonusCodesForActiveTokens(String [] activeDomains) {
		Specification specification = bonusRevisionSpecification(Arrays.asList(activeDomains));
		List<BonusRevision> bonusRevision = bonusRevisionRepository.findAll(specification);
		return bonusRevision.stream().filter(x -> x.isEnabled()).map(b -> b.getBonusCode()).distinct().collect(Collectors.toList());
	}

	public SimplePageImpl<ActiveBonus> findActiveBonusTokens(String[] activeDomains, String [] bonuses, String status, Date dateRangeFrom, Date dateRangeTo, PageRequest pageRequest) {
		Specification specs = activeBonusesSpecifications(status, bonuses, dateRangeFrom, dateRangeTo, Arrays.asList(activeDomains));
		Page<PlayerBonusHistory> playerBonusHistoryPages = playerBonusHistoryRepository.findAll(specs,pageRequest);
		List<PlayerBonusHistory> content = playerBonusHistoryPages.getContent();
		List<ActiveBonus> activeBonuses = content.stream().map(s -> {
			String domainName = s.getPlayerBonus().getPlayerGuid().split("/")[0];
			String currencySymbol = null;
			try {
				currencySymbol = cachingDomainClientService.getDefaultDomainCurrencySymbol(domainName);
			} catch (Status550ServiceDomainClientException e) {
				log.error("Failed to load currency symbol",e.getMessage());
			}

			return ActiveBonus.builder()
					.bonusId(s.getPlayerBonus().getId())
					.bonusCode(s.getBonus().getBonusCode())
					.currencySymbol(currencySymbol)
					.playerGuid(s.getPlayerBonus().getPlayerGuid() != null ? s.getPlayerBonus().getPlayerGuid() : null)
					.grantDate(s.getStartedDate())
					.bonusRevisionId(s.getBonus().getId())
					.bonusName(s.getBonus().getBonusName())
					.amount((s.getCustomFreeMoneyAmountCents()!=null)?s.getCustomFreeMoneyAmountCents():s.getBonusAmount())
					.completed(s.getCompleted())
					.expired(s.getExpired())
					.cancelled(s.getCancelled())
					.bonusCode(s.getBonus().getBonusCode())
					.build();
		}).collect(Collectors.toList());
		return new SimplePageImpl<>(activeBonuses, playerBonusHistoryPages.getNumber(), playerBonusHistoryPages.getSize(), playerBonusHistoryPages.getTotalElements());
	}

	private Specification activeBonusesSpecifications(String status,String[] bonuses,Date dateRangeFrom, Date dateRangeTo,
													   List<String> domains) {
		Specification<PlayerBonusHistory> spec;
		spec = bonusService.addToSpec(dateRangeFrom, false, null, PlayerBonusHistorySpecification::startedDateRangeStart);
		spec = bonusService.addToSpec(dateRangeTo, true, spec, PlayerBonusHistorySpecification::startedDateRangeEnd);
		spec = bonusService.addToSpec(bonuses,spec,PlayerBonusHistorySpecification::bonusCodes);
		spec = bonusService.addToSpec(domains, spec, PlayerBonusHistorySpecification::byDomains);

		if(!status.isEmpty()) {
			if(status.equals(ActiveBonusStatus.GRANTED.toString())) {
				spec = bonusService.addToSpec(status, spec,x -> PlayerBonusHistorySpecification.granted());
			} else if(status.equals(ActiveBonusStatus.EXPIRED.toString())) {
				spec = bonusService.addToSpec(status, spec,x -> PlayerBonusHistorySpecification.expired());
			} else if(status.equals(ActiveBonusStatus.CANCELLED.toString())) {
				spec = bonusService.addToSpec(status, spec,x -> PlayerBonusHistorySpecification.cancelled());
			} else if(status.equals(ActiveBonusStatus.ACTIVE.toString())) {
				spec = bonusService.addToSpec(status, spec,x -> PlayerBonusHistorySpecification.active());
			}
		}
		return spec;
	}

	private Specification bonusRevisionSpecification(List<String> activeDomains) {
		Specification<BonusRevision> spec = bonusService.addToSpecBonusCodes(activeDomains, BonusRevisionSpecification::byDomains);
		return spec;
	}
	
	public PlayerBonusHistory updatePlayerBonusHistory(Long id, PlayerBonus playerBonus, Long triggerAmount) {
		PlayerBonusHistory pbh = playerBonusHistoryRepository.findOne(id);
		return updatePlayerBonusHistory(pbh, playerBonus, triggerAmount);
	}
	
	public PlayerBonusHistory updatePlayerBonusHistory(PlayerBonusHistory playerBonusHistory, PlayerBonus playerBonus, Long triggerAmount) {
		playerBonusHistory.setPlayerBonus(playerBonus);
		playerBonusHistory.setTriggerAmount(triggerAmount);
		// Use the updatePlayerBonusCurrent method to keep the history in line with the pb
		//playerBonus.setCurrent(playerBonusHistoryRepository.save(playerBonusHistory));
		return playerBonusHistoryRepository.save(playerBonusHistory);
	}

	public PlayerBonusHistory savePlayerBonusHistory(BonusRevision bonusRevision, boolean instantBonus) {
		return savePlayerBonusHistory(bonusRevision, 0L, 0L, 0, instantBonus, null, null, null, null, null, null);
	}
	public PlayerBonusHistory savePlayerBonusHistory(BonusRevision bonusRevision, boolean instantBonus, String description, Long requestId, Long sessionId, String clientId, String noteText) {
		return savePlayerBonusHistory(bonusRevision, 0L, 0L, 0, instantBonus, null, description, requestId, sessionId, clientId, noteText);
	}
	public PlayerBonusHistory savePlayerBonusHistory(BonusRevision bonusRevision, boolean instantBonus, Long customAmountCents, String description, Long requestId, Long sessionId, String clientId, String noteText) {
		return savePlayerBonusHistory(bonusRevision, 0L, 0L, 0, instantBonus, customAmountCents, description, requestId, sessionId, clientId, noteText);
	}
	public PlayerBonusHistory savePlayerBonusHistory(BonusRevision bonusRevision, Long playThroughRequiredCents, Long bonusAmount, Integer bonusPercentage, boolean instantBonus) {
		return savePlayerBonusHistory(bonusRevision, playThroughRequiredCents, bonusAmount, bonusPercentage, instantBonus, null, null, null, null, null, null);
	}
	public PlayerBonusHistory savePlayerBonusHistory(BonusRevision bonusRevision, Long playThroughRequiredCents, Long bonusAmount, Integer bonusPercentage, boolean instantBonus, Long customAmountCents, String description, Long requestId, Long sessionId, String clientId, String noteText) {
		PlayerBonusHistory playerBonusHistory =
				PlayerBonusHistory.builder()
						.bonus(bonusRevision)
						.startedDate(new Date())
						.playThroughCents(0L)
						.playThroughRequiredCents(playThroughRequiredCents)
						.bonusAmount(bonusAmount)
						.bonusPercentage(bonusPercentage)
						.cancelled(false)
						.completed(instantBonus ? true : false) // Instant bonus is instantly completed
						.expired(false)
						.description(description)
						.requestId(requestId)
						.sessionId(sessionId)
						.clientId(clientId)
						.noteText(noteText)
						.build();
		switch(bonusRevision.getBonusType()) {
			case BonusRevision.BONUS_TYPE_TRIGGER : {
				playerBonusHistory.setCustomFreeMoneyAmountCents(customAmountCents);
				break;
			}
			case BonusRevision.BONUS_TYPE_BONUS_TOKEN : {
				playerBonusHistory.setCustomBonusTokenAmountCents(customAmountCents);
				break;
			}
		}
		return playerBonusHistoryRepository.save(playerBonusHistory);
	}
	
	public PlayerBonusPending savePlayerBonusPending(Bonus bonus, Long playThroughRequiredCents, Long bonusAmount, Integer bonusPercentage, String playerGuid, Long triggerAmount) {
		return savePlayerBonusPending(bonus, playThroughRequiredCents, bonusAmount, bonusPercentage, playerGuid, triggerAmount, null, null);
	}

	public PlayerBonusPending savePlayerBonusPending(Bonus bonus, Long playThroughRequiredCents, Long bonusAmount, Integer bonusPercentage, String playerGuid, Long triggerAmount, Long customFreeMoneyAmountCents, Long bonusRevisionId) {
		BonusRevision bonusRevisionToUse = bonus.getCurrent();
		if (bonusRevisionId != null && bonusRevisionId > 0) {
			//Lookup the bonus revision. This is used when players are being registered for older revisions of bonuses.
			BonusRevision bonusRevision = bonusRevisionRepository.findOne(bonusRevisionId);
			if (bonusRevision != null && bonusRevision.getBonus().getId() == bonus.getId()) {
				bonusRevisionToUse = bonusRevision;
			}
		}
		return playerBonusPendingRepository.save(
			PlayerBonusPending.builder()
			.bonusRevision(bonusRevisionToUse)
			.createdDate(new Date())
			.playThroughRequiredCents(playThroughRequiredCents)
			.bonusAmount(bonusAmount)
			.bonusPercentage(bonusPercentage)
			.playerGuid(playerGuid)
			.triggerAmount(triggerAmount)
			.customFreeMoneyAmountCents(customFreeMoneyAmountCents)
			.build()
		);
	}

	public PlayerBonus findOrCreatePlayerBonus(String playerGuid) {
		PlayerBonus playerBonus = playerBonusRepository.findByPlayerGuid(playerGuid);
		if (playerBonus == null) {
			playerBonus = playerBonusRepository.save(PlayerBonus.builder().playerGuid(playerGuid).build());
		}
		return playerBonus;
	}
	
	public PlayerBonus updatePlayerBonusCurrent(PlayerBonusHistory playerBonusHistory, String playerGuid, boolean instantBonus) {
		PlayerBonus playerBonus = findOrCreatePlayerBonus(playerGuid);
		if (!instantBonus) {
			playerBonus.setCurrent(playerBonusHistory);
		}
		return playerBonusRepository.save(playerBonus);
	}
	
	public PlayerBonus savePlayerBonus(PlayerBonusHistory playerBonusHistory, String playerGuid, boolean instantBonus) {
		PlayerBonus playerBonus = findOrCreatePlayerBonus(playerGuid);
		// Instant bonus should not become the current player bonus, since it never requires an action to complete
		if (!instantBonus) {
			playerBonus.setCurrent(playerBonusHistory);
		}
		return playerBonusRepository.save(playerBonus);
	}
	
	public SummaryAccountTransactionType checkDeposits(String domainName, String ownerGuid, int granularity) throws Exception {
		Response<SummaryAccountTransactionType> stt = getAccountingSummaryTransactionTypeService().find(
			DEPOSIT,
			domainName,
			URLEncoder.encode(ownerGuid, "UTF-8"),
			granularity,
			casinoService.getCurrency(domainName)
		);
		if (stt.isSuccessful()) {
			return stt.getData();
		}
		return null;
	}

	public void externalBonusInfo(PlayerBonus pb) {
		try {
			GetBonusInfoRequest request = GetBonusInfoRequest.builder()
			.userId(pb.getPlayerGuid())
//			.userId("luckybetz/riaans11")
			.build();
//			pb.getCurrent().getBonus().get
			request.setProviderGuid("service-casino-provider-nucleus");
			request.setDomainName(pb.getCurrent().getBonus().getDomain().getName());
			log.info("GetBonusInfoRequest : "+request);
			GetBonusInfoResponse response = freeRoundBonusController.handleGetBonusInfo(request);
			log.info("GetBonusInfoResponse : "+response);
		} catch (Exception e) {
			log.debug("Could not check freespins for :"+pb, e);
		}
	}
	
	public GetBonusInfoResponse externalBonusInfo(String playerGuid, String provider, String domainName, String gameId) {
		try {
			GetBonusInfoRequest request = GetBonusInfoRequest.builder()
			.userId(playerGuid)
			.gameId(gameId)
			.build();
			request.setProviderGuid(provider);
			request.setDomainName(domainName);
			log.info("GetBonusInfoRequest : "+request);
			GetBonusInfoResponse response = freeRoundBonusController.handleGetBonusInfo(request);
			log.info("GetBonusInfoResponse : "+response);
			return response;
		} catch (Exception e) {
			log.debug("Could not check freespins for :"+playerGuid, e);
		}
		return null;
	}

	public void triggerAdditionalFreeMoney(PlayerBonusHistory pbh, boolean instantBonus) throws Exception {
		pbh.getBonus().setBonusFreeMoney(bonusFreeMoneyRepository.findByBonusRevisionId(pbh.getBonus().getId()));
		log.info("Allocating additional free money "
						 + " BonusFreeMoney " + pbh.getBonus().getBonusFreeMoney()
						 + " on " + pbh.getPlayerBonus());
		if (pbh.getBonus().getBonusFreeMoney()==null) return;
		if (pbh.getBonus().getBonusFreeMoney().isEmpty()) return;

		for (lithium.service.casino.data.entities.BonusFreeMoney bfm : pbh.getBonus().getBonusFreeMoney()) {
			// adjust player casino bonus balance with bonus amount.
			Response<AdjustmentTransaction> tid =
				getAccountingClient().adjustMulti(
					bfm.getAmount(),
					DateTime.now().toString(),
					CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.value(), //accountCode
					CasinoTranType.PLAYERBALANCE.value(), //accountTypeCode
					CasinoTranType.CASINO_BONUS_ACTIVATE.value(), //transactionTypeCode
					CasinoTranType.CASINO_BONUS_ACTIVATE.value() + "_FREEMONEY", //contraAccountCode
					CasinoTranType.CASINO_BONUS_ACTIVATE.value(), //contraAccountTypeCode
					new String[] {PLAYER_BONUS_HISTORY_ID+"="+pbh.getId(), BONUS_REVISION_ID+"="+pbh.getBonus().getId()},
					bfm.getCurrency(),
					pbh.getBonus().getDomain().getName(),
					pbh.getPlayerBonus().getPlayerGuid(),
						User.SYSTEM_GUID,
					false,
					null
				);

			if (!tid.isSuccessful() || tid.getData().getStatus() != AdjustmentResponseStatus.NEW) {
				log.error("Unable to allocate free money: " + pbh + " " + tid);
				throw new Exception("A technical error occurred during the transfer of the free money funds");
			}

			if ((instantBonus && pbh.getBonus().getBonusTriggerType() != BonusRevision.TRIGGER_TYPE_RAF) || (bfm.getImmediateRelease() != null && bfm.getImmediateRelease())) {
				Response<AdjustmentTransaction> tid2 = getAccountingClient().adjustMulti(
						bfm.getAmount(),
						DateTime.now().toString(),
						CasinoTranType.PLAYERBALANCE.value(), //accountCode
						CasinoTranType.PLAYERBALANCE.value(), //accountTypeCode
						CasinoTranType.TRANSFER_FROM_CASINO_BONUS.value(), //transactionTypeCode
						CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.value(), //contraAccountCode
						CasinoTranType.PLAYERBALANCE.value(), //contraAccountTypeCode
						new String[] {PLAYER_BONUS_HISTORY_ID+"="+pbh.getId(), BONUS_REVISION_ID+"="+pbh.getBonus().getId()},
						bfm.getCurrency(),
						pbh.getBonus().getDomain().getName(),
						pbh.getPlayerBonus().getPlayerGuid(),
						User.SYSTEM_GUID,
						false,
						null
				);
				if (!tid2.isSuccessful() || tid2.getData().getStatus() != AdjustmentResponseStatus.NEW) {
					log.error("Unable to allocate additional free money for instant bonus from bonus balance to real balance: " + pbh + " " + tid2);
					throw new Exception("A technical error occurred during the transfer of the additional free money funds for instant bonus from bonus to real");
				}
			}

			// TODO: This is not captured on frontend from what I could see, so there will never be a requirement in its current state. We should maybe look into this.
//			if ((bfm.getWagerRequirement() == null) || (bfm.getWagerRequirement() <= 0)) return;
//
//			Long currentPlayThrough = pbh.getPlayThroughRequiredCents();
//			if (currentPlayThrough == null) currentPlayThrough = 0L;
//			Long freeMoneyPlayThrough = bfm.getAmount() * bfm.getWagerRequirement();
//			pbh.setPlayThroughRequiredCents(currentPlayThrough + freeMoneyPlayThrough);

//			Long currentBonusAmount = pbh.getBonusAmount();
//			if (currentBonusAmount == null) currentBonusAmount = 0L;
//			pbh.setBonusAmount(currentBonusAmount + pbh.getBonus().getFreeMoneyAmount());

// Removing the line below because why would it not have a player bonus linked to it, ever. This should be handled in the caller.
//			if (pbh.getPlayerBonus() == null) pbh.setPlayerBonus(pb);
			playerBonusHistoryRepository.save(pbh);

			log.info("Allocated additional free money "
							 + bfm.getAmount()
							 + " (" + bfm.getCurrency() + ")"
//							 + " with playthrough " + freeMoneyPlayThrough
							 + " on " + pbh);
		}
	}

	public boolean triggerExternalBonusGame(PlayerBonusHistory pbh) throws Exception {
		List<BonusExternalGameConfig> extBonusGameConf = bonusExternalGameConfigRepository.findByBonusRevisionId(pbh.getBonus().getId());

		for (BonusExternalGameConfig conf : extBonusGameConf) {
			switch (conf.getProvider()) {
				case "svc-reward-pr-ext-ig":
					log.debug("Bonus ("+pbh.getBonus().getBonusCode()+" - "+pbh.getBonus().getId()+") marked as incomplete. IncentiveGames Impl. Player: "+pbh.getPlayerBonus().getPlayerGuid()+" PBH Id: "+pbh.getId());
					//TODO: implement call to IG
					pbh.setCompleted(false);
					pbh.setPlayThroughCents(0L);
					pbh.setPlayThroughRequiredCents(0L);
					pbh = playerBonusHistoryRepository.save(pbh);
					return false;
				default:
					CasinoExternalBonusGameClient extBonusClient = getCasinoExternalBonusGameClient(conf.getProvider());
					final Response<String> stringResponse = extBonusClient.generateLink(pbh.getPlayerBonus().getPlayerGuid(), conf.getCampaignId());
					if (stringResponse.isSuccessful()) {
						PlayerBonusExternalGameLink externalGameLink = PlayerBonusExternalGameLink.builder()
								.bonusExternalGameConfig(conf)
								.playerBonusHistory(pbh)
								.externalGameUrl(stringResponse.getData())
								.build();
						playerBonusExternalGameLinkRepository.save(externalGameLink);
						log.debug("Saving game link for player: " + pbh.getPlayerBonus().getPlayerGuid() + " link: " + externalGameLink);
					}
					break;
			}
		}
		return true;
	}

	public void triggerBonusTokenAllocation(PlayerBonusHistory pbh) throws Exception {
		bonusTokenService.createPlayerBonusTokens(pbh);
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	public void triggerFreeMoney(PlayerBonusHistory pbh, boolean instantBonus) throws Exception {
		boolean useCustomFreeMoney = false;
		if (pbh.getCustomFreeMoneyAmountCents() != null && pbh.getCustomFreeMoneyAmountCents() > 0L) {
			//There is a custom fm value and we mus apply it
			useCustomFreeMoney = true;
		} else {
			if (pbh.getBonus().getFreeMoneyAmount() == null) return;
			if (pbh.getBonus().getFreeMoneyAmount() <= 0L) return;
		}

		Long freeMoneyAmountToUse = useCustomFreeMoney ? pbh.getCustomFreeMoneyAmountCents() : pbh.getBonus().getFreeMoneyAmount();
		// adjust player casino bonus balance with bonus amount.
		Response<AdjustmentTransaction> tid =  
			getAccountingClient().adjustMulti(
				freeMoneyAmountToUse,
				DateTime.now().toString(),
				CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.value(), //accountCode
				CasinoTranType.PLAYERBALANCE.value(), //accountTypeCode
				CasinoTranType.CASINO_BONUS_ACTIVATE.value(), //transactionTypeCode
				CasinoTranType.CASINO_BONUS_ACTIVATE.value() + "_FREEMONEY", //contraAccountCode
				CasinoTranType.CASINO_BONUS_ACTIVATE.value(), //contraAccountTypeCode
				new String[] {PLAYER_BONUS_HISTORY_ID+"="+pbh.getId(), BONUS_REVISION_ID+"="+pbh.getBonus().getId()},
				casinoService.getCurrency(pbh.getBonus().getDomain().getName()),
				pbh.getBonus().getDomain().getName(),
				pbh.getPlayerBonus().getPlayerGuid(),
				User.SYSTEM_GUID,
				false,
				null
			);
		
		if (!tid.isSuccessful() || tid.getData().getStatus() != AdjustmentResponseStatus.NEW) {
			log.error("Unable to allocate free money: " + pbh + " " + tid);
			throw new Exception("A technical error occurred during the transfer of the free money funds");
		}

		// This will be the case for instant bonuses
		// We will transfer the required funds to real money but we will have the auditrail showing it was for a specific bonus
		// This should actually happen in a single accounting transaction, will be fixed in the bonus rework project
		//if (pbh.getCompleted()) { // safer to just pass a param
		if (instantBonus) {
			if (pbh.getBonus().getBonusTriggerType() == BonusRevision.TRIGGER_TYPE_RAF ||
					(pbh.getBonus().getBonusTriggerType() == BonusRevision.TRIGGER_TYPE_MANUAL &&
					pbh.getBonus().getFreeMoneyWagerRequirement() != null &&
					pbh.getBonus().getFreeMoneyWagerRequirement() > 0) ) {
				//Force RAF and MANUAL with a fm wager requirement to not instantly complete.
			} else {
				Response<AdjustmentTransaction> tid2 = getAccountingClient().adjustMulti(
						freeMoneyAmountToUse,
						DateTime.now().toString(),
						CasinoTranType.PLAYERBALANCE.value(), //accountCode
						CasinoTranType.PLAYERBALANCE.value(), //accountTypeCode
						CasinoTranType.TRANSFER_FROM_CASINO_BONUS.value(), //transactionTypeCode
						CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.value(), //contraAccountCode
						CasinoTranType.PLAYERBALANCE.value(), //contraAccountTypeCode
						new String[]{PLAYER_BONUS_HISTORY_ID + "=" + pbh.getId(), BONUS_REVISION_ID + "=" + pbh.getBonus().getId()},
						casinoService.getCurrency(pbh.getBonus().getDomain().getName()),
						pbh.getBonus().getDomain().getName(),
						pbh.getPlayerBonus().getPlayerGuid(),
						User.SYSTEM_GUID,
						false,
						null
				);
				if (!tid2.isSuccessful() || tid2.getData().getStatus() != AdjustmentResponseStatus.NEW) {
					log.error("Unable to allocate free money for instant bonus from bonus balance to real balance: " + pbh + " " + tid2);
					throw new Exception("A technical error occurred during the transfer of the free money funds for instant bonus from bonus to real");
				}
			}
		}
		
		if (pbh.getBonus().getFreeMoneyWagerRequirement() == null) return;
		
		Long currentPlayThrough = pbh.getPlayThroughRequiredCents();
		if (currentPlayThrough == null) currentPlayThrough = 0L;
		Long freeMoneyPlayThrough = freeMoneyAmountToUse * pbh.getBonus().getFreeMoneyWagerRequirement();
		pbh.setPlayThroughRequiredCents(currentPlayThrough + freeMoneyPlayThrough);
		
		Long currentBonusAmount = pbh.getBonusAmount();
		if (currentBonusAmount == null) currentBonusAmount = 0L;
		pbh.setBonusAmount(currentBonusAmount + freeMoneyAmountToUse);

// The line below is a bandaid for the caller messing up state, removing this.
//		if (pbh.getPlayerBonus() == null) pbh.setPlayerBonus(pb);
		playerBonusHistoryRepository.save(pbh);
		
		log.info("Allocated free money " 
				+ freeMoneyAmountToUse
				+ " with playthrough " + freeMoneyPlayThrough 
				+ " on " + pbh);
	}
	
	public void transferToPendingBonus(PlayerBonusPending pbp) throws Exception {
		//move balance from player balance to player casino bonus balance
		Response<AdjustmentTransaction> tid1 = getAccountingClient().adjustMulti(
			pbp.getTriggerAmount(),
			DateTime.now().toString(),
			CasinoTranType.PLAYER_BALANCE_CASINO_BONUS_PENDING.value(), //accountCode
			CasinoTranType.PLAYERBALANCE.value(), //accountTypeCode
			CasinoTranType.TRANSFER_TO_CASINO_BONUS_PENDING.value(), //transactionTypeCode
			CasinoTranType.PLAYERBALANCE.value(), //contraAccountCode
			CasinoTranType.PLAYERBALANCE.value(), //contraAccountTypeCode
			new String[] {BONUS_REVISION_ID+"="+pbp.getBonusRevision().getId()},
			casinoService.getCurrency(pbp.getBonusRevision().getDomain().getName()),
			pbp.getBonusRevision().getDomain().getName(),
			pbp.getPlayerGuid(),
			User.SYSTEM_GUID,
			false,
			null
		);
		
		Response<AdjustmentTransaction> tid2 = null;
		if (tid1.isSuccessful() && tid1.getData().getStatus() == AdjustmentResponseStatus.NEW) {
			// adjust player casino bonus balance with bonus amount. 
			tid2 = getAccountingClient().adjustMulti(
				pbp.getBonusAmount(),
				DateTime.now().toString(),
				CasinoTranType.PLAYER_BALANCE_CASINO_BONUS_PENDING.value(), //accountCode
				CasinoTranType.PLAYERBALANCE.value(), //accountTypeCode
				CasinoTranType.CASINO_BONUS_PENDING.value(), //transactionTypeCode
				CasinoTranType.CASINO_BONUS_PENDING.value(), //contraAccountCode
				CasinoTranType.CASINO_BONUS_PENDING.value(), //contraAccountTypeCode
				new String[] {BONUS_REVISION_ID+"="+pbp.getBonusRevision().getId()},
				casinoService.getCurrency(pbp.getBonusRevision().getDomain().getName()),
				pbp.getBonusRevision().getDomain().getName(),
				pbp.getPlayerGuid(),
				User.SYSTEM_GUID,
				false,
				null
			);
			if (!tid2.isSuccessful()) {
				
			}
		} else {
			//TODO Upstream error handling needed, uncommenting this will cause user event not to be updated.
			//throw new Exception("Problem moving funds from player balance to player casino bonus balance.");
		}
		
		try {
			casinoMailSmsService.sendBonusMail(CasinoMailSmsService.BONUS_STATE_PENDING, null, pbp);
		} catch (Exception e) {
			log.error("Failed to send bonus pending email " + pbp, e);
		}

		try {
			casinoMailSmsService.sendBonusSms(CasinoMailSmsService.BONUS_STATE_PENDING, null, pbp);
		} catch (Exception e) {
			log.error("Failed to send bonus pending sms " + pbp, e);
		}
	}
	
	public void triggerOnDeposit(PlayerBonus pb) throws Exception {
		//move balance from player balance to player casino bonus balance
		Response<AdjustmentTransaction> tid1 = getAccountingClient().adjustMulti(
			pb.getCurrent().getTriggerAmount(),
			DateTime.now().toString(),
			CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.value(), //accountCode
			CasinoTranType.PLAYERBALANCE.value(), //accountTypeCode
			CasinoTranType.TRANSFER_TO_CASINO_BONUS.value(), //transactionTypeCode
			CasinoTranType.PLAYERBALANCE.value(), //contraAccountCode
			CasinoTranType.PLAYERBALANCE.value(), //contraAccountTypeCode
			new String[] {PLAYER_BONUS_HISTORY_ID+"="+pb.getCurrent().getId(), BONUS_REVISION_ID+"="+pb.getCurrent().getBonus().getId()},
			casinoService.getCurrency(pb.getCurrent().getBonus().getDomain().getName()),
			pb.getCurrent().getBonus().getDomain().getName(),
			pb.getPlayerGuid(),
			User.SYSTEM_GUID,
			false,
			null
		);
		
		Response<AdjustmentTransaction> tid2 = null;
		if (tid1.isSuccessful() && tid1.getData().getStatus() == AdjustmentResponseStatus.NEW) {
			// adjust player casino bonus balance with bonus amount. 
			tid2 = getAccountingClient().adjustMulti(
				pb.getCurrent().getBonusAmount(),
				DateTime.now().toString(),
				CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.value(), //accountCode
				CasinoTranType.PLAYERBALANCE.value(), //accountTypeCode
				CasinoTranType.CASINO_BONUS_ACTIVATE.value(), //transactionTypeCode
				CasinoTranType.CASINO_BONUS_ACTIVATE.value(), //contraAccountCode
				CasinoTranType.CASINO_BONUS_ACTIVATE.value(), //contraAccountTypeCode
				new String[] {PLAYER_BONUS_HISTORY_ID+"="+pb.getCurrent().getId(), BONUS_REVISION_ID+"="+pb.getCurrent().getBonus().getId()},
				casinoService.getCurrency(pb.getCurrent().getBonus().getDomain().getName()),
				pb.getCurrent().getBonus().getDomain().getName(),
				pb.getPlayerGuid(),
				User.SYSTEM_GUID,
				false,
				null
			);
			if (!tid2.isSuccessful()) {
				
			}
		} else {
			//TODO Upstream error handling needed, uncommenting this will cause user event not to be updated.
			//throw new Exception("Problem moving funds from player balance to player casino bonus balance.");
		}
	}
	
	//key = "{#domainName,#gameGuid,#bonusRevisionId}", 
	@Cacheable(cacheNames="lithium.service.casino.gamepercentage", unless="#result == null")
	public BonusRulesGamesPercentages getGamePercentage(String domainName, String gameGuid, Long bonusRevisionId) throws Exception {
		log.info("Getting GamePercentage");
		BonusRulesGamesPercentages bonusRulesGamesPercentages = bonusRulesGamesPercentagesRepository.findByGameGuidAndBonusRevisionId(gameGuid, bonusRevisionId);
		if (bonusRulesGamesPercentages != null) { 
			return bonusRulesGamesPercentages;
		} else {
			List<BonusRulesGamesPercentages> gamePercentageList = bonusRulesGamesPercentagesRepository.findByBonusRevisionIdAndGameGuidIsNullAndGameCategoryIsNotNull(bonusRevisionId);
			List<lithium.service.games.client.objects.Game> gameList = casinoBonusFreespinService.getDomainGameList(domainName);
			Optional<lithium.service.games.client.objects.Game> game = gameList.stream()
			.filter(g -> {
				if (g == null) return false;
				return (g.getGuid().equalsIgnoreCase(gameGuid.replaceAll("/", "_"))) ? true : false;
			}).findFirst();
			
			log.info("game: "+game);
			if (game.isPresent()) {
				
				log.info("found "+game);
				Optional<BonusRulesGamesPercentages> gamePercentage = gamePercentageList.stream()
				.filter(gpl -> {
					if (gpl == null) return false;
					
					return isGameCategoryMatchingGamePercentageCategory(game.get().getLabels().get("category").getValue(), gpl.getGameCategory());
				}).findFirst();
				if (gamePercentage.isPresent()) {
					return gamePercentage.get();
				} else {
					return BonusRulesGamesPercentages.builder().percentage(100).build();
				}
			}
			
			return BonusRulesGamesPercentages.builder().percentage(100).build();
		}
	}
	
	private boolean isGameCategoryMatchingGamePercentageCategory(final String gameLabelCategory, final String gamePercentageCategory) {
		boolean match = false;
		String[] gameCategoryValueList = gameLabelCategory.split(",");
		for (int p=0; p < gameCategoryValueList.length; ++p) {
			//if (gamePercentageCategory.contentEquals(gameCategoryValueList[p])) match = true;
			//change to semi-match cause LEthu said it would be fine
			if (gamePercentageCategory.startsWith(gameCategoryValueList[p])) match = true;
		}
		
		return match;
	}
	
	public TranProcessResponse process(BetRequest request, PlayerBonus pCurrentBonus) {
		try {
			return metrics.timer(log).time("processBet", (StopWatch sw) -> {

				bonusRoundTrackService.createOrUpdateBonusRound(request, pCurrentBonus.getCurrent());
				TranProcessResponse tranProcessResponse = TranProcessResponse.builder().tranId(-1L).build();
				PlayerBonus newCurrentBonus = pCurrentBonus; // required to make pending bonus stuff work
				int freespinsRemaining = -2;
				if ((request.getBonusTran()!=null) && (request.getBonusTran())) {
					sw.start("updating freespin");
					freespinsRemaining = casinoBonusFreespinService.subtractFreespin(request.getUserGuid(), request.getBonusId());
					sw.stop();
				}
				AdjustmentTransaction adjTran = AdjustmentTransaction.builder().status(AdjustmentResponseStatus.ERROR).transactionId(-1L).build();
				//Long responseTranId = -1L;
				Long amountBetCents = request.getBet();
				if (amountBetCents == null) amountBetCents = 0L;
				Long amountNegBetCents = request.getNegativeBet();
				Long amountWinCents = request.getWin();
				String domainName = request.getDomainName();
				String playerGuid = request.getUserGuid();
				String gameGuid = request.getGameGuid();
				
				boolean writeZeroValueTran = ((request.getBet() == null) || ((request.getBet() != null) && (request.getBet() == 0))) 
						&& ((request.getWin() == null) || ((request.getWin() != null) && (request.getWin() == 0)))
						&& ((request.getNegativeBet() == null) || ((request.getNegativeBet() != null) && (request.getNegativeBet() == 0)));
				
				boolean isFreespin = false;
				if (request.getGameGuid() != null) {
					isFreespin = request.getGameGuid().startsWith("frb"); //Freespins from betsoft and nucleus starts with a game guid of frb, this might change in future, but then we will handle it
				}
				
				log.info("Casino bonus service process transaction " + request + " " + newCurrentBonus);
				if (newCurrentBonus == null) { return tranProcessResponse; }
				if (newCurrentBonus.getCurrent()==null) { return tranProcessResponse; }
				if (newCurrentBonus.getCurrent().getBonus()==null) { return tranProcessResponse; }
				if (!newCurrentBonus.getCurrent().getBonus().isEnabled()) { return tranProcessResponse; }
				
				sw.start("checkValidDays");
				if (!checkWithinValidDays(newCurrentBonus)) {
					log.info("Casino bonus expired " + request + " " + newCurrentBonus);
					sw.stop();
					tranProcessResponse.setTranId(-1L);
					return tranProcessResponse;
				}
				sw.stop();

				Long bonusBalanceRemaining = getCasinoBonusBalance(newCurrentBonus);
				if ((amountBetCents > bonusBalanceRemaining) && !isBonusAllowedToEnd(newCurrentBonus)) {
					log.warn("Player placed a bet that would cause a bonus that still have active rounds left, to end. Returning error");
					tranProcessResponse.setTranId(-1L);
					return tranProcessResponse;
				}
				
				//TODO : Game Percentages.
//				sw.start("findByGameGuidAndBonusRevisionId");
//				BonusRulesGamesPercentages rule = bonusRulesGamesPercentagesRepository.findByGameGuidAndBonusRevisionId(gameGuid, currentBonus.getCurrent().getBonus().getId());
//				sw.stop();
//				int percentage = 100;
//				
//				if (rule != null) { 
//					log.debug("rule : "+rule);
//					percentage = rule.getPercentage();
//				}

				
				if (((request.getBet() != null) && (request.getBet() != 0)) || writeZeroValueTran) {
					sw.start("beforeBetBonusCompletedCheck");
					//TODO: This needs a revisit
					log.debug("freespinsRemaining(Bet) :: "+freespinsRemaining);
					//If not a bonus tran. Check if bonus is completed
					if ((request.getBonusTran()!=null) && (!request.getBonusTran())) {
						if (isBonusCompleted(newCurrentBonus)) { //bonus completed check activates next pending bonus if available so we do check for next active bonus
							if (findCurrentBonus(newCurrentBonus.getPlayerGuid()) == null) {
								// Bonus is already completed, re-process as a normal bet.
								log.debug("Bonus is already completed, re-process as a normal bet..");
								tranProcessResponse.setTranId(-2L);
								return tranProcessResponse;
							}
						}
					} else {
						if (isBonusCompleted(newCurrentBonus)) {
							if (findCurrentBonus(newCurrentBonus.getPlayerGuid()) == null) {
								// Bonus is already completed, re-process as a normal bet.
								log.debug("Bonus is already completed, re-process as a normal bet..");
								tranProcessResponse.setTranId(-2L);
								return tranProcessResponse;
							}
						}
					}
					sw.stop();
					sw.start("processBet");

					Long balanceDeficit = amountBetCents;
					int zeroBalanceTranCounter = 0;
					//While money can be deducted from the active bonus perform loop
					int counter = -1;
					while ((balanceDeficit > 0L && newCurrentBonus != null && newCurrentBonus.getBalance() > 0L) || (writeZeroValueTran && zeroBalanceTranCounter == 0)) {
						counter++;
						balanceDeficit -= newCurrentBonus.getBalance();
						if (newCurrentBonus.getCurrent().getBonus().isCancelOnBetBiggerThanBalance() || balanceDeficit <= 0L || writeZeroValueTran) {
							log.info("Bet was more than available balance and cancel requested " + newCurrentBonus + " amountBetCents " + amountBetCents + " bonusBalance " + newCurrentBonus.getBalance());
							long bonusTranAmount = 0L;
							// Subtract available balance in bonus from remaining outstanding amount and perform transaction
							// end-senario is bonus has more balance than is required for bet, then only take required amount from bonus
							if (!writeZeroValueTran) {
								if (balanceDeficit > 0L) {
									bonusTranAmount = newCurrentBonus.getBalance();
									amountBetCents -= newCurrentBonus.getBalance();
								} else {
									bonusTranAmount = amountBetCents;
									
								}
							} else {
								++zeroBalanceTranCounter;
							}
							String transactionId = request.getTransactionId();
							if (counter > 0) {
								transactionId += "_" + newCurrentBonus.getCurrent().getId(); // Appending pbhid to keep transaction id unique
							}
							adjTran = processBet(newCurrentBonus, bonusTranAmount, domainName, playerGuid, transactionId,
									request.getProviderGuid(), gameGuid, isFreespin, request.getGameSessionId(),
									request.getCurrencyCode(), request.getAdditionalReference(), request.getSessionId());
							if (adjTran.getStatus() !=  AdjustmentResponseStatus.NEW) {
								//FIXME: Adding the duplicate check caused rollback shit to happen. We might need to do some other magic to cater for this
								//if (adjTran.getAdjustmentResponse() ==  AdjustmentResponse.DUPLICATE) {
								//	return adjTran.getTransactionId();
								//}
								if (adjTran.getStatus() ==  AdjustmentResponseStatus.DUPLICATE) {
									tranProcessResponse.setDuplicate(true);
								}
								tranProcessResponse.setTranId(-1L);
								return tranProcessResponse;
							}
							//sw.start("savePlayThrough");
							savePlayThrough(newCurrentBonus, bonusTranAmount, getGameRulePercentage(domainName, gameGuid, newCurrentBonus.getCurrent().getBonus().getId()));
							tranProcessResponse.setTranId(adjTran.getTransactionId());
							//sw.stop();

							//Apply bonus completion check, will move pending to active. Perform loop for bonus money allocation again if required after getting latest active bonus

							isBonusCompleted(newCurrentBonus);
							newCurrentBonus = findCurrentBonus(newCurrentBonus.getPlayerGuid());
						} else {
							//FIXME: This else statement could have transactional inconsistency impact if it is a pending bonus chain with a non-cancelable bonus somewhere in the bet resolution and it is not the last one in the chain
							log.info("Bet was more than available balance and cancel not allowed " + newCurrentBonus + " amountBetCents " + amountBetCents + " bonusBalance " + newCurrentBonus.getBalance());
							tranProcessResponse.setTranId(-1L);
							return tranProcessResponse;
						}
						
					}
					
					if (balanceDeficit > 0L) {
						tranProcessResponse = casinoService.processBet(
								balanceDeficit, domainName, playerGuid, request.getTransactionId() + "_DEFICIT",
								request.getProviderGuid(), gameGuid, request.getGameSessionId(),
								request.getCurrencyCode(),
								request.getTranType() == null ? CasinoTranType.CASINO_BET : request.getTranType(),
								null, request.getAdditionalReference(), request.getSessionId());
						if (tranProcessResponse.getTranId() <= 0) {
							tranProcessResponse.setTranId(-1L);
							return tranProcessResponse;
						}
					}

					sw.stop();
				}
				
				if ((request.getNegativeBet() != null) && (request.getNegativeBet() != 0)) {
					sw.start("processNegativeBet");
					adjTran = processNegativeBet(newCurrentBonus, amountNegBetCents, domainName, playerGuid,
							request.getTransactionId(), request.getProviderGuid(), gameGuid, request.getGameSessionId(),
							request.getCurrencyCode(), request.getSessionId());
					sw.stop();
					sw.start("savePlayThrough");
					if (adjTran.getStatus() ==  AdjustmentResponseStatus.NEW) {
						savePlayThrough(newCurrentBonus, amountNegBetCents, getGameRulePercentage(domainName, gameGuid, newCurrentBonus.getCurrent().getBonus().getId()));
					}
					if(tranProcessResponse.getTranId() == -1L) {
						tranProcessResponse.setTranId(adjTran.getTransactionId());
						if (adjTran.getStatus() ==  AdjustmentResponseStatus.DUPLICATE) {
							tranProcessResponse.setDuplicate(true);
						}
					}
					sw.stop();
				}
				
				if ((request.getWin() != null) && ((request.getBonusTran()!=null) && (request.getBonusTran()))) {
					sw.start("processWin");
					adjTran = processWin(newCurrentBonus, amountWinCents, domainName, playerGuid, request.getTransactionId(),
							request.getProviderGuid(), gameGuid, isFreespin, request.getGameSessionId(),
							request.getCurrencyCode(), request.getOriginalTransactionId(), request.getSessionId());
					sw.stop();
					sw.start("afterBetBonusCompletedCheck");
					log.debug("freespinsRemaining :: "+freespinsRemaining);
					casinoBonusFreespinService.updatePlayThrough(request, newCurrentBonus);
					tranProcessResponse.setTranId(adjTran.getTransactionId());
					if (adjTran.getStatus() ==  AdjustmentResponseStatus.DUPLICATE) {
						tranProcessResponse.setDuplicate(true);
					}
					sw.stop();
					winnerFeedService.addWinner(request);
				} else if ((request.getWin() != null) && (request.getWin() != 0)) {
					sw.start("processWin");
					adjTran = processWin(newCurrentBonus, amountWinCents, domainName, playerGuid, request.getTransactionId(),
							request.getProviderGuid(), gameGuid, isFreespin, request.getGameSessionId(),
							request.getCurrencyCode(), request.getOriginalTransactionId(), request.getSessionId());
					sw.stop();
					sw.start("afterBetBonusCompletedCheck");
					isBonusCompleted(newCurrentBonus);
					tranProcessResponse.setTranId(adjTran.getTransactionId());
					if (adjTran.getStatus() ==  AdjustmentResponseStatus.DUPLICATE) {
						tranProcessResponse.setDuplicate(true);
					}
					sw.stop();
					winnerFeedService.addWinner(request);
				}
				
				registerUserEventPlayerBonusPostTransactionDisplay(domainName, playerGuid);
				
				//return responseTranId;
				return tranProcessResponse;
			});
		} catch (Exception e) {
			log.error("Could not process casino transaction.", e);
			return TranProcessResponse.builder().tranId(-1L).build();
		}
	}
	
	private int getGameRulePercentage(String domainName, String gameGuid, long bonusId) throws Exception {
		BonusRulesGamesPercentages rule = getGamePercentage(domainName, gameGuid, bonusId);
		int percentage = 100;
		if (rule != null) { 
			log.info("rule : "+rule);
			percentage = rule.getPercentage();
		}
		return percentage;
	}
	
	public int getGameRulePercentage(String domainName, String gameGuid, PlayerBonusHistory playerBonusHistory) throws Exception {
		int percentage = 100;
		if (playerBonusHistory != null) {
			BonusRulesGamesPercentages rule = getGamePercentage(domainName, gameGuid, playerBonusHistory.getBonus().getBonus().getId());
			if (rule != null) { 
				log.info("rule : "+rule);
				percentage = rule.getPercentage();
			}
		}
		return percentage;
	}
	
	private String accountCodeFromProviderGuid(String providerGuid) {
		int lastDash = providerGuid.lastIndexOf('-');
		return providerGuid.substring(lastDash+1).toUpperCase();
	}
	private AdjustmentTransaction processBet(PlayerBonus currentBonus, Long amountCents, String domainName,
	        String userGuid, String tranId, String providerGuid, String gameGuid, boolean isFreespin,
	        String gameSessionId, String currencyCode, String additionalReference, Long sessionId) throws Exception {
		LabelManager labelManager = LabelManager.instance()
				.addLabel(LabelManager.TRANSACTION_ID, tranId)
				.addLabel(LabelManager.PROVIDER_GUID, providerGuid)
				.addLabel(LabelManager.GAME_GUID, gameGuid);
		if (sessionId != null) {
			labelManager.addLabel(LabelManager.LOGIN_EVENT_ID, String.valueOf(sessionId));
		}
		if (gameSessionId != null) {
			labelManager.addLabel(LabelManager.GAME_SESSION_ID, gameSessionId);
		}
		//case where bet completed bonus so negative bet is not tied to bonus anymore
		boolean noMoreBonus = false;
		if (currentBonus == null || currentBonus.getCurrent() == null || currentBonus.getCurrent().getId() == null) {
//			labelManager.addLabel(LabelManager.PLAYER_BONUS_HISTORY_ID, "-1");
//			labelManager.addLabel(LabelManager.BONUS_REVISION_ID, "-1");
			noMoreBonus = true;
		} else {
			labelManager.addLabel(LabelManager.PLAYER_BONUS_HISTORY_ID, String.valueOf(currentBonus.getCurrent().getId()));
			labelManager.addLabel(LabelManager.BONUS_REVISION_ID, String.valueOf(currentBonus.getCurrent().getBonus().getId()));
		}

		AdjustmentTransaction accTranId = null;
		if (noMoreBonus) {
			TranProcessResponse tpr = casinoService.processBet(amountCents, domainName, userGuid, tranId,
					providerGuid, gameGuid, gameSessionId, currencyCode,
					CasinoTranType.CASINO_BET, null, additionalReference, sessionId);
			if (tpr.isDuplicate()) {
				accTranId = AdjustmentTransaction.builder().status(AdjustmentResponseStatus.DUPLICATE)
						.transactionId(tpr.getTranId()).build();
			} else {
				accTranId = AdjustmentTransaction.builder().status(AdjustmentResponseStatus.NEW)
					.transactionId(tpr.getTranId()).build();
			}
		} else {
			Response<AdjustmentTransaction> accTranResp = getAccountingClient().adjustMulti(
					(Math.abs(amountCents))*-1, 
					new DateTime().toDateTimeISO().toString(),
					CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.value(), //accountCode
					CasinoTranType.PLAYERBALANCE.value(), //accountTypeCode
					isFreespin ? CasinoTranType.CASINO_BET_FREESPIN.toString() : CasinoTranType.CASINO_BET.toString(), //transactionTypeCode
					"CASINO_BET_" + accountCodeFromProviderGuid(providerGuid), //contraAccountCode
					CasinoTranType.CASINO_BET.toString(), //contraAccountTypeCode
					labelManager.getLabelArray(),
					casinoService.getCurrency(domainName), 
					domainName, 
					userGuid,
					User.SYSTEM_GUID,
					false,
					null
				);
			
			if (accTranResp != null && accTranResp.getStatus() == Status.OK) {
				accTranId = accTranResp.getData();
			} else {
				log.error("processBet unsuccessful. "+accTranResp);
				accTranId = AdjustmentTransaction.builder().status(AdjustmentResponseStatus.ERROR).transactionId(-1L).build();
			}

// This now gets handled by completed accounting rabbit events being processed in serviceXP
//			try {
//				if (accTranId.getAdjustmentResponse() == AdjustmentResponse.NEW) {
//					xpService.processXp(domainName, userGuid, accTranId.getTransactionId(), amountCents);
//				}
//			} catch (Exception e) {
//				log.error("Problem processing xp gain (" + accTranId.getTransactionId() + ")" + e.getMessage(), e);
//			}
			
			try {
				if (accTranId.getStatus() == AdjustmentResponseStatus.NEW) {
					casinoGeoService.addTransactionGeoDeviceLabels(userGuid, accTranId.getTransactionId());
				}
			} catch (Exception e) {
				log.error("Problem adding geo device labels to transaction (" + accTranId.getTransactionId() + ")" + e.getMessage(), e);
			}
		}
		
		return accTranId;
	}
	
	public AdjustmentTransaction processNegativeBet(PlayerBonus currentBonus, Long amountCents, String domainName,
	        String userGuid, String tranId, String providerGuid, String gameGuid, String gameSessionId,
	        String currencyCode, Long sessionId) throws Exception {
		LabelManager labelManager = LabelManager.instance()
				.addLabel(LabelManager.TRANSACTION_ID, tranId)
				.addLabel(LabelManager.PROVIDER_GUID, providerGuid)
				.addLabel(LabelManager.GAME_GUID, gameGuid);
		if (sessionId != null) {
			labelManager.addLabel(LabelManager.LOGIN_EVENT_ID, String.valueOf(sessionId));
		}
		if (gameSessionId != null) {
			labelManager.addLabel(LabelManager.GAME_SESSION_ID, gameSessionId);
		}
		//case where bet completed bonus so negative bet is not tied to bonus anymore
		boolean noMoreBonus = false;
		if (currentBonus == null || currentBonus.getCurrent() == null || currentBonus.getCurrent().getId() == null) {
//			labelManager.addLabel(LabelManager.PLAYER_BONUS_HISTORY_ID, "-1");
//			labelManager.addLabel(LabelManager.BONUS_REVISION_ID, "-1");
			noMoreBonus = true;
		} else {
			labelManager.addLabel(LabelManager.PLAYER_BONUS_HISTORY_ID, String.valueOf(currentBonus.getCurrent().getId()));
			labelManager.addLabel(LabelManager.BONUS_REVISION_ID, String.valueOf(currentBonus.getCurrent().getBonus().getId()));
		}
		
		AdjustmentTransaction accTranId = null;
		if (noMoreBonus) {
			TranProcessResponse tpr = casinoService.processNegativeBet(amountCents, domainName, userGuid, tranId,
					providerGuid, gameGuid, gameSessionId, currencyCode, sessionId);
			if (tpr.isDuplicate()) {
				accTranId = AdjustmentTransaction.builder().status(AdjustmentResponseStatus.DUPLICATE)
						.transactionId(tpr.getTranId()).build();
			} else {
				accTranId = AdjustmentTransaction.builder().status(AdjustmentResponseStatus.NEW)
					.transactionId(tpr.getTranId()).build();
			}
		} else {
			Response<AdjustmentTransaction> accTranResp = getAccountingClient().adjustMulti(
				Math.abs(amountCents), 
				new DateTime().toDateTimeISO().toString(),
				CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.value(), //accountCode
				CasinoTranType.PLAYERBALANCE.value(), //accountTypeCode
				CasinoTranType.CASINO_NEGATIVE_BET.toString(), //transactionTypeCode
				"CASINO_NEGATIVEBET_" + accountCodeFromProviderGuid(providerGuid), //contraAccountCode
				CasinoTranType.CASINO_NEGATIVE_BET.toString(), //contraAccountTypeCode
				labelManager.getLabelArray(),
				casinoService.getCurrency(domainName), 
				domainName, 
				userGuid,
				User.SYSTEM_GUID,
				false,
				null
			);
			
			if (accTranResp != null && accTranResp.getStatus() == Status.OK) {
				accTranId = accTranResp.getData();
			} else {
				log.error("processNagativeBet unsuccessful. "+accTranResp);
				accTranId = AdjustmentTransaction.builder().status(AdjustmentResponseStatus.ERROR).transactionId(-1L).build();
			}
			
			try {
				if (accTranId.getStatus() == AdjustmentResponseStatus.NEW) {
					casinoGeoService.addTransactionGeoDeviceLabels(userGuid, accTranId.getTransactionId());
				}
			} catch (Exception e) {
				log.error("Problem adding geo device labels to transaction (" + accTranId.getTransactionId() + ")" + e.getMessage(), e);
			}
		}
		
		return accTranId;
	}
	
	public AdjustmentTransaction processWin(PlayerBonus currentBonus, Long amountCents, String domainName,
				String userGuid, String tranId, String providerGuid, String gameGuid, boolean isFreespin,
				String gameSessionId, String currencyCode, Long originalTransactionId, Long sessionId) throws Exception {
		LabelManager labelManager = LabelManager.instance()
				.addLabel(LabelManager.TRANSACTION_ID, tranId)
				.addLabel(LabelManager.PROVIDER_GUID, providerGuid)
				.addLabel(LabelManager.GAME_GUID, gameGuid);
		if (sessionId != null) {
			labelManager.addLabel(LabelManager.LOGIN_EVENT_ID, String.valueOf(sessionId));
		}
		if (gameSessionId != null) {
			labelManager.addLabel(LabelManager.GAME_SESSION_ID, gameSessionId);
		}
		//case where bet completed bonus so win is not tied to bonus anymore
		boolean noMoreBonus = false;
		if (currentBonus == null || currentBonus.getCurrent() == null || currentBonus.getCurrent().getId() == null) {
//			labelManager.addLabel(LabelManager.PLAYER_BONUS_HISTORY_ID, "-1");
//			labelManager.addLabel(LabelManager.BONUS_REVISION_ID, "-1");
			noMoreBonus = true;
		} else {
			labelManager.addLabel(LabelManager.PLAYER_BONUS_HISTORY_ID, String.valueOf(currentBonus.getCurrent().getId()));
			labelManager.addLabel(LabelManager.BONUS_REVISION_ID, String.valueOf(currentBonus.getCurrent().getBonus().getId()));
		}
		
		AdjustmentTransaction accTranId = null;
		if (noMoreBonus) {
			TranProcessResponse tpr = casinoService.processWin(
					amountCents, domainName, userGuid, tranId, providerGuid, gameGuid, gameSessionId,
					currencyCode, CasinoTranType.CASINO_WIN, originalTransactionId, null, sessionId);
			if (tpr.isDuplicate()) {
				accTranId = AdjustmentTransaction.builder().status(AdjustmentResponseStatus.DUPLICATE)
						.transactionId(tpr.getTranId()).build();
			} else {
				accTranId = AdjustmentTransaction.builder().status(AdjustmentResponseStatus.NEW)
					.transactionId(tpr.getTranId()).build();
			}
		} else {
			Response<AdjustmentTransaction> accTranResp = getAccountingClient().adjustMulti(
					(Math.abs(amountCents)), 
					new DateTime().toDateTimeISO().toString(),
					CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.value(), //accountCode
					CasinoTranType.PLAYERBALANCE.value(), //accountTypeCode
					isFreespin ? CasinoTranType.CASINO_WIN_FREESPIN.toString() : CasinoTranType.CASINO_WIN.toString(), //transactionTypeCode
					"CASINO_WIN_" + accountCodeFromProviderGuid(providerGuid), //contraAccountCode
					CasinoTranType.CASINO_WIN.toString(), //contraAccountTypeCode
					labelManager.getLabelArray(),
					casinoService.getCurrency(domainName), 
					domainName, 
					userGuid,
					User.SYSTEM_GUID,
					false,
					null
				);
			
			if (accTranResp != null && accTranResp.getStatus() == Status.OK) {
				accTranId = accTranResp.getData();
			} else {
				log.error("processBet unsuccessful. "+accTranResp);
				accTranId = AdjustmentTransaction.builder().status(AdjustmentResponseStatus.ERROR).transactionId(-1L).build();
			}
			
			try {
				if (accTranId.getStatus() == AdjustmentResponseStatus.NEW) {
					casinoGeoService.addTransactionGeoDeviceLabels(userGuid, accTranId.getTransactionId());
				}
			} catch (Exception e) {
				log.error("Problem adding geo device labels to transaction (" + accTranId.getTransactionId() + ")" + e.getMessage(), e);
			}
		}
		
		return accTranId;
	}

	private void clearAdditionalFreemoneyPlayerBalanceCasinoBonus(PlayerBonus playerBonus, CasinoTranType tranType) throws Exception {
		BonusRevision bonus = playerBonus.getCurrent().getBonus();
		boolean instantBonus = false;
		if (bonus.getBonusType() == BonusRevision.BONUS_TYPE_TRIGGER && bonus.getBonusTriggerType() != BonusRevision.TRIGGER_TYPE_RAF) {
			instantBonus = true;
		} else {
			if ((bonus.getFreeMoneyWagerRequirement() != null && bonus.getFreeMoneyWagerRequirement() <= 0) &&
				(playerBonus.getCurrent().getPlayThroughRequiredCents() != null && playerBonus.getCurrent().getPlayThroughRequiredCents() <= 0)) {
					instantBonus = true;
			}
		}
		List<lithium.service.casino.data.entities.BonusFreeMoney> bonusFreeMoneyList = bonusFreeMoneyRepository.findByBonusRevisionId(playerBonus.getCurrent().getBonus().getId());
		for (lithium.service.casino.data.entities.BonusFreeMoney bonusFreeMoney: bonusFreeMoneyList) {
			if ((!instantBonus) && (bonusFreeMoney.getImmediateRelease() == null || !bonusFreeMoney.getImmediateRelease())) {
				getAccountingClient().adjustMulti(
					bonusFreeMoney.getAmount(),
					DateTime.now().toString(),
					tranType.value(), //accountCode
					tranType.value(), //accountTypeCode
					CasinoTranType.TRANSFER_FROM_CASINO_BONUS.value(), //transactionTypeCode
					CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.value(), //contraAccountCode
					CasinoTranType.PLAYERBALANCE.value(), //contraAccountTypeCode
					new String[] {PLAYER_BONUS_HISTORY_ID+"="+playerBonus.getCurrent().getId(), BONUS_REVISION_ID+"="+playerBonus.getCurrent().getBonus().getId()},
					bonusFreeMoney.getCurrency(),
					playerBonus.getCurrent().getBonus().getDomain().getName(),
					playerBonus.getPlayerGuid(),
					User.SYSTEM_GUID,
					false,
					null
				);
			}
		}
	}
	
	private void moveCancelledCasinoBonusBalance(PlayerBonus playerBonus) throws Exception {
		
		if (!isBonusAllowedToEnd(playerBonus)) return;
		
		Long casinoBonusBalance = getCasinoBonusBalance(playerBonus);
		log.info("casinoBonusBalance : "+casinoBonusBalance);

		Response<SummaryAccountLabelValueType> salvg = getAccountingSummaryTransactionTypeService().summaryAccountLabelValueType(
			Period.GRANULARITY_TOTAL,
			CasinoTranType.CASINO_WIN.value(),
			CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.value(),
			URLEncoder.encode(playerBonus.getPlayerGuid(), "UTF-8"),
			playerBonus.getCurrent().getBonus().getDomain().getName(),
			casinoService.getCurrency(playerBonus.getCurrent().getBonus().getDomain().getName()),
			playerBonus.getCurrent().getId().toString(),
			PLAYER_BONUS_HISTORY_ID
		);
		log.info("salvg : "+salvg);
		if (salvg == null) throw new Exception("Could not retrieve win amount for bonus.");
		if (!salvg.isSuccessful()) throw new Exception("Could not retrieve win amount for bonus.");
		Long winnings = 0L;
		if (salvg.getData() != null) {
			winnings = salvg.getData().getCreditCents();
		}
		log.info("winnings : "+winnings);
		Long depositCents = playerBonus.getCurrent().getTriggerAmount();
		log.info("depositCents : "+depositCents);
		
		BigDecimal bonusAmount = new BigDecimal(playerBonus.getCurrent().getBonusAmount());
		log.info("bonusAmount : "+bonusAmount);
		BigDecimal winningsAndBonusAmount = bonusAmount.add(new BigDecimal(winnings));
		log.info("winningsAndBonusAmount : "+winningsAndBonusAmount);
		BigDecimal leftover = new BigDecimal(casinoBonusBalance).subtract(winningsAndBonusAmount);
		log.info("leftover : "+leftover.longValue());
		if (leftover.longValue() > 0L) {
			//transfer back to PB.
			getAccountingClient().adjustMulti(
				leftover.longValue(),
				DateTime.now().toString(),
				CasinoTranType.PLAYERBALANCE.value(), //accountCode
				CasinoTranType.PLAYERBALANCE.value(), //accountTypeCode
				CasinoTranType.TRANSFER_FROM_CASINO_BONUS.value(), //transactionTypeCode
				CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.value(), //contraAccountCode
				CasinoTranType.PLAYERBALANCE.value(), //contraAccountTypeCode
				new String[] {PLAYER_BONUS_HISTORY_ID+"="+playerBonus.getCurrent().getId(), BONUS_REVISION_ID+"="+playerBonus.getCurrent().getBonus().getId()},
				casinoService.getCurrency(playerBonus.getCurrent().getBonus().getDomain().getName()),
				playerBonus.getCurrent().getBonus().getDomain().getName(),
				playerBonus.getPlayerGuid(),
				User.SYSTEM_GUID,
				false,
				null
			);
			getAccountingClient().adjustMulti(
				(casinoBonusBalance - leftover.longValue()),
				DateTime.now().toString(),
				CasinoTranType.CASINO_BONUS_CANCEL.value(), //accountCode
				CasinoTranType.CASINO_BONUS_CANCEL.value(), //accountTypeCode
				CasinoTranType.TRANSFER_FROM_CASINO_BONUS.value(), //transactionTypeCode
				CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.value(), //contraAccountCode
				CasinoTranType.PLAYERBALANCE.value(), //contraAccountTypeCode
				new String[] {PLAYER_BONUS_HISTORY_ID+"="+playerBonus.getCurrent().getId(), BONUS_REVISION_ID+"="+playerBonus.getCurrent().getBonus().getId()},
				casinoService.getCurrency(playerBonus.getCurrent().getBonus().getDomain().getName()),
				playerBonus.getCurrent().getBonus().getDomain().getName(),
				playerBonus.getPlayerGuid(),
				User.SYSTEM_GUID,
				false,
				null
			);
		} else {
			//Nothing goes to PB, clear PCBB.
			getAccountingClient().adjustMulti(
				casinoBonusBalance,
				DateTime.now().toString(),
				CasinoTranType.CASINO_BONUS_CANCEL.value(), //accountCode
				CasinoTranType.CASINO_BONUS_CANCEL.value(), //accountTypeCode
				CasinoTranType.TRANSFER_FROM_CASINO_BONUS.value(), //transactionTypeCode
				CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.value(), //contraAccountCode
				CasinoTranType.PLAYERBALANCE.value(), //contraAccountTypeCode
				new String[] {PLAYER_BONUS_HISTORY_ID+"="+playerBonus.getCurrent().getId(), BONUS_REVISION_ID+"="+playerBonus.getCurrent().getBonus().getId()},
				casinoService.getCurrency(playerBonus.getCurrent().getBonus().getDomain().getName()),
				playerBonus.getCurrent().getBonus().getDomain().getName(),
				playerBonus.getPlayerGuid(),
				User.SYSTEM_GUID,
				false,
				null
			);
		}
		
	}
	
	public void copyPlayerBonus(PlayerBonus from, PlayerBonus to) {
		to.setPlayerGuid(from.getPlayerGuid());
		to.setBalance(from.getBalance());
		to.setCurrent(from.getCurrent());
	}
	
	public void completeCancelledBonus(PlayerBonus playerBonus) throws Exception {
		PlayerBonus playerBonusCopy = new PlayerBonus();
		copyPlayerBonus(playerBonus, playerBonusCopy);
		casinoBonusFreespinService.cancelFreespins(playerBonus);
		moveCancelledCasinoBonusBalance(playerBonus);
		clearAdditionalFreemoneyPlayerBalanceCasinoBonus(playerBonus, CasinoTranType.CASINO_BONUS_CANCEL);
		PlayerBonusHistory pbh = playerBonus.getCurrent();
		pbh.setCancelled(true);
		playerBonusHistoryRepository.save(pbh);
		playerBonus.setCurrent(null);
		playerBonusRepository.save(playerBonus);
		
		moveBalanceFromPendingBonusToActiveBonus(playerBonus);
		
		try {
			casinoMailSmsService.sendBonusMail(CasinoMailSmsService.BONUS_STATE_CANCEL, pbh, null);
		} catch (Exception e) {
			log.error("Failed to send bonus cancel email " + playerBonus, e);
		}

		try {
			casinoMailSmsService.sendBonusSms(CasinoMailSmsService.BONUS_STATE_CANCEL, pbh, null);
		} catch (Exception e) {
			log.error("Failed to send bonus cancel sms " + playerBonus, e);
		}
	}
	
	public Long getCasinoBonusBalance(PlayerBonus playerBonus) throws Exception {
		Response<Long> bonusBalance = getAccountingClient().getByOwnerGuid(
			playerBonus.getCurrent().getBonus().getDomain().getName(),
			"PLAYER_BALANCE_CASINO_BONUS",
			"PLAYER_BALANCE",
			casinoService.getCurrency(playerBonus.getCurrent().getBonus().getDomain().getName()),
			playerBonus.getPlayerGuid()
		);
		if (bonusBalance == null || bonusBalance.getStatus() != Status.OK) {
			log.error("Could not retrieve player casino bonus balance.");
			//sou 
			throw new Exception("player casino bonus balance unavailable.");
		}
		playerBonus.setBalance(bonusBalance.getData());
		return bonusBalance.getData();
	}
	
	public Long getCasinoBonusBalance(PlayerBonus playerBonus, String currency) throws Exception {
		Response<Long> bonusBalance = getAccountingClient().getByOwnerGuid(
			playerBonus.getCurrent().getBonus().getDomain().getName(),
			"PLAYER_BALANCE_CASINO_BONUS",
			"PLAYER_BALANCE",
			currency,
			playerBonus.getPlayerGuid()
		);
		if (bonusBalance == null || bonusBalance.getStatus() != Status.OK) {
			log.error("Could not retrieve player casino bonus balance.");
			//sou 
			throw new Exception("player casino bonus balance unavailable.");
		}
		return bonusBalance.getData();
	}
	
	public void moveAdditionalFreemoneyBalancesFromCasinoBonusToPlayer(PlayerBonus playerBonus) throws Exception {
		List<lithium.service.casino.data.entities.BonusFreeMoney> additionalFreemoney =
				bonusFreeMoneyRepository.findByBonusRevisionId(playerBonus.getCurrent().getBonus().getId());
		for (lithium.service.casino.data.entities.BonusFreeMoney bfm: additionalFreemoney) {
			if (bfm.getImmediateRelease() == null || !bfm.getImmediateRelease()) {
				Long casinoBonusBalance = getCasinoBonusBalance(playerBonus, bfm.getCurrency());
				getAccountingClient().adjustMulti(
					casinoBonusBalance,
					DateTime.now().toString(),
					CasinoTranType.PLAYERBALANCE.value(), //accountCode
					CasinoTranType.PLAYERBALANCE.value(), //accountTypeCode
					CasinoTranType.TRANSFER_FROM_CASINO_BONUS.value(), //transactionTypeCode
					CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.value(), //contraAccountCode
					CasinoTranType.PLAYERBALANCE.value(), //contraAccountTypeCode
					new String[]{PLAYER_BONUS_HISTORY_ID + "=" + playerBonus.getCurrent().getId(), BONUS_REVISION_ID + "=" + playerBonus.getCurrent().getBonus().getId()},
					bfm.getCurrency(),
					playerBonus.getCurrent().getBonus().getDomain().getName(),
					playerBonus.getPlayerGuid(),
					User.SYSTEM_GUID,
					false,
					null
				);
			}
		}
	}
	
	public void moveBalanceFromCasinoBonusToPlayer(PlayerBonus playerBonus) throws Exception {
		
		if (!isBonusAllowedToEnd(playerBonus)) return;
		
		Long casinoBonusBalance = getCasinoBonusBalance(playerBonus);
		Long maxPayout = playerBonus.getCurrent().getBonus().getMaxPayout();
		Long payout = casinoBonusBalance;
		Long excess = 0L;
		if ((maxPayout != null) && (maxPayout > 0)) {
			if (casinoBonusBalance > maxPayout) {
				payout = maxPayout;
				excess = casinoBonusBalance - maxPayout;
			}
		}
		//move balance from player casino bonus balance to player balance
		getAccountingClient().adjustMulti(
			payout,
			DateTime.now().toString(),
			CasinoTranType.PLAYERBALANCE.value(), //accountCode
			CasinoTranType.PLAYERBALANCE.value(), //accountTypeCode
			CasinoTranType.TRANSFER_FROM_CASINO_BONUS.value(), //transactionTypeCode
			CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.value(), //contraAccountCode
			CasinoTranType.PLAYERBALANCE.value(), //contraAccountTypeCode
			new String[] {PLAYER_BONUS_HISTORY_ID+"="+playerBonus.getCurrent().getId(), BONUS_REVISION_ID+"="+playerBonus.getCurrent().getBonus().getId()},
			casinoService.getCurrency(playerBonus.getCurrent().getBonus().getDomain().getName()),
			playerBonus.getCurrent().getBonus().getDomain().getName(),
			playerBonus.getPlayerGuid(),
			User.SYSTEM_GUID,
			false,
			null
		);
		if (excess > 0) {
			//max payout was specified, remaining funds to be transfered to 
			getAccountingClient().adjustMulti(
				excess,
				DateTime.now().toString(),
				CasinoTranType.CASINO_BONUS_MAXPAYOUT_EXCESS.value(), //accountCode
				CasinoTranType.CASINO_BONUS_MAXPAYOUT_EXCESS.value(), //accountTypeCode
				CasinoTranType.TRANSFER_FROM_CASINO_BONUS.value(), //transactionTypeCode
				CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.value(), //contraAccountCode
				CasinoTranType.PLAYERBALANCE.value(), //contraAccountTypeCode
				new String[] {PLAYER_BONUS_HISTORY_ID+"="+playerBonus.getCurrent().getId(), BONUS_REVISION_ID+"="+playerBonus.getCurrent().getBonus().getId()},
				casinoService.getCurrency(playerBonus.getCurrent().getBonus().getDomain().getName()),
				playerBonus.getCurrent().getBonus().getDomain().getName(),
				playerBonus.getPlayerGuid(),
				User.SYSTEM_GUID,
				false,
				null
			);
		}
	}

	public boolean isBonusCompleted(PlayerBonus playerBonus) throws Exception {
		if (playerBonus.getCurrent() == null) {
			return true;
		} else {
			return isBonusCompleted(playerBonus, false);
		}
	}
	public boolean isBonusCompleted(PlayerBonus playerBonus, boolean fromPendingMethod) throws Exception {
		if (casinoBonusFreespinService.hasIncompleteFreespins(playerBonus)) {
			log.info("Has incomplete freespins!");
			return false;
		}
		return checkPlayThroughAndBalance(playerBonus, fromPendingMethod);
	}

	boolean checkPlayThroughAndBalance(PlayerBonus playerBonus, boolean fromPendingMethod) throws Exception {
		long playThrough = playerBonus.getCurrent().getPlayThroughCents();
		long playThroughRequired = playerBonus.getCurrent().getPlayThroughRequiredCents();
		getCasinoBonusBalance(playerBonus); //This has side-effect (populates the playerBonus with latest balance)
		if (playThrough >= playThroughRequired) {
			//wager requirements met, lets finish bonus, and do balance adjustments.
			log.info("playThrough:"+playThrough+" playThroughRequired:"+playThroughRequired);
			moveBalanceFromCasinoBonusToPlayer(playerBonus);
			moveAdditionalFreemoneyBalancesFromCasinoBonusToPlayer(playerBonus);
			PlayerBonusHistory pbh = playerBonusHistoryRepository.findOne(playerBonus.getCurrent().getId());
			pbh.setCompleted(true);
			pbh = playerBonusHistoryRepository.save(pbh);
			playerBonus.setCurrent(null);
			playerBonus = playerBonusRepository.save(playerBonus);
			if (!fromPendingMethod) { // Need this in here to avoid recursion of pending method
				moveBalanceFromPendingBonusToActiveBonus(playerBonus);
			}
			return true;
		} else if (playerBonus.getBalance() == null || playerBonus.getBalance() <= 0L) {
			log.info("Bonus balance depleted. Cancelling bonus: " + playerBonus);
			completeCancelledBonus(playerBonus);
			return true;
		}
		return false;
	}
	
	public boolean isBonusAllowedToEnd(PlayerBonus playerBonus) {
		//FIXME: Remove this. It is here to always allow bonus to end. Iteration 1 of loophole fix can not work
		// We have a problem with round correlation when moving from one bonus to the next since only one active bonus is allowed and we don't have negative round allowances
		// and recon on round completion to allow the outstanding bet value to go to the relevant bonus / real money.
		// Also for youwagr we need to put real money in escrow when allowing negative bets on bonus, so we don't get ceated out of funds.
		return true;

//		boolean unfinishedRounds = bonusRoundTrackService.isUnfinishedRoundsOnBonus(playerBonus.getCurrent());
//		
//		if (unfinishedRounds) {
//			log.warn("Player with unfinished rounds detected at bonus end scenario." + playerBonus);
//			try {
//				registerUserEventIncompleteRounds(playerBonus.getCurrent().getBonus().getDomain().getName(), playerBonus.getPlayerGuid().split("/")[1], playerBonusDisplay(playerBonus.getPlayerGuid()));
//			} catch (JsonProcessingException e) {
//				log.error("Failure in dispatching incomplete bonus event: " + playerBonus, e);
//			}
//			
//		}
//		
//		return !unfinishedRounds;
	}
	
	private void moveBalanceFromCasinoBonusToExpired(PlayerBonus playerBonus) throws Exception {
		
		if (!isBonusAllowedToEnd(playerBonus)) return;
		
		Long casinoBonusBalance = getCasinoBonusBalance(playerBonus);
		//move balance from player casino bonus balance to CASINO_BONUS_EXPIRED ?
		getAccountingClient().adjustMulti(
			casinoBonusBalance,
			DateTime.now().toString(),
			CasinoTranType.CASINO_BONUS_EXPIRED.value(), //accountCode
			CasinoTranType.CASINO_BONUS_EXPIRED.value(), //accountTypeCode
			CasinoTranType.TRANSFER_FROM_CASINO_BONUS.value(), //transactionTypeCode
			CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.value(), //contraAccountCode
			CasinoTranType.PLAYERBALANCE.value(), //contraAccountTypeCode
			new String[] {PLAYER_BONUS_HISTORY_ID+"="+playerBonus.getCurrent().getId(), BONUS_REVISION_ID+"="+playerBonus.getCurrent().getBonus().getId()},
			casinoService.getCurrency(playerBonus.getCurrent().getBonus().getDomain().getName()),
			playerBonus.getCurrent().getBonus().getDomain().getName(),
			playerBonus.getPlayerGuid(),
			User.SYSTEM_GUID,
			false,
			null
		);

		clearAdditionalFreemoneyPlayerBalanceCasinoBonus(playerBonus, CasinoTranType.CASINO_BONUS_EXPIRED);

		moveBalanceFromPendingBonusToActiveBonus(playerBonus);
	}
	
	private void moveBalanceFromPendingBonusToActiveBonus(PlayerBonus playerBonus) throws Exception {
		if (findCurrentBonus(playerBonus.getPlayerGuid()) != null) return;

		//transfer values from pending to active bonus
		//move balance from player balance to player casino bonus balance
		List<PlayerBonusPending> pbpList = playerBonusPendingRepository.findByPlayerGuidOrderByCreatedDateAsc(playerBonus.getPlayerGuid());
		
		//Add bonuses without balance adjustment component to the end of the list for processing
		List<PlayerBonusPending> tmpPbpList = new ArrayList<>();
		for (PlayerBonusPending pbp : pbpList) {
			if (pbp.getBonusAmount() <= 0L && pbp.getTriggerAmount() <= 0L) {
				tmpPbpList.add(pbp);
				//pbpList.remove(pbp);
			}
		}
		pbpList.removeAll(tmpPbpList);
		pbpList.addAll(tmpPbpList);
		
		if (pbpList.isEmpty()) return;
		PlayerBonus pb = null;
		int counter = 0;
		do {
			PlayerBonusPending pbp = pbpList.get(counter);
			counter += 1;
			// Very uncivilized way to do instant bonus checking but it will do for now
			boolean instantBonus = false;
			if (pbp.getBonusRevision().getFreeMoneyWagerRequirement() != null && pbp.getBonusRevision().getFreeMoneyWagerRequirement() <= 0) {
				//In this method there is no active bonus, the first line of the method returns if an active bonus is present
				//Checking for instant bonus here is thus pointless
				//instantBonus = true;
			}

			PlayerBonusHistory pbh = savePlayerBonusHistory(pbp.getBonusRevision(), pbp.getPlayThroughRequiredCents(), pbp.getBonusAmount(), pbp.getBonusPercentage(), instantBonus, pbp.getCustomFreeMoneyAmountCents(), null, null, null , null, null);
			pb = savePlayerBonus(pbh, pbp.getPlayerGuid(), instantBonus);
			pbh = updatePlayerBonusHistory(pbh, pb, pbp.getBonusAmount());
			pb = updatePlayerBonusCurrent(pbh, pbp.getPlayerGuid(), instantBonus);

			try {
				User user = userService.findUserByGuid(pbp.getPlayerGuid());
				List<ChangeLogFieldChange> clfc = changeLogService.copy(pb.getCurrent(), new PlayerBonusHistory(), new String[] { "startedDate", "bonus" });
				changeLogService.registerChangesWithDomain("user.bonus", "create", user.getId(), tokenUtil().guid(), null, null, clfc, Category.BONUSES, SubCategory.BONUS_REGISTER, 0, pbp.getPlayerGuid().substring(0, pbp.getPlayerGuid().indexOf('/'))); //don't have the user event id, oh well

			}
			catch (Exception | UserClientServiceFactoryException e) {
				log.error("Failed to register bonus changelogs for user "+pbp.getPlayerGuid(), e);
			}

//			Response<AdjustmentTransaction> tid1 = 
			getAccountingClient().adjustMulti(
				(pbp.getTriggerAmount() + pbp.getBonusAmount()),
				DateTime.now().toString(),
				CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.value(), //accountCode
				CasinoTranType.PLAYERBALANCE.value(), //accountTypeCode
				CasinoTranType.TRANSFER_FROM_CASINO_BONUS_PENDING.value(), //transactionTypeCode
				CasinoTranType.PLAYER_BALANCE_CASINO_BONUS_PENDING.value(), //contraAccountCode
				CasinoTranType.PLAYERBALANCE.value(), //contraAccountTypeCode
				new String[] {PLAYER_BONUS_HISTORY_ID+"="+pb.getCurrent().getId(), BONUS_REVISION_ID+"="+pb.getCurrent().getBonus().getId()},
				casinoService.getCurrency(pbp.getBonusRevision().getDomain().getName()),
				pbp.getBonusRevision().getDomain().getName(),
				pbp.getPlayerGuid(),
				User.SYSTEM_GUID,
				false,
				null
			);
			
			//triggers to allow proper activation of bonus
			casinoBonusFreespinService.triggerFreeSpins(pbh, instantBonus);
			triggerFreeMoney(pb.getCurrent(), instantBonus);
			triggerAdditionalFreeMoney(pb.getCurrent(), instantBonus);
			triggerExternalBonusGame(pb.getCurrent());
			playerBonusPendingRepository.deleteById(pbp.getId());
		} while (isBonusCompleted(pb, true) && counter < pbpList.size()); //move to next bonus if the current pending one is an instant completed bonus

	}
	
	private void completeUnfinishedBonus(PlayerBonus playerBonus) throws Exception {
		moveBalanceFromCasinoBonusToExpired(playerBonus);
		PlayerBonusHistory pbh = playerBonus.getCurrent();
		pbh.setExpired(true);
		playerBonusHistoryRepository.save(pbh);
		playerBonus.setCurrent(null);
		playerBonusRepository.save(playerBonus);
		
		moveBalanceFromPendingBonusToActiveBonus(playerBonus);
		// do balance udjustments
	}
	
	public boolean checkWithinValidDays(PlayerBonus bonus) throws Exception {
//		boolean
		Integer validDays = bonus.getCurrent().getBonus().getValidDays();
		if (validDays == null) return true;
		DateTime startDate = new DateTime(bonus.getCurrent().getStartedDate().getTime());
		DateTime serverTimeLocal = new DateTime();
		if (serverTimeLocal.isBefore(startDate.plusDays(validDays))) {
			return true;
		}
		completeUnfinishedBonus(bonus);
		return false;
	}
	
	private void savePlayThrough(PlayerBonus playerBonus, Long amountCents, Integer percentage) {
		BigDecimal p = new BigDecimal(percentage).movePointLeft(2);
		BigDecimal playThroughCents = new BigDecimal(amountCents).multiply(p);
		
		playerBonus.getCurrent().setPlayThroughCents(playerBonus.getCurrent().getPlayThroughCents()+playThroughCents.longValue());
		playerBonusHistoryRepository.save(playerBonus.getCurrent());
	}
	
	public void savePlayThrough(PlayerBonusHistory playerBonusHistory, Long amountCents, Integer percentage) {
		if (playerBonusHistory == null) return;
		
		BigDecimal p = new BigDecimal(percentage).movePointLeft(2);
		BigDecimal playThroughCents = new BigDecimal(amountCents).multiply(p);
		
		playerBonusHistory.setPlayThroughCents(playerBonusHistory.getPlayThroughCents()+playThroughCents.longValue());
		playerBonusHistoryRepository.save(playerBonusHistory);
	}
	
	public UserEvent markUserEventReceived(String domainName, String userName, Long id) {
		Response<UserEvent> response = getUserEventService().markReceived(domainName, userName, id);
		if (response.isSuccessful()) {
			return response.getData();
		}
		return null;
	}
	
	public UserEvent getUserEvent(String domainName, String userName, Long id) {
		Response<UserEvent> response = getUserEventService().getUserEvent(domainName, userName, id);
		if (response.isSuccessful()) {
			return response.getData();
		}
		return null;
	}
	
	public UserEvent registerUserEventPlayerBonus(String domainName, String username, PlayerBonusDisplay playerBonus) throws JsonProcessingException {
		String bonusCode = "";
		if ((playerBonus != null) && (playerBonus.getPlayerBonusProjection()!=null)) bonusCode = playerBonus.getPlayerBonusProjection().getCurrent().getBonus().getBonusCode();
		return getUserEventService(true).registerEvent(
			domainName,
			username,
			UserEvent.builder()
			.type("CASINO_BONUS")
			.data(new ObjectMapper().writeValueAsString(playerBonus))
			.message("PlayerBonus("+bonusCode != null? bonusCode:""+") activated.")
			.build()
		).getData();
	}

	public List<BonusRulesCasinoChip> copyBonusRulesCasinoChip(Long bonusRevisionId, BonusRevision bonusRevisionNew) {
		List<BonusRulesCasinoChip> bonusRulesCasinoChipListNew = new ArrayList<>();
		List<BonusRulesCasinoChip> bonusRulesCasinoChipListOld = bonusRulesCasinoChipRepository.findByBonusRevisionId(bonusRevisionId);
		for (BonusRulesCasinoChip bonusRulesCasinoChip:bonusRulesCasinoChipListOld) {
			BonusRulesCasinoChip bonusRulesCasinoChipNew = BonusRulesCasinoChip.builder().build();
			BeanUtils.copyProperties(bonusRulesCasinoChip, bonusRulesCasinoChipNew);
			bonusRulesCasinoChipNew.setId(null);
			bonusRulesCasinoChipNew.setBonusRevision(bonusRevisionNew);
			bonusRulesCasinoChipNew = bonusRulesCasinoChipRepository.save(bonusRulesCasinoChipNew);
			bonusRulesCasinoChipListNew.add(bonusRulesCasinoChipNew);

			List<BonusRulesCasinoChipGames> bonusRulesCasinoChipGamesListOld = bonusRulesCasinoChipGamesRepository.findByBonusRulesCasinoChipBonusRevisionId(bonusRevisionId);
			for (BonusRulesCasinoChipGames bonusRulesCasinoChipGames:bonusRulesCasinoChipGamesListOld) {
				BonusRulesCasinoChipGames bonusRulesCasinoChipGamesNew = BonusRulesCasinoChipGames.builder().build();
				BeanUtils.copyProperties(bonusRulesCasinoChipGames, bonusRulesCasinoChipGamesNew);
				bonusRulesCasinoChipGamesNew.setId(null);
				bonusRulesCasinoChipGamesNew.setBonusRulesCasinoChip(bonusRulesCasinoChipNew);
				bonusRulesCasinoChipGamesNew = bonusRulesCasinoChipGamesRepository.save(bonusRulesCasinoChipGamesNew);
			}
		}
		return bonusRulesCasinoChipListNew;
	}
	
	public UserEvent registerUserEventIncompleteRounds(String domainName, String username, PlayerBonusDisplay playerBonus) throws JsonProcessingException {
		return getUserEventService().registerEvent(
			domainName,
			username,
			UserEvent.builder()
			.type("CASINO_BONUS")
			.data(new ObjectMapper().writeValueAsString(playerBonus))
			.message("You have incomplete game rounds on your bonus. Complete them first before attemting to complete the bonus.")
			.build()
		).getData();
	}
	
	public UserEvent registerUserEventUnfinishedFreespins(String domainName, String username, PlayerBonusDisplay playerBonus) throws JsonProcessingException {
		return getUserEventService().registerEvent(
			domainName,
			username,
			UserEvent.builder()
			.type("CASINO_BONUS")
			.data(new ObjectMapper().writeValueAsString(playerBonus))
			.message("You have incomplete free spins on your bonus. Complete them first before attemting to complete the bonus.")
			.build()
		).getData();
	}
	
	public void registerUserEventPlayerBonusPostTransactionDisplay(String domainName, String playerGuid) {
		try {
			getUserEventService().streamUserEvent(
				domainName,
				playerGuid.substring(playerGuid.indexOf("/") + 1),
				UserEvent.builder()
					.type("CASINO_BONUS")
					.data(new ObjectMapper().writeValueAsString(playerBonusDisplay(playerGuid, null)))
					.build()
			);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	private UserEventClient getUserEventService() {
		return getUserEventService(false);
	}
	private UserEventClient getUserEventService(boolean useSystemAuth) {
		UserEventClient uec = null;
		try {
			uec = services.target(UserEventClient.class, useSystemAuth);
		} catch (Exception e) {
			log.error("Problem getting user event service", e);
		}
		return uec;
	}

	private CasinoExternalBonusGameClient getCasinoExternalBonusGameClient(String provider) {
		CasinoExternalBonusGameClient uec = null;
		try {
			uec = services.target(CasinoExternalBonusGameClient.class, provider,true);
		} catch (Exception e) {
			log.error("Problem getting Casino External Bonus Game Client", e);
		}
		return uec;
	}
	
	private AccountingClient getAccountingClient() {
		AccountingClient ac = null;
		try {
			ac = services.target(AccountingClient.class, "service-accounting", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting accounting service", e);
		}
		return ac;
	}
	
	private AccountingSummaryTransactionTypeClient getAccountingSummaryTransactionTypeService() {
		AccountingSummaryTransactionTypeClient cl = null;
		try {
			cl = services.target(AccountingSummaryTransactionTypeClient.class, "service-accounting-provider-internal", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting accounting service", e);
		}
		return cl;
	}
	
	public List<BonusRulesFreespins> copyBonusRulesFreespins(Long bonusRevisionId, BonusRevision bonusRevisionNew) {
		List<BonusRulesFreespins> bonusRulesFreespinsListNew = new ArrayList<>();
		List<BonusRulesFreespins> bonusRulesFreespinsListOld = bonusRulesFreespinsRepository.findByBonusRevisionId(bonusRevisionId);
		for (BonusRulesFreespins bonusRulesFreespins:bonusRulesFreespinsListOld) {
			BonusRulesFreespins bonusRulesFreespinsNew = BonusRulesFreespins.builder().build();
			BeanUtils.copyProperties(bonusRulesFreespins, bonusRulesFreespinsNew);
			bonusRulesFreespinsNew.setId(null);
			bonusRulesFreespinsNew.setBonusRevision(bonusRevisionNew);
			bonusRulesFreespinsNew = bonusRulesFreespinsRepository.save(bonusRulesFreespinsNew);
			bonusRulesFreespinsListNew.add(bonusRulesFreespinsNew);
			
			List<BonusRulesFreespinGames> bonusRulesFreespinGamesListOld = bonusRulesFreespinGamesRepository.findByBonusRulesFreespinsBonusRevisionId(bonusRevisionId);
			for (BonusRulesFreespinGames bonusRulesFreespinGames:bonusRulesFreespinGamesListOld) {
				BonusRulesFreespinGames bonusRulesFreespinGamesNew = BonusRulesFreespinGames.builder().build();
				BeanUtils.copyProperties(bonusRulesFreespinGames, bonusRulesFreespinGamesNew);
				bonusRulesFreespinGamesNew.setId(null);
				bonusRulesFreespinGamesNew.setBonusRulesFreespins(bonusRulesFreespinsNew);
				bonusRulesFreespinGamesNew = bonusRulesFreespinGamesRepository.save(bonusRulesFreespinGamesNew);
			}
		}
		return bonusRulesFreespinsListNew;
	}

	public List<BonusRulesInstantReward> copyBonusRulesInstantReward(Long bonusRevisionId, BonusRevision bonusRevisionNew) {
		List<BonusRulesInstantReward> bonusRulesInstantRewardListNew = new ArrayList<>();
		List<BonusRulesInstantReward> bonusRulesInstantRewardListOld = bonusRulesInstantRewardRepository.findByBonusRevisionId(bonusRevisionId);
		for (BonusRulesInstantReward bonusRulesInstantRewards:bonusRulesInstantRewardListOld) {
			BonusRulesInstantReward bonusRulesInstantRewardNew = BonusRulesInstantReward.builder().build();
			BeanUtils.copyProperties(bonusRulesInstantRewards, bonusRulesInstantRewardNew);
			bonusRulesInstantRewardNew.setId(null);
			bonusRulesInstantRewardNew.setBonusRevision(bonusRevisionNew);
			bonusRulesInstantRewardNew = bonusRulesInstantRewardRepository.save(bonusRulesInstantRewardNew);
			bonusRulesInstantRewardListNew.add(bonusRulesInstantRewardNew);

			List<BonusRulesInstantRewardGames> bonusRulesInstantRewardGamesListOld = bonusRulesInstantRewardGamesRepository.findByBonusRulesInstantRewardBonusRevisionId(bonusRevisionId);
			for (BonusRulesInstantRewardGames bonusRulesInstantRewardGames:bonusRulesInstantRewardGamesListOld) {
				BonusRulesInstantRewardGames bonusRulesInstantRewardGamesNew = BonusRulesInstantRewardGames.builder().build();
				BeanUtils.copyProperties(bonusRulesInstantRewardGames, bonusRulesInstantRewardGamesNew);
				bonusRulesInstantRewardGamesNew.setId(null);
				bonusRulesInstantRewardGamesNew.setBonusRulesInstantReward(bonusRulesInstantRewardNew);
				bonusRulesInstantRewardGamesNew = bonusRulesInstantRewardGamesRepository.save(bonusRulesInstantRewardGamesNew);
			}
		}
		return bonusRulesInstantRewardListNew;
	}

	public List<BonusRulesInstantRewardFreespin> copyBonusRulesInstantRewardFreespin(Long bonusRevisionId, BonusRevision bonusRevisionNew) {
		List<BonusRulesInstantRewardFreespin> bonusRulesInstantRewardFreespinListNew = new ArrayList<>();
		List<BonusRulesInstantRewardFreespin> bonusRulesInstantRewardFreespinListOld = bonusRulesInstantRewardFreespinRepository.findByBonusRevisionId(bonusRevisionId);
		for (BonusRulesInstantRewardFreespin bonusRulesInstantRewardFreespins:bonusRulesInstantRewardFreespinListOld) {
			BonusRulesInstantRewardFreespin bonusRulesInstantRewardFreespinNew = BonusRulesInstantRewardFreespin.builder().build();
			BeanUtils.copyProperties(bonusRulesInstantRewardFreespins, bonusRulesInstantRewardFreespinNew);
			bonusRulesInstantRewardFreespinNew.setId(null);
			bonusRulesInstantRewardFreespinNew.setBonusRevision(bonusRevisionNew);
			bonusRulesInstantRewardFreespinNew = bonusRulesInstantRewardFreespinRepository.save(bonusRulesInstantRewardFreespinNew);
			bonusRulesInstantRewardFreespinListNew.add(bonusRulesInstantRewardFreespinNew);

			List<BonusRulesInstantRewardFreespinGames> bonusRulesInstantRewardFreespinGamesListOld = bonusRulesInstantRewardFreespinGamesRepository.findByBonusRulesInstantRewardFreespinBonusRevisionId(bonusRevisionId);
			for (BonusRulesInstantRewardFreespinGames bonusRulesInstantRewardFreespinGames:bonusRulesInstantRewardFreespinGamesListOld) {
				BonusRulesInstantRewardFreespinGames bonusRulesInstantRewardFreespinGamesNew = BonusRulesInstantRewardFreespinGames.builder().build();
				BeanUtils.copyProperties(bonusRulesInstantRewardFreespinGames, bonusRulesInstantRewardFreespinGamesNew);
				bonusRulesInstantRewardFreespinGamesNew.setId(null);
				bonusRulesInstantRewardFreespinGamesNew.setBonusRulesInstantRewardFreespin(bonusRulesInstantRewardFreespinNew);
				bonusRulesInstantRewardFreespinGamesNew = bonusRulesInstantRewardFreespinGamesRepository.save(bonusRulesInstantRewardFreespinGamesNew);
			}
		}
		return bonusRulesInstantRewardFreespinListNew;
	}

	public List<BonusRulesGamesPercentages> copyBonusRulesGamePercentagesAndCategoryPercentages(Long bonusRevisionId, BonusRevision bonusRevisionNew) {
		List<BonusRulesGamesPercentages> bonusRulesGamePercentagesListNew = new ArrayList<>();
		List<BonusRulesGamesPercentages> bonusRulesBonusRulesGamesPercentagesOld = bonusRulesGamesPercentagesRepository.findByBonusRevisionId(bonusRevisionId);
		for (BonusRulesGamesPercentages bonusRulesGamePercentage:bonusRulesBonusRulesGamesPercentagesOld) {
			BonusRulesGamesPercentages bonusRulesGamePercentageNew = BonusRulesGamesPercentages.builder().build();
			BeanUtils.copyProperties(bonusRulesGamePercentage, bonusRulesGamePercentageNew);
			bonusRulesGamePercentageNew.setId(null);
			bonusRulesGamePercentageNew.setBonusRevision(bonusRevisionNew);
			bonusRulesGamePercentageNew = bonusRulesGamesPercentagesRepository.save(bonusRulesGamePercentageNew);
			bonusRulesGamePercentagesListNew.add(bonusRulesGamePercentageNew);
		}
		return bonusRulesGamePercentagesListNew;
	}
	
	public List<BonusRequirementsDeposit> copyBonusRequirementsDeposit(Long bonusRevisionId, BonusRevision bonusRevisionNew) {
		List<BonusRequirementsDeposit> bonusRequirementsDepositListNew = new ArrayList<>();
		List<BonusRequirementsDeposit> bonusRequirementsDepositListOld = bonusRequirementsDepositRepository.findByBonusRevisionId(bonusRevisionId);
		for (BonusRequirementsDeposit bonusRequirementsDeposit:bonusRequirementsDepositListOld) {
			BonusRequirementsDeposit bonusRequirementsDepositNew = BonusRequirementsDeposit.builder().build();
			BeanUtils.copyProperties(bonusRequirementsDeposit, bonusRequirementsDepositNew);
			bonusRequirementsDepositNew.setId(null);
			bonusRequirementsDepositNew.setBonusRevision(bonusRevisionNew);
			bonusRequirementsDepositNew = bonusRequirementsDepositRepository.save(bonusRequirementsDepositNew);
			bonusRequirementsDepositListNew.add(bonusRequirementsDepositNew);
		}
		return bonusRequirementsDepositListNew;
	}
	
	public BonusRevision createNewBonusRevision(Long bonusRevisionId) throws Exception {
		BonusRevision bonusRevisionOld = findBonusRevisionById(bonusRevisionId);
		BonusRevision bonusRevisionNew = BonusRevision.builder().build();
		if (bonusRevisionOld!=null) BeanUtils.copyProperties(bonusRevisionOld, bonusRevisionNew);
		bonusRevisionNew.setId(null);
//		bonusRevisionNew.setBonus(null);
		bonusRevisionNew.setBonusFreeMoney(new ArrayList<>());
		bonusRevisionNew.setBonusExternalGameConfigs(new ArrayList<>());
		bonusRevisionNew.setBonusTokens(new ArrayList<>());
		bonusRevisionNew = bonusRevisionRepository.save(bonusRevisionNew);
		List<lithium.service.casino.data.entities.BonusFreeMoney> bfmNewList = new ArrayList<>();
		if (!bonusRevisionOld.getBonusFreeMoney().isEmpty()) {
			for (lithium.service.casino.data.entities.BonusFreeMoney bfm:bonusRevisionOld.getBonusFreeMoney()) {
				lithium.service.casino.data.entities.BonusFreeMoney bfmNew = bonusFreeMoneyRepository.save(
					lithium.service.casino.data.entities.BonusFreeMoney.builder()
					.amount(bfm.getAmount())
					.currency(bfm.getCurrency())
					.wagerRequirement(bfm.getWagerRequirement())
					.bonusRevision(bonusRevisionNew)
					.immediateRelease(bfm.getImmediateRelease())
					.build()
				);
				bfmNewList.add(bfmNew);
			}
			bonusRevisionNew.setBonusFreeMoney(bfmNewList);
			bonusRevisionNew = bonusRevisionRepository.save(bonusRevisionNew);
		}

		List<lithium.service.casino.data.entities.BonusExternalGameConfig> bonusExternalGameConfigs = new ArrayList<>();
		if (!bonusRevisionOld.getBonusExternalGameConfigs().isEmpty()) {
			for (lithium.service.casino.data.entities.BonusExternalGameConfig beg : bonusRevisionOld.getBonusExternalGameConfigs()) {
				lithium.service.casino.data.entities.BonusExternalGameConfig bonusExternalGameConfigsNew = bonusExternalGameConfigRepository.save(
						lithium.service.casino.data.entities.BonusExternalGameConfig.builder()
								.provider(beg.getProvider())
								.bonusRevision(bonusRevisionNew)
								.campaignId(beg.getCampaignId())
								.build()
				);
				bonusExternalGameConfigs.add(bonusExternalGameConfigsNew);
			}
			bonusRevisionNew.setBonusExternalGameConfigs(bonusExternalGameConfigs);
			bonusRevisionNew = bonusRevisionRepository.save(bonusRevisionNew);
		}

		List<lithium.service.casino.data.entities.BonusToken> bonusTokens = new ArrayList<>();
		if (!bonusRevisionOld.getBonusTokens().isEmpty()) {
			for (lithium.service.casino.data.entities.BonusToken bt : bonusRevisionOld.getBonusTokens()) {
				lithium.service.casino.data.entities.BonusToken bonusTokensNew =
						bonusTokenService.createBonusToken(bonusRevisionNew, bt.getCurrency(), bt.getMinimumOdds(), bt.getAmount());
				bonusTokens.add(bonusTokensNew);
			}
			bonusRevisionNew.setBonusTokens(bonusTokens);
			bonusRevisionNew = bonusRevisionRepository.save(bonusRevisionNew);
		}

		return bonusRevisionNew;
	}
	
	public Bonus createNewBonus() {
		return bonusRepository.save(new Bonus());
	}
	
	public BonusRevision setBonusRevisionEnabled(Long bonusRevisionId, Boolean enabled) throws Exception {
		BonusRevision bonusRevision = findBonusRevisionById(bonusRevisionId);
		Bonus existingBonus = findBonus(bonusRevision.getBonusCode(), bonusRevision.getDomain().getName(), bonusRevision.getBonusType());
		
		if ((existingBonus != null) && (existingBonus.getCurrent() != null)) {
			if (playerBonusHistoryRepository.findTop1ByBonusIdAndCompletedFalseAndExpiredFalseAndAndCancelledFalse(bonusRevisionId).isPresent() ||
				playerBonusPendingRepository.findTop1ByBonusRevisionId(bonusRevisionId).isPresent()) {
				throw new Exception("Existing active or pending bonus found for bonus: " + existingBonus.toString());
			}
		}
		bonusRevision.setEnabled(enabled);
		bonusRevision = bonusRevisionRepository.save(bonusRevision);
		return bonusRevision;
	}
	
	@Retryable
	public Domain findOrCreateDomain(String name) {
		Domain domain = domainRepository.findByName(name);
		if (domain == null) {
			domain = Domain.builder().name(name).build();
			domainRepository.save(domain);
		}
		return domain;
	}
	
	public Bonus createNewBonus(BonusCreate bonusCreate) throws Exception {
		Domain domain = findOrCreateDomain(bonusCreate.getDomainName());
		Bonus existingBonus = findBonus(bonusCreate.getBonusCode(), domain.getName(), bonusCreate.getBonusType());
		if ((existingBonus != null) && (existingBonus.getCurrent() != null)) {
			BonusRevision existingRevision = existingBonus.getCurrent();
			existingRevision.setEnabled(false);
			bonusRevisionRepository.save(existingRevision);
			throw new Exception("Existing active or pending bonus found for bonus: " + existingBonus.toString());
		}
		
		BonusRevision bonusRevision = BonusRevision.builder()
			.domain(domain)
			.bonusName(bonusCreate.getBonusName())
			.bonusCode(bonusCreate.getBonusCode())
			.bonusType(bonusCreate.getBonusType())
			.bonusTriggerType(bonusCreate.getBonusTriggerType())
			.triggerAmount(bonusCreate.getTriggerAmount())
			.triggerGranularity(bonusCreate.getTriggerGranularity())
			.enabled(false)
			.build();
		bonusRevision = bonusRevisionRepository.save(bonusRevision);
		
		Bonus bonus = Bonus.builder().edit(bonusRevision).editUser(bonusCreate.getCreatedBy()).build();
		bonus = bonusRepository.save(bonus);
		
		bonusRevision.setBonus(bonus);
		bonusRevision = bonusRevisionRepository.save(bonusRevision);
		
		return bonus;
	}
	
	private void unlockGames(BonusRevision bonusRevision, UnlockGames unlockGames) {
		if ((unlockGames.getGames()!=null) && (!unlockGames.getGames().isEmpty())) {
			for (UnlockGamesList unlockGame:unlockGames.getGames()) {
				casinoBonusUnlockGamesService.saveUnlockGames(
					BonusUnlockGames.builder()
					.id(unlockGame.getId())
					.bonusRevision(bonusRevision)
					.gameGuid(unlockGame.getGameGuid())
					.gameId(unlockGame.getGameId())
					.build()
				);
			}
		}
	}
	
	public Bonus saveBonus(BonusEdit bonusEdit) throws Exception {
		Bonus dependsOnBonus = null;
		if (bonusEdit.getDependsOnBonus() != null) {
			dependsOnBonus = bonusRepository.findOne(bonusEdit.getDependsOnBonus().getBonusId());
		}
		BonusRevision bonusRevision = bonusRevisionRepository.findOne(bonusEdit.getId());
		BonusRevision bonusRevisionCopy = BonusRevision.builder().build();
		BeanUtils.copyProperties(bonusRevision, bonusRevisionCopy);
		
		bonusRevision.setBonusCode(bonusEdit.getBonusCode());
		bonusRevision.setBonusName(bonusEdit.getBonusName());
		bonusRevision.setEnabled(bonusEdit.getEnabled());
		if (bonusEdit.getMaxPayout()!=null) bonusRevision.setMaxPayout(new BigDecimal(bonusEdit.getMaxPayout()).movePointRight(2).longValue());
		bonusRevision.setMaxRedeemable(bonusEdit.getMaxRedeemable());
		bonusRevision.setMaxRedeemableGranularity(bonusEdit.getMaxRedeemableGranularity());
		bonusRevision.setValidDays(bonusEdit.getValidDays());
		bonusRevision.setForDepositNumber(bonusEdit.getForDepositNumber());
		bonusRevision.setPlayThroughRequiredType(bonusEdit.getPlayThroughRequiredType());
		bonusRevision.setBonusType(bonusEdit.getBonusType());
		bonusRevision.setBonusTriggerType(bonusEdit.getBonusTriggerType());
		bonusRevision.setTriggerAmount(bonusEdit.getTriggerAmount());
		bonusRevision.setTriggerGranularity(bonusEdit.getTriggerGranularity());
		bonusRevision.setBonusDescription(bonusEdit.getBonusDescription());
//		bonusRevision.setVisibleToPlayer(bonusEdit.getVisibleToPlayer());
		bonusRevision.setPlayerMayCancel(bonusEdit.getPlayerMayCancel());
		if (bonusEdit.getCancelOnDepositMinimumAmount()!=null) bonusRevision.setCancelOnDepositMinimumAmount(new BigDecimal(bonusEdit.getCancelOnDepositMinimumAmount()).movePointRight(2).longValue());
		bonusRevision.setCancelOnBetBiggerThanBalance(true); //always allow bonus to cancel when bet is bigger than balance
		if (bonusEdit.getFreeMoneyAmount()!=null && !bonusEdit.getFreeMoneyAmount().trim().isBlank()) {
			bonusRevision.setFreeMoneyAmount(new BigDecimal(bonusEdit.getFreeMoneyAmount()).movePointRight(2).longValue());
			if (bonusRevision.getFreeMoneyAmount() <= 0L) {
				bonusRevision.setFreeMoneyAmount(null);
				bonusEdit.setFreeMoneyWagerRequirement(null);
			}
		} else {
			bonusRevision.setFreeMoneyAmount(null);
		}
		bonusRevision.setFreeMoneyWagerRequirement(bonusEdit.getFreeMoneyWagerRequirement());
		
//		bonusRevision.set.bonus(bonus)
		if (bonusEdit.getActiveDays()!=null) {
			String activeDays = "";
			for (String key:bonusEdit.getActiveDays().keySet()) {
				if (bonusEdit.getActiveDays().get(key)) activeDays += key+",";
			}
			activeDays = StringUtils.removeEnd(activeDays, ",");
			bonusRevision.setActiveDays((activeDays.isEmpty())?null:activeDays);
		}
		if (bonusEdit.getActiveTime()!=null) {
			String start = bonusEdit.getActiveTime().get("start");
			String end = bonusEdit.getActiveTime().get("end");
			bonusRevision.setActiveTimezone(null);
			bonusRevision.setActiveStartTime(null);
			if ((start!=null) && (!start.isEmpty())) {
				bonusRevision.setActiveStartTime(LocalTime.parse(start).toDateTimeToday().toDate());
				bonusRevision.setActiveTimezone(bonusEdit.getTimezone());
			}
			bonusRevision.setActiveEndTime(null);
			if ((end!=null) && (!end.isEmpty())) {
				bonusRevision.setActiveEndTime(LocalTime.parse(end).toDateTimeToday().toDate());
				bonusRevision.setActiveTimezone(bonusEdit.getTimezone());
			}
		}
		bonusRevision.setDependsOnBonus(dependsOnBonus);
		bonusRevision.setStartingDate(bonusEdit.getStartingDate());
		if (bonusEdit.getStartingDate() != null) {
			bonusRevision.setStartingDateTimezone(bonusEdit.getTimezone());
		} else {
			bonusRevision.setStartingDateTimezone(null);
		}
		bonusRevision.setExpirationDate(bonusEdit.getExpirationDate());
		if (bonusEdit.getExpirationDate() != null) {
			bonusRevision.setExpirationDateTimezone(bonusEdit.getTimezone());
		} else {
			bonusRevision.setExpirationDateTimezone(null);
		}
		
		if (bonusEdit.getPublicView() != null) {
			bonusRevision.setPublicView(bonusEdit.getPublicView());
		} else {
			bonusRevision.setPublicView(false);
		}
		
		if (bonusEdit.getImage() != null && bonusEdit.getImage().getFilesize() != null && bonusEdit.getImage().getFilesize() > 0) {
			Graphic graphic = graphicsService.saveGraphic(bonusEdit.getImage().getBase64(), bonusEdit.getImage().getFiletype());
			bonusRevision.setGraphic(graphic);
		}
		
		bonusRevision.setActivationNotificationName(bonusEdit.getActivationNotificationName());
		
		bonusRevision = bonusRevisionRepository.save(bonusRevision);
		
		//TODO:
		//Saving bonus/additional free money after revision save.

		//Yes, I know this is not a nice way of doing it.
		bonusRevision.setBonusFreeMoney(null);
		bonusRevision = bonusRevisionRepository.save(bonusRevision);
		bonusFreeMoneyRepository.findByBonusRevisionId(bonusRevision.getId()).forEach(conf -> {
			bonusFreeMoneyRepository.delete(conf);
		});
		for (BonusEdit.BonusFreeMoney bfm:bonusEdit.getBonusFreeMoney()) {
			//if (bfm.getId() == null) {
				bonusFreeMoneyRepository.save(lithium.service.casino.data.entities.BonusFreeMoney.builder()
				.amount(bfm.getAmount())
				.currency(bfm.getCurrency())
				.wagerRequirement(bfm.getWagerRequirement())
				.bonusRevision(bonusRevision)
				.immediateRelease(bfm.getImmediateRelease())
				.build());
			//}
		}
		bonusRevision.setBonusFreeMoney(bonusFreeMoneyRepository.findByBonusRevisionId(bonusRevision.getId()));
		bonusRevision = bonusRevisionRepository.save(bonusRevision);

		bonusRevision.setBonusExternalGameConfigs(null);
		bonusRevision = bonusRevisionRepository.save(bonusRevision);

		bonusExternalGameConfigRepository.findByBonusRevisionId(bonusRevision.getId()).forEach(conf -> {
			bonusExternalGameConfigRepository.delete(conf);
		});
		for (BonusEdit.BonusExternalGameConfig bem : bonusEdit.getBonusExternalGameConfigs()) {
			//if (bem.getId() == null) {
				bonusExternalGameConfigRepository.save(lithium.service.casino.data.entities.BonusExternalGameConfig.builder()
					.campaignId(bem.getCampaignId())
					.provider(bem.getProvider())
					.bonusRevision(bonusRevision)
					.build());
			//}
		}

		bonusRevision.setBonusExternalGameConfigs(bonusExternalGameConfigRepository.findByBonusRevisionId(bonusRevision.getId()));
		bonusRevision = bonusRevisionRepository.save(bonusRevision);

		//Bonus token link to bonus revision
		bonusRevision.setBonusTokens(null);
		bonusRevision = bonusRevisionRepository.save(bonusRevision);
		bonusTokenService.deleteBonusTokensForRevision(bonusRevision);
		BonusRevision finalBonusRevision = bonusRevision;
		bonusRevision.setBonusTokens(
				bonusEdit.getBonusTokens().stream()
					.map(bt -> bonusTokenService.createBonusToken(
							finalBonusRevision, bt.getCurrency(), bt.getMinimumOdds(), bt.getAmount()))
					.collect(Collectors.toList()));
		bonusRevision = bonusRevisionRepository.save(bonusRevision);

		List<ChangeLogFieldChange> clfc = changeLogService.compare(bonusRevision, bonusRevisionCopy, new String[] { 
			"bonusCode", "bonusName", "forDepositNumber", "maxPayout", "maxRedeemable", "maxRedeemableGranularity",
			"validDays", "playerMayCancel", "cancelOnDepositMinimumAmount", "cancelOnBetBiggerThanBalance",
			"freeMoneyAmount", "freeMoneyWagerRequirement", "activeDays", "activeStartTimeFormatted", "activeEndTimeFormatted", "activeTimezone",
			"dependsOnBonusCode", "startingDateFormatted", "expirationDateFormatted", "publicView", "bonusDescription", "bonusFreeMoney",
			"activationNotificationName", "bonusTokens"
		});

		String oldMd5 = "N/A";
		if (bonusRevisionCopy != null && bonusRevisionCopy.getGraphic() != null && bonusRevisionCopy.getGraphic().getMd5Hash() != null) {
			oldMd5 = bonusRevisionCopy.getGraphic().getMd5Hash();
		}
		
		String newMd5 = "N/A";
		if (bonusRevision != null && bonusRevision.getGraphic() != null && bonusRevision.getGraphic().getMd5Hash() != null) {
			newMd5 = bonusRevision.getGraphic().getMd5Hash();
		}
		
		if (!oldMd5.equals(newMd5)) {
			clfc.add(ChangeLogFieldChange.builder().field("graphic").fromValue(oldMd5).toValue(newMd5).build());
		}
		
		log.info("changeLog:"+clfc);
		if (!clfc.isEmpty()) {
			changeLogService.registerChangesWithDomain("bonus", "edit", bonusRevision.getBonus().getId(), bonusEdit.getPrincipalEdit(), null, null, clfc, Category.BONUSES, SubCategory.BONUS_REVISION, 0, bonusEdit.getDomain().getName());
		}
		
		unlockGames(bonusRevision, bonusEdit.getUnlockGames());
		
		if (bonusEdit.getFreespinRules() != null && !bonusEdit.getFreespinRules().isEmpty()) {
			List<FreespinRules> freespinRules = new ArrayList<FreespinRules>();
			freespinRules.add(bonusEdit.getFreespinRules().get(0));

			//TODO: Saving multiple freespin rules causes a problem when assigning the freespins to players.
			// The multiple freespin rules is not supposed to come from the frontend but sometimes, the admin staff manages to do this
			//Putting this in place to stop such a situation in future
//			
//			if (freespinRules.size() > 1) {
//				freespinRules = freespinRules.subList(0, 1).;
//			} //this code is not working for some weird reason. Perhaps because I am atempting to store a view into the origin object.
			
			for (FreespinRules freespinRule:freespinRules) {
				
				if (freespinRule.getId() < 0 && (freespinRule.getProvider().trim().isEmpty() || freespinRule.getFreespins() <= 0)) continue;
				
				BonusRulesFreespins brf = BonusRulesFreespins.builder()
					.id((freespinRule.getId()!=-1)?freespinRule.getId():null)
					.bonusRevision(bonusRevision)
					.freespins(freespinRule.getFreespins())
					.freeSpinValueInCents(freespinRule.getFreeSpinValueInCents())
					.provider(freespinRule.getProvider())
					.wagerRequirements(freespinRule.getWagerRequirements())
					.build();
				brf = bonusRulesFreespinsRepository.save(brf);
				bonusRulesFreespinGamesRepository.deleteByBonusRulesFreespinsId(brf.getId());
				for (FreespinGame game:freespinRule.getBonusRulesFreespinGames()) {
					BonusRulesFreespinGames brfg = BonusRulesFreespinGames.builder()
						.gameId(game.getGameId())
						.bonusRulesFreespins(brf)
						.build();
					brfg = bonusRulesFreespinGamesRepository.save(brfg);
				}
			}
		}

		if (bonusEdit.getInstantRewardRules() != null && !bonusEdit.getInstantRewardRules().isEmpty()) {
			List<BonusEdit.InstantRewardRules> instantRewardRules = new ArrayList<BonusEdit.InstantRewardRules>();
			instantRewardRules.add(bonusEdit.getInstantRewardRules().get(0));
			for (BonusEdit.InstantRewardRules instantRewardRule:instantRewardRules) {

				if (instantRewardRule.getId() < 0 && (instantRewardRule.getProvider().trim().isEmpty() || instantRewardRule.getNumberOfUnits() <= 0)) continue;

				BonusRulesInstantReward brir = BonusRulesInstantReward.builder()
						.id((instantRewardRule.getId()!=-1)?instantRewardRule.getId():null)
						.bonusRevision(bonusRevision)
						.numberOfUnits(instantRewardRule.getNumberOfUnits())
						.instantRewardUnitValue(instantRewardRule.getInstantRewardUnitValue())
						.volatility(instantRewardRule.getVolatility())
						.provider(instantRewardRule.getProvider())
						.build();
				brir = bonusRulesInstantRewardRepository.save(brir);
				bonusRulesInstantRewardGamesRepository.deleteByBonusRulesInstantRewardId(brir.getId());
				for (BonusEdit.InstantRewardRules.InstantRewardGame game:instantRewardRule.getBonusRulesInstantRewardGames()) {
					BonusRulesInstantRewardGames brirg = BonusRulesInstantRewardGames.builder()
							.gameId(game.getGameId())
							.bonusRulesInstantReward(brir)
							.build();
					brirg = bonusRulesInstantRewardGamesRepository.save(brirg);
				}
			}
		}

		if (bonusEdit.getInstantRewardFreespinRules() != null && !bonusEdit.getInstantRewardFreespinRules().isEmpty()) {
			List<BonusEdit.InstantRewardFreespinRules> instantRewardFreespinRules = new ArrayList<BonusEdit.InstantRewardFreespinRules>();
			instantRewardFreespinRules.add(bonusEdit.getInstantRewardFreespinRules().get(0));
			for (BonusEdit.InstantRewardFreespinRules instantRewardFreespinRule:instantRewardFreespinRules) {

				if (instantRewardFreespinRule.getId() < 0 && (instantRewardFreespinRule.getProvider().trim().isEmpty() || instantRewardFreespinRule.getNumberOfUnits() <= 0)) continue;

				BonusRulesInstantRewardFreespin brirf = BonusRulesInstantRewardFreespin.builder()
						.id((instantRewardFreespinRule.getId()!=-1)?instantRewardFreespinRule.getId():null)
						.bonusRevision(bonusRevision)
						.numberOfUnits(instantRewardFreespinRule.getNumberOfUnits())
						.instantRewardUnitValue(instantRewardFreespinRule.getInstantRewardUnitValue())
						.volatility(instantRewardFreespinRule.getVolatility())
						.provider(instantRewardFreespinRule.getProvider())
						.build();
				brirf = bonusRulesInstantRewardFreespinRepository.save(brirf);
				bonusRulesInstantRewardFreespinGamesRepository.deleteByBonusRulesInstantRewardFreespinId(brirf.getId());
				for (BonusEdit.InstantRewardFreespinRules.InstantRewardFreespinGame game:instantRewardFreespinRule.getBonusRulesInstantRewardFreespinGames()) {
					BonusRulesInstantRewardFreespinGames brirfg = BonusRulesInstantRewardFreespinGames.builder()
							.gameId(game.getGameId())
							.bonusRulesInstantRewardFreespin(brirf)
							.build();
					brirfg = bonusRulesInstantRewardFreespinGamesRepository.save(brirfg);
				}
			}
		}

		if (bonusEdit.getDepositRequirements() != null) {
			List<DepositRequirements> depositRequirements = bonusEdit.getDepositRequirements();
			bonusRequirementsDepositRepository.deleteByBonusRevisionId(bonusRevision.getId());
			for (DepositRequirements depositRequirement:depositRequirements) {
				if (depositRequirement.getMinDeposit() != null) {
					BonusRequirementsDeposit bonusRequirementsDeposit = BonusRequirementsDeposit.builder()
						.id((depositRequirement.getId()!=null && depositRequirement.getId()!=-1)?depositRequirement.getId():null)
						.minDeposit(new BigDecimal((depositRequirement.getMinDeposit()!=null)?depositRequirement.getMinDeposit():"0").movePointRight(2).longValue())
						.maxDeposit(new BigDecimal((depositRequirement.getMaxDeposit()!=null)?depositRequirement.getMaxDeposit():"0").movePointRight(2).longValue())
						.bonusPercentage(depositRequirement.getBonusPercentage())
						.wagerRequirements(depositRequirement.getWagerRequirements())
						.bonusRevision(bonusRevision)
						.build();
					bonusRequirementsDeposit = bonusRequirementsDepositRepository.save(bonusRequirementsDeposit);
				}
			}
		}

		if (bonusEdit.getCasinoChipRules() != null && !bonusEdit.getCasinoChipRules().isEmpty()) {
			List<BonusEdit.CasinoChipRules> chipRules = new ArrayList<BonusEdit.CasinoChipRules>();
			chipRules.add(bonusEdit.getCasinoChipRules().get(0));

			for (BonusEdit.CasinoChipRules chipRule:chipRules) {

				if (chipRule.getId() < 0 && (chipRule.getProvider().trim().isEmpty() || chipRule.getCasinoChipValue() <= 0)) continue;

				BonusRulesCasinoChip brcc = BonusRulesCasinoChip.builder()
						.id((chipRule.getId()!=-1)?chipRule.getId():null)
						.bonusRevision(bonusRevision)
						.casinoChipValue(chipRule.getCasinoChipValue())
						.provider(chipRule.getProvider())
						.build();
				brcc = bonusRulesCasinoChipRepository.save(brcc);
				bonusRulesCasinoChipGamesRepository.deleteByBonusRulesCasinoChipId(brcc.getId());
				for (BonusEdit.CasinoChipRules.CasinoChipGame game:chipRule.getBonusRulesCasinoChipGames()) {
					BonusRulesCasinoChipGames brccg = BonusRulesCasinoChipGames.builder()
							.gameId(game.getGameId())
							.bonusRulesCasinoChip(brcc)
							.build();
					brccg = bonusRulesCasinoChipGamesRepository.save(brccg);
				}
			}
		}


		if (bonusEdit.getGameCategories() != null) {
			List<GameCategory> gameCategories = bonusEdit.getGameCategories();
//			bonusRulesGamesPercentagesRepository.deleteByBonusRevisionIdAndGameGuidIsNullAndGameCategoryIsNotNull(bonusRevision.getId());
			for (GameCategory gameCategory:gameCategories) {
				log.info("gameCategory:"+gameCategory);
				BonusRulesGamesPercentages gp = BonusRulesGamesPercentages.builder()
					.id(gameCategory.getId())
					.bonusRevision(bonusRevision)
					.gameCategory(gameCategory.getCasinoCategory())
					.percentage(gameCategory.getPercentage())
					.build();
				gp = bonusRulesGamesPercentagesRepository.save(gp);
			}
		}
		
		if (bonusEdit.getGamePercentages() != null) {
			List<GamePercentages> gamePercentages = bonusEdit.getGamePercentages();
//			bonusRulesGamesPercentagesRepository.deleteByBonusRevisionIdAndGameGuidIsNotNull(bonusRevision.getId());
			for (GamePercentages gamePercentage:gamePercentages) {
				log.info("gamePercentage:"+gamePercentage);
				String category = null;
				if (gamePercentage.getGameCategory()!=null) {
					String[] gameCategoryList = gamePercentage.getGameCategory().split(",");
					for (String gameCategory: gameCategoryList) {
						GameCategory gc = gameCategoryRepository.findByGameCategoriesContainingIgnoreCase(gameCategory);
						if (gc == null) continue;
						category = gc.getCasinoCategory();
						break;
					}
				}
				log.info("category: "+category+"  :: "+gamePercentage.getGameCategory());
				BonusRulesGamesPercentages gp = BonusRulesGamesPercentages.builder()
					.id((gamePercentage.getId()!=null && gamePercentage.getId()!=-1)?gamePercentage.getId():null)
					.bonusRevision(bonusRevision)
					.gameCategory(category)
					.percentage(gamePercentage.getPercentage())
					.gameGuid(gamePercentage.getGameInfo().getProviderGuid()+"/"+gamePercentage.getGameId())
					.build();
				gp = bonusRulesGamesPercentagesRepository.save(gp);
			}
		}
		
		return bonusRevision.getBonus();
	}
	
	public Bonus saveBonus(Bonus bonus) {
		return bonusRepository.save(bonus);
	}
	
	public Bonus createBonus(String domainName, BonusRevision revision, BonusRequirementsSignup signupRequirements, BonusRequirementsDeposit[] depositRequirements, BonusRulesGamesPercentages[] gameRules, BonusRulesFreespinGames[] freeSpinGames) {
		Domain d = domain(domainName);
		Bonus bonus = bonusRepository.findByCurrentBonusCodeAndCurrentDomainNameAndCurrentBonusTypeAndCurrentEnabledTrue(revision.getBonusCode(), domainName, revision.getBonusType());
		if (bonus != null) {
			log.info("A current bonus with code " + revision.getBonusCode() + " already exists on domain " + domainName + ": " + bonus);
		} else {
			bonus = new Bonus();
			bonusRepository.save(bonus);
		}
		
		revision.setBonus(bonus);
		revision.setDomain(d);
		bonusRevisionRepository.save(revision);
		
		bonus.setCurrent(revision);
		bonusRepository.save(bonus);
		
		if (signupRequirements != null) {
			signupRequirements.setBonusRevision(revision);
			bonusRequirementsSignupRepository.save(signupRequirements);
		}
		if (depositRequirements != null) {
			for (BonusRequirementsDeposit bonusRequirementsDeposit: depositRequirements) {
				bonusRequirementsDeposit.setBonusRevision(revision);
				bonusRequirementsDepositRepository.save(bonusRequirementsDeposit);
			}
		}
		
		if (gameRules != null) {
			for (BonusRulesGamesPercentages bonusRulesGamesPercentages: gameRules) {
				bonusRulesGamesPercentages.setBonusRevision(revision);
				bonusRulesGamesPercentagesRepository.save(bonusRulesGamesPercentages);
			}
		}
		
//		if (freeSpins != null) {
//			for (BonusRulesFreespins bonusRulesFreespins: freeSpins) {
//				bonusRulesFreespins.setBonusRevision(revision);
//				bonusRulesFreespinsRepository.save(bonusRulesFreespins);
//			}
//		}
		
		if ((freeSpinGames != null) && (freeSpinGames.length > 0)) {
			for (BonusRulesFreespinGames bonusRulesFreespinGames: freeSpinGames) {
				BonusRulesFreespins bonusRulesFreespins = bonusRulesFreespinGames.getBonusRulesFreespins();
				bonusRulesFreespins.setBonusRevision(revision);
				bonusRulesFreespins.setWagerRequirements(40);
				bonusRulesFreespins = casinoBonusFreespinService.findOrCreate(bonusRulesFreespins);
				bonusRulesFreespinGames.setBonusRulesFreespins(bonusRulesFreespins);
				bonusRulesFreespinGamesRepository.save(bonusRulesFreespinGames);
			}
		}
		
		return bonus;
	}

	public boolean cancelPendingBonus(String playerGuid, Long pendingBonusId) {
		
		PlayerBonusPending pbp = playerBonusPendingRepository.findOne(pendingBonusId);
		if (pbp != null && pbp.getPlayerGuid().equalsIgnoreCase(playerGuid)) {
			List<ChangeLogFieldChange> clfc;
			try {
				User user =  userService.findUserByGuid(playerGuid);
				clfc = changeLogService.copy(pbp, new PlayerBonusPending(), new String[] { "createdDate", "bonusRevision", "triggerAmount", "bonusAmount" });
				changeLogService.registerChangesWithDomain("user.bonus", "edit", user.getId(), tokenUtil().guid(), null, null, clfc, Category.BONUSES, SubCategory.BONUS_CANCELLED, 0, pbp.getPlayerGuid().substring(0, pbp.getPlayerGuid().indexOf('/'))); //don't have the user event id, oh well
			} catch (Exception | UserClientServiceFactoryException e1) {
				log.warn("Unable to perform changelog capture for pending deposit bonus cancel for player : " + playerGuid + " pending bonus id: " + pendingBonusId, e1);
			}
			
			try {
//				Response<AdjustmentTransaction> tid1 = 
				getAccountingClient().adjustMulti(
					pbp.getTriggerAmount(),
					DateTime.now().toString(),
					CasinoTranType.PLAYERBALANCE.value(), //accountCode
					CasinoTranType.PLAYERBALANCE.value(), //accountTypeCode
					CasinoTranType.TRANSFER_FROM_CASINO_BONUS_PENDING.value(), //transactionTypeCode
					CasinoTranType.PLAYER_BALANCE_CASINO_BONUS_PENDING.value(), //contraAccountCode
					CasinoTranType.PLAYERBALANCE.value(), //contraAccountTypeCode
					new String[] {PLAYER_BONUS_HISTORY_ID+"=-1", BONUS_REVISION_ID+"="+pbp.getBonusRevision().getBonus().getId()},
					casinoService.getCurrency(pbp.getBonusRevision().getDomain().getName()),
					pbp.getBonusRevision().getDomain().getName(),
					pbp.getPlayerGuid(),
					User.SYSTEM_GUID,
					false,
					null
				);
				
				getAccountingClient().adjustMulti(
						pbp.getBonusAmount(),
						DateTime.now().toString(),
						CasinoTranType.CASINO_BONUS_PENDING_CANCEL.value(), //accountCode
						CasinoTranType.CASINO_BONUS_PENDING.value(), //accountTypeCode
						CasinoTranType.CASINO_BONUS_PENDING_CANCEL.value(), //transactionTypeCode
						CasinoTranType.PLAYER_BALANCE_CASINO_BONUS_PENDING.value(), //contraAccountCode
						CasinoTranType.PLAYERBALANCE.value(), //contraAccountTypeCode
						new String[] {PLAYER_BONUS_HISTORY_ID+"=-1", BONUS_REVISION_ID+"="+pbp.getBonusRevision().getBonus().getId()},
						casinoService.getCurrency(pbp.getBonusRevision().getDomain().getName()),
						pbp.getBonusRevision().getDomain().getName(),
						pbp.getPlayerGuid(),
						User.SYSTEM_GUID,
						false,
						null
					);
				//FIXME: Add counter tran for casino bonus pending transaction (contains the bonus free money amount) so should go to a magical place like cancel pending free money
			} catch (Exception e) {
				log.error("Unable to cancel pending deposit bonus for player: " + playerGuid + " pending bonus id: " + pendingBonusId, e);
			}
			playerBonusPendingRepository.delete(pbp);
			
			try {
				casinoMailSmsService.sendBonusMail(CasinoMailSmsService.BONUS_STATE_CANCEL_PENDING, null, pbp);
			} catch (Exception e) {
				log.error("Failed to send bonus cancel pending email " + pbp);
			}

			try {
				casinoMailSmsService.sendBonusSms(CasinoMailSmsService.BONUS_STATE_CANCEL_PENDING, null, pbp);
			} catch (Exception e) {
				log.error("Failed to send bonus cancel pending sms " + pbp);
			}
			
			return true;
		}
		
		return false;
	}

	public List<BonusRevision> findPublicBonusList(String domainName, Integer type, Integer triggerType) {
		return findPublicBonusList(domainName, type, triggerType, null);
	}

	public String getNumberSuffix(int day) {
		String suffix = "";
		if (day >= 11 && day <= 13) {
			suffix = "th";
		} else {
			switch (day % 10) {
				case 1:
					suffix = "st";
					break;
				case 2:
					suffix = "nd";
					break;
				case 3:
					suffix = "rd";
					break;
				default:
					suffix = "th";
			}
		}
		return day + suffix;
	}
	
	public boolean registerFrontendBonus(String domainName, String playerGuid, String bonusCode) throws Exception {
		Bonus bonus = bonusRepository.findByCurrentBonusCodeAndCurrentDomainNameAndCurrentBonusTypeAndCurrentBonusTriggerTypeAndCurrentEnabledTrue(bonusCode, domainName, 2, TriggerType.TRIGGER_FRONTEND.type());
		if ((bonus != null) && (bonus.getCurrent() != null)) {
			casinoTriggerBonusService.processTriggerBonus(
				BonusAllocate.builder()
				.bonusCode(bonusCode)
				.playerGuid(playerGuid)
				.build()
			);
			return true;
		}
		return false;
	}
	
	public boolean registerHourlyBonus(String domainName, String playerGuid, String bonusCode) throws Exception {
		Bonus bonus = bonusRepository.findByCurrentBonusCodeAndCurrentDomainNameAndCurrentBonusTypeAndCurrentEnabledTrue(bonusCode, domainName, 2);
		BonusRevision bonusRevision = bonus.getCurrent();
		boolean instantBonus = true; // It should always be instant, if it is not the bonus was setup incorrectly
		BonusHourly bonusHourly = checkHourlyBonus(bonus, domainName, playerGuid);
		if (bonusHourly.getAvailable()) {
			PlayerBonusHistory pbh = savePlayerBonusHistory(bonus.getCurrent(), instantBonus);
			PlayerBonus pb = savePlayerBonus(pbh, playerGuid, instantBonus);
			pbh = updatePlayerBonusHistory(pbh, pb, 0L);
			log.info("Saved player bonus. ("+playerGuid+") :: "+pb);
			List<ChangeLogFieldChange> clfc = changeLogService.copy(pbh, new PlayerBonusHistory(), new String[] { "startedDate", "bonus" });
			log.info("Checking for free spins connected to bonus. ("+playerGuid+") :: "+pb);
			triggerFreeMoney(pbh, instantBonus);
			triggerAdditionalFreeMoney(pbh, instantBonus);
			triggerExternalBonusGame(pbh);
			casinoBonusFreespinService.triggerFreeSpins(pbh, instantBonus);
			casinoBonusUnlockGamesService.triggerUnlockGames(pbh);

			try {
				User user = userService.findUserByGuid(playerGuid);

				changeLogService.registerChangesWithDomain("user.bonus", "create", user.getId(), tokenUtil().guid(),
						null, null, clfc, Category.BONUSES, SubCategory.BONUS_REGISTER, 0,
						playerGuid.substring(0, playerGuid.indexOf('/')));
			}
			catch (Exception | UserClientServiceFactoryException e) {

			}


			if (bonusRevision.getActivationNotificationName() != null && !bonusRevision.getActivationNotificationName().isEmpty()) {
				streamBonusNotification(playerGuid, bonusRevision.getActivationNotificationName());
			}
			
			try {
				UserEvent userEventPlayerBonus = registerUserEventPlayerBonus(
					domainName,
					playerGuid.split("/")[1],
					playerBonusDisplay(playerGuid, pbh)
				);
				log.debug("User event player bonus (" + userEventPlayerBonus + ")");
			} catch (Exception e) {
				log.error("Failed to register user event for player bonus (" + pb + "), " + e.getMessage(), e);
			}
			
			try {
				casinoMailSmsService.sendBonusMail(CasinoMailSmsService.BONUS_STATE_ACTIVATE, pbh, null);
			} catch (Exception e) {
				log.error("Failed to send bonus activate email " + pb, e);
			}

			try {
				casinoMailSmsService.sendBonusSms(CasinoMailSmsService.BONUS_STATE_ACTIVATE, pbh, null);
			} catch (Exception e) {
				log.error("Failed to send bonus activate sms " + pb, e);
			}

			if (!instantBonus) {
				log.info("Checking if bonus should be instant. (" + playerGuid + ") :: " + pb);
				isBonusCompleted(pb);
			}
			
			statsStream(playerGuid, bonusCode);
			return true;
		}
		return false;
	}
	
	private void statsStream(String playerGuid, String bonusCode) {
		QueueStatEntry queueStatEntry = QueueStatEntry.builder()
		.type(lithium.service.stats.client.enums.Type.USER.type())
		.event(Event.BONUS_HOURLY.event())
		.entry(
			StatEntry.builder()
			.name(
				"stats." +
				lithium.service.stats.client.enums.Type.USER.type() + "." +
				playerGuid.replaceAll("/", ".") + "." +
				Event.BONUS_HOURLY.event()
			)
			.domain(playerGuid.split("/")[0])
			.ownerGuid(playerGuid)
			.build()
		)
		.build();
		log.info("StatStream :: "+queueStatEntry);
		statsStream.register(queueStatEntry);
//		dailyMissionStream(playerGuid, bonusCode);
	}

	//TODO: revist hourly bonus
//	private void dailyMissionStream(String playerGuid, String bonusCode) {
//		MissionStatBasic mse = MissionStatBasic.builder()
//		.ownerGuid(playerGuid)
//		.type(Type.TYPE_CASINO)
//		.action(Action.ACTION_BONUS_HOURLY)
//		.timezone(ZoneId.systemDefault().getId())
//		.identifier(bonusCode)
//		.build();
//		log.info("MissionStatBasic : "+mse);
//		missionStatsStream.register(mse);
//	}
	
	public List<BonusHourly> findHourlyBonus(String domainName, String playerGuid) {
		List<BonusHourly> revisionList = new ArrayList<>();
		// FIXME: 2019/12/12 This XP thing is just a stopgap until we can implement the bonus currency limitation engine
		Long xp = 0L;
		try {
			xp = getAccountingClient().get("XP", domainName, playerGuid).getData();
		} catch (Exception e) {
			// We don't care about this problem;
		}
		log.trace("findHourlyBonus: "+playerGuid+" xp: "+xp+"/"+xpRequiredForHourlyBonus);
		if (xp < xpRequiredForHourlyBonus) return revisionList; // :`(

		List<Bonus> bonusList = bonusRepository.findByCurrentBonusTypeAndCurrentBonusTriggerTypeAndCurrentDomainNameAndCurrentEnabledTrue(2,9,domainName);
		
		bonusList.forEach(bonus -> {
			try {
				revisionList.add(checkHourlyBonus(bonus, domainName, playerGuid));
			} catch (InvalidBonusException e) { /*ignoring exception, because we're not interested in error messages */ }
		});
		
		return revisionList;
	}
	
	public BonusHourly checkHourlyBonus(Bonus bonus, String domainName, String playerGuid) throws InvalidBonusException {
		BonusHourly bh = BonusHourly.builder()
		.bonusId(bonus.getId())
		.bonusCode(bonus.getCurrent().getBonusCode())
		.bonusName(bonus.getCurrent().getBonusName())
		.bonusDescription(bonus.getCurrent().getBonusDescription())
		.image(bonus.getCurrent().getGraphic() == null ? null : bonus.getCurrent().getGraphic().getImage())
		.build();
			
		if (isBonusValidForPlayerPreCheck(bonus, playerGuid)) {
			int timeout = bonus.getCurrent().getTriggerAmount().intValue();
			
			LocalDateTime hourAgo = LocalDateTime.now().minusHours(1);
			List<PlayerBonusHistory> playerBonusHistories = playerBonusHistoryRepository.findByPlayerBonusPlayerGuidAndStartedDateGreaterThanAndBonusBonusCode(playerGuid, hourAgo.toDate(), bonus.getCurrent().getBonusCode());
			log.info("playerBonusHistories : "+playerBonusHistories);
			if ((playerBonusHistories == null) || (playerBonusHistories.isEmpty())) {
				log.info("No player bonus history for last hour. checking with timeout.");
				playerBonusHistories = playerBonusHistoryRepository.findByPlayerBonusPlayerGuidAndStartedDateBetweenAndBonusBonusCode(playerGuid, hourAgo.minusMinutes(timeout).toDate(), hourAgo.toDate(), bonus.getCurrent().getBonusCode());
				log.info("Searched between : "+hourAgo.minusMinutes(timeout).toDate()+" - "+hourAgo.toDate());
				log.info("playerBonusHistories : "+playerBonusHistories);
				if (!playerBonusHistories.isEmpty()) {
					LocalDateTime ldt = new LocalDateTime(playerBonusHistories.get(0).getStartedDate());
					bh.setAvailable(true);
					bh.setMsTimeToAvailable(0L);
					bh.setTimeAvailableStart(ldt.plusHours(1).toDate());
					bh.setTimeAvailableEnd(ldt.plusHours(1).plusMinutes(timeout).toDate());
					log.info(bh.toString());
				} else {
					LocalDateTime start,end;
					LocalDateTime now = LocalDateTime.now();
					
					boolean isAfter = now.isAfter(now.withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).plusMinutes(timeout));
					
					log.info("timeout: "+(timeout*60*1000)+" isAfter: "+isAfter);
					if (!isAfter) {
						//falls within the top of the hour.
						bh.setAvailable(true);
						start = now.withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
						end = now.withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).plusMinutes(timeout);
						bh.setMsTimeToAvailable(0L);
					} else {
						//going over to next hour.
						bh.setAvailable(false);
						start = now.plusHours(1).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
						end = now.plusHours(1).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).plusMinutes(timeout);
						org.joda.time.Period p2 = new org.joda.time.Period(now, start);
						long diff2 = Math.abs(
							p2.getMillis()+
							(p2.getSeconds()*1000)+
							(p2.getMinutes()*60*1000)+
							(p2.getHours()*60*60*1000)
						);
						bh.setMsTimeToAvailable(diff2);
					}
					
					bh.setTimeAvailableStart(start.toDate());
					log.info("start : "+start.toDate());
					log.info("end : "+end.toDate());
					log.info(""+timeout);
					bh.setTimeAvailableEnd(end.toDate());
					log.info(bh.toString());
				}
			} else {
				log.info("player bonus history found in last hour.");
				bh.setAvailable(false);
				LocalDateTime ldt = new LocalDateTime(playerBonusHistories.get(0).getStartedDate());
				org.joda.time.Period p = new org.joda.time.Period(ldt.plusHours(1), LocalDateTime.now());
				long diff = Math.abs(
					p.getMillis()+
					(p.getSeconds()*1000)+
					(p.getMinutes()*60*1000)+
					(p.getHours()*60*60*1000)
				);
				bh.setMsTimeToAvailable(diff);
				bh.setTimeAvailableStart(ldt.plusHours(1).toDate());
				bh.setTimeAvailableEnd(ldt.plusHours(1).plusMinutes(timeout).toDate());
				log.info(bh.toString());
			}
		}
		
		return bh;
	}
	
	public List<BonusRevision> findPublicBonusList(String domainName, Integer type, Integer triggerType, String userGuid) {
		List<BonusRevision> revisionList = new ArrayList<>();
		List<Bonus> bonusList = bonusRepository.findByCurrentBonusTypeAndCurrentBonusTriggerTypeAndCurrentDomainNameAndCurrentEnabledTrueAndCurrentPublicViewTrue(type, triggerType, domainName);
		
		if (userGuid != null) {
			bonusList.forEach(bonus -> {
				try {
					if (isBonusValidForPlayerPreCheck(bonus, userGuid)) revisionList.add(bonus.getCurrent());
				} catch (InvalidBonusException e) {
					//ignoring exception, because we're not interested in error messages
				}
			});
		} else {
			bonusList.forEach(bonus -> { if (bonusValid(bonus)) revisionList.add(bonus.getCurrent()); });
		}
		
		return revisionList;
	}
	
	public boolean hasCurrentActiveBonusAndMayCancel(BonusRevision bonusRevision, String playerGuid) {
		PlayerBonus currentActiveBonus = findCurrentBonus(playerGuid);
		if (currentActiveBonus != null) {
			Long currentCasinoBonusBalance;
			try {
				currentCasinoBonusBalance = getCasinoBonusBalance(currentActiveBonus);
			} catch (Exception e) {
				return false;
			}
			Long cancelOnDepositMinimumAmount = bonusRevision.getCancelOnDepositMinimumAmount();
			boolean mayCancel = currentActiveBonus.getCurrent().getBonus().isPlayerMayCancel();
			if ((cancelOnDepositMinimumAmount != null) && (currentCasinoBonusBalance <= cancelOnDepositMinimumAmount))  {
				mayCancel = true;
			}
			return mayCancel;
		}
		return true;
	}
	
	public boolean isBonusDepositRequirementsMet(BonusRevision bonusRevision, Long depositCents) {
		BonusRequirementsDeposit brd = bonusRequirementsDeposit(bonusRevision.getId(), depositCents, false);
		if (brd == null) {
			brd = findTop1BonusRequirementsDeposit(bonusRevision.getId());
			if (brd == null) {
				//No bonus requirements were saved for this bonus
				return true;
			} else {
				if ((depositCents >= brd.getMinDeposit()) && (depositCents <= brd.getMaxDeposit())) {
					return true;
				}
				return false;
			}
		} else {
			if ((depositCents >= brd.getMinDeposit()) && (depositCents <= brd.getMaxDeposit())) {
				return true;
			}
			return false;
		}
	}
	
	public boolean isBonusValidForPlayerPreCheck(Bonus bonus, String playerGuid) throws InvalidBonusException {
		List<String> errorList = new ArrayList<>();
		boolean isValid = true;
		
		BonusRevision bonusRevision = bonus.getCurrent();
		if (bonusRevision == null) isValid &= false;
		if (!isValid) {
			errorList.add("The bonus code does not exist.");
		} else {
//			isValid &= hasCurrentActiveBonusAndMayCancel(bonusRevision, playerGuid);
//			if (!isValid) errorList.add("You have a running bonus that may not be cancelled.");
			
			isValid &= bonusValidForPlayer(bonus, playerGuid);
			if (!isValid) {
				errorList.add("The bonus code you have entered is not valid.");
			} else {
				Bonus dependsOnBonus = bonusRevision.getDependsOnBonus();
				
				if (dependsOnBonus != null) {
					List<PlayerBonusHistory> playerParentBonusHistory = findPlayerBonusHistory(playerGuid, dependsOnBonus.getCurrent().getBonusCode());
					if (playerParentBonusHistory.size() == 0) {
						isValid &= false;
						if (!isValid) errorList.add("To register for this bonus you will first need to register for the '"+dependsOnBonus.getCurrent().getBonusName()+"' bonus.");
					}
				}
				
				try {
					if (!checkMaxRedeemableValid(bonusRevision, playerGuid)) {
						isValid &= false;
						if (!isValid) errorList.add("You have already taken up this bonus as many times as are allowed.");
					}
					
					SummaryAccountTransactionType stt = checkDeposits(bonusRevision.getDomain().getName(), playerGuid, Period.GRANULARITY_TOTAL);
					Integer forDepositNumber = bonusRevision.getForDepositNumber();
					if (forDepositNumber != null) {
						if (stt != null) {
							if (forDepositNumber.longValue() != (stt.getTranCount() +1 )) {
								isValid &= false;
							}
						} else {
							//case when no entries are found in summary acocunting, means zero deposits, so valid for deposit number 1 only
							if (forDepositNumber.longValue() != 1L) {
								isValid &= false;
							}
						}
						if (!isValid) errorList.add("This bonus is only valid for your "+ getNumberSuffix(forDepositNumber) + " deposit.");
					}
				} catch (Exception e) {
					isValid = false;
					errorList.add("Failed to determine if you are eligible for this bonus.");
					log.warn("Problem with evaluation of bonus validity: bonus: "+ bonus + " playerguid: " + playerGuid);
				}
			}
		}
		if (!isValid) throw new InvalidBonusException(errorList);
		
		return isValid;
	}
	
	public Long getPlayerRealBalanceExcludingBonusBalance(String playerGuid, String domainName) {
		Response<Long> response = null;
		try {
			response = getAccountingClient().getByOwnerGuid(domainName, CasinoTranType.PLAYERBALANCE.toString(), CasinoTranType.PLAYERBALANCE.toString(), casinoService.getCurrency(domainName), playerGuid);
		} catch (Exception e) {
			log.error("Unable to get player balance: playerguid: "+playerGuid + e.getMessage(), e);
			return 0L;
		}
		if (response.getStatus() == Status.OK) {
			return response.getData();
		} else {
			return 0L;
		}
	}

	/**
	 * Create player bonus object with no bound bonus to avoid all kinds of nasty null errors.
	 * @param userGuid
	 * @return
	 */
	public PlayerBonus createPlayerBonus(String userGuid) {
		return playerBonusRepository.save(PlayerBonus.builder().balance(0L).current(null).playerGuid(userGuid).build());
	}
	
	public void streamBonusNotification(String playerGuid, String notificationName) {
		log.info("Streaming bonus notification " + notificationName + " for " + playerGuid);
		notificationStream.process(UserNotification.builder().userGuid(playerGuid).notificationName(notificationName).build());
	}

	public void removeFailedPlayerBonusHistory(PlayerBonusHistory pbh) {
		playerBonusHistoryRepository.delete(pbh);
	}

	public void deleteBonus(Long id, Principal principal) throws Status404BonusNotFoundException, Status405BonusDeleteNotAllowedException {
		Bonus bonusRetrieved = bonusRepository.findById(id).orElseThrow(() -> new Status404BonusNotFoundException("Bonus with given id not found"));
		BonusRevision bonusRevision = bonusRetrieved.getCurrent() != null ? bonusRetrieved.getCurrent() : bonusRetrieved.getEdit();
		Date currentDate = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String timeStamp = formatter.format(currentDate);
		String newBonusName = bonusRevision.getBonusCode() + "_" + timeStamp.replace(" ", "_");
		bonusRevision.setBonusCode(newBonusName);
		bonusRevision.setBonusName(newBonusName);
		bonusRevision.setDeleted(true);
		bonusRevision.setEnabled(false);
		bonusRevisionRepository.save(bonusRevision);
		LithiumTokenUtil util = tokenService.getUtil(principal);
		log.info("User : {} Deleted bonus with id : {}", util.guid(), id);

		try {
			List<ChangeLogFieldChange> clfc = changeLogService.copy(bonusRevision, new BonusRevision(), new String[]{"deleted"});
			changeLogService.registerChangesForNotesWithFullNameAndDomain("bonusrevision", "delete", bonusRevision.getId(), util.guid(), util,
					bonusRevision.getBonusName(), null, clfc, Category.BONUSES, SubCategory.BONUS_DELETED, 0, bonusRevision.getDomain().getName());
		} catch (Exception e) {
			log.error("Failed to register bonus changelogs for deleteBonus " + bonusRevision.getBonusName() + ", (Id: " + bonusRevision.getId() + ")", e);
		}
	}

	public LithiumTokenUtil tokenUtil() {
		LithiumTokenUtil util = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		//We need to build the LithiumTokenUtil from the spring security context
		if (authentication instanceof OAuth2Authentication) {
			util = LithiumTokenUtil.builder(tokenStore, authentication).build();
		}

		return util;
	}
}

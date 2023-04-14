package lithium.service.promo.services;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.metrics.TimeThisMethod;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.promo.client.exception.Status464NoScheduledEventForPeriodException;
import lithium.service.promo.client.objects.Granularity;
import lithium.service.promo.context.PromoContext;
import lithium.service.promo.data.entities.Challenge;
import lithium.service.promo.data.entities.ChallengeGroup;
import lithium.service.promo.data.entities.Period;
import lithium.service.promo.data.entities.Promotion;
import lithium.service.promo.data.entities.PromotionRevision;
import lithium.service.promo.data.entities.PromotionStat;
import lithium.service.promo.data.entities.Rule;
import lithium.service.promo.data.entities.User;
import lithium.service.promo.data.entities.UserPromotion;
import lithium.service.promo.data.entities.UserPromotionChallenge;
import lithium.service.promo.data.entities.UserPromotionChallengeGroup;
import lithium.service.promo.data.entities.UserPromotionChallengeRule;
import lithium.service.promo.data.repositories.RuleRepository;
import lithium.service.promo.data.repositories.UserPromotionChallengeGroupRepository;
import lithium.service.promo.data.repositories.UserPromotionChallengeRepository;
import lithium.service.promo.data.repositories.UserPromotionChallengeRuleRepository;
import lithium.service.promo.data.repositories.UserPromotionRepository;
import lithium.service.promo.data.specifications.UserPromotionSpecifications;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.service.xp.client.XPClient;
import lithium.service.xp.client.objects.Level;
import lithium.tokens.LithiumTokenUtil;
import lithium.tokens.LithiumTokenUtilService;
import lombok.extern.slf4j.Slf4j;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserPromotionService {
	@Autowired RuleRepository ruleRepository;
	@Autowired
	UserPromotionRepository userPromotionRepository;
	@Autowired
	UserPromotionChallengeRepository userPromotionChallengeRepository;
	@Autowired
	UserPromotionChallengeRuleRepository userPromotionChallengeRuleRepository;
	@Autowired UserService userService;
	@Autowired DomainService domainService;
	@Autowired PeriodService periodService;
	@Autowired RecurrenceRuleService recurrenceRuleService;
	@Autowired
	PromotionService promotionService;

	@Autowired
	UserPromotionChallengeGroupRepository userPromotionChallengeGroupRepository;

	@Autowired LithiumServiceClientFactory services;

	@Autowired private UserPromotionService self;

	@Autowired private ChangeLogService changeLogService;

	@Autowired private UserApiInternalClientService userApiInternalClientService;

	@Autowired private LithiumTokenUtilService tokenUtilService;

	private final ZoneId DEFAULT_TIMEZONE_ID = ZoneId.of("UTC");

	@Transactional(propagation = Propagation.MANDATORY)
	public List<UserPromotion> lockingUpdate(String playerGuid, Long promotionRevisionId, Long periodId) {
		try {
			List<UserPromotion> userPromotions = userPromotionRepository.findByUserGuidAndPromotionRevisionAndPeriodForUpdate(playerGuid, promotionRevisionId, periodId);
			userPromotions.forEach(um -> log.trace("UM: " + um.getId()));
			log.trace("Acquired lock on userPromotions("+ userPromotions.size()+") playerGuid: {}, promotionId: {}, periodId: {}", playerGuid, promotionRevisionId, periodId);
			return userPromotions;
		} catch (Exception e) {
			log.error("Failed to acquire lock on userPromotions playerGuid: {}, promotionId: {}, periodId: {}", playerGuid, promotionRevisionId, periodId);
			throw e;
		}
	}

	@TimeThisMethod
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor=Exception.class)
	public void findUserPromotions(PromoContext promoContext)
	throws InvalidRecurrenceRuleException
	{
		//Get promotions filtered from PromotionStatsService#processpromotionTags (This is to avoid re-fetching the promotions if it is not necessary)

		for (Promotion promotion : promoContext.getPromotions()) {
			PromotionRevision currentRevision = promotion.getCurrent();
			if (log.isDebugEnabled()) recurrenceRuleService.firstEvents(promotion, 25);
			log.debug("------------------------------------------------------");
			log.debug("promo id: " + currentRevision.getId() + " name: " + currentRevision.getName());
			Period period = null;
			try {
				period = recurrenceRuleService.addPeriod(promotion);
			} catch (Status464NoScheduledEventForPeriodException e) {
				log.debug("No scheduled events found for this period, skipping. {}", promoContext.toString());
				continue;
			}
			ChronoUnit chronoUnit = switch (Granularity.fromGranularity(currentRevision.getEventDurationGranularity())) {
				case GRANULARITY_YEAR -> ChronoUnit.YEARS;
				case GRANULARITY_MONTH -> ChronoUnit.MONTHS;
				case GRANULARITY_DAY -> ChronoUnit.DAYS;
				case GRANULARITY_WEEK -> ChronoUnit.WEEKS;
				case GRANULARITY_TOTAL -> ChronoUnit.CENTURIES;
				case GRANULARITY_HOUR -> ChronoUnit.HOURS;
			};
			int countTotal = userPromotionRepository.countByUserGuidAndPromotionRevisionId(promoContext.playerGuid(), currentRevision.getId());
			log.debug(
					"Searching UserPromo: guid:" + promoContext.playerGuid() + " rev:" + currentRevision.getId() + " period:" + period.getId() + " total count:"
							+ countTotal);
			/** @see: #1 in Diagram */
			/** @see: docs/comps-engine/plantuml/promo-engine-activity-rrule.puml */
			List<UserPromotion> userPromotions = self.lockingUpdate(promoContext.playerGuid(), currentRevision.getId(), period.getId());
			if (userPromotions.isEmpty()) {
				/** @see: #2 in Diagram */
				if (countTotal < currentRevision.getRedeemableInTotal()) {
					/** @see: #2.1 in Diagram */
					if (promotion.getCurrent().getDependsOnPromotion() != null) {
						/** @see: #3 in Diagram */
						if (dependantPromoRequirementsCompleted(promoContext, promotion)) {
							/** @see: #4 in Diagram */
							verifyPlayerXPAndSave(promoContext, promotion, period);
						} else {
							/** @see: #5 in Diagram */
							log.warn("Player has not completed the dependant promo.");
						}
					} else {
						/** @see: #6 in Diagram */
						verifyPlayerXPAndSave(promoContext, promotion, period);
					}
				} else {
					/** @see: #2.2 in Diagram */
					log.warn("Player participated maximum allowed times.");
				}
			} else {
				/** @see: #7 in Diagram */
				List<UserPromotion> incompletePromotions = userPromotions.stream().filter(um -> !um.getPromotionComplete()).toList();
				if (incompletePromotions.isEmpty()) {
					/** @see: #8 in Diagram */
					// Check less than promo redeemablelnTotal/redeemableInEvent
					int eventsCompleted = userPromotions.size();
					if ((countTotal < currentRevision.getRedeemableInTotal()) && (eventsCompleted < currentRevision.getRedeemableInEvent())) {
						/** @see: #9 in Diagram */
						verifyPlayerXPAndSave(promoContext, promotion, period);
					} else {
						/** @see: #10 in Diagram */
						log.debug("User has completed max amount of promotions for this promotion for this period.");
					}
				} else {
					/** @see: #11 in Diagram */
					UserPromotion userPromotion = incompletePromotions.get(0);
					ZonedDateTime expiryDateTime = userPromotion.getStarted().toLocalDate().plus(1, chronoUnit).atStartOfDay(promoContext.getUserZoneId());
					if (!promoContext.getUserZonedDateTime().isBefore(expiryDateTime)) { // check if expired
						/** @see: #12 in Diagram */
						log.debug("UserPromotion (" + userPromotion.getId() + ") expired! ");
						userPromotion.setActive(false);
						userPromotion.setExpired(true);
						save(userPromotion);
					} else {
						/** @see: #13 in Diagram */
						log.debug("returning user promo: {} ", userPromotion.toString());
						promoContext.addUserPromotion(userPromotion);
					}
				}
			}
		}
	}

	private boolean dependantPromoRequirementsCompleted(PromoContext promoContext, Promotion promotion) {
		Promotion parent = promotion.getCurrent().getDependsOnPromotion();
		int countCompletedParentEvents = userPromotionRepository.countByUserGuidAndPromotionRevisionIdAndCompletedIsNotNullAndPromotionCompleteIsTrue(promoContext.playerGuid(), parent.getCurrent().getId());
		return countCompletedParentEvents > 0;
	}


	public Page<UserPromotion> findUserPromotionsByDomains(
			List<String> domains,
			String userGuid,
			Boolean active,
			Boolean current,
			String startedDateRangeStart,
			String startedDateRangeEnd,
			PromotionRevision promotionRevision,
			String searchValue,
			Pageable pageable
	) {
		boolean showOnlyForCurrentPromotions = Optional.ofNullable(current).orElse(false);

		Specification<UserPromotion> spec = Specification.where(UserPromotionSpecifications.domains(domains));
		if (userGuid != null && !userGuid.isEmpty()) spec = spec.and(UserPromotionSpecifications.user(userGuid));
		if (active != null && active) spec = spec.and(UserPromotionSpecifications.isActive());
		if (startedDateRangeStart != null && !startedDateRangeStart.isEmpty()) {
			DateTime dtStartedDateRangeStart = DateTimeFormat.forPattern("dd/MM/yyyy").parseDateTime(startedDateRangeStart);
			dtStartedDateRangeStart = dtStartedDateRangeStart.withTimeAtStartOfDay();
			spec = spec.and(UserPromotionSpecifications.startedDateRangeStart(dtStartedDateRangeStart.toDate()));
		}
		if (startedDateRangeEnd != null && !startedDateRangeEnd.isEmpty()) {
			DateTime dtStartedDateRangeEnd = DateTimeFormat.forPattern("dd/MM/yyyy").parseDateTime(startedDateRangeEnd);
			dtStartedDateRangeEnd = dtStartedDateRangeEnd.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);
			spec = spec.and(UserPromotionSpecifications.startedDateRangeEnd(dtStartedDateRangeEnd.toDate()));
		}
		if (promotionRevision != null) spec = spec.and(UserPromotionSpecifications.promotionRevision(promotionRevision));

		if(showOnlyForCurrentPromotions) {
			Specification<UserPromotion> s = Specification.where(UserPromotionSpecifications.isCurrent());
			spec = (spec == null) ? s : spec.and(s);
		}
		return userPromotionRepository.findAll(spec, pageable);
	}

	public UserPromotion findActiveByUser(String userGuid) throws Exception {
		return findActiveByUser(userService.findOrCreate(userGuid));
	}

	public UserPromotion findActiveByUser(User user) {
		return userPromotionRepository.findByUserAndCompletedIsNullAndPromotionCompleteIsFalseAndExpiredIsFalseAndActiveIsTrue(user);
	}

	private void verifyPlayerXPAndSave(PromoContext promoContext, Promotion promotion, Period period) {
		boolean playerMeetsXpRequirements = true;

		if (promotion.getCurrent().getXpLevel() > 0) {
			XPClient xpClient = getXPClient();
			if (xpClient == null) {
				playerMeetsXpRequirements = false;
			} else {
				Level level = Optional.ofNullable(xpClient.getLevel(promoContext.playerGuid(), promoContext.domainName()).getData())
						.orElse(Level.builder().number(0).build());
				playerMeetsXpRequirements = level.getNumber() >= promotion.getCurrent().getXpLevel();
			}
		}
		if (playerMeetsXpRequirements) {
//			UserPromotion userPromotion =
			saveComplete(promoContext, promotion, period);
		} else {
			log.info(String.format("Could not create user promotion for player: %s, the player does not meet the required XP for this promotion, required promotion xp: %s.",
					promoContext.getUser(), promotion.getCurrent().getXpLevel()));
		}
	}

	private UserPromotion saveComplete(PromoContext promoContext, Promotion promotion, Period period) {
		UserPromotion userPromotion = save(
				UserPromotion.builder()
						.user(promoContext.getUser())
						.promotionRevision(promotion.getCurrent())
						.timezone(promoContext.getUser().getTimezone())
						.started(ZonedDateTime.now(DEFAULT_TIMEZONE_ID).toLocalDateTime())
						.period(period)
						.expired(false)
						.active(true)
						.percentage(BigDecimal.ZERO)
						.promotionComplete(false)
						.build()
		);

		for(ChallengeGroup challengeGroup: userPromotion.getPromotionRevision().getChallengeGroups()) {

			UserPromotionChallengeGroup userPromotionChallengeGroup = UserPromotionChallengeGroup.builder()
					.challengeGroup(challengeGroup)
					.userPromotion(userPromotion)
					.build();

			userPromotionChallengeGroupRepository.save(userPromotionChallengeGroup);

			for (Challenge c : challengeGroup.getChallenges()) {
				UserPromotionChallenge umc = save(
						UserPromotionChallenge.builder()
								.userPromotion(userPromotion)
								.challenge(c)
								.started(ZonedDateTime.now(DEFAULT_TIMEZONE_ID).toLocalDateTime())
								.percentage(BigDecimal.ZERO)
								.userPromotionChallengeGroup(userPromotionChallengeGroup)
								.challengeComplete(false)
								.build()
				);


				for (Rule r : c.getRules()) {
					UserPromotionChallengeRule umcr = save(
							UserPromotionChallengeRule.builder()
									.userPromotionChallenge(umc)
									.rule(r)
									.percentage(BigDecimal.ZERO)
									.ruleComplete(false)
									.build()
					);
					umc.addRule(umcr);
				}

				userPromotionChallengeGroup.addChallenge(umc);
				userPromotion.addChallengeGroup(userPromotionChallengeGroup);
				userPromotionChallengeGroupRepository.save(userPromotionChallengeGroup);
			}
		}
		userPromotion = refresh(userPromotion);
		promoContext.addUserPromotion(userPromotion);

		String message = MessageFormat.format("Player {0} now participating in promotion {1} revision {2}", userPromotion.getUser().guid(), userPromotion.getPromotionRevision().getName(), userPromotion.getPromotionRevision().getId());
		registerChangelogs(userPromotion, message);
		log.info(message);
		return userPromotion;
	}

	public UserPromotion refresh(UserPromotion userPromotion) {
		return userPromotionRepository.findOne(userPromotion.getId());
	}
	public UserPromotion save(UserPromotion userPromotion) {
		return userPromotionRepository.save(userPromotion);
	}

	public UserPromotionChallengeRule save(UserPromotionChallengeRule userPromotionChallengeRule) {
		return userPromotionChallengeRuleRepository.save(userPromotionChallengeRule);
	}
	public UserPromotion findUserPromotion(Long userPromotionId) {
		return userPromotionRepository.findOne(userPromotionId);
	}
	public UserPromotionChallenge save(UserPromotionChallenge userPromotionChallenge) {
		return userPromotionChallengeRepository.save(userPromotionChallenge);
	}

	public UserPromotionChallengeGroup save(UserPromotionChallengeGroup userPromotionChallengeGroup) {
		return userPromotionChallengeGroupRepository.save(userPromotionChallengeGroup);
	}
	public UserPromotionChallenge findChallenge(Long challengeId) {
		return userPromotionChallengeRepository.findOne(challengeId);
	}

	public UserPromotionChallengeGroup findChallengeGroup(Long id) {
		return userPromotionChallengeGroupRepository.findOne(id);
	}

	public void updateUserPromotionChallengeRuleStarted(UserPromotionChallengeRule userPromotionChallengeRule) {
		userPromotionChallengeRuleRepository.updateRuleStarted(userPromotionChallengeRule.getId(), userPromotionChallengeRule.getStarted());
	}

	public void updateUserPromotionChallengeRulePercentage(UserPromotionChallengeRule userPromotionChallengeRule, BigDecimal percentage) {
		userPromotionChallengeRuleRepository.updatePercentage(userPromotionChallengeRule.getId(), percentage);
	}
	public void updateUserPromotionChallengeRulePromotionStat(UserPromotionChallengeRule userPromotionChallengeRule, PromotionStat promotionStat) {
		userPromotionChallengeRuleRepository.updatePromotionStat(userPromotionChallengeRule.getId(), promotionStat);
	}
	public void completeUserPromotionChallengeRule(UserPromotionChallengeRule userPromotionChallengeRule) {
		userPromotionChallengeRuleRepository.completeRule(userPromotionChallengeRule.getId(), LocalDateTime.now());
	}

	public List<UserPromotion> findCurrent(String userGuid) {

		Specification<UserPromotion> spec = Specification.where(UserPromotionSpecifications.isCurrent());
		spec = spec.and(UserPromotionSpecifications.user(userGuid));
		spec = spec.and(UserPromotionSpecifications.completed(false));

		return userPromotionRepository.findAll(spec);
	}

	public List<UserPromotion> findCompleted(String userGuid) {

		Specification<UserPromotion> spec = Specification.where(UserPromotionSpecifications.user(userGuid));
		spec = spec.and(UserPromotionSpecifications.completed(true));

		return userPromotionRepository.findAll(spec);
	}

	public boolean doesChallengeHavePrerequisiteChallenges(UserPromotionChallengeGroup group , UserPromotionChallenge userPromotionChallenge) {

		Challenge currentChallenge = userPromotionChallenge.getChallenge();

		boolean shouldCheckForPreviousChallenges =  Optional.ofNullable(group.getChallengeGroup().getSequenced()).orElse(false);
		boolean doesHavePrerequisiteChallenges = false;

		if (shouldCheckForPreviousChallenges) {
			 Optional<Challenge> previous = group.getChallengeGroup().getChallenges().stream().filter(c -> c.getSequenceNumber() < currentChallenge.getSequenceNumber())
					 .findFirst();

			 log.debug("Checking a prerequisite challenge for challenge [{}] in group {}", currentChallenge.getDescription(), currentChallenge.getChallengeGroup().getId());
			 if (previous.isPresent()) {
				 Challenge previousChallenge = previous.get();
				 log.debug("The [{}] challenge is a prerequisite to challenge [{}], now checking for completed user promotion", previousChallenge.getDescription(), currentChallenge.getDescription());


				 Optional<UserPromotionChallenge> previousUserChallenge = group.getUserPromotionChallenges().stream().filter(uc -> Objects.equals(uc.getChallenge().getId(), previousChallenge.getId()))
						 .findFirst();


				 doesHavePrerequisiteChallenges = !previousUserChallenge.isPresent() || !Optional.of(previousUserChallenge.get().getChallengeComplete()).orElse(false);

				 if (doesHavePrerequisiteChallenges) {
					 log.warn("Challenge [{}] cannot be completed before [{}]", currentChallenge.getDescription(), previousChallenge.getDescription());
				 }

			 }
			 else {
				 doesHavePrerequisiteChallenges = false;
				 log.debug("{} is the first challenge in the group", currentChallenge.getDescription());
			 }
		}

		return doesHavePrerequisiteChallenges;
	}


	private XPClient getXPClient() {
		XPClient client = null;
		try {
			client = services.target(XPClient.class, "service-xp", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting XPClient, " + e.getMessage(), e);
		}
		return client;
	}

	public void registerChangelogs(UserPromotion userPromotion, String comment) {
		try {
			String authorGuid = lithium.service.user.client.objects.User.SYSTEM_GUID;
			String authorName = lithium.service.user.client.objects.User.SYSTEM_FULL_NAME;
			LithiumTokenUtil util = tokenUtilService.getUtilForCurrentPrincipal();

			if (util != null) {
				authorName = util.userLegalName();
				authorGuid = util.guid();
			}

			lithium.service.user.client.objects.User externalUser = userApiInternalClientService.getUserByGuid(userPromotion.getUser().guid());

			changeLogService.registerChangesWithDomainAndFullName("user.promotion", "comment", externalUser.getId(), authorGuid, comment,  null, null, Category.PROMOTIONS, SubCategory.PROMOTION_PARTICIPATION, 10, externalUser.getDomain().getName(), authorName);
		} catch (UserClientServiceFactoryException|Exception exception) {
			log.error(exception.getMessage());
		}
	}
}
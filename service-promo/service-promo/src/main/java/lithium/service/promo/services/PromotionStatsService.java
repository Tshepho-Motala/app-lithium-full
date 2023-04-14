package lithium.service.promo.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.metrics.TimeThisMethod;
import lithium.service.promo.client.enums.Operation;
import lithium.service.promo.client.objects.PromoActivityBasic;
import lithium.service.promo.client.objects.UserCategoryType;
import lithium.service.promo.context.PromoContext;
import lithium.service.promo.data.entities.ActivityExtraFieldRuleValue;
import lithium.service.promo.data.entities.Challenge;
import lithium.service.promo.data.entities.ChallengeGroup;
import lithium.service.promo.data.entities.Domain;
import lithium.service.promo.data.entities.Period;
import lithium.service.promo.data.entities.PromotionRevision;
import lithium.service.promo.data.entities.PromotionStat;
import lithium.service.promo.data.entities.PromotionStatEntry;
import lithium.service.promo.data.entities.PromotionStatSummary;
import lithium.service.promo.data.entities.Reward;
import lithium.service.promo.data.entities.Rule;
import lithium.service.promo.data.entities.User;
import lithium.service.promo.data.entities.UserCategory;
import lithium.service.promo.data.entities.UserPromotion;
import lithium.service.promo.data.entities.UserPromotionChallenge;
import lithium.service.promo.data.entities.UserPromotionChallengeGroup;
import lithium.service.promo.data.entities.UserPromotionChallengeRule;
import lithium.service.promo.data.repositories.ActivityExtraFieldRuleValueRepository;
import lithium.service.promo.data.repositories.PromotionStatEntryRepository;
import lithium.service.promo.data.repositories.PromotionStatRepository;
import lithium.service.promo.data.repositories.PromotionStatSummaryRepository;
import lithium.service.promo.data.repositories.UserPromotionRepository;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PromotionStatsService {
	@Autowired
	PromotionService promotionService;
	@Autowired
	UserPromotionService userPromotionService;
	@Autowired
	UserPromotionUpdateService userPromotionUpdateService;
	@Autowired
	PromotionStatRepository promotionStatRepository;
	@Autowired
	PromotionStatEntryRepository promotionStatEntryRepository;
	@Autowired
	PromotionStatSummaryRepository promotionStatSummaryRepository;
	@Autowired
	UserPromotionRepository userPromotionRepository;

	@Autowired
	ActivityExtraFieldRuleValueRepository activityExtraFieldRuleValueRepository;
	@Autowired UserService userService;
	@Autowired DomainService domainService;
	@Autowired LabelService labelService;
	@Autowired LabelValueService labelValueService;
	@Autowired PeriodService periodService;
	@Autowired RewardService rewardService;
	@Autowired UserApiInternalClientService userApiInternalClientService;

	@Autowired private PromotionStatsService self;

	private final ZoneId DEFAULT_TIMEZONE_ID = ZoneId.of("UTC");

	public List<PromotionStatSummary> find(String name, String playerGuid) {
		return promotionStatSummaryRepository.findByPromotionStatNameAndOwnerGuid(name, playerGuid);
	}

	@TimeThisMethod
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor=Exception.class)
	public void registerStatsAndCalculatePercentages(PromoContext promoContext) throws Exception {
		if (promoContext.getUserPromotions() == null) return;
		for (UserPromotion um:promoContext.getUserPromotions()) {
			log.debug("um: " + um.toString());
			PromotionStat promotionStat = null;

			for (UserPromotionChallengeGroup group: um.getUserChallengeGroups()) {

				for (UserPromotionChallenge umc: group.getUserPromotionChallenges()) {

					if (userPromotionService.doesChallengeHavePrerequisiteChallenges(group, umc)) {
						log.debug("Cannot participate in challenge [{}] because there are prerequisite challenges in group {} that must be completed first",
								umc.getChallenge().getDescription(), umc.getChallenge().getChallengeGroup().getId());
						continue;
					}

					log.debug("ChallengeFE :: " + umc);
					for (UserPromotionChallengeRule umcr: umc.getRules()) {
						Rule r = umcr.getRule();
						log.debug("Rule :: " + r);
						if (shouldRegisterStat(promoContext, r, umcr)) {
							promotionStat = registerPromoStat(um, promoContext, umcr);
							if (umcr.getStarted() == null)
								umcr.setStarted(ZonedDateTime.now(DEFAULT_TIMEZONE_ID).toLocalDateTime());
							if (umcr.getPromotionStat() == null)
								umcr.setPromotionStat(promotionStat);

							calculateRulePercentage(um, promotionStat, promoContext.playerGuid(), umcr);

							userPromotionService.save(umcr);
						}
					}
					calculateChallengePercentage(umc, true);
				}

				calculateChallengeGroupPercentage(group, true);
			}
			calculatePromotionPercentage(um, true);
			/*
			 stream usermissionupdate after calculating and saving updated percentages userMissionUpdateService stream
			 */
		}
	}

	protected void calculatePromotionPercentage(UserPromotion userPromotion, boolean retrieveFreshDB) {
		if (retrieveFreshDB) userPromotion = userPromotionService.findUserPromotion(userPromotion.getId());
		BigDecimal requiredValue = BigDecimal.ZERO;
		BigDecimal currentValue;

		boolean promotionDoesNotRequireAllGroups = BooleanUtils.isNotTrue(userPromotion.getPromotionRevision().getRequiresAllChallengeGroups());

		Comparator<UserPromotionChallengeGroup> withHighPercentage = (o1, o2) -> {
			BigDecimal second = Optional.ofNullable(o2.getPercentage()).orElse(BigDecimal.ZERO);
			BigDecimal first = Optional.ofNullable(o1.getPercentage()).orElse(BigDecimal.ZERO);
			return first.compareTo(second);
		};

		if (promotionDoesNotRequireAllGroups) {
			requiredValue = BigDecimal.valueOf(100L);
			currentValue = userPromotion.getUserChallengeGroups().stream()
					.max(withHighPercentage)
					.map(UserPromotionChallengeGroup::getPercentage)
					.orElse(BigDecimal.ZERO);
		} else {
			requiredValue = requiredValue.add(BigDecimal.valueOf(userPromotion.getUserChallengeGroups().size() * 100L));
			currentValue = userPromotion.getUserChallengeGroups().stream()
					.map(u -> Optional.ofNullable(u.getPercentage()).orElse(BigDecimal.ZERO))
					.reduce(BigDecimal.ZERO, BigDecimal::add);
		}

		BigDecimal percentage = (currentValue.divide(requiredValue, 2, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(100L)).setScale(2, RoundingMode.HALF_UP);
		log.debug("Promotion :: requiredValue : "+requiredValue+"  currentValue : "+currentValue+"  percentage : "+percentage);
		userPromotion.setPercentage(percentage);
		if (percentage.intValue() >= 100) {
			userPromotion.setPercentage(BigDecimal.valueOf(100L));
			userPromotion.setCompleted(ZonedDateTime.now(DEFAULT_TIMEZONE_ID).toLocalDateTime());
			userPromotion.setPromotionComplete(true);
			triggerReward(userPromotion.getUser().guid(), userPromotion.getPromotionRevision().getReward());

			String message = MessageFormat.format("Player {0} has completed the promotion event for promotion {1} revision {2}", userPromotion.getUser().guid(), userPromotion.getPromotionRevision().getName(), userPromotion.getPromotionRevision().getId());
			userPromotionService.registerChangelogs(userPromotion, message);
			log.info(message);
		}
		userPromotionService.save(userPromotion);
	}

	protected UserPromotionChallenge calculateChallengePercentage(UserPromotionChallenge umc, boolean retrieveFreshDB) {
		if (retrieveFreshDB) umc = userPromotionService.findChallenge(umc.getId());
		BigDecimal requiredValue = BigDecimal.ZERO;
		BigDecimal currentValue = BigDecimal.ZERO;

		boolean challengeDoesNotRequireAllRules = BooleanUtils.isNotTrue(umc.getChallenge().getRequiresAllRules());

		Comparator<UserPromotionChallengeRule> withHighPercentage = (o1, o2) -> {
			BigDecimal second = Optional.ofNullable(o2.getPercentage()).orElse(BigDecimal.ZERO);
			BigDecimal first = Optional.ofNullable(o1.getPercentage()).orElse(BigDecimal.ZERO);
			return first.compareTo(second);
		};


		boolean rulesContributeToOverallChallengePercentage = umc.getRules().stream()
				.allMatch(r -> r.getRule().getOperation() != Operation.LAST_VALUE);

		if (challengeDoesNotRequireAllRules) {
			//Each rule can contribute any amount in percentage towards the challenge and all the sum of all rule percentage values must be >= 100%
			requiredValue = requiredValue.add(BigDecimal.valueOf(100L));

		} else {
			//Each rule must contribute 100% towards the challenge
			requiredValue = requiredValue.add(BigDecimal.valueOf(umc.getRules().size() * 100L));
		}

		if (rulesContributeToOverallChallengePercentage) {
			currentValue = umc.getRules().stream()
					.map(u -> Optional.ofNullable(u.getPercentage()).orElse(BigDecimal.ZERO))
					.reduce(BigDecimal.ZERO, BigDecimal::add);

		} else {
			currentValue = umc.getRules().stream()
					.max(withHighPercentage)
					.map(UserPromotionChallengeRule::getPercentage)
					.orElse(BigDecimal.ZERO);
		}


		log.debug("calculateChallengePercentage :: requiredValue : "+requiredValue+" currentValue : "+currentValue);

		if (requiredValue.equals(BigDecimal.ZERO)) {
			umc.setPercentage(BigDecimal.ZERO);
		} else {
			BigDecimal percentage = (currentValue.divide(requiredValue, 2, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(100L)).setScale(2, RoundingMode.HALF_UP);
			log.debug("Challenge :: requiredValue : "+requiredValue+"  currentValue : "+currentValue+"  percentage : "+percentage);
			umc.setPercentage(percentage);
			if (percentage.intValue() >= 100) {
				umc.setPercentage(BigDecimal.valueOf(100L));
				umc.setCompleted(ZonedDateTime.now(DEFAULT_TIMEZONE_ID).toLocalDateTime());


				umc.setChallengeComplete(true);
				triggerReward(umc.getUserPromotion().getUser().guid(), umc.getChallenge().getReward());
			}
		}
		userPromotionService.save(umc);
		return umc;
	}

	protected UserPromotionChallengeGroup calculateChallengeGroupPercentage(UserPromotionChallengeGroup umcg, boolean retrieveFreshDB) {
		if (retrieveFreshDB) umcg = userPromotionService.findChallengeGroup(umcg.getId());
		BigDecimal requiredValue = BigDecimal.ZERO;
		BigDecimal currentValue = BigDecimal.ZERO;

		Comparator<UserPromotionChallenge> withHighPercentage = (o1, o2) -> {
			BigDecimal second = Optional.ofNullable(o2.getPercentage()).orElse(BigDecimal.ZERO);
			BigDecimal first = Optional.ofNullable(o1.getPercentage()).orElse(BigDecimal.ZERO);
			return first.compareTo(second);
		};

		boolean groupDoesNotRequireAllChallenges = BooleanUtils.isNotTrue(umcg.getChallengeGroup().getRequiresAllChallenges());

		if (groupDoesNotRequireAllChallenges) {
			requiredValue = requiredValue.add(BigDecimal.valueOf(100L));

			currentValue = umcg.getUserPromotionChallenges().stream().max(withHighPercentage)
					.map(UserPromotionChallenge::getPercentage)
					.orElse(BigDecimal.ZERO);

		} else {
			requiredValue = requiredValue.add(BigDecimal.valueOf(umcg.getUserPromotionChallenges().size() * 100L));
			currentValue = umcg.getUserPromotionChallenges()
					.stream()
					.map(UserPromotionChallenge::getPercentage)
					.reduce(BigDecimal.ZERO, BigDecimal::add);
		}

		log.debug("calculateChallengePercentage :: requiredValue : "+requiredValue+" currentValue : "+currentValue);

		if (requiredValue.equals(BigDecimal.ZERO)) {
			umcg.setPercentage(BigDecimal.ZERO);
		} else {
			BigDecimal percentage = (currentValue.divide(requiredValue, 2, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(100L)).setScale(2, RoundingMode.HALF_UP);
			log.debug("ChallengeGroup :: requiredValue : "+requiredValue+"  currentValue : "+currentValue+"  percentage : "+percentage);
			umcg.setPercentage(percentage);
			if (percentage.intValue() >= 100) {
				umcg.setPercentage(BigDecimal.valueOf(100L));
				umcg.setCompleted(ZonedDateTime.now(DEFAULT_TIMEZONE_ID).toLocalDateTime());
			}
		}
		userPromotionService.save(umcg);
		return umcg;
	}

	private void triggerReward(String playerGuid, Reward reward) {
		if ((reward != null) && (reward.getRewardId() != null)) {
			log.debug("triggerReward :: "+playerGuid+" : "+reward);
			rewardService.triggerReward(playerGuid, reward.getRewardId());
		} else {
			log.debug("Trigger reward called for {}, but no reward configured. Ignoring.", playerGuid);
		}
	}

	private UserPromotionChallengeRule calculateRulePercentage(UserPromotion userPromotion, PromotionStat promotionStat, String playerGuid, UserPromotionChallengeRule umcr) {
		if (promotionStat == null) return umcr;
		PromotionStatSummary promotionStatSummary = findPromotionStatSummary(userPromotion, promotionStat, DateTime.now(), playerGuid);
		if (promotionStatSummary == null) return umcr;

		Long requiredValue = umcr.getRule().getValue();
		Long currentValue = promotionStatSummary.getValue();
		log.debug("calculateRulePercentage :: requiredValue : "+requiredValue+" currentValue : "+currentValue);

		if (requiredValue == 0L) {
			umcr.setPercentage(BigDecimal.ZERO);
			userPromotionService.updateUserPromotionChallengeRulePercentage(umcr, umcr.getPercentage());
		} else {
			BigDecimal percentage = (BigDecimal.valueOf(currentValue).divide(BigDecimal.valueOf(requiredValue), 2, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(100L)).setScale(2,
					RoundingMode.HALF_UP);
			log.debug("Rule :: requiredValue : "+requiredValue+"  currentValue : "+currentValue+"  percentage : "+percentage);
			umcr.setPercentage(percentage);
			if (percentage.intValue() >= 100) {
				umcr.setPercentage(BigDecimal.valueOf(100L));
				umcr.setCompleted(ZonedDateTime.now(DEFAULT_TIMEZONE_ID).toLocalDateTime());
				umcr.setRuleComplete(true);

				userPromotionService.completeUserPromotionChallengeRule(umcr);
			} else {
				userPromotionService.updateUserPromotionChallengeRulePercentage(umcr, umcr.getPercentage());
			}
		}
		return umcr;
	}

	private PromotionStatSummary findPromotionStatSummary(UserPromotion userPromotion, PromotionStat promotionStat, DateTime date, String playerGuid) {
		User owner = userService.find(playerGuid);
		if (owner == null) {
			log.error("No existing user found "+playerGuid);
			return null;
		}
		PromotionStatSummary promotionStatSummary = promotionStatSummaryRepository.findByPeriodAndPromotionStat(userPromotion.getPeriod(), promotionStat);
		log.debug("PromotionStatSummary :"+ promotionStatSummary);
		return promotionStatSummary;
	}

	private boolean shouldRegisterStat(PromoContext context, Rule rule, UserPromotionChallengeRule umcr) throws IOException { //Type type, Action action, String identifier) {
		PromoActivityBasic mab = context.getPromoActivityBasic();
		String statName = context.prepStatName(umcr.getId().toString());
		context.setStatName(statName);
		log.debug("ShouldRegisterStat [mab="+mab+", rule="+rule+", umcr="+umcr+"]");
		boolean shouldRegister = false;
		if (Boolean.TRUE.equals(umcr.getRuleComplete())) {
			log.debug("Rule complete, skipping.. ("+umcr+")");
		} else {
			boolean inCategory = (rule.getPromoProvider() != null && rule.getPromoProvider().getCategory().getName().equalsIgnoreCase(mab.getCategory().getCategory()));
			// Checking against the category and provider -- this is essential since all rule execution is not mandatory
			if (inCategory && rule.getPromoProvider().getUrl().equalsIgnoreCase(mab.getProvider())) {
				// promo provider specified, so this is used to check validity
				shouldRegister = checkRuleActivityAndFields(context, rule);
			}
//			else if ((rule.getPromoProvider() == null) && (rule.getCategory().equalsIgnoreCase(mab.getCategory().getCategory()))) {
//				// no promo provider specified, category from rule used. ie. any promo provider is applicable
//				shouldRegister = checkRuleActivityAndFields(context, rule);
//			}
		}
		log.debug("should register: "+shouldRegister);
		return shouldRegister;
	}

	private boolean checkRuleActivityAndFields(PromoContext context, Rule rule) throws IOException {
		boolean shouldRegister = false;
		PromoActivityBasic mab = context.getPromoActivityBasic();
		if (rule.getActivity().getName().equalsIgnoreCase(mab.getActivity().getActivity())) {
			List<ActivityExtraFieldRuleValue> ruleValues = activityExtraFieldRuleValueRepository.findByRule(rule);
			for (ActivityExtraFieldRuleValue fieldRuleValue: ruleValues) {
				String fieldRuleName = fieldRuleValue.getActivityExtraField().getName();
				context.appendToStatName("."+fieldRuleName.toLowerCase());
				if (mab.getLabelValues().keySet().contains(fieldRuleName)) {
					String incomingValue = mab.getLabelValues().get(fieldRuleName);
					shouldRegister = Arrays.stream(fieldRuleValue.getValue().split(","))
							.anyMatch(incomingValue::equalsIgnoreCase);

					if (!shouldRegister) {
						//exit if the activity rule value does not match in case might be matching values in next iteration
						break;
					}
				}
			}
			if (ruleValues.isEmpty()) {
				shouldRegister = true;
			}
		}
		return shouldRegister;
	}

	private boolean promotionsDefined(String domainName, LocalDateTime date) {
		return promotionService.promotionsDefined(domainName, date);
	}

	private void setup(PromoContext promoContext) throws Exception {
		String domainName = promoContext.getPromoActivityBasic().getOwnerGuid().split("/")[0];
		Domain domain = domainService.findOrCreate(domainName);
		promoContext.setDomain(domain);

		User player = userService.findOrCreate(promoContext.getPromoActivityBasic().getOwnerGuid(), promoContext.getPromoActivityBasic().getTimezone());
		promoContext.setUser(player);

		ZoneId zoneId = DEFAULT_TIMEZONE_ID;
		ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
		promoContext.setUserZoneId(zoneId);
		promoContext.setUserZonedDateTime(zonedDateTime);

		populateRelevantPromotionsInContext(promoContext);
	}
	public <T> boolean isListEmpty(List<T> list) {
		return list == null || list.isEmpty();
	}

	public List<Long> getUserCategoryIdsForType(PromotionRevision promotionRevision, UserCategoryType type) {

		if (isListEmpty(promotionRevision.getUserCategories())) {
			return new ArrayList<>();
		}

		return promotionRevision.getUserCategories().stream().filter(uc -> uc.getType() == type)
				.map(UserCategory::getUserCategoryId).toList();
	}

	@TimeThisMethod
	@Retryable(maxAttempts=5,backoff=@Backoff(random = true, delay=50, maxDelay = 1000), exclude={ NotRetryableErrorCodeException.class }, include=Exception.class)
	public void register(PromoContext promoContext) throws Exception {
		String domainName = promoContext.getPromoActivityBasic().getOwnerGuid().split("/")[0];
		if (!promotionsDefined(domainName, LocalDateTime.now())) {
			log.debug("No promotions defined for "+domainName+" skipping.");
			return;
		}
		setup(promoContext);
		if (!promoContext.getPromotions().isEmpty()) {
			log.debug("PromoContext created: " + promoContext);

			userPromotionService.findUserPromotions(promoContext);
			self.registerStatsAndCalculatePercentages(promoContext);
		}
	}

	private PromotionStat registerPromoStat(UserPromotion userPromotion, PromoContext promoContext, UserPromotionChallengeRule umcr) throws Exception {
		log.debug("Register promo stat: "+promoContext.getStatName());

		PromotionStat ms = promotionStatRepository.findByNameAndOwner(promoContext.getStatName(), promoContext.getUser());
		if (ms == null) {
			ms = promotionStatRepository.save(
					PromotionStat.builder()
							.name(promoContext.getStatName())
							.owner(promoContext.getUser())
							.category(promoContext.getPromoActivityBasic().getCategory().getCategory())
							.activity(promoContext.getPromoActivityBasic().getActivity().getActivity())
							.build()
			);
		}

		promotionStatEntryRepository.save(
				PromotionStatEntry.builder()
						.promotionStat(ms)
						.value((promoContext.getPromoActivityBasic().getValue() == null) ? 1L: promoContext.getPromoActivityBasic().getValue())
						.build()
		);

		Period period = userPromotion.getPeriod();

		//TODO: operations review/rework
		PromotionStatSummary promotionStatSummary = promotionStatSummaryRepository.findByPeriodAndPromotionStat(period, ms);
		if (promotionStatSummary == null) {

			Long value = promoContext.getPromoActivityBasic().getValue();

			if (umcr.getRule().getOperation() == Operation.COUNTER || value == null) {
				value = 1L;
			}

			promotionStatSummaryRepository.save(
					PromotionStatSummary.builder()
							.promotionStat(ms)
							.owner(promoContext.getUser())
							.value(value)
							.period(period)
							.build()
			);
		} else {
			long value = 0L;
			switch (umcr.getRule().getOperation()) {
				case ACCUMULATOR -> value = promotionStatSummary.getValue() + promoContext.getPromoActivityBasic().getValue();
				case LAST_VALUE -> value = promoContext.getPromoActivityBasic().getValue();
				case COUNTER -> value = promotionStatSummary.getValue() + 1;
			}
			promotionStatSummary.setValue(value);
			promotionStatSummaryRepository.save(promotionStatSummary);
		}

		return ms;
	}

	private void checkPromotionRequirements(PromoContext promoContext, lithium.service.promo.data.entities.Promotion promotion, Set<lithium.service.promo.data.entities.Promotion> relevantPromotions) {
		log.debug("checkPromotionRequirements: {}", promotion);
		for (ChallengeGroup challengeGroup: promotion.getCurrent().getChallengeGroups()) {
			for (Challenge challenge: challengeGroup.getChallenges()) {
				for (Rule rule: challenge.getRules()) {
					PromoActivityBasic pab = promoContext.getPromoActivityBasic();
					try {
						if (rule.getActivity().getName().equalsIgnoreCase(pab.getActivity().getActivity())) {
							log.debug("Promotion matches received activity, lets proceed with checks.");
							if (hasRequiredFieldsForPromotionChallengeRule(rule, pab)) {
								log.debug("Promotion matches received values for rule, a player can now participate in this promotion");
								relevantPromotions.add(promotion);
							}
						}
					} catch (IOException e) {
						log.error("Could not determine if promotion (current rev:{}) should be added to player. skipping. {}, {}", promotion.getCurrent().getId(), promoContext, e);
					}
				}
			}
		}
	}

	public boolean hasRequiredFieldsForPromotionChallengeRule(Rule rule, PromoActivityBasic promoActivityBasic) {
		boolean meetsFieldRequirements = false;

		List<ActivityExtraFieldRuleValue> ruleValues = activityExtraFieldRuleValueRepository.findByRule(rule);
		if (ruleValues.isEmpty()) {
			log.debug("The promotion can be participated on since it does not have the requirements for extra rule values");
			meetsFieldRequirements = true;
		} else {
			log.debug("Checking for matching promotion extra field rule values, promotionActivity: {}, activityExtraFields {}",
					promoActivityBasic, ruleValues);

			for (ActivityExtraFieldRuleValue fieldRuleValue: ruleValues) {
				String fieldRuleName = fieldRuleValue.getActivityExtraField().getName();
				if (promoActivityBasic.getLabelValues().containsKey(fieldRuleName)) {

					String incomingValue = promoActivityBasic.getLabelValues().get(fieldRuleName);

					if (StringUtil.isEmpty(fieldRuleValue.getValue())) {
						meetsFieldRequirements = true;
						log.debug("Rule value for field {} is empty, skipping", fieldRuleValue.getActivityExtraField().getName());
						continue;
					}

					String[] values = fieldRuleValue.getValue().split(",");
					meetsFieldRequirements = Arrays.stream(values)
							.anyMatch(incomingValue::equalsIgnoreCase);

					log.debug("Requirements met: {}, ruleValue: {}, triggerValue: {}", meetsFieldRequirements,  fieldRuleValue.getValue(), incomingValue);

					if (!meetsFieldRequirements) {
						break;
					}
				} else {
					log.debug("Field {} was not found on the incoming fields, promotion requirements not met", fieldRuleName);
					break;
				}
			}
		}

		return meetsFieldRequirements;
	}
	private void populateRelevantPromotionsInContext(PromoContext promoContext) {
		try {
			Set<lithium.service.promo.data.entities.Promotion> relevantPromotions = new HashSet<>();

			lithium.service.user.client.objects.User externalUser = userApiInternalClientService.getUserByGuid(
					promoContext.getPromoActivityBasic().getOwnerGuid());

			//Going check against promotions that have events today for the player
			List<lithium.service.promo.data.entities.Promotion> promotions = promotionService.findAllCurrentEnabledWithEventsByDomain(promoContext);

			/**
			 * If user categories are null here, it means something went wrong in the call to svc-user.
			 * If categories cannot be determined, promo should not be entered into, in-case player is on blacklist/not on whitelist.
			 * New Registrations will not be null, but will come through as empty list.
			 */
			if (externalUser.getUserCategories() != null) {

				for (lithium.service.promo.data.entities.Promotion promotion: promotions) {
					if (promotion.getCurrent() == null) continue;
					PromotionRevision current = promotion.getCurrent();

					List<Long> blacklisted = getUserCategoryIdsForType(current, UserCategoryType.TYPE_BLACKLIST);
					List<Long> whitelisted = getUserCategoryIdsForType(current, UserCategoryType.TYPE_WHITELIST);

					boolean playerInBlacklist = (externalUser.getUserCategories().stream().anyMatch(u -> blacklisted.contains(u.getId())));
					boolean playerInWhitelist = (externalUser.getUserCategories().stream().anyMatch(u -> whitelisted.contains(u.getId())));

					if (Boolean.TRUE.equals(current.getExclusive())) {
						log.debug("exclusive promo, checking blacklist first for {}", promoContext.playerGuid());
						if (!blacklisted.isEmpty()) {
							log.warn("blacklist defined, checking {}", promoContext.playerGuid());
							if (playerInBlacklist) {
								log.error("{} found in blacklist, stopping.", promoContext.playerGuid());
								continue;
							}
						} else {
							log.debug("blacklist not defined. Continue checks.");
						}
						Set<User> exclusivePlayers = current.getExclusivePlayers(); // Might need to revisit this in iteration2
						if (exclusivePlayers.stream().anyMatch(u -> u.guid().equalsIgnoreCase(promoContext.playerGuid()))) {
							log.debug("Guid: {} found in exclusive list, adding promo.", promoContext.playerGuid());
							checkPromotionRequirements(promoContext, promotion, relevantPromotions);
						} else {
							log.warn("Guid: {} NOT found in exclusive list, checking whitelist.", promoContext.playerGuid());
							if (!whitelisted.isEmpty()) {
								log.warn("whitelist defined, checking {}", promoContext.playerGuid());
								if (playerInWhitelist) {
									log.debug("{} found in whitelist, adding promo.", promoContext.playerGuid());
									checkPromotionRequirements(promoContext, promotion, relevantPromotions);
								} else {
									log.error("{} NOT found in defined whitelist, stopping.", promoContext.playerGuid());
									continue;
								}
							} else {
								log.debug("No whitelist override defined. promo not added for {}", promoContext.playerGuid());
							}
						}
					} else {
						log.debug("non exclusive promo, checking {}", promoContext.playerGuid());
						if (!blacklisted.isEmpty()) {
							log.debug("blacklist defined, checking {}", promoContext.playerGuid());
							if (playerInBlacklist) {
								log.error("{} found in blacklist, stopping.", promoContext.playerGuid());
								continue;
							}
							log.debug("{} not found in blacklist. checking whitelists.", promoContext.playerGuid());
						} else {
							log.debug("blacklist not defined.");
						}

						if (!whitelisted.isEmpty()) {
							log.debug("whitelist defined, checking {}", promoContext.playerGuid());
							if (playerInWhitelist) {
								log.debug("{} found in whitelist, adding promo.", promoContext.playerGuid());
								checkPromotionRequirements(promoContext, promotion, relevantPromotions);
							} else {
								log.error("{} NOT found in defined whitelist, stopping.", promoContext.playerGuid());
								continue;
							}
						} else {
							log.debug("No whitelist override defined. adding promo for {}", promoContext.playerGuid());
							checkPromotionRequirements(promoContext, promotion, relevantPromotions);
						}
					}

					promoContext.setPromotions(relevantPromotions);
					log.debug("Found {} promotions for {}", relevantPromotions.size(), promoContext.playerGuid());
				}
			}
		} catch (Exception | UserClientServiceFactoryException e) {
			log.error(String.format("Failed to populate user (%s) promotions.", promoContext.getPromoActivityBasic().getOwnerGuid()), e);
		}
	}
}
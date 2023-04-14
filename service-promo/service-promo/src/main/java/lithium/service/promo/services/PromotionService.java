package lithium.service.promo.services;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.service.Response;
import lithium.service.accounting.client.AccountingClient;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.promo.client.enums.Operation;
import lithium.service.promo.client.exception.Status411InvalidPromotionException;
import lithium.service.promo.client.objects.ActivityBO;
import lithium.service.promo.client.objects.ActivityExtraFieldBO;
import lithium.service.promo.client.objects.ActivityExtraFieldRuleValueBO;
import lithium.service.promo.client.objects.ChallengeBO;
import lithium.service.promo.client.objects.ChallengeGroupBO;
import lithium.service.promo.client.objects.DomainBO;
import lithium.service.promo.client.objects.PromoProviderBO;
import lithium.service.promo.client.objects.Promotion;
import lithium.service.promo.client.objects.PromotionBO;
import lithium.service.promo.client.objects.PromotionRevisionBO;
import lithium.service.promo.client.objects.RewardBO;
import lithium.service.promo.client.objects.RuleBO;
import lithium.service.promo.client.objects.UserCategoryBO;
import lithium.service.promo.client.objects.UserCategoryType;
import lithium.service.promo.context.PromoContext;
import lithium.service.promo.data.entities.Activity;
import lithium.service.promo.data.entities.ActivityExtraField;
import lithium.service.promo.data.entities.ActivityExtraFieldRuleValue;
import lithium.service.promo.data.entities.Challenge;
import lithium.service.promo.data.entities.ChallengeGroup;
import lithium.service.promo.data.entities.Domain;
import lithium.service.promo.data.entities.Graphic;
import lithium.service.promo.data.entities.PromoProvider;
import lithium.service.promo.data.entities.PromotionRevision;
import lithium.service.promo.data.entities.Reward;
import lithium.service.promo.data.entities.Rule;
import lithium.service.promo.data.entities.User;
import lithium.service.promo.data.entities.UserCategory;
import lithium.service.promo.data.repositories.ActivityExtraFieldRepository;
import lithium.service.promo.data.repositories.ActivityExtraFieldRuleValueRepository;
import lithium.service.promo.data.repositories.ActivityRepository;
import lithium.service.promo.data.repositories.CategoryRepository;
import lithium.service.promo.data.repositories.ChallengeGroupRepository;
import lithium.service.promo.data.repositories.ChallengeRepository;
import lithium.service.promo.data.repositories.GraphicRepository;
import lithium.service.promo.data.repositories.PromoProviderRepository;
import lithium.service.promo.data.repositories.PromotionRepository;
import lithium.service.promo.data.repositories.PromotionRevisionRepository;
import lithium.service.promo.data.repositories.RewardRepository;
import lithium.service.promo.data.repositories.RuleRepository;
import lithium.service.promo.data.repositories.UserCategoryRepository;
import lithium.service.promo.data.repositories.UserPromotionRepository;
import lithium.service.promo.data.specifications.PromotionRevisionSpecifications;
import lithium.service.promo.data.specifications.PromotionSpecifications;
import lithium.service.promo.exceptions.PromotionNotFoundException;
import lithium.service.promo.exceptions.Status410InvalidPromotionCreateException;
import lithium.service.promo.exceptions.Status411InvalidUserCreateException;
import lithium.service.promo.exceptions.Status412InvalidPromotionEditException;
import lithium.service.promo.objects.PromoQuery;
import lithium.service.xp.client.XPClient;
import lithium.service.xp.client.objects.Level;
import lithium.service.xp.client.objects.Scheme;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PromotionService {
	@Autowired ChallengeRepository challengeRepository;
	@Autowired DomainService domainService;
	@Autowired GraphicRepository graphicRepository;
	@Autowired
	PromotionRepository promotionRepository;
	@Autowired
	PromotionRevisionRepository promotionRevisionRepository;
	@Autowired RewardRepository rewardRepository;
	@Autowired RuleRepository ruleRepository;
	@Autowired
	UserPromotionRepository userMissionRepository;
	@Autowired UserService userService;
	@Autowired PeriodService periodService;
	@Autowired LithiumServiceClientFactory services;
	@Autowired UserCategoryRepository userCategoryRepository;
	@Autowired PromoProviderRepository promoProviderRepository;
	@Autowired
	ActivityExtraFieldRepository activityExtraFieldRepository;
	@Autowired
	ActivityExtraFieldRuleValueRepository activityExtraFieldRuleValueRepository;

	@Autowired ActivityRepository activityRepository;
	@Autowired ChallengeGroupRepository challengeGroupRepository;
	@Autowired ChallengeGroupService challengeGroupService;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private RecurrenceRuleService recurrenceRuleService;

	@Autowired
	private ChangeLogService changeLogService;

	@Autowired
	private PromotionService self;

	public List<lithium.service.promo.data.entities.Promotion> findAllCurrentByDomain(PromoContext promoContext) {
		Date date = Date.from(promoContext.getUserZonedDateTime().toLocalDate().atStartOfDay(promoContext.getUserZoneId()).toInstant());
		return promotionRepository.findByCurrentDomainNameAndCurrentEndDateGreaterThanOrCurrentEndDateIsNullAndCurrentStartDateLessThanEqualOrCurrentStartDateIsNull(
				promoContext.domainName(), date, date);
	}

	@Cacheable(value="lithium.service.promo.services.promotion-service.all-current-enabled-with-events-by-domain", key = "#promoContext.domainName()", unless="#result == null or #result.size() == 0")
	public List<lithium.service.promo.data.entities.Promotion> findAllCurrentEnabledByDomain(PromoContext promoContext) {
		Specification<lithium.service.promo.data.entities.Promotion> spec = PromotionSpecifications.domains(List.of(promoContext.domainName()))
				.and(PromotionSpecifications.enabled(true))
				.and(PromotionSpecifications.deleted(false));

		return promotionRepository.findAll(spec);
	}

	public List<lithium.service.promo.data.entities.Promotion> findAllCurrentEnabledWithEventsByDomain(PromoContext promoContext) {
		LocalDateTime date = promoContext.getUserZonedDateTime().toLocalDateTime();
		List<lithium.service.promo.data.entities.Promotion> promotions =  self.findAllCurrentEnabledByDomain(promoContext);

		return promotions.stream()
				.filter(promotion -> {
					boolean hasEvents = recurrenceRuleService.promotionHasEventsForDate(promotion, date);

					if (!hasEvents) {
						log.debug("Promotion {} on domain {} has no events for date {}, Skipping", promotion.getCurrent().getName(),promotion.getCurrent().getDomain().getName(), date);
					}

					return hasEvents;
				}).toList();
	}

	public Page<lithium.service.promo.data.entities.Promotion> findByDomains(List<String> domains, String searchValue, Pageable pageable) {
		Specification<lithium.service.promo.data.entities.Promotion> spec = Specification.where(lithium.service.promo.data.specifications.PromotionSpecifications.domains(domains));
		if ((searchValue != null) && (searchValue.length() > 0)) {
			Specification<lithium.service.promo.data.entities.Promotion> s = Specification.where(lithium.service.promo.data.specifications.PromotionSpecifications.any(searchValue));
			spec = (spec == null)? s: spec.and(s);
		}
		return promotionRepository.findAll(spec, pageable);
	}

	public List<lithium.service.promo.data.entities.Promotion> getCurrentPromotionsForDomain(String domainName) {
		return promotionRepository.findByCurrentDomainName(domainName);
	}

	public boolean promotionsDefined(String domainName, LocalDateTime date) {
		return (promotionRepository.count(PromotionSpecifications.activePromotionsForDomainAndDate(domainName, date)) >0);
	}

	public Page<PromotionRevision> findRevisionsByPromotion(lithium.service.promo.data.entities.Promotion promotion, String searchValue, Pageable pageable) {
		Specification<PromotionRevision> spec = Specification.where(PromotionRevisionSpecifications.revisionsByPromotion(promotion));
		Page<PromotionRevision> result = promotionRevisionRepository.findAll(spec, pageable);
		return result;
	}

	@Transactional( rollbackOn = Exception.class )
	@Retryable( backoff = @Backoff( delay = 500 ), maxAttempts = 10 )
	public PromotionBO createV1(PromotionBO promotionPost, LithiumTokenUtil tokenUtil)
	throws Status410InvalidPromotionCreateException, Status411InvalidUserCreateException, ParseException
	{
		PromotionRevisionBO editRevisionPost = promotionPost.getEdit();
		if (editRevisionPost == null) throw new Status410InvalidPromotionCreateException();

		Domain domain = domainService.findOrCreate(editRevisionPost.getDomain().getName());
		User editor = userService.findOrCreate(tokenUtil.guid());
		log.info("{} creating promotion for {}, from received object: {}", editor.guid(), domain.getName(), promotionPost);

		LocalDateTime startDate = parseFromDomainTimezoneToUTC(editRevisionPost.getStartDate(), domain);
		LocalDateTime endDate = parseFromDomainTimezoneToUTC(editRevisionPost.getEndDate(), domain);

		lithium.service.promo.data.entities.Promotion promotion = promotionRepository.save(
				lithium.service.promo.data.entities.Promotion.builder()
						.editor(editor)
						.enabled(true)
						.build()
		);
		lithium.service.promo.data.entities.Promotion dependsOnPromotion = null;
		if (editRevisionPost.getDependsOnPromotion() != null) {
			dependsOnPromotion = promotionRepository.findOne(editRevisionPost.getDependsOnPromotion());
		}

		PromotionRevision editRevision = PromotionRevision.builder()
				.promotion(promotion)
				.domain(domain)
				.name(editRevisionPost.getName())
				.description(editRevisionPost.getDescription())
				.dependsOnPromotion(dependsOnPromotion)
				.recurrencePattern(editRevisionPost.getRecurrencePattern())
				.redeemableInTotal(editRevisionPost.getRedeemableInTotal())
				.redeemableInEvent(editRevisionPost.getRedeemableInEvent())
				.eventDuration(editRevisionPost.getEventDuration())
				.eventDurationGranularity(editRevisionPost.getEventDurationGranularity())
				.xpLevel(Optional.ofNullable(editRevisionPost.getXpLevel()).orElse(0))
				.startDate(startDate)
				.endDate(endDate)
				.exclusive(editRevisionPost.isExclusive())
				.requiresAllChallengeGroups(BooleanUtils.toBoolean(editRevisionPost.getRequiresAllChallengeGroups()))
				.build();
		editRevision = promotionRevisionRepository.save(editRevision);
		promotion.setEdit(editRevision);
		promotion = promotionRepository.save(promotion);

		if (editRevisionPost.getReward() != null) {
			Reward reward = rewardRepository.save(
					Reward.builder().promotionRevision(editRevision).rewardId(editRevisionPost.getReward().getRewardId()).build()
			);
			editRevision.setReward(reward);
			editRevision = promotionRevisionRepository.save(editRevision);
		}

		createChallengeGroups(editRevisionPost.getChallengeGroups(), editRevision);
		createUserCategories(editRevisionPost.getUserCategories(), editRevision);
		if (editRevisionPost.isExclusive()) editRevision = createExclusivePlayers(editRevisionPost.getExclusivePlayers(), editRevision);

		editRevision = promotionRevisionRepository.save(editRevision);
		promotion.setEdit(editRevision);
		promotion = promotionRepository.save(promotion);
		log.info("Created promotion: {}", promotion);

		try {
			List<ChangeLogFieldChange> changes = changeLogService.copy(promotion, new lithium.service.promo.data.entities.Promotion(),
					new String[]{"id", "current", "edit", "editor", "version"});

			changeLogService.registerChangesWithDomainAndFullName("promotion", "create", promotion.getId(), tokenUtil.guid(), null, null, changes, Category.PROMOTIONS, SubCategory.PROMOTION_CREATE, 1, domain.getName(), tokenUtil.userLegalName());
		} catch (Exception e) {
			log.error("Failed to register changelogs after creating promotion with id:{}, author:{}, reason: {}", promotion.getId(), editor.guid(), e);
		}
		return convertPromotionBO(promotion);
	}

	private PromotionBO convertPromotionBO(lithium.service.promo.data.entities.Promotion promotion) {
		return PromotionBO.builder()
				.id(promotion.getId())
				.editor(
						lithium.service.promo.client.objects.User.builder()
						.guid(promotion.getEditor().guid())
						.build()
				)
				.current(convertPromotionRevisionBO(promotion.getCurrent()))
				.edit(convertPromotionRevisionBO(promotion.getEdit()))
				.build();
	}

	private String convertDateToString(LocalDateTime date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		return (date != null) ? ZonedDateTime.of(date, ZoneOffset.UTC).format(formatter): null;
	}
	private PromotionRevisionBO convertPromotionRevisionBO(PromotionRevision promotionRevision) {
		if (promotionRevision == null) return null;
		return PromotionRevisionBO.builder()
				.id(promotionRevision.getId())
				.domain(convertDomainBO(promotionRevision.getDomain()))
				.name(promotionRevision.getName())
				.description(promotionRevision.getDescription())
				.startDate(convertDateToString(promotionRevision.getStartDate()))
				.endDate(convertDateToString(promotionRevision.getEndDate()))
				.reward(RewardBO.builder().id(promotionRevision.getReward().getId()).rewardId(promotionRevision.getReward().getRewardId()).build())
				.xpLevel(promotionRevision.getXpLevel())
				.exclusive(promotionRevision.getExclusive())
				.dependsOnPromotion((promotionRevision.getDependsOnPromotion()!=null)?promotionRevision.getDependsOnPromotion().getId():null)
				.recurrencePattern(promotionRevision.getRecurrencePattern())
				.redeemableInEvent(promotionRevision.getRedeemableInEvent())
				.redeemableInTotal(promotionRevision.getRedeemableInTotal())
				.eventDuration(promotionRevision.getEventDuration())
				.eventDurationGranularity(promotionRevision.getEventDurationGranularity())
				.exclusivePlayers(convertPlayer(promotionRevision.getExclusivePlayers()))
				.userCategories(convertUserCategoriesBO(promotionRevision.getUserCategories()))
				.challengeGroups(convertChallengeGroupBO(promotionRevision.getChallengeGroups()))
				.requiresAllChallengeGroups(promotionRevision.getRequiresAllChallengeGroups())
				.build();
	}

	private DomainBO convertDomainBO(Domain domain) {
		return DomainBO.builder()
				.name(domain.getName())
				.timezone(domain.getTimezone())
				.build();
	}

	private Set<lithium.service.promo.client.objects.User> convertPlayer(Set<User> exclusivePlayers) {
		return exclusivePlayers.stream().map(u -> lithium.service.promo.client.objects.User.builder().guid(u.guid()).build()).collect(Collectors.toSet());
	}
	private List<UserCategoryBO> convertUserCategoriesBO(List<UserCategory> userCategories) {
		if (CollectionUtils.isEmpty(userCategories)) return Collections.emptyList();
		return userCategories.stream().map(
				uc -> UserCategoryBO.builder()
						.id(uc.getId())
						.userCategoryId(uc.getUserCategoryId())
						.type(uc.getType().getType())
						.build()
		).toList();
	}
	private List<ChallengeGroupBO> convertChallengeGroupBO(List<ChallengeGroup> challengeGroups) {
		if (CollectionUtils.isEmpty(challengeGroups)) return Collections.emptyList();
		return challengeGroups.stream().map(
				cg -> ChallengeGroupBO.builder()
						.id(cg.getId())
						.sequenced(cg.getSequenced())
						.challenges(convertChallengesBO(cg.getChallenges()))
						.requiresAllChallenges(cg.getRequiresAllChallenges())
						.build()
			).toList();
	}

	private List<ChallengeBO> convertChallengesBO(List<Challenge> challenges) {
		if (CollectionUtils.isEmpty(challenges)) return Collections.emptyList();
		return challenges.stream().map(
				c -> ChallengeBO.builder()
						.id(c.getId())
						.description(c.getDescription())
						.reward((c.getReward()!=null)?RewardBO.builder().id(c.getReward().getId()).rewardId(c.getReward().getRewardId()).build():null)
						.sequenceNumber(c.getSequenceNumber())
						.rules(convertRulesBO(c.getRules()))
						.requiresAllRules(c.getRequiresAllRules())
						.build()
		).toList();
	}

	private List<RuleBO> convertRulesBO(List<Rule> rules) {
		if (CollectionUtils.isEmpty(rules)) {
			return Collections.emptyList();
		};

		return rules.stream().map(
				r -> RuleBO.builder()
						.id(r.getId())
						.promoProvider(convertPromoProviderBO(r.getPromoProvider()))
						.category(Optional.ofNullable(r.getCategory()).map(lithium.service.promo.data.entities.Category::getName).orElse(null))
						.activity(convertActivityBO(r.getActivity()))
						.operation(r.getOperation().type())
						.value(r.getValue())
						.activityExtraFieldRuleValues(convertActivityExtraFieldRuleValuesBO(r))
						.build()
		).toList();
	}

	private List<ActivityExtraFieldRuleValueBO> convertActivityExtraFieldRuleValuesBO(Rule rule) {
		List<ActivityExtraFieldRuleValue> activityExtraFieldRuleValues = activityExtraFieldRuleValueRepository.findByRule(rule);
		return activityExtraFieldRuleValues.stream().map(
				av -> ActivityExtraFieldRuleValueBO.builder()
						.activityExtraField(
							ActivityExtraFieldBO.builder()
									.id(av.getActivityExtraField().getId())
									.dataType(av.getActivityExtraField().getDataType())
									.name(av.getActivityExtraField().getName())
									.description(av.getActivityExtraField().getDescription())
									.fieldType(av.getActivityExtraField().getFieldType())
									.fetchExternalData(av.getActivityExtraField().isFetchExternalData())
									.build()
						)
						.value(Arrays.stream(av.getValue().split(",")).toList())
						.build()
		).toList();
	}

	private ActivityBO convertActivityBO(Activity activity) {
		return ActivityBO.builder().name(activity.getName()).build();
	}

	private PromoProviderBO convertPromoProviderBO(PromoProvider promoProvider) {
		return PromoProviderBO.builder()
				.id(promoProvider.getId())
				.url(promoProvider.getUrl())
				.name(promoProvider.getName())
				.category(Optional.ofNullable(promoProvider.getCategory()).map(lithium.service.promo.data.entities.Category::getName).orElse(null))
				.build();
	}

	private void createChallengeGroups(List<ChallengeGroupBO> challengeGroupBOList, PromotionRevision promotionRevision) {
		List<ChallengeGroup> challengeGroups = new ArrayList<>();
		for (ChallengeGroupBO group: challengeGroupBOList) {
			ChallengeGroup challengeGroup = challengeGroupRepository.save(
					ChallengeGroup.builder()
							.sequenced(group.isSequenced())
							.promotionRevision(promotionRevision)
							.requiresAllChallenges(Optional.ofNullable(group.getRequiresAllChallenges()).orElse(true))
							.build()
			);
			List<Challenge> challenges = addChallengesToGroup(challengeGroup, group.getChallenges());
			challengeGroup.setChallenges(challenges);
			challengeGroups.add(challengeGroup);
		}
		promotionRevision.setChallengeGroups(challengeGroups);
	}

	private void createUserCategories(List<UserCategoryBO> userCategories, PromotionRevision promotionRevision) {
		if (userCategories != null) {
			for (UserCategoryBO category: userCategories) {
				userCategoryRepository.save(UserCategory.builder()
						.type(UserCategoryType.fromType(category.getType()))
						.userCategoryId(category.getUserCategoryId())
						.promotionRevision(promotionRevision)
						.build());
			}
		}
	}

	private PromotionRevision createExclusivePlayers(Set<lithium.service.promo.client.objects.User> exclusivePlayers, PromotionRevision promotionRevision)
	{
		if (exclusivePlayers != null) {
			for (lithium.service.promo.client.objects.User user: exclusivePlayers) {
				try {
					User exclusivePlayer = userService.findOrCreate(user.getGuid());
					promotionRevision.addExclusivePlayer(exclusivePlayer);
				} catch (Exception e) {
					log.error("Failed to add player {} to the exclusive list. {}", user.getGuid(), e);
				}
			}
		}
		return promotionRevisionRepository.save(promotionRevision);
	}
	
	@Transactional(rollbackOn=Exception.class)
	@Retryable(backoff=@Backoff(delay=500), maxAttempts=10)
	void copy(PromotionRevision from, PromotionRevision to) {
		to.setPromotion(from.getPromotion());
		to.setDomain(from.getDomain());
		to.setName(from.getName());
		to.setDescription(from.getDescription());
		to.setStartDate(from.getStartDate());
		to.setEndDate(from.getEndDate());
		to.setXpLevel(from.getXpLevel());
		to.setDependsOnPromotion(from.getDependsOnPromotion());
		to.setRecurrencePattern(from.getRecurrencePattern());
		to.setRedeemableInEvent(from.getRedeemableInEvent());
		to.setRedeemableInTotal(from.getRedeemableInTotal());
		to.setEventDuration(from.getEventDuration());
		to.setEventDurationGranularity(from.getEventDurationGranularity());
		to.setExclusive(from.getExclusive());

		to = promotionRevisionRepository.save(to);

		List<ChallengeGroup> challengeGroups = new ArrayList<>();

		for(ChallengeGroup challengeGroup: from.getChallengeGroups()) {
			List<Challenge> challenges = new ArrayList<>();

			ChallengeGroup newGroup = challengeGroupRepository.save(ChallengeGroup.builder()
							.promotionRevision(to)
					.build());
			for (Challenge challenge: challengeGroup.getChallenges()) {
				Graphic iconCopy = null;
				if (challenge.getIcon() != null) {
					iconCopy = Graphic.builder()
							.image(challenge.getIcon().getImage())
							.name(challenge.getIcon().getName())
							.size(challenge.getIcon().getSize())
							.type(challenge.getIcon().getType())
							.build();
					iconCopy = graphicRepository.save(iconCopy);
				}
				Reward rewardCopy = null;
				if (challenge.getReward() != null) {
					rewardCopy = Reward.builder()
							.rewardId(challenge.getReward().getRewardId())
							.build();
					rewardCopy = rewardRepository.save(rewardCopy);
				}
				Challenge challengeCopy = Challenge.builder()
						.description(challenge.getDescription())
						.icon(iconCopy)
						.reward(rewardCopy)
						.challengeGroup(newGroup)
						.build();
				challengeCopy = challengeRepository.save(challengeCopy);
				if (rewardCopy != null) {
					rewardCopy.setChallenge(challengeCopy);
					rewardRepository.save(rewardCopy);
				}
				List<Rule> rulesCopy = new ArrayList<>();
				if (!challenge.getRules().isEmpty()) {
					for (Rule rule: challenge.getRules()) {
						Rule ruleCopy = Rule.builder()
								.challenge(challengeCopy)
								.promoProvider(rule.getPromoProvider())
								.category(rule.getCategory())
								.operation(rule.getOperation())
								.activity(rule.getActivity())
								.value(rule.getValue())
								.build();
						ruleCopy = ruleRepository.save(ruleCopy);
						rulesCopy.add(ruleCopy);

						List<ActivityExtraFieldRuleValue> ruleValueList = activityExtraFieldRuleValueRepository.findByRule(rule);

						if (!ruleValueList.isEmpty()) {

							for (ActivityExtraFieldRuleValue ruleValue: ruleValueList) {
								ActivityExtraFieldRuleValue ruleValueCopy = ActivityExtraFieldRuleValue
										.builder()
										.value(ruleValue.getValue())
										.rule(ruleCopy)
										.activityExtraField(ruleValue.getActivityExtraField())
										.build();

								activityExtraFieldRuleValueRepository.save(ruleValueCopy);
							}
						}

					}
					challengeCopy.setRules(rulesCopy);

				}
				challenges.add(challengeCopy);
			}

			newGroup.setChallenges(challenges);
			challengeGroupRepository.save(newGroup);
		}

		if (from.getUserCategories() != null) {
			List<UserCategory> userCategories = new ArrayList<>();

			for(UserCategory userCategory: from.getUserCategories())
			{
				UserCategory categoryCopy = UserCategory.builder()
						.promotionRevision(to)
						.userCategoryId(userCategory.getUserCategoryId())
						.type(userCategory.getType())
						.build();

				userCategoryRepository.save(categoryCopy);
				userCategories.add(userCategory);
			}

			to.setUserCategories(userCategories);
		}

		to.setChallengeGroups(challengeGroups);
		if (from.getReward() != null) {
			Reward rewardCopy = Reward.builder()
			.promotionRevision(to)
			.rewardId(from.getReward().getRewardId())
			.build();
			rewardCopy = rewardRepository.save(rewardCopy);
			to.setReward(rewardCopy);
		}

		if (from.getExclusivePlayers() != null) {
			for (User player: from.getExclusivePlayers()) {
				to.addExclusivePlayer(player);
			}
		}
		promotionRevisionRepository.save(to);
	}

	@Transactional(rollbackOn=Exception.class)
	@Retryable(backoff=@Backoff(delay=500), maxAttempts=10)
	public lithium.service.promo.data.entities.Promotion modify(lithium.service.promo.data.entities.Promotion promotion, String editorGuid) throws Exception {
		if (promotion.getEdit() == null) {
			PromotionRevision edit = PromotionRevision.builder().build();
			copy(promotion.getCurrent(), edit);
			promotion.setEdit(edit);
			promotion.setEditor(userService.findOrCreate(editorGuid));
			promotion = promotionRepository.save(promotion);
		}
		return promotionRepository.findOne(promotion.getId());
	}

	@Transactional(rollbackOn=Exception.class)
	@Retryable(backoff=@Backoff(delay=500), maxAttempts=10)
	public lithium.service.promo.data.entities.Promotion modify(lithium.service.promo.data.entities.Promotion promotion, Promotion promotionPost) throws Exception {
		PromotionRevision edit = promotion.getEdit();
		edit.setName(promotionPost.getEdit().getName());

		lithium.service.promo.data.entities.Promotion dependsOn = null;

		if(promotionPost.getEdit().getDependsOnPromotion() != null) {
			dependsOn = promotionRepository.findOne(promotionPost.getEdit().getDependsOnPromotion().getId());
		}

		edit.setDescription(promotionPost.getEdit().getDescription());
		edit.setDependsOnPromotion(dependsOn);
		edit.setRecurrencePattern(promotionPost.getEdit().getRecurrencePattern());
		edit.setRedeemableInTotal(promotionPost.getEdit().getRedeemableInTotal());
		edit.setRedeemableInEvent(promotionPost.getEdit().getRedeemableInEvent());
		edit.setEventDurationGranularity(promotionPost.getEdit().getEventDurationGranularity());
		edit.setEventDuration(promotionPost.getEdit().getEventDuration());
		edit.setXpLevel(promotionPost.getEdit().getXpLevel());

		Reward missionReward = promotion.getCurrent().getReward();
		if (promotionPost.getEdit().getReward() != null) {
			if (missionReward != null) {
				missionReward.setRewardId(promotionPost.getEdit().getReward().getRewardId());
			} else {
				missionReward = Reward.builder().rewardId(promotionPost.getEdit().getReward().getRewardId()).build();
			}
			missionReward.setPromotionRevision(edit);
			missionReward = rewardRepository.save(missionReward);

			edit.setReward(missionReward);
		} else if (missionReward != null) {
			edit.setReward(null);
			edit = promotionRevisionRepository.save(edit);
			rewardRepository.delete(missionReward);
		}
		promotion.setEdit(edit);

		return promotionRepository.save(promotion);
	}

	public lithium.service.promo.data.entities.Promotion modifyAndSaveCurrent(lithium.service.promo.data.entities.Promotion promotion, Promotion promotionPost) throws Exception {
		promotion = modify(promotion, promotionPost);
		promotion.setCurrent(promotion.getEdit());
		promotion.setEdit(null);
		log.debug("Checking Promotion:" + promotion);
		log.debug("Checking PromotionPost:" + promotionPost);
		return promotionRepository.save(promotion);
	}

	@Transactional(rollbackOn=Exception.class)
	@Retryable(backoff=@Backoff(delay=500), maxAttempts=10)
	public lithium.service.promo.data.entities.Promotion addChallenge(lithium.service.promo.data.entities.Promotion promotion,
																	  lithium.service.promo.client.objects.Challenge challengePost
	) throws Exception {
		PromotionRevision edit = promotion.getEdit();
		Reward challengeReward = null;

		if (challengePost.getReward() != null && challengePost.getReward().getRewardId() != null) {
				challengeReward = rewardRepository.save(
					Reward.builder().rewardId(challengePost.getReward().getRewardId()).build());
		}

		Graphic challengeIcon = null;

		if (challengePost.getImage() != null) {
			challengeIcon = Graphic.builder()
			.image(challengePost.getImage().getBase64())
			.name(challengePost.getImage().getFilename())
			.size(challengePost.getImage().getFilesize())
			.type(challengePost.getImage().getFiletype())
			.build();
			challengeIcon = graphicRepository.save(challengeIcon);
		}

		ChallengeGroup challengeGroup = challengeGroupService.findOrCreate(challengePost.getChallengeGroup().getId(), promotion.getEdit());

		Challenge challenge = Challenge.builder()
		.description(challengePost.getDescription())
		.reward(challengeReward)
		.icon(challengeIcon)
		.challengeGroup(challengeGroup)
		.build();
		challenge = challengeRepository.save(challenge);
		//edit.getChallenges().add(challenge);
		edit = promotionRevisionRepository.save(edit);
		return promotionRepository.save(promotion);
	}

	@Transactional(rollbackOn=Exception.class)
	@Retryable(backoff=@Backoff(delay=500), maxAttempts=10)
	public lithium.service.promo.data.entities.Promotion removeChallenge(lithium.service.promo.data.entities.Promotion promotion, Challenge challenge) throws Exception {
		challenge.setDeleted(true);
		challengeRepository.save(challenge);
		return promotionRepository.findOne(promotion.getId());
	}

	@Transactional(rollbackOn=Exception.class)
	@Retryable(backoff=@Backoff(delay=500), maxAttempts=10)
	public lithium.service.promo.data.entities.Promotion modifyChallenge(lithium.service.promo.data.entities.Promotion promotion,
																		 Challenge challenge, lithium.service.promo.client.objects.Challenge challengePost) throws Exception {
		challenge.setDescription(challengePost.getDescription());
		Reward challengeReward = challenge.getReward();
		if (challengePost.getReward() != null && challengePost.getReward().getRewardId() != null) {
			if (challengeReward != null) {
				challengeReward.setRewardId(challengePost.getReward().getRewardId());
			} else {
				challengeReward = Reward.builder().rewardId(challengePost.getReward().getRewardId()).build();
			}
			challengeReward.setChallenge(challenge);
			challengeReward = rewardRepository.save(challengeReward);
			challenge.setReward(challengeReward);
		} else if (challengeReward != null) {
			challenge.setReward(null);
			challenge = challengeRepository.save(challenge);
			rewardRepository.delete(challengeReward);
		}
		Graphic challengeIcon = challenge.getIcon();
		if (challengePost.getImage() != null) {
			if (challengeIcon != null) {
				challengeIcon.setImage(challengePost.getImage().getBase64());
				challengeIcon.setName(challengePost.getImage().getFilename());
				challengeIcon.setSize(challengePost.getImage().getFilesize());
				challengeIcon.setType(challengePost.getImage().getFiletype());
				challengeIcon = graphicRepository.save(challengeIcon);
				challenge.setIcon(challengeIcon);
			} else {
				challengeIcon = Graphic.builder()
				.image(challengePost.getImage().getBase64())
				.name(challengePost.getImage().getFilename())
				.size(challengePost.getImage().getFilesize())
				.type(challengePost.getImage().getFiletype())
				.build();
				challengeIcon = graphicRepository.save(challengeIcon);
				challenge.setIcon(challengeIcon);
			}
		} else if (challengeIcon != null) {
			Long graphicId = challengeIcon.getId();
			challenge.setIcon(null);
			challenge = challengeRepository.save(challenge);
			graphicRepository.delete(graphicId);
		}
		challengeRepository.save(challenge);
		return promotionRepository.findOne(promotion.getId());
	}

	@Transactional(rollbackOn=Exception.class)
	@Retryable(backoff=@Backoff(delay=500), maxAttempts=10)
	public lithium.service.promo.data.entities.Promotion addChallengeRule(lithium.service.promo.data.entities.Promotion promotion, Challenge challenge,
																		  lithium.service.promo.client.objects.Rule challengeRulePost
	) throws Exception {
		PromoProvider promoProvider = promoProviderRepository.findByUrlAndCategoryName(challengeRulePost.getPromoProvider().getUrl(), challengeRulePost.getPromoProvider().getCategory());
		Activity activity = activityRepository.findByPromoProviderAndName(promoProvider, challengeRulePost.getActivity().getName());
		lithium.service.promo.data.entities.Category category = categoryRepository.findByName(challengeRulePost.getCategory());

		Rule rule = Rule.builder()
		.challenge(challenge)
		.promoProvider(promoProvider)
		.category(category)
		.activity(activity)
		.operation(challengeRulePost.getOperation())
		.value(challengeRulePost.getValue())
		.build();
		rule = ruleRepository.save(rule);
		challenge.getRules().add(rule);
		challengeRepository.save(challenge);
		return promotionRepository.findOne(promotion.getId());
	}

	@Transactional(rollbackOn=Exception.class)
	@Retryable(backoff=@Backoff(delay=500), maxAttempts=10)
	public lithium.service.promo.data.entities.Promotion removeChallengeRule(lithium.service.promo.data.entities.Promotion promotion, Challenge challenge, Rule rule) throws Exception {
		ruleRepository.save(rule);
		return promotionRepository.findOne(promotion.getId());
	}

	@Transactional(rollbackOn=Exception.class)
	@Retryable(backoff=@Backoff(delay=500), maxAttempts=10)
	public lithium.service.promo.data.entities.Promotion modifyChallengeRule(lithium.service.promo.data.entities.Promotion promotion,
																			 Challenge challenge, Rule rule, lithium.service.promo.client.objects.Rule challengeRulePost) throws Exception {
		PromoProvider promoProvider = promoProviderRepository.findByUrlAndCategoryName(challengeRulePost.getPromoProvider().getUrl(), challengeRulePost.getPromoProvider().getCategory());
		Activity activity = activityRepository.findByPromoProviderAndName(promoProvider, challengeRulePost.getActivity().getName());
		lithium.service.promo.data.entities.Category category = categoryRepository.findByName(challengeRulePost.getCategory());
		rule.setPromoProvider(promoProvider);
		rule.setCategory(category);
		rule.setActivity(activity);
		rule.setOperation(challengeRulePost.getOperation());
		rule.setValue(challengeRulePost.getValue());
		ruleRepository.save(rule);
		return promotionRepository.findOne(promotion.getId());
	}

	public ResponseEntity<byte[]> getChallengeIconAsResponseEntity(Long challengeId) {
		Challenge challenge = challengeRepository.findOne(challengeId);
		HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS).noTransform().cachePublic().getHeaderValue());
		byte[] imageBytes = null;
		if (challenge != null && challenge.getIcon() != null) {
			switch (challenge.getIcon().getType()) {
				case MediaType.IMAGE_GIF_VALUE: headers.setContentType(MediaType.IMAGE_GIF); break;
				case MediaType.IMAGE_JPEG_VALUE: headers.setContentType(MediaType.IMAGE_JPEG); break;
				case MediaType.IMAGE_PNG_VALUE: headers.setContentType(MediaType.IMAGE_PNG); break;
				default:;
			}
			imageBytes = challenge.getIcon().getImage();
		}
		if (imageBytes == null) {
			imageBytes = new byte[]	{
					0x47, 0x49, 0x46, 0x38, 0x37, 0x61, 0x01, 0x00, 0x01, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00,
					0x00, 0x00, 0x2C, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x02, 0x02, 0x44, 0x01
			};
			headers.setContentType(MediaType.IMAGE_PNG);
		}
		return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
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

	private AccountingClient getAccountingClient() {
		AccountingClient client = null;
		try {
			client = services.target(AccountingClient.class, "service-accounting", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting AccountingClient, " + e.getMessage(), e);
		}
		return client;
	}

	private Integer findPlayerXPLevel(String domainName, String playerGuid) throws Exception {
		Integer level = 0;
		Long xp = getAccountingClient().get("XP", domainName, playerGuid).getData();
		if (xp != null && xp > 0) {
			Scheme scheme = getXPClient().getActiveScheme(domainName).getData();
			List<Level> sortedLevels = scheme.getLevels().stream()
			.sorted(Comparator.comparingInt(Level::getNumber))
			.toList();
			for (Level l: sortedLevels) {
				if (xp >= l.getRequiredXp()) level = l.getNumber();
				else break;
			}
		}
		return level;
	}

    public lithium.service.promo.data.entities.Promotion addUserCategories(lithium.service.promo.data.entities.Promotion promotion, List<lithium.service.promo.client.objects.UserCategory> userCategories) {

		PromotionRevision edit = promotion.getEdit();
		List<UserCategory> userCategoryList;
		List<Long> userCategoryIds = edit.getUserCategories() == null ? new ArrayList<>() : edit.getUserCategories().stream().map(UserCategory::getUserCategoryId)
				.toList();

		if(userCategoryIds.isEmpty()) {
			userCategoryList = userCategories.stream().map(c -> UserCategory.builder()
							.userCategoryId(c.getUserCategoryId())
							.promotionRevision(edit)
							.type(c.getType())
							.build())
					.collect(Collectors.toList());
		}
		else {
			userCategoryList = userCategories.stream().filter(uc -> !userCategoryIds.contains(uc.getUserCategoryId()))
					.map(c -> UserCategory.builder()
							.userCategoryId(c.getUserCategoryId())
							.type(c.getType())
							.promotionRevision(edit)
							.build())
					.collect(Collectors.toList());
		}

		userCategoryRepository.saveAll(userCategoryList);
		userCategoryList.addAll(edit.getUserCategories());
		edit.setUserCategories(userCategoryList);
		promotion.setEdit(edit);

		return promotion;

    }

	public void deleteUserCategory(UserCategory userCategory) {
		userCategoryRepository.delete(userCategory);
	}

//	public lithium.service.promo.data.entities.Promotion addGroupedChallenges(lithium.service.promo.data.entities.Promotion promotion, lithium.service.promo.client.objects.ChallengeGroup challegeGroupPost) {
//		ChallengeGroup challengeGroup = challengeGroupService.findOrCreate(challegeGroupPost.getId(), promotion.getEdit());
//		addChallengesToGroup(challengeGroup, challegeGroupPost.getChallenges());
//		return promotion;
//	}


	private List<Challenge> addChallengesToGroup(ChallengeGroup challengeGroup, List<ChallengeBO> challenges) {
		List<Challenge> promotionChallenges = new ArrayList<>();

		for (ChallengeBO challengePost: challenges) {
			Reward challengeReward = null;
			if (challengePost.getReward() != null && challengePost.getReward().getRewardId() != null) {
				challengeReward = rewardRepository.save(
						Reward.builder().rewardId(challengePost.getReward().getRewardId()).build());
			}

			Challenge challenge = Challenge.builder()
					.description(challengePost.getDescription())
					.reward(challengeReward)
					.challengeGroup(challengeGroup)
					.sequenceNumber(challengePost.getSequenceNumber())
					.requiresAllRules(BooleanUtils.toBoolean(challengePost.getRequiresAllRules()))
					.build();

			challenge = challengeRepository.save(challenge);
			if (challengeReward != null) {
				challengeReward.setChallenge(challenge);
				rewardRepository.save(challengeReward);
			}
			List<Rule> challengeRules = new ArrayList<>();
			for (RuleBO challengeRule: challengePost.getRules()) {
				PromoProvider promoProvider = promoProviderRepository.findByUrlAndCategoryName(challengeRule.getPromoProvider().getUrl(), challengeRule.getPromoProvider().getCategory());
				Activity activity = activityRepository.findByPromoProviderAndName(promoProvider, challengeRule.getActivity().getName());
				List<ActivityExtraFieldRuleValueBO> promoProviderExtraFieldRuleValues = Optional.ofNullable(challengeRule.getActivityExtraFieldRuleValues()).orElse(new ArrayList<>());
				lithium.service.promo.data.entities.Category category = categoryRepository.findByName(challengeRule.getCategory());

				Rule rule = Rule.builder()
						.challenge(challenge)
						.promoProvider(promoProvider)
						.category(category)
						.activity(activity)
						.operation(Operation.fromType(challengeRule.getOperation()))
						.value(challengeRule.getValue())
						.build();

				rule = ruleRepository.save(rule);
				challengeRules.add(rule);

				for (ActivityExtraFieldRuleValueBO extraFieldRuleValue: promoProviderExtraFieldRuleValues) {
					ActivityExtraField activityExtraField = activityExtraFieldRepository.findByActivityAndName(activity, extraFieldRuleValue.getActivityExtraField().getName());

					String value = String.join(",", Optional.ofNullable(extraFieldRuleValue.getValue()).orElse(new ArrayList<>()));

					if (!StringUtil.isEmpty(value)) {
						ActivityExtraFieldRuleValue promoProviderExtraFieldRuleValue = ActivityExtraFieldRuleValue.builder()
								.value(value)
								.activityExtraField(activityExtraField)
								.rule(rule)
								.build();
						activityExtraFieldRuleValueRepository.save(promoProviderExtraFieldRuleValue);
					} else {
						log.debug("Skipping rule extra rule value for field {} and activity {} because an empty value was provided", activityExtraField.getName(), activityExtraField.getActivity().getName());
					}

				}
			}
			challenge.setRules(challengeRules);
			challenge = challengeRepository.save(challenge);
			promotionChallenges.add(challenge);
		}
		return promotionChallenges;
	}

	public List<PromotionBO> getPromotionsWithEventsWithPeriod(PromoQuery query) {

		List<String> domains = Optional.ofNullable(query.domains()).orElse(new ArrayList<>());
		Specification<lithium.service.promo.data.entities.Promotion> spec = PromotionSpecifications.domains(domains);
		spec = spec.and(PromotionSpecifications.enabled(true));

		if (query.endDate() != null) {
			spec = spec.and(PromotionSpecifications.startsBefore(LocalDateTime.of(query.endDate(), LocalTime.MAX)));
		}

		List<lithium.service.promo.data.entities.Promotion> promotions = promotionRepository.findAll(spec);
		return promotions.stream()
				.filter(p -> recurrenceRuleService.promotionHasEventsBetween(p, query.startDate(), query.endDate()))
				.map(this::convertPromotionBO)
				.toList();
	}

	public List<PromotionBO> getDisabledPromotions(PromoQuery query) {

		List<String> domains = Optional.ofNullable(query.domains()).orElse(new ArrayList<>());
		Specification<lithium.service.promo.data.entities.Promotion> spec = PromotionSpecifications.domains(domains);
		spec = spec.and(PromotionSpecifications.enabled(false));

		if (query.startDate() != null) {
			spec = spec.and(PromotionSpecifications.startDate(LocalDateTime.of(query.startDate(), LocalTime.MIN)));
		}
		if (query.endDate() != null) {
			spec = spec.and(PromotionSpecifications.endDate(LocalDateTime.of(query.endDate(), LocalTime.MAX)));
		}

		List<lithium.service.promo.data.entities.Promotion> promotions = promotionRepository.findAll(spec);
		return promotions.stream()
				.map(this::convertPromotionBO)
				.toList();
	}

  public List<Long> promotionsLinkedToReward(Long rewardId) {
		return promotionRepository.findByCurrentRewardRewardId(rewardId).stream().map(lithium.service.promo.data.entities.Promotion::getId).toList();
  }

	public Optional<lithium.service.promo.data.entities.Promotion> findPromotion(Long id) {
		return promotionRepository.findById(id);
	}


	public Promotion applyPatchToPromotion(String patch, Long targetPromotionId)
			throws JsonProcessingException, PromotionNotFoundException {
		lithium.service.promo.data.entities.Promotion promotion = findPromotion(targetPromotionId)
				.orElseThrow(PromotionNotFoundException::new);

		return applyPatchToPromotion(patch, promotion);
	}

	public Promotion applyPatchToPromotion(String patch, lithium.service.promo.data.entities.Promotion targetPromotion)
			throws JsonProcessingException
	{
		//    JsonNode patched = patch.apply(objectMapper.convertValue(targetPromotion, JsonNode.class));
		//    return objectMapper.treeToValue(patched, Promotion.class);
		return null;
	}
	public lithium.service.promo.data.entities.Promotion findOne(Long id) {
		return promotionRepository.findOne(id);
	}

	public PromotionBO markAsCurrentV1(Long promotionId, LithiumTokenUtil tokenUtil) {
		lithium.service.promo.data.entities.Promotion promotion = promotionRepository.findOne(promotionId);

		User editor = userService.findOrCreate(tokenUtil.guid());
		log.debug("{} marking promotion as current. {}", editor.guid(), promotion);

		PromotionRevision editRevision = promotion.getEdit();
		promotion.setCurrent(editRevision);
		promotion.setEdit(null);

		promotion.setEditor(editor);


		try {
			List<ChangeLogFieldChange> changes = changeLogService.copy(promotion, new lithium.service.promo.data.entities.Promotion(),
					new String[]{"id", "editor", "current"});

			changeLogService.registerChangesWithDomainAndFullName("promotion", "edit", promotion.getId(), tokenUtil.guid(), MessageFormat.format("PromotionRevision with id {0} has been changed from draft to current", editRevision.getId()), null, changes, Category.PROMOTIONS, SubCategory.PROMOTION_CREATE, 1, promotion.getCurrent().getDomain().getName(), tokenUtil.userLegalName());
		}
		catch (Exception e) {
			log.error("Failed to register changelogs after editing promotion with id:{}, author:{}, reason: {}", promotion.getId(), editor.guid(), e);
		}

		return convertPromotionBO(promotionRepository.save(promotion));
	}

	public PromotionBO editV1(PromotionBO request, LithiumTokenUtil tokenUtil) throws ParseException {
		TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("UTC")));

		PromotionRevisionBO editRevisionPost = request.getEdit();
		if (request.getId() == null) throw new Status412InvalidPromotionEditException();
		lithium.service.promo.data.entities.Promotion promotion = promotionRepository.findOne(request.getId());
		if (promotion == null) throw new Status412InvalidPromotionEditException();
		if (editRevisionPost == null) throw new Status412InvalidPromotionEditException();

		User editor = userService.findOrCreate(tokenUtil.guid());
		Domain domain = domainService.findOrCreate(editRevisionPost.getDomain().getName());

		log.info("{} editing draft promotion {}", editor.guid(), editRevisionPost);

		lithium.service.promo.data.entities.Promotion dependsOnPromotion = null;
		if (editRevisionPost.getDependsOnPromotion() != null) {
			dependsOnPromotion = promotionRepository.findOne(editRevisionPost.getDependsOnPromotion());
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		LocalDateTime startDate = parseFromDomainTimezoneToUTC(editRevisionPost.getStartDate(), domain);
		LocalDateTime endDate = parseFromDomainTimezoneToUTC(editRevisionPost.getEndDate(), domain);

		if (editRevisionPost.getId() == null) {
			// New draft creation.
			PromotionRevision editRevision = PromotionRevision.builder()
					.promotion(promotion)
					.domain(domain)
					.name(editRevisionPost.getName())
					.description(editRevisionPost.getDescription())
					.dependsOnPromotion(dependsOnPromotion)
					.recurrencePattern(editRevisionPost.getRecurrencePattern())
					.redeemableInTotal(editRevisionPost.getRedeemableInTotal())
					.redeemableInEvent(editRevisionPost.getRedeemableInEvent())
					.eventDuration(editRevisionPost.getEventDuration())
					.eventDurationGranularity(editRevisionPost.getEventDurationGranularity())
					.xpLevel(Optional.ofNullable(editRevisionPost.getXpLevel()).orElse(0))
					.startDate(startDate)
					.endDate(endDate)
					.exclusive(editRevisionPost.isExclusive())
					.requiresAllChallengeGroups(BooleanUtils.toBoolean(editRevisionPost.getRequiresAllChallengeGroups()))
					.build();


			editRevision = promotionRevisionRepository.save(editRevision);

			if (editRevisionPost.getReward() != null) {
				Reward reward = rewardRepository.save(
						Reward.builder().promotionRevision(editRevision).rewardId(editRevisionPost.getReward().getRewardId()).build()
				);
				editRevision.setReward(reward);
				editRevision = promotionRevisionRepository.save(editRevision);
			}

			createChallengeGroups(editRevisionPost.getChallengeGroups(), editRevision);
			createUserCategories(editRevisionPost.getUserCategories(), editRevision);

			if (editRevisionPost.isExclusive()) editRevision = createExclusivePlayers(editRevisionPost.getExclusivePlayers(), editRevision);

			editRevision = promotionRevisionRepository.save(editRevision);

			promotion.setEdit(editRevision);
			promotion.setEditor(editor);
			promotion = promotionRepository.save(promotion);
		} else {
			PromotionRevision editRevision = promotionRevisionRepository.findOne(editRevisionPost.getId());
			BeanUtils.copyProperties(editRevisionPost, editRevision);

			editRevision.setStartDate(startDate);
			editRevision.setEndDate(endDate);

			if ((editRevision.getReward() == null) && (editRevisionPost.getReward() != null)) {
				Reward reward = rewardRepository.save(
						Reward.builder().promotionRevision(editRevision).rewardId(editRevisionPost.getReward().getRewardId()).build()
				);
				editRevision.setReward(reward);
			}

			applyChallengeGroupSync(editRevision, editRevisionPost);
			applyUserCategoriesSync(editRevision, editRevisionPost);
			applyExclusionPlayersSync(editRevision, editRevisionPost);
			editRevision = promotionRevisionRepository.save(editRevision);
			promotion = editRevision.getPromotion();
		}

		try {
			List<ChangeLogFieldChange> changes = changeLogService.copy(promotion, new lithium.service.promo.data.entities.Promotion(),
					new String[]{"id", "edit", "editor", "version"});

			changeLogService.registerChangesWithDomainAndFullName("promotion", "edit", promotion.getId(), tokenUtil.guid(), null, null, changes, Category.PROMOTIONS, SubCategory.PROMOTION_CREATE, 1, promotion.getCurrent().getDomain().getName(), tokenUtil.userLegalName());
		} catch (Exception e) {
			log.error("Failed to register changelogs after editing promotion with id:{}, author:{}, reason: {}", promotion.getId(), editor.guid(), e);
		}

		log.info("Updated Promotion {}", promotion);

		return convertPromotionBO(promotion);
	}

	private void applyChallengeGroupSync(PromotionRevision edit, PromotionRevisionBO promotionRevisionPost) {
		List<ChallengeGroup> challengeGroups = new ArrayList<>();

		List<ChallengeGroupBO> challengeGroupList = Optional.of(promotionRevisionPost.getChallengeGroups())
				.orElse(new ArrayList<>());

		if (edit.getChallengeGroups() != null) {
			//It makes no sense to mark (challenge groups, challenges, rules, etc) as deleted when they are still in draft state,
			//deleting them makes a lot of sense in this instance and it will save space on the database
			for (ChallengeGroup challengeGroup: edit.getChallengeGroups()) {
				if (challengeGroup.getChallenges() != null) {
					challengeGroup.getChallenges().forEach(challenge -> {
						if (challenge.getRules() != null) {
							challenge.getRules().forEach(rule -> {
								List<ActivityExtraFieldRuleValue> values = activityExtraFieldRuleValueRepository.findByRule(rule);
								if (values.isEmpty()) {
									activityExtraFieldRuleValueRepository.deleteAll(values);
								}
							});
						}
						ruleRepository.deleteAll(challenge.getRules());
					});
					challengeRepository.deleteAll(challengeGroup.getChallenges());
					challengeGroupRepository.delete(challengeGroup);
				}
			}
		}


		for (ChallengeGroupBO group: challengeGroupList) {
			ChallengeGroup challengeGroup = challengeGroupService.findOrCreate(group.getId(), edit);
			addChallengesToGroup(challengeGroup, group.getChallenges());
			challengeGroups.add(challengeGroup);
		}

		edit.setChallengeGroups(challengeGroups);
	}

	private void applyUserCategoriesSync(PromotionRevision edit, PromotionRevisionBO promotionRevisionPost) {

		if (edit.getUserCategories() != null) {
			userCategoryRepository.deleteAll(edit.getUserCategories());
		}

		createUserCategories(promotionRevisionPost.getUserCategories(), edit);
	}

	private void applyExclusionPlayersSync(PromotionRevision edit, PromotionRevisionBO promotionRevisionPost) {

		if (edit.getExclusivePlayers() != null) {
			edit.getExclusivePlayers().clear();
			promotionRevisionRepository.save(edit);
		}

		createExclusivePlayers(promotionRevisionPost.getExclusivePlayers(), edit);
	}

	public PromotionBO findOneAndConvert(Long promotionId) {
		lithium.service.promo.data.entities.Promotion promotion = findOne(promotionId);
		return convertPromotionBO(promotion);
	}

	public boolean toggleEnabled(lithium.service.promo.data.entities.Promotion promotion, boolean enabled, LithiumTokenUtil lithiumTokenUtil) {
		boolean currentState = Optional.ofNullable(promotion.getEnabled()).orElse(false);
		String state = enabled ? "enabled": "disabled";

		if(currentState == enabled) {
			log.warn("Promotion with Id {} is aready {}", promotion.getId(), state);
			return currentState;
		}

		promotion.setEnabled(enabled);

		promotionRepository.save(promotion);

		try {
			List<ChangeLogFieldChange> changes = Arrays.asList(ChangeLogFieldChange.builder()
							.field("enabled")
							.fromValue(String.valueOf(currentState))
							.toValue(String.valueOf(enabled))
					.build());

			changeLogService.registerChangesWithDomainAndFullName("promotion", "edit", promotion.getId(), lithiumTokenUtil.guid(), "Promotion was updated", null, changes, Category.PROMOTIONS, SubCategory.PROMOTION_EDIT, 100, promotion.getCurrent().getDomain().getName(), lithiumTokenUtil.userLegalName());
		}
		catch (Exception e) {
			log.error("Failed to register changelog entry after promotion with id {} was {} by {}", promotion.getId(), state, lithiumTokenUtil.guid());
		}

		return enabled;
	}

	public boolean delete(Long promotionId, LithiumTokenUtil lithiumTokenUtil) throws Status411InvalidPromotionException {
		lithium.service.promo.data.entities.Promotion promotion = findOne(promotionId);

		if (promotion == null) {
			throw new Status411InvalidPromotionException();
		}

		promotion.setDeleted(true);
		promotionRepository.save(promotion);

		try {
			List<ChangeLogFieldChange> changes = Arrays.asList(ChangeLogFieldChange.builder()
					.field("deleted")
					.fromValue(String.valueOf(false))
					.toValue(String.valueOf(true))
					.build());

			changeLogService.registerChangesWithDomainAndFullName("promotion", "delete", promotion.getId(), lithiumTokenUtil.guid(), "Promotion was updated", null, changes, Category.PROMOTIONS, SubCategory.PROMOTION_EDIT, 100, promotion.getCurrent().getDomain().getName(), lithiumTokenUtil.userLegalName());
		} catch (Exception e) {
			log.error("Failed to register changelog entry after promotion with id {} was {} by {}", promotion.getId(), "deleted", lithiumTokenUtil.guid());
		}

		return promotion.getDeleted();
	}

	public Response<ChangeLogs> changelogs(lithium.service.promo.data.entities.Promotion promotion, String[] entities, Integer page) throws Exception {
		return changeLogService.listLimited(ChangeLogRequest.builder()
						.entityRecordId(promotion.getId())
						.page(page)
						.entities(entities)
				.build());
	}

	public LocalDateTime parseFromDomainTimezoneToUTC(String datetimeString, Domain domain) {

		if (datetimeString == null) {
			return null;
		}
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		ZoneId domainZoneId = ZoneId.of(Optional.ofNullable(domain.getTimezone()).orElse("UTC"));
		LocalDateTime localDateTime = LocalDateTime.from(dateTimeFormatter.parse(datetimeString));
		ZonedDateTime domainZonedDateTime = ZonedDateTime.of(localDateTime, domainZoneId);
		ZonedDateTime utcDateTime = domainZonedDateTime.withZoneSameInstant(ZoneOffset.UTC);

		log.debug("Time in {}: {}, UTC time: {}", domain.getName(), domainZonedDateTime, utcDateTime);
		return utcDateTime.toLocalDateTime();
	}
}

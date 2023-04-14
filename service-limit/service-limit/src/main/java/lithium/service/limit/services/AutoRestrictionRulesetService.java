package lithium.service.limit.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.limit.data.entities.AutoRestrictionRule;
import lithium.service.limit.data.entities.AutoRestrictionRuleSet;
import lithium.service.limit.data.entities.Domain;
import lithium.service.limit.data.entities.DomainRestrictionSet;
import lithium.service.limit.data.repositories.AutoRestrictionRuleRepository;
import lithium.service.limit.data.repositories.AutoRestrictionRuleSetRepository;
import lithium.service.limit.data.repositories.DomainRepository;
import lithium.service.limit.data.repositories.DomainRestrictionSetRepository;
import lithium.service.limit.data.specifications.AutoRestrictionRulesetSpecification;
import lithium.service.limit.objects.AutoRestrictionRuleField;
import lithium.service.limit.objects.AutoRestrictionRuleOperator;
import lithium.service.limit.objects.AutoRestrictionRuleSetOutcome;
import lithium.service.limit.objects.RestrictionEvent;
import lithium.service.user.client.UserClient;
import lithium.service.user.client.objects.User;
import lithium.util.DomainValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AutoRestrictionRulesetService {
	@Autowired
	private AutoRestrictionRuleRepository ruleRepository;
	@Autowired
	private AutoRestrictionRuleSetRepository ruleSetRepository;
	@Autowired
	private ChangeLogService changeLogService;
	@Autowired
	private DomainRepository domainRepository;
	@Autowired
	private DomainRestrictionSetRepository domainRestrictionSetRepository;
	@Autowired
	private MessageSource messages;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private ExternalUserService externalUserService;

	public List<AutoRestrictionRuleField> ruleFields(Locale locale) {
		List<AutoRestrictionRuleField> fields = Arrays
				.stream(lithium.service.limit.enums.AutoRestrictionRuleField.values())
				.map(field -> {
					return new AutoRestrictionRuleField(field.id(), field.field(),
							messages.getMessage("SERVICE-LIMIT.RESTRICTIONS.RULESETS.RULE.FIELD." + field.field(),
									null, locale));
				})
				.collect(Collectors.toList());
		return fields;
	}

	public List<RestrictionEvent> events(Locale locale) {
		List<RestrictionEvent> events = Arrays
				.stream(lithium.service.limit.enums.RestrictionEvent.values())
				.map(event -> {
					return new RestrictionEvent(event.id(), event.event(),
							messages.getMessage("SERVICE-LIMIT.RESTRICTIONS.RULESETS.RULE.EVENTS." + event.event(),
									null, locale));
				})
				.collect(Collectors.toList());
		return events;
	}

	public List<AutoRestrictionRuleOperator> ruleOperators(Locale locale) {
		List<AutoRestrictionRuleOperator> operators = Arrays
				.stream(lithium.service.limit.enums.AutoRestrictionRuleOperator.values())
				.map(operator -> {
					return new AutoRestrictionRuleOperator(operator.id(), operator.operator(),
							messages.getMessage("SERVICE-LIMIT.RESTRICTIONS.RULESETS.RULE.OPERATOR." +
									operator.operator(), null, locale));
				})
				.collect(Collectors.toList());
		return operators;
	}

	public List<AutoRestrictionRuleSetOutcome> ruleSetOutcomes(Locale locale) {
		List<AutoRestrictionRuleSetOutcome> operators = Arrays
				.stream(lithium.service.limit.enums.AutoRestrictionRuleSetOutcome.values())
				.map(outcome -> {
					return new AutoRestrictionRuleSetOutcome(outcome.id(), outcome.outcome(),
							messages.getMessage("SERVICE-LIMIT.RESTRICTIONS.RULESETS.OUTCOME." +
									outcome.outcome(), null, locale));
				})
				.collect(Collectors.toList());
		return operators;
	}

	public Page<AutoRestrictionRuleSet> find(String[] domains, Boolean enabled, String name, Date lastUpdatedStart,
											 Date lastUpdatedEnd, String searchValue, Pageable pageable) {
		Specification<AutoRestrictionRuleSet> spec = null;
		spec = addToSpec(false, spec, AutoRestrictionRulesetSpecification::deleted);
		spec = addToSpec(enabled, spec, AutoRestrictionRulesetSpecification::enabled);
		spec = addToSpec(name, spec, AutoRestrictionRulesetSpecification::nameStartsWith);
		spec = addToSpec(lastUpdatedStart, false, spec, AutoRestrictionRulesetSpecification::lastUpdatedStart);
		spec = addToSpec(lastUpdatedEnd, true, spec, AutoRestrictionRulesetSpecification::lastUpdatedEnd);
		spec = addToSpec(domains, spec, AutoRestrictionRulesetSpecification::domains);
		spec = addToSpec(searchValue, spec, AutoRestrictionRulesetSpecification::any);
		Page<AutoRestrictionRuleSet> result = ruleSetRepository.findAll(spec, pageable);

		final List<User> users = new ArrayList<>();

		try {
			users.addAll(getUsersFromRuleSets(result.getContent()));
		} catch (Exception e) {
			log.error("Failed to fetch users", e);
		}

		return result.map(rs -> mapUpdatedByToAuthorFullName(rs, users));
	}

	public AutoRestrictionRuleSet find(Long id) throws Exception {
		AutoRestrictionRuleSet set = ruleSetRepository.findOne(id);
		if (set == null)
			throw new Status500InternalServerErrorException("AutoRestrictionRuleSet not found [id=" + id + "]");

		List<User> users = getUsersFromRuleSets(Arrays.asList(set));

		set = mapUpdatedByToAuthorFullName(set, users);

		return set;
	}

	public AutoRestrictionRule findRule(Long id) throws Status500InternalServerErrorException {
		AutoRestrictionRule rule = ruleRepository.findOne(id);
		if (rule == null) throw new Status500InternalServerErrorException("Rule not found [id=" + id + "]");
		return rule;
	}

	public AutoRestrictionRuleSet findByDomainAndName(Domain domain, String name) {
		return ruleSetRepository.findByDomainAndName(domain, name);
	}

	@Transactional(rollbackOn = Exception.class)
	public AutoRestrictionRuleSet create(String domainName, AutoRestrictionRuleSet ruleset, String authorGuid)
			throws Status500InternalServerErrorException {
		Domain domain = domainRepository.findOrCreateByName(domainName, () -> new Domain());
		if (findByDomainAndName(domain, ruleset.getName()) != null) {
			throw new Status500InternalServerErrorException("A ruleset with this name already exists");
		}
		ruleset.setDomain(domain);
		ruleset.setLastUpdated(new Date());
		ruleset.setLastUpdatedBy(authorGuid);
		DomainRestrictionSet domainRestrictionSet = domainRestrictionSetRepository.findOne(
				ruleset.getRestrictionSet().getId());
		DomainValidationUtil.validate(domainRestrictionSet.getDomain().getName(), domainName);
		ruleset.setRestrictionSet(domainRestrictionSet);
		ruleset = ruleSetRepository.save(ruleset);
		for (AutoRestrictionRule rule : ruleset.getRules()) {
			rule.setRuleset(ruleset);
			rule = ruleRepository.save(rule);
		}
		try {
			List<ChangeLogFieldChange> clfc = changeLogService.compare(ruleset, new AutoRestrictionRuleSet(),
					new String[]{"domain", "name", "enabled", "lastUpdated", "lastUpdatedBy", "rules", "outcome",
							"restrictionSet"});
			changeLogService.registerChangesBlocking("auto-restriction-ruleset", "create", ruleset.getId(),
					authorGuid, null, null, clfc, Category.ACCESS, SubCategory.RULE_SET, 0, domainName);
		} catch (Exception e) {
			String msg = "Changelog registration for auto-restriction ruleset create failed";
			log.error(msg + " [ruleset=" + ruleset + ", authorGuid=" + authorGuid + "] " + e.getMessage(), e);
			throw new Status500InternalServerErrorException(msg);
		}
		return ruleset;
	}

	@Transactional(rollbackOn = Exception.class)
	public AutoRestrictionRuleSet changeName(AutoRestrictionRuleSet ruleset, String newName, String authorGuid)
			throws Status500InternalServerErrorException {
		if (ruleset.isDeleted()) {
			throw new Status500InternalServerErrorException("Ruleset not found");
		}
		if (findByDomainAndName(ruleset.getDomain(), newName) != null) {
			throw new Status500InternalServerErrorException("A ruleset with this name already exists");
		}
		AutoRestrictionRuleSet oldRuleset = new AutoRestrictionRuleSet();
		oldRuleset.setName(String.valueOf(ruleset.getName()));
		ruleset.setName(newName);
		ruleset.setLastUpdated(new Date());
		ruleset.setLastUpdatedBy(authorGuid);
		ruleset = ruleSetRepository.save(ruleset);
		try {
			List<ChangeLogFieldChange> clfc = changeLogService.compare(ruleset, oldRuleset,
					new String[]{"name"});
			changeLogService.registerChangesBlocking("auto-restriction-ruleset", "edit", ruleset.getId(),
					authorGuid, null, null, clfc, Category.ACCESS, SubCategory.RULE_SET, 0, ruleset.getDomain().getName());
		} catch (Exception e) {
			String msg = "Changelog registration for auto-restriction ruleset name change failed";
			log.error(msg + " [ruleset=" + ruleset + ", newName=" + newName + ", authorGuid=" + authorGuid + "] " + e.getMessage(),
					e);
			throw new Status500InternalServerErrorException(msg);
		}
		return ruleset;
	}

	@Transactional(rollbackOn = Exception.class)
	public AutoRestrictionRuleSet changeOutcome(AutoRestrictionRuleSet ruleset, Integer newOutcome, String authorGuid)
			throws Status500InternalServerErrorException {
		if (ruleset.isDeleted()) {
			throw new Status500InternalServerErrorException("Ruleset not found");
		}
		AutoRestrictionRuleSet oldRuleset = new AutoRestrictionRuleSet();
		oldRuleset.setOutcome(lithium.service.limit.enums.AutoRestrictionRuleSetOutcome.fromId(
				ruleset.getOutcome().id()));
		ruleset.setOutcome(lithium.service.limit.enums.AutoRestrictionRuleSetOutcome.fromId(newOutcome));
		ruleset.setLastUpdated(new Date());
		ruleset.setLastUpdatedBy(authorGuid);
		ruleset = ruleSetRepository.save(ruleset);
		try {
			List<ChangeLogFieldChange> clfc = changeLogService.compare(ruleset, oldRuleset, new String[]{"outcome"});
			changeLogService.registerChangesBlocking("auto-restriction-ruleset", "edit", ruleset.getId(),
					authorGuid, null, null, clfc, Category.ACCESS, SubCategory.RULE_SET, 0, ruleset.getDomain().getName());
		} catch (Exception e) {
			String msg = "Changelog registration for auto-restriction ruleset outcome change failed";
			log.error(msg + " [ruleset=" + ruleset + ", newOutcome=" + newOutcome + ", authorGuid=" + authorGuid + "] "
					+ e.getMessage(), e);
			throw new Status500InternalServerErrorException(msg);
		}
		return ruleset;
	}

	@Transactional(rollbackOn = Exception.class)
	public AutoRestrictionRuleSet applySkipTestUser(AutoRestrictionRuleSet ruleset,
			boolean skipTestUser, String authorGuid)
			throws Status500InternalServerErrorException {
		AutoRestrictionRuleSet oldRuleset = new AutoRestrictionRuleSet();
		oldRuleset.setOutcome(lithium.service.limit.enums.AutoRestrictionRuleSetOutcome.fromId(
				ruleset.getOutcome().id()));
		ruleset.setSkipTestUser(skipTestUser);
		ruleset.setLastUpdated(new Date());
		ruleset.setLastUpdatedBy(authorGuid);
		ruleset = ruleSetRepository.save(ruleset);
		try {
			List<ChangeLogFieldChange> clfc = changeLogService.compare(ruleset, oldRuleset,
					new String[]{"skipTestUser"});
			changeLogService.registerChangesBlocking("auto-restriction-ruleset", "edit", ruleset.getId(),
					authorGuid, null, null, clfc, Category.ACCESS, SubCategory.RULE_SET, 0,
					ruleset.getDomain().getName());
		} catch (Exception e) {
			String msg = "Changelog registration for auto-restriction ruleset skipTestUser change failed";
			log.error(msg + " [ruleset=" + ruleset + ", skipTestUser=" + skipTestUser + ", authorGuid="
					+ authorGuid + "] "
					+ e.getMessage(), e);
			throw new Status500InternalServerErrorException(msg);
		}
		return ruleset;
	}

	@Transactional(rollbackOn = Exception.class)
	public AutoRestrictionRuleSet changeRestriction(AutoRestrictionRuleSet ruleset,
													DomainRestrictionSet domainRestrictionSet, String authorGuid)
			throws Status500InternalServerErrorException {
		if (ruleset.isDeleted()) {
			throw new Status500InternalServerErrorException("Ruleset not found");
		}
		AutoRestrictionRuleSet oldRuleset = new AutoRestrictionRuleSet();
		oldRuleset.setRestrictionSet(ruleset.getRestrictionSet());
		ruleset.setRestrictionSet(domainRestrictionSet);
		ruleset.setLastUpdated(new Date());
		ruleset.setLastUpdatedBy(authorGuid);
		ruleset = ruleSetRepository.save(ruleset);
		try {
			List<ChangeLogFieldChange> clfc = changeLogService.compare(ruleset, oldRuleset,
					new String[]{"restrictionSet"});
			changeLogService.registerChangesBlocking("auto-restriction-ruleset", "edit", ruleset.getId(),
					authorGuid, null, null, clfc, Category.ACCESS, SubCategory.RULE_SET, 0, ruleset.getDomain().getName());
		} catch (Exception e) {
			String msg = "Changelog registration for auto-restriction ruleset restriction change failed";
			log.error(msg + " [ruleset=" + ruleset + ", domainRestrictionSet=" + domainRestrictionSet
					+ ", authorGuid=" + authorGuid + "] " + e.getMessage(), e);
			throw new Status500InternalServerErrorException(msg);
		}
		return ruleset;
	}

	@Transactional(rollbackOn = Exception.class)
	public AutoRestrictionRuleSet toggleEnabled(AutoRestrictionRuleSet ruleset, String authorGuid)
			throws Status500InternalServerErrorException {
		if (ruleset.isDeleted()) {
			throw new Status500InternalServerErrorException("Ruleset not found");
		}
		boolean enabled = ruleset.isEnabled();
		ruleset.setEnabled(!ruleset.isEnabled());
		ruleset.setLastUpdated(new Date());
		ruleset.setLastUpdatedBy(authorGuid);
		ruleset = ruleSetRepository.save(ruleset);
		try {
			ChangeLogFieldChange c = ChangeLogFieldChange.builder().field("enabled")
					.fromValue(String.valueOf(enabled)).toValue(String.valueOf(ruleset.isEnabled())).build();
			List<ChangeLogFieldChange> clfc = new ArrayList<>();
			clfc.add(c);
			String changeType = (ruleset.isEnabled()) ? "enable" : "disable";
			changeLogService.registerChangesBlocking("auto-restriction-ruleset", changeType, ruleset.getId(),
					authorGuid, null, null, clfc, Category.ACCESS, SubCategory.RULE_SET, 0, ruleset.getDomain().getName());
		} catch (Exception e) {
			String msg = "Changelog registration for auto-restriction ruleset toggle enabled failed";
			log.error(msg + " [ruleset=" + ruleset + ", authorGuid=" + authorGuid + "] " + e.getMessage(), e);
			throw new Status500InternalServerErrorException(msg);
		}

		return ruleset;
	}

	@Transactional(rollbackOn = Exception.class)
	public AutoRestrictionRuleSet deleteRuleset(AutoRestrictionRuleSet ruleset, String authorGuid)
			throws Status500InternalServerErrorException {
		if (ruleset.isDeleted()) {
			throw new Status500InternalServerErrorException("Ruleset not found");
		}
		AutoRestrictionRuleSet oldRuleset = new AutoRestrictionRuleSet();
		oldRuleset.setDeleted(false);
		oldRuleset.setEnabled(Boolean.valueOf(ruleset.isEnabled()));
		ruleset.setDeleted(true);
		ruleset.setEnabled(false);
		ruleset.setName(ruleset.getName() + "_deleted_" + new Date().getTime());
		ruleset.setLastUpdated(new Date());
		ruleset.setLastUpdatedBy(authorGuid);
		ruleset = ruleSetRepository.save(ruleset);
		try {
			List<ChangeLogFieldChange> clfc = changeLogService.compare(ruleset, oldRuleset,
					new String[]{"deleted", "name", "enabled"});
			changeLogService.registerChangesBlocking("auto-restriction-ruleset", "delete", ruleset.getId(),
					authorGuid, null, null, clfc, Category.ACCESS, SubCategory.RULE_SET, 0, ruleset.getDomain().getName());
		} catch (Exception e) {
			String msg = "Changelog registration for auto-restriction ruleset delete failed";
			log.error(msg + " [ruleset=" + ruleset + ", authorGuid=" + authorGuid + "] " + e.getMessage(), e);
			throw new Status500InternalServerErrorException(msg);
		}
		return ruleset;
	}

	@Transactional(rollbackOn = Exception.class)
	public AutoRestrictionRuleSet addRule(AutoRestrictionRuleSet ruleset, AutoRestrictionRule rule, String authorGuid)
			throws Status500InternalServerErrorException {
		if (ruleset.isDeleted()) {
			throw new Status500InternalServerErrorException("Ruleset not found");
		}
		AutoRestrictionRule existingFieldRule = ruleRepository.findByRulesetAndField(ruleset, rule.getField());
		if (existingFieldRule != null) {
			existingFieldRule.setDeleted(false);
			existingFieldRule.setEnabled(rule.isEnabled());
			existingFieldRule.setField(rule.getField());
			existingFieldRule.setValue(rule.getValue());
			existingFieldRule.setValue2(rule.getValue2());
			existingFieldRule.setEvent(rule.getEvent());
			existingFieldRule.setDelay(rule.getDelay());
			existingFieldRule.setOperator(rule.getOperator());
			rule = existingFieldRule;
		} else {
			rule.setRuleset(ruleset);
		}
		rule = ruleRepository.save(rule);
		try {
			List<ChangeLogFieldChange> clfc = changeLogService.compare(rule, new AutoRestrictionRule(),
					new String[]{"enabled", "field", "operator", "value", "value2"});
			changeLogService.registerChangesBlocking("auto-restriction-ruleset.rule", "create",
					ruleset.getId(), authorGuid, null, null, clfc, Category.ACCESS, SubCategory.RULE_SET, 0, ruleset.getDomain().getName());
		} catch (Exception e) {
			String msg = "Changelog registration for auto-restriction ruleset rule create failed";
			log.error(msg + " [ruleset=" + ruleset + ", rule=" + rule + ", authorGuid=" + authorGuid + "] " + e.getMessage(), e);
			throw new Status500InternalServerErrorException(msg);
		}
		ruleset.getRules().add(rule);
		ruleset.setLastUpdated(new Date());
		ruleset.setLastUpdatedBy(authorGuid);
		ruleset = ruleSetRepository.save(ruleset);
		return ruleset;
	}

	@Transactional(rollbackOn = Exception.class)
	public AutoRestrictionRuleSet updateRule(AutoRestrictionRuleSet ruleset, AutoRestrictionRule rule,
											 AutoRestrictionRule ruleUpdate, String authorGuid)
			throws Status500InternalServerErrorException {
		if (rule.getRuleset().isDeleted()) {
			throw new Status500InternalServerErrorException("Ruleset not found");
		}
		if (rule.isDeleted()) {
			throw new Status500InternalServerErrorException("Rule not found");
		}
		if (rule.getRuleset().getId().longValue() != ruleset.getId().longValue()) {
			throw new Status500InternalServerErrorException("Rule does not belong to ruleset");
		}
		AutoRestrictionRule oldRule = new AutoRestrictionRule();
		modelMapper.map(rule, oldRule);
		final long ruleId = rule.getId();
		ruleset.getRules().removeIf(r -> r.getId().longValue() == ruleId);
		rule.setEnabled(ruleUpdate.isEnabled());
		rule.setOperator(ruleUpdate.getOperator());
		rule.setValue(ruleUpdate.getValue());
		rule.setValue2(ruleUpdate.getValue2());
		rule.setEvent(ruleUpdate.getEvent());
		rule.setDelay(ruleUpdate.getDelay());
		rule = ruleRepository.save(rule);
		ruleset.getRules().add(rule);
		try {
			List<ChangeLogFieldChange> clfc = changeLogService.compare(rule, oldRule,
					new String[]{"enabled", "operator", "value", "value2"});
			changeLogService.registerChangesBlocking("auto-restriction-ruleset.rule", "edit",
					ruleset.getId(), authorGuid, "[rule.id=" + rule.getId() + ", rule.field=" + rule.getField() + "]",
					null, clfc, Category.ACCESS, SubCategory.RULE_SET, 0, ruleset.getDomain().getName());
		} catch (Exception e) {
			String msg = "Changelog registration for auto-restriction ruleset rule edit failed";
			log.error(msg + " [ruleset=" + ruleset + ", rule=" + rule + ", ruleUpdate=" + ruleUpdate + ", authorGuid=" + authorGuid + "] "
					+ e.getMessage(), e);
			throw new Status500InternalServerErrorException(msg);
		}
		ruleset.getRules().sort(Comparator.comparingLong(AutoRestrictionRule::getId));
		ruleset.setLastUpdated(new Date());
		ruleset.setLastUpdatedBy(authorGuid);
		ruleset = ruleSetRepository.save(ruleset);
		return ruleset;
	}

	@Transactional(rollbackOn = Exception.class)
	public AutoRestrictionRuleSet deleteRule(AutoRestrictionRuleSet ruleset, AutoRestrictionRule rule, String authorGuid)
			throws Status500InternalServerErrorException {
		if (rule.getRuleset().isDeleted()) {
			throw new Status500InternalServerErrorException("Ruleset not found");
		}
		if (rule.isDeleted()) {
			throw new Status500InternalServerErrorException("Rule not found");
		}
		if (rule.getRuleset().getId().longValue() != ruleset.getId().longValue()) {
			throw new Status500InternalServerErrorException("Rule does not belong to ruleset");
		}
		AutoRestrictionRule oldRule = new AutoRestrictionRule();
		modelMapper.map(rule, oldRule);
		final long ruleId = rule.getId();
		ruleset.getRules().removeIf(r -> r.getId().longValue() == ruleId);
		rule.setDeleted(true);
		rule.setEnabled(false);
		rule = ruleRepository.save(rule);
		try {
			List<ChangeLogFieldChange> clfc = changeLogService.compare(rule, oldRule,
					new String[]{"deleted", "enabled"});
			changeLogService.registerChangesBlocking("auto-restriction-ruleset.rule", "delete",
					ruleset.getId(), authorGuid, "[rule.id=" + rule.getId() + ", rule.field=" + rule.getField() + "]",
					null, clfc, Category.ACCESS, SubCategory.RULE_SET, 0, ruleset.getDomain().getName());
		} catch (Exception e) {
			String msg = "Changelog registration for auto-restriction ruleset rule delete failed";
			log.error(msg + " [ruleset=" + ruleset + ", rule=" + rule + ", authorGuid=" + authorGuid + "] "
					+ e.getMessage(), e);
			throw new Status500InternalServerErrorException(msg);
		}
		ruleset.setLastUpdated(new Date());
		ruleset.setLastUpdatedBy(authorGuid);
		ruleset = ruleSetRepository.save(ruleset);
		return ruleset;
	}

	public Response<ChangeLogs> getChangeLogs(Long id, String[] entities, int page) throws Exception {
		return changeLogService.listLimited(ChangeLogRequest.builder()
				.entityRecordId(id)
				.entities(entities)
				.page(page)
				.build()
		);
	}

	private Specification<AutoRestrictionRuleSet> addToSpec(final String[] anArrayOfStrings,
			Specification<AutoRestrictionRuleSet> spec,
			Function<String[], Specification<AutoRestrictionRuleSet>> predicateMethod) {
		if (anArrayOfStrings != null && anArrayOfStrings.length > 0) {
			Specification<AutoRestrictionRuleSet> localSpec = Specification.where(predicateMethod.apply(anArrayOfStrings));
			spec = (spec == null) ? localSpec : spec.and(localSpec);
			return spec;
		}
		return spec;
	}

	private Specification<AutoRestrictionRuleSet> addToSpec(final String aString,
			Specification<AutoRestrictionRuleSet> spec,
			Function<String, Specification<AutoRestrictionRuleSet>> predicateMethod) {
		if (aString != null && !aString.isEmpty()) {
			Specification<AutoRestrictionRuleSet> localSpec = Specification.where(predicateMethod.apply(aString));
			spec = (spec == null) ? localSpec : spec.and(localSpec);
			return spec;
		}
		return spec;
	}

	private Specification<AutoRestrictionRuleSet> addToSpec(final Date aDate, boolean addDay,
			Specification<AutoRestrictionRuleSet> spec,
			Function<Date, Specification<AutoRestrictionRuleSet>> predicateMethod) {
		if (aDate != null) {
			DateTime someDate = new DateTime(aDate);
			if (addDay) {
				someDate = someDate.plusDays(1).withTimeAtStartOfDay();
			} else {
				someDate = someDate.withTimeAtStartOfDay();
			}
			Specification<AutoRestrictionRuleSet> localSpec = Specification.where(predicateMethod.apply(someDate.toDate()));
			spec = (spec == null) ? localSpec : spec.and(localSpec);
			return spec;
		}
		return spec;
	}

	private Specification<AutoRestrictionRuleSet> addToSpec(final Boolean aBoolean,
			Specification<AutoRestrictionRuleSet> spec,
			Function<Boolean, Specification<AutoRestrictionRuleSet>> predicateMethod) {
		if (aBoolean != null) {
			Specification<AutoRestrictionRuleSet> localSpec = Specification.where(predicateMethod.apply(aBoolean));
			spec = (spec == null) ? localSpec : spec.and(localSpec);
			return spec;
		}
		return spec;
	}

	private AutoRestrictionRuleSet mapUpdatedByToAuthorFullName(AutoRestrictionRuleSet ruleSet, List<User> users) {
		Optional<User> result = users.stream().filter(u -> u.getGuid().equalsIgnoreCase(ruleSet.getLastUpdatedBy())).findFirst();

		if(result.isPresent()) {
			User user = result.get();
			ruleSet.setLastUpdatedBy(String.format("%s %s", user.getFirstName(), user.getLastName()));
		}
		else {
			ruleSet.setLastUpdatedBy(User.SYSTEM_FULL_NAME);
		}


		return ruleSet;
	}

	private List<User> getUsersFromRuleSets(List<AutoRestrictionRuleSet> ruleSets) throws Exception {
		List<String> guids = ruleSets.stream().map(rs -> rs.getLastUpdatedBy()).collect(Collectors.toList());
		return externalUserService.getUsersByGuids(guids);
	}

}

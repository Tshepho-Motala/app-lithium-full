package lithium.service.cashier.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.cashier.client.frontend.DoMachineState;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.data.entities.AutoWithdrawalRuleSet;
import lithium.service.cashier.data.entities.AutoWithdrawalRuleSetProcess;
import lithium.service.cashier.data.entities.AutoWithdrawalRuleSettings;
import lithium.service.cashier.data.entities.Domain;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.objects.AutoWithdrawalRuleOperator;
import lithium.service.cashier.data.repositories.AutoWithdrawalRuleRepository;
import lithium.service.cashier.data.repositories.AutoWithdrawalRuleSetProcessRepository;
import lithium.service.cashier.data.repositories.AutoWithdrawalRuleSetRepository;
import lithium.service.cashier.data.repositories.AutoWithdrawalRuleSettingsRepository;
import lithium.service.cashier.data.repositories.TransactionRepository;
import lithium.service.cashier.data.specifications.AutoWithdrawalRulesetSpecification;
import lithium.service.cashier.machine.DoMachine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
public class AutoWithdrawalRulesetService {
	@Autowired
	private AutoWithdrawalRuleRepository ruleRepository;
	@Autowired
	private AutoWithdrawalRuleSetRepository ruleSetRepository;
	@Autowired
	private AutoWithdrawalRuleSetProcessRepository ruleSetProcessRepository;
	@Autowired
	private ChangeLogService changeLogService;
	@Autowired
	private DomainService domainService;
	@Autowired
	private MessageSource messages;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private WebApplicationContext beanContext;
	@Autowired
	private AutoWithdrawalRuleSettingsRepository settingsRepository;

	public List<AutoWithdrawalRuleOperator> ruleOperators(Locale locale) {
		List<AutoWithdrawalRuleOperator> operators = Arrays
			.stream(lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleOperator.values())
			.map(operator -> {
				return new AutoWithdrawalRuleOperator(operator.id(), operator.operator(),
					messages.getMessage("SERVICE-CASHIER.AUTOWITHDRAWALS.RULESETS.RULE.OPERATOR." +
						operator.operator(),null, locale));
			})
			.collect(Collectors.toList());
		return operators;
	}

	public AutoWithdrawalRuleSet findById(Long id) {
		return ruleSetRepository.findOne(id);
	}

	public Page<AutoWithdrawalRuleSet> find(String[] domains, Boolean enabled, String name, Date lastUpdatedStart,
											Date lastUpdatedEnd, String searchValue, Pageable pageable) {
		Specification<AutoWithdrawalRuleSet> spec = null;
		spec = addToSpec(false, spec, AutoWithdrawalRulesetSpecification::deleted);
		spec = addToSpec(enabled, spec, AutoWithdrawalRulesetSpecification::enabled);
		spec = addToSpec(name, spec, AutoWithdrawalRulesetSpecification::nameStartsWith);
		spec = addToSpec(lastUpdatedStart, false, spec, AutoWithdrawalRulesetSpecification::lastUpdatedStart);
		spec = addToSpec(lastUpdatedEnd, true, spec, AutoWithdrawalRulesetSpecification::lastUpdatedEnd);
		spec = addToSpec(domains, spec, AutoWithdrawalRulesetSpecification::domains);
		spec = addToSpec(searchValue, spec, AutoWithdrawalRulesetSpecification::any);
		Page<AutoWithdrawalRuleSet> result = ruleSetRepository.findAll(spec, pageable);
		return result;
	}

	@Transactional(rollbackOn=Exception.class)
	public AutoWithdrawalRuleSet create(String domainName, AutoWithdrawalRuleSet ruleset, String authorGuid)
			throws Status500InternalServerErrorException {
		Domain domain = domainService.findOrCreateDomain(domainName);
		checkRuleSetNameForDuplicates(ruleset.getName(), domain);
		ruleset.setDomain(domain);
		ruleset.setLastUpdated(new Date());
		ruleset.setLastUpdatedBy(authorGuid);
		ruleset = ruleSetRepository.save(ruleset);
		for (AutoWithdrawalRule rule: ruleset.getRules()) {
			rule.setRuleset(ruleset);
			saveRuleSettings(rule.getSettings(), ruleRepository.save(rule));
		}
		try {
			List<ChangeLogFieldChange> clfc = changeLogService.compare(ruleset, new AutoWithdrawalRuleSet(),
				new String[] {"domain", "name", "enabled", "lastUpdated", "lastUpdatedBy", "rules"});
			changeLogService.registerChangesBlocking("auto-withdrawal-ruleset", "create", ruleset.getId(),
				authorGuid, null, null, clfc, Category.ACCESS, SubCategory.RULE_SET, 0, domainName);
		} catch (Exception e) {
			String msg = "Changelog registration for auto-withdrawal ruleset create failed";
			log.error(msg + " [ruleset="+ruleset+", authorGuid="+authorGuid+"] " + e.getMessage(), e);
			throw new Status500InternalServerErrorException(msg);
		}
		return ruleset;
	}

	private AutoWithdrawalRule saveRuleSettings(List<AutoWithdrawalRuleSettings> settings, AutoWithdrawalRule rule) {
		if (settings == null || settings.isEmpty()) {
			return rule;
		}
		List<AutoWithdrawalRuleSettings> storedSettings = settingsRepository.findByRule(rule);
		for (AutoWithdrawalRuleSettings setting: settings) {
			Optional<AutoWithdrawalRuleSettings> stored = storedSettings.stream().filter(stor -> stor.getKey().equals(setting.getKey())).findFirst();
			if (stored.isPresent()) {
				AutoWithdrawalRuleSettings storedSetting = stored.get();
				setting.setId(storedSetting.getId());
				setting.setVersion(storedSetting.getVersion());
			}
			setting.setRule(rule);
		}
		settingsRepository.saveAll(settings);
		rule.setSettings(settings);
		return rule;
	}

	@Transactional(rollbackOn = Exception.class)
	public AutoWithdrawalRuleSet updateRuleSet(AutoWithdrawalRuleSet newRuleSet, String authorGuid)
			throws Status500InternalServerErrorException {

		AutoWithdrawalRuleSet existingRuleSet = findById(newRuleSet.getId());

		if (existingRuleSet == null || existingRuleSet.isDeleted()) {
			throw new Status500InternalServerErrorException("Ruleset not found by Id:" + newRuleSet.getId());
		}

		if (newRuleSet.isDelayedStart() && newRuleSet.getDelay() == null) {
			throw new Status500InternalServerErrorException("Missing delay value for delayed ruleSet :" + existingRuleSet.getId());
		}

		AutoWithdrawalRuleSet beforeChanges = new AutoWithdrawalRuleSet();
		beforeChanges.setName(existingRuleSet.getName());
		beforeChanges.setDelay(existingRuleSet.getDelay());
		beforeChanges.setDelayedStart(existingRuleSet.isDelayedStart());
		beforeChanges.setEnabled(existingRuleSet.isEnabled());

		if (newRuleSet.getName() != null && !existingRuleSet.getName().contentEquals(newRuleSet.getName())) {
			checkRuleSetNameForDuplicates(newRuleSet.getName(), existingRuleSet.getDomain());
			existingRuleSet.setName(newRuleSet.getName());
		}

		existingRuleSet.setDelayedStart(newRuleSet.isDelayedStart());
		existingRuleSet.setDelay(newRuleSet.isDelayedStart() ? newRuleSet.getDelay() : null);

		existingRuleSet.setEnabled(newRuleSet.isEnabled());

		existingRuleSet.setLastUpdated(new Date());
		existingRuleSet.setLastUpdatedBy(authorGuid);
		newRuleSet = ruleSetRepository.save(existingRuleSet);

		try {
			List<ChangeLogFieldChange> clfc = changeLogService.compare(newRuleSet, beforeChanges,
					new String[]{"name", "delay", "delayedStart", "enabled"});
			changeLogService.registerChangesBlocking("auto-withdrawal-ruleset", "edit", newRuleSet.getId(),
					authorGuid, null, null, clfc, Category.ACCESS, SubCategory.RULE_SET, 0, newRuleSet.getDomain().getName());
		} catch (Exception e) {
			String msg = "Changelog registration for auto-withdrawal ruleset name change failed";
			log.error(msg + " [ruleset=" + newRuleSet + ", authorGuid=" + authorGuid + "] " + e.getMessage(),
					e);
			throw new Status500InternalServerErrorException(msg);
		}
		return newRuleSet;
	}

	@Transactional(rollbackOn=Exception.class)
	public AutoWithdrawalRuleSet changeName(AutoWithdrawalRuleSet ruleset, String newName, String authorGuid)
			throws Status500InternalServerErrorException {
		if (ruleset.isDeleted()) {
			throw new Status500InternalServerErrorException("Ruleset not found");
		}
        checkRuleSetNameForDuplicates(newName, ruleset.getDomain());
		AutoWithdrawalRuleSet oldRuleset = new AutoWithdrawalRuleSet();
		oldRuleset.setName(String.valueOf(ruleset.getName()));
		ruleset.setName(newName);
		ruleset.setLastUpdated(new Date());
		ruleset.setLastUpdatedBy(authorGuid);
		ruleset = ruleSetRepository.save(ruleset);
		try {
			List<ChangeLogFieldChange> clfc = changeLogService.compare(ruleset, oldRuleset,
				new String[] {"name"});
			changeLogService.registerChangesBlocking("auto-withdrawal-ruleset", "edit", ruleset.getId(),
				authorGuid, null, null, clfc, Category.ACCESS, SubCategory.RULE_SET, 0, ruleset.getDomain().getName());
		} catch (Exception e) {
			String msg = "Changelog registration for auto-withdrawal ruleset name change failed";
			log.error(msg + " [ruleset="+ruleset+", newName="+newName+", authorGuid="+authorGuid+"] " + e.getMessage(),
				e);
			throw new Status500InternalServerErrorException(msg);
		}
		return ruleset;
	}

	@Transactional(rollbackOn=Exception.class)
	public AutoWithdrawalRuleSet changeDelay(AutoWithdrawalRuleSet ruleset, boolean delayedStart, Long newDelay, String authorGuid)
			throws Status500InternalServerErrorException {
		if (ruleset.isDeleted()) {
			throw new Status500InternalServerErrorException("Ruleset not found");
		}

		AutoWithdrawalRuleSet oldRuleset = new AutoWithdrawalRuleSet();
		oldRuleset.setName(String.valueOf(ruleset.getName()));
		ruleset.setDelayedStart(delayedStart);
		ruleset.setDelay(delayedStart ? newDelay : null);
		ruleset.setLastUpdated(new Date());
		ruleset.setLastUpdatedBy(authorGuid);
		ruleset = ruleSetRepository.save(ruleset);
		try {
			List<ChangeLogFieldChange> clfc = changeLogService.compare(ruleset, oldRuleset,
					new String[] {"delay", "delayedStart"});
			changeLogService.registerChangesBlocking("auto-withdrawal-ruleset", "edit", ruleset.getId(),
					authorGuid, null, null, clfc, Category.ACCESS, SubCategory.RULE_SET, 0, ruleset.getDomain().getName());
		} catch (Exception e) {
			String msg = "Changelog registration for auto-withdrawal ruleset delay change failed";
			log.error(msg + " [ruleset="+ruleset+", newDelay="+newDelay+", newDelayedStart=" + delayedStart + ", authorGuid="+authorGuid+"] " + e.getMessage(),
					e);
			throw new Status500InternalServerErrorException(msg);
		}
		return ruleset;
	}

	@Transactional(rollbackOn=Exception.class)
	public AutoWithdrawalRuleSet toggleEnabled(AutoWithdrawalRuleSet ruleset, String authorGuid)
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
			changeLogService.registerChangesBlocking("auto-withdrawal-ruleset", changeType, ruleset.getId(),
				authorGuid, null, null, clfc, Category.ACCESS, SubCategory.RULE_SET, 0, ruleset.getDomain().getName());
		} catch (Exception e) {
			String msg = "Changelog registration for auto-withdrawal ruleset toggle enabled failed";
			log.error(msg + " [ruleset="+ruleset+", authorGuid="+authorGuid+"] " + e.getMessage(), e);
			throw new Status500InternalServerErrorException(msg);
		}

		return ruleset;
	}

	@Transactional(rollbackOn=Exception.class)
	public AutoWithdrawalRuleSet deleteRuleset(AutoWithdrawalRuleSet ruleset, String authorGuid)
			throws Status500InternalServerErrorException {
		if (ruleset.isDeleted()) {
			throw new Status500InternalServerErrorException("Ruleset not found");
		}
		AutoWithdrawalRuleSet oldRuleset = new AutoWithdrawalRuleSet();
		oldRuleset.setDeleted(false);
		oldRuleset.setEnabled(Boolean.valueOf(ruleset.isEnabled()));
		ruleset.setDeleted(true);
		ruleset.setEnabled(false);
		ruleset.setName(ruleset.getName()+"_deleted_"+new Date().getTime());
		ruleset.setLastUpdated(new Date());
		ruleset.setLastUpdatedBy(authorGuid);
		ruleset = ruleSetRepository.save(ruleset);
		try {
			List<ChangeLogFieldChange> clfc = changeLogService.compare(ruleset, oldRuleset,
				new String[] {"deleted", "name", "enabled"});
			changeLogService.registerChangesBlocking("auto-withdrawal-ruleset", "delete", ruleset.getId(),
				authorGuid, null, null, clfc, Category.ACCESS, SubCategory.RULE_SET, 0, ruleset.getDomain().getName());
		} catch (Exception e) {
			String msg = "Changelog registration for auto-withdrawal ruleset delete failed";
			log.error(msg + " [ruleset="+ruleset+", authorGuid="+authorGuid+"] " + e.getMessage(), e);
			throw new Status500InternalServerErrorException(msg);
		}
		return ruleset;
	}

	@Transactional(rollbackOn=Exception.class)
	public AutoWithdrawalRuleSet addRule(AutoWithdrawalRuleSet ruleset, AutoWithdrawalRule rule, String authorGuid)
			throws Status500InternalServerErrorException {
		if (ruleset.isDeleted()) {
			throw new Status500InternalServerErrorException("Ruleset not found");
		}
		AutoWithdrawalRule existingFieldRule = ruleRepository.findByRulesetAndField(ruleset, rule.getField());
		final List<AutoWithdrawalRuleSettings> settings = rule.getSettings();
		if (existingFieldRule != null) {
			existingFieldRule.setDeleted(false);
			existingFieldRule.setEnabled(rule.isEnabled());
			existingFieldRule.setField(rule.getField());
			existingFieldRule.setValue(rule.getValue());
			existingFieldRule.setValue2(rule.getValue2());
			existingFieldRule.setOperator(rule.getOperator());
			rule = existingFieldRule;
		} else {
			rule.setRuleset(ruleset);
		}
		rule = saveRuleSettings(settings, ruleRepository.save(rule));
		try {
			List<ChangeLogFieldChange> clfc = changeLogService.compare(rule, new AutoWithdrawalRule(),
				new String[] {"enabled", "field", "operator", "value", "value2"});
			changeLogService.registerChangesBlocking("auto-withdrawal-ruleset.rule", "create",
				ruleset.getId(), authorGuid, null, null, clfc, Category.ACCESS, SubCategory.RULE_SET, 0, ruleset.getDomain().getName());
		} catch (Exception e) {
			String msg = "Changelog registration for auto-withdrawal ruleset rule create failed";
			log.error(msg + " [ruleset="+ruleset+", rule="+rule+", authorGuid="+authorGuid+"] " + e.getMessage(), e);
			throw new Status500InternalServerErrorException(msg);
		}
		ruleset.getRules().add(rule);
		ruleset.setLastUpdated(new Date());
		ruleset.setLastUpdatedBy(authorGuid);
		ruleset = ruleSetRepository.save(ruleset);
		return ruleset;
	}

	@Transactional(rollbackOn=Exception.class)
	public AutoWithdrawalRuleSet updateRule(AutoWithdrawalRuleSet ruleset, AutoWithdrawalRule rule,
											AutoWithdrawalRule ruleUpdate, String authorGuid)
			throws Status500InternalServerErrorException {
		if (rule.getRuleset().isDeleted()) {
			throw new Status500InternalServerErrorException("Ruleset not found");
		}
		if (rule.isDeleted()) {
			throw new Status500InternalServerErrorException("Rule not found");
		}
		AutoWithdrawalRule oldRule = new AutoWithdrawalRule();
		modelMapper.map(rule, oldRule);
		final long ruleId = rule.getId();
		ruleset.getRules().removeIf(r -> r.getId().longValue() == ruleId);
		rule.setEnabled(ruleUpdate.isEnabled());
		rule.setOperator(ruleUpdate.getOperator());
		rule.setValue(ruleUpdate.getValue());
		rule.setValue2(ruleUpdate.getValue2());
		rule = saveRuleSettings(ruleUpdate.getSettings(), ruleRepository.save(rule));
		ruleset.getRules().add(rule);
		try {
			List<ChangeLogFieldChange> clfc = changeLogService.compare(rule, oldRule,
				new String[] {"enabled", "operator", "value", "value2"});
			changeLogService.registerChangesBlocking("auto-withdrawal-ruleset.rule", "edit",
				ruleset.getId(), authorGuid, "[rule.id="+rule.getId()+", rule.field="+rule
					.getField()+"]",
				null, clfc, Category.ACCESS, SubCategory.RULE_SET, 0, ruleset.getDomain().getName());
		} catch (Exception e) {
			String msg = "Changelog registration for auto-withdrawal ruleset rule edit failed";
			log.error(msg + " [ruleset="+ruleset+", rule="+rule+", ruleUpdate="+ruleUpdate+", authorGuid="+authorGuid+"] "
				+ e.getMessage(), e);
			throw new Status500InternalServerErrorException(msg);
		}
		ruleset.getRules().sort(Comparator.comparingLong(AutoWithdrawalRule::getId));
		ruleset.setLastUpdated(new Date());
		ruleset.setLastUpdatedBy(authorGuid);
		ruleset = ruleSetRepository.save(ruleset);
		return ruleset;
	}

	@Transactional(rollbackOn=Exception.class)
	public AutoWithdrawalRuleSet deleteRule(AutoWithdrawalRuleSet ruleset, AutoWithdrawalRule rule, String authorGuid)
			throws Status500InternalServerErrorException {
		if (rule.getRuleset().isDeleted()) {
			throw new Status500InternalServerErrorException("Ruleset not found");
		}
		if (rule.isDeleted()) {
			throw new Status500InternalServerErrorException("Rule not found");
		}
		AutoWithdrawalRule oldRule = new AutoWithdrawalRule();
		modelMapper.map(rule, oldRule);
		final long ruleId = rule.getId();
		ruleset.getRules().removeIf(r -> r.getId().longValue() == ruleId);
		rule.setDeleted(true);
		rule.setEnabled(false);
		rule = ruleRepository.save(rule);
		try {
			List<ChangeLogFieldChange> clfc = changeLogService.compare(rule, oldRule,
				new String[] {"deleted", "enabled"});
			changeLogService.registerChangesBlocking("auto-withdrawal-ruleset.rule", "delete",
				ruleset.getId(), authorGuid, "[rule.id="+rule.getId()+", rule.field="+rule.getField()+"]",
				null, clfc, Category.ACCESS, SubCategory.RULE_SET, 0, ruleset.getDomain().getName());
		} catch (Exception e) {
			String msg = "Changelog registration for auto-withdrawal ruleset rule delete failed";
			log.error(msg + " [ruleset="+ruleset+", rule="+rule+", authorGuid="+authorGuid+"] "
				+ e.getMessage(), e);
			throw new Status500InternalServerErrorException(msg);
		}
		ruleset.setLastUpdated(new Date());
		ruleset.setLastUpdatedBy(authorGuid);
		ruleset = ruleSetRepository.save(ruleset);
		return ruleset;
	}

	@Transactional(rollbackOn=Exception.class)
	public AutoWithdrawalRuleSetProcess queueAutoWithdrawalRulesetProcess(AutoWithdrawalRuleSet ruleset,
																		  String authorGuid)
			throws Status500InternalServerErrorException {
		AutoWithdrawalRuleSetProcess process = ruleSetProcessRepository.save(
			AutoWithdrawalRuleSetProcess.builder()
			.ruleset(ruleset)
			.createdBy(userService.findOrCreate(authorGuid))
			.build()
		);
		try {
			List<ChangeLogFieldChange> clfc = changeLogService.compare(process, new AutoWithdrawalRuleSetProcess(),
				new String[] {"created", "createdBy", "ruleset"});
			changeLogService.registerChangesBlocking("auto-withdrawal-ruleset.process", "create",
				ruleset.getId(), authorGuid, null, null, clfc, Category.ACCESS, SubCategory.RULE_SET, 0, ruleset.getDomain().getName());
		} catch (Exception e) {
			String msg = "Changelog registration for auto-withdrawal ruleset process queued failed";
			log.error(msg + " [ruleset="+ruleset+", authorGuid="+authorGuid+"] " + e.getMessage(), e);
			throw new Status500InternalServerErrorException(msg);
		}
		return process;
	}

	public Response<ChangeLogs> getChangeLogs(Long id, String[] entities, int page) throws Exception {
		return changeLogService.listLimited(ChangeLogRequest.builder()
			.entityRecordId(id)
			.entities(entities)
			.page(page)
			.build()
		);
	}

	private Specification<AutoWithdrawalRuleSet> addToSpec(final String[] anArrayOfStrings, Specification<AutoWithdrawalRuleSet> spec,
															Function<String[], Specification<AutoWithdrawalRuleSet>> predicateMethod) {
		if (anArrayOfStrings != null && anArrayOfStrings.length > 0) {
			Specification<AutoWithdrawalRuleSet> localSpec = Specification.where(predicateMethod.apply(anArrayOfStrings));
			spec = (spec == null) ? localSpec : spec.and(localSpec);
			return spec;
		}
		return spec;
	}

	private Specification<AutoWithdrawalRuleSet> addToSpec(final String aString, Specification<AutoWithdrawalRuleSet> spec,
															Function<String, Specification<AutoWithdrawalRuleSet>> predicateMethod) {
		if (aString != null && !aString.isEmpty()) {
			Specification<AutoWithdrawalRuleSet> localSpec = Specification.where(predicateMethod.apply(aString));
			spec = (spec == null) ? localSpec : spec.and(localSpec);
			return spec;
		}
		return spec;
	}

	private Specification<AutoWithdrawalRuleSet> addToSpec(final Date aDate, boolean addDay,
															Specification<AutoWithdrawalRuleSet> spec,
															Function<Date, Specification<AutoWithdrawalRuleSet>> predicateMethod) {
		if (aDate != null) {
			DateTime someDate = new DateTime(aDate);
			if (addDay) {
				someDate = someDate.plusDays(1).withTimeAtStartOfDay();
			} else {
				someDate = someDate.withTimeAtStartOfDay();
			}
			Specification<AutoWithdrawalRuleSet> localSpec = Specification.where(predicateMethod.apply(someDate.toDate()));
			spec = (spec == null) ? localSpec : spec.and(localSpec);
			return spec;
		}
		return spec;
	}

	private Specification<AutoWithdrawalRuleSet> addToSpec(final Boolean aBoolean, Specification<AutoWithdrawalRuleSet> spec,
															Function<Boolean, Specification<AutoWithdrawalRuleSet>> predicateMethod) {
		if (aBoolean != null) {
			Specification<AutoWithdrawalRuleSet> localSpec = Specification.where(predicateMethod.apply(aBoolean));
			spec = (spec == null) ? localSpec : spec.and(localSpec);
			return spec;
		}
		return spec;
	}

	@Async
	public void processRuleset(AutoWithdrawalRuleSetProcess toProcess) {
		try {
			toProcess.setStarted(new Date());
			toProcess = ruleSetProcessRepository.save(toProcess);

			AutoWithdrawalRuleSet ruleset = toProcess.getRuleset();
			if (!ruleset.isEnabled()) {
				toProcess.setCompleted(new Date());
				toProcess = ruleSetProcessRepository.save(toProcess);
				return;
			}

			int page = 0;
			boolean process = true;
			while (process) {
				Pageable pageRequest = PageRequest.of(page, 1000);
				Page<Transaction> pageResult = transactionRepository.findByTransactionTypeAndStatusCode(
					TransactionType.WITHDRAWAL, DoMachineState.WAITFORAPPROVAL.name(), pageRequest);
				log.debug("Found " + pageResult.getContent().size() + " entries."
					+ " Page " + pageResult.getNumber() + " of " + pageResult.getTotalPages());
				for (Transaction tran: pageResult.getContent()) {
					// No need to drop entire process if a single transaction fails.
					// Just skip and carry on.
					try {
						DoMachine machine = beanContext.getBean(DoMachine.class);
						machine.autoApprove(ruleset, tran.getId(), toProcess.getCreatedBy().guid(), toProcess.getCreated());
					} catch (Exception e) {
						log.warn("Failed to perform auto-approval requirement check on tran " + tran.getId() + " ["
							+ e.getMessage() + "]");
					}
				}
				page++;
				if (!pageResult.hasNext()) process = false;
			}

			toProcess.setCompleted(new Date());
			ruleSetProcessRepository.save(toProcess);
		} catch (Exception e) {
			log.error("An error occurred while processing autowithdrawal ruleset [" + toProcess + "] "
				+ e.getMessage(), e);
			toProcess.setCompleted(new Date());
			ruleSetProcessRepository.save(toProcess);
		}
	}

	public String collectRulesToString(List<AutoWithdrawalRuleSet> ruleSets) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		List<String> rulesStr = new ArrayList<>();
		for (AutoWithdrawalRuleSet ruleSet : ruleSets) {
			rulesStr.add(mapper.writeValueAsString(ruleSet));
		}

		return rulesStr.stream()
				.map(strRuleSet -> String.valueOf(strRuleSet))
				.collect(Collectors.joining(",", "[", "]"));
	}

	public List<AutoWithdrawalRuleSet> importFromFile(final MultipartFile multipartFile) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		List<AutoWithdrawalRuleSet> ruleSets = new ArrayList<>();
		InputStream inputStream = multipartFile.getInputStream();
		String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

		JSONArray jsonArray = new JSONArray(result);

		for (int i = 0; i < jsonArray.length(); i++) {
			ruleSets.add(mapper.readValue(jsonArray.get(i).toString(), AutoWithdrawalRuleSet.class));
		}

		return ruleSets;
	}

	private void checkRuleSetNameForDuplicates(String newName, Domain domain) throws Status500InternalServerErrorException {
		if (findByDomainAndName(domain, newName) != null) {
			throw new Status500InternalServerErrorException("A ruleset with name: " + newName +" already exists in the domain:" + domain.getName());
		}
	}

	private AutoWithdrawalRuleSet findByDomainAndName(Domain domain, String name) {
		return ruleSetRepository.findByDomainAndName(domain, name);
	}
}





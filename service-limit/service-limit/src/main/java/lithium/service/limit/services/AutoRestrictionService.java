
package lithium.service.limit.services;


import java.util.ArrayList;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.accounting.objects.Period;
import lithium.service.cashier.client.CashierInternalClient;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.EcosystemRelationshipTypes;
import lithium.service.limit.data.entities.AutoRestrictionRule;
import lithium.service.limit.data.entities.AutoRestrictionRuleSet;
import lithium.service.limit.data.entities.UserRestrictionSet;
import lithium.service.limit.data.entities.VerificationStatus;
import lithium.service.limit.data.repositories.VerificationStatusRepository;
import lithium.service.limit.enums.AutoRestrictionRuleOperator;
import lithium.service.limit.enums.AutoRestrictionRuleSetOutcome;
import lithium.service.limit.objects.AutoRestrictionRuleSetResult;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.util.ObjectUtils;

import static lithium.service.limit.enums.AutoRestrictionRuleOperator.EQUALS;

@Service
@Slf4j
public class AutoRestrictionService {
	@Autowired private AccountingService accountingService;
	@Autowired private CachingDomainClientService cachingDomainClientService;
	@Autowired private AutoRestrictionRulesetService service;
	@Autowired private UserApiInternalClientService userApiInternalClientService;
	@Autowired private UserRestrictionService userRestrictionService;
    @Autowired private VerificationStatusRepository verificationStatusRepository;
	@Autowired private LithiumServiceClientFactory servicesFactory;
	@Autowired private CashierInternalClientService cashierService;
	@Autowired private AutoRestrictionPlayerCommService autoRestrictionPlayerCommService;


	@TimeThisMethod
	public void processAutoRestrictionRulesets(String userGuid) {
		processAutoRestrictionRulesets(userGuid, false, false);
	}

	@TimeThisMethod
	public void processAutoRestrictionRulesets(String userGuid, boolean skipLift, boolean skipPlace) {
		log.debug("AutoRestrictionService.processAutoRestrictionRulesets [userGuid="+userGuid+"]");

		User user = null;
		try {
			user = userApiInternalClientService.getUserByGuid(userGuid);
		} catch (UserClientServiceFactoryException | Exception e) {
			log.error("Auto-restriction rulesets could not be checked. Could not resolve user"
				+ " [userGuid="+userGuid+"]", e);
			return;
		}

		int page = 0;
		boolean process = true;
		DateTime now = DateTime.now().withMillisOfSecond(0);
		while (process) {
			Pageable pageRequest = PageRequest.of(page, 10, Sort.Direction.ASC, new String[] { "id" });
			Page<AutoRestrictionRuleSet> pageResult = service.find(new String[] { user.getDomain().getName() },
				true, null, null,null, null, pageRequest);
			log.debug("Found " + pageResult.getContent().size() + " entries."
				+ " Page " + pageResult.getNumber() + " of " + pageResult.getTotalPages());

			for (AutoRestrictionRuleSet ruleset: pageResult.getContent()) {

				if(ruleset.isSkipTestUser() && user.getTestAccount()){
					continue;
				}

				if((skipLift && AutoRestrictionRuleSetOutcome.LIFT.outcome().equals(ruleset.getOutcome().outcome())) ||
						(skipPlace && AutoRestrictionRuleSetOutcome.PLACE.outcome().equals(ruleset.getOutcome().outcome()))) {
					continue;
				}
				AutoRestrictionRuleSetResult result = AutoRestrictionRuleSetResult.builder()
						.outcome(ruleset.getOutcome())
						.trace(new StringBuilder())
						.result(true)
						.createdOn(now)
						.restrictionSet(ruleset.getRestrictionSet())
						.user(user)
						.rootOnly(ruleset.isRootOnly())
						.allEcosystem(ruleset.isAllEcosystem())
						.build();
				processResult(checkRuleset(ruleset, result));
			}

			page++;
			if (!pageResult.hasNext()) process = false;
		}
	}

	private void processResult(AutoRestrictionRuleSetResult result) {

		if (!result.isResult()) {
			return;
		}

		List<User> userList = getUsers(result);
		userList.forEach(user -> {
			switch (result.getOutcome()) {
				case PLACE:
					try {
						DateTime activeFrom = result.getActiveFrom() == null ? result.getCreatedOn() : result.getActiveFrom();
						userRestrictionService.place(user.guid(), result.getRestrictionSet(), "Auto-Restriction Process",
								result.getTrace().toString(), user.getId(),  null, activeFrom, null, result.getCreatedOn(), null);
					} catch (Exception e) {
						log.error("Restriction could not be placed on user account via auto-restriction process"
								+ " [userGuid="+user.guid()+", activeFrom=" + result.getActiveFrom() + ", set="+result.getRestrictionSet()+"]" + e.getMessage(), e);
					}
					break;
				case LIFT:
					try {
						userRestrictionService.lift(user.guid(), result.getRestrictionSet(), "Auto-Restriction Process",
								result.getTrace().toString(), user.getId(),  null, result.getActiveTo(), result.getCreatedOn());
					} catch (Exception e) {
						log.error("Restriction could not be lifted from user account via auto-restriction process"
								+ " [userGuid="+user.guid()+", activeTo=" + result.getActiveTo() + ", set="+result.getRestrictionSet()+"]" + e.getMessage(), e);
					}
					break;
				default: log.error("Invalid, unhandled auto-restriction outcome [outcome="+result.getOutcome()+"]");
			}
		});
	}

	private List<User> getUsers(AutoRestrictionRuleSetResult result) {
		List<User> userList = new ArrayList<>();

		// These fields should be hidden on LBO whenever a domain is selected which is not part of ecosystem
		if (!result.isRootOnly() && !result.isAllEcosystem()) {
			userList.add(result.getUser());
		} else {
			if (cachingDomainClientService.isDomainInAnyEcosystem(result.getUser().getDomain().getName())) {

				//When rootOnly is selected on LBO, then allEcosystem does not apply, and vice versa
				if(result.isRootOnly()) {
					try {
						User rootUser = userApiInternalClientService.getLinkedEcosystemUserGuid(
								result.getUser().getGuid(), EcosystemRelationshipTypes.ECOSYSTEM_ROOT);
						if (!ObjectUtils.isEmpty(rootUser)) {
							userList.add(rootUser);
						}
					} catch (Status500InternalServerErrorException e) {
						log.error("Unable to retrieve the linked root ecosystem user for auto-restriction rootOnly processing"
								+ " [userGuid="+ result.getUser().guid()  + ", set="+ result.getRestrictionSet()+"]" + e.getMessage(), e);
					}
				} else if (result.isAllEcosystem()) {

					userList.add(result.getUser());

					if (cachingDomainClientService.isDomainNameOfEcosystemRootType(
							result.getUser().getDomain().getName())) {
						try {
							User exclusiveUser = userApiInternalClientService.getLinkedEcosystemUserGuid(
									result.getUser().getGuid(), EcosystemRelationshipTypes.ECOSYSTEM_MUTUALLY_EXCLUSIVE);
							if (!ObjectUtils.isEmpty(exclusiveUser)) {
								userList.add(exclusiveUser);
							}
						} catch (Status500InternalServerErrorException e) {
							log.error("Unable to retrieve the linked exclusive ecosystem user for auto-restriction allEcosystem processing"
									+ " [userGuid="+ result.getUser().guid()  + ", set="+ result.getRestrictionSet()+"]" + e.getMessage(), e);
						}
					} else {

						try {
							User rootUser = userApiInternalClientService.getLinkedEcosystemUserGuid(
									result.getUser().getGuid(), EcosystemRelationshipTypes.ECOSYSTEM_ROOT);
							if (!ObjectUtils.isEmpty(rootUser)) {
								userList.add(rootUser);
							}
						} catch (Status500InternalServerErrorException e) {
							log.error("Unable to retrieve the linked root ecosystem user for auto-restriction allEcosystem processing"
									+ " [userGuid="+ result.getUser().guid()  + ", set="+ result.getRestrictionSet()+"]" + e.getMessage(), e);
						}
					}
				}
			}
		}
		return userList;
	}

	private AutoRestrictionRuleSetResult checkRuleset(AutoRestrictionRuleSet ruleset, AutoRestrictionRuleSetResult result) {
		List<AutoRestrictionRule> rules = ruleset.getRules()
		.stream()
		.filter(r -> r.isEnabled())
		.collect(Collectors.toList());

		if (rules.isEmpty()) {
			log.debug("Ruleset has no enabled rules and thus it is not considered"
				+ " in auto-restriction outcome processing [ruleset="+ruleset+"]");
			result.setResult(false);
			return result;
		}

		log.debug("Checking " + rules.size() + " rules");
		SW.start("checkingRuleset_"+ruleset.getDomain().getName()+"_"+ruleset.getName());
		checkRules(rules, result);
		SW.stop();
		return result;
	}

	private AutoRestrictionRuleSetResult checkRules(List<AutoRestrictionRule> rules, AutoRestrictionRuleSetResult result) {

		String defaultDomainCurrency = null;


		UserRestrictionSet userRestrictionSet;


		for (AutoRestrictionRule rule: rules) {

			switch (rule.getField()) {

				case DAYS_SINCE_REGISTRATION:
					long daysSinceReg = Days.daysBetween(new DateTime(result.getUser().getCreatedDate()), DateTime.now()).getDays();
					if (!validateRule(rule, String.valueOf(daysSinceReg), result.getTrace())) {
						result.setResult(false);
						return result;
					}
					break;
				case VERIFICATION_STATUS:
                    VerificationStatus verificationStatus = verificationStatusRepository.findOne(result.getUser().getVerificationStatus());
					if (!validateRule(rule, verificationStatus.getCode(), result.getTrace())) {
						result.setResult(false);
						return result;
					}
					break;
				case LT_DEPOSITS_IN_CENTS:
					long ltDepositAmountCents = -1;
					try {
						if (defaultDomainCurrency == null) {
							defaultDomainCurrency = cachingDomainClientService.getDefaultDomainCurrency(
								result.getUser().getDomain().getName());
						}
						ltDepositAmountCents = accountingService.getTransactionTypeAmountCents(result.getUser(), defaultDomainCurrency,
							Period.GRANULARITY_TOTAL, 0, AccountingService.ACCOUNT_CODE_PLAYER_BALANCE,
							AccountingService.TRAN_TYPE_DEPOSIT);
					} catch (Exception e) {
						log.error("Failed to retrieve user LT deposit amount cents in auto-restriction process. "
							+ " Cannot proceed. Outcome not processed. [user.guid="+result.getUser().guid()+", rule="+rule+"] "
							+ e.getMessage(), e);
							result.setResult(false);
							return result;
					}
					if (!validateRule(rule, String.valueOf(ltDepositAmountCents), result.getTrace())) {
						result.setResult(false);
						return result;
					}
					break;
				case LT_WITHDRAWALS_IN_CENTS:
					long ltWithdrawalAmountCents = -1;
					try {
						if (defaultDomainCurrency == null) {
							defaultDomainCurrency = cachingDomainClientService.getDefaultDomainCurrency(
									result.getUser().getDomain().getName());
						}
						ltWithdrawalAmountCents = accountingService.getTransactionTypeAmountCents(result.getUser(), defaultDomainCurrency,
							Period.GRANULARITY_TOTAL, 0, AccountingService.ACCOUNT_CODE_PLAYER_BALANCE_PENDING_WITHDRAWAL,
							AccountingService.TRAN_TYPE_WITHDRAWAL);
					} catch (Exception e) {
						log.error("Failed to retrieve user LT withdrawal amount cents in auto-restriction process. "
							+ " Cannot proceed. Outcome not processed. [user.guid="+result.getUser().guid()+", rule="+rule+"] "
							+ e.getMessage(), e);
							result.setResult(false);
							return result;
					}
					if (!validateRule(rule, String.valueOf(ltWithdrawalAmountCents), result.getTrace())) {
						result.setResult(false);
						return result;
					}
					break;
				case CONTRA_PAYMENT_ACCOUNT_SET:
					try {
						ProcessorAccount processorAccount = cashierService.getUserContraAccount(result.getUser().guid());
						if (!validateRule(rule, String.valueOf(processorAccount != null), result.getTrace())) {
							result.setResult(false);
							return result;
						}
					} catch (Exception e) {
						log.error("Failed to check payment contra account."
								+ " Cannot proceed. Outcome not processed. [user.guid="+result.getUser().guid()+", rule="+rule+"] "
								+ e.getMessage(), e);
					}
					break;
				case HOURS_SINCE_1ST_DEPOSIT:
					try {
						CashierInternalClient cashierInternalClient = servicesFactory.target(CashierInternalClient.class);
						long hoursSinceTransaction = cashierInternalClient.hoursSinceTransaction(result.getUser().guid(), TransactionType.DEPOSIT).getData();
						if (!validateRule(rule, String.valueOf(hoursSinceTransaction), result.getTrace())) {
							result.setResult(false);
							return result;
						}
					} catch (Exception e) {
						log.error("Failed to check 1st deposit time."
								+ " Cannot proceed. Outcome not processed. [user.guid="+result.getUser().guid()+", rule="+rule+"] "
								+ e.getMessage(), e);
						result.setResult(false);
						return result;
					}
					break;
				case SPECIFIC_RESTRICTION_EVENT:
					checkSpecificRestrictionEvent(rule, result);
					if (!result.isResult()) return result;
					break;
				case DAYS_SINCE_RESTRICTION_ACTIVE:
					userRestrictionSet = userRestrictionService.find(result.getUser().getGuid(), result.getRestrictionSet());

					if(userRestrictionSet != null) {
						long daysSinceActive = Days.daysBetween(new DateTime(userRestrictionSet.getActiveFrom()), DateTime.now()).getDays();
						boolean isRestrictionActive = userRestrictionService.isActiveUserRestriction(userRestrictionSet);

						if(isRestrictionActive && !validateRule(rule, String.valueOf(daysSinceActive), result.getTrace())) {
							result.setResult(false);
						}
					}
					else {
						result.setResult(false);
					}


					break;
				case RESTRICTION_SUB_TYPE:
					userRestrictionSet = userRestrictionService.find(result.getUser().getGuid(), result.getRestrictionSet());

					if(userRestrictionSet != null && userRestrictionSet.getSubType() != null) {
						if(!validateRule(rule, userRestrictionSet.getSubType().toString(), result.getTrace())) {
							result.setResult(false);
						}
					}
					else {
						result.setResult(false);
					}

					break;
				case AGE:
						doAgeChecks(rule, result);
					break;
				case USER_STATUS_IS_USER_ENABLED:
					boolean userEnabled = result.getUser().getStatus().getUserEnabled();

					if(!validateRule(rule, String.valueOf(userEnabled), result.getTrace())) {
						result.setResult(false);
					}
					break;
				default:
					result.setResult(false);
					return result;
			}
		}
		return result;
	}

	private void checkSpecificRestrictionEvent(AutoRestrictionRule rule, AutoRestrictionRuleSetResult result) {
		DateTime appliedOn = rule.getDelay() != null ? result.getCreatedOn() : null;
		switch(rule.getEvent()) {
			case SPECIFIC_RESTRICTION_APPLIED:
				Optional<UserRestrictionSet> userRestrictionSet = userRestrictionService.getAppliedUserRestrictions(result.getUser().guid(), result.getCreatedOn()).stream().filter(rs -> rs.getSet().getName().equals(rule.getValue())).findFirst();
				if (!userRestrictionSet.isPresent()) {
					result.setResult(false);
					return;
				}
				appliedOn = new DateTime(userRestrictionSet.get().getActiveFrom());
				break;
			case REGISTRATION:
				//no verification here always true
				appliedOn = new DateTime(result.getUser().getCreatedDate());
				break;
			case FIRST_DEPOSIT:
				try {
					CashierInternalClient cashierInternalClient = servicesFactory.target(CashierInternalClient.class);
					Date firstDepositDate = cashierInternalClient.firstTransactionDate(result.getUser().guid(), TransactionType.DEPOSIT).getData();
					if (firstDepositDate == null) {
						result.setResult(false);
						return;
					}
					appliedOn = new DateTime(firstDepositDate);
				} catch (Exception e) {
					log.error("Failed to check 1st deposit time."
							+ " Cannot proceed. Outcome not processed. [user.guid=" + result.getUser().guid() + ", rule=" + rule + "] "
							+ e.getMessage(), e);
					result.setResult(false);
					return;
				}
				break;
			default:
				break;
		}
		traceEventRule(rule, true, result.getTrace());
		updateResult(result, true, appliedOn != null && rule.getDelay() != null
				? new DateTime(appliedOn.getMillis() + rule.getDelay() * 1000)
				: null);
	}

	private void updateResult(AutoRestrictionRuleSetResult ruleSetResult, boolean result, DateTime appliedOn) {
		ruleSetResult.setResult(result);

		switch (ruleSetResult.getOutcome()) {
		case PLACE:
			if ( appliedOn != null && (ruleSetResult.getActiveFrom() == null || appliedOn.isBefore(ruleSetResult.getActiveFrom()))) {
				ruleSetResult.setActiveFrom(appliedOn);
			}
			break;
		case LIFT:
			if (appliedOn != null && (ruleSetResult.getActiveTo() == null || appliedOn.isAfter(ruleSetResult.getActiveTo()))) {
				ruleSetResult.setActiveTo(appliedOn);
			}
		break;
			default: log.error("Invalid, unhandled auto-restriction outcome [outcome="+ruleSetResult.getOutcome()+"]");
		}
	}

	private boolean validateRule(AutoRestrictionRule rule, String strValue, StringBuilder trace) {
		boolean result = false;

		switch (rule.getField().type()) {
			case "long":
				long value = -1;
				long ruleValue = -1;
				Long ruleValue2 = null;
				try {
					value = Long.parseLong(strValue);
					ruleValue = Long.parseLong(rule.getValue());
					if (rule.getValue2() != null) {
						ruleValue2 = Long.parseLong(rule.getValue2());
					}
					result = validate(rule.getOperator(), value, ruleValue, ruleValue2);
				} catch (NumberFormatException e) {
					return false;
				}
				break;
			case "csv":
				String[] values = rule.getValue().split(",");
				result = validate(rule.getOperator(), strValue, values);
				break;
			case "boolean":
				try {
					result = validate(rule.getOperator(), Boolean.parseBoolean(strValue), Boolean.parseBoolean(rule.getValue().toLowerCase()));
				} catch (Exception e) {
					log.error("Failed to parse to boolean auto restriction rule id: " + rule.getId() + " value: " + rule.getValue());
				}
				break;

			default: return false;
		}

		trace(result, rule, strValue, trace);

		return result;
	}

	private boolean validate(AutoRestrictionRuleOperator operator, long value, long ruleValue, Long ruleValue2) {
		boolean result = false;
		switch (operator) {
			case BETWEEN:
				if (ruleValue2 == null) return false;
				result = (value >= ruleValue && value <= ruleValue2.longValue());
				break;
			case EQUALS:
				result = (value == ruleValue);
				break;
			case GREATER_THAN:
				result = (value > ruleValue);
				break;
			case GREATER_THAN_OR_EQUALS:
				result = (value >= ruleValue);
				break;
			case LESS_THAN:
				result = (value < ruleValue);
				break;
			case LESS_THAN_OR_EQUALS:
				result = (value <= ruleValue);
				break;
			default: return false;
		}
		return result;
	}

	private boolean validate(AutoRestrictionRuleOperator operator, String strValue, String[] ruleValues) {
		boolean result = false;
		switch (operator) {
			case IN:
				result = Arrays.stream(ruleValues).anyMatch(value -> value.contentEquals(strValue));
				break;
			default: return false;
		}
		return result;
	}

	private boolean validate(AutoRestrictionRuleOperator operator, boolean value, boolean ruleValue) {
		return operator == EQUALS && value == ruleValue;
	}

	private void trace(boolean result, AutoRestrictionRule rule, String strValue, StringBuilder trace) {
		if (result) {
			if (trace.length() > 0) {
				trace.append("\r\n");
				trace.append(" and ");
				trace.append("\r\n");
			} else {
				trace.append("Trace:\r\n");
			}
			String field = rule.getField().field().toLowerCase().replaceAll("_", " ");
			field = StringUtils.capitalize(field);
			trace.append(field);
			trace.append(" with value " + strValue);
			trace.append(" is " + rule.getOperator().operator().toLowerCase().replaceAll("_", " "));
			trace.append(" " + rule.getValue());
			if (rule.getValue2() != null) {
				trace.append(" and " + rule.getValue2());
			}
		}
	}

	private void traceEventRule(AutoRestrictionRule rule, boolean result, StringBuilder trace) {
		if (result) {
			if (trace.length() > 0) {
				trace.append("\r\n");
				trace.append(" and ");
				trace.append("\r\n");
			} else {
				trace.append("Trace:\r\n");
			}
			String field = rule.getField().field().toLowerCase().replaceAll("_", " ");
			String event = rule.getEvent().event().toLowerCase().replaceAll("_", " ");
			field = StringUtils.capitalize(field);
			trace.append(field);
			trace.append(" Event: " + event);
			if (rule.getValue() != null) {
				trace.append(" Value: " + rule.getValue());
			}
			if (rule.getDelay() != null) {
				trace.append(rule.getDelay() != null ? " With delay in seconds: " + rule.getDelay() : "without delay");
			}
		}
	}
	private void doAgeChecks(AutoRestrictionRule rule, AutoRestrictionRuleSetResult result) {
		Integer dobDay = result.getUser().getDobDay();
		Integer dobMonth = result.getUser().getDobMonth();
		Integer dobYear =  result.getUser().getDobYear();

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.M.d");
		LocalDate dob = LocalDate.parse(dobYear + "." + dobMonth + "." + dobDay, dateTimeFormatter);

		// Adding as fail-safe. The auto-restriction rule would not be configured on root ecosystem domain where
		// registration does not require dob to be set.
		if ((dobYear == null) || (dobMonth == null) || (dobDay == null)) {
			result.setResult(false);
			return;
		}

		int userAgeYears = java.time.Period.between(dob, LocalDate.now()).getYears();

		Boolean valid = validateRule(rule,String.valueOf(userAgeYears), result.getTrace());

		if(valid) {
			result.setResult(true);
		} else {
			result.setResult(false);
		}
	}
}

package lithium.service.cashier.services.autowithdrawal;

import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.cashier.client.objects.enums.AutoWithdrawalRuleType;
import lithium.service.cashier.data.entities.AutoWithdrawalRule;
import lithium.service.cashier.data.entities.AutoWithdrawalRuleSet;
import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.objects.AutoApproveResult;
import lithium.service.cashier.services.AutoWithdrawalRulesetService;
import lithium.service.cashier.services.TransactionService;
import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Service
@Slf4j
public class AutoWithdrawalService {
    private AutoWithdrawalRulesetService service;
    private TransactionService transactionService;
    private Map<AutoWithdrawalRuleType, AutoWithdrawalRuleTemplate> autoWithdrawalRuleTemplatesMap;

    public AutoWithdrawalService (List<AutoWithdrawalRuleTemplate> autoWithdrawalRuleTemplates,
                                  AutoWithdrawalRulesetService service, TransactionService transactionService){
        this.service = service;
        this.transactionService = transactionService;
        this.autoWithdrawalRuleTemplatesMap = autoWithdrawalRuleTemplates.stream()
                .collect(Collectors.toMap(AutoWithdrawalRuleTemplate::getType, template -> template));
    }

    @TimeThisMethod
    public AutoApproveResult shouldAutoApproveTransaction(AutoWithdrawalRuleSet ruleset, User user, DomainMethod domainMethod,
                                                          Transaction transaction) {
        if (ruleset == null) {
            return shouldAutoApproveTransaction(user, domainMethod, transaction);
        }
        return checkRuleset(ruleset, user, domainMethod, transaction);
    }

    // TODO: I am concerned that as the number of auto-withdrawal rulesets and rules therein get larger, this
    //       may start taking a bit longer to complete. And it is currently a blocking call on the DoMachine
    //       initiated by the player withdrawal action. That may need to change.
    private AutoApproveResult shouldAutoApproveTransaction(User user, DomainMethod domainMethod,
                                                           Transaction transaction) {
        int page = 0;
        boolean process = true;
        AutoApproveResult finalResult = AutoApproveResult.builder().approved(false).build();
        while (process) {
            Pageable pageRequest = PageRequest.of(page, 10, Sort.Direction.ASC, new String[]{"id"});
            Page<AutoWithdrawalRuleSet> pageResult = service.find(new String[]{user.getDomain().getName()},
                    true, null, null, null, null, pageRequest);
            log.debug("Found " + pageResult.getContent().size() + " entries."
                    + " Page " + pageResult.getNumber() + " of " + pageResult.getTotalPages());

            // TODO: Should there be some priority in the rulesets? Currently just checking top -> bottom by id asc.

            for (AutoWithdrawalRuleSet ruleset : pageResult.getContent()) {
                AutoApproveResult autoResult = checkRuleset(ruleset, user, domainMethod, transaction);

                //set the biggest delay from the rulesets that are met.
                //in case ruleset without delay is met - approve transaction immediately (without delay)
                if (autoResult.isApproved()) {
                    finalResult.setApproved(true);
                    finalResult.setTrace(autoResult.getTrace());
                    if (autoResult.getProcessDelay() == null) {
                        finalResult.setProcessDelay(null);
                        return finalResult;
                    } else if (finalResult.getProcessDelay() == null || autoResult.getProcessDelay() > finalResult.getProcessDelay()) {
                        finalResult.setProcessDelay(autoResult.getProcessDelay());
                    }
                }
            }

            page++;
            if (!pageResult.hasNext()) process = false;
        }
        return finalResult;
    }

    private AutoApproveResult checkRuleset(AutoWithdrawalRuleSet ruleset, User user,
                                           DomainMethod domainMethod, Transaction transaction) {
        List<AutoWithdrawalRule> rules = ruleset.getRules()
                .stream()
                .filter(AutoWithdrawalRule::isEnabled)
                .collect(Collectors.toList());

        if (rules.isEmpty()) {
            log.debug("Ruleset has no enabled rules and thus it is not considered"
                    + " in auto-approval determination [ruleset=" + ruleset + "]");
            return AutoApproveResult.builder().approved(false).build();
        }

        log.debug("Checking " + rules.size() + " rules");
        SW.start("checkingRuleset_" + ruleset.getDomain().getName() + "_" + ruleset.getName());
        StringBuilder trace = new StringBuilder();
        AutoApproveResult autoResult = AutoApproveResult.builder()
                .approved(checkRules(rules, user, domainMethod, transaction, trace))
                .processDelay(ruleset.getDelay())
                .trace(trace.toString())
                .build();
        SW.stop();

        return autoResult;
    }

    private boolean checkRules(List<AutoWithdrawalRule> rules, User user, DomainMethod domainMethod,
                               Transaction transaction, StringBuilder trace) {
        trace.setLength(0);
        RuleValidateContext context = RuleValidateContext.builder()
                .user(user)
                .transaction(transaction)
                .domainMethod(domainMethod)
                .build();

        return rules.stream()
                .allMatch(rule -> check(rule, context, trace));
    }

    private boolean check(AutoWithdrawalRule rule, RuleValidateContext context, StringBuilder trace) {
        ValidatedResult result = ValidatedResult.failed();
        try {
            result = getAutoWithdrawalRuleTemplateByType(rule.getField())
                    .map(template -> template.validate(rule, context))
                    .orElse(ValidatedResult.failed());

        } catch (Exception ex) {
             log.error("Got error during check rule "+rule.getRuleset().getName(), ex);
        }
        if (result.isValidated()) {
            trace(rule, result.getCheckingValue(), trace);
        }
        return result.isValidated();
    }

    private void trace(AutoWithdrawalRule rule, String strValue, StringBuilder trace) {
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

    public Optional<AutoWithdrawalRuleTemplate> getAutoWithdrawalRuleTemplateByType(AutoWithdrawalRuleType ruleType) {
        return ofNullable(autoWithdrawalRuleTemplatesMap.get(ruleType));
    }

}

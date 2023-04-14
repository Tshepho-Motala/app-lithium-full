package lithium.service.access.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.service.Response;
import lithium.service.access.client.objects.Action;
import lithium.service.access.data.entities.AccessControlList;
import lithium.service.access.data.entities.AccessRule;
import lithium.service.access.data.entities.ExternalList;
import lithium.service.access.data.entities.ExternalListRuleStatusOptionConfig;
import lithium.service.access.data.objects.RuleEdit;
import lithium.service.access.data.objects.RuleMessageUpdate;
import lithium.service.access.data.repositories.AccessControlListRepository;
import lithium.service.access.data.repositories.AccessControlListRuleStatusOptionConfigRepository;
import lithium.service.access.data.repositories.AccessRuleRepository;
import lithium.service.access.data.repositories.AccessRuleStatusOptionsRepository;
import lithium.service.access.data.repositories.ExternalListRepository;
import lithium.service.access.data.repositories.ExternalListRuleStatusOptionConfigRepository;
import lithium.service.access.data.repositories.ListRepository;
import lithium.service.domain.client.CachingDomainClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
public class RulesetService {

  @Autowired
  ListRepository listRepository;
  @Autowired
  AccessRuleRepository accessRuleRepository;
  @Autowired
  ExternalListRepository externalListRepository;
  @Autowired
  AccessControlListRepository accessControlListRepository;
  @Autowired
  AccessRuleStatusOptionsRepository accessRuleStatusOptionsRepository;
  @Autowired
  ExternalListRuleStatusOptionConfigRepository externalListRuleStatusOptionConfigRepository;
  @Autowired
  AccessControlListRuleStatusOptionConfigRepository accessControlListRuleStatusOptionConfigRepository;

  @Autowired
  MessageSource messageSource;
  @Autowired
  ChangeLogService changeLogService;
  @Autowired
  TransactionService transactionService;
  @Autowired
  CachingDomainClientService cachingDomainClientService;

  public AccessRule addRule(RuleEdit ruleAdd) {
    int priority = ruleAdd.getRuleset().getAccessControlList().size() + ruleAdd.getRuleset().getExternalList().size();
    AccessRule accessRule = findOne(ruleAdd.getRuleset().getId());
    if ("provider".equalsIgnoreCase(ruleAdd.getType())) {
      ExternalList providerRule = ExternalList.builder().build();
      providerRule.setAccessRule(ruleAdd.getRuleset());
      providerRule.setActionSuccess(ruleAdd.getActionSuccess());
      providerRule.setActionFailed(ruleAdd.getActionFailed());
      providerRule.setValidateOnce(ruleAdd.getValidateOnce());
      providerRule.setListName(ruleAdd.getName());
      providerRule.setProviderUrl(ruleAdd.getProviderUrl());
      providerRule.setEnabled(ruleAdd.getEnabled());
      providerRule.setPriority(priority);
      providerRule = externalListRepository.save(providerRule);

      createRuleOutcomes(providerRule, ruleAdd.getOutcomes());
      accessRule.addExternalList(providerRule);
    } else if ("list".equalsIgnoreCase(ruleAdd.getType())) {
      lithium.service.access.data.entities.List list = listRepository.findOne(ruleAdd.getListId());
      AccessControlList listRule = AccessControlList.builder().build();
      listRule.setAccessRule(ruleAdd.getRuleset());
      listRule.setActionSuccess(ruleAdd.getActionSuccess());
      listRule.setActionFailed(ruleAdd.getActionFailed());
      listRule.setIpResetTime(ruleAdd.getIpResetTime());
      listRule.setList(list);
      listRule.setEnabled(ruleAdd.getEnabled());
      listRule.setPriority(priority);
      listRule = accessControlListRepository.save(listRule);
      accessRule.addList(listRule);
    }
    return accessRule;
  }

  public AccessRule editRule(RuleEdit ruleEdit, String authorGuid) throws Exception {
    if ("provider".equalsIgnoreCase(ruleEdit.getType())) {
      ExternalList providerRule = ruleEdit.getRuleset().getExternalList().stream()
          .filter(r -> r.getId() == ruleEdit.getRuleId()).findFirst().orElse(null);
      ExternalList providerRuleCopy = providerRule.toBuilder().build();
      providerRule.setActionSuccess(ruleEdit.getActionSuccess());
      providerRule.setActionFailed(ruleEdit.getActionFailed());
      providerRule.setValidateOnce(ruleEdit.getValidateOnce());
      providerRule.setListName(ruleEdit.getName());
      providerRule.setEnabled(ruleEdit.getEnabled());
      providerRule = externalListRepository.save(providerRule);
      List<ChangeLogFieldChange> outcomeChanges = saveRuleOutcomes(ruleEdit.getOutcomes(), providerRule.getExternalListRuleStatusOptionConfigList());
      providerRuleChangelog(ruleEdit, providerRule, providerRuleCopy, outcomeChanges, authorGuid);
    } else if ("list".equalsIgnoreCase(ruleEdit.getType())) {
      AccessControlList listRule = ruleEdit.getRuleset().getAccessControlList().stream()
          .filter(r -> r.getId() == ruleEdit.getRuleId()).findFirst().orElse(null);
      AccessControlList listRuleCopy = listRule.toBuilder().build();
      listRuleCopy.setList(listRuleCopy.getList().toBuilder().build());
      listRule.setActionSuccess(ruleEdit.getActionSuccess());
      listRule.setActionFailed(ruleEdit.getActionFailed());
      listRule.setIpResetTime(ruleEdit.getIpResetTime());
      listRule.getList().setName(ruleEdit.getName());
      listRule.setEnabled(ruleEdit.getEnabled());
      listRule = accessControlListRepository.save(listRule);
      listRuleChangelog(ruleEdit, listRule, listRuleCopy, authorGuid);
    }
    return findOne(ruleEdit.getRuleset().getId());
  }

  @Transactional(rollbackFor = Exception.class)
  @Retryable(backoff = @Backoff(delay = 500), maxAttempts = 10)
  public AccessRule deleteRule(AccessRule ruleset, Long ruleId, String type, String authorGuid) throws Exception {
    String comment = "";
    int[] deletedPriority = new int[1];
    String defaultLocale = cachingDomainClientService.domainLocale(ruleset.getDomain().getName());
    log.debug("Translation: locale:" + defaultLocale);

    if ("provider".equalsIgnoreCase(type)) {
      ExternalList providerRule = ruleset.getExternalList().stream()
          .filter(r -> r.getId() == ruleId).findFirst().orElse(null);
      comment = providerRule.getListName();
      deletedPriority[0] = providerRule.getPriority();
      ruleset.getExternalList().remove(providerRule);
      ruleset = save(ruleset);
      externalListRuleStatusOptionConfigRepository.deleteByExternalList(providerRule);
      externalListRepository.delete(providerRule);
    } else if ("list".equalsIgnoreCase(type)) {
      AccessControlList listRule = ruleset.getAccessControlList().stream()
          .filter(r -> r.getId() == ruleId).findFirst().orElse(null);
      comment = listRule.getList().getName();
      deletedPriority[0] = listRule.getPriority();
      ruleset.getAccessControlList().remove(listRule);
      ruleset = save(ruleset);
      accessControlListRepository.delete(listRule);
    }
    comment += " " + messageSource.getMessage("UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.EDITRULE.DELETED", null, Locale.forLanguageTag(defaultLocale));
    changeLogService.registerChangesWithDomain("accessrule", "edit", ruleset.getId(), authorGuid, comment, null, null, Category.ACCESS,
        SubCategory.ACCESS_RULE, 0, ruleset.getDomain().getName());

    ruleset.getAccessControlList().stream()
        .sorted(Comparator.comparingInt(AccessControlList::getPriority))
        .filter(l -> (l.getPriority() > deletedPriority[0]))
        .forEach(l -> {
          l.setPriority(l.getPriority() - 1);
          accessControlListRepository.save(l);
        });
    ruleset.getExternalList().stream()
        .sorted(Comparator.comparingInt(ExternalList::getPriority))
        .filter(l -> (l.getPriority() > deletedPriority[0]))
        .forEach(l -> {
          l.setPriority(l.getPriority() - 1);
          externalListRepository.save(l);
        });

    return ruleset;
  }

  private void createRuleOutcomes(ExternalList externalList, Map<String, String> outcomes) {
    List<ExternalListRuleStatusOptionConfig> statusOptionConfigList = new ArrayList<>();
    outcomes.forEach((key, value) -> {
      statusOptionConfigList.add(externalListRuleStatusOptionConfigRepository.save(
          ExternalListRuleStatusOptionConfig.builder()
              .externalList(externalList)
              .outcome(accessRuleStatusOptionsRepository.findByNameAndOutcomeTrue(key))
              .output(accessRuleStatusOptionsRepository.findByNameAndOutputTrue(value))
              .build()
      ));
    });
    externalList.setExternalListRuleStatusOptionConfigList(statusOptionConfigList);
    externalListRepository.save(externalList);
  }

  private List<ChangeLogFieldChange> saveRuleOutcomes(Map<String, String> outcomes,
      List<ExternalListRuleStatusOptionConfig> externalListRuleStatusOptionConfigList) {
    List<ChangeLogFieldChange> clfc = new ArrayList<>();
    for (ExternalListRuleStatusOptionConfig ruleStatusOptionConfig : externalListRuleStatusOptionConfigList) {
      String outcomeName = ruleStatusOptionConfig.getOutcome().getName();
      String outputName = ruleStatusOptionConfig.getOutput().getName();
      if (!outcomes.get(outcomeName).equalsIgnoreCase(outputName)) {
        log.info(outcomeName + " changing from: " + outputName + " to: " + outcomes.get(outcomeName) + " [" + ruleStatusOptionConfig + "]");
        ruleStatusOptionConfig.setOutput(accessRuleStatusOptionsRepository.findByNameAndOutcomeTrue(outcomes.get(outcomeName)));
        externalListRuleStatusOptionConfigRepository.save(ruleStatusOptionConfig);
        ChangeLogFieldChange c = ChangeLogFieldChange.builder()
            .field("outcome [" + outcomeName + "]")
            .fromValue(outputName)
            .toValue(outcomes.get(outcomeName))
            .build();
        clfc.add(c);
      }
    }
    return clfc;
  }

  public AccessRule increasePriority(AccessRule ruleset, Long ruleId, String type, String authorGuid) throws Exception {
    String comment = "";
    int[] changingPriority = new int[1];

    String defaultLocale = cachingDomainClientService.domainLocale(ruleset.getDomain().getName());
    log.debug("Translation: locale:" + defaultLocale);

    if ("provider".equalsIgnoreCase(type)) {
      ExternalList providerRule = ruleset.getExternalList().stream()
          .filter(r -> r.getId() == ruleId).findFirst().orElse(null);
      changingPriority[0] = providerRule.getPriority();
      comment = providerRule.getListName();
    } else if ("list".equalsIgnoreCase(type)) {
      AccessControlList listRule = ruleset.getAccessControlList().stream()
          .filter(r -> r.getId() == ruleId).findFirst().orElse(null);
      changingPriority[0] = listRule.getPriority();
      comment = listRule.getList().getName();
    }
    comment += " " + messageSource
        .getMessage("UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.EDITRULE.INCREASE_PRIORITY", null, Locale.forLanguageTag(defaultLocale));

    ruleset.getAccessControlList().stream()
        .filter(l -> ((l.getPriority() >= (changingPriority[0] - 1)) && (l.getPriority() <= changingPriority[0])))
        .forEach(l -> {
          l.setPriority((l.getPriority() == changingPriority[0]) ? (l.getPriority() - 1) : (l.getPriority() + 1));
          accessControlListRepository.save(l);
        });
    ruleset.getExternalList().stream()
        .filter(l -> ((l.getPriority() >= (changingPriority[0] - 1)) && (l.getPriority() <= changingPriority[0])))
        .forEach(l -> {
          l.setPriority((l.getPriority() == changingPriority[0]) ? (l.getPriority() - 1) : (l.getPriority() + 1));
          externalListRepository.save(l);
        });

    ChangeLogFieldChange c = ChangeLogFieldChange.builder().field("priority").build();
    changeLogService.registerChangesWithDomain("accessrule", "edit", ruleset.getId(), authorGuid, comment, null, Collections.singletonList(c), Category.ACCESS,
        SubCategory.ACCESS_RULE, 0, ruleset.getDomain().getName());
    return findOne(ruleset.getId());
  }

  public AccessRule decreasePriority(AccessRule ruleset, Long ruleId, String type, String authorGuid) throws Exception {
    String comment = "";
    int[] changingPriority = new int[1];

    String defaultLocale = cachingDomainClientService.domainLocale(ruleset.getDomain().getName());
    log.debug("Translation: locale:" + defaultLocale);

    if ("provider".equalsIgnoreCase(type)) {
      ExternalList providerRule = ruleset.getExternalList().stream()
          .filter(r -> r.getId() == ruleId).findFirst().orElse(null);
      changingPriority[0] = providerRule.getPriority();
      comment = providerRule.getListName();
    } else if ("list".equalsIgnoreCase(type)) {
      AccessControlList listRule = ruleset.getAccessControlList().stream()
          .filter(r -> r.getId() == ruleId).findFirst().orElse(null);
      changingPriority[0] = listRule.getPriority();
      comment = listRule.getList().getName();
    }
    comment += " " + messageSource
        .getMessage("UI_NETWORK_ADMIN.ACCESSCONTROL.RULESETS.EDITRULE.DECREASE_PRIORITY", null, Locale.forLanguageTag(defaultLocale));

    ruleset.getAccessControlList().stream()
        .filter(l -> ((l.getPriority() <= (changingPriority[0] + 1)) && (l.getPriority() >= changingPriority[0])))
        .forEach(l -> {
          l.setPriority((l.getPriority() == changingPriority[0]) ? (l.getPriority() + 1) : (l.getPriority() - 1));
          accessControlListRepository.save(l);
        });
    ruleset.getExternalList().stream()
        .filter(l -> ((l.getPriority() <= (changingPriority[0] + 1)) && (l.getPriority() >= changingPriority[0])))
        .forEach(l -> {
          l.setPriority((l.getPriority() == changingPriority[0]) ? (l.getPriority() + 1) : (l.getPriority() - 1));
          externalListRepository.save(l);
        });

    ChangeLogFieldChange c = ChangeLogFieldChange.builder().field("priority").build();
    changeLogService.registerChangesWithDomain("accessrule", "edit", ruleset.getId(), authorGuid, comment, null, Collections.singletonList(c), Category.ACCESS,
        SubCategory.ACCESS_RULE, 0, ruleset.getDomain().getName());
    return findOne(ruleset.getId());
  }

  public AccessRule changeRuleMessage(AccessRule ruleset, Long ruleId, String type, RuleMessageUpdate messageUpdate, String authorGuid) throws Exception {
    String rejectMessage = null;
    String reviewMessage = null;
    String timeoutMessage = null;

    if (!ObjectUtils.isEmpty(messageUpdate.getMessage())) {
      rejectMessage = messageUpdate.getMessage();
    }
    if (!ObjectUtils.isEmpty(messageUpdate.getReviewMessage())) {
      reviewMessage = messageUpdate.getReviewMessage();
    }
    if (!ObjectUtils.isEmpty(messageUpdate.getTimeoutMessage())) {
      timeoutMessage = messageUpdate.getTimeoutMessage();
    }

    String oldMessage = null;
    String oldReviewMessage = null;
    String oldTimeoutMessage = null;
    String rule = "";
    switch (type) {
      case "default":
        oldMessage = ruleset.getDefaultMessage();
        ruleset.setDefaultMessage(rejectMessage);
        accessRuleRepository.save(ruleset);
      case "provider":
        ExternalList providerRule = ruleset.getExternalList().stream()
            .filter(r -> Objects.equals(r.getId(), ruleId)).findFirst().orElse(null);
        if (providerRule != null) {
          rule = providerRule.getListName();
          oldMessage = providerRule.getMessage();
          oldReviewMessage = providerRule.getReviewMessage();
          oldTimeoutMessage = providerRule.getTimeoutMessage();
          providerRule.setMessage(rejectMessage);
          providerRule.setReviewMessage(reviewMessage);
          providerRule.setTimeoutMessage(timeoutMessage);
          externalListRepository.save(providerRule);
        }
        break;
      case "list":
        AccessControlList listRule = ruleset.getAccessControlList().stream()
            .filter(r -> Objects.equals(r.getId(), ruleId)).findFirst().orElse(null);
        if (listRule != null) {
          oldMessage = listRule.getMessage();
          listRule.setMessage(messageUpdate.getMessage());
          rule = listRule.getList().getName();
          accessControlListRepository.save(listRule);
        }
        break;
    }
    List<ChangeLogFieldChange> clfc = new ArrayList<>();
    ChangeLogFieldChange changeReject = ChangeLogFieldChange.builder()
        .field("message")
        .fromValue(oldMessage)
        .toValue(rejectMessage)
        .build();

    ChangeLogFieldChange changeReview = ChangeLogFieldChange.builder()
        .field("reviewMessage")
        .fromValue(oldReviewMessage)
        .toValue(reviewMessage)
        .build();

    ChangeLogFieldChange changeTimeout = ChangeLogFieldChange.builder()
        .field("timeoutMessage")
        .fromValue(oldTimeoutMessage)
        .toValue(timeoutMessage)
        .build();

    String defaultLocale = cachingDomainClientService.domainLocale(ruleset.getDomain().getName());
    log.debug("Translation: locale:" + defaultLocale);
    String comment = messageSource.getMessage("UI_NETWORK_ADMIN.ACCESSCONTROL.SAVE_MESSAGE.DEFAULT_RULE", null, Locale.forLanguageTag(defaultLocale));
    if (!"default".equalsIgnoreCase(type)) {
      comment = messageSource.getMessage("UI_NETWORK_ADMIN.ACCESSCONTROL.SAVE_MESSAGE.RULE", null, Locale.forLanguageTag(defaultLocale));
      comment += rule;
    }

    if (!ObjectUtils.isEmpty(rejectMessage) && !ObjectUtils.nullSafeEquals(rejectMessage, oldMessage)) {
      clfc.add(changeReject);
    }
    if (!ObjectUtils.isEmpty(reviewMessage) && !ObjectUtils.nullSafeEquals(reviewMessage, oldReviewMessage)) {
      clfc.add(changeReview);
    }
    if (!ObjectUtils.isEmpty(timeoutMessage) && !ObjectUtils.nullSafeEquals(timeoutMessage, oldTimeoutMessage)) {
      clfc.add(changeTimeout);
    }

    if (!clfc.isEmpty()) {
      changeLogService.registerChangesWithDomain("accessrule", "edit", ruleset.getId(), authorGuid, comment, null, clfc, Category.ACCESS,
          SubCategory.ACCESS_RULE, 0, ruleset.getDomain().getName());
    }

    return findOne(ruleset.getId());
  }

  private void listRuleChangelog(RuleEdit ruleEdit, AccessControlList listRule, AccessControlList listRuleCopy, String authorGuid)
      throws Exception {
    List<ChangeLogFieldChange> clfc = changeLogService.compare(
        listRule,
        listRuleCopy,
        new String[]{
            "actionSuccess", "actionFailed", "ipResetTime", "list.name", "enabled"
        }
    );
    ruleChangelog(ruleEdit.getRuleset().getDomain().getName(), ruleEdit.getName(), ruleEdit.getRuleset().getId(), clfc, authorGuid);
  }

  private void ruleChangelog(String domainName, String ruleName, Long rulesetId, List<ChangeLogFieldChange> changes, String authorGuid)
      throws Exception {
    String defaultLocale = cachingDomainClientService.domainLocale(domainName);
    log.debug("Translation: locale:" + defaultLocale);
    String comment = messageSource.getMessage("UI_NETWORK_ADMIN.ACCESSCONTROL.SAVE_MESSAGE.RULE", null, Locale.forLanguageTag(defaultLocale));
    comment += ruleName;
    if (!changes.isEmpty()) {
      changeLogService.registerChangesWithDomain("accessrule", "edit", rulesetId, authorGuid, comment, null, changes, Category.ACCESS,
          SubCategory.ACCESS_RULE, 0, domainName);
    }
  }

  private void providerRuleChangelog(
      RuleEdit ruleEdit,
      ExternalList providerRule,
      ExternalList providerRuleCopy,
      List<ChangeLogFieldChange> outcomeChanges,
      String authorGuid
  ) throws Exception {
    List<ChangeLogFieldChange> clfc = changeLogService.copy(
        providerRule,
        providerRuleCopy,
        new String[]{
            "actionSuccess", "actionFailed", "validateOnce", "listName", "enabled"
        }
    );
    clfc.addAll(outcomeChanges);
    ruleChangelog(ruleEdit.getRuleset().getDomain().getName(), ruleEdit.getName(), ruleEdit.getRuleset().getId(), clfc, authorGuid);
  }

  private AccessRule copy(AccessRule ruleset) {
    return ruleset.toBuilder().build();
  }

  public AccessRule save(AccessRule ruleset) {
    return accessRuleRepository.save(ruleset);
  }

  public AccessRule findOne(Long id) {
    return accessRuleRepository.findOne(id);
  }

  public AccessRule save(AccessRule ruleset, String name, String description, boolean enabled, String authorGuid) {
    AccessRule rulesetCopy = copy(ruleset);
    ruleset.setName(name);
    ruleset.setDescription(description);
    ruleset.setEnabled(enabled);
    ruleset = save(ruleset);
    try {
      List<ChangeLogFieldChange> clfc = changeLogService.copy(
          ruleset,
          rulesetCopy,
          new String[]{
              "name", "description", "enabled"
          }
      );
      if (!clfc.isEmpty()) {
        changeLogService.registerChangesWithDomain("accessrule", "edit", ruleset.getId(), authorGuid, null, null, clfc, Category.ACCESS,
            SubCategory.ACCESS_RULE, 0, authorGuid.substring(0, authorGuid.indexOf('/')));
      }
    } catch (Exception e) {
      log.error("AccessRule changes saved, but changelog failed. (" + ruleset + ")", e);
    }
    return ruleset;
  }

  public AccessRule save(AccessRule ruleset, Action defaultAction, String authorGuid) {
    AccessRule rulesetCopy = copy(ruleset);
    ruleset.setDefaultAction(defaultAction);
    ruleset = save(ruleset);
    try {
      List<ChangeLogFieldChange> clfc = changeLogService.copy(
          ruleset,
          rulesetCopy,
          new String[]{
              "defaultAction"
          }
      );

      String defaultLocale = cachingDomainClientService.domainLocale(ruleset.getDomain().getName());
      log.debug("Translation: locale:" + defaultLocale);
      String comment = messageSource
          .getMessage("UI_NETWORK_ADMIN.ACCESSCONTROL.SAVE_MESSAGE.DEFAULT_RULE", null, Locale.forLanguageTag(defaultLocale));

      if (!clfc.isEmpty()) {
        changeLogService.registerChangesWithDomain("accessrule", "edit", ruleset.getId(), authorGuid, comment, null, clfc, Category.ACCESS,
            SubCategory.ACCESS_RULE, 0, ruleset.getDomain().getName());
      }
    } catch (Exception e) {
      log.error("AccessRule changes saved, but changelog failed. (" + ruleset + ")", e);
    }
    return ruleset;
  }

  public Response<ChangeLogs> changeLogs(
      Long rulesetId,
      int page
  ) throws Exception {
    return changeLogService.listLimited(
        ChangeLogRequest.builder()
            .entityRecordId(rulesetId)
            .entities(new String[]{"accessrule"})
            .page(page)
            .build()
    );
  }

  public boolean ruleTranData(AccessRule ruleset, Long ruleId, String type) {
    if ("provider".equalsIgnoreCase(type)) {
      ExternalList providerRule = ruleset.getExternalList().stream()
          .filter(r -> r.getId() == ruleId).findFirst().orElse(null);
      return transactionService.externalListHasTransactionData(providerRule);
    } else if ("list".equalsIgnoreCase(type)) {
      AccessControlList listRule = ruleset.getAccessControlList().stream()
          .filter(r -> r.getId() == ruleId).findFirst().orElse(null);
      return transactionService.accessControlListHasTransactionData(listRule);
    }
    return false;
  }

  public Iterable<lithium.service.access.data.entities.List> eligableLists(AccessRule accessRule) {
    List<AccessControlList> arAcls = accessRule.getAccessControlList();
    List<lithium.service.access.data.entities.List> arLists = new ArrayList<>();
    arAcls.parallelStream().forEach(acl -> {
      arLists.add(acl.getList());
    });
    List<lithium.service.access.data.entities.List> domainLists = listRepository.findByDomainName(accessRule.getDomain().getName());
    List<lithium.service.access.data.entities.List> listsNotUsed = domainLists.stream().filter(dl -> !arLists.contains(dl))
        .collect(Collectors.toList());
    return listsNotUsed;
  }
}

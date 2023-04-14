package lithium.service.access.services;

import static lithium.metrics.builders.kyc.EntryPoint.ACCESS_SERVICE_ON_AUTHORIZE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.InetAddresses;
import feign.FeignException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lithium.metrics.KycMetricService;
import lithium.service.Response;
import lithium.service.access.client.AccessService.ListType;
import lithium.service.access.client.ExternalAuthorizationClient;
import lithium.service.access.client.exceptions.Status551ServiceAccessClientException;
import lithium.service.access.client.objects.Action;
import lithium.service.access.client.objects.AuthorizationRequest;
import lithium.service.access.client.objects.AuthorizationResult;
import lithium.service.access.client.objects.CheckAuthorizationResult;
import lithium.service.access.client.objects.EAuthorizationOutcome;
import lithium.service.access.client.objects.ExternalAuthorizationRequest;
import lithium.service.access.client.objects.ProviderAuthorizationResult;
import lithium.service.access.client.objects.RawAuthorizationData;
import lithium.service.access.data.entities.AccessControlList;
import lithium.service.access.data.entities.AccessRule;
import lithium.service.access.data.entities.AccessRuleTransaction;
import lithium.service.access.data.entities.ExternalList;
import lithium.service.access.data.entities.ExternalListRuleStatusOptionConfig;
import lithium.service.access.data.entities.UserExternalListValidation;
import lithium.service.access.data.entities.Value;
import lithium.service.access.data.objects.AuthorizationRule;
import lithium.service.access.data.repositories.AccessControlListRepository;
import lithium.service.access.data.repositories.AccessRuleRepository;
import lithium.service.access.data.repositories.ValueRepository;
import lithium.service.access.services.duplicatecheck.DuplicateChecksService;
import lithium.service.access.services.duplicatecheck.UserDuplicateCheckFailedException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Provider;
import lithium.service.user.client.UserApiInternalClient;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.objects.UserVerificationStatusUpdate;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
public class AuthorizationService {
    @Autowired ValueRepository valueRepository;
    @Autowired AccessRuleService accessRuleService;
    @Autowired LithiumServiceClientFactory services;
    @Autowired AccessRuleRepository accessRuleRepository;
    @Autowired AccessControlListRepository accessControlListRepository;
    @Autowired UserExternalListValidationService userExternalListValidationService;
    @Autowired DuplicateChecksService duplicateChecksService;
    @Autowired KycMetricService kycMetricService;
    @Autowired ProviderClientService providerClientService;
    @Autowired UserApiInternalClientService userService;

    private boolean ipExistsInIpRangeList(String ip, java.util.Set<Value> ipRangeList) {
        return ipRangeList.parallelStream().filter(ipRange -> {
            String ipRangeStart = ipRange.getData().substring(0, ipRange.getData().indexOf("|"));
            String ipRangeEnd = ipRange.getData().substring((ipRange.getData().indexOf("|") + 1), ipRange.getData().length());
            long ipLow = ipToLong(InetAddresses.forString(ipRangeStart));
            long ipHigh = ipToLong(InetAddresses.forString(ipRangeEnd));
            long ipToTest = ipToLong(InetAddresses.forString(ip));
            return ((ipToTest >= ipLow) && (ipToTest <= ipHigh));
        }).findFirst().isPresent();
    }

    private long ipToLong(InetAddress ip) {
        byte[] octets = ip.getAddress();
        long result = 0;
        for (byte octet : octets) {
            result <<= 8;
            result |= octet & 0xff;
        }
        return result;
    }

    public boolean isAccessRuleEnabledByDomainAndRulesetName(String domainName, String rulesetName) {
      return accessRuleRepository.findByDomainNameAndNameIgnoreCase(domainName, rulesetName).isEnabled();
    }

    public AuthorizationResult checkAuthorization(
        String domainName,
        String rulesetName,
        AuthorizationRequest authorizationRequest,
        Boolean test
    ) {
        AuthorizationResult authorizationResult = AuthorizationResult.builder().build();
        List<CheckAuthorizationResult> results = new ArrayList<>();
        log.debug("checkAuthorization dn: "+domainName+" ruleset: "+rulesetName+" req: "+authorizationRequest);
        AccessRule accessRule = accessRuleRepository.findByDomainNameAndNameIgnoreCase(domainName, rulesetName);

        AuthorizationResult authorizationResultResponse = sanityChecks(accessRule, rulesetName);
        if (authorizationResultResponse != null) {
            log.debug("authorizationService.doAuthSanityChecks return :: "+authorizationResultResponse);
            return authorizationResultResponse;
        }

        AccessRuleTransaction accessRuleTransaction = accessRuleService.startAccessRuleTransaction(authorizationRequest);
        List<AuthorizationRule> rules = buildRules(accessRule, test);
        for (AuthorizationRule r:rules) {
            r.setUserGuid(authorizationRequest.getUserGuid());
            log.debug("Rule: ("+r.getRuleName()+") "+r);
            if ((r.getExternal()!=null) && (r.isEnabled())) {
                log.debug("Evaluating " + r);
                if (!r.getExternal()) {
                    CheckAuthorizationResult checkAuthorizationResult = doInternalAuthorization(
                        r.getRule(),
                        authorizationRequest,
                        accessRuleTransaction
                    );
                    log.debug("Internal Rule Result:" + checkAuthorizationResult);
                    results.add(checkAuthorizationResult);
                    authorizationResult = buildResponse(r, results);
                    if (authorizationResult != null) return authorizationResult;
                } else {
                    ExternalList externalRule = r.getExternalRule();
                    checkForEnabledProvider(domainName, externalRule);
                    if (externalRule.isValidateOnce()) {
                        if (authorizationRequest.getOverrideValidateOnce() != null && authorizationRequest.getOverrideValidateOnce()) {
                            log.debug("Validate once configured for " + externalRule.getListName() + ", but override requested."
                                + " Proceeding with external provider request.");
                        } else {
                            if (authorizationRequest.getUserGuid() != null) {
                              log.debug("Validate once configured for " + externalRule.getListName()
                                  + ". No need to perform external access check if internal data store contains data from previous external provider request.");
                              UserExternalListValidation userExternalListValidation = userExternalListValidationService.find(authorizationRequest.getUserGuid(), externalRule);
                              if (userExternalListValidation != null) {
                                if (userExternalListValidation.getMessage() != null && userExternalListValidation.getMessage().contentEquals(EAuthorizationOutcome.TIMEOUT.name())) {
                                  log.debug("Internal data store contains previous information for " + authorizationRequest.getUserGuid() + " | "
                                      + externalRule.getListName() + ", but the outcome was a timeout. Trying again, data will be updated.");
                                } else {
                                  CheckAuthorizationResult checkAuthorizationResult = CheckAuthorizationResult.builder()
                                      .listName(externalRule.getListName())
                                      .listType("external")
                                      .enabled(externalRule.isEnabled())
                                      .priority(externalRule.getPriority())
                                      .passed(userExternalListValidation.getPassed())
                                      .actionSuccess(externalRule.getActionSuccess().name())
                                      .actionFailed(externalRule.getActionFailed().name())
                                      .message(userExternalListValidation.getMessage())
                                      .answerMessage(userExternalListValidation.getErrorMessage())
                                      .found(true)
                                      .build();
                                  log.debug("External Rule Result:" + checkAuthorizationResult);
                                  userExternalListValidationService.update(
                                      userExternalListValidation,
                                      checkAuthorizationResult.getPassed(),
                                      checkAuthorizationResult.getMessage(),
                                      checkAuthorizationResult.getAnswerMessage()
                                  );
                                  results.add(checkAuthorizationResult);
                                  authorizationResult = buildResponse(r, results);
                                  if (authorizationResult != null) return authorizationResult;
                                  continue;
                                }
                              } else {
                                log.debug("Internal data store does not contain previous information for " + authorizationRequest.getUserGuid() + " | "
                                    + externalRule.getListName() + ". Request to external provider is necessary.");
                              }
                            } else {
                              log.warn("Validate once is ignored. It is most likely an incorrecly configured access rule as we do not have a user guid.");
                            }
                        }
                    }
                    CheckAuthorizationResult checkAuthorizationResult = doExternalAuthorization(
                        r.getExternalRule(),
                        authorizationRequest,
                        accessRuleTransaction
                    );
                    log.debug("External Rule Result:" + checkAuthorizationResult);
                    if (externalRule.isValidateOnce() && authorizationRequest.getUserGuid() != null) {
                        userExternalListValidationService.updateOrCreate(
                            authorizationRequest.getUserGuid(),
                            externalRule,
                            checkAuthorizationResult.getPassed(),
                            checkAuthorizationResult.getMessage(),
                            checkAuthorizationResult.getAnswerMessage()
                        );
                    }

                    results.add(checkAuthorizationResult);
                    authorizationResult = buildResponse(r, results);
                    if (authorizationResult != null) return authorizationResult;
                }
            } else if ((r.getExternal()==null) && (r.isEnabled())) {
                //Default rule
                log.debug("Default Rule Evaluation");
                return actionSwitch(accessRule.getDefaultAction(), r, results);
            } else {
                log.warn("Disabled. Skipping ("+r.getRuleName()+") "+r);
            }
        }
        return authorizationResult;
    }

    private AuthorizationResult buildResponse(AuthorizationRule r, List<CheckAuthorizationResult> results) {
        if (ruleSuccess(r, results)) {
            log.debug(r.getRuleName()+" evaluated successfully. Following ActionSuccess ("+r.getActionSuccess()+").");
            return actionSwitch(r.getActionSuccess(), r, results);
        } else {
            log.debug(r.getRuleName()+" evaluated unsuccessfully. Following ActionFailed ("+r.getActionFailed()+").");
            return actionSwitch(r.getActionFailed(), r, results);
        }
    }

    private AuthorizationResult actionSwitch(Action action, AuthorizationRule r, List<CheckAuthorizationResult> results) {
        switch (action) {
            case ACCEPT:
                log.trace("ACCEPT");
                return buildAccept(r, results);
            case REJECT:
                log.trace("REJECT");
                return buildReject(r, results);
            case ACCEPT_AND_VERIFY:
                log.trace("ACCEPT_AND_VERIFY");
                return buildAcceptAndVerify(r, results);
            case CONTINUE:
                log.trace("CONTINUE");
                break;
        }
        return null;
    }

    private AuthorizationResult buildAccept(AuthorizationRule rule, List<CheckAuthorizationResult> results) {
        return AuthorizationResult.builder()
            .message("Accepted because of \""+rule.getRuleName()+"\"")
            .successful(true)
            .rawResults(results)
            .build();
    }

    private void editUserVerificationStatus(AuthorizationRule rule) {
        Response<User> response = null;
        try {
            UserApiInternalClient userApiInternalClient = services.target(UserApiInternalClient.class, true);
            response = userApiInternalClient.editUserVerificationStatus(
                UserVerificationStatusUpdate.builder()
                .statusId(3L)
                .comment(rule.getRuleName()+" ("+rule.getRuleset().getName()+")")
                .userGuid(rule.getUserGuid())
                .build()
            );
        } catch (Exception | UserClientServiceFactoryException e) {
            log.error("User ("+rule.getUserGuid()+") verification status NOT updated with [EXTERNALLY_VERIFIED]", e);
        }
        log.debug("User ("+rule.getUserGuid()+") verification status updated with [EXTERNALLY_VERIFIED], response: "+response);
    }

    private AuthorizationResult buildAcceptAndVerify(AuthorizationRule rule, List<CheckAuthorizationResult> results) {
        if (!rule.getTest()) editUserVerificationStatus(rule);
        return AuthorizationResult.builder()
            .message("Accepted because of \""+rule.getRuleName()+"\". Verifying.")
            .successful(true)
            .rawResults(results)
            .build();
    }

    private AuthorizationResult buildReject(AuthorizationRule rule, List<CheckAuthorizationResult> results) {
      CheckAuthorizationResult result = results.get(results.size()-1);
      String errorMessage = (rule.getMessage() != null) ? rule.getMessage() : "Authorization failed.";
      if (!ObjectUtils.isEmpty(result.getTimeout()) && result.getTimeout()) {
        errorMessage = rule.getTimeoutMessage() != null ? rule.getTimeoutMessage() : errorMessage;
      } else if (!ObjectUtils.isEmpty(result.getReview()) && result.getReview()) {
        errorMessage = rule.getReviewMessage() != null ? rule.getReviewMessage() : errorMessage;
      }

      return AuthorizationResult.builder()
          .providerUrl(rule.getProviderUrl())
          .message("Rejected because of \""+rule.getRuleName()+"\"")
          .successful(false)
          .rawResults(results)
          .errorMessage(errorMessage)
          .rejectOutcome(result.getInitialOutcome())
          .build();
    }

    private boolean ruleSuccess(AuthorizationRule r, List<CheckAuthorizationResult> results) {
        CheckAuthorizationResult result = results.get(results.size()-1);
        if (result.isEnabled()) {
            if (result.isFound()) {
                //success
                if (!r.getExternal()) result.setPassed(true);
                result.setMessage("Rule successful. Next Action: "+r.getActionSuccess().name());
            } else {
                //fail
                if (!r.getExternal()) result.setPassed(false);
                result.setMessage("Rule failed. Next Action: "+r.getActionFailed().name());
            }
        } else {
            //fail
            if (!r.getExternal()) result.setPassed(false);
            result.setMessage("Rule failed. Next Action: "+r.getActionFailed().name());
        }
//        results.clear();
        return result.getPassed();
    }

    private List<AuthorizationRule> buildRules(AccessRule accessRule, Boolean test) {
        List<AccessControlList> accessControlList = accessRule.getAccessControlList();
        List<ExternalList> externalList = accessRule.getExternalList();
        List<AuthorizationRule> authorizationRules = new ArrayList<>();
        authorizationRules.addAll(
            accessControlList.stream()
            .filter(AccessControlList::isEnabled)
            .map(
                l -> AuthorizationRule.builder()
                .ruleset(l.getAccessRule())
                .ruleId(l.getId())
                .ruleName(l.getList().getName())
                .providerUrl(null)
                .external(false)
                .actionSuccess(l.getActionSuccess())
                .actionFailed(l.getActionFailed())
                .enabled(l.getList().isEnabled())
                .priority(l.getPriority())
                .ipResetTime(l.getIpResetTime())
                .validateOnce(null)
                .message((l.getMessage()!=null)?l.getMessage():accessRule.getDefaultMessage())
                .rule(l)
                .test(test)
                .build()
            )
            .collect(Collectors.toList())
        );
        authorizationRules.addAll(
            externalList.stream()
            .filter(ExternalList::isEnabled)
            .map(
                l -> AuthorizationRule.builder()
                .ruleset(l.getAccessRule())
                .ruleId(l.getId())
                .ruleName(l.getListName())
                .providerUrl(l.getProviderUrl())
                .external(true)
                .actionSuccess(l.getActionSuccess())
                .actionFailed(l.getActionFailed())
                .enabled(l.isEnabled())
                .priority(l.getPriority())
                .ipResetTime(null)
                .validateOnce(l.isValidateOnce())
                .message((l.getMessage()!=null)?l.getMessage():accessRule.getDefaultMessage())
                .timeoutMessage((l.getTimeoutMessage()!=null)?l.getTimeoutMessage():accessRule.getDefaultMessage())
                .reviewMessage((l.getReviewMessage()!=null)?l.getReviewMessage():accessRule.getDefaultMessage())
                .externalRule(l)
                .test(test)
                .build()
            )
            .collect(Collectors.toList())
        );
        // Default Rule, always there, always last.
        authorizationRules.add(
            AuthorizationRule.builder()
            .ruleset(accessRule)
            .ruleName("Default")
            .actionSuccess(accessRule.getDefaultAction())
            .enabled(true)
            .priority(Integer.MAX_VALUE)
            .message(accessRule.getDefaultMessage())
            .test(test)
            .build()
        );
        return authorizationRules.stream().sorted(Comparator.comparingInt(AuthorizationRule::getPriority)).collect(Collectors.toList());
    }

    /**
     * Perform service call to access provider implementation and return an outcome.
     * Saves the rule execution step(s) to DB.
     *
     * @param externalRule
     * @return ProviderAuthorizationResult
     */
    private CheckAuthorizationResult doExternalAuthorization(
        final ExternalList externalRule,
        final AuthorizationRequest authorizationRequest,
        final AccessRuleTransaction accessRuleTransaction
    ) {
        CheckAuthorizationResult checkAuthorizationResult = CheckAuthorizationResult.builder()
            .listName(externalRule.getListName())
            .listType("external")
            .enabled(externalRule.isEnabled())
            .priority(externalRule.getPriority())
            .actionSuccess(externalRule.getActionSuccess().name())
            .actionFailed(externalRule.getActionFailed().name())
            .passed(false)
            .timeout(false)
            .review(false)
            .found(false)
            .build();
      String providerUrl = externalRule.getProviderUrl();
      try {
          String domainName = externalRule.getAccessRule().getDomain().getName();
          ExternalAuthorizationRequest externalAuthorizationRequest = ExternalAuthorizationRequest.builder()
                .domainName(domainName)
                .deviceId(authorizationRequest.getDeviceId())
                .ruleName(externalRule.getListName())
                .ip(authorizationRequest.getIpAddress())
                .userGuid(authorizationRequest.getUserGuid())
                .playerBasic(authorizationRequest.getPlayerBasic())
                .additionalData(authorizationRequest.getAdditionalData())
                .build();
            ExternalAuthorizationClient authClient = services.target(ExternalAuthorizationClient.class, providerUrl, true);
            kycMetricService.startAttemptMetrics(domainName, ACCESS_SERVICE_ON_AUTHORIZE, providerUrl);
            Response<ProviderAuthorizationResult> authorizationResultResponse = authClient.checkAuthorization(externalAuthorizationRequest);
            log.debug("Authorization result response: " + authorizationResultResponse);
            ProviderAuthorizationResult providerAuthorizationResult;
            if (authorizationResultResponse.getStatus() == Response.Status.OK) {
                kycMetricService.passAttemptMetrics(domainName, ACCESS_SERVICE_ON_AUTHORIZE, providerUrl);
                if (authorizationResultResponse.getData() == null || authorizationResultResponse.getData().getRawDataList() == null) {
                    log.warn("No raw result data was returned from remote provider. Result: " + authorizationResultResponse);
                } else {
                    for (RawAuthorizationData authData : authorizationResultResponse.getData().getRawDataList()) {
                        accessRuleService.saveRuleStep(accessRuleTransaction, externalRule, authData);
                    }
                    providerAuthorizationResult = authorizationResultResponse.getData();
                    checkAuthorizationResult.setFound(true);
                    checkAuthorizationResult.setPassed(doStatusOutcomeToOutputMapping(externalRule, providerAuthorizationResult));
                    checkAuthorizationResult.setData(providerAuthorizationResult.getData());
                }
            } else {
                kycMetricService.failAttemptMetrics(domainName, ACCESS_SERVICE_ON_AUTHORIZE, providerUrl);
                log.error("Problem performing external access rule execution: " +
                    externalAuthorizationRequest.toString() +
                    " message: " + authorizationResultResponse.getMessage());
                // We want to get the access rule status configurations even  if the External Access Provider gives us timeout reponse during auth process..
                 if(authorizationResultResponse.getData().getAuthorisationOutcome() == EAuthorizationOutcome.TIMEOUT) {
                   providerAuthorizationResult = authorizationResultResponse.getData();
                   checkAuthorizationResult.setFound(false);
                   checkAuthorizationResult.setPassed(doStatusOutcomeToOutputMapping(externalRule, providerAuthorizationResult));
                }
            }
          checkAuthorizationResult.setInitialOutcome(authorizationResultResponse.getData().getAuthorisationOutcome());
          if (!checkAuthorizationResult.getPassed()) {
              checkAuthorizationResult.setAnswerMessage(externalRule.getMessage()); //Sets the reject Message as default, then we check whether there was a timeout reject or review reject outcome override.

              if(authorizationResultResponse.getData().getAuthorisationOutcome() == EAuthorizationOutcome.TIMEOUT) {
                checkAuthorizationResult.setTimeout(true);
                checkAuthorizationResult.setAnswerMessage(externalRule.getTimeoutMessage());
              }

              if(authorizationResultResponse.getData().getAuthorisationOutcome() == EAuthorizationOutcome.REVIEW) {
                checkAuthorizationResult.setReview(true);
                checkAuthorizationResult.setAnswerMessage(externalRule.getReviewMessage());
              }
            }
        } catch (FeignException e) {
            log.warn("Could  not communicate to external service: "+e.getMessage());
        } catch (Exception e) {
            // There was some weird issue in the call and we don't want to fail horribly.
            log.error("Unable to process remote access provider call url: " + providerUrl + " Request: " + authorizationRequest, e);
        }
        return checkAuthorizationResult;
        // TODO: 2019/10/10 This should possibly change to something other than timeout, will think on it a bit.
    }

    /**
     * Sanity check for auth rule execution. Will return null if the request should continue with actual rule processing
     *
     * @return Response<AuthorizationResult> or null
     */
    private AuthorizationResult sanityChecks(AccessRule accessRule, String accessRuleName) {
        if (accessRule == null) {
            return AuthorizationResult.builder()
                    .successful(false)
                    .message("Access rule " + accessRuleName + " not found.")
                    .build();
        }
        if (!accessRule.isEnabled()) {
            return AuthorizationResult.builder()
                    .successful(false)
                    .message("Access rule " + accessRuleName + " is disabled. " + accessRule.getDefaultMessage() + ".")
                    .build();
        }
        return null;
    }

    /**
     * Check which list type it is and perform required auth checks then add outcome of auth to the results object.
     *
     * @param acl
     * @param authorizationRequest
     * @return
     */
    private CheckAuthorizationResult doInternalAuthorization(
        final AccessControlList acl,
        final AuthorizationRequest authorizationRequest,
        final AccessRuleTransaction accessRuleTransaction
    ) {
        String listTypeName = acl.getList().getListType().getName();
        String listType = "";
        Map<String, String> authorisationResultData = new HashMap<>();
        boolean found = false;
        RawAuthorizationData rawAuthorizationData = RawAuthorizationData.builder()
                .rawRequestToProvider(null)
//                .rawRequestToProvider(createRawRequestJsonString(acl, authorizationRequest)) //LSNOC-291 This forced the entire ACL with all values to be fetched from DB - slow running query
                .build();

        if (listTypeName.equalsIgnoreCase(ListType.IP_LIST.type())) {
            listType = ListType.IP_LIST.type();
            found = (valueRepository.findByListAndDataIgnoreCase(acl.getList(), authorizationRequest.getIpAddress()) != null);
        } else if (listTypeName.equalsIgnoreCase(ListType.IP_RANGE.type())) {
            listType = ListType.IP_RANGE.type();
            found = ipExistsInIpRangeList(authorizationRequest.getIpAddress(), acl.getList().getValues());
        } else if (listTypeName.equalsIgnoreCase(ListType.COUNTRY_LIST.type())) {
            listType = ListType.COUNTRY_LIST.type();
            found = (valueRepository.findByListAndDataIgnoreCase(acl.getList(), authorizationRequest.getCountry()) != null);
        } else if (listTypeName.equalsIgnoreCase(ListType.COUNTRY_LIST_PROFILE.type())) {
            listType = ListType.COUNTRY_LIST_PROFILE.type();
            if (authorizationRequest.getClaimedCountry() != null) {
              found = (valueRepository.findByListAndDataIgnoreCase(acl.getList(), authorizationRequest.getClaimedCountry()) != null);
            }
        } else if (listTypeName.equalsIgnoreCase(ListType.STATE_LIST.type())) {
            listType = ListType.STATE_LIST.type();
            found = (valueRepository.findByListAndDataIgnoreCase(acl.getList(), authorizationRequest.getState()) != null);
        } else if (listTypeName.equalsIgnoreCase(ListType.STATE_LIST_PROFILE.type())) {
            listType = ListType.STATE_LIST_PROFILE.type();
            if (authorizationRequest.getClaimedState() != null) {
                found = (valueRepository.findByListAndDataIgnoreCase(acl.getList(), authorizationRequest.getClaimedState()) != null);
            }
        } else if (listTypeName.equalsIgnoreCase(ListType.CITY_LIST.type())) {
            listType = ListType.CITY_LIST.type();
            found = (valueRepository.findByListAndDataIgnoreCase(acl.getList(), authorizationRequest.getCity()) != null);
        } else if (listTypeName.equalsIgnoreCase(ListType.CITY_LIST_PROFILE.type())) {
            listType = ListType.CITY_LIST_PROFILE.type();
            if (authorizationRequest.getClaimedCity() != null) {
                found = (valueRepository.findByListAndDataIgnoreCase(acl.getList(), authorizationRequest.getClaimedCity()) != null);
            }
        } else if (listTypeName.equalsIgnoreCase(ListType.BROWSER_LIST.type())) {
            listType = ListType.BROWSER_LIST.type();
            found = (valueRepository.findByListAndDataIgnoreCase(acl.getList(), authorizationRequest.getBrowser()) != null);
        } else if (listTypeName.equalsIgnoreCase(ListType.OS_LIST.type())) {
            listType = ListType.OS_LIST.type();
            found = (valueRepository.findByListAndDataIgnoreCase(acl.getList(), authorizationRequest.getOs()) != null);
        } else if (listTypeName.equalsIgnoreCase(ListType.POST_LIST.type())) {
            listType = ListType.POST_LIST.type();
            found = (valueRepository.findByListAndDataIgnoreCase(acl.getList(), authorizationRequest.getPostCode()) != null);
            if (authorizationRequest.getPostCode() == null) {
                found = true;
            }
        } else if (listTypeName.equalsIgnoreCase(ListType.PLAYER_LIST.type())) {
          User user = null;

          try {
            user = userService.findByUsernameThenEmailThenCell(authorizationRequest.getUserGuid().split("/")[0],
                authorizationRequest.getUserGuid().split("/")[1]);
          } catch (LithiumServiceClientFactoryException e) {
            // Blacklisted ACLs also needs to be configured on post login access rules, incase this does break, then the second check will find userGuid
            log.error("Failed to find user by unique identifier: {} and domain: {}", authorizationRequest.getUserGuid().split("/")[1],
                authorizationRequest.getUserGuid().split("/")[0], e);
          }

          if(!ObjectUtils.isEmpty(user)){
            authorizationRequest.setUserGuid(user.guid());
          }
          listType = ListType.PLAYER_LIST.type();
          found = (valueRepository.findByListAndDataIgnoreCase(acl.getList(), authorizationRequest.getUserGuid()) != null);
        }
        else if (listTypeName.equalsIgnoreCase(ListType.DUPLICATE_CHECK.type())) {
          listType = ListType.DUPLICATE_CHECK.type();
          found = proceedDuplicateCheck(acl, authorizationRequest, authorisationResultData);
        }

        boolean aclListEnabled = acl.getList().isEnabled();
        boolean aclEnabled = acl.isEnabled();

        String answerMessage = null;

        if (found && aclEnabled && aclListEnabled) {
          answerMessage = (acl.getMessage()!=null)?acl.getMessage():acl.getAccessRule().getDefaultMessage();
        } else {
          answerMessage = acl.getAccessRule().getDefaultMessage();
        }
        final CheckAuthorizationResult checkAuthorizationResult = CheckAuthorizationResult.builder()
                .listName(acl.getList().getName())
                .enabled(acl.getList().isEnabled() && acl.isEnabled())
                .priority(acl.getPriority())
                .actionSuccess(acl.getActionSuccess().name())
                .actionFailed(acl.getActionFailed().name())
                .found(found)
                .listType(listType)
                .data(authorisationResultData)
                .answerMessage(answerMessage)
                .build();
        try {
            rawAuthorizationData.setRawResponseFromProvider(new ObjectMapper().writeValueAsString(checkAuthorizationResult));
        } catch (JsonProcessingException e) {
            log.warn("Unable to produce json string for raw access data: " + checkAuthorizationResult, e);
        }

        accessRuleService.saveRuleStep(accessRuleTransaction, acl, rawAuthorizationData);
        return checkAuthorizationResult;
    }

  private boolean proceedDuplicateCheck(AccessControlList acl, AuthorizationRequest authorizationRequest, Map<String, String> authorisationResultData) {
    try {
      Set<User> duplicates = duplicateChecksService.findUserDuplicates(authorizationRequest.getUserGuid(), acl.getList());
      boolean found = duplicates.size() > 0;
      if (found) {
        String userGuids = duplicates.stream().map(User::guid).collect(Collectors.joining(","));
        authorisationResultData.put("message", "Account is blocked due to suspected duplicated accounts : " + userGuids);
        authorisationResultData.put("signupEventComment", "User blocked " + duplicates.size() + " duplicates found");
      }
      return found;
    } catch (Status551ServiceAccessClientException | UserDuplicateCheckFailedException e) {
      log.error("Unable to proceed List.type duplicate_check for userGuid "+ authorizationRequest.getUserGuid() +"caused by: " + e.getMessage(), e);
      authorisationResultData.put("message", "Account blocked due to exception while processing duplicate check");
      return true;
    }
  }

  /**
     * Convenience aggregation method to create a json string value for the access list request data components.
     *
     * @param acl
     * @param authorizationRequest
     * @return JsonString
     */
    private String createRawRequestJsonString(final AccessControlList acl, final AuthorizationRequest authorizationRequest) {
        StringBuilder sb = new StringBuilder();
        try {
            ObjectMapper mapper = new ObjectMapper();
            sb.append("[");
            sb.append(mapper.writeValueAsString(acl));
            sb.append(",");
            sb.append(mapper.writeValueAsString(authorizationRequest));
            sb.append("]");
        } catch (JsonProcessingException e) {
            log.warn("Unable to serialize raw data for access transaction" + acl.toString() + " " + authorizationRequest.toString(), e);
        }
        return sb.toString();

    }

    /**
     * Perform the mapping operation where the provider status outcome is mapped to the configured output of the access rule
     *
     * @param externalRule
     * @param authorizationResult
     * @return boolean (pass or fail)
     */
    private boolean doStatusOutcomeToOutputMapping(ExternalList externalRule, ProviderAuthorizationResult authorizationResult) {
        Optional<ExternalListRuleStatusOptionConfig> data = externalRule.getExternalListRuleStatusOptionConfigList()
                .stream()
                .filter(config -> {
                    if (authorizationResult.getAuthorisationOutcome().name().contentEquals(config.getOutcome().getName())) {
                        return true;
                    }
                    return false;
                })
                .findFirst();

        if (data.isPresent()) {
            return (data.get().getOutput().getName().contentEquals(EAuthorizationOutcome.ACCEPT.name())) ? true : false;
        } else {
            // If no outcome to output mapping exists then pass EAuthorizationOutcome.ACCEPT, EAuthorizationOutcome.REVIEW, and EAuthorizationOutcome.TIMEOUT.
            // Fail all other possible outcomes.
            if (!(authorizationResult.getAuthorisationOutcome() == EAuthorizationOutcome.ACCEPT ||
                    authorizationResult.getAuthorisationOutcome() == EAuthorizationOutcome.REVIEW ||
                    authorizationResult.getAuthorisationOutcome() == EAuthorizationOutcome.TIMEOUT)) {
                return false;
            } else {
                return true;
            }
        }
    }

    private void checkForEnabledProvider(String domainName, ExternalList externalRule) {
        try {
            Provider provider = providerClientService.findProviderByUrlAndDomainName(domainName, externalRule.getProviderUrl());
            if (!provider.getEnabled()) {
                log.warn("Provider {} for external rule {} is disabled.", provider.getName(), externalRule.getListName());
            }
        } catch (Status550ServiceDomainClientException e) {
            log.error("Failed to retrieve domain from domain service | " + e.getMessage(), e);
        }
    }
}

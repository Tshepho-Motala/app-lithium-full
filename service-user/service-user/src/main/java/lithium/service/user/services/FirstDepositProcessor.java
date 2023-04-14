package lithium.service.user.services;

import static java.util.Optional.ofNullable;

import java.util.Map;
import lithium.service.access.client.AccessService;
import lithium.service.access.client.exceptions.Status551ServiceAccessClientException;
import lithium.service.access.client.objects.AuthorizationResult;
import lithium.service.cashier.client.event.ICashierFirstDepositProcessor;
import lithium.service.cashier.client.objects.SuccessfulTransactionEvent;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.user.data.entities.LoginEvent;
import lithium.service.user.data.entities.User;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class FirstDepositProcessor implements ICashierFirstDepositProcessor {
    @Autowired
    private AccessService accessService;
    @Autowired
    private UserService userService;
    @Autowired
    CachingDomainClientService cachingDomainClientService;
    @Override
    @Transactional
    public void processFirstDeposit(SuccessfulTransactionEvent request) throws Exception {
        log.debug("First deposit event is received: " + request);

        try {
          User user = userService.findFromGuid(request.getUserGuid());
          lithium.service.domain.client.objects.Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(user.domainName());

          String firstDepositRule = domain.getFirstDepositAccessRule();
          if (!StringUtil.isEmpty(firstDepositRule)) {
            AuthorizationResult authorizationResult = checkAuthorization(user, firstDepositRule);
            if (!authorizationResult.isSuccessful()) {
              handleUnsuccessfulAuthorizationResult(user, authorizationResult);
            }
          }
        } catch(Exception ex) {
          log.error("Failed to perform access check on first deposit for user: " + request.getUserGuid() + ". " + ex.getMessage(), ex);
        }
    }


  private AuthorizationResult checkAuthorization(User user, String ruleName) throws Status551ServiceAccessClientException {
      Map<String, String> ipAndUserAgentData = accessService.parseIpAndUserAgent(ofNullable(user.getLastLogin()).map(LoginEvent::getIpAddress).orElse("unknown"),
                                                                                  ofNullable(user.getLastLogin()).map(LoginEvent::getUserAgent).orElse("unknown"));

      AuthorizationResult authorizationResult = accessService.checkAuthorization(user.domainName(),
          ruleName, null, ipAndUserAgentData, null, user.guid(), false);
      log.info("AuthorizationResult on first deposit: " + authorizationResult);
      return authorizationResult;
  }

  private void handleUnsuccessfulAuthorizationResult(User user, AuthorizationResult authorizationResult) {
      //ignore for now. The KYC access rules is expected to be configured here. User verification status should be already updated.
    log.error("Access check is failed on first deposit for user: " + user.guid() + ". Result: " + authorizationResult);
  }
}

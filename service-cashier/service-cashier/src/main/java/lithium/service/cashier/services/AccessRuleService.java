package lithium.service.cashier.services;

import lithium.service.access.client.AccessService;
import lithium.service.access.client.exceptions.Status551ServiceAccessClientException;
import lithium.service.access.client.objects.AuthorizationResult;
import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class AccessRuleService {
	@Autowired
	private AccessService accessService;

	/**
	 * Authorization check for method display.
	 * @param domainMethod
	 * @param ipAddress
	 * @param userAgent
	 * @return
	 */
	public boolean checkAuthorization(DomainMethod domainMethod, String ipAddress, String userAgent) {
		return checkAuthorization(domainMethod.getDomain().getName(), domainMethod.getAccessRule(), ipAddress, userAgent, null, null);
	}

	/**
	 * Authorization check involving user and device acceptance in addition to geo and ip rules.
	 * @param domainMethod
	 * @param ipAddress
	 * @param userAgent
	 * @param deviceId
	 * @param userGuid
	 * @return
	 */
	public boolean checkAuthorization(DomainMethod domainMethod, String ipAddress, String userAgent, String deviceId, String userGuid) {
		return checkAuthorization(domainMethod.getDomain().getName(), domainMethod.getAccessRuleOnTranInit(), ipAddress, userAgent, deviceId, userGuid);
	}

	/**
	 * Authorization check for processor display.
	 * @param domainMethodProcessor
	 * @param ipAddress
	 * @param userAgent
	 * @return
	 */
	public boolean checkAuthorization(DomainMethodProcessor domainMethodProcessor, String ipAddress, String userAgent) {
		return checkAuthorization(domainMethodProcessor.getDomainMethod().getDomain().getName(), domainMethodProcessor.getDomainMethod().getAccessRule(), ipAddress, userAgent, null, null);
	}

	/**
	 * Authorization check involving user and device acceptance in addition to geo and ip rules.
	 * @param domainMethodProcessor
	 * @param ipAddress
	 * @param userAgent
	 * @param deviceId
	 * @param userGuid
	 * @return
	 */
	public boolean checkAuthorization(DomainMethodProcessor domainMethodProcessor, String ipAddress, String userAgent, String deviceId, String userGuid) {
		return checkAuthorization(domainMethodProcessor.getDomainMethod().getDomain().getName(), domainMethodProcessor.getDomainMethod().getAccessRuleOnTranInit(), ipAddress, userAgent, deviceId, userGuid);
	}

	/**
	 * Authorization lookup method.
	 * When a device id and userGuid is present, the access method used will also perform external type access checks (eg. iovation)
	 * If they are not present, the internal access rules are applied.
	 * @param domainName
	 * @param accessRuleName
	 * @param ipAddress
	 * @param userAgent
	 * @param deviceId
	 * @param userGuid
	 * @return
	 */
	private boolean checkAuthorization(String domainName, String accessRuleName, String ipAddress, String userAgent, String deviceId, String userGuid) {
		if (accessRuleName != null && !accessRuleName.isEmpty()) {
			Map<String, String> ipAndUserAgentData = accessService.parseIpAndUserAgent(ipAddress, userAgent);
			AuthorizationResult authorizationResult;
			try {
				if (deviceId == null && userGuid == null) {
					// Original access rule resolver
					authorizationResult = accessService.checkAuthorization(domainName, accessRuleName, null, ipAndUserAgentData);
				} else {
					/// Tran init type access rule resolver
					authorizationResult = accessService.checkAuthorization(domainName, accessRuleName, null, ipAndUserAgentData, deviceId, userGuid, false);
				}
			} catch (Status551ServiceAccessClientException e) {
				log.error("Could not checkAuthorization", e);
				return false;
			}
			log.debug("authorizationResult " + authorizationResult);
			boolean authSuccessful = (authorizationResult != null)? authorizationResult.isSuccessful(): true;
			return authSuccessful;
		}
		return true;
	}
}

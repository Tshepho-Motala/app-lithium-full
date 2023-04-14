package lithium.service.user.services;

import lithium.service.access.client.AccessService;
import lithium.service.access.client.objects.AuthorizationResult;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.objects.Domain;
import lithium.service.user.data.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class AccessRuleService {
	@Autowired
	private AccessService accessService;
	@Autowired
	private CachingDomainClientService cachingDomainClientService;

	public void userDetailsUpdateAccessRule(User user) throws Exception {
		Domain externalDomain = cachingDomainClientService.retrieveDomainFromDomainService(user.domainName());
		if (externalDomain.getUserDetailsUpdateAccessRule() != null && !externalDomain.getUserDetailsUpdateAccessRule().isEmpty()) {
			checkAuthorization(
				user.domainName(),
				externalDomain.getUserDetailsUpdateAccessRule(),
				(user.getLastLogin() != null)? user.getLastLogin().getIpAddress(): null,
				(user.getLastLogin() != null)? user.getLastLogin().getUserAgent(): null,
				null,
				user.guid(),
				true
			);
		}
	}

	private boolean checkAuthorization(String domainName, String accessRuleName, String ipAddress, String userAgent, String deviceId, String userGuid, boolean overrideValidateOnce) throws Exception {
		if (ipAddress == null) ipAddress = "unknown";
		if (userAgent == null) userAgent = "unknown";
		if (deviceId == null) deviceId = "unknown";
		Map<String, String> ipAndUserAgentData = accessService.parseIpAndUserAgent(ipAddress, userAgent);
		AuthorizationResult authorizationResult;
		authorizationResult = accessService.checkAuthorization(domainName, accessRuleName, null, ipAndUserAgentData, deviceId, userGuid, overrideValidateOnce);
		log.debug("authorizationResult " + authorizationResult);
		boolean authSuccessful = (authorizationResult != null)? authorizationResult.isSuccessful(): true;
		return authSuccessful;
	}
}

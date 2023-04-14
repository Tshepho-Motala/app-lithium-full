package lithium.service.pushmsg.services;

import java.util.Map;

import lithium.service.access.client.exceptions.Status551ServiceAccessClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.access.client.AccessService;
import lithium.service.access.client.objects.AuthorizationResult;
import lithium.service.pushmsg.data.entities.DomainProvider;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccessRuleService {
	@Autowired
	private AccessService accessService;
	
	public boolean checkAuthorization(DomainProvider domainProvider, String ipAddress, String userAgent) {
		String accessRule = domainProvider.getAccessRule();
		if (accessRule != null && !accessRule.isEmpty()) {
			Map<String, String> ipAndUserAgentData = accessService.parseIpAndUserAgent(ipAddress, userAgent);
			AuthorizationResult authorizationResult;
			try {
				authorizationResult = accessService.checkAuthorization(domainProvider.getDomain().getName(), accessRule, null, ipAndUserAgentData);
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
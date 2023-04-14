package lithium.service.access.provider.iovation.services;

import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import lithium.service.access.provider.iovation.config.Config;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.ProviderProperty;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccessProviderIovationService {
	@Autowired LithiumServiceClientFactory services;
	
	private Iterable<ProviderProperty> getProviderProperties(String providerGuid, String domainName) {
		ProviderClient pc = null;
		try {
			pc = services.target(ProviderClient.class, "service-domain", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Unable to get provider client for: " + providerGuid + " and domain: " + domainName);
		}
		if (pc == null) return null;
		return pc.propertiesByProviderUrlAndDomainName(providerGuid, domainName).getData();
	}
	
	public Map<String, String> getProviderPropertiesMap(String providerGuid, String domainName) {
		Iterable<ProviderProperty> properties = getProviderProperties(providerGuid, domainName);
		Map<String, String> propertiesMap = new LinkedHashMap<String, String>();
		for (ProviderProperty pp: properties) {
			propertiesMap.put(pp.getName(), pp.getValue());
		}
		return propertiesMap;
	}
	
	public HttpHeaders buildHeaders(Map<String, String> properties) throws Exception {
		final HttpHeaders headers = new HttpHeaders();
		String subscriberId = properties.get(Config.SUBSCRIBER_ID.property());
		String subscriberAccount = properties.get(Config.SUBSCRIBER_ACCOUNT.property());
		String subscriberPasscode = properties.get(Config.SUBSCRIBER_PASSCODE.property());
		if (subscriberId == null || subscriberId.isEmpty()) throw new Exception("Subscriber id not set.");
		if (subscriberAccount == null || subscriberAccount.isEmpty()) throw new Exception("Subscriber account not set.");
		if (subscriberPasscode == null || subscriberPasscode.isEmpty()) throw new Exception("Subscriber passcode not set.");
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Basic " +
			Base64.getEncoder().encodeToString((subscriberId + "/" + subscriberAccount + ":" + subscriberPasscode).getBytes()));
		return headers;
	}
}

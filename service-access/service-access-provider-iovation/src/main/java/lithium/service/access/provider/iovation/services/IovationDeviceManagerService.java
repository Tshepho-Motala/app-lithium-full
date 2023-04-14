package lithium.service.access.provider.iovation.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lithium.service.access.provider.iovation.config.Config;
import lithium.service.access.provider.iovation.data.DeviceRegistrationRequest;
import lithium.service.access.provider.iovation.data.DeviceRegistrationResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IovationDeviceManagerService {
	@Autowired AccessProviderIovationService accessProviderIovationService;
	@Autowired RestService restService;

	public DeviceRegistrationResponse registerDevice(String domainName, DeviceRegistrationRequest request) throws Exception {
		log.debug("registerDevice (" + request.toString() + ")");
		Map<String, String> properties = accessProviderIovationService.getProviderPropertiesMap("service-access-provider-iovation", domainName);
		String baseUrl = properties.get(Config.BASE_URL.property());
		String subscriberId = properties.get(Config.SUBSCRIBER_ID.property());
		if (baseUrl == null || baseUrl.isEmpty()) throw new Exception("Base url not set.");
		if (subscriberId == null || subscriberId.isEmpty()) throw new Exception("Subscriber id not set");
		request.setSubscriberId(subscriberId);
		final HttpEntity<DeviceRegistrationRequest> entity = new HttpEntity<>(request, accessProviderIovationService.buildHeaders(properties));
		String url = baseUrl + "/reg/v1/register";
		ResponseEntity<String> response = restService.restTemplate(domainName).exchange(
			url,
			HttpMethod.POST,
			entity,
			String.class
		);
		log.debug("Device registration response: " + response +  " body: " +  (response.getBody() != null ? response.getBody(): ""));

		//return response.getBody();
		// FIXME: 2019/09/18 Remove this once we know what we are receiving
		return new DeviceRegistrationResponse();
	}
}

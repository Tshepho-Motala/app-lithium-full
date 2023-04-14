package lithium.service.sms.provider;

import org.springframework.web.client.RestTemplate;

import lithium.service.sms.client.internal.DoProviderRequest;
import lithium.service.sms.client.internal.DoProviderResponse;

public interface DoProviderInterface {
	public DoProviderResponse send(DoProviderRequest request, RestTemplate restTemplate) throws Exception;
}
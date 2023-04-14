package lithium.service.sms.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lithium.service.sms.client.internal.DoProviderRequest;
import lithium.service.sms.client.internal.DoProviderResponse;

@Component
public class DoMessage {
	@Autowired DoProviderInterface provider;
	@Autowired RestTemplate rest;
	
	public DoProviderResponse run(DoProviderRequest request) throws Exception {
		return provider.send(request, rest);
	}
}
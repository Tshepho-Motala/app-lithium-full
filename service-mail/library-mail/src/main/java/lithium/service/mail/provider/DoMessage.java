package lithium.service.mail.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import lithium.service.mail.client.internal.DoProviderRequest;
import lithium.service.mail.client.internal.DoProviderResponse;

@Component
@Lazy
public class DoMessage {
	@Autowired DoProviderInterface provider;
	
	public DoProviderResponse run(DoProviderRequest request) throws Exception {
		return provider.send(request);
	}
}
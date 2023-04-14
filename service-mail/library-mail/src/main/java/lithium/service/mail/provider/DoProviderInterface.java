package lithium.service.mail.provider;

import lithium.service.mail.client.internal.DoProviderRequest;
import lithium.service.mail.client.internal.DoProviderResponse;

public interface DoProviderInterface {
	public DoProviderResponse send(DoProviderRequest request) throws Exception;
}
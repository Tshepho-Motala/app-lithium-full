package lithium.service.mail.services.provider;

import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.mail.client.internal.DoProviderClient;
import lithium.service.mail.client.internal.DoProviderRequest;
import lithium.service.mail.client.internal.DoProviderResponse;
import lithium.service.mail.data.entities.DomainProvider;
import lithium.service.mail.data.entities.DomainProviderProperty;
import lithium.service.mail.services.DomainProviderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class DoProvider {
	@Autowired LithiumServiceClientFactory serviceFactory;
	@Autowired DomainProviderService dpService;
	
	public DoProviderResponse run(DomainProvider domainProvider, DoProviderRequest request) throws Exception {
		try {
			DoProviderClient client = serviceFactory.target(DoProviderClient.class,
				(domainProvider != null)? domainProvider.getProvider().getUrl(): "service-mail-provider-smtp", true);
			if (domainProvider != null) {
                Map<String, String> properties = request.getProperties();
                if (properties == null) properties = new HashMap<>();

				for (DomainProviderProperty prop: dpService.propertiesWithDefaults(domainProvider.getId())) {
					properties.put(prop.getProviderProperty().getName(), prop.getValue());
				}
				request.setProperties(properties);
			}
			return client.doPost(request);
		} catch (Exception e) {
			log.error("Call to provider failed. " + e.getMessage(), e);
			return null;
		}
	}
	
	public void processProviderCallback(DoProviderResponse response) throws Exception {
	}
}
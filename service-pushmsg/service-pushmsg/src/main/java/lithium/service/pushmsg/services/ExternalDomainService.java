package lithium.service.pushmsg.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.DomainClient;
import lithium.service.domain.client.objects.Domain;

@Service
public class ExternalDomainService {
	@Autowired LithiumServiceClientFactory services;
	
	public Domain getExternalDomain(String domainName) throws LithiumServiceClientFactoryException {
		DomainClient domainClient = services.target(DomainClient.class, "service-domain", true);
		Response<Domain> response = domainClient.findByName(domainName);
		if (response.isSuccessful()) {
			return response.getData();
		}
		return null;
	}
}
package lithium.service.access.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.DomainClient;
import lithium.service.domain.client.objects.Domain;

@Service
public class ExternalDomainService {
	@Autowired LithiumServiceClientFactory serviceFactory;
	
	public Domain findByName(String name) throws Exception {
		DomainClient client = serviceFactory.target(DomainClient.class, true);
		Response<Domain> response = client.findByName(name);
		Domain domain = response.getData();
		if (domain == null) throw new Exception("No such domain");
		if (!domain.getEnabled()) throw new Exception("Domain disabled");
		if (domain.getDeleted()) throw new Exception("Domain does not exist");
		return domain;
	}
}
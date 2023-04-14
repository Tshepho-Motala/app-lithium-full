package lithium.service.user.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.domain.client.DomainClient;
import lithium.service.domain.client.objects.Domain;

@Service
public class ExternalUserService {
	
	@Autowired LithiumServiceClientFactory serviceFactory;

	public Domain findByName(String name) throws Exception {
		DomainClient client = serviceFactory.target(DomainClient.class, true);
		Response<Domain> response = client.findByName(name);
		return response.getData();
	}

}

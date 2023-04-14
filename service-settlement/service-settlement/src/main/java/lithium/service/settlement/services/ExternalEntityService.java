package lithium.service.settlement.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.entity.client.EntityClient;
import lithium.service.entity.client.objects.Entity;

@Service
public class ExternalEntityService {
	@Autowired LithiumServiceClientFactory serviceFactory;
	
	public Entity findByUuid(String uuid) throws Exception {
		EntityClient client = serviceFactory.target(EntityClient.class, true);
		Response<Entity> response = client.findByUuid(uuid);
		return response.getData();
	}
}
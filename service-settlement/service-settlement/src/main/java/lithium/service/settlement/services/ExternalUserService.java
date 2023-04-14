package lithium.service.settlement.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.user.client.UserApiInternalClient;
import lithium.service.user.client.objects.User;

@Service
public class ExternalUserService {
	@Autowired LithiumServiceClientFactory serviceFactory;
	
	public User findByGuid(String guid) throws Exception {
		UserApiInternalClient client = serviceFactory.target(UserApiInternalClient.class, true);
		Response<User> response = client.getUser(guid);
		return response.getData();
	}
}
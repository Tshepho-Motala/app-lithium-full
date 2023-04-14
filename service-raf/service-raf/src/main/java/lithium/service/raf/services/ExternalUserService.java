package lithium.service.raf.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.user.client.UserApiInternalClient;
import lithium.service.user.client.objects.User;

@Service
public class ExternalUserService {
	@Autowired LithiumServiceClientFactory services;
	
	public User getExternalUser(String userGuid) throws LithiumServiceClientFactoryException {
		UserApiInternalClient userClient = services.target(UserApiInternalClient.class, "service-user", true);
		Response<User> response = userClient.getUser(userGuid);
		if (response.isSuccessful()) {
			return response.getData();
		}
		return null;
	}

}

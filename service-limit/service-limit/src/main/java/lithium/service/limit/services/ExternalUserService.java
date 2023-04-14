package lithium.service.limit.services;

import lithium.service.user.client.UserClient;
import lithium.service.user.client.service.UserApiInternalClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.user.client.UserApiInternalClient;
import lithium.service.user.client.objects.User;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExternalUserService {
	@Autowired LithiumServiceClientFactory serviceFactory;

	/**
	 * @deprecated
	 * @see UserApiInternalClientService#getUserByGuid(java.lang.String)
	 */
	@Deprecated
	public User findByGuid(String guid) throws Exception {
		UserApiInternalClient client = serviceFactory.target(UserApiInternalClient.class, true);
		Response<User> response = client.getUser(guid);
		return response.getData();
	}

	public List<User> getUsersByGuids(List<String> guids) throws Exception{
		List<User> users = new ArrayList<>();

		if(guids != null && guids.size() > 0) {
			UserApiInternalClient client = serviceFactory.target(UserApiInternalClient.class, true);
			Response<List<User>> response = client.getUsers(guids);
			users =  response.getData();
		}

		return users;
	}

}
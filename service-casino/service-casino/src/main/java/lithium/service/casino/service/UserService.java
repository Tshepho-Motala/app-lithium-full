package lithium.service.casino.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Optional;

import lithium.leader.LeaderCandidate;
import lithium.service.casino.data.entities.Domain;
import lithium.service.casino.data.repositories.UserRepository;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.objects.UserAttributesData;
import lithium.service.user.client.service.UserApiInternalClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.user.client.UserClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {
	@Autowired private LithiumServiceClientFactory serviceFactory;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserApiInternalClientService userApiInternalClientService;

	@Autowired
	private DomainService domainService;
	@Autowired
	private LeaderCandidate leaderCandidate;

	public User findUserByGuid(String playerGuid) throws UserNotFoundException, UserClientServiceFactoryException {
		return userApiInternalClientService.getUserByGuid(playerGuid);
	}
	
	public lithium.service.user.client.objects.User svcUserGetUser(String domainName, String username) {
		UserClient userClient;
		try {
			userClient = serviceFactory.target(UserClient.class);

		Response<lithium.service.user.client.objects.User> userResponse = userClient.user(domainName, username, Collections.emptyMap());
		log.debug("Response :: "+userResponse);
		if (!userResponse.isSuccessful()) return null;
		
		return userResponse.getData();
		
		} catch (Exception e) {
			log.error("Unable to get user service", e);
		}
		
		return null;
	}
	
	public String hashSha256UserGuid(lithium.service.user.client.objects.User user) {
		MessageDigest digest;
		try {
			
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest((user.getDomain().getName()+"/"+user.getUsername()).getBytes(StandardCharsets.UTF_8));
			return bytesToHex(hash);
		} catch (NoSuchAlgorithmException e) {
			log.warn("Unable to hash user: " + user, e);
		}
		return null;
	}
	
	private static String bytesToHex(byte[] hash) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if(hex.length() == 1) hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}

	public void processUserAttributesData(UserAttributesData data) {
		lithium.service.casino.data.entities.User user = Optional.ofNullable(userRepository.findByGuid(data.getGuid()))
			.orElse(lithium.service.casino.data.entities.User.builder().guid(data.getGuid()).build());
		if (user.getDomain() == null) {
			user.setDomain(domainService.findOrCreate(user.getGuid().split("/")[0]));
		}
		user.setTestAccount(data.isTestAccount());
		userRepository.save(user);
	}
}
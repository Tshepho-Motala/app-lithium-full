package lithium.service.changelog.services;

import lithium.leader.LeaderCandidate;
import lithium.service.changelog.data.entities.User;
import lithium.service.changelog.data.repositories.UserRepository;
import lithium.service.user.client.objects.UserAttributesData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

	@Autowired UserRepository userRepository;
	@Autowired LeaderCandidate leaderCandidate;

	@Retryable
	public User findOrCreate(String guid) {
		User user = userRepository.findByGuid(guid.toLowerCase());
		if (user != null) return user;
		return userRepository.save(User.builder().guid(guid.toLowerCase()).build());
	}

	public void processUserAttributesData(UserAttributesData data) {
		User user = Optional.ofNullable(userRepository.findByGuid(data.getGuid()))
				.orElse(User.builder().guid(data.getGuid()).build());
		user.setTestAccount(data.isTestAccount());
		userRepository.save(user);
	}

}

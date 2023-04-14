package lithium.service.notifications.services;

import lithium.leader.LeaderCandidate;
import lithium.service.user.client.objects.UserAttributesData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.notifications.data.entities.User;
import lithium.service.notifications.data.repositories.UserRepository;

import java.util.Optional;

@Service
public class UserService {
	@Autowired UserRepository userRepository;
	@Autowired LeaderCandidate leaderCandidate;
	
	public User findOrCreate(String guid) {
		User user = userRepository.findByGuid(guid);
		if (user == null) user = userRepository.save(User.builder().guid(guid).build());
		return user;
	}

	public void processUserAttributesData(UserAttributesData data) {
		User user = Optional.ofNullable(userRepository.findByGuid(data.getGuid()))
				.orElse(User.builder().guid(data.getGuid()).build());
		user.setTestAccount(data.isTestAccount());
		userRepository.save(user);
	}

}

package lithium.service.leaderboard.services;

import lithium.leader.LeaderCandidate;
import lithium.service.user.client.objects.UserAttributesData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.leaderboard.data.entities.User;
import lithium.service.leaderboard.data.repositories.UserRepository;

import java.util.Optional;

@Service
public class UserService {
	@Autowired UserRepository userRepository;
	@Autowired
	private LeaderCandidate leaderCandidate;
	
	public User findOrCreate(String guid) {
		User user = userRepository.findByGuid(guid);
		if (user == null) user = userRepository.save(User.builder().guid(guid).build());
		return user;
	}
	
	public User find(String guid) {
		User user = userRepository.findByGuid(guid);
		if (user != null) return user;
		return null;
	}
	
	public User optout(String guid) {
		User user = findOrCreate(guid);
		user.setOptOut(!user.getOptOut());
		return userRepository.save(user);
	}
	public void processUserAttributesData(UserAttributesData data) {
		User user = Optional.ofNullable(userRepository.findByGuid(data.getGuid()))
			.orElse(User.builder().guid(data.getGuid()).build());
		user.setTestAccount(data.isTestAccount());
		userRepository.save(user);
	}
}

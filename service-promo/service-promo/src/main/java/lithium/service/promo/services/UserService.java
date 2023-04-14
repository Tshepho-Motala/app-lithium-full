package lithium.service.promo.services;

import java.time.ZoneId;
import java.util.Optional;

import lithium.leader.LeaderCandidate;
import lithium.service.promo.exceptions.Status411InvalidUserCreateException;
import lithium.service.user.client.objects.UserAttributesData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.promo.data.entities.User;
import lithium.service.promo.data.repositories.UserRepository;

@Service
public class UserService {
	@Autowired UserRepository userRepository;
	@Autowired LeaderCandidate leaderCandidate;
	
	public User findOrCreate(String guid, String timezone) throws Exception {
		String[] domainAndUser = guid.split("/");
		if (domainAndUser.length != 2 || domainAndUser[0].isEmpty() || domainAndUser[1].isEmpty())
			throw new Exception("Invalid guid");
		
		if (timezone == null) findOrCreate(guid);

		User user = userRepository.findByGuid(guid);
		if (user == null) user = userRepository.save(User.builder().guid(guid).timezone(timezone).build());
		return user;
	}
	public User findOrCreate(String guid) throws Status411InvalidUserCreateException {
		String[] domainAndUser = guid.split("/");
		if (domainAndUser.length != 2 || domainAndUser[0].isEmpty() || domainAndUser[1].isEmpty()) {
			throw new Status411InvalidUserCreateException();
		}
		
		User user = userRepository.findByGuid(guid);
		if (user == null) user = userRepository.save(User.builder().guid(guid).timezone(ZoneId.systemDefault().getId()).build());
		return user;
	}
	public User find(String guid) {
		User user = userRepository.findByGuid(guid);
		if (user != null) return user;
		return null;
	}

	public void processUserAttributesData(UserAttributesData data) {
		User user = Optional.ofNullable(userRepository.findByGuid(data.getGuid()))
				.orElse(User.builder().guid(data.getGuid()).build());
		user.setTestAccount(data.isTestAccount());
		userRepository.save(user);
	}
}

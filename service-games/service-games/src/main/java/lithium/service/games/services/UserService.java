package lithium.service.games.services;

import lithium.leader.LeaderCandidate;
import lithium.service.user.client.objects.UserAttributesData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.games.data.entities.User;
import lithium.service.games.data.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class UserService {
	@Autowired 
	private UserRepository userRepository;
	@Autowired
	private LeaderCandidate leaderCandidate;
	
	public User findOrCreate(String userGuid) {
		log.debug("Find/Create User : "+userGuid);
		User user = userRepository.findByGuid(userGuid);
		if (user == null) user = userRepository.save(User.builder().guid(userGuid).build());
		return user;
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public User findForUpdate(Long userId) {
		return userRepository.findForUpdate(userId);
	}

	public void processUserAttributesData(UserAttributesData data) {
		User user = Optional.ofNullable(userRepository.findByGuid(data.getGuid()))
				.orElse(User.builder().guid(data.getGuid()).build());
		user.setTestAccount(data.isTestAccount());
		userRepository.save(user);
	}

}
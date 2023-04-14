package lithium.service.kyc.service;

import lithium.leader.LeaderCandidate;
import lithium.service.kyc.entities.User;
import lithium.service.kyc.repositories.UserRepository;
import lithium.service.user.client.objects.UserAttributesData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserService {
	@Autowired
	UserRepository userRepository;
	@Autowired
	private LeaderCandidate leaderCandidate;

	public void processUserAttributesData(UserAttributesData data) {
		User user = Optional.ofNullable(userRepository.findByGuid(data.getGuid()))
				.orElse(User.builder().guid(data.getGuid()).build());
		user.setTestAccount(data.isTestAccount());
		userRepository.save(user);
	}
}
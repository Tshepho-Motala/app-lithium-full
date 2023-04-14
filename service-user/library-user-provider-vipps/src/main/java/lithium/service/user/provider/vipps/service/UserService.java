package lithium.service.user.provider.vipps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.user.provider.vipps.domain.AuthAttempt;
import lithium.service.user.provider.vipps.domain.User;
import lithium.service.user.provider.vipps.repository.UserRepository;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AuthAttemptService authAttemptService;
	
	public User findOrCreate(String domainName, String userId) {
		User user = userRepository.findByDomainNameAndUserId(domainName, userId);
		if (user == null) user = User.builder().userId(userId).domainName(domainName).build();
		return user;
	}
	
	public User find(String domainName, String userId) {
		User user = userRepository.findByDomainNameAndUserId(domainName, userId);
		return user;
	}
	
	public User save(User user) {
		return userRepository.save(user);
	}
	
	public void delete(String domainName, String userId) {
		User user = find(domainName, userId);
		if (user == null) return;
		user.setDeleted(true);
//		UserDetails  userDetails = user.getCurrentUserDetails();
//		if (userDetails == null) return;
		AuthAttempt authAttempt = user.getCurrentAuthAttempt();
		authAttempt = authAttemptService.obfuscateAndSaveAuthAttempt(authAttempt);
		user.setCurrentAuthAttempt(authAttempt);
		save(user);
	}
}

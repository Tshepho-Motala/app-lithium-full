package lithium.service.stats.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.stats.data.entities.User;
import lithium.service.stats.data.repositories.UserRepository;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	
	public User findOrCreate(String guid) {
		User user = userRepository.findByGuid(guid);
		if (user == null) {
			user = User.builder().guid(guid).build();
			userRepository.save(user);
		}
		return user;
	}
	
}

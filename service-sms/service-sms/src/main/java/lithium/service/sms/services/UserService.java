package lithium.service.sms.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.sms.data.entities.User;
import lithium.service.sms.data.repositories.UserRepository;

@Service
public class UserService {
	@Autowired UserRepository userRepository;
	
	public User findOrCreate(String guid) {
		User user = userRepository.findByGuid(guid);
		if (user == null) user = userRepository.save(User.builder().guid(guid).build());
		return user;
	}
}
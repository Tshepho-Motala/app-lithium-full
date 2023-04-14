package lithium.service.product.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.product.data.entities.User;
import lithium.service.product.data.repositories.UserRepository;

@Service
public class UserService {
	@Autowired UserRepository userRepository;
	
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
}
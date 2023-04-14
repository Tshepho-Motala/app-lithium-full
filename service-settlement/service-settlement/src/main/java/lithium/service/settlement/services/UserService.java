package lithium.service.settlement.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.settlement.data.entities.User;
import lithium.service.settlement.data.repositories.UserRepository;

@Service
public class UserService {
	@Autowired UserRepository repo;
	
	public User findOrCreate(String guid) {
		User user = repo.findByGuid(guid);
		if (user == null) {
			user = User.builder().guid(guid).build();
			user = repo.save(user);
		}
		return user;
	}
}

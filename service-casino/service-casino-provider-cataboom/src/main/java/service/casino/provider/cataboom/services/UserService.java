package service.casino.provider.cataboom.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import service.casino.provider.cataboom.entities.User;
import service.casino.provider.cataboom.repositories.UserRepository;
@Slf4j
@Service
public class UserService {
@Autowired
UserRepository userRepo;

	public void createUserIfNotExist(String playerGuid) {
		User user=userRepo.findByPlayerGuid(playerGuid);
		if(user == null) {
			User obj=new User();
			obj.setPlayerGuid(playerGuid);
			userRepo.save(obj);
			log.info("created user: "+ obj.toString());
		}
}
}
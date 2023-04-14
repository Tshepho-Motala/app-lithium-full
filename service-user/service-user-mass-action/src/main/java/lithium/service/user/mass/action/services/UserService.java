package lithium.service.user.mass.action.services;

import lithium.service.user.mass.action.data.entities.User;
import lithium.service.user.mass.action.data.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User findOrCreate(String guid) {
        User user = userRepository.findByGuid(guid.toLowerCase());
        if (user == null) {
            user = User.builder().guid(guid.toLowerCase()).build();
            userRepository.save(user);
        }
        return user;
    }
}
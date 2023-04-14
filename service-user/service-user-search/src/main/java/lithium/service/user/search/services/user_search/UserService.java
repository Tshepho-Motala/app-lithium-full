package lithium.service.user.search.services.user_search;

import lithium.service.user.search.data.entities.User;
import lithium.service.user.search.data.repositories.user_search.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service(value = "user_search.UserService")
public class UserService {

  @Autowired
  @Qualifier("user_search.UserRepository")
  private UserRepository userRepository;


  public User findOrCreateUser(String userGuid) {
    User user = userRepository.findUserByGuid(userGuid);
    if (user == null) {
      User newUser = User.builder().guid(userGuid).build();
      user = save(newUser);
      log.info("Created new user ({}): {}", user.getId(), user);
    }
    return user;
  }

  public User save(User user) {
    return userRepository.save(user);
  }

  @Transactional(propagation = Propagation.MANDATORY)
  public User lockingUpdate(String userGuid) {
    try {
      User user = userRepository.findForUpdate(userGuid);
      log.trace("Acquired lock on guid: {}, user: {}", userGuid, user);
      return user;
    } catch (Exception e) {
      log.error("Failed to acquire lock on guid: {} | {}", userGuid, e.getMessage(), e);
      User user = userRepository.findUserByGuid(userGuid);
      log.trace("User from DB: {}", user);
      throw e;
    }
  }
}

package lithium.service.user.search.services.cashier;

import lithium.service.cashier.data.entities.User;
import lithium.service.user.search.data.repositories.cashier.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service(value = "cashier.UserService")
public class UserService {

  @Autowired
  @Qualifier("cashier.UserRepository")
  UserRepository userRepository;

  public User find(String userGuid) {
    return userRepository.findByGuid(userGuid);
  }
}

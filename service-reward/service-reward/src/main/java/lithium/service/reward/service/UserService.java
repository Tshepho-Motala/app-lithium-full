package lithium.service.reward.service;

import lithium.service.reward.data.entities.User;
import lithium.service.reward.data.repositories.UserRepository;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  UserApiInternalClientService userApiInternalClientService;

  @Retryable(maxAttempts = 10, backoff = @Backoff(delay = 10, maxDelay = 100, random = true))
  public User findOrCreate(String guid) {
    User user = userRepository.findByGuid(guid);
    if (user == null) {
      user = populateFromSvcUser(guid, User.builder().guid(guid).build());
    } else if (user.getApiToken() == null || user.getOriginalId() == null) {
      user = populateFromSvcUser(guid, user);
    }
    return user;
  }

  private User populateFromSvcUser(String guid, User user) {
    try {
      lithium.service.user.client.objects.User svcUser = userApiInternalClientService.getUserByGuid(guid);
      user.setApiToken(svcUser.getUserApiToken().getToken());
      user.setOriginalId(svcUser.getId().toString());
      user = userRepository.save(user);
    } catch (Exception e) {//TODO: add more specific exception?
      log.error("Could not retrieve user from svc-user", e);
    } catch (UserClientServiceFactoryException e) {
      log.error("Could not retrieve user from svc-user", e);
    }
    return user;
  }

}

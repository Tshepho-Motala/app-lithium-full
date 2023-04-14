package lithium.service.user.provider.threshold.services.impl;


import java.util.Optional;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.jpa.exceptions.CannotAcquireLockException;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.user.client.UserApiInternalClient;
import lithium.service.user.provider.threshold.data.entities.User;
import lithium.service.user.provider.threshold.data.repositories.UserRepository;
import lithium.service.user.provider.threshold.services.DomainService;
import lithium.service.user.provider.threshold.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

  @Autowired
  private LithiumServiceClientFactory serviceFactory;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private DomainService domainService;

  @Override
  public User save(User user) {
    return userRepository.save(user);
  }

  @Override
  public Iterable<User> findAll() {
    return userRepository.findAll();
  }

  @Override
  public Optional<User> findOne(Long id) {
    return userRepository.findById(id);
  }

  @Override
  public User findByGuid(String userGuid) {
    return userRepository.findByGuid(userGuid);
  }

  private lithium.service.user.client.objects.User getUserDetails(String userGuid) throws Status500InternalServerErrorException {
    UserApiInternalClient userClient;
    try {
      userClient = serviceFactory.target(UserApiInternalClient.class);

      Response<lithium.service.user.client.objects.User> userResponse = userClient.getUser(userGuid);
      log.debug("Response :: " + userResponse);
      if (!userResponse.isSuccessful()) {
        return null;
      }

      return userResponse.getData();

    } catch (Exception e) {
      log.trace("LithiumServiceClientFactoryException: Message = " + e.getMessage() + ". Failed from = " + e.getStackTrace() + ". User = " + userGuid + ".");
      throw new Status500InternalServerErrorException(e.getMessage(), e);
    }
  }

  public User updateUser(User user, boolean isTestAccount)
  throws Status500InternalServerErrorException
  {
    if (isTestAccount != user.isTestAccount()) {
      user.setTestAccount(isTestAccount);
      user = userRepository.save(user);
    }
    if (user.getDobDay() <= 0) {
      lithium.service.user.client.objects.User userDetails = getUserDetails(user.getGuid());
      if (userDetails != null) {
        if(userDetails.getDobDay()!=null && userDetails.getDobMonth()!=null && userDetails.getDobYear()!=null) {
          user.setDobDay(userDetails.getDobDay());
          user.setDobMonth(userDetails.getDobMonth());
          user.setDobYear(userDetails.getDobYear());
        }
        user.setName(userDetails.getUsername());
        user = userRepository.save(user);
      }
    }
    return user;
  }

  @Override
  @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
  @Retryable(maxAttempts=100, backoff = @Backoff(value = 10, delay = 10))
  public User findOrCreate(String userGuid)
  throws Status500InternalServerErrorException
  {
    User user;

    try {
      user = userRepository.findOrCreateByGuidAlwaysLock(userGuid, () -> User.builder().notifications(true).domain(domainService.findOrCreate(userGuid.split("/")[0])).build());
    } catch (CannotAcquireLockException exception) {
      throw new Status500InternalServerErrorException("Unable to lock user. "
          + "Did you send more than one request for the same user at the same time?", exception);
    } catch (Exception exception) {
      throw new Status500InternalServerErrorException("Unable to create user. "
          + "This will be retried at a later stage in another call.", exception);
    }

    return user;
  }
}


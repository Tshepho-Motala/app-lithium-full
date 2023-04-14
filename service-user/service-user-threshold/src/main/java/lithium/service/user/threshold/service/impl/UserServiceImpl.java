package lithium.service.user.threshold.service.impl;


import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.jpa.exceptions.CannotAcquireLockException;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.user.client.UserApiInternalClient;
import lithium.service.user.threshold.data.entities.Domain;
import lithium.service.user.threshold.data.entities.User;
import lithium.service.user.threshold.data.repositories.UserRepository;
import lithium.service.user.threshold.service.DomainService;
import lithium.service.user.threshold.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private LithiumServiceClientFactory serviceFactory;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private DomainService domainService;

  public User save(User user) {
    return userRepository.save(user);
  }

  public User findByGuid(String userGuid) {
    return userRepository.findByGuid(userGuid);
  }

  private lithium.service.user.client.objects.User getUserDetails(String userGuid)
  throws Status500InternalServerErrorException
  {
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
      log.trace("LithiumServiceClientFactoryException: Message = " + e.getMessage() + ". Failed from = " + e.getStackTrace() + ". User = " + userGuid
          + ".");
      throw new Status500InternalServerErrorException(e.getMessage(), e);
    }
  }

  @Override
  public boolean updateNotifications(String playerGuid, boolean notifications)
  throws Status500InternalServerErrorException
  {
    User user = findByGuid(playerGuid);
    if (user == null) {
      user = findOrCreate(playerGuid);
    }
    user.setNotifications(notifications);
    user = save(user);
    return user.isNotifications();
  }

  @Override
  public boolean getNotifications(String playerGuid)
  throws Status500InternalServerErrorException
  {
    User user = findByGuid(playerGuid);
    if (user == null) {
      user = findOrCreate(playerGuid);
    }
    return user.isNotifications();
  }

  public User updateUser(User user, boolean isTestAccount)
  throws Status500InternalServerErrorException
  {
    if (isTestAccount != user.isTestAccount()) {
      user.setTestAccount(isTestAccount);
      user = userRepository.save(user);
    }
    if (ObjectUtils.isEmpty(user.getDobDay())) {
      lithium.service.user.client.objects.User userDetails = getUserDetails(user.getGuid());
      if (userDetails != null) {
        user.setDobDay(userDetails.getDobDay());
        user.setDobMonth(userDetails.getDobMonth());
        user.setDobYear(userDetails.getDobYear());
        user.setUsername(userDetails.getUsername());
        user.setAccountCreationDate(userDetails.getCreatedDate());
        user = userRepository.save(user);
      }
    }
    return user;
  }

  public int playerAge(User user) {
    LocalDate playerDob = LocalDate.of(user.getDobYear(), user.getDobMonth(), user.getDobDay());
    return Period.between(playerDob, LocalDate.now()).getYears();
  }

  private User findOrCreate(String playerGuid)
  throws Status500InternalServerErrorException
  {
    Domain domain = domainService.findOrCreate(playerGuid.split("/")[0]);
    return findOrCreate(playerGuid, domain);
  }

  @Override
  public User findOrCreate(String userGuid, Domain domain)
  throws Status500InternalServerErrorException
  {
    User user;

    try {
      user = userRepository.findOrCreateByGuidAlwaysLock(userGuid, () -> User.builder().notifications(true).domain(domain).build());
    } catch (CannotAcquireLockException exception) {
      throw new Status500InternalServerErrorException(
          "Unable to lock user. " + "Did you send more than one request for the same user at the same time?", exception);
    } catch (Exception exception) {
      throw new Status500InternalServerErrorException("Unable to create user. " + "This will be retried at a later stage in another call.",
          exception);
    }

    return user;
  }
}

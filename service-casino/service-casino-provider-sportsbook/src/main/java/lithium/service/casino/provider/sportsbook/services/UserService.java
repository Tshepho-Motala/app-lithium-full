package lithium.service.casino.provider.sportsbook.services;


import lithium.exceptions.Status500InternalServerErrorException;
import lithium.jpa.exceptions.CannotAcquireLockException;
import lithium.service.casino.provider.sportsbook.storage.entities.Domain;
import lithium.service.casino.provider.sportsbook.storage.entities.User;
import lithium.service.casino.provider.sportsbook.storage.repositories.UserRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserService {

  @Autowired
  @Setter
  UserRepository userRepository;

  /**
   * This was created to handle LSPLAT-1429
   *
   * @param guid
   * @param domain
   * @return
   * @throws Status500InternalServerErrorException
   */
  @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
  @Retryable(maxAttempts=100, backoff = @Backoff(value = 10, delay = 10))
  public User findOrCreateByGuid(String guid, Domain domain) throws
      Status500InternalServerErrorException {

    User user = null;

    try {
      user = userRepository.findOrCreateByGuidAlwaysLock(guid, () -> User.builder().domain(domain).build());
    } catch (CannotAcquireLockException exception) {
      throw new Status500InternalServerErrorException("Unable to lock user. "
          + "Did you send more than one request for the same user at the same time?", exception);
    } catch (Exception exception) {
      throw new Status500InternalServerErrorException("Unable to create user. "
          + "This will be retried at a later stage in another call.", exception);
    }

    return user;
  }

  @Retryable(maxAttempts = 20, backoff = @Backoff(delay = 10, maxDelay = 50))
  public User findOrCreateByGuidNoLock(String guid, Domain domain) {
    return userRepository.findOrCreateByGuid(guid, () -> User.builder().domain(domain).build());
  }

  @Transactional(propagation = Propagation.MANDATORY)
  public User findUserForUpdate(Long userId) {
    return userRepository.findForUpdate(userId);
  }
}

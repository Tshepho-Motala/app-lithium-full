package lithium.service.user.threshold.service;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.user.threshold.data.entities.Domain;
import lithium.service.user.threshold.data.entities.User;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {

  @Transactional( rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW )
  boolean updateNotifications(String playerGuid, boolean notifications)
  throws Status500InternalServerErrorException;

  @Transactional( rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW )
  boolean getNotifications(String playerGuid)
  throws Status500InternalServerErrorException;

  User updateUser(User user, boolean isTestAccount)
  throws Status500InternalServerErrorException;

  User findByGuid(String userGuid);

  int playerAge(User user);

  @Transactional( rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW )
  @Retryable( maxAttempts = 100, backoff = @Backoff( value = 10, delay = 10 ) )
  User findOrCreate(String userGuid, Domain domain)
  throws Status500InternalServerErrorException;
}


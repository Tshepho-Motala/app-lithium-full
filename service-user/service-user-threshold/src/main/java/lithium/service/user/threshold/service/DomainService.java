package lithium.service.user.threshold.service;

import lithium.service.user.threshold.data.entities.Domain;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface DomainService {

  @Transactional( rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW )
  @Retryable( maxAttempts = 100, backoff = @Backoff( value = 10, delay = 10 ) )
  Domain findOrCreate(String domainName);
}

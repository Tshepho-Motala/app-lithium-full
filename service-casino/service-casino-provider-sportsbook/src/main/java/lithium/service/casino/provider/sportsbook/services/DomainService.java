package lithium.service.casino.provider.sportsbook.services;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.casino.provider.sportsbook.storage.entities.Currency;
import lithium.service.casino.provider.sportsbook.storage.entities.Domain;
import lithium.service.casino.provider.sportsbook.storage.repositories.DomainRepository;
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
public class DomainService {

  @Autowired
  @Setter
  private DomainRepository domainRepository;

  /**
   * This was created to handle LSPLAT-1429
   *
   * @param domainName
   * @return
   * @throws Status500InternalServerErrorException
   */
  @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
  @Retryable(maxAttempts=100, backoff = @Backoff(value = 10, delay = 10))
  public Domain findOrCreateByName(String domainName, Currency currency) throws
      Status500InternalServerErrorException {

    Domain domain = domainRepository.findOrCreateByName(domainName, () -> Domain.builder().currency(currency).build());

    return domain;
  }
}

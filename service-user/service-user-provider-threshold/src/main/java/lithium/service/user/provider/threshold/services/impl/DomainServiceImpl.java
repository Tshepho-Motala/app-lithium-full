package lithium.service.user.provider.threshold.services.impl;

import java.util.Optional;
import lithium.service.user.provider.threshold.data.entities.Domain;
import lithium.service.user.provider.threshold.data.repositories.DomainRepository;
import lithium.service.user.provider.threshold.services.DomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DomainServiceImpl implements DomainService {

  @Autowired
  private DomainRepository domainRepository;

  @Override
  public Domain save(Domain domain) {
    return domainRepository.save(domain);
  }

  @Override
  public Iterable<Domain> findAll() {
    return domainRepository.findAll();
  }

  @Override
  public Optional<Domain> findOne(Long id) {
    return domainRepository.findById(id);
  }

  @Override
  @Retryable(maxAttempts = 10, backoff = @Backoff(delay = 10, maxDelay = 100, random = true))
  public Domain findOrCreate(String domainName) {
    return domainRepository.findOrCreateByName(domainName, Domain::new);
  }
}

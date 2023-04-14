package lithium.service.reward.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.reward.data.entities.Domain;
import lithium.service.reward.data.repositories.DomainRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DomainService {

  private Map<String, Domain> cache = new ConcurrentHashMap<>(1000);
  @Autowired
  private DomainRepository domainRepository;
  @Setter
  @Autowired
  private CachingDomainClientService cachingDomainClientService;

  @Retryable(maxAttempts = 10, backoff = @Backoff(delay = 10, maxDelay = 100, random = true))
  public Domain findOrCreate(String name) {
    Domain domain = cacheGet(name);
    if (domain != null) return domain;
    domain = domainRepository.findByName(name);
    if (domain != null) return cachePut(domain);
    domain = Domain.builder().name(name).build();
    domain = domainRepository.save(domain);
    return cachePut(domain);
  }

  public lithium.service.domain.client.objects.Domain externalDomain(String domainName) {
    try {
      return cachingDomainClientService.retrieveDomainFromDomainService(domainName);
    } catch (Exception e) {
      log.error("Could not retrieve domain/website details for domain : " + domainName, e);
      //throw new Status500RuntimeException(context);
      return null;
    }
  }

  private Domain cachePut(Domain object) {
    String cacheKey = object.getName();
    cache.put(cacheKey, object);
    return object;
  }

  private Domain cacheGet(String code) {
    String cacheKey = code;
    return cache.get(cacheKey);
  }
}

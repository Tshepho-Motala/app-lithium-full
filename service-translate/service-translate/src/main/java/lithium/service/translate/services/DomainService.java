package lithium.service.translate.services;

import java.util.Optional;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.translate.data.entities.Domain;
import lithium.service.translate.data.repositories.DomainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomainService {
  @Autowired DomainRepository domainRepository;
  @Autowired CachingDomainClientService cachingDomainClientService;

  public Domain findDomainByName(String name) throws Status550ServiceDomainClientException {
    lithium.service.domain.client.objects.Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(name);
    return findOrCreate(domain.getName());
  }

  public Domain findOrCreate(String name) {
    Optional<Domain> domain = domainRepository.findByName(name.toLowerCase());
    Domain domainSaved = null;
    if (!domain.isPresent()) {
      domainSaved = domainRepository.save(Domain.builder().name(name.toLowerCase()).build());
    }
    return domain.isPresent() ? domain.get() : domainSaved;
  }
}

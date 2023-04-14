package lithium.service.user.search.services.user;

import lithium.service.user.data.entities.Domain;
import lithium.service.user.search.data.repositories.user.DomainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service(value = "user.DomainService")
public class DomainService {

  @Autowired
  @Qualifier("user.DomainRepository")
  private DomainRepository domainRepository;

  public Domain findOrCreate(String name) {
    Domain domain = domainRepository.findByName(name.toLowerCase());
    if (domain == null) {
      domain = Domain.builder().name(name).build();
      domainRepository.save(domain);
    }
    return domain;
  }
}

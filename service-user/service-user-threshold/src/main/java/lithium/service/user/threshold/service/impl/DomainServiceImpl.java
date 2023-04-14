package lithium.service.user.threshold.service.impl;

import lithium.service.user.threshold.data.entities.Domain;
import lithium.service.user.threshold.data.repositories.DomainRepository;
import lithium.service.user.threshold.service.DomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DomainServiceImpl implements DomainService {

  @Autowired
  private DomainRepository domainRepository;

  @Override
  public Domain findOrCreate(String domainName) {
    return domainRepository.findOrCreateByName(domainName, Domain::new);
  }
}

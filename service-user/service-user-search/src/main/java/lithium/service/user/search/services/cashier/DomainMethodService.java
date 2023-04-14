package lithium.service.user.search.services.cashier;

import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.user.search.data.repositories.cashier.DomainMethodRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service("cashier.DomainMethodService")
public class DomainMethodService {

  @Autowired
  @Qualifier("cashier.DomainMethodRepository")
  private DomainMethodRepository domainMethodRepository;

  public DomainMethod findOne(Long id) {
    return domainMethodRepository.findOne(id);
  }

}

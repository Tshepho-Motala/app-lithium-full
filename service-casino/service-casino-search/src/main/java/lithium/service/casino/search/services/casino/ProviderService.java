package lithium.service.casino.search.services.casino;

import lithium.service.casino.data.entities.Provider;
import lithium.service.casino.search.data.repositories.casino.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.List;

@Service("casino.ProviderService")
public class ProviderService {
  @Autowired @Qualifier("casino.ProviderRepository")
  private ProviderRepository providerRepository;

  public List<Provider> findByDomainName(String domainName) {
    return providerRepository.findByDomainName(domainName);
  }
}

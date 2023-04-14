package lithium.service.domain.data.repositories;

import lithium.service.domain.data.entities.Domain;
import lithium.service.domain.data.entities.ProviderAuthClient;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProviderAuthClientRepository extends PagingAndSortingRepository<ProviderAuthClient, Long>, JpaSpecificationExecutor<ProviderAuthClient> {
	ProviderAuthClient findByDomainAndCode(Domain domain, String code);
	ProviderAuthClient findByDomainAndCodeAndIdNot(Domain domain, String code, Long id);

  default ProviderAuthClient findOne(Long id) {
    return findById(id).orElse(null);
  }
}

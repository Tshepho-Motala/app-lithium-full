package lithium.service.casino.search.data.repositories.casino;

import lithium.service.casino.data.entities.Provider;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository("casino.ProviderRepository")
public interface ProviderRepository extends PagingAndSortingRepository<Provider, Long> {
  List<Provider> findByDomainName(String domainName);
}

package lithium.service.casino.data.repositories;

import lithium.jpa.repository.FindOrCreateByGuidRepository;
import lithium.service.casino.data.entities.Provider;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderRepository extends FindOrCreateByGuidRepository<Provider, Long> {
	@Cacheable(value = "lithium.service.casino.entities.provider.byGuid", unless = "#result == null")
	Provider findByGuid(String guid);
}

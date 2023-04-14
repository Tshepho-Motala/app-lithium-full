package lithium.service.pushmsg.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.pushmsg.data.entities.Provider;
import lithium.service.pushmsg.data.entities.ProviderProperty;

public interface ProviderPropertyRepository extends PagingAndSortingRepository<ProviderProperty, Long> {
	ProviderProperty findByProviderAndName(Provider provider, String name);
}
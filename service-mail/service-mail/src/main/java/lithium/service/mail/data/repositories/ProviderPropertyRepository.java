package lithium.service.mail.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.mail.data.entities.Provider;
import lithium.service.mail.data.entities.ProviderProperty;

public interface ProviderPropertyRepository extends PagingAndSortingRepository<ProviderProperty, Long> {
	ProviderProperty findByProviderAndName(Provider provider, String name);
	ProviderProperty findFirstByProviderAndName(Provider provider, String name);
}
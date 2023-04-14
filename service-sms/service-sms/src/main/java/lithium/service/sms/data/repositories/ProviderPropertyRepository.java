package lithium.service.sms.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.sms.data.entities.Provider;
import lithium.service.sms.data.entities.ProviderProperty;

public interface ProviderPropertyRepository extends PagingAndSortingRepository<ProviderProperty, Long> {
	ProviderProperty findByProviderAndName(Provider provider, String name);
}
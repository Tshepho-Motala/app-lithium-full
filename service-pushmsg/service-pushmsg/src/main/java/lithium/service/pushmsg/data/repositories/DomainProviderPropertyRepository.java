package lithium.service.pushmsg.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.pushmsg.data.entities.DomainProviderProperty;

public interface DomainProviderPropertyRepository extends PagingAndSortingRepository<DomainProviderProperty, Long> {
	List<DomainProviderProperty> findByDomainProviderIdOrderByProviderPropertyName(Long domainProviderId);
	DomainProviderProperty findByDomainProviderIdAndProviderPropertyId(Long domainProviderId, Long providerPropertyId);
}
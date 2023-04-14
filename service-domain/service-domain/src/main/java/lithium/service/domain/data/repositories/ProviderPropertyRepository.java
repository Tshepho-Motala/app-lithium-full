package lithium.service.domain.data.repositories;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.domain.data.entities.Provider;
import lithium.service.domain.data.entities.ProviderProperty;

import java.util.List;

public interface ProviderPropertyRepository extends PagingAndSortingRepository<ProviderProperty, Long> {
	ProviderProperty findByName(String name);
	ProviderProperty findByProviderAndName(Provider provider, String propertyName);
//	ProviderProperty findByPropertyNameAndProviderIdAndProviderDomainId(String propertyName, Long providerId, Long domainId);
	Iterable<ProviderProperty> findByProviderId(Long id);
	Iterable<ProviderProperty> findByProviderDomainIdAndProviderProviderTypeName(Long id, String type);

	@Caching(evict = {
			@CacheEvict(cacheNames={"lithium.service.domain.data.entities.Provider.findByUrlAndDomainName"}),
			@CacheEvict(cacheNames={"lithium.service.games.services.getProviderProperties"}, allEntries = true),
      @CacheEvict(cacheNames={"lithium.service.domain.client.ProviderClient.propertiesByProviderUrlAndDomainName"}, allEntries = true),
      @CacheEvict(cacheNames={"lithium.service.user.provider.incomplete-applicant-hash"}, allEntries = true)
			})
	@Override
	public <S extends ProviderProperty> S save(S arg0);

  default ProviderProperty findOne(Long id) {
    return findById(id).orElse(null);
  }
}

package lithium.service.domain.data.repositories;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.domain.data.entities.Provider;

public interface ProviderRepository extends PagingAndSortingRepository<Provider, Long> {
	Provider findByName(String name);
	Iterable<Provider> findByDomainNameOrderByPriority(String domainName);
	Iterable<Provider> findByDomainNameAndProviderTypeNameOrderByPriority(String domainName, String type);
	Iterable<Provider> findByDomainIdAndProviderTypeNameOrderByPriority(Long id, String type);
  Iterable<Provider> findByDomainIdAndProviderTypeNameAndEnabledTrueOrderByPriority(Long id, String type);
	Iterable<Provider> findByProviderTypeNameOrderByPriority(String type);
	Iterable<Provider> findByProviderTypeNameAndUrlOrderByPriority(String type, String url);
	Provider findByNameAndDomainNameAndProviderTypeId(String name, String domainName, Long providerTypeid);
	Provider findByUrl(String url);
	Provider findByNameAndUrlAndDomainId(String providerName, String providerUrl, Long domainId);
	Provider findByNameAndDomainName(String name, String domainName);
	@Cacheable(value="lithium.service.domain.data.entities.Provider.findByUrlAndDomainName", unless="#result == null")
	Provider findByUrlAndDomainName(String url, String domainName);
//	@Cacheable(value="lithium.service.domain.data.entities.Provider.findByUrlAndDomainNameAndProviderTypeName", unless="#result == null")
	Provider findByUrlAndDomainNameAndProviderTypeName(String url, String domainName, String providerType);

	@CacheEvict(allEntries = true, cacheNames = {
		"lithium.service.domain.data.entities.Provider.findByUrlAndDomainName",
		"lithium.service.domain.data.entities.Provider.findByUrlAndDomainNameAndProviderTypeName",
		"lithium.service.accounting.service.Provider",
		"lithium.service.domain.enabledProvidersByDomainAndProviderType",
		"lithium.service.domain.client.ProviderClient.propertiesByProviderUrlAndDomainName",
		"lithium.service.domain.client.ProviderClient.findByUrlAndDomainName",
	})
	@Override
	public <S extends Provider> S save(S arg0);

  default Provider findOne(Long id) {
    return findById(id).orElse(null);
  }
}

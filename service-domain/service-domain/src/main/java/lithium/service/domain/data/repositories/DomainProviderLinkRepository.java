package lithium.service.domain.data.repositories;

import lithium.service.domain.data.entities.DomainProviderLink;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DomainProviderLinkRepository extends PagingAndSortingRepository<DomainProviderLink, Long> {
	DomainProviderLink findByDomainNameAndProviderUrlAndProviderProviderTypeIdAndDeletedFalseAndEnabledTrue(String domainName, String providerUrl, Long providerTypeId);
	DomainProviderLink findByDomainNameAndProviderUrlAndDeletedFalseAndEnabledTrue(String domainName, String providerUrl);
//	Iterable<DomainProviderLink> findByProviderUrlAndOwnerLinkFalseAndDeletedFalse(String providerUrl);
	Iterable<DomainProviderLink> findByDomainNameAndOwnerLinkFalseAndDeletedFalseAndEnabledTrue(String domainName);
	Iterable<DomainProviderLink> findByProviderIdAndOwnerLinkFalseAndDeletedFalse(Long providerId);
	DomainProviderLink findByProviderIdAndOwnerLinkTrueAndDeletedFalse(Long providerId);
	Iterable<DomainProviderLink> findByDomainNameAndProviderProviderTypeNameAndOwnerLinkFalseAndDeletedFalseAndEnabledTrue(String domainName, String providerTypeName);
  Iterable<DomainProviderLink> findByDomainNameAndProviderEnabledTrueAndProviderProviderTypeNameAndOwnerLinkFalseAndDeletedFalseAndEnabledTrue(String domainName, String providerTypeName);

	@Override
	@Caching(evict = {
		@CacheEvict(allEntries=true, value="lithium.service.domain.enabledProvidersByDomainAndProviderType")
	})
	public <S extends DomainProviderLink> S save(S entity);

  default DomainProviderLink findOne(Long id) {
    return findById(id).orElse(null);
  }
}

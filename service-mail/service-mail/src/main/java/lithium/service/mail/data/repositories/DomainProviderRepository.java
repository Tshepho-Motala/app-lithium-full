package lithium.service.mail.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.mail.data.entities.DomainProvider;

public interface DomainProviderRepository extends PagingAndSortingRepository<DomainProvider, Long> {
	List<DomainProvider> findByDomainNameAndDeletedFalseOrderByPriority(String domainName);
	DomainProvider findByDomainNameAndProviderUrlAndDeletedFalseAndEnabledTrue(String domainName, String providerUrl);
	Iterable<DomainProvider> findByDomainNameAndProviderProviderTypeNameAndDeletedFalseAndEnabledTrue(String domainName, String providerTypeName);
	List<DomainProvider> findByDomainNameAndDeletedFalseAndEnabledTrueOrderByPriority(String domainName);

	default DomainProvider findOne(Long id) {
		return findById(id).orElse(null);
	}
}
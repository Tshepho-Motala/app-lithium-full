package lithium.service.sms.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.sms.data.entities.DomainProvider;

public interface DomainProviderRepository extends PagingAndSortingRepository<DomainProvider, Long> {
	List<DomainProvider> findByDomainNameAndDeletedFalseOrderByPriority(String domainName);
	default DomainProvider findOne(Long id) {
		return findById(id).orElse(null);
	}
}
package lithium.service.domain.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.domain.data.entities.DomainImage;

public interface DomainImageRepository extends PagingAndSortingRepository<DomainImage, Long> {
	DomainImage findByDomainId(Long domainId);
	DomainImage findByDomainIdAndName(Long domainId, String name);
}

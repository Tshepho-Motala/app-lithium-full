package lithium.service.casino.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.casino.data.entities.WinnerAugmentation;

public interface WinnerAugmentationRepository extends PagingAndSortingRepository<WinnerAugmentation, Long> {	
	WinnerAugmentation findTop1ByDomainNameOrderByIdAsc(String domainName);
}
package lithium.service.affiliate.provider.data.repositories;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.affiliate.provider.data.entities.BatchRunStatus;

public interface BatchRunStatusRepository extends PagingAndSortingRepository<BatchRunStatus, Long> {
	
	@Cacheable(value = "lithium.service.affiliate.provider.data.entities.BatchRunStatus.name", unless = "#result == null")
	BatchRunStatus findByName(String name);
	
	@Override
	@CacheEvict(allEntries=true, value={"lithium.service.affiliate.provider.data.entities.BatchRunStatus.name"})
	<S extends BatchRunStatus> S save(S entity);
}
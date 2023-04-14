package lithium.service.stats.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.stats.data.entities.StatEntry;

public interface StatEntryRepository extends PagingAndSortingRepository<StatEntry, Long> {
		
}
